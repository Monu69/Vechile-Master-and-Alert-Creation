package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertDetailsDTO {

    @SerializedName("id")
    private Integer id;

    @SerializedName("loc_name")
    private String locName;

    @SerializedName("act_lat")
    private Double actLat;

    @SerializedName("act_long")
    private Double actLong;

    @SerializedName("sys_lat")
    private Double sysLat;

    @SerializedName("sys_long")
    private Double sysLong;

    @SerializedName("vs_update_time")
    private String vsUpdateTime;
}
