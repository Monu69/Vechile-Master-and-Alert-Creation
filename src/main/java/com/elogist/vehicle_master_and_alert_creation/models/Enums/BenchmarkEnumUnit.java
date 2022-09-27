package com.elogist.vehicle_master_and_alert_creation.models.Enums;

import java.util.List;

public enum BenchmarkEnumUnit {
    KILOMETER("Kilometer"),
    METER("Meter"),
    SECONDS("Second"),
    MINUTE("Minute"),
    TIME("Time"),
    KMPH("KMPH");


    String text;

    BenchmarkEnumUnit(String text) {
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public static BenchmarkEnumUnit getBenchmarkEnumUnit(String value) {
        if(value == null)
           // return BenchmarkEnumUnit.CHECK;
            return null;

        for (BenchmarkEnumUnit e : values()) {
            if (e.text.equals(value)) {
                return e;
            }
        }
        return null;
    }

    public static BenchmarkEnumUnit[] unitList(){
        return BenchmarkEnumUnit.values();
    }

}
