package k_paas.balloon.keeper.global.async;

import k_paas.balloon.keeper.batch.climate.runner.ClimateBatchRunner;
import k_paas.balloon.keeper.batch.climate.runner.ClimateRunnerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ClimateBatchAsyncWrapper {

    private final ClimateBatchRunner climateBatchRunner;

    @Async("threadPoolTaskExecutor")
    public void runBatch(ClimateRunnerRequest request) {
        CompletableFuture.runAsync(() -> {
            climateBatchRunner.run(request);
        });
    }
}