package aircrew.version1.utils;

import aircrew.version1.entity.Air;
import aircrew.version1.entity.Excel;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jiangzhendong
 * @Description 该类用于读取excel文件中的数据
 *
 */
public class AirExcelUtils {
    public static List<Air> addAir(InputStream inputStream) {
        List<Air> Excellist = new ArrayList<>();
        Workbook workbook = null;
        Date datetime = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            inputStream.close();
            //工作表对象
            Sheet sheet = workbook.getSheetAt(0);

            //总行数
            int rowLength = sheet.getLastRowNum();

            //工作表的列
            Row row = sheet.getRow(0);

            //总列数
            int colLength = row.getLastCellNum();

            int[] subArr = new int[8];

            Cell cell1 = row.getCell(0);
            for (int i = 0; i < colLength ;i++){
                cell1 = row.getCell(i);
                if(cell1 != null){
                    cell1.setCellType(CellType.STRING);
                    String data = cell1.getStringCellValue();
                    data = data.trim();
                    switch (data) {
                        case "名字":
                            subArr[0] = i;
                            break;
                        case "工号":
                            subArr[1] = i;
                            break;
                        case "离港":
                            subArr[2] = i;
                            break;
                        case "到港":
                            subArr[3] = i;
                            break;
                        case "性质":
                            subArr[4] = i;
                            break;
                        case "职务":
                            subArr[5] = i;
                            break;
                        case "起飞":
                            subArr[6] = i;
                            break;
                        case "落地":
                            subArr[7] = i;
                            break;
                    }
                }
            }

            //得到指定的单元格
            Cell cell = row.getCell(0);
            for (int i = 1; i <= rowLength; i++) {
                Air SocExcel = new Air();
                row = sheet.getRow(i);
                for (int j = 0; j < 8; j++) {
                    //列： 0姓名   1工号   2离港   3到港   4性质   5职务   6起飞    7落地
                    cell = row.getCell(subArr[j]);
                    if (cell != null) {
                        cell.setCellType(CellType.STRING);
                        String data = cell.getStringCellValue();
                        data = data.trim();
                        if (j == 0) {
                            SocExcel.setName(data);
                        }else if (j == 1) {
                            if(data.equals(""))
                                data = "00000";
                            SocExcel.setEid(Integer.parseInt(data));
                        }else if (j == 2) {
                            SocExcel.setDep(data);
                        }else if (j == 3) {
                            SocExcel.setArr(data);
                        }else if (j == 4) {
                            SocExcel.setProperties(data);
                        }else if (j == 5) {
                            SocExcel.setPost(data);
                        }else if (j == 6) {
                            SocExcel.setStartTime(data);
                        }else {
                            SocExcel.setEndTime(data);
                        }
                    }
                }
                Excellist.add(SocExcel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Excellist;
    }
}