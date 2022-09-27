package com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.client;

import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.dtos.AuthDTO;
import feign.Logger;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "jklc-auth", url = "https://idcs-365caf248c1c44169f950c1bbe4494cb.identity.oraclecloud.com/oauth2",configuration = IJKLCAuth.IJKLCAuthConfiguration.class)
public interface IJKLCAuth {

    @RequestMapping(value = "/v1/token",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded",produces = "application/json")
    AuthDTO getAuth(@RequestBody Map<String,?> body, @RequestHeader("Authorization") String basicAuth);

    class IJKLCAuthConfiguration {

        @Autowired
        ObjectFactory<HttpMessageConverters> converters;

        @Bean
        Encoder feignFormEncoder() {
            return new FormEncoder(new SpringEncoder(converters));
        }
        @Bean
        Logger.Level feignLoggerLevel() {
            return Logger.Level.FULL;
        }
    }
}
