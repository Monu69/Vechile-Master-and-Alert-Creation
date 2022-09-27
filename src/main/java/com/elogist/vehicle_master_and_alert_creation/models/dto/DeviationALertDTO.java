package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
public class DeviationALertDTO {

    @SerializedName("v_fo")
    private Integer vFoId;

    @SerializedName("vs_id")
    private Long vsId;

    @SerializedName("vt_fo")
    private Long vtFo;

    @SerializedName("vt_id")
    private Long vtId;

    @SerializedName("vts_id")
    private Long vtsId;

    @SerializedName("act_dis")
    private Long actDis;

    @SerializedName("sys_dis")
    private Long sysDis;

    @SerializedName("vehicle_id")
    private Long vehicleId;

    @SerializedName("trip_type")
    private String tripType;

    @SerializedName("vs_loc_name")
    private String destination;

    @SerializedName("gps_lead_dis_dev")
    private Integer gpsLeadDisDeviation;

    @SerializedName("des_dev")
    private Integer destinationDeviation;

    @SerializedName("des_dev_meters")
    private Integer destinationDeviationInMetres;

    @SerializedName("des_start_time")
    private String lastUnloadingEntryTime;

    @SerializedName("des_end_time")
    private String lastUnloadingExitTime;

    @SerializedName("invoice_no")
    private String invoiceNumber;

    @SerializedName("invoice_time")
    private String invoiceTime;

    @SerializedName("transporter_name")
    private String transporterName;

    @SerializedName("origin")
    private String origin;

    @SerializedName("actual_dest_name")
    private String actualDestName;

    @SerializedName("lead_dis")
    private Integer leadDis;

    @SerializedName("gps_dis")
    private Integer gpsDis;

    @SerializedName("driver_name")
    private String driverName;

    @SerializedName("driver_mobile")
    private Long driverMobile;

    @SerializedName("trip_share_link")
    private String tripSharpLink;

    @SerializedName("eta")
    private String eta;

    public DeviationALertDTO(Integer vFoId, Long vsId, Long vtFo, Long vtId, Long vtsId, Long actDis, Long sysDis, Long vehicleId, String tripType) {
        this.vFoId = vFoId;
        this.vsId = vsId;
        this.vtFo = vtFo;
        this.vtId = vtId;
        this.vtsId = vtsId;
        this.actDis = actDis;
        this.sysDis = sysDis;
        this.vehicleId = vehicleId;
        this.tripType = tripType;

    }
}
