package aircrew.version1.mapper;

import aircrew.version1.entity.Air;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface AirRepository extends JpaRepository<Air,Integer> {
    @Query(value = "select position from air where date=?1 and flightNo =?2 and eid=?3",nativeQuery = true)
    List<Integer> number(String date,String no,int eid);

    @Query(value = "select dep from air where date=?1 and flightNo =?2 and eid=?3 ORDER BY takeOffTime ",nativeQuery = true)
    List<String> dep(String date,String no,int eid);

    @Query(value = "select arr from air where date=?1 and flightNo =?2 and eid=?3 ORDER BY takeOffTime",nativeQuery = true)
    List<String> arr(String date,String no,int eid);

    @Query(value = "select * from air  ORDER BY date,takeOffTime",nativeQuery = true)
    List<Air> findAllBySort();

    @Transactional
    @Modifying
    @Query(value = "truncate table air",nativeQuery = true)
    void truncateAir();

    @Query(value = "select * from air order by id DESC limit 1",nativeQuery =  true)
    Air getLastAir();
}
