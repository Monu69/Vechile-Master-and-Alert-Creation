package com.elogist.vehicle_master_and_alert_creation.excepetion;

public class ATFetchExcepetion extends RuntimeException{

    private String message;

    public ATFetchExcepetion(String msg){
        super(msg);
    }
}
