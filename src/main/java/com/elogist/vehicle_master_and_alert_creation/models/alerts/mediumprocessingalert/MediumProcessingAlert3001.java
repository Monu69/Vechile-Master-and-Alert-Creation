package com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert;

import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.services.MediumProcessingCommonService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/** Less Than x km in y hrs. */
//TODO: need to be rethought and refactored. (Major Issue)
public class MediumProcessingAlert3001 extends MediumProcessingAlert {

    @Autowired
    MediumProcessingCommonService mediumProcessingCommonService;

    public static Integer alertId = 3001;

    @Override
    public Integer getAlertTypeId() {
        return alertId;
    }

    @Override
    public Benchmarks getDefaultBenchmark() {

        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    @Override
    public String getRemark(Master master, Issues issues) {

        Benchmarks benchmarks = (Benchmarks) issues.getFinalBenchmark(this);
        String remark = "Vehicle "+master.getRegNo()+" covered less than "+benchmarks.getMinDistanceInMeters()/1000
                +" kms in last "+benchmarks.getDurationInMins()/60+" hrs for time "+master.getTtTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        return remark;
    }

    @Override
    public Integer getPriId(Master master) {
        return master.getVId();
    }

    @Override
    public Boolean isResolved(MasterTableTemp1 master1, Issues issues) {
        return null;
    }

    @Override
    public Benchmarks getBenchmarks(String str) {

        Gson gson = new Gson();
        Benchmarks benchmarks = DateAndTime.getJsonToClass(str, Benchmarks.class);
        return benchmarks;
    }

    @Override
    public SpecificParameters getSpecifiedParameter() {

        return null;
    }

    @Data
    class SpecificParameters extends Alerts.SpecificParameters{


    }


    @Override
    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues) {

        Benchmarks benchmarks = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDistanceInMeters = benchmarks.getMinDistanceInMeters();
        Integer durationInMins = benchmarks.getDurationInMins();

//        List<Integer> stateCodes = benchmarks.getStateCodes();
//
//        LocalDateTime startTime = master1.getTtTime();
//        int state = master1.getHStatus(); // TODO: get status from a new column
//        while(stateCodes.contains(state)) {
//            startTime = master1.getTtTime(); // TODO: new column needs to be added for start time of state.
//
//        }

//        boolean alertState = mediumProcessingCommonService.generateAlert3001(master1, minDistanceInMeters,durationInMins);
//        boolean prevState = mediumProcessingCommonService.generateAlert3001(master1.getMasterTableTemp2(), minDistanceInMeters, durationInMins);
        boolean alertState = mediumProcessingCommonService.generateAlert3001(master1, minDistanceInMeters,durationInMins);
        boolean prevState = mediumProcessingCommonService.generateAlert3001(master1.getMasterTableTemp2(), minDistanceInMeters, durationInMins);

        if(!prevState && alertState)
            return true;
        else
            return false;
    }

    @Data
    class Benchmarks extends Alerts.Benchmarks {

        @SerializedName("minDistance")
        Integer minDistanceInMeters;
        @SerializedName("duration")
        Integer durationInMins;

        public Benchmarks() {
            minDistanceInMeters = 20000;
            durationInMins = 60;
        }

    }

}