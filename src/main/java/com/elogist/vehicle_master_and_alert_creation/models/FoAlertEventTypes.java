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
@Table(name = "fo_alert_event_types", schema = "foadmin")
public class FoAlertEventTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String eventName;

    @Column(name = "addtime")
    private LocalDateTime addTime;
}
