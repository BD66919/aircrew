package aircrew.version1.mapper;

import aircrew.version1.entity.Mp;
import aircrew.version1.entity.NextMp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public interface NextMpRepository extends JpaRepository<NextMp, Integer> {
    @Query(value = "select * from next_mp order by id DESC limit 1", nativeQuery = true)
    NextMp findByIdByDescByLimit1();

    @Query(value = "select * from next_mp WHERE property=?1 ORDER BY eid,date,takeOffTime ",nativeQuery = true)
    List<NextMp> findByPropertyOrderByEidAndDateAndTakeOffTime(String property);

    @Transactional
    @Modifying
    @Query(value = "truncate table next_mp", nativeQuery = true)
    void truncateNextMp();

    @Query(value = "select * from next_mp WHERE property='调组乘机乘车' ", nativeQuery = true)
    List<NextMp> findByProperty();

}
