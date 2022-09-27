package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.clients.HaltSwiftClient;
import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.HaltsEnumeration;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.PointsWTime;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29002ParamDto;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29003ParamDto;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29004ParamDto;
import com.elogist.vehicle_master_and_alert_creation.models.alertparamsdto.MedAlert29201ParamDto;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert.MediumProcessingAlert29002;
import com.elogist.vehicle_master_and_alert_creation.models.constants.RedisConstant;
import com.elogist.vehicle_master_and_alert_creation.models.constants.UtilConstant;
import com.elogist.vehicle_master_and_alert_creation.models.dto.AlertDetailsDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.dto.RawHaltData;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoEscalationTicketsRepository;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.elogist.vehicle_master_and_alert_creation.utils.haversine;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import swift.common.pojos.Point;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import swift.common.pojos.utilpojos.PointInTime;
import swift.common.pojos.utilpojos.Vehicle;
import swift.common.utils.GeoCalcUtil;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class MediumProcessingCommonService {

    @Autowired
    FoEscalationTicketsRepository foEscalationTicketsRepository;

    @Autowired
    HaltSwiftClient haltSwiftClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    FeignService feignService;

    Gson gson = new Gson();

    public List<AlertDetailsDTO> getAlertDetail(Integer tripId){
        String jsonResult = foEscalationTicketsRepository.getAlertDetail(tripId);
        Type listType = new TypeToken<ArrayList<AlertDetailsDTO>>(){}.getType();
        Gson gson = new Gson();
        List<AlertDetailsDTO> alertDatas = gson.fromJson(jsonResult, listType);

        return alertDatas;
    }

    public Halt getLastSufficientHalt(Integer vehId, LocalDateTime tTime, Integer minHaltInMinutes) {

            Halt suffHalt = null;
            // getting halts
            RawHaltData rawHalts;
            List<Halt> halts = new ArrayList<>();
            try{
                LocalDateTime dataStart = tTime.minusDays(1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedStartTime = dataStart.format(formatter);
                String formattedEndTime = tTime.format(formatter);
                rawHalts = haltSwiftClient.getHalts(vehId.longValue(), formattedStartTime, formattedEndTime);
                halts = rawHalts.getData();
            } catch (Exception e) {
                log.info("Exception in fetching halts for vehId: {}, msg: {}", vehId, e.getMessage());
            }

            if(halts.size() == 0) {
                return null;
            }

            // if vehicle was on halt at tTime
            if(halts.get(halts.size()-1).getEndTime() == null || halts.get(halts.size()-1).getEndTime().isAfter(tTime)) {
                halts.get(halts.size()-1).setEndTime(tTime);
            }

            // iterating halts latest to old
            for(int i=halts.size()-1; i>=0;i--) {

                Halt currHalt = halts.get(i);
                if(DateAndTime.timeDiffInMinutes(DateAndTime.max(currHalt.getKey().getStartTime(),tTime.minusMinutes(24*60)),currHalt.getEndTime()) >= minHaltInMinutes) {
                    suffHalt = currHalt;
                    break;
                }
            }

            return suffHalt;
    }

    public Halt getLastSuffHalt(Master master, Issues issues, Integer minHaltInMinutes, Integer thresholdMinutes, Boolean redis) {

        String redisKey  = master.getVId().toString()+issues.getId().toString();
        Halt suffHalt = null;

        if(redis && redisTemplate.opsForHash().hasKey(RedisConstant.ALERT,redisKey)) {

            String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT,redisKey);
            MedAlert29004ParamDto param = new Gson().fromJson(paramjson, MedAlert29004ParamDto.class);
            suffHalt = param.getSuffHalt();

            if(suffHalt != null && DateAndTime.timeDiffInMinutes(DateAndTime.max(suffHalt.getKey().getStartTime(), master.getTtTime().minusMinutes(thresholdMinutes)), suffHalt.getEndTime()) >= minHaltInMinutes) {
                return suffHalt;
            }
        }

        List<Halt> halts = feignService.getHalts(Long.valueOf(master.getVId()), master.getTtTime().minusMinutes(thresholdMinutes), master.getHEndTime());

        if(halts.size() == 0) {
            return null;
        }

        // if vehicle was on halt at tTime
        if(halts.get(halts.size()-1).getEndTime() == null || halts.get(halts.size()-1).getEndTime().isAfter(master.getTtTime())) {
            halts.get(halts.size()-1).setEndTime(master.getTtTime());
        }

        // iterating halts latest to old
        for(int i=halts.size()-1; i>=0;i--) {

            Halt currHalt = halts.get(i);
            if(DateAndTime.timeDiffInMinutes(DateAndTime.max(currHalt.getKey().getStartTime(),master.getTtTime().minusMinutes(thresholdMinutes)),currHalt.getEndTime()) >= minHaltInMinutes) {
                suffHalt = currHalt;
                break;
            }
        }

        return suffHalt;
    }

    public LocalDateTime getLastNightRunTime(Integer vehId, LocalDateTime toTime,Issues issues, Integer thresholdInMinutes, Boolean redis) {
        // returns last date time of night run in threshold time else null
        // first we check in REDIS
        String redisKey = vehId.toString()+issues.getId().toString();

        if(redis && redisTemplate.opsForHash().hasKey(RedisConstant.ALERT,redisKey)) {

            String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT, redisKey);
            MedAlert29002ParamDto param = new Gson().fromJson(paramjson, MedAlert29002ParamDto.class);
            LocalDateTime nightRun = param.getLastNightRun();

            // if last run is within threshold in minutes
            if(nightRun != null && DateAndTime.timeDiffInMinutes(nightRun,toTime) <= thresholdInMinutes)
                return nightRun;
        }

        LocalTime sNight = issues.getNightStart();
        LocalTime eNight = issues.getNightEnd();
        List<RunPOJO> runList = getRuns(Long.valueOf(vehId), toTime.minusMinutes(thresholdInMinutes), toTime);

        //iterating runs in descending order and chekcing overlapping with startNight and endNight
        for(RunPOJO run : runList) {

            if(DateAndTime.isOverlap(run.getStartTime(),run.getEndTime(),sNight,eNight)) {
                if(DateAndTime.isBetween(run.getEndTime().toLocalTime(),sNight,eNight))
                    return run.getEndTime();
                else if(sNight.getHour() > eNight.getHour())
                    return LocalDateTime.of(run.getEndTime().toLocalDate(),eNight);
                else
                    return LocalDateTime.of(run.getStartTime().toLocalDate(),eNight);
            }
        }

        return null;
    }

    public List<RunPOJO> getRuns(Long vehId, LocalDateTime startTime, LocalDateTime endTime) {

        List<Halt> halts = feignService.getHalts(vehId, startTime, endTime);

        if(halts.size() == 0)
            return new ArrayList<>(List.of(new RunPOJO(vehId, startTime, endTime)));

        List<RunPOJO> runList = new ArrayList<>();

        LocalDateTime sTime = startTime;

        if(halts.get(halts.size()-1).getEndTime() ==null || halts.get(halts.size()-1).getEndTime().isAfter(endTime))
            halts.get(halts.size()-1).setEndTime(endTime);
        // making a list of all the runs
        for(int i=0;i<halts.size() && sTime.isBefore(endTime);i++) {

            Halt currHalt = halts.get(i);

            // if start time is not between halt start and halt end
            if(!DateAndTime.isBetween(sTime,currHalt.getKey().getStartTime(),currHalt.getEndTime()))
                runList.add(new RunPOJO(currHalt.getKey().getVehicleId(), sTime, currHalt.getKey().getStartTime()));

            sTime = currHalt.getEndTime().plusSeconds(1);
        }

        if(endTime.isAfter(sTime))
            runList.add(new RunPOJO(vehId.longValue(), sTime,endTime));

        return runList;
    }

    public Boolean generateAlert29002(Master master, Issues issues, Integer maxRunInMinutes, Integer dailyDefInMinutes) {

        // Redis Key is vid + propertyId
        String redisKey = master.getVId().toString()+issues.getId().toString();

        List<RunPOJO> runs;

        // if redis contains list of runs
        if(redisTemplate.opsForHash().hasKey(RedisConstant.ALERT, redisKey)) {

            String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT, redisKey);
            MedAlert29002ParamDto param = new Gson().fromJson(paramjson, MedAlert29002ParamDto.class);
            runs = param.getRuns() != null ? param.getRuns() : null;
        }
        else {
                runs = getRuns(Long.valueOf(master.getVId()), master.getTtTime().minusMinutes(dailyDefInMinutes), master.getTtTime());
        }

        Long totalRunInMinutes  = 0L;

        for(RunPOJO run : runs) {
            totalRunInMinutes += DateAndTime.timeDiffInMinutes(run.getStartTime(),run.getEndTime());
        }
        if(totalRunInMinutes <= maxRunInMinutes)
            return false;
        else
            return true;
    }

    public Boolean getPrevState29002(Master master,Issues issues, Integer maxDayDuration, Integer maxNightDuration, Integer thresholdMinutes)  {

        String redisKey = master.getVId().toString()+issues.getId().toString();

        if(redisTemplate.opsForHash().hasKey(RedisConstant.ALERT, redisKey)){

            String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT,redisKey);
            MedAlert29002ParamDto param = new Gson().fromJson(paramjson, MedAlert29002ParamDto.class);
            if(param.getLastAlertState() != null)
                return param.getLastAlertState();
        }

        if(master.getHStatus() != HaltsEnumeration.RUNNING.getValue())
            return false;

        List<RunPOJO> runs = getRuns(Long.valueOf(master.getVId()), master.getTtTime().minusMinutes(thresholdMinutes), master.getTtTime());

        LocalDateTime lastNightRun = getLastNightRunTime(master.getVId(), master.getTtTime(), issues, thresholdMinutes, false);

        Integer thresholdRunInMinutes;

        if(lastNightRun != null)
            thresholdRunInMinutes = maxDayDuration;
        else
            thresholdRunInMinutes = maxNightDuration;

        Long totalRun = 0L;

        for(RunPOJO run : runs)
            totalRun += DateAndTime.timeDiffInMinutes(run.getStartTime(), run.getEndTime());

        if(totalRun <= thresholdRunInMinutes)
            return false;
        else
            return true;
    }


    public void saveParam(Master master, Issues issues, String paramjson) {

        String redisKey = master.getVId().toString()+issues.getId().toString();
        redisTemplate.opsForHash().put(RedisConstant.ALERT,redisKey,paramjson);
    }

    public Boolean getPrevState29004(Master master, Issues issues, Integer minHaltInMinutes, Integer thresholdMinutes) {

        String redisKey = master.getVId().toString()+issues.getId().toString();

        if(redisTemplate.opsForHash().hasKey(RedisConstant.ALERT, redisKey)) {

            String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT,redisKey);
            MedAlert29004ParamDto param = new Gson().fromJson(paramjson, MedAlert29004ParamDto.class);
            if(param.getLastAlertState() != null)
                return param.getLastAlertState();
        }

        if(master.getHStatus() != HaltsEnumeration.RUNNING.getValue())
            return false;

        Halt suffHalt = getLastSuffHalt(master, issues, minHaltInMinutes, thresholdMinutes, false);

        if(suffHalt != null)
            return false;
        else
            return true;
    }

    public Boolean getPrevState29003(Master master, Issues issues,Integer minHaltInMiutes, Integer thresholdMinutes) {

        String reidsKey = master.getVId().toString()+issues.getId().toString();

        if(redisTemplate.opsForHash().hasKey(RedisConstant.ALERT,reidsKey)) {

            String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT,reidsKey);
            MedAlert29003ParamDto param = new Gson().fromJson(paramjson,MedAlert29003ParamDto.class);
            if(param.getLastAlertState() != null)
                return param.getLastAlertState();
        }

        if(master.getHStatus() != HaltsEnumeration.RUNNING.getValue())
            return false;

        Halt suffHalt = getLastSuffHalt(master,issues,minHaltInMiutes,thresholdMinutes,false);

        if(suffHalt != null)
            return false;
        else
            return true;
    }

    public void deleteParam(Master master, Issues issues) {

        String redisKey = master.getVId().toString()+issues.getId().toString();
        if(redisTemplate.opsForHash().hasKey(RedisConstant.ALERT, redisKey)) {
            redisTemplate.opsForHash().delete(RedisConstant.ALERT,redisKey);
        }
    }

    public Boolean generateAlert29201(Master master, Issues issues, Integer maxRunDurInMin, Integer maxRunDisInMtr, Boolean isResolve) {

        //rediskey is vid+propertyId
        String redisKey = master.getVId().toString()+issues.getId().toString();

        List<PointsWTime> pointsWTimes = new ArrayList<>();

        Boolean alertState;
        Boolean prevState = null;

        Double distance = 0.0;
        Long duration = 0L;

        if(master.getHStatus() == HaltsEnumeration.IDLE.getValue()) {
            log.debug("redisKey: {} , is in halt", redisKey);
            alertState = false;
            if(redisTemplate.opsForHash().hasKey(RedisConstant.ALERT,redisKey)) {
                String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT,redisKey);
                MedAlert29201ParamDto param = new Gson().fromJson(paramjson,MedAlert29201ParamDto.class);
                pointsWTimes = param.getPointsWTimes();
                pointsWTimes.add(new PointsWTime(master.getTLat(),master.getTLong(),master.getTtTime()));
            }
        }
        else {
            // if redisKey does not exist
            if (!redisTemplate.opsForHash().hasKey(RedisConstant.ALERT, redisKey)) {
                log.debug("redisKey: {} has no key in redis",redisKey);
                alertState = false;
                pointsWTimes.add(new PointsWTime(master.getTLat(),master.getTLong(),master.getTtTime()));
            }
            // getting prev run points from redis
            else {
                String paramjson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT, redisKey);
                MedAlert29201ParamDto param = new Gson().fromJson(paramjson, MedAlert29201ParamDto.class);
                pointsWTimes = param.getPointsWTimes();
                prevState = param.getPrevState();
                if(param.getPointsWTimes() != null)
                    log.debug("redisKey: {} has key in redis and length of points: {} and prevState: {}", redisKey, pointsWTimes.size(), prevState);
                else
                    log.debug("redisKey: {} has key and no points prevState: {}", redisKey, prevState);

                // adding latest point
                if ( pointsWTimes == null || pointsWTimes.isEmpty() || master.getTtTime().isAfter(pointsWTimes.get(pointsWTimes.size() - 1).getDateTime())) {
                    if(pointsWTimes == null)
                        pointsWTimes = new ArrayList<>();
                    pointsWTimes.add(new PointsWTime(master.getTLat(), master.getTLong(), master.getTtTime()));

                }
                else {
                    if(prevState == null)
                        alertState = false;
                    else
                        alertState = prevState;
                }

            }
            // calculating total distance travelled
