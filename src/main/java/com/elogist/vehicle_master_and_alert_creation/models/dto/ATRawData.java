package com.elogist.vehicle_master_and_alert_creation.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ATRawData {

    private Integer atvid;
    private List<Integer> sid;

    public ATRawData(Integer atVid, List<Integer> sId){
        this.atvid = atVid;
        this.sid = sId;
    }
}
