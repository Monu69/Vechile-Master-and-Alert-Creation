package com.elogist.vehicle_master_and_alert_creation.models.alerts;

import java.util.Map;

public interface SelectionDTI {

    Integer getAlertTypeId();

    String getAlertTypeName();

    Integer getAlertPropertyId();

    Object getBenchmarks();

    Integer getAutoResolveType();
}
