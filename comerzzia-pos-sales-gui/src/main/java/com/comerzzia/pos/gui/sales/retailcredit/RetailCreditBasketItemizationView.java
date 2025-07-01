
package com.comerzzia.pos.gui.sales.retailcredit;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.sales.retail.items.RetailBasketItemizationView;

@Component
public class RetailCreditBasketItemizationView extends RetailBasketItemizationView {

	@Override
	protected String getFXMLName() {
		return getFXMLName(RetailBasketItemizationView.class);
	}
	
}
