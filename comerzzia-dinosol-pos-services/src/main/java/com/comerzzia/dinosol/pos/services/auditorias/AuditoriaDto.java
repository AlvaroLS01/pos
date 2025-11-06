package com.comerzzia.dinosol.pos.services.auditorias;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jfree.util.Log;

import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;
import com.google.gson.Gson;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditoriaDto {

	private String uidActividad;

	private String uidAuditoria;

	private Date fecha;

	private String hora;

	private String codalm;

	private String codcaja;

	private String tipo;

	private String cajeroCaja;

	private String cajeroOperacion;

	private String uidTicket;

	private String codart;

	private BigDecimal precioAnterior;

	private BigDecimal precioNuevo;

	private String numTarjetaFidelizado;

	private String nombreFidelizado;

	private BigDecimal importeTicket;

	private BigDecimal numArticulosTicket;

	private String codMotivo;

	private String desMotivo;

	private BigDecimal cantidadLinea;

	public String getUidActividad() {
		return uidActividad;
	}

	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad;
	}

	public String getUidAuditoria() {
		return uidAuditoria;
	}

	public void setUidAuditoria(String uidAuditoria) {
		this.uidAuditoria = uidAuditoria;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getCodalm() {
		return codalm;
	}

	public void setCodalm(String codalm) {
		this.codalm = codalm;
	}

	public String getCodcaja() {
		return codcaja;
	}

	public void setCodcaja(String codcaja) {
		this.codcaja = codcaja;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCajeroCaja() {
		return cajeroCaja;
	}

	public void setCajeroCaja(String cajeroCaja) {
		this.cajeroCaja = cajeroCaja;
	}

	public String getCajeroOperacion() {
		return cajeroOperacion;
	}

	public void setCajeroOperacion(String cajeroOperacion) {
		this.cajeroOperacion = cajeroOperacion;
	}

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	public String getCodart() {
		return codart;
	}

	public void setCodart(String codart) {
		this.codart = codart;
	}

	public BigDecimal getPrecioAnterior() {
		return precioAnterior;
	}

	public void setPrecioAnterior(BigDecimal precioAnterior) {
		this.precioAnterior = precioAnterior;
	}

	public BigDecimal getPrecioNuevo() {
		return precioNuevo;
	}

	public void setPrecioNuevo(BigDecimal precioNuevo) {
		this.precioNuevo = precioNuevo;
	}

	public String getNumTarjetaFidelizado() {
		return numTarjetaFidelizado;
	}

	public void setNumTarjetaFidelizado(String numTarjetaFidelizado) {
		this.numTarjetaFidelizado = numTarjetaFidelizado;
	}

	public String getNombreFidelizado() {
		return nombreFidelizado;
	}

	public void setNombreFidelizado(String nombreFidelizado) {
		this.nombreFidelizado = nombreFidelizado;
	}

	public BigDecimal getImporteTicket() {
		return importeTicket;
	}

	public void setImporteTicket(BigDecimal importeTicket) {
		this.importeTicket = importeTicket;
	}

	public BigDecimal getNumArticulosTicket() {
		return numArticulosTicket;
	}

	public void setNumArticulosTicket(BigDecimal numArticulosTicket) {
		this.numArticulosTicket = numArticulosTicket;
	}

	public String getCodMotivo() {
		return codMotivo;
	}

	public void setCodMotivo(String codMotivo) {
		this.codMotivo = codMotivo;
	}

	public String getDesMotivo() {
		return desMotivo;
	}

	public void setDesMotivo(String desMotivo) {
		this.desMotivo = desMotivo;
	}

	public BigDecimal getCantidadLinea() {
		return cantidadLinea;
	}

	public void setCantidadLinea(BigDecimal cantidadLinea) {
		this.cantidadLinea = cantidadLinea;
	}

	@Override
	public String toString() {
		String xml = "";
		try {
			xml = new String(MarshallUtil.crearXML(this));
		}
		catch (Exception e) {
			Log.error("toString() - Error al parsear a XML: " + e.getMessage(), e);
		}
		return xml;
	}

}
