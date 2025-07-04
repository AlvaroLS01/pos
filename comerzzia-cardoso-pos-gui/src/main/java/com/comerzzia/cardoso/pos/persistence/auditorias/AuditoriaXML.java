package com.comerzzia.cardoso.pos.persistence.auditorias;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@XmlRootElement(name = "auditoria")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditoriaXML {

	@XmlElement(name = "uid_ticket")
	private String uidTicket;
	@XmlElement(name = "tipo_operacion")
	private String tipoOperacion;
	@XmlElement(name = "id_tipo_documento")
	private Integer idTipoDocumento;
	@XmlElement(name = "codalm")
	private String codalm;
	@XmlElement(name = "desalm")
	private String desalm;
	@XmlElement(name = "codcaja")
	private String codcaja;
	@XmlElement(name = "usuario")
	private Integer usuario;
	@XmlElement(name = "desusuario")
	private String desusuario;
	@XmlElement(name = "fecha")
	private String fecha;
	@XmlElement(name = "payment_id")
	private Long paymentId;
	@XmlElement(name = "payment_id_origen")
	private Long paymentIdOrigen;
	@XmlElement(name = "pos_id")
	private Integer posId;
	@XmlElement(name = "comercio")
	private Integer comercio;
	@XmlElement(name = "importe")
	private String importe;
	@XmlElement(name = "observaciones")
	private String observaciones;

	public Long getPaymentIdOrigen() {
		return paymentIdOrigen;
	}

	public void setPaymentIdOrigen(Long paymentIdOrigen) {
		this.paymentIdOrigen = paymentIdOrigen;
	}

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public int getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Integer idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getCodalm() {
		return codalm;
	}

	public void setCodalm(String codalm) {
		this.codalm = codalm;
	}

	public String getDesalm() {
		return desalm;
	}

	public void setDesalm(String desalm) {
		this.desalm = desalm;
	}

	public String getCodcaja() {
		return codcaja;
	}

	public void setCodcaja(String codcaja) {
		this.codcaja = codcaja;
	}

	public int getUsuario() {
		return usuario;
	}

	public void setUsuario(Integer usuario) {
		this.usuario = usuario;
	}

	public String getDesusuario() {
		return desusuario;
	}

	public void setDesusuario(String desusuario) {
		this.desusuario = desusuario;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public int getPosId() {
		return posId;
	}

	public void setPosId(Integer posId) {
		this.posId = posId;
	}

	public int getComercio() {
		return comercio;
	}

	public void setComercio(Integer comercio) {
		this.comercio = comercio;
	}

	public String getImporte() {
		return importe;
	}

	public void setImporte(String importe) {
		this.importe = importe;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

}
