package com.comerzzia.pos.gui.sales.retail.items;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.retail.items.edit.RetailBasketItemModificationController;
import com.comerzzia.pos.gui.sales.retail.items.picklist.RetailPickListController;
import com.comerzzia.pos.gui.sales.retail.payments.RetailPaymentsController;
import com.comerzzia.pos.gui.sales.retail.scale.RetailAskWeightController;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@CzzActionScene
public class RetailBasketItemizationController extends BasketItemizationControllerAbstract<RetailBasketManager> {
	
	@Override
	public boolean canClose() {
		if (!super.canClose()) return false;
		
		if (saleBasketDocumentService.count(session.getApplicationSession().getCodAlmacen(), session.getApplicationSession().getTillCode(), basketManager.getDefaultDocumentType().getDocTypeId()) > 0L) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Existen tickets aparcados. Finalice las ventas antes de cerrar la pantalla"));

			return false;
		}
		
		return true;
	}
	
	@Override
	protected Class<? extends SceneController> getAskWeightController() {
		return RetailAskWeightController.class;
	}

	
	@Override
	protected Class<? extends SceneController> getPaymentsController() {
		return RetailPaymentsController.class;
	}
	
	@Override
	protected Class<? extends SceneController> getBasketItemModificationController() {
		return RetailBasketItemModificationController.class;
	}

	@Override
	protected Class<? extends SceneController> getPickListController() {
		return RetailPickListController.class;
	}
	
}
