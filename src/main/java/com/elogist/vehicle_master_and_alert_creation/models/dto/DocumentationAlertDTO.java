package com.elogist.vehicle_master_and_alert_creation.models.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentationAlertDTO {

    private Integer vehicleId;
    private Integer docTypeId;
    private Integer foId;
    private String regNo;
    private String docTypeName;
    private Integer issueTypeId;
    private String expireDate;
    private Integer priId;
    private Integer tripId;

    public DocumentationAlertDTO(String docTypeName, String expireDate){

        this.docTypeName = docTypeName;
        this.expireDate = expireDate;

    }

}
