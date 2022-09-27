package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;
import com.elogist.vehicle_master_and_alert_creation.utils.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertUtilService {

    public Boolean isOnward(Master master){

        Boolean isOnward = false;

        if(isOnwardForTrip(master) || isOnwardForRoute(master) || isOnwardForDynamicRoute(master)){

            isOnward = true;

        }

        return isOnward;
    }

    public Boolean isOnwardForTrip(Master master){

        Boolean isOnward = false;

        if (master != null && master.getVtAddtime() != null && master.getVtTripCompleteTime() == null) {
                isOnward = true;
        }

        return isOnward;
    }

    public Boolean isOnwardForRoute(Master master){

        Boolean isOnward = false;

        //ToDo: refactor stringConstants
        if(master.getMasterRoute() != null && master.getMasterRoute().getPrimStatus() != null && master.getMasterRoute().getPrimStatus().equals(StringConstants.ROUTE_IS_ONWARD)){

            return true;

        }

        return isOnward;
    }

    public Boolean isOnwardForDynamicRoute(Master master){

        Boolean isOnward = false;

        if(master.getMasterDynamicRoute() != null && master.getMasterDynamicRoute().getPrimStatus()!= null && master.getMasterDynamicRoute().getPrimStatus().equals(StringConstants.DYNAMIC_ROUTE_IS_ONWARD)){

            return true;

        }

        return isOnward;
    }



}
