package aircrew.version1.mapper;

import aircrew.version1.entity.Fl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public interface FlRepository extends JpaRepository<Fl,Integer> {
    @Query(value = "select * from fl order by id DESC limit 1",nativeQuery =  true)
    Fl getLastFl();

    @Transactional
    @Modifying
    @Query(value = "truncate table fl",nativeQuery = true)
    void truncateFl();
}
