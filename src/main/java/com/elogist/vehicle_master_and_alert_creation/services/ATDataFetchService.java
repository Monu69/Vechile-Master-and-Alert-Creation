package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.dto.ATRawData;
import com.elogist.vehicle_master_and_alert_creation.models.dti.VehicleOutDTI;
import com.elogist.vehicle_master_and_alert_creation.models.dto.ATAlertMappingDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.AlertDetailsDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleOutDTO;
import com.elogist.vehicle_master_and_alert_creation.repository.mssql.ATGpsDataRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.ClientApiLogRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoEscalationTicketsRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp2Repository;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.elogist.vehicle_master_and_alert_creation.utils.StringConstants;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ATDataFetchService {


    @Autowired
    ATGpsDataRepository atGpsDataRepository;

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    MasterTableTemp2Repository masterTableTemp2Repository;

    @Autowired
    FoEscalationTicketsRepository foEscalationTicketsRepository;

    @Autowired
    ClientApiLogRepository clientApiLogRepository;

    @Autowired
    AlertProcessingService alertProcessingService;

    @Autowired
    IssueBenchmarkService issueBenchmarkService;

    @Autowired
    StatsDClient dataDogClient;


    private static final Logger LOGGER = LoggerFactory.getLogger(ATDataFetchService.class);

    @Scheduled(fixedDelayString = "${elogist.atDataProcessing}")
    public void showDetails() {

        LOGGER.info("Schedule service started");

        LocalDateTime atDataServiceStartTime = LocalDateTime.now();

        List<ATAlertMappingDTO> result = new ArrayList<>();
        HashMap<Integer, List<Integer>> map = new HashMap<>();
        HashMap<String, List<Integer>> listmap = new HashMap<>();
        LocalDateTime endTime = null;
        List<VehicleOutDTO> vehicleOutDTOList = getATResult();
        LocalDateTime startTime = masterTableTemp1Repository.getStartTime();
        startTime = startTime.withNano(0);
        endTime = LocalDateTime.now().withNano(0);
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(1).withNano(0);
        }
        for (VehicleOutDTO vehicleOutDTO : vehicleOutDTOList) {
            if (listmap.containsKey(vehicleOutDTO.getAxVehicleId().toString())) {
                continue;
            } else {
                List<Integer> list = new ArrayList<>(List.of(vehicleOutDTO.getVehicleId(), vehicleOutDTO.getFoId(), vehicleOutDTO.getFoIssuePropertyId()));
                String key = vehicleOutDTO.getAxVehicleId() + "#" + vehicleOutDTO.getFoIssueTypeId();
                listmap.put(key, list);
            }
        }
        List<ATRawData> ATRawDataList = new ArrayList<>();
        for (VehicleOutDTO vehicleOutDTO : vehicleOutDTOList) {
            if (map.containsKey(vehicleOutDTO.getAxVehicleId())) {
                map.get(vehicleOutDTO.getAxVehicleId()).add(vehicleOutDTO.getFoIssueTypeId());
            } else {
                List<Integer> foIssueTypeId = new ArrayList<>();
                foIssueTypeId.add(vehicleOutDTO.getFoIssueTypeId());
                map.put(vehicleOutDTO.getAxVehicleId(), foIssueTypeId);
            }
        }
        for (Integer key : map.keySet()) {
            ATRawData ATRawData = new ATRawData(key, map.get(key));
            ATRawDataList.add(ATRawData);
        }
        try {
            result = getATAlert(ATRawDataList, startTime, endTime);
            LOGGER.info("Size of DataFetch From At:" + result.size() + " start time " + startTime + " end time " + endTime);
            List<FoEscalationTickets> foEscalationTickets = new ArrayList<>();
            List<ClientAPILogsModel> clientAPILogsModels = new ArrayList<>();
            for (ATAlertMappingDTO ATAlertMappingDTO : result) {
                try {

                    String key = ATAlertMappingDTO.getAtVid() + "#" + ATAlertMappingDTO.getServiceId();
                    Integer vehicleId = listmap.get(key).get(0);
                    Integer foId = listmap.get(key).get(1);
                    Integer issuePropertyId = listmap.get(key).get(2);
                    MasterTableTemp1 masterTableTemp1 = alertProcessingService.getM1ByVehicleId(vehicleId);

                    String generalParameter = masterTableTemp1Repository.getGeneralParameter(vehicleId);

                    FoEscalationTickets foEscalationTickets1 = new FoEscalationTickets(ATAlertMappingDTO, foId, vehicleId, issuePropertyId, masterTableTemp1, generalParameter, null);
                    foEscalationTickets.add(foEscalationTickets1);

                } catch (Exception e) {
                    LOGGER.error("error in ticket raised in showDetails()" + e.getMessage());
                }
            }
            alertProcessingService.saveIntoFoEscalation(foEscalationTickets);
            String atAlertSync = StringConstants.AT_ALERT_SYNC;
            masterTableTemp1Repository.updateTime(atAlertSync);
            Integer foId = -1;
            String apiType = "At-Alert";
            ClientAPILogsModel clientAPILogsModel = new ClientAPILogsModel(foId, apiType, endTime, 1, "Success Size " + result.size() + " StartTime: " + startTime + " endTime: " + endTime);
            clientApiLogRepository.save(clientAPILogsModel);
        } catch (Exception e) {
            Integer foId = -1;
            String apiType = "At-Alert";
            ClientAPILogsModel clientAPILogsModel = new ClientAPILogsModel(foId, apiType, endTime, -1, "error Size " + result.size() + " StartTime: " + startTime + " endTime: " + endTime, "error");
            clientApiLogRepository.save(clientAPILogsModel);
            LOGGER.error("Error Message: " + e.getMessage());
        }

        LocalDateTime atDataServiceEndTime = LocalDateTime.now();

        Long actualTimeInSec = DateAndTime.getSecDifference(atDataServiceStartTime, atDataServiceEndTime);

        dataDogClient.time("M1M2ATDataFetchService", actualTimeInSec, "");

        LOGGER.info("Schedule service ended");
    }

    public List<ATAlertMappingDTO> getATAlert(List<ATRawData> ATRawDataList, LocalDateTime startTime, LocalDateTime endTime) throws SQLException {

        return atGpsDataRepository.fetchDataFromAT(ATRawDataList, startTime, endTime);

    }

    public List<VehicleOutDTO> getATResult(){

        String response = masterTableTemp1Repository.getATResult();

//        String response = masterTableTemp1Repository.getATResultForParticularVId();

        Gson gson = new Gson();

        Type listType = new TypeToken<List<VehicleOutDTO>>(){}.getType();

        List<VehicleOutDTO> vehicleOutDTOList = gson.fromJson(response, listType);

        return vehicleOutDTOList;

    }
}
