package aircrew.version1.service;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {
    String user(Model model);
    String deleteUser(@PathVariable String id);
    Map<String,Object> addUser(HttpServletRequest request);
}
