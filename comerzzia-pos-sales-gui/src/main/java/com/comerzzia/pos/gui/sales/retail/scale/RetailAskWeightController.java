package com.comerzzia.pos.gui.sales.retail.scale;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.scale.askweight.AskWeightControllerAbstract;

@Component
@CzzScene
public class RetailAskWeightController extends AskWeightControllerAbstract<RetailBasketManager> {

}
