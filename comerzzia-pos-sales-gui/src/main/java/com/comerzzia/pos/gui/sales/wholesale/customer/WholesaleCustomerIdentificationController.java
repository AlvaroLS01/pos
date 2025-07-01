package com.comerzzia.pos.gui.sales.wholesale.customer;

import org.springframework.stereotype.Controller;

import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.omnichannel.facade.service.basket.wholesale.WholesaleBasketManager;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.gui.sales.basket.CustomerIdentificationControllerAbstract;
import com.comerzzia.pos.gui.sales.basket.IdentifiedBasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.wholesale.items.WholesaleItemizationController;

@Controller
@CzzActionScene
public class WholesaleCustomerIdentificationController extends CustomerIdentificationControllerAbstract {
	
	@Override
	protected BasketManager<?, ?> getBasketManager() {
		return BasketManagerBuilder.build(WholesaleBasketManager.class, session.getApplicationSession().getStorePosBusinessData());
	}
	
	@Override
	protected Class<? extends ActionSceneController> getBasketItemizationController() {		
		return WholesaleItemizationController.class;
	}	
	
	@Override
	public void sell(Integer index) {
		Customer customer = this.result.get(index);
		if (customer != null) {
			auditOperation(new ComerzziaAuditEventBuilder().addOperation("sell").addField("customer", customer)
					.addField("offline", offline));
			sceneData.clear();
			sceneData.put(IdentifiedBasketItemizationControllerAbstract.PARAM_CUSTOMER, customer);
			openSalesScreen();
		}
	}
}
