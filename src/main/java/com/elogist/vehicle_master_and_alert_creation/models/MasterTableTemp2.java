package com.elogist.vehicle_master_and_alert_creation.models;

import com.google.gson.annotations.SerializedName;
import com.vladmihalcea.hibernate.type.array.DoubleArrayType;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.array.LongArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import swift.common.pojos.dto.MasterDynamicRoute;
import swift.common.pojos.dto.MasterDynamicRouteTemp2;
import swift.common.pojos.dto.MasterRoute;
import swift.common.pojos.dto.MasterRouteTemp2DTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "master_vehicle_temp2")
@TypeDefs({
        @TypeDef(
                name = "long-array",
                typeClass = LongArrayType.class
        ),
        @TypeDef(
                name = "double-array",
                typeClass = DoubleArrayType.class
        ),
        @TypeDef(
                name = "list-array",
                typeClass = ListArrayType.class

        ),
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        )
})
public class MasterTableTemp2 extends Master {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "v_id")
    @SerializedName("v_id")
    private Integer vId;

    //registration no
    @Column(name = "v_regno")
    @SerializedName("v_regno")
    private String regNo;

    //vehicle owner
    @Column(name = "v_foid")
    @SerializedName("v_foid")
    private Integer foid;

    //primary driver number
    @Column(name = "mobileno")
    @SerializedName("mobileno")
    private Long mobileNo;

    //last location time
    @Column(name = "ttime")
    @SerializedName("ttime")
    private LocalDateTime ttTime;

    //1 - idle ,0 - running , -1 no halts yet
    //last halt status
    @Column(name = "h_status")
    @SerializedName("h_status")
    private Integer hStatus;

    //type master type_id 4 describes this column
    //last halt type id
    @Column(name = "h_halt_type_id")
    @SerializedName("h_halt_type_id")
    private Integer hhaltTypeId;

    //last halt site id (always global (foid null in sites))
    @Column(name = "h_site_id")
    @SerializedName("h_site_id")
    private Integer hsiteId;

    //last halt site type
    @Column(name = "sh_type_id")
    @SerializedName("sh_type_id")
    private Integer hSiteType;

    //last halt start time
    @Column(name = "h_start_time")
    @SerializedName("h_start_time")
    private LocalDateTime hStartTime;

    //last vehicle trip foid(might not be equal to v_foid)
    @Column(name = "vt_fo")
    @SerializedName("vt_fo")
    private Integer vtFo;

    //placement or future states lats-array
    @Type(type = "double-array")
    @Column(name = "p_loc_lat", columnDefinition = "double[]")
    @SerializedName("p_loc_lat")
    private Double[] pLocLatDoubleArray;

    //placement or future states longs-array
    @Type(type = "double-array")
    @Column(name = "p_loc_long", columnDefinition = "double[]")
    @SerializedName("p_loc_long")
    private Double[] pLocLongDoubleArray;

    // last location lat
    @Column(name = "tlat")
    @SerializedName("tlat")
    private Double tLat;

    // last location long
    @Column(name = "tlong")
    @SerializedName("tlong")
    private Double tLong;

    // primary vehicle status code (like loading unloading issues etc refer ry_create_vehicle_m1_and_m2)
    @Column(name ="prim_status")
    @SerializedName("prim_status")
    private Integer primStatus;

    @Column(name ="sec_status")
    @SerializedName("sec_status")
    private Integer secStatus;

    // last non future state start_time
    @Column(name = "vs_start_time")
    @SerializedName("vs_start_time")
    private LocalDateTime vsStartTime;

    // last non future state Latitute
    @Column(name = "vs_lat")
    @SerializedName("vs_lat")
    private Double vsLat;

    // last non future state Longitude
    @Column(name = "vs_long")
    @SerializedName("vs_long")
    private Double vsLong;

    //last trip complete time( all inner states are non future and time will be of last state in the trip)
    @Column(name = "vt_trip_complete_time")
    @SerializedName("vt_trip_complete_time")
    private LocalDateTime vtTripCompleteTime;

    //last trip addTime
    @Column(name = "vt_addtime")
    @SerializedName("vt_addtime")
    private LocalDateTime vtAddtime;

    //last halt id
    @Column(name = "h_id")
    @SerializedName("h_id")
    private Integer hId;

    //last trip invoice time
    @Column(name = "vt_start_time")
    @SerializedName("vt_start_time")
    private LocalDateTime vtStartTime;

    //last vehicle trip loading halts start_time-array (for latest pick first)
    @Column(name = "hl_start_time", columnDefinition = "timestamp[]")
    @Type(type = "list-array")
    @SerializedName("hl_start_time")
    private List<Timestamp> hlStartTime;

    //last vehicle trip loading halts end_time-array (for latest pick first)
    @Column(name = "hl_end_time", columnDefinition = "timestamp[]")
    @Type(type = "list-array")
    @SerializedName("hl_end_time")
    private List<Timestamp> hlEndTime;

    //prim status text (like loading unloading issues etc refer ry_create_vehicle_m1_and_m2)
    @Column(name = "showprim_status")
    @SerializedName("showprim_status")
    private String showPrimStatus;

    //last location speed instantaneous
    @Column(name = "speed")
    @SerializedName("speed")
    private Integer speed;

    //placement or future states target-array
    @Column(name = "p_target_time", columnDefinition = "timestamp[]")
    @Type(type = "list-array")
    @SerializedName("p_target_time")
    private List<Timestamp> pTargetTime;

    //last vehicle trip loading halts lats-array (for latest pick first)
    @Type(type = "double-array")
    @Column(name = "hl_lat", columnDefinition = "double[]")
    @SerializedName("hl_lat")
    private BigDecimal[] hlLatDoubleArray;

    //last vehicle trip loading halts longs-array (for latest pick first)
    @Type(type = "double-array")
    @Column(name = "hl_long", columnDefinition = "double[]")
    @SerializedName("hl_long")
    private BigDecimal[] hlLongDoubleArray;

    //Sim Data Consent error for last primary driver
    @Column(name = "sdc_last_error")
    @SerializedName("sdc_last_error")
    private String sdcLastError;

    //Sim Data Consent error time for last primary driver
    @Column(name = "sdc_last_error_time")
    @SerializedName("sdc_last_error_time")
    private LocalDateTime sdcLastErrorTime;

    //Sim Data Consent status for last primary driver
    @Column(name = "sdc_status")
    @SerializedName("sdc_status")
    private String sdcStatus;

    //Sim Data Consent status for last primary driver
    @Column(name = "sdc_last_fetch_time")
    @SerializedName("sdc_last_fetch_time")
    private LocalDateTime sdcLastFetchTime;

    //last halt end time
    @Column(name = "h_end_time")
    @SerializedName("h_end_time")
    private LocalDateTime hEndTime;

    //last halt lat
    @Column(name = "h_lat")
    @SerializedName("h_lat")
    private Double hLat;

    //last halt long
    @Column(name = "h_long")
    @SerializedName("h_long")
    private Double hLong;

    //vehicle groups this vehicle is present in
    @Column(name = "vg_grpid")
    @Type(type = "list-array")
    @SerializedName("vg_grpid")
    private List<Integer> vgGrpId;

    //last vehicle trip loading halts sites-array (for latest pick first)
    @Column(name = "hl_site_id")
    @Type(type = "list-array")
    @SerializedName("hl_site_id")
    private List<Integer> hlSiteId;

    //placement or future states site_id-array
    @Column(name = "p_site_id")
    @Type(type = "list-array")
    @SerializedName("p_site_id")
    private List<Integer> pSiteId;

    //driver aduserid
    @Column(name = "aduserid")
    @SerializedName("aduserid")
    private Integer adUserId;

    //last vehicle trip loading halts id-array (for latest pick first)
    @Column(name = "hl_id", columnDefinition = "int[]")
    @Type(type = "list-array")
    @SerializedName("hl_id")
    private List<Integer> hlId;

    //last trip id
    @Column(name = "vt_id")
    @SerializedName("vt_id")
    private Integer vtId;

    //last trip process time(trip post process)
    @Column(name = "vt_processed_time")
    @SerializedName("vt_processed_time")
    private LocalDateTime vtProcessedTime;

    //abs(locflag) = 1->GPS, 3->SIM, 0->no data received yet, < 0 no ping received 1 hour or later
    @Column(name = "loc_flag")
    @SerializedName("loc_flag")
    private Integer locFlag;

    //last ewybill expirydate
    @Column(name = "lr_eway_expirydt", columnDefinition = "timestamp[]")
    @Type(type = "list-array")
    @SerializedName("lr_eway_expirydt")
    private List<Timestamp> lrEwayExpirydt;

    @Column(name = "vt_stamp_time")
    @SerializedName("vt_stamp_time")
    private LocalDateTime vtStampTime;

    @Transient
    MasterRouteTemp2DTO masterRouteTemp2DTO;

    @Transient
    MasterDynamicRouteTemp2 masterDynamicDynamicRouteTemp2;
