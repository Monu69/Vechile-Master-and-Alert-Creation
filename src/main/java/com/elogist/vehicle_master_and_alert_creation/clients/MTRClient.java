package com.elogist.vehicle_master_and_alert_creation.clients;

import com.elogist.vehicle_master_and_alert_creation.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FeignClient(value="${mtr.vip.name}", url = "${mtr.local.url:#{null}}",configuration = FeignConfig.class)
public interface MTRClient {

    @RequestMapping(value = "TripsOperation/getTripMasterReportv1",method = RequestMethod.GET,consumes = "application/json")
    Map<String,Object> getMTRData(@RequestHeader("authkey") String authkey,
                                        @RequestHeader("entrymode") Integer entrymode,
                                        @RequestHeader("version") Double version,
                                        @RequestParam("vid") Integer vId,
                                        @RequestParam("startTime")String startTime,
                                        @RequestParam("endTime") String endTime,
                                        @RequestParam("orgin") String origin,
                                        @RequestParam("destination") String destination,
                                        @RequestParam("invoicenumber") String invoicenumber,
                                        @RequestParam("transportar") String transportar,
                                        @RequestParam("consigner") String consigner,
                                        @RequestParam("consignee") String consignee,
                                        @RequestParam("datatype") Integer datatype,
                                        @RequestParam("forpushapi") Integer forpushapi,
                                        @RequestParam("rangetype") Integer rangetype,
                                        @RequestParam("tripid") Integer tripId,
                                        @RequestParam("foAdminId") Integer foAdminId,
                                        @RequestParam("multipleAccounts") Integer multipleAccounts,
                                        @RequestParam("version") Double version1);
}
