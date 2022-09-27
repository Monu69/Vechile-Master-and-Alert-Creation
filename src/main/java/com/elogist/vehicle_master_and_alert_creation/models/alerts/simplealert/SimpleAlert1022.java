package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.services.AlertUtilService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.elogist.vehicle_master_and_alert_creation.utils.StringConstants;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


//Phone Switch Off During Trip// 1022
public class SimpleAlert1022 extends SimpleAlert {

    @Autowired
    AlertUtilService alertUtilService;

    public static Integer alertTypeId = 1022;

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
        remarked = "Phone Switch Off During Trip "+DateAndTime.convertMinutesToDayHoursMinutes(minDuration) + " Hours";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        Long time;
        Boolean isOnward = false;

        isOnward = alertUtilService.isOnward(master);

        if(isOnward){
            if(master.getSdcLastError() != null) {
                List<String> ans = new ArrayList<>(List.of(master.getSdcLastError()));
                List<String> temp = new ArrayList<>(List.of(StringConstants.ALERT_1022_LAST_ERROR1, StringConstants.ALERT_1022_LAST_ERROR2));
                if (ans.containsAll(temp) && master.getSdcLastErrorTime() != null) {
                    time = DateAndTime.TimeDiff(master.getSdcLastErrorTime()) * 60;
                    if (time >= minDuration) {
                        return true;
                    }
                }
            }
        }
        return false;
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
