package aircrew.version1.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PilotRepositoryTest {

    @Autowired
    private PilotRepository pilotRepository;

    @Test
    void findALL(){
        System.out.println(pilotRepository.findAll());
    }

}