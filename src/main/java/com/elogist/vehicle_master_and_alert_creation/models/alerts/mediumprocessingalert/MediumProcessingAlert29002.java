package com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert;

import com.elogist.vehicle_master_and_alert_creation.models.Enums.HaltsEnumeration;
import com.elogist.vehicle_master_and_alert_creation.models.RunPOJO;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29002ParamDto;
import com.elogist.vehicle_master_and_alert_creation.services.MediumProcessingCommonService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediumProcessingAlert29002 extends MediumProcessingAlert {
    // For Over Driving

    public static Integer alertId = 29002;

    final String nightRule = " - Night Rule";
    final String dayRule = " - Day Rule";
    String remark = " vehicle, in last 24 hr exceeded over driving duration of ";

    SpecificParameters specificParameters;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yy HH:mm:ss");

    @Autowired
    MediumProcessingCommonService mediumProcessingCommonService;

    @Override
    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues)  {
        return isValidAlert(master1,issues,false);
    }

    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues, Boolean isResolve)  {

        //****** parameter value is list of runs in approximately 1 day
        /* list of benchmarks:
        0 : max day run limit
        1 : max night run limit
         */

        Boolean alertState;
        Integer dailyDefInMinutes = 24 * 60;
        Integer maxRunInMinutes;
        LocalDateTime lastNightRun = null;
        MasterTableTemp2 master2 = master1.getMasterTableTemp2();
        Benchmarks benchmarks = (Benchmarks) issues.getFinalBenchmark(this);


        // if vehicle is at halt or no halts were ever created then false else true
        if(master1.getHStatus() == HaltsEnumeration.UNDEFINED.getValue())
            return false;
        if (!Objects.equals(master1.getHStatus(), HaltsEnumeration.RUNNING.getValue()))
            alertState =  false;
        else {

            lastNightRun  = mediumProcessingCommonService.getLastNightRunTime(master1.getVId(), master1.getTtTime(), issues, dailyDefInMinutes, true);
            if(lastNightRun == null) {
                maxRunInMinutes = benchmarks.getMaxDayDuration();
                remark = master1.getRegNo()+remark+maxRunInMinutes/60+" hrs "+dayRule;
            }
            else {
                maxRunInMinutes = benchmarks.getMaxNightDuration();
                remark = master1.getRegNo()+remark+maxRunInMinutes/60+" hrs " +nightRule;
            }

            // if last halt is greater than 24 - maxRunLimt
            if(master1.getHId() != null && DateAndTime.timeDiffInMinutes(master1.getHStartTime(), master1.getHEndTime()) > (dailyDefInMinutes-maxRunInMinutes))
                alertState = false;
            else {

                // getting alert state
                alertState = mediumProcessingCommonService.generateAlert29002(master1, issues, maxRunInMinutes, dailyDefInMinutes);
            }
        }

        if(!isResolve) {
            Boolean prevState = mediumProcessingCommonService.getPrevState29002(master2, issues, benchmarks.getMaxDayDuration(), benchmarks.getMaxNightDuration(), dailyDefInMinutes);
            List<RunPOJO> runList = mediumProcessingCommonService.getRuns(Long.valueOf(master1.getVId()), master1.getTtTime().minusMinutes(dailyDefInMinutes), master1.getTtTime());
            MedAlert29002ParamDto param = new MedAlert29002ParamDto(runList, lastNightRun, alertState);
            String paramjson = new Gson().toJson(param);
            mediumProcessingCommonService.saveParam(master1, issues, paramjson);

            if (!prevState && alertState) {

                Long runDuration = mediumProcessingCommonService.getRunDuration(runList);
                Long haltDuration = dailyDefInMinutes - runDuration;
                specificParameters = new SpecificParameters(master1.getTtTime().minusMinutes(dailyDefInMinutes).format(dateTimeFormatter), master1.getTtTime().format(dateTimeFormatter),
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

        // 0 : day max run limit and 1 : night max run limit
        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    @Override
    public String getRemark(Master master, Issues issues) {
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

        Integer maxDayDuration;
        Integer maxNightDuration;
        LocalTime nightStart;
        LocalTime nightEnd;

        public Benchmarks(){

            this.maxDayDuration = 780;
            this.maxNightDuration = 720;
            this.nightStart = LocalTime.of(23,0);
            this.nightEnd = LocalTime.of(5,0);

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
        String violationStart;
    }

}
