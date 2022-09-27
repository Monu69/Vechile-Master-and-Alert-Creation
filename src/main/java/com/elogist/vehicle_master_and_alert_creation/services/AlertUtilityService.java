package com.elogist.vehicle_master_and_alert_creation.services;

//import com.elogist.vehicle_master_and_alert_creation.config.RedisConfig;
import com.elogist.vehicle_master_and_alert_creation.models.VehicleSuggestionDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dti.RedisHMap;
//import com.elogist.vehicle_master_and_alert_creation.repository.pstgresql.FoIssueConstraintsRepository;
//import com.elogist.vehicle_master_and_alert_creation.repository.pstgresql.FoTicketEscalationRepository;
import com.elogist.vehicle_master_and_alert_creation.models.constants.RedisConstant;
import com.elogist.vehicle_master_and_alert_creation.models.dti.VehicleOutDTI;
import com.elogist.vehicle_master_and_alert_creation.models.dto.AllVehicleDto;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleKpisColumnsDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleRecipientDto;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import java.lang.reflect.Type;

import javax.persistence.Tuple;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AlertUtilityService {

    @Autowired
    private FoTicketEscalationRepository foTicketEscalationRepository;

    @Autowired
    private FoIssueConstraintsRepository foIssueConstraintsRepository;

    @Autowired
    private FoIssueConstraintsRepository foVehicleRepository;

    @Autowired
    private FoTicketPropertiesRepository foTicketPropertiesRepository;

    @Autowired
    StatsDClient dataDogClient;

//    @Autowired
//    @Qualifier("RedisCommand")
//    redisTemplate<String,String> redisTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    @Scheduled(fixedDelayString = "${elogist.ticketEscalation}")
    public Map<String,String> getTicketEscalation()
    {
        Map<String,String> data = redisTemplate.opsForHash().entries(RedisConstant.TICKET_ESCALATION);
        if(data.size() != 0)
            return data;

        List<RedisHMap> ticketEscalation = foTicketEscalationRepository.getEscalationString();
        for(RedisHMap redisHMap : ticketEscalation) {
            redisTemplate.opsForHash().put(RedisConstant.TICKET_ESCALATION, redisHMap.getKey(), redisHMap.getValue());
        }
        redisTemplate.expire(RedisConstant.TICKET_ESCALATION,10, TimeUnit.MINUTES);
        Map<String,String> retMap = redisTemplate.opsForHash().entries(RedisConstant.TICKET_ESCALATION);
        return retMap;


    }

    @Scheduled(fixedDelayString = "${elogist.alertVehicleRecipient}")
    public Map<String,String> getAlertVehicleRecepient()
    {
        Map<String,String> data = redisTemplate.opsForHash().entries(RedisConstant.ALERT_VEHICLE_RECIPIENTS);
        if(data.size() != 0)
            return data;

        List<RedisHMap> alertvehicleRecipientsList = foTicketEscalationRepository.getAlertvehicleRecipients();
        for(RedisHMap redisHMap : alertvehicleRecipientsList) {
            redisTemplate.opsForHash().put(RedisConstant.ALERT_VEHICLE_RECIPIENTS, redisHMap.getKey(), redisHMap.getValue());
        }
        redisTemplate.expire(RedisConstant.ALERT_VEHICLE_RECIPIENTS,15, TimeUnit.MINUTES);
        Map<String,String> retMap = redisTemplate.opsForHash().entries(RedisConstant.ALERT_VEHICLE_RECIPIENTS);

        return retMap;


    }

    @Scheduled(fixedDelayString = "${elogist.issueConstraints}")
    public Map<String,String> getIssueConstraints()
    {
        Map<String,String> data = redisTemplate.opsForHash().entries(RedisConstant.ISSUE_CONSTRAINTS);

        if(data.size() != 0)
            return data;
        try {
            List<RedisHMap> issueConstraints = foIssueConstraintsRepository.getIssueConstraintString();
            for (RedisHMap redisHMap : issueConstraints) {
                redisTemplate.opsForHash().put(RedisConstant.ISSUE_CONSTRAINTS, redisHMap.getKey(), redisHMap.getValue());
            }
            redisTemplate.expire(RedisConstant.ISSUE_CONSTRAINTS,10, TimeUnit.MINUTES);
            Map<String, String> retMap = redisTemplate.opsForHash().entries(RedisConstant.ISSUE_CONSTRAINTS);
            return retMap;
        }
        catch(Exception e){
            log.error("issueConstraints exception----->" + e.getMessage());
        }

      return null;
    }

    @Scheduled(fixedDelayString = "${elogist.ticketProperties}")
    public Map<String,String> getTicketProperties()
    {
        Map<String,String> data = redisTemplate.opsForHash().entries(RedisConstant.TICKET_PROPERTIES);

        if(data.size() != 0)
            return data;

        List<RedisHMap> ticketProperties = foTicketPropertiesRepository.getTicketPropertiesString();
        for(RedisHMap redisHMap : ticketProperties)
        {
            redisTemplate.opsForHash().put(RedisConstant.TICKET_PROPERTIES,redisHMap.getKey(),redisHMap.getValue());
        }
        redisTemplate.expire(RedisConstant.TICKET_PROPERTIES,10, TimeUnit.MINUTES);

        Map<String,String>retMap = redisTemplate.opsForHash().entries(RedisConstant.TICKET_PROPERTIES);
        return retMap;
    }

    public List<Map<String,String>> getFoVehicleList(Integer foid,Integer foadminid,String search,Boolean crossFlag,Boolean global)
    {



        try {
            //List<RedisHMap> data = null;
            log.info("start data when call " + foadminid);
            List<Map<String, String>> newListOfMaps = new ArrayList<>();
            if(global == true){
                HashMap<String, String> data = (HashMap<String, String>) redisTemplate.opsForHash().entries(RedisConstant.FO_VEHICLE);

                for (Map.Entry<String, String> entry : data.entrySet()) {

                        List<Map<String, String>> entryList = new ArrayList<>();
                        Gson gson = new Gson();
                        entryList = gson.fromJson(entry.getValue(), List.class);
                        for (int i = 0; i < entryList.size(); i++) {
                            if (entryList.get(i).get("regno").contains(search.toUpperCase())) {
                                if (newListOfMaps.size() == 0) {
                                    newListOfMaps.add(entryList.get(i));
//                                    if (newListOfMaps.size() == 8) {
//                                        break;
//                                    }
                                }else{
                                    if (newListOfMaps.size() == 8) {
                                        break;
                                    }else{
                                        for (int j = 0; j < newListOfMaps.size(); j++) {
                                            if (newListOfMaps.get(j).get("regno").contains(entryList.get(i).get("regno"))) {
                                                continue;
                                            } else {
                                                newListOfMaps.add(entryList.get(i));
                                            }
                                        }
                                    }
                                }
                            }
                        }



                }
            }
            else if (global == false){
            if (crossFlag == true) {

                HashMap<String, String> CrossData = (HashMap<String, String>) redisTemplate.opsForHash().entries(RedisConstant.FO_VEHICLE);

                for (Map.Entry<String, String> entry : CrossData.entrySet()) {
                    if (entry.getKey().equals(foadminid.toString())) {
                        List<Map<String, String>> entryList = new ArrayList<>();
                        Gson gson = new Gson();
                        entryList = gson.fromJson(entry.getValue(), List.class);
                        for (int i = 0; i < entryList.size(); i++) {
                            if (entryList.get(i).get("regno").contains(search.toUpperCase())) {
                                newListOfMaps.add(entryList.get(i));
                                if (newListOfMaps.size() == 8) {
                                    break;
                                }
                            }
                        }

                    }

                }
            } else if (crossFlag == false) {

                HashMap<String, String> data = (HashMap<String, String>) redisTemplate.opsForHash().entries(RedisConstant.FO_VEHICLE);

                for (Map.Entry<String, String> entry : data.entrySet()) {
                    if (entry.getKey().equals(foadminid.toString())) {
                        List<Map<String, String>> entryList = new ArrayList<>();
                        Gson gson = new Gson();
                        entryList = gson.fromJson(entry.getValue(), List.class);
                        for (int i = 0; i < entryList.size(); i++) {
                            if (entryList.get(i).get("regno").contains(search.toUpperCase())) {
                                newListOfMaps.add(entryList.get(i));
                                if (newListOfMaps.size() == 8) {
                                    break;
                                }
                            }
                        }

                    }

                }
            }
        }
            log.info("end data when call " +newListOfMaps.size());

            return newListOfMaps;

        }
        catch(Exception e){
            log.error("error data when call "+e.getMessage() );
        }

        return null;
    }

    public String saveFoVehicleList()
    {

        try {
            LocalDateTime start = LocalDateTime.now();
            List<RedisHMap> vehicleList = foVehicleRepository.getFoVehicleList(0,0,false);

            for(RedisHMap redisHMap : vehicleList)
            {
                redisTemplate.opsForHash().put(RedisConstant.FO_VEHICLE,redisHMap.getKey(),redisHMap.getValue());
            }
            redisTemplate.expire(RedisConstant.FO_VEHICLE,90, TimeUnit.MINUTES);

            List<RedisHMap> vehicleListWithCross = foVehicleRepository.getFoVehicleList(0,0,true);
            for(RedisHMap redisHCrossMap : vehicleListWithCross)
            {
                redisTemplate.opsForHash().put(RedisConstant.FO_VEHICLE_CROSS,redisHCrossMap.getKey(),redisHCrossMap.getValue());
            }
            redisTemplate.expire(RedisConstant.FO_VEHICLE_CROSS,90, TimeUnit.MINUTES);

            Map<String,String>retMap = redisTemplate.opsForHash().entries(RedisConstant.FO_VEHICLE);
            Map<String,String>resCrossMap = redisTemplate.opsForHash().entries(RedisConstant.FO_VEHICLE_CROSS);

            Duration elapsedTime = Duration.between(start,LocalDateTime.now());

            dataDogClient.time("RedisVehicleSave.time",elapsedTime.toSeconds(), "Data:timeInSec");
            return "success";

        }
        catch(Exception e){
            log.error("Scheduled Service Save Search Vehicle error ---  "+e.getMessage());
        }

        return null;
    }

    public  String saveVehicleWithRedis(Integer vehID,String vehicleNo)
    {
        log.info("Save Vehicle with redis service started");
        try {
            List<RedisHMap> ticketProperties = foVehicleRepository.getFoVehicleList(0,vehID,false);
            for(int i=0;i<ticketProperties.size();i++)
            {
                    redisTemplate.opsForHash().put(RedisConstant.FO_VEHICLE,ticketProperties.get(i).getKey(),ticketProperties.get(i).getValue());
            }
            log.info("vehicleid--->"+vehID+"regno--->"+vehicleNo);
            redisTemplate.opsForHash().put(RedisConstant.ALL_VEHICLE,vehID.toString(),vehicleNo);
            log.info("Save Vehicle With Redis Service Finished ");
            return "Success";
        }
        catch(Exception e){
            log.error("error in saving vehicle---->"+e.getMessage());
        }

        return null;
    }

    public  String saveAllVehicleWithRedis()
    {
log.info("Save All Vehicle Service Started");
        try {

            List<Map<String,Object>> vehicleList = foVehicleRepository.getCompleteVehiclelist();
            for(int i=0;i<vehicleList.size();i++){
                redisTemplate.opsForHash().put(RedisConstant.ALL_VEHICLE,vehicleList.get(i).get("id").toString(),vehicleList.get(i).get("regno"));
            }
            redisTemplate.expire(RedisConstant.ALL_VEHICLE,90,TimeUnit.MINUTES);
log.info("Save All Vehicle Service Finished with vehicle list----->"+vehicleList.size());
            return "Success";

        }
        catch(Exception e){
            log.error("saveAllVehicleWithRedis exception---");
        }

        return null;
    }

    public List<VehicleSuggestionDTO> getAllVechiclelist(String searchValue){
    try{

        Map<String,String> data = (HashMap<String, String>) redisTemplate.opsForHash().entries(RedisConstant.ALL_VEHICLE);
        log.info("Data Searched Complete");
        return data.keySet().stream().map(el -> {
             return new VehicleSuggestionDTO(Long.parseLong(el),data.get(el));
        }).filter(reg-> ((String) reg.getRegno()).contains(searchValue.toUpperCase())).limit(8).collect(Collectors.toList());

    }catch (Exception e){
        log.error("Search error ---->"+e.getMessage());
        throw e;
    }
    }

public  String saveNewVehicleInRedisAllVehicle() {
    try {
        List<Map<String,Object>> data = foVehicleRepository.addNewVehicleInRedis();
        for(int i=0;i<data.size();i++){
        if (data.get(i).get("is_deleted").equals(0)){
            log.info("Save id  is--->"+data.get(i).get("id")+"and regno is--->"+data.get(i).get("regno"));
            redisTemplate.opsForHash().put(RedisConstant.ALL_VEHICLE,data.get(i).get("id").toString(),data.get(i).get("regno"));
        }else if(data.get(i).get("is_deleted").equals(1)){
            log.info("Deleted id  is--->"+data.get(i).get("id")+"and regno is--->"+data.get(i).get("regno"));
            redisTemplate.opsForHash().delete(RedisConstant.ALL_VEHICLE,data.get(i).get("id").toString());

        }else{
        log.info("List is empty--->"+data.size());}
        }
        return "Success";
    } catch (Exception e) {
        log.error("error in save new all vehicle"+e.getMessage());
        throw e;
    }
}
}
