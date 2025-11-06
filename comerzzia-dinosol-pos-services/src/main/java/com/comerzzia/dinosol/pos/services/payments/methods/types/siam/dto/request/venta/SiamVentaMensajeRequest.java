package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.venta;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("deprecation")
public class SiamVentaMensajeRequest {
	//75 Caracteres enviamos contando el estado
	//Ej. 1VTX0000009599202308221154270000000001000000051200000008960000000000000000
	
	//estado							//long 1	-->1
	protected String transaccion;		//long 2	-->VT
	protected String interfazEMV;		//long 1	-->X
	protected Integer codigoAplazado;	//long 2	-->00
	protected BigDecimal importe;		//long 8	-->00009599
	protected Date fechaHora;			//long 14	-->20230822115427
	protected String fechaHoraString;
	protected String codigoTienda;		//long 10	-->0000000001
	protected String codigoTPV;			//long 10	-->0000000512
	protected String codigoTicket;		//long 10	-->0000000896
	protected String secuenciaPago;		//long 1	-->1
	protected String codigoCliente;		//long 15	-->0000000000000000

	public SiamVentaMensajeRequest() {

	}

	public SiamVentaMensajeRequest(String transaccion, String interfazEMV, Integer codigoAplazado, BigDecimal importe, Date fechaHora, String fechaHoraString, String codigoTienda, String codigoTPV,
	        String codigoTicket, String secuencia, String codigoCliente) {
		super();
		this.transaccion = transaccion;
		this.interfazEMV = interfazEMV;
		this.codigoAplazado = codigoAplazado;
		this.importe = importe;
		this.fechaHora = fechaHora;
		this.fechaHoraString = fechaHoraString;
		this.codigoTienda = codigoTienda;
		this.codigoTPV = codigoTPV;
		this.codigoTicket = codigoTicket;
		this.secuenciaPago = secuencia;
		this.codigoCliente = codigoCliente;
	}

	@Override
	public String toString() {
		return transaccion + 
				interfazEMV + 
				formateoCodigoAplazado(codigoAplazado) + 
				formateoImporte(importe) + 
				(fechaHora != null ? formateoFecha(fechaHora) : fechaHoraString) + 
				formateoCodigoTienda(codigoTienda) + formateoCodigoTPV(codigoTPV) + 
				formateoCodigoTicket(codigoTicket) + 
				secuenciaPago + 
				formateoCodigoCliente(codigoCliente);
	}
	
	public String formateoImporte(BigDecimal importe) {
		return StringUtils.leftPad(importe.toString().replace(".", ""), 8, "0");
	}
	
	public String formateoCodigoAplazado(Integer codigoAplazado) {
		return StringUtils.leftPad(codigoAplazado.toString(), 2, "0");
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
	
	public String formateoCodigoCliente(String codigoCliente) {
		return StringUtils.leftPad(codigoCliente, 15, "0");
	}
	
	public String formateoFecha(Date fechaHora) {
		 
		String year = String.valueOf(1900 + fechaHora.getYear());
		String month = String.valueOf(fechaHora.getMonth()+1);
		if(month.length()==1) {
			month = "0" + month;
		}
		String day = String.valueOf(fechaHora.getDate());
		if(day.length()==1) {
			day = "0" + day;
		}
		String hour = String.valueOf(fechaHora.getHours());
		if(hour.length()==1) {
			hour = "0" + hour;
		}
		String min = String.valueOf(fechaHora.getMinutes());
		if(min.length()==1) {
			min = "0" + min;
		}
		String secs = String.valueOf(fechaHora.getSeconds());
		if(secs.length()==1) {
			secs = "0" + secs;
		}
		
		return year + month + day + hour + min + secs;
	}
	
	public String formateoFecha(String fechaString) {
		
		String[] fechaSplit = fechaString.split(" "); 
		String[] fechaArray = fechaSplit[0].trim().split("-");
		String[] horaArray =  fechaSplit[0].trim().split(":");
		
		return fechaArray[0]+fechaArray[1]+fechaArray[2]+horaArray[0]+horaArray[1]+horaArray[2].substring(0,1);
		
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

	public Integer getCodigoAplazado() {
		return codigoAplazado;
	}

	public void setCodigoAplazado(Integer codigoAplazado) {
		this.codigoAplazado = codigoAplazado;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Date getFechahora() {
		return fechaHora;
	}

	public void setFechahora(Date fechahora) {
		this.fechaHora = fechahora;
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
	
	public Date getFechaHora() {
		return fechaHora;
	}
	
	public void setFechaHora(Date fechaHora) {
		this.fechaHora = fechaHora;
	}
	
	public String getSecuenciaPago() {
		return secuenciaPago;
	}
	
	public void setSecuenciaPago(String secuencia) {
		this.secuenciaPago = secuencia;
	}

	public String getCodigoCliente() {
		return codigoCliente;
	}

	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = codigoCliente;
	}
	
}
