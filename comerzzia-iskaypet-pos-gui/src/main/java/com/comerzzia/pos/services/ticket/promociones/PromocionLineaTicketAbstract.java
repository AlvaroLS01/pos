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


package com.comerzzia.pos.services.ticket.promociones;

import java.math.BigDecimal;

import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.util.format.FormatUtil;


public abstract class PromocionLineaTicketAbstract  implements IPromocionLineaTicket {
    
    protected Long idPromocion;
    
    protected Long idTipoPromocion;

    protected Integer puntos;

    protected Long tipoDescuento;

    protected BigDecimal cantidadPromocion;

    protected BigDecimal cantidadPromocionAplicada;
    
    protected BigDecimal importeTotalDtoMenosMargen; // Indica el importe total de descuento de la promoción a la línea menos margen
    protected BigDecimal importeTotalDtoMenosIngreso; // Indica el importe total de descuento de la promoción a la línea menos ingreso
    protected BigDecimal importeTotalDtoFuturo; // Indica el importe total de descuento de la promoción a la línea menos ingreso
    
    protected String acceso;
    protected String codAcceso;
    protected boolean exclusiva;

    public PromocionLineaTicketAbstract() {
    }
    
    public PromocionLineaTicketAbstract(PromocionTicket promocionTicket) {
        idPromocion = promocionTicket.getIdPromocion();
        idTipoPromocion = promocionTicket.getIdTipoPromocion();
        acceso = promocionTicket.getAcceso();
        codAcceso = promocionTicket.getCodAcceso();
        puntos = 0;
        cantidadPromocion = BigDecimal.ZERO;
        cantidadPromocionAplicada = BigDecimal.ZERO;
        importeTotalDtoMenosMargen = BigDecimal.ZERO;
        importeTotalDtoMenosIngreso = BigDecimal.ZERO;
        exclusiva = promocionTicket.isExclusiva();
        tipoDescuento = promocionTicket.getTipoDescuento();
    }

    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#addCantidadPromocion()
	 */
    @Override
	public void addCantidadPromocion(BigDecimal aumento){
        cantidadPromocion = cantidadPromocion.add(aumento);
    }
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#addCantidadPromocionAplicada()
	 */
    @Override
	public void addCantidadPromocionAplicada(BigDecimal aumento){
        cantidadPromocionAplicada = cantidadPromocionAplicada.add(aumento);
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#addImporteTotalDto(java.math.BigDecimal)
	 */
    @Override
	public void addImporteTotalDto(BigDecimal importe){
    	if (isDescuentoMenosIngreso()){
    		importeTotalDtoMenosIngreso = importeTotalDtoMenosIngreso.add(importe);
    	}
    	else if(isDescuentoMenosMargen()){
    		importeTotalDtoMenosMargen = importeTotalDtoMenosMargen.add(importe);
    	}
    	else if(isDescuentoFuturo()) {
    		importeTotalDtoFuturo = importeTotalDtoFuturo.add(importe);
    	}
    }
    

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setImporteTotalDto(java.math.BigDecimal)
	 */
    @Override
	public void setImporteTotalDto(BigDecimal importe){
    	if (isDescuentoMenosIngreso()){
    		importeTotalDtoMenosIngreso = importe;
    	}
    	else if(isDescuentoMenosMargen()){
    		importeTotalDtoMenosMargen = importe;
    	}
    	else if(isDescuentoFuturo()) {
    		importeTotalDtoFuturo = importe;
    	}
    }
    
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getCantidadPromocion()
	 */
    @Override
	public BigDecimal getCantidadPromocion() {
        return cantidadPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setCantidadPromocion(java.math.BigDecimal)
	 */
    @Override
	public void setCantidadPromocion(BigDecimal cantidadPromocion) {
        this.cantidadPromocion = cantidadPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getCantidadPromocionAplicada()
	 */
    @Override
	public BigDecimal getCantidadPromocionAplicada() {
        return cantidadPromocionAplicada;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setCantidadPromocionAplicada(java.math.BigDecimal)
	 */
    @Override
	public void setCantidadPromocionAplicada(BigDecimal cantidadPromocionAplicada) {
        this.cantidadPromocionAplicada = cantidadPromocionAplicada;
    }

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getIdPromocion()
	 */
	@Override
	public Long getIdPromocion() {
        return idPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setIdPromocion(java.lang.Long)
	 */
    @Override
	public void setIdPromocion(Long idPromocion) {
        this.idPromocion = idPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getIdTipoPromocion()
	 */
    @Override
	public Long getIdTipoPromocion() {
        return idTipoPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setIdTipoPromocion(java.lang.Long)
	 */
    @Override
	public void setIdTipoPromocion(Long idTipoPromocion) {
        this.idTipoPromocion = idTipoPromocion;
    }


    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getPuntos()
	 */
    @Override
	public Integer getPuntos() {
        return puntos;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setPuntos(java.lang.Integer)
	 */
    @Override
	public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getIdPromocionAsString()
	 */
    @Override
	public String getIdPromocionAsString(){
        return String.valueOf(idPromocion);
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getImporteTotalDtoMenosMargen()
	 */
    @Override
	public BigDecimal getImporteTotalDtoMenosMargen() {
    	return importeTotalDtoMenosMargen;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getImporteTotalDtoMenosMargenAsStringNegate()
	 */
    @Override
	public String getImporteTotalDtoMenosMargenAsStringNegate() {
    	return "-" + FormatUtil.getInstance().formateaNumero(importeTotalDtoMenosMargen, 2);
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setImporteTotalDtoMenosMargen(java.math.BigDecimal)
	 */
    @Override
	public void setImporteTotalDtoMenosMargen(BigDecimal importeTotalDtoMenosMargen) {
        this.importeTotalDtoMenosMargen = importeTotalDtoMenosMargen;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getImporteTotalDtoMenosIngreso()
	 */
    @Override
	public BigDecimal getImporteTotalDtoMenosIngreso() {
		return importeTotalDtoMenosIngreso;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setImporteTotalDtoMenosIngreso(java.math.BigDecimal)
	 */
	@Override
	public void setImporteTotalDtoMenosIngreso(BigDecimal importeTotalDtoMenosIngreso) {
		this.importeTotalDtoMenosIngreso = importeTotalDtoMenosIngreso;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getAcceso()
	 */
	@Override
	public String getAcceso() {
        return acceso;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setAcceso(java.lang.String)
	 */
    @Override
	public void setAcceso(String acceso) {
        this.acceso = acceso;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getCodAcceso()
	 */
    @Override
	public String getCodAcceso() {
        return codAcceso;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setCodAcceso(java.lang.String)
	 */
    @Override
	public void setCodAcceso(String codAcceso) {
        this.codAcceso = codAcceso;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#isExclusiva()
	 */
    @Override
	public boolean isExclusiva() {
        return exclusiva;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setExclusiva(boolean)
	 */
    @Override
	public void setExclusiva(boolean exclusiva) {
        this.exclusiva = exclusiva;
    }
    
    
    
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#getTipoDescuento()
	 */
    @Override
	public Long getTipoDescuento() {
		return tipoDescuento;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#setTipoDescuento(java.lang.Long)
	 */
	@Override
	public void setTipoDescuento(Long tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#isDescuentoMenosMargen()
	 */
	@Override
	public boolean isDescuentoMenosMargen(){
        return getTipoDescuento().equals(Promocion.TIPO_DTO_MENOS_MARGEN);
    }
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket#isDescuentoMenosIngreso()
	 */
    @Override
	public boolean isDescuentoMenosIngreso(){
        return getTipoDescuento().equals(Promocion.TIPO_DTO_MENOS_INGRESO);
    }
    
}
