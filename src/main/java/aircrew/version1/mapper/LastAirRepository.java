package aircrew.version1.mapper;

import aircrew.version1.entity.LastAir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface LastAirRepository extends JpaRepository<LastAir,Integer> {
    @Query(value = "select * from last_air order by id DESC limit 1",nativeQuery =  true)
    LastAir findOrderByIdDescLimit1();

    @Query(value = "select * from last_air ORDER BY eid ,date ,takeOffTime",nativeQuery = true)
    List<LastAir> findOrderByEidAndDateAndTakeOffTime();

    @Query(value = "select * from last_air ORDER BY eid ,date ,slideTime",nativeQuery = true)
    List<LastAir> findOrderByEidAndDateAndSlideTime();

    @Transactional
    @Modifying
    @Query(value = "truncate table last_air",nativeQuery = true)
    void truncateLastAir();

}
