package com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto;

import com.elogist.vehicle_master_and_alert_creation.models.Halt;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedAlert29004ParamDto {

    Halt suffHalt;

    Boolean lastAlertState;
}
