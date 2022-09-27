package com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert;

import com.elogist.vehicle_master_and_alert_creation.models.Enums.HaltsEnumeration;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.PointsWTime;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29002ParamDto;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29201ParamDto;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.constants.UtilConstant;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
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
import java.util.Map;

@Data
@NoArgsConstructor
//@AllArgsConstructor
// Night Drive Alert (>30min or >1km)
public class MediumProcessingAlert29201 extends MediumProcessingAlert{
    // benchmark : 0th : night start
    //             1st: night end

    public static Integer alertId = 29201;

    @Autowired
    MediumProcessingCommonService mediumProcessingCommonService;

    SpecificParameters specificParameters;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yy HH:mm:ss");

    @Override
    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues)  {
        return isValidAlert(master1,issues, false);
    }

    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues, Boolean isResolve)  {

        //****** parameter value is list of points ran in night
        // redis key is vid + propertyId

        Benchmarks benchmarks = (Benchmarks) issues.getFinalBenchmark(this);


        //TODO: max night run duration and distance is harcoded
        Integer maxDuration = benchmarks.getMaxDuration();
        Integer maxDistance = benchmarks.getMaxDistance();
        Boolean alertState;
        LocalTime nightStart = benchmarks.getNightStart();
        LocalTime nightEnd = benchmarks.getNightEnd();

        //if no gps points generated
        if(master1.getHStatus() == HaltsEnumeration.UNDEFINED.getValue()) {
            return false;
        }

        // if tTime is not in night
        if(!DateAndTime.isBetween(master1.getTtTime().toLocalTime(), nightStart, nightEnd) ) {

            if(!isResolve)
                mediumProcessingCommonService.deleteParam(master1,issues);
            return false;
        }
        // if data is from sim return false
        if(master1.getLocFlag() == null || Math.abs(master1.getLocFlag()) == 3) {
            return false;
        }
        // if vehicle is at halt alert state is false
        alertState = mediumProcessingCommonService.generateAlert29201(master1, issues,maxDuration,maxDistance, isResolve);

        if(alertState) {
            String paramsJson = mediumProcessingCommonService.getParamJson(master1, issues);
            MedAlert29201ParamDto params = new Gson().fromJson(paramsJson, MedAlert29201ParamDto.class);
            List<PointsWTime> pointsWTimeList = params.getPointsWTimes();
            LocalDateTime startTime = LocalDateTime.of(master1.getTtTime().toLocalDate(), nightStart);
            Map<String, Double> specificParams = mediumProcessingCommonService.getSpecificParameters(pointsWTimeList, startTime, master1.getTtTime());
            String runDuration = DateAndTime.convertMinutesToDayHoursMinutes(specificParams.get(UtilConstant.runDurationMK).intValue());
            specificParameters = new SpecificParameters(startTime.format(dateTimeFormatter), master1.getTtTime().format(dateTimeFormatter), runDuration,
                    specificParams.get(UtilConstant.distanceCoveredMK)/1000);

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

        Benchmarks benchmarks = (Benchmarks) issues.getFinalBenchmark(this);
        return master.getRegNo()+" vehicle drove at night for more than " + benchmarks.getMaxDuration() + " min duration or " +  benchmarks.getMaxDistance() / 1000 + " km distance";
    }

    @Override
    public Integer getPriId(Master master) {
        return master.getVId();
    }

    @Override
    public Boolean isResolved(MasterTableTemp1 master1, Issues issues)  {
        return !isValidAlert(master1,issues,true);
    }

    @Override
    public Benchmarks getBenchmarks(String str) {

        Benchmarks benchmarks = DateAndTime.getJsonToClass(str, Benchmarks.class);
        return benchmarks;
    }

    @Data
    class Benchmarks extends Alerts.Benchmarks{


        Integer maxDistance;
        Integer maxDuration;
        LocalTime nightStart;
        LocalTime nightEnd;

        public Benchmarks(){

            this.maxDistance = 1000;
            this.maxDuration = 30;
            this.nightStart = LocalTime.of(23,0);
            this.nightEnd = LocalTime.of(5,0);

        }
    };

    public SpecificParameters getSpecifiedParameter(){

        return specificParameters;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SpecificParameters extends Alerts.SpecificParameters{

        String startTime;
        String endTime;
        String runDuration;
        Double distanceCovered;
    }

}
