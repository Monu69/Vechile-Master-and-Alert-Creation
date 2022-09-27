package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.dti.RedisHMap;
import com.elogist.vehicle_master_and_alert_creation.models.TicketProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;


public interface FoTicketPropertiesRepository extends JpaRepository<TicketProperties,Integer> {

//    @Query(value = "select * from foadmin.fo_issue_ticket_properties fitp where is_active is true order by foid",nativeQuery = true)
//    List<TicketProperties> getAllTicketProperties();


    @Query(value = "select foid \\:\\:text \"key\", jsonb_agg(jsonb_build_object('id',id,'foIssueTypeId',fo_issue_type_id,'escalationTime',esc_time,'completeReminderTime',compl_rem_time,'completeEscalationTime',compl_esc_time,'isReminder',is_reminder,'isEscalate',is_escalate,'isDeliveryTime',is_deliverytime,'isUrgent',is_urgent,'benchmark',benchmark,'benchmark2',benchmark2,'issueConstraintId',issue_contraint_id,'benchmarks',benchmarks\\:\\:text)) \\:\\:text \"value\" from foadmin.fo_issue_ticket_properties where is_active group by foid order by foid ",nativeQuery = true)
    List<RedisHMap> getTicketPropertiesString();

    @Query(value = "select * from foadmin.fo_issue_ticket_properties fitp where fitp .id in(?1)", nativeQuery = true)
    public List<TicketProperties> getTicketProperties(Set<Integer> id);


}
