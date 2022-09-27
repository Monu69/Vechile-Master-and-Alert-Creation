package com.elogist.vehicle_master_and_alert_creation.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fo_alert_events" , schema = "foadmin")
public class FoAlertEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "fo_alert_event_type_id")
    private Integer foAlertEventTypeId;

    @Column(name = "foid")
    private Integer foId;

    @Column(name = "wef")
    private LocalDateTime withEffectiveTime;

    @Column(name = "expirytime")
    private LocalDateTime expiryTime;

    @Column(name = "handle_type")
    private String handleType;

    @Column(name = "aduserid")
    private Integer adUserId;

    @Column(name = "entrymode")
    private Integer entryMode;

    @Column(name = "addtime")
    private LocalDateTime addTime;

}
