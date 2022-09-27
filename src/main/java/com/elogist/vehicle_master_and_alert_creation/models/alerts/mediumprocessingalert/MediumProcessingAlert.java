package com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert;

import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;

public abstract class MediumProcessingAlert extends Alerts {

    public abstract Boolean isValidAlert(MasterTableTemp1 master1, Issues issues) ;


}
