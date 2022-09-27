package com.elogist.vehicle_master_and_alert_creation.utils;

public class SQLExceptions extends RuntimeException{
    public  SQLExceptions(String errorMessage) {
        super(errorMessage);
    }
}
