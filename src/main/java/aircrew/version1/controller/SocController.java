package aircrew.version1.controller;

import aircrew.version1.entity.Soc;
import aircrew.version1.mapper.SocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SocController {

    @Autowired
    SocRepository socRepository;

    @GetMapping("/soc/check")
    public String checkSoc(Model model){
        List<Soc>  plane = socRepository.findAll();
        List<Soc>  doublePlane = new ArrayList<>();
        for(Soc pl:plane)
            System.out.println(pl);
        return "";
    }
}
