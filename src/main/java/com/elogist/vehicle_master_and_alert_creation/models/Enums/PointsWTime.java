package com.elogist.vehicle_master_and_alert_creation.models.Enums;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
public class PointsWTime {

    // TODO: remove this class on monday

    Double lat;

    Double lng;

    LocalDateTime dateTime;

    public Boolean samePos(PointsWTime pointsWTime) {

        if(Objects.equals(this.lat, pointsWTime.lat) && Objects.equals(this.lng, pointsWTime.lng))
            return true;
        else
            return false;
    }
}
