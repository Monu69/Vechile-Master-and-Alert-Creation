package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;


import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

//EwayBill Expiry Alert
public class SimpleAlert6001 extends SimpleAlert {

    public static Integer alertTypeId = 6001;

    public LocalDateTime thresholdTime = null;

    public Integer getPriId(Master master) {

        Integer priId = master.getHId();
        return priId;

    }

    public Integer getAlertTypeId() {
        return alertTypeId;
    }

    public Benchmarks getDefaultBenchmark() {
        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    public String getRemark(Master master, Issues issues) {
        String remarked = "";
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        remarked = "EwayBill expire " + DateAndTime.convertMinutesToDayHoursMinutes(minDuration) + " Hours After("+DateAndTime.localDateTimeToString(thresholdTime)+")";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues) {

        List<Timestamp> ewayExpiryTimeList = master.getLrEwayExpirydt();
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();

        thresholdTime = master.getCreatedTime().plusMinutes(minDuration);

        if (master.getVtId() != null && master.getVtTripCompleteTime() == null) {       //Generate as many times trips are added

            if (ewayExpiryTimeList != null && ewayExpiryTimeList.size() > 0) {

                for (int i = 0; i < ewayExpiryTimeList.size(); i++) {

                    if (thresholdTime.isAfter(ewayExpiryTimeList.get(i).toLocalDateTime().withNano(0))) {

                        return true;
                    }
                }
            }
        }

        return false;
    }


    public Boolean isResolved(MasterTableTemp1 master1, Issues issues) {
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
    class Benchmarks extends Alerts.Benchmarks {

        Integer minDuration;

        public Benchmarks() {

            this.minDuration = 60;

        }
    }

    ;

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
