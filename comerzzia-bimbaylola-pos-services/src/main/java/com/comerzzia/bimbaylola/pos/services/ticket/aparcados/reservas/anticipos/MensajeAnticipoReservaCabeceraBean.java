package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.anticipos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.DatosFidelizadoReservaTicket;
import com.comerzzia.pos.persistence.clientes.ClienteBean;

@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "uidActividad", "codTicket", "uidTicket", "codEmp", "codAlmacen", "codCaja", "fecha", "turno", "moneda", "codConceptoMov", "datosFidelizado", "cliente"})
public class MensajeAnticipoReservaCabeceraBean {

	@XmlElement(name = "uidActividad", required = true)
	private String uidActividad;

	@XmlElement(name = "codTicket", required = true)
	private String codTicket;

	@XmlElement(name = "uidTicket", required = true)
	private String uidTicket;

	@XmlElement(name = "codEmp", required = true)
	private String codEmp;

	@XmlElement(name = "codAlmacen", required = true)
	private String codAlmacen;

	@XmlElement(name = "codCaja", required = true)
	private String codCaja;

	@XmlElement(name = "fecha", required = true)
	private String fecha;

	@XmlElement(name = "turno", required = true)
	private Integer turno;

	@XmlElement(name = "moneda", required = true)
	private String moneda;

	@XmlElement(name = "codConceptoMov", required = true)
	private String codConceptoMov;
	
	@XmlElement(name="datos_fidelizacion")
    protected DatosFidelizadoReservaTicket datosFidelizado;
	
	@XmlElement(name = "cliente", required = true)
	protected ClienteBean cliente;

	public MensajeAnticipoReservaCabeceraBean() {
	}

	public MensajeAnticipoReservaCabeceraBean(String uidActividad, String codTicket, String uidTicket, String codEmp, String codAlmacen, String codCaja, String fecha, Integer turno, String moneda, String codConceptoMov) {
		super();
		this.uidActividad = uidActividad;
		this.codTicket = codTicket;
		this.uidTicket = uidTicket;
		this.codEmp = codEmp;
		this.codAlmacen = codAlmacen;
		this.codCaja = codCaja;
		this.fecha = fecha;
		this.turno = turno;
		this.moneda = moneda;
		this.codConceptoMov = codConceptoMov;
	}

	public String getUidActividad() {
		return uidActividad;
	}

	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad;
	}

	public String getCodTicket() {
		return codTicket;
	}

	public void setCodTicket(String codTicket) {
		this.codTicket = codTicket;
	}

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	public String getCodEmp() {
		return codEmp;
	}

	public void setCodEmp(String codEmp) {
		this.codEmp = codEmp;
	}

	public String getCodAlmacen() {
		return codAlmacen;
	}

	public void setCodAlmacen(String codAlmacen) {
		this.codAlmacen = codAlmacen;
	}

	public String getCodCaja() {
		return codCaja;
	}

	public void setCodCaja(String codCaja) {
		this.codCaja = codCaja;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public Integer getTurno() {
		return turno;
	}

	public void setTurno(Integer turno) {
		this.turno = turno;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	
	public String getCodConceptoMov() {
		return codConceptoMov;
	}
	
	public void setCodConceptoMov(String codConceptoMov) {
		this.codConceptoMov = codConceptoMov;
	}

	public DatosFidelizadoReservaTicket getDatosFidelizado() {
		return datosFidelizado;
	}

	public void setDatosFidelizado(DatosFidelizadoReservaTicket datosFidelizado) {
		this.datosFidelizado = datosFidelizado;
	}

	public ClienteBean getCliente() {
		return cliente;
	}

	public void setCliente(ClienteBean cliente) {
		this.cliente = cliente;
	}

	@Override
	public String toString() {
		return "MensajeAnticipoReservaCabeceraBean [uidActividad=" + uidActividad + ", uidTicket=" + uidTicket + ", codEmp=" + codEmp + ", codAlmacen=" + codAlmacen + ", codCaja=" + codCaja
		        + ", fecha=" + fecha + ", turno=" + turno + ", moneda=" + moneda + "]";
	}

}
