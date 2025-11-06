package com.comerzzia.dinosol.pos.gui.configuracion.mediospago.sipay.modooffline;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.payments.methods.types.sipay.ServicioConfiguracionSipay;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
public class ModoOfflineSipayController extends Controller {

	private Logger log = Logger.getLogger(ModoOfflineSipayController.class);
	@Autowired
	private ServicioConfiguracionSipay servicioConfiguracionSipay;

	@FXML
	private PasswordField tfPassword;

	@FXML
	private PasswordField tfPasswordRep;

	@FXML
	private Label lbError, lbModo;

	@FXML
	private Button btDesactivar, btCambioModo;

	@FXML
	private VBox vbEstado, vbFormulario;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registraEventoTeclado(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent tecla) {
				if (tecla.getCode().equals(KeyCode.ENTER) && !servicioConfiguracionSipay.esModoOfflineActivado()) {
					cambiarModo();
					tecla.consume();
				}
			}
		}, KeyEvent.KEY_RELEASED);

		refrescarPantalla();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		refrescarPantalla();
	}

	@Override
	public void initializeFocus() {
		tfPassword.requestFocus();
	}

	@FXML
	private void cambiarModo() {
		String pass = tfPassword.getText();
		String passRep = tfPasswordRep.getText();

		if (StringUtils.isNotEmpty(pass) && StringUtils.isNotEmpty(passRep) && StringUtils.equals(pass, passRep)) {
			try {
				if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("A continuación se ACTIVARÁ el modo offline en este equipo, ¿deseas continuar?"), getStage())) {
					servicioConfiguracionSipay.validarPassword(pass);
					refrescarPantalla();

				}
			}
			catch (Exception e) {
				log.error("cambiarModo() - Error al cargar al validar la contraseña: " + e.getMessage(), e);
				lbError.setText(e.getMessage());
			}
		}
		else {
			lbError.setText(I18N.getTexto("La contraseña introducida no es correcta"));
		}
	}

	@FXML
	private void desactivarModoOffline() {
		try {
			if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Estás seguro de que desea DESACTIVAR el modo offline?"), getStage())) {
				servicioConfiguracionSipay.operativaOffline(true);
				refrescarPantalla();
				getApplication().getMainView().close();

			}
		}
		catch (Exception e) {
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
		}
	}

	private void refrescarPantalla() {
		try {
			servicioConfiguracionSipay.recargarVariableOffline();
		}
		catch (Exception e) {
			log.error("comportamientoPantalla() - No se ha podido actualizar la variable, será creada en la comprobación.");
		}

		tfPassword.setText("");
		tfPasswordRep.setText("");
		lbError.setText("");

		boolean modoOfflineActivado = servicioConfiguracionSipay.esModoOfflineActivado();

		vbEstado.setVisible(modoOfflineActivado);
		vbFormulario.setVisible(!modoOfflineActivado);
		vbEstado.setManaged(modoOfflineActivado);
		vbFormulario.setManaged(!modoOfflineActivado);
		lbModo.setTextFill(modoOfflineActivado ? Color.DARKRED : Color.DARKGREEN);
		lbModo.setText(modoOfflineActivado ? I18N.getTexto("ACTIVADO") : I18N.getTexto("DESACTIVADO"));
	}

}
