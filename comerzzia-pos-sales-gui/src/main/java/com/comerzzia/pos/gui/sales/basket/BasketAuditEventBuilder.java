package com.comerzzia.pos.gui.sales.basket;

import org.apache.commons.lang.StringUtils;

import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;

public class BasketAuditEventBuilder extends ComerzziaAuditEventBuilder {
	public BasketAuditEventBuilder(BasketPromotable<?> basketTransaction) {
		if (basketTransaction == null) return;
		
		if (basketTransaction.getHeader().getLoyalCustomer() != null) {
			if (basketTransaction.getHeader().getLoyalCustomer().getLyCustomerId() != null) {
				insertField("lyCustomer", basketTransaction.getHeader().getLoyalCustomer().getLyCustomerId());
			}
	        if(StringUtils.isNotBlank(basketTransaction.getHeader().getLoyalCustomer().getCardNumber())) {
	        	insertField("lyCustomerCard", basketTransaction.getHeader().getLoyalCustomer().getCardNumber());
	        }
	            
		}
		
		insertField("cashier", basketTransaction.getHeader().getCashier().getUserCode())
		     .insertField("totalItems", basketTransaction.getTotalItems())
	         .insertField("totalAmount", basketTransaction.getTotals().getTotalWithTaxes())
	         .insertField("basketUid", basketTransaction.getBasketUid());		
		
	}
}
