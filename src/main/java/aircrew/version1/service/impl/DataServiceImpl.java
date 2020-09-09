package aircrew.version1.service.impl;

import aircrew.version1.entity.*;
import aircrew.version1.mapper.*;
import aircrew.version1.service.DataService;
import aircrew.version1.utils.*;
import lombok.Data;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Data
class Table {
    String name;
    String state;
    long count;
}

@Service
public class DataServiceImpl implements DataService {

    private final FlRepository flRepository;
    private final AirRepository airRepository;
    private final MpRepository mpRepository;
    private final LastMpRepository lastMpRepository;
    private final NextMpRepository nextMpRepository;
    private final AirlineRepository airlineRepository;
    private final CadreRepository cadreRepository;
    private final StageDoubleFlightRepository stageDoubleFlightRepository;
    private final NightFlightRepository nightFlightRepository;

    @Autowired
    DataServiceImpl(FlRepository flRepository, MpRepository mpRepository, AirRepository airRepository, LastMpRepository lastMpRepository, NextMpRepository nextMpRepository, AirlineRepository airlineRepository, AirlineRepository airlineRepository1, CadreRepository cadreRepository, StageDoubleFlightRepository stageDoubleFlightRepository, NightFlightRepository nightFlightRepository) {
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

//    static String getNumeric(String str) {
//        str=str.trim();
//        String str2="";
//        if(!"".equals(str)){
//            for(int i=0;i<str.length();i++  ){
//                if((str.charAt(i)>=48 && str.charAt(i)<=57) || str.charAt(i) ==47 ){
//                    str2 =str2 + str.charAt(i);
//                }
//            }
//        }
//        return str2;
//    }

    private static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    List<Air> getFlAirCompareList() throws ParseException {
        List<Air> airList = airRepository.findAll();
        List<Fl> flList = flRepository.findAll();
        List<Air> airResultList = new ArrayList<>();
        int daysOfMonth = 27;
        if (!flList.isEmpty()) {
            //获取本月天数
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(flList.get(0).getDate()));
            daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        //修正财务的国际时刻为国内时刻
        for (Fl fl : flList) {
            if (fl.getDate() == null || fl.getFlightNo() == null)
                continue;
            //将财务的国际时刻修改为国内
            String date = "20" + fl.getDate();
            if (fl.getTimeType().equals("国际")) {
                if (Integer.parseInt(fl.getSlideTime().substring(0, 2)) >= 16) {
                    int day = (Integer.parseInt(date.substring(8)) + 1);
                    if (day < 10)
                        date = date.substring(0, 8) + "0" + String.valueOf(day);
                        //进入新的一月
                    else if (day > daysOfMonth) {
                        //大于十二月就要进入新的一年
                        if (date.startsWith("12", 5))
                            date = String.valueOf(Integer.parseInt(date.substring(0, 4)) + 1) + "-01-01";
                            //进入新的一月
                        else {
                            if (Integer.parseInt(date.substring(5, 7)) < 9)
                                date = date.substring(0, 5) + "0" + String.valueOf(Integer.parseInt(date.substring(5, 7)) + 1) + "-01";
                            else
                                date = date.substring(0, 5) + String.valueOf(Integer.parseInt(date.substring(5, 7)) + 1) + "-01";
                        }
                    } else
                        date = date.substring(0, 8) + String.valueOf(day);
                }
            }
            fl.setDate(date);
        }

        //逐一选择每一行财务数据进行比较
        for (Fl fl : flList) {
            if (fl.getDate() == null || fl.getFlightNo() == null)
                continue;
            boolean appear = false;
            Air airResult = new Air();
            //判断是否有不同的地方
            boolean flag = false;
            int length = fl.getAirline().length();
            if (fl.getFlightNo().substring(0, 1).equals("0"))
                fl.setFlightNo(fl.getFlightNo().substring(1));
            //将选择的财务的数据与对应的飞行数据比对
            for (Air air : airList) {
                if (air.getDate() == null || air.getFlightNo() == null)
                    continue;
                if (!air.getFlightNo().equals(fl.getFlightNo()))
                    continue;
                if (!air.getDate().equals(fl.getDate()))
                    continue;
                appear = true;
                airResult.setDate(air.getDate());
                airResult.setFlightNo(air.getFlightNo());
                boolean first = false;
                if (length == 7) {
                    //离港
                    if (!air.getDep().equals(fl.getDep())) {
                        airResult.setDep(" 财务:" + fl.getDep() + "飞行:" + air.getDep());
                        flag = true;
                    } else {
                        airResult.setDep(air.getDep());
                        first = true;//第一个相同
                    }
                    //到港
                    if (!air.getArr().equals(fl.getArr())) {
                        airResult.setArr(" 财务:" + fl.getArr() + "飞行:" + air.getArr());
                        flag = true;
                    } else {
                        airResult.setArr(air.getArr());
                        if (first)//第二个再次相同将判断差异flag复原，防止flag在上一次循环被设置为true
                            flag = false;
                    }
                } else {
                    if (!fl.getAirline().contains(air.getDep() + "-" + air.getArr())) {
                        airResult.setDep(fl.getDep());
                        airResult.setArr(fl.getArr());
                        flag = true;
                    }
                }
            }
            if (flag) {
                airResultList.add(airResult);
            }
            if (!appear) {
                Air air = new Air();
                air.setDate(fl.getDate());
                air.setFlightNo(fl.getFlightNo());
                air.setDep(fl.getDep() + " 该条数据在飞行数据中不存在");
                air.setArr(fl.getArr() + " 该条数据在飞行数据中不存在");
                airResultList.add(air);
            }
        }

        //逐一选择每一行飞行数据进行比较
        for (Air air : airList) {
            if (air.getProperty().equals("调机") || air.getProperty().equals("训练"))
                continue;
            Air airResult = new Air();
            boolean appear = false;
            for (Fl fl : flList) {
                if (!air.getDate().equals(fl.getDate()))
                    continue;
                if (!air.getFlightNo().equals(fl.getFlightNo()))
                    continue;
                boolean flag = false;
                appear = true;
                if (fl.getAirline().length() == 7) {
                    if (!air.getArr().equals(fl.getArr())) {
                        airResult.setArr("财务: " + fl.getArr() + "飞行: " + air.getArr());
                        flag = true;
                    } else
                        airResult.setArr(air.getArr());

                    if (!air.getDep().equals(fl.getDep())) {
                        airResult.setDep("财务: " + fl.getDep() + "飞行: " + air.getDep());
                        flag = true;
                    } else
                        airResult.setDep(air.getDep());
                } else {
                    if (!fl.getAirline().contains(air.getDep() + "-" + air.getArr())) {
                        airResult.setDep("财务航线：" + fl.getAirline());
                        airResult.setArr("飞行航线:" + air.getDep() + "-" + air.getArr());
                        flag = true;
                    }
                }
                airResult.setDate(air.getDate());
                airResult.setFlightNo(air.getFlightNo());
                if (flag) {
                    boolean add = true;
                    for (Air air1 : airResultList)
                        if (air1.getDate().equals(airResult.getDate()) & air1.getFlightNo().equals(airResult.getFlightNo())) {
                            add = false;
                            break;
                        }
                    if (add)
                        airResultList.add(airResult);
                }
                break;
            }
            if (!appear) {
                airResult.setDate(air.getDate());
                airResult.setFlightNo(air.getFlightNo());
                airResult.setDep(air.getDep() + " 该条数据在财务数据中不存在");
                airResult.setArr(air.getArr() + " 该条数据在财务数据中不存在");
                boolean add = true;
                for (Air air1 : airResultList)
                    if (air1.getDate().equals(airResult.getDate()) & air1.getFlightNo().equals(airResult.getFlightNo())) {
                        add = false;
                        break;
                    }
                if (add)
                    airResultList.add(airResult);
            }
        }

        Collections.sort(airResultList, new Comparator<Air>() {
            @Override
            public int compare(Air air1, Air air2) {
                return air1.getDate().compareTo(air2.getDate());
            }
        });
        return airResultList;
    }

    HashMap<String,Object> getAirMpCompareList() throws ParseException {
        List<Air> airList = airRepository.findAllBySort();
        List<Mp> mpList = mpRepository.findAll();
        List<Mp> mpResultList = new ArrayList<>();
        long airCount = airRepository.count();
        int mpCount = 0;
        //逐一选择每一行的人力数据
        for (Mp mp : mpList) {
            //人力中航线为该飞行完整任务航线，需要划分
            String[] flightNoList = mp.getFlightNo().split("/");
            mpCount = mpCount + flightNoList.length;
            if (mp.getFlightNo().substring(0, 1).equals("0"))
                continue;
            if (String.valueOf(mp.getEid()).isEmpty())
                continue;
            if (mp.getDate().isEmpty())
                continue;

            StringBuilder airLine = new StringBuilder();
            Mp result = new Mp();

            //判断是否有不同的地方
            boolean flag = false;
            boolean isAppear = false;
            String qualify = null;
            if (mp.getProperty().equals("调组乘机乘车"))
                isAppear = true;

            //对每一任务的航线划分为每一段，对每一段进行比较
            for (int sub = 0; sub < flightNoList.length; sub++) {
                //相同的航班号就不再比较
                if (sub != 0)
                    if (flightNoList[sub].equals(flightNoList[sub - 1]))
                        continue;
                //与飞行数据进行比较
                for (Air air : airList) {
                    if (air.getFlightNo().substring(0, 1).equals("0"))
                        continue;
                    qualify = air.getQualify();
                    if (!mp.getDate().equals(air.getDate()))
                        continue;
                    if (mp.getEid() != air.getEid())
                        continue;
                    if (!flightNoList[sub].equals(air.getFlightNo()))
                        continue;
                    mp.setAirplaneNumber(mp.getAirplaneNumber().replace("-", ""));
                    result.setDate(mp.getDate());
                    result.setFlightNo(mp.getFlightNo());
                    result.setEid(mp.getEid());
                    result.setName(mp.getName());
                    result.setAirplaneNumber(mp.getAirplaneNumber());
                    isAppear = true;
                    //航线
                    if (sub == 0) {
                        airLine.append(air.getDep()).append("-").append(air.getArr());
                    } else {
                        airLine.append("-").append(air.getArr());
                    }
                    //资质
                    if (!mp.getPost().substring(0, 1).equals(air.getQualify().substring(0, 1))) {
                        result.setPost("飞行:" + air.getQualify() + " 人力：" + mp.getPost());
                        flag = true;
                    } else {
                        result.setPost(air.getQualify());
                    }
                }
            }
            //结果的航线中可能存在重复的出发地/到达地，需要去除
            String[] tcc = airLine.toString().split("-");
            StringBuilder tccAmend = new StringBuilder();
            for (int sub = 0; sub < tcc.length; sub++) {
                if (tcc[sub].length() > 3)
                    tcc[sub] = tcc[sub].substring(0, 3);
                if (sub == 0)
                    tccAmend.append(tcc[sub]);
                else
                    tccAmend.append("-").append(tcc[sub]);
            }

            if (!tccAmend.toString().equals(mp.getTcc())) {
                if (result.getDate() != null) {
                    //为训练等杂数据时
                    if (mp.getTcc() == null)
                        result.setTcc("飞行:" + tccAmend.toString() + " 人力：" + mp.getAirLine());
                        //不为训练等杂数据时
                    else
                        result.setTcc("飞行：" + tccAmend.toString() + " 人力：" + mp.getTcc());
                    flag = true;
                }
            } else
                result.setTcc(mp.getTcc());
            if (flag)
                mpResultList.add(result);
            if (!isAppear) {
                result.setDate(mp.getDate());
                result.setFlightNo(mp.getFlightNo());
                result.setTcc(mp.getTcc() + " 该条数据在飞行数据中不存在");
                result.setEid(mp.getEid());
                result.setName(mp.getName());
                result.setPost(qualify);
                mpResultList.add(result);
            }
        }

        for (Air air : airList) {
            if (air.getFlightNo().substring(0, 1).equals("0"))
                continue;
            if (air.getProperty().equals("本场") || air.getProperty().equals("训练"))
                continue;
            boolean flag = false;
            String date = air.getDate();
            for (Mp mp : mpList) {
                if (mp.getFlightNo().substring(0, 1).equals("0"))
                    continue;
                if (!mp.getDate().equals(date))
                    continue;
                String[] flightNo = mp.getFlightNo().split("/");
                for (String no : flightNo) {
                    if (air.getFlightNo().equals(no)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                Mp mp = new Mp();
                mp.setDate(air.getDate());
                mp.setFlightNo(air.getFlightNo());
                mp.setTcc(air.getDep() + "-" + air.getArr() + " 该条数据在人力数据中不存在");
                mp.setEid(air.getEid());
                mp.setName(air.getName());
                mp.setPost(air.getQualify());
                mpResultList.add(mp);
            }
        }

        int daysOfMonth = 27;
        int daysOfLastMonth = 1;
        if (!mpResultList.isEmpty()) {
            //获取本月天数
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(mpResultList.get(0).getDate()));
            daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            //获取上月天数
            calendar.setTime(simpleDateFormat.parse(mpResultList.get(0).getDate().substring(0, 5) + "0" + String.valueOf(Integer.parseInt(mpResultList.get(0).getDate().substring(5, 7)) - 1) + "-01"));
            daysOfLastMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        }

        //结果矫正
        for (int i = 0; i < mpResultList.size(); i++) {
            Mp mp = mpResultList.get(i);
            if (!mp.getTcc().contains("飞行：")) {
                continue;
            }
            String flightNumber = mp.getAirplaneNumber().replace("-", "");
            String airTcc = mp.getTcc().split(" ")[0];
            String mpTcc = mp.getTcc().split(" ")[1];
            if (airTcc.length() != mpTcc.length()) {
                String[] flightNoArray = mp.getFlightNo().split("/");
                String airTccCheck = null;
                String firstFlightNo = flightNoArray[0];
                for (int j = 0; j < flightNoArray.length; j++) {
                    String flightNo = flightNoArray[j];
                    //由于前一天、这一天和后一天都会查询，因此相同的航班号跳过
                    if (j != 0)
                        if (flightNo.equals(firstFlightNo))
                            continue;
                    //前一天
                    String yesterday = mp.getDate();
                    int day = (Integer.parseInt(yesterday.substring(8)) - 1);
                    if (day < 10 & day != 1)
                        yesterday = yesterday.substring(0, 8) + "0" + String.valueOf(day);
                        //进入上一月
                    else if (day == 1) {
                        //等于一月一号就要进入上一年
                        if (yesterday.startsWith("01", 5))
                            yesterday = String.valueOf(Integer.parseInt(yesterday.substring(0, 4)) - 1) + "-12-31";
                            //进入上一月
                        else {
                            if (Integer.parseInt(yesterday.substring(5, 7)) < 10)
                                yesterday = yesterday.substring(0, 5) + "0" + String.valueOf(Integer.parseInt(yesterday.substring(5, 7)) - 1) + daysOfLastMonth;
                            else
                                yesterday = yesterday.substring(0, 5) + String.valueOf(Integer.parseInt(yesterday.substring(5, 7)) - 1) + daysOfLastMonth;
                        }
                    } else
                        yesterday = yesterday.substring(0, 8) + String.valueOf(day);

                    List<Air> yesterdayAirCheckList = airRepository.getAirCheck(yesterday, flightNo, mp.getEid(), flightNumber);
                    if (yesterdayAirCheckList.isEmpty())
                        yesterdayAirCheckList = airRepository.getAirCheck(yesterday, flightNo, mp.getEid(), "B" + flightNumber);

                    List<Air> todayAirCheckList = airRepository.getAirCheck(mp.getDate(),flightNo,mp.getEid(),flightNumber);

                    //后一天
                    String tomorrow = mp.getDate();
                    day = (Integer.parseInt(tomorrow.substring(8)) + 1);
                    if (day < 10)
                        tomorrow = tomorrow.substring(0, 8) + "0" + String.valueOf(day);
                        //进入新的一月
                    else if (day > daysOfMonth) {
                        //大于十二月就要进入新的一年
                        if (tomorrow.startsWith("12", 5))
                            tomorrow = String.valueOf(Integer.parseInt(tomorrow.substring(0, 4)) + 1) + "-01-01";
                            //进入新的一月
                        else {
                            if (Integer.parseInt(tomorrow.substring(5, 7)) < 9)
                                tomorrow = tomorrow.substring(0, 5) + "0" + String.valueOf(Integer.parseInt(tomorrow.substring(5, 7)) + 1) + "-01";
                            else
                                tomorrow = tomorrow.substring(0, 5) + String.valueOf(Integer.parseInt(tomorrow.substring(5, 7)) + 1) + "-01";
                        }
                    } else
                        tomorrow = tomorrow.substring(0, 8) + String.valueOf(day);

                    List<Air> tomorrowAirCheckList = airRepository.getAirCheck(tomorrow, flightNo, mp.getEid(), flightNumber);
                    if (tomorrowAirCheckList.isEmpty())
                        tomorrowAirCheckList = airRepository.getAirCheck(tomorrow, flightNo, mp.getEid(), "B" + flightNumber);

                    for (Air air : yesterdayAirCheckList) {
                        if (airTccCheck == null) {
                            airTccCheck = air.getDep() + ("-") + air.getArr();
                        } else
                            airTccCheck = air.getDep() + ("-") + airTccCheck;
                    }

                    for(Air air : todayAirCheckList){
                        if (airTccCheck == null) {
                            airTccCheck = air.getDep() + ("-") + air.getArr();
                        } else
                            airTccCheck = airTccCheck + ("-") + air.getArr();
                    }

                    for (Air air : tomorrowAirCheckList) {
                        if (airTccCheck == null) {
                            airTccCheck = air.getDep() + ("-") + air.getArr();
                        } else
                            airTccCheck = airTccCheck + "-" + air.getArr();
                    }
                    firstFlightNo = flightNo;
                }
                if (airTccCheck != null)
                    //相同就去除
                    if (airTccCheck.equals(mpTcc.substring(3))) {
                        mpResultList.remove(i);
                        i--;
                    } else
                        mp.setTcc("飞行：" + airTccCheck + " 人力：" + mpTcc.substring(3));
            }
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("airCount",airCount);
        map.put("mpCount",mpCount);
        map.put("mpResultList",mpResultList);
        return map;
    }

    private void cellValue(HSSFCellStyle cellStyle, HSSFRow row, int i, String value) {
        Cell cell = row.createCell(i);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
    }

    @Override
    public Map<String, Object> login(HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Table> tableList = new ArrayList<>();
        Table airTable = new Table();
        Table lastMpTable = new Table();
        Table mpTable = new Table();
        Table nextMpTable = new Table();
        Table flTable = new Table();
        Air lastAir = airRepository.getLastAir();
        LastMp lastLastMp = lastMpRepository.getLastLastMp();
        Mp lastMp = mpRepository.getLastMp();
        NextMp nextLastMp = nextMpRepository.getNextLastMp();
        Fl lastFl = flRepository.getLastFl();
        if (lastAir != null) {
            airTable.setState(lastAir.getDate() + " " + lastAir.getTakeOffTime() + " " + lastAir.getName() + " " + lastAir.getFlightNo());
            airTable.setCount(airRepository.count());
        }
        if (lastLastMp != null) {
            lastMpTable.setState(lastLastMp.getDate() + " " + lastLastMp.getTakeOffTime() + " " + lastLastMp.getName() + " " + lastLastMp.getFlightNo());
            lastMpTable.setCount(lastMpRepository.count());
        }
        if (lastMp != null) {
            mpTable.setState(lastMp.getDate() + " " + lastMp.getTakeOffTime() + " " + lastMp.getName() + " " + lastMp.getFlightNo());
            mpTable.setCount(mpRepository.count());
        }
        if (nextLastMp != null) {
            nextMpTable.setState(nextLastMp.getDate() + " " + nextLastMp.getTakeOffTime() + " " + nextLastMp.getName() + " " + nextLastMp.getFlightNo());
            nextMpTable.setCount(nextMpRepository.count());
        }
        if (lastFl != null) {
            flTable.setState(lastFl.getDate() + " " + lastFl.getTakeOffTime() + " " + lastFl.getAirline() + " " + lastFl.getFlightNo());
            flTable.setCount(flRepository.count());
        }

        if (!airRepository.findAll().isEmpty()) {
            airTable.setName("本月航后数据(飞行)");
            tableList.add(airTable);
        }

        if (!lastMpRepository.findAll().isEmpty()) {
            lastMpTable.setName("上月飞行小时费数据(人力)");
            tableList.add(lastMpTable);
        }

        if (!mpRepository.findAll().isEmpty()) {
            mpTable.setName("本月飞行小时费数据(人力)");
            tableList.add(mpTable);
        }

        if (!nextMpRepository.findAll().isEmpty()) {
            nextMpTable.setName("下月飞行小时费数据(人力)");
            tableList.add(nextMpTable);
        }

        if (!flRepository.findAll().isEmpty()) {
            flTable.setName("本月生产台账数据(财务)");
            tableList.add(flTable);
        }
        map.put("tableList", tableList);
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int a = Integer.parseInt(str);
        if (a >= 0 && a <= 6) {
            map.put("time", "夜深了！");
            session.setAttribute("time", "夜深了！");
        }
        if (a > 6 && a <= 12) {
            map.put("time", "上午好！");
            session.setAttribute("time", "上午好！");
        }
        if (a == 13) {
            map.put("time", "中午好！");
            session.setAttribute("time", "中午好！");
        }
        if (a > 13 && a <= 18) {
            map.put("time", "下午好！");
            session.setAttribute("time", "下午好！");
        }
        if (a > 18 && a <= 24) {
            map.put("time", "晚上好！");
            session.setAttribute("time", "晚上好！");
        }
        return map;
    }

    @Override
    public Map<String, Object> addMp(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<String, Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5) {
            map.put("msg", "文件格式错误");
            return map;
        } else {
            String substring = fileName.substring(fileName.length() - 5);
            if (!(substring.equals(".xlsx")) & !(substring.equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))) {
                map.put("msg", "文件类型错误");
                return map;
            }
        }
        List<Mp> excelList;
        try {
            excelList = MpExcelUtils.addMpExcel(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg", "导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                mpRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg", "导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg", "导入异常");
            return map;
        }
        map.put("msg", "导入成功");
        return map;
    }

    @Override
    public Map<String, Object> addLastMp(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<String, Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5) {
            map.put("msg", "文件格式错误");
            return map;
        } else {
            String substring = fileName.substring(fileName.length() - 5);
            if (!(substring.equals(".xlsx")) & !(substring.equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))) {
                map.put("msg", "文件类型错误");
                return map;
            }
        }
        List<LastMp> excelList;
        try {
            excelList = LastMpExcelUtils.addLastMp(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg", "导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                lastMpRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg", "导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg", "导入异常");
            return map;
        }
        map.put("msg", "导入成功");
        return map;
    }

    @Override
    public Map<String, Object> addNextMp(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<String, Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5) {
            map.put("msg", "文件格式错误");
            return map;
        } else {
            if (!(fileName.substring(fileName.length() - 5).equals(".xlsx")) & !(fileName.substring(fileName.length() - 5).equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))) {
                map.put("msg", "文件类型错误");
                return map;
            }
        }
        List<NextMp> excelList;
        try {
            excelList = NextMpExcelUtils.addNextMp(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg", "导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                nextMpRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg", "导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg", "导入异常");
            return map;
        }
        map.put("msg", "导入成功");
        return map;
    }

