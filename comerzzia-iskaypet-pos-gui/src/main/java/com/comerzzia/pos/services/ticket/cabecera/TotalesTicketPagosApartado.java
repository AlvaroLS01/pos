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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
public class TotalesTicketPagosApartado extends TotalesTicket{
	
	public TotalesTicketPagosApartado(){
	}
	
	public TotalesTicketPagosApartado(ITicket ticket){
		super(ticket);
	}
	
	public void recalcular(){
		entregado = BigDecimal.ZERO;

        // Calculamos entregado sumando todos los pagos
        for (Object pagoTicket : ((TicketVenta)ticket).getPagos()) {
            entregado = entregado.add(((IPagoTicket)pagoTicket).getImporte());
        }
        
        BigDecimal totalEntregado = entregado.add(entregadoACuenta);
		if(BigDecimalUtil.isMenorACero(totalAPagar)){
        	if(BigDecimalUtil.isMayor(totalEntregado, totalAPagar)){
        		pendiente = totalEntregado.subtract(totalAPagar);
        		cambio.setImporte(BigDecimal.ZERO);
        	}else{
        		pendiente = BigDecimal.ZERO;
        		cambio.setImporte(totalEntregado.subtract(totalAPagar));
        	}
        }else{
        	if(BigDecimalUtil.isMenor(totalEntregado, totalAPagar)){
        		pendiente = totalAPagar.subtract(totalEntregado);
        		cambio.setImporte(BigDecimal.ZERO);
        	}else{
        		pendiente = BigDecimal.ZERO;
        		cambio.setImporte(totalEntregado.subtract(totalAPagar));
        	}
        }
    }
}
