package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.clients.HaltSwiftClient;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.M1M2Sequence;
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
public class M1M2SequenceService {

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    HaltSwiftClient haltSwiftClient;

    @Autowired
    StatsDClient dataDogClient;

    public static final Logger LOGGER = LoggerFactory.getLogger(M1M2SequenceService.class);


    @Scheduled(cron = "${elogist.M1M2Sequence}")
    public void m1m2Sequence(){
        LocalDateTime addTime = null;
        LocalDateTime endTimeMasterVehicle = null;
        LocalDateTime endTimeAutoHalt = null;
        LocalDateTime endTimeAutoRule = null;
        Long actualTimeInSec = 0l;
        String resultKey = null;


        try {

            LOGGER.info("M1M2Sequence M1M2DataSP Started----->");

            addTime = LocalDateTime.now().withNano(0);;
            Integer response = masterTableTemp1Repository.getM1M2Data();

            resultKey = "Success";
//            Thread.sleep(60000);

            LOGGER.info("M1M2Sequence M1M2DataSP Ended----->");

        }
        catch (Exception ex){

            resultKey = "Failed";

            LOGGER.error("M1M2Sequence M1M2DataSP Error----->" + ex.getMessage());

        }
        finally {

            endTimeMasterVehicle = LocalDateTime.now().withNano(0);

            actualTimeInSec = DateAndTime.getSecDifference(addTime,endTimeMasterVehicle);

            dataDogClient.time("M1M2DataSP", actualTimeInSec, "Result:" + resultKey);

            masterTableTemp1Repository.updateTime(M1M2Sequence.MASTER_VEHICLES_ENGINE.getDescription(), addTime, endTimeMasterVehicle);

        }

        try{

            LOGGER.info("M1M2Sequence AutoHaltJava Started----->");

            String response = haltSwiftClient.getAutoHalt(10);

            resultKey = "Success";

            LOGGER.info("M1M2Sequence AutoHaltJava Ended----->");

        }
        catch (Exception ex){

            resultKey = "Failed";

            LOGGER.error("M1M2Sequence AutoHalt Error----->" + ex.getMessage());

        }
        finally {

            endTimeAutoHalt = LocalDateTime.now().withNano(0);

            actualTimeInSec = DateAndTime.getSecDifference(endTimeMasterVehicle, endTimeAutoHalt);

            dataDogClient.time("AutoHaltJava", actualTimeInSec, "Result:" + resultKey);

            masterTableTemp1Repository.updateTime(M1M2Sequence.HALTS_GENERATION.getDescription(), endTimeMasterVehicle, endTimeAutoHalt);

        }

        try{

            LOGGER.info("M1M2Sequence AutoHaltReviewSP Started----->");

            Integer response = masterTableTemp1Repository.getAutoHaltReview();

            resultKey = "Success";

            LOGGER.info("M1M2Sequence AutoHaltReviewSP Ended----->");


        }
        catch (Exception ex){

            resultKey = "Failed";

            LOGGER.error("M1M2Sequence AutoHaltReviewSP Error----->" + ex.getMessage());

        }
        finally {

            endTimeAutoRule = LocalDateTime.now().withNano(0);

            actualTimeInSec = DateAndTime.getSecDifference(endTimeAutoHalt, endTimeAutoRule);

            dataDogClient.time("AutoHaltReviewSP", actualTimeInSec, "Result:" + resultKey);

            masterTableTemp1Repository.updateTime(M1M2Sequence.AUTO_RULES_ENGINE.getDescription(), endTimeAutoHalt, endTimeAutoRule);

        }

    }

    @Scheduled(fixedDelayString = "${elogist.DataDogMetrix}")
    public void sendToDataDog(){

        try {

            LOGGER.info("DataDog scheduled service started ");

            for (Integer key : AlertProcessingService.alertsMap.keySet()) {
                dataDogClient.count("AlertCreation.count", AlertProcessingService.alertsMap.get(key), "Alert:" + key);
                AlertProcessingService.alertsMap.put(key, 0);
            }

            LOGGER.info("DataDog scheduled service ended");
        }
        catch (Exception ex){
            LOGGER.error("DataDog error---->" + ex.getMessage());
        }

    }
}
