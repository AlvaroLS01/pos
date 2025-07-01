package com.comerzzia.pos.core.gui.controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.util.config.AppConfig;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Component
public class StageControllerBuilder {

	@Autowired
  	protected CoreContextHolder contextHolder;
		
	public StageController buildStageController(Class<? extends StageController> clazz, Stage owner, StageController ownerController, Map<String, Object> stageData) {
		Stage newStage = createStage(owner);
		return buildStageController(clazz, newStage, owner, ownerController, stageData);
	}
	
	public StageController buildStageController(Class<? extends StageController> clazz, Stage stage, Stage owner, StageController ownerController, Map<String, Object> stageData) {
		StageController stageController = contextHolder.getBeanInstance(clazz);
		stageController.setStageData(new HashMap<>());
		if(stageData != null) {
			stageController.getStageData().putAll(stageData);
		}
		stageController.setStage(stage);
		stageController.setOwnerController(ownerController);
		stageController.addEventsListeners();
		return stageController;
	}
	
	protected Stage createStage(Stage owner) {
		Stage newStage = new Stage();
		newStage.initModality(Modality.APPLICATION_MODAL);
				
		if(BooleanUtils.isNotTrue(AppConfig.getCurrentConfiguration().getDeveloperMode())) {
			newStage.initStyle(StageStyle.UNDECORATED); // Removed minimize and maximized window
		}
		newStage.setIconified(false);
		if(owner!=null) {
			newStage.initOwner(owner);
			double ancho = owner.getWidth();
			double alto = owner.getHeight();
			newStage.setWidth(ancho);
			newStage.setHeight(alto);
			newStage.setX(owner.getX());
			newStage.setY(owner.getY());
			newStage.initOwner(owner);
		}
		return newStage;
	}
}
