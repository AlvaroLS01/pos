package com.comerzzia.pos.gui.sales.wholesale.scale;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.wholesale.WholesaleBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.scale.askweight.AskWeightControllerAbstract;

@Component
@CzzScene
public class WholesaleAskWeightController extends AskWeightControllerAbstract<WholesaleBasketManager> {

}
