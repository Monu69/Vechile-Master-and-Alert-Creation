package com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert;

import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.HaltsEnumeration;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29001ParamDto;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.constants.RedisConstant;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.services.MedProcessAlert29001Service;
import com.elogist.vehicle_master_and_alert_creation.services.MediumProcessingCommonService;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediumProcessingAlert29001 extends MediumProcessingAlert{
    // For Continuous Drive Alerts

    public static Integer alertId = 29001;

    @Autowired
    MedProcessAlert29001Service medProcessAlert29001Service;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MediumProcessingCommonService mediumProcessingCommonService;

    SpecificParameters specificParameters;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yy HH:mm:ss");

    @Override
    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues)  {
        return isValidAlert(master1,issues,false);
    }

    public Boolean isValidAlert(MasterTableTemp1 master1, Issues issues, Boolean isResolve)  {

        // the alerts are generated here
        //*** parameter value is the last halt with duration greater than minHalt
        //list of benchmark has the following:
        // 0th index: max continuous run in minutes
        // 1st index: min halt in minutes
        Benchmarks benchmarks = (Benchmarks) issues.getFinalBenchmark(this);
        Integer maxDayRunDuration = benchmarks.getMaxDayRunDuration();
        Integer minDayHaltDuration = benchmarks.getMinDayHaltDuration();
        boolean alertState ;
        Halt sufficientHalt = null;
        MasterTableTemp2 master2 = master1.getMasterTableTemp2();
        // Redis key is vid+propertyId(issueId)
        String redisKey = master1.getVId().toString() + issues.getId().toString();

        if(master1.getHStatus() == HaltsEnumeration.UNDEFINED.getValue())
            return false;

        // if vehicle is not moving no alert generation
        //null Hendtime means that the vehicle is in halt
        if(master1 != null && master1.getHEndTime() == null) {
            alertState = false;
        }

        //if the last halt was greater than threshold halt
        else if(master1 != null && master1.getHId() != null && DateAndTime.timeDiffInMinutes(master1.getHStartTime(),master1.getHEndTime()) >= minDayHaltDuration) {

            sufficientHalt = new Halt(master1);
            // it ended in the range of now - threshhold run then false else true
            if(DateAndTime.timeDiffInMinutes(master1.getHEndTime(), master1.getTtTime()) <= maxDayRunDuration)
                alertState =  false;
            else {
                alertState = true;
            }
        }

        //here the last halt duration in less than the minHalt required then
        // getting all the halts generated in the last 24 hrs
        else{
            // if vid has corresponding params in redis then pick sufficicent halt from there else calculate
            if(redisTemplate.opsForHash().hasKey(RedisConstant.ALERT,redisKey)) {

                String param = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT, redisKey);
                MedAlert29001ParamDto paramDto = new Gson().fromJson(param, MedAlert29001ParamDto.class);
                sufficientHalt = paramDto.getSufficientHalt();

            }
            else
                sufficientHalt = mediumProcessingCommonService.getLastSufficientHalt(master1.getVId(), master1.getTtTime(), minDayHaltDuration);

            // if sufficient halt is in max run limit then false else true
            if(sufficientHalt == null || DateAndTime.timeDiffInMinutes(sufficientHalt.getEndTime(), master1.getTtTime()) <= maxDayRunDuration)
                alertState = false;
            else
                alertState = true;
//            alertState = medProcessAlert29001Service.getAlertVerify(master1.getVId(),benchmarks);
        }

        // picking M2 alert state from redis or calculating
        if(!isResolve) {
            boolean prevState;
            if (redisTemplate.opsForHash().hasKey(RedisConstant.ALERT, redisKey)) {

                String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT, redisKey);
                MedAlert29001ParamDto paramDto = new Gson().fromJson(paramjson, MedAlert29001ParamDto.class);
                prevState = paramDto.isLastAlertState();
            } else {
                prevState = medProcessAlert29001Service.getAlertVerify(master1.getVId(), maxDayRunDuration, minDayHaltDuration, master2.getTtTime());
            }

            if (sufficientHalt == null) {
                sufficientHalt = mediumProcessingCommonService.getLastSufficientHalt(master1.getVId(), master1.getTtTime(), minDayHaltDuration);
            }

            // saving state and last suff halt in redis
            MedAlert29001ParamDto paramDto = new MedAlert29001ParamDto(sufficientHalt, alertState);
            String paramjson = new Gson().toJson(paramDto);
            redisTemplate.opsForHash().put(RedisConstant.ALERT, redisKey, paramjson);

            if(!prevState && alertState) {
                List<RunPOJO> runList = mediumProcessingCommonService.getRuns(master1.getVId().longValue(), master1.getTtTime().minusMinutes(maxDayRunDuration), master1.getTtTime());
                Long runDuration = mediumProcessingCommonService.getRunDuration(runList);
                Long haltDuration = maxDayRunDuration - runDuration;
                specificParameters = new SpecificParameters(master1.getTtTime().minusMinutes(maxDayRunDuration).format(dateTimeFormatter), master1.getTtTime().format(dateTimeFormatter),
                        DateAndTime.convertMinutesToDayHoursMinutes(runDuration.intValue()), DateAndTime.convertMinutesToDayHoursMinutes(haltDuration.intValue()));
                return true;
            }
            else
                return false;
        }
        return alertState;
//            foEscalationTickets.add(new FoEscalationTickets((MasterTableTemp1) master1,this, issues, benchmarks ));
//        return foEscalationTickets;
    }

    @Override
    public Integer getAlertTypeId() {

        return alertId;
    }

    @Override
    // if benchmarks are not set in DB then we use these default benchmarks
    public Benchmarks getDefaultBenchmark() {

        // 0th index has max continuous run and 1st index has min halt
        Benchmarks benchmarks = new Benchmarks();
        return benchmarks;
    }

    @Override
    public String getRemark(Master master, Issues issues) {

        Benchmarks benchmarks = (Benchmarks) issues.getFinalBenchmark(this);
        String remark = "Vehicle does not have a "+benchmarks.getMinDayHaltDuration()+" minute halt in last "+benchmarks.getMaxDayRunDuration()/60+" hours of moving";
        return remark;
    }

    @Override
    public Integer getPriId(Master master) {

        // Assuming vid is the primary id (alerts are generated for vids)
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


    @Override
    public SpecificParameters getSpecifiedParameter(){

        return specificParameters;

    }

    @Data
    public class Benchmarks extends Alerts.Benchmarks{

        Integer minDayHaltDuration;
        Integer maxDayRunDuration;
        Integer minNightHaltDuration;
        Integer maxNightRunDuration;
        LocalTime nightStart;
        LocalTime nightEnd;

        public Integer getMinDayHaltDuration(){
           return this.minDayHaltDuration;
        }

        public Benchmarks(){

            this.minDayHaltDuration = 20;
            this.minNightHaltDuration = 15;
            this.maxDayRunDuration = 240;
            this.maxNightRunDuration = 120;
            this.nightStart = LocalTime.of(23,0);
            this.nightEnd = LocalTime.of(5,0);

        }
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
