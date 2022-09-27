package com.elogist.vehicle_master_and_alert_creation.models.Enums;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum StateBenchmarkEnum {

    LOADING("Loading",1),
    ONWARD("Onward",2),
    UNLOADING("Unloading",3),
    AVAILABLE("Available",4);

    String state;
    Integer code;

    StateBenchmarkEnum(String state, Integer code) {
        this.state = state;
        this.code = code;
    }

    public String getName() {
        return this.state;
    }

    public Integer getCode() {
        return this.code;
    }

    public static List<Integer> getCodes() {
        List<Integer> codes = new ArrayList<>();
        StateBenchmarkEnum[] stateBenchmarkEnums = StateBenchmarkEnum.values();
        for(StateBenchmarkEnum stateBenchmarkEnum : stateBenchmarkEnums)
            codes.add(stateBenchmarkEnum.getCode());
        return codes;
    }

    public static Map<Integer,String> getAllEnums(){
        Map<Integer,String> allEnums = new HashMap<>();
        StateBenchmarkEnum[] stateBenchmarkEnums = StateBenchmarkEnum.values();
        for(StateBenchmarkEnum stateBenchmarkEnum : stateBenchmarkEnums)
            allEnums.put(stateBenchmarkEnum.getCode(),stateBenchmarkEnum.getName());
        return allEnums;
    }

}
