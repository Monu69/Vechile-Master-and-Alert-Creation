package com.elogist.vehicle_master_and_alert_creation.clients;


import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "${lds.vip.name}", url = "${lds.local.url:#{null}}")
public interface LocationDataSwitchingClient {

    @RequestMapping(value = "/getrawdistance",method = RequestMethod.POST,consumes = "application/json")
    String getRawDistance(List<VehicleStatsDTO> vehicleStatDTOS);
}
