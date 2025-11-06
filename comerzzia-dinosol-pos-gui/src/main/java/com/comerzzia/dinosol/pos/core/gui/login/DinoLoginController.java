package com.comerzzia.dinosol.pos.core.gui.login;

import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.base64.Base64Coder;
import com.comerzzia.core.util.base64.Base64DecoderException;
import com.comerzzia.dinosol.pos.core.gui.logout.ConfirmacionPasswordLogoutController;
import com.comerzzia.dinosol.pos.core.gui.logout.ConfirmacionPasswordLogoutView;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.cajas.DinoCajasService;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionAplicacion;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionCaja;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionUsuario;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.exceptions.PoliticaContrasenaCaducaProximoException;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.exceptions.PoliticaContrasenaCaducadaException;
import com.comerzzia.dinosol.pos.services.payments.methods.types.sipay.SipayService;
import com.comerzzia.dinosol.pos.services.usuarios.DinoUsuariosService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.keyboard.Keyboard;
import com.comerzzia.pos.core.gui.login.LoginController;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuariosView;
import com.comerzzia.pos.core.gui.main.MainViewController;
import com.comerzzia.pos.core.gui.main.cabecera.cambioPassword.CambioPasswordView;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.persistence.core.acciones.AccionBean;
import com.comerzzia.pos.persistence.core.acciones.POSAccionMapper;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.permisos.PermisoException;
import com.comerzzia.pos.services.core.permisos.PermisosEfectivosAccionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;
import com.comerzzia.pos.services.core.usuarios.UsuarioNotFoundException;
import com.comerzzia.pos.services.core.usuarios.UsuariosServiceException;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

@Component
@Primary
public class DinoLoginController extends LoginController {

	private Logger log = Logger.getLogger(DinoLoginController.class);

	public static String ESTADO_ACTIVO = "0";
	public static String ESTADO_CADUCADO = "1";
	public static String ESTADO_BLOQUEADO = "2";
	
	public static String ACCION_CAMBIO_CONTRASENIA_QR = "ACCION_CAMBIO_CONTRASENA_QR";

	public static String POLITICA_PASSWORD_ACTIVA = "X_USUARIOS.POLITICA_PASSWORD_ACTIVA";
	
	public static String CAMBIO_PASS_OK = "cambioPassOK";

	@Autowired
	protected Sesion sesion;
	@Autowired
	private DinoCajasService cajasService;
	@Autowired
	private POSAccionMapper accionMapper;
	
	@Autowired
	private AuditoriasService auditoriasService;
	@Autowired
	private DinoUsuariosService dinoUsuariosService;
	
	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	@Autowired
	protected SipayService sipayService;
	
	private boolean enProceso;

