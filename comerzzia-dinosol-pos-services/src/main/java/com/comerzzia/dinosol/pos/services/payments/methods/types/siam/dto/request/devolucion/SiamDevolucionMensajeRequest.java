package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.devolucion;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("deprecation")
public class SiamDevolucionMensajeRequest {

	protected String transaccion;		
	protected String interfazEMV;		
	protected String centroAutorizador;
	protected String codigoAutorizacion;
	protected BigDecimal importe;		
	protected Date fechahora;			
	protected String fechaHoraString;
	protected String codigoTienda;		
	protected String codigoTPV;			
	protected String codigoTicket;	
	protected String secuenciaPagoTEFTicket;
//	protected String codigoCliente;		
	
	//Sólo Santander Consumer o ceros
	protected BigDecimal importeOriginal;
	protected String secuenciaOriginal;
	protected Date fechaOriginal;
	protected String fechaOriginalAsString;
	
	@Override
	public String toString() {
		return transaccion + 
				interfazEMV + 
				centroAutorizador + 
//				codigoAutorizacion + 
				formateoImporte(importe) + 
				(fechahora != null ? formateoFecha(fechahora) : formateoFecha(fechaHoraString)) + 
				formateoCodigoTienda(codigoTienda) +
				formateoCodigoTPV(codigoTPV) +
				formateoCodigoTicket(codigoTicket) +
				secuenciaPagoTEFTicket +
//				formateoCodigoCliente(codigoCliente) +
				getFechaOriginalString() +
				getImporteOriginalString() +
				getSecuenciaOriginal();
	}

	public String formateoImporte(BigDecimal importe) {
		return StringUtils.leftPad(importe.negate().toString().replace(".", ""), 10, "0");
	}

	// solo para santader
	public String formateoImporteOriginal(BigDecimal importeOriginal) {
		return StringUtils.leftPad((importeOriginal.compareTo(BigDecimal.ZERO)<=0 ? importeOriginal.negate().toString().replace(".", "") : importeOriginal.toString().replace(".", "")), 9, "0");
	}

	public String formateoCodigoCliente(String codigoCliente) {
		return StringUtils.leftPad(codigoCliente, 15, "0");
	}

	public String formateoFecha(Date fechaHora) {

		String year = String.valueOf(fechaHora.getYear());
		String month = String.valueOf(fechaHora.getMonth() + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}
		String day = String.valueOf(fechaHora.getDate());
		if (day.length() == 1) {
			day = "0" + day;
		}
		String hour = String.valueOf(fechaHora.getHours());
		if (hour.length() == 1) {
			hour = "0" + hour;
		}
		String min = String.valueOf(fechaHora.getMinutes());
		if (min.length() == 1) {
			min = "0" + min;
		}
		String secs = String.valueOf(fechaHora.getSeconds());
		if (secs.length() == 1) {
			secs = "0" + secs;
		}

		return year + month + day + hour + min + secs;
	}

	public String formateoFecha(String fechaString) {

		String[] fechaSplit = fechaString.split(" ");
		String[] fechaArray = fechaSplit[0].trim().split("-");
		fechaArray = fechaSplit[0].trim().split("/");
		String[] horaArray = fechaSplit[1].trim().split(":");

		return fechaArray[2] + fechaArray[1] + fechaArray[0] + horaArray[0] + horaArray[1] + "00";

	}

	public String formateoFechaOriginal(Date fechaHora) {

		String year = String.valueOf(fechaHora.getYear());
		String month = String.valueOf(fechaHora.getMonth() + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}
		String day = String.valueOf(fechaHora.getDate());
		if (day.length() == 1) {
			day = "0" + day;
		}

		return year + month + day;
	}

	public String formateoCodigoTicket(String codTicket) {
		return StringUtils.leftPad(codTicket, 10, "0");
	}

	public String formateoCodigoTienda(String codTienda) {
		return StringUtils.leftPad(codTienda, 10, "0");
	}

	public String formateoCodigoTPV(String codigoTPV) {
		return StringUtils.leftPad(codigoTPV, 10, "0");
	}

	public String getTransaccion() {
		return transaccion;
	}

	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion;
	}

	public String getInterfazEMV() {
		return interfazEMV;
	}

	public void setInterfazEMV(String interfazEMV) {
		this.interfazEMV = interfazEMV;
	}

	public String getCentroAutorizador() {
		return centroAutorizador;
	}

	public void setCentroAutorizador(String centroAutorizador) {
		this.centroAutorizador = centroAutorizador;
	}

	public String getCodigoAutorizacion() {
		return codigoAutorizacion;
	}

	public void setCodigoAutorizacion(String codigoAutorizacion) {
		this.codigoAutorizacion = codigoAutorizacion;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Date getFechahora() {
		return fechahora;
	}

	public void setFechahora(Date fechahora) {
		this.fechahora = fechahora;
	}

	public String getFechaHoraString() {
		return fechaHoraString;
	}

	public void setFechaHoraString(String fechaHoraString) {
		this.fechaHoraString = fechaHoraString;
	}

	public String getCodigoTienda() {
		return codigoTienda;
	}

	public void setCodigoTienda(String codigoTienda) {
		this.codigoTienda = codigoTienda;
	}

	public String getCodigoTPV() {
		return codigoTPV;
	}

	public void setCodigoTPV(String codigoTPV) {
		this.codigoTPV = codigoTPV;
	}

	public String getCodigoTicket() {
		return codigoTicket;
	}

	public void setCodigoTicket(String codigoTicket) {
		this.codigoTicket = codigoTicket;
	}

	public String getSecuenciaPagoTEFTicket() {
		return secuenciaPagoTEFTicket;
	}

	public void setSecuenciaPagoTEFTicket(String secuenciaPagoTEFTicket) {
		this.secuenciaPagoTEFTicket = secuenciaPagoTEFTicket;
	}

//	public String getCodigoCliente() {
//		return codigoCliente;
//	}
//
//	public void setCodigoCliente(String codigoCliente) {
//		this.codigoCliente = codigoCliente;
//	}

	public BigDecimal getImporteOriginal() {
		return importeOriginal;
	}

	public void setImporteOriginal(BigDecimal importeOriginal) {
		this.importeOriginal = importeOriginal;
	}

	public String getImporteOriginalString() {
		return importeOriginal != null ? formateoImporteOriginal(importeOriginal) : "";
	}

	public String getSecuenciaOriginal() {
		return secuenciaOriginal;
	}

	public void setSecuenciaOriginal(String secuenciaOriginal) {
		this.secuenciaOriginal = secuenciaOriginal;
	}

	public Date getFechaOriginal() {
		return fechaOriginal;
	}

	public void setFechaOriginal(Date fechaOriginal) {
		this.fechaOriginal = fechaOriginal;
	}

	public String getFechaOriginalString() {
		return fechaOriginal != null ? formateoFechaOriginal(fechaOriginal) : (StringUtils.isNotBlank(getFechaOriginalAsString()) ? getFechaOriginalAsString() : "000000") ;
	}

	public String getFechaOriginalAsString() {
		return fechaOriginalAsString;
	}

	public void setFechaOriginalAsString(String fechaOriginalAsString) {
		
		String[] fechaArray = fechaOriginalAsString.split("/");
		String fechaParse = fechaArray[2].substring(2)+fechaArray[1]+fechaArray[0];
		
		this.fechaOriginalAsString = fechaParse;
	}

}
