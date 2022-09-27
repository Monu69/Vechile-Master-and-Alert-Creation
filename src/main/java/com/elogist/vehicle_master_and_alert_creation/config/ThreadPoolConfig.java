package com.elogist.vehicle_master_and_alert_creation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${alert.server.threads}")
    private String numberOfThreads;

    @Value("${alert.server.max.threads}")
    private String numberOfMaxThreads;

    @Value("${alert.server.queue.capacity}")
    private String threadPoolQueueCapacity;

    @Value("${tripEndEvent.server.threads}")
    private String tripEndEventNumberOfThreads;

    @Value("${tripEndEvent.server.max.threads}")
    private String tripEndEventNumberOfMaxThreads;

    @Value("${tripEndEvent.server.queue.capacity}")
    private String tripEndEventThreadPoolQueueCapacity;



    @Bean(name = "tripEndEventThreadPool")
    public ThreadPoolTaskExecutor tripEndEventThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(Integer.parseInt(tripEndEventNumberOfThreads));
        threadPoolTaskExecutor.setMaxPoolSize(Integer.parseInt(tripEndEventNumberOfMaxThreads));
        threadPoolTaskExecutor.setQueueCapacity(Integer.parseInt(tripEndEventThreadPoolQueueCapacity));
        threadPoolTaskExecutor.initialize();
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        return threadPoolTaskExecutor;
    }

    @Bean(name = "simpleAlertThreadPool")
    public ThreadPoolTaskExecutor nightDrivingThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(Integer.parseInt(numberOfThreads));
        threadPoolTaskExecutor.setMaxPoolSize(Integer.parseInt(numberOfMaxThreads));
        threadPoolTaskExecutor.setQueueCapacity(Integer.parseInt(threadPoolQueueCapacity));
        threadPoolTaskExecutor.initialize();
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        return threadPoolTaskExecutor;
    }

    @Bean(name = "mediumAlertThreadPool")
    public ThreadPoolTaskExecutor overSpeedThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(Integer.parseInt(numberOfThreads));
        threadPoolTaskExecutor.setMaxPoolSize(Integer.parseInt(numberOfMaxThreads));
        threadPoolTaskExecutor.setQueueCapacity(Integer.parseInt(threadPoolQueueCapacity));
        threadPoolTaskExecutor.initialize();
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        return threadPoolTaskExecutor;
    }

    @Bean(name = "alertEventThreadPool")
    public ThreadPoolTaskExecutor stoppageThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(Integer.parseInt(numberOfThreads));
        threadPoolTaskExecutor.setMaxPoolSize(Integer.parseInt(numberOfMaxThreads));
        threadPoolTaskExecutor.setQueueCapacity(Integer.parseInt(threadPoolQueueCapacity));
        threadPoolTaskExecutor.initialize();
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        return threadPoolTaskExecutor;
    }
}
