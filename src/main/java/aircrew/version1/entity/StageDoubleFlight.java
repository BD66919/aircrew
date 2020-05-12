package aircrew.version1.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class StageDoubleFlight {

    @Id
    public String number;

    @Column
    public String remarks;

    public StageDoubleFlight(){

    }

    public StageDoubleFlight(String number,String remarks){
        this.number = number;
        this.remarks = remarks;
    }

}
