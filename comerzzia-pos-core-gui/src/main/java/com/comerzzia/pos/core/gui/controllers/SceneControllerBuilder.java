package com.comerzzia.pos.core.gui.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.htmlparser.LongOperationTask;
import com.comerzzia.pos.core.gui.view.CssScene;
import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.scene.layout.Region;

@Component
public class SceneControllerBuilder {
	
	@Autowired
  	protected CoreContextHolder contextHolder;
	
	@Autowired
	protected AppConfig appConfig;
	
	public SceneController buildSceneController(Class<? extends SceneController> clazz, StageController stageController, Map<String, Object> sceneData) throws InitializeGuiException {
		SceneController sceneController = contextHolder.getBeanInstance(clazz);
		if(sceneData != null) {
			sceneController.setSceneData(new HashMap<>(sceneData));
		}
		initSceneController(sceneController, stageController);
		return sceneController;
	}
	
	protected void initSceneController(SceneController sceneController, StageController stageController) throws InitializeGuiException {
		try {
			
			sceneController.setStageController(stageController);
			if(!sceneController.isInitialized()) {
				SceneView mainView = contextHolder.getBeanInstance(sceneController.getSceneViewClass());
				sceneController.setScene(new CssScene(new Region()));
				mainView.setScene(sceneController.getScene());
				mainView.loadFXML(sceneController);
				sceneController.setSceneView(mainView);
				sceneController.initialize();
			}

			sceneController.onSceneOpen();
			createAndExecuteLongOperationTask(sceneController);
			
		} catch (InitializeGuiException e) {
			contextHolder.destroyBean(sceneController.getClass());
			throw e;
		} catch(Exception e) {
            contextHolder.destroyBean(sceneController.getClass());
            throw new InitializeGuiException(I18N.getText("Se ha producido un error durante la carga de la pantalla. Por favor, revise el log."), e);
        }
    	
	}

	/**
	 * @param sceneController
	 */
	protected void createAndExecuteLongOperationTask(SceneController sceneController) {
		sceneController.getScene().getRoot().setDisable(true);
		LongOperationTask<Void> task = new LongOperationTask<Void>(sceneController, false, false) {

			@Override
			protected Void execute() throws Exception {
				sceneController.executeLongOperations();
				return null;
			}
			
			@Override
			protected void taskSucceded() {
				try {
					sceneController.succededLongOperations();
					sceneController.onSceneShow();
				}catch(Exception e) {
					sceneController.closeCancel();
					throw e;
				}
			}
			
			@Override
			protected void taskFailed() {
				Throwable e = getException();
				sceneController.failedLongOperations(e);
			}
			
			@Override
			protected void displayExceptionError(Throwable e) {}
		};
		
		task.start();
	}
	
	

}
