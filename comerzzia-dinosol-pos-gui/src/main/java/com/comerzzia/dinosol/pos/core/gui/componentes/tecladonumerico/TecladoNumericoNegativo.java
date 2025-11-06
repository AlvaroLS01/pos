package com.comerzzia.dinosol.pos.core.gui.componentes.tecladonumerico;

import javafx.scene.Scene;

import com.comerzzia.pos.core.gui.componentes.Component;

public class TecladoNumericoNegativo extends Component {

	@Override
	public void init(Scene scene) {
		super.init(scene);
		TecladoNumericoNegativoController controller = (TecladoNumericoNegativoController) getController();
		controller.crearRobot(scene);
	}
    
}
