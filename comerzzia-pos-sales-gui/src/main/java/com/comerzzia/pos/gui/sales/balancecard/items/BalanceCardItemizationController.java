package com.comerzzia.pos.gui.sales.balancecard.items;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.basket.NewBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.UpdateBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.service.basket.retail.balancecard.BalanceCardBasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.balancecard.payments.BalanceCardPaymentsController;
import com.comerzzia.pos.gui.sales.basket.PaymentsControllerAbstract;
import com.comerzzia.pos.gui.sales.digital.DigitalItemizationControllerAbstract;
import com.comerzzia.pos.util.format.FormatUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Component
@CzzScene
@Slf4j
public class BalanceCardItemizationController extends DigitalItemizationControllerAbstract<BalanceCardBasketManager> {
	
	@FXML
    protected Button btCancel;
	
	@FXML
	protected Button btAccept;

	@FXML
	protected TextField tfBalance;
	
	@FXML
	protected NumericKeypad numericKeypad;
	
	protected NewBasketItemRequest itemRequest;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		itemRequest = (NewBasketItemRequest) sceneData.get(PARAM_ITEM_REQUEST);
		tfBalance.setDisable(!itemRequest.getItemData().getGenericItem());
		tfBalance.setEditable(itemRequest.getItemData().getGenericItem());
		
		tfBalance.setText(FormatUtils.getInstance().formatAmount(itemRequest.getRateItem().getSalesPriceWithTaxes()));
		
	}

	@Override
	protected void initializeFocus() {
		if(tfBalance.isEditable()) {
			tfBalance.requestFocus();
		}else {
			btAccept.requestFocus();
		}
	}
	
	@Override
	protected Class<? extends PaymentsControllerAbstract<?>> getPaymentControllerClass() {
		return BalanceCardPaymentsController.class;
	}
	
	@Override
	public UpdateBasketItemRequest createUpdateBasketItemRequest() {
		if(!itemRequest.getItemData().getGenericItem()) {
			return null;
		}
		
		BigDecimal currentBalance = FormatUtils.getInstance().parseBigDecimal(tfBalance.getText(), 2);
		BasketItem item = basketManager.getBasketTransaction().getLines().get(0);
		if(item.getExtendedPriceWithTaxes().compareTo(currentBalance)==0) {
			return null;
		}
		log.debug("createBasketUpdateRequest() - Creating update basket item request");
		
		UpdateBasketItemRequest updateRequest = modelMapper.map(item, UpdateBasketItemRequest.class);
		updateRequest.setDirectPrice(currentBalance);
		return updateRequest;
	}

	@Override
	public NewBasketItemRequest createNewBasketItemRequest() {
		log.debug("createNewBasketItemRequest() - Creating new basket item request");
		NewBasketItemRequest newItemRequest = modelMapper.map(itemRequest, NewBasketItemRequest.class);
		if(newItemRequest.getItemData().getGenericItem()) {
			BigDecimal currentBalance = FormatUtils.getInstance().parseBigDecimal(tfBalance.getText(), 2);
			newItemRequest.setDirectPrice(currentBalance);
		}
		return newItemRequest;
	}

}
