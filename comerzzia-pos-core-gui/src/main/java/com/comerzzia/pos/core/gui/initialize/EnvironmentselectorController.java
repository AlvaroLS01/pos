package com.comerzzia.pos.core.gui.initialize;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.services.config.Environment;
import com.comerzzia.pos.core.services.config.EnvironmentSelector;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

@Component
public class EnvironmentselectorController extends SceneController implements ButtonsGroupController{
	
	protected static final Integer COL_NUM = 4;
	@FXML
	protected ScrollPane scrollPane;
	@FXML
	protected AnchorPane panelBotonera;
	protected ButtonsGroupComponent botonera;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		final List<ButtonConfigurationBean> listaAcciones = new LinkedList<>();
		for(Environment env:EnvironmentSelector.getAvailableEnvironments().values()) {
			ButtonConfigurationBean conf = new ButtonConfigurationBean(null, env.getName(), "", "environmentSelected", ButtonConfigurationBean.TYPE_METHOD);
			conf.setParam("environment", env.getName());
			listaAcciones.add(conf);
		}
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					int rowNum = (int)Math.ceil(listaAcciones.size()/COL_NUM.doubleValue());
					double height = rowNum *60;
					botonera = new ButtonsGroupComponent(rowNum, COL_NUM, EnvironmentselectorController.this, listaAcciones, panelBotonera.getWidth(), height, ActionButtonNormalComponent.class.getName(), 5);
					panelBotonera.getChildren().clear();
					panelBotonera.getChildren().add(botonera);
				} catch (LoadWindowException e) {
				}
			}
		});
		
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
	}
	

	@Override
	protected void initializeFocus() {
	}
	
	public void environmentSelected(HashMap<String, String>params) {
		closeSuccess(EnvironmentSelector.getAvailableEnvironments().get(params.get("environment")));
	}

	@Override
	public void executeAction(ActionButtonComponent botonAccionado) throws PermissionDeniedException {
	}

}
