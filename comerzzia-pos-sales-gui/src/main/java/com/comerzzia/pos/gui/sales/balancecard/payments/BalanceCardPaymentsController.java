package com.comerzzia.pos.gui.sales.balancecard.payments;

import java.math.BigDecimal;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.balancecard.BasketBalanceCardOperationData;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketBalanceCard;
import com.comerzzia.omnichannel.facade.service.basket.retail.balancecard.BalanceCardBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.htmlparser.LongOperationTask;
import com.comerzzia.pos.core.services.balancecard.BalanceCardService;
import com.comerzzia.pos.gui.sales.balancecard.search.BalanceCardSearchController;
import com.comerzzia.pos.gui.sales.digital.DigitalItemPaymentsControllerAbstract;

import javafx.scene.web.WebView;

@Component
@CzzScene
public class BalanceCardPaymentsController extends DigitalItemPaymentsControllerAbstract<BalanceCardBasketManager> {
	
	@Autowired
	protected BalanceCardService balanceCardService;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	@Override
	protected Class<? extends SceneController> getAdditionalDataScreen() {
		return BalanceCardSearchController.class;
	}
	
	@Override
	protected SceneCallback<BasketBalanceCard> getAdditionalDataCallback() {
		return new SceneCallback<BasketBalanceCard>() {
			
			@Override
			public void onSuccess(BasketBalanceCard balanceCard) {
				BasketBalanceCardOperationData balanceCardData = modelMapper.map(balanceCard, BasketBalanceCardOperationData.class);
				balanceCardData.setRechargeAmount(BalanceCardPaymentsController.this.basketManager.getBasketTransaction().getTotals().getTotalWithTaxes());
				new BackgroundRechargeBalanceCard(BalanceCardPaymentsController.this,balanceCardData).start();
			}
			
			@Override
			public void onCancel() {
				btAccept.setDisable(false);
				refreshScreenData();
			}
		};
	}
	
	@Override
	protected void succededLongOperations() {
		super.succededLongOperations();
		refreshScreenData();
	}
	
	protected class BackgroundRechargeBalanceCard extends LongOperationTask<Void>{
		
		BasketBalanceCardOperationData balanceCardData;

		public BackgroundRechargeBalanceCard(SceneController sceneController, BasketBalanceCardOperationData balanceCardData) {
			super(sceneController, true);
			this.balanceCardData = balanceCardData;
		}

		@Override
		protected Void execute() throws Exception {
			String transactionId = UUID.randomUUID().toString();
			BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
			if(basketTransaction.getTotals().getTotalWithTaxes().compareTo(BigDecimal.ZERO)>0) {
				balanceCardService.recharge(transactionId, balanceCardData.getCardCode(), basketTransaction);
			}else {
				balanceCardService.returnBalance(transactionId, balanceCardData.getCardCode(), basketTransaction);
			}
			balanceCardData.setTransactionUid(transactionId);
			basketManager.addBalanceCardOperationData(balanceCardData);
			return null;
		}
		
		@Override
		protected void taskSucceded() {
			super.taskSucceded();
			saveDocument();
		}
		
		@Override
		protected void taskFailed() {
			btAccept.setDisable(false);
			refreshScreenData();
			super.taskFailed();
		}
		
	}
	
	@Override
	protected void saveDocumentFailed(Exception e) {
		btAccept.setDisable(false);
		refreshScreenData();
		super.saveDocumentFailed(e);
	}
	
	@Override
	public WebView getWebView() {
		return wvPrincipal;
	}
	
}
