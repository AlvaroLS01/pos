package com.comerzzia.cardoso.pos.gui.configuracion.c3i.dialogo;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class AutotestDialogoController extends Controller {

	public static String PARAM_TITULO_ENTRADA = "PARAM_TITULO_ENTRADA";
	public static String PARAM_RESPUESTA_SALIDA = "PARAM_RESPUESTA_SALIDA";

	@FXML
	private TextField tfRespuesta;
	@FXML
	private Label lbTitulo;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {

	}

	@Override
	public void initializeFocus() {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		String titulo = (String) getDatos().get(PARAM_TITULO_ENTRADA);
		lbTitulo.setText(titulo);
		tfRespuesta.requestFocus();
	}

	public void accionAceptarIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			accionAceptar();
		}

	}

	public void accionAceptar() {
		String respuesta = tfRespuesta.getText();
		getDatos().put(PARAM_RESPUESTA_SALIDA, respuesta);
		tfRespuesta.clear();
		getStage().close();
	}
}
