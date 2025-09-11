/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.gui.ventas.tickets.pagos;

import java.math.BigDecimal;

import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;

public class PagoTicketGui {

	private final String codMedioPago;
	private final String formaPago;
	private final BigDecimal importe;
	private final Integer paymentId;
	private final boolean removable;

	public PagoTicketGui(IPagoTicket pago) {
		codMedioPago = pago.getMedioPago().getCodMedioPago();
		formaPago = pago.getMedioPago().getDesMedioPago();
		importe = pago.getImporte();
		paymentId = pago.getPaymentId();
		removable = pago.isEliminable();
	}

	public String getFormaPago() {
		return formaPago;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public String getCodMedioPago() {
		return codMedioPago;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public boolean isRemovable() {
		return removable;
	}

}
