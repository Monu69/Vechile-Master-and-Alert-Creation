package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripEndEventDTO {

    @SerializedName("vt_id")
    private Integer vtId;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("vehicle_id")
    private Integer vehicleId;
}
