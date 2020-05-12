package aircrew.version1.controller;


import aircrew.version1.entity.Mp;
import aircrew.version1.mapper.MpRepository;
import aircrew.version1.utils.MpExcelUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MpController {
    @Autowired
    MpRepository mpRepository;

    @PostMapping(value = "/addMp")
    @ResponseBody
    public Map<String,Object> addMp(@RequestParam("file") MultipartFile file
    ) throws IOException, InvalidFormatException {
        System.out.println("ok");
        Map<String,Object> map = new HashMap<String,Object>();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.length() < 5 ) {
            map.put("msg","文件格式错误");
            return map;
        } else if(!(fileName.substring(fileName.length() - 5).equals(".xlsx")) & !(fileName.substring(fileName.length() - 4).equals(".xls"))){
            map.put("msg","文件类型错误");
            return map;
        }
        List<Mp> excelList = null;
        try {
            excelList = MpExcelUtils.addMp(file.getInputStream());
            if (excelList.size() <= 0) {
                map.put("msg","导入的数据为空或者不符合要求");
                return map;
            }
            //excel的数据保存到数据库
            try {
                mpRepository.saveAll(excelList);
            } catch (Exception e) {
                map.put("msg","导入的数据格式有误或者与当前已存在的数据中存在重复");
                return map;
            }
        } catch (Exception e) {
            map.put("msg","导入异常");
            return map;
        }
        map.put("msg","导入成功");
        return map;
    }
}
