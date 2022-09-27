package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.google.gson.Gson;
import lombok.Data;


//Over Speeding // 1014
public class SimpleAlert1014 extends SimpleAlert {
    public static Integer alertTypeId = 1014;

    public Integer getAlertTypeId() {
        return alertTypeId;
    }

    public Benchmarks getDefaultBenchmark() {
       Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    public Integer getPriId(Master master) {

        Integer priId = master.getVId();
        return priId;

    }


    public String getRemark(Master master, Issues issues)  {
        String remarked = "";
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minSpeed = benchmark.getMinSpeed();
        remarked = "Over Speeding > " + minSpeed + " Kmph";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minSpeed = benchmark.getMinSpeed();
        if (master.getSpeed() == null) {
            return false;
        }
        if (master.getSpeed() > minSpeed) {
            return true;
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

        Integer minSpeed;

        public Benchmarks(){

            this.minSpeed = 60;

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