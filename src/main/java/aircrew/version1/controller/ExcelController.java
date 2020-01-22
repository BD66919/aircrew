package aircrew.version1.controller;


import aircrew.version1.entity.Excel;
import aircrew.version1.entity.Pilot;
import aircrew.version1.mapper.PilotMapper;
import aircrew.version1.utils.ExcelUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jiangzhendong
 * @param /coverUploadExcel 覆盖性上传excel文件
 * @param loginPwd 网页页面提交的密码
 * @param userName 数据库中的用户名
 * @param userPwd 数据库中的用户密码
 * @Description 该类用于excel文件的上传和下载
 *
 */

@Controller
public class ExcelController {
    @Autowired
    PilotMapper pilotMapper;

    //覆盖性上传EXCEL数据
    @PostMapping("/coverUploadExcel")
    public String coverUploadExcel(@RequestParam("file") MultipartFile file,
                              Map<String, Object> map) throws IOException, InvalidFormatException {
        String fileName = file.getOriginalFilename();
        if (fileName.length() < 5 ) {
            map.put("message","文件格式错误");
            return "/pilot/coverupload.html";
        } else if(!(fileName.substring(fileName.length() - 5).equals(".xlsx")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))){
            map.put("message","文件类型错误");
            return "/pilot/coverupload.html";
        }
        List<Excel> excelList = null;
        try {
            excelList = ExcelUtils.excelToShopIdList(file.getInputStream());
            if (excelList == null || excelList.size() <= 0) {
                map.put("message","导入的数据为空或者不符合要求");
                return "/pilot/coverupload.html";
            }
            //excel的数据保存到数据库
            try {
                pilotMapper.delete();
                pilotMapper.listInsertExcel(null, excelList,0);
            } catch (Exception e) {
                map.put("message","导入的数据格式有误或者与当前已存在的数据中存在重复");
                return "/pilot/coverupload.html";
            }
        } catch (Exception e) {
            map.put("message","导入异常");
            return "/pilot/coverupload.html";
        }
        return "redirect:/pilot";
    }

    //追加性上传EXCEL数据
    @PostMapping("/addUploadExcel")
    public String addUploadExcel(@RequestParam("file") MultipartFile file,
                              Map<String, Object> map) throws IOException, InvalidFormatException {
        String fileName = file.getOriginalFilename();
        if (fileName.length() < 5 ) {
            map.put("message","文件格式错误");
            return "/pilot/addupload.html";
        } else if(!(fileName.substring(fileName.length() - 5).equals(".xlsx")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))){
            map.put("message","文件类型错误");
            return "/pilot/addupload.html";
        }
        List<Excel> excelList = null;
        try {
            excelList = ExcelUtils.excelToShopIdList(file.getInputStream());
            if (excelList == null || excelList.size() <= 0) {
                map.put("message","导入的数据为空或者不符合要求");
                return "/pilot/addupload.html";
            }
            //excel的数据保存到数据库
            try {
                Integer lastId = pilotMapper.getLastId();
                for (Excel pilot : excelList) {
                    lastId++;
                    pilotMapper.insertByExcel(lastId,pilot,0);
                }
            } catch (Exception e) {
                map.put("message","导入的数据格式有误或者与当前已存在的数据中存在重复");
                return "/pilot/addupload.html";
            }
        } catch (Exception e) {
            map.put("message","导入异常");
            return "/pilot/addupload.html";
        }
        return "redirect:/pilot";
    }

    //下载所有数据保存到excel中
    @RequestMapping(value = "excelDownloads", method = RequestMethod.GET)
    public void excelDownloads(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");
        List<Pilot> pilotList =pilotMapper.list();
        String datetime=null;
        try {
            datetime = pilotMapper.getById(1).getDate().substring(0, 7);
        }catch (NullPointerException ignored){
        }finally {
            String fileName = datetime + "飞行员数据.xlsx";//设置要导出的文件的名字
            //新增数据行，并且设置单元格数据
            int rowNum = 1;
            String[] headers = {"姓名", "员工号", "航线名称","城市三字码","任务性质","时间"};
            //headers表示excel表中第一行的表头
            HSSFRow row = sheet.createRow(0);
            //在excel表中添加表头
            for(int i=0;i<headers.length;i++){
                HSSFCell cell = row.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }
            //在表中存放查询到的数据放入对应的列
            for (Pilot pilot : pilotList) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.createCell(0).setCellValue(pilot.getName());
                row1.createCell(1).setCellValue(pilot.getEid());
                row1.createCell(2).setCellValue(pilot.getLine());
                row1.createCell(3).setCellValue(pilot.getTcc());
                row1.createCell(4).setCellValue(pilot.getProperties());
                row1.createCell(5).setCellValue(pilot.getDate());
                rowNum++;
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" +  java.net.URLEncoder.encode(fileName, "UTF-8"));
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        }
    }

    //下载所有无法闭环人员数据到excel中
    @RequestMapping(value = "errorExcelDownloads", method = RequestMethod.GET)
    public String errorExcelDownloads(HttpServletResponse response) throws IOException {
        String date = null;
        List<Pilot> pilotListByOrder = pilotMapper.ByOrder();
        List<Pilot> errorPilotList = new ArrayList<>();
        if (pilotListByOrder.isEmpty()) {
            return "redirect:/pilot/pilotcheck/check";
        }
        int eid = pilotListByOrder.get(0).getEid();
        int length = pilotListByOrder.get(0).getTcc().length();
        String endTcc = pilotListByOrder.get(0).getTcc();
        endTcc = endTcc.substring(length - 3, length);
        for (Pilot pilot : pilotListByOrder) {
            length = pilot.getTcc().length();
            //长度为零时进入下一次判断
            if (length == 0)
                continue;
            if (pilot.getError() == 1) {
                endTcc = pilot.getTcc().substring(length - 3, length);
                continue;
            }
            if (eid != pilot.getEid()) {
                eid = pilot.getEid();
                endTcc = pilot.getTcc().substring(length - 3, length);
                continue;
            }
            String Tcc, startTcc;
            Tcc = pilot.getTcc();
            startTcc = Tcc.substring(0, 3);
            if (!endTcc.equals(startTcc)) {
                errorPilotList.add(pilot);
            }
            length = pilot.getTcc().length();
            endTcc = Tcc.substring(length - 3, length);
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");
        List<Pilot> pilotList = errorPilotList;
        try {
            date = pilotMapper.getById(1).getDate().substring(0, 7);
        } catch (NullPointerException ignored) {
        } finally {
            String fileName = date + "无法闭环飞行员" + ".xlsx";
            int rowNum = 1;
            String[] headers = {"姓名", "员工号", "航线名称", "城市三字码", "任务性质", "时间"};
            HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                HSSFCell cell = row.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }
            //在表中存放查询到的数据放入对应的列
            for (Pilot pilot : pilotList) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.createCell(0).setCellValue(pilot.getName());
                row1.createCell(1).setCellValue(pilot.getEid());
                row1.createCell(2).setCellValue(pilot.getLine());
                row1.createCell(3).setCellValue(pilot.getTcc());
                row1.createCell(4).setCellValue(pilot.getProperties());
                row1.createCell(5).setCellValue(pilot.getDate());
                rowNum++;
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        }
        return null;
    }
}