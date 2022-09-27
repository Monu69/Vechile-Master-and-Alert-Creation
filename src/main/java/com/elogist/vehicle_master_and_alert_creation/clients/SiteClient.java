package com.elogist.vehicle_master_and_alert_creation.clients;


import com.elogist.vehicle_master_and_alert_creation.config.FeignConfig;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "${sites.vip.name}", url ="${sites.local.url:#{null}}",configuration = FeignConfig.class)
public interface SiteClient {
    @RequestMapping(value = "/foSiteMultiple", method = RequestMethod.POST, consumes = "application/json")
    String getReverseGeoCodedLocation(@RequestHeader("entrymode") Integer entrymode,
                                      @RequestHeader("foAdminId") Integer foAdminId,
                                      @RequestHeader("authkey") String authkey,
                                      @RequestBody String gpsData);
}

