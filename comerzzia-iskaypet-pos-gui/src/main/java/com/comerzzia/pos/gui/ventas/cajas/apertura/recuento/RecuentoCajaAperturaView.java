package com.comerzzia.pos.gui.ventas.cajas.apertura.recuento;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.ModalView;

@Component
public class RecuentoCajaAperturaView extends ModalView {
	
	@Override
	protected String getFXMLName() {
		return getFXMLName(RecuentoCajaAperturaView.class);
	}

}
