package com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573;


import com.elogist.vehicle_master_and_alert_creation.clients.MTRClient;
import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.AlertEventHandler;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.client.IJKLCAuth;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.client.IJKLCTripComplete;
import com.elogist.vehicle_master_and_alert_creation.models.alerteventhandler.fo30573.dtos.AuthDTO;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.ClientApiLogRepository;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AEHAPI30573AE2 extends AlertEventHandler {

    @Autowired
    MTRClient mtrClient;

    @Autowired
    IJKLCAuth ijklcAuth;

    @Autowired
    IJKLCTripComplete ijklcTripComplete;

    @Autowired
    ClientApiLogRepository clientApiLogRepository;

    @Value("${alert.fo30573.AEHAPI30573AE.authKey}")
    private String authKey;

    @Value("${alert.fo30573.AEHAPI30573AE.scope}")
    private String scope;

    @Value("${alert.fo30573.AEHAPI30573AE.grantType}")
    private String grantType;

    @Value("${alert.fo30573.AEHAPI30573AE.authBasic}")
    private String authBasic;

    @Value("${alert.fo30573.AEHAPI30573AE2.apiType}")
    private String apiType;

    @Value("${alert.fo30573.AEHAPI30573AE2.foAdminId}")
    private Integer foAdminId;

    Integer entryMode = 1;
    Double version = 1.0;
    Integer foId = 30573;
    Integer dataType = 0;
    Integer forPushApi = 1;
    Integer rangeType = 0;
    Integer multipeAccount = -1;

    public Boolean isTriggered(MasterTableTemp1 master){
        Map<String,String> reqBody = new HashMap<>();
        String response = "";

        reqBody.put("scope",scope);
        reqBody.put("grant_type",grantType);

        AuthDTO authDTO = ijklcAuth.getAuth(reqBody,authBasic);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startTime = master.getVtStartTime().minusDays(1).format(formatter);
        String endTime = master.getVsStartTime().plusDays(1).format(formatter);
        try {
            Map<String, Object> feignResult = mtrClient.getMTRData(authKey, entryMode, version, master.getVId(), startTime, endTime, "", "", "", "", "", "", dataType, forPushApi, rangeType, master.getVtId(), foAdminId, multipeAccount, version);
            Map<String,Object> dataInterMediate = (Map<String,Object>) feignResult.get("data");
            Type types = new TypeToken<ArrayList<Map<String,Object>>>() {
            }.getType();
            if (dataInterMediate.get("y_data") != null) {
                ArrayList<Map<String,Object>> data = new Gson().fromJson(dataInterMediate.get("y_data").toString(),types);
                List<Map<String,Object>> mtrData = new ArrayList<>();

                for(int i=0; i<data.size(); i++){
                    Map<String,Object> mtrMap = new HashMap<>();
                    for(String mtrKeys : data.get(i).keySet()){
                        if(!mtrKeys.startsWith("_")){
                            mtrMap.put(mtrKeys, data.get(i).get(mtrKeys));
                            mtrData.add(mtrMap);
                        }
                    }
                }

                mtrData.get(0).put("EventName","ETA");
                String clientResponse;
                clientResponse = ijklcTripComplete.sendTripComplete("Bearer " + authDTO.getAccessToken(), mtrData.get(0));
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("clientResponse",clientResponse);
                responseMap.put("payload",mtrData.get(0));
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .create();
                response = gson.toJson(responseMap);
            } else {
                response = "MTR Data not fetched for tripId-->" + " " + master.getVtId() + " vehicleId-->" + master.getVId() + " " + " foAdminId-->" + foAdminId + "mtr data --> " + feignResult.toString()+" mtrinputs ((authKey, entryMode, version, master.getVId(), startTime, endTime, dataType, forPushApi, rangeType, master.getVtId(), foAdminId, multipeAccount, version)) -->" + (authKey+","+entryMode+","+version+","+master.getVId()+","+startTime+","+endTime+","+dataType+","+forPushApi+","+rangeType+","+master.getVtId()+","+foAdminId+","+multipeAccount+","+version);
            }
        }
        catch (Throwable e){
            response = e.getMessage() + " tripId-->" + master.getVtId() + " vehicleId-->" + master.getVId() + " " + " foAdminId-->" + foAdminId;
        }
        ClientAPILogsModel clientAPILogsModel = new ClientAPILogsModel(foId,apiType, LocalDateTime.now(),1,response);
        clientApiLogRepository.save(clientAPILogsModel);
        return true;
    }
}
