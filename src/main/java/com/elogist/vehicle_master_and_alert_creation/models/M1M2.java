package com.elogist.vehicle_master_and_alert_creation.models;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class M1M2 {

    private Master MasterM1;
    private Master MasterM2;

    public M1M2(Master masterM1, Master masterM2){
        this.MasterM1 = masterM1;
        this.MasterM2 = masterM2;
    }
}
