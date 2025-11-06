package com.comerzzia.dinosol.pos.gui.componentes.aviso;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
public class AvisoComponentController extends WindowController {
	
	private Logger log = Logger.getLogger(AvisoComponentController.class);

	public static String PARAM_AVISO_MENSAJE = "AvisoComponentController.Mensaje";
	public static String PARAM_AVISO_RESULTADO = "AvisoComponentController.Resultado";
	
	@FXML
	private Label lbMensaje;

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
		String mensaje = (String) getDatos().get(PARAM_AVISO_MENSAJE);
		
		log.debug("initializeForm() - Mensaje que se mostrará: " + mensaje);
		
		lbMensaje.setText(mensaje);
	}
	
	public void aceptar() {
		log.debug("aceptar() - El cajero acepta.");
		
		getDatos().put(PARAM_AVISO_RESULTADO, true);
		getStage().close();
	}
	
	public void cancelar() {
		log.debug("aceptar() - El cajero cancela.");
		
		getDatos().put(PARAM_AVISO_RESULTADO, false);
		getStage().close();
	}

}
