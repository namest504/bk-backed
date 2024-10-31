package k_paas.balloon.keeper.api.domain.climateData;

import k_paas.balloon.keeper.api.domain.climateData.dto.ClimateDataPathResponse;
import k_paas.balloon.keeper.global.async.ClimateAsyncService;
import k_paas.balloon.keeper.global.exception.InvalidAPIKeyException;
import k_paas.balloon.keeper.global.property.ApiKeyProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ClimateAsyncService climateAsyncService;

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

    /**
     * 외부 배치 실행 트리거 API
     *
     * API의 응답을 특정 시간동안 반환을 안할경우 Client에서 API를
     * 재전송하게 되어 배치가 N번 수행되는 문제 발생
     * CompletableFuture runSync를 통해 내부 배치 로직을 비동기로 수행
     * Client에 응답을 바로 전달해주는 방법을 통해 문제 해결
     */
    @GetMapping("/batch/init")
    public ResponseEntity<Void> initBatchJob(
            @RequestHeader(value = "BK-API-KEY") String apiKey,
            @RequestParam String utcTime,
            @RequestParam String predictHour
    ) {
        if (apiKey == null || !apiKey.equals(apiKeyProperty.key())) {
            throw new InvalidAPIKeyException();
        }
        climateAsyncService.runBatch(utcTime, predictHour);

        return ResponseEntity.status(ACCEPTED)
                .build();
    }


}
