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

    public Mp(){
    }

    public Mp(String date,int eid,String name,String tcc,String flightNo,String property){
        this.date = date;
        this.eid = eid;
        this.name = name;
        this.tcc = tcc;
        this.flightNo = flightNo;
        this.property = property;
    }

    public Mp(String date,int eid,String name,String tcc,String flightNo,String property,String post,String takeOffTime,String type,String airplaneNumber){
        this.date = date;
        this.eid = eid;
        this.name = name;
        this.tcc = tcc;
        this.flightNo = flightNo;
        this.property = property;
        this.post = post;
        this.takeOffTime = takeOffTime;
        this.type = type;
        this.airplaneNumber = airplaneNumber;
    }

    public Mp(LastMp lastMp) {
        this.id = lastMp.getId();
        this.eid = lastMp.getEid();
        this.name = lastMp.getName();
        this.date = lastMp.getDate();
        this.takeOffTime = lastMp.getTakeOffTime();
        this.flightNo = lastMp.getFlightNo();
        this.airplaneNumber = lastMp.getAirplaneNumber();
        this.type = lastMp.getType();
        this.property = lastMp.getProperty();
        this.post = lastMp.getPost();
        this.isNight = lastMp.getIsNight();
        this.isInternational = lastMp.getIsInternational();
        this.isCost = lastMp.getIsCost();
        this.isTake = lastMp.getIsTake();
        this.realTime = lastMp.getRealTime();
        this.airLine = lastMp.getAirLine();
        this.tcc = lastMp.getTcc();
        this.specialAirlineProportion = lastMp.getSpecialAirlineProportion();
        this.specialAirlineDays = lastMp.getSpecialAirlineDays();
        this.eachAirlineTime = lastMp.getEachAirlineTime();
        this.eachAirlineProportion = lastMp.getEachAirlineProportion();
        this.isEachAirlineInternational = lastMp.getIsEachAirlineInternational();
        this.reversal = lastMp.getReversal();
        this.remarks = lastMp.getRemarks();
        this.payTime = lastMp.getPayTime();
        this.organization = lastMp.getOrganization();
        this.department = lastMp.getDepartment();
        this.errorStatement = lastMp.getErrorStatement();
    }

    public Mp(NextMp nextMp) {
        this.id = nextMp.getId();
        this.eid = nextMp.getEid();
        this.name = nextMp.getName();
        this.date = nextMp.getDate();
        this.takeOffTime = nextMp.getTakeOffTime();
        this.flightNo = nextMp.getFlightNo();
        this.airplaneNumber = nextMp.getAirplaneNumber();
        this.type = nextMp.getType();
        this.property = nextMp.getProperty();
        this.post = nextMp.getPost();
        this.isNight = nextMp.getIsNight();
        this.isInternational = nextMp.getIsInternational();
        this.isCost = nextMp.getIsCost();
        this.isTake = nextMp.getIsTake();
        this.realTime = nextMp.getRealTime();
        this.airLine = nextMp.getAirLine();
        this.tcc = nextMp.getTcc();
        this.specialAirlineProportion = nextMp.getSpecialAirlineProportion();
        this.specialAirlineDays = nextMp.getSpecialAirlineDays();
        this.eachAirlineTime = nextMp.getEachAirlineTime();
        this.eachAirlineProportion = nextMp.getEachAirlineProportion();
        this.isEachAirlineInternational = nextMp.getIsEachAirlineInternational();
        this.reversal = nextMp.getReversal();
        this.remarks = nextMp.getRemarks();
        this.payTime = nextMp.getPayTime();
        this.organization = nextMp.getOrganization();
        this.department = nextMp.getDepartment();
        this.errorStatement = nextMp.getErrorStatement();
    }
}
