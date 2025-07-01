package com.comerzzia.pos.core.gui.controllers;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.controllers.SceneController.SceneCallback;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModalStageController extends StageController {
	
	/**
     * Open a modal scene with a closing callback. 
     * When the modal scene opens the execution thread is paused until is closed again.
     * When the modal scene is closed the callback is called first, then the execution thread is resumed.
     * 
     * @param clazz
     * @param callback
     */
	public void showModalScene(Class<? extends SceneController> clazz, SceneCallback<?> callback) {
		showScene(clazz, callback);
		stage.showAndWait();
	}

	public void showModalScene(Class<? extends SceneController> clazz) {
		showModalScene(clazz, null);
	}
}
