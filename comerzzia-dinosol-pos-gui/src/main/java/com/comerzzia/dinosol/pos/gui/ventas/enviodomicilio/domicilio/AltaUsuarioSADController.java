package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.DomicilioApi;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.AddressData;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.Campos;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponseCrud;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.ListAddressData;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.PersonData;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.gui.componentes.AutocompletionlTextField;
import com.comerzzia.dinosol.pos.gui.componentes.AutocompletionlTextField.TextSelectionListener;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.DomicilioOfflineDto;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.key.EnvioDomicilioKeys;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.domicilio.DireccionTablaBean;
import com.comerzzia.dinosol.pos.services.google.GoogleMapsService;
import com.comerzzia.dinosol.pos.services.sherpa.SherpaApiBuilder;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;
import com.google.maps.PlaceAutocompleteRequest.SessionToken;

import feign.RetryableException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

@Component
public class AltaUsuarioSADController extends WindowController{

	public static final String PARAM_OFFLINE = "AltaUsuarioSADController.ModoOffline";

	private Logger log = Logger.getLogger(AltaUsuarioSADController.class);
	
	@FXML
	protected Label lbTitulo;
	@FXML
	private AnchorPane apTitulo;
	@FXML
	protected TextField tfNombre, tfApellidos, tfDNI, tfCodigoPostal, tfPoblacion, tfProvincia, tfTelefono, tfEmail;
	@FXML
	protected AutocompletionlTextField tfDireccion;
	@FXML
	protected Button bLimpiar, bInsertar, bActualizar, bEliminar;
	@FXML
	protected ComboBox<String> cbTipoVia;
	protected ObservableList<String> tiposVia;
	
	private DomicilioApi domicilioService;
	private String codigoSherpa;
	private String codigoSherpaDireccion;
	private Boolean modificar;
	
	@Autowired
	protected VariablesServices variablesServices;
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private GoogleMapsService googleMapsService;
	
	private SessionToken tokenGoogle;
	
	private boolean offline;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		/* Insertamos el título de la ventana */
		String tituloPantalla = "Alta de Cliente en Servicio a Domicilio";
		lbTitulo.setText(I18N.getTexto(tituloPantalla));
		
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setPintarPiePantalla(true);
		tfNombre.setUserData(keyboardDataDto);
		tfApellidos.setUserData(keyboardDataDto);
		tfDNI.setUserData(keyboardDataDto);
		tfDireccion.setUserData(keyboardDataDto);
		tfCodigoPostal.setUserData(keyboardDataDto);
		tfPoblacion.setUserData(keyboardDataDto);
		tfProvincia.setUserData(keyboardDataDto);
		tfTelefono.setUserData(keyboardDataDto);
		tfEmail.setUserData(keyboardDataDto);
		
