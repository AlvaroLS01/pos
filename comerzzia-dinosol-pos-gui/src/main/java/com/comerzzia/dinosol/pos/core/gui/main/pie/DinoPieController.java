package com.comerzzia.dinosol.pos.core.gui.main.pie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.ventas.reparto.ServiciosRepartoService;
import com.comerzzia.pos.core.gui.main.pie.PieController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
@Primary
public class DinoPieController extends PieController{

	@FXML
	protected Label lbTarifa;
	
	@Autowired
	private ServiciosRepartoService serviciosRepartoService;
	
	@Override
	public void initializeComponents() {
		super.initializeComponents();
		
		if (serviciosRepartoService.getServiciosReparto() != null) {
			lbTarifa.setText(I18N.getTexto("DELIVERY"));
		}
	}
}
