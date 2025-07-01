package com.comerzzia.pos.gui.sales.basket;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.service.basket.RefundBasketManager;

public abstract class RefundPaymentsControllerAbstract<T extends RefundBasketManager<?, ?>> extends PaymentsControllerAbstract<T> {
	//TODO: AMA, reimplementar el salvado doble de documento cuando hay lineas positivas y negativas (cuando sea necesario en funcion de la naturaleza del documento)

	@Override
	protected String getWebViewPath() {
		return "sales/refund/payments/payments";
	}

	@Override
	protected void validateServiceData(BasketPromotable<?> basketTransaction) {
	}
}
