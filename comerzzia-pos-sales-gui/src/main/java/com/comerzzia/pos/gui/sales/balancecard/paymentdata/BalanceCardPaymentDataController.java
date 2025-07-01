package com.comerzzia.pos.gui.sales.balancecard.paymentdata;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketTenderRequest;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.gui.sales.balancecard.BalanceCardControllerAbstract;
import com.comerzzia.pos.gui.sales.basket.PaymentsControllerAbstract;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class BalanceCardPaymentDataController extends BalanceCardControllerAbstract {
	
	public static final String PARAM_PENDING_PAYMENT = "PENDING_PAYMENT";
	
	@FXML
    protected Button btCancel;
    
    @FXML
    protected HBox hbAmount;
	
	protected BigDecimal actualAmount;
	protected BasketTenderRequest basketTenderRequest;
	protected BasketPromotable<?> basketTransaction;
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		basketTenderRequest = (BasketTenderRequest) sceneData.get(PaymentsControllerAbstract.PAYMENT_GUI_BASKET_TENDER_REQUEST);
		basketTransaction = ((BasketManager<?, ?>) sceneData.get(PaymentsControllerAbstract.PAYMENT_GUI_BASKET_MANAGER)).getBasketTransaction();
		actualAmount = basketTenderRequest.getAmount();
		
		tfAmount.setDisable(true);
		super.onSceneOpen();
	}
	
	@Override
	protected void showCardData() {
		super.showCardData();
		boolean showData = balanceCard != null;
		btAccept.setDisable(!showData);
		tfAmount.setText("");

		if(showData) {
			BigDecimal balance = balanceCard.getBalance();
			if(actualAmount.compareTo(balance)>0) {
				actualAmount = balance;
			} else if(balance.compareTo(BigDecimal.ZERO)<0) {
				actualAmount = BigDecimal.ZERO;
			}
			
			tfAmount.setText(FormatUtils.getInstance().formatNumber(actualAmount.abs(), 2));
		}
	}
	
	@Override
	public void actionAccept() {
		if(balanceCard==null) {
			return;
		}
		if(balanceCard.getBalance().compareTo(BigDecimal.ZERO)<=0) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("La tarjeta no contiene saldo"));
			return;
		}
		if(balanceCard.getBalance().compareTo(actualAmount)<0) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("El importe de pago no puede superar el saldo de la tarjeta"));
			return;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			basketTenderRequest.getData().put("PARAM_CARD", objectMapper.writeValueAsString(balanceCard));
			basketTenderRequest.setAmount(actualAmount);
			basketTenderRequest.setDescription(basketTransaction.getHeader().getDocTypeDes()+ " " + basketTransaction.getBasketCode());
			
			closeSuccess(basketTenderRequest);
		} catch (Exception e) {
			log.error("actionAccept() - Error creating balancecard payment", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha ocurrido un error creando la petición de pago."));
		} 
	}

}
