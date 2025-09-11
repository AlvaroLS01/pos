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


package com.comerzzia.pos.services.ticket.cabecera;

import java.math.BigDecimal;
import java.util.List;

import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;


public abstract class TotalesTicketAbstract<C extends IPagoTicket>  implements ITotalesTicket {
    

    protected ITicket ticket;
    
    protected BigDecimal impuestos;
    
    protected BigDecimal base;
    
    protected BigDecimal total;
    
    protected BigDecimal entregado;
    
    protected BigDecimal totalSinPromociones;
    
    protected BigDecimal totalPromocionesLineas;
    
    protected BigDecimal totalPromocionesCabecera;
    
    protected BigDecimal totalPromociones;
    
    protected BigDecimal totalAPagar;
    
    protected int puntos;
    
    protected C cambio;
    
    protected BigDecimal entregadoACuenta = BigDecimal.ZERO;
    
    public TotalesTicketAbstract(){    
        totalPromocionesCabecera = BigDecimal.ZERO;
        totalPromocionesLineas = BigDecimal.ZERO;
    }
    
    public TotalesTicketAbstract(ITicket ticket){
        resetearTotales();
        this.ticket = ticket;
    }
    
    protected void resetearTotales(){
    	base = BigDecimal.ZERO;
        impuestos = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
        entregado = BigDecimal.ZERO;
        totalSinPromociones = BigDecimal.ZERO;
        totalPromocionesLineas = BigDecimal.ZERO;
        totalPromocionesCabecera = BigDecimal.ZERO;
        totalPromociones = BigDecimal.ZERO;
        totalAPagar = BigDecimal.ZERO;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#recalcular()
	 */
    @Override
	public void recalcular(){
        // Reseteamos los totales
        resetearTotales();
        
        // Calculamos total, base e impuestos sumando todas las líneas
        for (LineaTicketAbstract lineaT :  (List<LineaTicketAbstract>)ticket.getLineas()) {
        	BigDecimal importeTotalConDto = lineaT.getImporteTotalConDto();
        	importeTotalConDto = BigDecimalUtil.redondear(importeTotalConDto);
    		total = total.add(importeTotalConDto);
    		BigDecimal importeTotalPromocionesMenosIngreso = BigDecimalUtil.redondear(lineaT.getImporteTotalPromocionesMenosIngreso());
			totalPromocionesCabecera = totalPromocionesCabecera.add(importeTotalPromocionesMenosIngreso);
            totalSinPromociones = totalSinPromociones.add(lineaT.getImporteTotalSinDto());
            base = base.add(BigDecimalUtil.redondear(lineaT.getImporteConDto()));
        }
        totalPromocionesLineas = totalSinPromociones.subtract(total);
        
        // Calculamos entregado sumando todos los pagos
        for (Object pagoTicket : ((TicketVenta)ticket).getPagos()) {
            entregado = entregado.add(((IPagoTicket)pagoTicket).getImporte());
        }
        
        // Calculamos totales 
        impuestos = total.subtract(base);
        totalPromociones = totalPromocionesCabecera.add(totalPromocionesLineas);
        totalSinPromociones = total.subtract(totalPromociones);
        totalAPagar = total;
        
        totalSinPromociones = BigDecimalUtil.redondear(totalSinPromociones);
        totalPromocionesLineas = BigDecimalUtil.redondear(totalPromocionesLineas);
        totalPromociones = BigDecimalUtil.redondear(totalPromociones);
        
        if(ticket.isEsDevolucion()){
            getCambio().setImporte(entregado.abs().subtract(totalAPagar.abs()));
        }
        else{
        	getCambio().setImporte(entregado.add(entregadoACuenta).subtract(totalAPagar));
        }
        
        if (BigDecimalUtil.isMenorACero(cambio.getImporte())){
        	getCambio().setImporte(BigDecimal.ZERO);
        }
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getPendiente()
	 */
    @Override
	public BigDecimal getPendiente() {
        BigDecimal pendiente = totalAPagar.abs().subtract(entregado.abs().add(entregadoACuenta));
        
        if (BigDecimalUtil.isMayorACero(pendiente)) {
            return pendiente;
        }
        return BigDecimal.ZERO;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotal()
	 */
    @Override
	public BigDecimal getTotal() {
        return total;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#setTicket(com.comerzzia.pos.services.ticket.TicketVenta)
	 */
    @Override
	public void setTicket(TicketVenta ticket) {
        this.ticket = ticket;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getEntregado()
	 */
    @Override
	public BigDecimal getEntregado() {
        return entregado;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#setImpuestos(java.math.BigDecimal)
	 */
    @Override
	public void setImpuestos(BigDecimal impuestos) {
    	this.impuestos = impuestos;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#setBase(java.math.BigDecimal)
	 */
    @Override
	public void setBase(BigDecimal base) {
        this.base = base;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getImpuestos()
	 */
    @Override
	public BigDecimal getImpuestos() {
		return impuestos;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getBase()
	 */
	@Override
	public BigDecimal getBase() {
		return base;
	}
	
    // Métodos para representación en pantalla e impresión    

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getPendienteAsString()
	 */
	@Override
	public String getPendienteAsString() {
        return FormatUtil.getInstance().formateaImporte(getPendiente());
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getCambioAsString()
	 */
    @Override
	public String getCambioAsString() {
        return FormatUtil.getInstance().formateaImporte(getCambio().getImporte());
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalAsString()
	 */
    @Override
	public String getTotalAsString() {
        return FormatUtil.getInstance().formateaImporte(getTotal());
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getEntregadoAsString()
	 */
    @Override
	public String getEntregadoAsString() {
        return FormatUtil.getInstance().formateaImporte(getEntregado());
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalSinPromociones()
	 */
    @Override
	public BigDecimal getTotalSinPromociones() {
        return totalSinPromociones;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalPromocionesLineas()
	 */
    @Override
	public BigDecimal getTotalPromocionesLineas() {
        return totalPromocionesLineas;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalPromocionesCabecera()
	 */
    @Override
	public BigDecimal getTotalPromocionesCabecera() {
        return totalPromocionesCabecera;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalPromociones()
	 */
    @Override
	public BigDecimal getTotalPromociones() {
    	return totalPromociones;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalPromocionesAsString()
	 */
    @Override
	public String getTotalPromocionesAsString() {
    	return FormatUtil.getInstance().formateaImporte(totalPromociones);
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalPromocionesCabeceraAsString()
	 */
    @Override
	public String getTotalPromocionesCabeceraAsString(){
        return FormatUtil.getInstance().formateaImporte(totalPromocionesCabecera);
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalAPagar()
	 */
    @Override
	public BigDecimal getTotalAPagar() {
        return totalAPagar;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#setTotalAPagar(java.math.BigDecimal)
	 */
    @Override
	public void setTotalAPagar(BigDecimal totalAPagar) {
        this.totalAPagar = totalAPagar;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getTotalAPagarAsString()
	 */
    @Override
	public String getTotalAPagarAsString(){
        return FormatUtil.getInstance().formateaImporte(totalAPagar);
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#hayPromocionesCabecera()
	 */
    @Override
	public boolean hayPromocionesCabecera(){
        return totalPromocionesCabecera.compareTo(BigDecimal.ZERO)>0;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getPuntos()
	 */
    @Override
	public int getPuntos() {
        return puntos;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#setPuntos(int)
	 */
    @Override
	public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#addPuntos(java.lang.Integer)
	 */
	@Override
	public void addPuntos(Integer puntos) {
		this.puntos += puntos;
	}
	
	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#resetPuntos()
	 */
	@Override
	public void resetPuntos(){
		this.puntos = 0;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getEntregadoACuenta()
	 */
	@Override
	public BigDecimal getEntregadoACuenta() {
		return entregadoACuenta;
	}

	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#setEntregadoACuenta(java.math.BigDecimal)
	 */
	@Override
	public void setEntregadoACuenta(BigDecimal entregadoACuenta) {
		this.entregadoACuenta = entregadoACuenta;
	}
        
        /* (non-Javadoc)
		 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#getEntregadoACuentaAsString()
		 */
        @Override
		public String getEntregadoACuentaAsString(){
            return FormatUtil.getInstance().formateaImporte(entregadoACuenta);
        }
        
        /* (non-Javadoc)
		 * @see com.comerzzia.pos.services.ticket.cabecera.ITotalesTicket#isHayEntregaCuenta()
		 */
        @Override
		public boolean isHayEntregaCuenta(){
            return (entregadoACuenta!=null && entregadoACuenta.compareTo(BigDecimal.ZERO)>0);
        }

		@Override
		public abstract C getCambio();

		@Override
		public void setCambio(IPagoTicket cambio) {
			this.cambio = (C) cambio;
			
		}


    
}
