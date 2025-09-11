package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.observaciones;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class PagoObservacionesController extends WindowController {

	public static final String PAGO_OBSERVACIONES = "OBSERVACIONES";
	public static final String CANCELADO = "CANCELADO";

	@FXML
	protected TextArea tfObservaciones;

	@FXML
	protected Button btAceptar;

	@FXML
	protected Label lbTitulo, lbMensajeError;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		lbTitulo.setText(I18N.getTexto("INFORMACIÓN ADICIONAL"));

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registraEventoTeclado(tecla -> {
			if (tecla.getCode().equals(KeyCode.ENTER)) {
				accionAceptar();
				tecla.consume();
			}
		}, KeyEvent.KEY_RELEASED);
		registraEventoTeclado(tecla -> {
			if (tecla.getCode().equals(KeyCode.ESCAPE)) {
				accionAceptar();
			}
		}, KeyEvent.KEY_RELEASED);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {

		if (datos.containsKey(PAGO_OBSERVACIONES)) {
			tfObservaciones.setText((String) datos.get(PAGO_OBSERVACIONES));
			datos.remove(PAGO_OBSERVACIONES);
		}
		else {
			tfObservaciones.setText("");
		}

		lbMensajeError.setText("");

	}

	@Override
	public void initializeFocus() {
		tfObservaciones.requestFocus();
	}

	@FXML
	void accionAceptar() {

		String observaciones = tfObservaciones.getText();

		if (StringUtils.isNotBlank(observaciones)) {
			getDatos().put(PAGO_OBSERVACIONES, observaciones);
		}

		getStage().close();

	}
}
