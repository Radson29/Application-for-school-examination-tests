package pl.edu.ur.quizserver.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.persistence.entity.RoleEntity;
import pl.edu.ur.quizserver.persistence.repository.PeopleRepository;
import pl.edu.ur.quizserver.persistence.repository.RoleRepository;
import pl.edu.ur.quizserver.web.dto.CreatePersonDto;
import pl.edu.ur.quizserver.web.dto.UpdateUserDetailsDto;
import pl.edu.ur.quizserver.web.error.UserAlreadyExistException;
import pl.edu.ur.quizserver.web.error.UserNotFoundException;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class PeopleServiceImpl implements PeopleService {

    @Autowired
    private PeopleRepository peopleRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RoleRepository roleRepository;
    /**
     Retrieves the person entity with the specified ID from the people repository.
     @param id the ID of the person entity to retrieve
     @return the person entity with the specified ID
     @throws UserNotFoundException if a person entity with the specified ID is not found
     */
    @Override
    public PersonEntity findById(long id) throws UserNotFoundException {
        PersonEntity person = peopleRepository.findById(id);
        if(person == null)
            throw new UserNotFoundException();
        return person;
    }
    /**
     Deletes the person entity with the specified ID from the people repository.
     The person entity is marked as deleted by setting its deletedAt field to the current date and time.
     @param id the ID of the person entity to delete
     @param type the type of person associated with the login entity, used to check the validity of the person entity
     @throws UserNotFoundException if a person entity with the specified ID is not found, or if the person entity is not valid
     */
    @Override
    public void deletePerson(long id, String type) throws UserNotFoundException {
        PersonEntity person = peopleRepository.findById(id);
        if(!isPersonValid(person,type))
            throw new UserNotFoundException();
        person.setDeletedAt(new Date());
        peopleRepository.save(person);
    }
    /**
     Updates the person entity with the specified ID in the people repository with the details provided in the UpdateUserDetailsDto object.
     The method checks if the person entity is valid and if the login and pesel fields of the UpdateUserDetailsDto object are unique.
     @param id the ID of the person entity to update
     @param updateUserDetailsDto the details to update the person entity with
     @param type the type of person associated with the login entity, used to check the validity of the person entity
     @throws UserNotFoundException if a person entity with the specified ID is not found, or if the person entity is not valid
     @throws UserAlreadyExistException if the login or pesel fields in the UpdateUserDetailsDto object are not unique
     */
    @Override
    public void updatePerson(long id, UpdateUserDetailsDto updateUserDetailsDto, String type) throws UserNotFoundException, UserAlreadyExistException {
        PersonEntity person = peopleRepository.findById(id);
        if(!isPersonValid(person,type))
            throw new UserNotFoundException();

        if(!updateUserDetailsDto.getLogin().equals(person.getLogin().getLogin()) && loginService.findByLogin(updateUserDetailsDto.getLogin()) != null)
            throw new UserAlreadyExistException();

        if(!updateUserDetailsDto.getPesel().equals(person.getPesel()) && peopleRepository.findByPesel(updateUserDetailsDto.getPesel()) != null)
            throw new UserAlreadyExistException();


        person.setModifiedAt(new Date());
        person.setFirstName(updateUserDetailsDto.getFirstName());
        person.setLastName(updateUserDetailsDto.getLastName());
        person.setPesel(updateUserDetailsDto.getPesel());
        person.getLogin().setLogin(updateUserDetailsDto.getLogin());

        peopleRepository.save(person);
    }
    /**
     Retrieves details of a person with the given id and type.
     @param id the id of the person to retrieve details for
     @param type the type of the person
     @return a Map containing the person's login and details
     @throws UserNotFoundException if the person is not found or is invalid for the given type
     */
    @Override
    public Map<String, Object> getPersonDetails(long id, String type) throws UserNotFoundException {
        PersonEntity person = peopleRepository.findById(id);
        if(!isPersonValid(person,type))
            throw new UserNotFoundException();

        Map<String,Object> result = new HashMap<>();
        result.put("login", person.getLogin().getLogin());
        result.put("person",person);

        return result;
    }
    /**
     Retrieves a list of people with the given type.
     @param type the type of people to retrieve
     @return a List of PersonEntity objects matching the given type
     */
    @Override
    public List<PersonEntity> getPeople(String type) {
       return peopleRepository.findByLoginRoleNameAndDeletedAtIsNullOrderByFirstName(type);
    }
    /**
     Creates a new person with the given details and type.
     @param createPersonDto the details of the person to create
     @param type the type of person to create
     @return the generated password for the new person's login
     @throws UserAlreadyExistException if a person with the given login or PESEL already exists
     */
    @Override
    public String createPerson(CreatePersonDto createPersonDto, String type) throws UserAlreadyExistException {
        if(loginService.findByLogin(createPersonDto.getLogin()) != null
                || peopleRepository.findByPesel(createPersonDto.getPesel()) != null)
            throw new UserAlreadyExistException();

        RoleEntity role = roleRepository.findByName(type);

        LoginEntity login = new LoginEntity();
        login.setLogin(createPersonDto.getLogin());
        login.setResetPassword(true);
        login.setRole(role);

        PersonEntity person = new PersonEntity();
        person.setFirstName(createPersonDto.getFirstName());
        person.setLastName(createPersonDto.getLastName());
        person.setModifiedAt(new Date());
        person.setGroups(Collections.emptyList());

        person.setPesel(createPersonDto.getPesel());
        person.setCreatedAt(new Date());

        login.setPerson(person);

        String password = loginService.generateNewPassword(login);

        return password;
    }
    /**
     Checks if a given person is valid for the specified type.
     @param person the person to check for validity
     @param type the type of person to check for (e.g. "admin", "user")
     @return true if the person is valid for the given type, false otherwise
     */
    public boolean isPersonValid(PersonEntity person, String type)
    {
        return (person != null && person.getLogin().getRole().getName().equals(type) && !person.isDeleted());
    }


}