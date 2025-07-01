package com.comerzzia.pos.core.gui.controllers;

import java.util.Map;

import com.comerzzia.pos.core.gui.InitializeGuiException;

public abstract class SecondaryScreenSceneController extends SceneController {
	
	@Override
	public void initialize() throws InitializeGuiException {
		initializeComponents();
		addEventHandlers();
		setInitialized(true);
	}

	@Override
	protected void checkPermissions(Map<String, Object> params) {
	}
}
