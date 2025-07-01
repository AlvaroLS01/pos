package com.comerzzia.pos.gui.sales.wholesale.items;


import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.wholesale.WholesaleBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.basket.IdentifiedBasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.wholesale.items.edit.WholesaleBasketItemModificationController;
import com.comerzzia.pos.gui.sales.wholesale.items.picklist.WholesalePickListController;
import com.comerzzia.pos.gui.sales.wholesale.items.search.WholesaleItemsSearchController;
import com.comerzzia.pos.gui.sales.wholesale.payments.WholesalePaymentsController;
import com.comerzzia.pos.gui.sales.wholesale.scale.WholesaleAskWeightController;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@CzzActionScene
public class WholesaleItemizationController extends IdentifiedBasketItemizationControllerAbstract<WholesaleBasketManager> {

	@Override
	protected String getWebViewPath() {
		return "sales/wholesale/basket";
	}
			
	@Override
	protected Class<? extends SceneController> getAskWeightController() {
		return WholesaleAskWeightController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getPaymentsController() {
		return WholesalePaymentsController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getBasketItemModificationController() {
		return WholesaleBasketItemModificationController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getItemSearchController() {
		return WholesaleItemsSearchController.class;
	}

	@Override
	protected Class<? extends SceneController> getPickListController() {
		return WholesalePickListController.class;
	}

}
