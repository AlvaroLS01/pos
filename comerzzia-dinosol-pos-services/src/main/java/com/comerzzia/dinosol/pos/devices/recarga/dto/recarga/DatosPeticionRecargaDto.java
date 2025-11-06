package com.comerzzia.dinosol.pos.devices.recarga.dto.recarga;

import java.math.BigDecimal;

public class DatosPeticionRecargaDto {

	private String numReferenciaProveedor;

	private BigDecimal importe;

	private String telefono;

	private String codTicket;

	private String usuario;

	public String getNumReferenciaProveedor() {
		return numReferenciaProveedor;
	}

	public void setNumReferenciaProveedor(String numReferenciaProveedor) {
		this.numReferenciaProveedor = numReferenciaProveedor;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCodTicket() {
		return codTicket;
	}

	public void setCodTicket(String codTicket) {
		this.codTicket = codTicket;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
