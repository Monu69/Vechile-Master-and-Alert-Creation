package com.elogist.vehicle_master_and_alert_creation.models.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BenchMarkDetails
{
    private String description;
    private String name;

    @SerializedName("datatype")
    @JsonProperty("datatype")
    private String dataType;

    private String unit;

    private String defaultValue ;
}
