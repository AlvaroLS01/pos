package com.comerzzia.api.loyalty.persistence.triggers;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

public class Trigger extends TriggerKey {
    private String triggerCode;

    private String triggerDescription;

    private Long lastModUserId;

    private Date lastModDate;

    private String actionUid;

    private String lastTriggerExecUid;

    @Schema(type = "BYTE")
    private byte[] triggerData;

    public String getTriggerCode() {
        return triggerCode;
    }

    public void setTriggerCode(String triggerCode) {
        this.triggerCode = triggerCode == null ? null : triggerCode.trim();
    }

    public String getTriggerDescription() {
        return triggerDescription;
    }

    public void setTriggerDescription(String triggerDescription) {
        this.triggerDescription = triggerDescription == null ? null : triggerDescription.trim();
    }

    public Long getLastModUserId() {
        return lastModUserId;
    }

    public void setLastModUserId(Long lastModUserId) {
        this.lastModUserId = lastModUserId;
    }

    public Date getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(Date lastModDate) {
        this.lastModDate = lastModDate;
    }

    public String getActionUid() {
        return actionUid;
    }

    public void setActionUid(String actionUid) {
        this.actionUid = actionUid == null ? null : actionUid.trim();
    }

    public String getLastTriggerExecUid() {
        return lastTriggerExecUid;
    }

    public void setLastTriggerExecUid(String lastTriggerExecUid) {
        this.lastTriggerExecUid = lastTriggerExecUid == null ? null : lastTriggerExecUid.trim();
    }

    public byte[] getTriggerData() {
        return triggerData;
    }

    public void setTriggerData(byte[] triggerData) {
        this.triggerData = triggerData;
    }
}