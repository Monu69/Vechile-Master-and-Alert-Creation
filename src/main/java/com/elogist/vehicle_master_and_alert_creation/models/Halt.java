package com.elogist.vehicle_master_and_alert_creation.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "halts")
@Data
@NoArgsConstructor
@ToString
public class Halt {

    @EmbeddedId
    private HaltKey key;

    @Column(name = "id",nullable = false)
    @Generated(GenerationTime.INSERT)
    private Long id;

    @Column(name = "site_id")
    private Long siteId;

    @Column(name = "foid")
    private Long foId;

    @Column(name = "halt_type_id")
    private Integer haltTypeId;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "addtime")
    private LocalDateTime addTime;

    @Column(name = "cleartime")
    private LocalDateTime clearTime;

    @Column(name = "lat")
    private BigDecimal lat;

    @Column(name = "long")
    private BigDecimal lng;

    @Column(name = "entrymode")
    private Integer entryMode;

    @Column(name = "aduserid")
    private Integer addUserId;

    @Column(name = "road_dist")
    private Integer roadDist;

    @Column(name = "chkmiss")
    private Boolean checkMiss;

    @Column(name = "mod_type")
    private Integer modType;


    public Halt(HaltKey key, Long siteId, Integer haltTypeId, LocalDateTime endTime, LocalDateTime addtime, LocalDateTime clearTime,
                BigDecimal lat, BigDecimal lng, Integer entryMode, Integer addUserId, Integer roadDist, Boolean checkMiss ) {
        this.key = key;
        this.siteId = siteId;
        this.haltTypeId = haltTypeId;
        this.endTime = endTime;
        this.addTime = LocalDateTime.now();
        this.clearTime = clearTime;
        this.lat = lat;
        this.lng = lng;
        this.entryMode = entryMode;
        this.addUserId = addUserId;
        this.roadDist = roadDist;
        this.checkMiss = false;
    }

    public void createInstance(HaltKey haltKey, Long siteId, Integer haltTypeId, LocalDateTime endTime, LocalDateTime addTime, LocalDateTime clearTime, BigDecimal lat, BigDecimal lng, Integer entryMode, Integer addUserId, Integer roadDist, Boolean checkMiss){
        this.setKey(haltKey);
        this.setSiteId(siteId);
        this.setHaltTypeId(haltTypeId);
        this.setEndTime(endTime);
        this.setAddTime(LocalDateTime.now());
        this.setClearTime(clearTime);
        this.setLat(lat);
        this.setLng(lng);
        this.setEntryMode(entryMode);
        this.setAddUserId(addUserId);
        this.setRoadDist(roadDist);
        this.setCheckMiss(checkMiss);
    }

    public Halt(Master master) {
        this.key = new HaltKey(master.getVId().longValue(),master.getHStartTime());
//        this.key.setVehicleId(vehId);
//        this.key.setStartTime(startTime);
        this.siteId = master.getHsiteId()==null ? null : master.getHsiteId().longValue();
        this.haltTypeId = master.getHhaltTypeId()==null?null:master.getHhaltTypeId();
        this.endTime = master.getHEndTime()==null?null:master.getHEndTime();
//        this.addTime = LocalDateTime.now();
//        this.clearTime = clearTime;
        this.lat = master.getHLat()==null?null:new BigDecimal(master.getHLat());
        this.lng = master.getHLong()==null?null:new BigDecimal(master.getHLong());
//        this.entryMode = entryMode;
//        this.addUserId = addUserId;
//        this.roadDist = roadDist;
//        this.checkMiss = false;
    }

//    public Point getPoint(){
//        return new Point(this.getLat(),this.getLng());
//    }

}
