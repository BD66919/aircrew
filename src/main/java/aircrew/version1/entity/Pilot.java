package aircrew.version1.entity;

import lombok.Data;
import javax.persistence.*;



/**
 * @author Jiangzhendong
 * @Description 该类为Excel的实体类
 *
 */
@Entity
@Data
public class Pilot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private int eid;

    @Column
    private String line;

    @Column
    private String tcc;

    @Column
    private String properties;

    @Column
    private String date;

    @Column
    private int error;

    public Pilot(Integer id, String name, int eid, String line, String tcc, String properties, String date,int error) {
        this.id = id;
        this.name = name;
        this.eid = eid;
        this.line = line;
        this.tcc = tcc;
        this.properties = properties;
        this.date = date;
        this.error = error;
    }

    public Pilot(){

    }
    @Override
    public String toString(){
        return "Pilot{" +
                "id=" + id +
                ",name=" + name +
                ", eid=" + eid +
                ", line=" + line +
                ", tcc=" + tcc +
                ", properties=" + properties +
                ", date=" + date +
                ",error=" + error +
                '}';
    }

}
