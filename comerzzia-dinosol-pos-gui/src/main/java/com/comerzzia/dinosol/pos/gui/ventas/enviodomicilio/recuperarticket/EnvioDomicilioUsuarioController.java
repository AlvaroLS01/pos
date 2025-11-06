package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.recuperarticket;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.DomicilioApi;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.DomicilioOfflineDto;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.AltaUsuarioSADView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.key.EnvioDomicilioKeys;
import com.comerzzia.dinosol.pos.services.sherpa.SherpaApiBuilder;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.Variables;
import com.comerzzia.pos.util.i18n.I18N;

import feign.RetryableException;

/**
 * Esta es la pantalla en la que se busca al cliente por número de tarjeta, DNI o teléfono
 *
 */
@Component
public class EnvioDomicilioUsuarioController extends WindowController {

	@Autowired
	protected Sesion sesion;
	private Logger log = Logger.getLogger(EnvioDomicilioUsuarioController.class);

	@FXML
	private TextField tfNumeroTarjeta, tfDNI, tfTelefono;
	@FXML
	private Label lbTitulo, lbError;
	@FXML
	protected Button bAltaFidelizado;

	private DomicilioApi domicilioService;
	private DomicilioResponse domicilio;
	private List<DomicilioResponse> domicilios;

	@Autowired
	protected VariablesServices variablesServices;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		ImageView imagenInsertar = new ImageView("/skins/standard" + Variables.IMAGES_BASE_PATH + "iconos/add.png");
		bAltaFidelizado.setGraphic(imagenInsertar);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		limpiarDatosPantalla();
		/* Iniciamos al entrar en la pantalla los servicios de SAD */
		iniciarServicioSad();

