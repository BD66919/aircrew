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

    public Air(LastAir lastAir) {
        this.id = lastAir.getId();
        this.date = lastAir.getDate();
        this.flightNo = lastAir.getFlightNo();
        this.type = lastAir.getType();
        this.flightNumber = lastAir.getFlightNumber();
        this.dep = lastAir.getDep();
        this.slideTime = lastAir.getSlideTime();
        this.takeOffTime = lastAir.getTakeOffTime();
        this.arr = lastAir.getArr();
        this.landTime = lastAir.getLandTime();
        this.inPlaceTime = lastAir.getInPlaceTime();
        this.property = lastAir.getProperty();
        this.eid = lastAir.getEid();
        this.name = lastAir.getName();
        this.position = lastAir.getPosition();
        this.qualify = lastAir.getQualify();
        this.statement = lastAir.getStatement();
    }

    public Air(NextAir nextAir) {
        this.id = nextAir.getId();
        this.date = nextAir.getDate();
        this.flightNo = nextAir.getFlightNo();
        this.type = nextAir.getType();
        this.flightNumber = nextAir.getFlightNumber();
        this.dep = nextAir.getDep();
        this.slideTime = nextAir.getSlideTime();
        this.takeOffTime = nextAir.getTakeOffTime();
        this.arr = nextAir.getArr();
        this.landTime = nextAir.getLandTime();
        this.inPlaceTime = nextAir.getInPlaceTime();
        this.property = nextAir.getProperty();
        this.eid = nextAir.getEid();
        this.name = nextAir.getName();
        this.position = nextAir.getPosition();
        this.qualify = nextAir.getQualify();
        this.statement = nextAir.getStatement();
    }
}

