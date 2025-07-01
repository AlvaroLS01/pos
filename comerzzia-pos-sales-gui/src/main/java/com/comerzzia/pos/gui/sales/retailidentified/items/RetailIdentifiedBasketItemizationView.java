package com.comerzzia.pos.gui.sales.retailidentified.items;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.sales.retail.items.RetailBasketItemizationView;

@Component
public class RetailIdentifiedBasketItemizationView extends RetailBasketItemizationView {

	@Override
	protected String getFXMLName() {
		return getFXMLName(RetailBasketItemizationView.class);
	}
}
