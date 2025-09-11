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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.impuestos.porcentajes.PorcentajeImpuestoBean;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.ticket.cabecera.ComparadorSubtotalesIvaTicketPorcentaje;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.promociones.IPromocionTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.config.SpringContext;


@XmlAccessorType(XmlAccessType.NONE)
public abstract class TicketVenta<L extends LineaTicket, P extends IPagoTicket, M extends IPromocionTicket> extends Ticket{
    
    protected List<P> pagos;
    
    protected List<M> promociones;
    
    protected List<CuponEmitidoTicket> cuponesEmitidos;

    protected List<CuponAplicadoTicket> cuponesAplicados;
    
    protected boolean esDevolucion;
    
    public TicketVenta(){
        super();
        pagos = new ArrayList<>();
        promociones = new ArrayList<>();
        cuponesEmitidos = new ArrayList<>();
        cuponesAplicados = new ArrayList<>();
    }
    
    @Override
    public LineaTicketAbstract getLinea(Integer idLinea){
        for (LineaTicket linea : getLineas()) {
            if (linea.getIdLinea().equals(idLinea)){
                return linea;
            }
        }
        return null;
    }
    
    public void recalcularSubtotalesIva(){
        // Construimos mapa con subtotales recorriendo todas las líneas del ticket
        Map<String, SubtotalIvaTicket> subtotales = new HashMap<>();
        for (LineaTicket linea : getLineas()) {
        	linea.recalcularImporteFinal();
            String codImpuesto = linea.getArticulo().getCodImpuesto();
            SubtotalIvaTicket subtotal = subtotales.get(codImpuesto);
            if (subtotal == null){
                subtotal = new SubtotalIvaTicket();
                PorcentajeImpuestoBean porcentajeImpuesto = SpringContext.getBean(Sesion.class).getImpuestos().getPorcentaje(getCliente().getIdTratImpuestos(), codImpuesto);
                subtotal.setPorcentajeImpuestoBean(porcentajeImpuesto);
                subtotales.put(codImpuesto, subtotal);
            }
            subtotal.addLinea(linea);
        }
        
        // Añadimos cada subtotal a la lista de la cabecera del ticket
        getCabecera().getSubtotalesIva().clear();
        BigDecimal impuestosTotal = BigDecimal.ZERO;
        BigDecimal baseTotal = BigDecimal.ZERO;
        for (SubtotalIvaTicket subtotal : subtotales.values()) {
            // Recalculamos cada subtotal (impuestos, cuotas y totales)
            subtotal.recalcular();
            getCabecera().getSubtotalesIva().add(subtotal);
            impuestosTotal = impuestosTotal.add(subtotal.getImpuestos());
            baseTotal = baseTotal.add(subtotal.getBase());
        }
        getTotales().setImpuestos(impuestosTotal);
        getTotales().setBase(baseTotal);
        
        ComparadorSubtotalesIvaTicketPorcentaje comparador = new ComparadorSubtotalesIvaTicketPorcentaje();
        Collections.sort(getCabecera().getSubtotalesIva(),comparador);
    }
    
    public void addLinea(L linea) {
    	getLineas().add(linea);
    }

    @Override
	public String getCodTarifa() {
		ClienteBean cliente = getCabecera().getCliente();
		if (cliente != null && cliente.getCodtar() != null) {
			return cliente.getCodtar();
		}
		
		Tienda tienda = getCabecera().getTienda();
		ClienteBean clienteTienda = tienda.getCliente();
		if (clienteTienda != null && clienteTienda.getCodtar() != null){
			return clienteTienda.getCodtar();
		} else {
			return ArticulosTarifaService.COD_TARIFA_GENERAL;
		}
	}
    
    public abstract List<P> getPagos();
    
    public abstract void addPago(P pago);
    
    public IPagoTicket getPago(String codMedioPago) {
        for (IPagoTicket pagoTicket : pagos) {
            if (pagoTicket.getMedioPago().getCodMedioPago().equals(codMedioPago)) {
                return pagoTicket;
            }
        }
        return null;
    }
    
    /**
     * Elimina un pago de la lista de pagos
     *
     * @param codMedioPago
     */
    public void removePago(String codMedioPago) {   
        for (IPagoTicket pagoTicket : pagos) {
            if (pagoTicket.getMedioPago().getCodMedioPago().equals(codMedioPago)) {
                pagos.remove(pagoTicket);
                return;
            }
        }
    }
        
    /**
     * Comprueba que el total pendiente de pagar del ticket es 0
     * @return
     */
    public boolean isPagosCubiertos(){
        return (getTotales().getPendiente()!=null && getTotales().getPendiente().compareTo(BigDecimal.ZERO) == 0);
    }
    
    @Override
    public abstract List<L> getLineas();
       
    
    public void setEsDevolucion(boolean esDevolucion){
        this.esDevolucion = esDevolucion;
    }

    @Override
    public boolean isEsDevolucion() {
    	return esDevolucion;
    }
    
    public Long getIdTratImpuestos(){
        Sesion sesion = SpringContext.getBean(Sesion.class);
        return sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos();
    }
    

    @Override
    public abstract List<M> getPromociones();
    
    public abstract List<LineaTicket> getLinea(String codigo);
    
    public abstract void setCuponesEmitidos(List<CuponEmitidoTicket> cuponesEmitidos);

    public abstract void setCuponesAplicados(List<CuponAplicadoTicket> cuponesAplicados);
    
    public abstract void aplicarPromocion(TicketVenta ticket, LineasAplicablesPromoBean lineasCondicion, LineasAplicablesPromoBean lineasAplicables);
    
    public abstract PromocionTicket getPromocion(Long idPromocion);
    
    public abstract void addPromocion(PromocionTicket promocion);

    public abstract List<CuponEmitidoTicket> getCuponesEmitidos();
    
    public abstract void addCuponEmitido(CuponEmitidoTicket cupon);

    public abstract List<CuponAplicadoTicket> getCuponesAplicados();
  
    public abstract void addCuponAplicado(CuponAplicadoTicket cupon);
    
    public abstract void inicializarTotales();	

	public abstract void setPromociones(List promociones);
	
	public abstract CuponAplicadoTicket getCuponAplicado(String codigo);
    

}
