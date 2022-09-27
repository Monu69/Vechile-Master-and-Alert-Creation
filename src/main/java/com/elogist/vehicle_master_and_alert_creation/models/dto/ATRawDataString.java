package com.elogist.vehicle_master_and_alert_creation.models.dto;

public class ATRawDataString {

    private String atvid;
    private String sid;
    public ATRawDataString(ATRawData ATRawData){
        this.atvid = ATRawData.getAtvid().toString();
        this.sid = ATRawData.getSid().toString();
    }
}
