package com.elogist.vehicle_master_and_alert_creation.models.Enums;

public enum PrimStatusEnum {

    LOADING(11, "Loading"),
    UNLOADING(12, "Unloading"),

    ONWARD_NEARBY_ORIGIN(13,"Onward, Nearby Origin"),

    ONWARD_NEARBY_DESTINATION(14,"Onward, Nearby Destination"),

    AVAILABLE_NEARBY_ORIGIN(15,"Available, Nearby Origin"),

    AVAILABLE_NEARBY_DESTINATION(20,"Available, Nearby Destination"),

    AVAILABLE_UNATHORISED_MOVEMENT(21,"Available, Unauthorised Movement"),

    AVAILABLE_EMPTY_MOVEMENT(22,"Available, Empty Movement"),

    AVAILABLE_NEAR_POINT(24,"Available, Near Point"),

    ONWARD_NEAR_POINT(25,"Onward, Near Point"),

    ONWARD(51,"Onward"),

    ONWARD_VIA_POINT(53,"Onward, via point");



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


    PrimStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
