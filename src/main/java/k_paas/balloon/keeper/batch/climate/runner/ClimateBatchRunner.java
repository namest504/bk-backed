package k_paas.balloon.keeper.batch.climate.runner;

import k_paas.balloon.keeper.batch.climate.config.ClimateJobConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import static k_paas.balloon.keeper.global.util.TimeUtil.getCurrentTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClimateBatchRunner {

    private final JobLauncher jobLauncher;
    private final ClimateJobConfig climateJobConfig;

    private static final String DEFAULT_PREDICT_HOUR = "24";

    /**
     * 기후 데이터 배치 작업을 수행
     * 수행 전 UTC 시간과 예측 시간은 작업을 시작하기 전에 검증
     */
    public void run(ClimateRunnerRequest request) {
        String utcTime = getValidUtcTime(request.utcTime());
        String predictHour = getValidPredictHour(request.predictHour());

        log.info("[{}][{}]", utcTime, predictHour);

        JobParameters jobParameters = buildJobParameters(utcTime, predictHour);

        try {
            jobLauncher.run(climateJobConfig.climateJob(), jobParameters);
            log.info("Batch job started successfully for parameters: {}", jobParameters);
        } catch (JobExecutionException e) {
            log.error("Job 수행 실패 cause : {}", e.getMessage());
        }
    }

    private String getValidUtcTime(String utcTime) {
        return (utcTime == null || utcTime.trim().isEmpty()) ? getCurrentTime() : utcTime.trim();
    }

    private String getValidPredictHour(String predictHour) {
        return (predictHour == null || predictHour.trim().isEmpty()) ? DEFAULT_PREDICT_HOUR : predictHour.trim();
    }

    private JobParameters buildJobParameters(String utcTime, String predictHour) {
        return new JobParametersBuilder()
                .addString("batchStartedTime", utcTime)
                .addString("predictHour", predictHour)
                .addLong("uniqueId", System.currentTimeMillis())
                .toJobParameters();
    }
}
