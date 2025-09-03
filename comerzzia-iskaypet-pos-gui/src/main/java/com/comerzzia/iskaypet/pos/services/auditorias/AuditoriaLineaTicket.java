package com.comerzzia.iskaypet.pos.services.auditorias;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "auditoria")
public class AuditoriaLineaTicket {

    private String uidAuditoria;
    private String tipo;
    private String codigo;

    public AuditoriaLineaTicket() {
    }

    public AuditoriaLineaTicket(String uidAuditoria, String tipo, String codigo) {
        this.uidAuditoria = uidAuditoria;
        this.tipo = tipo;
        this.codigo = codigo;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
