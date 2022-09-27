package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.FoAlertEvents;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.AlertEventHandler;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.AEHAPI30573AE1;
import com.elogist.vehicle_master_and_alert_creation.models.alertevents.AlertsEvents;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert.MediumProcessingAlert;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert.SimpleAlert;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import org.geolatte.geom.M;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class AsyncService {

    @Autowired
    AlertProcessingService alertProcessingService;

    @Autowired
    MedAlertProcessingService medAlertProcessingService;

    @Autowired
    AlertEventsProcessIngService alertEventsProcessIngService;

    @Autowired
    ApplicationContext applicationContext;

    @Async("alertEventThreadPool")
    public void getAlertEvent(FoAlertEvents foAlertEvents, Map<Integer, MasterTableTemp1> M1M2Map, List<Class<? extends AlertsEvents>> alertEventsClass, List<Class<? extends AlertEventHandler>> alertEventsHandlerClass, Integer res1) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        alertEventsProcessIngService.alertEventProcessing(foAlertEvents, M1M2Map, alertEventsClass, alertEventsHandlerClass, res1);

    }

    @Async("simpleAlertThreadPool")
    public void getSimpleAlert(Map<Integer, MasterTableTemp1> M1M2Map, Issues issueElement, SimpleAlert alertClassObj, Boolean requiredM1, Boolean requiredM2, Integer foId, Integer res1){

        alertProcessingService.simpleAlertProcessing(M1M2Map, issueElement, alertClassObj, requiredM1, requiredM2, foId, res1);

    }

    @Async("mediumAlertThreadPool")
    public void getMediumAlert(Map<Integer, MasterTableTemp1> M1M2Map, Issues issue, MediumProcessingAlert alrtClassObj, Boolean requiredM1, Integer foId, Integer m1m2mapkey){

        medAlertProcessingService.mediumAlertProcessing(M1M2Map, issue, alrtClassObj, requiredM1, m1m2mapkey, foId);

    }

    @Async("tripEndEventThreadPool")
    public Future<Void> getTripEndEvent(MasterTableTemp1 masterTableTemp1){

        AEHAPI30573AE1 aehapi30573AE1 = new AEHAPI30573AE1();

        applicationContext.getAutowireCapableBeanFactory().autowireBean(aehapi30573AE1);

        aehapi30573AE1.isTriggered(masterTableTemp1);

        return null;

    }
}
