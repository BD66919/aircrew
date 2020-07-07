package aircrew.version1.mapper;

import aircrew.version1.entity.LastMp;
import aircrew.version1.entity.Mp;
import aircrew.version1.entity.NextMp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public interface NextMpRepository extends JpaRepository<NextMp,Integer> {

    @Query(value = "select * from next_mp ORDER BY eid ,date ,time;",nativeQuery = true)
    List<Mp> ByOrder();

    @Query(value = "select * from next_mp WHERE eid=?1 ORDER BY date ,time",nativeQuery = true)
    List<NextMp> findByEid(int eid);

    @Transactional
    @Modifying
    @Query(value = "truncate table next_mp",nativeQuery = true)
    void truncateNextMp();

    @Query(value = "select * from next_mp order by id DESC limit 1",nativeQuery =  true)
    NextMp getNextLastMp();

}
