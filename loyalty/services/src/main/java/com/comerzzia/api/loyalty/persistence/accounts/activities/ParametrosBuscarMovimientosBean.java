/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.api.loyalty.persistence.accounts.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.core.util.base.ParametrosBuscarBean;

public class ParametrosBuscarMovimientosBean extends ParametrosBuscarBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7279308881177002384L;

	private Date fechaDesde;
	private Date fechaHasta;
	private Long idTarjeta;
	private Long idCuenta;
	private boolean movimientosNoProcesados = false;
	private List<Card> tarjetasAsociadas;
	private Long idTarjetaSelect;
	private Integer estado;
	private Integer ultimosMovimientos;
	private boolean soloSalida = false;
	private boolean soloEntrada = false;
	private boolean soloMovPositivos = false;
	

	public ParametrosBuscarMovimientosBean() {
		tarjetasAsociadas = new ArrayList<Card>();
		setOrden("FECHA ASC");
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Long getIdTarjeta() {
		return idTarjeta;
	}

	public void setIdTarjeta(Long idTarjeta) {
		this.idTarjeta = idTarjeta;
	}

	public Long getIdCuenta() {
		return idCuenta;
	}

	public void setIdCuenta(Long idCuenta) {
		this.idCuenta = idCuenta;
	}

	public boolean isMovimientosNoProcesados() {
		return movimientosNoProcesados;
	}

	public void setMovimientosNoProcesados(boolean noProcesados) {
		this.movimientosNoProcesados = noProcesados;
	}

	public List<Card> getTarjetasAsociadas() {
		return tarjetasAsociadas;
	}

	public void setTarjetasAsociadas(List<Card> tarjetasAsociadas) {
		this.tarjetasAsociadas = tarjetasAsociadas;
	}

    public Long getIdTarjetaSelect() {
    	return idTarjetaSelect;
    }
	
    public void setIdTarjetaSelect(Long idTarjetaSelect) {
    	this.idTarjetaSelect = idTarjetaSelect;
    }

	
    public Integer getEstado() {
    	return estado;
    }

	
    public void setEstado(Integer estado) {
    	this.estado = estado;
    }

	public Integer getUltimosMovimientos() {
		return ultimosMovimientos;
	}

	public void setUltimosMovimientos(Integer ultimosMovimientos) {
		this.ultimosMovimientos = ultimosMovimientos;
	}

	public boolean isSoloSalida() {
		return soloSalida;
	}

	public void setSoloSalida(boolean soloSalida) {
		this.soloSalida = soloSalida;
	}

	public boolean isSoloEntrada() {
		return soloEntrada;
	}

	public void setSoloEntrada(boolean soloEntrada) {
		this.soloEntrada = soloEntrada;
	}

	public boolean isSoloMovPositivos() {
		return soloMovPositivos;
	}

	public void setSoloMovPositivos(boolean soloMovPositivos) {
		this.soloMovPositivos = soloMovPositivos;
	}

    
}
