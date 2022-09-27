package com.elogist.vehicle_master_and_alert_creation.models;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(name = "fo_ticket_escalation")

public class TicketEscalation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private Integer foid;

    @Column(name = "fo_issue_type_id")
    private Integer foIssueTypeId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "senior_user_id")
    private Integer seniorUserId;

    @Column(name = "user_level")
    private Integer userLevel;

    @Column(name = "from_time")
    private String fromTime;

    @Column(name = "to_time")
    private String toTime;

    @Column(name = "issue_contraint_id")
    private Integer issueConstraintId;

    @Column(name = "entrymode")
    private Integer entryMode;

    @Column(name = "issue_properties_id")
    private Integer issuePropertiesId;




}
