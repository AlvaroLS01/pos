package com.comerzzia.iskaypet.pos.persistence.auditorias.motivos;

public class MotivosAuditoriaKey {

    private String uidActividad;
    private String codEmp;
    private String tipoAuditoria;
    private Long codMotivo;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getCodEmp() {
        return codEmp;
    }

    public void setCodEmp(String codEmp) {
        this.codEmp = codEmp == null ? null : codEmp.trim();
    }

    public String getTipoAuditoria() {
        return tipoAuditoria;
    }

    public void setTipoAuditoria(String tipoAuditoria) {
        this.tipoAuditoria = tipoAuditoria == null ? null : tipoAuditoria.trim();
    }

    public Long getCodMotivo() {
        return codMotivo;
    }

    public void setCodMotivo(Long codMotivo) {
        this.codMotivo = codMotivo;
    }
}