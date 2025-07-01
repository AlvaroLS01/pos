package com.comerzzia.pos.gui.sales.retail.items.picklist;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.item.picklist.PickListControllerAbstract;
import com.comerzzia.pos.gui.sales.retail.items.edit.RetailBasketItemModificationController;
import com.comerzzia.pos.gui.sales.retail.scale.RetailAskWeightController;

@CzzScene
@Component
public class RetailPickListController extends PickListControllerAbstract<RetailBasketManager> {

	@Override
	protected Class<? extends SceneController> getAskWeightController() {
		return RetailAskWeightController.class;
	}

	@Override
	protected Class<? extends SceneController> getPaymentsController() {
		return null;
	}

	@Override
	protected Class<? extends SceneController> getBasketItemModificationController() {
		return RetailBasketItemModificationController.class;
	}

	@Override
	protected Class<? extends SceneController> getPickListController() {
		return null;
	}

}
