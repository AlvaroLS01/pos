package com.comerzzia.pos.gui.sales.retailidentified.items;

import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.basket.IdentifiedBasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.retail.items.edit.RetailBasketItemModificationController;
import com.comerzzia.pos.gui.sales.retail.items.picklist.RetailPickListController;
import com.comerzzia.pos.gui.sales.retail.payments.RetailPaymentsController;
import com.comerzzia.pos.gui.sales.retail.scale.RetailAskWeightController;

@Controller
@CzzActionScene
public class RetailIdentifiedBasketItemizationController extends IdentifiedBasketItemizationControllerAbstract<RetailBasketManager> {
	
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
