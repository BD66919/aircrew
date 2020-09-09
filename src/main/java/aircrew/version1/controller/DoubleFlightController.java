package aircrew.version1.controller;

import aircrew.version1.service.DoubleFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class DoubleFlightController {

    @Autowired
    DoubleFlightService doubleFlightService;

    @GetMapping(value = "/doubleFlight/flight")
    public String flight(Model model, HttpServletRequest request, HttpSession session) {
       return doubleFlightService.flight(model, request, session);
    }

    @PostMapping(value = "/doubleFlight/MtoJ")
    @ResponseBody
    public Map<String, Object> MtoJ(){
        return doubleFlightService.MtoJ();
    }

    @GetMapping(value = "/doubleFlight/property")
    public String property(Model model) { return doubleFlightService.property(model); }

    @PostMapping(value = "/addProperty")
    @ResponseBody
    public Map<String, Object> addProperty(HttpServletRequest request){
        return doubleFlightService.addProperty(request);
    }

    @PostMapping(value = "/doubleFlight/calculateFlight")
    @ResponseBody
    public Map<String, Object> calculateFlight(HttpServletRequest request) {
        return doubleFlightService.calculateFlight(request);
    }

    @GetMapping(value = "/doubleFlight/doubleFlight")
    public String doubleFlight(Model model) {
        return doubleFlightService.doubleFlight(model);
    }

    @GetMapping(value = "/doubleFlight/filterDoubleFlight")
    public String filterDoubleFlight(Model model) {
        return doubleFlightService.filterDoubleFlight(model);
    }

    @PostMapping(value = "/doubleFlight/updateDoubleFlight")
    @ResponseBody
    public Map<String, Object> updateDoubleFlight(HttpServletRequest request) {
        return doubleFlightService.updateDoubleFlight(request);
    }

    @PostMapping(value = "/doubleFlight/updateFilterDoubleFlight")
    @ResponseBody
    public Map<String, Object> updateFilterDoubleFlight(HttpServletRequest request) {
        return doubleFlightService.updateFilterDoubleFlight(request);
    }

    @PostMapping(value = "/doubleFlight/confirm")
    @ResponseBody
    public void confirm(HttpServletRequest request) {
        doubleFlightService.confirm(request);
    }

    @PostMapping(value = "/doubleFlight/cancelConfirm")
    @ResponseBody
    public void cancelConfirm(HttpServletRequest request) {
        doubleFlightService.cancelConfirm(request);
    }

    @PostMapping(value = "/doubleFlight/downloadJudge")
    @ResponseBody
    public Map<String, Object> downloadJudge() {
        return  doubleFlightService.downloadJudge();
    }

    @RequestMapping(value = "/doubleFlight/downloadDoubleFlight", method = RequestMethod.GET)
    public void downloadDoubleFlight(HttpServletResponse response) throws IOException {
        doubleFlightService.downloadDoubleFlight(response);
    }

    @RequestMapping(value = "/doubleFlight/downloadFlMtoJ", method = RequestMethod.GET)
    public void downloadFlMtoJ(HttpServletResponse response) throws IOException {
        doubleFlightService.downloadFlMtoJ(response);
    }

    @RequestMapping(value = "/doubleFlight/downloadFl", method = RequestMethod.GET)
    public void downloadFl(HttpServletResponse response) throws IOException {
        doubleFlightService.downloadFl(response);
    }

    @PostMapping(value = "/deleteProperty={property}")
    public String deleteAirline(@PathVariable String property){
        return doubleFlightService.deleteProperty(property);
    }
}
