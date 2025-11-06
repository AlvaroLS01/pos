package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.DomicilioApi;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.Campos;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.DomicilioOfflineDto;
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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class BusquedaDomicilioController extends WindowController {

	private Logger log = Logger.getLogger(BusquedaDomicilioController.class);

	@FXML
	private TextField tfDNI, tfTelefono;
	@FXML
	private Label lbTitulo;
	@FXML
	protected Button bAltaFidelizado;
	
	private DomicilioApi domicilioService;
	private List<DomicilioResponse> domicilios;
	
	@Autowired
	protected VariablesServices variablesServices;
	
	@Autowired
	private Sesion sesion;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		ImageView imagenInsertar = new ImageView("/skins/standard" + Variables.IMAGES_BASE_PATH + "iconos/add.png");
		bAltaFidelizado.setGraphic(imagenInsertar);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		/* Cargamos el título de la pantalla */
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(true);
		keyboardDataDto.setPintarPiePantalla(true);
		tfDNI.setUserData(keyboardDataDto);
		tfTelefono.setUserData(keyboardDataDto);
		
		addSeleccionarTodoEnFoco(tfDNI);
		addSeleccionarTodoEnFoco(tfTelefono);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		/* Reseteamos los datos de la pantalla */
		limpiarPantalla();
		
		domicilioService = SherpaApiBuilder.getSherpaDomicilioApiService();		
	}

	@Override
	public void initializeFocus() {
		tfDNI.requestFocus();
	}
	
	public void limpiarPantalla() {
		tfDNI.clear();
		tfTelefono.clear();
	}
	
	/**
	 * Busca el Fidelizado a partir del código de tarjeta en Sherpa(Segment)
	 * Lo devuelve a la pantalla de "Envío a domicilio".
	 */
	@FXML
	public void buscarFidelizado(){
		log.debug("buscarFidelizado() - Iniciamos la carga del Fidelizado...");
		domicilios = null;
		
		/* Por prioridad buscamos por DNI */
		if(StringUtils.isNotBlank(tfDNI.getText())){
			
			new BuscarPorDniTask().start();
			
		}else if(StringUtils.isNotBlank(tfTelefono.getText())){
			
			new BuscarPorTlfTask().start();
			
		}else{
			String mensajeInfo = "Alguno de los campos debe estar relleno";
			log.info("buscarFidelizado() - " + mensajeInfo);
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
			return;
		}
	}

	private class BuscarPorDniTask extends BackgroundTask<List<DomicilioResponse>> {
		public BuscarPorDniTask() {
			super();
		}
		@Override
		protected List<DomicilioResponse> call() {
			log.debug("BuscarPorDniTask::call() - Iniciamos la consulta para traer un usuario con DNI : " + tfDNI.getText());
			
			List<DomicilioResponse> respuesta = null;
			try{
				String posType = SherpaApiBuilder.getSherpaPosType();
				String sherpaShop = sesion.getAplicacion().getCodAlmacen();
				String sherpaTpv = sesion.getAplicacion().getCodCaja();
				String dni = tfDNI.getText().trim();
				
				log.debug("call() - Realizando petición con parámetros: posType = " + posType + ", sherpaShop = " + sherpaShop + ", sherpaTpv = " + sherpaTpv + ", dni = " + dni);
				respuesta = domicilioService.searchDomicilioDNI(posType, sherpaShop, sherpaTpv, dni, "");
			}catch(Exception e){
				log.error("BuscarPorDniTask::call() - Ha habido un error al realizar la consulta: " + e.getMessage(), e);
				
				if(e instanceof RetryableException) {
					throw e;
				}
				
				if(e.getMessage().contains("404")){
					respuesta = new ArrayList<DomicilioResponse>();
				}
				throw e;
			}			
			return respuesta;
		}
		@Override
		protected void succeeded() {
			super.succeeded();
			domicilios = getValue();
			
			log.debug("BuscarPorDniTask::call() - El resultado de la consulta es : " + domicilios);
			
			comprobarBusquedaRealizada();
		}
		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeInfo = "Se ha producido un error al buscar el fidelizado. Contacte con el administrador.";
			log.debug("BuscarPorDniTask::failed() - " + mensajeInfo + " - " + exception.getMessage());
			
			if(exception instanceof RetryableException) {
				cerrarPantallaTrasFalloConexion();
			}
			else {
				if(exception.getMessage().contains("404") || exception.getMessage().contains("400")){
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha encontrado ningún fidelizado con estos datos."), getStage());
				}
				else {
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(mensajeInfo), exception);
				}
				
				tfTelefono.requestFocus();
				tfDNI.requestFocus();
			}
		}
	}
	
	private class BuscarPorTlfTask extends BackgroundTask<List<DomicilioResponse>> {
		
		public BuscarPorTlfTask() {
			super();
		}
		
		@Override
		protected List<DomicilioResponse> call() {
			log.debug("BuscarPorTlfTask::call() - Iniciamos la consulta para traer un usuario con teléfono : " + tfTelefono.getText());
			
			List<DomicilioResponse> respuesta = null;
			try{
				String posType = SherpaApiBuilder.getSherpaPosType();
				String sherpaShop = sesion.getAplicacion().getCodAlmacen();
				String sherpaTpv = sesion.getAplicacion().getCodCaja();
				String telefono = tfTelefono.getText().trim();
				
				log.debug("call() - Realizando petición con parámetros: posType = " + posType + ", sherpaShop = " + sherpaShop + ", sherpaTpv = " + sherpaTpv + ", telefono = " + telefono);
				respuesta = domicilioService.searchDomicilioPhone(posType, sherpaShop, sherpaTpv, telefono, "1");
			}
			catch(Exception e){
				log.error("BuscarPorTlfTask::call() - Ha habido un error al realizar la consulta: " + e.getMessage(), e);
				
				if(e instanceof RetryableException) {
					throw e;
				}
				
				/* Significa que no lo encuentra */
				if(e.getMessage().contains("404")){
					respuesta = new ArrayList<DomicilioResponse>();
				}
				throw e;
			}			
			
			return respuesta;
		}
		@Override
		protected void succeeded() {
			super.succeeded();
			domicilios = getValue();
			
			log.debug("BuscarPorTlfTask::succeeded() - El resultado de la consulta es : " + domicilios);
			
			comprobarBusquedaRealizada();
		}
		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeInfo = "Se ha producido un error al buscar el fidelizado. Contacte con el administrador.";
			log.debug("BuscarPorTlfTask::failed() - " + mensajeInfo + " - " + exception.getMessage());
			
			if(exception instanceof RetryableException) {
				cerrarPantallaTrasFalloConexion();
			}
			else {
				if(exception.getMessage().contains("404") || exception.getMessage().contains("400")){
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha encontrado ningún fidelizado con estos datos."), getStage());
				}
				else {
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(mensajeInfo), exception);
				}
				
				tfDNI.requestFocus();
				tfTelefono.requestFocus();
			}
		}
	}
	
	private void comprobarBusquedaRealizada() {
		/* Si llega como nulo ha esta parte, se muestra el error */
		if(domicilios != null && !domicilios.isEmpty()){
			/* Del listado de resultados seleccionamos el primero */
			DomicilioResponse domicilio = new DomicilioResponse();
			domicilio = domicilios.get(0);
			
			getDatos().put(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO, domicilio);
			
			String nombre = null;
			String apellido = null;
			String nif = null;
			for(Campos campo : domicilio.getPersonData().getCampos()) {
				if(campo.getField().equals("nif")) {
					nif = campo.getValue();
				}
				else if(campo.getField().equals("nombre")) {
					nombre = campo.getValue();
				}
				else if(campo.getField().equals("apellidos")) {
					apellido = campo.getValue();
				}
			}
			getDatos().put(EnvioDomicilioKeys.FIDELIZADO_NOMBRE, nombre + " " + apellido);
			getDatos().put(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO, nif);
			
			getStage().close();
		}else{
			String mensajeInfo = "No se han encontrado resultados con estos datos";
			log.info("comprobarBusquedaRealizada() - " + mensajeInfo);
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
			return;
		}
		log.debug("comprobarBusquedaRealizada() - Finalizada la carga del Fidelizado.");
	}
	
	@FXML
    public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			buscarFidelizado();
        }
    }

	@FXML
	public void irAltaFidelizado(){
		getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos());
		
		if(getDatos().containsKey(EnvioDomicilioKeys.DOMICILIOS) && getDatos().get(EnvioDomicilioKeys.DOMICILIOS) instanceof DomicilioOfflineDto) {
			getStage().close();
			return;
		}
		
		if(getDatos().containsKey(EnvioDomicilioKeys.ALTA_DIRECCION_SAD)){
			DomicilioResponse direccion = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.ALTA_DIRECCION_SAD);
			if(getDatos().containsKey(EnvioDomicilioKeys.SHERPA_ADDRESS)){
				getDatos().put(EnvioDomicilioKeys.SHERPA_ADDRESS, (String)getDatos().get(EnvioDomicilioKeys.SHERPA_ADDRESS));
			}
			getDatos().put(EnvioDomicilioKeys.ALTA_DIRECCION_SAD, direccion);
			getStage().close();
		}
		
		if(getDatos().containsKey(PagosController.ACCION_CANCELAR)){
			getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
		}
	}
	
	@FXML
    public void accionCancelar(){
		/* Para que no se reinicie el ticket */
		getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
    	getStage().close();
    }

	private void cerrarPantallaTrasFalloConexion() {
		getDatos().put(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO, new DomicilioOfflineDto());
		getStage().close();
	}
	
}
