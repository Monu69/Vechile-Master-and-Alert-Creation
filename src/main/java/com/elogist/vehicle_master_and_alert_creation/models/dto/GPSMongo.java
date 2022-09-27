package com.elogist.vehicle_master_and_alert_creation.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPSMongo {

    @Id
    private String id;

    @Column(name = "Loc")
    private BigDecimal[] loc;

    @Column(name = "AddLoc")
    private BigDecimal[] addLoc;

    @Column(name = "address")
    private String address;

    @Column(name = "address_wocomma")
    private String addressWoComma;

    @Column(name = "street_number")
    private String streetNumber;

    private String route;
    private String neighborhood;

    @Column(name = "sublocality")
    private String subLocality;

    private String locality;
    private String admin2;
    private String admin1;
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    private Integer version;

    private Integer siteId;
}

