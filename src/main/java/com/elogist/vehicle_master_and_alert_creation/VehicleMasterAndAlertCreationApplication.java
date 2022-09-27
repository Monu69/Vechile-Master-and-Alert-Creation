package com.elogist.vehicle_master_and_alert_creation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableCaching
@EnableEurekaClient
@EnableScheduling
@EnableFeignClients
public class VehicleMasterAndAlertCreationApplication {


	public static void main(String[] args) {
		//for checking
		System.out.println("checking");
		SpringApplication.run(VehicleMasterAndAlertCreationApplication.class, args);
	}

	@PostConstruct
	void started() throws Exception{
		// set JVM timezone as IST
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}

}
