package aircrew.version1.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "last_mp")
public class LastMp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private int eid;

    @Column
    private String name;

    @Column
    private String date;

    @Column
    private String takeOffTime;

    @Column
    private String flightNo;

    @Column
    private String airplaneNumber;

    @Column
    private String type;

    @Column
    private String property;

    @Column
    private String post;

    @Column
    private String isNight;

    @Column
    private String isInternational;

    @Column
    private String isCost;

    @Column
    private String isTake;

    @Column
    private String realTime;

    @Column
    private String airLine;

    @Column
    private String tcc;

    @Column
    private String specialAirlineProportion;

    @Column
    private String specialAirlineDays;

    @Column
    private String eachAirlineTime;

    @Column
    private String eachAirlineProportion;

    @Column
    private String isEachAirlineInternational;

    @Column
    private String reversal;

    @Column
    private String remarks;

    @Column
    private String payTime;

    @Column
    private String organization;

    @Column
    private String department;

    @Column
    private String errorStatement;

    public LastMp(){

    }


}
