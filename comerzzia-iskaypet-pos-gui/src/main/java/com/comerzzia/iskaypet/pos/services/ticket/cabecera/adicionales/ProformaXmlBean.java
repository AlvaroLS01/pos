package com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * CZZ-1498 - TOGO PROFORMAS
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ProformaXmlBean {

    @XmlElement(name = "id_proforma")
    private String idProforma;

    @XmlElement(name = "automatica")
    private boolean automatica;

    public ProformaXmlBean() {

    }

    public ProformaXmlBean(String idProforma, boolean automatica) {
        this.idProforma = idProforma;
        this.automatica = automatica;
    }

    public String getIdProforma() {
        return idProforma;
    }

    public void setIdProforma(String idProforma) {
        this.idProforma = idProforma;
    }

    public boolean isAutomatica() {
        return automatica;
    }

    public void setAutomatica(boolean automatica) {
        this.automatica = automatica;
    }


}
