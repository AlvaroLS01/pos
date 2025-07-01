
package com.comerzzia.pos.core.gui.components;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.gui.controllers.SceneController;

import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class ComponentController {
	
	protected static final Logger log = Logger.getLogger(ComponentController.class.getName());

	protected Component component;
	protected SceneController parentScene;
	
	public ComponentController() {
	}

	public Stage getStage() {
		return (Stage) component.getScene().getWindow();
	}

	public Scene getScene() {
		return component.getScene();
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
	
	public void setParentScene(SceneController parentScene) {
		this.parentScene = parentScene;
	}
	
	public void addEventFilters() {}
	
	/**
	 * This methos is called only once, after the fxml is loader but before the controller is asigned to the scene
	 */
	public void initializeComponents() {}

}
