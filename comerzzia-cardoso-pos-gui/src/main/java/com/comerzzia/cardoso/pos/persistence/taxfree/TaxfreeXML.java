package com.comerzzia.cardoso.pos.persistence.taxfree;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "taxfree")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxfreeXML {
	
	@XmlElement(name = "uid_actividad")
	private String uidActividad;
	
	@XmlElement(name = "cod_ticket")
	private String codTicket;
	
	@XmlElement(name = "barcode")
	private String barcode;
	
	@XmlElement(name = "tipo_movimiento")
	private String tipoMovimiento;
	
	@XmlElement(name = "fecha_movimiento")
	private String fechaMovimiento;
	
	@XmlElement(name = "caja_movimiento")
	private String cajaMovimiento;
	
	@XmlElement(name = "pasaporte")
	private String pasaporte;
	
	@XmlElement(name = "uid_ticket")
	private String uidTicket;

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
	public String getFechaMovimiento() {
		return fechaMovimiento;
	}
	public void setFechaMovimiento(String fechaMovimiento) {
		this.fechaMovimiento = fechaMovimiento;
	}
	public String getCajaMovimiento() {
		return cajaMovimiento;
	}
	public void setCajaMovimiento(String cajaMovimiento) {
		this.cajaMovimiento = cajaMovimiento;
	}
	public String getPasaporte() {
		return pasaporte;
	}
	public void setPasaporte(String pasaporte) {
		this.pasaporte = pasaporte;
	}
	
	public String getUidTicket() {
		return uidTicket;
	}
	
	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}
	
}
