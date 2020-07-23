package aircrew.version1.service;

import aircrew.version1.entity.Air;
import aircrew.version1.entity.Mp;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface DataService {
    List<Air> compareFlAir();
    List<Mp> compareAirMp();
    Map<String,Object> login();

    Map<String,Object> addMp(@RequestParam("file") MultipartFile file);
    Map<String,Object> addNextMp(@RequestParam("file") MultipartFile file);
    Map<String,Object> addLastMp(@RequestParam("file") MultipartFile file);
    Map<String,Object> addFl(@RequestParam("file") MultipartFile file);
    Map<String,Object> addAir(@RequestParam("file") MultipartFile file);
    Map<String,Object> addAirline(HttpServletRequest request);
    Map<String,Object> addCadre(HttpServletRequest request);
    Map<String,Object> addNightFlight(HttpServletRequest request);
    Map<String,Object> addStageDoubleFlight(HttpServletRequest request);

    Map<String,Object> deleteAll();
    Map<String,Object> deleteAir();
    Map<String,Object> deleteFl();
    Map<String,Object> deleteMp();
    Map<String,Object> deleteLastMp();
    Map<String,Object> deleteNextMp();

    String deleteAirline(@PathVariable String airline);
    String deleteCadre(@PathVariable String id);
    String deleteNightFlight(@PathVariable String number);
    String deleteStageDoubleFlight(@PathVariable String number);

    Map<String, Object> updateAirline(HttpServletRequest request);
    Map<String, Object> updateCadre(HttpServletRequest request);
    Map<String, Object> updateNightFlight(HttpServletRequest request);
    Map<String, Object> updateStageDoubleFlight(HttpServletRequest request);

    String airline(Model model);
    String cadre(Model model);
    String nightFlight(Model model);
    String stageDoubleFlight(Model model);

}
