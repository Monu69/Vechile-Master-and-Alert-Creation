package com.elogist.vehicle_master_and_alert_creation.utils;

import com.elogist.vehicle_master_and_alert_creation.excepetion.ATFetchExcepetion;
import com.elogist.vehicle_master_and_alert_creation.models.ApiError;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Validated
@ControllerAdvice
public class RestExcepetionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        JsonResponse jsonResponse = new JsonResponse(false,ex.getMessage(),null);
        return new ResponseEntity<>(jsonResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//        String error = "Missing Parameter";
//        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "coloum:" +"  "+((MethodArgumentTypeMismatchException) ex).getName()+ "  " + ex.getMessage().split(";")[0];
        JsonResponse jsonResponse = new JsonResponse(false,error,null);
        return new ResponseEntity<>(jsonResponse, BAD_REQUEST);
//        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(value = { SQLException.class })
    public ResponseEntity<Object> handleSQLException(SQLException e){
        String error = "Db Querying Exception";
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,error,e));
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        JsonResponse jsonResponse = new JsonResponse(false,e.getMessage(),null);
        return new ResponseEntity<>(jsonResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//        String error = "Validation error";
//        return buildResponseEntity(new ApiError(BAD_REQUEST,error,e));
    }

    @ExceptionHandler(value = ATFetchExcepetion.class)
    public ResponseEntity<Object> exception(ATFetchExcepetion exception) {
        JsonResponse jsonResponse = new JsonResponse(false, exception.getMessage(), null);
        return new ResponseEntity<>(jsonResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
