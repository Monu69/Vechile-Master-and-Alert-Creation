package com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto;

import com.elogist.vehicle_master_and_alert_creation.models.Enums.PointsWTime;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MedAlert29201ParamDto {

    List<PointsWTime> pointsWTimes;

    Boolean prevState;

}
