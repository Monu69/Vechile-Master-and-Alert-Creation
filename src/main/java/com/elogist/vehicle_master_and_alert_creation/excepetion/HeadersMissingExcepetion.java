package com.elogist.vehicle_master_and_alert_creation.excepetion;

public class HeadersMissingExcepetion extends RuntimeException {

    private String message;

    public HeadersMissingExcepetion(String msg){
        super(msg);
    }
}
