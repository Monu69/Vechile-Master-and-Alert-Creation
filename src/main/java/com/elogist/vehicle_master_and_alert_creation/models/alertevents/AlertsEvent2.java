package com.elogist.vehicle_master_and_alert_creation.models.alertevents;

import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;

public class AlertsEvent2 extends AlertsEvents {

    public static Integer eventTypeId = 2;

    public Integer getEventTypeId(){
        return eventTypeId;
    }

    public Boolean isValidEvent(MasterTableTemp1 masterTableTemp1, MasterTableTemp2 masterTableTemp2){
        if((masterTableTemp2.getVtId() == null && masterTableTemp1.getVtId() != null) || (masterTableTemp2.getVtId() != null && !masterTableTemp1.getVtId().equals(masterTableTemp2.getVtId()))){
                return true;
        }
        return false;
    }


}
