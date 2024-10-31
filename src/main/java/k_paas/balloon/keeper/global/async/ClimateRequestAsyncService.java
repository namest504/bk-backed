package k_paas.balloon.keeper.global.async;

import k_paas.balloon.keeper.infrastructure.client.ClimateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClimateRequestAsyncService {

    private final ClimateClient climateClient;

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String[][]> sendRequest(String varn, String level, String predictHour, String timeStamp) {
        return CompletableFuture.completedFuture(climateClient.fetchGetClimateData(varn, level, predictHour, timeStamp));
    }
}
