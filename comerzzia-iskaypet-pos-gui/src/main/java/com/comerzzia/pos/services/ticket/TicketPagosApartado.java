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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.TotalesTicketPagosApartado;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.promociones.IPromocionTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.config.SpringContext;

@Component
@Scope("prototype")
public class TicketPagosApartado extends TicketVenta{
	
	@XmlElement(name="cabecera")
    protected CabeceraTicket cabecera;
	
	@XmlElementWrapper(name = "lineas")
	@XmlElement(name = "linea")
	protected List<LineaTicket> lineas;
	    
	@XmlElementWrapper(name = "pagos")
	@XmlElement(name = "pago")
	protected List<PagoTicket> pagos;
	    
	@XmlElementWrapper(name = "promociones")
	@XmlElement(name = "promocion")
	protected List<PromocionTicket> promociones;	

	public TicketPagosApartado(){
		super();
		cabecera = new CabeceraTicket();
		pagos = new ArrayList<>();
	}

	@Override
	public List getLineas() {
		return new ArrayList();
	}

	@Override
	public void setLineas(List lineas) {
	}
	
	public void inicializarTotales(){
    	getCabecera().setTotales(SpringContext.getBean(TotalesTicketPagosApartado.class, this));
    }

	@Override
	public void setPagos(List pagos) {
		this.pagos = pagos;		
	}

	@Override
	public List getPagos() {
		return pagos;
	}

	@Override
	public void addPago(IPagoTicket pago) {
		pagos.add((PagoTicket) pago);	
	}

	@Override
	public CabeceraTicket getCabecera() {
		return cabecera;
	}
	
	@Override
	public List<PromocionTicket> getPromociones() {
		return this.promociones;
	}

	@Override
	public void setPromociones(List promociones) {
		this.promociones = promociones;
		
	}
	
	public void addPromocion(IPromocionTicket promocion) {
		this.addPromocion((PromocionTicket)promocion);
		
	}

	@Override
	public void setCuponesEmitidos(List cuponesEmitidos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCuponesAplicados(List cuponesAplicados) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aplicarPromocion(TicketVenta ticket,
			LineasAplicablesPromoBean lineasCondicion,
			LineasAplicablesPromoBean lineasAplicables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PromocionTicket getPromocion(Long idPromocion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPromocion(PromocionTicket promocion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List getCuponesEmitidos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCuponEmitido(CuponEmitidoTicket cupon) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List getCuponesAplicados() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCuponAplicado(CuponAplicadoTicket cupon) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CuponAplicadoTicket getCuponAplicado(String codigo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getLinea(String codigo) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public void addPago(PagoTicket pago) {
        pagos.add(pago);
    }
    
    public IPagoTicket getPago(String codMedioPago) {
        for (IPagoTicket pagoTicket : pagos) {
            if (pagoTicket.getMedioPago().getCodMedioPago().equals(codMedioPago)) {
                return pagoTicket;
            }
        }
        return null;
    }
	
}
