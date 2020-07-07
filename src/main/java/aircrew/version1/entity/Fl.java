package aircrew.version1.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "fl")
public class Fl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String date;

    @Column
    private String flightNo;

    @Column
    private String airline;

    @Column
    private String dep;

    @Column
    private String arr;

    @Column
    private String type;

    @Column
    private String airplaneNumber;

    @Column
    private String timeType;

    @Column
    private String slideTime;

    @Column
    private String takeOffTime;

    @Column
    private String landTime;

    @Column
    private String inPlaceTime;

    public Fl(){
    }


}
