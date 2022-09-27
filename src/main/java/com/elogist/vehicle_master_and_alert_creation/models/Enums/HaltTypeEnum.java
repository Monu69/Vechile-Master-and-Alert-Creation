package com.elogist.vehicle_master_and_alert_creation.models.Enums;

public enum HaltTypeEnum {

    DATA_MISSING(-1, "Data Missing"),

    OTHER(0, "Other"),

    LOADING(11, "Loading"),

    REJECT_LOADING(12,"Reject Loading"),

    UNLOADING(21, "Unloading"),

    REJECT_UNLOADING(22,"Reject Unloading"),

    FOOD_TEA(41,"Food/Tea"),

    REST(42,"Rest"),

    FOOD_REST(43,"Food+Rest"),

    FUEL_FILLING(51,"Fuel Filling"),

    NEARBY_TOLL(61, "Nearby Toll"),

    NEARBY_ORIGIN(101,"Nearby Origin"),

    NEARBY_DESTINATION(201,"Nearby Destination"),

    RTO(301, "Rto Issue"),

    BREAK_DOWN(311, "Break Down"),

    PUNCTURE(321,"Puncture"),

    MAINTENANCE(331, "Maintenance"),

    HISSAB(401,"Hisaab"),

    DOCUMENTS(411,"Documents"),

    NO_ENTRY_AHEAD(501,"No Entry Ahead"),

    DRIVER_UNAVAILABLE(601,"Driver Unavailable"),

    OTHER_REASON(701, "Other Reason"),

    UNKNOWN(901,"Unknown");


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


    HaltTypeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
