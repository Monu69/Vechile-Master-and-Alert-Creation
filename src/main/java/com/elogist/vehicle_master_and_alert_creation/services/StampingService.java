package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StampingService {

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    StatsDClient dataDogClient;

    public static final Logger LOGGER = LoggerFactory.getLogger(StampingService.class);


    @Scheduled(cron = "${elogist.stampingService}")
    public void stampingService(){

        LocalDateTime startTime = LocalDateTime.now();

        Long timeDiffInSec = 0l;

        String resultKey = null;

        try {

            LOGGER.info("Stamping service started-------> ");

            masterTableTemp1Repository.stampedTrips();

            resultKey = "Success";

            LOGGER.info("Stamping service ended-------> ");
        }
        catch (Exception e){

            resultKey = "Failed";

            LOGGER.error("Stamping Service Error---->" + e.getMessage());

        }
        finally {

            LocalDateTime endTime = LocalDateTime.now();

            timeDiffInSec = DateAndTime.getSecDifference(startTime, endTime);

            dataDogClient.time("StampingServiceElapsedTime", timeDiffInSec, "Result:" + resultKey);
        }

    }
}
