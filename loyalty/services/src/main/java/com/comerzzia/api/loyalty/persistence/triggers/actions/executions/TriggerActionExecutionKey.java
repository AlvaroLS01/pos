package com.comerzzia.api.loyalty.persistence.triggers.actions.executions;

import javax.xml.bind.annotation.XmlTransient;

public class TriggerActionExecutionKey {
    @XmlTransient
    private String uidActividad;

    private String accExecutionUid;

    public TriggerActionExecutionKey() {       
    }

    public TriggerActionExecutionKey(String uidActividad, String accExecutionUid) {
      this.uidActividad = uidActividad;
      this.accExecutionUid = accExecutionUid;
   }

   public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getAccExecutionUid() {
        return accExecutionUid;
    }

    public void setAccExecutionUid(String accExecutionUid) {
        this.accExecutionUid = accExecutionUid == null ? null : accExecutionUid.trim();
    }
}