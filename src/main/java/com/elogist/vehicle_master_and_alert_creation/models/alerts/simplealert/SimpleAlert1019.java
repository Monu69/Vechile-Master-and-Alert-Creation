package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.services.AlertUtilService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.elogist.vehicle_master_and_alert_creation.utils.DistanceUtil;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;


//Plant Exit Time & Dis // 1019
public class SimpleAlert1019 extends SimpleAlert {
    public static Integer alertTypeId = 1019;

    @Autowired
    AlertUtilService alertUtilService;

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
        Integer minDistance = benchmark.getMinDistance();
        remarked = "Plant Exit Time " + DateAndTime.convertMinutesToDayHoursMinutes(minDuration) + " Hours " + " Distance " + minDistance + " Kms";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        Integer minDistance = benchmark.getMinDistance();
        Boolean isOnward = false;
        LocalDateTime hlTime = null;
        if (master.getHlStartTime() != null && master.getHlEndTime() != null && master.getHlStartTime().size() > 0 && master.getHlEndTime().size() > 0) {
            LocalDateTime hlSTime = master.getHlStartTime().get(0) != null ? master.getHlStartTime().get(0).toLocalDateTime() : null;
            LocalDateTime hlETime = master.getHlEndTime().get(0) != null ? master.getHlEndTime().get(0).toLocalDateTime() : null;
            hlTime = hlSTime != hlETime ? hlSTime : null;
        }
        Long time = 0l;
        Double dis = 0.0, hLat = 0.0, hLong = 0.0;
        if (master.getHlLat() != null && master.getHlLong() != null && master.getHlLat().size() > 0 && master.getHlLong().size() > 0) {
            hLat = master.getHlLat().get(0);
            hLong = master.getHlLong().get(0);
        }
        isOnward = alertUtilService.isOnward(master);

        if (isOnward && hlTime != null && master.getTtTime() != null) {
            time = DateAndTime.getMinDifference(hlTime, master.getTtTime());
            if (time > minDuration) {
                if (master.getTLat() != null && master.getTLong() != null && hLat != null && hLong != null) {
                    dis = DistanceUtil.distanceBtwAAndB(master.getTLat(), master.getTLong(), hLat, hLong);
                    if (dis <= minDistance) {
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
        Integer minDistance;

        public Benchmarks(){

            this.minDuration = 60;
            this.minDistance = 2000;

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
