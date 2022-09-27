package com.elogist.vehicle_master_and_alert_creation.models.alertevents;

import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;

public abstract class AlertsEvents {

    public  abstract Boolean isValidEvent(MasterTableTemp1 masterTableTemp1, MasterTableTemp2 masterTableTemp2);

    public abstract Integer getEventTypeId();
}
