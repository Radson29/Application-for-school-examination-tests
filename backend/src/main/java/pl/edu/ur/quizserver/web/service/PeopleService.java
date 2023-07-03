package pl.edu.ur.quizserver.web.service;

import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.web.dto.CreatePersonDto;
import pl.edu.ur.quizserver.web.dto.UpdateUserDetailsDto;
import pl.edu.ur.quizserver.web.error.UserAlreadyExistException;
import pl.edu.ur.quizserver.web.error.UserNotFoundException;

import java.util.List;
import java.util.Map;

public interface PeopleService {

    PersonEntity findById(long id) throws UserNotFoundException;
    void deletePerson(long id, String type) throws UserNotFoundException;
    void updatePerson(long id, UpdateUserDetailsDto updateUserDetailsDto, String type) throws UserNotFoundException, UserAlreadyExistException;
    Map<String,Object> getPersonDetails(long id, String type) throws UserNotFoundException;
    List<PersonEntity> getPeople(String type);
    String createPerson(CreatePersonDto createPersonDto, String type) throws UserAlreadyExistException;

}
