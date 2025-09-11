package com.comerzzia.iskaypet.pos.persistence.proformas.lineas.auditorias;


public class AuditoriaLineaProformaBean extends AuditoriaLineaProformaBeanKey {
    private String codemp;

    private String tipoAuditoria;

    private Long codMotivo;

    private String observaciones;

    public String getCodemp() {
        return codemp;
    }

    public void setCodemp(String codemp) {
        this.codemp = codemp == null ? null : codemp.trim();
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones == null ? null : observaciones.trim();
    }
}