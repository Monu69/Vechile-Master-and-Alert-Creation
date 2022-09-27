package com.elogist.vehicle_master_and_alert_creation.controllers;

import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.dto.ATRawData;
import com.elogist.vehicle_master_and_alert_creation.models.dto.ATAlertMappingDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import com.elogist.vehicle_master_and_alert_creation.services.ATDataFetchService;
import com.elogist.vehicle_master_and_alert_creation.services.AlertEventsProcessIngService;
import com.elogist.vehicle_master_and_alert_creation.services.AlertProcessingService;
import com.elogist.vehicle_master_and_alert_creation.services.TestingService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AlertController {

   @Autowired
    private AlertProcessingService alertProcessingService;

   @Autowired
    AlertEventsProcessIngService alertEventsProcessIngService;

   @Autowired
    ATDataFetchService atDataFetchService;

   @Autowired
    TestingService testingService;

    @GetMapping("M1M2SimpleAlertCreation")
    public ResponseEntity<?> getDataFromMaster1() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException{
        List<FoEscalationTickets> foEscalationTickets =  alertProcessingService.getM1M2SimpleAlert();
        JsonResponse response = new JsonResponse(true, "Data From Master Table",foEscalationTickets);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("getATAlerts")
    public JsonResponse getATAlert(@RequestBody List<ATRawData> ATRawDataList, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) throws SQLException {
        List<ATAlertMappingDTO> atAlertMappingDTOS = new ArrayList<>();
        LocalDateTime sTime = DateAndTime.stringToLocalDateTime(startTime);
        LocalDateTime eTime = DateAndTime.stringToLocalDateTime(endTime);
        atAlertMappingDTOS = atDataFetchService.getATAlert(ATRawDataList, sTime, eTime);
        JsonResponse jsonResponse = new JsonResponse(true,"AT Alerts",atAlertMappingDTOS);
        return jsonResponse;
    }

    @GetMapping("testRespectiveAlert")
    public JsonResponse testAlert(@RequestParam("vehicleId") Integer vehicleId, @RequestParam("alertId") Integer alertId) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        Boolean result = testingService.testAlert(vehicleId, alertId);
        JsonResponse jsonResponse = new JsonResponse(true,"AT Alerts",result);
        return jsonResponse;
    }

    @GetMapping("testMediumProcessingAlert")
    public JsonResponse testMediumProcessingAlert(@RequestParam("vehicleId") Integer vehicleId, @RequestParam("alertId") Integer alertId) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        Boolean result = testingService.testMediumProcessingAlert(vehicleId, alertId);
        JsonResponse jsonResponse = new JsonResponse(true,"AT Alerts",result);
        return jsonResponse;
    }

}
