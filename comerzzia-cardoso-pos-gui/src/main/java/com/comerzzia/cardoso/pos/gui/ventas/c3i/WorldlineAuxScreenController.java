package com.comerzzia.cardoso.pos.gui.ventas.c3i;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

@Component
@Scope("prototype")
public class WorldlineAuxScreenController extends Controller {

	@FXML
	private Button btAnular;
	@FXML
	private Button btCorregir;
	@FXML
	private Button btAceptar;
	@FXML
	private Button btCero;
	@FXML
	private Button btUno;
	@FXML
	private Button btDos;
	@FXML
	private Button btTres;
	@FXML
	private Button btCuatro;
	@FXML
	private Button btCinco;
	@FXML
	private Button btSeis;
	@FXML
	private Button btSiete;
	@FXML
	private Button btOcho;
	@FXML
	private Button btNueve;
	@FXML
	private TextField tfPosDisplay;

	private Button[] ANN_COR_VAL;
	private Button[] NUM;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		NUM = new Button[] { btCero, btUno, btDos, btTres, btCuatro, btCinco, btSeis, btSiete, btOcho, btNueve };
		ANN_COR_VAL = new Button[] { btAnular, btCorregir, btAceptar };
		tfPosDisplay.setEditable(false);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
	}

	public TextField getTfPosDisplay() {
		return tfPosDisplay;
	}

	public Button[] getANN_COR_VAL() {
		return ANN_COR_VAL;
	}

	public Button[] getNUM() {
		return NUM;
	}

}
