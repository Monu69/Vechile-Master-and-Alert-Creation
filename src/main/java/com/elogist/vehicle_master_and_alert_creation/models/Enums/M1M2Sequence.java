package com.elogist.vehicle_master_and_alert_creation.models.Enums;

public enum M1M2Sequence {

    MASTER_VEHICLES_ENGINE("master_vehicles_engine"),
    AUTO_RULES_ENGINE("auto_rules_engine"),
    HALTS_GENERATION("halts_generation");

    private String description;

    M1M2Sequence(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }



//    master_vehicles_engine
//
//    auto_rules_engine
//
//    halts_generation


}
