package com.comerzzia.pos.gui.sales.wholesale.items.picklist;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.gui.sales.retail.items.picklist.RetailPickListView;

@Component
public class WholesalePickListView extends SceneView {
	
	protected String getFXMLName() {
		return getFXMLName(RetailPickListView.class);
	}
	
}
