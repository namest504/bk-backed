package k_paas.balloon.keeper.application.domain.climateData;

import jakarta.validation.Valid;
import k_paas.balloon.keeper.batch.climate.runner.ClimateRunnerRequest;
import k_paas.balloon.keeper.global.annotation.ValidAPIKey;
import k_paas.balloon.keeper.global.async.ClimateBatchAsyncWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/climate")
@Slf4j
@RequiredArgsConstructor
public class ClimateDataController {

    private final ClimateDataService climateDataService;
    private final ClimateBatchAsyncWrapper climateBatchAsyncWrapper;

    /**
     * 가장 최근에 저장된 NCP Object Storage 의 CSV 파일 경로를 검색
     */
    @ValidAPIKey
    @GetMapping("/data-path")
    public ResponseEntity<ClimateDataPathResponse> getResentBatchedCsvPath() {

        final ClimateDataPathResponse recentCsvFilePath = climateDataService.getRecentCsvFilePath();

        return ResponseEntity.status(OK)
                .body(recentCsvFilePath);
    }

    /**
     * 기후 데이터 시뮬레이션을 위한 학습 과정 시작 트리거
     * 시뮬레이션 서버에서 학습 로직을 비동기 수행 처리 후 응답
     */
    @ValidAPIKey
    @GetMapping("/simulation/init")
    public ResponseEntity<Void> initLearning() {

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
    @ValidAPIKey
    @GetMapping("/batch/init")
    public ResponseEntity<Void> initBatchJob(
            @ModelAttribute @Valid ClimateRunnerRequest climateRunnerRequest
    ) {
        climateBatchAsyncWrapper.runBatch(climateRunnerRequest);

        return ResponseEntity.status(ACCEPTED)
                .build();
    }


}
