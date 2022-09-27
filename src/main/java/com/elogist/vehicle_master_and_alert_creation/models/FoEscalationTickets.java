package com.elogist.vehicle_master_and_alert_creation.models;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.customalert.DeviationAlert;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert.MediumProcessingAlert;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert.SimpleAlert;
import com.elogist.vehicle_master_and_alert_creation.models.dto.*;
import com.elogist.vehicle_master_and_alert_creation.services.AlertProcessingService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "foadmin.fo_escalation_tickets")
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
public class FoEscalationTickets {

    @Autowired
    @Transient
    AlertProcessingService alertProcessingService;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "fo_issue_type_id")
    private Integer foIssueTypeId;

    @Column(name = "foid")
    private Integer foId;

    @Column(name = "pri_id")
    private Integer priId;

    @Column(name = "sec_id1")
    private Integer secId1;

    @Column(name = "sec_id2")
    private Integer secId2;

    @Column(name = "remark")
    private String remark;

    @Column(name = "entrymode")
    private Integer entryMode;

    @Column(name = "aduserid")
    private Integer adUserId;

    @Column(name = "addtime")
    private LocalDateTime addTime;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "issue_properties_id")
    private Integer issuePropertiesId;

    @Column(name = "is_allocated")
    private Boolean isAllocated;

    @Column(name = "clearuserid")
    private Integer clearUserId;

    @Column(name = "cleartime")
    private LocalDateTime clearTime;

    @Column(name = "is_auto_resolvable")
    private Boolean isAutoResolvable;


    @Type(type = "jsonb")
    @Column(name = "gen_params")
    private String generalParam;

    @Type(type = "jsonb")
    @Column(name = "specific_params")
    private String specificParam;

    @Type(type = "jsonb")
    @Column(name = "m1state", columnDefinition = "jsonb")
    private String m1state;

    @Type(type = "jsonb")
    @Column(name = "m2state", columnDefinition = "jsonb")
    private String m2state;

    @Type(type = "jsonb")
    @Column(name = "benchmarks", columnDefinition = "jsonb")
    private String benchmarks;


    public FoEscalationTickets(Integer foIssueTypeId, Integer foId, Integer priId, Integer secId1, Integer secId2, String remark, Integer entryMode, Integer adUserId, LocalDateTime addTime, Boolean isAllocated, Integer clearUserId, LocalDateTime clearTime, Integer vehicleId, Integer issuePropertiesId) {
        this.foIssueTypeId = foIssueTypeId;
        this.foId = foId;
        this.priId = priId;
        this.secId1 = secId1;
        this.secId2 = secId2;
        this.remark = remark;
        this.entryMode = entryMode;
        this.adUserId = adUserId;
        this.addTime = addTime;
        this.isAllocated = isAllocated;
        this.clearUserId = clearUserId;
        this.clearTime = clearTime;
        this.vehicleId = vehicleId;
        this.issuePropertiesId = issuePropertiesId;
    }

    public FoEscalationTickets(Integer foIssueTypeId, Integer foId, Integer priId, String remark, Integer entryMode, Integer adUserId, LocalDateTime addTime, Boolean isAllocated, Integer vehicleId, Integer issuePropertiesId, String m1state) {
        this.foIssueTypeId = foIssueTypeId;
        this.foId = foId;
        this.priId = priId;
        this.remark = remark;
        this.entryMode = entryMode;
        this.adUserId = adUserId;
        this.addTime = addTime;
        this.isAllocated = isAllocated;
        this.vehicleId = vehicleId;
        this.issuePropertiesId = issuePropertiesId;
        this.m1state = m1state;
    }

    public FoEscalationTickets(ATAlertMappingDTO atAlertMappingDTO, Integer foId, Integer vehicleId, Integer issuePropertiesId, MasterTableTemp1 masterTableTemp1, String generalParameter, String specifiedParameter) {
        this.foIssueTypeId = atAlertMappingDTO.getServiceId() + 31000;
        this.foId = foId;
        this.priId = atAlertMappingDTO.getAtVid();
        this.remark = atAlertMappingDTO.getMessage();
        this.entryMode = -1;
        this.adUserId = -2;
        this.addTime = atAlertMappingDTO.getAlertTime().toLocalDateTime();
        this.isAllocated = false;
        this.vehicleId = vehicleId;
        this.issuePropertiesId = issuePropertiesId;
        this.m1state = getM1Data(masterTableTemp1);
        this.benchmarks = null;
        this.specificParam = specifiedParameter;
        this.generalParam = generalParameter;
    }

    public FoEscalationTickets(MasterTableTemp1 masterTableTemp1, SimpleAlert alertClassObj, Issues issueElement, Integer foid, String specifiedParameter, String generalParameter, String benchmarks)  {
        this.foIssueTypeId = alertClassObj.getAlertTypeId();
        this.foId = foid;
        this.priId = alertClassObj.getPriId(masterTableTemp1);
        this.secId2 = masterTableTemp1.getVtId();
        this.remark = alertClassObj.getRemark(masterTableTemp1, issueElement);
        this.entryMode = 0;
        this.adUserId = 0;
        this.addTime = LocalDateTime.now().withNano(0);
        this.isAllocated = false;
        this.vehicleId = masterTableTemp1.getVId();
        this.issuePropertiesId = issueElement.getId();
        this.m1state = getM1Data(masterTableTemp1);
        this.specificParam = specifiedParameter;
        this.generalParam = generalParameter;
        this.benchmarks = benchmarks;
    }

    public FoEscalationTickets(MasterTableTemp1 masterTableTemp1, MediumProcessingAlert alertClassObj, Issues issueElement, Integer foId, String specifiedParameter, String generalParameter, String benchmarks)  {
        this.foIssueTypeId = alertClassObj.getAlertTypeId();
        this.foId = foId;
        this.priId = alertClassObj.getPriId(masterTableTemp1);
        this.secId2 = masterTableTemp1.getVtId();
        this.remark = alertClassObj.getRemark(masterTableTemp1, issueElement);
        this.entryMode = 0;
        this.adUserId = 0;
        this.addTime = LocalDateTime.now().withNano(0);
        this.isAllocated = false;
        this.vehicleId = masterTableTemp1.getVId();
        this.issuePropertiesId = issueElement.getId();
        this.m1state = getM1Data(masterTableTemp1);
        this.specificParam = specifiedParameter;
        this.generalParam = generalParameter;
        this.benchmarks = benchmarks;
    }

    public FoEscalationTickets(MaintainaceAlertDTO maintainaceAlertDTO, String m1state) {
        this.foIssueTypeId = 11001;
        this.foId = maintainaceAlertDTO.getFoId();
        this.priId = maintainaceAlertDTO.getJpId();
        this.secId1 = null;
        this.secId2 = maintainaceAlertDTO.getTripId();
        this.remark = maintainaceAlertDTO.getRegNo() + " " + maintainaceAlertDTO.getPartName() + " service due in next " + maintainaceAlertDTO.getStatus();
        this.entryMode = -1;
        this.adUserId = -3;
        this.addTime = LocalDateTime.now();
        this.isAllocated = false;
        this.vehicleId = maintainaceAlertDTO.getVid();
        this.issuePropertiesId = maintainaceAlertDTO.getIssuePropertyId();
        this.m1state = m1state;
        this.benchmarks = null;

    }

    public FoEscalationTickets(DocumentationAlertDTO documentationAlertDTO, MasterTableTemp1 masterTableTemp1, String generalParam, String specifiedParameter){
        this.foIssueTypeId = 10001;
        this.foId = documentationAlertDTO.getFoId();
        this.priId = documentationAlertDTO.getPriId();
        this.secId1 = null;
        this.secId2 = documentationAlertDTO.getTripId();
        this.remark = "Document " + documentationAlertDTO.getDocTypeName() + " is expired on " + documentationAlertDTO.getExpireDate();
        this.entryMode = -1;
        this.adUserId = -4;
        this.addTime = LocalDateTime.now();
        this.isAllocated = false;
        this.vehicleId = documentationAlertDTO.getVehicleId();
        this.issuePropertiesId = documentationAlertDTO.getIssueTypeId();
        this.m1state = getM1Data(masterTableTemp1);
        this.benchmarks = null;
        this.generalParam = generalParam;
        this.specificParam = specifiedParameter;

    }

    public FoEscalationTickets(DeviationALertDTO deviationALertDTO, Issues issues, String remark, MasterTableTemp1 masterTableTemp1, Integer foId, String specifiedParameter, String generalParameter, String benchmarks){
        this.foIssueTypeId = DeviationAlert.alertId;
        this.foId = foId;
        this.priId = deviationALertDTO.getVtsId().intValue();
        this.secId1 = null;
        this.secId2 = deviationALertDTO.getVtId().intValue();
        this.remark = remark;
        this.entryMode = 0;
        this.adUserId = 0;
        this.addTime = LocalDateTime.now();
        this.isAllocated = false;
        this.issuePropertiesId = issues.getId();
        this.vehicleId = deviationALertDTO.getVehicleId().intValue();
        this.m1state = getM1Data(masterTableTemp1);
        this.specificParam = specifiedParameter;
        this.generalParam = generalParameter;
        this.benchmarks = benchmarks;
    }

    public String getM1Data(MasterTableTemp1 masterTableTemp1){

        if(masterTableTemp1 != null) {
            Gson gson = new Gson();
            String m1Result = gson.toJson(masterTableTemp1);
            return m1Result;
        }

        return null;
    }

    public String getM2Data(MasterTableTemp2 masterTableTemp2){

        if (masterTableTemp2 != null) {

            Gson gson = new Gson();
            String m2Result = gson.toJson(masterTableTemp2);

            return m2Result;
        }

        return null;

    }

}
