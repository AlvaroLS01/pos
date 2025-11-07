package com.comerzzia.api.loyalty.persistence.triggers.executions;

import javax.xml.bind.annotation.XmlTransient;

public class TriggerExecutionKey {
    @XmlTransient
    private String uidActividad;

    private String triggerExecUid;

    public TriggerExecutionKey() {
    }
    
    public TriggerExecutionKey(String uidActividad, String triggerExecUid) {
      this.uidActividad = uidActividad;
      this.triggerExecUid = triggerExecUid;
   }

   public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getTriggerExecUid() {
        return triggerExecUid;
    }

    public void setTriggerExecUid(String triggerExecUid) {
        this.triggerExecUid = triggerExecUid == null ? null : triggerExecUid.trim();
    }
}