	@Override
	public void accionCancelar() {
		log.debug("actionBtCancelar()");

		if (btCancelar.isDisabled()) {
			// Puede haber entrado al método al pulsar escape. No permitimos cerrar.
			return;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		getApplication().getMainView().showModalCentered(ConfirmacionPasswordLogoutView.class, params, getStage());
		if (!params.containsKey(ConfirmacionPasswordLogoutController.PARAM_CORRECTO) || !((boolean) params.get(ConfirmacionPasswordLogoutController.PARAM_CORRECTO))) {
			getApplication().desactivarTimer();
			return;
		}

		if (isModoSeleccionDeCajero()) { // Cerramos la ventana
			getStage().close();
		}
		else {
			if (AppConfig.loginBotonera) {
				getApplication().showFullScreenView(SeleccionUsuariosView.class);
			}
			else {
				Dispositivos.getInstance().liberarDispositivos();
				POSApplication.getInstance().getStage().close();
				System.exit(100);
			}
		}
	}

	@Override
	public void actionBtAceptar(ActionEvent event) {
		if(enProceso) {
			log.warn("actionBtAceptar() - Intentando login mientras está en proceso.");
			return;
		}
		
		try {
			enProceso = true;
			if(accionQRCajero()) {
				return;
			}
			if (!((DinoSesionAplicacion) sesion.getAplicacion()).isCajaMasterActivada()) {
				actionBtAceptarEstandar(event);
				return;
			}

			log.debug("actionBtAceptar()");
			if (accionFrLoginSubmit()) {

				// Comprobamos que el usuario tiene acceso a la pantalla activa
				if (!hasPermissionsCurrentScreen(tfUsuario.getText())) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El usuario no tiene permisos para ejecutar la pantalla activa."), getStage());
					return;
				}

				sesion.initUsuarioSesion(tfUsuario.getText(), tfPassword.getText());

				iniciarSesion();
			}
		}
		catch (SesionInitException ex) {
			VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageDefault(), ex);
			accionLimpiarFormulario();
			initializeFocus();
		}
		catch (UsuarioInvalidLoginException ex) {
			if (ex instanceof PoliticaContrasenaCaducadaException) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Usuario caducado, por favor cambie su contraseña."), getStage());

				getDatos().put("usuario", tfUsuario.getText());
				POSApplication posApplication = POSApplication.getInstance();
				posApplication.getMainView().showModalCentered(CambioPasswordView.class, getDatos(), getStage());

				if (getDatos().get(CAMBIO_PASS_OK) != null) {
					try {
						iniciarSesion();
					}
					catch (CajasServiceException | CajaEstadoException e) {
						log.error("actionBtAceptar() - Error no controlado -" + ex.getMessage(), ex);
						VentanaDialogoComponent.crearVentanaError(getStage(), ex);
					}
				}
			}
			else if (ex instanceof PoliticaContrasenaCaducaProximoException) {
				if (VentanaDialogoComponent.crearVentanaConfirmacion(ex.getMessage(), getStage())) {
					POSApplication posApplication = POSApplication.getInstance();
					posApplication.getMainView().showModalCentered(CambioPasswordView.class, getDatos(), getStage());
				}

				try {
					iniciarSesion();
				}
				catch (CajasServiceException | CajaEstadoException e) {
					log.error("actionBtAceptar() - Error no controlado -" + ex.getMessage(), ex);
					VentanaDialogoComponent.crearVentanaError(getStage(), ex);
				}
			}

