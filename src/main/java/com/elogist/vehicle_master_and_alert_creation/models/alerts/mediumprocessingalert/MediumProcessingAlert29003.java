package com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert;

import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.HaltsEnumeration;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29003ParamDto;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.services.MediumProcessingCommonService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediumProcessingAlert29003 extends MediumProcessingAlert {
    // For Daily Rest Violation

    final String nightRule = " - Night Rule";
    final String dayRule = " - Day Rule";
    String remark = " vehicle, in last 24 hr did not have any halt with duration ";

    public static Integer alertId = 29003;

    @Autowired
    RedisTemplate redisTemplate;


    @Autowired
    MediumProcessingCommonService mediumProcessingCommonService;

    SpecificParameters specificParameters;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yy HH:mm:ss");


    @Override
    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues)  {
        return isValidAlert(master1, issues, false);
    }

    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues, Boolean isResolve)  {

        //******** parameter value is last suffcient halt for day and night also last state
        /* list of benchmark:
            0 : min coninuous halt for day in minutes
            1: min continuous halt for night in minutes
         */
        // redis key is vid + propertyId
        Benchmarks benchmarks = (Benchmarks) issues.getFinalBenchmark(this);
        MasterTableTemp2 master2 = master1.getMasterTableTemp2();

        // min required halt depend on day and night
        Integer minHaltReq ;
        Integer dailyDefInMinutes = 24*60;
        LocalDateTime lastNightRun = null;
        Boolean alertState;
        Halt suffHalt = null;

        LocalTime sNight = issues.getNightStart();
        LocalTime eNight = issues.getNightEnd();

        if(master1.getHStatus() == HaltsEnumeration.UNDEFINED.getValue()){
            return false;
        }

        //getting last night run time
        lastNightRun = mediumProcessingCommonService.getLastNightRunTime(master1.getVId(),master1.getTtTime(),issues,dailyDefInMinutes,true);
        //using appropriate benchmark and initialising remark accordingly
        if(lastNightRun == null) {
            minHaltReq = benchmarks.getMinDayDuration();
            remark = master1.getRegNo()+remark+minHaltReq/60+ " hrs " + dayRule;
        }
        else {
            minHaltReq = benchmarks.minNightDuration;
            remark = master1.getRegNo()+remark+minHaltReq/60+ " hrs " + nightRule;
        }

        //if vehicle is not running then this state is false
        if(master1.getHStatus() == HaltsEnumeration.IDLE.getValue())
            alertState = false;
        else {

            // if last halt from master is sufficient
            if(master1.getHId() != null && DateAndTime.timeDiffInMinutes(DateAndTime.max(master1.getHStartTime(),master1.getTtTime().minusMinutes(dailyDefInMinutes)), master1.getHEndTime()) >= minHaltReq) {
                suffHalt = new Halt(master1);
                alertState = false;
            }
            else {
                suffHalt = mediumProcessingCommonService.getLastSuffHalt(master1,issues,minHaltReq,dailyDefInMinutes,true);
                if(suffHalt == null)
                    alertState = true;
                else
                    alertState = false;
            }
        }

        if(!isResolve) {
            Boolean prevState = mediumProcessingCommonService.getPrevState29003(master2, issues, minHaltReq, dailyDefInMinutes);

            //initializing param to save in reids
            MedAlert29003ParamDto param;

            if (minHaltReq == benchmarks.getMinDayDuration())
                param = new MedAlert29003ParamDto(suffHalt, alertState, lastNightRun);
            else
                param = new MedAlert29003ParamDto(suffHalt, suffHalt, alertState, lastNightRun);
            String paramjson = new Gson().toJson(param);

            mediumProcessingCommonService.saveParam(master1, issues, paramjson);

            if (!prevState && alertState) {
                List<RunPOJO> runList = mediumProcessingCommonService.getRuns(master1.getVId().longValue(), master1.getTtTime().minusMinutes(dailyDefInMinutes), master1.getTtTime());
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
    public Benchmarks getDefaultBenchmark() {

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
        return !isValidAlert(master1,issues,true);
    }

    @Override
    public Benchmarks getBenchmarks(String str) {

        Gson gson = new Gson();
        Benchmarks benchmarks = DateAndTime.getJsonToClass(str, Benchmarks.class);
        return benchmarks;
    }

    @Data
    class Benchmarks extends Alerts.Benchmarks{

        Integer minDayDuration;
        Integer minNightDuration;
        LocalTime nightStart;
        LocalTime nightEnd;

        public Benchmarks(){

            this.minDayDuration = 480;
            this.minNightDuration = 600;
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
