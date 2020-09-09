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

    @Query(value = "select dep from air where date=?1 and flightNo =?2 and eid=?3 ORDER BY slideTime ",nativeQuery = true)
    List<String> dep(String date,String no,int eid);

    @Query(value = "select arr from air where date=?1 and flightNo =?2 and eid=?3 ORDER BY slideTime ",nativeQuery = true)
    List<String> arr(String date,String no,int eid);

    @Query(value = "select * from air ORDER BY date,slideTime,eid",nativeQuery = true)
    List<Air> findAllBySort();

    @Query(value = "select * from air where date=?1 and flightNo = ?2 and eid=?3 and flightNumber=?4 ORDER BY slideTime", nativeQuery = true)
    List<Air> getAirCheck(String date,String flightNo,int eid,String flightNumber);

    @Query(value = "select * from last_air where ORDER BY eid ,date ,takeOffTime",nativeQuery = true)
    List<Air> getLastByOrder(int eid,String date,String takeOffTime);

    @Query(value = "select * from next_air where ORDER BY eid ,date ,takeOffTime",nativeQuery = true)
    List<Air> getNextByOrder(int eid,String date,String takeOffTime);

    @Transactional
    @Modifying
    @Query(value = "truncate table air",nativeQuery = true)
    void truncateAir();

    @Query(value = "select * from air order by id DESC limit 1",nativeQuery =  true)
    Air getLastAir();
}
