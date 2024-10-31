package k_paas.balloon.keeper.batch;

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

    public void run(String utcTime, String predictHour) {
        if(utcTime.trim().isEmpty() || utcTime == null) {
            utcTime = getCurrentTime();
        }

        if(predictHour.trim().isEmpty() || predictHour == null) {
            predictHour = "24";
        }
        log.info("[{}][{}]", utcTime, predictHour);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("batchStartedTime", utcTime)
                .addString("predictHour", predictHour)
                .addLong("uniqueId", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(climateJobConfig.climateJob(), jobParameters);
            log.info("Batch job started successfully for parameters: {}", jobParameters);
        } catch (JobExecutionException e) {
            log.error("Job 수행 실패 cause : {}", e.getMessage());
            // 필요한 경우 여기에 추가적인 오류 처리 로직을 구현할 수 있습니다.
        }
    }
}
