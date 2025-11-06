package com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion;

import java.math.BigDecimal;

public class DatosPeticionCancelacionRecargaDto {

	private String numReferenciaProveedor;

	private BigDecimal importe;

	private String telefono;

	private String operador;

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

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
