package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email.confirmacion;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email.EmailConstants;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class EmailConfirmacionController extends WindowController {

	@FXML
	protected Label lbTextError;
	@FXML
	protected TextField tfEmail;

	protected String emailConfirmacion;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		// Evitar que se pueda copiar y pegar
		tfEmail.focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
		tfEmail.selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());

	}

	@Override
	public void initializeFocus() {

	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		emailConfirmacion = (String) getDatos().get(EmailConstants.PARAM_EMAIL_CONFIRMACION);
		tfEmail.setText("");
		lbTextError.setText("");
		lbTextError.setVisible(false);
	}

	@FXML
	public void accionAceptarIntro(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			accionAceptar();
		}
	}

	@FXML
	public void accionAceptar() {

		// Se verifica que el email tiene un formato correcto
		if (!EmailConstants.EMAIL_REGEX.matcher(tfEmail.getText()).find()) {
			lbTextError.setText(I18N.getTexto("El email no contiene un formato correcto"));
			lbTextError.setVisible(true);
			return;
		}

		// Se verifica que el email introducido es igual al rellenado en el paso previo
		if (!emailConfirmacion.equalsIgnoreCase(tfEmail.getText())) {
			lbTextError.setText(I18N.getTexto("El email introducido no coincide con el introducido en el paso previo"));
			lbTextError.setVisible(true);
			return;
		}

		getDatos().put(EmailConstants.PARAM_EMAIL_FIDELIZADO_MODAL, tfEmail.getText());
		getDatos().put(EmailConstants.ACCION_SELECCIONADA, EmailConstants.ACCION_ACEPTAR_CONFIRMACION);
		getStage().close();

	}

	@FXML
	public void accionVolver() {
		getDatos().put(EmailConstants.ACCION_SELECCIONADA, EmailConstants.ACCION_VOLVER_CONFIRMACION);
		getStage().close();
	}

}
