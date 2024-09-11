package k_paas.balloon.keeper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import k_paas.balloon.keeper.batch.job.ClimateJobConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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

	public Application(JobLauncher jobLauncher, ClimateJobConfig climateJobConfig) {
		this.jobLauncher = jobLauncher;
		this.climateJobConfig = climateJobConfig;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Batch Process Development Test
	 */
	
	@Bean
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
	}
}
