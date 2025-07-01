package com.comerzzia.pos.core.gui.controllers;

import java.util.Map;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.service.action.ActionServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class ActionSceneControllerBuilder extends SceneControllerBuilder{
	@Autowired
  	protected ActionServiceFacade actionsService;
  	
  	/**
  	 * Builds an action scene controller. The controller is created by the Spring 
  	 * context using the action execution class defined in the database
  	 * 
  	 * @param actionId 
  	 * @param stageController
  	 * @param sceneData          - <b>Optional</b>. If not null, the data will be 
  	 *                           passed to the scene controller
  	 * @return
  	 * @throws InitializeGuiException 
  	 */
  	public ActionSceneController buildActionSceneController(Long actionId, StageController stageController, Map<String, Object> sceneData) throws InitializeGuiException {
  		ActionDetail action;
		try {
			action = actionsService.findActionDetailById(actionId);
		} catch (NotFoundException e) {
			throw new InitializeGuiException(e);
		}
		
		ActionSceneController sceneController;
		try {
			sceneController = contextHolder.getBeanInstance(action.getExecution());
		} catch (NoSuchBeanDefinitionException e) {
			throw new InitializeGuiException(I18N.getText("Controlador {0} no encontrado para la acción {1}", action.getExecution(), actionId), e);
		}
		
		sceneController.setAction(action);
		if(sceneData != null) {
			sceneController.getSceneData().putAll(sceneData);
		}
		initSceneController(sceneController, stageController);
		
		return sceneController;
  	}
  	
	/**
	 * Builds an action scene controller. The controller may be created by the Spring
	 * context using the class passed as parameter
	 * 
	 * @param actionId
	 * @param clazz			  - <b>Optional</b>. The class of the controller. If null, the action 
	 *                        execution class
	 * @param stageController
	 * @param sceneData       - <b>Optional</b>. If not null, the data will be passed to the
	 *                        scene controller
	 * @return
	 * @throws InitializeGuiException
	 */
  	public ActionSceneController buildActionSceneController(Long actionId, Class<? extends ActionSceneController> clazz, StageController stageController, Map<String, Object> sceneData) throws InitializeGuiException {
  		ActionDetail action;
		try {
			action = actionsService.findActionDetailById(actionId);
		} catch (NotFoundException e) {
			throw new InitializeGuiException(e);
		}
  		ActionSceneController sceneController =  clazz != null ? (ActionSceneController) contextHolder.getBeanInstance(clazz) : contextHolder.getBeanInstance(action.getExecution());
  		if(sceneData != null) {
			sceneController.getSceneData().putAll(sceneData);
		} 
		sceneController.setAction(action);
		initSceneController(sceneController, stageController);
		
		return sceneController;
  	}
  	

}
