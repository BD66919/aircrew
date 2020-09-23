package aircrew.version1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan( value = "aircrew.version1.mapper")
public class Version1Application {
    public static void main(String[] args) {
        SpringApplication.run(Version1Application.class, args);
    }
}
