package aircrew.version1.mapper;

import aircrew.version1.entity.StageDoubleFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface StageDoubleFlightRepository extends JpaRepository<StageDoubleFlight,String> {

}
