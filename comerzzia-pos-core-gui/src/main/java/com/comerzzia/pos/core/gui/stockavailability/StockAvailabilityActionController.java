package com.comerzzia.pos.core.gui.stockavailability;

import org.springframework.stereotype.Component;

@Component
public class StockAvailabilityActionController extends StockAvailabilityController {
	
	@Override
	public void onSceneOpen() {
		super.onSceneOpen();
		hbButtons.setDisable(true);
		hbButtons.setManaged(false);
	}

}
