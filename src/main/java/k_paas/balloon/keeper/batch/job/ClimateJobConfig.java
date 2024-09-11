package k_paas.balloon.keeper.batch.job;

import java.util.List;
import k_paas.balloon.keeper.batch.dto.UpdateClimateServiceSpec;
import k_paas.balloon.keeper.batch.reader.ClimateReader;
import k_paas.balloon.keeper.batch.writer.ClimateWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@EnableBatchProcessing
@Configuration
public class ClimateJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ClimateReader climateReader;
    private final ClimateWriter climateWriter;

    public ClimateJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, ClimateReader climateReader, ClimateWriter climateWriter) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.climateReader = climateReader;
        this.climateWriter = climateWriter;
    }

    @Bean
    public Job climateJob() {
        return new JobBuilder("climateJob", jobRepository)
                .start(climateStep())
                .build();
    }

    @Bean
    public Step climateStep() {
        log.info("climateStep start");
        return new StepBuilder("climateStep", jobRepository)
                .<List<UpdateClimateServiceSpec>, List<UpdateClimateServiceSpec>>chunk(10, transactionManager)
                .reader(climateReader)
                .writer(climateWriter)
                .build();
    }
}
