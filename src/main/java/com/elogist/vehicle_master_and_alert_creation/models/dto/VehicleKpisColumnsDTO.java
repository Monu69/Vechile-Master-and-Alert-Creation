package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleKpisColumnsDTO implements Comparable<VehicleKpisColumnsDTO> {

    @SerializedName("col_id")
    private Integer colId;

    @SerializedName("col_name")
    private String colName;

    @SerializedName("col_order")
    private Integer colOrder;

    @SerializedName("col_title")
    private String colTitle;

    @SerializedName("ref_table")
    private String refTable;

    @SerializedName("is_default")
    private Boolean isDefault;

    @SerializedName("col_title_actual")
    private String colTitleActual;

    @Override
    public int compareTo(VehicleKpisColumnsDTO vehicleKpisColumnsDTO) {
        if(this.colOrder < vehicleKpisColumnsDTO.getColOrder())
            return -1;
        else if(this.colOrder == vehicleKpisColumnsDTO.getColOrder())
            return  0;
        else
            return 1;
    }


}
