package aircrew.version1.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class User {
    @Id
    private String userName;

    @Column
    private String userPwd;

    @Column
    private String department;

}
