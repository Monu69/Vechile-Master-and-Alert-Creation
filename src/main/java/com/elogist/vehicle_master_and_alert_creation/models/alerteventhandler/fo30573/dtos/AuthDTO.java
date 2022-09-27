package com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    @SerializedName("access_token")
    @JsonProperty("access_token")
    String accessToken;

    @SerializedName("token_type")
    @JsonProperty("token_type")
    String tokenType;

    @SerializedName("expires_in")
    @JsonProperty("expires_in")
    Integer expiresIn;
}
