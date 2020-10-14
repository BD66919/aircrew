package aircrew.version1.utils;

import aircrew.version1.entity.Fl;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FlExcelUtils {
    public static List<Fl> getFlExcel(InputStream inputStream) {
        List<Fl> flExcelList = new ArrayList<>();
        Workbook workbook;
        try{
            workbook = WorkbookFactory.create(inputStream);
            inputStream.close();
            //工作表对象
            Sheet sheet = workbook.getSheetAt(0);
            //总行数
            int rowLength = sheet.getLastRowNum();
            //工作表的列
            Row row = sheet.getRow(1);
            //总列数
            int colLength = row.getLastCellNum();
            int[] subArr = new int[12];
            Cell cellSub;
            for (int i = 0 ;i < colLength ;i++){
                cellSub = row.getCell(i);
                if(cellSub != null){
                    if(cellSub.getCellTypeEnum()!=CellType.STRING)
                        cellSub.setCellType(CellType.STRING);
                    String data = cellSub.getStringCellValue().trim();
                    if(data.contains("\n"))
                        data = data.replaceAll("\n","");
                    switch (data) {
                        case "起飞日期":
                            subArr[0] = i;
                            break;
                        case "航班号":
                            subArr[1] = i;
                            break;
                        case "航线":
                            subArr[2] = i;
                            break;
                        case "起飞地":
                            subArr[3] = i;
                            break;
                        case "降落地":
                            subArr[4] = i;
                            break;
                        case "机型":
                            subArr[5] = i;
                            break;
                        case "机号":
                            subArr[6] = i;
                            break;
                        case "时刻类型":
                            subArr[7] = i;
                            break;
                        case "滑出时刻":
                            subArr[8] = i;
                            break;
                        case "起飞时刻":
                            subArr[9] = i;
                            break;
                        case "降落时刻":
                            subArr[10] = i;
                            break;
                        case "到位时刻":
                            subArr[11] = i;
                            break;
                    }
                }
            }

            //得到指定的单元格
            Cell cell;
            for (int i = 2; i <= rowLength; i++) {
                Fl flExcel = new Fl();
                row = sheet.getRow(i);
                for (int j = 0; j < 12; j++) {
                    cell = row.getCell(subArr[j]);
                    if (cell != null) {
                        if(cell.getCellTypeEnum()!=CellType.STRING)
                            cell.setCellType(CellType.STRING);
                        String cellData = cell.getStringCellValue().trim();
                        if(cellData.isEmpty())
                            continue;
                        if (j == 0) {
                            flExcel.setDate(cellData);
                        }else if (j == 1) {
                            flExcel.setFlightNo(cellData);
                        }else if (j == 2) {
                            flExcel.setAirline(cellData);
                        }else if (j == 3) {
                            flExcel.setDep(cellData);
                        }else if (j == 4) {
                            flExcel.setArr(cellData);
                        }else if (j == 5) {
                            flExcel.setType(cellData);
                        }else if (j == 6) {
                            flExcel.setAirplaneNumber(cellData);
                        }else if (j == 7) {
                            flExcel.setTimeType(cellData);
                        }else if (j == 8) {
                            flExcel.setSlideTime(cellData);
                        }else if (j == 9){
                            flExcel.setTakeOffTime(cellData);
                        }else if (j == 10){
                            flExcel.setLandTime(cellData);
                        }else
                            flExcel.setInPlaceTime(cellData);
                    }
                }
                flExcelList.add(flExcel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flExcelList;
    }
}
