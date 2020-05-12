package aircrew.version1.utils;

import aircrew.version1.entity.Mp;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MpExcelUtils {
    public static List<Mp> addMp(InputStream inputStream) {
        List<Mp> Excellist = new ArrayList<>();
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

            int[] subArr = new int[9];

            Cell cell1 = row.getCell(0);
            for (int i = 0; i < colLength ;i++){
                cell1 = row.getCell(i);
                if(cell1 != null){
                    cell1.setCellType(CellType.STRING);
                    String data = cell1.getStringCellValue();
                    //data = data.trim();
                    switch (data) {
                        //列： 0姓名   1工号   2日期   3时间   4航班号   5性质   6岗位    7航线   8三字码
                        case "姓名":
                            subArr[0] = i;
                            break;
                        case "人员编号":
                            subArr[1] = i;
                            break;
                        case "起飞日期":
                            subArr[2] = i;
                            break;
                        case "起飞时刻":
                            subArr[3] = i;
                            break;
                        case "航班号":
                            subArr[4] = i;
                            break;
                        case "航班性质":
                            subArr[5] = i;
                            break;
                        case "机上岗位":
                            subArr[6] = i;
                            break;
                        case "航线名称":
                            subArr[7] = i;
                            break;
                        case "城市三字码":
                            subArr[8] = i;
                            break;
                    }
                }
            }

            //得到指定的单元格
            Cell cell = row.getCell(0);
            for (int i = 1; i <= rowLength; i++) {
                Mp mpExcel = new Mp();
                row = sheet.getRow(i);
                for (int j = 0; j < 9; j++) {
                    //列： 0姓名   1工号   2日期   3时间   4航班号   5性质   6岗位    7航线   8三字码
                    cell = row.getCell(subArr[j]);
                    if (cell != null) {
                        cell.setCellType(CellType.STRING);
                        String data = cell.getStringCellValue();
                        data = data.trim();
                        if (j == 0) {
                            mpExcel.setName(data);
                        }else if (j == 1) {
                            if(data.equals(""))
                                data = "00000";
                            mpExcel.setEid(Integer.parseInt(data));
                        }else if (j == 2) {
                            mpExcel.setDate(data);
                        }else if (j == 3) {
                            mpExcel.setTime(data);
                        }else if (j == 4) {
                            mpExcel.setFlightNo(data);
                        }else if (j == 5) {
                            mpExcel.setProperties(data);
                        }else if (j == 6) {
                            mpExcel.setPost(data);
                        }else if (j == 7) {
                            mpExcel.setLine(data);
                        }else {
                            mpExcel.setTcc( data);
                        }
                    }
                }
                Excellist.add(mpExcel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Excellist;
    }
}
