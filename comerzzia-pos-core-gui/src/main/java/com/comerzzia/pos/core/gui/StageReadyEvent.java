package com.comerzzia.pos.core.gui;

import org.springframework.context.ApplicationEvent;

import javafx.stage.Stage;

public class StageReadyEvent extends ApplicationEvent {
	private static final long serialVersionUID = 6121948623031183437L;

	public StageReadyEvent(Stage stage) {
        super(stage);
    }

    public Stage getStage() {
        return ((Stage) getSource());
    }
    
}