package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.AEHAPI30573AE1;
import com.elogist.vehicle_master_and_alert_creation.models.dto.TripEndEventDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleKpisColumnsDTO;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.G2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Slf4j
public class TripEndEventService {

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    AsyncService asyncService;

    @Autowired
    StatsDClient dataDogClient;

    @Autowired
    ApplicationContext applicationContext;

    public static final Logger LOGGER = LoggerFactory.getLogger(TripEndEventService.class);

    public void tripEndEvent(String tripIds) {

        try {

            List<TripEndEventDTO> tripEndEventDTOList = getTripEndEventDTO(tripIds);

            List<Future<Void>> tripEnsEventFutureList= new ArrayList<>();

            if(tripEndEventDTOList != null && tripEndEventDTOList.size() > 0) {

                for (TripEndEventDTO tripEndEventDTO : tripEndEventDTOList) {

                    MasterTableTemp1 masterTableTemp1 = new MasterTableTemp1();
                    masterTableTemp1.setVtId(tripEndEventDTO.getVtId());
                    masterTableTemp1.setVId(tripEndEventDTO.getVehicleId());
                    masterTableTemp1.setVtStartTime(DateAndTime.stringToLocalDateTime(tripEndEventDTO.getStartTime()));
                    masterTableTemp1.setVsStartTime(DateAndTime.stringToLocalDateTime(tripEndEventDTO.getEndTime()));

                    Future<Void> result = asyncService.getTripEndEvent(masterTableTemp1);
                    tripEnsEventFutureList.add(result);

                }

                for(int i=0; i<tripEnsEventFutureList.size(); i++){
                    tripEnsEventFutureList.get(i).get();
                }
            }

        }
        catch (Exception e){

            LOGGER.error("TripEndEvent Excepetion---->" + e.getMessage());

        }
    }

    public List<TripEndEventDTO> getTripEndEventDTO(String tripIds){

        String response = masterTableTemp1Repository.getTripEndEventList(tripIds);

        Gson gson = new Gson();
        Type type1 = new TypeToken<List<TripEndEventDTO>>() {}.getType();

        List<TripEndEventDTO> tripEndEventDTOList = gson.fromJson(response, type1);

        return tripEndEventDTOList;

    }

    @Scheduled(fixedDelayString = "${elogist.JKLCTripEndEvent}")
    public void JKLCEndEvent(){

        LocalDateTime startTime = null;
        String resultKey = "";

        try {

            LOGGER.info("JKLCtripEndEvent Scheduled Service Started----->");

            startTime = LocalDateTime.now();

            tripEndEvent("");

            resultKey = "Success";

            LOGGER.info("JKLCTripEndEvent Scheduled Service Ended----->");

        }
        catch (Exception e){

            resultKey = "Success";

            LOGGER.error("JKLCTripEndEventExcepetion----->" + e.getMessage());

        }
        finally {

            LocalDateTime endTime = LocalDateTime.now();

            Long totalTimeInSec = DateAndTime.getSecDifference(startTime, endTime);

            dataDogClient.time("JKLCTripEndEvent", totalTimeInSec, "Result:" + resultKey);

        }

    }



}
