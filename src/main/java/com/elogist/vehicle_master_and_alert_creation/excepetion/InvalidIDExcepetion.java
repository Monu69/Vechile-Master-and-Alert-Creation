package com.elogist.vehicle_master_and_alert_creation.excepetion;

public class InvalidIDExcepetion extends RuntimeException{

    private String message;

    public InvalidIDExcepetion(String msg){
        super(msg);
    }
}
