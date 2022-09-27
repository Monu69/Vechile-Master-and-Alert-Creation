package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


public interface MasterTableTemp2Repository extends JpaRepository<MasterTableTemp2, Integer> {

    @Query(value ="select * from master_vehicle_temp2 where vt_fo in(:requiredList)",nativeQuery = true)
    List<MasterTableTemp2> getResult(Set<Integer> requiredList);

    @Query(value ="select * from master_vehicle_temp2 where vt_fo in(?1) and v_id = ?2 ",nativeQuery = true)
    List<MasterTableTemp2> getResult2(Set<Integer> requiredList, Long VehicleId);
}
