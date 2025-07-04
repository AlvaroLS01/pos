package com.comerzzia.cardoso.pos.persistence.balanza;

import java.math.BigDecimal;
import java.util.Date;

/**
 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA
 */
public class TicketBalanzaBean extends TicketBalanzaKey{

	private String numTicketBalanza;

	private String codseccion;

	private Date fecha;

	private BigDecimal total;

	private String uidTicket;

	private Boolean procesado;

	private Date fechaProceso;

	private String mensajeProceso;

	private byte[] ticketBalanza;

	public String getNumTicketBalanza(){
		return numTicketBalanza;
	}

	public void setNumTicketBalanza(String numTicketBalanza){
		this.numTicketBalanza = numTicketBalanza == null ? null : numTicketBalanza.trim();
	}

	public String getCodseccion(){
		return codseccion;
	}

	public void setCodseccion(String codseccion){
		this.codseccion = codseccion == null ? null : codseccion.trim();
	}

	public Date getFecha(){
		return fecha;
	}

	public void setFecha(Date fecha){
		this.fecha = fecha;
	}

	public BigDecimal getTotal(){
		return total;
	}

	public void setTotal(BigDecimal total){
		this.total = total;
	}

	public String getUidTicket(){
		return uidTicket;
	}

	public void setUidTicket(String uidTicket){
		this.uidTicket = uidTicket == null ? null : uidTicket.trim();
	}

	public Boolean getProcesado(){
		return procesado;
	}

	public void setProcesado(Boolean procesado){
		this.procesado = procesado;
	}

	public Date getFechaProceso(){
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso){
		this.fechaProceso = fechaProceso;
	}

	public String getMensajeProceso(){
		return mensajeProceso;
	}

	public void setMensajeProceso(String mensajeProceso){
		this.mensajeProceso = mensajeProceso == null ? null : mensajeProceso.trim();
	}

	public byte[] getTicketBalanza(){
		return ticketBalanza;
	}

	public void setTicketBalanza(byte[] ticketBalanza){
		this.ticketBalanza = ticketBalanza;
	}

}