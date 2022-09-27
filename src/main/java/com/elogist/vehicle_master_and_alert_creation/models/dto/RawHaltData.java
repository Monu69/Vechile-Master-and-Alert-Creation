package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.elogist.vehicle_master_and_alert_creation.models.Halt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class RawHaltData {

    private List<Halt> existingHalts;

    private List<Halt> generatedHalts;

    private String success;

    private String msg;

    private List<Halt> data;

}