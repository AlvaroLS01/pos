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
package com.comerzzia.api.loyalty.persistence.collectives;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

public class ParametrosBuscarColectivosBean extends ParametrosBuscarBean {

	private static final long serialVersionUID = 5964462700965021184L;
	private String codColectivo = null;
	private String desColectivo = null;
	private String activo = null;
	private String codtipcolectivo = null;
	private String destipcolectivo = null;

	protected static final String TRUE = "S";

	public ParametrosBuscarColectivosBean() {
		super.setOrden("COD_COLECTIVO");
	}

	public String getCodColectivo() {
		return codColectivo;
	}

	public void setCodColectivo(String codColectivo) {
		this.codColectivo = codColectivo;
	}

	public String getDesColectivo() {
		return desColectivo;
	}

	public void setDesColectivo(String desColectivo) {
		this.desColectivo = desColectivo;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public Boolean isActivo() {
		if (activo.equalsIgnoreCase(TRUE)) {
			return true;
		}
		return false;
	}

	public String getCodtipcolectivo() {
		return codtipcolectivo;
	}

	public void setCodtipcolectivo(String codtipcolectivo) {
		this.codtipcolectivo = codtipcolectivo;
	}

	public String getDestipcolectivo() {
		return destipcolectivo;
	}

	public void setDestipcolectivo(String destipcolectivo) {
		this.destipcolectivo = destipcolectivo;
	}
}
