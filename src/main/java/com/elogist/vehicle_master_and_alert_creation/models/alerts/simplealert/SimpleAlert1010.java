package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.utils.DistanceUtil;
import com.google.gson.Gson;
import lombok.Data;


//Near Destination // 1010
public class SimpleAlert1010 extends SimpleAlert {

    public static Integer alertTypeId = 1010;

    public static Integer nightStart = 5;

    public static Integer nightEnd = 5;


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
        Integer minDistance = benchmark.getMinDistance();
        remarked = "Near Destination " + Math.ceil(minDistance / 1000) + " Kms";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        String priTypeColumn = master.getVId().toString();
        Integer minDistance = benchmark.getMinDistance();
        Boolean isNear = false;
        if (master.getTLat() == null || master.getPLocLat() == null || master.getTLong() == null || master.getPLocLong() == null) {
            return false;
        } else {
            if (master.getPLocLat() != null && master.getPLocLat().size() > 0 && master.getPLocLong().size() > 0) {
                for (int i = 0; i < master.getPLocLat().size(); i++) {
                    if (master.getPLocLat().get(i) != null && master.getPLocLong().get(i) != null && master.getTLat() != null && master.getTLong() != null) {
                        if (DistanceUtil.distanceBtwAAndB(master.getPLocLat().get(i), master.getPLocLong().get(i), master.getTLat(), master.getTLong()) <= minDistance) {
                            isNear = true;
                            break;
                        }
                    }
                }

            }
        }
        return isNear;
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

        Integer minDistance;

        public Benchmarks(){

            this.minDistance = 10000;

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
