package com.elogist.vehicle_master_and_alert_creation.models.dto;


import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumDataType;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumUnit;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class JsonResponse<T> {



    private boolean success;

    private String msg;

    private T data;

    private HttpStatus status;

    private Integer code;

   // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private String timeStamp;

    private String debugMessage;

    private JsonResponse()
    {
        timeStamp = LocalDateTime.now().toString();
    }

    public JsonResponse(boolean success,String msg,T data,HttpStatus status,Integer code)
    {
        this();
        this.success = success;
        this.msg = msg;
        this.data = data;
        this.status = HttpStatus.OK;
        this.code = code;
    }


    public JsonResponse(HttpStatus status)
    {
        this();
        this.status = status;
    }



    public JsonResponse(boolean success, String msg, T data)
    {
        this();
        this.success = success;
        this.msg = msg;
        this.data = data;
        this.status = success == true?HttpStatus.OK:HttpStatus.INTERNAL_SERVER_ERROR;
        this.code = success == true?1:-99;
    }

    public JsonResponse(HttpStatus status,Throwable ex)
    {
        this();
        this.success=false;
        this.status=status;
        this.msg = "Unexpected error";
        this.data=null;
        this.debugMessage = ex.getLocalizedMessage();
    }


    public JsonResponse(HttpStatus status,String msg, Throwable ex)
    {
        this();
        this.success = false;
        this.status = status;
        this.msg = msg;
        this.debugMessage = ex.getLocalizedMessage();
    }

    public JsonResponse(String msg, T data, boolean success, HttpStatus Status) {
        this.msg = msg;
        this.data = data;
        this.success = success;
        this.status = Status;
    }


}
