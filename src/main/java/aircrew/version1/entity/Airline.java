package aircrew.version1.entity;

import lombok.Data;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Airline {
    @Id
    private String Airline;

    @Column
    private String remarks;

    @Override
    public String toString(){
        return "Airline=" + Airline +
                " remarks=" + remarks;
    }
}
