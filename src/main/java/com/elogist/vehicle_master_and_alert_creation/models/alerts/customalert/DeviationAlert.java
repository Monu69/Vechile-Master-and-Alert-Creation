package com.elogist.vehicle_master_and_alert_creation.models.alerts.customalert;

import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;
import com.elogist.vehicle_master_and_alert_creation.models.dto.DeviationALertDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.DeviationBenchmarkDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.DeviationParameterDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoAlertEventsRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp2Repository;
import com.elogist.vehicle_master_and_alert_creation.services.AlertProcessingService;
import com.elogist.vehicle_master_and_alert_creation.services.IssueBenchmarkService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeviationAlert {

    @Autowired
    FoAlertEventsRepository foAlertEventsRepository;

    @Autowired
    IssueBenchmarkService issueBenchmarkService;

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    MasterTableTemp2Repository masterTableTemp2Repository;

    @Autowired
    AlertProcessingService alertProcessingService;

    public static Integer alertId = 29101;

    public static final Logger LOGGER = LoggerFactory.getLogger(DeviationAlert.class);



    public List<FoEscalationTickets> getAlerts(){

        List<FoEscalationTickets> foEscalationTicketsList = new ArrayList<>();

        try {

            Map<String, List<Integer>> alertVehicleRecipientMap = issueBenchmarkService.getAlertVehicleRecipients();

            Map<Integer, Map<Integer, List<Issues>>> matrix = issueBenchmarkService.getIssueBenchmark();

            List<DeviationALertDTO> deviationAlertDTOS = getDeviationAlertDTO();

            for (DeviationALertDTO deviationALertDTO : deviationAlertDTOS) {

                FoEscalationTickets foEscalationTickets = getDeviationAlert(deviationALertDTO, alertVehicleRecipientMap, matrix);

                if (foEscalationTickets != null) {

                    foEscalationTicketsList.add(foEscalationTickets);

                }

            }

            if(foEscalationTicketsList.size() <= 0){
                LOGGER.info("Deviation Alert---->" + new Gson().toJson(deviationAlertDTOS));
            }

        }
        catch (Exception e){

            LOGGER.error("ErrorMessage--->" + e.getMessage());

        }

        return foEscalationTicketsList;
    }
    public FoEscalationTickets getDeviationAlert(DeviationALertDTO deviationALertDTO, Map<String, List<Integer>> alertVehicleRecipientMap, Map<Integer, Map<Integer, List<Issues>>> matrix){

        String redisKey = deviationALertDTO.getVehicleId() + "#" + deviationALertDTO.getVFoId();

        if (alertVehicleRecipientMap.containsKey(redisKey)) {

            List<Integer> foidList = alertVehicleRecipientMap.get(redisKey);

                for (int k = 0; k < foidList.size(); k++) {

                    if(!matrix.containsKey(foidList.get(k))){
                        continue;
                    }

                    List<Issues> issuesList = matrix.get(foidList.get(k)).get(alertId);

                    if(issuesList == null){
                        continue;
                    }

                    for (Issues issues : issuesList) {

                        String remark = isValidAlert(issues, deviationALertDTO);

                        if (remark != null) {

                            MasterTableTemp1 masterTableTemp1 = alertProcessingService.getM1ByVehicleId(deviationALertDTO.getVehicleId().intValue());

                            String benchmarks = issues.getBenchmarks();

                            String specifiedParameter = getSpecifiedParameter(deviationALertDTO);
                            String generalParameter = masterTableTemp1Repository.getGeneralParameter(deviationALertDTO.getVehicleId().intValue());

                            FoEscalationTickets foEscalationTickets = new FoEscalationTickets(deviationALertDTO, issues, remark, masterTableTemp1, foidList.get(k), specifiedParameter, generalParameter, benchmarks);
                            return foEscalationTickets;

                        }
                    }
                }
            }

        return null;
    }

    public String isValidAlert(Issues issues, DeviationALertDTO deviationALertDTO){

        if(deviationALertDTO.getVtsId() != null) {

            Integer treshHoldValue = null;

            String response;

            String benchmark = issues.getBenchmarks();

            Gson gson = new Gson();

            DeviationBenchmarkDTO deviationBenchmarkDTO = gson.fromJson(benchmark, DeviationBenchmarkDTO.class);

            if (deviationBenchmarkDTO.getMinDistance() == null && deviationBenchmarkDTO.getSO() == null && deviationBenchmarkDTO.getSTO() == null) {

                deviationBenchmarkDTO = getDefaultBenchmark();

            }
            if (deviationALertDTO.getTripType() != null && (deviationALertDTO.getTripType().equals("SO") || deviationALertDTO.getTripType().equals("STO"))) {

                if (deviationBenchmarkDTO.getSTO() != null) {

                    treshHoldValue = deviationBenchmarkDTO.getSTO();

                } else if (deviationBenchmarkDTO.getSO() != null) {

                    treshHoldValue = deviationBenchmarkDTO.getSO();

                } else {

                    treshHoldValue = deviationBenchmarkDTO.getMinDistance();

                }

                if (treshHoldValue != null) {

                    response = getRemark(deviationALertDTO, treshHoldValue);

                    return response;

                } else {

                    return null;

                }


            } else {

                if (deviationBenchmarkDTO.getMinDistance() != null) {

                    treshHoldValue = deviationBenchmarkDTO.getMinDistance();

                    response = getRemark(deviationALertDTO, treshHoldValue);

                    return response;

                } else {

                    return null;

                }

            }

        }
        else{

            String response = deviationALertDTO.getDestination() + "---> Rejected";
            return response;

        }

    }

    public DeviationBenchmarkDTO getDefaultBenchmark(){

        DeviationBenchmarkDTO deviationBenchmarkDTO = new DeviationBenchmarkDTO(1000);

        return deviationBenchmarkDTO;

    }

    public String getRemark(DeviationALertDTO deviationALertDTO, Integer threshHoldValue){

        String response = null;

        Boolean isValid = checkValidAlert(deviationALertDTO);

        if(deviationALertDTO.getDestinationDeviationInMetres() >= threshHoldValue && isValid) {

            if ((deviationALertDTO.getActDis() - deviationALertDTO.getSysDis()) >= 0) {

                response = deviationALertDTO.getDestination() + "---> Forward Deviation";


            } else if ((deviationALertDTO.getActDis() - deviationALertDTO.getSysDis()) < 0) {

                response = deviationALertDTO.getDestination() + "---> Backward Deviation";

            }
        }

        return response;
    }

    public List<DeviationALertDTO> getDeviationAlertDTO(){

        String response = foAlertEventsRepository.getDeviationAlert();

        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<DeviationALertDTO>>() {}.getType();

        List<DeviationALertDTO> deviationALertDTOS = gson.fromJson(response, type);

        return deviationALertDTOS;
    }

    public String getSpecifiedParameter(DeviationALertDTO deviationALertDTO){

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        DeviationParameterDTO deviationParameterDTO = new DeviationParameterDTO(deviationALertDTO);

        String specifiedParameter = gson.toJson(deviationParameterDTO);

        return specifiedParameter;

    }

    public Boolean checkValidAlert(DeviationALertDTO deviationALertDTO){

        Long duration = null;

        if(deviationALertDTO.getLastUnloadingExitTime() != null){

            duration = DateAndTime.getMinDifference(DateAndTime.stringToLocalDateTime(deviationALertDTO.getLastUnloadingExitTime()), LocalDateTime.now().withNano(0));

        }
        else if(deviationALertDTO.getLastUnloadingEntryTime() != null){

            duration = DateAndTime.getMinDifference(DateAndTime.stringToLocalDateTime(deviationALertDTO.getLastUnloadingEntryTime()), LocalDateTime.now().withNano(0));

        }

        if(duration != null && duration > (3*24*60)){

            return false;
        }

        return true;
    }




}
