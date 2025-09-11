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
package com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets;

import java.util.Date;

import org.apache.log4j.Logger;

import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;


public class LineaTicketAparcadoGui {
	
	private static Logger log = Logger.getLogger(LineaTicketAparcadoGui.class);
    
    private Date fecha;
    
    private String importe;
    
    private String cliente;
    
    private String cajero;
    
    private TicketAparcadoBean ticket;
    
    private String caja;
    
    private String codDoc;
    
    public LineaTicketAparcadoGui(TicketAparcadoBean ticket){
        
        this.importe = FormatUtil.getInstance().formateaNumero(ticket.getImporte(),2);
        this.cajero = ticket.getUsuario();
        this.cliente = ticket.getCodCliente();
        this.fecha = ticket.getFecha();
        this.ticket = ticket;
        this.caja = ticket.getCodCaja();
        try {
			this.codDoc = SpringContext.getBean(Sesion.class).getAplicacion().getDocumentos().getDocumento(ticket.getIdTipoDocumento()).getCodtipodocumento();
		} catch (DocumentoException e) {
			log.error("LineaTicketAparcadoGui() - Excepción capturada" + e.getMessage(), e);
		}
    }

    public Date getFecha() {
        return fecha;
    }

    public String getImporte() {
        return importe;
    }

    public String getCliente() {
        return cliente;
    }

    public String getCajero() {
        return cajero;
    }
    
    public TicketAparcadoBean getTicket() {
        return ticket;
    }

    public String getCaja() {
        return caja;
    }

	public String getCodDoc() {
		return codDoc;
	}
        
}