		tfNombre.textProperty().addListener(new ChangeListener<String>(){
			@Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tfNombre.setText(newValue.toUpperCase());
            }
		});
		
		tfApellidos.textProperty().addListener(new ChangeListener<String>(){
			@Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tfApellidos.setText(newValue.toUpperCase());
            }
		});
		
		tfDNI.textProperty().addListener(new ChangeListener<String>(){
			@Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tfDNI.setText(newValue.toUpperCase());
            }
		});
		
		tfDireccion.setTextFormatter(new TextFormatter<>((change) -> {
			change.setText(change.getText().toUpperCase());
			return change;
		}));
		
		tfDireccion.textProperty().addListener(new ChangeListener<String>(){
			@Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				String direccionMayus = newValue.toUpperCase();
				
				if(newValue.length() >= 3) {					
					Task<List<String>> task = new Task<List<String>>(){
						@Override
						protected List<String> call() throws Exception {
							List<String> localizaciones = googleMapsService.buscarLocalizaciones(direccionMayus, tokenGoogle);
							return localizaciones;
						}
						
						@Override
						protected void succeeded() {
							super.succeeded();
							tfDireccion.changePopup(getValue(), newValue);
						}
					};
					ExecutorService executor = Executors.newFixedThreadPool(1);
					executor.execute(task);
				}
            }
		});
		
		tfDireccion.addTextSelectionListener(new TextSelectionListener(){
			public void textSelection(String text) {
				if(StringUtils.isNotBlank(text)) {
					String[] trozos = text.split(",");
					
					tfDireccion.setText(trozos[0]);
					tfDireccion.requestFocus();
					tfDireccion.end();
					tfPoblacion.setText(trozos[trozos.length - 2]);
					
					tokenGoogle = new SessionToken();
					log.debug("initializeComponents() - Se ha pulsado en el cuadro de selección. El token nuevo es: " + tokenGoogle.getUUID());
				}
			}
		});
		
		tfPoblacion.textProperty().addListener(new ChangeListener<String>(){
			@Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tfPoblacion.setText(newValue.toUpperCase());
            }
		});
		
		tfProvincia.textProperty().addListener(new ChangeListener<String>(){
			@Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tfProvincia.setText(newValue.toUpperCase());
            }
		});
		
		tfEmail.textProperty().addListener(new ChangeListener<String>(){
			@Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tfEmail.setText(newValue.toUpperCase());
            }
		});
		
		tfCodigoPostal.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				String cpText = tfCodigoPostal.getText();
				if(StringUtils.isNotBlank(cpText)) {
					Integer cp = new Integer(cpText);
					if(cp >= 35000 && cp <= 35999) {
						tfProvincia.setText("LAS PALMAS");
					}
					if(cp >= 38000 && cp <= 38999) {
						tfProvincia.setText("SANTA CRUZ DE TENERIFE");
					}
				}
            }
		});
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		/* Al entrar en pantalla vaciamos todo */
		limpiarDatosPantalla();
		/* En caso de haberse bloqueados al modificar un usuario */
		tfNombre.setDisable(false);
		tfApellidos.setDisable(false);
		tfDNI.setDisable(false);
		
		/* Cargamos el combo con los tipos de vía al iniciar la pantalla*/
		rellenarTiposVias();
		
		/* Iniciamos los servicios para cargar los domicilios */
		domicilioService = SherpaApiBuilder.getSherpaDomicilioApiService();
		
		if(getDatos().containsKey(PARAM_OFFLINE)) {
			offline = true;
			String tituloPantalla = "Alta de Cliente en Servicio a Domicilio (sin conexión)";
			lbTitulo.setText(I18N.getTexto(tituloPantalla));
			apTitulo.getStyleClass().clear();
			apTitulo.getStyleClass().addAll("titulo-ventana", "cabecera-offline");
		}
		else {
			offline = false;
			String tituloPantalla = "Alta de Cliente en Servicio a Domicilio";
			lbTitulo.setText(I18N.getTexto(tituloPantalla));
			apTitulo.getStyleClass().clear();
			apTitulo.getStyleClass().addAll("titulo-ventana", "cabecera-venta");
		}
		
		if(!offline) {
			/* Realizamos comprobaciones en caso de que se modifique */
			codigoSherpa = null;
			codigoSherpaDireccion = null;
			modificar = false;
			comprobarModificar();
		}
		
		tokenGoogle = new SessionToken();
		log.debug("initializeComponents() - Se crea un nuevo token de consulta a Google. El token nuevo es: " + tokenGoogle.getUUID());
	}

	@Override
	public void initializeFocus() {
		tfNombre.requestFocus();
	}
	
	@FXML
	public void accionAceptar(){
		log.debug("accionAceptar() - Iniciamos la carga de los datos para el registro...");
		
		/* Validamos los campos de formulario */
		String mensajeValidador = validarCampos();
		if(StringUtils.isNotBlank(mensajeValidador)){
			log.info("accionAceptar() - " + mensajeValidador);
			VentanaDialogoComponent.crearVentanaAviso(mensajeValidador, getStage());
			return;
		}
		
		if(offline) {
			procesarFormulariOffline();
			return;
		}
		
		/* ====================== DATOS USUARIO ====================== */
		PersonData datosUsuario = new PersonData();
		List<Campos> listadoCamposUsuario = new ArrayList<Campos>();
		
		Campos campoNombre = new Campos();
		campoNombre.setField("nombre");
		campoNombre.setName("Nombre");
		campoNombre.setType("string");
		campoNombre.setValue(tfNombre.getText().trim());
		listadoCamposUsuario.add(campoNombre);
		
		Campos campoApellidos = new Campos();
		campoApellidos.setField("apellidos");
		campoApellidos.setName("Apellidos");
		campoApellidos.setType("string");
		campoApellidos.setValue(tfApellidos.getText().trim());
		listadoCamposUsuario.add(campoApellidos);
		
		Campos campoDNI = new Campos();
		campoDNI.setField("dni");
		campoDNI.setName("NIF/NIE");
		campoDNI.setType("string");
		campoDNI.setValue(tfDNI.getText().trim());
		listadoCamposUsuario.add(campoDNI);
		
		datosUsuario.setCampos(listadoCamposUsuario);
		
		/* ====================== DATOS DOMICILIO ====================== */
		AddressData datosDirecciones = new AddressData();
		List<Campos> listadoCamposDireccion = new ArrayList<Campos>();
		
		Campos campoDireccion = new Campos();
		campoDireccion.setField("direccion");
		campoDireccion.setName("Dirección Postal");
		campoDireccion.setType("string");
		campoDireccion.setValue(cbTipoVia.getSelectionModel().getSelectedItem() + " " + tfDireccion.getText().trim());
		listadoCamposDireccion.add(campoDireccion);
		
		Campos campoCodigoPostal = new Campos();
		campoCodigoPostal.setField("codigopostal");
		campoCodigoPostal.setName("Código Postal");
		campoCodigoPostal.setType("string");
		campoCodigoPostal.setValue(tfCodigoPostal.getText().trim());
		listadoCamposDireccion.add(campoCodigoPostal);
		
		Campos campoPoblacion = new Campos();
		campoPoblacion.setField("ciudad");
		campoPoblacion.setName("Población");
		campoPoblacion.setType("string");
		campoPoblacion.setValue(tfPoblacion.getText().trim());
		listadoCamposDireccion.add(campoPoblacion);
		
		Campos campoProvincia = new Campos();
		campoProvincia.setField("provincia");
		campoProvincia.setName("Provincia");
		campoProvincia.setType("string");
		campoProvincia.setValue(tfProvincia.getText().trim());
		listadoCamposDireccion.add(campoProvincia);
		
		Campos campoTelefono = new Campos();
		campoTelefono.setField("telefono");
		campoTelefono.setName("Teléfono");
		campoTelefono.setType("string");
		campoTelefono.setValue(tfTelefono.getText().trim());
		listadoCamposDireccion.add(campoTelefono);
		
		Campos campoEmail = new Campos();
		campoEmail.setField("email");
		campoEmail.setName("Email");
		campoEmail.setType("string");
		campoEmail.setValue(tfEmail.getText().trim());
		listadoCamposDireccion.add(campoEmail);
		
		datosDirecciones.setCampos(listadoCamposDireccion);
		if(codigoSherpaDireccion != null){
			datosDirecciones.setSherpaAddressCode(codigoSherpaDireccion);
		}else{
			datosDirecciones.setSherpaAddressCode("");
		}
		
		/* Después de cargar los datos creamos el objeto para la petición */
		DomicilioResponseCrud peticion = new DomicilioResponseCrud();
		if(codigoSherpa != null){
			datosUsuario.setSherpaCode(codigoSherpa);
		}else{
			datosUsuario.setSherpaCode("");
		}
		peticion.setPersonData(datosUsuario);
		peticion.setAddressData(datosDirecciones);
		
		if(!validarDireccion()) {
			if(!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("La dirección no es válida. ¿Desea continuar con el registro?"), getStage())) {
				return;
			}
		}
		
		if(modificar){
			new UpdateDomicilioTask(peticion).start();
		}else{
			new AltaDomicilioTask(peticion).start();
		}
	}

	private class AltaDomicilioTask extends BackgroundTask<DomicilioResponseCrud> {
		
		private DomicilioResponseCrud peticion;
		
		public AltaDomicilioTask(DomicilioResponseCrud peticion) {
			super();
			this.peticion = peticion;
		}
		@Override
		protected DomicilioResponseCrud call() {
			log.debug("AltaDomicilioTask() - Iniciamos la consulta para realizar un alta de la dirección : " + peticion);
			String posType = SherpaApiBuilder.getSherpaPosType();
			String sherpaShop = sesion.getAplicacion().getCodAlmacen();
			String sherpaTpv = sesion.getAplicacion().getCodCaja();
			return domicilioService.addDomicilio(posType, sherpaShop, sherpaTpv, peticion);
		}
		@Override
		protected void succeeded() {
			super.succeeded();
			DomicilioResponseCrud respuesta = getValue();
			log.debug("AltaDomicilioTask() - El resultado de la consulta es : " + respuesta);
			
			DomicilioResponse respuestaConsulta = consultarUsuarioSherpa(respuesta.getPersonData().getSherpaCode());
			
			/* Si no se produce ningún error, se procede a devolver la inserción */
			getDatos().put(EnvioDomicilioKeys.ALTA_DIRECCION_SAD, respuestaConsulta);
			getDatos().put(EnvioDomicilioKeys.SHERPA_ADDRESS, respuesta.getAddressData().getSherpaAddressCode());
			getStage().close();
		}
		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeError = "Se ha producido un error al guardar los datos del usuario";
			log.error("AltaDomicilioTask() - " + mensajeError + " - " + exception.getMessage(), getException());
			
			if(exception instanceof RetryableException) {
				log.error("AltaDomicilioTask:: - Error de conexión al dar de alta el cliente.");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ha fallado la conexión y no se podrá dar de alta el cliente en el sistema."), getStage());
				procesarFormulariOffline();
				return;
			}
			
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}
	}
	
	private class UpdateDomicilioTask extends BackgroundTask<DomicilioResponseCrud> {
		
		private DomicilioResponseCrud peticion;
		
		public UpdateDomicilioTask(DomicilioResponseCrud peticion) {
			super();
			this.peticion = peticion;
		}
		@Override
		protected DomicilioResponseCrud call() {
			log.debug("accionAceptar() - Iniciamos la consulta para realizar una modificación de la dirección : " + peticion);
			String posType = SherpaApiBuilder.getSherpaPosType();
			String sherpaShop = sesion.getAplicacion().getCodAlmacen();
			String sherpaTpv = sesion.getAplicacion().getCodCaja();
			return domicilioService.updateDomicilio(posType, sherpaShop, sherpaTpv, peticion);
		}
		@Override
		protected void succeeded() {
			super.succeeded();
			DomicilioResponseCrud respuesta = getValue();
			log.debug("accionAceptar() - El resultado de la consulta es : " + respuesta);
			
			DomicilioResponse respuestaConsulta = consultarUsuarioSherpa(respuesta.getPersonData().getSherpaCode());
			
			/* Si no se produce ningún error, se procede a devolver la inserción */
			getDatos().put(EnvioDomicilioKeys.ALTA_DIRECCION_SAD, respuestaConsulta);
			getDatos().put(EnvioDomicilioKeys.SHERPA_ADDRESS, respuesta.getAddressData().getSherpaAddressCode());
			getStage().close();
		}
		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeError = "Se ha producido un error al modificar los datos del usuario";
			log.error("UpdateDomicilioTask() - " + mensajeError + " - " + exception.getMessage(), getException());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}
	}
	
	public DomicilioResponse consultarUsuarioSherpa(String sherpaCode){
		DomicilioResponse domicilio = null;
		try{
			log.debug("consultarUsuarioSherpa() - Iniciamos la búsqueda del usuario con código " + sherpaCode);
			String posType = SherpaApiBuilder.getSherpaPosType();
			String sherpaShop = sesion.getAplicacion().getCodAlmacen();
			String sherpaTpv = sesion.getAplicacion().getCodCaja();
			domicilio = domicilioService.getDomicilio(posType, sherpaShop, sherpaTpv, sherpaCode);
			log.debug("consultarUsuarioSherpa() - El resultado de la consulta es : " + domicilio);
		}catch(Exception e){
			String mensajeError = "Se ha producido un error al cargar los datos del usuario del sistema de Servicio a Domicilio";
			log.error("consultarUsuarioSherpa() - " + mensajeError, e);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(I18N.getTexto(mensajeError)), getApplication().getMainView().getStage());
		}
		return domicilio;
	}
	
	public String validarCampos(){
		
		if(StringUtils.isBlank(tfNombre.getText())){
			return "El Nombre del usuario no puede estar vacío";
		}
		if(StringUtils.isBlank(tfApellidos.getText())){
			return "El Apellido del usuario no puede estar vacío";
		}
		if(StringUtils.isBlank(tfDireccion.getText())){
			return "La Dirección no puede estar vacía";
		}
		if(StringUtils.isBlank(tfCodigoPostal.getText())){
			return "El Código postal no puede estar vacío";
		}
		if(StringUtils.isBlank(tfPoblacion.getText())){
			return "La Población no puede estar vacía";
		}
		if(StringUtils.isBlank(tfTelefono.getText())){
			return "El Teléfono no puede estar vacío";
		}
		if(tfTelefono.getText().length() > 15){
			return "Solo se pueden indicar 15 caracteres en el campo del teléfono.";
		}
		if(StringUtils.isNotBlank(tfEmail.getText())){
			/* Patrón para validar el Email introducido */
			Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
	                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
			Matcher mather = pattern.matcher(tfEmail.getText());
	        /* Si es diferente de true, es que está mal escrito */
			if (mather.find() != true) {
				return "El formato del Email no es válido";
	        }
		}
		if(StringUtils.isBlank(cbTipoVia.getSelectionModel().getSelectedItem())){
			return "Debe seleccionar un Tipo de Vía";
		}
		
		return "";
	}
	
	public void limpiarDatosPantalla(){
		/* Limpiamos los datos del usuario */
		tfNombre.clear();
		tfApellidos.clear();
		tfDNI.clear();
		/* Limpiamos datos del domicilio */
		tfDireccion.clear();
		tfCodigoPostal.clear();
		tfPoblacion.clear();
		tfProvincia.clear();
		tfTelefono.clear();
		tfEmail.clear();
	}
	
	/**
	 * Realiza comprobaciones y si todo sale bien carga el usuario en la pantalla.
	 */
	public void comprobarModificar() {
		/* Significa que venimos para modificar una dirección */
		if(getDatos().containsKey(EnvioDomicilioKeys.UPDATE_DIRECCION_SAD)){
			DireccionTablaBean direccionSeleccionada = (DireccionTablaBean) getDatos().get(EnvioDomicilioKeys.UPDATE_DIRECCION_SAD);
			DomicilioResponse direccionModificar = direccionSeleccionada.getDirecciones();
			if(StringUtils.isNotBlank(direccionModificar.getPersonData().getSherpaCode())){
				/* Rescatamos el SherpaCode para utilizar el objeto */
				codigoSherpa = direccionModificar.getPersonData().getSherpaCode();
				modificar = true;
				log.debug("comprobarModificar() - Inicio de carga de datos del usuario " + codigoSherpa);
			
				/* Cargamos un mapa con los datos de usuario */
				for(Campos campoDireccion : direccionModificar.getPersonData().getCampos()){
					if("nombre".equals(campoDireccion.getField())){
						tfNombre.setText(campoDireccion.getValue());
						tfNombre.setDisable(true);
					}else if ("apellidos".equals(campoDireccion.getField())) {
						tfApellidos.setText(campoDireccion.getValue());
						tfApellidos.setDisable(true);
					}else if ("nif".equals(campoDireccion.getField())) {
						tfDNI.setText(campoDireccion.getValue());
						tfDNI.setDisable(true);
					}
				}
				
				/* Cargamos los datos de la dirección que vamos a modificar */
				codigoSherpaDireccion = direccionSeleccionada.getSherpaCodeDireccion();
				if(direccionSeleccionada.getDirecciones() != null){
					
					/* Cargamos un mapa con los datos de usuario */
					for(Campos campoDireccion : direccionModificar.getPersonData().getCampos()){
						if("nombre".equals(campoDireccion.getField())){
							tfNombre.setText(campoDireccion.getValue());
							tfNombre.setDisable(true);
						}else if ("apellidos".equals(campoDireccion.getField())) {
							tfApellidos.setText(campoDireccion.getValue());
							tfApellidos.setDisable(true);
						}else if ("nif".equals(campoDireccion.getField())) {
							tfDNI.setText(campoDireccion.getValue());
							tfDNI.setDisable(true);
						}
					}
					
					for(ListAddressData address : direccionModificar.getListAddressData()){
						if(codigoSherpaDireccion.equals(address.getSherpaAddressCode())){
							for(Campos campoDireccion : address.getCampos()){
								if("direccion".equals(campoDireccion.getField())){
									String direccion = "";
									/* Cargamos el combo con el tipo de via que tiene la dirección */
									for(String tipoVia : tiposVia){
										if(campoDireccion.getValue().contains(tipoVia)){
											/* Seleccionamos el tipo de vía */
											cbTipoVia.getSelectionModel().select(tipoVia);
											/* Quitamos la primera parte de la direccion, añadiendo uno mas por el espacio */
											direccion = campoDireccion.getValue().substring(tipoVia.length() + 1, campoDireccion.getValue().length());
											break;
										}
									}
									if(StringUtils.isNotBlank(direccion)){
										tfDireccion.setText(direccion);
									}else{
										tfDireccion.setText(campoDireccion.getValue());
									}
								}else if ("codigopostal".equals(campoDireccion.getField())) {
									tfCodigoPostal.setText(campoDireccion.getValue());
								}else if ("ciudad".equals(campoDireccion.getField())) {
									tfPoblacion.setText(campoDireccion.getValue());
								}else if ("provincia".equals(campoDireccion.getField())) {
									tfProvincia.setText(campoDireccion.getValue());			
								}else if ("email".equals(campoDireccion.getField())) {
									tfEmail.setText(campoDireccion.getValue());
								}else if ("telefono".equals(campoDireccion.getField())) {
									tfTelefono.setText(campoDireccion.getValue());
								}
							}
						}
					}
				}
				/* Establecemos el focus en la dirección porque la parte de arriba está bloqueada */
				tfDireccion.requestFocus();
			}
			else{
				String mensajeError = "Se ha producido un error al cargar los datos del Usuario";
				log.error("comprobarModificar() - " + mensajeError);
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
				return;
			}
		}else if(getDatos().containsKey(EnvioDomicilioKeys.IR_ALTA_DIRECCION_SAD)){
			
			DomicilioResponse datosNuevaDireccion = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.IR_ALTA_DIRECCION_SAD);
			if(StringUtils.isNotBlank(datosNuevaDireccion.getPersonData().getSherpaCode())){
				/* Rescatamos el SherpaCode para utilizar el objeto */
				codigoSherpa = datosNuevaDireccion.getPersonData().getSherpaCode();
				modificar = false;
				log.debug("comprobarModificar() - Inicio de carga de datos del usuario " + codigoSherpa);
			
				/* Cargamos un mapa con los datos de usuario */
				for(Campos campoDireccion : datosNuevaDireccion.getPersonData().getCampos()){
					if("nombre".equals(campoDireccion.getField())){
						tfNombre.setText(campoDireccion.getValue());
						tfNombre.setDisable(true);
					}else if ("apellidos".equals(campoDireccion.getField())) {
						tfApellidos.setText(campoDireccion.getValue());
						tfApellidos.setDisable(true);
					}else if ("nif".equals(campoDireccion.getField())) {
						tfDNI.setText(campoDireccion.getValue());
						tfDNI.setDisable(true);
					}
				}
			}
			/* Establecemos el focus en la dirección porque la parte de arriba está bloqueada */
			tfDireccion.requestFocus();
		}
	}
		
	@FXML
	public void accionCancelar(){
		/* Para que no se reinicie el ticket */
		getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
		getStage().close();
	}
	
	/**
	 * Rellena el ComboBox de tipos de vía a partir del listado que obtiene
	 * del fichero de configuración.
	 */
	public void rellenarTiposVias(){
		
		tiposVia = FXCollections.observableArrayList(new ArrayList<String>());
		cbTipoVia.getItems().clear();
		
		/* Cargamos el listado con los tipos de vias disponibles */
		cargarFicheroTiposVias();
		cbTipoVia.setItems(tiposVia);
		
		/* Ordenamos la lista por ID para seleccionar la primera por defecto */
		Collections.sort(tiposVia, new Comparator<String>(){
		    @Override
		    public int compare(String o1, String o2) {
		        return o1.compareToIgnoreCase(o2);
		    }
		});
		/* Seleccionamos la opción de Calle por defecto */
		for(String tipoVia : tiposVia){
			if("Calle".equals(tipoVia)){
				cbTipoVia.getSelectionModel().select(tipoVia);
			}
		}
	}
	
	/**
	 * Carga un listado de String desde el archivo de XML de tipos de via.
	 * @return
	 */
	public void cargarFicheroTiposVias(){
		
		/* Procedemos a cargar el fichero de tipos de vía */
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = null;
		try{
			url = comerzziaApp.obtenerUrlFicheroConfiguracion(EnvioDomicilioKeys.NOMBRE_FICHERO_VIAS + ".xml");
		}
		catch(Exception e){
			String mensajeError = "Se ha producido un error al realizar la carga del archivo : " + EnvioDomicilioKeys.NOMBRE_FICHERO_VIAS;
			log.error("cargarFicheroTiposVias() - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}
		
		File file = new File(url.getPath());
		log.debug("cargarFicheroTiposVias() - Iniciamos la lectura del archivo " + EnvioDomicilioKeys.NOMBRE_FICHERO_VIAS + " en la ruta : " + url.toString());
				
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			/* Eliminamos los nodos vacíos y combinamos los adyacentes en caso de que los hubiera */
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("tipoVia");
			log.debug("cargarFicheroTiposVias() - El archivo XML contiene " + nList.getLength() + " restricciones");
			
			/* Almacenamos los tipos de vías en el listado */
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					tiposVia.add(eElement.getElementsByTagName("via").item(0).getTextContent());
					log.trace("cargarFicheroTiposVias() - Tipo de Vía cargada : " + eElement.getElementsByTagName("via").item(0).getTextContent());
				}
			}
		} catch (Exception e) {
			String mensajeError = "Se ha producido un error al cargar los Tipos de Vías";
			log.error("cargarFicheroTiposVias() - " + mensajeError + " : " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}
	}
	
	@FXML
    public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			accionAceptar();
        }
    }
	
	private boolean validarDireccion() {
		try {
			boolean direccionValida = googleMapsService.validarDireccion(tfDireccion.getText(), tfCodigoPostal.getText(), "España", tfPoblacion.getText());
			return direccionValida;
		}
		catch (Exception e) {
			log.error("validarDireccion() - Ha habido un error al validar la dirección: " + e.getMessage(), e);
			return false;
		}
	}

	private void procesarFormulariOffline() {
		DomicilioOfflineDto domicilio = new DomicilioOfflineDto();
		domicilio.setNombre(tfNombre.getText().trim());
		domicilio.setApellidos(tfApellidos.getText().trim());
		domicilio.setNif(tfDNI.getText().trim());
		domicilio.setDireccion(cbTipoVia.getSelectionModel().getSelectedItem() + " " + tfDireccion.getText().trim());
		domicilio.setPoblacion(tfPoblacion.getText().trim());
		domicilio.setProvincia(tfProvincia.getText().trim());
		domicilio.setTelefono(tfTelefono.getText().trim());
		domicilio.setEmail(tfEmail.getText().trim());
		domicilio.setCp(tfCodigoPostal.getText().trim());
		
		getDatos().put(EnvioDomicilioKeys.DOMICILIOS, domicilio);
		getStage().close();
	}
}
