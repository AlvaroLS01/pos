package com.comerzzia.pos.core.gui.components.numerickeypad;

import java.util.HashMap;
import java.util.Map;

import com.comerzzia.pos.core.gui.components.alphanumerickeyboard.AlphanumericKeyboard;
import com.comerzzia.pos.core.gui.components.keyboard.Keyboard;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.util.czzrobot.CzzRobot;
import com.comerzzia.pos.core.gui.util.czzrobot.CzzRobotFactory;
import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NumericKeypad extends AnchorPane {

	protected VBox keyboard;

	protected CzzRobot robot;
	
	protected boolean isFullKeypad = true;

	public NumericKeypad() {
		super();
		this.setMinHeight(300.0);
		this.setMaxHeight(360.0);
		this.setPrefWidth(320.0);
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		sceneProperty().addListener((observable, oldScene, newScene) -> {
			sceneChangedEvent(oldScene, newScene);
		});
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
		this.isFullKeypad = !getParent().getChildrenUnmodifiable().stream().anyMatch(p-> p instanceof AlphanumericKeyboard);
		setKeyboard();
		internationalization(2, 0);
		
		Map<String, Object> userData = (HashMap<String, Object>) getScene().getUserData();
		if(userData != null && userData instanceof Map) {
			SceneController newController = (SceneController) userData.get("CONTROLLER");
			newController.setShowKeyboard(false);
		}

		SceneView.organizeCSS(getClass(), this);
	}
	
	protected void internationalization(int numpadStart, int clearRow) {
		HBox rowHBox = (HBox) this.keyboard.getChildren().get(numpadStart + (isFullKeypad?3:2));

		Button comma = (Button) rowHBox.getChildren().get(1);
		comma.setText(FormatUtils.getInstance().getDecimalSeparator() + "");
		Button ok = (Button) rowHBox.getChildren().get(2);
		ok.setText(I18N.getText(ok.getText()));

		rowHBox = (HBox) this.keyboard.getChildren().get(clearRow);
		
		Button clear = (Button) rowHBox.getChildren().get(clearRow);
		clear.setText(I18N.getText(clear.getText()));
	}

	protected void setKeyboard() {
		keyboard = new VBox();
		keyboard.getStyleClass().addAll("area-component", "keyboard");
		keyboard.setAlignment(Pos.CENTER);
		AnchorPane.setTopAnchor(keyboard, 0.0);
		AnchorPane.setBottomAnchor(keyboard, 0.0);
		AnchorPane.setRightAnchor(keyboard, 0.0);
		AnchorPane.setLeftAnchor(keyboard, 0.0);
		this.getChildren().add(keyboard);
		addButtons();
	}

	protected void addButtons() {
		addRows(isFullKeypad?6:5);
		addBasicKeyboard(isFullKeypad?2:1,0);
		addOperationRow(isFullKeypad?1:0);
	}

	// Structure of the numeric keyboard

	protected void addRows(int numRows) {
		for (int i = 0; i < numRows; i++) {
			HBox row = new HBox();
			row.setMaxHeight(60.0);
			row.setPrefHeight(60);
			row.setMinHeight(50.0);
			if (i != 0) {
				VBox.setVgrow(row, Priority.ALWAYS);
			}
			keyboard.getChildren().add(row);
		}
	}
	
	protected void addBasicKeyboard(int numpadStart, int clearRow) {
		addNumPad(numpadStart);
		addZero(numpadStart);
		addComma(numpadStart);
		addOK(numpadStart);
		if(isFullKeypad) {
			addClear(clearRow);
			addDelete(clearRow);
		}
	}
	
	protected void addEmptyRow(int rowNumber) {
		HBox rowHBox = (HBox) this.keyboard.getChildren().get(rowNumber);
		rowHBox.getChildren().clear();
	}
	
	protected void addOperationRow(int rowNumber) {
		addMultiply(rowNumber);
		addSubtract(rowNumber);
		if(isFullKeypad) {
			addAlphanumericButton(rowNumber);
		}else {
			addDelete(rowNumber);
		}
	}

	protected void addNumPad(int numpadStart) {
		int buttonNumber = 1;

		for (int row = numpadStart + 2; row > numpadStart - 1; row--) {
			for (int i = 0; i < 3; i++) {
				Button button = new Button(String.valueOf(buttonNumber));
				setCommon(button);
				setDimensions(button, true);
				button.setAlignment(Pos.CENTER);
				addEventHandlerNumber(button);

				HBox rowHBox = (HBox) keyboard.getChildren().get(row);
				rowHBox.getChildren().add(button);

				buttonNumber++;
			}
		}
	}

	protected void addZero(int numpadStart) {
		Button button = new Button("0");
		setCommon(button);
		setDimensions(button, true);
		button.setAlignment(Pos.CENTER);
		addEventHandlerNumber(button);
		HBox rowHBox = (HBox) keyboard.getChildren().get(numpadStart + 3);
		rowHBox.getChildren().add(button);
	}

	protected void addComma(int numpadStart) {
		Button button = new Button(",");
		setCommon(button);
		setDimensions(button, true);
		addEventHandlerComma(button);
		button.getStyleClass().add("buttonNoPaddingTop");
		HBox rowHBox = (HBox) keyboard.getChildren().get(numpadStart + 3);
		rowHBox.getChildren().add(button);
	}

	protected void addOK(int numpadStart) {
		Button button = new Button(I18N.getText("OK"));
		setCommon(button);
		setDimensions(button, true);
		addEventHandlerOK(button);
		button.setId("okButton");
		HBox rowHBox = (HBox) keyboard.getChildren().get(numpadStart + 3);
		rowHBox.getChildren().add(button);
	}
	
	protected void addClear(int rowNumber) {
		Button button = new Button(I18N.getText("Limpiar"));
		setCommon(button);
		setDimensions(button, false);
		addEventHandlerClear(button);
		button.setId("clearButton");
		HBox rowHBox = (HBox) keyboard.getChildren().get(rowNumber);
		rowHBox.getChildren().add(button);
	}

	protected void addDelete(int rowNumber) {
		Button button = new Button("");
		setCommon(button);
		setDimensions(button, false);
		addEventHandlerDelete(button);
		button.setId("deleteButton");
		HBox rowHBox = (HBox) keyboard.getChildren().get(rowNumber);
		rowHBox.getChildren().add(button);
	}

	protected void addMultiply(int rowNumber) {
		Button button = new Button("X");
		setCommon(button);
		setDimensions(button, true);
		addEventHandlerMultiply(button);
		button.setId("multiplyButton");
		HBox rowHBox = (HBox) keyboard.getChildren().get(rowNumber);
		rowHBox.getChildren().add(button);
	}

	protected void addSubtract(int rowNumber) {
		Button button = new Button("-");
		setCommon(button);
		setDimensions(button, true);
		addEventHandlerSubtract(button);
		button.getStyleClass().add("buttonNoPaddingTop");
		HBox rowHBox = (HBox) keyboard.getChildren().get(rowNumber);
		rowHBox.getChildren().add(button);
	}

	protected void addAlphanumericButton(int rowNumber) {
		Button button = new Button(I18N.getText("ABC"));
		setCommon(button);
		setDimensions(button, true);
		addEventHandlerSearch(button);
		button.setAlignment(Pos.CENTER);
		HBox rowHBox = (HBox) keyboard.getChildren().get(rowNumber);
		rowHBox.getChildren().add(button);
	}

	protected void setDimensions(Button button, Boolean setMin) {
		if (setMin) {
			button.setMinHeight(60.0);
			button.setMinWidth(60.0);
			HBox.setHgrow(button, Priority.ALWAYS);
		}
		button.setPrefHeight(60.0);
		button.setPrefWidth(500.0);
	}

	protected void setCommon(Button button) {
		button.setFocusTraversable(false);
		button.setMnemonicParsing(false);
	}

	// Logic of the numeric keyboard
	
	protected void addEventHandler(Button button, KeyCode keycode) {
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				robot.keyType(keycode, button.getText());
			}
		};
		button.setOnAction(eventHandler);
	}

	protected void addEventHandlerNumber(Button button) {
		KeyCode keycode = KeyCode.NUMPAD0;

		switch (button.getText()) {
			case "1":
				keycode = KeyCode.NUMPAD1;
				break;
			case "2":
				keycode = KeyCode.NUMPAD2;
				break;
			case "3":
				keycode = KeyCode.NUMPAD3;
				break;
			case "4":
				keycode = KeyCode.NUMPAD4;
				break;
			case "5":
				keycode = KeyCode.NUMPAD5;
				break;
			case "6":
				keycode = KeyCode.NUMPAD6;
				break;
			case "7":
				keycode = KeyCode.NUMPAD7;
				break;
			case "8":
				keycode = KeyCode.NUMPAD8;
				break;
			case "9":
				keycode = KeyCode.NUMPAD9;
				break;
		}

		addEventHandler(button, keycode);
	}

	protected void addEventHandlerComma(Button button) {
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				robot.keyType(KeyCode.COMMA, "" + FormatUtils.getInstance().getDecimalSeparator());
			}
		};
		button.setOnAction(eventHandler);
	}

	protected void addEventHandlerOK(Button button) {
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				robot.keyType(KeyCode.ENTER);
			}
		};
		button.setOnAction(eventHandler);
	}

	protected void addEventHandlerMultiply(Button button) {
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(ActionEvent e) {
				robot.keyType(KeyCode.MULTIPLY, "*");
			}
		};
		button.setOnAction(eventHandler);
	}

	protected void addEventHandlerClear(Button button) {
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				if(getScene().getFocusOwner() instanceof TextField) {
					robot.keyType(KeyCode.A, "A", true);
					robot.keyType(KeyCode.BACK_SPACE);
				}
			}
		};
		button.setOnAction(eventHandler);
	}

	protected void addEventHandlerDelete(Button button) {
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				robot.keyType(KeyCode.BACK_SPACE);
			}
		};
		button.setOnAction(eventHandler);
	}
	
	protected void addEventHandlerSubtract(Button button) {
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				robot.keyType(KeyCode.SUBTRACT, button.getText());
			}
		};
		button.setOnAction(eventHandler);
	}

	protected void addEventHandlerSearch(Button button) {
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				Keyboard keyboard = SpringContext.getBean(Keyboard.class);
				Node focusOwner = getScene().getFocusOwner();
				if (focusOwner instanceof TextInputControl) {
					keyboard.setPopupVisible(!Keyboard.getKeyboardPopup().isShowing(), (TextInputControl) focusOwner, getScene().getWindow());
				}
				else {
					keyboard.setPopupVisible(!Keyboard.getKeyboardPopup().isShowing(), null, getScene().getWindow());
				}
			}
		};
		button.setOnAction(eventHandler);
	}
	

	
}
