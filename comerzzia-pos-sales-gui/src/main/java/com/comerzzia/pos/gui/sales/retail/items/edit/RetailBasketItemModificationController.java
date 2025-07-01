package com.comerzzia.pos.gui.sales.retail.items.edit;

import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.basket.BasketItemModificationControllerAbstract;

@Controller
@CzzScene
public class RetailBasketItemModificationController extends BasketItemModificationControllerAbstract<RetailBasketManager> {

	@Override
	protected boolean isPriceWithTaxes() {
		return true;
	}
    
}