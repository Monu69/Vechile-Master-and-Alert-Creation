package com.elogist.vehicle_master_and_alert_creation.controllers;

import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoEscalationTicketsRepository;
import com.elogist.vehicle_master_and_alert_creation.services.*;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DemoController {

    @Autowired
    ATDataFetchService ATDataFetchService;

    @Autowired
    MedProcessAlert29001Service medProcessAlert29001Service;

    @Autowired
    CustomAlertServices customAlertServices;

    @Autowired
    AlertResolveService alertResolveService;

    @Autowired
    TestingService testingService;

    @Autowired
    M1M2SequenceService m1M2SequenceService;

    @Autowired
    FoEscalationTicketsRepository foEscalationTicketsRepository;

    @GetMapping("/testing2")
    public void populateKmsInVTStates1() throws SQLException {
        ATDataFetchService.showDetails();
    }

    @GetMapping("/testing1")
    public boolean checkContinuousAlert(@RequestParam Integer vehId) {

        List<Integer> benchmark = new ArrayList<>(List.of(240, 20));
        return false;
    }

    @GetMapping("/getMaintainanceAlert")
    public void getCustomAlert() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
      //  alertResolveService.getAlertResolved();
    }

    @GetMapping("/testGson")
    public void getData(){
//        FoEscalationTickets foEscalationTickets = new FoEscalationTickets(1006, 5014,-1000, "Demo entry", -1, -1, LocalDateTime.now(), false, -1, 1000, null, "[{\"vehicleId\":16421, \"startTime\":\"2022-04-30T21:19:09.583\"}]", "[{\"vehicleId\":16421, \"startTime\":\"2022-04-30T21:19:09.583\"}]");
//        foEscalationTicketsRepository.save(foEscalationTickets);
        m1M2SequenceService.m1m2Sequence();
    }

}
