package com.comerzzia.pos.gui.sales.retailcredit;


import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.retail.credit.RetailCreditBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.retail.items.edit.RetailBasketItemModificationController;
import com.comerzzia.pos.gui.sales.retail.items.picklist.RetailPickListController;
import com.comerzzia.pos.gui.sales.retail.payments.RetailPaymentsController;
import com.comerzzia.pos.gui.sales.retail.scale.RetailAskWeightController;

import lombok.extern.log4j.Log4j;

@Component
@CzzActionScene
@Log4j
public class RetailCreditBasketItemizationController extends BasketItemizationControllerAbstract<RetailCreditBasketManager> {
	@Override
	protected Class<? extends SceneController> getAskWeightController() {
		return RetailAskWeightController.class;
	}

	@Override
	protected Class<? extends SceneController> getPaymentsController() {
		return RetailPaymentsController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getBasketItemModificationController() {
		return RetailBasketItemModificationController.class;
	}

	@Override
	protected Class<? extends SceneController> getPickListController() {
		return RetailPickListController.class;
	}
    
}
