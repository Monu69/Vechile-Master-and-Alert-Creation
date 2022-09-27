package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.customalert.DeviationAlert;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.customalert.DocumentationAlert;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.customalert.MaintainanceAlert;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoEscalationTicketsRepository;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomAlertServices {

    @Autowired
    FoEscalationTicketsRepository foEscalationTicketsRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    AlertProcessingService alertProcessingService;

    @Autowired
    StatsDClient dataDogClient;

    public static final Logger LOGGER = LoggerFactory.getLogger(CustomAlertServices.class);


    @Scheduled(cron = "${elogist.customAlert}")
    public void getCustomAlert(){
        try {
            LOGGER.info("Custom Alert Service Started------>");

            LocalDateTime customAlertStartTime = LocalDateTime.now();

            List<FoEscalationTickets> foEscalationTickets = new ArrayList<>();
            List<FoEscalationTickets> foEscalationTicketsList3 = new ArrayList<>();
            List<FoEscalationTickets> foEscalationTicketsList1 = new ArrayList<>();

            MaintainanceAlert maintainanceAlert = new MaintainanceAlert();
            DocumentationAlert documentationAlert = new DocumentationAlert();
            DeviationAlert deviationAlert = new DeviationAlert();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(maintainanceAlert);
            applicationContext.getAutowireCapableBeanFactory().autowireBean(documentationAlert);
            applicationContext.getAutowireCapableBeanFactory().autowireBean(deviationAlert);
            try {
                foEscalationTicketsList3 = deviationAlert.getAlerts();
            }
            catch (Exception e){
                LOGGER.error("Documentation Alert Custom Exception---->" + e.getMessage());
            }

            try {
                foEscalationTicketsList1 = documentationAlert.getAlert();
            }
            catch (Exception e){
                LOGGER.error("Deviation Alert Custom Exception---->" + e.getMessage());
            }

            List<FoEscalationTickets> foEscalationTicketsList2 = new ArrayList<>();//maintainanceAlert.getAlerts();
            foEscalationTickets = Stream.concat(Stream.concat(foEscalationTicketsList1.stream(), foEscalationTicketsList2.stream()), foEscalationTicketsList3.stream()).collect(Collectors.toList());
            alertProcessingService.saveIntoFoEscalation(foEscalationTickets);

            LocalDateTime customAlertEndTime = LocalDateTime.now();

            Long actualTimeInSec = DateAndTime.getSecDifference(customAlertStartTime, customAlertEndTime);

            LOGGER.info("Custom Alert Service Ended------>");

            dataDogClient.time("M1M2CustomAlert", actualTimeInSec, "");
        }
        catch (Exception e){
            LOGGER.error("CustomAlertExcepetion------>" + e.getMessage());
        }
    }
}
