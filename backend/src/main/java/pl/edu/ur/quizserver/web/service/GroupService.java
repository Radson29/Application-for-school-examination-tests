package pl.edu.ur.quizserver.web.service;

import pl.edu.ur.pdflib.dto.GroupedPeopleDTO;
import pl.edu.ur.quizserver.persistence.pojo.GroupedPeoplePojo;
import pl.edu.ur.quizserver.web.error.*;

import java.util.List;

public interface GroupService {
    List<GroupedPeoplePojo> getGroupedPeople();
    void createGroup(String groupName) throws GroupAlreadyExistException;
    void deleteGroup(long id) throws GroupNotFoundException;
    void addPersonToGroup(long groupId, long personId) throws UserNotFoundException, GroupNotFoundException, UserAlreadyExistInGroupException;
    void removePersonFromGroup(long groupId, long personId) throws UserNotFoundException, GroupNotFoundException, UserNotExistInGroupException;
    List<GroupedPeopleDTO> mapToDTO(List<GroupedPeoplePojo> groupedPeoplePojos);
}
