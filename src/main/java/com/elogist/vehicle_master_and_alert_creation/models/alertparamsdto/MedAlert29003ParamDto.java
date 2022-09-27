package com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto;

import com.elogist.vehicle_master_and_alert_creation.models.Halt;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MedAlert29003ParamDto {

    Halt suffNightHalt;

    Halt suffDayHalt;

    Boolean lastAlertState;

    LocalDateTime lastNightRunTime;

    public MedAlert29003ParamDto(Halt suffDayHalt, boolean lastAlertState, LocalDateTime lastNightRunTime) {

        this.suffDayHalt = suffDayHalt;
        this.lastAlertState = lastAlertState;
        this.lastNightRunTime = lastNightRunTime;
    }

//    public void setSuffNightHalt(Halt suffHalt) {
//        this.suffNightHalt = suffHalt;
//        this.suffDayHalt = suffHalt;
//    }

}
