package com.elogist.vehicle_master_and_alert_creation.models.Enums;

public enum CONSENTSTATUS {
    PENDING("PENDING"), // Consent Send But Not Accepted
    ALLOWED("ALLOWED"), // Consent Send And Accepted
    DENIED("DENIED"), // Consent Denied By User
    EXPIRED("EXPIRED"),// Consent Expired
    DISALLOWED("DISALLOWED"), // Consent Stopped by user
    ALLOWED_NODATA("ALLOW_NODATA"), // Consent Allowed but no data
    EXCEPTION("EXCEPTION"), // Error Fetching Consent
    NO_CONSENT("NO_CONSENT"); // Consent Not Taken
    public final String value;
    private CONSENTSTATUS(String value) {
        this.value = value;
    }
    public static CONSENTSTATUS valueOfvalue(String value) {
        if(value == null)
            return CONSENTSTATUS.NO_CONSENT;
        for (CONSENTSTATUS e : values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}