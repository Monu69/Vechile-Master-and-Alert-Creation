package com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert;

import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.HaltsEnumeration;
import com.elogist.vehicle_master_and_alert_creation.models.Halt;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29004ParamDto;
import com.elogist.vehicle_master_and_alert_creation.services.MediumProcessingCommonService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediumProcessingAlert29004 extends MediumProcessingAlert {
    //For Weekly Rest Violation

    public static Integer alertId = 29004;

    //    public final String NIGHT_VIOLATION = " - Night Rule";
//    public final String

    @Autowired
    MediumProcessingCommonService mediumProcessingCommonService;

    SpecificParameters specificParameters;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yy HH:mm:ss");

    @Override
    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues)  {
        return isValidAlert(master1, issues, false);
    }

    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues, Boolean isResolve)  {

        // ********* parameter valus is last suffcient halt in 7 days
        /* list of benchmarks:
        0 : min rest limit in a week
         */

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        MasterTableTemp2 master2 = master1.getMasterTableTemp2();
        Boolean alertState;
        Integer weekDefInMinutes = 7 * 24 * 60;
        Integer minHaltInMinutes = benchmark.getMinDuration();
        Halt suffHalt = null;

        // if vehicle is at halt or no halts were created from false else true
        if(master1.getHStatus() == HaltsEnumeration.UNDEFINED.getValue())
            return false;
        if(master1.getHStatus() != HaltsEnumeration.RUNNING.getValue())
            alertState = false;
        // if last halt duration in greater than min halt required
        else if(master1.getHId() != null &&
                DateAndTime.timeDiffInMinutes(DateAndTime.max(master1.getHStartTime(),master1.getTtTime().minusMinutes(weekDefInMinutes)), master1.getHEndTime()) >= minHaltInMinutes) {
            suffHalt = new Halt(master1);
            alertState = false;
        }
        else {
            // getting suff halt from redis if not found then getting from halts
            suffHalt = mediumProcessingCommonService.getLastSuffHalt(master1, issues, minHaltInMinutes, weekDefInMinutes, true);
            if(suffHalt != null)
                alertState = false;
            else
                alertState = true;
        }

        if(!isResolve) {
            Boolean prevState = mediumProcessingCommonService.getPrevState29004(master2, issues, minHaltInMinutes, weekDefInMinutes);

            MedAlert29004ParamDto param = new MedAlert29004ParamDto(suffHalt, alertState);
            String paramjson = new Gson().toJson(param);
            mediumProcessingCommonService.saveParam(master1, issues, paramjson);

            if (!prevState && alertState) {
                List<RunPOJO> runList = mediumProcessingCommonService.getRuns(master1.getVId().longValue(), master1.getTtTime().minusMinutes(weekDefInMinutes), master1.getTtTime());
                Long runDuration = mediumProcessingCommonService.getRunDuration(runList);
                Long haltDuration = weekDefInMinutes - runDuration;
                specificParameters = new SpecificParameters(master1.getTtTime().minusMinutes(weekDefInMinutes).format(dateTimeFormatter), master1.getTtTime().format(dateTimeFormatter),
                        DateAndTime.convertMinutesToDayHoursMinutes(runDuration.intValue()), DateAndTime.convertMinutesToDayHoursMinutes(haltDuration.intValue()));
                return true;
            }
            else
                return false;
        }
        return alertState;
    }

    @Override
    public Integer getAlertTypeId() {
        return alertId;
    }

    @Override
    // if we dont get benchmarks from DB we use default benchmarks
    public Benchmarks getDefaultBenchmark() {
        // 0th ind : min continuous halt in week in minutes
        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    @Override
    public String getRemark(Master master, Issues issues) {

        Benchmarks benchmark = (Benchmarks) issues.getFinalBenchmark(this);
        String remark = "Vehicle "+master.getRegNo()+" did not have a "+benchmark.getMinDuration()+" hr continuous halt in last 7 " +
                "days";
        return remark;
    }

    @Override
    public Integer getPriId(Master master) {
        Integer priId = master.getVId();
        return priId;
    }

    public Boolean isResolved(MasterTableTemp1 master1, Issues issues)  {
        return !isValidAlert(master1, issues, true);
    }

    @Override
    public Benchmarks getBenchmarks(String str) {

        Gson gson = new Gson();
        Benchmarks benchmarks = DateAndTime.getJsonToClass(str, Benchmarks.class);
        return benchmarks;
    }

    @Data
    class Benchmarks extends Alerts.Benchmarks{

        Integer minDuration;

        public Benchmarks(){

            this.minDuration = 1440;

        }
    };

    public SpecificParameters getSpecifiedParameter(){

        return specificParameters;

    }

    @Data
    @AllArgsConstructor
    class SpecificParameters extends Alerts.SpecificParameters{

        String startTime;
        String endTime;
        String runDuration;
        String haltDuration;
    }

}
