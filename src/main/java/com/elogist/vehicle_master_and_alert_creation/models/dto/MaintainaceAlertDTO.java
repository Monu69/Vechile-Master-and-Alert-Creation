package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintainaceAlertDTO {

    @SerializedName("foid")
    private Integer foId;

    @SerializedName("regno")
    private String regNo;

    @SerializedName("last_service_date")
    private String lastServiceDate;

    @SerializedName("odometer")
    private Integer odometer;

    @SerializedName("next_service_date")
    private String nextServiceDate;

    @SerializedName("_nextodo")
    private Integer nextOdo;

    @SerializedName("_curodo")
    private Integer curOdo;

    @SerializedName("status")
    private String status;

    @SerializedName("Remarks")
    private String remark;

    @SerializedName("_vid")
    private Integer vid;

    @SerializedName("_jobid")
    private Integer jobId;

    @SerializedName("_jpid")
    private Integer jpId;

    @SerializedName("part_name")
    private String partName;

    @SerializedName("_nextmonths")
    private Integer nextMonths;

    @SerializedName("_nextkms")
    private Integer nextKms;

    @SerializedName("_nextdate")
    private String nextDate;

    @SerializedName("date")
    private String date;

    @SerializedName("?column?")
    private Integer column;

    @SerializedName("vt_id")
    private Integer tripId;

    @SerializedName("issue_property_id")
    private Integer issuePropertyId;
}
