package com.comerzzia.api.loyalty.persistence.triggers;

import javax.xml.bind.annotation.XmlTransient;

public class TriggerKey {
    @XmlTransient
    private String uidActividad;

    private String triggerUid;

    public TriggerKey() {
    }
    
    public TriggerKey(String uidActividad, String triggerUid) {
       this.uidActividad = uidActividad;
       this.triggerUid = triggerUid;
    }
    
    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getTriggerUid() {
        return triggerUid;
    }

    public void setTriggerUid(String triggerUid) {
        this.triggerUid = triggerUid == null ? null : triggerUid.trim();
    }

}