package com.elogist.vehicle_master_and_alert_creation.models.constants;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class RedisConstant {

    // map names
    public static final String TICKET_ESCALATION = "TicketEscalation";
    public static final String ISSUE_CONSTRAINTS = "IssueConstraints";
    public static final String TICKET_PROPERTIES = "TicketProperties";
    public static final String ALERT = "AlertsParamValue";
    public static final String ALERT_VEHICLE_RECIPIENTS = "AlertVehicleRecipients";
    public static final String VEHICLE_KPI_DISTANCE = "VehicleKPIDistance";
    public static final String FO_VEHICLE = "FoVehicle";
    public static final String FO_VEHICLE_CROSS = "FoVehicleCross";
    public static final String ALL_VEHICLE = "AllVehicle";




    public static final LocalTime NIGHT_START = LocalTime.of(23,0);
    public static final LocalTime NIGHT_END = LocalTime.of(5,0);

    // map value keys (keys of json values)
//    public static final String

}