    @Override
    public Map<String, Object> addFl(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5) {
            map.put("msg", "文件格式错误");
            return map;
        } else if (!(fileName.substring(fileName.length() - 5).equals(".xlsx")) & !(fileName.substring(fileName.length() - 5).equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))) {
            map.put("msg", "文件类型错误");
            return map;
        }
        List<Fl> excelList;
        try {
            excelList = FlExcelUtils.getFlExcel(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg", "导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                flRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg", "导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg", "导入异常");
            return map;
        }
        map.put("msg", "导入成功");
        return map;
    }

    @Override
    public Map<String, Object> addAir(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<String, Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5) {
            map.put("msg", "文件格式错误");
            return map;
        } else {
            String substring = fileName.substring(fileName.length() - 5);
            if (!(substring.equals(".xlsx")) & !(substring.equals(".xlsm")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))) {
                map.put("msg", "文件类型错误");
                return map;
            }
        }
        List<Air> excelList;
        try {
            excelList = AirExcelUtils.addAir(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg", "导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                airRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg", "导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg", "导入异常");
            return map;
        }
        map.put("msg", "导入成功");
        return map;
    }

    @Override
    public Map<String, Object> addAirline(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String airline = request.getParameter("airline");
        String remark = request.getParameter("airlineRemark");
        if (airlineRepository.findByAirline(airline) != null) {
            map.put("msg", "添加失败，该航线已存在");
            return map;
        }
        Airline air = new Airline(airline, remark);
        airlineRepository.save(air);
        map.put("msg", "添加成功");
        return map;
    }

    @Override
    public Map<String, Object> addCadre(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String eid = request.getParameter("eid");
        if (eid.equals("")) {
            map.put("msg", "添加失败：员工编号不可为空");
            return map;
        }
        if (!isNumeric(eid)) {
            map.put("msg", "添加失败：员工编号需为数字");
            return map;
        }
        if (cadreRepository.findByEid(Integer.parseInt(eid)) != null) {
            map.put("msg", "添加失败：该员工编号已存在数据库中");
            return map;
        }
        String name = request.getParameter("name");
        if (name.equals("")) {
            map.put("msg", "添加失败：姓名不可为空");
            return map;
        }
        String department = request.getParameter("department");
        if (department.equals("")) {
            map.put("msg", "添加失败：部门不可为空");
            return map;
        }
        String qualify = request.getParameter("qualify");
        String category = request.getParameter("category");
        if (category.equals("")) {
            map.put("msg", "添加失败：空勤人员类别不可为空");
            return map;
        }
        Cadre cadre = new Cadre();
        cadre.setEid(Integer.parseInt(eid));
        cadre.setName(name);
        cadre.setDepartment(department);
        cadre.setQualify(qualify);
        cadre.setCategory(category);
        cadreRepository.save(cadre);
        map.put("msg", "添加成功");
        return map;
    }

    @Override
    public Map<String, Object> addNightFlight(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String number = request.getParameter("number");
        String nightFlightRemarks = request.getParameter("nightFlightRemarks");
        if (nightFlightRepository.findById(number).isPresent()) {
            map.put("msg", "添加失败，该航班号已存在");
            return map;
        }
        NightFlight nightFlight = new NightFlight(number, nightFlightRemarks);
        nightFlightRepository.save(nightFlight);
        map.put("msg", "添加成功");
        return map;
    }

    @Override
    public Map<String, Object> addStageDoubleFlight(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String number = request.getParameter("number");
        String remarks = request.getParameter("remarks");
        if (stageDoubleFlightRepository.findById(number).isPresent()) {
            map.put("msg", "添加失败，该航班号已存在");
            return map;
        }
        StageDoubleFlight stageDoubleFlight = new StageDoubleFlight(number, remarks);
        stageDoubleFlightRepository.save(stageDoubleFlight);
        map.put("msg", "添加成功");
        return map;
    }

    @Override
    public List<Air> compareFlAir() throws ParseException {
        return getFlAirCompareList();
    }

    @Override
    public void flAirExcelDownload(HttpServletResponse response) throws IOException, ParseException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("task");
        //设置宽度
        for (int i = 0; i < 6; i++)
            sheet.setColumnWidth(i, 4088);
        sheet.setColumnWidth(2, 6000);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        List<Air> flAirList = getFlAirCompareList();
        String fileName = "财务与飞行对比数据" + ".xls";
        int rowNum = 1;
        String[] headers = {"日期", "航班号", "离港", "到港"};
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        //在表中存放查询到的数据放入对应的列
        for (Air air : flAirList) {
            //设置居中
            HSSFRow row1 = sheet.createRow(rowNum);
            String[] value = {air.getDate(), air.getFlightNo(), air.getDep(), air.getArr()};
            for (int i = 0; i < headers.length; i++) {
                cellValue(cellStyle, row1, i, value[i]);
            }
            rowNum++;
        }
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String compareAirMp(Model model) throws ParseException {
        HashMap<String,Object> map = getAirMpCompareList();
        model.addAttribute("mpCount",map.get("mpCount"));
        model.addAttribute("airCount",map.get("airCount"));
        model.addAttribute("mpResultList",map.get("mpResultList"));
        return "data/compareAirMp.html";
    }

    @Override
    public void airMpExcelDownload(HttpServletResponse response) throws IOException, ParseException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("task");
        //设置宽度
        for (int i = 0; i < 6; i++)
            sheet.setColumnWidth(i, 4088);
        sheet.setColumnWidth(2, 6000);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        HashMap<String,Object> map = getAirMpCompareList();
        List<Mp> airMpList = (List<Mp>)map.get("mpResultList");
        String fileName = "飞行与人力对比数据" + ".xls";
        int rowNum = 1;
        String[] headers = {"日期", "航班号", "航线", "人员编号", "姓名", "资质"};
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        //在表中存放查询到的数据放入对应的列
        for (Mp mp : airMpList) {
            //设置居中
            HSSFRow row1 = sheet.createRow(rowNum);
            String[] value = {mp.getDate(), mp.getFlightNo(), mp.getTcc(), String.valueOf(mp.getEid()), mp.getName(), mp.getPost()};
            for (int i = 0; i < headers.length; i++) {
                cellValue(cellStyle, row1, i, value[i]);
            }
            rowNum++;
        }
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> deleteAll() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            airRepository.truncateAir();
            lastMpRepository.truncateLastMp();
            mpRepository.truncateMp();
            nextMpRepository.truncateNextMp();
            flRepository.truncateFl();
        } catch (Exception e) {
            map.put("msg", "清空失败,请重试");
            return map;
        }
        map.put("msg", "清空成功");
        return map;
    }

    @Override
    public Map<String, Object> deleteMp() {
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
    public Map<String, Object> deleteFl() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            flRepository.truncateFl();
        } catch (Exception e) {
            map.put("msg", "清空失败,请重试");
            return map;
        }
        map.put("msg", "清空成功");
        return map;
    }

    @Override
    public Map<String, Object> deleteAir() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            airRepository.truncateAir();
        } catch (Exception e) {
            map.put("msg", "清空失败,请重试");
            return map;
        }
        map.put("msg", "清空成功");
        return map;
    }

    @Override
    public Map<String, Object> deleteLastMp() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            lastMpRepository.truncateLastMp();
        } catch (Exception e) {
            map.put("msg", "清空失败,请重试");
            return map;
        }
        map.put("msg", "清空成功");
        return map;
    }

    @Override
    public Map<String, Object> deleteNextMp() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            nextMpRepository.truncateNextMp();
        } catch (Exception e) {
            map.put("msg", "清空失败,请重试");
            return map;
        }
        map.put("msg", "清空成功");
        return map;
    }

    @Override
    public String deleteAirline(@PathVariable String airline) {
        airlineRepository.deleteById(airline);
        return "redirect:/data/airline";
    }

    @Override
    public String deleteCadre(@PathVariable String id) {
        cadreRepository.deleteById(Integer.parseInt(id));
        return "redirect:/data/cadre";
    }

    @Override
    public String deleteNightFlight(@PathVariable String number) {
        nightFlightRepository.deleteById(number);
        return "redirect:/data/nightFlight";
    }

    @Override
    public String deleteStageDoubleFlight(@PathVariable String number) {
        stageDoubleFlightRepository.deleteById(number);
        return "redirect:/data/stageDoubleFlight";
    }

    @Override
    public String airline(Model model) {
        List<Airline> airLineList = airlineRepository.findAll();
        long number = airlineRepository.count();
        model.addAttribute("airLineList", airLineList);
        model.addAttribute("number", number);
        return "data/airline.html";
    }

    @Override
    public String cadre(Model model) {
        List<Cadre> cadreList = cadreRepository.findAll();
        long number = cadreRepository.count();
        model.addAttribute("cadreList", cadreList);
        model.addAttribute("number", number);
        return "data/cadre.html";
    }

    @Override
    public String nightFlight(Model model) {
        List<NightFlight> nightFlightList = nightFlightRepository.findAll();
        long number = nightFlightRepository.count();
        model.addAttribute("nightFlightList", nightFlightList);
        model.addAttribute("number", number);
        return "data/nightFlight.html";
    }

    @Override
    public String stageDoubleFlight(Model model) {
        List<StageDoubleFlight> stageDoubleFlightList = stageDoubleFlightRepository.findAll();
        long number = stageDoubleFlightRepository.count();
        model.addAttribute("stageDoubleFlightList", stageDoubleFlightList);
        model.addAttribute("number", number);
        return "data/stageDoubleFlight.html";
    }

    @Override
    public Map<String, Object> updateAirline(HttpServletRequest request) {
        String airline = request.getParameter("airline");
        String remarks = request.getParameter("remarks");
        Airline air = new Airline(airline, remarks);
        airlineRepository.save(air);
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "修改成功");
        return map;
    }

    @Override
    public Map<String, Object> updateCadre(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String eid = request.getParameter("eid");
        String name = request.getParameter("name");
        if (name.equals("")) {
            map.put("msg", "修改失败：姓名不可为空");
            return map;
        }
        String department = request.getParameter("department");
        if (department.equals("")) {
            map.put("msg", "修改失败：部门不可为空");
            return map;
        }
        String qualify = request.getParameter("qualify");
        String category = request.getParameter("category");
        if (category.equals("")) {
            map.put("msg", "修改失败：空勤人员类别不可为空");
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
        map.put("msg", "修改成功");
        return map;
    }

    @Override
    public Map<String, Object> updateNightFlight(HttpServletRequest request) {
        String number = request.getParameter("number");
        String remarks = request.getParameter("remarks");
        NightFlight nightFlight = new NightFlight(number, remarks);
        nightFlightRepository.save(nightFlight);
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "修改成功");
        return map;
    }

    @Override
    public Map<String, Object> updateStageDoubleFlight(HttpServletRequest request) {
        String number = request.getParameter("number");
        String remarks = request.getParameter("remarks");
        StageDoubleFlight stageDoubleFlight = new StageDoubleFlight(number, remarks);
        stageDoubleFlightRepository.save(stageDoubleFlight);
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "修改成功");
        return map;
    }

}