			log.error("actionBtAceptar() - " + ex.getMessageI18N());
			lbError.setText(I18N.getTexto(ex.getMessageI18N()));
			tfPassword.clear();
			initializeFocus();
		}
		catch (Exception ex) {
			log.error("actionBtAceptar() - Error no controlado -" + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(getStage(), ex);
			tfPassword.clear();
		}
		finally {
			enProceso = false;
		}

	}

	protected void realizarAperturaCaja() throws SinPermisosException, CajasServiceException, CajaEstadoException {
		UsuarioBean usuario = sesion.getSesionUsuario().getUsuario();

		Caja cajaAbierta = consultarCajaAbierta();
		if (cajaAbierta != null) {
			if (!cajaAbierta.getUsuario().equals(usuario.getUsuario())) {
				compruebaPermisoSuplantar();
			}
		}
		else {
			boolean reintentar = true;
			boolean segundaConfirmacion = true;

			String textoAdic = "";

			while (segundaConfirmacion) {
				while (reintentar) {
					try {
						cajaAbierta = cajasService.pedirYGuardarCajaAbierta(usuario);
						reintentar = false;
						segundaConfirmacion = false;
					}
					catch (RestException e) {
						log.error("pedirCajaEnMaster() - Ha habido un error al transferir la caja a la caja máster: " + e.getCause().getMessage(), e);

						String mensaje = I18N.getTexto("No se ha podido establecer la comunicación con la caja máster. ¿Desea intentarlo de nuevo?.");

						VentanaDialogoComponent ventana = VentanaDialogoComponent.crearVentana(null, mensaje, VentanaDialogoComponent.TIPO_ERROR, true, getStage(), e, true);
						reintentar = ventana.isPulsadoAceptar();
					}
					catch (RestHttpException e) {
						log.error("transferirCajaAMaster() - Ha habido un error al transferir la caja a la caja máster: " + e.getCause().getMessage(), e);

						String mensaje = I18N.getTexto("Ha habido un problema al llamar a la caja máster para iniciar sesión. Contacte con un administrador.");

						Short codigoCajaAbierta = 7001;
						if (codigoCajaAbierta.equals(e.getCodError())) {
							textoAdic = e.getMessage() + System.lineSeparator() + System.lineSeparator();
							reintentar = false;
						}
						else {
							VentanaDialogoComponent ventana = VentanaDialogoComponent.crearVentana(null, mensaje, VentanaDialogoComponent.TIPO_ERROR, true, getStage(), e, true);
							reintentar = ventana.isPulsadoAceptar();
						}
					}
					catch (Exception e) {
						String mensaje = e.getCause().getMessage();
						if (e.getCause().getCause() instanceof IllegalStateException || e.getCause() instanceof NoRouteToHostException || e.getCause().getCause() instanceof NoRouteToHostException) {
							mensaje = I18N.getTexto("No se ha podido conectar al servidor de la caja máster.");
						}
						mensaje = mensaje + System.lineSeparator() + System.lineSeparator() + I18N.getTexto("¿Desea intentarlo de nuevo?");

						VentanaDialogoComponent ventana = VentanaDialogoComponent.crearVentana(null, mensaje, VentanaDialogoComponent.TIPO_ERROR, true, getStage(), e, true);
						reintentar = ventana.isPulsadoAceptar();
					}

					if (cajaAbierta != null) {
						if (StringUtils.isNotBlank(cajaAbierta.getUidDiarioCaja())) {
							((DinoSesionCaja) sesion.getSesionCaja()).setCajaAbierta(cajaAbierta);
							VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Se ha transferido su caja desde la caja máster."), getStage());
							reintentar = false;
							segundaConfirmacion = false;
						}
					}
				}

				if (cajaAbierta == null && segundaConfirmacion) {
					String textoSegundaConfirmacion = textoAdic + I18N.getTexto("Si continúa, se abrirá una caja no sincronizada con la caja máster, por lo que deberá") + System.lineSeparator()
					        + I18N.getTexto("realizar el cierre de caja en este terminal para poder cerrar la sesión en el futuro.") + System.lineSeparator() + System.lineSeparator()
					        + I18N.getTexto("¿Seguro que desea continuar?");
					if (VentanaDialogoComponent.crearVentanaConfirmacion(textoSegundaConfirmacion, getStage())) {
						reintentar = false;
						segundaConfirmacion = false;
					}
					else {
						throw new IllegalAccessError();
					}
				}
			}
		}

	}

	protected Caja consultarCajaAbierta() {
		try {
			return cajasService.consultarCajaAbierta();
		}
		catch (Exception e) {
			return null;
		}
	}

	protected void compruebaPermisoSuplantar() throws SinPermisosException {
		AccionBean accion = accionMapper.selectByPrimaryKey(AppConfig.accionInicio);
		try {
			PermisosEfectivosAccionBean permiso = sesion.getSesionUsuario().getPermisosEfectivos(accion);
			if (!permiso.isPuedeEjecucion("SUPLANTAR")) {
				log.info("compruebaPermisos() - Acción " + accion + " sin permiso de suplantar para usuario " + permiso.getIdUsuario());
				throw new SinPermisosException();
			}
		}
		catch (PermisoException ex) {
			log.error("compruebaPermisos() - Error consultando permisos en base de datos: " + ex.getMessage(), ex);
			throw new SinPermisosException();
		}
	}

	@Override
	public void initializeForm() {
		super.initializeForm();

		if (modoBloqueo != null && modoBloqueo) {
			tfUsuario.setText(sesionUsuario.getUsuario().getUsuario());
	    	
	    	AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setTipo("BLOQUEO DE CAJA");
			auditoria.setCajeroOperacion(sesion.getSesionUsuario().getUsuario().getUsuario());
			auditoriasService.guardarAuditoria(auditoria);
		}

		if (modoCambioUsuario != null && modoCambioUsuario) {
			tfUsuario.setText("");
			btCancelar.setDisable(true);
		}
	}

	@Override
	public void initializeFocus() {
		super.initializeFocus();

		if (modoCambioUsuario != null && modoCambioUsuario) {
			tfUsuario.requestFocus();
		}
	}

	private void actionBtAceptarEstandar(ActionEvent event) throws SesionInitException, UsuarioInvalidLoginException {
		log.debug("actionBtAceptar()");
		if (accionFrLoginSubmit()) {
			// Comprobamos que el usuario tiene acceso a la pantalla activa
			if (!hasPermissionsCurrentScreen(tfUsuario.getText())) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El usuario no tiene permisos para ejecutar la pantalla activa."), getStage());
				return;
			}
			sesion.initUsuarioSesion(tfUsuario.getText(), tfPassword.getText());
			sesion.getSesionUsuario().clearPermisos();
			getApplication().comprobarPermisosUI();

			((MainViewController) getApplication().getMainView().getController()).actualizarUsuario();
			
			// DIN-414-integracion-de-la-pasarela-sipay-en-el-pos
			inicializarPinpadSipay();
			
			if (isModoSeleccionDeCajero()) {
				getDatos().put(PARAMETRO_SALIDA_CAMBIO_USUARIO, "S");
				getStage().close();
			}
			else if (isModoBloqueo()) {
				getStage().close();
			}
			else {
				getApplication().showFullScreenView(mainView);
			}

		}
	}

	private void iniciarSesion() throws CajasServiceException, CajaEstadoException {
		// Si la caja no es maestra, se hace la petición a la misma por si tiene una caja abierta
		
		if (((DinoSesionAplicacion) sesion.getAplicacion()).isCajaMasterActivada()) {
			if (sesion.getAplicacion().getTiendaCaja().getIdTipoCaja() != 0L) {
				try {
					realizarAperturaCaja();
				}
				catch (IllegalAccessError e) {
					sesion.closeUsuarioSesion();
					return;
				}
				catch (SinPermisosException e) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La caja abierta actual no coincide con el cajero logueado."), getStage());
					return;
				}
			}
		}
		sesion.getSesionUsuario().clearPermisos();
		getApplication().comprobarPermisosUI();

		((MainViewController) getApplication().getMainView().getController()).actualizarUsuario();
		
		// DIN-414-integracion-de-la-pasarela-sipay-en-el-pos
		inicializarPinpadSipay();
		
		if (isModoSeleccionDeCajero()) {
			getDatos().put(PARAMETRO_SALIDA_CAMBIO_USUARIO, "S");
			getStage().close();
		}
		else if (isModoBloqueo()) {
			getApplication().activarTimer();
			this.getStage().close();
		}
		else {
			getApplication().showFullScreenView(mainView);
			getApplication().activarTimer();
		}
	}
	
	private boolean accionQRCajero() {
		log.debug("accionQRCajero() - Verificamos si es un código QR");
		lbError.setText("");
		String decodificado = "";
		Base64Coder b64Coder = new Base64Coder(Base64Coder.UTF8);
		try {
			decodificado = b64Coder.decodeBase64(tfUsuario.getText());
			// No es código QR
			if (!decodificado.startsWith(DinoUsuariosService.PREFIJO_QR_LOGIN_CAJERO)) {
				log.debug("accionQRCajero() - No es un QR de cajero");
				return false;
			}
			else {
				String[] split = decodificado.split("-");
				String usuarioQR = split[1];
				String fechaVigenciaQR = split[2];
				String usuarioAutorizado = split[3];

				// Comprobamos que trae usuario autorizado
				dinoUsuariosService.consultarUsuario(usuarioAutorizado);
				SimpleDateFormat sdf = new SimpleDateFormat(DinoUsuariosService.FORMATO_FECHA_QR_LOGIN);
				Date fecVigQR = sdf.parse(fechaVigenciaQR);
				if (fecVigQR.before(new Date())) {
					log.debug("accionQRCajero() - Este QR de autentificación ya no está vigente.");
					
					lbError.setText(I18N.getTexto("Este QR de autentificación ya no está vigente. Contacte con su responsable"));
				}
				else {
					pantallaCambioContrasenia(usuarioQR);
				}
				return true;
			}
		}
		catch (UnsupportedEncodingException | Base64DecoderException e) {
			log.error("accionQRCajero() - Ha fallado la decodificación del usuario " + e.getMessage(), e);
			return false;
		}
		catch (ParseException e) {
			log.error("accionQRCajero() - Ha fallado el parseo de la fecha del QR " + e.getMessage(), e);
			lbError.setText(I18N.getTexto("Código QR no válido. Contacte con el administrador."));
			return true;
		}
		catch (UsuarioNotFoundException | UsuariosServiceException e) {
			log.error("accionQRCajero() - Error consultando usuario autorizado en la generación del QR " + e.getMessage(), e);
			lbError.setText(I18N.getTexto("Código QR no válido. Contacte con el administrador."));
			return true;
		}
		catch (Exception e) {
			log.error("accionQRCajero() - Error: " + e.getMessage(), e);
			return false;
		}
		finally {
			Platform.runLater(new Runnable(){	
				@Override
				public void run() {
					tfUsuario.setText("");
					tfPassword.setText("");
				}
			});
		}
	}

	private void pantallaCambioContrasenia(String usuario) {
		log.debug("pantallaCambioContrasenia() - Se procede a mostrar la pantalla de cambio de contraseña.");

		try {
			SpringContext.getBean(Keyboard.class).setPopupVisible(false, tfUsuario, getStage(), true);
			
			UsuarioBean user = dinoUsuariosService.consultarUsuario(usuario);
			((DinoSesionUsuario) sesion.getSesionUsuario()).initUsuarioQR(user);
			
			tfUsuario.setText("");
			getDatos().put(ACCION_CAMBIO_CONTRASENIA_QR, true);
			POSApplication posApplication = POSApplication.getInstance();
			posApplication.getMainView().showModalCentered(CambioPasswordView.class, getDatos(), getStage());
			
			if (getDatos().get(CAMBIO_PASS_OK) != null) {
				iniciarSesion();
			}
		}
		catch (Exception e) {
			log.error("pantallaCambioContrasenia() - Ha habido un error al consultar el usuario: " + e.getMessage(), e);
			lbError.setText(I18N.getTexto("Ha habido un problema al realizar el proceso. Contacte con un administrador"));
			return;
		}
	}
	
	@Override
	public void initializeComponents() {
		super.initializeComponents();
		
		tfUsuario.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> paramObservableValue, String oldValue, String newValue) {
				Base64Coder b64Coder = new Base64Coder(Base64Coder.UTF8);
				try {
					String decodificado = b64Coder.decodeBase64(tfUsuario.getText());
					if (decodificado.startsWith(DinoUsuariosService.PREFIJO_QR_LOGIN_CAJERO) && decodificado.endsWith(DinoUsuariosService.SUFIJO_QR_LOGIN_CAJERO)) {
						actionBtAceptar(null);
					}
				}
				catch (Exception e) {
					log.trace("initializeComponents() - Ha habido un error al descodificar el campo: " + e.getMessage(), e);
				}
			}
		});
	}
	
	private void inicializarPinpadSipay() {
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getControlClass().equals("tefSipayManager")) {
				try {
					sipayService.inicializarDispositivo(true);
				} catch (Exception e) {
					log.error("inicializarPinpadSipay() - Error al inicializar dispositivo - " + e.getMessage());
				}
				
			}
		}
	}
	
}
