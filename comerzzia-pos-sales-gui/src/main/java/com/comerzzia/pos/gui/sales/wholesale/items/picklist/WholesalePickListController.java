package com.comerzzia.pos.gui.sales.wholesale.items.picklist;

import com.comerzzia.omnichannel.facade.service.basket.wholesale.WholesaleBasketManager;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.item.picklist.PickListControllerAbstract;
import com.comerzzia.pos.gui.sales.wholesale.items.edit.WholesaleBasketItemModificationController;
import com.comerzzia.pos.gui.sales.wholesale.scale.WholesaleAskWeightController;

public class WholesalePickListController extends PickListControllerAbstract<WholesaleBasketManager> {

	@Override
	protected Class<? extends SceneController> getAskWeightController() {
		return WholesaleAskWeightController.class;
	}

	@Override
	protected Class<? extends SceneController> getPaymentsController() {
		return null;
	}

	@Override
	protected Class<? extends SceneController> getBasketItemModificationController() {
		return WholesaleBasketItemModificationController.class;
	}

	@Override
	protected Class<? extends SceneController> getPickListController() {
		return null;
	}

}
