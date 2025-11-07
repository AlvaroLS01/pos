package com.comerzzia.api.loyalty.persistence.triggers.actions.executions;

import java.util.Date;

public class TriggerActionExecution extends TriggerActionExecutionKey {
    private String triggerExecUid;

    private String actionUid;

    private Boolean manualExecution;

    private Date startDate;

    private Date endDate;

    private Long userId;

    private Long statusId;

    private String statusText;

    public String getTriggerExecUid() {
        return triggerExecUid;
    }

    public void setTriggerExecUid(String triggerExecUid) {
        this.triggerExecUid = triggerExecUid == null ? null : triggerExecUid.trim();
    }

    public String getActionUid() {
        return actionUid;
    }

    public void setActionUid(String actionUid) {
        this.actionUid = actionUid == null ? null : actionUid.trim();
    }

    public Boolean getManualExecution() {
        return manualExecution;
    }

    public void setManualExecution(Boolean manualExecution) {
        this.manualExecution = manualExecution;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText == null ? null : statusText.trim();
    }
}