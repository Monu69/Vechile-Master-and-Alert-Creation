package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteDTO {

    @SerializedName("id")
    @JsonProperty("id")
    private  Integer id;

    @SerializedName("name")
    @JsonProperty("name")
    private String name;

    @SerializedName("type_id")
    @JsonProperty("typeId")
    private Integer typeId;

    @SerializedName("type_name")
    @JsonProperty("typeName")
    private String typeName;

    @SerializedName("lat")
    @JsonProperty("lat")
    private Double lat;

    @SerializedName("lng")
    @JsonProperty("lng")
    private Double lng;

    @SerializedName("loc_name")
    @JsonProperty("locName")
    private String locName;

    @JsonProperty("poly")
    private String poly;

}
