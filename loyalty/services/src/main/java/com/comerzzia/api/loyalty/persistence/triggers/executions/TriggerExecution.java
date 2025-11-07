package com.comerzzia.api.loyalty.persistence.triggers.executions;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

public class TriggerExecution extends TriggerExecutionKey {
    private String triggerUid;

    private Long executionId;

    private Date startDate;

    private Date endDate;

    private Long userId;

    private Long statusId;

    private String statusText;

    private Long affectedRecords;

    private String lastAccExecutionUid;
    
    @Schema(type = "BYTE")
    private byte[] triggerData;    

    public String getTriggerUid() {
        return triggerUid;
    }

    public void setTriggerUid(String triggerUid) {
        this.triggerUid = triggerUid == null ? null : triggerUid.trim();
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
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

    public Long getAffectedRecords() {
        return affectedRecords;
    }

    public void setAffectedRecords(Long affectedRecords) {
        this.affectedRecords = affectedRecords;
    }

    public String getLastAccExecutionUid() {
        return lastAccExecutionUid;
    }

    public void setLastAccExecutionUid(String lastAccExecutionUid) {
        this.lastAccExecutionUid = lastAccExecutionUid == null ? null : lastAccExecutionUid.trim();
    }

   public byte[] getTriggerData() {
      return triggerData;
   }

   public void setTriggerData(byte[] triggerData) {
      this.triggerData = triggerData;
   }
}