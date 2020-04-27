package aircrew.version1.mapper;

import aircrew.version1.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface AirlineRepository extends JpaRepository<Airline,String> {

}
