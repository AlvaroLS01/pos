package com.comerzzia.iskaypet.pos.services.proformas.rest.classes.lineas.auditorias;

public class AuditoriaLineaProformaDTO {

    private String uidAuditoria;
    private String tipo;
    private Long codigo;
    private String motivo;
    private String observaciones;

    public AuditoriaLineaProformaDTO() {
    }

    public AuditoriaLineaProformaDTO(String uidAuditoria, String tipo, Long codigo, String motivo, String observaciones) {
        this.uidAuditoria = uidAuditoria;
        this.tipo = tipo;
        this.codigo = codigo;
        this.motivo = motivo;
        this.observaciones = observaciones;
    }

    public String getUidAuditoria() {
        return uidAuditoria;
    }

    public void setUidAuditoria(String uidAuditoria) {
        this.uidAuditoria = uidAuditoria;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
