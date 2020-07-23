package aircrew.version1.controller;

import aircrew.version1.entity.*;
import aircrew.version1.mapper.*;
import aircrew.version1.service.DataService;
import aircrew.version1.utils.*;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jiangzhendong
 * @Description 该类用于机组项目下的页面跳转和增删查改功能实现
 *
 */
@Controller
public class DataController {
//    @Autowired
//    PilotRepository pilotRepository;

    @Autowired
    AirRepository airRepository;

    @Autowired
    MpRepository mpRepository;

    @Autowired
    CadreRepository cadreRepository;

    @Autowired
    LastMpRepository lastMpRepository;

    private DataService dataService;

    @Autowired
    public DataController(DataService dataService){
        this.dataService = dataService;
    }

    /**
     * 跳转至pilot主页面
     */
    @GetMapping("/data/data")
    public String data(Model model){
        model.addAllAttributes(dataService.login());
        return "data/data.html";
    }

    @PostMapping(value = "/addAir")
    @ResponseBody
    public Map<String,Object> addAir(@RequestParam("file") MultipartFile file) {
        return dataService.addAir(file);
    }

    @PostMapping(value = "/addMp")
    @ResponseBody
    public Map<String,Object> addMp(@RequestParam("file") MultipartFile file) {
        return dataService.addMp(file);
    }

    @PostMapping(value = "/addFl")
    @ResponseBody
    public Map<String,Object> addFl(@RequestParam("file") MultipartFile file) { return dataService.addFl(file); }

    @PostMapping(value = "/addLastMp")
    @ResponseBody
    public Map<String,Object> addLastMp(@RequestParam("file") MultipartFile file) {
        return dataService.addLastMp(file);
    }

    @PostMapping(value = "/addNextMp")
    @ResponseBody
    public Map<String,Object> addNextMp(@RequestParam("file") MultipartFile file) {
        return dataService.addNextMp(file);
    }

    @PostMapping(value = "/addAirline")
    @ResponseBody
    public Map<String,Object> addAirline(HttpServletRequest request){
        return dataService.addAirline(request);
    }

    @PostMapping(value = "/addCadre")
    @ResponseBody
    public Map<String,Object> addCadre(HttpServletRequest request){
        return dataService.addCadre(request);
    }

    @PostMapping(value = "/addNightFlight")
    @ResponseBody
    public Map<String,Object> addNightFlight(HttpServletRequest request){
        return dataService.addNightFlight(request);
    }

    @PostMapping(value = "/addStageDoubleFlight")
    @ResponseBody
    public Map<String, Object> addStageDoubleFlight(HttpServletRequest request){
        return dataService.addStageDoubleFlight(request);
    }

    @PostMapping(value = "/deleteAll")
    @ResponseBody
    public Map<String,Object> deleteAll(){
        return dataService.deleteAll();
    }

    @PostMapping(value = "/deleteMp")
    @ResponseBody
    public Map<String,Object> deleteMp(){
        return dataService.deleteMp();
    }

    @PostMapping(value = "/deleteFl")
    @ResponseBody
    public Map<String,Object> deleteFl(){
        return dataService.deleteFl();
    }

    @PostMapping(value = "/deleteAir")
    @ResponseBody
    public Map<String,Object> deleteAir(){
        return dataService.deleteAir();
    }

    @PostMapping(value = "/deleteAirline={airline}")
    public String deleteAirline(@PathVariable String airline){
        return dataService.deleteAirline(airline);
    }

    @PostMapping(value = "/deleteCadre={id}")
    public String deleteCadre(@PathVariable String id){
        return dataService.deleteCadre(id);
    }

    @PostMapping(value = "/deleteNightFlight={number}")
    public String deleteNightFlight(@PathVariable String number){
        return dataService.deleteNightFlight(number);
    }

    @PostMapping(value = "/deleteStageDoubleFlight={number}")
    public String deleteStageDoubleFlight(@PathVariable String number){
        return dataService.deleteStageDoubleFlight(number);
    }

    @PostMapping(value = "/deleteLastMp")
    @ResponseBody
    public Map<String,Object> deleteLastMp(){
        return dataService.deleteLastMp();
    }

    @PostMapping(value = "/deleteNextMp")
    @ResponseBody
    public Map<String,Object> deleteNextMp(){
        return dataService.deleteNextMp();
    }

    @PostMapping(value = "/updateAirline")
    @ResponseBody
    public Map<String, Object> updateAirline(HttpServletRequest request){
        return dataService.updateAirline(request);
    }

    @PostMapping(value = "/updateCadre")
    @ResponseBody
    public Map<String, Object> updateCadre(HttpServletRequest request){
        return dataService.updateCadre(request);
    }

    @PostMapping(value = "/updateNightFlight")
    @ResponseBody
    public Map<String, Object> updateNightFlight(HttpServletRequest request){
        return dataService.updateNightFlight(request);
    }

    @PostMapping(value = "/updateStageDoubleFlight")
    @ResponseBody
    public Map<String, Object> updateStageDoubleFlight(HttpServletRequest request){
        return dataService.updateStageDoubleFlight(request);
    }

    @GetMapping("/data/airline")
    public String airline(Model model){
        return dataService.airline(model);
    }

    @GetMapping("/data/cadre")
    public String cadre(Model model){
        return dataService.cadre(model);
    }

    @GetMapping("/data/nightFlight")
    public String nightFlight(Model model){
        return dataService.nightFlight(model);
    }

    @GetMapping("/data/stageDoubleFlight")
    public String stageDoubleFlight(Model model){
        return dataService.stageDoubleFlight(model);
    }

    /**
     * 实现财务飞行数据比较
     */
    @GetMapping("/data/compareFlAir")
    public String compareFlAir(Model model){
       model.addAttribute("airList",dataService.compareFlAir());
        return "/data/compareFlAir.html";
    }

    /**
     * 实现飞行人力数据比较
     */
    @GetMapping("/data/compareAirMp")
    public String compareAirMp(Model model){
        model.addAttribute("mpList",dataService.compareAirMp());
        return "/data/compareAirMp.html";
    }

}
