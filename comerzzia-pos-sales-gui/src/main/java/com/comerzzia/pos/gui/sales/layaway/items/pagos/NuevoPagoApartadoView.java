
package com.comerzzia.pos.gui.sales.layaway.items.pagos;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.sales.retail.payments.RetailPaymentsView;

@Component
public class NuevoPagoApartadoView extends RetailPaymentsView{

	@Override
	protected String getFXMLName() {
		return getFXMLName(RetailPaymentsView.class);
	}
}
