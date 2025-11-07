package com.comerzzia.api.loyalty.persistence.triggers.actions;

import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

public class TriggerAction extends TriggerActionKey {
    private Short actionType;

    private Boolean manualExecution;

    private Date nextExecutionDate;

    private String timeInterval;

    private String cronInterval;

    private String lastActionExecUid;
    
    @XmlTransient
    private String triggerUid;

    private byte[] actionData;

    public Short getActionType() {
        return actionType;
    }

    public void setActionType(Short actionType) {
        this.actionType = actionType;
    }

    public Boolean getManualExecution() {
        return manualExecution;
    }

    public void setManualExecution(Boolean manualExecution) {
        this.manualExecution = manualExecution;
    }

    public Date getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(Date nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval == null ? null : timeInterval.trim();
    }

    public String getCronInterval() {
        return cronInterval;
    }

    public void setCronInterval(String cronInterval) {
        this.cronInterval = cronInterval == null ? null : cronInterval.trim();
    }

    public String getLastActionExecUid() {
        return lastActionExecUid;
    }

    public void setLastActionExecUid(String lastActionExecUid) {
        this.lastActionExecUid = lastActionExecUid == null ? null : lastActionExecUid.trim();
    }

    public String getTriggerUid() {
      return triggerUid;
   }

   public void setTriggerUid(String triggerUid) {
      this.triggerUid = triggerUid;
   }

   public byte[] getActionData() {
        return actionData;
    }

    public void setActionData(byte[] actionData) {
        this.actionData = actionData;
    }
}