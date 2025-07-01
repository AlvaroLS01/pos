package com.comerzzia.pos.gui.sales.wholesalerefund.payments;

import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.service.basket.retail.refund.RetailRefundBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.basket.RefundPaymentsControllerAbstract;

@Controller
@CzzScene
public class WholesaleRefundPaymentsController extends RefundPaymentsControllerAbstract<RetailRefundBasketManager> {
	
}
