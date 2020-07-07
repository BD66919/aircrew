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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @Autowired
    DoubleFlightServiceImpl(MpRepository mpRepository, AirRepository airRepository, CadreRepository cadreRepository, DoubleFlightRepository doubleFlightRepository, StageDoubleFlightRepository stageDoubleFlightRepository, AirlineRepository airlineRepository, NightFlightRepository nightFlightRepository){
        this.mpRepository = mpRepository;
        this.airRepository = airRepository;
        this.cadreRepository = cadreRepository;
        this.doubleFlightRepository = doubleFlightRepository;
        this.stageDoubleFlightRepository = stageDoubleFlightRepository;
        this.airlineRepository = airlineRepository;
        this.nightFlightRepository = nightFlightRepository;
    }

    @Override
    public String flight(Model model, HttpServletRequest request) {
        String department = (String) request.getSession().getAttribute("department");
        model.addAttribute("department", department);
        if (doubleFlightRepository.findAll().isEmpty())
            model.addAttribute("doubleFlight", "待生成");
        else
            model.addAttribute("doubleFlight", "双机长航班表");
        DoubleFlight doubleFlight = doubleFlightRepository.getLastDoubleFlight();
        if (doubleFlightRepository.findAll().size() != 0) {
            model.addAttribute("doubleFlightState", doubleFlight.getDate() + " " + doubleFlight.getNo() + " " + doubleFlight.getLine());
            model.addAttribute("doubleFlightCount", String.valueOf(doubleFlightRepository.count()));
        } else {
            model.addAttribute("doubleFlightState", "无");
            model.addAttribute("doubleFlightCount", "0");
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
        return "/doubleFlight/flight.html";
    }

    @Override
    public Map<String, Object> calculateFlight(HttpServletRequest request) {
        doubleFlightRepository.truncateDoubleFlight();
        doubleFlightRepository.cancelMpConfirm();
        doubleFlightRepository.cancelFlConfirm();
        doubleFlightRepository.cancelAirConfirm();
        String doubleFlightStatus = request.getParameter("doubleFlight");
        String cadreStatus = request.getParameter("cadre");
        String nightFlightStatus = request.getParameter("nightFlight");
        String stageDoubleFlightStatus = request.getParameter("stageDoubleFlight");
        Map<String, Object> map = new HashMap<>();
        List<Mp> mpList = mpRepository.findAll();
        if (mpList.isEmpty()) {
            map.put("msg", "系统数据库数据为空，无法生成");
            return map;
        }
        List<DoubleFlight> doubleFlightList = new ArrayList<>();
        List<Airline> airlines = airlineRepository.findAll();
        List<Cadre> cadreList = cadreRepository.findAll();
        List<NightFlight> nightFlightList = nightFlightRepository.findAll();
        List<StageDoubleFlight> stageDoubleFlightList = stageDoubleFlightRepository.findAll();
        Collections.sort(mpList, new Comparator<Mp>() {
            @Override
            public int compare(Mp p1, Mp p2) {
                return p1.getDate().compareTo(p2.getDate());
            }
        });
        String date;
        String FlightNo;
        String No;
        String cadreDate = "";
        String cadreNo = "";
        String cadre = "";
        int eid;
        int id = 0;
        String judgeFlightNo = "";
        DoubleFlight doubleFlight = new DoubleFlight();
        int number1 = 0;
        int number2 = 0;
        for (Mp pilot : mpList) {
            if (pilot.getPost().substring(0, 1).equals("F") || pilot.getPost().substring(0, 1).equals("G")) {
                number1 = number2;
                continue;
            }
            if (pilot.getProperty().equals("调组乘机乘车") || pilot.getProperty().equals("本场训练")) {
                number1 = 0;
                continue;
            }
            date = pilot.getDate();
            FlightNo = pilot.getFlightNo();
            if (!judgeFlightNo.equals(FlightNo))
                number1 = 0;
            judgeFlightNo = FlightNo;
            eid = pilot.getEid();
            No = FlightNo.split("/")[0];
            for (Cadre cadre1 : cadreList) {
                if (eid == cadre1.getEid()) {
                    cadre = cadre1.getCategory();
                    cadreDate = date;
                    cadreNo = FlightNo;
                }
            }
            doubleFlight.setDate(date);
            doubleFlight.setNo(FlightNo);
            doubleFlight.setLine(pilot.getAirLine());
            doubleFlight.setTcc(pilot.getTcc());
            try {
                List<Integer> numberList = airRepository.number(date, No, eid);
                number2 = numberList.get(0);
            } catch (Exception e) {
                continue;
            }
            if (number1 != 0 && number2 != 0 && number1 > number2) {
                doubleFlight.setSecondPosition(doubleFlight.getFirstPosition());
                doubleFlight.setSecondQualification(doubleFlight.getFirstQualification());
                doubleFlight.setFirstPosition(pilot.getName());
                doubleFlight.setFirstQualification(pilot.getPost());
            } else if (number1 != 0 && number2 != 0 && number1 < number2) {
                doubleFlight.setSecondPosition(pilot.getName());
                doubleFlight.setSecondQualification(pilot.getPost());
            } else {
                doubleFlight.setFirstPosition(pilot.getName());
                doubleFlight.setFirstQualification(pilot.getPost());
            }
            number1 = number2;
            if (doubleFlight.getFirstPosition() != null && doubleFlight.getSecondPosition() != null) {
                doubleFlight.setId(id);
                id++;
                DoubleFlight doubleFlight1 = new DoubleFlight();
                doubleFlight1.setDoubleLine("");
                doubleFlight1.setNightFlight("");
                doubleFlight1.setStageDoubleFlight("");
                doubleFlight1.setFlightCheck("");
                doubleFlight1.setCadre("");
                doubleFlight1.setAirChangeRecord("");
                doubleFlight1.setMpChangeRecord("");
                doubleFlight1.setAirRemark("");
                doubleFlight1.setMpRemark("");
                doubleFlight1.setId(id);
                doubleFlight1.setDate(doubleFlight.getDate());
                doubleFlight1.setNo(doubleFlight.getNo());
                doubleFlight1.setLine(doubleFlight.getLine());
                doubleFlight1.setTcc(doubleFlight.getTcc());
                doubleFlight1.setFirstPosition(doubleFlight.getFirstPosition());
                doubleFlight1.setFirstQualification(doubleFlight.getFirstQualification());
                doubleFlight1.setSecondPosition(doubleFlight.getSecondPosition());
                doubleFlight1.setSecondQualification(doubleFlight.getSecondQualification());
                String combine = doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1);
                doubleFlight1.setFlightCombine(combine);


                if (cadreStatus != null)
                    if (cadreDate.equals(doubleFlight.getDate()) && cadreNo.equals(doubleFlight.getNo())) {
                        doubleFlight1.setCadre(cadre);
                    }

                if (doubleFlightStatus != null)
                    for (Airline airline : airlines) {
                        if (doubleFlight1.getTcc().contains(airline.getAirline())) {
                            doubleFlight1.setDoubleLine("双组航线");
                        }
                    }

                if (nightFlightStatus != null)
                    for (NightFlight nightFlight : nightFlightList) {
                        if (doubleFlight1.getNo().contains(nightFlight.getNumber())) {
                            doubleFlight1.setNightFlight("过夜航班");
                        }
                    }

                if (stageDoubleFlightStatus != null)
                    for (StageDoubleFlight stageDoubleFlight : stageDoubleFlightList) {
                        if (doubleFlight1.getNo().contains(stageDoubleFlight.getNumber())) {
                            doubleFlight1.setStageDoubleFlight("阶段双组航班");
                        }
                    }
                if (cadreStatus != null)
                    if (doubleFlight1.getFirstQualification().equals("C飞行检查员") || doubleFlight1.getSecondQualification().equals("C飞行检查员")) {
                        if (doubleFlight1.getCadre() == null) {
                            doubleFlight1.setFlightCheck("C检查航线");
                        }
                    }
                doubleFlightList.add(doubleFlight1);
                cadre = "";
                doubleFlight.setFirstPosition(null);
                doubleFlight.setFirstQualification(null);
                doubleFlight.setSecondPosition(null);
                doubleFlight.setSecondQualification(null);
                number1 = 0;
                number2 = 0;
            }
        }
        doubleFlightRepository.saveAll(doubleFlightList);

        map.put("msg", "双机长航班表已生成");
        return map;
    }

    @Override
    public String doubleFlight(Model model) {
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        model.addAttribute("doubleFlightList", doubleFlightList);
        model.addAttribute("count", doubleFlightList.size());
        return "/doubleFlight/doubleFlight.html";
    }

    @Override
    public String filterDoubleFlight(Model model) {
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        for (int i = 0; i < doubleFlightList.size(); i++) {
            DoubleFlight doubleFlight1 = doubleFlightList.get(i);
            if (!doubleFlight1.getDoubleLine().isEmpty() || !doubleFlight1.getNightFlight().isEmpty() || !doubleFlight1.getStageDoubleFlight().isEmpty() || !doubleFlight1.getFlightCheck().isEmpty() || !doubleFlight1.getCadre().isEmpty()) {
                doubleFlightList.remove(doubleFlight1);
                i--;
            }
        }
        model.addAttribute("doubleFlightList", doubleFlightList);
        model.addAttribute("count", doubleFlightList.size());
        return "/doubleFlight/filterDoubleFlight.html";
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
                if (!doubleFlight.getFirstQualification().equals(request.getParameter("firstQualification")))
                    recorder = recorder.concat("资质1：" + doubleFlight.getFirstQualification() + "修改为" + request.getParameter("firstQualification")) + ";";
                if (!doubleFlight.getSecondQualification().equals(request.getParameter("secondQualification")))
                    recorder = recorder.concat("资质2：" + doubleFlight.getSecondQualification() + "修改为" + request.getParameter("secondQualification")) + ";";
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
                if (!request.getParameter("remarks").equals(""))
                    doubleFlight.setAirRemark(doubleFlight.getAirRemark() + request.getParameter("remarks") + ";");
                doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
                doubleFlight.setAirChangeRecord(doubleFlight.getAirChangeRecord() + " " + recorder);
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
                    if (!request.getParameter("remarks").equals(""))
                        doubleFlight.setMpRemark(doubleFlight.getMpRemark() + request.getParameter("remarks") + ";");
                    doubleFlight.setFlightCombine(doubleFlight.getFirstQualification().substring(0, 1) + doubleFlight.getSecondQualification().substring(0, 1));
                    doubleFlight.setMpChangeRecord(doubleFlight.getMpChangeRecord() + " " + recorder);
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
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        List<DoubleFlight> doubleFlightList = doubleFlightRepository.findAll();
        try {
            for (int i = 0; i < doubleFlightList.size(); i++) {
                DoubleFlight doubleFlight1 = doubleFlightList.get(i);
                if (!doubleFlight1.getDoubleLine().isEmpty() || !doubleFlight1.getNightFlight().isEmpty() || !doubleFlight1.getStageDoubleFlight().isEmpty() || !doubleFlight1.getFlightCheck().isEmpty() || !doubleFlight1.getCadre().isEmpty()) {
                    doubleFlightList.remove(doubleFlight1);
                    i--;
                }
            }
        } catch (NullPointerException ignored) {
        } finally {
            String fileName = "双组航班表" + ".xls";
            int rowNum = 1;
            String[] headers = {"序号", "起飞日期", "航班号", "航线名称", "城市三字码", "机组1", "资质1", "机组2", "资质2", "机组组合", "双组航线检查", "过夜航班", "阶段双组", "C检查航线", "飞行管理人员", "飞行部资质修改", "飞行部修改说明", "人力资质修改", "人力修改说明"};
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
                String[] value = {String.valueOf(rowNum),doubleFlight.getDate(),doubleFlight.getNo(),doubleFlight.getLine(),doubleFlight.getTcc(),doubleFlight.getFirstPosition(),doubleFlight.getFirstQualification(),doubleFlight.getSecondPosition(),
                        doubleFlight.getSecondQualification(),doubleFlight.getFlightCombine(),doubleFlight.getDoubleLine(),doubleFlight.getNightFlight(),doubleFlight.getStageDoubleFlight(),doubleFlight.getFlightCheck(),doubleFlight.getCadre(),doubleFlight.getAirChangeRecord(),
                        doubleFlight.getAirRemark(),doubleFlight.getMpChangeRecord(),doubleFlight.getMpRemark()};
                for(int i=0;i<headers.length;i++){
                    cellValue(cellStyle, row1, i, value[i]);
                }
                rowNum++;
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
    public void downloadFlMtoJ(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("task");
        //设置宽度
        for (int i = 0; i < 27; i++)
            sheet.setColumnWidth(i, 4088);
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
                    System.out.println(mp.getPost());
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
            for(int i =0;i<10-length;i++)
                eid.insert(0, "0");
            String[] value = {eid.toString(),mp.getName(),mp.getDate(),mp.getTakeOffTime(),mp.getFlightNo(),mp.getAirplaneNumber(),mp.getType(),mp.getProperty(),mp.getPost(),mp.getIsNight(),
                    mp.getIsInternational(),mp.getIsCost(),mp.getIsTake(),mp.getRealTime(),mp.getAirLine(),mp.getTcc(),mp.getEachAirlineProportion(),mp.getSpecialAirlineDays(),
                    mp.getEachAirlineTime(),mp.getEachAirlineProportion(),mp.getIsEachAirlineInternational(),mp.getReversal(),mp.getRemarks(),mp.getPayTime(),mp.getOrganization(),
                    mp.getDepartment(),mp.getErrorStatement()};
            for(int i=0;i<headers.length;i++){
                cellValue(cellStyle, row1, i, value[i]);
            }
            rowNum++;
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
    public void downloadFl(HttpServletResponse response) throws IOException{
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("task");
        //设置宽度
        for (int i = 0; i < 27; i++)
            sheet.setColumnWidth(i, 4088);
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
                        System.out.println(mp.getPost());
                    }
                }
                if (doubleFlight.getSecondPosition().equals(mp.getName())) {
                    if (!doubleFlight.getSecondQualification().equals(mp.getPost())) {
                        mp.setPost(doubleFlight.getSecondQualification());
                        System.out.println(mp.getPost());
                    }
                }
            }
            StringBuilder eid = new StringBuilder(String.valueOf(mp.getEid()));
            int length = eid.length();
            for(int i =0;i<10-length;i++)
                eid.insert(0, "0");
            String[] value = {eid.toString(),mp.getName(),mp.getDate(),mp.getTakeOffTime(),mp.getFlightNo(),mp.getAirplaneNumber(),mp.getType(),mp.getProperty(),mp.getPost(),mp.getIsNight(),
                                mp.getIsInternational(),mp.getIsCost(),mp.getIsTake(),mp.getRealTime(),mp.getAirLine(),mp.getTcc(),mp.getEachAirlineProportion(),mp.getSpecialAirlineDays(),
                                mp.getEachAirlineTime(),mp.getEachAirlineProportion(),mp.getIsEachAirlineInternational(),mp.getReversal(),mp.getRemarks(),mp.getPayTime(),mp.getOrganization(),
                                mp.getDepartment(),mp.getErrorStatement()};
            for(int i=0;i<headers.length;i++){
                cellValue(cellStyle, row1, i, value[i]);
            }
            rowNum++;
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

    private void cellValue(HSSFCellStyle cellStyle,HSSFRow row, int i ,String value) {
        Cell cell = row.createCell(i);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
    }

}
