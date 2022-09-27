package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ATAlertMappingDTO {

    private Integer atVid;
    private String regNo;
    private Integer serviceId;
    private String message;
    private Timestamp alertTime;
    private Timestamp alertAddTime;
    private BigDecimal lat;
    private BigDecimal lng;

    @SerializedName("service_name")
    @JsonProperty("service_name")
    private String serviceName;

}
