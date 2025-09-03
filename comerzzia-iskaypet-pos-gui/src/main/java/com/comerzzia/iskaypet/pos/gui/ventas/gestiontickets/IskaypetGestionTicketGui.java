package com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.persistence.ticket.IskaypetTicketBean;
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionTicketGui;
import com.comerzzia.pos.persistence.tickets.TicketBean;

@Primary
@Component
public class IskaypetGestionTicketGui extends GestionTicketGui {
	
	protected String locatorId;
	protected String codalm;
	protected String tienda;
	protected String tipoDoc;
	protected Long idTipoDoc;
	protected String contrato;

	public IskaypetGestionTicketGui(TicketBean ticket) {
		super(ticket);
		if(ticket instanceof IskaypetTicketBean) {
			setContrato(((IskaypetTicketBean)ticket).getContrato());
		}
	}
	
	public String getLocatorId() {
		return locatorId;
	}
	
	public void setLocatorId(String locatorId) {
		this.locatorId = locatorId;
	}

	public String getCodalm() {
		return codalm;
	}

	public void setCodalm(String codalm) {
		this.codalm = codalm;
	}

	public String getTienda() {
		return tienda;
	}

	public void setTienda(String tienda) {
		this.tienda = tienda;
	}
	
	public String getTipoDoc() {
		return tipoDoc;
	}
	
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}
	
	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public String getContrato() {
		return contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
	
	
	
}
