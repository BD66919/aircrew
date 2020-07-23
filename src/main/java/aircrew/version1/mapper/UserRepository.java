package aircrew.version1.mapper;


import aircrew.version1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User,String> {
    @Query(value = "SELECT userName FROM user WHERE userName=?1",nativeQuery = true)
    String getUserName(String loginName);

    @Query(value = "SELECT userPwd FROM user WHERE userName=?1 and userPwd=?2",nativeQuery = true)
    String getUserPwd(String loginName,String loginPwd);

    @Query(value = "SELECT department FROM user WHERE userName=?1",nativeQuery = true)
    String getDepartment(String loginName);

}
