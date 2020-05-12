package aircrew.version1.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Air {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private int eid;

    @Column
    private String name;

    @Column
    private String dep;

    @Column
    private String arr;

    @Column
    private String properties;

    @Column
    private String post;

    @Column
    private String startTime;

    @Column
    private String endTime;

//    public Air(Integer id,int eid,String name,String dep,String arr,String properties,String post,String startTime,String endTime){
//        this.id = id;
//        this.eid = eid;
//        this.name = name;
//        this.dep = dep;
//        this.arr = arr;
//        this.properties = properties;
//        this.post = post;
//        this.startTime = startTime;
//        this.endTime = endTime;
//    }

    public Air(){

    }

    @Override
    public String toString(){
        return "Air{" +
                "id=" + id +
                "eid=" + eid +
                "name=" + name +
                "dep=" +  dep +
                "arr=" + arr +
                "properties=" + properties +
                "post=" + post +
                "startTime=" + startTime +
                "endTime=" + endTime +
                "}";
    }
}
