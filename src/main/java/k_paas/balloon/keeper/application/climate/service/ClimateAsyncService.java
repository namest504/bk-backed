package k_paas.balloon.keeper.application.climate.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import k_paas.balloon.keeper.infrastructure.client.ClimateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClimateAsyncService {

    private final ClimateClient climateClient;
    private final Executor asyncExecutor;

    public ClimateAsyncService(ClimateClient climateClient, Executor threadPoolTaskExecutor) {
        this.climateClient = climateClient;
        this.asyncExecutor = threadPoolTaskExecutor;
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String[][]> sendRequest(String varn, String level, String predictHour) {
//        log.info("[{} : {} : {}] sendRequest 수행 [{}]", varn,level,predictHour, Thread.currentThread().getName());
        return CompletableFuture.completedFuture(climateClient.sendRequestClimateData(varn, level, predictHour));
    }

    public CompletableFuture<String[][]> sendRequestCompletable(String varn, String level, String predictHour) {
        return CompletableFuture.supplyAsync(
                () -> climateClient.sendRequestClimateData(varn, level, predictHour), asyncExecutor);
    }
}
