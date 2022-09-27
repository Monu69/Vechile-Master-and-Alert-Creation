package com.elogist.vehicle_master_and_alert_creation.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
