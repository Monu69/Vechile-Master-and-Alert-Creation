package com.elogist.vehicle_master_and_alert_creation.models.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ClientRseult {

    private Integer alertId;
    private String eventType;
    private Integer vehicleId;
    private String  regNo;
    private String  remark;
    private LocalDateTime entryTime;
    private LocalDateTime generatedTime;

    public ClientRseult(Integer alertId, String eventType, Integer vehicleId, String regNo, String remark, LocalDateTime entryTime, LocalDateTime generatedTime){
        this.alertId = alertId;
        this.eventType = eventType;
        this.vehicleId = vehicleId;
        this.regNo = regNo;
        this.remark = remark;
        this.entryTime = entryTime;
        this.generatedTime = generatedTime;
    }
    public ClientRseult(Integer alertId, Integer vehicleId, String regNo, String remark, LocalDateTime entryTime, LocalDateTime generatedTime){
        this.alertId = alertId;
        this.vehicleId = vehicleId;
        this.regNo = regNo;
        this.remark = remark;
        this.entryTime = entryTime;
        this.generatedTime = generatedTime;
    }

}
