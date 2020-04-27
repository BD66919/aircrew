package aircrew.version1.controller;

import aircrew.version1.entity.Soc;
import aircrew.version1.mapper.SocRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SocControllerTest {

    @Autowired
    SocRepository socRepository;

    @Test
    void checkSoc() {
    }

    @Test
    void testCheckSoc() {
        List<Soc> plane = socRepository.findAll();
        List<Soc>  doublePlane = new ArrayList<>();
        System.out.println(plane);
    }
}