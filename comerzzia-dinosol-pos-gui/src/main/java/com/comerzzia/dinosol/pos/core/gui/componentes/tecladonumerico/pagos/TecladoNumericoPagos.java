package com.comerzzia.dinosol.pos.core.gui.componentes.tecladonumerico.pagos;

import javafx.scene.Scene;

import com.comerzzia.pos.core.gui.componentes.Component;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumericoController;

public class TecladoNumericoPagos extends TecladoNumerico {

	@Override
	public void init(Scene scene) {
		super.init(scene);
		TecladoNumericoController controller = (TecladoNumericoController) getController();
		controller.crearRobot(scene);
	}
    
}
