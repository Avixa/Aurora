package altn72.aurora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.ZoneId;

@SpringBootApplication
public class AuroraApplication {

    public static void main(String[] args) {

        SpringApplication.run(AuroraApplication.class, args);


    }

    @Bean
    public Clock clock() {
        // date_ouverture is a TIMESTAMP without time zone written in Paris time
        return Clock.system(ZoneId.of("Europe/Paris"));
    }


}
