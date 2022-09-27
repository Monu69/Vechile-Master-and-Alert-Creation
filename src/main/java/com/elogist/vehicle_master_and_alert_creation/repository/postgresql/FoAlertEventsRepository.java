package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.FoAlertEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoAlertEventsRepository extends JpaRepository<FoAlertEvents, Integer> {

    @Query(value = "select * from foadmin.fo_alert_events where expirytime is null", nativeQuery = true)
    List<FoAlertEvents> getFoAlertEventsData();

    @Query(value = "select * from ps_get_vehicle_main_alert_list()", nativeQuery = true)
    public String getMaintainceAlert();

    @Query(value = "select id from foadmin.fo_escalation_tickets where pri_id=?1 and fo_issue_type_id=11001 and clearuserid is null", nativeQuery = true)
    public List<Integer> checkIfVehicleServiceExpiryTicketExists(Integer serviceId);


    @Procedure(value = "uj_get_vehicle_doc_expiry")
    String getDocumentationAlert();

    @Procedure(value = "uj_get_dest_deviation_alerts")
    String getDeviationAlert();






}
