package aircrew.version1.utils;

import aircrew.version1.entity.LastMp;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LastMpExcelUtils {
    public static List<LastMp> addLastMpExcel(InputStream inputStream) {
        List<LastMp> excelList = new ArrayList<>();
        Workbook workbook ;
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
            int[] subArr = new int[28];
            Cell cellSub ;
            for (int i = 0; i < colLength ;i++){
                cellSub = row.getCell(i);
                if(cellSub != null){
                    if(cellSub.getCellTypeEnum()!=CellType.STRING)
                        cellSub.setCellType(CellType.STRING);
                    String data = cellSub.getStringCellValue();
                    switch (data) {
                        case "人员编号":
                            subArr[0] = i;
                            break;
                        case "姓名":
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
                        case "机号":
                            subArr[5] = i;
                            break;
                        case "计费机型":
                            subArr[6] = i;
                            break;
                        case "航班性质":
                            subArr[7] = i;
                            break;
                        case "机上岗位":
                            subArr[8] = i;
                            break;
                        case "是否夜航":
                            subArr[9] = i;
                            break;
                        case "是否国际":
                            subArr[10] = i;
                            break;
                        case "是否计费":
                            subArr[11] = i;
                            break;
                        case "是否乘机":
                            subArr[12] = i;
                            break;
                        case "实飞时间":
                            subArr[13] = i;
                            break;
                        case "航线名称":
                            subArr[14] = i;
                            break;
                        case "城市三字码":
                            subArr[15] = i;
                            break;
                        case "特殊航线计发比例":
                            subArr[16] = i;
                            break;
                        case "特殊航线驻外天数":
                            subArr[17] = i;
                            break;
                        case "各航段实飞时间":
                            subArr[18] = i;
                            break;
                        case "各航段计发比例":
                            subArr[19] = i;
                            break;
                        case "各航段是否国际":
                            subArr[20] = i;
                            break;
                        case "返航或备降":
                            subArr[21] = i;
                            break;
                        case "备注":
                            subArr[22] = i;
                            break;
                        case "计薪时间":
                            subArr[23] = i;
                            break;
                        case "任职组织":
                            subArr[24] = i;
                            break;
                        case "薪酬部门":
                            subArr[25] = i;
                            break;
                        case "出错说明":
                            subArr[26] = i;
                            break;
                    }
                }
            }

            //得到指定的单元格
            Cell cell ;
            for (int i = 1; i <= rowLength; i++) {
                LastMp mpExcel = new LastMp();
                Row rowValue;
                rowValue = sheet.getRow(i);
                for (int j = 0; j < 27; j++) {
                    cell = rowValue.getCell(subArr[j]);
                    if (cell != null) {
                        if(cell.getCellTypeEnum()!=CellType.STRING)
                            cell.setCellType(CellType.STRING);
                        String cellData = cell.getStringCellValue().trim();
                        if(cellData.isEmpty())
                            continue;
                        if (j == 0) {
                            mpExcel.setEid(Integer.parseInt(cellData));
                        }else if (j == 1) {
                            mpExcel.setName(cellData);
                        }else if (j == 2) {
                            mpExcel.setDate(cellData);
                        }else if (j == 3) {
                            mpExcel.setTakeOffTime(cellData);
                        }else if (j == 4) {
                            mpExcel.setFlightNo(cellData);
                        }else if (j == 5) {
                            mpExcel.setAirplaneNumber(cellData);
                        }else if (j == 6) {
                            mpExcel.setType(cellData);
                        }else if (j == 7) {
                            mpExcel.setProperty(cellData);
                        }else if (j == 8) {
                            mpExcel.setPost(cellData);
                        }else if (j == 9) {
                            mpExcel.setIsNight(cellData);
                        }else if (j == 10) {
                            mpExcel.setIsInternational(cellData);
                        }else if (j == 11) {
                            mpExcel.setIsCost(cellData);
                        }else if (j == 12) {
                            mpExcel.setIsTake(cellData);
                        }else if (j == 13) {
                            mpExcel.setRealTime(cellData);
                        }else if (j == 14) {
                            mpExcel.setAirLine(cellData);
                        }else if (j == 15) {
                            mpExcel.setTcc(cellData);
                        }else if (j == 16) {
                            mpExcel.setSpecialAirlineProportion(cellData);
                        }else if (j == 17) {
                            mpExcel.setSpecialAirlineDays(cellData);
                        }else if (j == 18) {
                            mpExcel.setEachAirlineTime(cellData);
                        }else if (j == 19) {
                            mpExcel.setEachAirlineProportion(cellData);
                        }else if (j == 20) {
                            mpExcel.setIsEachAirlineInternational(cellData);
                        }else if (j == 21) {
                            mpExcel.setReversal(cellData);
                        }else if (j == 22) {
                            mpExcel.setRemarks(cellData);
                        }else if (j == 23) {
                            mpExcel.setPayTime(cellData);
                        }else if (j == 24) {
                            mpExcel.setOrganization(cellData);
                        }else if (j == 25) {
                            mpExcel.setDepartment(cellData);
                        }else {
                            mpExcel.setErrorStatement(cellData);
                        }
                    }
                }
                excelList.add(mpExcel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excelList;
    }
}
