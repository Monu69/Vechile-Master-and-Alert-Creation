package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.Enums.SecStatusEnum;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.Data;

import java.time.LocalDateTime;


//Long RTO Issue more than constant hrs // 1012
public class SimpleAlert1012 extends SimpleAlert {

    public static Integer alertTypeId = 1012;

    public Integer getAlertTypeId(){
        return alertTypeId;
    }

    public Benchmarks getDefaultBenchmark(){
        Benchmarks benchmark = new Benchmarks();
        return benchmark;
    }

    public Integer getPriId(Master master){

        Integer priId = master.getVId();
        return priId;

    }


    public String getRemark(Master master, Issues issues)  {
        String remarked = "";
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        remarked = "Long RTO Issue more than "+DateAndTime.convertMinutesToDayHoursMinutes(minDuration) + " Hours";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        Long minuteDiff = 0l;
        if(master.getTtTime() != null && master.getVsStartTime() != null){
            minuteDiff = DateAndTime.getMinDifference(master.getVsStartTime(), LocalDateTime.now());
        }
        else{
            return false;
        }
        if(master.getSecStatus().equals(SecStatusEnum.RTO.getValue()) && (minuteDiff > minDuration)){
            return true;
        }
        else {
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
