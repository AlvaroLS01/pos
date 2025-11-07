package com.comerzzia.api.loyalty.persistence.triggers.executions.data;

import javax.xml.bind.annotation.XmlTransient;

public class TriggerExecutionDataKey {
    @XmlTransient
    private String uidActividad;

    @XmlTransient
    private String triggerExecUid;

    private Long idFidelizado;

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

    public Long getIdFidelizado() {
        return idFidelizado;
    }

    public void setIdFidelizado(Long idFidelizado) {
        this.idFidelizado = idFidelizado;
    }
}