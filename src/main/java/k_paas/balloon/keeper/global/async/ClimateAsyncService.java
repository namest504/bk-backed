package k_paas.balloon.keeper.global.async;

import k_paas.balloon.keeper.batch.ClimateBatchRunner;
import k_paas.balloon.keeper.infrastructure.client.ClimateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClimateAsyncService {

    private final ClimateClient climateClient;
    private final ClimateBatchRunner climateBatchRunner;

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String[][]> sendRequest(String varn, String level, String predictHour, String timeStamp) {
        return CompletableFuture.completedFuture(climateClient.fetchGetClimateData(varn, level, predictHour, timeStamp));
    }

    @Async("threadPoolTaskExecutor")
    public void runBatch(String utcTime, String predictHour) {
        CompletableFuture.runAsync(() -> {
            climateBatchRunner.run(utcTime, predictHour);
        });
    }
}
