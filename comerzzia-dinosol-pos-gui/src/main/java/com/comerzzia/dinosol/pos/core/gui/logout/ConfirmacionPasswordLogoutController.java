package com.comerzzia.dinosol.pos.core.gui.logout;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class ConfirmacionPasswordLogoutController extends WindowController {

	private Logger log = Logger.getLogger(ConfirmacionPasswordLogoutController.class);

	public static final String PARAM_CORRECTO = "passwordCorrecta";

	public static String PASSWORD_CIERRE_POS = "X_USUARIOS.PASSWORD_CIERRE_POS";

	@Autowired
	private VariablesServices variablesServices;

	@FXML
	private PasswordField tfPassword;

	@FXML
	private Label lbError;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setPintarPiePantalla(true);
		tfPassword.setUserData(keyboardDataDto);
		addSeleccionarTodoEnFoco(tfPassword);

		registrarAccionCerrarVentanaEscape();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Abriendo pantalla de confirmación de salida del POS.");
		tfPassword.clear();
		lbError.setText("");
	}

	@Override
	public void initializeFocus() {
		tfPassword.requestFocus();
	}

	public void accionAceptarIntro(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			accionAceptar();
		}
	}

	public void accionAceptar() {
		String password = tfPassword.getText();

		if (StringUtils.isBlank(password)) {
			lbError.setText(I18N.getTexto("Debe indicar la contraseña de cierre para cerrar la aplicación."));
			return;
		}

		String passCierrePos = variablesServices.getVariableAsString(PASSWORD_CIERRE_POS);
		if (password.equals(passCierrePos)) {
			getDatos().put(PARAM_CORRECTO, true);
		}
		else {
			lbError.setText(I18N.getTexto("La contraseña introducida no es correcta."));
			initializeFocus();
			return;
		}
		
		getStage().close();
	}

	@Override
	public void accionCancelar() {
		getDatos().put(PARAM_CORRECTO, false);
		super.accionCancelar();
	}

}
