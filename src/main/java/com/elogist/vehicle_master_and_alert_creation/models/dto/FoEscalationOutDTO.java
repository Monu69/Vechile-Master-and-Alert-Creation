package com.elogist.vehicle_master_and_alert_creation.models.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoEscalationOutDTO {

    private Integer id;

    private Integer foIssueTypeId;

    private Integer foId;

    private Integer priId;

    private Integer secId1;

    private Integer secId2;

    private String remark;

    private Integer entryMode;

    private Integer addUserId;

    private LocalDateTime addTime;

    private Integer vehicleId;

    private Integer issuePropertiesId;

    private Boolean isAllocated;

    private Integer clearUserId;

    private LocalDateTime clearTime;
}
