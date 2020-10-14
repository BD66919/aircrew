package aircrew.version1.utils;

import aircrew.version1.entity.NextAir;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiangzhendong
 * @Description 该类用于读取SOC航后excel文件中的数据
 *
 */
public class NextAirExcelUtils {
    public static List<NextAir> addNextAir(InputStream inputStream) {
        List<NextAir> excelList = new ArrayList<>();
        Workbook workbook;
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
            int[] subArr = new int[15];
            for (int i = 0; i < colLength ;i++){
                Cell cellSub = row.getCell(i);
                if(cellSub != null){
                    if(cellSub.getCellTypeEnum()!=CellType.STRING)
                        cellSub.setCellType(CellType.STRING);
                    String dataSub = cellSub.getStringCellValue().trim();
                    switch (dataSub) {
                        case "起飞":
                            subArr[0] = i;
                            break;
                        case "班号":
                            subArr[1] = i;
                            break;
                        case "机型":
                            subArr[2] = i;
                            break;
                        case "机号":
                            subArr[3] = i;
                            break;
                        case "离港":
                            subArr[4] = i;
                            break;
                        case "撤轮挡":
                            subArr[5] = i;
                            break;
                        case "到港":
                            subArr[6] = i;
                            break;
                        case "落地":
                            subArr[7] = i;
                            break;
                        case "上轮挡":
                            subArr[8] = i;
                            break;
                        case "性质":
                            subArr[9] = i;
                            break;
                        case "工号":
                            subArr[10] = i;
                            break;
                        case "名字":
                            subArr[11] = i;
                            break;
                        case "号位":
                            subArr[12] = i;
                            break;
                        case "职务":
                            subArr[13] = i;
                            break;
                        case "说明":
                            subArr[14] = i;
                            break;
                    }
                }
            }


            for (int i = 1; i <= rowLength; i++) {
                NextAir SocExcel = new NextAir();
                Row rowValue = sheet.getRow(i);
                for (int j = 0; j < 15; j++) {
                    Cell cell = rowValue.getCell(subArr[j]);
                    if (cell != null) {
                        if(cell.getCellTypeEnum()!=CellType.STRING)
                            cell.setCellType(CellType.STRING);
                        String cellData = cell.getStringCellValue().trim();
                        if(cellData.isEmpty())
                            continue;
                        if (j == 0) {
                            SocExcel.setTakeOffTime(cellData.split(" ")[1]);
                        }else if (j == 1) {
                            SocExcel.setFlightNo(cellData);
                        }else if (j == 2) {
                            SocExcel.setType(cellData);
                        }else if (j == 3) {
                            SocExcel.setFlightNumber(cellData);
                        }else if (j == 4) {
                            SocExcel.setDep(cellData);
                        }else if (j == 5) {
                            SocExcel.setSlideTime(cellData.split(" ")[1]);
                            SocExcel.setDate(cellData.split(" ")[0].replace("/","-"));
                        }else if (j == 6) {
                            SocExcel.setArr(cellData);
                        }else if (j == 7) {
                            SocExcel.setLandTime(cellData.split(" ")[1]);
                        }else if (j == 8) {
                            SocExcel.setInPlaceTime(cellData.split(" ")[1]);
                        }else if (j == 9) {
                            SocExcel.setProperty(cellData);
                        }else if (j == 10) {
                            SocExcel.setEid(Integer.parseInt(cellData));
                        }else if (j == 11) {
                            SocExcel.setName(cellData);
                        }else if (j == 12) {
                            SocExcel.setPosition(cellData);
                        }else if (j == 13) {
                            SocExcel.setQualify(cellData);
                        }else
                            SocExcel.setStatement(cellData);
                    }
                }
                excelList.add(SocExcel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excelList;
    }

//    static String getNumeric(String str) {
//        str=str.trim();
//        String str2="";
//        if(str != null && !"".equals(str)){
//            for(int i=0;i<str.length();i++  ){
//                if(str.charAt(i)>=48 && str.charAt(i)<=57){
//                    str2 =str2 + str.charAt(i);
//                }
//            }
//        }
//        return str2;
//    }
}
