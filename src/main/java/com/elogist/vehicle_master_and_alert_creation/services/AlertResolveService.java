package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoEscalationTicketsRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoTicketPropertiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

@Service
@Slf4j
public class AlertResolveService {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    FoEscalationTicketsRepository foEscalationTicketsRepository;

    @Autowired
    FoTicketPropertiesRepository foTicketPropertiesRepository;

    @Autowired
    IssueBenchmarkService issueBenchmarkService;

    @Autowired
    AlertUtilityService alertUtilityService;

    @Autowired
    AlertProcessingService alertProcessingService;

    @Scheduled(cron = "${elogist.alertResolve}")
    public void getAlertResolved() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Boolean result;
        Boolean isResolve = false;

        Alerts alertClassObj = null;

        Map<Integer, MasterTableTemp1> M1M2Map = alertProcessingService.getM1M2Intersection();

        List<FoEscalationTickets> foEscalationTicketsList = new ArrayList<>();

        Map<Integer, FoEscalationTickets> issueIdmap = new HashMap<>();

        Map<Integer, Issues> issuesMap = new HashMap<>();

        List<FoEscalationTickets> unresolvedAlerts = foEscalationTicketsRepository.getAlertsType();

        for (int i = 0; i < unresolvedAlerts.size(); i++) {
            if (issueIdmap.containsKey(unresolvedAlerts.get(i).getIssuePropertiesId())) {
                continue;
            } else {
                issueIdmap.put(unresolvedAlerts.get(i).getIssuePropertiesId(), unresolvedAlerts.get(i));
            }
        }

        Set<Integer> issueList = issueIdmap.keySet();

        issuesMap = getAlertIssues(issueList);

        Reflections reflections = new Reflections("com.elogist.vehicle_master_and_alert_creation.models", new SubTypesScanner(false));
        Set<Class<? extends Alerts>> classes = reflections.getSubTypesOf(Alerts.class);
        Map<Integer, Alerts> existingAlerts = new HashMap<>();

        for (Class item : classes) {

            if (!Modifier.isAbstract(item.getModifiers())) {
                alertClassObj = (Alerts) item.getDeclaredConstructor().newInstance();
                applicationContext.getAutowireCapableBeanFactory().autowireBean(alertClassObj);
                existingAlerts.put(alertClassObj.getAlertTypeId(), alertClassObj);
            }
        }

        Map<Integer, Map<Integer, List<Issues>>> matrix = issueBenchmarkService.getIssueBenchmark();


        for (int i = 0; i < unresolvedAlerts.size(); i++) {
            try {

                if (M1M2Map.get(unresolvedAlerts.get(i).getVehicleId()) != null && matrix.get(M1M2Map.get(unresolvedAlerts.get(i).getVehicleId()).getFoid()) != null) {

                    if (existingAlerts.containsKey(unresolvedAlerts.get(i).getFoIssueTypeId())) {
                        isResolve = existingAlerts.get(i).isResolved(M1M2Map.get(unresolvedAlerts.get(i).getVehicleId()), issuesMap.get(unresolvedAlerts.get(i).getIssuePropertiesId()));
                    } else {
                        isResolve = true;
                    }
                } else {
                    isResolve = true;
                }

                if (isResolve) {
                    unresolvedAlerts.get(i).setIsAutoResolvable(true);
                    foEscalationTicketsList.add(unresolvedAlerts.get(i));
                }

            } catch (Exception e) {
                log.error("Alert Resolve exception---->" + alertClassObj.getAlertTypeId() + "  " + unresolvedAlerts.get(i).getVehicleId());
            }
        }

        alertProcessingService.updateFoEscalation(foEscalationTicketsList);


    }

    public Map<Integer, Issues> getAlertIssues(Set<Integer> issueList) {

        Map<Integer, Issues> issuesMap = new HashMap<>();
        List<TicketProperties> ticketPropertiesList = foTicketPropertiesRepository.getTicketProperties(issueList);

        Map<String, String> issueConstraints = alertUtilityService.getIssueConstraints();

        Map<Integer, IssueConstraints> issueConstraintsMap = issueBenchmarkService.getIssueConstraints(issueConstraints);


        for (int i = 0; i < ticketPropertiesList.size(); i++) {

            IssueConstraints issueConstraints1 = issueConstraintsMap.get(ticketPropertiesList.get(i).getIssueConstraintId());
            Issues issues = new Issues(ticketPropertiesList.get(i), issueConstraints1);
            issuesMap.put(ticketPropertiesList.get(i).getId(), issues);
        }

        return issuesMap;
    }

}
