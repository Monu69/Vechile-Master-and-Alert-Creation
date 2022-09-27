package com.elogist.vehicle_master_and_alert_creation.models.Enums;

public enum HaltsEnumeration {
    IDLE(1,"idle"),
    RUNNING(0,"runing"),
    UNDEFINED(-1,"undefined");


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


    HaltsEnumeration(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}
