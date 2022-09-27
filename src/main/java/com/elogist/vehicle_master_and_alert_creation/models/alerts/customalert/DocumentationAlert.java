package com.elogist.vehicle_master_and_alert_creation.models.alerts.customalert;

import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;
import com.elogist.vehicle_master_and_alert_creation.models.dto.DeviationALertDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.DeviationParameterDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.DocumentationAlertDTO;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoAlertEventsRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp2Repository;
import com.elogist.vehicle_master_and_alert_creation.services.AlertProcessingService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocumentationAlert {

    @Autowired
    FoAlertEventsRepository foAlertEventsRepository;

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    AlertProcessingService alertProcessingService;

    @Autowired
    MasterTableTemp2Repository masterTableTemp2Repository;

    public List<FoEscalationTickets> getAlert(){

        List<FoEscalationTickets> foEscalationTicketsList = new ArrayList<>();
        String result = foAlertEventsRepository.getDocumentationAlert();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DocumentationAlertDTO>>(){}.getType();
        List<DocumentationAlertDTO> documentationAlertDTOS = gson.fromJson(result,type);

        if(documentationAlertDTOS != null && documentationAlertDTOS.size() > 0) {
            for (DocumentationAlertDTO documentationAlertDTO : documentationAlertDTOS) {

                MasterTableTemp1 masterTableTemp1 = alertProcessingService.getM1ByVehicleId(documentationAlertDTO.getVehicleId());

                String generalParameter = masterTableTemp1Repository.getGeneralParameter(documentationAlertDTO.getVehicleId());

                String specificParam = getSpecifiedParameter(documentationAlertDTO);

                FoEscalationTickets foEscalationTickets = new FoEscalationTickets(documentationAlertDTO, masterTableTemp1, generalParameter, specificParam);
                foEscalationTicketsList.add(foEscalationTickets);
            }
        }

        return foEscalationTicketsList;


    }

    public String getSpecifiedParameter(DocumentationAlertDTO documentationAlertDTO){

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        DocumentationAlertDTO documentationAlert = new DocumentationAlertDTO(documentationAlertDTO.getDocTypeName(), documentationAlertDTO.getExpireDate());



        String specifiedParameter = gson.toJson(documentationAlert);

        return specifiedParameter;

    }
}
