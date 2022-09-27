package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.ClientAPILogsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientApiLogRepository extends JpaRepository<ClientAPILogsModel, Integer> {

    @Query(value = "select foid from foadminusers  where id =:foAdminId",nativeQuery = true)
    Integer findFoidByFoAdminId(@Param("foAdminId") Integer foAdminId);

    @Query(value = "\tselect jsonb_build_object('TripId',vt.id,'Invoice No.',vt.invoice_no,'EventName','TRIP_COMPLETE'\n" +
            "\t\t\t\t\t\t\t ,'EventStartTime',max(vts.trip_end_time),'Location',uj_get_loc_name(mvt.tlat,mvt.tlong),'Duration',sum(vts.hat_time_ss)\n" +
            "\t\t\t\t\t\t\t ,'TrackURL','http://xswift.biz/dost/#/direct/tripconsignment?token='||vt.id||'XX'||mvt.v_regno||right(replace((vt.addtime\\:\\:timestamp(0))\\:\\:text,':',''),6)\n" +
            "\t\t\t\t\t\t\t ,'IdleDuration',sum(vts.hat_time_ss),'DelayDuration',0,'System ETA',vt.end_time,'Updated ETA',max(vts.trip_end_time)\n" +
            "\t\t\t\t\t\t\t ,'TrackHealth','','TrackAccuracy','','TripClosureType','','TripClosureSubType','','Address',uj_get_loc_name(mvt.tlat,mvt.tlong)\n" +
            "\t\t\t\t\t\t\t ,'AverageSpeed','','RunningTime','','StoppageTime','','LocationSource','','Distance','','TotalPings',''\n" +
            "\t\t\t\t\t\t\t ,'TollsCrossed','','TollsName','')\\:\\:varchar\n" +
            "\tfrom vehicle_trips vt\n" +
            "\t\t inner join master_vehicle_temp1 mvt on mvt.v_id = vt.vehicle_id\n" +
            "\t\t left join vehicle_trip_summary vts on vts.trip_id = vt.id\n" +
            "\twhere vt.id = :tripId\t \n" +
            "\tgroup by vt.id,mvt.v_id ",nativeQuery = true)
    String getTripDataJKLC(@Param("tripId") Integer tripId);

    @Query(value = "select jsonb_build_array(jsonb_build_object('vehicle_plate',v.regno,'start_time',vt.start_time\\:\\:timestamp(0),'route_tag',string_agg(vs.loc_name,'-' order by vs.start_time)))\\:\\:varchar\n" +
            "            from vehicle_trips vt\n" +
            "            \t inner join vehicles v on v.id = vt.vehicle_id \n" +
            "            \t inner join vt_states vs on vs.vehicle_trip_id = vt.id \n" +
            "where vt.id = :tripId \n" +
            "group by vt.id,v.id",nativeQuery = true)
    String getTripDataSGMPL(@Param("tripId") Integer tripId);

}
