package com.comerzzia.cardoso.pos.persistence.promociones;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.core.util.base.MantenimientoBean;

/**
 * GAP - PERSONALIZACIONES V3 - PROMOCIÓN EMPLEADO
 */

public class PromoCuentaBean {
	
	private Long idPromocion;
	private Long idCuentaTarjeta;
	private String uidTransaccion;
	private String referenciaUso;
	private Date fechaUso;
	private BigDecimal importeUso;
	private BigDecimal importeTotalAhorro;
	private boolean confirmado = false;
	private Long tipoDto;
	private String textoPromocion;
	private BigDecimal descuento;
	private BigDecimal importeTotal;

	/* Campo extra para entrada de servicios REST. Número de tarjeta del fidelizado para el que se realiza la
	 * petición. */
	private String numeroTarjeta;

	public Long getIdPromocion(){
		return idPromocion;
	}

	public void setIdPromocion(Long idPromocion){
		this.idPromocion = idPromocion;
	}

	public Long getIdCuentaTarjeta(){
		return idCuentaTarjeta;
	}

	public void setIdCuentaTarjeta(Long idCuentaTarjeta){
		this.idCuentaTarjeta = idCuentaTarjeta;
	}

	public String getUidTransaccion(){
		return uidTransaccion;
	}

	public void setUidTransaccion(String uidTransaccion){
		this.uidTransaccion = uidTransaccion;
	}

	public String getReferenciaUso(){
		return referenciaUso;
	}

	public void setReferenciaUso(String referenciaUso){
		this.referenciaUso = referenciaUso;
	}

	public Date getFechaUso(){
		return fechaUso;
	}

	public void setFechaUso(Date fechaUso){
		this.fechaUso = fechaUso;
	}

	/*public String getConfirmado(){
		return (confirmado) ? TRUE : FALSE;
	}

	public void setConfirmado(String confirmado){
		if(confirmado.equals(TRUE_BOOLEAN)){
			confirmado = TRUE;
		}
		else if(confirmado.equals(FALSE_BOOLEAN)){
			confirmado = FALSE;
		}
		this.confirmado = confirmado.equals(TRUE);
	}*/

	public boolean isConfirmado(){
		return this.confirmado;
	}

	public void setConfirmado(boolean confirmado){
		this.confirmado = confirmado;
	}

	public BigDecimal getImporteUso(){
		return importeUso;
	}

	public void setImporteUso(BigDecimal importeUso){
		this.importeUso = importeUso;
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		PromoCuentaBean other = (PromoCuentaBean) obj;
		if(fechaUso == null){
			if(other.fechaUso != null)
				return false;
		}
		else if(!fechaUso.equals(other.fechaUso))
			return false;
		if(idCuentaTarjeta == null){
			if(other.idCuentaTarjeta != null)
				return false;
		}
		else if(!idCuentaTarjeta.equals(other.idCuentaTarjeta))
			return false;
		if(idPromocion == null){
			if(other.idPromocion != null)
				return false;
		}
		else if(!idPromocion.equals(other.idPromocion))
			return false;
		if(referenciaUso == null){
			if(other.referenciaUso != null)
				return false;
		}
		else if(!referenciaUso.equals(other.referenciaUso))
			return false;
		if(uidTransaccion == null){
			if(other.uidTransaccion != null)
				return false;
		}
		else if(!uidTransaccion.equals(other.uidTransaccion))
			return false;
		return true;
	}

	public String getNumeroTarjeta(){
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta){
		this.numeroTarjeta = numeroTarjeta;
	}

	/*public static long getSerialversionuid(){
		return serialVersionUID;
	}*/

	public BigDecimal getImporteTotalAhorro(){
		return importeTotalAhorro;
	}

	public void setImporteTotalAhorro(BigDecimal importeTotalAhorro){
		this.importeTotalAhorro = importeTotalAhorro;
	}

	public Long getTipoDto(){
		return tipoDto;
	}

	public void setTipoDto(Long tipoDto){
		this.tipoDto = tipoDto;
	}

	public String getTextoPromocion(){
		return textoPromocion;
	}

	public void setTextoPromocion(String textoPromocion){
		this.textoPromocion = textoPromocion;
	}

	public BigDecimal getDescuento(){
		return descuento;
	}

	public void setDescuento(BigDecimal descuento){
		this.descuento = descuento;
	}

	public BigDecimal getImporteTotal(){
		return importeTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal){
		this.importeTotal = importeTotal;
	}

}
