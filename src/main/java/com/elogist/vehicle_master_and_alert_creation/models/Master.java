package com.elogist.vehicle_master_and_alert_creation.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swift.common.pojos.dto.MasterDynamicRoute;
import swift.common.pojos.dto.MasterRoute;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Master {

    public abstract  Integer getVId();

    public abstract  String getRegNo();

    public abstract  Integer getFoid();

    public abstract  Long getMobileNo();

    public abstract  LocalDateTime getTtTime();

    public abstract  Integer getHStatus();

    public abstract  Integer getHhaltTypeId();

    public abstract  Integer getHsiteId();

    public abstract  Integer getHSiteType();

    public abstract  LocalDateTime getHStartTime();

    public abstract Integer getVtFo();

    public abstract List<Double> getPLocLat();

    public abstract List<Double> getPLocLong();

    public abstract Double getTLat();

    public abstract Double getTLong();

    public abstract Integer getPrimStatus();

    public abstract Integer getSecStatus();

    public abstract LocalDateTime getVsStartTime();

    public abstract Double getVsLat();

    public abstract Double getVsLong();

    public abstract LocalDateTime getVtAddtime();

    public abstract LocalDateTime getVtTripCompleteTime();

    public abstract Integer getHId();

    public abstract LocalDateTime getVtStartTime();

    public abstract List<Timestamp> getHlStartTime();

    public abstract List<Timestamp> getHlEndTime();

    public abstract  String getShowPrimStatus();

    public abstract Integer getSpeed();

    public abstract List<Timestamp> getPTargetTime();

    public abstract List<Double> getHlLat();

    public abstract List<Double> getHlLong();

    public abstract String getSdcLastError();

    public abstract LocalDateTime getSdcLastErrorTime();

    public abstract String getSdcStatus();

    public abstract LocalDateTime getSdcLastFetchTime();

    public abstract LocalDateTime getHEndTime();

    public abstract Double getHLat();

    public abstract Double getHLong();

    public abstract List<Integer> getVgGrpId();

    public abstract List<Integer> getPSiteId();

    public abstract List<Integer> getHlSiteId();

    public abstract Integer getAdUserId();

    public abstract List<Integer> getHlId();

    public abstract Integer getVtId();

    public abstract LocalDateTime getVtProcessedTime();

    public abstract Integer getLocFlag();

    public abstract Integer getVFo();

    public abstract List<Timestamp> getLrEwayExpirydt();

    public abstract LocalDateTime getCreatedTime();

    public abstract MasterRoute getMasterRoute();

    public abstract MasterDynamicRoute getMasterDynamicRoute();

}
