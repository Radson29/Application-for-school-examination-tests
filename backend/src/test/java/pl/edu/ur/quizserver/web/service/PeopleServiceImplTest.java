package pl.edu.ur.quizserver.web.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.persistence.entity.RoleEntity;
import pl.edu.ur.quizserver.persistence.repository.LoginRepository;
import pl.edu.ur.quizserver.persistence.repository.PeopleRepository;
import pl.edu.ur.quizserver.persistence.repository.RoleRepository;
import pl.edu.ur.quizserver.web.dto.CreatePersonDto;
import pl.edu.ur.quizserver.web.dto.UpdateUserDetailsDto;
import pl.edu.ur.quizserver.web.error.UserAlreadyExistException;
import pl.edu.ur.quizserver.web.error.UserNotFoundException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PeopleServiceImplTest {

    @Mock
    private LoginService loginService;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PeopleRepository peopleRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private PeopleServiceImpl peopleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_ExistingId_ReturnsPersonEntity() {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_STUDENT");

        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        person.getLogin().setLogin(createPersonDto.getLogin());
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setPesel(createPersonDto.getPesel());

        long id = 1;

        // Set up the mock repository to return the expected person
        when(peopleRepository.findById(id)).thenReturn(person);

        // Create an instance of the PeopleServiceImpl class and inject the mock repository

        // Act
        PersonEntity result = peopleService.findById(id);

        // Assert
        assertEquals(person, result);
    }

    @Test
    void testFindById_NonExistingId_ThrowsUserNotFoundException() {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_STUDENT");

        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        person.getLogin().setLogin(createPersonDto.getLogin());
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setPesel(createPersonDto.getPesel());

        long id = 1;

        // Set up the mock repository to return null (person not found)
        when(peopleRepository.findById(id)).thenReturn(null);

        // Assert
        assertThrows(UserNotFoundException.class, () -> {
            // Act
            peopleService.findById(id);
        });
    }

    @Test
    void testDeletePerson_ValidPerson_Success() throws UserNotFoundException {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_STUDENT");

        // Set test data from CreatePersonDto
        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        person.getLogin().setLogin(createPersonDto.getLogin());
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setPesel(createPersonDto.getPesel());

        String type = "ROLE_STUDENT";
        long id = 123;

        MockitoAnnotations.openMocks(this);
        when(peopleRepository.findById(id)).thenReturn(person);

        // Act
        peopleService.deletePerson(id, type);

        // Assert
        verify(peopleRepository, times(1)).findById(id);
        verify(peopleRepository, times(1)).save(person);
        assertNotNull(person.getDeletedAt());
    }

    @Test
    void testDeletePerson_InvalidPerson_UserNotFoundException() {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_STUDENT");

        // Set test data from CreatePersonDto
        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        person.getLogin().setLogin(createPersonDto.getLogin());
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setPesel(createPersonDto.getPesel());

        String type = "ROLE_STUDENT";
        long id = 123;

        MockitoAnnotations.openMocks(this);
        when(peopleRepository.findById(id)).thenReturn(null);

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> peopleService.deletePerson(id, type));
        verify(peopleRepository, times(1)).findById(id);
        verify(peopleRepository, never()).save(any(PersonEntity.class));
    }

    @Test
    public void testUpdatePerson_Success() throws UserNotFoundException, UserAlreadyExistException {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_STUDENT");

        long id = 123;
        String type = "ROLE_STUDENT";

        UpdateUserDetailsDto updateUserDetailsDto = new UpdateUserDetailsDto();
        updateUserDetailsDto.setLogin("newlogin");
        updateUserDetailsDto.setFirstName("Jane");
        updateUserDetailsDto.setLastName("Smith");
        updateUserDetailsDto.setPesel("0987654321");

        when(peopleRepository.findById(id)).thenReturn(person);
//        when(peopleService.isPersonValid(person, type)).thenReturn(true);
        when(loginService.findByLogin(updateUserDetailsDto.getLogin())).thenReturn(null);
        when(peopleRepository.findByPesel(updateUserDetailsDto.getPesel())).thenReturn(null);

        // Perform the method call
        peopleService.updatePerson(id, updateUserDetailsDto, type);

        // Verify the repository method was called
        verify(peopleRepository, times(1)).findById(id);
        verify(peopleRepository, times(1)).save(person);

        // Verify the loginService method was called
        verify(loginService, times(1)).findByLogin(updateUserDetailsDto.getLogin());

        // Verify the updated person details
        assertEquals(updateUserDetailsDto.getLogin(), person.getLogin().getLogin());
        assertEquals(updateUserDetailsDto.getFirstName(), person.getFirstName());
        assertEquals(updateUserDetailsDto.getLastName(), person.getLastName());
        assertEquals(updateUserDetailsDto.getPesel(), person.getPesel());
        assertNotNull(person.getModifiedAt());
    }

    @Test
    public void testGetPersonDetails_Success() throws UserNotFoundException {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_STUDENT");

        // Set test data from CreatePersonDto
        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        person.getLogin().setLogin(createPersonDto.getLogin());
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setPesel(createPersonDto.getPesel());

        String type = "ROLE_STUDENT";
        long id = 123;

        when(peopleRepository.findById(id)).thenReturn(person);

        // Perform the method call
        Map<String, Object> result = peopleService.getPersonDetails(id, type);

        // Verify the repository method was called
        verify(peopleRepository, times(1)).findById(id);

        // Verify the result
        assertNotNull(result);
        assertEquals("testlogin", result.get("login"));
        assertEquals(person, result.get("person"));
    }

    @Test
    public void testGetPeople_Success() {
        // Prepare test data
        String type = "ROLE_STUDENT";

        // Prepare mocked repository response
        List<PersonEntity> expectedPeople = Arrays.asList(
                new PersonEntity(),
                new PersonEntity()
        );

        when(peopleRepository.findByLoginRoleNameAndDeletedAtIsNullOrderByFirstName(type)).thenReturn(expectedPeople);

        // Perform the method call
        List<PersonEntity> actualPeople = peopleService.getPeople(type);

        // Verify the repository method was called
        verify(peopleRepository, times(1)).findByLoginRoleNameAndDeletedAtIsNullOrderByFirstName(type);

        // Verify the result
        assertEquals(expectedPeople, actualPeople);
    }

    @Test
    public void testCreatePerson_Success() throws UserAlreadyExistException {
        // Prepare test data
        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        String type = "ROLE_STUDENT";

        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setLogin(createPersonDto.getLogin());
        loginEntity.setResetPassword(true);
        loginEntity.setRole(new RoleEntity());
        when(loginService.generateNewPassword(any())).thenReturn("testpassword");
        when(loginRepository.save(any(LoginEntity.class))).thenReturn(loginEntity);

        // Mock the findByLogin and findByPesel methods to return null, indicating that the user doesn't exist
        when(loginService.findByLogin(createPersonDto.getLogin())).thenReturn(null);
        when(peopleRepository.findByPesel(createPersonDto.getPesel())).thenReturn(null);

        // Mock the roleRepository.findByName method to return a valid RoleEntity
        RoleEntity roleEntity = new RoleEntity();
        when(roleRepository.findByName(type)).thenReturn(roleEntity);

        // Perform the method call
        String password = peopleService.createPerson(createPersonDto, type);

        // Verify the results
        Assertions.assertEquals("testpassword", password);
        verify(loginService).generateNewPassword(any());
//        verify(loginRepository).save(any(LoginEntity.class));
        verify(loginService).findByLogin(createPersonDto.getLogin());
        verify(peopleRepository).findByPesel(createPersonDto.getPesel());
        verify(roleRepository).findByName(type);
    }

    @Test
    public void testCreatePerson_UserAlreadyExists() {
        // Prepare test data
        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        String type = "ROLE_STUDENT";

        // Mock the findByLogin method to return a non-null value, indicating that the user already exists
        when(loginService.findByLogin(createPersonDto.getLogin())).thenReturn(new LoginEntity());

        // Perform the method call and verify that UserAlreadyExistException is thrown
        Assertions.assertThrows(UserAlreadyExistException.class, () -> {
            peopleService.createPerson(createPersonDto, type);
        });

        // Verify that the findByPesel and save methods are not called
        verify(peopleRepository, never()).findByPesel(createPersonDto.getPesel());
        verify(loginRepository, never()).save(any(LoginEntity.class));
    }

    @Test
    public void testIsPersonValid_ValidPerson() {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_STUDENT");

        // Set test data from CreatePersonDto
        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        person.getLogin().setLogin(createPersonDto.getLogin());
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setPesel(createPersonDto.getPesel());

        String type = "ROLE_STUDENT";

        // Perform the method call
        boolean isValid = peopleService.isPersonValid(person, type);

        // Verify the result
        assertTrue(isValid);
    }

    @Test
    public void testIsPersonValid_NullPerson() {
        // Prepare test data
        PersonEntity person = null;

        String type = "ROLE_STUDENT";

        // Perform the method call
        boolean isValid = peopleService.isPersonValid(person, type);

        // Verify the result
        assertFalse(isValid);
    }

    @Test
    public void testIsPersonValid_WrongRole() {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_ADMIN");

        // Set test data from CreatePersonDto
        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        person.getLogin().setLogin(createPersonDto.getLogin());
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setPesel(createPersonDto.getPesel());

        String type = "ROLE_STUDENT";

        // Perform the method call
        boolean isValid = peopleService.isPersonValid(person, type);

        // Verify the result
        assertFalse(isValid);
    }

    @Test
    public void testIsPersonValid_DeletedPerson() {
        // Prepare test data
        PersonEntity person = new PersonEntity();
        person.setLogin(new LoginEntity());
        person.getLogin().setRole(new RoleEntity());
        person.getLogin().getRole().setName("ROLE_STUDENT");
        person.setDeletedAt(new Date());

        // Set test data from CreatePersonDto
        CreatePersonDto createPersonDto = new CreatePersonDto();
        createPersonDto.setLogin("testlogin");
        createPersonDto.setFirstName("John");
        createPersonDto.setLastName("Doe");
        createPersonDto.setPesel("1234567890");

        person.getLogin().setLogin(createPersonDto.getLogin());
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setPesel(createPersonDto.getPesel());

        String type = "ROLE_STUDENT";

        // Perform the method call
        boolean isValid = peopleService.isPersonValid(person, type);

        // Verify the result
        assertFalse(isValid);
    }
}
