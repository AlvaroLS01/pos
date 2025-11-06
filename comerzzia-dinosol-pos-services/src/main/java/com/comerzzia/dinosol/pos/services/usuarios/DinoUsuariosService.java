package com.comerzzia.dinosol.pos.services.usuarios;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.core.util.base64.Base64Coder;
import com.comerzzia.core.util.criptografia.CriptoUtil;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionUsuario;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasena;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasenaRequest;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasenaResponse;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasenaUsuario;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.exceptions.PoliticaContrasenaBloqueadaException;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.exceptions.PoliticaContrasenaCaducaProximoException;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.exceptions.PoliticaContrasenaCaducadaException;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.exceptions.PoliticaContrasenaLoginIncorrectoException;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.perfiles.PerfilException;
import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import com.comerzzia.pos.services.core.usuarios.UsuariosServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.ClientBuilder;
import com.comerzzia.rest.client.exceptions.RestConnectException;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;
import com.comerzzia.rest.client.exceptions.RestTimeoutException;

@Component
@Primary
public class DinoUsuariosService extends UsuariosService {

	public static String POLITICA_PASSWORD_ACTIVA = "X_USUARIOS.POLITICA_PASSWORD_ACTIVA";
	public static String VIGENCIA_QR_LOGIN = "X_POS.VIGENCIA_QR_LOGIN";
	public static String PREFIJO_QR_LOGIN_CAJERO = "RDC";
	public static String SUFIJO_QR_LOGIN_CAJERO = "RDCEND";
	public static String FORMATO_FECHA_QR_LOGIN = "ddMMyyyyHHmm";
	public static String ESTADO_ACTIVO = "0";
	public static String ESTADO_CADUCADO = "1";
	public static String ESTADO_BLOQUEADO = "2";

	@Autowired
	protected AuditoriasService auditoriasService;
	@Autowired
	protected VariablesServices variablesServices;

	@Override
	public UsuarioBean login(String usuario, String password) throws UsuarioInvalidLoginException, UsuariosServiceException {
		UsuarioBean user = null;

		Boolean tienePoliticaCambio = variablesServices.getVariableAsBoolean(POLITICA_PASSWORD_ACTIVA);
		PoliticaContrasenaResponse response;
		try {
			PoliticaContrasenaRequest request = new PoliticaContrasenaRequest();
			PoliticaContrasenaUsuario politicaContrasenaUsuario = new PoliticaContrasenaUsuario();

			politicaContrasenaUsuario.setClave(CriptoUtil.cifrar(CriptoUtil.ALGORITMO_MD5, password.getBytes()));

			politicaContrasenaUsuario.setUsuario(usuario.toUpperCase());
			politicaContrasenaUsuario.setUidInstancia(sesion.getAplicacion().getUidInstancia());
			request.setUsuario(politicaContrasenaUsuario);
			request.setTienePoliticaCambio(tienePoliticaCambio);

			response = llamarLoginCentral(request);

			controlLoginCentral(response);

			user = parseUsuarioBean(response.getUsuario());

			crearAuditoriaLogin(user);
		}
		catch (RestHttpException | RestException e) {
			log.error("login() - Ha habido un error al conectar con la central para hacer el login: " + e.getMessage(), e);
			log.debug("login() - Se hará el login en local");
			UsuarioBean usuarioLocal = super.login(usuario, password);
			if (usuarioLocal != null) {
				crearAuditoriaLogin(usuarioLocal);
			}
			
			return usuarioLocal;
		}
		catch (PoliticaContrasenaLoginIncorrectoException e) {
			String msg = "Se ha producido un error realizando login de usuario: " + usuario + " : " + e.getMessage();
			log.error("login() - " + msg, e);
			throw new PoliticaContrasenaLoginIncorrectoException(e.getMessage(), e);
		}
		catch (PoliticaContrasenaBloqueadaException e) {
			String msg = "Se ha producido un error realizando login de usuario: " + usuario + " : " + e.getMessage();
			log.error("login() - " + msg, e);
			throw new PoliticaContrasenaBloqueadaException(e.getMessage(), e);
		}
		catch (PoliticaContrasenaCaducadaException e) {
			String msg = "Se ha producido un error realizando login de usuario: " + usuario + " : " + e.getMessage();
			log.error("login() - " + msg, e);
			throw new PoliticaContrasenaCaducadaException(e.getMessage(), e);
		}
		catch (PoliticaContrasenaCaducaProximoException e) {
			String msg = "Se ha producido un error realizando login de usuario: " + usuario + " : " + e.getMessage();
			log.error("login() - " + msg, e);
			throw new PoliticaContrasenaCaducaProximoException(e.getMessage(), e);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error realizando login de usuario: " + usuario + " : " + e.getMessage();
			log.error("login() - " + msg, e);
			throw new UsuariosServiceException("error.service.core.usuarios.service.login", e);
		}

		return user;
	}

