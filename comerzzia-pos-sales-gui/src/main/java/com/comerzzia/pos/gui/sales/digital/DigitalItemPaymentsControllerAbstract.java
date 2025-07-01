package com.comerzzia.pos.gui.sales.digital;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.conversion.BasketDocumentIssued;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.model.documents.PrintDocumentRequest;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.basket.PaymentsControllerAbstract;
import com.comerzzia.pos.gui.sales.basket.SaveDocumentBackgroundTask;
import com.comerzzia.pos.gui.sales.basket.SaveDocumentCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DigitalItemPaymentsControllerAbstract<T extends BasketManager<?, ?>> extends PaymentsControllerAbstract<T> {
	
	protected void acceptPayments() {
		// evitar dobles pulsaciones
		btAccept.setDisable(true);
				
		log.debug("acceptPayments() - Pagos cubiertos");
		
		// assign cash journal data
		basketManager.updateCashJournalData(session.getCashJournalSession().getCashJournalUid(), session.getCashJournalSession().getAccountingDate());
		
		// update document print settings
		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
		BasketLoyalCustomer datosFidelizado = basketTransaction.getHeader().getLoyalCustomer();
						
		PrintDocumentRequest printSettings = printService.getDefaultPrintSettings(basketTransaction.getHeader().getDocTypeCode());
		printSettings.getCustomParams().put("paperLess", datosFidelizado != null && datosFidelizado.getPaperLess() != null && datosFidelizado.getPaperLess());
		
		basketManager.updatePrintSettings(printSettings);		

		Class<? extends SceneController> additionalDataScreen = getAdditionalDataScreen();
		
		if(additionalDataScreen != null) {
			openScene(additionalDataScreen, getAdditionalDataCallback());
		}else {
			saveDocument();
		}
		
	}
	
	protected void saveDocument() {
		new SaveDocumentBackgroundTask(basketManager, new SaveDocumentCallback() {	
			@Override
			public void onSucceeded(BasketPromotable<?> basketTransaction, BasketDocumentIssued<?> documentIssued) {
				saveDocumentSucceeded(basketTransaction, documentIssued);				
			}
			
			@Override
			public void onFailure(Exception e) {
				saveDocumentFailed(e);
			}			
		}).start();
	}
	
	protected Class<? extends SceneController> getAdditionalDataScreen(){
		return null;
	}
	
	protected SceneCallback<?> getAdditionalDataCallback() {
		return new SceneCallback<Void>() {
			@Override
			public void onSuccess(Void callbackData) {
				saveDocument();
			}
		};
	}
}
