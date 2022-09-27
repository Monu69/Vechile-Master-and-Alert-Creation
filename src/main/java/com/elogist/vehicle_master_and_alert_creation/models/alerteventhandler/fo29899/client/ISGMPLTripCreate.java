package com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo29899.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "sgmpl-trip-create", url = "https://indium-apis.intangles.io/api")
public interface ISGMPLTripCreate {

    @RequestMapping(value = "/v1/vendor/trip_management/12DF03C6:01019475861430272638/create"
                   ,method = RequestMethod.POST,consumes = "application/json",produces = "application/json")
    String sendTripComplete(@RequestHeader("vendor-access-token") String authHeader,Object jklcTripCompleteDTO);

}

