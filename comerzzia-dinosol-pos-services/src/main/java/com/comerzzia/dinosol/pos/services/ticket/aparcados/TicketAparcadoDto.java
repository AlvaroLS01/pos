package com.comerzzia.dinosol.pos.services.ticket.aparcados;

import java.math.BigDecimal;
import java.util.Date;

public class TicketAparcadoDto {

	private String uidActividad;

	private String uidTicket;

	private String codAlmacen;

	private Date fecha;

	private String usuario;

	private String codCliente;

	private BigDecimal importe;

	private Integer numArticulos;

	private String codCaja;

	private Long idTipoDocumento;

	private String ticket;

	private String apiKey;

	public String getUidActividad() {
		return uidActividad;
	}

	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad;
	}

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	public String getCodAlmacen() {
		return codAlmacen;
	}

	public void setCodAlmacen(String codAlmacen) {
		this.codAlmacen = codAlmacen == null ? null : codAlmacen.trim();
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario == null ? null : usuario.trim();
	}

	public String getCodCliente() {
		return codCliente;
	}

	public void setCodCliente(String codCliente) {
		this.codCliente = codCliente == null ? null : codCliente.trim();
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Integer getNumArticulos() {
		return numArticulos;
	}

	public void setNumArticulos(Integer numArticulos) {
		this.numArticulos = numArticulos;
	}

	public String getCodCaja() {
		return codCaja;
	}

	public void setCodCaja(String codCaja) {
		this.codCaja = codCaja == null ? null : codCaja.trim();
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public String toString() {
		return "TicketAparcadoDto [uidActividad=" + uidActividad + ", uidTicket=" + uidTicket + ", codAlmacen=" + codAlmacen + ", fecha=" + fecha + ", usuario=" + usuario + ", codCliente="
		        + codCliente + ", importe=" + importe + ", numArticulos=" + numArticulos + ", codCaja=" + codCaja + ", idTipoDocumento=" + idTipoDocumento + ", ticket=" + ticket + ", apiKey="
		        + apiKey + "]";
	}

}