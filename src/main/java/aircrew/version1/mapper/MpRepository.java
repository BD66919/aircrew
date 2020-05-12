package aircrew.version1.mapper;

import aircrew.version1.entity.Mp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MpRepository extends JpaRepository<Mp,Integer> {

    @Query(value = "select * from mp ORDER BY eid ,date ,time;",nativeQuery = true)
    List<Mp> ByOrder();

    @Query(value = "select * from mp WHERE eid=?1 ORDER BY date ,time",nativeQuery = true)
    List<Mp> findByEid(int eid);
}
