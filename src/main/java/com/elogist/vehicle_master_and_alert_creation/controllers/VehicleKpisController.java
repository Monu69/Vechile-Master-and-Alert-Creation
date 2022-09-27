package com.elogist.vehicle_master_and_alert_creation.controllers;

import com.elogist.vehicle_master_and_alert_creation.excepetion.HeadersMissingExcepetion;
import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.models.VehicleSuggestionDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import com.elogist.vehicle_master_and_alert_creation.models.dto.RequestInfo;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleKpiResponseDTO;
import com.elogist.vehicle_master_and_alert_creation.services.RequestInfoService;
import com.elogist.vehicle_master_and_alert_creation.services.TripEndEventService;
import com.elogist.vehicle_master_and_alert_creation.services.VehicleKpisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class VehicleKpisController {

    @Autowired
    RequestInfoService requestInfoService;

    @Autowired
    VehicleKpisService vehicleKpisService;

    @Autowired
    TripEndEventService tripEndEventService;

    @GetMapping("/vehicleKpis")
    public JsonResponse getVehicleKpis(@RequestHeader HttpHeaders headers){
        try {
            RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

            if (requestInfo.getAdUser() == null) {
                JsonResponse jsonResponse = new JsonResponse(true, "User Unspecified!!", requestInfo.getAdUser());
            }
            // List<Map<String,Object>> response = vehicleKpisService.getVehicleKpis(requestInfo);

            VehicleKpiResponseDTO response = vehicleKpisService.getVehicleKpis(requestInfo);
            JsonResponse jsonResponse = new JsonResponse(true, "Success", response);

            return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }
    @GetMapping("/saveFoVehicleList")
    public JsonResponse saveFoVehicleList(@RequestHeader HttpHeaders headers){
        try {

        RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

        if(requestInfo.getAdUser() == null){
            JsonResponse jsonResponse = new JsonResponse(true,"User Unspecified!!", requestInfo.getAdUser());
        }

        String response = vehicleKpisService.saveFoVehicleList();
        JsonResponse jsonResponse = new JsonResponse(true,"Success", response);

        return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }
    @GetMapping("/getFoVehicleList")
    public JsonResponse getFoVehicleList(@RequestHeader HttpHeaders headers,
                                         @RequestParam String search){
        try {

        RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

        if(requestInfo.getAdUser() == null){
            JsonResponse jsonResponse = new JsonResponse(true,"User Unspecified!!", requestInfo.getAdUser());
        }

        List<Map<String, String>> response = vehicleKpisService.getFoVehicleList(requestInfo.getFoId(),requestInfo.getFoAdminId(),search,false,false);
        JsonResponse jsonResponse = new JsonResponse(true,"Success", response);

        return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }

    @GetMapping("/saveVehicleWithRedis")
    public JsonResponse getFoVehicleList(@RequestHeader HttpHeaders headers,
                                         @RequestParam Integer vehicleId,
                                         @RequestParam String regNo){
        try {

        RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

        if(requestInfo.getAdUser() == null){
            JsonResponse jsonResponse = new JsonResponse(true,"User Unspecified!!", requestInfo.getAdUser());
        }

        String response = vehicleKpisService.saveVehicleWithRedis(vehicleId,regNo);
        JsonResponse jsonResponse = new JsonResponse(true,"Success", response);

        return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }
    @GetMapping("/getFoVehicleListWithCross")
    public JsonResponse getFoVehicleListWithCross(@RequestHeader HttpHeaders headers,
                                                  @RequestParam String search,
                                                  @RequestParam Boolean Crossflag){
        try {

            RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

            if(requestInfo.getAdUser() == null){
                JsonResponse jsonResponse = new JsonResponse(true,"User Unspecified!!", requestInfo.getAdUser());
            }

            List<Map<String, String>> response = vehicleKpisService.getFoVehicleList(requestInfo.getFoId(),requestInfo.getFoAdminId(),search,Crossflag, false);
            JsonResponse jsonResponse = new JsonResponse(true,"Success", response);

            return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }
    @GetMapping("/getFoVehicleListGlobal")
    public JsonResponse getFoVehicleListGlobal(@RequestHeader HttpHeaders headers,
                                         @RequestParam String search){
        try {

            RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

            if(requestInfo.getAdUser() == null){
                JsonResponse jsonResponse = new JsonResponse(true,"User Unspecified!!", requestInfo.getAdUser());
            }

            List<Map<String, String>> response = vehicleKpisService.getFoVehicleList(requestInfo.getFoId(),requestInfo.getFoAdminId(),search,false,true);
            JsonResponse jsonResponse = new JsonResponse(true,"Success", response);

            return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }

    @GetMapping("/saveAllVehicleRedis")
    public JsonResponse saveAllVehichleredis(@RequestHeader HttpHeaders headers){
        try {

            RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

            if(requestInfo.getAdUser() == null){
                JsonResponse jsonResponse = new JsonResponse(true,"User Unspecified!!", requestInfo.getAdUser());
            }

            String response = vehicleKpisService.saveAllVehicleList();
            JsonResponse jsonResponse = new JsonResponse(true,"Success", response);

            return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }

    @GetMapping("/getAllVehicleRedis")
    public JsonResponse getAllVehicleListRedis(@RequestHeader HttpHeaders headers,@RequestParam String search){
        try {

            RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

            if(requestInfo.getAdUser() == null){
                JsonResponse jsonResponse = new JsonResponse(true,"User Unspecified!!", requestInfo.getAdUser());
            }

            List<VehicleSuggestionDTO> response = vehicleKpisService.getAllVehicleList(search);
            JsonResponse jsonResponse = new JsonResponse(true,"Success", response);

            return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }

    @GetMapping("/tripEndEvent")
    public JsonResponse getTripEndEvent(@RequestParam("tripIds") String tripIds){

        if(tripIds != null) {

            tripEndEventService.tripEndEvent(tripIds);

            JsonResponse jsonResponse = new JsonResponse(true, "Success", null);

            return jsonResponse;
        }
        else{

            JsonResponse jsonResponse = new JsonResponse(false, "tripIds must not be null", null);

            return jsonResponse;

        }
    }
    @GetMapping("/saveNewVehicleInRedis")
    public JsonResponse saveNewVehicleInAllVehicleRedis(@RequestHeader HttpHeaders headers){
        try {

            RequestInfo requestInfo = requestInfoService.getRequestInfo(headers);

            if(requestInfo.getAdUser() == null){
                JsonResponse jsonResponse = new JsonResponse(true,"User Unspecified!!", requestInfo.getAdUser());
            }

            String response = vehicleKpisService.saveNewAllVehickeInRedis();
            JsonResponse jsonResponse = new JsonResponse(true,"Success", response);

            return jsonResponse;
        }
        catch (HeadersMissingExcepetion e){
            JsonResponse jsonResponse = new JsonResponse(e.getMessage(), null, false, HttpStatus.FORBIDDEN);
            return jsonResponse;
        }
    }
}
