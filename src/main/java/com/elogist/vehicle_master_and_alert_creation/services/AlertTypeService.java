package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.annotations.FieldDescription;
import com.elogist.vehicle_master_and_alert_creation.excepetion.InvalidIDExcepetion;
import com.elogist.vehicle_master_and_alert_creation.models.DataTypeChecker;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumDataType;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumUnit;
import com.elogist.vehicle_master_and_alert_creation.models.UnitChecker;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.AlertType;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.BenchMarkDetails;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.AlertTypeRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertTypeService {

    @Autowired
    AlertTypeRepository alertTypeRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertTypeService.class);
    boolean check(Integer id) {
        return false;
    }


    public Map<String,Object> getDatatypeAndUnit(){
        Map<String,Object> datatypeAndUnitMap=new HashMap<>();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        for (BenchmarkEnumDataType benchmarkEnumDataType: BenchmarkEnumDataType.dataTypeList()
             ) {
            list1.add(benchmarkEnumDataType.getText());
        }
        datatypeAndUnitMap.put("DataTypes",list1);

        for(BenchmarkEnumUnit benchmarkEnumUnit : BenchmarkEnumUnit.unitList()){
            list2.add(benchmarkEnumUnit.getText());
        }

        datatypeAndUnitMap.put("UnitTypes",list2);

        return datatypeAndUnitMap;

    }

    public List<AlertType> seeAlertType() {
        List<AlertType> list = alertTypeRepository.findbyorder();

        for(AlertType alertType : list) {
            alertType.setBenchmark(Alerts.populateGeneralBenchmarks(alertType));
        }
        return list;
    }

    public AlertType alertById(Integer id) {
        AlertType alertType = alertTypeRepository.alertById(id);
        alertType.setBenchmark(Alerts.populateGeneralBenchmarks(alertType));
        return alertType;
    }

    public Integer insert(AlertType alertType) throws Exception {
        Integer id = alertType.getId();
        try {
            if (id != null && (id > 0 && id < 100)) {
                List<BenchMarkDetails> benchMarkDetails = alertType.getBenchmark();
                for (BenchMarkDetails benchMarkDetail : benchMarkDetails) {
                    BenchmarkEnumDataType benchmarkEnumDataType = BenchmarkEnumDataType.getBenchmarkEnumDataType(benchMarkDetail.getDataType());
                    BenchmarkEnumUnit benchmarkEnumUnit = BenchmarkEnumUnit.getBenchmarkEnumUnit(benchMarkDetail.getUnit());
                    if (benchmarkEnumDataType == null) {
                        throw DataTypeChecker.notString(benchMarkDetail.getDataType());
                    } else if (benchmarkEnumUnit == null) {
                        throw UnitChecker.notString(benchMarkDetail.getUnit());
                    }
                }
                alertTypeRepository.save(alertType);
                return 1;

            }
        }
        catch (InvalidIDExcepetion excepetion){
            throw new InvalidIDExcepetion("Invalid ID !!!!!!");
        }
        catch (Exception e) {
               LOGGER.error("Message--->" + e.getMessage());
                throw e;
        }

        return -1;
    }

    public int deleteAlert(Integer id) {
        boolean a=check(id);
        if(!a) {
            alertTypeRepository.deleteById(id);
            return 1;
        }
        else return -1;
    }

}
