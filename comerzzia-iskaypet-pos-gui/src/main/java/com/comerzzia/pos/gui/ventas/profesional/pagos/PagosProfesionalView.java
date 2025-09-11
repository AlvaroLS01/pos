package com.comerzzia.pos.gui.ventas.profesional.pagos;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosView;

@Component
public class PagosProfesionalView extends PagosView {
	
	@Override
	public Object loadCustomController() {
		return new PagosController();
	}
	
	@Override
	protected String getFXMLName() {
		return getFXMLName(PagosView.class);
	}

}
