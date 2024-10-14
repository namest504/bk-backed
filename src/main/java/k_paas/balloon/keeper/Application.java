package k_paas.balloon.keeper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import k_paas.balloon.keeper.batch.ClimateJobConfig;
import k_paas.balloon.keeper.infrastructure.objectStorage.NcpObjectStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {

    private final JobLauncher jobLauncher;
    private final ClimateJobConfig climateJobConfig;
    private final NcpObjectStorageService ncpObjectStorageService;

    public Application(JobLauncher jobLauncher, ClimateJobConfig climateJobConfig, NcpObjectStorageService ncpObjectStorageService) {
        this.jobLauncher = jobLauncher;
        this.climateJobConfig = climateJobConfig;
        this.ncpObjectStorageService = ncpObjectStorageService;
    }




    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
//
//    @PostConstruct
//    public void init() {
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
//        log.info("The server time is set in Asia/Seoul");
//    }

    @Bean
    public ApplicationRunner run() {
        return args -> {
            log.info("The server time is {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        };
    }

    /**
     * Batch Process Development Test
     */
	
	/*@Bean
	public ApplicationRunner runJobOnStartup() {
		return args -> {
			JobParameters jobParameters = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis())
					.toJobParameters();

			try {
				log.info("Application job excute in {}", LocalDateTime.now());
				jobLauncher.run(climateJobConfig.climateJob(), jobParameters);
			} catch (JobExecutionException e) {
				log.error("Job 수행 실패 cause : {}", e.getMessage());
			}
		};
	}*/

    /**
     * NCP Object Storage Download Test Method
     */
    /*@Bean
    public ApplicationRunner run() {
        return args -> {
            log.info("download start");
            ncpObjectStorageService.downloadObject();
        };
    }*/
}
