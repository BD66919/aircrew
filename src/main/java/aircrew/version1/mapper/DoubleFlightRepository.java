package aircrew.version1.mapper;

import aircrew.version1.entity.Air;
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

    @Query(value = "select * from doubleFlight order by id DESC limit 1",nativeQuery =  true)
    DoubleFlight findDescLimit1();

    @Query(value = "select flConfirm from confirm ",nativeQuery =  true)
    String getFlConfirm();

    @Query(value = "select airConfirm from confirm ",nativeQuery =  true)
    String getAirConfirm();

    @Query(value = "select mpConfirm from confirm ",nativeQuery =  true)
    String getMpConfirm();

    @Query(value = "select property from property",nativeQuery = true)
    List<String> propertiesList();

    @Query(value = "select * from doubleFlight where isMore = 1",nativeQuery = true)
    List<DoubleFlight> findByIsMore();

    @Query(value = "select * from doubleFlight where isMore = 1 order by id Desc limit 1",nativeQuery = true)
    DoubleFlight findByIsMoreDescLimit1();

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.firstQualification='J机长(原为M见习机长)' where d.firstQualification='M见习机长' ",nativeQuery = true)
    void firstMtoJ();

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.secondQualification='J机长(原为M见习机长)' where d.secondQualification='M见习机长' ",nativeQuery = true)
    void secondMtoJ();

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.thirdQualification='J机长(原为M见习机长)' where d.thirdQualification='M见习机长' ",nativeQuery = true)
    void thirdMtoJ();

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.fourthQualification='J机长(原为M见习机长)' where d.fourthQualification='M见习机长' ",nativeQuery = true)
    void fourthMtoJ();

    @Transactional
    @Modifying
    @Query(value = "update doubleFlight as d set d.fifthQualification='J机长(原为M见习机长)' where d.fifthQualification='M见习机长' ",nativeQuery = true)
    void fifthMtoJ();

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
    @Query(value = "UPDATE confirm SET airConfirm='已确认'",nativeQuery = true)
    void airConfirm();

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET flConfirm='已确认'",nativeQuery = true)
    void flConfirm();

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET mpConfirm='已确认'",nativeQuery = true)
    void mpConfirm();

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET airConfirm='未确认'",nativeQuery = true)
    void cancelAirConfirm();

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET flConfirm='未确认'",nativeQuery = true)
    void cancelFlConfirm();

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirm SET mpConfirm='未确认'",nativeQuery = true)
    void cancelMpConfirm();

}
