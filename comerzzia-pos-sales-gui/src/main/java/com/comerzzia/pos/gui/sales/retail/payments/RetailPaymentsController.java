package com.comerzzia.pos.gui.sales.retail.payments;

import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.basket.PaymentsControllerAbstract;

@Controller
@CzzScene
public class RetailPaymentsController extends PaymentsControllerAbstract<RetailBasketManager> {

}
