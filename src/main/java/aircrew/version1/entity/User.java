package aircrew.version1.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String userName;

    @Column
    private String userPwd;

    @Column
    private String department;

    public User(){}

    public User(String name, String password, String department) {
        this.userName = name;
        this.userPwd = password;
        this.department = department;
    }
}
