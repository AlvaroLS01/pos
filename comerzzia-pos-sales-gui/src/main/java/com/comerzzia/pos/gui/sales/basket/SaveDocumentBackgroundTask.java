package com.comerzzia.pos.gui.sales.basket;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.conversion.BasketDocumentIssued;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.pos.core.gui.BackgroundTask;

import lombok.extern.log4j.Log4j;

@Log4j
public class SaveDocumentBackgroundTask extends BackgroundTask<BasketDocumentIssued<?>> {
	protected SaveDocumentCallback callback;
	protected BasketManager<?, ?> basketManager;
	protected BasketPromotable<?> basketTransactionCopy;
	
	public SaveDocumentBackgroundTask(BasketManager<?, ?> basketManager, SaveDocumentCallback callback) {
		super(true);
		this.basketManager = basketManager;
		this.callback = callback;
		this.basketTransactionCopy = basketManager.getBasketTransaction();
    }


	@Override
	protected BasketDocumentIssued<?> execute() throws Exception {
		return basketManager.convertBasketToDocument();
	}

	@Override
	protected void succeeded() {
		super.succeeded();
						
		callback.onSucceeded(basketTransactionCopy, getValue());
	}

	@Override
	protected void failed() {
		super.failed();
		Exception ex = null;
		//TODO: Provitional change to avoid casting errors
		if (getException() instanceof Exception) {
			ex = (Exception) getException();
		} else {
			ex = new Exception(getException());
		}
		
		log.error(ex.getMessage(), ex);
		            
		callback.onFailure(ex);

	}
}
