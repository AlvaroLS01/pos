package com.comerzzia.pos.gui.sales.wholesale.scale;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.gui.sales.retail.scale.RetailAskWeightView;

@Component
public class WholesaleAskWeightView extends SceneView {

	@Override
	protected String getFXMLName() {
		return getFXMLName(RetailAskWeightView.class);
	}
}
