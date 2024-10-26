package k_paas.balloon.keeper.api.domain.climateData;

import k_paas.balloon.keeper.batch.ClimateJobConfig;
import k_paas.balloon.keeper.batch.ClimateSchedule;
import k_paas.balloon.keeper.global.exception.InvalidAPIKeyException;
import k_paas.balloon.keeper.global.property.ApiKeyProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/climate")
@Slf4j
@RequiredArgsConstructor
public class ClimateDataController {

    private final ClimateDataService climateDataService;
    private final ApiKeyProperty apiKeyProperty;
    private final JobLauncher jobLauncher;
    private final ClimateJobConfig climateJobConfig;
    private final ClimateSchedule climateSchedule;

    @GetMapping("/data-path")
    public ResponseEntity<ClimateDataPathResponse> getResentBatchedCsvPath(@RequestHeader(value = "BK-API-KEY") String apiKey) {

        if (apiKey == null || !apiKey.equals(apiKeyProperty.key())) {
            throw new InvalidAPIKeyException();
        }

        final ClimateDataPathResponse recentCsvFilePath = climateDataService.getRecentCsvFilePath();

        return ResponseEntity.status(OK)
                .body(recentCsvFilePath);
    }

    @GetMapping("/simulation/init")
    public ResponseEntity<Void> initLearning(@RequestHeader(value = "BK-API-KEY") String apiKey) {

        if (apiKey == null || !apiKey.equals(apiKeyProperty.key())) {
            throw new InvalidAPIKeyException();
        }

        climateDataService.initLearning();

        return ResponseEntity.status(ACCEPTED)
                .build();
    }

    @GetMapping("/batch/init")
    public ResponseEntity<Void> initBatchJob(
            @RequestHeader(value = "BK-API-KEY") String apiKey,
            @RequestParam String utcTime,
            @RequestParam String predictHour
    ) {
        if (apiKey == null || !apiKey.equals(apiKeyProperty.key())) {
            throw new InvalidAPIKeyException();
        }

        if(utcTime.trim().isEmpty() || utcTime == null) {
            utcTime = getCurrentTime();
        }

        if(predictHour.trim().isEmpty() || predictHour == null) {
            predictHour = "24";
        }
        log.info("[{}][{}]", utcTime, predictHour);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("batchStartedTime", utcTime)
                .addString("predictHour", predictHour)
                .addLong("uniqueId", System.currentTimeMillis())
                .toJobParameters();

        CompletableFuture.runAsync(() -> {
            try {
                jobLauncher.run(climateJobConfig.climateJob(), jobParameters);
                log.info("Batch job started successfully for parameters: {}", jobParameters);
            } catch (JobExecutionException e) {
                log.error("Job 수행 실패 cause : {}", e.getMessage());
                // 필요한 경우 여기에 추가적인 오류 처리 로직을 구현할 수 있습니다.
            }
        });

        return ResponseEntity.status(ACCEPTED)
                .build();
    }

    private static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        ZoneId utcZone = ZoneId.of("UTC");

        ZonedDateTime now = ZonedDateTime.now(utcZone);
        log.info("raw timestamp = {}", now.format(formatter));

        return now.minusHours(24).format(formatter);
    }
}
