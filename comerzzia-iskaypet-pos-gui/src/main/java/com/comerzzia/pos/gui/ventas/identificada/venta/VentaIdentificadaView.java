package com.comerzzia.pos.gui.ventas.identificada.venta;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosView;

@Component
public class VentaIdentificadaView extends FacturacionArticulosView {

	@Override
	protected String getFXMLName() {
		return getFXMLName(FacturacionArticulosView.class);
	}
	
	@Override
	public Object loadCustomController() {
		return new VentaIdentificadaController();
	}
	
}
