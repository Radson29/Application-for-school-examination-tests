package pl.edu.ur.quizserver.web.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.edu.ur.pdflib.dto.GroupedPeopleDTO;
import pl.edu.ur.pdflib.dto.PersonDTO;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.persistence.entity.PersonGroupEntity;
import pl.edu.ur.quizserver.persistence.entity.RoleEntity;
import pl.edu.ur.quizserver.persistence.pojo.GroupedPeoplePojo;
import pl.edu.ur.quizserver.persistence.repository.PeopleRepository;
import pl.edu.ur.quizserver.persistence.repository.PersonGroupRepository;
import pl.edu.ur.quizserver.web.dto.CreatePersonDto;
import pl.edu.ur.quizserver.web.error.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupServiceImplTest {
    @Mock
    private PersonGroupRepository groupRepository;

    @Mock
    private PeopleRepository peopleRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetGroupedPeople() {
        // Mock data
        PersonGroupEntity group1 = new PersonGroupEntity();
        group1.setId(1L);
        PersonGroupEntity group2 = new PersonGroupEntity();
        group2.setId(2L);

        List<PersonGroupEntity> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        PersonEntity person1 = new PersonEntity();
        person1.setId(1L);
        PersonEntity person2 = new PersonEntity();
        person2.setId(2L);

        List<PersonEntity> people1 = new ArrayList<>();
        people1.add(person1);

        List<PersonEntity> people2 = new ArrayList<>();
        people2.add(person2);

        // Mock the behavior of the repositories
        when(groupRepository.findDistinctByDeletedAtIsNullOrPeopleIsNullAndDeletedAtIsNull()).thenReturn(groups);
        when(peopleRepository.findByDeletedAtIsNullAndGroups_id(1L)).thenReturn(people1);
        when(peopleRepository.findByDeletedAtIsNullAndGroups_id(2L)).thenReturn(people2);

        // Call the method
        List<GroupedPeoplePojo> result = groupService.getGroupedPeople();

        // Assertions
        Assertions.assertEquals(2, result.size());

        GroupedPeoplePojo result1 = result.get(0);
        Assertions.assertEquals(group1, result1.getGroup());
        Assertions.assertEquals(people1, result1.getPeople());

        GroupedPeoplePojo result2 = result.get(1);
        Assertions.assertEquals(group2, result2.getGroup());
        Assertions.assertEquals(people2, result2.getPeople());

        // Verify that the repositories were called correctly
        verify(groupRepository, times(1)).findDistinctByDeletedAtIsNullOrPeopleIsNullAndDeletedAtIsNull();
        verify(peopleRepository, times(1)).findByDeletedAtIsNullAndGroups_id(1L);
        verify(peopleRepository, times(1)).findByDeletedAtIsNullAndGroups_id(2L);
    }

    @Test
    void testCreateGroup_WhenGroupDoesNotExist() {
        // Mock data
        String groupName = "New Group";
        PersonGroupEntity existingGroup = null;

        // Mock the behavior of the repository
        when(groupRepository.findByName(groupName)).thenReturn(existingGroup);

        // Call the method
        try {
            groupService.createGroup(groupName);

            // Verify that the repository method was called
            verify(groupRepository, times(1)).findByName(groupName);
            verify(groupRepository, times(1)).save(any(PersonGroupEntity.class));
        } catch (GroupAlreadyExistException e) {
            // Group should not exist, so this exception should not be thrown
            Assertions.fail("GroupAlreadyExistException should not be thrown");
        }
    }

    @Test
    void testCreateGroup_WhenGroupAlreadyExists() {
        // Mock data
        String groupName = "Existing Group";
        PersonGroupEntity existingGroup = new PersonGroupEntity();

        // Mock the behavior of the repository
        when(groupRepository.findByName(groupName)).thenReturn(existingGroup);

        // Call the method and assert that GroupAlreadyExistException is thrown
        Assertions.assertThrows(GroupAlreadyExistException.class, () -> groupService.createGroup(groupName));

        // Verify that the repository method was called
        verify(groupRepository, times(1)).findByName(groupName);
        verify(groupRepository, never()).save(any(PersonGroupEntity.class));
    }

    @Test
    void testDeleteGroup_WhenGroupExists() throws GroupNotFoundException {
        // Mock data
        long groupId = 1L;
        PersonGroupEntity existingGroup = new PersonGroupEntity();

        // Mock the behavior of the repository
        when(groupRepository.findById(groupId)).thenReturn(existingGroup);

        // Call the method
        try {
            groupService.deleteGroup(groupId);

            // Verify that the repository method was called
            verify(groupRepository, times(1)).findById(groupId);
            verify(groupRepository, times(1)).save(existingGroup);
            // Assert that the group's deletedAt field was set to a non-null value
            assertNotNull(existingGroup.getDeletedAt());
        } catch (GroupNotFoundException e) {
            // Group exists, so this exception should not be thrown
            Assertions.fail("GroupNotFoundException should not be thrown");
        }
    }

    @Test
    void testDeleteGroup_WhenGroupDoesNotExist() {
        // Mock data
        long groupId = 1L;
        PersonGroupEntity nonExistingGroup = null;

        // Mock the behavior of the repository
        when(groupRepository.findById(groupId)).thenReturn(nonExistingGroup);

        // Call the method and assert that GroupNotFoundException is thrown
        Assertions.assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroup(groupId));

        // Verify that the repository method was called
        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, never()).save(any(PersonGroupEntity.class));
    }

    @Test
    public void testAddPersonToGroup() throws UserNotFoundException, GroupNotFoundException, UserAlreadyExistInGroupException {
        // Prepare test data
        long groupId = 1;
        long personId = 2;
        PersonGroupEntity groupEntity = new PersonGroupEntity();
        List<PersonEntity> emptyPeopleList = new ArrayList<>();
        groupEntity.setPeople(emptyPeopleList);

        PersonEntity personEntity = new PersonEntity();
        List<PersonGroupEntity> emptyGroupsList = new ArrayList<>();
        personEntity.setGroups(emptyGroupsList);

        // Configure mock behavior
        when(groupRepository.findById(groupId)).thenReturn(groupEntity);
        when(peopleRepository.findById(personId)).thenReturn(personEntity);
        when(peopleRepository.save(personEntity)).thenReturn(personEntity);

        // Call the method
        groupService.addPersonToGroup(groupId, personId);

        // Verify the method behavior
        verify(groupRepository, times(1)).findById(groupId);
        verify(peopleRepository, times(1)).findById(personId);
        verify(peopleRepository, times(1)).save(personEntity);

        assertTrue(personEntity.getGroups().contains(groupEntity));
    }

    @Test
    public void testRemovePersonFromGroup() throws UserNotFoundException, GroupNotFoundException, UserNotExistInGroupException {
        // Prepare test data
        long groupId = 1;
        long personId = 2;

        PersonGroupEntity groupEntity = new PersonGroupEntity();
        groupEntity.setId(groupId);
        List<PersonEntity> peopleList = new ArrayList<>();
        groupEntity.setPeople(peopleList);

        PersonEntity personEntity = new PersonEntity();
        personEntity.setId(personId);
        List<PersonGroupEntity> groupsList = new ArrayList<>();
        groupsList.add(groupEntity);
        personEntity.setGroups(groupsList);

        peopleList.add(personEntity);
        groupEntity.setPeople(peopleList);

        // Configure mock behavior
        when(groupRepository.findById(groupId)).thenReturn(groupEntity);
        when(peopleRepository.findById(personId)).thenReturn(personEntity);
        when(peopleRepository.save(personEntity)).thenReturn(personEntity);

        // Call the method
        groupService.removePersonFromGroup(groupId, personId);

        // Verify the method behavior
        verify(groupRepository, times(1)).findById(groupId);
        verify(peopleRepository, times(1)).findById(personId);
        verify(peopleRepository, times(1)).save(personEntity);

        assertFalse(personEntity.getGroups().contains(groupEntity));
    }

    @Test
    public void testMapToDTO() {
        // Prepare test data
        GroupServiceImpl groupService = new GroupServiceImpl();

        LoginEntity loginPerson1 = new LoginEntity();
        loginPerson1.setLogin("john.doe");
        PersonEntity person1 = new PersonEntity();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setLogin(loginPerson1);

        LoginEntity loginPerson2 = new LoginEntity();
        loginPerson2.setLogin("jane.smith");
        PersonEntity person2 = new PersonEntity();
        person2.setFirstName("Jane");
        person2.setLastName("Smith");
        person2.setLogin(loginPerson2);

        List<PersonEntity> people = new ArrayList<>();
        people.add(person1);
        people.add(person2);

        PersonGroupEntity group = new PersonGroupEntity();
        group.setName("Group 1");

        GroupedPeoplePojo pojo = new GroupedPeoplePojo(group, people);

        List<GroupedPeoplePojo> pojos = new ArrayList<>();
        pojos.add(pojo);

        // Call the method
        List<GroupedPeopleDTO> dtos = groupService.mapToDTO(pojos);

        // Verify the result
        assertEquals(1, dtos.size());

        GroupedPeopleDTO dto = dtos.get(0);
        assertEquals("Group 1", dto.getGroupName());
        assertEquals(2, dto.getPeople().size());

        PersonDTO personDTO1 = dto.getPeople().get(0);
        assertEquals("John", personDTO1.getFirstName());
        assertEquals("Doe", personDTO1.getLastName());
//        assertEquals("johndoe", personDTO1.getLogin()); // brakuje w libce do PDF

        PersonDTO personDTO2 = dto.getPeople().get(1);
        assertEquals("Jane", personDTO2.getFirstName());
        assertEquals("Smith", personDTO2.getLastName());
//        assertEquals("janesmith", personDTO2.getLogin()); // brakuje w libce do PDF
    }
}
