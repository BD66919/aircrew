package aircrew.version1.controller;

import aircrew.version1.entity.Airline;
import aircrew.version1.mapper.AirlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AirlineController {
    @Autowired
    AirlineRepository airlineRepository;

//    @PostMapping("/airline/add")
//    public String add(Airline airline, Model model)
//    {
//        if(airlineRepository.getOne(airline.getAirline())==null){
//            airlineRepository.save(airline);
//            model.addAttribute("msg","添加成功");
//            return "airline";
//        }
//        model.addAttribute("msg","该航线已存在，重复添加失败");
//        return "airline";
//    }
//
//    @PostMapping("/airline/delete")
//    public String delete(Airline airline, Model model)
//    {
//        if(airlineRepository.getOne(airline.getAirline())!=null){
//            airlineRepository.delete(airline);
//            model.addAttribute("msg","删除成功");
//            return "airline";
//        }
//        model.addAttribute("msg","该航线不存在数据库中，无法删除");
//        return "airline";
//    }
//
//    @GetMapping("/airline/select")
//    public String select(Model model){
//        List<Airline>  airlines = airlineRepository.findAll();
//        model.addAttribute("airlines",airlines);
//        return "airline";
//    }
//    @PostMapping("/airline/updata")
//    public String updata(Model model){
//        List<Airline>
//    }

    @PostMapping(value = "/Airline")
    @ResponseBody
    public Map<String,Object> addAirline(HttpServletRequest request){
        Map<String,Object> map  = new HashMap<>();
        String airline = request.getParameter("airline");
        String remark = request.getParameter("airlineRemark");
        if(airlineRepository.findByAirline(airline)!=null){
            map.put("msg","添加失败，该航线已存在");
            return map;
        }
        Airline air = new Airline(airline,remark);
        airlineRepository.save(air);
        map.put("msg","添加成功");
        return map;
    }

    @GetMapping("/airline")
    public String airline(Model model){
        List<Airline> airLineList = airlineRepository.findAll();
        System.out.println(airLineList);
        model.addAttribute("airLineList",airLineList);
        return "/pilot/airline.html";
    }

    @PostMapping(value = "/updateAirline")
    @ResponseBody
    public HashMap<String, Object> updateAirline(HttpServletRequest request){
        String airline = request.getParameter("airline");
        String remarks = request.getParameter("remarks");
        Airline air = new Airline(airline,remarks);
        airlineRepository.save(air);
        HashMap<String,Object> map = new HashMap<>();
        map.put("msg","修改成功");
        return map;
    }

    @PostMapping(value = "/deleteAirline={airline}")
    public String deleteAirline(@PathVariable String airline){
        airlineRepository.deleteById(airline);
        return "redirect:/airline";
    }

}
