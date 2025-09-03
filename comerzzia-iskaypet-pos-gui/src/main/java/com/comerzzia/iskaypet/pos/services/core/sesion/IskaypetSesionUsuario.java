package com.comerzzia.iskaypet.pos.services.core.sesion;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.iskaypet.pos.persistence.usuarios.x.UsuarioX;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.FirstLoginException;
import com.comerzzia.iskaypet.pos.services.core.sesion.exception.PasswordExpiradoLoginException;
import com.comerzzia.iskaypet.pos.services.usuarios.x.UsuariosXServiceImpl;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.sesion.SesionUsuario;
import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;

/*
 * ISK-386-gap-184-renovacion-de-contrasenas
 */

@Component
@Primary
public class IskaypetSesionUsuario extends SesionUsuario {

	protected static final Logger log = Logger.getLogger(IskaypetSesionUsuario.class);
	
	public static final String VARIABLE_X_PASSWORD_EXPIRACION_ACTIVA = "X_PASSWORD.EXPIRACION_ACTIVA";	
	public static final String VARIABLE_X_PASSWORD_DIAS_RENOVACION = "X_PASSWORD.DIAS_RENOVACION";	
	public static final Integer VALOR_DEFECTO_DIAS_RENOVACION = 90;	

	@Autowired
	protected UsuariosXServiceImpl usuarioXService;
	
	@Override
	public void init(String usuario, String password) throws SesionInitException, UsuarioInvalidLoginException {
		log.debug("init() - Iniciando sesión...");
			
		super.init(usuario, password);

		// ISK-386-gap-184-renovacion-de-contrasenas
		boolean isExpiracionActiva = variablesServices.getVariableAsBoolean(VARIABLE_X_PASSWORD_EXPIRACION_ACTIVA, false);
		if (isExpiracionActiva) {
			validatePasswordExpiration(getUsuario());
		}

		log.debug("init() - Sesión iniciada correctamente");

	}

	private void validatePasswordExpiration(UsuarioBean usuario) throws UsuarioInvalidLoginException {
		log.debug("validatePasswordExpiration() - Validando expiración de contraseña...");

		try {

			// Se consulta en la nueva tabla si hay registro de clave para este usuario
			UsuarioX usuarioX = usuarioXService.getUsuarioX(usuario.getIdUsuario());

			// Si no hay registro aún es porque es su primer inicio de sesión, así que se considera como clave expirada
			if (usuarioX == null) {
				log.error("validatePasswordExpiration() - Primer inicio de sesión del usuario, se requiere la renovación de la contraseña");
				throw new FirstLoginException();
			}

			Integer diasExpiracionClave = getValorDefectoDiasRenovacion();

			// Si hay registro, se comprueba si la fecha de la última clave ha excedido el tiempo máximo de duración
			Fecha expiracionClave = new Fecha(usuarioX.getFechaPassword());
			expiracionClave.sumaDias(diasExpiracionClave);
			if (expiracionClave.antes(new Fecha())) {
				log.error("validatePasswordExpiration() - Contraseña expirada");
				throw new PasswordExpiradoLoginException();
			}

		} catch (FirstLoginException | PasswordExpiradoLoginException e) {
			// Excepción no controlada, se pasa a login en tienda
			log.error("validatePasswordExpiration() - Error iniciando sesión: " + e.getMessage(), e);
			throw new UsuarioInvalidLoginException(e);
		} catch (Exception e) {
			// Excepción no controlada, se pasa a login en tienda
			log.error("validatePasswordExpiration() - Error iniciando sesión: " + e.getMessage(), e);
		}
	}

	private Integer getValorDefectoDiasRenovacion() {
		Integer diasExpiracionClave = VALOR_DEFECTO_DIAS_RENOVACION;
		try {
			return variablesServices.getVariableAsInteger(VARIABLE_X_PASSWORD_DIAS_RENOVACION);
		} catch (Exception e) {
			log.debug("validatePasswordExpiration() - La variable " + VARIABLE_X_PASSWORD_DIAS_RENOVACION + " no está definida o no tiene valor, por lo que se le otorga su valor por defecto de " + VALOR_DEFECTO_DIAS_RENOVACION + " días");
			return  VALOR_DEFECTO_DIAS_RENOVACION;
		}
	}

}
