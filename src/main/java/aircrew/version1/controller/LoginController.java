package aircrew.version1.controller;
import aircrew.version1.mapper.*;
import aircrew.version1.service.DataService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Jiangzhendong
 * @param loginName 网页页面提交的登陆名
 * @param loginPwd 网页页面提交的密码
 * @param userName 数据库中的用户名
 * @param userPwd 数据库中的用户密码
 * @Description 该类用于用户登陆页面用户名和密码检查以及跳转至主页面
 *
 */

@Controller
public class LoginController {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private DataService dataService;

    @Autowired
    public LoginController(DataService dataService){
        this.dataService = dataService;
    }

    //生成32位小写MD5字符串
    private static String getMD5String(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value="/userLogin",method= RequestMethod.POST)
    public String userLogin(@RequestParam("loginName") String loginName,
                            @RequestParam("loginPwd") String loginPwd,
                            Map<String,Object> map,
                            Model model,
                            HttpSession session){
        Subject subject = SecurityUtils.getSubject();
        loginPwd = getMD5String(loginPwd);
        System.out.println(loginPwd);
        UsernamePasswordToken token = new UsernamePasswordToken(loginName,loginPwd);
        String userName = userRepository.findUserNameByLoginName(loginName);
        String department = userRepository.findDepartmentByLoginName(loginName);
        try{
            subject.login(token);
            session.setAttribute("userName",userName);
            session.setAttribute("department",department);
            model.addAllAttributes(dataService.login(session));
            return "data/data.html";
        }catch(UnknownAccountException e){
            map.put("message","用户名错误");
            return "login.html";
        }catch(IncorrectCredentialsException e){
            map.put("message","密码错误");
            return "login.html";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        Enumeration em = request.getSession().getAttributeNames();
        while(em.hasMoreElements()){
            request.getSession().removeAttribute(em.nextElement().toString());
        }
        Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                subject.logout();
            }
        return "login.html";
    }

}
