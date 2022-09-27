package com.elogist.vehicle_master_and_alert_creation.utils;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateAndTime {

    //Handle Date and time conversion
    public Timestamp getTimeStampFromString(String timestampStr, String pattern) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date parsedDate = dateFormat.parse(timestampStr);
        Timestamp timestamp = new Timestamp(parsedDate.getTime());
        return timestamp;
    }



    //Add hours and minutes to time
    public Date addTimeToJavaUtilDate(Date date, int hours,int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE,minutes);
        return calendar.getTime();
    }

    public static Long getHourDiff(LocalDateTime time1, LocalDateTime time2){
        if(time1 != null && time2 != null) {
            Duration duration = Duration.between(time1, time2);
            Long res = duration.toHours();
            Long item;
            if ( res != 0) {
                if(time2.isBefore(time1)) {
                    item = Long.parseLong(res.toString().substring(1, res.toString().length()));
                    return item;
                }
                else{
                    return res;
                }
            }
            return 0l;
        }
        else{
            return 0l;
        }
    }

    public static Long getSecDifference(LocalDateTime time1, LocalDateTime time2) {

        Duration duration = Duration.between(time1, time2);
        Long res = duration.toSeconds();
        return res;
    }

    public static Long getMinDifference(LocalDateTime time1, LocalDateTime time2){

        Duration duration = Duration.between(time1, time2);
        Long res = duration.toMinutes();
        return res;

    }

    public static Long TimeDiff(LocalDateTime time1){
        LocalDateTime time2 = LocalDateTime.now();
        Duration duration = Duration.between(time1, time2);
        Long res = duration.toHours();
        return res;
    }


    public static String convertMinutesToDayHoursMinutes(Integer time)
    {
        if(time/24/60 == 0)
            return "" + helpingFunctionForMinutesConversation(time/60%24) + ":" + helpingFunctionForMinutesConversation(time%60);
        return helpingFunctionForMinutesConversation(time/24/60) + "d " + helpingFunctionForMinutesConversation(time/60%24) + ':' + helpingFunctionForMinutesConversation(time%60);
    }

    public static String helpingFunctionForMinutesConversation(Integer num)
    {
        if(num<=9)
            return "0"+num;
        else
            return ""+num;
    }

    public static String localDateTimeToString(LocalDateTime dateTime) {
        String dateTimestr = null;
        try {
            String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
            dateTimestr = dateTime.format(dateTimeFormatter);
        }
        catch (Exception e) {
           log.error("Exception in parsing date: " + e.getMessage());
        }
        return dateTimestr;
    }

    public static long timeDiffInMinutes(LocalDateTime startTime, LocalDateTime endTime) {

        Duration duration = Duration.between(startTime, endTime);
        return duration.toMinutes();
    }

    public static LocalDateTime stringToLocalDateTime(String time) {
        return stringToLocalDateTime(time,true);
    }

    public static LocalDateTime stringToLocalDateTime(String time,Boolean isReplaceT) {
        if(time == null)
            return null;
        if(time.contains(".")){
            time = time.split("\\.")[0];
        }
        if(time.charAt(10) == 'T' && isReplaceT) {
            time = time.replace('T', ' ');
        }
//        System.out.println(time);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(time,formatter);
        return dateTime;
    }

    public static Boolean isBetween(LocalTime targetTime, LocalTime startTime, LocalTime endTime) {
        if(startTime.isAfter(endTime)){
            if( !(targetTime.isAfter(endTime) && targetTime.isBefore(startTime))){
                return true;
            } else{
                return false;
            }
        } else{
            if(targetTime.isAfter(startTime) && targetTime.isBefore(endTime)){
                return true;
            } else{
                return false;
            }
        }
    }

    public static LocalDateTime max (LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if(dateTime1.isAfter(dateTime2))
            return dateTime1;
        else
            return dateTime2;
    }

    public static Boolean isOverlap(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalTime startTime, LocalTime endTime) {

        if(isBetween(startDateTime.toLocalTime(),startTime,endTime) || isBetween(endDateTime.toLocalTime(),startTime,endTime))
            return true;


        LocalDateTime startTimeWDate, endTimeWDate;

        if(startTime.isAfter(endTime)) {

            startTimeWDate = LocalDateTime.of(startDateTime.toLocalDate(),startTime);
            endTimeWDate = LocalDateTime.of(startDateTime.plusDays(1).toLocalDate(), endTime);
        }
        else {
            if(startDateTime.toLocalTime().isAfter(startTime)) {
                startTimeWDate = LocalDateTime.of(startDateTime.plusDays(1).toLocalDate(), startTime);
                endTimeWDate = LocalDateTime.of(startDateTime.plusDays(1).toLocalDate(), endTime);
            }
            else {
                startTimeWDate = LocalDateTime.of(startDateTime.toLocalDate(), startTime);
                endTimeWDate = LocalDateTime.of(startDateTime.toLocalDate(), endTime);
            }
        }

        if(isBetween(startTimeWDate,startDateTime,endDateTime) || isBetween(endTimeWDate,startDateTime,endDateTime))
            return true;
        else
            return false;
    }

    public static Boolean isBetween(LocalDateTime targetDateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        if((targetDateTime.isAfter(startDateTime) && targetDateTime.isBefore(endDateTime)) || targetDateTime.isEqual(startDateTime) || targetDateTime.isEqual(endDateTime))
            return true;
        else
            return false;
    }

    public static  <T> T getJsonToClass(String json, Type tClass){
        // Gson gson = new Gson();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalTime.class, new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                LocalTime localTime = LocalTime.parse(json.getAsString(),DateTimeFormatter.ofPattern("HH:mm"));
                return localTime;
            }
        }).create();
        return gson.fromJson(json,tClass);
    }
}
