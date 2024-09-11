package k_paas.balloon.keeper.batch.job;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import k_paas.balloon.keeper.batch.dto.UpdateClimateServiceSpec;
import k_paas.balloon.keeper.batch.reader.ClimateReader;
import k_paas.balloon.keeper.batch.writer.ClimateWriter;
import k_paas.balloon.keeper.domain.climateData.ClimateData;
import k_paas.balloon.keeper.domain.climateData.ClimateDataJpaRepository;
import k_paas.balloon.keeper.infrastructure.objectStorage.NcpObjectStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@EnableBatchProcessing
@Configuration
public class ClimateJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NcpObjectStorageService ncpObjectStorageService;

    private final ClimateReader climateReader;
    private final ClimateWriter climateWriter;

    private final ClimateDataJpaRepository climateDataJpaRepository;

    public ClimateJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, NcpObjectStorageService ncpObjectStorageService, ClimateReader climateReader, ClimateWriter climateWriter,
            ClimateDataJpaRepository climateDataJpaRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.ncpObjectStorageService = ncpObjectStorageService;
        this.climateReader = climateReader;
        this.climateWriter = climateWriter;
        this.climateDataJpaRepository = climateDataJpaRepository;
    }

    @Bean
    public Job climateJob() {
        return new JobBuilder("climateJob", jobRepository)
                .start(climateStep())
                .next(uploadToObjectStep())
                .next(deleteLocalObjectStep())
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

    @Bean
    public Step uploadToObjectStep() {
        return new StepBuilder("uploadToS3Step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("S3에 업로드 시작");
                    String object = ncpObjectStorageService.putObject();
                    climateDataJpaRepository.save(new ClimateData(object));
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step deleteLocalObjectStep() {
        return new StepBuilder("deleteLocalObjectStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Local Data 삭제");
                    Files.delete(Path.of("climate_data.csv"));
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
