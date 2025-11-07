package com.comerzzia.api.loyalty.persistence.triggers.actions;

import javax.xml.bind.annotation.XmlTransient;

public class TriggerActionKey {
    @XmlTransient
    private String uidActividad;

    private String actionUid;

    public TriggerActionKey() {      
    }
    
    public TriggerActionKey(String uidActividad, String actionUid) {
       this.uidActividad = uidActividad;
       this.actionUid = actionUid;
    }
    
    public String getUidActividad() {
        return uidActividad;
    }

   public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getActionUid() {
        return actionUid;
    }

    public void setActionUid(String actionUid) {
        this.actionUid = actionUid == null ? null : actionUid.trim();
    }
}