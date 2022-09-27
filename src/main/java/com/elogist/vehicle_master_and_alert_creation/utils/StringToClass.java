package com.elogist.vehicle_master_and_alert_creation.utils;

public class StringToClass {

    public static Class getClassName(String className) throws ClassNotFoundException {
        try {
            Class alertClass = Class.forName(className);
            return alertClass;
        }
        catch (ClassNotFoundException classNotFoundException){
            return null;
        }

    }
}
