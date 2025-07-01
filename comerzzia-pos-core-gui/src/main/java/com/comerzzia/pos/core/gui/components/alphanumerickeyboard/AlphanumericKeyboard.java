package com.comerzzia.pos.core.gui.components.alphanumerickeyboard;

import java.util.HashMap;
import java.util.Map;

import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.util.czzrobot.CzzRobot;
import com.comerzzia.pos.core.gui.util.czzrobot.CzzRobotFactory;
import com.comerzzia.pos.core.gui.view.SceneView;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AlphanumericKeyboard extends HBox{
	
	protected AnchorPane keyboardPane;
	protected VBox keyboard;
	protected VBox specialCharacters;

	protected CzzRobot robot;
	
	protected ToggleButton shiftButton;
	
	public AlphanumericKeyboard() {
		super();
		
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		sceneProperty().addListener((observable, oldScene, newScene) -> sceneChangedEvent(oldScene, newScene));
	}

	protected void sceneChangedEvent(Scene oldScene, Scene newScene) {
		//initialized scene
		if (oldScene == null && newScene != null) {
			init();
		}
	}

	@SuppressWarnings("unchecked")
	public void init() {
		if(robot != null) {
			return;
		}
		
		this.robot = CzzRobotFactory.createRobot(getScene());
		setKeyboard();

		Map<String, Object> userData = (HashMap<String, Object>) getScene().getUserData();
		if(userData instanceof Map) {
			SceneController newController = (SceneController) userData.get("CONTROLLER");
			newController.setShowKeyboard(false);
		}
		
		SceneView.organizeCSS(getClass(), this);
	}

	protected void setKeyboard() {
		keyboardPane = new AnchorPane();
		keyboardPane.setMinHeight(300.0);
		keyboardPane.setPrefHeight(300.0);
		keyboardPane.setPrefWidth(500.0);
		
		this.setId("czz-keyboard");
		keyboard = new VBox();
		keyboard.getStyleClass().addAll("area-component", "keyboard");
		keyboard.setAlignment(Pos.CENTER);
		AnchorPane.setTopAnchor(keyboard, 0.0);
		AnchorPane.setBottomAnchor(keyboard, 0.0);
		AnchorPane.setRightAnchor(keyboard, 0.0);
		AnchorPane.setLeftAnchor(keyboard, 0.0);
		keyboardPane.getChildren().add(keyboard);
		
		specialCharacters = new VBox();
		specialCharacters.getStyleClass().addAll("area-component", "keyboard");
		specialCharacters.setAlignment(Pos.CENTER);
		AnchorPane.setTopAnchor(specialCharacters, 0.0);
		AnchorPane.setBottomAnchor(specialCharacters, 0.0);
		AnchorPane.setRightAnchor(specialCharacters, 0.0);
		AnchorPane.setLeftAnchor(specialCharacters, 0.0);
		keyboardPane.getChildren().add(specialCharacters);
		specialCharacters.setVisible(false);
		specialCharacters.setManaged(false);
		
		this.getChildren().add(keyboardPane);
		
		
		addKeyboardButtons();
	}
	
	protected void addKeyboardButtons() {
		shiftButton = new ToggleButton("^");
		shiftButton.setId("shift-button");
		setCommon(shiftButton);
		shiftButton.setMinHeight(60.0);
		shiftButton.setMinWidth(75.0);
		shiftButton.setPrefHeight(60.0);
		shiftButton.setAlignment(Pos.CENTER);
		shiftButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		
		addRows(keyboard, 5);
		addKeys(keyboard, new String[]{"1","2","3","4","5","6","7","8","9","0"}, 0);
		addKeys(keyboard, new String[]{"q","w","e","r","t","y","u","i","o","p"}, 1);
		addKeys(keyboard, new String[]{"a","s","d","f","g","h","j","k","l","ñ"}, 2);
		addShift(keyboard, 3);
		addKeys(keyboard, new String[]{"z","x","c","v","b","n","m"}, 3);
		addBackspace(keyboard, 3);
		addSpecialChars(keyboard, 4);
		addSpacebar(keyboard, 4);
		addClear(keyboard, 4);
		
		addRows(specialCharacters, 5);
		addKeys(specialCharacters, new String[]{"!","\"","#","$","%","&","/","(",")","="}, 0);
		addKeys(specialCharacters, new String[]{"*","+","-",";",":","_","?","¿","¡","€"}, 1);
		addKeys(specialCharacters, new String[]{"@","[","]","{","}","\\","|","<",">","."}, 2);
		addKeys(specialCharacters, new String[]{"~","^","£","$","±","'","°","¬","¢"}, 3);
		
		addBackspace(specialCharacters, 3);
		addSpecialChars(specialCharacters, 4);
		addSpacebar(specialCharacters, 4);
		addClear(specialCharacters, 4);
	}
	
	// Structure of the keyboard

	protected void addRows(VBox keyboard, int numRows) {
		for (int i = 0; i < numRows; i++) {
			HBox row = new HBox();
			if (i != 0) {
				VBox.setVgrow(row, Priority.NEVER);
			}
			keyboard.getChildren().add(row);
		}
	}
	
	protected void addKeys(VBox keyboard, String[] keys, int row) {
		for(String key : keys) {
			addKey(keyboard, key, row);
		}
	}
	
	protected void addKey(VBox keyboard, String key, int row) {
		Button button = new Button(key);
		setCommon(button);
		button.setMinHeight(60.0);
		button.setMinWidth(45.0);
		button.setPrefHeight(60.0);
		button.setMaxWidth(Double.POSITIVE_INFINITY);
		button.setAlignment(Pos.CENTER);
		HBox.setHgrow(button, Priority.ALWAYS);
		addEventHandlerLetter(button);
		
		button.textProperty().bind(Bindings.when(shiftButton.selectedProperty()).then(key.toUpperCase()).otherwise(key));

		HBox rowHBox = (HBox) keyboard.getChildren().get(row);
		rowHBox.getChildren().add(button);
	}
	
	protected void addShift(VBox keyboard, int row) {
		HBox rowHBox = (HBox) keyboard.getChildren().get(row);
		rowHBox.getChildren().add(shiftButton);
	}
	
	protected void addBackspace(VBox keyboard, int row) {
		Button button = new Button("<");
		button.setId("backspace-button");
		setCommon(button);
		button.setMinHeight(60.0);
		button.setMinWidth(75.0);
		button.setPrefHeight(60.0);
		button.setAlignment(Pos.CENTER);
		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		addEventHandlerBackspace(button);
		
		HBox rowHBox = (HBox) keyboard.getChildren().get(row);
		rowHBox.getChildren().add(button);
	}
	
	protected void addSpecialChars(VBox keyboard, int row) {
		Button button = new Button("=\\<");
		button.setId("special-chars-button");
		setCommon(button);
		button.setMinHeight(60.0);
		button.setMinWidth(75.0);
		button.setPrefHeight(60.0);
		button.setAlignment(Pos.CENTER);
		addEventHandlerSpecialCharacters(button);
		
		HBox rowHBox = (HBox) keyboard.getChildren().get(row);
		rowHBox.getChildren().add(button);
	}

	protected void addSpacebar(VBox keyboard, int row) {
		Button button = new Button(" ");
		setCommon(button);
		button.setPrefHeight(60.0);
		button.setPrefWidth(552.0);
		button.setAlignment(Pos.CENTER);
		addEventHandlerLetter(button);
		
		HBox rowHBox = (HBox) keyboard.getChildren().get(row);
		rowHBox.getChildren().add(button);
	}
	
	protected void addClear(VBox keyboard, int i) {
		Button button = new Button("Limpiar");
		button.setId("clear-button");
		setCommon(button);
		button.setMinHeight(60.0);
		button.setMinWidth(90.0);
		button.setPrefHeight(60.0);
		button.setAlignment(Pos.CENTER);
		addEventHandlerClear(button);

		HBox rowHBox = (HBox) keyboard.getChildren().get(i);
		rowHBox.getChildren().add(button);
	}

	protected void setCommon(ButtonBase button) {
		button.setFocusTraversable(false);
		button.setMnemonicParsing(false);
	}

//	// Logic of the keyboard

	protected void addEventHandlerBackspace(Button button) {
		EventHandler<ActionEvent> eventHandler = e -> {
			Node focusNode = getScene().focusOwnerProperty().get();
			if (focusNode == null) {
				return;
			}
			robot.keyType(KeyCode.BACK_SPACE);
		};
		button.setOnAction(eventHandler);
	}
	protected void addEventHandlerLetter(Button button) {
		EventHandler<ActionEvent> eventHandler = e -> {
			Node focusNode = getScene().focusOwnerProperty().get();
			if (focusNode == null) {
		      return;
		    }
			robot.keyType(KeyCode.UNDEFINED, button.getText());
		};
		button.setOnAction(eventHandler);
	}
	
	protected void addEventHandlerSpecialCharacters(Button button) {
		EventHandler<ActionEvent> eventHandler = e -> toggleKeyboard();
		button.setOnAction(eventHandler);
	}
	
	private void addEventHandlerClear(Button button) {
		EventHandler<ActionEvent> eventHandler = e -> {
			Node focusNode = getScene().focusOwnerProperty().get();
			if (focusNode == null) {
				return;
			}
			robot.keyType(KeyCode.A, "A", true);
			robot.keyType(KeyCode.BACK_SPACE);
		};
		button.setOnAction(eventHandler);
	}

	
	protected void toggleKeyboard() {
		keyboard.setVisible(!keyboard.isVisible());
		keyboard.setManaged(!keyboard.isManaged());
		specialCharacters.setVisible(!specialCharacters.isVisible());
		specialCharacters.setManaged(!specialCharacters.isManaged());
	}
	
	/**
	 * Toggles shift on or off if the button is not forced
	 */
	public void shiftSelect(boolean pressed) {
		if(!shiftButton.isDisabled()) {
			shiftButton.selectedProperty().set(pressed);
		}
	}
	
	/**
	 * Forces shift button into one state (pressed or not pressed). User input can not change it
	 */
	public void shiftForce(boolean forced) {
		shiftButton.setDisable(forced);
	}
	
}