		/* Cargamos el teclado de la pantalla */
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfNumeroTarjeta.setUserData(keyboardDataDto);
		tfDNI.setUserData(keyboardDataDto);
		tfTelefono.setUserData(keyboardDataDto);
	}

	@Override
	public void initializeFocus() {
		tfNumeroTarjeta.requestFocus();
	}

	public void limpiarDatosPantalla() {
		tfNumeroTarjeta.clear();
		tfDNI.clear();
		tfTelefono.clear();
		lbError.setText("");
	}

	/**
	 * Inicia los servicios de Sherpa/Domicilios
	 * 
	 * @throws InitializeGuiException
	 */
	public void iniciarServicioSad() throws InitializeGuiException {
		/* Iniciamos los servicios para cargar los domicilios */
		domicilioService = SherpaApiBuilder.getSherpaDomicilioApiService();
	}

	/**
	 * Busca el Fidelizado a partir del código de tarjeta en Sherpa(Segment) Lo devuelve a la pantalla de
	 * "Envío a domicilio".
	 */
	@FXML
	public void buscarUsuarioSad() {
		/* Limpiamos los errores y objetos */
		lbError.setText("");
		domicilio = null;
		domicilios = null;

		/* Por prioridad buscamos por Número de Tarjeta */
		if (StringUtils.isNotBlank(tfNumeroTarjeta.getText())) {

			new BuscarPorNumeroTarjetaRecuperadoTask().start();
			/* La siguiente prioridad es el DNI */
		}
		else if (StringUtils.isNotBlank(tfDNI.getText())) {

			new BuscarPorDniRecuperadoTask().start();
			/* Como última opción está el Teléfono */
		}
		else if (StringUtils.isNotBlank(tfTelefono.getText())) {

			new BuscarPorTlfRecuperadoTask().start();

		}
		else {
			String mensajeInfo = "Alguno de los campos debe estar relleno";
			log.info("buscarUsuarioSad() - " + mensajeInfo);
			lbError.setText(I18N.getTexto(mensajeInfo));
			return;
		}
	}

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			buscarUsuarioSad();
		}
	}

	private class BuscarPorNumeroTarjetaRecuperadoTask extends BackgroundTask<DomicilioResponse> {

		@Override
		protected DomicilioResponse call() {
			log.debug("BuscarPorNumeroTarjetaTask() - Iniciamos la consulta para traer un usuario con Número Tarjeta : " + tfDNI.getText());
			DomicilioResponse respuesta = null;
			try {
				String posType = SherpaApiBuilder.getSherpaPosType();
				String sherpaShop = sesion.getAplicacion().getCodAlmacen();
				String sherpaTpv = sesion.getAplicacion().getCodCaja();
				respuesta = domicilioService.getDomicilio(posType, sherpaShop, sherpaTpv, tfNumeroTarjeta.getText().trim());
			}
			catch (Exception e) {
				
				if(e instanceof RetryableException) {
					throw e;
				}
				
				/* Significa que no lo encuentra */
				if (e.getMessage().contains("404") || e.getMessage().contains("400")) {
					respuesta = new DomicilioResponse();
				}
			}
			return respuesta;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			if (getValue().getPersonData() != null) {
				domicilio = getValue();
			}
			else {
				domicilio = null;
			}
			log.debug("BuscarPorNumeroTarjetaTask() - El resultado de la consulta es : " + domicilio);

			comprobarBusquedaRealizada();
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			log.error("BuscarPorDniRecuperadoTask::failed() - Ha habido un error al consultar el fidelizado por el número de tarjeta: " + exception.getMessage(), exception);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al consultar el fidelizado por el número de tarjeta. Consulte con el administrador."), exception);
			
			cerrarPantallaTrasFalloConexion();
		}
	}

	private class BuscarPorDniRecuperadoTask extends BackgroundTask<List<DomicilioResponse>> {

		public BuscarPorDniRecuperadoTask() {
			super();
		}

		@Override
		protected List<DomicilioResponse> call() {
			log.debug("BuscarPorDniTask() - Iniciamos la consulta para traer un usuario con DNI : " + tfDNI.getText());
			List<DomicilioResponse> respuesta = null;
			try {
				String posType = SherpaApiBuilder.getSherpaPosType();
				String sherpaShop = sesion.getAplicacion().getCodAlmacen();
				String sherpaTpv = sesion.getAplicacion().getCodCaja();
				respuesta = domicilioService.searchDomicilioDNI(posType, sherpaShop, sherpaTpv, tfDNI.getText().trim(), "");
			}
			catch (Exception e) {
				log.error("BuscarPorDniTask() - " + e.getMessage());
				
				if(e instanceof RetryableException) {
					throw e;
				}
				
				/* Significa que no lo encuentra */
				if (e.getMessage().contains("404")) {
					respuesta = new ArrayList<DomicilioResponse>();
				}
			}
			return respuesta;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			domicilios = getValue();
			log.debug("BuscarPorDniTask() - El resultado de la consulta es : " + domicilios);

			comprobarBusquedaRealizada();
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			log.error("BuscarPorDniRecuperadoTask::failed() - Ha habido un error al consultar el fidelizado por el DNI: " + exception.getMessage(), exception);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al consultar el fidelizado por el DNI. Consulte con el administrador."), exception);
			
			cerrarPantallaTrasFalloConexion();
		}
	}

	private class BuscarPorTlfRecuperadoTask extends BackgroundTask<List<DomicilioResponse>> {

		public BuscarPorTlfRecuperadoTask() {
			super();
		}

		@Override
		protected List<DomicilioResponse> call() {
			log.debug("BuscarPorTlfTask() - Iniciamos la consulta para traer un usuario con Teléfono : " + tfTelefono.getText());
			List<DomicilioResponse> respuesta = null;
			try {
				String posType = SherpaApiBuilder.getSherpaPosType();
				String sherpaShop = sesion.getAplicacion().getCodAlmacen();
				String sherpaTpv = sesion.getAplicacion().getCodCaja();
				respuesta = domicilioService.searchDomicilioPhone(posType, sherpaShop, sherpaTpv, tfTelefono.getText().trim(), "");
			}
			catch (Exception e) {
				
				if(e instanceof RetryableException) {
					throw e;
				}
				
				/* Significa que no lo encuentra */
				if (e.getMessage().contains("404")) {
					respuesta = new ArrayList<DomicilioResponse>();
				}
			}
			return respuesta;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			domicilios = getValue();
			log.debug("BuscarPorTlfTask() - El resultado de la consulta es : " + domicilios);

			comprobarBusquedaRealizada();
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			log.error("BuscarPorTlfRecuperadoTask::failed() - Ha habido un error al consultar el fidelizado por el teléfono: " + exception.getMessage(), exception);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al consultar el fidelizado por el teléfono. Consulte con el administrador."), exception);

			cerrarPantallaTrasFalloConexion();
		}
	}

	/**
	 * Comprueba los resultados de la búsqueda y lo envía a la siguiente pantalla.
	 */
	private void comprobarBusquedaRealizada() {
		/* Si llega como nulo a esta parte, se muestra el error */
		if (domicilio == null && (domicilios != null && !domicilios.isEmpty())) {
			/* Del listado de resultados seleccionamos el primero */
			domicilio = domicilios.get(0);
			getDatos().put(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO, domicilio);
			getStage().close();

		}
		else if (domicilio != null && (domicilios == null || domicilios.isEmpty())) {
			getDatos().put(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO, domicilio);
			getStage().close();
		}
		else {
			log.debug("comprobarBusquedaRealizada() - No se han encontrado resultados con estos datos.");
			
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha encontrado un fidelizado con estos datos."), getStage());
		}
	}

	@FXML
	public void irAltaFidelizado() {
		getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos());

		if (getDatos().containsKey(EnvioDomicilioKeys.ALTA_DIRECCION_SAD)) {
			domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.ALTA_DIRECCION_SAD);
			if (getDatos().containsKey(EnvioDomicilioKeys.SHERPA_ADDRESS)) {
				getDatos().put(EnvioDomicilioKeys.SHERPA_ADDRESS, (String) getDatos().get(EnvioDomicilioKeys.SHERPA_ADDRESS));
			}
			getDatos().put(EnvioDomicilioKeys.ALTA_DIRECCION_SAD, domicilio);
			getStage().close();
		}

		if (getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
			getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
		}
	}

	private void cerrarPantallaTrasFalloConexion() {
		getDatos().put(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO, new DomicilioOfflineDto());
		getStage().close();
	}
	
}
