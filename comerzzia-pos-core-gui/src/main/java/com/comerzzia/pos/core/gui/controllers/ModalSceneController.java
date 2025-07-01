package com.comerzzia.pos.core.gui.controllers;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class ModalSceneController extends SceneController {
	
	public void registrarAccionCerrarVentanaEscape() {
        addKeyEventHandler(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ESCAPE) {
                	actionCancel();
                    t.consume();
                }
            }
        }, KeyEvent.KEY_RELEASED);
    }

    public void actionCancel(){
    	closeCancel();
    }
}
