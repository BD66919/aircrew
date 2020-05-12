package aircrew.version1.entity;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class NightFlight {

    @Id
    public String number;

    @Column
    public String remarks;

    public NightFlight(){
    }

    public NightFlight(String number,String remarks){
        this.number = number;
        this.remarks = remarks;
    }

}
