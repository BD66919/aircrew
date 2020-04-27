package aircrew.version1.utils;

import aircrew.version1.entity.Excel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.CellType;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jiangzhendong
 * @Description 该类用于读取excel文件中的数据
 *
 */
public class ExcelUtils {

    public static List<Excel> excelToFinanceList(InputStream inputStream) {
        List<Excel> Excellist = new ArrayList<>();
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

            //得到指定的单元格
            Cell cell = row.getCell(0);
            for (int i = 1; i <= rowLength; i++) {
                Excel PilotExcel = new Excel();
                row = sheet.getRow(i);
                for (int j = 0; j < colLength; j++) {
                    //列： 0姓名   1员工号   2航线   3航线三字码   4性质    5时间
                    cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellType(CellType.STRING);
                        String data = cell.getStringCellValue();
                        data = data.trim();
                        if (j == 0) {
                            PilotExcel.setName(data);
                        }else if (j == 1) {
                            if(data.equals(""))
                                data = "00000";
                            PilotExcel.setEid(Integer.parseInt(data));
                        }else if (j == 2) {
                            PilotExcel.setLine(data);
                        }else if (j == 3) {
                            PilotExcel.setTcc(data);
                        }else if (j == 4){
                            PilotExcel.setProperties(data);
                        }else if (j == 5){
                            cell = row.getCell(6);
                            cell.setCellType(CellType.STRING);
                            String time = cell.getStringCellValue();
                            time = time.trim();
                            PilotExcel.setDate(data+" "+time);
                        }
                    }
                }
                Excellist.add(PilotExcel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Excellist;
    }
}