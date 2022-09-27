package com.elogist.vehicle_master_and_alert_creation.controllers;

import com.elogist.vehicle_master_and_alert_creation.excepetion.ATFetchExcepetion;
import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.customalert.DocumentationAlert;
import com.elogist.vehicle_master_and_alert_creation.models.dto.ClientRseult;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import com.elogist.vehicle_master_and_alert_creation.models.dto.RequestInfo;
import com.elogist.vehicle_master_and_alert_creation.services.IssueBenchmarkService;
import com.elogist.vehicle_master_and_alert_creation.services.RequestInfoService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@ControllerAdvice
public class IssueBenchmarkController {

    @Autowired
    private IssueBenchmarkService issueBenchmarkService;

    @Autowired
    RequestInfoService requestInfoService;

    @Autowired
    ApplicationContext applicationContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueBenchmarkController.class);

    @GetMapping("issueBenchmark")
    public JsonResponse issueBenchmark()
    {
        Map<Integer,Map<Integer,List<Issues>>> matrix =  issueBenchmarkService.getIssueBenchmark();
        JsonResponse jsonResponse = new JsonResponse(true,"success",matrix);
        return jsonResponse;
    }

    @GetMapping("/requireAlert")
    public JsonResponse getAlert (@RequestHeader HttpHeaders headers,
                                  @RequestParam List<Integer> id,
                                  @RequestParam String startTime,
                                  @RequestParam String endTime) throws Exception {
        RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);
        LocalDateTime sTime = DateAndTime.stringToLocalDateTime(startTime);
        LocalDateTime eTime = DateAndTime.stringToLocalDateTime(endTime);
        JsonResponse jsonResponse;
        try {
            List<ClientRseult> clientRseults = issueBenchmarkService.getAlerts(id, sTime, eTime, requestInfo.getFoId());
            jsonResponse = new JsonResponse(true,"success",clientRseults);
            return jsonResponse;

        }
        catch (ATFetchExcepetion e){
            jsonResponse = new JsonResponse(false,e.getMessage(),null);
            return jsonResponse;
        }
        catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @GetMapping("/test")
    private List<FoEscalationTickets> getDocumentAlert(){

        DocumentationAlert documentationAlert = new DocumentationAlert();


        List<FoEscalationTickets> foEscalationTicketsList = documentationAlert.getAlert();

        return foEscalationTicketsList;
    }


}
