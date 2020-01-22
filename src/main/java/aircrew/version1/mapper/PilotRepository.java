package aircrew.version1.mapper;


import aircrew.version1.entity.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
public interface PilotRepository extends JpaRepository<Pilot,Integer> {

    @Query(value = "select * from pilot where if(?1 !='',name=?1,1=1) and if(?2 !='',eid=?2,1=1) and if(?3 !='',line=?3,1=1)  and if(?4 !='',tcc=?4,1=1)  and if(?5 !='',properties=?5,1=1)  and if(?6 !=null,date=?6,1=1)  ", nativeQuery = true)
    List<Pilot> select(String name, String eid, String line, String tcc, String properties, String date,int error) ;

    @Query(value = "select * from pilot where  eid = ?1", nativeQuery = true)
    List<Pilot> getByEid(int eid);

    @Query(value = "select date from pilot where id =?1",nativeQuery = true)
    String getByDate(Integer id);

    @Query(value = "select * from pilot where error=1",nativeQuery = true)
    List<Pilot> selectByError();

    @Transactional
    @Modifying
    @Query(value = "UPDATE pilot SET error = 1 where id = ?1",nativeQuery = true)
    void updataErrorById(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE pilot SET error = 0 where id = ?1",nativeQuery = true)
    void updataByError(Integer id);


}
