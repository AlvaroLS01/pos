package com.comerzzia.iskaypet.pos.persistence.ticket.lineas;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AcumuladosPromo {

	private Long idPromocion;
	private BigDecimal totalAcumulado;
	private Long idTipoPromo;
	private List<Integer> lineasAplicadas;
	private boolean accesoCupon;
	
	public Long getIdPromocion() {
		return idPromocion;
	}

	public void setIdPromocion(Long idPromocion) {
		this.idPromocion = idPromocion;
	}

	public BigDecimal getTotalAcumulado() {
		return totalAcumulado;
	}

	public void setTotalAcumulado(BigDecimal totalAcumulado) {
		this.totalAcumulado = totalAcumulado;
	}

	public Long getIdTipoPromo() {
		return idTipoPromo;
	}

	public void setIdTipoPromo(Long idTipoPromo) {
		this.idTipoPromo = idTipoPromo;
	}

	public List<Integer> getLineasAplicadas() {
		return lineasAplicadas;
	}

	public void setLineasAplicadas(List<Integer> lineasAplicadas) {
		this.lineasAplicadas = lineasAplicadas;
	}
	
	public boolean isAccesoCupon() {
		return accesoCupon;
	}

	public void setAccesoCupon(String accesoCupon) {
		if (StringUtils.isNotBlank(accesoCupon) && StringUtils.equals("CUPON", accesoCupon)) {
			this.accesoCupon = true;
		} else {
			this.accesoCupon = false;
		}
	}
	


}
