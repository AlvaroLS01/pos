package com.comerzzia.dinosol.pos.core.gui.componentes.keyboard;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.stage.Screen;
import javafx.stage.Window;

import org.comtel2000.keyboard.control.KeyboardType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.componentes.keyboard.Keyboard;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;

@Primary
@Component
public class DinoKeyboard extends Keyboard {

	public DinoKeyboard() throws IOException, URISyntaxException {
		super();
	}

	@Override
	protected void addFocusListener(Scene scene) {
		DinoSceneFocusChangeListener sceneFocusChangeListener = (DinoSceneFocusChangeListener) mapScenesInUse.get(scene);
		if (sceneFocusChangeListener == null) {
			sceneFocusChangeListener = new DinoSceneFocusChangeListener(scene.getWindow());
			mapScenesInUse.put(scene, sceneFocusChangeListener);
		}
		if (!sceneFocusChangeListener.isAttached()) {
			scene.focusOwnerProperty().addListener(sceneFocusChangeListener);
			sceneFocusChangeListener.setAttached(true);
		}


		for (Node nodo : getAllNodes(scene.getRoot())) {
			if(nodo instanceof TextInputControl) {
				nodo.setOnMouseClicked(new EventHandler<Event>(){
					@Override
                    public void handle(Event event) {
						boolean mostrarDebajoPantalla = false;
						boolean mostrar = true;
						if(nodo.getUserData() instanceof KeyboardDataDto) {					
							KeyboardDataDto keyboardDataDto = (KeyboardDataDto) nodo.getUserData();
							mostrarDebajoPantalla = keyboardDataDto.isPintarPiePantalla();
							mostrar = keyboardDataDto.isMostrar();
						}
						
						if(mostrar && ((TextInputControl) nodo).isEditable()) {
							setPopupVisible(true, (TextInputControl) nodo, scene.getWindow(), mostrarDebajoPantalla);
						}
						else {
							setPopupVisible(false, (TextInputControl) nodo, scene.getWindow(), mostrarDebajoPantalla);
						}
                    }
				});
			}
		}
	}

	protected class DinoSceneFocusChangeListener extends SceneFocusChangeListener implements ChangeListener<Node> {

		public DinoSceneFocusChangeListener(Window window) {
			super(window);
		}

		@Override
		public void changed(ObservableValue<? extends Node> value, Node n1, Node n2) {
			if (n2 != null && n2 instanceof TextInputControl) {
				if(((TextInputControl) n2).isEditable()) {
					boolean mostrarAlInicio = true;
					boolean mostrarDebajoPantalla = false;
					boolean mostrar = true;
					if(n2.getUserData() != null && n2.getUserData() instanceof KeyboardDataDto) {					
						KeyboardDataDto keyboardDataDto = (KeyboardDataDto) n2.getUserData();
						mostrarAlInicio = keyboardDataDto.isVisibleAlInicio();
						mostrarDebajoPantalla = keyboardDataDto.isPintarPiePantalla();
						mostrar = keyboardDataDto.isMostrar();
					}
					if(mostrarAlInicio && mostrar) {
						setPopupVisible(true, (TextInputControl) n2, this.window, mostrarDebajoPantalla);
					}
				}
			}
			else {
				close();
			}
		}

		public Boolean isAttached() {
			return isAttached;
		}

		public void setAttached(Boolean isAttached) {
			this.isAttached = isAttached;
		}
	}

	public ArrayList<Node> getAllNodes(Parent root) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		addAllDescendents(root, nodes);
		return nodes;
	}

	private void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			nodes.add(node);
			if (node instanceof Parent)
				addAllDescendents((Parent) node, nodes);
		}
	}
	
	@Override
	protected void calcularTamañoPantalla(boolean show, TextInputControl textNode, boolean printBottomScreen) {
		if (show) {
			Object userData = textNode.getUserData();
			if(userData != null && userData instanceof KeyboardDataDto && ((KeyboardDataDto) userData).isPintarSignoNegativo()) {
				getKeyboardPopup().getKeyBoard().setKeyboardType(KeyboardType.EMAIL);
			}
			else if(textNode instanceof TextFieldImporte) {
				getKeyboardPopup().getKeyBoard().setKeyboardType(KeyboardType.NUMERIC);
			}
			else {
				getKeyboardPopup().getKeyBoard().setKeyboardType(KeyboardType.TEXT_SHIFT);							
			}
			
			if (textNode != null && textNode.getScene() != null) {
				Window window = textNode.getScene().getWindow();
				Rectangle2D textNodeBounds = new Rectangle2D(
						window.getX() + textNode.getLocalToSceneTransform().getTx(), 
						window.getY() + textNode.getLocalToSceneTransform().getTy(), 
						textNode.getWidth(), 
						textNode.getHeight());

				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				if (textNodeBounds.getMinX() + getKeyboardPopup().getWidth() > screenBounds.getMaxX()) {
					getKeyboardPopup().setX(screenBounds.getMaxX() - getKeyboardPopup().getWidth());
				} else {
					getKeyboardPopup().setX(textNodeBounds.getMinX());
				}

				if (textNodeBounds.getMaxY() + getKeyboardPopup().getHeight() > screenBounds.getMaxY()) {
					getKeyboardPopup().setY(textNodeBounds.getMinY() - getKeyboardPopup().getHeight() - 75);
				} else {
					getKeyboardPopup().setY(textNodeBounds.getMaxY() + 75);
				}
				
				if(printBottomScreen) {
					getKeyboardPopup().show(window);
					double widthKeyboard = getKeyboardPopup().getWidth();
					double x = (screenBounds.getWidth() - widthKeyboard) / 2;
					getKeyboardPopup().setX(x);
					getKeyboardPopup().setY(window.getY() + window.getHeight());
				}
			}
		}
	}

}
