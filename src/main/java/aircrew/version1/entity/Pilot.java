package aircrew.version1.entity;

import javax.persistence.*;



/**
 * @author Jiangzhendong
 * @Description 该类为Excel的实体类
 *
 */
@Entity
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getTcc() {
        return tcc;
    }

    public void setTcc(String tcc) {
        this.tcc = tcc;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
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
