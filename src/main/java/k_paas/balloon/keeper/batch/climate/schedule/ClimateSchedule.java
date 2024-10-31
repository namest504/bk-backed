package k_paas.balloon.keeper.batch.climate.schedule;

import k_paas.balloon.keeper.batch.climate.config.ClimateJobConfig;
import k_paas.balloon.keeper.batch.climate.runner.ClimateBatchRunner;
import k_paas.balloon.keeper.batch.climate.runner.ClimateRunnerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static k_paas.balloon.keeper.global.util.TimeUtil.getCurrentTime;

@Slf4j
@EnableScheduling
@Component
public class ClimateSchedule {

    private final ClimateBatchRunner climateBatchRunner;

    public ClimateSchedule(JobLauncher jobLauncher, ClimateJobConfig climateJobConfig, ClimateBatchRunner climateBatchRunner) {
        this.climateBatchRunner = climateBatchRunner;
    }

    /**
     * 매일 6시간 간격 10분에 작업을 수행 UTC 기준
     */
    @Scheduled(cron = "0 30 */6 * * *", zone = "UTC" )
    public void execute() {
        climateBatchRunner.run(ClimateRunnerRequest.of(getCurrentTime(), "24"));
    }
}
