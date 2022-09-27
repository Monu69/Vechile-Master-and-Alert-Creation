package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviationBenchmarkDTO {

    private Integer minDistance;

    @SerializedName("minDistanceSTO")
    private Integer STO;

    @SerializedName("minDistanceSO")
    private Integer SO;


    public DeviationBenchmarkDTO(Integer minDistance) {

        this.minDistance = minDistance;

    }
}
