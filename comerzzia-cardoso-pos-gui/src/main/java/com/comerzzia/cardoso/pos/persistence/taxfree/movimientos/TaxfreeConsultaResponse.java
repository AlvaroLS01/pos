package com.comerzzia.cardoso.pos.persistence.taxfree.movimientos;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TaxfreeConsultaResponse")
public class TaxfreeConsultaResponse {
	
	private String uidActividad;
	private String codTicket;
	private String barcode;
	private String tipoMovimiento;
	private Date fechaMovimiento;
	private String cajaMovimiento;
	
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
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getTipoMovimiento() {
		return tipoMovimiento;
	}
	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}
	public Date getFechaMovimiento() {
		return fechaMovimiento;
	}
	public void setFechaMovimiento(Date fechaMovimiento) {
		this.fechaMovimiento = fechaMovimiento;
	}
	public String getCajaMovimiento() {
		return cajaMovimiento;
	}
	public void setCajaMovimiento(String cajaMovimiento) {
		this.cajaMovimiento = cajaMovimiento;
	}
	
	

}
