package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.models.FoEscalationTickets;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp1;
import com.elogist.vehicle_master_and_alert_creation.models.MasterTableTemp2;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert.MediumProcessingAlert;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert.SimpleAlert;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp2Repository;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.elogist.vehicle_master_and_alert_creation.utils.StringToClass;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestingService {

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    MasterTableTemp2Repository masterTableTemp2Repository;

    @Autowired
    ApplicationContext applicationContext;

    public void testDataGson() {
        Gson gson = new Gson();
        String result = "{\"foIssueTypeId\":1014,\"foId\":19379,\"priId\":124767,\"secId2\":1281580,\"remark\":\"Over Speeding \\u003e 45 Kmph\",\"entryMode\":0,\"adUserId\":0,\"addTime\":{\"date\":{\"year\":2022,\"month\":4,\"day\":19},\"time\":{\"hour\":12,\"minute\":19,\"second\":49,\"nano\":0}},\"vehicleId\":124767,\"issuePropertiesId\":380,\"isAllocated\":false,\"m1state\":\"{\\\"showprim_status\\\":\\\"Available, Unauthorised Movement\\\",\\\"suggest\\\":0,\\\"hrssince\\\":736,\\\"showtripstart\\\":\\\"KANDLA#008000\\\",\\\"showstarttime\\\":\\\"2022-03-17T17:10:24\\\",\\\"showtripend\\\":\\\"VAYOR#ff0000\\\",\\\"showendtime\\\":\\\"2022-03-19T18:51:38\\\",\\\"showtripplacement\\\":\\\"\\\",\\\"kpidist\\\":0,\\\"kpitime\\\":0,\\\"distleft\\\":0,\\\"prim_status\\\":21,\\\"sec_status\\\":51,\\\"halt_sec_status\\\":51,\\\"halt_prim_status\\\":21,\\\"nearby_status\\\":0,\\\"ttime\\\":\\\"2022-04-19T12:09:28.557\\\",\\\"tlat\\\":23.010987,\\\"tlong\\\":71.456076,\\\"load_status\\\":0,\\\"h_status\\\":0,\\\"ssh_status\\\":-1,\\\"loc_flag\\\":1,\\\"v_id\\\":124767,\\\"v_regno\\\":\\\"GJ12BX9030\\\",\\\"v_foid\\\":19379,\\\"v_latch_time\\\":\\\"2022-04-19T11:18:53.697\\\",\\\"v_latch_lat\\\":23.073236,\\\"v_latch_long\\\":71.793289,\\\"v_is_ncv\\\":null,\\\"vpid\\\":722811,\\\"pickup_time\\\":\\\"2021-12-02T18:20:17.491008\\\",\\\"vp_driver_id\\\":33125,\\\"vp_vehicle_id\\\":124767,\\\"vg_vehid\\\":null,\\\"vg_grpid\\\":null,\\\"p_vehicle_id\\\":null,\\\"p_id\\\":null,\\\"p_placement_type\\\":null,\\\"p_site_id\\\":null,\\\"p_loc_name\\\":null,\\\"p_loc_lat\\\":null,\\\"p_loc_long\\\":null,\\\"p_target_time\\\":null,\\\"p_lr_id\\\":null,\\\"p_addtime\\\":null,\\\"p_allowed_halt_hours\\\":null,\\\"id\\\":33125,\\\"empname\\\":\\\"GJ12BX9030\\\",\\\"mobileno\\\":9602952912,\\\"vid\\\":124767,\\\"vlat\\\":23.010987,\\\"vlong\\\":71.456076,\\\"dttime\\\":\\\"2022-04-19T12:09:28.557\\\",\\\"speed\\\":46,\\\"angle\\\":264,\\\"vs_id\\\":108490842,\\\"vs_vehicle_id\\\":124767,\\\"vs_state_id\\\":3,\\\"vs_loc_name\\\":\\\"VAYOR\\\",\\\"vs_lat\\\":23.426307,\\\"vs_long\\\":68.712782,\\\"vs_start_time\\\":\\\"2022-03-19T19:53:14\\\",\\\"vs_site_id\\\":12600,\\\"vs_will_prevail\\\":false,\\\"h_id\\\":39061509,\\\"h_lat\\\":23.073853,\\\"h_long\\\":71.797347,\\\"h_site_id\\\":455,\\\"h_start_time\\\":\\\"2022-04-19T10:41:40.113\\\",\\\"h_end_time\\\":\\\"2022-04-19T11:16:28.457\\\",\\\"h_halt_type_id\\\":901,\\\"h_vehicle_id\\\":124767,\\\"sh_lat\\\":23.073886624489,\\\"sh_long\\\":71.797566400827,\\\"sh_type_id\\\":111,\\\"sh_name\\\":\\\" Hotel Anmol\\\",\\\"hl_vehicle_id\\\":124767,\\\"hl_id\\\":[108378694],\\\"hl_state_id\\\":[1],\\\"hl_lat\\\":[22.987858],\\\"hl_long\\\":[70.21592],\\\"hl_site_id\\\":[946],\\\"hl_start_time\\\":[\\\"2022-03-17T17:10:24\\\"],\\\"hl_end_time\\\":[\\\"2022-03-17T20:46:07\\\"],\\\"hl_loc_name\\\":[\\\"KANDLA\\\"],\\\"hl_lr_id\\\":[null],\\\"shl_lat\\\":[22.9934228185825],\\\"shl_long\\\":[70.2190466971713],\\\"shl_type_id\\\":[1],\\\"shl_name\\\":[\\\"Kandla\\\"],\\\"shl_loc_name\\\":[\\\"Kandla\\\"],\\\"hul_vehicle_id\\\":124767,\\\"hul_id\\\":[108490842],\\\"hul_state_id\\\":[3],\\\"hul_lat\\\":[23.426307],\\\"hul_long\\\":[68.712782],\\\"hul_site_id\\\":[12600],\\\"hul_start_time\\\":[\\\"2022-03-19T18:51:38\\\"],\\\"hul_end_time\\\":[\\\"2022-03-19T19:58:14\\\"],\\\"hul_loc_name\\\":[\\\"VAYOR\\\"],\\\"shul_lat\\\":[23.43237563321],\\\"shul_long\\\":[68.713143649387],\\\"shul_type_id\\\":[1],\\\"shul_name\\\":[\\\"Sewagram Cement Works\\\"],\\\"shul_loc_name\\\":[\\\"Sewagram\\\"],\\\"aduserid\\\":null,\\\"dloc_lat\\\":null,\\\"dloc_long\\\":null,\\\"location_fetch_time\\\":null,\\\"ssh_id\\\":null,\\\"ssh_subsite_id\\\":null,\\\"ssh_entry_time\\\":null,\\\"ssh_exit_time\\\":null,\\\"ssh_vehicle_id\\\":null,\\\"ssh_lat\\\":null,\\\"ssh_long\\\":null,\\\"ssh_type_id\\\":null,\\\"mobileno2\\\":null,\\\"vt_id\\\":1281580,\\\"vt_fo\\\":19379,\\\"lr_eway_expirydt\\\":[\\\"0001-01-01T00:00:00 BC\\\"],\\\"vt_addtime\\\":\\\"2022-03-18T11:12:42.691299\\\",\\\"vt_trip_complete_time\\\":\\\"2022-03-18T21:53:05\\\",\\\"vt_start_time\\\":\\\"2022-03-17T00:00:00\\\",\\\"sdc_status\\\":null,\\\"sdc_last_fetch_time\\\":null,\\\"sdc_last_error\\\":null,\\\"sdc_last_error_time\\\":null,\\\"first_plant_entry_time\\\":null,\\\"last_plant_entry_time\\\":null,\\\"now_plant_entry_time\\\":null,\\\"vt_processed_time\\\":\\\"2022-03-19T11:10:19\\\",\\\"hl_states_id\\\":[null],\\\"hl_districts_id\\\":[null],\\\"hul_states_id\\\":[null],\\\"hul_districts_id\\\":[null]}\"}";
        FoEscalationTickets foEscalationTickets1 = gson.fromJson(result, FoEscalationTickets.class);
    }

    public Boolean testAlert(Integer vehicleId, Integer alertId) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        MasterTableTemp1 master1 = masterTableTemp1Repository.findById(vehicleId).orElse(null);
        MasterTableTemp2 master2 = masterTableTemp2Repository.findById(vehicleId).orElse(null);

        List<Timestamp> localDateTimeList = masterTableTemp1Repository.getCreatedTime();

        Issues issues = new Issues();

        master1.setCreatedTime(localDateTimeList.get(0).toLocalDateTime());
        master2.setCreatedTime(localDateTimeList.get(1).toLocalDateTime());

        String alertClass = "com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert." + "SimpleAlert" + alertId;
        Class simpleAlertClass = StringToClass.getClassName(alertClass);

        SimpleAlert alertClassObj = (SimpleAlert) simpleAlertClass.getDeclaredConstructor().newInstance();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(alertClassObj);

        Boolean resultMaster1 = alertClassObj.isValidAlert(master1, issues);

        Boolean resultMaster2 = alertClassObj.isValidAlert(master2, issues);

        if(resultMaster1 && !resultMaster2){
            return true;
        }

        return false;



    }

    public Boolean testMediumProcessingAlert(Integer vehicleId, Integer alertId) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        MasterTableTemp1 master1 = masterTableTemp1Repository.findById(vehicleId).orElse(null);
        MasterTableTemp2 master2 = masterTableTemp2Repository.findById(vehicleId).orElse(null);

        List<Timestamp> localDateTimeList = masterTableTemp1Repository.getCreatedTime();

        Issues issues = new Issues();
        issues.setId(alertId);

        master1.setCreatedTime(localDateTimeList.get(0).toLocalDateTime());
        master2.setCreatedTime(localDateTimeList.get(1).toLocalDateTime());

        master1.setMasterTableTemp2(master2);
        String alertClass = "com.elogist.vehicle_master_and_alert_creation.models.alerts.mediumprocessingalert." + "MediumProcessingAlert" + alertId;
        Class simpleAlertClass = StringToClass.getClassName(alertClass);

        MediumProcessingAlert alertClassObj = (MediumProcessingAlert) simpleAlertClass.getDeclaredConstructor().newInstance();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(alertClassObj);

        Boolean resultMaster1 = alertClassObj.isValidAlert(master1, issues);

        return resultMaster1;



    }


}
