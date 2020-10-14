package aircrew.version1.mapper;

import aircrew.version1.entity.NextAir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


public interface NextAirRepository extends JpaRepository<NextAir,Integer> {

    @Query(value = "select * from next_air order by id DESC limit 1",nativeQuery =  true)
    NextAir findOrderByIdDescLimit1();

    @Query(value = "select * from next_air ORDER BY eid ,date ,takeOffTime",nativeQuery = true)
    List<NextAir> findOrderByEidAndDateAndTakeOffTime();

    @Query(value = "select * from next_air ORDER BY eid ,date ,slideTime",nativeQuery = true)
    List<NextAir> findOrderByEidAndDateAndSlideTime();

    @Transactional
    @Modifying
    @Query(value = "truncate table next_air",nativeQuery = true)
    void truncateNextAir();
}
