package k_paas.balloon.keeper.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncThreadConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3); // 기본 스레드 수
        // TODO: 적정 Thread 및 Queue 사이즈 설정 필요
        taskExecutor.setMaxPoolSize(10); // 최대 스레드 수
        taskExecutor.setQueueCapacity(20); // Queue 사이즈
        taskExecutor.setThreadNamePrefix("Executor-"); // 스레드 접두사
        return taskExecutor;
    }

}
