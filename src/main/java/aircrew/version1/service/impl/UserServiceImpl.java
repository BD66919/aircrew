package aircrew.version1.service.impl;


import aircrew.version1.entity.User;
import aircrew.version1.mapper.UserRepository;
import aircrew.version1.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {


    final private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @Override
    public String deleteUser(@PathVariable String id) {
        userRepository.deleteById(Integer.parseInt(id));
        return "redirect:/user/user";
    }


    @Override
    public String user(Model model) {
        List<User> userList = userRepository.findAll();
        System.out.println(userList);
        model.addAttribute("userList",userList);
        return "/user/user";
    }

    @Override
    public Map<String,Object> addUser(HttpServletRequest request){
        String name = request.getParameter("name");
        String password = getMD5String(request.getParameter("password"));
        String department = request.getParameter("department");
        User user = new User(name,password,department);
        userRepository.save(user);
        Map<String,Object> map = new HashMap<>();
        map.put("msg","添加成功");
        return map;
    }
}
