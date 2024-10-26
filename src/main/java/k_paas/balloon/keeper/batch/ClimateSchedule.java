package k_paas.balloon.keeper.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@EnableScheduling
@Component
public class ClimateSchedule {

    private final JobLauncher jobLauncher;
    private final ClimateJobConfig climateJobConfig;

    public ClimateSchedule(JobLauncher jobLauncher, ClimateJobConfig climateJobConfig) {
        this.jobLauncher = jobLauncher;
        this.climateJobConfig = climateJobConfig;
    }

    /**
     * 매일 6시간 간격 10분에 작업을 수행 UTC 기준
     */
    @Scheduled(cron = "0 30 */6 * * *", zone = "UTC" )
    public void execute() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("batchStartedTime", getCurrentTime())
                .addString("predictHour", "24")
                .addLong("uniqueId", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(climateJobConfig.climateJob(), jobParameters);
        } catch (JobExecutionException e) {
            log.error("Job 수행 실패 cause : {}", e.getMessage());
        }
    }

    private static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        ZoneId utcZone = ZoneId.of("UTC");

        ZonedDateTime now = ZonedDateTime.now(utcZone);
        log.info("raw timestamp = {}", now.format(formatter));

        return now.minusHours(24).format(formatter);
    }
}
