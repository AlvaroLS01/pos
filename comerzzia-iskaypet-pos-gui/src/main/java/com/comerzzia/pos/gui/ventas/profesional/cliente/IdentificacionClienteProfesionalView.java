package com.comerzzia.pos.gui.ventas.profesional.cliente;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.ventas.identificada.cliente.IdentificacionClienteView;

@Component
public class IdentificacionClienteProfesionalView extends IdentificacionClienteView {
	
	@Override
	public Object loadCustomController() {
		return new IdentificacionClienteProfesionalController();
	}

	@Override
	protected String getFXMLName() {
		return getFXMLName(IdentificacionClienteView.class);
	}
	
}
