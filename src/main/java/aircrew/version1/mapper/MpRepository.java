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

    @Query(value = "select * from mp ORDER BY date,flightNo,takeOffTime,property",nativeQuery = true)
    List<Mp> findOrderByDateAndFlightNoAndTakeOffTimeAndProperty();

    @Query(value = "select * from mp WHERE property=?1 ",nativeQuery = true)
    List<Mp> findByProperty(String property);

    @Query(value = "select * from mp WHERE date=?1 and flightNo=?2 and type=?3 and takeOffTime=?4",nativeQuery = true)
    List<Mp> findByDateAndFlightNoAndType(String date,String flightNo,String type,String takeOffTime);

    @Transactional
    @Modifying
    @Query(value = "update mp as m set m.post=?1 where m.post=?2 and m.property='正班'",nativeQuery = true)
    void updatePost(String newPost,String oldPost);

    @Transactional
    @Modifying
    @Query(value = "truncate table mp",nativeQuery = true)
    void truncateMp();

    @Query(value = "select * from mp order by id DESC limit 1",nativeQuery =  true)
    Mp findOrderByIdDescLimit1();

}
