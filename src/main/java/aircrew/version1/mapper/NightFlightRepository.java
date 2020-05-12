package aircrew.version1.mapper;


import aircrew.version1.entity.NightFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface NightFlightRepository extends JpaRepository<NightFlight,String> {
}
