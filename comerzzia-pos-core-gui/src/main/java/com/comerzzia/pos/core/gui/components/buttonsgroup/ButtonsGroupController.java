package com.comerzzia.pos.core.gui.components.buttonsgroup;

import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public interface ButtonsGroupController {

    
    /**
     * Devuelve la escena actual
     * @return 
     */
    public Scene getScene();
    
    public Stage getStage();
    
    /**
     * Registra el evento de teclado para una acción de una botonera
     * @param keyEventHandler handler que registramos
     * @param keyEvent tipo de evento
     */
    public void addKeyEventHandler(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType);
    
    /**
     * Elimina el evento de teclado para una acción de una botonera
     * @param keyEventHandler handler que registramos
     * @param keyEvent tipo de evento
     */
    public void removeKeyEventHandler(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType);

    /**
     * Función que ejecuta la acción ejecutada por un elemento de la botonera
     * @param aThis 
     */
    default void executeAction(ActionButtonComponent botonAccionado) throws PermissionDeniedException {
    	throw new RuntimeException("executeAction() - not implemented");
	};
    
    public void checkOperationPermissions(String operacion) throws PermissionDeniedException;

	public void openActionScene(Long actionId);
}
