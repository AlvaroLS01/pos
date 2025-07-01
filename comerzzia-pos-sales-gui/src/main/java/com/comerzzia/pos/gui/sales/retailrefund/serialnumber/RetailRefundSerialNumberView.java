package com.comerzzia.pos.gui.sales.retailrefund.serialnumber;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.gui.sales.retail.items.serialnumbers.RetailSerialNumberView;

@Component
public class RetailRefundSerialNumberView extends SceneView {

	@Override
	protected String getFXMLName() {
        return getFXMLName(RetailSerialNumberView.class);
    }
}
