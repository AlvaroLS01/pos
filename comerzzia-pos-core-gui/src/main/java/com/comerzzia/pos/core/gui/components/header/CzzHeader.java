package com.comerzzia.pos.core.gui.components.header;

import com.comerzzia.pos.core.gui.components.Component;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CzzHeader extends Component {

	protected Scene scene;

	public CzzHeader() {
		super();
		HBox.setHgrow(this, Priority.ALWAYS);
	}
	
	@Override
	public CzzHeaderController getController() {
		return (CzzHeaderController) super.getController();
	}
	
}
