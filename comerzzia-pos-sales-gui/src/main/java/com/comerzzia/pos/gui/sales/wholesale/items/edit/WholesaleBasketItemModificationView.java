
package com.comerzzia.pos.gui.sales.wholesale.items.edit;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.sales.retail.items.edit.RetailBasketItemModificationView;

@Component
public class WholesaleBasketItemModificationView extends RetailBasketItemModificationView {

	@Override
	protected String getFXMLName() {
		return getFXMLName(RetailBasketItemModificationView.class);
	}
	
}
