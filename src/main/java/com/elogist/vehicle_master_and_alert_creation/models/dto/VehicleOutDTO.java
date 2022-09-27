package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleOutDTO {

    @SerializedName("vehicleid")
    private Integer vehicleId;

    @SerializedName("foid")
    private Integer foId;

    @SerializedName("axvehid")
    private Integer axVehicleId;

    @SerializedName("foissuepropertyid")
    private Integer foIssuePropertyId;

    @SerializedName("foissuetypeid")
    private Integer foIssueTypeId;
}
