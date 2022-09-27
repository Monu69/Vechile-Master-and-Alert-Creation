package com.elogist.vehicle_master_and_alert_creation.models.alertevents;

import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;

public class AlertsEvent1 {  // First it's is call by M1M2 and now it's is call by Umang Sir SP, Due to prevention multi trip existing on same vehicle causing only to send last vehicles Event...

    public static Integer eventTypeId = 1;

    public Integer getEventTypeId(){
        return eventTypeId;
    }

    public Boolean isValidEvent(MasterTableTemp1 masterTableTemp1, MasterTableTemp2 masterTableTemp2){
        if(masterTableTemp1.getVtId() != null && masterTableTemp2.getVtId() != null && masterTableTemp1.getVtId().equals(masterTableTemp2.getVtId())){

            if(masterTableTemp1.getVtStampTime() != null && masterTableTemp2.getVtStampTime() == null){

                return true;

            }
        }
        return false;
    }


}
