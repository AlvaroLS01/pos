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
package com.comerzzia.pos.services.ticket;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.pos.persistence.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
public class TicketApartado {
	
	protected Logger log = Logger.getLogger(getClass());

	protected ApartadosCabeceraBean cabecera;
	
	protected List<ApartadosDetalleBean> articulos;
	
	protected List<ApartadosPagoBean> movimientos;
	
	protected BigDecimal totalPendiente;
	
	protected BigDecimal totalServido;
	
	protected BigDecimal totalAbonado;
	
	protected BigDecimal totalPagado;
	
	protected BigDecimal importeTotal;

	public TicketApartado(){
	
		totalServido = BigDecimal.ZERO;
		totalPendiente = BigDecimal.ZERO;
		totalAbonado = BigDecimal.ZERO;
		totalPagado = BigDecimal.ZERO;
	}

	public ApartadosCabeceraBean getCabecera(){
		return cabecera;
	}
	
	public List<ApartadosDetalleBean> getArticulos(){
		return articulos;
	}
	
	public List<ApartadosPagoBean> getMovimientos(){
		return movimientos;
	}

	public void setCabecera(ApartadosCabeceraBean cabecera) {
		this.cabecera = cabecera;
	}

	public void setArticulos(List<ApartadosDetalleBean> articulos) {
		this.articulos = articulos;
	}

	public void setMovimientos(List<ApartadosPagoBean> pagos) {
		this.movimientos = pagos;
	}

	public void calcularTotales() {
		totalPendiente = BigDecimal.ZERO;
		totalServido = BigDecimal.ZERO;
		totalAbonado = BigDecimal.ZERO;
		totalPagado = BigDecimal.ZERO;
		importeTotal = BigDecimal.ZERO;
		
		//calculamos el importe de lo servido y el pendiente
		for(ApartadosDetalleBean articulo : articulos){
			if(articulo.getEstadoLineaApartado() != ApartadosCabeceraBean.ESTADO_CANCELADO){
				if(articulo.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_FINALIZADO){
					totalServido = totalServido.add(articulo.getImporteTotal());
				}
				else{
					totalPendiente = totalPendiente.add(articulo.getImporteTotal());
				}
			}
		}
		
		importeTotal = totalPendiente;
		
		//total abonado
		CajaMovimientoBean movimientoBean;				
		for(ApartadosPagoBean pago: movimientos){			
			try {
				CajasService cajasService = SpringContext.getBean(CajasService.class);
				movimientoBean = cajasService.consultarMovimientoApartado(pago.getUidDiarioCaja(), pago.getLinea(), SpringContext.getBean(Sesion.class).getAplicacion().getUidActividad());
				if(movimientoBean != null) {
					totalAbonado = totalAbonado.add(movimientoBean.getCargo());
				}
			} catch (CajasServiceException e) {
				log.warn("Error recuperando los movimientos asociados al apartado.", e);
			}			
		}
		
		totalPendiente = totalPendiente.subtract(cabecera.getSaldoCliente());
		
		if(totalPendiente.compareTo(BigDecimal.ZERO)<0){
			totalPendiente = BigDecimal.ZERO;
		}
	}
	
	public BigDecimal getImporteTotal(){
		return importeTotal;
	}
	
	public BigDecimal getTotalPagado(){
		return totalPagado;
	}
	
	public String getTotalPagadoTicket(){
		return FormatUtil.getInstance().formateaImporte(totalPagado);
	}

	public BigDecimal getTotalPendiente() {
		return totalPendiente;
	}
	
	public String getTotalPendienteTicket() {
		return FormatUtil.getInstance().formateaImporte(totalPendiente);
	}

	public BigDecimal getTotalServido() {
		return totalServido;
	}
	
	public String getServidoTicket() {
		return FormatUtil.getInstance().formateaImporte(totalServido);
	}

	public BigDecimal getTotalAbonado() {
		return totalAbonado;
	}
	
	public String getTotalAbonadoTicket(){
		return FormatUtil.getInstance().formateaImporte(totalAbonado);
	}
	
	public ApartadosDetalleBean getLineaDetalleApartado (int idLinea){
		
		for(ApartadosDetalleBean detalle: articulos){
			if(detalle.getLinea() == idLinea){
				return detalle;
			}
		}
		return null;
	}

}
