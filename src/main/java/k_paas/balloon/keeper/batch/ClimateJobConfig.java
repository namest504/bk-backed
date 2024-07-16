package k_paas.balloon.keeper.batch;

import java.util.List;
import k_paas.balloon.keeper.application.climate.dto.UpdateClimateServiceSpec;
import k_paas.balloon.keeper.domain.climate.entity.Climate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@EnableScheduling
@EnableBatchProcessing
@Configuration
public class ClimateJobConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ClimateReader climateReader;
    private final ClimateProcessor climateProcessor;
    private final ClimateWriter climateWriter;

    public ClimateJobConfig(JobLauncher jobLauncher, JobRepository jobRepository, PlatformTransactionManager transactionManager, ClimateReader climateReader, ClimateProcessor climateProcessor,
            ClimateWriter climateWriter) {
        this.jobLauncher = jobLauncher;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.climateReader = climateReader;
        this.climateProcessor = climateProcessor;
        this.climateWriter = climateWriter;
    }

    @Scheduled(cron = "0 10 */8 * * *")
    public void execute() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(climateJob(), jobParameters);
        } catch (JobExecutionException e) {
            log.error("Job 수행 실패 cause : {}", e.getMessage());
        }
    }

    @Bean
    public Job climateJob() {
        return new JobBuilder("climateJob", jobRepository)
                .start(climateStep())
                .build();
    }

    @Bean
    public Step climateStep() {
        return new StepBuilder("climateStep", jobRepository)
                .<List<UpdateClimateServiceSpec>, List<Climate>>chunk(100, transactionManager)
                .reader(climateReader)
                .processor(climateProcessor)
                .writer(climateWriter)
                .build();
    }
}
