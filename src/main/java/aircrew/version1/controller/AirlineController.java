package aircrew.version1.controller;

import aircrew.version1.entity.Airline;
import aircrew.version1.mapper.AirlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AirlineController {
    @Autowired
    AirlineRepository airlineRepository;

    @PostMapping("/airline/add")
    public String add(Airline airline, Model model)
    {
        if(airlineRepository.getOne(airline.getAirline())==null){
            airlineRepository.save(airline);
            model.addAttribute("msg","添加成功");
            return "airline";
        }
        model.addAttribute("msg","该航线已存在，重复添加失败");
        return "airline";
    }

    @PostMapping("/airline/delete")
    public String delete(Airline airline, Model model)
    {
        if(airlineRepository.getOne(airline.getAirline())!=null){
            airlineRepository.delete(airline);
            model.addAttribute("msg","删除成功");
            return "airline";
        }
        model.addAttribute("msg","该航线不存在数据库中，无法删除");
        return "airline";
    }

    @GetMapping("/airline/select")
    public String select(Model model){
        List<Airline>  airlines = airlineRepository.findAll();
        model.addAttribute("airlines",airlines);
        System.out.println(airlines);
        return "airline";
    }

//    @PostMapping("/airline/updata")
//    public String updata(Model model){
//        List<Airline>
//    }


}
