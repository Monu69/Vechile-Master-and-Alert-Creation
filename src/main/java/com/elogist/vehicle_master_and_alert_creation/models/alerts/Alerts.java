package com.elogist.vehicle_master_and_alert_creation.models.alerts;

import com.elogist.vehicle_master_and_alert_creation.annotations.FieldDescription;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.StateBenchmarkEnum;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.Master;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
public abstract class Alerts {

    public abstract Integer getAlertTypeId();

    public abstract Benchmarks getDefaultBenchmark();

    public abstract String getRemark(Master master, Issues issues) ;

    public abstract Integer getPriId(Master master);

    public abstract Boolean isResolved(MasterTableTemp1 master1, Issues issues) ;

    public abstract Benchmarks getBenchmarks(String str);

    public abstract SpecificParameters getSpecifiedParameter();

    @Data
    public static class Benchmarks{

        @FieldDescription(description = "activation state of vehicle", unit = "multi select")
        List<Integer> stateCodes ;

        public static String getDefaultStateCodes() {

            Map<Integer,String> stateBenchmarks = StateBenchmarkEnum.getAllEnums();
            String json = new Gson().toJson(stateBenchmarks);
            return json;
        }

    }


    public static class SpecificParameters{ }

    public static List<BenchMarkDetails> populateGeneralBenchmarks(AlertType alertType) {

        Field[] fields = Alerts.Benchmarks.class.getDeclaredFields();

        for(Field field : fields) {
            String name = field.getName();
            String typeName = field.getGenericType().getTypeName();
            String dataType = typeName.replaceAll("java\\..*?\\.","");
            String description = "UNKNOWN", unit = "UNKNOWN";
            if (field.isAnnotationPresent(FieldDescription.class)) {
                description = field.getAnnotation(FieldDescription.class).description();
                unit = field.getAnnotation(FieldDescription.class).unit();
            }

            String methodName = "getDefault";
            String defaultValue = "";
            methodName = methodName + name.replaceFirst(String.valueOf(name.charAt(0)),String.valueOf(name.charAt(0)).toUpperCase(Locale.ROOT));
            try{
                Method method = Alerts.Benchmarks.class.getMethod(methodName);
                defaultValue = (String) method.invoke(new Alerts.Benchmarks());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error("no {} method exist with no arguments", methodName);
            }

            BenchMarkDetails benchMarkDetails = new BenchMarkDetails(description, name, dataType, unit, defaultValue);
            if(alertType.getBenchmark()!= null)
                alertType.getBenchmark().add(benchMarkDetails);
            else {
                List<BenchMarkDetails> benchMarkDetails1 = new ArrayList<>();
                benchMarkDetails1.add(benchMarkDetails);
                alertType.setBenchmark(benchMarkDetails1);
            }
        }
        return alertType.getBenchmark();
    }
}
