package com.elogist.vehicle_master_and_alert_creation.services;


import com.elogist.vehicle_master_and_alert_creation.excepetion.ATFetchExcepetion;
import com.elogist.vehicle_master_and_alert_creation.models.*;
//import com.elogist.vehicle_master_and_alert_creation.repository.pstgresql.MasterTableTemp1Repository;
//import com.elogist.vehicle_master_and_alert_creation.repository.pstgresql.MasterTableTemp2Repository;

import com.elogist.vehicle_master_and_alert_creation.models.dto.ClientRseult;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoEscalationTicketsRepository;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IssueBenchmarkService {

    @Autowired
    private AlertUtilityService alertUtilityService;

    @Autowired
    FoEscalationTicketsRepository foEscalationTicketsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueBenchmarkService.class);

    public  Map<Integer,Map<Integer,List<Issues>>> getIssueBenchmark()
    {
        Map<String,String> issueConstraints = alertUtilityService.getIssueConstraints();
        Map<String,String> ticketEscalation = alertUtilityService.getTicketEscalation();
        Map<String,String> ticketProperties = alertUtilityService.getTicketProperties();


        Map<Integer,IssueConstraints> issueConstraintsMap = getIssueConstraints(issueConstraints);
        Map<Integer, List<TicketEscalation>> ticketEscalationMap = new HashMap<>();
        Map<Integer, List<TicketProperties>> ticketPropertiesMap = new HashMap<>();

        List<IssueConstraints> checkList = new ArrayList<>();


        //TicketProperties
        for(Map.Entry<String,String> entry : ticketProperties.entrySet())
        {
            String jsonString = entry.getValue();
            Integer key = Integer.parseInt(entry.getKey());
            Type listType = new TypeToken<ArrayList<TicketProperties>>(){}.getType();
            List<TicketProperties> ticketPropertiesList = new Gson().fromJson(jsonString,listType);
            ticketPropertiesMap.put(key,ticketPropertiesList);
        }

        //TicketEscalation
        for(Map.Entry<String,String> entry : ticketEscalation.entrySet())
        {
            String jsonString  = entry.getValue();
            Integer key = Integer.parseInt(entry.getKey());
            Type listType = new TypeToken<ArrayList<TicketEscalation>>(){}.getType();
            List<TicketEscalation> ticketEscalationsList = new Gson().fromJson(jsonString,listType);
            ticketEscalationMap.put(key,ticketEscalationsList);
        }

        Map<Integer,Map<Integer,List<Issues>>> matrix = new HashMap<>();


        for(Map.Entry<Integer,List<TicketProperties>> entry : ticketPropertiesMap.entrySet())
        {
            List<TicketProperties> ticketPropertiesList = entry.getValue();
            Integer foid = entry.getKey();

            Map<Integer,List<Issues>> subMatrix = new HashMap<>();


            for(TicketProperties ticketProperties1 : ticketPropertiesList) {
                Integer propertyType = ticketProperties1.getFoIssueTypeId();
                List<Integer> benchmarks = new ArrayList<>();
                String benchmark1 = "", benchmark2 = "";
                if (ticketProperties1.getBenchmark() != null)
                    benchmarks.add(ticketProperties1.getBenchmark());
                if (ticketProperties1.getBenchmark2() != null)
                    benchmarks.add(ticketProperties1.getBenchmark2());
               // String benchmark = benchmark1 + "," + benchmark2;

                Integer issueConstraintId = ticketProperties1.getIssueConstraintId();
                Integer id = ticketProperties1.getId();
               // Map<List<Integer>, IssueConstraints> subMatrix1 = new HashMap<>();
                IssueConstraints issueConstraints1 = new IssueConstraints();
                Issues issues;
                if (issueConstraintId != null) {
                    issueConstraints1 = issueConstraintsMap.get(issueConstraintId);
                    if(issueConstraints1 != null) {
                        List<Integer> consignees = issueConstraints1.getConsignees();
                        List<Integer> destinations = issueConstraints1.getDestinations();
                        List<Integer> transporters = issueConstraints1.getTransporters();
                        List<Integer> vehicles = issueConstraints1.getVehicles();
                        List<Integer> groups = issueConstraints1.getGroups();
                        List<Integer> sources = issueConstraints1.getSources();
                        issues = new Issues(id, benchmarks, consignees, destinations, transporters, vehicles, groups, sources,ticketProperties1.getBenchmarks());
                    }
                    else{
                        issues = new Issues(id, benchmarks, ticketProperties1.getBenchmarks());
                    }
                }
                else{
                    issues = new Issues(id, benchmarks, ticketProperties1.getBenchmarks());
                }
                if (subMatrix.containsKey(propertyType))
                    subMatrix.get(propertyType).add(issues);
                else {
                    List<Issues> temp = new ArrayList<>();
                    temp.add(issues);
                    subMatrix.put(propertyType,temp);

                }
                matrix.put(foid, subMatrix);

            }

        }
        return matrix;


    }
    public Map<String,List<Integer>> getAlertVehicleRecipients(){

        Map<String, String> alertRecipientMap = alertUtilityService.getAlertVehicleRecepient();

        Map<String,List<Integer>> resultantMap = new HashMap<>();

        for (Map.Entry<String, String> entry : alertRecipientMap.entrySet()) {
            String jsonString = entry.getValue();
            Type listType = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            List<Integer> alertList = new Gson().fromJson(jsonString, listType);
            resultantMap.put(entry.getKey(), alertList);
        }
        return resultantMap;
    }
    public  Map<Integer,IssueConstraints> getIssueConstraints(Map<String,String> issueConstraints) {
        Map<Integer,IssueConstraints> issueConstraintsMap = new HashMap<>();
        for (Map.Entry<String, String> entry : issueConstraints.entrySet()) {
            String jsonString = entry.getValue();
            Integer key = Integer.parseInt(entry.getKey());
            Type listType = new TypeToken<ArrayList<IssueConstraints>>() {
            }.getType();
            List<IssueConstraints> issueConstraintsList = new Gson().fromJson(jsonString, listType);
            issueConstraintsMap.put(key, issueConstraintsList.get(0));
        }
        return issueConstraintsMap;
    }

    public List<ClientRseult> getAlerts(List<Integer> serviceIds, LocalDateTime sTime, LocalDateTime eTime, Integer foId) {


            if(serviceIds.size() == 0){
                throw new ATFetchExcepetion("ServiceId must not be null!!!!");
            }
            List<String> regNo = new ArrayList<>();
            List<FoEscalationTickets> foEscalationTickets = foEscalationTicketsRepository.getRequiredAlert(serviceIds, foId, sTime, eTime);
//            List<FoEscalationOutDTI> foEscalationTickets = foEscalationTicketsRepository.getRequiredAlert(serviceIds, foId, sTime, eTime);
            List<Integer> vehicleIds = new ArrayList<>();
            List<ClientRseult> clientRseults = new ArrayList<>();
            HashMap<Integer, String> map = new HashMap<>();
            Integer j=0;
            for (FoEscalationTickets foEscalationTickets1 : foEscalationTickets){
                vehicleIds.add(foEscalationTickets1.getVehicleId());
            }
            if(vehicleIds != null && vehicleIds.size() > 0) {
                regNo = foEscalationTicketsRepository.getRegNo(vehicleIds);
            }

            for(int i=0;i<foEscalationTickets.size();i++){
                if(!map.containsKey(foEscalationTickets.get(i).getVehicleId())){
//                    map.put(foEscalationTickets.get(i).getVehicle_id(),map.get(foEscalationTickets.get(i)));
                    map.put(foEscalationTickets.get(i).getVehicleId(),regNo.get(j));
                    j++;
                }
            }

            for(int i=0;i<foEscalationTickets.size();i++){
                String[] remakSplit = foEscalationTickets.get(i).getRemark().split("##");
                if(remakSplit.length > 1) {
                    ClientRseult clientRseult = new ClientRseult(foEscalationTickets.get(i).getIssuePropertiesId(), remakSplit[1], foEscalationTickets.get(i).getVehicleId(), map.get(foEscalationTickets.get(i).getVehicleId()), remakSplit[0], foEscalationTickets.get(i).getClearTime(), foEscalationTickets.get(i).getAddTime());
                    clientRseults.add(clientRseult);
                }
                else{
                    ClientRseult clientRseult = new ClientRseult(foEscalationTickets.get(i).getIssuePropertiesId(), foEscalationTickets.get(i).getVehicleId(), map.get(foEscalationTickets.get(i).getVehicleId()), remakSplit[0], foEscalationTickets.get(i).getClearTime(), foEscalationTickets.get(i).getAddTime());
                    clientRseults.add(clientRseult);
                }
            }
            return clientRseults;
        
    }






}
