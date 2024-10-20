package k_paas.balloon.keeper.batch;

import k_paas.balloon.keeper.infrastructure.client.SimulationClient;
import k_paas.balloon.keeper.infrastructure.persistence.memory.ClimateDataInMemoryStore;
import k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp.NcpObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static k_paas.balloon.keeper.batch.BatchContextUtil.addContextData;
import static k_paas.balloon.keeper.batch.BatchContextUtil.getCurrentBatchContext;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class ClimateJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final SimulationClient simulationClient;
    private final ClimateDataInMemoryStore climateDataInMemoryStore;

    private final ClimateReader climateReader;
    private final ClimateWriter climateWriter;

    @Bean
    public Job climateJob() {
        return new JobBuilder("climateJob", jobRepository)
                .start(climateStep())
                .next(processClimateDataStep())
                .next(uploadToObjectStep())
                .next(deleteLocalObjectStep())
                .build();
    }

    @Bean
    public Step processClimateDataStep() {
        return new StepBuilder("processClimateDataStep", jobRepository)
                .<List<UpdateClimateServiceSpec>, List<UpdateClimateServiceSpec>>chunk(10, transactionManager)
                .reader(climateReader)
                .writer(climateWriter)
                .build();
    }


    @Bean
    public Step climateStep() {
        return new StepBuilder("climateStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    log.info("raw timestamp = {}", sdf.format(LocalDateTime.now(ZoneId.of("UTC"))));

                    // 현재 시간에서 12시간을 뺍니다
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendar.add(Calendar.HOUR_OF_DAY, -12);

                    String timestamp = sdf.format(calendar.getTime());
                    log.info("calc timestamp = {}", timestamp);
                    addContextData(chunkContext, "timestamp", timestamp);

                    String csvFileName = String.format("./climate_data_%s.csv", timestamp);
                    log.info("csvFileName = {}", csvFileName);
                    addContextData(chunkContext, "csvFileName", csvFileName);
                    existFileInLocal(csvFileName);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step uploadToObjectStep() {
        return new StepBuilder("uploadToS3Step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    uploadProcess(chunkContext);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step deleteLocalObjectStep() {
        return new StepBuilder("deleteLocalObjectStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    deleteLocalFile(chunkContext);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    private void existFileInLocal(String csvFileName) {
        File file = new File(csvFileName);
        boolean isNewFile;
        if (!file.exists()) {
            try {
                isNewFile = file.createNewFile();
                if (isNewFile) {
                    log.info("New file created: {}", csvFileName);
                }
            } catch (IOException e) {
                log.error("Error creating new file: {}", csvFileName, e);
            }
        }
    }

    private void uploadProcess(ChunkContext chunkContext) {
        String object = putObjectInNCPObjectStorage(chunkContext);
//        simulationClient.fetchObjectPath(object);
        climateDataInMemoryStore.put("recent-path", object);
    }

    private String putObjectInNCPObjectStorage(ChunkContext chunkContext) {
        String fileName = getCurrentBatchContext(chunkContext, "csvFileName");
        String object = ncpObjectStorageService.putObject(fileName);
        return object;
    }

    private void deleteLocalFile(ChunkContext chunkContext) throws IOException {
        String fileName = getCurrentBatchContext(chunkContext, "csvFileName");

        Path filePath = Path.of(fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("File {} has been successfully deleted.", filePath);
        } else {
            log.warn("File {} does not exist, skipping deletion.", filePath);
        }
    }
}
