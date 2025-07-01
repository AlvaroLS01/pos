package com.comerzzia.pos.gui.sales.retail.items.search;

import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.item.ItemSearchControllerAbstract;

@Controller
@CzzScene
public class ItemSearchController extends ItemSearchControllerAbstract<RetailBasketManager> {

}
