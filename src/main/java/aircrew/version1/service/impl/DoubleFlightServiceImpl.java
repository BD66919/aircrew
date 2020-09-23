package aircrew.version1.service.impl;

import aircrew.version1.entity.*;
import aircrew.version1.mapper.*;
import aircrew.version1.service.DoubleFlightService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Service
public class DoubleFlightServiceImpl implements DoubleFlightService {
    private final AirRepository airRepository;
    private final MpRepository mpRepository;
    private final CadreRepository cadreRepository;
    private final DoubleFlightRepository doubleFlightRepository;
    private final StageDoubleFlightRepository stageDoubleFlightRepository;
    private final AirlineRepository airlineRepository;
    private final NightFlightRepository nightFlightRepository;

    /**
     * 根据map的key排序
     *
     * @param map    待排序的map
     * @param isDesc 是否降序，true：降序，false：升序
     * @return 排序好的map
     * @author zero 2019/04/08
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = Maps.newLinkedHashMap();
        if (isDesc) {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey().reversed())
                    .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey())
                    .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }

    @Autowired
    DoubleFlightServiceImpl(MpRepository mpRepository, AirRepository airRepository, CadreRepository cadreRepository, DoubleFlightRepository doubleFlightRepository, StageDoubleFlightRepository stageDoubleFlightRepository, AirlineRepository airlineRepository, NightFlightRepository nightFlightRepository) {
        this.mpRepository = mpRepository;
        this.airRepository = airRepository;
        this.cadreRepository = cadreRepository;
        this.doubleFlightRepository = doubleFlightRepository;
        this.stageDoubleFlightRepository = stageDoubleFlightRepository;
        this.airlineRepository = airlineRepository;
        this.nightFlightRepository = nightFlightRepository;
    }

    @Override
    public Map<String, Object> addProperty(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String property = request.getParameter("property");
        List<String> propertiesList = doubleFlightRepository.propertiesList();
        if (propertiesList.contains(property)) {
            map.put("msg", "已存在该资质");
            return map;
        }
        doubleFlightRepository.addProperty(property);
        map.put("msg", "添加成功");
        return map;
    }

    @Override
    public String flight(Model model, HttpServletRequest request, HttpSession session) {
        String department = (String) request.getSession().getAttribute("department");
        model.addAttribute("department", department);
        if (doubleFlightRepository.findAll().isEmpty())
            model.addAttribute("doubleFlight", "待生成");
        else
            model.addAttribute("doubleFlight", "双机长航班表");
        DoubleFlight doubleFlight = doubleFlightRepository.findDescLimit1();
        if (doubleFlightRepository.findAll().size() != 0) {
            model.addAttribute("doubleFlightState", doubleFlight.getDate() + " " + doubleFlight.getNo() + " " + doubleFlight.getLine());
            model.addAttribute("doubleFlightCount", String.valueOf(doubleFlightRepository.count() - doubleFlightRepository.findByIsMore().size()));
        } else {
            model.addAttribute("doubleFlightState", "无");
            model.addAttribute("doubleFlightCount", "0");
        }
        if (doubleFlightRepository.findAll().isEmpty())
            model.addAttribute("moreDoubleFlight", "待生成");
        else
            model.addAttribute("moreDoubleFlight", "多机长航班表");

        DoubleFlight moreDoubleFlight = doubleFlightRepository.findByIsMoreDescLimit1();

        if (doubleFlightRepository.findByIsMore().size() != 0) {
            model.addAttribute("moreDoubleFlightState", moreDoubleFlight.getDate() + " " + moreDoubleFlight.getNo() + " " + moreDoubleFlight.getLine());
            model.addAttribute("moreDoubleFlightCount", String.valueOf(doubleFlightRepository.findByIsMore().size()));
        } else {
            model.addAttribute("moreDoubleFlightState", "无");
            model.addAttribute("moreDoubleFlightCount", "0");
        }

        if (doubleFlightRepository.getFlConfirm() == null) {
            model.addAttribute("flConfirm", "未确认");
        }
        if (doubleFlightRepository.getAirConfirm() == null) {
            model.addAttribute("airConfirm", "未确认");
        }
        if (doubleFlightRepository.getMpConfirm() == null) {
            model.addAttribute("mpConfirm", "未确认");
        } else {
            if (doubleFlightRepository.getFlConfirm().equals("未确认"))
                model.addAttribute("flConfirm", "未确认");
            else
                model.addAttribute("flConfirm", "已确认");

            if (doubleFlightRepository.getAirConfirm().equals("未确认"))
                model.addAttribute("airConfirm", "未确认");
            else
                model.addAttribute("airConfirm", "已确认");

            if (doubleFlightRepository.getMpConfirm().equals("未确认"))
                model.addAttribute("mpConfirm", "未确认");
            else
                model.addAttribute("mpConfirm", "已确认");
        }
        model.addAttribute("time", session.getAttribute("time"));
        return "doubleFlight/flight.html";
    }

    @Override
    public String property(Model model) {
        List<String> propertiesList = doubleFlightRepository.propertiesList();
        model.addAttribute("propertiesList", propertiesList);
        return "doubleFlight/property.html";
    }

    @Override
    public Map<String, Object> calculateFlight(HttpServletRequest request) {

        String doubleFlightStatus = request.getParameter("doubleFlight");
        String cadreStatus = request.getParameter("cadre");
        String nightFlightStatus = request.getParameter("nightFlight");
        String stageDoubleFlightStatus = request.getParameter("stageDoubleFlight");
        Map<String, Object> map = new HashMap<>();
        if (mpRepository.findAll().isEmpty()) {
            map.put("msg", "系统数据库数据为空，无法生成");
            return map;
        }

        doubleFlightRepository.cancelMpConfirm();
        doubleFlightRepository.cancelFlConfirm();
        doubleFlightRepository.cancelAirConfirm();
        doubleFlightRepository.truncateDoubleFlight();
        //生成双组航班
        List<DoubleFlight> doubleFlightList = new ArrayList<>();
        List<Mp> moreMpList = new ArrayList<>();
        List<Airline> airlines = airlineRepository.findAll();
        List<Cadre> cadreList = cadreRepository.findAll();
        List<NightFlight> nightFlightList = nightFlightRepository.findAll();
        List<StageDoubleFlight> stageDoubleFlightList = stageDoubleFlightRepository.findAll();

        String cadreDate = "";
        String cadreNo = "";
        String cadre = "";
        int id = 0;
        DoubleFlight doubleFlightBasic = new DoubleFlight();

        //生成双机长航班
        List<Mp> doubleMpByFlightNoOrderList = mpRepository.orderByFlightNo();
        //统计机长数量是否等于2
        int count = 0;
        List<Mp> doubleMpList = new ArrayList<>();
        //判断是否满足双机长条件
        String doubleFlightNo = doubleMpByFlightNoOrderList.get(0).getFlightNo();
        Mp mp1 = doubleMpByFlightNoOrderList.get(0);
        for (Mp mp : doubleMpByFlightNoOrderList
        ) {
            if (!doubleFlightNo.equals(mp.getFlightNo()))
                if (!doubleFlightNo.equals(mp.getFlightNo())) {
                    doubleFlightNo = mp.getFlightNo();
                    if (count == 2)
                        doubleMpList.add(mp1);
                    if (count >= 3)
                        moreMpList.add(mp1);
                    mp1 = mp;
                    count = 0;
                }

            if (!mp.getPost().substring(0, 1).equals("F") && !mp.getPost().substring(0, 1).equals("G") && mp.getProperty().equals("正班")) {
                count++;
            }
        }

        for (Mp mp : doubleMpList
        ) {
            boolean isFlag = true;
            DoubleFlight doubleFlight = new DoubleFlight();
            doubleFlight.setDoubleLine("");
            doubleFlight.setNightFlight("");
            doubleFlight.setStageDoubleFlight("");
            doubleFlight.setFlightCheck("");
            doubleFlight.setCadre("");
            doubleFlight.setAirChangeRecord("");
            doubleFlight.setMpChangeRecord("");
            doubleFlight.setAirRemark("");
            doubleFlight.setMpRemark("");
            doubleFlight.setId(id);
            doubleFlight.setDate(mp.getDate());
            doubleFlight.setNo(mp.getFlightNo());
            doubleFlight.setLine(mp.getAirLine());
            doubleFlight.setTcc(mp.getTcc());
            Map<String, String> captainMap = new HashMap<>();
            Map<Integer, String> orderMap = new HashMap<>();

            List<Mp> flightNoMpList = mpRepository.findByFlightNo(mp.getDate(), mp.getFlightNo(), mp.getType());
            for (Mp eachMp : flightNoMpList
            ) {
                if (eachMp.getPost().substring(0, 1).equals("F") || eachMp.getPost().substring(0, 1).equals("G"))
                    continue;
                if (cadreStatus != null)
                    if (eachMp.getPost().equals("C飞行检查员"))
                        if (doubleFlight.getCadre() == null) {
                            doubleFlight.setFlightCheck("C检查航线");
                        }
                for (Cadre cadre1 : cadreList
                ) {
                    if (eachMp.getEid() == cadre1.getEid()) {
                        doubleFlight.setCadre(cadre1.getCategory());
                        isFlag = false;
                    }
                }

                List<Air> airList = airRepository.findByFlightNo(eachMp.getDate(), eachMp.getFlightNo().split("/")[0], eachMp.getEid());
                if (airList.size() == 0)
                    continue;
                Air air = airList.get(0);
                //去掉字符串中的数字
                captainMap.put(String.valueOf(eachMp.getName()).replaceAll("\\d+", ""), eachMp.getPost());
                if (air != null)
                    orderMap.put(Integer.parseInt(air.getPosition()), air.getName().replaceAll("\\d+", ""));
            }
            orderMap = sortByKey(orderMap, false);
            int index = 0;
            for (String value : orderMap.values()) {
                if (index == 0) {
                    doubleFlight.setFirstPosition(value);
                    doubleFlight.setFirstQualification(captainMap.get(value));
                    doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1));
                } else if (index == 1) {
                    doubleFlight.setSecondPosition(value);
                    doubleFlight.setSecondQualification(captainMap.get(value));
                    doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
                }
                index++;
            }

            if (doubleFlightStatus != null)
                for (Airline airline : airlines) {
                    if (doubleFlight.getTcc().contains(airline.getAirline())) {
                        doubleFlight.setDoubleLine("双组航线");
                        isFlag = false;
                    }
                }

            if (nightFlightStatus != null)
                for (NightFlight nightFlight : nightFlightList) {
                    if (doubleFlight.getNo().contains(nightFlight.getNumber())) {
                        doubleFlight.setNightFlight("过夜航班");
                        isFlag = false;
                    }
                }

            if (stageDoubleFlightStatus != null)
                for (StageDoubleFlight stageDoubleFlight : stageDoubleFlightList) {
                    if (doubleFlight.getNo().contains(stageDoubleFlight.getNumber())) {
                        doubleFlight.setStageDoubleFlight("阶段双组航班");
                        isFlag = false;
                    }
                }

            System.out.println(doubleFlight);
            if (doubleFlight.getFlightCombine() == null)
                continue;
            if (doubleFlight.getFlightCombine().equals("JJ") || doubleFlight.getFlightCombine().contains("C"))
                isFlag = false;

            if (isFlag)
                doubleFlight.setIsFlag(isFlag);

            doubleFlightList.add(doubleFlight);
        }

        doubleFlightRepository.saveAll(doubleFlightList);

        for (
                Mp mp : moreMpList
        ) {
            DoubleFlight doubleFlight = new DoubleFlight();
            doubleFlight.setDoubleLine("");
            doubleFlight.setNightFlight("");
            doubleFlight.setStageDoubleFlight("");
            doubleFlight.setFlightCheck("");
            doubleFlight.setCadre("");
            doubleFlight.setAirChangeRecord("");
            doubleFlight.setMpChangeRecord("");
            doubleFlight.setAirRemark("");
            doubleFlight.setMpRemark("");
            doubleFlight.setId(id);
            doubleFlight.setDate(mp.getDate());
            doubleFlight.setNo(mp.getFlightNo());
            doubleFlight.setLine(mp.getAirLine());
            doubleFlight.setTcc(mp.getTcc());
            Map<String, String> captainMap = new HashMap<>();
            Map<Integer, String> orderMap = new HashMap<>();
            List<Mp> flightNoMpList = mpRepository.findByFlightNo(mp.getDate(), mp.getFlightNo(), mp.getType());
            for (Mp eachMp : flightNoMpList
            ) {
                if (eachMp.getPost().substring(0, 1).equals("F") || eachMp.getPost().substring(0, 1).equals("G"))
                    continue;
                if (cadreStatus != null)
                    if (eachMp.getPost().equals("C飞行检查员"))
                        if (doubleFlight.getCadre() == null) {
                            doubleFlight.setFlightCheck("C检查航线");
                        }
                for (Cadre cadre1 : cadreList
                ) {
                    if (eachMp.getEid() == cadre1.getEid()) {
                        doubleFlight.setCadre(cadre1.getCategory());
                    }
                }
                List<Air> airList = airRepository.findByFlightNo(eachMp.getDate(), eachMp.getFlightNo().split("/")[0], eachMp.getEid());
                if (airList.size() == 0)
                    continue;
                Air air = airList.get(0);
                captainMap.put(String.valueOf(eachMp.getName()).replaceAll("\\d+", ""), eachMp.getPost());
                if (air != null)
                    orderMap.put(Integer.parseInt(air.getPosition()), air.getName().replaceAll("\\d+", ""));
            }
            orderMap = sortByKey(orderMap, false);
            int index = 0;
            for (String value : orderMap.values()) {
                if (index == 0) {
                    doubleFlight.setFirstPosition(value);
                    doubleFlight.setFirstQualification(captainMap.get(value));
                    doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1));
                } else if (index == 1) {
                    doubleFlight.setSecondPosition(value);
                    doubleFlight.setSecondQualification(captainMap.get(value));
                    doubleFlight.setFlightCombine(doubleFlight.getFlightCombine() + doubleFlight.getSecondQualification().substring(0, 1));
                } else if (index == 2) {
                    doubleFlight.setThirdPosition(value);
                    doubleFlight.setThirdQualification(captainMap.get(value));
                    doubleFlight.setFlightCombine(doubleFlight.getFlightCombine() + doubleFlight.getThirdQualification().substring(0, 1));
                } else if (index == 3) {
                    doubleFlight.setFourthPosition(value);
                    doubleFlight.setFourthQualification(captainMap.get(value));
                    doubleFlight.setFlightCombine(doubleFlight.getFlightCombine() + doubleFlight.getFourthQualification().substring(0, 1));
                } else if (index == 4) {
                    doubleFlight.setFifthPosition(value);
                    doubleFlight.setFifthQualification(captainMap.get(value));
                    doubleFlight.setFlightCombine(doubleFlight.getFlightCombine() + doubleFlight.getFifthQualification().substring(0, 1));
                }
                index++;
            }
            if (cadreStatus != null)
                if (cadreDate.equals(doubleFlightBasic.getDate()) && cadreNo.equals(doubleFlightBasic.getNo())) {
                    doubleFlight.setCadre(cadre);
                }

            if (doubleFlightStatus != null)
                for (Airline airline : airlines) {
                    if (doubleFlight.getTcc().contains(airline.getAirline())) {
                        doubleFlight.setDoubleLine("双组航线");
                    }
                }

            if (nightFlightStatus != null)
                for (NightFlight nightFlight : nightFlightList) {
                    if (doubleFlight.getNo().contains(nightFlight.getNumber())) {
                        doubleFlight.setNightFlight("过夜航班");
                    }
                }

            if (stageDoubleFlightStatus != null)
                for (StageDoubleFlight stageDoubleFlight : stageDoubleFlightList) {
                    if (doubleFlight.getNo().contains(stageDoubleFlight.getNumber())) {
                        doubleFlight.setStageDoubleFlight("阶段双组航班");
                    }
                }

            doubleFlight.setIsMore(true);
            doubleFlightRepository.save(doubleFlight);

        }


        map.put("msg", "双机长航班表已生成");
        return map;
    }

    @Override
    public String doubleFlight(Model model) {
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        int count = doubleFlightList.size();
        for (int i = 0; i < doubleFlightList.size(); i++) {
            if (doubleFlightList.get(i).getIsMore() != null) {
                doubleFlightList.remove(i);
                i--;
                count--;
            }
        }
        model.addAttribute("doubleFlightList", doubleFlightList);
        model.addAttribute("count", count);
        return "doubleFlight/doubleFlight.html";
    }

    @Override
    public String moreDoubleFlight(Model model) {
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        int count = doubleFlightList.size();
        for (int i = 0; i < doubleFlightList.size(); i++) {
            if (doubleFlightList.get(i).getIsMore() == null) {
                doubleFlightList.remove(i);
                i--;
                count--;
            }
        }
        int select = 2;
        for (DoubleFlight doubleFlight : doubleFlightList
        ) {
            if (doubleFlight.getThirdPosition() != null)
                select = 3;
            if (doubleFlight.getFourthPosition() != null)
                select = 4;
            if (doubleFlight.getFifthQualification() != null)
                select = 5;
        }
        List<String> propertiesList = doubleFlightRepository.propertiesList();
        model.addAttribute("doubleFlightList", doubleFlightList);
        model.addAttribute("count", count);
        model.addAttribute("propertiesList", propertiesList);
        model.addAttribute("select", select);
        return "doubleFlight/moreDoubleFlight.html";
    }

    @Override
    public String filterDoubleFlight(Model model) {
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        int count = doubleFlightList.size();
        for (int i = 0; i < doubleFlightList.size(); i++) {
            if (doubleFlightList.get(i).getIsFlag() == null) {
                doubleFlightList.remove(i);
                i--;
                count--;
            }
        }
        List<String> propertiesList = doubleFlightRepository.propertiesList();
        model.addAttribute("doubleFlightList", doubleFlightList);
        model.addAttribute("count", count);
        model.addAttribute("propertiesList", propertiesList);
        return "doubleFlight/filterDoubleFlight.html";
    }

    @Override
    public Map<String, Object> updateDoubleFlight(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        DoubleFlight doubleFlight = doubleFlightRepository.findById(id).orElse(null);
        HashMap<String, Object> map = new HashMap<>();
        if (doubleFlight != null) {
            doubleFlight.setLine(request.getParameter("line"));
            doubleFlight.setFirstPosition(request.getParameter("firstPosition"));
            doubleFlight.setFirstQualification(request.getParameter("firstQualification"));
            doubleFlight.setSecondPosition(request.getParameter("secondPosition"));
            doubleFlight.setSecondQualification(request.getParameter("secondQualification"));
            doubleFlight.setDoubleLine(request.getParameter("doubleLine"));
            doubleFlight.setNightFlight(request.getParameter("nightFlight"));
            doubleFlight.setStageDoubleFlight(request.getParameter("stageDoubleFlight"));
            doubleFlight.setFlightCheck(request.getParameter("flightCheck"));
            doubleFlight.setCadre(request.getParameter("cadre"));
            doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
            doubleFlightRepository.save(doubleFlight);
            map.put("msg", "修改成功");
            return map;
        }
        map.put("msg", "修改失败");
        return map;
    }

    @Override
    public Map<String, Object> updateMoreDoubleFlight(HttpServletRequest request) {
        String department = (String) request.getSession().getAttribute("department");
        int id = Integer.parseInt(request.getParameter("id"));
        DoubleFlight doubleFlight = doubleFlightRepository.findById(id).orElse(null);
        HashMap<String, Object> map = new HashMap<>();
        if (doubleFlightRepository.getFlConfirm().equals("已确认")) {
            //飞行部修改
            if (doubleFlight != null & department.equals("飞行")) {
                String recorder = "";
                //资质修改
                if (!doubleFlight.getFirstQualification().equals(request.getParameter("firstQualification")))
                    recorder = recorder.concat("资质1：" + doubleFlight.getFirstQualification() + "修改为" + request.getParameter("firstQualification")) + ";";
                if (!doubleFlight.getSecondQualification().equals(request.getParameter("secondQualification")))
                    recorder = recorder.concat("资质2：" + doubleFlight.getSecondQualification() + "修改为" + request.getParameter("secondQualification")) + ";";
                if (request.getParameter("thirdQualification") != null)
                    if (!doubleFlight.getThirdQualification().equals(request.getParameter("thirdQualification")))
                        recorder = recorder.concat("资质3：" + doubleFlight.getThirdQualification() + "修改为" + request.getParameter("thirdQualification")) + ";";
                if (request.getParameter("fourthQualification") != null)
                    if (!doubleFlight.getFourthQualification().equals(request.getParameter("fourthQualification")))
                        recorder = recorder.concat("资质4：" + doubleFlight.getFourthQualification() + "修改为" + request.getParameter("fourthQualification")) + ";";
                if (request.getParameter("fifthQualification") != null)
                    if (!doubleFlight.getFifthQualification().equals(request.getParameter("fifthQualification")))
                        recorder = recorder.concat("资质5：" + doubleFlight.getFifthQualification() + "修改为" + request.getParameter("fifthQualification")) + ";";
                doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + " " + recorder);
                doubleFlight.setLine(request.getParameter("line"));
                doubleFlight.setFirstPosition(request.getParameter("firstPosition"));
                doubleFlight.setFirstQualification(request.getParameter("firstQualification"));
                doubleFlight.setSecondPosition(request.getParameter("secondPosition"));
                doubleFlight.setSecondQualification(request.getParameter("secondQualification"));
                if (request.getParameter("thirdQualification") != null) {
                    doubleFlight.setThirdPosition(request.getParameter("thirdPosition"));
                    doubleFlight.setThirdQualification(request.getParameter("thirdQualification"));
                }
                if (request.getParameter("fourthQualification") != null) {
                    doubleFlight.setFourthPosition(request.getParameter("fourthPosition"));
                    doubleFlight.setFourthQualification(request.getParameter("fourthQualification"));
                }
                if (request.getParameter("fifthQualification") != null) {
                    doubleFlight.setFifthPosition(request.getParameter("fifthPosition"));
                    doubleFlight.setFifthQualification(request.getParameter("fifthQualification"));
                }
                doubleFlight.setDoubleLine(request.getParameter("doubleLine"));
                doubleFlight.setNightFlight(request.getParameter("nightFlight"));
                doubleFlight.setStageDoubleFlight(request.getParameter("stageDoubleFlight"));
                doubleFlight.setFlightCheck(request.getParameter("flightCheck"));
                if (doubleFlight.getFlightCheck() != null) {
                    if (request.getParameter("checkSelect1") != null) {
                        doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + "资质1：" + doubleFlight.getFirstQualification() + "修改为C飞行检查员;");
                        doubleFlight.setFirstQualification("C飞行检查员");
                        doubleFlight.setFlightCheck("机组1检查航线");
                    }
                    if (request.getParameter("checkSelect2") != null) {
                        doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + "资质2：" + doubleFlight.getSecondQualification() + "修改为C飞行检查员;");
                        doubleFlight.setSecondQualification("C飞行检查员");
                        doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组2检查航线");
                    }
                    if (request.getParameter("checkSelect3") != null) {
                        doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + "资质3：" + doubleFlight.getThirdQualification() + "修改为C飞行检查员;");
                        doubleFlight.setSecondQualification("C飞行检查员");
                        doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组3检查航线");
                    }
                    if (request.getParameter("checkSelect4") != null) {
                        doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + "资质4：" + doubleFlight.getFourthQualification() + "修改为C飞行检查员;");
                        doubleFlight.setSecondQualification("C飞行检查员");
                        doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组4检查航线");
                    }
                    if (request.getParameter("checkSelect5") != null) {
                        doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + "资质5：" + doubleFlight.getFifthQualification() + "修改为C飞行检查员;");
                        doubleFlight.setSecondQualification("C飞行检查员");
                        doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组5检查航线");
                    }
                }
                if (request.getParameter("cadreSelect1") != null)
                    doubleFlight.setCadre("1" + request.getParameter("cadre"));
                if (request.getParameter("cadreSelect2") != null)
                    doubleFlight.setCadre(doubleFlight.getCadre() + " " + "2" + request.getParameter("cadre"));
                if (request.getParameter("cadreSelect3") != null)
                    doubleFlight.setCadre(doubleFlight.getCadre() + " " + "3" + request.getParameter("cadre"));
                if (request.getParameter("cadreSelect4") != null)
                    doubleFlight.setCadre(doubleFlight.getCadre() + " " + "4" + request.getParameter("cadre"));
                if (request.getParameter("cadreSelect5") != null)
                    doubleFlight.setCadre(doubleFlight.getCadre() + " " + "5" + request.getParameter("cadre"));

                doubleFlight.setAirRemark(request.getParameter("remarks"));
                String combine = doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1) + doubleFlight.getThirdQualification().substring(0, 1);
                if (request.getParameter("fourthQualification") != null)
                    combine = combine + doubleFlight.getFourthQualification().substring(0, 1);
                if (request.getParameter("fifthQualification") != null)
                    combine = combine + doubleFlight.getFifthQualification().substring(0, 1);
                doubleFlight.setFlightCombine(combine);
                doubleFlightRepository.save(doubleFlight);
                map.put("msg", "修改成功");
                return map;
            }

            //人力部修改
            if (doubleFlightRepository.getAirConfirm().equals("已确认")) {
                if (doubleFlight != null & department.equals("人力")) {
                    String recorder = "";
                    if (!doubleFlight.getFirstQualification().equals(request.getParameter("firstQualification")))
                        recorder = recorder.concat("资质1：" + doubleFlight.getFirstQualification() + "修改为" + request.getParameter("firstQualification")) + ";";
                    if (!doubleFlight.getSecondQualification().equals(request.getParameter("secondQualification")))
                        recorder = recorder.concat("资质2：" + doubleFlight.getSecondQualification() + "修改为" + request.getParameter("secondQualification")) + ";";
                    if (request.getParameter("thirdQualification") != null)
                        if (!doubleFlight.getThirdQualification().equals(request.getParameter("thirdQualification")))
                            recorder = recorder.concat("资质3：" + doubleFlight.getThirdQualification() + "修改为" + request.getParameter("thirdQualification")) + ";";
                    if (request.getParameter("fourthQualification") != null)
                        if (!doubleFlight.getFourthQualification().equals(request.getParameter("fourthQualification")))
                            recorder = recorder.concat("资质4：" + doubleFlight.getFourthQualification() + "修改为" + request.getParameter("fourthQualification")) + ";";
                    if (request.getParameter("fifthQualification") != null)
                        if (!doubleFlight.getFifthQualification().equals(request.getParameter("fifthQualification")))
                            recorder = recorder.concat("资质5：" + doubleFlight.getFifthQualification() + "修改为" + request.getParameter("fifthQualification")) + ";";
                    doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + " " + recorder);
                    doubleFlight.setLine(request.getParameter("line"));
                    doubleFlight.setFirstPosition(request.getParameter("firstPosition"));
                    doubleFlight.setFirstQualification(request.getParameter("firstQualification"));
                    doubleFlight.setSecondPosition(request.getParameter("secondPosition"));
                    doubleFlight.setSecondQualification(request.getParameter("secondQualification"));
                    if (request.getParameter("thirdQualification") != null) {
                        doubleFlight.setThirdPosition(request.getParameter("thirdPosition"));
                        doubleFlight.setThirdQualification(request.getParameter("thirdQualification"));
                    }
                    if (request.getParameter("fourthQualification") != null) {
                        doubleFlight.setFourthPosition(request.getParameter("fourthPosition"));
                        doubleFlight.setFourthQualification(request.getParameter("fourthQualification"));
                    }
                    if (request.getParameter("fifthQualification") != null) {
                        doubleFlight.setFifthPosition(request.getParameter("fifthPosition"));
                        doubleFlight.setFifthQualification(request.getParameter("fifthQualification"));
                    }
                    doubleFlight.setDoubleLine(request.getParameter("doubleLine"));
                    doubleFlight.setNightFlight(request.getParameter("nightFlight"));
                    doubleFlight.setStageDoubleFlight(request.getParameter("stageDoubleFlight"));
                    doubleFlight.setFlightCheck(request.getParameter("flightCheck"));
                    if (doubleFlight.getFlightCheck() != null) {
                        if (request.getParameter("checkSelect1") != null) {
                            doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + "资质1：" + doubleFlight.getFirstQualification() + "修改为C飞行检查员;");
                            doubleFlight.setFirstQualification("C飞行检查员");
                            doubleFlight.setFlightCheck("机组1检查航线");
                        }
                        if (request.getParameter("checkSelect2") != null) {
                            doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + "资质2：" + doubleFlight.getSecondQualification() + "修改为C飞行检查员;");
                            doubleFlight.setSecondQualification("C飞行检查员");
                            doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组2检查航线");
                        }
                        if (request.getParameter("checkSelect3") != null) {
                            doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + "资质3：" + doubleFlight.getThirdQualification() + "修改为C飞行检查员;");
                            doubleFlight.setSecondQualification("C飞行检查员");
                            doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组3检查航线");
                        }
                        if (request.getParameter("checkSelect4") != null) {
                            doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + "资质4：" + doubleFlight.getFourthQualification() + "修改为C飞行检查员;");
                            doubleFlight.setSecondQualification("C飞行检查员");
                            doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组4检查航线");
                        }
                        if (request.getParameter("checkSelect5") != null) {
                            doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + "资质5：" + doubleFlight.getFifthQualification() + "修改为C飞行检查员;");
                            doubleFlight.setSecondQualification("C飞行检查员");
                            doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组5检查航线");
                        }
                    }
                    if (request.getParameter("cadreSelect1") != null)
                        doubleFlight.setCadre("1" + request.getParameter("cadre"));
                    if (request.getParameter("cadreSelect2") != null)
                        doubleFlight.setCadre(doubleFlight.getCadre() + " " + "2" + request.getParameter("cadre"));
                    if (request.getParameter("cadreSelect3") != null)
                        doubleFlight.setCadre(doubleFlight.getCadre() + " " + "3" + request.getParameter("cadre"));
                    if (request.getParameter("cadreSelect4") != null)
                        doubleFlight.setCadre(doubleFlight.getCadre() + " " + "4" + request.getParameter("cadre"));
                    if (request.getParameter("cadreSelect5") != null)
                        doubleFlight.setCadre(doubleFlight.getCadre() + " " + "5" + request.getParameter("cadre"));
                    doubleFlight.setMpRemark(request.getParameter("remarks"));
                    String combine = doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1) + doubleFlight.getThirdQualification().substring(0, 1);
                    if (request.getParameter("fourthQualification") != null)
                        combine = combine + doubleFlight.getFourthQualification().substring(0, 1);
                    if (request.getParameter("fifthQualification") != null)
                        combine = combine + doubleFlight.getFifthQualification().substring(0, 1);
                    doubleFlight.setFlightCombine(combine);
                    doubleFlightRepository.save(doubleFlight);
                    map.put("msg", "修改成功");
                    return map;
                }
            } else {
                map.put("msg", "待飞行部确认方可修改");
                return map;
            }
        } else
            map.put("msg", "待财务部确认方可修改");
        return map;
    }

    @Override
    public Map<String, Object> updateFilterDoubleFlight(HttpServletRequest request) {
        String department = (String) request.getSession().getAttribute("department");
        int id = Integer.parseInt(request.getParameter("id"));
        DoubleFlight doubleFlight = doubleFlightRepository.findById(id).orElse(null);
        HashMap<String, Object> map = new HashMap<>();
        //财务部修改
        if (department.equals("财务")) {
            assert doubleFlight != null;
            doubleFlight.setLine(request.getParameter("line"));
            doubleFlight.setFirstPosition(request.getParameter("firstPosition"));
            doubleFlight.setFirstQualification(request.getParameter("firstQualification"));
            doubleFlight.setSecondPosition(request.getParameter("secondPosition"));
            doubleFlight.setSecondQualification(request.getParameter("secondQualification"));
            doubleFlight.setDoubleLine(request.getParameter("doubleLine"));
            doubleFlight.setNightFlight(request.getParameter("nightFlight"));
            doubleFlight.setStageDoubleFlight(request.getParameter("stageDoubleFlight"));
            doubleFlight.setFlightCheck(request.getParameter("flightCheck"));
            doubleFlight.setCadre(request.getParameter("cadre"));
            doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
            doubleFlightRepository.save(doubleFlight);
            map.put("msg", "修改成功");
            return map;
        }

        if (doubleFlightRepository.getFlConfirm().equals("已确认")) {
            //飞行部修改
            if (doubleFlight != null & department.equals("飞行")) {
                String recorder = "";
                //资质修改
                if (!doubleFlight.getFirstQualification().equals(request.getParameter("firstQualification")))
                    recorder = recorder.concat("资质1：" + doubleFlight.getFirstQualification() + "修改为" + request.getParameter("firstQualification")) + ";";
                if (!doubleFlight.getSecondQualification().equals(request.getParameter("secondQualification")))
                    recorder = recorder.concat("资质2：" + doubleFlight.getSecondQualification() + "修改为" + request.getParameter("secondQualification")) + ";";
                doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + " " + recorder);
                doubleFlight.setLine(request.getParameter("line"));
                doubleFlight.setFirstPosition(request.getParameter("firstPosition"));
                doubleFlight.setFirstQualification(request.getParameter("firstQualification"));
                doubleFlight.setSecondPosition(request.getParameter("secondPosition"));
                doubleFlight.setSecondQualification(request.getParameter("secondQualification"));
                doubleFlight.setDoubleLine(request.getParameter("doubleLine"));
                doubleFlight.setNightFlight(request.getParameter("nightFlight"));
                doubleFlight.setStageDoubleFlight(request.getParameter("stageDoubleFlight"));
                doubleFlight.setFlightCheck(request.getParameter("flightCheck"));
                if (doubleFlight.getFlightCheck() != null) {
                    if (request.getParameter("checkSelect1") != null) {
                        doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + "资质1：" + doubleFlight.getFirstQualification() + "修改为C飞行检查员;");
                        doubleFlight.setFirstQualification("C飞行检查员");
                        doubleFlight.setFlightCheck("机组1检查航线");
                    }
                    if (request.getParameter("checkSelect2") != null) {
                        doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + "资质2：" + doubleFlight.getSecondQualification() + "修改为C飞行检查员;");
                        doubleFlight.setSecondQualification("C飞行检查员");
                        doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组2检查航线");
                    }
                }
                if (request.getParameter("cadreSelect1") != null)
                    doubleFlight.setCadre("1" + request.getParameter("cadre"));
                if (request.getParameter("cadreSelect2") != null)
                    doubleFlight.setCadre(doubleFlight.getCadre() + " " + "2" + request.getParameter("cadre"));
                doubleFlight.setAirRemark(request.getParameter("remarks"));
                doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
                doubleFlightRepository.save(doubleFlight);
                map.put("msg", "修改成功");
                return map;
            }

            //人力部修改
            if (doubleFlightRepository.getAirConfirm().equals("已确认")) {
                if (doubleFlight != null & department.equals("人力")) {
                    String recorder = "";
                    if (!doubleFlight.getFirstQualification().equals(request.getParameter("firstQualification")))
                        recorder = recorder.concat("资质1：" + doubleFlight.getFirstQualification() + "修改为" + request.getParameter("firstQualification")) + ";";
                    if (!doubleFlight.getSecondQualification().equals(request.getParameter("secondQualification")))
                        recorder = recorder.concat("资质2：" + doubleFlight.getSecondQualification() + "修改为" + request.getParameter("secondQualification")) + ";";
                    doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + " " + recorder);
                    doubleFlight.setLine(request.getParameter("line"));
                    doubleFlight.setFirstPosition(request.getParameter("firstPosition"));
                    doubleFlight.setFirstQualification(request.getParameter("firstQualification"));
                    doubleFlight.setSecondPosition(request.getParameter("secondPosition"));
                    doubleFlight.setSecondQualification(request.getParameter("secondQualification"));
                    doubleFlight.setDoubleLine(request.getParameter("doubleLine"));
                    doubleFlight.setNightFlight(request.getParameter("nightFlight"));
                    doubleFlight.setStageDoubleFlight(request.getParameter("stageDoubleFlight"));
                    doubleFlight.setFlightCheck(request.getParameter("flightCheck"));
                    if (doubleFlight.getFlightCheck() != null) {
                        if (request.getParameter("checkSelect1") != null) {
                            doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + "资质1：" + doubleFlight.getFirstQualification() + "修改为C飞行检查员;");
                            doubleFlight.setFirstQualification("C飞行检查员");
                            doubleFlight.setFlightCheck("机组1检查航线");
                        }
                        if (request.getParameter("checkSelect2") != null) {
                            doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + "资质2：" + doubleFlight.getSecondQualification() + "修改为C飞行检查员;");
                            doubleFlight.setSecondQualification("C飞行检查员");
                            doubleFlight.setFlightCheck(doubleFlight.getFlightCheck() + "机组2检查航线");
                        }
                    }
                    if (request.getParameter("cadreSelect1") != null)
                        doubleFlight.setCadre("1" + request.getParameter("cadre"));
                    if (request.getParameter("cadreSelect2") != null)
                        doubleFlight.setCadre(doubleFlight.getCadre() + " " + "2" + request.getParameter("cadre"));
                    doubleFlight.setMpRemark(request.getParameter("remarks"));
                    doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
                    doubleFlightRepository.save(doubleFlight);
                    map.put("msg", "修改成功");
                    return map;
                }
            } else {
                map.put("msg", "待飞行部确认方可修改");
                return map;
            }
        } else
            map.put("msg", "待财务部确认方可修改");
        return map;
    }

    @Override
    public void confirm(HttpServletRequest request) {
        String department = (String) request.getSession().getAttribute("department");
        if (department.equals("飞行"))
            doubleFlightRepository.airConfirm();
        if (department.equals("财务"))
            doubleFlightRepository.flConfirm();
        if (department.equals("人力"))
            doubleFlightRepository.mpConfirm();
    }

    @Override
    public void cancelConfirm(HttpServletRequest request) {
        String department = (String) request.getSession().getAttribute("department");
        if (department.equals("飞行"))
            doubleFlightRepository.cancelAirConfirm();
        if (department.equals("财务"))
            doubleFlightRepository.cancelFlConfirm();
        if (department.equals("人力"))
            doubleFlightRepository.cancelMpConfirm();
    }

    @Override
    public Map<String, Object> downloadJudge() {
        HashMap<String, Object> map = new HashMap<>();
        if (doubleFlightRepository.getAirConfirm().equals("已确认") & doubleFlightRepository.getFlConfirm().equals("已确认") & doubleFlightRepository.getMpConfirm().equals("已确认")) {
            map.put("msg", "三部门已确认");
            return map;
        } else {
            map.put("msg", "存在尚未确认的部门");
            return map;
        }
    }

    @Override
    public void downloadDoubleFlight(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");
        //设置宽度
        sheet.setColumnWidth(0, 1338);
        sheet.setColumnWidth(1, 3088);
        sheet.setColumnWidth(2, 3600);
        sheet.setColumnWidth(3, 3800);
        sheet.setColumnWidth(4, 4088);
        sheet.setColumnWidth(5, 3225);
        sheet.setColumnWidth(6, 3225);
        sheet.setColumnWidth(7, 3225);
        sheet.setColumnWidth(8, 3225);
        sheet.setColumnWidth(9, 2913);
        sheet.setColumnWidth(10, 2975);
        sheet.setColumnWidth(11, 2975);
        sheet.setColumnWidth(12, 2725);
        sheet.setColumnWidth(13, 2888);
        sheet.setColumnWidth(14, 2975);
        sheet.setColumnWidth(15, 3600);
        sheet.setColumnWidth(16, 3713);
        sheet.setColumnWidth(17, 3713);
        sheet.setColumnWidth(18, 3713);
        sheet.setColumnWidth(19, 3713);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        try {
            for (int i = 0; i < doubleFlightList.size(); i++) {
                if (doubleFlightList.get(i).getIsFlag() == null) {
                    doubleFlightList.remove(i);
                    i--;
                }
            }
        } catch (NullPointerException ignored) {
        } finally {
            String fileName = "双组航班表" + ".xls";
            int rowNum = 1;
            String[] headers = {"序号", "起飞日期", "航班号", "航线名称", "城市三字码", "机组1", "资质1", "机组2", "资质2", "机组组合", "双组航线检查", "过夜航班", "阶段双组", "C检查航线", "飞行管理人员", "飞行部资质修改", "飞行部修改说明", "人力部资质修改", "人力部修改说明"};
            HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(cellStyle);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }
            //在表中存放查询到的数据放入对应的列
            for (DoubleFlight doubleFlight : doubleFlightList) {
                //设置居中
                HSSFRow row1 = sheet.createRow(rowNum);
                String[] value = {String.valueOf(rowNum), doubleFlight.getDate(), doubleFlight.getNo(), doubleFlight.getLine(), doubleFlight.getTcc(), doubleFlight.getFirstPosition(), doubleFlight.getFirstQualification(), doubleFlight.getSecondPosition(),
                        doubleFlight.getSecondQualification(), doubleFlight.getFlightCombine(), doubleFlight.getDoubleLine(), doubleFlight.getNightFlight(), doubleFlight.getStageDoubleFlight(), doubleFlight.getFlightCheck(), doubleFlight.getCadre(), doubleFlight.getAirChangeRecord(),
                        doubleFlight.getAirRemark(), doubleFlight.getMpChangeRecord(), doubleFlight.getMpRemark()};
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
    }

    @Override
    public void downloadMoreDoubleFlight(HttpServletResponse response) throws IOException {
        HSSFWorkbook moreWorkBook = new HSSFWorkbook();
        HSSFSheet moreSheet = moreWorkBook.createSheet("信息表");
        //设置宽度
        moreSheet.setColumnWidth(0, 1338);
        moreSheet.setColumnWidth(1, 3088);
        moreSheet.setColumnWidth(2, 3600);
        moreSheet.setColumnWidth(3, 3800);
        moreSheet.setColumnWidth(4, 4088);

        moreSheet.setColumnWidth(5, 3225);
        moreSheet.setColumnWidth(6, 3225);

        moreSheet.setColumnWidth(7, 3225);
        moreSheet.setColumnWidth(8, 3225);

        moreSheet.setColumnWidth(9, 3225);
        moreSheet.setColumnWidth(10, 3225);

        moreSheet.setColumnWidth(11, 3225);
        moreSheet.setColumnWidth(12, 3225);

        moreSheet.setColumnWidth(13, 3225);
        moreSheet.setColumnWidth(14, 3225);

        moreSheet.setColumnWidth(15, 3225);
        moreSheet.setColumnWidth(16, 3225);
        moreSheet.setColumnWidth(17, 2913);
        moreSheet.setColumnWidth(18, 2975);
        moreSheet.setColumnWidth(19, 2975);
        moreSheet.setColumnWidth(20, 2725);
        moreSheet.setColumnWidth(21, 2888);
        moreSheet.setColumnWidth(22, 2975);
        moreSheet.setColumnWidth(23, 3600);
        moreSheet.setColumnWidth(24, 3713);
        moreSheet.setColumnWidth(25, 3713);
        moreSheet.setColumnWidth(26, 3713);
        moreSheet.setColumnWidth(27, 3713);
        HSSFCellStyle moreCellStyle = moreWorkBook.createCellStyle();
        moreCellStyle.setWrapText(true);
        moreCellStyle.setAlignment(HorizontalAlignment.CENTER);
        moreCellStyle.setBorderBottom(BorderStyle.THIN);
        moreCellStyle.setBorderLeft(BorderStyle.THIN);
        moreCellStyle.setBorderRight(BorderStyle.THIN);
        moreCellStyle.setBorderTop(BorderStyle.THIN);
        List<DoubleFlight> moreDoubleFlightList = doubleFlightRepository.findByIsMore();
        String fileName = "多机长航班表" + ".xls";
        int rowNum = 1;
        String[] headers = {"序号", "起飞日期", "航班号", "航线名称", "城市三字码", "机组1", "资质1", "机组2", "资质2", "机组3", "资质3", "机组4", "资质4", "机组5", "资质5", "机组组合", "双组航线检查", "过夜航班", "阶段双组", "C检查航线", "飞行管理人员", "飞行部资质修改", "飞行部修改说明", "人力部资质修改", "人力部修改说明"};
        HSSFRow row = moreSheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(moreCellStyle);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        //在表中存放查询到的数据放入对应的列
        for (DoubleFlight doubleFlight : moreDoubleFlightList) {
            //设置居中
            HSSFRow row1 = moreSheet.createRow(rowNum);
            String[] value = {String.valueOf(rowNum), doubleFlight.getDate(), doubleFlight.getNo(), doubleFlight.getLine(), doubleFlight.getTcc(),
                    doubleFlight.getFirstPosition(), doubleFlight.getFirstQualification(),
                    doubleFlight.getSecondPosition(), doubleFlight.getSecondQualification(),
                    doubleFlight.getThirdPosition(), doubleFlight.getThirdQualification(),
                    doubleFlight.getFourthPosition(), doubleFlight.getFourthQualification(),
                    doubleFlight.getFifthPosition(), doubleFlight.getFifthQualification(),
                    doubleFlight.getFlightCombine(), doubleFlight.getDoubleLine(), doubleFlight.getNightFlight(), doubleFlight.getStageDoubleFlight(), doubleFlight.getFlightCheck(), doubleFlight.getCadre(), doubleFlight.getAirChangeRecord(),
                    doubleFlight.getAirRemark(), doubleFlight.getMpChangeRecord(), doubleFlight.getMpRemark()};
            for (int i = 0; i < headers.length; i++) {
                cellValue(moreCellStyle, row1, i, value[i]);
            }
            rowNum++;
        }
        for (int i = 0; i < headers.length; i++) {
            moreSheet.autoSizeColumn(i);
            moreSheet.setColumnWidth(i, moreSheet.getColumnWidth(i) * 12 / 10);
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
        try {
            response.flushBuffer();
            moreWorkBook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, Object> MtoJ() {
        Map<String, Object> map = new HashMap<>();
        try {
            mpRepository.setPostMChangeToJ();
            doubleFlightRepository.firstMtoJ();
            doubleFlightRepository.secondMtoJ();
            doubleFlightRepository.thirdMtoJ();
            doubleFlightRepository.fourthMtoJ();
            doubleFlightRepository.fifthMtoJ();
            List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
            for (DoubleFlight doubleFlight : doubleFlightList
            ) {
                if (doubleFlight.getIsMore() == null) {
                    if (!doubleFlight.getFlightCombine().equals(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1))) {
                        doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
                        doubleFlightRepository.save(doubleFlight);
                    }
                } else {
                    if (doubleFlight.getThirdQualification() == null) {
                        doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
                        doubleFlightRepository.save(doubleFlight);
                        continue;
                    }
                    if (doubleFlight.getFourthQualification() == null) {
                        doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1) + doubleFlight.getThirdQualification().substring(0, 1));
                        doubleFlightRepository.save(doubleFlight);
                        continue;
                    }
                    if (doubleFlight.getFifthQualification() == null) {
                        doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1) + doubleFlight.getThirdQualification().substring(0, 1)
                                + doubleFlight.getFourthQualification().substring(0, 1));
                        doubleFlightRepository.save(doubleFlight);
                        continue;
                    }
                    if (doubleFlight.getFifthQualification() != null) {
                        doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1) + doubleFlight.getThirdQualification().substring(0, 1)
                                + doubleFlight.getFourthQualification().substring(0, 1) + doubleFlight.getFifthQualification().substring(0, 1));
                    }
                }
            }

        } catch (Exception e) {
            map.put("msg", "修改失败");
            return map;
        }
        map.put("msg", "修改成功");
        return map;
    }

    @Override
    public void downloadFlMtoJ(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("task");
        //设置宽度
        for (int i = 0; i < 27; i++)
            sheet.setColumnWidth(i, 4088);
        sheet.setColumnWidth(14, 6666);
        sheet.setColumnWidth(15, 8888);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        List<Mp> mpList = mpRepository.findAll();
        String fileName = "机组信息薪酬" + ".xls";
        int rowNum = 1;
        String[] headers = {"人员编号", "姓名", "起飞日期", "起飞时刻", "航班号", "机号", "计费机型", "航班性质", "机上岗位", "是否夜航", "是否国际", "是否计费", "是否乘机", "实飞时间", "航线名称", "城市三字码", "特殊航线计发比例", "特殊航线驻外天数", "各航段实飞时间", "各航段计发比例", "各航段是否国际", "返航或备降", "备注", "计薪时间", "任职组织", "薪酬部门", "出错说明"};
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        //在表中存放查询到的数据放入对应的列
        for (Mp mp : mpList) {
            //设置居中
            HSSFRow row1 = sheet.createRow(rowNum);
            if (mp.getPost().substring(0, 1).equals("M")) {
                if (!mp.getProperty().equals("调组乘机乘车")) {
                    mp.setPost("J机长");
                }
            }
            for (DoubleFlight doubleFlight : doubleFlightList) {
                if (!doubleFlight.getDate().equals(mp.getDate()))
                    continue;
                if (!doubleFlight.getNo().equals(mp.getFlightNo()))
                    continue;
                if (doubleFlight.getFirstPosition().equals(mp.getName())) {
                    if (!doubleFlight.getFirstQualification().equals(mp.getPost())) {
                        mp.setPost(doubleFlight.getFirstQualification());
                    }
                }
                if (doubleFlight.getSecondPosition().equals(mp.getName())) {
                    if (!doubleFlight.getSecondQualification().equals(mp.getPost())) {
                        mp.setPost(doubleFlight.getSecondQualification());
                    }
                }
            }
            StringBuilder eid = new StringBuilder(String.valueOf(mp.getEid()));
            int length = eid.length();
            for (int i = 0; i < 10 - length; i++)
                eid.insert(0, "0");
            String[] value = {eid.toString(), mp.getName(), mp.getDate(), mp.getTakeOffTime(), mp.getFlightNo(), mp.getAirplaneNumber(), mp.getType(), mp.getProperty(), mp.getPost(), mp.getIsNight(),
                    mp.getIsInternational(), mp.getIsCost(), mp.getIsTake(), mp.getRealTime(), mp.getAirLine(), mp.getTcc(), mp.getEachAirlineProportion(), mp.getSpecialAirlineDays(),
                    mp.getEachAirlineTime(), mp.getEachAirlineProportion(), mp.getIsEachAirlineInternational(), mp.getReversal(), mp.getRemarks(), mp.getPayTime(), mp.getOrganization(),
                    mp.getDepartment(), mp.getErrorStatement()};
            for (int i = 0; i < headers.length; i++) {
                cellValue(cellStyle, row1, i, value[i]);
            }
            rowNum++;
        }

//        for (int i = 0; i < headers.length; i++) {
//            sheet.autoSizeColumn(i);
//            sheet.setColumnWidth(i,sheet.getColumnWidth(i)*12/10);
//        }

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
    public void downloadFl(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("task");
        //设置宽度
        for (int i = 0; i < 27; i++)
            sheet.setColumnWidth(i, 4088);
        sheet.setColumnWidth(14, 6666);
        sheet.setColumnWidth(15, 8888);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        List<Mp> mpList = mpRepository.findAll();
        String fileName = "机组信息薪酬" + ".xls";
        int rowNum = 1;
        String[] headers = {"人员编号", "姓名", "起飞日期", "起飞时刻", "航班号", "机号", "计费机型", "航班性质", "机上岗位", "是否夜航", "是否国际", "是否计费", "是否乘机", "实飞时间", "航线名称", "城市三字码", "特殊航线计发比例", "特殊航线驻外天数", "各航段实飞时间", "各航段计发比例", "各航段是否国际", "返航或备降", "备注", "计薪时间", "任职组织", "薪酬部门", "出错说明"};
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        //在表中存放查询到的数据放入对应的列
        for (Mp mp : mpList) {
            //设置居中
            HSSFRow row1 = sheet.createRow(rowNum);
            for (DoubleFlight doubleFlight : doubleFlightList) {
                if (!doubleFlight.getDate().equals(mp.getDate()))
                    continue;
                if (!doubleFlight.getNo().equals(mp.getFlightNo()))
                    continue;
                if (doubleFlight.getFirstPosition().equals(mp.getName())) {
                    if (!doubleFlight.getFirstQualification().equals(mp.getPost())) {
                        mp.setPost(doubleFlight.getFirstQualification());
                    }
                }
                if (doubleFlight.getSecondPosition().equals(mp.getName())) {
                    if (!doubleFlight.getSecondQualification().equals(mp.getPost())) {
                        mp.setPost(doubleFlight.getSecondQualification());
                    }
                }
            }
            StringBuilder eid = new StringBuilder(String.valueOf(mp.getEid()));
            int length = eid.length();
            for (int i = 0; i < 10 - length; i++)
                eid.insert(0, "0");
            String[] value = {eid.toString(), mp.getName(), mp.getDate(), mp.getTakeOffTime(), mp.getFlightNo(), mp.getAirplaneNumber(), mp.getType(), mp.getProperty(), mp.getPost(), mp.getIsNight(),
                    mp.getIsInternational(), mp.getIsCost(), mp.getIsTake(), mp.getRealTime(), mp.getAirLine(), mp.getTcc(), mp.getEachAirlineProportion(), mp.getSpecialAirlineDays(),
                    mp.getEachAirlineTime(), mp.getEachAirlineProportion(), mp.getIsEachAirlineInternational(), mp.getReversal(), mp.getRemarks(), mp.getPayTime(), mp.getOrganization(),
                    mp.getDepartment(), mp.getErrorStatement()};
            for (int i = 0; i < headers.length; i++) {
                cellValue(cellStyle, row1, i, value[i]);
            }
            rowNum++;
        }
//        for (int i = 0; i < headers.length; i++) {
//            sheet.autoSizeColumn(i);
//            sheet.setColumnWidth(i,sheet.getColumnWidth(i)*12/10);
//        }
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
    public String deleteProperty(@PathVariable String property) {
        doubleFlightRepository.deleteProperty(property);
        return "redirect:/doubleFlight/property";
    }

    private void cellValue(HSSFCellStyle cellStyle, HSSFRow row, int i, String value) {
        Cell cell = row.createCell(i);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
    }

}
