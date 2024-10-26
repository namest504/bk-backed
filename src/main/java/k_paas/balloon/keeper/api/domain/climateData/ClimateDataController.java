package k_paas.balloon.keeper.api.domain.climateData;

import k_paas.balloon.keeper.batch.ClimateJobConfig;
import k_paas.balloon.keeper.batch.ClimateSchedule;
import k_paas.balloon.keeper.global.exception.InvalidAPIKeyException;
import k_paas.balloon.keeper.global.property.ApiKeyProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        climateSchedule.execute(utcTime, predictHour);

        return ResponseEntity.status(ACCEPTED)
                .build();
    }
}
