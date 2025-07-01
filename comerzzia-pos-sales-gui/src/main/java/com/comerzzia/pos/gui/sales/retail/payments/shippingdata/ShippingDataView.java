
package com.comerzzia.pos.gui.sales.retail.payments.shippingdata;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.gui.sales.retail.payments.customerdata.ChangeCustomerDataView;

@Component
public class ShippingDataView extends SceneView{

	@Override
	protected String getFXMLName() {
		return getFXMLName(ChangeCustomerDataView.class);
	}
	
}
