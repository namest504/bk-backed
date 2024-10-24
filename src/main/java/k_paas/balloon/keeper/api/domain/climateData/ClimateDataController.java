package k_paas.balloon.keeper.api.domain.climateData;

import k_paas.balloon.keeper.batch.ClimateJobConfig;
import k_paas.balloon.keeper.global.exception.InvalidAPIKeyException;
import k_paas.balloon.keeper.global.property.ApiKeyProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
    public ResponseEntity<Void> initBatchJob(@RequestHeader(value = "BK-API-KEY") String apiKey) {
        if (apiKey == null || !apiKey.equals(apiKeyProperty.key())) {
            throw new InvalidAPIKeyException();
        }

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            log.info("Application job excute in {}", LocalDateTime.now());
            jobLauncher.run(climateJobConfig.climateJob(), jobParameters);
        } catch (JobExecutionException e) {
            log.error("Job 수행 실패 cause : {}", e.getMessage());
        }

        return ResponseEntity.status(ACCEPTED)
                .build();
    }
}
