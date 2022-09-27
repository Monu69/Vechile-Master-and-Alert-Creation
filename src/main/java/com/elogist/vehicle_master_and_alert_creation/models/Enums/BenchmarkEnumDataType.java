package com.elogist.vehicle_master_and_alert_creation.models.Enums;


import java.util.List;

public enum BenchmarkEnumDataType {


        INTEGER("Integer"),
        FLOAT("Float"),
        STRING("String"),
        TIME("Time");
        String text;
        // private String value;

        BenchmarkEnumDataType(String text) {
            this.text = text;
        }

        public String getText(){
            return text;
        }

        public static BenchmarkEnumDataType getBenchmarkEnumDataType(String value) {
            if(value == null)
                return null;
            for (BenchmarkEnumDataType e : values()) {
                if (e.text.equals(value)) {
                    return e;
                }
            }
            return null;
        }

    public static BenchmarkEnumDataType[] dataTypeList(){
        return BenchmarkEnumDataType.values();
    }


}

