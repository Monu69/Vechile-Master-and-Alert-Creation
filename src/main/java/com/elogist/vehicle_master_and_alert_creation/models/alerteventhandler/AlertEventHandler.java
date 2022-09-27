package com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler;

import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;

import java.util.Map;

public abstract class AlertEventHandler {

    public abstract Boolean isTriggered(MasterTableTemp1 master);
}
