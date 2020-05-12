package aircrew.version1.controller;

import aircrew.version1.entity.NightFlight;
import aircrew.version1.mapper.NightFlightRepository;
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
public class NightFlightController {

    @Autowired
    NightFlightRepository nightFlightRepository;

    @PostMapping(value = "/nightFlight")
    @ResponseBody
    public Map<String, Object> addNightFlight(HttpServletRequest request){
        Map<String,Object> map  = new HashMap<>();
        String number = request.getParameter("number");
        String nightFlightRemarks = request.getParameter("nightFlightRemarks");
        if(nightFlightRepository.findById(number).isPresent()){
            map.put("msg","添加失败，该航班号已存在");
            return map;
        }
        NightFlight nightFlight = new NightFlight(number,nightFlightRemarks);
        nightFlightRepository.save(nightFlight);
        map.put("msg","添加成功");
        return map;
    }

    @GetMapping("/nightFlight")
    public String nightFlight(Model model){
        List<NightFlight> nightFlightList = nightFlightRepository.findAll();
        model.addAttribute("nightFlightList",nightFlightList);
        return "/pilot/nightFlight.html";
    }

    @PostMapping(value = "/updateNightFlight")
    @ResponseBody
    public HashMap<String, Object> updateNightFlight(HttpServletRequest request){
        String number = request.getParameter("number");
        String remarks = request.getParameter("remarks");
        NightFlight nightFlight = new NightFlight(number,remarks);
        nightFlightRepository.save(nightFlight);
        HashMap<String,Object> map = new HashMap<>();
        map.put("msg","修改成功");
        return map;
    }

    @PostMapping(value = "/deleteNightFlight={number}")
    public String deleteNightFlight(@PathVariable String number){
        nightFlightRepository.deleteById(number);
        return "redirect:/nightFlight";
    }




}
