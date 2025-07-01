package com.comerzzia.pos.gui.sales.cashjournal.opening.cashcount;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.gui.sales.cashjournal.counts.CashCountView;

@Component
public class OpeningCashCountView extends SceneView {
	
	@Override
	protected String getFXMLName() {
		return getFXMLName(CashCountView.class);
	}

}
