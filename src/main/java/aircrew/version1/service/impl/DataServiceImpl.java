package aircrew.version1.service.impl;

import aircrew.version1.entity.*;
import aircrew.version1.mapper.*;
import aircrew.version1.service.DataService;
import aircrew.version1.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataServiceImpl implements DataService {
    private final FlRepository flRepository;
    private final AirRepository airRepository;
    private final MpRepository mpRepository;
    private final LastMpRepository lastMpRepository;
    private final NextMpRepository nextMpRepository;
    private final AirlineRepository airlineRepository;
    private final CadreRepository cadreRepository;
    private final StageDoubleFlightRepository  stageDoubleFlightRepository;
    private final NightFlightRepository nightFlightRepository;

    @Autowired
    DataServiceImpl(FlRepository flRepository, MpRepository mpRepository, AirRepository airRepository, LastMpRepository lastMpRepository, NextMpRepository nextMpRepository, AirlineRepository airlineRepository, AirlineRepository airlineRepository1, CadreRepository cadreRepository, StageDoubleFlightRepository stageDoubleFlightRepository, NightFlightRepository nightFlightRepository){
        this.flRepository = flRepository;
        this.mpRepository = mpRepository;
        this.airRepository = airRepository;
        this.lastMpRepository = lastMpRepository;
        this.nextMpRepository = nextMpRepository;
        this.airlineRepository = airlineRepository1;
        this.cadreRepository = cadreRepository;
        this.stageDoubleFlightRepository = stageDoubleFlightRepository;
        this.nightFlightRepository = nightFlightRepository;
    }

    private static boolean isNumeric(String str)
    {
        for (int i = 0; i < str.length(); i++)
        {
            if (!Character.isDigit(str.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String,Object> login(){
        Map<String,Object> map = new HashMap<String, Object>();
        if(!airRepository.findAll().isEmpty())
            map.put("air","本月航后数据(飞行)");

        if(!lastMpRepository.findAll().isEmpty())
            map.put("lastMp","上月飞行小时费数据(人力)");

        if(!mpRepository.findAll().isEmpty())
            map.put("mp","本月飞行小时费数据(人力)");

        if(!nextMpRepository.findAll().isEmpty())
            map.put("nextMp","下月飞行小时费数据(人力)");

        if(!flRepository.findAll().isEmpty())
            map.put("fl","本月生产台账数据(财务)");

        Air lastAir = airRepository.getLastAir();
        LastMp lastLastMp = lastMpRepository.getLastLastMp();
        Mp lastMp = mpRepository.getLastMp();
        NextMp nextLastMp = nextMpRepository.getNextLastMp();
        Fl lastFl = flRepository.getLastFl();

        if(lastAir!=null) {
            map.put("lastAirState", lastAir.getDate() + " " + lastAir.getTakeOffTime() + " " + lastAir.getName() + " " + lastAir.getFlightNo());
            map.put("airCount",airRepository.count());
        }
        if(lastLastMp!=null) {
            map.put("lastLastMpState", lastLastMp.getDate() + " " + lastLastMp.getTakeOffTime() + " " + lastLastMp.getName() + " " + lastLastMp.getFlightNo());
            map.put("lastMpCount",lastMpRepository.count());
        }
        if(lastMp!=null) {
            map.put("lastMpState", lastMp.getDate() + " " + lastMp.getTakeOffTime() + " " + lastMp.getName() + " " + lastMp.getFlightNo());
            map.put("mpCount",mpRepository.count());
        }
        if(nextLastMp!=null) {
            map.put("nextLastMpState", nextLastMp.getDate() + " " + nextLastMp.getTakeOffTime() + " " + nextLastMp.getName() + " " + nextLastMp.getFlightNo());
            map.put("nextMpCount",nextMpRepository.count());
        }
        if(lastFl!=null) {
            map.put("lastFlState", lastFl.getDate() + " " + lastFl.getTakeOffTime() + " " + lastFl.getAirline() + " " + lastFl.getFlightNo());
            map.put("flCount",flRepository.count());
        }
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int a = Integer.parseInt(str);
        if (a >= 0 && a <= 6) {
            map.put("time","夜深了！");
        }
        if (a > 6 && a <= 12) {
            map.put("time","上午好！");
        }
        if (a == 13) {
            map.put("time","中午好！");
        }
        if (a > 13 && a <= 18) {
            map.put("time","下午好！");
        }
        if (a > 18 && a <= 24) {
            map.put("time","晚上好！");
        }
        return map;
    }

    @Override
    public Map<String,Object> addMp(@RequestParam("file") MultipartFile file ){
        Map<String,Object> map = new HashMap<String,Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5 ) {
            map.put("msg","文件格式错误");
            return map;
        } else {
            String substring = fileName.substring(fileName.length() - 5);
            if(!(substring.equals(".xlsx")) &!(substring.equals(".xlsm"))& !(fileName.substring(fileName.length() - 4).equals(".xls"))){
                map.put("msg","文件类型错误");
                return map;
            }
        }
        List<Mp> excelList;
        try {
            excelList = MpExcelUtils.addMpExcel(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg","导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                mpRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg","导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg","导入异常");
            return map;
        }
        map.put("msg","导入成功");
        return map;
    }

    @Override
    public Map<String,Object> addLastMp(@RequestParam("file") MultipartFile file) {
        Map<String,Object> map = new HashMap<String,Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5 ) {
            map.put("msg","文件格式错误");
            return map;
        } else {
            String substring = fileName.substring(fileName.length() - 5);
            if(!(substring.equals(".xlsx")) &!(substring.equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls")) ){
                map.put("msg","文件类型错误");
                return map;
            }
        }
        List<LastMp> excelList ;
        try {
            excelList = LastMpExcelUtils.addLastMp(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg","导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                lastMpRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg","导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg","导入异常");
            return map;
        }
        map.put("msg","导入成功");
        return map;
    }

    @Override
    public Map<String,Object> addNextMp(@RequestParam("file") MultipartFile file ){
        Map<String,Object> map = new HashMap<String,Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5 ) {
            map.put("msg","文件格式错误");
            return map;
        } else {
            if(!(fileName.substring(fileName.length() - 5).equals(".xlsx")) &!(fileName.substring(fileName.length() - 5).equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls")) ){
                map.put("msg","文件类型错误");
                return map;
            }
        }
        List<NextMp> excelList ;
        try {
            excelList = NextMpExcelUtils.addNextMp(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg","导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                nextMpRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg","导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg","导入异常");
            return map;
        }
        map.put("msg","导入成功");
        return map;
    }

    @Override
    public Map<String,Object> addFl(@RequestParam("file") MultipartFile file) {
        Map<String,Object> map = new HashMap<>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5 ) {
            map.put("msg","文件格式错误");
            return map;
        } else if(!(fileName.substring(fileName.length() - 5).equals(".xlsx"))  &!(fileName.substring(fileName.length() - 5).equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))){
            map.put("msg","文件类型错误");
            return map;
        }
        List<Fl> excelList;
        try {
            excelList = FlExcelUtils.getFlExcel(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg","导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                flRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg","导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg","导入异常");
            return map;
        }
        map.put("msg","导入成功");
        return map;
    }

    @Override
    public Map<String,Object> addAir(@RequestParam("file") MultipartFile file ){
        Map<String,Object> map = new HashMap<String,Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5 ) {
            map.put("msg","文件格式错误");
            return map;
        } else {
            String substring = fileName.substring(fileName.length() - 5);
            if(!(substring.equals(".xlsx")) &!(substring.equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))){
                map.put("msg","文件类型错误");
                return map;
            }
        }
        List<Air> excelList ;
        try {
            excelList = AirExcelUtils.addAir(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg","导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                airRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg","导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg","导入异常");
            return map;
        }
        map.put("msg","导入成功");
        return map;
    }

    @Override
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

    @Override
    public Map<String,Object> addCadre(HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        String eid = request.getParameter("eid");
        if( eid.equals("")){
            map.put("msg","添加失败：员工编号不可为空");
            return map;
        }
        if(!isNumeric(eid)){
            map.put("msg","添加失败：员工编号需为数字");
            return map;
        }
        if(cadreRepository.findByEid(Integer.parseInt(eid))!=null){
            map.put("msg","添加失败：该员工编号已存在数据库中");
            return map;
        }
        String name = request.getParameter("name");
        if( name.equals("")){
            map.put("msg","添加失败：姓名不可为空");
            return map;
        }
        String department = request.getParameter("department");
        if( department.equals("")){
            map.put("msg","添加失败：部门不可为空");
            return map;
        }
        String qualify = request.getParameter("qualify");
        String category = request.getParameter("category");
        if( category.equals("")){
            map.put("msg","添加失败：空勤人员类别不可为空");
            return map;
        }
        Cadre cadre = new Cadre();
        cadre.setEid(Integer.parseInt(eid));
        cadre.setName(name);
        cadre.setDepartment(department);
        cadre.setQualify(qualify);
        cadre.setCategory(category);
        cadreRepository.save(cadre);
        map.put("msg","添加成功");
        return map;
    }

    @Override
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

    @Override
    public Map<String, Object> addStageDoubleFlight(HttpServletRequest request){
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

    @Override
    public List<Air> compareFlAir(){
        List<Air> airList = airRepository.findAll();
        List<Fl> flList = flRepository.findAll();
        List<Air> airResultList = new ArrayList<>();
        //逐一选择每一行财务数据
        for (Fl fl : flList) {
            if(fl.getDate()==null || fl.getFlightNo()==null)
                continue;
                //将选择的财务的数据与对应的飞行数据比对
                for (Air air : airList){
                    if(air.getDate()==null || air.getFlightNo()==null)
                        continue;
                    if(!air.getFlightNo().equals(fl.getFlightNo()))
                        continue;
                    if(!air.getDate().substring(2).equals(fl.getDate()))
                        continue;
                    if(!air.getArr().equals(fl.getArr()) & !air.getDep().equals(fl.getDep()))
                        if(!air.getArr().equals(fl.getDep()) || !air.getDep().equals(fl.getArr()))
                            continue;
                    Air airResult = new Air();
                    airResult.setDate(air.getDate());
                    airResult.setFlightNo(air.getFlightNo());
                    //判断是否有不同的地方
                    boolean flag = false;

                    //时刻校准之后需要复原
                    String slideTime = fl.getSlideTime();
                    String takeOffTime = fl.getTakeOffTime();
                    String landTime = fl.getLandTime();
                    String inPlaceTime = fl.getInPlaceTime();

                    //时刻校准
                    if(fl.getTimeType().equals("国际")){
                        fl.setSlideTime(Math.floorMod(Integer.parseInt(fl.getSlideTime().substring(0, 2)) + 8,24)+fl.getSlideTime().substring(2));
                        if(fl.getSlideTime().length()==4 )
                            fl.setSlideTime("0"+fl.getSlideTime());

                        fl.setTakeOffTime(Math.floorMod(Integer.parseInt(fl.getTakeOffTime().substring(0, 2)) + 8,24) +fl.getTakeOffTime().substring(2));
                        if(fl.getTakeOffTime().length()==4 )
                            fl.setTakeOffTime("0"+fl.getTakeOffTime());

                        fl.setLandTime(Math.floorMod(Integer.parseInt(fl.getLandTime().substring(0, 2)) + 8,24) +fl.getLandTime().substring(2));
                        if(fl.getLandTime().length()==4 )
                            fl.setLandTime("0"+fl.getLandTime());

                        fl.setInPlaceTime(Math.floorMod(Integer.parseInt(fl.getInPlaceTime().substring(0, 2)) + 8,24) +fl.getInPlaceTime().substring(2));
                        if(fl.getInPlaceTime().length()==4 )
                            fl.setInPlaceTime("0"+fl.getInPlaceTime());

                    }

                    //出发
                    if(!air.getDep().equals(fl.getDep())) {
                        airResult.setDep(" 财务:" + fl.getDep() + "飞行:" + air.getDep());
                        flag = true;
                    }
                    else
                        airResult.setDep(air.getDep());
                    //到达
                    if(!air.getArr().equals(fl.getArr())) {
                        airResult.setArr(" 财务:" + fl.getArr() + "飞行:" + air.getArr());
                        flag = true;
                    }
                    else
                        airResult.setArr(air.getArr());
                    //滑行
                    if(!air.getSlideTime().equals(fl.getSlideTime())) {
                        airResult.setSlideTime(" 财务:" + fl.getSlideTime() + "飞行:" + air.getSlideTime());
                        flag = true;
                    }
                    else
                        airResult.setSlideTime(air.getSlideTime());
                    //起飞
                    if(!air.getTakeOffTime().equals(fl.getTakeOffTime())) {
                        airResult.setTakeOffTime(" 财务:" + fl.getTakeOffTime() +  "飞行:" + air.getTakeOffTime());
                        flag = true;
                    }
                    else
                        airResult.setTakeOffTime(air.getTakeOffTime());
                    //降落
                    if(!air.getLandTime().equals(fl.getLandTime())) {
                        airResult.setLandTime(" 财务:" + fl.getLandTime() + "飞行:" + air.getLandTime());
                        flag = true;
                    }
                    else
                        airResult.setLandTime(air.getLandTime());

                    //到位
                    if(!air.getInPlaceTime().equals(fl.getInPlaceTime())) {
                        airResult.setInPlaceTime(" 财务:" + fl.getInPlaceTime() + "飞行:" + air.getInPlaceTime());
                        flag = true;
                    }
                    else
                        airResult.setInPlaceTime(air.getInPlaceTime());

                    //机型
//                    if(!air.getType().equals(fl.getType())){
//                        airResult.setType(" 财务:"+fl.getType() + "飞行:"+air.getType());
//                        flag = true;
//                    }
//                    else
//                        airResult.setType(air.getType());

                    //机号
                    if(!air.getFlightNumber().equals(fl.getAirplaneNumber())) {
                        airResult.setFlightNumber(" 财务:" + fl.getAirplaneNumber() + "飞行:" + air.getFlightNumber());
                        flag = true;
                    }
                    else
                        airResult.setFlightNumber(air.getFlightNumber());
                    //存在不同的地方就将数据添加进结果中
                    if(flag) {
                        airResultList.add(airResult);
                        break;
                    }
                    //时刻校准复原
                    else{
                        fl.setSlideTime(slideTime);
                        fl.setTakeOffTime(takeOffTime);
                        fl.setLandTime(landTime);
                        fl.setInPlaceTime(inPlaceTime);
                    }
                }
        }
        return airResultList;
    }

    @Override
    public List<Mp> compareAirMp() {
        List<Air> airList = airRepository.findAllBySort();
        List<Mp> mpList = mpRepository.findAll();
        List<Mp> mpResultList = new ArrayList<>();
        //逐一选择每一行的人力数据
        for(Mp mp:mpList){
            if(String.valueOf(mp.getEid()).isEmpty())
                continue;
            if(mp.getDate().isEmpty())
                continue;
            //人力中航线为该飞行完整任务航线，需要划分
            String []flightNoList = mp.getFlightNo().split("/");
            StringBuilder airLine = new StringBuilder();
            Mp result = new Mp();
            //判断是否有不同的地方
            boolean flag = false;
            //对每一任务的航线划分为每一段，对每一段进行比较
            for(int sub =0; sub<flightNoList.length; sub++) {
                //相同的航班号就不再比较
                if(sub!=0)
                    if(flightNoList[sub].equals(flightNoList[sub-1]))
                        continue;
                //与飞行数据进行比较
                for (Air air : airList) {
                    if (!mp.getDate().equals(air.getDate()))
                        continue;

                    if (mp.getEid() != air.getEid())
                        continue;

                    if(!flightNoList[sub].equals(air.getFlightNo()))
                        continue;

                    mp.setAirplaneNumber(mp.getAirplaneNumber().replace("-", ""));
                    result.setDate(mp.getDate());
                    result.setFlightNo(mp.getFlightNo());
                    result.setEid(mp.getEid());

                    //航线
                    if(sub==0) {
                        airLine.append(air.getDep()).append("-").append(air.getArr());
                    } else {
                        airLine.append("-").append(air.getArr());
                    }

                    //姓名
                    if (!mp.getName().equals(air.getName())) {
                        result.setName(air.getName() + " 人力：" + mp.getName());
                        flag = true;
                    } else {
                        result.setName(air.getName());
                    }

                    //性质
//                    if (!mp.getProperty().equals(air.getProperty())) {
//                        if(air.getProperty().equals("客加")&mp.getProperty().equals("正班")) {
//                            result.setProperty(air.getProperty());
//                            break;
//                        }
//                        result.setProperty(air.getProperty() + " 人力：" + mp.getProperty());
//                        flag = true;
//                    } else {
//                        result.setProperty(air.getProperty());
//                    }

                    //资质
                    if (!mp.getPost().substring(0, 1).equals(air.getQualify().substring(0, 1))) {
                        result.setPost(air.getQualify() + " 人力：" + mp.getPost());
                        flag = true;
                    } else {
                        result.setPost(air.getQualify());
                    }

                }
            }


            //结果的航线中可能存在重复的出发地/到达地，需要去除
            String []tcc =airLine.toString().split("-");
            StringBuilder tccAmend = new StringBuilder();
            for(int sub=0; sub<tcc.length; sub++){
                if(tcc[sub].length()>3)
                    tcc[sub] = tcc[sub].substring(0,3);
                if(sub==0)
                    tccAmend.append(tcc[sub]);
                else
                    tccAmend.append("-").append(tcc[sub]);
            }
            //判断是否为训练等杂数据
            if(!tccAmend.toString().equals(mp.getTcc())) {
                if(result.getDate()!=null) {
                    result.setTcc(tccAmend.toString() + " 人力：" + mp.getTcc());
                    flag = true;
                }
            }
            else
                result.setTcc(mp.getTcc());

            if (flag)
                mpResultList.add(result);
        }
        return mpResultList;
    }

    @Override
    public Map<String,Object> deleteMp() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            mpRepository.truncateMp();
        } catch (Exception e) {
            map.put("msg", "清空失败,请重试");
            return map;
        }
        map.put("msg", "清空成功");
        return map;
    }

    @Override
    public Map<String,Object> deleteFl(){
        Map<String,Object> map = new HashMap<String,Object>();
        try{
            flRepository.truncateFl();
        }catch (Exception e){
            map.put("msg","清空失败,请重试");
            return map;
        }
        map.put("msg","清空成功");
        return map;
    }

    @Override
    public Map<String,Object> deleteAir(){
        Map<String,Object> map = new HashMap<String,Object>();
        try{
            airRepository.truncateAir();
        }catch (Exception e){
            map.put("msg","清空失败,请重试");
            return map;
        }
        map.put("msg","清空成功");
        return map;
    }

    @Override
    public Map<String,Object> deleteLastMp(){
        Map<String,Object> map = new HashMap<String,Object>();
        try{
            lastMpRepository.truncateLastMp();
        }catch (Exception e){
            map.put("msg","清空失败,请重试");
            return map;
        }
        map.put("msg","清空成功");
        return map;
    }

    @Override
    public Map<String,Object> deleteNextMp(){
        Map<String,Object> map = new HashMap<String,Object>();
        try{
            nextMpRepository.truncateNextMp();
        }catch (Exception e){
            map.put("msg","清空失败,请重试");
            return map;
        }
        map.put("msg","清空成功");
        return map;
    }

    @Override
    public String deleteAirline(@PathVariable String airline){
        airlineRepository.deleteById(airline);
        return "redirect:/data/airline";
    }

    @Override
    public String deleteCadre(@PathVariable String id){
        cadreRepository.deleteById(Integer.parseInt(id));
        return "redirect:/data/cadre";
    }

    @Override
    public String deleteNightFlight(@PathVariable String number){
        nightFlightRepository.deleteById(number);
        return "redirect:/data/nightFlight";
    }

    @Override
    public String deleteStageDoubleFlight(@PathVariable String number){
        stageDoubleFlightRepository.deleteById(number);
        return "redirect:/data/stageDoubleFlight";
    }

    @Override
    public String airline(Model model){
        List<Airline> airLineList = airlineRepository.findAll();
        long number = airlineRepository.count();
        model.addAttribute("airLineList",airLineList);
        model.addAttribute("number",number);
        return "/data/airline.html";
    }

    @Override
    public String cadre(Model model){
        List<Cadre> cadreList = cadreRepository.findAll();
        long number = cadreRepository.count();
        model.addAttribute("cadreList",cadreList);
        model.addAttribute("number",number);
        return "/data/cadre.html";
    }

    @Override
    public String nightFlight(Model model){
        List<NightFlight> nightFlightList = nightFlightRepository.findAll();
        long number = nightFlightRepository.count();
        model.addAttribute("nightFlightList",nightFlightList);
        model.addAttribute("number",number);
        return "/data/nightFlight.html";
    }

    @Override
    public String stageDoubleFlight(Model model){
        List<StageDoubleFlight> stageDoubleFlightList = stageDoubleFlightRepository.findAll();
        long number = stageDoubleFlightRepository.count();
        model.addAttribute("stageDoubleFlightList",stageDoubleFlightList);
        model.addAttribute("number",number);
        return "/data/stageDoubleFlight.html";
    }

    @Override
    public Map<String, Object> updateAirline(HttpServletRequest request){
        String airline = request.getParameter("airline");
        String remarks = request.getParameter("remarks");
        Airline air = new Airline(airline,remarks);
        airlineRepository.save(air);
        Map<String,Object> map = new HashMap<>();
        map.put("msg","修改成功");
        return map;
    }

    @Override
    public Map<String,Object> updateCadre(HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        String eid = request.getParameter("eid");
        String name = request.getParameter("name");
        if( name.equals("")){
            map.put("msg","修改失败：姓名不可为空");
            return map;
        }
        String department = request.getParameter("department");
        if( department.equals("")){
            map.put("msg","修改失败：部门不可为空");
            return map;
        }
        String qualify = request.getParameter("qualify");
        String category = request.getParameter("category");
        if( category.equals("")){
            map.put("msg","修改失败：空勤人员类别不可为空");
            return map;
        }
        Cadre cadre = new Cadre();
        int id = cadreRepository.findByEid(Integer.parseInt(eid)).getId();
        cadreRepository.deleteById(id);
        cadre.setEid(Integer.parseInt(eid));
        cadre.setName(name);
        cadre.setDepartment(department);
        cadre.setQualify(qualify);
        cadre.setCategory(category);
        cadreRepository.save(cadre);
        map.put("msg","修改成功");
        return map;
    }

    @Override
    public Map<String, Object> updateNightFlight(HttpServletRequest request){
        String number = request.getParameter("number");
        String remarks = request.getParameter("remarks");
        NightFlight nightFlight = new NightFlight(number,remarks);
        nightFlightRepository.save(nightFlight);
        Map<String,Object> map = new HashMap<>();
        map.put("msg","修改成功");
        return map;
    }

    @Override
    public Map<String, Object> updateStageDoubleFlight(HttpServletRequest request){
        String number = request.getParameter("number");
        String remarks = request.getParameter("remarks");
        StageDoubleFlight stageDoubleFlight = new StageDoubleFlight(number,remarks);
        stageDoubleFlightRepository.save(stageDoubleFlight);
        Map<String,Object> map = new HashMap<>();
        map.put("msg","修改成功");
        return map;
    }


}
