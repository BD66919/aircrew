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

    @Column
    private Boolean isFlag;

    public DoubleFlight(){

    }

}
