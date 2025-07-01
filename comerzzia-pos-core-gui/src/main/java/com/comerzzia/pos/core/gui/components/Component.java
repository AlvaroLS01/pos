package com.comerzzia.pos.core.gui.components;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class Component extends AnchorPane {

	private static Logger log = Logger.getLogger(Component.class);

	protected ComponentController controller;

	public Component() {
		FXMLLoader fxmlLoader = new FXMLLoader(SceneView.getFXMLResource(SceneView.getFXMLName(getClass())), I18N.getResourceBundle());
		fxmlLoader.setRoot(this);

		try {
			fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
	    		@Override
	    		public Object call(Class<?> defaultControllerClass) {
	    			return CoreContextHolder.getInstance(defaultControllerClass.getName());
	    		}
	    	});
			fxmlLoader.load();
			Parent parent = fxmlLoader.getRoot();

			SceneView.organizeCSS(getClass(), parent);
			controller = fxmlLoader.getController();
			controller.setComponent(this);
			controller.initializeComponents();
			
			sceneProperty().addListener((observable, oldScene, newScene) -> {
				sceneChangedEvent(oldScene, newScene);
			});
			addEventFilters();
		}
		catch (IOException exception) {
			log.error("Component - Error cargando pantalla/controller fxml :" + exception.getMessage(), exception);
			throw new RuntimeException(exception);
		}
	}

	public ComponentController getController() {
		return controller;
	}
	
	@SuppressWarnings("unchecked")
	protected void sceneChangedEvent(Scene oldScene, Scene newScene) {
		if(ObjectUtils.notEqual(oldScene, newScene)) {
			SceneController newController = null;
			if(newScene != null) {
				Map<String, Object> userData = (Map<String, Object>) newScene.getUserData();
				if(userData != null && userData instanceof Map) {
					newController = (SceneController) userData.get("CONTROLLER");
				}
			}
			controller.setParentScene(newController);
		}
	}
	
	protected void addEventFilters() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				controller.addEventFilters();
			}
		});
	}
	
}
