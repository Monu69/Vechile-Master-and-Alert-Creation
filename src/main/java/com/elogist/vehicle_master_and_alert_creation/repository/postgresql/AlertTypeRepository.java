package com.elogist.vehicle_master_and_alert_creation.repository.postgresql;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.AlertType;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.SelectionDTI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertTypeRepository extends JpaRepository<AlertType,Integer> {

    @Query(value = "select * from foadmin.fo_issue_types order by id", nativeQuery = true)
    List<AlertType> findbyorder();

    @Query(value="select * from foadmin.fo_issue_types where id=?1", nativeQuery = true)
    AlertType alertById(Integer id);

    @Query(value="select fit.id alertTypeId,fit.\"name\" alertTypeName, fitp.id alertPropertyId,fitp.benchmarks,fitp.auto_resolve_type autoResolveType \n" +
            "from foadmin.fo_issue_types fit\n" +
            "\t left join foadmin.fo_issue_ticket_properties fitp on fit.id = fitp.fo_issue_type_id and fitp.foid = ?1",nativeQuery = true)
    List<SelectionDTI> findSelection(Integer id);


}