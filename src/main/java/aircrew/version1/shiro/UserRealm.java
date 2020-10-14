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
        if(arg0.getPrimaryPrincipal().equals("系统管理员")){
            info.addStringPermission("user:admin");
            info.addStringPermission("user:data");
            info.addStringPermission("user:update");
            info.addStringPermission("user:check");
            info.addStringPermission("user:fl");
            info.addStringPermission("user:air");
            info.addStringPermission("user:mp");
        }
        if(arg0.getPrimaryPrincipal().equals("财务")) {
            info.addStringPermission("user:data");
            info.addStringPermission("user:check");
            info.addStringPermission("user:fl");
        }
        if(arg0.getPrimaryPrincipal().equals("飞行")) {
            info.addStringPermission("user:add");
            info.addStringPermission("user:update");
            info.addStringPermission("user:air");
        }
        if(arg0.getPrimaryPrincipal().equals("人力")) {
            info.addStringPermission("user:add");
            info.addStringPermission("user:update");
            info.addStringPermission("user:mp");
        }


        return info;
    }

    /**
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken)arg0;
        String userName = userRepository.findUserNameByLoginName(token.getUsername());
        String loginPwd = new String(token.getPassword());
        String userPwd = userRepository.findUserPwdByLoginNameAndLoginPwd(userName,loginPwd);
        if(!token.getUsername().equals(userName)){
            return null;
        }
        if(!loginPwd.equals(userPwd))
            throw new IncorrectCredentialsException();
        String department = userRepository.findDepartmentByLoginName(userName);
        return new SimpleAuthenticationInfo(department,userPwd,"");
    }

}