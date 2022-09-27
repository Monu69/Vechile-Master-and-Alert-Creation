package com.elogist.vehicle_master_and_alert_creation.models;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.BenchMarkDetails;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(name = "fo_issue_ticket_properties", schema ="foadmin")
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
public class TicketProperties implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "fo_issue_type_id")
    private Integer foIssueTypeId;

    @Column(name = "foid")
    private Integer foid;

    @Column(name = "esc_time")
    private Integer escalationTime;

    @Column(name = "compl_rem_time")
    private Integer completeReminderTime;

    @Column(name = "compl_esc_time")
    private Integer completeEscalationTime;

    @Column(name = "is_reminder")
    private Boolean isReminder;

    @Column(name = "is_escalate")
    private Boolean isEscalate;

    @Column(name = "is_deliverytime")
    private Boolean isDeliveryTime;

    @Column(name = "is_urgent")
    private Boolean isUrgent;

    @Type(type = "jsonb")
    @Column(name = "benchmarks",columnDefinition = "jsonb")
    private String benchmarks;

    @Column(name="benchmark")
    private Integer benchmark;

    @Column(name="benchmark2")
    private Integer benchmark2;


    @Column(name = "issue_contraint_id")
    private Integer issueConstraintId;








}
