package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert.MediumProcessingAlert;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.elogist.vehicle_master_and_alert_creation.utils.StringToClass;
import com.google.gson.Gson;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MedAlertProcessingService {

    @Autowired
    IssueBenchmarkService issueBenchmarkService;

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    AlertUtilityService alertUtilityService;

    @Autowired
    AlertProcessingService alertProcessingService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Lazy
    AsyncService asyncService;

    public static final Logger LOGGER = LoggerFactory.getLogger(MedAlertProcessingService.class);

    public List<FoEscalationTickets> getMedProcessingAlert(Map<Integer, MasterTableTemp1> M1M2Map, Boolean requiredM1) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        List<Integer> benchmarks;
        List<Class> classes;
        Boolean isM1 = null;
        List<FoEscalationTickets> foEscalationTicketsList = new ArrayList<>();

        List<Class<? extends MediumProcessingAlert>> mediumProcessingList = getMedProcessingAlertClassesList();

        Map<String,List<Integer>> alertVehicleRecipientMap = issueBenchmarkService.getAlertVehicleRecipients();

        Map<Integer, Map<Integer, List<Issues>>> matrix = issueBenchmarkService.getIssueBenchmark();

        for (Integer m1m2mapkey : M1M2Map.keySet()) {

            String key = m1m2mapkey + "#" + M1M2Map.get(m1m2mapkey).getVFo();
            if(alertVehicleRecipientMap.containsKey(key)) {
                List<Integer> foidList = alertVehicleRecipientMap.get(key);
                for (int k = 0; k < foidList.size(); k++) {
                    if (matrix.get(foidList.get(k)) != null) {

                        // TODO: write the code for single instead of getting all the set
                        // keysOfFoId id propertyType (foIssueTypeId in DB)
                        Set<Integer> keysOfFoId = matrix.get(foidList.get(k)).keySet();

                        List<Integer> medAlertIssueType = new ArrayList<>();
                        for (Integer alert : keysOfFoId) {
                            medAlertIssueType.add(alert);
                        }

                        classes = getCommonAlertClass(keysOfFoId, mediumProcessingList);

                        for (int i = 0; i < medAlertIssueType.size(); i++) {

                            String mediumAlert = "com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert." + "MediumProcessingAlert" + medAlertIssueType.get(i);
                            Class mediumAlertClass = StringToClass.getClassName(mediumAlert);

                            if (mediumAlertClass != null && mediumProcessingList.contains(mediumAlertClass)) {
                                MediumProcessingAlert alrtClassObj = (MediumProcessingAlert) mediumAlertClass.getDeclaredConstructor().newInstance();
                                applicationContext.getAutowireCapableBeanFactory().autowireBean(alrtClassObj);
                                Integer alertId = alrtClassObj.getAlertTypeId();
                                List<Issues> alert = matrix.get(foidList.get(k)).get(alertId);

                                for (Issues issue : alert) {

                                    Integer foId = foidList.get(k);

                                        asyncService.getMediumAlert(M1M2Map, issue, alrtClassObj, requiredM1, foId, m1m2mapkey);

                                }
                            }
//
                        }
                    }

                }
            }
        }
        return foEscalationTicketsList;
    }

    public void mediumAlertProcessing(Map<Integer, MasterTableTemp1> M1M2Map, Issues issue, MediumProcessingAlert alrtClassObj, Boolean requiredM1, Integer m1m2mapkey, Integer foId){

        Boolean isM1;

        try {
            Boolean resultForM1 = isIssueConstraintValid((Master) M1M2Map.get(m1m2mapkey), issue);
            Boolean resultForM2 = isIssueConstraintValid((Master) M1M2Map.get(m1m2mapkey).getMasterTableTemp2(), issue);
            if (resultForM1 && resultForM2) {
                isM1 = alrtClassObj.isValidAlert(M1M2Map.get(m1m2mapkey), issue);
                if (isM1 != null && isM1 == requiredM1) {
                    MasterTableTemp1 masterTableTemp1 = M1M2Map.get(m1m2mapkey);
                    Alerts.SpecificParameters specifiedParameter = alrtClassObj.getSpecifiedParameter();
                    String generalParameter = masterTableTemp1Repository.getGeneralParameter(M1M2Map.get(m1m2mapkey).getVId());
                    Alerts.Benchmarks benchmarks = issue.getFinalBenchmark(alrtClassObj);
                    FoEscalationTickets foEscalationTickets = new FoEscalationTickets(M1M2Map.get(m1m2mapkey), alrtClassObj, issue, foId, alertProcessingService.getDeserializeSpecificParam(specifiedParameter), generalParameter, alertProcessingService.getDeseriliazeBenchmarks(benchmarks));
                    alertProcessingService.saveIntoFoEscalationTickets(foEscalationTickets);
                }
            }
        } catch (Exception e) {
            LOGGER.info(alrtClassObj.getAlertTypeId() + " " + M1M2Map.get(m1m2mapkey).getVId() + " message: " + e.getMessage());
        }
    }

    public List<Class<? extends MediumProcessingAlert>> getMedProcessingAlertClassesList() {

        Reflections reflections = new Reflections("com.elogist.vehicle_master_and_alert_creation.models", new SubTypesScanner(false));
        Set<Class<? extends MediumProcessingAlert>> classes = reflections.getSubTypesOf(MediumProcessingAlert.class);
        List<Class<? extends MediumProcessingAlert>> medProcessingAlertList = new ArrayList<>();

        for (Class<? extends MediumProcessingAlert> singleClass : classes)
            medProcessingAlertList.add(singleClass);

        return medProcessingAlertList;
    }

    public List<Class> getCommonAlertClass(Set<Integer> keysOfFoid, List<Class<? extends MediumProcessingAlert>> medProcessClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        List<Class> classes = new ArrayList<>();
        for (int i = 0; i < medProcessClass.size(); i++) {

            Constructor<?> constructor = medProcessClass.get(i).getConstructor();
            MediumProcessingAlert mediumProcessingAlert = (MediumProcessingAlert) constructor.newInstance(new Object[]{});
            if (keysOfFoid.contains(mediumProcessingAlert.getAlertTypeId())) {
                classes.add(medProcessClass.get(i));
            }
        }
        return classes;
    }

    public static Boolean isIssueConstraintValid(Master master, Issues issuesList) {

        Boolean isContinue;
        isContinue = checkForVehicle(master.getVId(), issuesList.getVehicles());

        if (isContinue)
            isContinue = checkForGroups(master.getVgGrpId(), issuesList.getGroups());

        if (isContinue)
            isContinue = checkForGroups(master.getHlSiteId(), issuesList.getSources());

        if (isContinue)
            isContinue = checkForGroups(master.getPSiteId(), issuesList.getDestinations());

        return isContinue;
    }

    public static Boolean checkForVehicle(Integer vId, List<Integer> vehicles) {
        if (vehicles.size() > 0) {
            return vehicles.contains(vId);
        } else {
            return true;
        }
    }

    public static Boolean checkForGroups(List<Integer> items, List<Integer> groups) {
        if (groups.size() > 0) {
            items = items == null? new ArrayList<>() : items;
            for (Integer res : items) {
                if (groups.contains(res)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

}
