package aircrew.version1.controller;

import aircrew.version1.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class CheckController {
    @Autowired
    CheckService checkService;

    /**
     *
     */
    @GetMapping(value="/check/check")
    public String check(Model model){
        return checkService.check(model);
    }

    @GetMapping(value="/check/excelDownload")
    public void check(HttpServletResponse response) throws IOException {
        checkService.excelDownload(response);
    }


}
