package aircrew.version1.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class DoubleFlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private  String date;

    @Column
    private  String no;

    @Column
    private  String line;

    @Column
    private  String tcc;

    @Column
    private  String firstPosition;

    @Column
    private  String firstQualification;

    @Column
    private  String secondPosition;

    @Column
    private  String secondQualification;

    @Column
    private  String flightCombine;

    @Column
    private  String doubleLine;

    @Column
    private  String nightFlight;

    @Column
    private  String stageDoubleFlight;

    @Column
    private  String flightCheck;

    @Column
    private  String cadre;

    @Column
    private  String airChangeRecord;

    @Column
    private  String mpChangeRecord;

    @Column
    private  String airRemark;

    @Column
    private  String mpRemark;

    public DoubleFlight(){

    }

//    public DoubleFlight(int id, String date, String no, String line, String tcc, String firstPosition, String firstQualification, String secondPosition, String secondQualification,
//                        String flightCombine, String doubleLine, String nightFlight, String stageDoubleFlight, String flightCheck, String cadre, String airChangeRecord, String mpChangeRecord, String remarks){
//        this.id = id;
//        this.date = date;
//        this.no = no;
//        this.line = line;
//        this.tcc = tcc;
//        this.firstPosition = firstPosition;
//        this.firstQualification = firstQualification;
//        this.secondPosition = secondPosition;
//        this.secondQualification = secondQualification;
//        this.flightCombine = flightCombine;
//        this.doubleLine = doubleLine;
//        this.nightFlight = nightFlight;
//        this.stageDoubleFlight = stageDoubleFlight;
//        this.flightCheck = flightCheck;
//        this.cadre =cadre;
//        this.airChangeRecord = airChangeRecord;
//        this.mpChangeRecord = mpChangeRecord;
//        this.remarks = remarks;
//    }



}
