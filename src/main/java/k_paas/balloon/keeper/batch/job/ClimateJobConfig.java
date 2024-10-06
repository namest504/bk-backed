package k_paas.balloon.keeper.batch.job;

import java.net.URI;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@EnableBatchProcessing
@Configuration
public class ClimateJobConfig {

    private final static String API_URL = "http://" + "default-my-simulation-se-e5042-26768252-5a90f913f7f8.kr.lb.naverncp.com" + ":8001";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NcpObjectStorageService ncpObjectStorageService;

    private final ClimateReader climateReader;
    private final ClimateWriter climateWriter;

    private final ClimateDataJpaRepository climateDataJpaRepository;

    private final RestTemplate restTemplate;

    public ClimateJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, NcpObjectStorageService ncpObjectStorageService, ClimateReader climateReader,
            ClimateWriter climateWriter,
            ClimateDataJpaRepository climateDataJpaRepository, RestTemplate restTemplate) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.ncpObjectStorageService = ncpObjectStorageService;
        this.climateReader = climateReader;
        this.climateWriter = climateWriter;
        this.climateDataJpaRepository = climateDataJpaRepository;
        this.restTemplate = restTemplate;
    }

    @Bean
    public Job climateJob() {
        return new JobBuilder("climateJob", jobRepository)
                .start(climateStep())
                .on("COMPLETED")
                .to(uploadToObjectStep())
                .from(uploadToObjectStep())
                .on("*").to(deleteLocalObjectStep())
                .end()
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
                    log.info("업로드 경로 : {}", object);
                    ClimateData save = climateDataJpaRepository.save(ClimateData.builder().filePath(object).build());
                    log.info("저장 : [{} || {}]", save.getId(), save.getFilePath());
                    fetchObjectPath(object);
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

    private void fetchObjectPath(String path) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path("/climate-data")
                .queryParam("object_name", path)
                .build()
                .toUri();
        log.info("request url: [{}]", uri);
        ResponseEntity<String> response = restTemplate.getForEntity(uri.toString(), String.class);

        // 응답 결과 출력
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Succeed fetch Object path data");
        } else {
            log.error("Failed fetch");
        }
    }
}
