package com.comerzzia.iskaypet.pos.services.ticket.regalo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ticket_regalo")
public class RegaloDto {

    @XmlElement(name = "uidActividad")
    private String uidActividad;

    @XmlElement(name = "uidTicketOrigen")
    private String uidTicketOrigen;

    @XmlElement(name = "fecha_envio")
    private Date fechaEnvio;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad;
    }

    public String getUidTicketOrigen() {
        return uidTicketOrigen;
    }

    public void setUidTicketOrigen(String uidTicket) {
        this.uidTicketOrigen = uidTicket;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

}
