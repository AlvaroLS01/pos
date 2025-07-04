package com.comerzzia.cardoso.pos.persistence.pagos.wordline;

import java.util.Date;

public class RegistroPagoWordlineKey {

	private String uidActividad;

	private Date fecha;

	private String tipoOperacion;

	public String getUidActividad() {
		return uidActividad;
	}

	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad == null ? null : uidActividad.trim();
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion == null ? null : tipoOperacion.trim();
	}
}