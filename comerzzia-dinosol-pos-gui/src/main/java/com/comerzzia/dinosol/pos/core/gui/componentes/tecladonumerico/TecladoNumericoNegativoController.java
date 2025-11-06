package com.comerzzia.dinosol.pos.core.gui.componentes.tecladonumerico;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;

import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.ComponentController;
import com.comerzzia.pos.core.gui.componentes.keyboard.Keyboard;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

public class TecladoNumericoNegativoController extends ComponentController implements Initializable {

	protected FXRobot robot;
    
    @FXML
    protected Button btSeparadorDecimal;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btSeparadorDecimal.setText(FormatUtil.getInstance().getDecimalSeparator()+"");
    }    

    @Override
    public void initializeForm() {
    }
    @Override
    public void initializeFocus() {
    }

    @Override
    public void initializeComponents() {
    }
    
    @Override
    public void initializeComponent() {
    }
    
	public void crearRobot(Scene scene){
        robot = FXRobotFactory.createRobot(scene);
    }
    
    @FXML
    public void actionButton1(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD1, "1");
    }
    
    @FXML
    public void actionButton2(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD2, "2");
    }
    
    @FXML
    public void actionButton3(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD3, "3");
    }
    
    @FXML
    public void actionButton4(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD4, "4");
    }
    
    @FXML
    public void actionButton5(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD5, "5");
    }
    
    @FXML
    public void actionButton6(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD6, "6");
    }
    
    @FXML
    public void actionButton7(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD7, "7");
    }
    
    @FXML
    public void actionButton8(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD8, "8");
    }
    
    @FXML
    public void actionButton9(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD9, "9");
    }
    
    @FXML
    public void actionButton0(ActionEvent event) {
        robot.keyType(KeyCode.NUMPAD0, "0");
    }
    
    @FXML
    public void actionButtonDelete(ActionEvent event) {
        robot.keyPress(KeyCode.BACK_SPACE);
    }
    
    @FXML
    public void actionButtonX(ActionEvent event) {
        robot.keyRelease(KeyCode.MULTIPLY);
    }
    
    @FXML
    public void actionButtonMenos(ActionEvent event) {
        robot.keyType(KeyCode.MINUS, "-");
    }
    
    @FXML
    public void actionButtonOK(ActionEvent event) {
        robot.keyType(KeyCode.ENTER, "\n");
        robot.keyRelease(KeyCode.ENTER);
    }
    
    @FXML
    public void actionButtonComa(ActionEvent event) {
        robot.keyType(KeyCode.COMMA, ""+FormatUtil.getInstance().getDecimalSeparator());
    }
    
    @FXML
    public void actionButtonABC(ActionEvent event) {
    	Keyboard keyboard = SpringContext.getBean(Keyboard.class);
    	Node focusOwner = POSApplication.getInstance().getScene().getFocusOwner();
    	if (focusOwner instanceof TextInputControl) {
    		keyboard.setPopupVisible(!Keyboard.getKeyboardPopup().isShowing(), (TextInputControl) focusOwner, getStage(), false);
    	} else {
    		keyboard.setPopupVisible(!Keyboard.getKeyboardPopup().isShowing(), null, getStage(), false);
    	}
    }
    
}
