package com.elogist.vehicle_master_and_alert_creation.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleKPIDistanceDTO {

    private String startTime;
    private String endTime;
    private Double distance;

    public VehicleKPIDistanceDTO(String startTime, String endTime, Double distance) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
    }

}
