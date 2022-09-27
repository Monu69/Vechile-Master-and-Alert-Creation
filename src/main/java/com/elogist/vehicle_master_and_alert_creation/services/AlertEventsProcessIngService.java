package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.FoAlertEvents;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.AlertEventHandler;
import com.elogist.vehicle_master_and_alert_creation.models.alertevents.AlertsEvents;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert.SimpleAlert;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoAlertEventsRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoAlertEventsTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.M;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class AlertEventsProcessIngService {

    @Autowired
    FoAlertEventsRepository foAlertEventsRepository;

    @Autowired
    AlertProcessingService alertProcessingService;

    @Autowired
    AlertUtilityService alertUtilityService;

    @Autowired
    IssueBenchmarkService issueBenchmarkService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    @Lazy
    AsyncService asyncService;

    @Scheduled(cron = "${elogist.alertEventProcessing}")
    public void alertEventProcessing() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        log.info("Started alertEventProcessing "+ LocalDateTime.now());

        Map<Integer, MasterTableTemp1> M1M2Map = alertProcessingService.getM1M2Intersection();

        log.info("Fetched M1M2 alertEventProcessing "+ LocalDateTime.now());

        List<Class<? extends AlertsEvents>> alertEventsClass = getSimpleAlertEventsClassesList();

        List<Class<? extends AlertEventHandler>> alertEventsHandlerClass = getSimpleAlertEventsHandlerClassesList();

        Map<Integer, List<FoAlertEvents>> foEventsMap = new HashMap<>();

        Boolean result = false, isTriggered = false;

        Map<String, List<Integer>> alertVehicleRecipientMap = issueBenchmarkService.getAlertVehicleRecipients();

        foEventsMap = getFoAlertEventsMap();

        for (Integer res1 : M1M2Map.keySet()) {

            String key = res1 + "#" + M1M2Map.get(res1).getVFo();

            if (alertVehicleRecipientMap.containsKey(key)) {

                List<Integer> foidList = alertVehicleRecipientMap.get(key);

                for (int k = 0; k < foidList.size(); k++) {

                    if (foEventsMap.get(foidList.get(k)) != null) {

                        for (int i = 0; i < foEventsMap.get(foidList.get(k)).size(); i++) {

                            FoAlertEvents foAlertEvents = foEventsMap.get(foidList.get(k)).get(i);

                            asyncService.getAlertEvent(foAlertEvents, M1M2Map, alertEventsClass, alertEventsHandlerClass, res1);

                        }

                    }
                }
            }
        }
        log.info("Ended alertEventProcessing "+ LocalDateTime.now());

    }


    public void alertEventProcessing(FoAlertEvents foAlertEvents, Map<Integer, MasterTableTemp1> M1M2Map, List<Class<? extends AlertsEvents>> alertEventsClass, List<Class<? extends AlertEventHandler>> alertEventsHandlerClass, Integer res1) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Boolean isTriggered;
        Boolean result = false;
        String eventClass = "com.elogist.vehicle_master_and_alert_creation.models.alertevents." + "AlertsEvent" + foAlertEvents.getFoAlertEventTypeId();
        Class eventCls = Class.forName(eventClass);
        if (alertEventsClass.contains(eventCls)) {
            AlertsEvents alertsEventsObj = (AlertsEvents) eventCls.getDeclaredConstructor().newInstance();
            result = alertsEventsObj.isValidEvent(M1M2Map.get(res1), M1M2Map.get(res1).getMasterTableTemp2());
        }
        if (result) {
            String eventHandlerClass = "com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo" + foAlertEvents.getFoId() + "." + "AEH" + foAlertEvents.getHandleType() + foAlertEvents.getFoId() + "AE" + foAlertEvents.getFoAlertEventTypeId();
            Class handleCls = Class.forName(eventHandlerClass);
            if (alertEventsHandlerClass.contains(handleCls)) {
                AlertEventHandler alertEventHandlerObj = (AlertEventHandler) handleCls.getDeclaredConstructor().newInstance();
                applicationContext.getAutowireCapableBeanFactory().autowireBean(alertEventHandlerObj);
                isTriggered = alertEventHandlerObj.isTriggered(M1M2Map.get(res1));
            }
        }
    }




    public List<Class<? extends AlertsEvents>> getSimpleAlertEventsClassesList(){
        Reflections reflections = new Reflections("com.elogist.vehicle_master_and_alert_creation.models", new SubTypesScanner(false));
        Set<Class<? extends AlertsEvents>> classes = reflections.getSubTypesOf(AlertsEvents.class);
        List<Class<? extends AlertsEvents>> alertEventsList = new ArrayList<>();
        for(Class<? extends AlertsEvents> clas : classes) {
            alertEventsList.add(clas);
        }
        return alertEventsList;
    }

    public List<Class<? extends AlertEventHandler>> getSimpleAlertEventsHandlerClassesList(){
        Reflections reflections = new Reflections("com.elogist.vehicle_master_and_alert_creation.models", new SubTypesScanner(false));
        Set<Class<? extends AlertEventHandler>> classes = reflections.getSubTypesOf(AlertEventHandler.class);
        List<Class<? extends AlertEventHandler>> alertEventsHandlerList = new ArrayList<>();
        for(Class<? extends AlertEventHandler> clas : classes) {
            alertEventsHandlerList.add(clas);
        }
        return alertEventsHandlerList;

    }


    public List<Class> getCommonAlertEventsClass(List<FoAlertEvents> foAlertEventsList ,List<Class<? extends AlertsEvents>> res) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Class> items = new ArrayList<>();
        List<Integer> foAlertEventTypeId = new ArrayList<>();
        for(FoAlertEvents foAlertEvents : foAlertEventsList){
            foAlertEventTypeId.add(foAlertEvents.getFoAlertEventTypeId());
        }
        for (int i = 0; i < res.size(); i++) {

            Constructor<?> ctor = res.get(i).getConstructor();
            AlertsEvents alertsEvents = (AlertsEvents)ctor.newInstance(new Object[] { });
            if (foAlertEventTypeId.contains(alertsEvents.getEventTypeId())) {
                items.add(res.get(i));
            }
        }
        return items;
    }

    public Map<Integer, List<FoAlertEvents>> getFoAlertEventsMap(){
        List<FoAlertEvents> foAlertEventsList = foAlertEventsRepository.getFoAlertEventsData();
        Map<Integer, List<FoAlertEvents>> foAlertEventsMap = new HashMap<>();
        for(FoAlertEvents foAlertEvents : foAlertEventsList){
            if(foAlertEventsMap.containsKey(foAlertEvents.getFoId())){
                foAlertEventsMap.get(foAlertEvents.getFoId()).add(foAlertEvents);
            }
            else{
                List<FoAlertEvents> foAlertEventsList1 = new ArrayList<>();
                foAlertEventsList1.add(foAlertEvents);
                foAlertEventsMap.put(foAlertEvents.getFoId(), foAlertEventsList1);
            }
        }
        return foAlertEventsMap;
    }

}
