package aircrew.version1.mapper;

import aircrew.version1.entity.DoubleFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface DoubleFlightRepository extends JpaRepository<DoubleFlight,Integer> {

    @Transactional
    @Modifying
    @Query(value = "truncate table doubleflight",nativeQuery = true)
    void truncateDoubleFlight();

    @Query(value = "select flConfirm from confirm ",nativeQuery =  true)
    String findByFlConfirm();

    @Query(value = "select airConfirm from confirm ",nativeQuery =  true)
    String findByAirConfirm();

    @Query(value = "select mpConfirm from confirm ",nativeQuery =  true)
    String findByMpConfirm();

    @Query(value = "select property from property",nativeQuery = true)
    List<String> findAllProperty();

    @Query(value = "select * from doubleFlight where isMore = 1",nativeQuery = true)
    List<DoubleFlight> findByIsMore();

    @Query(value = "select * from doubleFlight where isMore is null order by id Desc limit 1",nativeQuery = true)
    DoubleFlight findByNoIsMoreDescLimit1();

    @Query(value = "select * from doubleFlight where isMore = 1 order by id Desc limit 1",nativeQuery = true)
    DoubleFlight findByIsMoreDescLimit1();

    @Query(value = "select mChangeToJStatus from confirm ",nativeQuery = true)
    String findByMChangeToj();

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.firstQualification=?1 where d.firstQualification=?2",nativeQuery = true)
    void updateFirstQualification(String newQualification,String oldQualification);

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.secondQualification=?1 where d.secondQualification=?2",nativeQuery = true)
    void  updateSecondQualification(String newQualification,String oldQualification);

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.thirdQualification=?1 where d.thirdQualification=?2",nativeQuery = true)
    void updateThirdQualification(String newQualification,String oldQualification);

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.fourthQualification=?1 where d.fourthQualification=?2",nativeQuery = true)
    void updateFourthQualification(String newQualification,String oldQualification);

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.fifthQualification=?1 where d.fifthQualification=?2",nativeQuery = true)
    void updateFifthQualification(String newQualification,String oldQualification);

    @Transactional
    @Modifying
    @Query(value = "delete from property where property = ?1",nativeQuery = true)
    void deleteProperty(String property);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO property(property) VALUES(?1)",nativeQuery = true)
    void addProperty(String property);

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET airConfirm=?1",nativeQuery = true)
    void updateAirConfirm(String airConfirm);

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET flConfirm=?1",nativeQuery = true)
    void updateFlConfirm(String flConfirm);

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET mpConfirm=?1",nativeQuery = true)
    void updateMpConfirm(String mpConfirm);

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET mChangeToJStatus=?1",nativeQuery = true)
    void updateMChangeToJStatus(String mChangeToJStatus);
}
