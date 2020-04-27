package aircrew.version1.controller;

import aircrew.version1.entity.Pilot;
import aircrew.version1.mapper.PilotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequestMapping("/pp")
@RestController
public class TestController {
    @Autowired
    private PilotRepository pilotRepository;

    @GetMapping("/findAll")
    public List<Pilot> findAll(){
        return pilotRepository.findAll();
    }
}
