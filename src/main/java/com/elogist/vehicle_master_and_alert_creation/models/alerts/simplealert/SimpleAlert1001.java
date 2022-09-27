package com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert;

import com.elogist.vehicle_master_and_alert_creation.models.Enums.HaltTypeEnum;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.PrimStatusEnum;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.SecStatusEnum;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.HaltsEnumeration;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor

//Vehicle Halt more than constant hrs // 1001
public class SimpleAlert1001 extends SimpleAlert {
    public static Integer alertTypeId = 1001;

    public Integer getPriId(Master master){

        Integer priId = master.getHId();
        return priId;

    }

    public Integer getAlertTypeId(){
        return alertTypeId;
    }

    public Benchmarks getDefaultBenchmark(){
        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    public String getRemark(Master master, Issues issues)  {
        String remarked = "";
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        remarked = "Vehicle Halt more than "+DateAndTime.convertMinutesToDayHoursMinutes(minDuration)+ " Hours";
        return remarked;
    }

    public Boolean isValidAlert(Master master, Issues issues)  {
        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        Integer minDuration = benchmark.getMinDuration();
        Long minuteDiff;

        if(master.getTtTime() != null && master.getHStartTime() != null){
            minuteDiff = DateAndTime.getMinDifference(master.getHStartTime(), master.getTtTime());
        }
        else{
            return false;
        }
        if(master.getHStatus().equals(HaltsEnumeration.IDLE.getValue()) && master.getHhaltTypeId() != HaltTypeEnum.LOADING.getValue()
                                                                   && master.getHhaltTypeId() != HaltTypeEnum.UNLOADING.getValue()
                                                                   && master.getHSiteType() != HaltsEnumeration.IDLE.getValue() && (minuteDiff > minDuration)) {
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
        Benchmarks benchmarks = gson.fromJson(str,Benchmarks.class);
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

        LocalDateTime haltStartTimeM1 = masterTableTemp1.getHStartTime();
        LocalDateTime haltEndTimeM1 = masterTableTemp1.getHEndTime();
        LocalDateTime haltStartTimeM2 = masterTableTemp1.getMasterTableTemp2().getHStartTime();
        LocalDateTime haltEndTimeM2 = masterTableTemp1.getMasterTableTemp2().getHEndTime();

        if (haltStartTimeM1 != null && haltStartTimeM2 != null && haltStartTimeM1.equals(haltStartTimeM2) && haltEndTimeM1 != null && haltEndTimeM2 == null) {

            return false;

        } else {

            return true;

        }
    }



}
