package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface FoEscalationTicketsRepository extends JpaRepository<FoEscalationTickets, Integer> {



    @Query(value = "select fet.* from foadmin.fo_escalation_tickets fet inner join foadmin.fo_issue_ticket_properties fitp\n" +
            "on fet.issue_properties_id = fitp.id where fitp.id in (?1) and fitp.foid = ?2 and fitp.is_active is true and fet.addtime between ?3 and ?4",nativeQuery = true)
    List<FoEscalationTickets> getRequiredAlert(List<Integer> id, Integer foid, LocalDateTime startTime, LocalDateTime endTime);

    @Query(value = "select regno from vehicles where id in (?1)",nativeQuery = true)
    List<String> getRegNo(List<Integer> id);

    @Query(value = "select * from uj_get_state_function(?1)", nativeQuery = true)
    String getAlertDetail(Integer tripId);

    @Query(value = "select * from foadmin.fo_escalation_tickets where clearuserid is null and is_auto_resolvable is false order by id desc limit 2000", nativeQuery = true)
    List<FoEscalationTickets> getAlertsType();
}
