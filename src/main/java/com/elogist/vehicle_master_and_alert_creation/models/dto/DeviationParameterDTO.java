package com.elogist.vehicle_master_and_alert_creation.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviationParameterDTO {


    private Integer gpsLeadDisDeviation;

    private Integer destinationDeviation;

    private String lastUnloadingEntryTime;

    private String lastUnloadingExitTime;

    private String invoiceNumber;

    private String invoiceTime;

    private String transporterName;

    private String origin;

    private String actualDestName;

    private Integer leadDis;

    private Integer gpsDis;

    private String driverName;

    private Long driverMobile;

    private String eta;

    private String tripSharpLink;

    private String destination;

    public DeviationParameterDTO(DeviationALertDTO deviationALertDTO) {
        this.gpsLeadDisDeviation = deviationALertDTO.getGpsLeadDisDeviation();
        this.destinationDeviation = deviationALertDTO.getDestinationDeviation();
        this.lastUnloadingEntryTime = deviationALertDTO.getLastUnloadingEntryTime();
        this.lastUnloadingExitTime = deviationALertDTO.getLastUnloadingExitTime();
        this.invoiceNumber = deviationALertDTO.getInvoiceNumber();
        this.invoiceTime = deviationALertDTO.getInvoiceTime();
        this.transporterName = deviationALertDTO.getTransporterName();
        this.origin = deviationALertDTO.getOrigin();
        this.actualDestName = deviationALertDTO.getActualDestName();
        this.leadDis = deviationALertDTO.getLeadDis();
        this.gpsDis = deviationALertDTO.getGpsDis();
        this.driverName = deviationALertDTO.getDriverName();
        this.driverMobile = deviationALertDTO.getDriverMobile();
        this.eta = deviationALertDTO.getEta();
        this.tripSharpLink = deviationALertDTO.getTripSharpLink();
        this.destination = deviationALertDTO.getDestination();
    }

}
