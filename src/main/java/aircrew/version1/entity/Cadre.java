package aircrew.version1.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Cadre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int eid;

    @Column
    private String name;

    @Column
    private String department;

    @Column
    private String qualify;

    @Column
    private String category;

    public Cadre(){

    }
}
