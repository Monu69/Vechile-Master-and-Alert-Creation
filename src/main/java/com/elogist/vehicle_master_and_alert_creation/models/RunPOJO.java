package com.elogist.vehicle_master_and_alert_creation.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RunPOJO {

    Long vehId;

    LocalDateTime startTime;

    LocalDateTime endTime;

}
