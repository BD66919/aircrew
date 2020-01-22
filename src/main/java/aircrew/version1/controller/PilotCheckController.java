package aircrew.version1.controller;
import aircrew.version1.entity.Pilot;
import aircrew.version1.mapper.PilotMapper;
import aircrew.version1.mapper.PilotRepository;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *@author Jiangzhendong
 *@Description 该类用于飞行员校验功能下的页面跳转和功能实现
 */
@Controller
public class PilotCheckController {
    @Autowired
    PilotRepository pilotRepository;

    @Autowired
    PilotMapper pilotMapper;

    @GetMapping("/pilot/pilotcheck/checkresult")
    public String result(){ return "/pilot/pilotcheck/checkresult.html"; }

    /**
     * 实现飞行员数据修改功能
     */
    @RequestMapping("/pilot/piloltcheck/updata")
    public String updataErrorPilot(Pilot pilot){
        return "/pilot/pilotcheck/updata.html";
    }

    /**
     * 实现通过eid查找飞行员数据功能
     */
    @GetMapping("/pilot/eid={eid}")
    public String selectEid(@PathVariable("eid")  int eid, Model model){
        List<Pilot> pilotListEid = pilotRepository.getByEid(eid);//
        Collections.sort(pilotListEid, new Comparator<Pilot>() {
            @Override
            public int compare(Pilot p1, Pilot p2) {
                return p1.getDate().compareTo(p2.getDate());
            }
        });
        PageInfo<Pilot> selectPilotListInfo = new PageInfo<Pilot>(pilotListEid);
        model.addAttribute("selectPilotListInfo",selectPilotListInfo);
        model.addAttribute("data","1");
        return "/pilot/pilotcheck/checkresult";
    }

    /**
     * 将修改的飞行员数据ID添加至Model中实现飞行员数据修改功能
     */
    @GetMapping("/pilot/pilotcheck/ErrorUpdata={id}")
    public String toUpdataPilot(@PathVariable("id") int id,
                                Model model){
        Pilot pilot = pilotMapper.getById(id);
        model.addAttribute("pilot",pilot);
        return "/pilot/pilotcheck/updata";
    }

    /**
     * 实现飞行员数据修改功能
     */
    @PostMapping("/pilot/pilotcheck/updataPilot")
    public String updataPilot(Pilot pilot){
        pilotMapper.updateById(pilot);
        int e = pilot.getEid();
        String eid = String.valueOf(e);
        return "redirect:/pilot/eid="+eid;
    }

    /**
     * 实现飞行员数据删除功能
     */
    @PostMapping("/pilot/pilotcheck/deletePilot={id}")
    public String deletePilot(@PathVariable("id") int id ){
        Pilot pilot = pilotMapper.getById(id);
        pilotMapper.deleteById(id);
        int e = pilot.getEid();
        String eid = String.valueOf(e);
        return "redirect:/pilot/eid="+eid;
    }

    /**
     * 实现飞行员数据忽略功能
     */
    @GetMapping("/pilot/id={id}")
    public String setPilotError(@PathVariable("id")  int id){
        pilotRepository.updataErrorById(id);
        return "redirect:/pilot/pilotcheck/check";
    }

    /**
     * 实现取消飞行员数据忽略的功能
     */
    @GetMapping("/pilot/errorid={id}")
    public String cancelPilotError(@PathVariable("id")  int id){
        pilotRepository.updataByError(id);
        return "redirect:/pilot/pilotcheck/record";
    }

    /**
     * 查看已忽略的页面
     */
    @RequestMapping("/pilot/pilotcheck/record")
    public String errorRecord(Model model){
        List<Pilot> errorPilotList = pilotRepository.selectByError();
        int length = errorPilotList.size();
        model.addAttribute("errorPilotList",errorPilotList);
        model.addAttribute("length",length);
        return "pilot/pilotcheck/record";
    }
}
