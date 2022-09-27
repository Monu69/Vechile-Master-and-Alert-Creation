package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.clients.LocationDataSwitchingClient;
import com.elogist.vehicle_master_and_alert_creation.clients.SiteClient;
import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.dto.*;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.elogist.vehicle_master_and_alert_creation.utils.StringConstants;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class VehicleKpisService {

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    LocationDataSwitchingClient locationDataSwitchingClient;

    @Autowired
    SiteClient siteClient;

    @Autowired
    VehicleKPIDistanceService vehicleKPIDistanceService;

    @Autowired
    RawDistanceService rawDistanceService;

    @Autowired
    RedisTemplate redisTemplate;


    @Autowired
    private AlertUtilityService alertUtilityService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleKpisService.class);

    public VehicleKpiResponseDTO getVehicleKpis(RequestInfo requestInfo){
        Boolean output = false;
        Boolean isCalDis = false;
        Integer type;
        Map<String,List<Map<String,Object>>> response = new HashMap<>();
        List<Point> points = new ArrayList<>();
        List<VehicleStatsDTO> distances = new ArrayList<>();
        Map<Long, Integer> distanceMap = new HashMap<>();
        LocalDate startTime = LocalDateTime.now().toLocalDate();
        VehicleKpiResponseDTO vehicleKpiResponseDTO = getVehicleKpisResponse(requestInfo.getFoAdminId());
        Map<String,Double> vehicleDisMap = new HashMap<>();

        for(int i=0; i<vehicleKpiResponseDTO.getColumns().size(); i++) {
            if (vehicleKpiResponseDTO.getColumns().get(i).getColName().equals(StringConstants.X_KMPH)) {
                output = true;
            }
            if (vehicleKpiResponseDTO.getColumns().get(i).getColName().equals(StringConstants.ACTUAL_DISTANCE) || vehicleKpiResponseDTO.getColumns().get(i).getColName().equals(StringConstants.DISTANCE_COVERED_PERCENTAGE)) {
                isCalDis = true;
            }


        }
        if(output){
           distances = getMultipleVehicleDistances(requestInfo.getFoId(), startTime, requestInfo.getFoAdminId());
        }

        if(isCalDis){
            vehicleDisMap = vehicleKPIDistanceService.getVehicleKpiDistance(vehicleKpiResponseDTO.getData());
        }

        for(int i=0; i<distances.size(); i++){
            distanceMap.put(distances.get(i).getVehicleId(), distances.get(i).getDistance());
        }
        for(int i=0;i<vehicleKpiResponseDTO.getData().size();i++){
                Point point = new Point((Double) vehicleKpiResponseDTO.getData().get(i).get(StringConstants.X_TLAT), (Double) vehicleKpiResponseDTO.getData().get(i).get(StringConstants.X_TLONG));
                points.add(point);

        }

        String authkey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6NDUsIm5hbWUiOiJNYWhlc2ggR3VwdGEiLCJtb2JpbGVubyI6NzMwMDI2MDU1NSwiZW1haWwiOm51bGwsInRpbWUiOiIyMDIyLTAyLTI1VDExOjA5OjEyKzA1OjMwIn0.Z6lHmhYIlpwnF1KhdNWM036IS2EKv7wWKq-YwiH0n_M";
        Gson gson = new Gson();
        List<GPSMongo> gpsData = new ArrayList<>();
        String jsonPoints = gson.toJson(points);
        try {
           gpsData = getSiteClient(requestInfo.getFoAdminId(), jsonPoints);
        }
        catch (Exception e){
            gpsData = new ArrayList<>();
        }

        List<String> requireList = new ArrayList<>();
        requireList = getRequiredConstantList();



        for(int i=0; i < vehicleKpiResponseDTO.getData().size();i++){
            try {
                String redisKey = vehicleKpiResponseDTO.getData().get(i).get(StringConstants.VEHICLE_ID) + "#" + vehicleKpiResponseDTO.getData().get(i).get(StringConstants.FOID);
                String showtripend = vehicleKpiResponseDTO.getData().get(i).get(StringConstants.SHOW_TRIP_END).toString().split(",")[0];
                String showtripstart = vehicleKpiResponseDTO.getData().get(i).get(StringConstants.SHOW_TRIP_START).toString().split(",")[0];
                vehicleKpiResponseDTO.getData().get(i).put(StringConstants.SHOW_TRIP_END, showtripend);
                vehicleKpiResponseDTO.getData().get(i).put(StringConstants.SHOW_TRIP_START, showtripstart);
                try {
                    vehicleKpiResponseDTO.getData().get(i).put(StringConstants.ADDRESS, gpsData.get(i).getAddress());
                    vehicleKpiResponseDTO.getData().get(i).put(StringConstants.CITY, gpsData.get(i).getAddress());
                }
                catch (Exception e){
                    vehicleKpiResponseDTO.getData().get(i).put(StringConstants.ADDRESS, "NA");
                    vehicleKpiResponseDTO.getData().get(i).put(StringConstants.CITY, "NA");
                }
                try {
                    if(vehicleDisMap.containsKey(redisKey)) {
                        Integer distanceCoveredPercentage = getDistanceCoveredPercentage((Double) vehicleKpiResponseDTO.getData().get(i).get(StringConstants.X_SAP_DIST),  vehicleDisMap.get(redisKey));
                        vehicleKpiResponseDTO.getData().get(i).put(StringConstants.ACTUAL_DISTANCE, vehicleDisMap.get(redisKey) + " Km.");
                        vehicleKpiResponseDTO.getData().get(i).put(StringConstants.DISTANCE_COVERED_PERCENTAGE, distanceCoveredPercentage + " %");
                        vehicleKpiResponseDTO.getData().get(i).put(StringConstants.X_SAP_DIST, vehicleKpiResponseDTO.getData().get(i).get(StringConstants.X_SAP_DIST) + " Km.");

                    }
                }
                catch (Exception e){
                    vehicleKpiResponseDTO.getData().get(i).put(StringConstants.ACTUAL_DISTANCE, null);
                    vehicleKpiResponseDTO.getData().get(i).put(StringConstants.DISTANCE_COVERED_PERCENTAGE, null);
                }

                type = tripStatusType(((Double) (vehicleKpiResponseDTO.getData().get(i).get(StringConstants.PRIM_STATUS))).intValue(), ((List<Double>) (vehicleKpiResponseDTO.getData().get(i).get(StringConstants.P_PLACEMENT_TYPE))));
                vehicleKpiResponseDTO.getData().get(i).put(StringConstants.TRIP_STATUS_TYPE, type);


                if (distances.size() > 0) {
                    vehicleKpiResponseDTO.getData().get(i).put(StringConstants.X_KMPH, distanceMap.get(((Double)(vehicleKpiResponseDTO.getData().get(i).get(StringConstants.X_VEHICLE_ID))).longValue()));

                } else {
                    vehicleKpiResponseDTO.getData().get(i).put(StringConstants.X_KMPH, null);

                }
            }
            catch (Exception e){
                log.error("VehicleKPI Exception" + e.getMessage() + " " + i);
            }
        }
        Map<String,Integer> kpisMap = new HashMap<>();
        for(int i=0; i<vehicleKpiResponseDTO.getColumns().size(); i++){
            kpisMap.put(vehicleKpiResponseDTO.getColumns().get(i).getColTitle(), vehicleKpiResponseDTO.getColumns().get(i).getColOrder());
        }
        List<Map<String,Object>> finalresponse = new ArrayList<>();

        if(vehicleKpiResponseDTO.getData().size()>0){
            for (int i=0; i<vehicleKpiResponseDTO.getData().get(0).keySet().size();i++){
                if(((String)vehicleKpiResponseDTO.getData().get(0).keySet().toArray()[i]).startsWith("x_"))
                kpisMap.put((String)vehicleKpiResponseDTO.getData().get(0).keySet().toArray()[i],i+1000);
            }
        }

        for(int i=0; i<vehicleKpiResponseDTO.getData().size(); i++){
            TreeMap<String, Object> dataMap = new TreeMap<>(Comparator.comparing(x -> (kpisMap.containsKey(x)?kpisMap.get(x):0)));
            for(String key : vehicleKpiResponseDTO.getData().get(i).keySet()){
                if(key.startsWith(StringConstants.BEGIN_WITH)){
                    dataMap.put(key,vehicleKpiResponseDTO.getData().get(i).get(key));
                }
            }
            for(int j=0;j<vehicleKpiResponseDTO.getColumns().size();j++){


                if(requireList.contains(vehicleKpiResponseDTO.getColumns().get(j).getColName())) {
                    dataMap.put(vehicleKpiResponseDTO.getColumns().get(j).getColTitle(), vehicleKpiResponseDTO.getData().get(i).get(vehicleKpiResponseDTO.getColumns().get(j).getColName()));
                }
                else{
                    dataMap.put(vehicleKpiResponseDTO.getColumns().get(j).getColTitle(), vehicleKpiResponseDTO.getData().get(i).get(vehicleKpiResponseDTO.getColumns().get(j).getColTitle()));
                }

            }

            finalresponse.add(dataMap);

        }
        VehicleKpiResponseDTO vehicleKpiResponseDTO1 = new VehicleKpiResponseDTO(finalresponse, vehicleKpiResponseDTO.getColumns());
        return vehicleKpiResponseDTO1;

    }

    public List<VehicleStatsDTO> getMultipleVehicleDistances(Integer foid, LocalDate sTime, Integer foAdminId){


        LocalDateTime startTime = LocalDateTime.of(sTime, LocalTime.of(0,0,0));
        LocalDateTime endTime = LocalDateTime.now().withNano(0);
        List<VehicleStatsDTO> vehicleStatsDTOList = new ArrayList<>();
        List<Long> vehIds = masterTableTemp1Repository.getAllVehicleForFo(foAdminId);

        for(int i=0; i<vehIds.size(); i++){
            VehicleStatsDTO vehicleStatsDTO = new VehicleStatsDTO(vehIds.get(i), startTime, endTime);
            vehicleStatsDTOList.add(vehicleStatsDTO);
        }

        List<VehicleStatsDTO> response = rawDistanceService.getRawDistance(vehicleStatsDTOList);

        return response;
    }

    public List<String> getRequiredConstantList(){

        List<String> requireList = new ArrayList<>();
        requireList.add(StringConstants.SHOW_TRIP_END);
        requireList.add(StringConstants.SHOW_TRIP_START);
        requireList.add(StringConstants.ADDRESS);
        requireList.add(StringConstants.CITY);
        requireList.add(StringConstants.ACTUAL_DISTANCE);
        requireList.add(StringConstants.DISTANCE_COVERED_PERCENTAGE);
        requireList.add(StringConstants.X_SAP_DIST);
        requireList.add(StringConstants.TRIP_STATUS_TYPE);
        requireList.add(StringConstants.X_KMPH);

        return requireList;
    }

    public Integer tripStatusType(Integer status, List<Double> placement){

        switch (status){
            case 11:
                return 0;//"At Origin";
            case 12:
                return 1;//"At Destination";

            case 13:
            case 14:
            case 51:
            case 52:
            case 53:
                return 2;//"Onward";

            case 20:
            case 21:
                if(placement != null && placement.size() > 0)
                    return 4;//"Available Next";
                return 3;//"Available Done";

            case 15:
            case 22:
            case 23:
                return 5;//"Available Move";

            default:
                return 6;//"Ambiguous" 1, 61, 71, 81;
        }
    }

    public VehicleKpiResponseDTO getVehicleKpisResponse(Integer foAdminId){
        List<Map<String,Object>> datas = masterTableTemp1Repository.getVehicleKpis1(foAdminId);
        Gson gson = new Gson();
        Type resultType = new TypeToken<List<Map<String, Object>>>(){}.getType();
        Type type1 = new TypeToken<List<VehicleKpisColumnsDTO>>(){}.getType();
        String dataString = (String) datas.get(0).get("y_data");
        List<Map<String,Object>> result = gson.fromJson(dataString, resultType);
        String jsonString1 = (String) datas.get(0).get("y_columns");
        List<VehicleKpisColumnsDTO> vehicleKpisColumnsDTOS = gson.fromJson(jsonString1, type1);
        VehicleKpiResponseDTO vehicleKpiResponseDTO = new VehicleKpiResponseDTO(result, vehicleKpisColumnsDTOS);
        return vehicleKpiResponseDTO;
    }

    public List<GPSMongo> getSiteClient(Integer foAdminId, String points){
        Gson gson = new Gson();
        List<GPSMongo> gpsData = new ArrayList<>();
        String authkey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6NDUsIm5hbWUiOiJNYWhlc2ggR3VwdGEiLCJtb2JpbGVubyI6NzMwMDI2MDU1NSwiZW1haWwiOm51bGwsInRpbWUiOiIyMDIyLTAyLTI1VDExOjA5OjEyKzA1OjMwIn0.Z6lHmhYIlpwnF1KhdNWM036IS2EKv7wWKq-YwiH0n_M";
        String gpsDatajson = siteClient.getReverseGeoCodedLocation(1, foAdminId, authkey, points);
        JsonResponse jsonResponse = gson.fromJson(gpsDatajson, JsonResponse.class);
        String jsonString = gson.toJson(jsonResponse.getData());
        Type types = new TypeToken<List<GPSMongo>>() {
        }.getType();
        gpsData = gson.fromJson(jsonString, types);
        return gpsData;
    }

    public Integer getDistanceCoveredPercentage(Double googleDis, Double actualDis){
        Integer distanceCoveredPercentage = null;

        if(googleDis != null && actualDis != null) {
            if (googleDis <= 0) {
                distanceCoveredPercentage = 100;
            } else if(actualDis <= 0){
                distanceCoveredPercentage = 0;
            } else {
                distanceCoveredPercentage = ((actualDis.intValue() * 100) / googleDis.intValue());
            }


        }
        return distanceCoveredPercentage;
    }

    public List<Map<String,String>> getFoVehicleList(Integer foid,Integer foadminid,String search,Boolean crossFlag,Boolean global) {

        List<Map<String,String>> foVehicleResult =  alertUtilityService.getFoVehicleList(foid,foadminid,search,crossFlag,global);
        return foVehicleResult;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public String saveFoVehicleList() {
        LOGGER.info("Scheduled Service Started For Save Search Vehicle");
        String foVehicleResult = alertUtilityService.saveFoVehicleList();

        LOGGER.info("Scheduled Service Ended For Save Search Vehicle");
        return foVehicleResult;
    }

    public String saveVehicleWithRedis(Integer vehid,String vehicleNo) {

        String foVehicleResult =  alertUtilityService.saveVehicleWithRedis(vehid,vehicleNo);
        return foVehicleResult;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public String saveAllVehicleList() {

        String allVehicleList =  alertUtilityService.saveAllVehicleWithRedis();
        return allVehicleList;
    }
    public List<VehicleSuggestionDTO> getAllVehicleList(String searchValue) {

        List<VehicleSuggestionDTO> foVehicleResult =  alertUtilityService.getAllVechiclelist(searchValue);
        return foVehicleResult;
    }
    @Scheduled(cron = "0 */10 * * * *")
    public String saveNewAllVehickeInRedis() {

        String saveVehicle =  alertUtilityService.saveNewVehicleInRedisAllVehicle();
        return saveVehicle;
    }
}