//    @Column(name = "c_ul_site", columnDefinition = "int[]")
//    @Type(type = "list-array")
//    @SerializedName("c_ul_site")
//    private List<Integer> currentUlSite;


    @Transient
    private LocalDateTime createdTime;

    public MasterRoute getMasterRoute(){

        return this.masterRouteTemp2DTO;

    }

    public MasterDynamicRoute getMasterDynamicRoute(){

        return this.masterDynamicDynamicRouteTemp2;

    }


//    public List<Long> getHlId(){
//        List<Long> res = new ArrayList<>();
//        for(Long res1 : this.hlIdLongArray){
//            res.add(res1.longValue());
//        }
//        return res;
//    }

    public List<Double> getPLocLat(){
        if(this.getPLocLatDoubleArray() == null){
            return null;
        }
        List<Double> res = new ArrayList<>();
        for(Double res1 : this.pLocLatDoubleArray){
            res.add(res1.doubleValue());
        }
        return res;

    }

    public List<Double> getPLocLong(){
        if(this.pLocLongDoubleArray == null){
            return null;
        }
        List<Double> res = new ArrayList<>();
        for(Double res1 : this.pLocLongDoubleArray){
            res.add(res1.doubleValue());
        }
        return res;

    }

    public List<Double> getHlLat(){
        if(this.hlLatDoubleArray == null){
            return null;
        }
        List<Double> res = new ArrayList<>();
        for(BigDecimal res1 : this.hlLatDoubleArray){
            res.add(res1.doubleValue());
        }
        return res;

    }

    public List<Double> getHlLong(){
        if(this.hlLongDoubleArray == null){
            return null;
        }
        List<Double> res = new ArrayList<>();
        for(BigDecimal res1 : this.hlLongDoubleArray){
            res.add(res1.doubleValue());
        }
        return res;

    }

    public Integer getVFo(){
        return this.foid;
    }
}
