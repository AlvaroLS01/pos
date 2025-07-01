package com.comerzzia.pos.gui.sales.wholesale.payments;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.service.basket.wholesale.WholesaleBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.basket.PaymentsControllerAbstract;

@Controller
@CzzScene
public class WholesalePaymentsController extends PaymentsControllerAbstract<WholesaleBasketManager> {
	
	@Override
	protected void loadWebViewPrincipal(BasketPromotable<?> basketTransaction) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("basketTransaction", basketTransaction);
		
		loadWebView("sales/wholesale/payments/payments", params, wvPrincipal);
	}

}
