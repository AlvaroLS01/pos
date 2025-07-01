package com.comerzzia.pos.gui.sales.retailidentified.customer;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.gui.sales.basket.CustomerIdentificationControllerAbstract;
import com.comerzzia.pos.gui.sales.retailidentified.items.RetailIdentifiedBasketItemizationController;

@Component
@CzzActionScene
public class CustomerIdentificationController extends CustomerIdentificationControllerAbstract{
	

	protected BasketManager<?, ?> getBasketManager() {
		return BasketManagerBuilder.build(RetailBasketManager.class, session.getApplicationSession().getStorePosBusinessData());
	}
	
	@Override
	protected Class<? extends ActionSceneController> getBasketItemizationController() {		
		return RetailIdentifiedBasketItemizationController.class;
	}
}
