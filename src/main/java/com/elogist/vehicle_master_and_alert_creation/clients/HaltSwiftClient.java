package com.elogist.vehicle_master_and_alert_creation.clients;

import com.elogist.vehicle_master_and_alert_creation.config.FeignConfig;
import com.elogist.vehicle_master_and_alert_creation.models.dto.RawHaltData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value="${halts.vip.name}", url = "${halts.local.url:#{null}}",configuration = FeignConfig.class)
public interface HaltSwiftClient {

    @RequestMapping(value = "/existingHalts",method = RequestMethod.GET,consumes = "application/json")
    RawHaltData getHalts(@RequestParam Long vehId, @RequestParam String receivedStartTime, @RequestParam String receivedEndTime);

    @RequestMapping(value = "autoHaltsGeneration/{timeInMin}",method = RequestMethod.POST,consumes = "application/json")
    String getAutoHalt(@PathVariable("timeInMin") Integer timeInMin);

}
