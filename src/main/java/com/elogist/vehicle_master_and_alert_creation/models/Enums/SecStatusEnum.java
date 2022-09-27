package com.elogist.vehicle_master_and_alert_creation.models.Enums;

public enum SecStatusEnum {

    DATA_MISSING(-1, "Data Missing"),

    OTHER(0, "Other"),
    LOADING(1, "Loading"),

    ONWARD(2, "Onward"),
    UNLOADING(3, "Unloading"),

    AVAILABLE(4, "Available"),

    ACCIDENT(301, "Accident"),
    BREAK_DOWN(311, "Break Down"),
    MAINTENANCE(321, "Maintenance"),

    WAITING_FOR_LOAD(331, "Waiting for Load"),
    DRIVER_UNAVAILABLE(341,"Driver Unavailable"),
    RTO(401, "Rto Issue"),

    DRIVER_RELATED_ISSUE(411,"Driver Related Issue"),

    TRAFFIC_ISSUE(421,"Traffic Issue"),

    DOCUMENTS_ISSUE(431,"Documents Issue"),

    DRIVER_AT_HOME(412,"Driver At Home"),

    TYRE_PUNCTURE(501, "Tyre Puncture"),

    TYRE_BURST(502,"Tyre Burst"),

    OTHER_ISSUES(999, "Other Issues");


    private Integer value;
    private   String description= "";

    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value ;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(){
        this.description = description;
    }


    SecStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }



}

