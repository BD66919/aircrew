package aircrew.version1.controller;

import aircrew.version1.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CheckController {
    @Autowired
    CheckService checkService;

    /**
     * 跳转至飞行员飞行及调度路线校验页面
     */
    @GetMapping(value="/check/check")
    public String check(Model model){
        return checkService.check(model);
    }

    /**
     * 跳转至飞行员飞行及调度路线校验页面
     */
    @GetMapping(value="/check/socCheck")
    public String socCheck(Model model){
        return checkService.socCheck(model);
    }

}
