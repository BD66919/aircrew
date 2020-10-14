package aircrew.version1.mapper;

import aircrew.version1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User,String> {
    @Query(value = "SELECT userName FROM user WHERE userName=?1",nativeQuery = true)
    String findUserNameByLoginName(String loginName);

    @Query(value = "SELECT userPwd FROM user WHERE userName=?1 and userPwd=?2",nativeQuery = true)
    String findUserPwdByLoginNameAndLoginPwd(String loginName,String loginPwd);

    @Query(value = "SELECT department FROM user WHERE userName=?1",nativeQuery = true)
    String findDepartmentByLoginName(String loginName);

    @Transactional
    @Modifying
    @Query(value = "delete FROM user WHERE id=?1",nativeQuery = true)
    void deleteById(int parseInt);
}
