package com.comerzzia.pos.gui.sales.basket;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.conversion.BasketDocumentIssued;

public interface SaveDocumentCallback {
	void onSucceeded(BasketPromotable<?> basketTransactionCopy, BasketDocumentIssued<?> documentIssued);
	void onFailure(Exception e);
}
