package com.comerzzia.dinosol.pos.services.dispositivos.recargas;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TicketRecarga {

	public static final String TIPO_RECARGA_MOVIL = "TIPO_RECARGA.MOVIL";
	public static final String TIPO_RECARGA_POSA = "TIPO_RECARGA.POSA";
	public static final String TIPO_RECARGA_PIN_PRINTING = "TIPO_RECARGA.PIN";
	public static final String TIPO_RECARGA_CANCELACION = "TIPO_RECARGA.CANCELACION";

	private String uidTicketOriginal;
	private String codTicketOriginal;
	private String codTienda;
	private String codCaja;
	private Long idTicket;
	private Date fecha;
	private String cajero;
	private String ean;
	private String telefono;
	private String uidTicket;
	private String codReferenciaProveedor;
	private String mensaje;
	private BigDecimal importe;
	private String tipoRecarga;
	private String pin;

	public String getUidTicketOriginal() {
		return uidTicketOriginal;
	}

	public void setUidTicketOriginal(String uidTicketOriginal) {
		this.uidTicketOriginal = uidTicketOriginal;
	}

	public String getCodTicketOriginal() {
		return codTicketOriginal;
	}

	public void setCodTicketOriginal(String codTicketOriginal) {
		this.codTicketOriginal = codTicketOriginal;
	}

	public String getCodTienda() {
		return codTienda;
	}

	public void setCodTienda(String codTienda) {
		this.codTienda = codTienda;
	}

	public String getCodCaja() {
		return codCaja;
	}

	public void setCodCaja(String codCaja) {
		this.codCaja = codCaja;
	}

	public Long getIdTicket() {
		return idTicket;
	}

	public void setIdTicket(Long idTicket) {
		this.idTicket = idTicket;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getFechaStr() {
		return new SimpleDateFormat("dd/MM/yyyy").format(fecha);
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getCajero() {
		return cajero;
	}

	public void setCajero(String cajero) {
		this.cajero = cajero;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCodReferenciaProveedor() {
		return codReferenciaProveedor;
	}

	public void setCodReferenciaProveedor(String codReferenciaProveedor) {
		this.codReferenciaProveedor = codReferenciaProveedor;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public String getTipoRecarga() {
		return tipoRecarga;
	}

	public void setTipoRecarga(String tipoRecarga) {
		this.tipoRecarga = tipoRecarga;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

}
