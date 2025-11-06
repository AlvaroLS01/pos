package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.cupones;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.Controller;

@Component
public class PantallaSecundariaInfoCuponesController extends Controller {
	
	@FXML
	private VBox vbNoAplicados;

	@Override
    public void initialize(URL url, ResourceBundle rb) {
    }

	@Override
    public void initializeComponents() throws InitializeGuiException {
		setShowKeyboard(false);
    }

	@Override
    public void initializeForm() throws InitializeGuiException {
		refrescarDatosPantalla();
    }

	public void refrescarDatosPantalla() {
	    vbNoAplicados.getChildren().clear();
		
		List<String> cuponesNoAplicados = DinoVisorPantallaSecundaria.getCuponesNoAplicados();
		
		for(String cuponNoAplicado : cuponesNoAplicados) {
			Label label = new Label(cuponNoAplicado);
			vbNoAplicados.getChildren().add(label);
		}
    }

	@Override
    public void initializeFocus() {
    }

}
