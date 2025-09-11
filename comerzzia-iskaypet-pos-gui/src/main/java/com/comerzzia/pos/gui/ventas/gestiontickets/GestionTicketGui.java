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


package com.comerzzia.pos.gui.ventas.gestiontickets;

import java.util.Date;

import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;


public class GestionTicketGui {
    
    // Datos que se muestran
    protected String caja;
    protected Date fecha;
    protected long idTicket;
    protected String desDoc;
    
    // Datos que vamos a usar para pasarselos al detalle
    protected String uidTicket;
    
    protected byte[] ticketXML;
    
    public GestionTicketGui(TicketBean ticket){
        
        this.caja = ticket.getCodcaja();
        this.fecha =  ticket.getFecha();
        this.idTicket = ticket.getIdTicket();
        this.uidTicket = ticket.getUidTicket();
        this.ticketXML = ticket.getTicket();
        
        try {
            TipoDocumentoBean documento = SpringContext.getBean(Sesion.class).getAplicacion().getDocumentos().getDocumento(ticket.getIdTipoDocumento());
            this.desDoc = documento.getDestipodocumento();
        }
        catch (DocumentoException ex) {
            this.desDoc = "";
            ex.printStackTrace();
        }
    }
    
    /**
     * @return the caja
     */
    public String getCaja() {
        return caja;
    }

    /**
     * @param caja the caja to set
     */
    public void setCaja(String caja) {
        this.caja = caja;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the idTicket
     */
    public long getIdTicket() {
        return idTicket;
    }

    /**
     * @param idTicket the idTicket to set
     */
    public void setIdTicket(long idTicket) {
        this.idTicket = idTicket;
    }

    public String getUidTicket() {
        return uidTicket;
    }

    public void setUidTicket(String uidTicket) {
        this.uidTicket = uidTicket;
    }

    public String getDesDoc() {
        return desDoc;
    }

    public void setDesDoc(String desDoc) {
        this.desDoc = desDoc;
    }

	public byte[] getTicketXML() {
		return ticketXML;
	}

    
}
