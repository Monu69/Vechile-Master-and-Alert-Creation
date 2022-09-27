package com.elogist.vehicle_master_and_alert_creation.config;

import com.elogist.vehicle_master_and_alert_creation.models.dto.ParseStat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RefreshScope
public class AppConfig {

    @Value("${alertKafka.server.threads}")
    private String numberOfHandlerThreads;

    @Value("${alertKafka.server.max.threads}")
    private String numberOfMaxHandlerThreads;

    @Value("${alertKafka.server.queue.capacity}")
    private String threadPoolQueueCapacity;

    @Value("${alertKafka.server.thread.name.prefix}")
    private String threadNamePrefix;

    @Value("${alertKafka.threads}")
    private String numberOfKafkaHandlerThreads;

    @Value("${alertKafka.max.threads}")
    private String numberOfMaxKafkaHandlerThreads;

    @Value("${alertKafka.queue.capacity}")
    private String kafkaThreadPoolQueueCapacity;

    @Value("${alertKafka.thread.name.prefix}")
    private String kafkaHandlerThreadNamePrefix;

    @Bean(name = "serverThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(numberOfHandlerThreads));
        executor.setMaxPoolSize(Integer.parseInt(numberOfMaxHandlerThreads));
        executor.setQueueCapacity(Integer.parseInt(threadPoolQueueCapacity));
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    @Bean(name = "kafkaThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor kafkaTaskExecutor() {
        return getThreadPoolTaskExecutorForKafka(kafkaHandlerThreadNamePrefix);
    }

    private ThreadPoolTaskExecutor getThreadPoolTaskExecutorForKafka(String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(numberOfKafkaHandlerThreads));
        executor.setMaxPoolSize(Integer.parseInt(numberOfMaxKafkaHandlerThreads));
        executor.setQueueCapacity(Integer.parseInt(kafkaThreadPoolQueueCapacity));
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(false);
        return executor;
    }

    @Bean(name = "kafkaThreadPoolTaskExecutorNew")
    public ThreadPoolTaskExecutor kafkaTaskExecutorNew() {
        return getThreadPoolTaskExecutorForKafka(kafkaHandlerThreadNamePrefix);
    }

    @Bean(name = "sendCommandPoolTaskExecutorNew")
    public ThreadPoolTaskExecutor sendCommandExecutorNew() {
        return getThreadPoolTaskExecutorForKafka(kafkaHandlerThreadNamePrefix);
    }

//    @Bean(name = "executorService")
//    public ExecutorService executorService(){
//        //This creates the fixed thread pool for the main server thread
//        return Executors.newFixedThreadPool(10);
//    }

    @Bean
    public ParseStat getParseStat(){
        ParseStat parseStat = new ParseStat(0l,0l,0l);
        return parseStat;
    }
}
