package com.elogist.vehicle_master_and_alert_creation.services;

//import org.apache.coyote.RequestInfo;
//import org.springframework.beans.factory.annotation.Autowired;

//import java.net.http.HttpHeaders;
import com.elogist.vehicle_master_and_alert_creation.excepetion.HeadersMissingExcepetion;
import com.elogist.vehicle_master_and_alert_creation.models.dto.RequestInfo;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.ClientApiLogRepository;
import com.elogist.vehicle_master_and_alert_creation.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
@Service
public class RequestInfoService {


    @Autowired
    ClientApiLogRepository clientApiLogRepository;

    public RequestInfo getRequestInfo(HttpHeaders headers){
        try {
            RequestInfo requestInfo = new RequestInfo();
            Integer entryMode = Integer.parseInt(headers.get("entryMode").get(0));
            Integer adUser = JWTUtil.getAdUser(headers.get("authKey").get(0));
            Integer foAdminId = null;
            Integer foId;
            if (entryMode == 3) {
                if (headers.containsKey("foAdminId"))
                    try {
                        foAdminId = Integer.parseInt(headers.get("foAdminId").get(0));
                    } catch (Exception e) {
                        foAdminId = adUser;
                    }
                else
                    foAdminId = adUser;
            } else {
                foAdminId = Integer.parseInt(headers.get("foAdminId").get(0));
            }
            foId = clientApiLogRepository.findFoidByFoAdminId(foAdminId);

            requestInfo.setEntryMode(entryMode);
            requestInfo.setAdUser(adUser);
            requestInfo.setFoAdminId(foAdminId);
            requestInfo.setFoId(foId);

            return requestInfo;
        }
        catch (Exception e){

            String message = "Headers is Missing";
            HeadersMissingExcepetion headersMissingExcepetion = new HeadersMissingExcepetion(message);
            throw headersMissingExcepetion;
        }
    }
}
