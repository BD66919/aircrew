package aircrew.version1.mapper;

import aircrew.version1.entity.LastMp;
import aircrew.version1.entity.Mp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public interface LastMpRepository extends JpaRepository<LastMp,Integer> {
    @Query(value = "select * from last_mp order by id DESC limit 1",nativeQuery =  true)
    LastMp findOrderByIdDescLimit1();

    @Query(value = "select * from last_mp WHERE property=?1 ORDER BY eid,date,takeOffTime ",nativeQuery = true)
    List<LastMp> findByPropertyOrderByEidAndDateAndTakeOffTime(String property);

    @Transactional
    @Modifying
    @Query(value = "truncate table last_mp",nativeQuery = true)
    void truncateLastMp();

    @Query(value = "select * from last_mp WHERE property='调组乘机乘车' ",nativeQuery = true)
    List<LastMp> findByProperty();

}
