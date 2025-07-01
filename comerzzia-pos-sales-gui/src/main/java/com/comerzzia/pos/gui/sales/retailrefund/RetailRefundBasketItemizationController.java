package com.comerzzia.pos.gui.sales.retailrefund;

import java.util.HashMap;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.basket.UpdateBasketItemRequest;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.service.basket.retail.refund.RetailRefundBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.basket.RefundBasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.retail.items.edit.RetailBasketItemModificationController;
import com.comerzzia.pos.gui.sales.retail.items.picklist.RetailPickListController;
import com.comerzzia.pos.gui.sales.retail.scale.RetailAskWeightController;
import com.comerzzia.pos.gui.sales.retailrefund.originalitems.OriginRefundDocumentController;
import com.comerzzia.pos.gui.sales.retailrefund.payments.RetailRefundPaymentsController;
import com.comerzzia.pos.gui.sales.retailrefund.serialnumber.RetailRefundSerialNumberController;
import com.comerzzia.pos.gui.sales.serialnumber.SerialNumberControllerAbstract;

@Component
@CzzActionScene
public class RetailRefundBasketItemizationController extends RefundBasketItemizationControllerAbstract<RetailRefundBasketManager> {

	@Override
	protected Class<? extends SceneController> getAskWeightController() {
		return RetailAskWeightController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getPaymentsController() {
		return RetailRefundPaymentsController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getBasketItemModificationController() {
		return RetailBasketItemModificationController.class;
	}

	@Override
	public Class<? extends SceneController> getOriginRefundController() {
		return OriginRefundDocumentController.class;
	}
	
	
	
	protected void assignSerialNumber(BasketItem line) {
		if (line != null && line.getItemData().getSerialNumbersActive()) {
			HashMap<String, Object> stageData = new HashMap<>();
			stageData.put(SerialNumberControllerAbstract.PARAM_LINE_DESCRIPTION, line.getItemDes());
			stageData.put(SerialNumberControllerAbstract.PARAM_REQUIRED_QUANTITY, line.getQuantity().abs().longValue());
			openModalScene(RetailRefundSerialNumberController.class, new SceneCallback<Set<String>>() {

				@Override
				public void onSuccess(Set<String> serialnumbers) {
					UpdateBasketItemRequest basketItemRequest = modelMapper.map(line, UpdateBasketItemRequest.class);
					basketItemRequest.setLineId(line.getLineId());
					basketItemRequest.setSerialNumbers(serialnumbers);
					
					basketManager.updateBasketItem(basketItemRequest);
				}
				
			}, stageData);
		}
	}

	@Override
	protected Class<? extends SceneController> getPickListController() {
		return RetailPickListController.class;
	}

	
	
	
}