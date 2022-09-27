package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.clients.HaltSwiftClient;
import com.elogist.vehicle_master_and_alert_creation.models.Halt;
import com.elogist.vehicle_master_and_alert_creation.models.dto.RawHaltData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeignService {

    @Autowired
    HaltSwiftClient haltSwiftClient;

    public static final Logger LOGGER = LoggerFactory.getLogger(FeignService.class);

    public List<Halt> getHalts(Long vehId, LocalDateTime startTime, LocalDateTime endTime) {

        RawHaltData rawHalts;
        List<Halt> halts = new ArrayList<>();
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedStartTime = startTime.format(formatter);
            String formattedEndTime = endTime.format(formatter);
            rawHalts = haltSwiftClient.getHalts(vehId.longValue(), formattedStartTime, formattedEndTime);
            halts = rawHalts.getData();
        } catch (Exception e) {
            LOGGER.error("Exception in fetching halts for vehId: {}, msg: {}", vehId, e.getMessage());
        }

        if(halts == null)
            halts = new ArrayList<>();

        return halts;
    }
}
