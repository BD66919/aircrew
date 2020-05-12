package aircrew.version1.controller;

import aircrew.version1.entity.StageDoubleFlight;
import aircrew.version1.mapper.StageDoubleFlightRepository;
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
public class StageDoubleFlightController {

    @Autowired
    StageDoubleFlightRepository stageDoubleFlightRepository;

    @PostMapping(value = "/stageDoubleFlight")
    @ResponseBody
    public Map<String, Object> stageDoubleFlight(HttpServletRequest request){
        Map<String,Object> map  = new HashMap<>();
        String number = request.getParameter("number");
        String remarks = request.getParameter("remarks");
        if(stageDoubleFlightRepository.findById(number).isPresent()){
            map.put("msg","添加失败，该航班号已存在");
            return map;
        }
        StageDoubleFlight stageDoubleFlight = new StageDoubleFlight(number,remarks);
        stageDoubleFlightRepository.save(stageDoubleFlight);
        map.put("msg","添加成功");
        return map;
    }

    @GetMapping("/stageDoubleFlight")
    public String stageDoubleFlight(Model model){
        List<StageDoubleFlight> stageDoubleFlightList = stageDoubleFlightRepository.findAll();
        model.addAttribute("stageDoubleFlightList",stageDoubleFlightList);
        return "/pilot/stageDoubleFlight.html";
    }

    @PostMapping(value = "/updateStageDoubleFlight")
    @ResponseBody
    public HashMap<String, Object> updateStageDoubleFlight(HttpServletRequest request){
        String number = request.getParameter("number");
        String remarks = request.getParameter("remarks");
        StageDoubleFlight stageDoubleFlight = new StageDoubleFlight(number,remarks);
        stageDoubleFlightRepository.save(stageDoubleFlight);
        HashMap<String,Object> map = new HashMap<>();
        map.put("msg","修改成功");
        return map;
    }

    @PostMapping(value = "/deleteStageDoubleFlight={number}")
    public String deleteStageDoubleFlight(@PathVariable String number){
        stageDoubleFlightRepository.deleteById(number);
        return "redirect:/stageDoubleFlight";
    }

}
