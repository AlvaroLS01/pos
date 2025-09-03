package com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.response;

import java.util.Date;

import com.comerzzia.api.model.loyalty.TipoTarjetaBean;
import com.comerzzia.api.rest.client.response.ResponseRest;

public class IskaypetResponseGetTarjetaRegaloRest extends ResponseRest {

	private Double saldo = null;
	private Double saldoProvisional = null;
	private String numeroTarjeta = null;
	private String activa;
	private String baja;
	private boolean tarjetaValida;
	private String errorValidacion;
	private TipoTarjetaBean tipoTarjeta;
	private Date fechaEmision;
	private Date fechaActivacion;
	private Date fechaBaja;

	public IskaypetResponseGetTarjetaRegaloRest() {
		super();
	}

	public IskaypetResponseGetTarjetaRegaloRest(short codError, String mensaje) {
		super(codError, mensaje);
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public Double getSaldoProvisional() {
		return saldoProvisional;
	}

	public void setSaldoProvisional(Double saldoProvisional) {
		this.saldoProvisional = saldoProvisional;
	}

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
	}

	public String getActiva() {
		return activa;
	}

	public void setActiva(String activa) {
		this.activa = activa;
	}

	public String getBaja() {
		return baja;
	}

	public void setBaja(String baja) {
		this.baja = baja;
	}

	public boolean isTarjetaValida() {
		return tarjetaValida;
	}

	public void setTarjetaValida(boolean tarjetaValida) {
		this.tarjetaValida = tarjetaValida;
	}

	public String getErrorValidacion() {
		return errorValidacion;
	}

	public void setErrorValidacion(String errorValidacion) {
		this.errorValidacion = errorValidacion;
	}

	public TipoTarjetaBean getTipoTarjeta() {
		return tipoTarjeta;
	}

	public void setTipoTarjeta(TipoTarjetaBean tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}

	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public Date getFechaActivacion() {
		return fechaActivacion;
	}

	public void setFechaActivacion(Date fechaActivacion) {
		this.fechaActivacion = fechaActivacion;
	}

	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

}
