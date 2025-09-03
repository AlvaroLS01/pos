package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email.seleccion;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email.EmailConstants;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

@Controller
public class EmailSeleccionController extends WindowController {

	protected Logger log = Logger.getLogger(EmailSeleccionController.class);

	@FXML
	protected AnchorPane apCorreo;
	@FXML
	protected Label lbEmail;
	@FXML
	protected TextField tfEmail;
	@FXML
	protected Label lbTextError;

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

		if (getDatos().containsKey(EmailConstants.PARAM_EMAIL_FIDELIZADO_API) && StringUtils.isNotBlank((String) getDatos().get(EmailConstants.PARAM_EMAIL_FIDELIZADO_API))) {
			lbEmail.setText(I18N.getTexto("Correo del cliente"));
			tfEmail.setText((String) getDatos().get(EmailConstants.PARAM_EMAIL_FIDELIZADO_API));
            tfEmail.setEditable(true);

		} else {
			lbEmail.setText(I18N.getTexto("Introduzca un correo"));
			tfEmail.setText("");
			tfEmail.setEditable(true);
		}

		apCorreo.setVisible(false);
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
		if (StringUtils.isBlank(tfEmail.getText())) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha encontrado el email del fidelizado, puede escribir su email a mano"), getStage());
			tfEmail.setEditable(true);
			return;
		}
	
		if (EmailConstants.EMAIL_REGEX.matcher(tfEmail.getText()).find()) {
			getDatos().put(EmailConstants.PARAM_EMAIL_FIDELIZADO_MODAL, tfEmail.getText());
			getStage().close();
		}
		else {
			lbTextError.setVisible(true);
		}
	}

	@FXML
	public void accionImprimir() {
		log.debug("accionImprimir()");
		getDatos().put(EmailConstants.ACCION_SELECCIONADA, EmailConstants.ACCION_IMPRIMIR);
		getStage().close();

	}

	@FXML
	public void accionEmail() {
		log.debug("accionEmail()");
		getDatos().put(EmailConstants.ACCION_SELECCIONADA, EmailConstants.ACCION_EMAIL);
		apCorreo.setVisible(true);

	}

	@FXML
	public void accionImprimirEmail() {
		log.debug("accionImprimirEmail()");
		getDatos().put(EmailConstants.ACCION_SELECCIONADA, EmailConstants.ACCION_IMPRIMIR_EMAIL);
		apCorreo.setVisible(true);
	}
	
	@Override
	public void accionCancelar() {
		log.debug("accionCancelar()");
		getDatos().put(EmailConstants.ACCION_SELECCIONADA, EmailConstants.ACCION_CANCELAR);
		super.accionCancelar();
	}

}
