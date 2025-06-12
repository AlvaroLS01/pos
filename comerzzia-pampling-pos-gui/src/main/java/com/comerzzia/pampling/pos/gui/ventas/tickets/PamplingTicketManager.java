package com.comerzzia.pampling.pos.gui.ventas.tickets;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.services.ticket.cabecera.PamplingCabeceraTicket;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Primary
@Scope("prototype")
@SuppressWarnings("unchecked")
public class PamplingTicketManager extends TicketManager {

	@Override
	protected void redondearImportesTicket() {
		super.redondearImportesTicket();

		if (ticketOrigen != null && lineasTicketTienenLineaDocumentoOrigen()) {
			boolean devolucionCompleta = true;
			for (LineaTicket lineaOrigen : (List<LineaTicket>) ticketOrigen.getLineas()) {
				boolean lineaDevuelta = false;
				for (LineaTicket linea : (List<LineaTicket>) ticketPrincipal.getLineas()) {
					boolean esMismaLinea = linea.getLineaDocumentoOrigen().equals(lineaOrigen.getIdLinea());
					if (esMismaLinea) {
						lineaDevuelta = true;
					}

					boolean esMismaCantidad = BigDecimalUtil.isIgual(linea.getCantidad(), lineaOrigen.getCantidad());
					if (esMismaLinea && !esMismaCantidad) {
						devolucionCompleta = false;
						break;
					}
				}

				if (!devolucionCompleta) {
					break;
				}

				if (!lineaDevuelta) {
					devolucionCompleta = false;
					break;
				}
			}

			if (ticketPrincipal.getCabecera() instanceof PamplingCabeceraTicket) {
				PamplingCabeceraTicket cabecera = (PamplingCabeceraTicket) ticketPrincipal.getCabecera();
				cabecera.setDevolucionCompleta(devolucionCompleta);
			}
		}
	}

	@Override
	protected boolean tratarTicketRecuperado(byte[] ticketRecuperado) throws TicketsServiceException {
		boolean result = super.tratarTicketRecuperado(ticketRecuperado);

		if (ticketPrincipal.getCabecera() instanceof PamplingCabeceraTicket && ticketOrigen.getCabecera() instanceof PamplingCabeceraTicket) {
			PamplingCabeceraTicket cabecera = (PamplingCabeceraTicket) ticketPrincipal.getCabecera();
			PamplingCabeceraTicket cabeceraOrigen = (PamplingCabeceraTicket) ticketOrigen.getCabecera();

			cabecera.setNumReciboFiscalOrigen(cabeceraOrigen.getNumReciboFiscal());
			cabecera.setFechaReciboFiscalOrigen(cabeceraOrigen.getFechaReciboFiscal());
			cabecera.setPrinterIdOrigen(cabeceraOrigen.getPrinterId());
			cabecera.setzRepNumOrigen(cabeceraOrigen.getzRepNum());
		}

		return result;
	}

	private boolean lineasTicketTienenLineaDocumentoOrigen() {
		for (LineaTicket linea : (List<LineaTicket>) ticketPrincipal.getLineas()) {
			if (linea.getLineaDocumentoOrigen() == null) {
				return false;
			}
		}
		return true;
	}

}
