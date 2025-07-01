package com.comerzzia.pos.gui.sales.wholesale.items.edit;

import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.service.basket.wholesale.WholesaleBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.basket.BasketItemModificationControllerAbstract;

@Controller
@CzzScene
public class WholesaleBasketItemModificationController extends BasketItemModificationControllerAbstract<WholesaleBasketManager>{

	@Override
	protected boolean isPriceWithTaxes() {
		return false;
	}
	
}
