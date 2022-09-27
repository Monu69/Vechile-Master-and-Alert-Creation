package com.elogist.vehicle_master_and_alert_creation.clients;

import com.elogist.vehicle_master_and_alert_creation.config.FeignConfig;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "dynamic-route", url ="${dynamicRoute.local.url:#{null}}",configuration = FeignConfig.class)
public interface DynamicRouteClient {

    @RequestMapping(value = "/master-dynamic-route-d1d2" ,method = RequestMethod.GET,consumes = "application/json")
    public JsonResponse getDynamicRouteData();
}
