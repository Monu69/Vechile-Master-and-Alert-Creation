package com.elogist.vehicle_master_and_alert_creation.utils;

public class DistanceUtil {
    //calculating distance using Haversine method
    public static double x = Math.PI/180.0;
    public static double distanceBtwAAndB(Double lat1, Double lng1, Double lat2, Double lng2){
        if(lat1.equals(lat2) && lng1.equals(lng2)){
            return 0;
        }
        int radius = 6371000;
        lat1 *= x;
        lat2 *= x;
        lng1 *= x;
        lng2 *= x;
        double distance=
                2 * Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2)/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((lng1 - lng2) / 2), 2)));
        return distance * radius;
    }
}
