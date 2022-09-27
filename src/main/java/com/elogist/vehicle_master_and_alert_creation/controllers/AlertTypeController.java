package com.elogist.vehicle_master_and_alert_creation.controllers;

import com.elogist.vehicle_master_and_alert_creation.excepetion.InvalidIDExcepetion;
import com.elogist.vehicle_master_and_alert_creation.models.DataTypeChecker;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumDataType;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumUnit;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import com.elogist.vehicle_master_and_alert_creation.models.UnitChecker;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.AlertType;
import com.elogist.vehicle_master_and_alert_creation.services.AlertTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/alertTypes")
public class AlertTypeController {

    @Autowired
    AlertTypeService alertTypeService;

    @GetMapping("/findAll")
    public JsonResponse viewAll() {
        List<AlertType> list = alertTypeService.seeAlertType();
        return new JsonResponse("Correct Data",list,true, HttpStatus.ACCEPTED);
    }

    @GetMapping("/findById")
    public JsonResponse getAlertById(@RequestParam(value="id") Integer id){
        AlertType list=alertTypeService.alertById(id);
        return new JsonResponse("correct",list,true,HttpStatus.OK);
    }


    @GetMapping("/DatatypeAndUnit")
    public JsonResponse getDatatypeAndUnit(){
        Map<String,Object> map= alertTypeService.getDatatypeAndUnit();
        return new JsonResponse("correct",map,true,HttpStatus.ACCEPTED);
    }

    @PostMapping("/insert")
    public JsonResponse insert(@RequestBody AlertType alertType) throws Exception {
        Integer result;
        try {
            result = alertTypeService.insert(alertType);
        }
        catch (InvalidIDExcepetion excepetion){
            return new JsonResponse(excepetion.getMessage(),null,false,HttpStatus.BAD_REQUEST);
        }
        catch (DataTypeChecker e) {
            return new JsonResponse(e.getMessage(),null,false,HttpStatus.BAD_REQUEST);
        }
        catch (UnitChecker e) {
            return new JsonResponse(e.getMessage(),null,false,HttpStatus.BAD_REQUEST);
        }

        if (result==1)
            return new JsonResponse("Added to your database",result,true,HttpStatus.ACCEPTED);
        else
            return new JsonResponse("Already present",result,true,HttpStatus.ALREADY_REPORTED);
    }


    @DeleteMapping("/delete/{id}")
    public JsonResponse deleteAlert(@PathVariable("id") Integer id) {
        Integer result;

        result = alertTypeService.deleteAlert(id);

        if (result==1) return new JsonResponse ("Your data has been Deleted successfully",result,true, HttpStatus.ACCEPTED);
        else return new JsonResponse("Student ID not found",result,false, HttpStatus.BAD_REQUEST);

    }
}
