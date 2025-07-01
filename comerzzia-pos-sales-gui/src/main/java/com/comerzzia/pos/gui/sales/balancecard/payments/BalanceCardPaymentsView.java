
package com.comerzzia.pos.gui.sales.balancecard.payments;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.gui.sales.retail.payments.RetailPaymentsView;

@Component
public class BalanceCardPaymentsView extends SceneView {

	protected String getFXMLName() {
        return getFXMLName(RetailPaymentsView.class);
    }
}
