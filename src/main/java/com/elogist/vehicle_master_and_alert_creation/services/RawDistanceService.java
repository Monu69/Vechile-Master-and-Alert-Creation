package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.clients.LocationDataSwitchingClient;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleStatsDTO;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RawDistanceService {
    @Autowired
    LocationDataSwitchingClient locationDataSwitchingClient;

    public List<VehicleStatsDTO> getRawDistance(List<VehicleStatsDTO> vehicleStatsDTOList){
        Gson gson = new Gson();
        String responseString =  locationDataSwitchingClient.getRawDistance(vehicleStatsDTOList);
        JsonResponse jsonResponse = gson.fromJson(responseString, JsonResponse.class);
        String jsonString = gson.toJson(jsonResponse.getData());
        Type types = new TypeToken<List<VehicleStatsDTO>>(){}.getType();
        List<VehicleStatsDTO> response = gson.fromJson(jsonString, types);
        for(VehicleStatsDTO vehicleStatsDTO : response){
            if(vehicleStatsDTO.getDistance() != null) {
                vehicleStatsDTO.setDistance((vehicleStatsDTO.getDistance().intValue() / 1000));
            }
        }
        return response;
    }

}
