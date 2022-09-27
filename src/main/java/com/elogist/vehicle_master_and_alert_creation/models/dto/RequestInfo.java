package com.elogist.vehicle_master_and_alert_creation.models.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@AllArgsConstructor
@NoArgsConstructor
public class RequestInfo {
    Integer adUser;
    Integer foId;
    Integer entryMode;
    Integer foAdminId;

    public Integer getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(Integer entryMode) {
        this.entryMode = entryMode;
    }

    public Integer getFoAdminId() {
        return foAdminId;
    }

    public void setFoAdminId(Integer foAdminId) {
        this.foAdminId = foAdminId;
    }

    public Integer getAdUser() {
        return adUser;
    }

    public void setAdUser(Integer adUser) {
        this.adUser = adUser;
    }

    public Integer getFoId() {
        return foId;
    }

    public void setFoId(Integer foId) {
        this.foId = foId;
    }
}
