package com.comerzzia.pos.services.ticket.lineas;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;

@XmlType(propOrder={"uidDocumentoOrigen", "idLineaDocumentoOrigen", "promociones"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentoOrigen {

	protected String uidDocumentoOrigen;
	protected Integer idLineaDocumentoOrigen;
	@XmlElementWrapper(name = "promociones")
	@XmlElement(name = "promocion")
	protected List<PromocionLineaTicket> promociones;
	
	public DocumentoOrigen() {
	}

	public DocumentoOrigen(String uidDocumentoOrigen, Integer idLineaDocumentoOrigen) {
		this.uidDocumentoOrigen = uidDocumentoOrigen;
		this.idLineaDocumentoOrigen = idLineaDocumentoOrigen;
	}

	public String getUidDocumentoOrigen() {
		return uidDocumentoOrigen;
	}

	public void setUidDocumentoOrigen(String uidDocumentoOrigen) {
		this.uidDocumentoOrigen = uidDocumentoOrigen;
	}

	public Integer getIdLineaDocumentoOrigen() {
		return idLineaDocumentoOrigen;
	}

	public void setIdLineaDocumentoOrigen(Integer idLineaDocumentoOrigen) {
		this.idLineaDocumentoOrigen = idLineaDocumentoOrigen;
	}

	public void setPromociones(List<PromocionLineaTicket> promociones) {
		this.promociones = promociones;
	}

	public List<PromocionLineaTicket> getPromociones() {
		return promociones;
	}
	
}
