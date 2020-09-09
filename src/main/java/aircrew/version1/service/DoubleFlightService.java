package aircrew.version1.service;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public interface DoubleFlightService {
    Map<String,Object> addProperty(HttpServletRequest request);
    Map<String,Object> MtoJ();
    String flight(Model model, HttpServletRequest request, HttpSession session);
    String property(Model model);
    Map<String, Object> calculateFlight(HttpServletRequest request);
    String doubleFlight(Model model);
    String filterDoubleFlight(Model model);
    Map<String, Object> updateDoubleFlight(HttpServletRequest request);
    Map<String, Object> updateFilterDoubleFlight(HttpServletRequest request);
    void confirm(HttpServletRequest request);
    void cancelConfirm(HttpServletRequest request);
    Map<String, Object> downloadJudge();
    void downloadDoubleFlight(HttpServletResponse response) throws IOException;
    void downloadFlMtoJ(HttpServletResponse response) throws IOException;
    void downloadFl(HttpServletResponse response) throws IOException;
    String deleteProperty(@PathVariable String property);
}
