
package com.comerzzia.pos.core.gui.stockavailability;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.ModalSceneView;

@Component
public class StockAvailabilityModalView extends ModalSceneView {

	@Override
	protected String getFXMLName() {
		return super.getFXMLName(StockAvailabilityActionView.class);
	}

}
