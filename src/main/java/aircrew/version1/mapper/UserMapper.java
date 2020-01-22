package aircrew.version1.mapper;

public interface UserMapper {
    public String getUserName(String loginName);

    public String getUserPwd(String loginPwd,String loginName);

}
