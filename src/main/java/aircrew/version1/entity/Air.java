package aircrew.version1.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "air")
public class Air {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String date;

    @Column
    private String flightNo;

    @Column
    private String type;

    @Column
    private String flightNumber;

    @Column
    private String dep;

    @Column
    private String slideTime;

    @Column
    private String takeOffTime;

    @Column
    private String arr;

    @Column
    private String landTime;

    @Column
    private String inPlaceTime;

    @Column
    private String property;

    @Column
    private int eid;

    @Column
    private String name;

    @Column
    private String position;

    @Column
    private String qualify;

    @Column
    private String statement;

    public Air() {
    }
}

