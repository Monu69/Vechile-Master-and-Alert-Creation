package com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto;

import com.elogist.vehicle_master_and_alert_creation.models.RunPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MedAlert29002ParamDto {

    List<RunPOJO> runs;

    LocalDateTime lastNightRun;

    Boolean lastAlertState;
}
