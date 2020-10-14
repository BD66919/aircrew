package aircrew.version1.controller;

import aircrew.version1.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/user")
    public String user(Model model){
        return userService.user(model);
    }

    @PostMapping(value = "/deleteUser={id}")
    public String deleteUser(@PathVariable String id){
        return userService.deleteUser(id);
    }

    @PostMapping(value = "/addUser")
    @ResponseBody
    public Map<String,Object> addUser(HttpServletRequest request){
        return userService.addUser(request);
    }
}
