package pl.edu.ur.quizserver.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "permissions")
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String groupName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String permission;

    public PermissionEntity(String groupName, String name, String permission) {
        this.groupName = groupName;
        this.name = name;
        this.permission = permission;
    }
}
