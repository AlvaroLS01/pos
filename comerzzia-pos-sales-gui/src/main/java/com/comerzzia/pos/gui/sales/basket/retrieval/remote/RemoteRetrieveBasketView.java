package com.comerzzia.pos.gui.sales.basket.retrieval.remote;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.gui.sales.basket.retrieval.RetrieveBasketView;

@Component
public class RemoteRetrieveBasketView extends SceneView{

	@Override
	protected String getFXMLName() {
		return getFXMLName(RetrieveBasketView.class);
	}
}
