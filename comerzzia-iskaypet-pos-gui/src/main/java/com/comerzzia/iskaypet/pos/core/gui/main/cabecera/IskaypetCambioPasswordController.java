package com.comerzzia.iskaypet.pos.core.gui.main.cabecera;

import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.ValidationDataRestException;
import com.comerzzia.core.util.criptografia.CriptoUtil;
import com.comerzzia.iskaypet.pos.persistence.usuarios.x.UsuarioX;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.NewPasswordConfirmEmptyException;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.NewPasswordEmptyException;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.NotMatchingException;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.SamePasswordException;
import com.comerzzia.iskaypet.pos.services.passwords.validacion.ValidacionPasswordService;
import com.comerzzia.iskaypet.pos.services.usuarios.x.UsuariosXServiceImpl;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.main.cabecera.cambioPassword.CambioPasswordController;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.fxml.FXML;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Date;

/*
 * ISK-386-gap-184-renovacion-de-contrasenas
 */

@Component
@Primary
public class IskaypetCambioPasswordController extends CambioPasswordController {

	public static final String PARAM_PANTALLA_ORIGEN = "pantallaOrigen"; // Pantalla origen, será null en estándar
	public static final String PANTALLA_ORIGEN_LOGIN = "LOGIN";

	public static final String USUARIO_CAMBIO_PASSWORD = "usuarioCambioPassword";
	public static final String USUARIO_PASSWORD_ANTIGUA = "usuarioPasswordAntigua";
	public static final String HA_CAMBIADO_PASSWORD = "haCambiadoPassword";

	private static Logger log = Logger.getLogger(IskaypetCambioPasswordController.class);

	private String usuario, textoLbUsuario, passAntigua;

	@Autowired
	protected ValidacionPasswordService validacionPasswordService;
	@Autowired
	protected UsuariosXServiceImpl usuarioXService;

	@Override
	public void initializeForm() throws InitializeGuiException {

		if (datos == null || datos.get(PARAM_PANTALLA_ORIGEN) == null || !(datos.get(PARAM_PANTALLA_ORIGEN) instanceof String) || StringUtils.isEmpty((String) datos.get(PARAM_PANTALLA_ORIGEN))) {
			// Si no hay param, viene de estándar, por lo que se conserva comportamiento anterior
			usuario = sesion.getSesionUsuario().getUsuario().getUsuario();
			textoLbUsuario = sesion.getSesionUsuario().getUsuario().getDesusuario();
		}
		else if (PANTALLA_ORIGEN_LOGIN.equals(datos.get(PARAM_PANTALLA_ORIGEN))) {
			textoLbUsuario = I18N.getTexto("Su contraseña ha expirado, debe renovarla");
			usuario = (String) datos.get(USUARIO_CAMBIO_PASSWORD);
			passAntigua = (String) datos.get(USUARIO_PASSWORD_ANTIGUA);
		}

		lbUsuario.setText(textoLbUsuario);
		pfNuevaPw.setText("");
		pfConfirmPw.setText("");

	}
	
	@Override
    public void initializeFocus() {
		pfNuevaPw.requestFocus();
    }

	@FXML
	@Override
	public void accionAceptar() {
		String passNueva = pfNuevaPw.getText();
		String passConfirmar = pfConfirmPw.getText();

		boolean pwCambiada;
		try {

			boolean passwordValido = validaCambioPassword(passAntigua, passNueva, passConfirmar);
			if (!passwordValido) {
				return;
			}

			pwCambiada = sesion.getSesionUsuario().cambiarPassword(CriptoUtil.cifrar(CriptoUtil.ALGORITMO_MD5, passNueva.getBytes("UTF-8")),
			        CriptoUtil.cifrar(CriptoUtil.ALGORITMO_MD5, passAntigua.getBytes("UTF-8")), usuario.toUpperCase());

			if (pwCambiada) {
				UsuarioBean usuarioBean = usuariosService.consultarUsuario(usuario);
				usuariosService.cambiarPassword(usuarioBean.getIdUsuario(), passAntigua, passNueva);
				
				// Cambiamos la fecha de la clave
				UsuarioX usuarioX = usuarioXService.getUsuarioX(usuarioBean.getIdUsuario());
				if (usuarioX == null) {
					usuarioX = new UsuarioX();
					usuarioX.setUidInstancia(usuarioBean.getUidInstancia());
					usuarioX.setIdUsuario(usuarioBean.getIdUsuario());
					usuarioX.setFechaPassword(new Date());
					usuarioXService.insertUsuarioX(usuarioX);
				} else {
					usuarioX.setFechaPassword(new Date());
					usuarioXService.updateUsuarioX(usuarioX);
				}

				getDatos().put(HA_CAMBIADO_PASSWORD, "S");

				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Clave cambiada correctamente"), getStage());
				getStage().close();
			}
			else {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pudo cambiar el password"), getStage());
			}

		}
		catch (ValidationDataRestException ex) {
			if (ex.getCodError() == 5) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La clave actual no es correcta"), getStage());
			}
		}
		catch (RestException | RestHttpException e) {
			log.error("accionAceptar() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se pudo cambiar el password: No hay conexión con central."), e);
		}
		catch (Exception e) {
			log.error("accionAceptar() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se pudo cambiar el password"), e);
		}
	}

	protected boolean validaCambioPassword(String passAntigua, String passNueva, String passConfirmar) {
		log.debug("validaCambioClave() - Validando cambio de clave...");

		try {
			validacionPasswordService.validaCambioPassword(passAntigua, passNueva, passConfirmar);
			log.debug("validaCambioClave() - Cambio clave validado con éxito");
			return true;
		}
		catch (Exception e) {
			Toolkit.getDefaultToolkit().beep();
			trataErrorCambioClave(e);
			return false;
		}
	}

	protected void trataErrorCambioClave(Exception e) {
		log.debug("trataErrorCambioClave() - Tratando error en cambio de clave");

		if (e instanceof SamePasswordException) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La contraseña actual no puede ser la misma que la anterior"), getStage());
			pfNuevaPw.requestFocus();
		}
		else if(e instanceof NotMatchingException) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Los campos contraseña nueva y confirmar contraseña no coinciden"), getStage());
			pfConfirmPw.requestFocus();
		}
		else if (e instanceof NewPasswordEmptyException) {
			pfNuevaPw.requestFocus();
		}
		else if (e instanceof NewPasswordConfirmEmptyException) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Los campos contraseña nueva y confirmar contraseña no coinciden"), getStage());
			pfConfirmPw.requestFocus();
		}

	}

}
