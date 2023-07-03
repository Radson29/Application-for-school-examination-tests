package pl.edu.ur.quizserver.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.pdflib.dto.GroupedPeopleDTO;
import pl.edu.ur.pdflib.dto.PersonDTO;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.persistence.entity.PersonGroupEntity;
import pl.edu.ur.quizserver.persistence.pojo.GroupedPeoplePojo;
import pl.edu.ur.quizserver.persistence.repository.PeopleRepository;
import pl.edu.ur.quizserver.persistence.repository.PersonGroupRepository;
import pl.edu.ur.quizserver.web.error.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupServiceImpl implements GroupService{

    @Autowired
    private PersonGroupRepository groupRepository;

    @Autowired
    private PeopleRepository peopleRepository;

    /**
     * Retrieves a list of groups along with the people assigned to them that have not been deleted.
     *
     * @return a list of GroupedPeoplePojo objects containing groups and their assigned people
     */
    @Override
    public List<GroupedPeoplePojo> getGroupedPeople() {
        List<GroupedPeoplePojo> result = new ArrayList<>();
        List<PersonGroupEntity> groups = groupRepository.findDistinctByDeletedAtIsNullOrPeopleIsNullAndDeletedAtIsNull();
        System.out.println(groups.size());
        for (PersonGroupEntity group :
                groups) {
            List<PersonEntity> people = peopleRepository.findByDeletedAtIsNullAndGroups_id(group.getId());
            System.out.println(people.size());
            result.add(new GroupedPeoplePojo(group,people));
        }

        return result;
    }

    /**
     * Creates a new group with the given name.
     *
     * @param groupName the name of the group
     * @throws GroupAlreadyExistException if a group with the given name already exists
     */
    @Override
    public void createGroup(String groupName) throws GroupAlreadyExistException {
        if(groupRepository.findByName(groupName) != null)
            throw new GroupAlreadyExistException();

        PersonGroupEntity group = new PersonGroupEntity();
        group.setName(groupName);
        group.setCreatedAt(new Date());
        group.setModifiedAt(new Date());

        groupRepository.save(group);
    }

    /**
     * Deletes the group with the given ID.
     *
     * @param id the ID of the group
     * @throws GroupNotFoundException if a group with the given ID does not exist
     */
    @Override
    public void deleteGroup(long id) throws GroupNotFoundException {
        PersonGroupEntity group = groupRepository.findById(id);
        if(group == null)
            throw new GroupNotFoundException();

        group.setDeletedAt(new Date());
        groupRepository.save(group);
    }
    /**
     * Adds a person with the given ID to the group with the given ID.
     *
     * @param groupId  the ID of the group
     * @param personId the ID of the person
     * @throws UserNotFoundException          if a person with the given ID does not exist
     * @throws GroupNotFoundException         if a group with the given ID does not exist
     * @throws UserAlreadyExistInGroupException if the person is already a member of the group
     */
    @Override
    public void addPersonToGroup(long groupId, long personId) throws UserNotFoundException, GroupNotFoundException, UserAlreadyExistInGroupException {
        PersonGroupEntity group = groupRepository.findById(groupId);
        PersonEntity person = peopleRepository.findById(personId);
        if(group == null)
            throw new GroupNotFoundException();
        if(person == null)
            throw new UserNotFoundException();
        if(userAlreadyInGroup(personId,group))
            throw new UserAlreadyExistInGroupException();
        List<PersonGroupEntity> groups = person.getGroups();
        groups.add(group);
        person.setGroups(groups);

        peopleRepository.save(person);
    }
    /**
     * Removes a person with the given ID from the group with the given ID.
     *
     * @param groupId  the ID of the group
     * @param personId the ID of the person
     * @throws UserNotFoundException        if a person with the given ID does not exist
     * @throws GroupNotFoundException       if a group with the given ID does not exist
     * @throws UserNotExistInGroupException if the person is not a member of the group
     */
    @Override
    public void removePersonFromGroup(long groupId, long personId) throws UserNotFoundException, GroupNotFoundException, UserNotExistInGroupException {
        PersonGroupEntity group = groupRepository.findById(groupId);
        PersonEntity person = peopleRepository.findById(personId);
        if(group == null)
            throw new GroupNotFoundException();
        if(person == null)
            throw new UserNotFoundException();

        if(!userAlreadyInGroup(personId,group))
            throw new UserNotExistInGroupException();

        List<PersonGroupEntity> groups = person.getGroups();
        groups.remove(group);

        person.setGroups(groups);
        peopleRepository.save(person);
    }

    private boolean userAlreadyInGroup(long id, PersonGroupEntity group)
    {
        for(PersonEntity person : group.getPeople())
            if(person.getId() == id)
                return true;
        return false;
    }
    /**
     * Maps a list of GroupedPeoplePojo objects to a list of GroupedPeopleDTO objects.
     *
     * @param groupedPeoplePojos the list of GroupedPeoplePojo objects to map
     * @return a list of GroupedPeopleDTO objects
     */
    @Override
    public List<GroupedPeopleDTO> mapToDTO(List<GroupedPeoplePojo> groupedPeoplePojos) {
        return groupedPeoplePojos.stream()
                .map(pojo -> {
                    List<PersonDTO> people = pojo.getPeople().stream()
                            .map(personEntity -> new PersonDTO(
                                    personEntity.getFirstName(),
                                    personEntity.getLastName(),
                                    personEntity.getLogin().getLogin()
                            ))
                            .collect(Collectors.toList());
                    return new GroupedPeopleDTO(pojo.getGroup().getName(), people);
                })
                .collect(Collectors.toList());
    }
}
