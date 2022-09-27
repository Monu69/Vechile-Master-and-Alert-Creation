package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.dti.RedisHMap;
import com.elogist.vehicle_master_and_alert_creation.models.dti.VehicleOutDTI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface MasterTableTemp1Repository extends JpaRepository<MasterTableTemp1, Integer> {

    @Query(value ="select * from master_vehicle_temp1 where vt_fo in(?1)",nativeQuery = true)
    List<MasterTableTemp1> getResult(Set<Integer> requiredList);

    @Query(value ="select * from master_vehicle_temp1 where vt_fo in(?1) and v_id = ?2 ",nativeQuery = true)
    List<MasterTableTemp1> getResult1(Set<Integer> requiredList, Long VehicleId);

    @Procedure(value = "uj_get_at_alert_list")
    String getATResult();

    @Query(value = "select * from uj_get_at_alert_list(null,153186)", nativeQuery = true)
    String getATResultForParticularVId();

    @Query(value = "select max(addtime) startTime from client_api_logs cal where api_type=\'At-Alert\' and res_code =1",nativeQuery = true)
    LocalDateTime getStartTime();



    @Query(value = "select r_vid from fn_fovehicles(?1)", nativeQuery = true)
    List<Long> getAllVehicleForFo(Integer foAdminId);

    @Query(value = "select * from ry_get_vehicle_kpis_dynamic(?1)", nativeQuery = true)
    List<Map<String,Object>> getVehicleKpis1(Integer foAdminId);

    @Query(value = "select array_to_json(array_agg(mvt))->>0 from master_vehicle_temp1 mvt where v_id =?1", nativeQuery = true)
    String getM1State(Integer vId);

    @Query(value = "select array_to_json(array_agg(mvt))->>0 from master_vehicle_temp2 mvt where v_id =?1", nativeQuery = true)
    String getM2State(Integer vId);

    @Query(value = "select \"now\" from masterveh_now mn order by tbl_nm", nativeQuery = true)
    List<Timestamp> getCreatedTime();

    @Query(value = "select 1 \\:\\:int from mm_service_confings(?1)", nativeQuery = true)
    Integer updateTime(String aiRepairEngine);

    @Query(value = "SELECT 1 \\:\\:int from ry_create_vehicle_m1_and_m2_uj()", nativeQuery = true)
    Integer getM1M2Data();

    @Query(value = "select 1 \\:\\:int from uj_auto_halt_review()", nativeQuery = true)
    Integer getAutoHaltReview();

    @Query(value = "select 1 \\:\\:int from mm_service_confings(?1,x_last_call_start_dt\\:=?2,x_last_call_end_dt\\:=?3)", nativeQuery = true)
    void updateTime(String resultantString, LocalDateTime startTime, LocalDateTime endTime);

    @Query(value = "select 1 \\:\\:int from uj_stamping_auto_cron()", nativeQuery = true)
    Integer stampedTrips();

    @Query(value = "select * from uj_get_trip_end_events_jklc(?1)", nativeQuery = true)
    String getTripEndEventList(String tripIds);

    @Query(value = "select * from uj_get_general_params(?1)", nativeQuery = true)
    String getGeneralParameter(Integer vehicleId);




}