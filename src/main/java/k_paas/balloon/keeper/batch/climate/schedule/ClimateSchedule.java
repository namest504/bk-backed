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

    public ClimateSchedule(ClimateBatchRunner climateBatchRunner) {
        this.climateBatchRunner = climateBatchRunner;
    }

    /**
     * UTC 기준 매일 6시간 간격 10분에 작업을 수행
     * 
     * 기상 데이터 하루 6시간 단위로 조회해야 응답 있음, 이 외에 시간에 조회할 경우 데이터 없음
     *
     * 기상 데이터 업데이트가 실시간으로 되지 않은 점을 고려하여
     * 현재 시간으로 부터 24시간 이전 값을 조회 후 24시간 후의 예측 기상 데이터를 사용
     */
    @Scheduled(cron = "0 30 */6 * * *", zone = "UTC" )
    public void execute() {
        climateBatchRunner.run(ClimateRunnerRequest.of(getCurrentTime(), "24"));
    }
}
