package aircrew.version1.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "mp")
public class Mp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private int eid;

    @Column
    private String date;

    @Column
    private String time;

    @Column
    private String FlightNo;

    @Column
    private String properties;

    @Column
    private String post;

    @Column
    private String line;

    @Column
    private String tcc;

    public Mp(){

    }


}
