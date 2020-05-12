package aircrew.version1.controller;

import aircrew.version1.entity.Mp;
import aircrew.version1.entity.Pilot;
import aircrew.version1.mapper.AirRepository;
import aircrew.version1.mapper.MpRepository;
import aircrew.version1.mapper.PilotMapper;
import aircrew.version1.mapper.PilotRepository;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jiangzhendong
 * @Description 该类用于机组项目下的页面跳转和增删查改功能实现
 *
 */
@Controller
public class PilotController {
    @Autowired
    PilotMapper pilotMapper;

    @Autowired
    PilotRepository pilotRepository;

    @Autowired
    AirRepository airRepository;

    @Autowired
    MpRepository mpRepository;

    @GetMapping("/pilot/findAll")
    public List<Pilot> findAll(){
        return pilotRepository.findAll();
    }

//    /**
//     * 跳转至pilot主页面
//     */
//    @GetMapping("/pilot")
//    public String pilot(Model model,
//                       @RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum){
//        PageHelper.startPage(pageNum,10);
//        List<Pilot> pilotList = pilotMapper.list();
//        PageInfo<Pilot> pilotPageInfo = new PageInfo<Pilot>(pilotList);
//        model.addAttribute("pilotPageInfo",pilotPageInfo);
//        return "/pilot/pilot.html";
//    }

    /**
     * 跳转至pilot主页面
     */
    @GetMapping("/pilot")
    public String pilot(Model model){
        pilotRepository.getByEid(0);
        return "/pilot/pilot.html";
    }

    /**
     * 跳转至pilot添加页面
     */
    @GetMapping("/pilot/add")
    public String add(){ return "/pilot/add.html"; }

    /**
     * 跳转至pilot修改页面
     */
    @GetMapping("/pilot/updata")
    public String updata(){ return "/pilot/updata.html"; }

    /**
     * 跳转至pilot查询页面
     */
    @GetMapping("/pilot/query")
    public String query(){ return "/pilot/query.html"; }

    /**
     * 跳转至pilot查询结果页面
     */
    @GetMapping("/pilot/result")
    public String result(){ return "/pilot/result.html"; }

    /**
     * 跳转至覆盖性上传页面
     */
    @GetMapping("/pilot/coverupload")
    public String coverupload(){ return "/pilot/coverupload.html";}

    /**
     * 跳转至追加性上传页面
     */
    @GetMapping("/pilot/addupload")
    public String addupload(){ return "/pilot/addupload.html";}

    /**
     *
     */
    @GetMapping("/pilot/queryEdit={id}")
    public String toEditPilot(@PathVariable("id") int id, Model model){
        Pilot pilot = pilotMapper.getById(id);
        model.addAttribute("pilot",pilot);//将数据添加在web的model中
        return "pilot/updata";
    }

    /**
     * 跳转至飞行员飞行及调度路线校验页面
     */
    @GetMapping(value="/pilot/pilotcheck/check")
    public String check(Model model){
        List<Mp> pilotListByOrder = mpRepository.ByOrder();
//        List<Pilot> pilotListByOrder = pilotMapper.ByOrder();
        List<Mp> errorPilotList = new ArrayList<>();

        if(pilotListByOrder.isEmpty()) {
            PageInfo<Mp> errorPilotInfo = new PageInfo<Mp>(pilotListByOrder);
            model.addAttribute("errorPilotInfo",errorPilotInfo);
            return "pilot/pilotcheck/check.html";
        }

        int length = 0;
        int length2 = 0;
        String startTcc ;
        String endTcc ;
        for(int i = 0;i<pilotListByOrder.size()-1;i++){
            Mp pilot1 = pilotListByOrder.get(i);
            Mp pilot2 = pilotListByOrder.get(i+1);
            length = pilot1.getTcc().length();
            length2 = pilot2.getTcc().length();
            if (length==0 || length2 == 0) {//长度为零时进入下一次判断，防止被调机、正班以外的情况干扰
                continue;
            }
//            if(pilot1.getError()==1){
//                continue;
//            }
            if (pilot2.getEid() != pilot1.getEid()) {
                continue;
            }
            endTcc = pilot1.getTcc().substring(length - 3, length);
            startTcc = pilot2.getTcc().substring(0, 3);
            if (!endTcc.equals(startTcc) ){
                errorPilotList.add(pilot1);
                errorPilotList.add(pilot2);
            }
        }
        for(int i=1;i<errorPilotList.size()-1;i++){
            if (errorPilotList.get(i).equals(errorPilotList.get(i+1))){
                errorPilotList.remove(i);
            }
        }
        PageInfo<Mp> errorPilotInfo = new PageInfo<Mp>(errorPilotList);
        model.addAttribute("errorPilotInfo",errorPilotInfo);
        return "/pilot/pilotcheck/check.html";
    }

    /**
     * 实现飞行员数据添加功能
     */
    @PostMapping("/pilot/addPilot")
    public String addPilot(Pilot pilot){
        pilot.setError(0);
        pilotMapper.insert(pilot);
        return "redirect:/pilot";
    }

    /**
     * 实现飞行员数据删除功能
     */
    @PostMapping("/pilot/queryDelete={id}")
    public String deletePilot(@PathVariable("id") int id ){
        pilotMapper.deleteById(id);
        return "/pilot/query";
    }

    /**
     * 实现飞行员数据修改功能
     */
    @PostMapping("/pilot/queryUpdata")
    public String queryUpdataPilot(Pilot pilot){
        pilotMapper.updateById(pilot);
        return "/pilot/query";
    }

    /**
     * 实现飞行员数据查找功能
     */
    @PostMapping("/pilot/selectPilot")
    public String find(@RequestParam(value = "name",required = false) String name,
                       @RequestParam(value = "eid",required = false) String eid,
                       @RequestParam(value = "line",required = false) String line,
                       @RequestParam(value = "tcc",required =  false) String tcc,
                       @RequestParam(value = "properties",required = false) String properties,
                       @RequestParam(value = "date",required =  false) String date, Model model){
        if(date.equals("")){
            date = null;
        }
        List<Pilot> selectPilotList = pilotRepository.select(name, eid, line, tcc, properties, date,0);
        Collections.sort(selectPilotList, new Comparator<Pilot>() {
            @Override
            public int compare(Pilot p1, Pilot p2) {
                return p1.getDate().compareTo(p2.getDate());
            }
        });
        PageInfo<Pilot> selectPilotListInfo = new PageInfo<Pilot>(selectPilotList);
        model.addAttribute("selectPilotListInfo",selectPilotListInfo);
        return "/pilot/result";
    }
}
