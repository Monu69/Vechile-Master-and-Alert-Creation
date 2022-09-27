package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.dti.RedisHMap;
import com.elogist.vehicle_master_and_alert_creation.models.TicketEscalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoTicketEscalationRepository extends JpaRepository<TicketEscalation,Integer> {

    @Query(value = "select foid \\:\\:text \"key\" ,jsonb_agg(jsonb_build_object('id',id,'foIssueTypeId',fo_issue_type_id,'userId', user_id,'seniorUserId',senior_user_id,'userLevel',user_level,'fromTime', from_time,'toTime',to_time,'issueConstraintId',issue_contraint_id, 'entryMode' ,entryMode , 'issuePropertiesId',issue_properties_id)) \\:\\:text \"value\" from foadmin.fo_ticket_escalation group by foid order by foid ",nativeQuery = true)
    List<RedisHMap> getEscalationString();

    @Query(value = "select y_key \"key\",y_value \"value\" from uj_get_alert_vehicle_recipients()", nativeQuery = true)
    List<RedisHMap> getAlertvehicleRecipients();


}
