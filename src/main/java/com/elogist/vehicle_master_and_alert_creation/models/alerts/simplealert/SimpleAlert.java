package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;

public abstract class SimpleAlert extends Alerts {

    public abstract Boolean isValidAlert(Master master1, Issues issues) ;

    public abstract Boolean isM1M2Valid(MasterTableTemp1 masterTableTemp1);
    
}
