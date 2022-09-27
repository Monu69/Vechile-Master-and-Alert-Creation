package com.elogist.vehicle_master_and_alert_creation.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class VehicleKpiResponseDTO {

    private List<Map<String,Object>> data;
    private List<VehicleKpisColumnsDTO> columns;

    public VehicleKpiResponseDTO(List<Map<String,Object>> data, List<VehicleKpisColumnsDTO> columns){
        this.data = data==null?new ArrayList<>():data;
        this.columns = columns==null?new ArrayList<>():columns;
    }

}
