package com.elogist.vehicle_master_and_alert_creation.models.alerts.customalert;

import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.models.dto.MaintainaceAlertDTO;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoAlertEventsRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MaintainanceAlert {

    @Autowired
    FoAlertEventsRepository foAlertEventsRepository;

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    public List<FoEscalationTickets> getAlerts(){

        List<FoEscalationTickets> foEscalationTicketsList = new ArrayList<>();
        String jsonResult = foAlertEventsRepository.getMaintainceAlert();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<MaintainaceAlertDTO>>(){}.getType();
        List<MaintainaceAlertDTO> maintainaceAlertDTOS = gson.fromJson(jsonResult, type);

        for(int i=0; i<maintainaceAlertDTOS.size(); i++){

            List<Integer> issue = foAlertEventsRepository.checkIfVehicleServiceExpiryTicketExists(maintainaceAlertDTOS.get(i).getJpId());
            if(issue.size() == 0){
                String m1State = masterTableTemp1Repository.getM1State(maintainaceAlertDTOS.get(i).getVid());
                FoEscalationTickets foEscalationTickets = new FoEscalationTickets(maintainaceAlertDTOS.get(i), m1State);
                foEscalationTicketsList.add(foEscalationTickets);
            }
        }

        return foEscalationTicketsList;

    }
}
