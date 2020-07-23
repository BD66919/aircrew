package aircrew.version1.mapper;

import aircrew.version1.entity.Cadre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface CadreRepository extends JpaRepository<Cadre,Integer> {
    @Query(value = "select * from cadre where eid=?1",nativeQuery = true)
    Cadre findByEid(int eid);

}
