package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.services.AlertUtilService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;


//Long Onward more than constant hrs // 1013
public class SimpleAlert1013 extends SimpleAlert {

    @Autowired
    AlertUtilService alertUtilService;

    public static Integer alertTypeId = 1013;

    public Integer getAlertTypeId(){
        return alertTypeId;
    }

    public Benchmarks getDefaultBenchmark(){
        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    public Integer getPriId(Master master){

        Integer priId = master.getVId();
        return priId;

    }


    public String getRemark(Master master, Issues issues)  {
        String remarked = "";
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        remarked = "Long Onward more than "+DateAndTime.convertMinutesToDayHoursMinutes(minDuration) + " Hours";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        Long minuteDiff = 0l;
        Boolean isOnward = false;
        if(master.getTtTime() != null && master.getVsStartTime() != null){
            minuteDiff = DateAndTime.getMinDifference(master.getTtTime(), master.getVsStartTime());
        }
        else{
            return false;
        }

        isOnward = alertUtilService.isOnward(master);

        if(isOnward && (minuteDiff > minDuration)){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean isResolved(MasterTableTemp1 master1, Issues issues)  {
        Boolean result = isValidAlert(master1, issues);
        return result;
    }

    @Override
    public Benchmarks getBenchmarks(String str) {

        Gson gson = new Gson();
        Benchmarks benchmarks = gson.fromJson(str, Benchmarks.class);
        return benchmarks;
    }

    @Data
    class Benchmarks extends Alerts.Benchmarks{

        Integer minDuration;

        public Benchmarks(){

            this.minDuration = 60;

        }
    };

    public SpecificParameters getSpecifiedParameter(){

        return null;

    }

    @Data
    class SpecificParameters extends Alerts.SpecificParameters{


    }


    public Boolean isM1M2Valid(MasterTableTemp1 masterTableTemp1){

        return true;
    }

}
