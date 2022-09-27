package com.elogist.vehicle_master_and_alert_creation.clients;

import com.elogist.vehicle_master_and_alert_creation.config.FeignConfig;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "routes", url ="${route.local.url:#{null}}",configuration = FeignConfig.class)
public interface RouteClient {

    @RequestMapping(value = "master-route-temp-r1r2" ,method = RequestMethod.GET,consumes = "application/json")
    public JsonResponse getRouteData();


}
