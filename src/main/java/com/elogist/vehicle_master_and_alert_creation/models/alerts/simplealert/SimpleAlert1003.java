package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;


import com.elogist.vehicle_master_and_alert_creation.models.Enums.PrimStatusEnum;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.SecStatusEnum;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.Data;

import java.time.LocalDateTime;


//Loading Delay more than constant hrs // 1003
public class SimpleAlert1003 extends SimpleAlert {

    public static Integer alertTypeId = 1003;

    public Integer getAlertTypeId(){
        return alertTypeId;
    }

    public Integer getPriId(Master master){

        Integer priId = master.getVId();
        return priId;

    }

    public Benchmarks getDefaultBenchmark(){
        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    public String getRemark(Master master, Issues issues)  {
        String remarked = "";
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        remarked = "Loading Delay more than "+DateAndTime.convertMinutesToDayHoursMinutes(minDuration) + " Hours";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        LocalDateTime hlTime = null;
        Integer minDuration = benchmark.getMinDuration();
        Long minuteDiff = 0l;
        if(master.getHlStartTime() != null && master.getHlStartTime().get(0) != null) {
            hlTime = master.getHlStartTime().get(0).toLocalDateTime();
        }

        if(master.getHlEndTime() != null && master.getHlEndTime().get(0) != null ){
            return false;
        }

        if(hlTime != null && master.getTtTime() != null){
            minuteDiff = DateAndTime.getMinDifference(hlTime, master.getTtTime());
        }
        else {
            return false;
        }
        if(minuteDiff > minDuration && minuteDiff < (3*24*60)){ // minuteDiff is less than 3 days(in min)
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

        MasterTableTemp2 masterTableTemp2 = masterTableTemp1.getMasterTableTemp2();
        
        if(masterTableTemp1.getHlStartTime() != null && masterTableTemp2.getHlStartTime() != null && masterTableTemp1.getHlStartTime().get(0) != null && masterTableTemp2.getHlStartTime().get(0) != null){

            if(masterTableTemp1.getHlStartTime().get(0).equals(masterTableTemp2.getHlStartTime().get(0))){

                if((masterTableTemp1.getHlEndTime() == null || masterTableTemp1.getHlEndTime().get(0) == null) && masterTableTemp2.getHlEndTime() != null && masterTableTemp2.getHlEndTime().get(0) != null){

                    return false;
                }
            }
        }
        return true;
    }

}
