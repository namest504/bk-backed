package k_paas.balloon.keeper.global.async;

import k_paas.balloon.keeper.infrastructure.client.climate.ClimateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClimateRequestAsyncWrapper {

    private final ClimateClient climateClient;

    /**
     * 기후 데이터를 가져오기 위한 요청을 비동기식으로 보냄
     */
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String[][]> sendRequest(String varn, String level, String predictHour, String timeStamp) {
        return CompletableFuture.completedFuture(climateClient.fetchGetClimateData(varn, level, predictHour, timeStamp));
    }
}
