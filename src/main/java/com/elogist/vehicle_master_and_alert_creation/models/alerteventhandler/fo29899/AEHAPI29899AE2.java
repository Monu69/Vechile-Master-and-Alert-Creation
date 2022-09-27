package com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo29899;

import com.elogist.vehicle_master_and_alert_creation.models.ClientAPILogsModel;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.AlertEventHandler;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo29899.client.ISGMPLTripCreate;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.ClientApiLogRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AEHAPI29899AE2 extends AlertEventHandler {

    @Autowired
    ISGMPLTripCreate ISGMPLTripCreate;

    @Autowired
    ClientApiLogRepository clientApiLogRepository;

    @Value("${alert.fo29899.AEHAPI29899AE2.vendorAccessToken}")
    private String vendorAccessToken;

    @Value("${alert.fo29899.AEHAPI29899AE2.apiType}")
    private String apiType;

    Integer foId = 29899;

    public Boolean isTriggered(MasterTableTemp1 master){
        String data = clientApiLogRepository.getTripDataSGMPL(master.getVtId());
        Type type = new TypeToken<List<Map<String,Object>>>(){}.getType();
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        List<Map<String,String>> dataObj = gson.fromJson(data,type);
        String response = ISGMPLTripCreate.sendTripComplete(vendorAccessToken,dataObj);
        ClientAPILogsModel clientAPILogsModel = new ClientAPILogsModel(foId,apiType, LocalDateTime.now(),1,response);
        clientApiLogRepository.save(clientAPILogsModel);
        return true;
    }
}
