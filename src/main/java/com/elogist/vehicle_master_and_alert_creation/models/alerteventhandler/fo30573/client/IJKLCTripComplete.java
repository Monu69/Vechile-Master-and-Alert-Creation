package com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.client;

import okhttp3.OkHttpClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "jklc-trip-complete1", url = "https://jklcoicprd-frfimymfh4tj-fr.integration.ocp.oraclecloud.com:443/ic/api/integration"
        ,configuration = IJKLCTripComplete.IJKLCTripCompleteConfiguration.class)
public interface IJKLCTripComplete {

    @RequestMapping(value = "/v1/flows/rest/JKLC_TELEMATICS_TRIPEVENTS_OTM/1.0/shipment-status"
            ,method = RequestMethod.PATCH,consumes = "application/json",produces = "application/json")
    String sendTripComplete(@RequestHeader("Authorization") String authHeader,Object jklcTripCompleteDTO);

    @org.springframework.context.annotation.Configuration
    class IJKLCTripCompleteConfiguration {
        @Bean
        public OkHttpClient client() {
            return new OkHttpClient();
        }
    }

}
