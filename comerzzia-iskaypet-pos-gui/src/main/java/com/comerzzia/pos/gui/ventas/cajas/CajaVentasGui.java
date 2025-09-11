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
package com.comerzzia.pos.gui.ventas.cajas;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;

public class CajaVentasGui {
	
	private Date fecha;
	
	private BigDecimal cargo;
	
	private BigDecimal abono;
	
	private String concepto;
	
	private String documento;
	
	private String codMedioPago;
	
	private String codDocumento;
	
	private String idDocumento;
	
	public CajaVentasGui(CajaMovimientoBean mov){
		
		fecha        = mov.getFecha();
		cargo        = mov.getCargo();
		abono        = mov.getAbono();
		concepto     = mov.getConcepto();
		documento    = mov.getDocumento();
		idDocumento  = mov.getIdDocumento();
		codMedioPago = mov.getCodMedioPago();
		try {
			codDocumento = SpringContext.getBean(Sesion.class).getAplicacion().getDocumentos().getDocumento(mov.getIdTipoDocumento()).getCodtipodocumento();
		} catch (DocumentoException e) {
			codDocumento = "";
		}
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public BigDecimal getCargo() {
		return cargo;
	}

	public void setCargo(BigDecimal cargo) {
		this.cargo = cargo;
	}

	public BigDecimal getAbono() {
		return abono;
	}

	public void setAbono(BigDecimal abono) {
		this.abono = abono;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getCodMedioPago() {
		return codMedioPago;
	}

	public void setCodMedioPago(String codMedioPago) {
		this.codMedioPago = codMedioPago;
	}

	public String getCodDocumento() {
		return codDocumento;
	}

	public void setCodDocumento(String codDocumento) {
		this.codDocumento = codDocumento;
	}
	
	public String getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(String idDocumento) {
		this.idDocumento = idDocumento;
	}
}
