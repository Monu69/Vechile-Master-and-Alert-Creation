package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.clients.LocationDataSwitchingClient;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleKPIDistanceDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleStatsDTO;
import com.elogist.vehicle_master_and_alert_creation.models.constants.RedisConstant;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.elogist.vehicle_master_and_alert_creation.utils.StringConstants;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehicleKPIDistanceService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RawDistanceService rawDistanceService;

    @Autowired
    LocationDataSwitchingClient locationDataSwitchingClient;

    public Map<String, Double> getVehicleKpiDistance(List<Map<String,Object>> vehicleKpiResponseData){
        List<VehicleStatsDTO> vehicleStatsDTOList = new ArrayList<>();
        Map<String,Double> distanceMap = new HashMap<>();
        List<Map<String,Object>> vehicleKpiResponseDataList = new ArrayList<>();

        for(int i=0; i<vehicleKpiResponseData.size(); i++) {

            String redisKey = vehicleKpiResponseData.get(i).get(StringConstants.VEHICLE_ID) + "#" + vehicleKpiResponseData.get(i).get(StringConstants.FOID);
            if (redisTemplate.opsForHash().hasKey(RedisConstant.VEHICLE_KPI_DISTANCE, redisKey)) {

                String result = (String) redisTemplate.opsForHash().get(RedisConstant.VEHICLE_KPI_DISTANCE, redisKey);
                VehicleKPIDistanceDTO vehicleKPIDistanceDTO = new Gson().fromJson(result, VehicleKPIDistanceDTO.class);
                if(vehicleKpiResponseData.get(i).get(StringConstants.START_TIME) != null && vehicleKpiResponseData.get(i).get(StringConstants.END_TIME) != null){

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String resultantLDT = DateAndTime.stringToLocalDateTime((String) vehicleKpiResponseData.get(i).get(StringConstants.START_TIME)).format(formatter);
                    if(!resultantLDT.equals(vehicleKPIDistanceDTO.getStartTime())){
                        VehicleStatsDTO vehicleStatsDTO = new VehicleStatsDTO(((Double) (vehicleKpiResponseData.get(i).get(StringConstants.VEHICLE_ID))).longValue(), DateAndTime.stringToLocalDateTime((String) vehicleKpiResponseData.get(i).get(StringConstants.START_TIME)) , DateAndTime.stringToLocalDateTime((String) vehicleKpiResponseData.get(i).get(StringConstants.END_TIME)));
                        vehicleStatsDTOList.add(vehicleStatsDTO);
                        vehicleKpiResponseDataList.add(vehicleKpiResponseData.get(i));
                    }
                    else if(DateAndTime.getSecDifference(DateAndTime.stringToLocalDateTime(vehicleKPIDistanceDTO.getEndTime()) , DateAndTime.stringToLocalDateTime((String) vehicleKpiResponseData.get(i).get(StringConstants.END_TIME))) > 3600){
                        VehicleStatsDTO vehicleStatsDTO = new VehicleStatsDTO(((Double) (vehicleKpiResponseData.get(i).get(StringConstants.VEHICLE_ID))).longValue(), DateAndTime.stringToLocalDateTime((String) vehicleKpiResponseData.get(i).get(StringConstants.START_TIME)) , DateAndTime.stringToLocalDateTime((String) vehicleKpiResponseData.get(i).get(StringConstants.END_TIME)));
                        vehicleStatsDTOList.add(vehicleStatsDTO);
                        vehicleKpiResponseDataList.add(vehicleKpiResponseData.get(i));
                    }
                    else{
                        distanceMap.put(redisKey, vehicleKPIDistanceDTO.getDistance());
                    }
                }
            }
            else{
                if(vehicleKpiResponseData.get(i).get(StringConstants.START_TIME) != null && vehicleKpiResponseData.get(i).get(StringConstants.END_TIME) != null) {
                    VehicleStatsDTO vehicleStatsDTO = new VehicleStatsDTO(((Double) (vehicleKpiResponseData.get(i).get(StringConstants.VEHICLE_ID))).longValue(), DateAndTime.stringToLocalDateTime((String) vehicleKpiResponseData.get(i).get(StringConstants.START_TIME)) , DateAndTime.stringToLocalDateTime((String) vehicleKpiResponseData.get(i).get(StringConstants.END_TIME)));
                    vehicleStatsDTOList.add(vehicleStatsDTO);
                    vehicleKpiResponseDataList.add(vehicleKpiResponseData.get(i));
                }
            }
        }

        List<VehicleStatsDTO> vehicleStatsDTOS = rawDistanceService.getRawDistance(vehicleStatsDTOList);

        for(int i=0; i<vehicleStatsDTOList.size(); i++){
            String redisKey = vehicleKpiResponseDataList.get(i).get(StringConstants.VEHICLE_ID) + "#" + vehicleKpiResponseDataList.get(i).get(StringConstants.FOID);
            Gson gson = new Gson();
            Double distance = vehicleStatsDTOS.get(i).getDistance() != null ? vehicleStatsDTOS.get(i).getDistance().doubleValue() : null;
            VehicleKPIDistanceDTO vehicleKPIDistanceDTO = new VehicleKPIDistanceDTO(vehicleStatsDTOS.get(i).getStartTime(), vehicleStatsDTOS.get(i).getEndTime(), distance);
            String jsonString = gson.toJson(vehicleKPIDistanceDTO);
            redisTemplate.opsForHash().put(RedisConstant.VEHICLE_KPI_DISTANCE, redisKey, jsonString);
            distanceMap.put(redisKey, vehicleKPIDistanceDTO.getDistance());

        }

        return distanceMap;

    }


}
