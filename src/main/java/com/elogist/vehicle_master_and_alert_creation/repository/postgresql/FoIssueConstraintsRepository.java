package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.IssueConstraints;
import com.elogist.vehicle_master_and_alert_creation.models.dti.RedisHMap;
import com.elogist.vehicle_master_and_alert_creation.models.dti.VehicleOutDTI;
import com.elogist.vehicle_master_and_alert_creation.models.dto.AllVehicleDto;
import com.elogist.vehicle_master_and_alert_creation.models.dto.VehicleRecipientDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;


public interface FoIssueConstraintsRepository extends JpaRepository<IssueConstraints,Integer> {

    @Query(value = "select * from foadmin.fo_issue_contraints fic group by 1",nativeQuery = true)
    List<IssueConstraints> getAllIssueConstraints();

    @Query(value = "select * from foadmin.fo_issue_contraints fic where id = ?1",nativeQuery = true)
    IssueConstraints getIssueConstraintById(Integer id);

    @Query(value = "select id \\:\\:text \"key\",jsonb_agg(jsonb_build_object('id',id, 'consignees',array_to_json(consignees),'destinations',array_to_json(destinations),'transporters',array_to_json(transporters),'vehicles',array_to_json(vehicles),'groups',array_to_json(groups),'sources',array_to_json(sources))) \\:\\:text \"value\" from foadmin.fo_issue_contraints  group by id order by id ",nativeQuery = true)
    List<RedisHMap> getIssueConstraintString();

//    @Query(value = "select ff.r_vid\\:\\:varchar \"key\",ff.r_regno \"value\" from vehicles v \n" +
//            "        inner join fn_fovehicles_cross_v1(0,?1) ff on ff.r_vid = v.id \n" +
//            "        left join master_vehicle_temp1 mv on mv.v_id = v.id  \n" , nativeQuery = true)

    @Query(value = "select foadminuserid \\:\\:text \"key\",vdata \\:\\:text \"value\" from mm_redis_GetVehicles(0,?1)",nativeQuery = true)
    List<RedisHMap> getFoVehicleList(Integer vehid);
    @Query(value = "select foadminuserid \\:\\:text \"key\",vdata \\:\\:text \"value\" from mm_redis_getvehicles_uj(?1,?2,?3)",nativeQuery = true)

    List<RedisHMap> getFoVehicleList(Integer foadminid,Integer vehid,Boolean cross);

    @Query(value = "select id,regno from vehicles where expdate is null",nativeQuery = true)
    List<Map<String,Object>> getCompleteVehiclelist();
    @Query(value = "select id,regno,foid ,case when expdate is not null then 1 else 0 end is_deleted\n" +
            "from vehicles where updatetime >now()+'- 20 minute'",nativeQuery = true)
    List<Map<String,Object>> addNewVehicleInRedis();




}
