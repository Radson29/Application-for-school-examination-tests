package pl.edu.ur.pdflib.dto;

import java.util.ArrayList;
import java.util.List;

public class GroupedPeopleDTO {
    private String groupName;
    private List<PersonDTO> people = new ArrayList<>();

    public GroupedPeopleDTO(String groupname, List<PersonDTO> people) {
        this.groupName = groupname;
        this.people = people;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<PersonDTO> getPeople() {
        return people;
    }

    public void setPeople(List<PersonDTO> people) {
        this.people = people;
    }

    public void addPerson(PersonDTO person) {
        this.people.add(person);
    }
}
