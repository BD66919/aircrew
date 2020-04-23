package aircrew.version1.controller;

import aircrew.version1.entity.Pilot;
import aircrew.version1.mapper.PilotMapper;
import aircrew.version1.mapper.PilotRepository;
import aircrew.version1.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
    UserMapper userMapper;
    String userName;
    String userPwd;

    @Autowired
    PilotMapper pilotMapper;

    @Autowired
    PilotRepository pilotRepository;

    @PostMapping(value = "/userLogin")
    public String userLogin(@RequestParam("loginName") String loginName,
                            @RequestParam("loginPwd") String loginPwd,
                            Map<String,Object> map,
                            HttpSession session,
                            Model model){
        userName = userMapper.getUserName(loginName);
        userPwd = userMapper.getUserPwd(loginPwd,loginName);
        if (userName == null || userPwd == null){
            map.put("message","用户名或者密码错误");
            return "login.html";
        }else if (userName.equals(loginName) && userPwd.equals(loginPwd)){
            session.setAttribute("userName",userName);

            return "main.html";
        }else{
            map.put("message","用户名或者密码错误");
            return "login.html";
        }
    }

    @GetMapping("/main")
    public String main(Model model){
        List<Pilot> pilotList = pilotMapper.ByOrder();
        List<Pilot> errorPilotList = new ArrayList<>();
        List<Pilot> fisrtPilotList = new ArrayList<>();
        if(pilotList.isEmpty())
            return  "login.html";
        int number = 0;
        int id = pilotList.get(0).getId();
        int eid = pilotList.get(0).getEid();
        int length = pilotList.get(0).getTcc().length();
        String date ;
        String a1 ;
        String b ;
        Pilot pilot1;
        Pilot pilot2;
        date = pilotRepository.getByDate(id);
        try{date = date.substring(0,4)+"年"+date.substring(5,7);
        } catch(Exception e) {
            date = "空空空空空空空空空空空空";
        }
        String end = pilotList.get(0).getTcc();
        end = end.substring(length-3,length);
        for(Pilot a:pilotList) {
            length = a.getTcc().length();
            if (length==0)//长度为零时进入下一次判断
                continue;
            if(a.getError()==1){
                end = a.getTcc().substring(length - 3, length);
                continue;
            }
            if (eid != a.getEid()) {
                eid = a.getEid();
                end = a.getTcc().substring(length - 3, length);
                continue;
            }
            b = a.getTcc();
            a1 = b.substring(0,3);
            if (!end.equals(a1)){
                errorPilotList.add(a);
            }
            length = a.getTcc().length();
            end = b.substring(length-3,length);
        }
        fisrtPilotList.add(errorPilotList.get(0));
        for(int i = 0;i<errorPilotList.size()-1;i++){
            pilot1 = errorPilotList.get(i);
            pilot2 = errorPilotList.get(i+1);
            if(pilot1.getEid()!=pilot2.getEid()){
                fisrtPilotList.add(pilot1);
                number++;
            }
        }
        model.addAttribute("firstPilotList",fisrtPilotList);
        model.addAttribute("number",number);
        model.addAttribute("date",date);
        return "main.html";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        Enumeration em = request.getSession().getAttributeNames();
        while(em.hasMoreElements()){
            request.getSession().removeAttribute(em.nextElement().toString());
        }
        return "login.html";
    }
}
