package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.clients.HaltSwiftClient;
import com.elogist.vehicle_master_and_alert_creation.models.Halt;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert.MediumProcessingAlert29001;
import com.elogist.vehicle_master_and_alert_creation.models.dto.RawHaltData;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MedProcessAlert29001Service {

    @Autowired
    HaltSwiftClient haltSwiftClient;

    public static final Logger LOGGER = LoggerFactory.getLogger(MedProcessAlert29001Service.class);

    public boolean getAlertVerify(Integer vehId, Integer maxDayRunInHrs, Integer minDayHaltInMin, LocalDateTime tTime) {

        // list of benchmarks contains:
        // max run in minutes on 0th index
        Integer maxRunInMinutes = maxDayRunInHrs * 60;
        // min halt in minutes on 1st index
        Integer minHaltInMinutes = minDayHaltInMin;

        // getting halts generated in the last 4 hrs
        RawHaltData rawHalts;
        List<Halt> halts = new ArrayList<>();
        try{
            LocalDateTime dataStart = tTime.minusMinutes(maxRunInMinutes);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedStartTime = dataStart.format(formatter);
            String formattedEndTime = tTime.format(formatter);
            rawHalts = haltSwiftClient.getHalts(vehId.longValue(), formattedStartTime, formattedEndTime);
            halts = rawHalts.getData();
        } catch (Exception e) {
            LOGGER.info("Exception in fetching halts for vehId: {}", vehId);
        }

        // if there are no halts in this time
        if(halts == null || halts.size() == 0)
            return true;

        // if endtime is null then the vehicle is in halt
        if(halts.get(halts.size()-1).getEndTime() == null)
            return false;

        Halt satisfiedHalt = null;

        // iterating halts in reverse to find the latest halt with duration >= minHaltInMinutes
        for(int i=halts.size()-1;i>=0;i--) {

            Halt currHalt = halts.get(i);
            // if halt duration > minHaltInMinutes
            if(DateAndTime.timeDiffInMinutes(currHalt.getKey().getStartTime(),currHalt.getEndTime()) >= minHaltInMinutes) {
                satisfiedHalt = currHalt;
                break;
            }
        }

        if(satisfiedHalt != null) {
            return false;
        } else
            return true;
    }

//    public Halt getLastSufficientHalt(Integer vehId, LocalDateTime tTime, Integer minHaltInMinutes) {
//
//        Halt suffHalt = null;
//        // getting halts
//        RawHaltData rawHalts;
//        List<Halt> halts = new ArrayList<>();
//        try{
//            LocalDateTime dataStart = tTime.minusDays(1);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String formattedStartTime = dataStart.format(formatter);
//            String formattedEndTime = tTime.format(formatter);
//            rawHalts = haltSwiftClient.getHalts(vehId.longValue(), formattedStartTime, formattedEndTime);
//            halts = rawHalts.getData();
//        } catch (Exception e) {
//            LOGGER.info("Exception in fetching halts for vehId: {}", vehId);
//        }
//
//        if(halts.size() == 0) {
//            return null;
//        }
//
//        // if vehicle was on halt at tTime
//        if(halts.get(halts.size()-1).getEndTime() == null || halts.get(halts.size()-1).getEndTime().isAfter(tTime)) {
//            halts.get(halts.size()-1).setEndTime(tTime);
//        }
//
//        // iterating halts latest to old
//        for(int i=halts.size()-1; i>=0;i--) {
//
//            Halt currHalt = halts.get(i);
//            if(DateAndTime.timeDiffInMinutes(currHalt.getKey().getStartTime(),currHalt.getEndTime()) >= minHaltInMinutes) {
//                suffHalt = currHalt;
//                break;
//            }
//        }
//
//        return suffHalt;
//    }

}
