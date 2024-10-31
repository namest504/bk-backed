package k_paas.balloon.keeper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 기본적으로 Asia/Seoul 시간대를 사용하도록 애플리케이션을 초기화
     * JVM의 기본 시간대를 설정하고 시간대가 Asia/Seoul로 설정되었음을 로그로 남김
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("The server time is set in Asia/Seoul");
    }

    /**
     * 시작 시 현재 서버 시간을 로그로 남김
     */
    @Bean
    public ApplicationRunner run() {
        return args -> {
            log.info("The server time is {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        };
    }
}