//            PointsWTime prevPoint = pointsWTimes.get(0);

            for (int i = 1; i < pointsWTimes.size(); i++) {

                PointsWTime currPoint = pointsWTimes.get(i);
                PointsWTime prevPoint = pointsWTimes.get(i-1);
                distance += GeoCalcUtil.haversineDistanceInMetres(prevPoint.getLat(),prevPoint.getLng(),currPoint.getLat(),currPoint.getLng());

                if (!prevPoint.samePos(currPoint))
                    duration += DateAndTime.timeDiffInMinutes(prevPoint.getDateTime(), currPoint.getDateTime());
            }

            if (distance >= maxRunDisInMtr || duration >= maxRunDurInMin)
                alertState = true;
            else
                alertState = false;

            log.debug("redisKey: {} distance: {}, duration: {}", redisKey, distance, duration);

        }



        if(!alertState && prevState != null && prevState && !isResolve)
            deleteParam(master, issues);
        else if(!isResolve){
            MedAlert29201ParamDto param = new MedAlert29201ParamDto(pointsWTimes, alertState);
            String paramjson = new Gson().toJson(param);
            redisTemplate.opsForHash().put(RedisConstant.ALERT,redisKey,paramjson);
        }

        if(alertState && (prevState == null || !prevState)) {
            if(redisTemplate.opsForHash().hasKey(RedisConstant.ALERT, redisKey))
                log.debug("redisKey: {} night drive alert for vid: {}, dur: {}, dist: {}, redis: {} ",redisKey, master.getVId(), duration, distance, redisTemplate.opsForHash().get(RedisConstant.ALERT, redisKey));
            else
                log.debug("redisKey: {} night drive alert for vid: {}, dur: {}, dist: {}, redis: {} ",redisKey, master.getVId(), duration, distance, "empty");
            return true;
        }
        else
            return false;
    }

    public boolean generateAlert3001(Master master, Integer minDistanceInMeters, Integer durationInMins) {

        String redisKey = "GpsData"+master.getVId();

        Set<String> pointInTimesSet;
//        LocalDateTime dataStart;
//        if(master.getTtTime().minusMinutes(durationInMins).isBefore(startTime))
//            dataStart = master.getTtTime().minusMinutes(durationInMins);
//        else
//            dataStart = startTime;

        LocalDateTime dataStart = master.getTtTime().minusMinutes(durationInMins);

        double setRangeStart = dataStart.toEpochSecond(ZoneOffset.UTC);
        if(redisTemplate.hasKey(redisKey))
            pointInTimesSet = redisTemplate.opsForZSet().rangeByScore(redisKey,setRangeStart, master.getTtTime().toEpochSecond(ZoneOffset.UTC));
        else {
            log.debug("No redis key present for vid: {}", master.getVId());
            return false;
        }

        //calculating total distance travelled.
        List<PointInTime> pointInTimeList = new ArrayList<>();
        for(String pointInTimeJson : pointInTimesSet) {
            pointInTimeList.add(gson.fromJson(pointInTimeJson, PointInTime.class));
        }
        Collections.sort(pointInTimeList, (p1,p2) -> p1.getDttime().compareTo(p2.getDttime()));

        double distanceCovered = 0;
        for(int itr = 1; itr < pointInTimeList.size(); itr++) {

            Point currPoint = pointInTimeList.get(itr).getPoint();
            Point prevPoint = pointInTimeList.get(itr-1).getPoint();

            distanceCovered += GeoCalcUtil.haversineDistanceInMetres(currPoint.getLat(),currPoint.getLng(),prevPoint.getLat(), prevPoint.getLng());
        }
        if(distanceCovered < minDistanceInMeters)
            return true;
        else
            return false;
    }

    public Long getRunDuration(List<RunPOJO> runList) {

        Duration duration = Duration.ZERO;
        for(RunPOJO run : runList) {
            duration =  duration.plusMinutes(DateAndTime.timeDiffInMinutes(run.getStartTime(), run.getEndTime()));
        }

        return duration.toMinutes();
    }

    public String getParamJson(Master master, Issues issues) {

        String redisKey = master.getVId().toString()+issues.getId().toString();
        String paramJson = (String) redisTemplate.opsForHash().get(RedisConstant.ALERT, redisKey);
        return paramJson;
    }

    public Map<String,Double> getSpecificParameters(List<PointsWTime> pointsWTimeList, LocalDateTime startTime, LocalDateTime endTime) {

        Map<String,Double> specificParameters = new HashMap<>();
        Double runDuration = 0.0, distanceCovered = 0.0;

        for(int itr=1;itr<pointsWTimeList.size();itr++) {

            PointsWTime prevPoint = pointsWTimeList.get(itr-1);
            PointsWTime currPoint = pointsWTimeList.get(itr);
            if(prevPoint.getDateTime().isBefore(startTime))
                continue;

            if(!prevPoint.getLat().equals(currPoint.getLat()) && !prevPoint.getLng().equals(currPoint.getLng())) {
                runDuration += DateAndTime.timeDiffInMinutes(prevPoint.getDateTime(), currPoint.getDateTime());
                distanceCovered += GeoCalcUtil.haversineDistanceInMetres(prevPoint.getLat(), prevPoint.getLng(), currPoint.getLat(), currPoint.getLng());
            }

            if(!currPoint.getDateTime().isBefore(endTime))
                break;
        }

        specificParameters.put(UtilConstant.distanceCoveredMK, distanceCovered);
        specificParameters.put(UtilConstant.runDurationMK, runDuration);
        return specificParameters;
    }
}
