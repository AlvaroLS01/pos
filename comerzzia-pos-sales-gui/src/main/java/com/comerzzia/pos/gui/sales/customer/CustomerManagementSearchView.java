
package com.comerzzia.pos.gui.sales.customer;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;

@Component
public class CustomerManagementSearchView extends SceneView {

	@Override
	protected String getFXMLName() {
		return getFXMLName(CustomerManagementSearchView.class);
	}
	
}
