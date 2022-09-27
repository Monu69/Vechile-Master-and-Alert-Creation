package com.elogist.vehicle_master_and_alert_creation.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleStatsDTO {
    private Long vehicleId;
    private String startTime;
    private String endTime;
    private Integer distance;

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        Character ch = startTime.charAt(10);
        if(ch.equals('T')){
            this.startTime = this.startTime.replace('T', ' ');
        }
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
        Character ch = endTime.charAt(10);
        if(ch.equals('T')){
            this.endTime = this.endTime.replace('T', ' ');
        }
    }

    public VehicleStatsDTO(Long id, LocalDateTime startTime, LocalDateTime endtime){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.vehicleId = id;
        this.startTime = startTime.format(formatter);
        this.endTime = endtime.format(formatter);

    }
}

