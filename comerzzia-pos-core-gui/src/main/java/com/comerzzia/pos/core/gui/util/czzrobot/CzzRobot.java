package com.comerzzia.pos.core.gui.util.czzrobot;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CzzRobot {
	
	protected Scene scene;
	
	public CzzRobot(Scene scene) {
		this.scene = scene;
	}
	
	public void keyType(KeyCode code, String ch, boolean controlDown, boolean shiftDown, boolean altDown, boolean metaDown) {
		firePressedTypedReleased(getKeyEventTarget(), ch, code, controlDown, shiftDown, altDown, metaDown);
	}

	public void keyType(KeyCode code, String ch, boolean controlDown) {
		firePressedTypedReleased(getKeyEventTarget(), ch, code, controlDown, false, false, false);
	}

	public void keyRelease(KeyCode code, String ch) {
		getKeyEventTarget().fireEvent(
                createKeyEvent(getKeyEventTarget(), KeyEvent.KEY_RELEASED, ch, code, false, false, false, false));
	}

	public void keyPress(KeyCode code, String ch) {
		getKeyEventTarget().fireEvent(
				createKeyEvent(getKeyEventTarget(), KeyEvent.KEY_PRESSED, ch, code, false, false, false, false));
	}

	public void keyType(KeyCode code) {
		keyType(code, getKeyCodeStr(code), false, false, false, false);
	}
	
	public void keyType(KeyCode code, String ch) {
		firePressedTypedReleased(getKeyEventTarget(), ch, code, false, false, false, false);
	}
	
	public void keyRelease(KeyCode code) {
		keyRelease(code, getKeyCodeStr(code));
	}
	
	public void keyPress(KeyCode code) {
		keyPress(code, getKeyCodeStr(code));
	}
	
	public String getKeyCodeStr(KeyCode code) {
		switch (code) {
		case MULTIPLY:
			return Character.toString((char)java.awt.event.KeyEvent.VK_MULTIPLY);
		case BACK_SPACE:
			return Character.toString((char)java.awt.event.KeyEvent.VK_BACK_SPACE);
		case ENTER:
			return Character.toString((char)java.awt.event.KeyEvent.VK_ENTER);
		default:
			return code.toString();
		}
	}
	
	public void firePressedTypedReleased(Node focusNode, String ch, KeyCode code) {
		firePressedTypedReleased(focusNode, ch, code, false, false, false, false);
	}
	
    public void firePressedTypedReleased(Node focusNode, String ch, KeyCode code, boolean controlDown, boolean shiftDown, boolean altDown, boolean metaDown) {
        focusNode.fireEvent(
                createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, ch, code, controlDown, shiftDown, altDown, metaDown));
        focusNode.fireEvent(
        		createKeyEvent(focusNode, KeyEvent.KEY_TYPED, ch, code, controlDown, shiftDown, altDown, metaDown));
        focusNode.fireEvent(
                createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, ch, code, controlDown, shiftDown, altDown, metaDown));
    }

    public KeyEvent createKeyEvent(EventTarget target, EventType<KeyEvent> eventType,
    		String character, KeyCode code, boolean controlDown, boolean shiftDown, boolean altDown, boolean metaDown) {
    	return new KeyEvent(eventType, character, code.toString(), code, shiftDown, controlDown, altDown, metaDown);
    }
    
    protected Node getKeyEventTarget() {
    	return scene.getFocusOwner() != null ? scene.getFocusOwner() : scene.getRoot();
    }

}
