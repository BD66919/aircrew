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
    @Query(value = "select * from air ORDER BY date,slideTime,eid",nativeQuery = true)
    List<Air> findOrderByDateAndSlideTimeAndEid();

    @Query(value = "select * from air where date=?1 and flightNo = ?2 and eid=?3 and flightNumber=?4 ORDER BY slideTime", nativeQuery = true)
    List<Air> findByDateAndFlightNoAndEidAndFlightNumberOrderBySlideTime(String date,String flightNo,int eid,String flightNumber);

    @Query(value = "select * from air where date=?1 and eid=?2 and flightNo like CONCAT('%',?3,'%')  ORDER BY slideTime", nativeQuery = true)
    List<Air> findByDateAndEidLikeFlightNoOrderBySlideTime(String date,int eid,String flightNo);

    @Query(value = "select * from air WHERE date=?1 and flightNo=?2 and eid=?3 ",nativeQuery = true)
    List<Air> findByFlightNo(String date,String flightNo,int eid);

    @Transactional
    @Modifying
    @Query(value = "truncate table air",nativeQuery = true)
    void truncateAir();

    @Query(value = "select * from air order by id DESC limit 1",nativeQuery =  true)
    Air findOrderByIdDescLimit1();
}
