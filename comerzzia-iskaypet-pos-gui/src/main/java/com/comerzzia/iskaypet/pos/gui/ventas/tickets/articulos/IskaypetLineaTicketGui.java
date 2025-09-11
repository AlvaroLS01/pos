package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos;

import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteDTO;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IskaypetLineaTicketGui extends LineaTicketGui {

	protected Boolean mascota;
	protected StringProperty loteProperty;

	public IskaypetLineaTicketGui() {
		super();
	}

	public IskaypetLineaTicketGui(LineaTicketAbstract linea) {
		super(linea);

		mascota = ((IskaypetLineaTicket) linea).isMascota() && ((IskaypetLineaTicket) linea).isRequiereContrato();
		LoteDTO lote = ((IskaypetLineaTicket) linea).getLote();
		loteProperty = new SimpleStringProperty(lote==null ? "" : lote.getLote());
	}

	public Boolean getMascota() {
		return mascota;
	}

	public void setMascota(Boolean mascota) {
		this.mascota = mascota;
	}

	public StringProperty getLoteProperty() {
		return loteProperty;
	}

	public void setLoteProperty(StringProperty loteProperty) {
		this.loteProperty = loteProperty;
	}

}
