package aircrew.version1.shiro;

import aircrew.version1.mapper.UserRepository;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm{
    @Autowired
    private UserRepository userRepository;

    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if(arg0.getPrimaryPrincipal().equals("财务")) {
            info.addStringPermission("user:data");
            info.addStringPermission("user:check");
        }
        if(arg0.getPrimaryPrincipal().equals("飞行")) {
            info.addStringPermission("user:add");
            info.addStringPermission("user:update");
        }
        if(arg0.getPrimaryPrincipal().equals("人力")) {
            info.addStringPermission("user:add");
            info.addStringPermission("user:update");
        }


        return info;
    }

    /**
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken)arg0;
        String userName = userRepository.getUserName(token.getUsername());
        String loginPwd = new String(token.getPassword());
        String userPwd = userRepository.getUserPwd(userName,loginPwd);
        if(!token.getUsername().equals(userName)){
            return null;
        }
        if(!loginPwd.equals(userPwd))
            throw new IncorrectCredentialsException();
        String department = userRepository.getDepartment(userName);
        return new SimpleAuthenticationInfo(department,userPwd,"");
    }

}