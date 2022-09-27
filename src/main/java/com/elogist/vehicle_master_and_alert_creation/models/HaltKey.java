package com.elogist.vehicle_master_and_alert_creation.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class HaltKey implements Serializable {

    @NotNull
    @Column(name = "vehicleId")
    private Long vehicleId;


    @NotNull
    @Column(name = "start_time")
    LocalDateTime startTime;

}
