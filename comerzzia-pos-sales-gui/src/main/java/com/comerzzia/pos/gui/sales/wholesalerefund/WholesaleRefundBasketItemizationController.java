package com.comerzzia.pos.gui.sales.wholesalerefund;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.wholesale.refund.WholesaleRefundBasketManager;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.basket.RefundBasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.retailrefund.originalitems.OriginRefundDocumentController;
import com.comerzzia.pos.gui.sales.wholesale.items.edit.WholesaleBasketItemModificationController;
import com.comerzzia.pos.gui.sales.wholesale.items.picklist.WholesalePickListController;
import com.comerzzia.pos.gui.sales.wholesale.scale.WholesaleAskWeightController;
import com.comerzzia.pos.gui.sales.wholesalerefund.payments.WholesaleRefundPaymentsController;

@Component
@CzzActionScene
public class WholesaleRefundBasketItemizationController extends RefundBasketItemizationControllerAbstract<WholesaleRefundBasketManager> {

	@Override
	protected Class<? extends SceneController> getAskWeightController() {
		return WholesaleAskWeightController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getPaymentsController() {
		return WholesaleRefundPaymentsController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getBasketItemModificationController() {
		return WholesaleBasketItemModificationController.class;
	}

	@Override
	public Class<? extends SceneController> getOriginRefundController() {
		return OriginRefundDocumentController.class;
	}

	@Override
	protected Class<? extends SceneController> getPickListController() {
		return WholesalePickListController.class;
	}
	
}