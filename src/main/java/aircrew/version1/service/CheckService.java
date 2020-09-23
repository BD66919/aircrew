package aircrew.version1.service;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface CheckService {
    String check(Model model);
    void excelDownload(HttpServletResponse response) throws IOException;
}