	@Override
	public void cambiarPassword(Long idUsuario, String password, String nuevaPassword) throws UsuarioInvalidLoginException, UsuariosServiceException {
		super.cambiarPassword(idUsuario, password, nuevaPassword);

		AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("CAMBIO DE CONTRASEÑA");
		auditoria.setCajeroOperacion(sesion.getSesionUsuario().getUsuario().getUsuario());
		auditoriasService.guardarAuditoria(auditoria);
	}

	protected PoliticaContrasenaResponse llamarLoginCentral(PoliticaContrasenaRequest request) throws RestHttpException, RestConnectException, RestTimeoutException, RestException {
		log.debug("llamarLoginCentral() - Realizando petición de login en central para el usuario " + request.getUsuario().getUsuario());

		GenericType<JAXBElement<PoliticaContrasenaResponse>> genericType = new GenericType<JAXBElement<PoliticaContrasenaResponse>>(){
		};

		PoliticaContrasenaResponse response = null;
		try {
			String restUrl = variablesServices.getVariableAsString(VariablesServices.REST_URL);
			String uidActividad = sesion.getAplicacion().getUidActividad();
			String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);

			WebTarget target = ClientBuilder.getClient().target(restUrl).path("/dinosol/politica/login");

			log.info("llamarLoginCentral() - URL de servicio rest en la que se realiza la petición: " + target.getUri());

			JAXBElement<PoliticaContrasenaRequest> jaxbElement = new JAXBElement<PoliticaContrasenaRequest>(new QName("request"), PoliticaContrasenaRequest.class, request);
			response = target.request().header("apiKey", apiKey).header("uidActividad", uidActividad).post(Entity.entity(jaxbElement, MediaType.APPLICATION_XML), genericType).getValue();
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

	private Boolean controlLoginCentral(PoliticaContrasenaResponse response)
	        throws PoliticaContrasenaLoginIncorrectoException, PoliticaContrasenaBloqueadaException, PoliticaContrasenaCaducadaException, PoliticaContrasenaCaducaProximoException, PerfilException {
		Boolean correcto = Boolean.FALSE;
		if (response != null) {
			if (!response.isLoginCorrecto()) {
				log.error("controlLoginCentral() - Login incorrecto" );
				
				if(response.getCodError() != null && response.getCodError().equals(404) && StringUtils.isNotBlank(response.getMensaje())) {
					throw new PoliticaContrasenaLoginIncorrectoException(response.getMensaje());
				}
				
				if (response.getUsuarioPoliticaCambio() != null && response.getUsuarioPoliticaCambio().getEstado().equals(ESTADO_BLOQUEADO)) {
					/* Usuario bloqueada */
					AuditoriaDto auditoria = new AuditoriaDto();
					auditoria.setTipo("USUARIO BLOQUEADO");
					auditoria.setCajeroOperacion(response.getUsuario().getUsuario());
					auditoriasService.guardarAuditoria(auditoria);
					
					log.error("controlLoginCentral() - Usuario bloqueado" );
					throw new PoliticaContrasenaBloqueadaException(I18N.getTexto("Usuario bloqueado, contacte con su responsable."));
				}
				
				throw new PoliticaContrasenaLoginIncorrectoException(I18N.getTexto("Login incorrecto. Vuelve a intentarlo"));
			}
			else {
				if (response.getUsuarioPoliticaCambio() != null) {
					PoliticaContrasena politica = response.getUsuarioPoliticaCambio();

					if (politica.getEstado().equals(ESTADO_BLOQUEADO)) {
						/* Usuario bloqueada */
						AuditoriaDto auditoria = new AuditoriaDto();
						auditoria.setTipo("USUARIO BLOQUEADO");
						auditoria.setCajeroOperacion(response.getUsuario().getUsuario());
						auditoriasService.guardarAuditoria(auditoria);
						
						log.error("controlLoginCentral() - Usuario bloqueado" );
						throw new PoliticaContrasenaBloqueadaException(I18N.getTexto("Usuario bloqueado, contacte con su responsable."));
					}
					else {
						((DinoSesionUsuario) sesion.getSesionUsuario()).initUsuarioOnline(response.getUsuario(), politica);
						/* Usuario caducado */
						if (politica.getEstado().equals(ESTADO_CADUCADO)) {
							log.error("controlLoginCentral() - Usuario caducado con fecha de caducidad " + politica.getFechaCaducidad());
							throw new PoliticaContrasenaCaducadaException(I18N.getTexto("Usuario caducado, por favor cambie su contraseña."));
						}
						else { /* Usuario activo */
							correcto = Boolean.TRUE;
							log.debug("controlLoginCentral() - Login Correcto" );
							if (response.isProximaCaducidad()) {
								Date fechaCaducidad = politica.getFechaCaducidad();
								String fechaCaducidadString = FormatUtil.getInstance().formateaFecha(fechaCaducidad);
								log.warn("controlLoginCentral() - La contraseña caducará el " + fechaCaducidadString );
								throw new PoliticaContrasenaCaducaProximoException(I18N.getTexto("La contraseña caducará el " + fechaCaducidadString + ". ¿Desea actualizar ahora la contraseña?"));
							}
						}
					}
				}
			}
		}

		return correcto;
	}

	private UsuarioBean parseUsuarioBean(PoliticaContrasenaUsuario politicaContrasenaUsuario) {
		UsuarioBean usuario = new UsuarioBean();

		usuario.setActivo(true);
		usuario.setClave(politicaContrasenaUsuario.getClave());
		usuario.setIdUsuario(politicaContrasenaUsuario.getIdUsuario());
		usuario.setUidInstancia(politicaContrasenaUsuario.getUidInstancia());
		usuario.setUsuario(politicaContrasenaUsuario.getUsuario());
		usuario.setDesusuario(politicaContrasenaUsuario.getDesUsuario());

		return usuario;
	}
	

	public PoliticaContrasenaResponse llamarCentralEstadoCajero(PoliticaContrasenaRequest request) throws RestHttpException, RestConnectException, RestTimeoutException, RestException {
		log.debug("llamarCentralEstadoCajero() - Realizando petición de login en central para el usuario " + request.getUsuario().getUsuario());

		GenericType<JAXBElement<PoliticaContrasenaResponse>> genericType = new GenericType<JAXBElement<PoliticaContrasenaResponse>>(){
		};

		PoliticaContrasenaResponse response = null;
		try {
			String restUrl = variablesServices.getVariableAsString(VariablesServices.REST_URL);
			String uidActividad = sesion.getAplicacion().getUidActividad();
			String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);

			WebTarget target = ClientBuilder.getClient().target(restUrl).path("/dinosol/politica/estado");
			log.info("llamarCentralEstadoCajero() - URL de servicio rest en la que se realiza la petición: " + target.getUri());

			JAXBElement<PoliticaContrasenaRequest> jaxbElement = new JAXBElement<PoliticaContrasenaRequest>(new QName("request"), PoliticaContrasenaRequest.class, request);
			response = target.request().header("apiKey", apiKey).header("uidActividad", uidActividad).post(Entity.entity(jaxbElement, MediaType.APPLICATION_XML), genericType).getValue();
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
	
	/**
	 * Formato del qr -> (Prefijo) RDC - (Número empleado) - (Fecha vigencia y hora) ddMMyyyyHHmm - Número empleado autorizado operación 
	 * @param usuarioQR, usuarioOperadorAutorizado
	 * @return String QR
	 * @throws Exception 
	 */
	public String generarQRUsuario(String usuarioQR, String usuarioOperadorAutorizado) throws Exception {
		log.debug("generarQRUsuario() - Generando código QR para el usuario");
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA_QR_LOGIN);
		Integer minutosVigencia = variablesServices.getVariableAsInteger(VIGENCIA_QR_LOGIN);
		if(minutosVigencia == null) {
			minutosVigencia = Integer.valueOf(190);
		}
		Date hoy = new Date();
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(hoy);
        calendar.add(Calendar.MINUTE, minutosVigencia);
        
		String qr = PREFIJO_QR_LOGIN_CAJERO + "-" + usuarioQR + "-"+ sdf.format(calendar.getTime()) + "-" + usuarioOperadorAutorizado + "-" + SUFIJO_QR_LOGIN_CAJERO;
		log.debug("generarQRUsuario() - QR generado: " + qr);
		Base64Coder b64 = new Base64Coder(Base64Coder.UTF8);
		return b64.encodeBase64(qr);
	}

	
	public void cambiarPasswordUsuarioLocal(String uidInstancia, Long idUsuario, String clave) {
		log.debug("cambiarPasswordUsuarioLocal()");
		UsuarioBean usuarioCambioClave = new UsuarioBean();
		usuarioCambioClave.setUidInstancia(uidInstancia);
		usuarioCambioClave.setIdUsuario(idUsuario);
		usuarioCambioClave.setClave(clave);

		log.debug("cambiarPasswordUsuarioLocal() - actualizando clave para el usuario " + idUsuario);
		usuarioMapper.updateByPrimaryKeySelective(usuarioCambioClave);
	}
	
	private void crearAuditoriaLogin(UsuarioBean user) {
		AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("LOGIN");
		auditoria.setCajeroOperacion(user.getUsuario());
		auditoriasService.guardarAuditoria(auditoria);
	}
}
