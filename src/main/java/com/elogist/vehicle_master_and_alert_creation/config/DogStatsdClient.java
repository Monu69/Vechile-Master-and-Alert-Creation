package com.elogist.vehicle_master_and_alert_creation.config;

import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DogStatsdClient {

    @Bean(name = "dataDogClient")
    public StatsDClient getStatsd() {
        return new NonBlockingStatsDClientBuilder()
                .prefix("vehicle-master-M1M2")
                .hostname("localhost")
                .port(8125)
                .build();
    }

}
