package k_paas.balloon.keeper.application.climate.service;

import java.util.concurrent.CompletableFuture;
import k_paas.balloon.keeper.infrastructure.client.ClimateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClimateAsyncService {

    private final ClimateClient climateClient;

    public ClimateAsyncService(ClimateClient climateClient) {
        this.climateClient = climateClient;
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String[][]> sendRequest(String varn, String level, String predictHour) {
        return CompletableFuture.completedFuture(climateClient.sendRequestClimateData(varn, level, predictHour));
    }
}
