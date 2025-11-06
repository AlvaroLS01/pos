package com.comerzzia.dinosol.pos.core.gui.main.cabecera.cambiopassword;

import java.awt.Toolkit;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.comerzzia.core.rest.client.usuarios.CambiarClaveUsuarioRequestRest;
import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.core.util.criptografia.CriptoUtil;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.core.gui.login.DinoLoginController;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasenaResponse;
import com.comerzzia.dinosol.pos.services.usuarios.DinoUsuariosService;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.main.cabecera.cambioPassword.CambioPasswordController;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.ClientBuilder;
import com.comerzzia.rest.client.exceptions.RestConnectException;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;
import com.comerzzia.rest.client.exceptions.RestTimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DinoCambioPasswordController extends CambioPasswordController {

	private static Logger log = Logger.getLogger(DinoCambioPasswordController.class);

	public static String POLITICA_PASSWORD_ACTIVA = "X_USUARIOS.POLITICA_PASSWORD_ACTIVA";

	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	protected AuditoriasService auditoriasService;
	
	protected boolean esAccionQR;

	@Override
	public void initializeComponents() {

		super.initializeComponents();
		
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setPintarPiePantalla(true);
		
		pfAntiguaPw.setUserData(keyboardDataDto);
		pfNuevaPw.setUserData(keyboardDataDto);
		pfConfirmPw.setUserData(keyboardDataDto);
		
	}
	
	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		/*
		 * DIN -119 - Permitir cambio de contraseña introduciendo QR sin tener que añadir la antigua
		 */
		if(getDatos().get(DinoLoginController.ACCION_CAMBIO_CONTRASENIA_QR) != null) {
			esAccionQR = true;
			pfAntiguaPw.setText("*********");
			pfAntiguaPw.setDisable(true);
		} else {
			esAccionQR = false;
			pfAntiguaPw.setDisable(false);			
		}
	}



	@Override
	public void accionAceptar() {
		String regex = "[a-zA-Z]";
		Pattern pattern = Pattern.compile(regex);
		try {
			String passAntigua = pfAntiguaPw.getText();
			String passNueva = pfNuevaPw.getText();
			String passConfirmar = pfConfirmPw.getText();

			if (passAntigua == null || passAntigua.isEmpty()) {
				Toolkit.getDefaultToolkit().beep();
				pfAntiguaPw.requestFocus();
				return;
			}
			if (passNueva == null || passNueva.isEmpty()) {
				Toolkit.getDefaultToolkit().beep();
				pfNuevaPw.requestFocus();
				return;
			}
			if (passConfirmar == null || passConfirmar.isEmpty()) {
				Toolkit.getDefaultToolkit().beep();
				pfConfirmPw.requestFocus();
				return;
			}

			if(passAntigua.equals(passNueva)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La nueva contraseña no puede ser igual que la actual"), getStage());
				pfNuevaPw.requestFocus();
				return;	
			}
			
			if (passNueva.length() < 6) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La longitud de la contraseña debe ser mínimo 6"), getStage());
				pfNuevaPw.requestFocus();
				return;
			}

			Matcher matcher = pattern.matcher(passNueva);
			if (!matcher.find()) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La contraseña tiene que tener al menos 1 carácter"), getStage());
				pfNuevaPw.requestFocus();
				return;
			}
			
			if (passNueva.toUpperCase().contains(sesion.getSesionUsuario().getUsuario().getUsuario().toUpperCase())) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La contraseña no puede coincidir o contener el número del operador"), getStage());
				pfNuevaPw.requestFocus();
				return;
			}

			if (!passNueva.equals(passConfirmar)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Los campos contraseña nueva y confirmar contraseña no coinciden"), getStage());
				pfNuevaPw.requestFocus();
				return;
			}

			CambiarClaveUsuarioRequestRest request = new CambiarClaveUsuarioRequestRest();
			/*
			 * DIN -119 - Permitir cambio de contraseña introduciendo QR sin tener que añadir la antigua
			 */
			if (!esAccionQR) {
				request.setClave(CriptoUtil.cifrar(CriptoUtil.ALGORITMO_MD5, passAntigua.getBytes("UTF-8")));
			}
			request.setClaveNueva(CriptoUtil.cifrar(CriptoUtil.ALGORITMO_MD5, passNueva.getBytes("UTF-8")));
			request.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
			request.setApiKey(variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY));
			request.setUidActividad(sesion.getAplicacion().getUidActividad());

			new UpdatePassCentral(request).start();

		}
		catch (Exception ex) {
			log.debug("actionBtAceptar() - Error no controlado -" + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(getStage(), ex);
		}

	}

	public class UpdatePassCentral extends BackgroundTask<PoliticaContrasenaResponse> {

		private CambiarClaveUsuarioRequestRest request;

		public UpdatePassCentral(CambiarClaveUsuarioRequestRest request) {
			super();
			this.request = request;
		}

		@Override
		protected PoliticaContrasenaResponse call() throws Exception {
			return updatePassCentral();
		}

		protected PoliticaContrasenaResponse updatePassCentral() throws Exception {
			log.debug("updatePassCentral() - Realizando petición de cambio de contraseña en central");
			
			GenericType<JAXBElement<PoliticaContrasenaResponse>> genericType = new GenericType<JAXBElement<PoliticaContrasenaResponse>>(){
			};

			PoliticaContrasenaResponse response = null;
			try {
				Boolean tienePoliticaCambio = variablesServices.getVariableAsBoolean(POLITICA_PASSWORD_ACTIVA);

				String restUrl = variablesServices.getVariableAsString(VariablesServices.REST_URL);
				String uidInstancia = sesion.getAplicacion().getUidInstancia();
				String uidActividad = sesion.getAplicacion().getUidActividad();
				String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);
				String usuario = request.getUsuario();

				if (esAccionQR) {
					PoliticaContrasenaResponse usuarioCentral = null;
					usuarioCentral = recuperarUsuarioCentral(restUrl, uidInstancia, uidActividad, apiKey, usuario);
					if (usuarioCentral == null || usuarioCentral.getUsuario() == null) {
						throw new Exception("updatePassCentral() - Ha ocurrido un error al intentar recuperar el usuario de central");
					}
					/* Seteamos la clave actual que esta en central a la request */
					request.setClave(usuarioCentral.getUsuario().getClave());
				}
								
				WebTarget target = ClientBuilder.getClient().target(restUrl).path("/dinosol/politica");

				log.info("updatePassCentral() - URL de servicio rest en la que se realiza la petición: " + target.getUri());

				JAXBElement<CambiarClaveUsuarioRequestRest> jaxbElement = new JAXBElement<CambiarClaveUsuarioRequestRest>(new QName("request"), CambiarClaveUsuarioRequestRest.class, request);
				response = target.request().header("apiKey", apiKey).header("uidInstancia", uidInstancia).header("tienePoliticaCambio", tienePoliticaCambio)
				        .post(Entity.entity(jaxbElement, MediaType.APPLICATION_XML), genericType).getValue();
			}
			catch (BadRequestException e) {
				throw RestHttpException.establecerException(e);
			}
			catch (WebApplicationException e) {
				throw new RestHttpException(e.getResponse().getStatus(),
				        "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
			catch (ProcessingException e) {
				if (e.getCause() instanceof ConnectException) {
					throw new RestConnectException("Se ha producido un error al conectar con el servidor - " + e.getLocalizedMessage(), e);
				}
				else if (e.getCause() instanceof SocketTimeoutException) {
					throw new RestTimeoutException("Se ha producido timeout al conectar con el servidor - " + e.getLocalizedMessage(), e);
				}
				throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
			return response;
		}
		
		private PoliticaContrasenaResponse recuperarUsuarioCentral(String restUrl, String uidInstancia, String uidActividad, String apiKey, String usuario) {
			log.debug("recuperarUsuarioCentral() - Realizando petición de cambio de contraseña en central");

			PoliticaContrasenaResponse response = null;
			GenericType<JAXBElement<PoliticaContrasenaResponse>> genericType = new GenericType<JAXBElement<PoliticaContrasenaResponse>>(){
			};

			WebTarget target = ClientBuilder.getClient().target(restUrl).path("/dinosol/politica/getUsuarioCentral");

			log.info("recuperarUsuarioCentral() - URL de servicio rest en la que se realiza la petición: " + target.getUri());

			JAXBElement<CambiarClaveUsuarioRequestRest> jaxbElement = new JAXBElement<CambiarClaveUsuarioRequestRest>(new QName("request"), CambiarClaveUsuarioRequestRest.class, request);
			response = target.request().header("apiKey", apiKey).header("uidInstancia", uidInstancia).header("uidActividad", uidActividad).header("usuario", usuario)
			        .post(Entity.entity(jaxbElement, MediaType.APPLICATION_XML), genericType).getValue();

			return response;
		}

		@Override
		protected void succeeded() {
			PoliticaContrasenaResponse response = getValue();
			if (response.getCodError() != null && response.getCodError() != 0) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(response.getMensaje()), getStage());
			}
			else {
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Clave cambiada correctamente"), getStage());
				
				AuditoriaDto auditoria = new AuditoriaDto();
				auditoria.setTipo("CAMBIO DE CONTRASEÑA");
				auditoria.setCajeroOperacion(sesion.getSesionUsuario().getUsuario().getUsuario());
				auditoriasService.guardarAuditoria(auditoria);

				((DinoUsuariosService) usuariosService).cambiarPasswordUsuarioLocal(sesion.getAplicacion().getUidInstancia(), sesion.getSesionUsuario().getUsuario().getIdUsuario(),
				        request.getClaveNueva());

				getDatos().put(DinoLoginController.CAMBIO_PASS_OK, Boolean.TRUE);
				getStage().close();
			}
			super.succeeded();
		}

		@Override
		protected void failed() {
			super.failed();

			Throwable e = getException();
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al realizar el cambio de contraseña. Contacte con un administrador."), e);
		}
	}
}
