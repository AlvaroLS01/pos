package com.comerzzia.dinosol.pos.gui.configuracion;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasenaRequest;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasenaResponse;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasenaUsuario;
import com.comerzzia.dinosol.pos.services.usuarios.DinoUsuariosService;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.client.exceptions.RestConnectException;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;
import com.comerzzia.rest.client.exceptions.RestTimeoutException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class GenerarQRCajeroController extends Controller {

	private static Logger log = Logger.getLogger(GenerarQRCajeroController.class);

	private static String PLANTILLA_LOGIN_CAJERO_QR = "login_cajero_qr";

	@FXML
	protected TextField tfUsuario;
	@FXML
	protected Label lbMensajeError;

	@FXML
	protected Button btGenerarQR;

	@Autowired
	private Sesion sesion;

	@Autowired
	private DinoUsuariosService dinoUsuariosService;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfUsuario.setUserData(keyboardDataDto);

		addSeleccionarTodoEnFoco(tfUsuario);
	}

	@Override
	public void initializeFocus() {
		tfUsuario.requestFocus();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		tfUsuario.setText("");
		lbMensajeError.setText("");

	}

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER && !btGenerarQR.isDisable()) {
			accionGenerarQR();
		}
	}

	@FXML
	private void accionGenerarQR() {
		lbMensajeError.setText("");
		if (validaTextField()) {
			new UpdateUserState().start();
		}
		else {
			lbMensajeError.setText(I18N.getTexto("El campo usuario no puede estar vacío."));
		}
	}

	private boolean validaTextField() {
		return StringUtils.isNotBlank(tfUsuario.getText());
	}

	public class UpdateUserState extends BackgroundTask<PoliticaContrasenaResponse> {

		@Override
		protected PoliticaContrasenaResponse call() throws Exception {
			return updateUserState();
		}

		protected PoliticaContrasenaResponse updateUserState() throws RestHttpException, RestConnectException, RestTimeoutException, RestException {
			log.debug("updateUserState() - Realizando petición de cambio de estado QR");
			PoliticaContrasenaRequest request = new PoliticaContrasenaRequest();
			PoliticaContrasenaUsuario politicaContrasenaUsuario = new PoliticaContrasenaUsuario();
			String usuario = tfUsuario.getText();
			politicaContrasenaUsuario.setUsuario(usuario.toUpperCase());
			politicaContrasenaUsuario.setUidInstancia(sesion.getAplicacion().getUidInstancia());
			request.setUsuario(politicaContrasenaUsuario);
			return dinoUsuariosService.llamarCentralEstadoCajero(request);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			
			PoliticaContrasenaResponse response = getValue();
			if (response != null && response.getCodError() != null && response.getCodError() != 0) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(response.getMensaje()), getStage());
			}
			else {
				try {
					String qr = dinoUsuariosService.generarQRUsuario(tfUsuario.getText(), sesion.getSesionUsuario().getUsuario().getUsuario());
					log.debug("succeeded() - Código QR generado: " + qr);
					imprimirDatosLoginCajero(qr);
					tfUsuario.clear();
				}
				catch (Exception e) {
					log.error("succeeded() - Error generando el QR del usuario: " + tfUsuario.getText());
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al generar el "), e);
				}

			}
		}

		@Override
		protected void failed() {
			cerrarVentanaCargando();
			
			Throwable e = getException();
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al generar el QR de login de cajero. Contacte con un administrador."), e);
		}
	}

	private void imprimirDatosLoginCajero(String qr) {
		log.trace("imprimirDatosLoginCajero()");
		try {
			// Rellenamos los parametros
			Map<String, Object> contextoTicket = new HashMap<String, Object>();

			// Introducimos los parámetros que necesita el ticket para imprimir la información del cierre
			contextoTicket.put("usuarioAutorizador", sesion.getSesionUsuario().getUsuario().getUsuario());
			contextoTicket.put("usuarioQr", tfUsuario.getText());
			contextoTicket.put("qrCajero", qr);
			SimpleDateFormat df = new SimpleDateFormat();
			contextoTicket.put("fechaEmision", df.format(new Date()));

			// Llamamos al servicio de impresión
			ServicioImpresion.imprimir(PLANTILLA_LOGIN_CAJERO_QR, contextoTicket);

		}
		catch (DeviceException e) {
			log.error("imprimirDatosLoginCajero() - Error en la impresión de la boleta de QR de login de cajero: " + e.getMessage(), e);
		}
	}

}
