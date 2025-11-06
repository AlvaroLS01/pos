package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos;

import java.math.BigDecimal;

import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;

public class TarjetaFidelizadoGui {

	private String numeroTarjeta;

	private MedioPagoBean medioPago;

	private BigDecimal saldoDisponible;

	private BigDecimal importePago;
	
	private String mensaje;
	
	private boolean pagoHabilitado;

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
	}

	public MedioPagoBean getMedioPago() {
		return medioPago;
	}

	public void setMedioPago(MedioPagoBean medioPago) {
		this.medioPago = medioPago;
	}

	public BigDecimal getSaldoDisponible() {
		return saldoDisponible;
	}

	public void setSaldoDisponible(BigDecimal saldoDisponible) {
		this.saldoDisponible = saldoDisponible;
	}

	public BigDecimal getImportePago() {
		return importePago;
	}

	public void setImportePago(BigDecimal importePago) {
		this.importePago = importePago;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public boolean isPagoHabilitado() {
		return pagoHabilitado;
	}

	public void setPagoHabilitado(boolean pagoHabilitado) {
		this.pagoHabilitado = pagoHabilitado;
	}

	@Override
	public String toString() {
		return numeroTarjeta;
	}

}
