package aircrew.version1.mapper;

import aircrew.version1.entity.Mp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface MpRepository extends JpaRepository<Mp,Integer> {
    @Query(value = "select * from mp ORDER BY eid ,date ,takeOffTime",nativeQuery = true)
    List<Mp> ByOrder();

    @Query(value = "select * from last_mp ORDER BY eid ,date ,takeOffTime",nativeQuery = true)
    List<Mp> LastByOrder();

    @Query(value = "select * from next_mp ORDER BY eid ,date ,takeOffTime",nativeQuery = true)
    List<Mp> NextByOrder();

    @Query(value = "select * from mp WHERE eid=?1 ORDER BY date ,slideTime",nativeQuery = true)
    List<Mp> findByEid(int eid);

    @Query(value = "select * from mp WHERE property='调组乘机乘车' ",nativeQuery = true)
    List<Mp> findByProperty();

    @Transactional
    @Modifying
    @Query(value = "update mp as m set m.post='J机长' where m.post='M见习机长' ",nativeQuery = true)
    void MtoJ();

    @Transactional
    @Modifying
    @Query(value = "truncate table mp",nativeQuery = true)
    void truncateMp();

    @Query(value = "select * from mp order by id DESC limit 1",nativeQuery =  true)
    Mp getLastMp();

}
