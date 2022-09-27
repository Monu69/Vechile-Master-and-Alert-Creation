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

import java.time.LocalDateTime;


//No Data Received for x Hours and Onward// 1021
public class SimpleAlert1021 extends SimpleAlert {

    @Autowired
    AlertUtilService alertUtilService;

    public static Integer alertTypeId = 1021;

    @Override
    public Integer getAlertTypeId() {
        return alertTypeId;
    }

    public Integer getPriId(Master master) {

        Integer priId = master.getVId();
        return priId;

    }


    public Benchmarks getDefaultBenchmark() {
        Benchmarks benchmark = new Benchmarks();
        return benchmark;
    }

    public String getRemark(Master master, Issues issues)  {
        String remarked = "";
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        remarked = "No Data Received for x Hours and Onward " + DateAndTime.convertMinutesToDayHoursMinutes(minDuration) + " Hours";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        LocalDateTime hlTime = null;
        Boolean isOnward = false, isDataMiss = false;
        if (master.getHlStartTime() != null && master.getHlEndTime() != null && master.getHlStartTime().size() > 0 && master.getHlEndTime().size() > 0) {
            LocalDateTime hlSTime = master.getHlStartTime().get(0) != null ? master.getHlStartTime().get(0).toLocalDateTime() : null;
            LocalDateTime hlETime = master.getHlEndTime().get(0) != null ? master.getHlEndTime().get(0).toLocalDateTime() : null;
            hlTime = hlSTime != hlETime ? hlSTime : null;
        }
        Long time = 0l;

        isOnward = alertUtilService.isOnward(master);

        if (isOnward) {
            if (master.getTtTime() == null) {
                isDataMiss = true;
            } else if (master.getVtAddtime() != null && master.getTtTime().isBefore(master.getVtAddtime())) {
                time = DateAndTime.TimeDiff(master.getVtAddtime()) * 60;
                if (time > minDuration) {
                    isDataMiss = true;
                }
            } else {
                if (master.getVtAddtime() != null) {
                    time = DateAndTime.TimeDiff(master.getVtAddtime()) * 60;
                    if (master.getTtTime() == null && time > minDuration) {
                        isDataMiss = true;
                    }
                }
            }
            if (isDataMiss) {
                return true;
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
