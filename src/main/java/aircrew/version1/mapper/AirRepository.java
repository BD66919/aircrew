package aircrew.version1.mapper;

import aircrew.version1.entity.Air;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface AirRepository extends JpaRepository<Air,Integer> {

}
