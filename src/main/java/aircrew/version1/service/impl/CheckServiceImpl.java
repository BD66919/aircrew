package aircrew.version1.service.impl;

import aircrew.version1.entity.*;
import aircrew.version1.mapper.*;
import aircrew.version1.service.CheckService;
import aircrew.version1.utils.*;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
public class CheckServiceImpl implements CheckService {

    private final AirRepository airRepository;
    private final MpRepository mpRepository;
    private final CadreRepository cadreRepository;

    @Autowired
    CheckServiceImpl(MpRepository mpRepository, AirRepository airRepository, CadreRepository cadreRepository) {
        this.mpRepository = mpRepository;
        this.airRepository = airRepository;
        this.cadreRepository = cadreRepository;
    }

    @Override
    public String check(Model model) {

        List<Mp> lastMpList = mpRepository.LastByOrder();
        List<Mp> nextMpList = mpRepository.NextByOrder();
        if (lastMpList.isEmpty())
            model.addAttribute("msg", "上月数据为空");
        if (nextMpList.isEmpty())
            model.addAttribute("msg2", " 下月数据为空");
        List<Mp> errorPilotList = getCheckResult();
        PageInfo<Mp> errorPilotInfo = new PageInfo<Mp>(errorPilotList);
        model.addAttribute("errorPilotInfo", errorPilotInfo);
        return "check/check.html";
    }

    @Override
    public void excelDownload(HttpServletResponse response) throws IOException{
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("task");
        //设置宽度
        for (int i = 0; i < 6; i++)
            sheet.setColumnWidth(i, 4088);
        sheet.setColumnWidth(2,6000);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        List<Mp> errorPilotList = getCheckResult();
        String fileName = "飞行闭环检查" + ".xls";
        int rowNum = 1;
        String[] headers = {"日期", "人员编号", "姓名", "城市三字码", "航班号", "任务性质"};
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        //在表中存放查询到的数据放入对应的列
        for (Mp mp : errorPilotList) {
            //设置居中
            HSSFRow row1 = sheet.createRow(rowNum);
            String[] value = {mp.getDate(), String.valueOf(mp.getEid()),mp.getName(),mp.getTcc(),mp.getFlightNo(),mp.getProperty()};
            for (int i = 0; i < headers.length; i++) {
                cellValue(cellStyle, row1, i, value[i]);
            }
            rowNum++;
        }
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i,sheet.getColumnWidth(i)*12/10);
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

    private List<Mp> getCheckResult(){
        List<Mp> pilotListByOrder = mpRepository.findByProperty();
        List<Air> airList = airRepository.findAllBySort();
        for (Air air : airList
        ) {
            Mp mp = new Mp();
            mp.setDate(air.getDate());
            mp.setTakeOffTime(air.getTakeOffTime());
            mp.setTcc(air.getDep() + "-" + air.getArr());
            mp.setEid(air.getEid());
            mp.setName(air.getName());
            mp.setFlightNo(air.getFlightNo());
            mp.setProperty(air.getProperty());
            pilotListByOrder.add(mp);

        }

        List<Mp> lastMpList = mpRepository.LastByOrder();
        List<Mp> nextMpList = mpRepository.NextByOrder();
        List<Mp> lastOneMpList = new ArrayList<>();
        List<Mp> nextOneMpList = new ArrayList<>();
        for (int i = 0; i < lastMpList.size() - 1; i++) {
            Mp pilot1 = lastMpList.get(i);
            Mp pilot2 = lastMpList.get(i + 1);
            if (pilot1.getEid() != pilot2.getEid()) {
                lastOneMpList.add(pilot1);
                continue;
            }
            if (i == lastMpList.size() - 2)
                lastOneMpList.add(pilot2);
        }
        for (int i = 0; i < nextMpList.size() - 1; i++) {
            Mp pilot1 = nextMpList.get(i);
            Mp pilot2 = nextMpList.get(i + 1);
            if (i == 0)
                nextOneMpList.add(pilot1);
            if (pilot1.getEid() != pilot2.getEid()) {
                nextOneMpList.add(pilot2);
            }
        }
        pilotListByOrder.addAll(lastOneMpList);
        pilotListByOrder.addAll(nextOneMpList);
        String[] sortNameArr = new String[]{"eid", "date", "takeOffTime"};
        boolean[] isAscArr = {true, true, true};
        ListUtils.sort(pilotListByOrder, sortNameArr, isAscArr);
        List<Mp> errorPilotList = new ArrayList<>();
        int length1;
        int length2;
        String startTcc;
        String endTcc;
        List<Cadre> cadreList = cadreRepository.findAll();
        List<Integer> cadreNumberList = new ArrayList<>();
        for (Cadre cadre : cadreList) {
            cadreNumberList.add(cadre.getEid());
        }
        for (int i = 0; i < pilotListByOrder.size() - 1; i++) {
            Mp pilot1 = pilotListByOrder.get(i);
            Mp pilot2 = pilotListByOrder.get(i + 1);
            if(pilot1.getProperty().equals("试飞") || pilot2.getProperty().equals("训练")){
                continue;
            }
            if (pilot1.getTcc() == null || pilot2.getTcc() == null) {//长度为零时进入下一次判断，防止被调机、正班以外的情况干扰
                continue;
            }
            length1 = pilot1.getTcc().length();
            length2 = pilot2.getTcc().length();
            if (length1 == 0 || length2 == 0) {//长度为零时进入下一次判断，防止被调机、正班以外的情况干扰
                continue;
            }
            if (pilot2.getEid() != pilot1.getEid()) {
                continue;
            }
            endTcc = pilot1.getTcc().substring(length1 - 3, length1);
            startTcc = pilot2.getTcc().substring(0, 3);
            String cadre1 = "";
            String cadre2 = "";
            if (!endTcc.equals(startTcc)) {
                if (cadreNumberList.contains(pilot1.getEid()))
                    cadre1 = "备注：飞行管理干部";
                if (cadreNumberList.contains(pilot2.getEid()))
                    cadre2 = "备注：飞行管理干部";
                Mp mpCheck1 = new Mp(pilot1.getDate() + " " + pilot1.getTakeOffTime().substring(0,5), pilot1.getEid(), pilot1.getName(), pilot1.getTcc(), pilot1.getFlightNo(), pilot1.getProperty() + " " + cadre1);
                errorPilotList.add(mpCheck1);
                Mp mpCheck2 = new Mp(pilot2.getDate() + " " + pilot2.getTakeOffTime().substring(0,5), pilot2.getEid(), pilot2.getName(), pilot2.getTcc(), pilot2.getFlightNo(), pilot2.getProperty() + " " + cadre2);
                errorPilotList.add(mpCheck2);
            }
        }
        for (int i = 1; i < errorPilotList.size() - 1; i++) {
            if (errorPilotList.get(i).equals(errorPilotList.get(i + 1))) {
                errorPilotList.remove(i);
                i = i - 1;
            }
        }
        return errorPilotList;
    }

    private void cellValue(HSSFCellStyle cellStyle, HSSFRow row, int i, String value) {
        Cell cell = row.createCell(i);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
    }
}
