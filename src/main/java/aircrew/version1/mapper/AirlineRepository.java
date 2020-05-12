package aircrew.version1.mapper;

import aircrew.version1.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface AirlineRepository extends JpaRepository<Airline,String> {
    @Query(value = "select * from airline where airline=?1",nativeQuery = true)
    public Airline findByAirline(String airline);
}
