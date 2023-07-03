package pl.edu.ur.quizserver.persistence.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.ur.quizserver.web.util.Views;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@JsonView({Views.PeopleAll.class})
@Entity
@Getter
@Setter
@NoArgsConstructor
@Data
@Table(name  = "people")
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String firstName;

    @Column(nullable = false, length = 20)
    private String lastName;

    @JsonView(Views.PersonDetails.class)
    @Column(nullable = false, length = 11)
    private String pesel;

    @JsonView(Views.PersonDetails.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @JsonView(Views.PersonDetails.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date modifiedAt;

    @JsonView(Views.PersonDetails.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @JsonIgnore
    @OneToOne(mappedBy = "person", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private LoginEntity login;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "people_groups", joinColumns = @JoinColumn(name = "personId"), inverseJoinColumns = @JoinColumn(name = "groupId"))
    private List<PersonGroupEntity> groups;

    public PersonEntity(String firstName, String lastName, String pesel, Date createdAt, Date modifiedAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @JsonIgnore
    public boolean isDeleted()
    {
        return deletedAt != null;
    }
}
