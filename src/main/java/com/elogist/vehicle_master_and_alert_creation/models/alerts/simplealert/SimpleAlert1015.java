package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.Data;

import java.time.LocalDateTime;


//ETA Delay // 1015
public class SimpleAlert1015 extends SimpleAlert {
    public static Integer alertTypeId = 1015;

    @Override
    public Integer getAlertTypeId() {
        return alertTypeId;
    }

    public Integer getPriId(Master master) {

        Integer priId = master.getVId();
        return priId;

    }


    public Benchmarks getDefaultBenchmark() {
        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    public String getRemark(Master master, Issues issues)  {
        String remarked = "";
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        remarked = "ETA Delay " + DateAndTime.convertMinutesToDayHoursMinutes(minDuration) + " Hours";
        return remarked;
    }


    public Boolean isValidAlert(Master master, Issues issues)  {

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        LocalDateTime pTime = null;
        Boolean isEtaDelay = false;
        if (master.getPTargetTime() != null) {
            for (int i = 0; i < master.getPTargetTime().size(); i++) {
                if (master.getVtStartTime() != null && master.getPTargetTime().get(i) != null && master.getVtStartTime().isBefore(master.getPTargetTime().get(i).toLocalDateTime())) {
                    pTime = master.getPTargetTime().get(i).toLocalDateTime();
                    break;
                }
            }
        }
        if (pTime != null && master.getTtTime() != null) {
            return false;
        } else {
            if(pTime != null && master.getTtTime() != null) {
                if (DateAndTime.getMinDifference(pTime, master.getTtTime()) >= minDuration) {
                    isEtaDelay = true;
                }
            }
        }
        if (isEtaDelay) {
            return true;
        } else {
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

