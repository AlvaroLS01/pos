package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.DomicilioApi;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.Campos;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.ListAddressData;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RutasServiciosException;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.key.EnvioDomicilioKeys;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.domicilio.DireccionDatosBean;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.domicilio.DireccionTablaBean;
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

@Component
public class SeleccionarDomicilioController extends WindowController{

	private Logger log = Logger.getLogger(SeleccionarDomicilioController.class);
	
	@FXML
	protected TableView<DireccionTablaBean> tvDirecciones;
	@FXML
	protected TableColumn<DireccionTablaBean, String> tcDatosDirecciones;
	protected ObservableList<DireccionTablaBean> direcciones;
	@FXML
	protected Label lbTitulo, lbNombre, lbDni;
	@FXML
	protected Button bAltaDireccion, bModificarDireccion, bBorrarDireccion;
	
	private DomicilioApi domicilioService;
	
	@Autowired
	protected VariablesServices variablesServices;
	
	@Autowired
	private Sesion sesion;
	
	private DomicilioResponse respuesta;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tcDatosDirecciones.setCellValueFactory(new PropertyValueFactory<DireccionTablaBean, String>("contenidoTabla"));
		
		direcciones = FXCollections.observableList(new ArrayList<DireccionTablaBean>());
		tvDirecciones.setItems(direcciones);
		
		tvDirecciones.setStyle("-fx-font-weight:bold;");
		tvDirecciones.setStyle("-fx-font-size:13px;");
		
		ImageView imagenAlta = new ImageView("/skins/standard" + Variables.IMAGES_BASE_PATH + "iconos/add.png");
		bAltaDireccion.setGraphic(imagenAlta);
		
		ImageView imagenModificar = new ImageView("/skins/standard" + Variables.IMAGES_BASE_PATH + "iconos/edit.png");
		bModificarDireccion.setGraphic(imagenModificar);
		
		ImageView imagenEliminar = new ImageView("/skins/standard" + Variables.IMAGES_BASE_PATH + "iconos/delete.png");
		bBorrarDireccion.setGraphic(imagenEliminar);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override	
	public void initializeForm() throws InitializeGuiException {
		respuesta = null;
		/* Limpiamos al entrar la tabla */
		direcciones.clear();
		/* Cargamos las direcciones del usuario */
		if(getDatos().containsKey(EnvioDomicilioKeys.DOMICILIOS)){
			respuesta = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.DOMICILIOS);
			if(!respuesta.getListAddressData().isEmpty()){
				generarDireccion(respuesta);
				
				if(respuesta.getListAddressData().size() == 1){
					DireccionTablaBean direccionTabla = direcciones.get(0);
					getDatos().put(EnvioDomicilioKeys.SELECCION_DOMICILIO, direccionTabla);
					getStage().close();
				}
			}
		}
		
		if(getDatos().containsKey(EnvioDomicilioKeys.FIDELIZADO_NOMBRE)) {
			String nombre = (String) getDatos().get(EnvioDomicilioKeys.FIDELIZADO_NOMBRE);
			lbNombre.setText(nombre);
		}
		else {
			lbNombre.setText("");
		}
		
		if(getDatos().containsKey(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO)) {
			String documento = (String) getDatos().get(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO);
			lbDni.setText(documento);
		}
		else {
			lbDni.setText("");
		}
		
		controlarBloqueoBotones();
	}

	public void controlarBloqueoBotones() {
		if(getDatos().containsKey(EnvioDomicilioKeys.CAMBIO_DIRECCION)){
			bAltaDireccion.setVisible(false);
			bModificarDireccion.setVisible(false);
			bBorrarDireccion.setVisible(false);
		}else{
			bAltaDireccion.setVisible(true);
			bModificarDireccion.setVisible(true);
			bBorrarDireccion.setVisible(true);
		}
	}

	@Override
	public void initializeFocus() {
		
	}

	/**
	 * Se lanza al pulsar el botón de Aceptar.
	 * Envía a la pantalla de SAD la dirección seleccionada.
	 */
	@FXML
	public void seleccionarDireccion(){
		/* Cargamos los datos de la tabla */
		DireccionTablaBean direccionTabla = new DireccionTablaBean();
		if(tvDirecciones.getSelectionModel().getSelectedItem() != null){
			direccionTabla = tvDirecciones.getSelectionModel().getSelectedItem();
			getDatos().put(EnvioDomicilioKeys.SELECCION_DOMICILIO, direccionTabla);
			getStage().close();
		}else{
			String mensajeInfo = "Debe seleccionar un Domicilio";
			log.info("seleccionarDireccion() - " + mensajeInfo);
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
		}
	}
	
	/**
	 * Genera la cadena de texto que vamos a mostrar en la tabla, una por cada dirección.
	 */
	public void generarDireccion(DomicilioResponse domicilios){
		log.debug("generarDireccion() - Iniciamos la carga de los objetos de la tabla...");
		
		for(ListAddressData listaDirecciones : domicilios.getListAddressData()){
			/* Cargamos los String con los datos del objeto que hemos cargado de Sherpa */
			Map<String, String> mapaDatosDomicilio = new HashMap<String, String>();
			DireccionDatosBean datosDireccion = new DireccionDatosBean();
			
			for(Campos campoDireccion : listaDirecciones.getCampos()){
				if("direccion".equals(campoDireccion.getField())){
					mapaDatosDomicilio.put("direccion", campoDireccion.getValue());
				}else if ("codigopostal".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("codigopostal", campoDireccion.getValue());
				}else if ("ciudad".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("ciudad", campoDireccion.getValue());
				}else if ("provincia".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("provincia", campoDireccion.getValue());				
				}else if ("email".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("email", campoDireccion.getValue());
				}else if ("telefono".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("telefono", campoDireccion.getValue());
				}
			}
			
			String cadenaPrincipal = "";
			
			if(mapaDatosDomicilio.containsKey("direccion")){
				cadenaPrincipal = cadenaPrincipal + "Dirección : " + mapaDatosDomicilio.get("direccion") + "\n";
				datosDireccion.setDireccion(mapaDatosDomicilio.get("direccion"));
			}else{
				datosDireccion.setDireccion("");
			}
			
			if(mapaDatosDomicilio.containsKey("codigopostal")){
				cadenaPrincipal = cadenaPrincipal + "C.P : " + mapaDatosDomicilio.get("codigopostal") + "\n";
				datosDireccion.setCodigoPostal(mapaDatosDomicilio.get("codigopostal"));
			}else{
				datosDireccion.setCodigoPostal("");
			}
			
			if(mapaDatosDomicilio.containsKey("ciudad")){
				cadenaPrincipal = cadenaPrincipal + "Población : " + mapaDatosDomicilio.get("ciudad");
				datosDireccion.setPoblacion(mapaDatosDomicilio.get("ciudad"));
				
				if(mapaDatosDomicilio.containsKey("provincia")){
					cadenaPrincipal = cadenaPrincipal + " (" + mapaDatosDomicilio.get("provincia") + ")" + "\n";
					datosDireccion.setProvincia(mapaDatosDomicilio.get("provincia"));
				}else{
					cadenaPrincipal = cadenaPrincipal + "\n";
					datosDireccion.setProvincia("");
				}
			}else{
				datosDireccion.setPoblacion("");
			}
			
			if(mapaDatosDomicilio.containsKey("email")){
				cadenaPrincipal = cadenaPrincipal + "Email : " + mapaDatosDomicilio.get("email") + "\n";
				datosDireccion.setEmail(mapaDatosDomicilio.get("email"));
			}else{
				datosDireccion.setEmail("");
			}
			
			if(mapaDatosDomicilio.containsKey("telefono")){
				cadenaPrincipal = cadenaPrincipal + "Teléfono : " + mapaDatosDomicilio.get("telefono");
				datosDireccion.setTelefono(mapaDatosDomicilio.get("telefono"));
			}else{
				datosDireccion.setTelefono("");
			}
			
			DireccionTablaBean direccionTabla = new DireccionTablaBean();
			direccionTabla.setContenidoTabla(cadenaPrincipal);
			direccionTabla.setDirecciones(domicilios);
			direccionTabla.setSherpaCodeDireccion(listaDirecciones.getSherpaAddressCode());
			direccionTabla.setDireccionDatos(datosDireccion);
			
			direcciones.add(direccionTabla);
			
			tvDirecciones.requestFocus();
			tvDirecciones.getSelectionModel().select(null);
			tvDirecciones.getFocusModel().focus(null);				
		}
		
		log.debug("generarDireccion() - Finalizada la carga de los objetos de la tabla...");
	}
	
	@FXML
	public void altaDireccion(){
		getDatos().put(EnvioDomicilioKeys.IR_ALTA_DIRECCION_SAD, respuesta);
		getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos());
		
		controlarBloqueoBotones();
		
		if(getDatos().containsKey(EnvioDomicilioKeys.ALTA_DIRECCION_SAD)){
			consultarUsuarioSherpa();
		}
		
		if(getDatos().containsKey(PagosController.ACCION_CANCELAR)){
			getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
		}
	}
	
	@FXML
	public void modificarDireccion(){
		/* Para poder modificar necesitamos tener seleccionado algo de la tabla */
		if(tvDirecciones.getSelectionModel().getSelectedItem() != null){
			getDatos().put(EnvioDomicilioKeys.UPDATE_DIRECCION_SAD, tvDirecciones.getSelectionModel().getSelectedItem());
			getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos());
			
			controlarBloqueoBotones();
			
			if(getDatos().containsKey(EnvioDomicilioKeys.ALTA_DIRECCION_SAD)){
				consultarUsuarioSherpa();
			}
			
			if(getDatos().containsKey(PagosController.ACCION_CANCELAR)){
				getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
			}
		}else{
			String mensajeInfo = "Debe seleccionar alguna de las Direcciones";
			log.info("irAltaFidelizado() - " + mensajeInfo);
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
		}
	}
	
	public void consultarUsuarioSherpa(){
		DomicilioResponse direccion = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.ALTA_DIRECCION_SAD);
		
		if(domicilioService == null){
			try {
				cargarServiciosSherpa();
			} catch (RutasServiciosException e) {
				log.error("borrarDireccion() - " + e.getMessage());
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
				return;
			}
		}
		
		DomicilioResponse domicilio = null;
		try{
			log.debug("consultarUsuarioSherpa() - Iniciamos la búsqueda del usuario con código " + direccion.getPersonData().getSherpaCode());
			String posType = SherpaApiBuilder.getSherpaPosType();
			String sherpaShop = sesion.getAplicacion().getCodAlmacen();
			String sherpaTpv = sesion.getAplicacion().getCodCaja();
			domicilio = domicilioService.getDomicilio(posType, sherpaShop, sherpaTpv, direccion.getPersonData().getSherpaCode());
			log.debug("consultarUsuarioSherpa() - El resultado de la consulta es : " + domicilio);
			
			direcciones.clear();
			/* Igualamos los datos globales de la clase, a lo devuelto por la consulta */
			respuesta = domicilio;
			generarDireccion(domicilio);
		}catch(Exception e){
			String mensajeError = "Se ha producido un error al cargar los datos del usuario del sistema de Servicio a Domicilio";
			log.error("consultarUsuarioSherpa() - " + mensajeError);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(I18N.getTexto(mensajeError)), getApplication().getMainView().getStage());
		}
	}
	
	@FXML
    public void accionCancelar(){
		/* Para que no se reinicie el ticket */
		getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
    	getStage().close();
    }
	
	/**
	 * Realiza la carga de los servicios necesarios en la clase para consultar en Sherpa.
	 * @throws RutasServiciosException 
	 */
	public void cargarServiciosSherpa() throws RutasServiciosException {
		domicilioService = SherpaApiBuilder.getSherpaDomicilioApiService();
	}
	
	@FXML
	public void borrarDireccion(){
		if(tvDirecciones.getSelectionModel().getSelectedItem() != null){
			String mensajeConfirmar = "¿Está seguro de eliminar el Domicilio?";
			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto(mensajeConfirmar), getStage())){
				DireccionTablaBean direccion = tvDirecciones.getSelectionModel().getSelectedItem();
				if(direccion.getDirecciones().getPersonData().getSherpaCode() != null){
					if(domicilioService == null){
						try {
							cargarServiciosSherpa();
						} catch (RutasServiciosException e) {
							log.error("borrarDireccion() - " + e.getMessage());
							VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
							return;
						}
					}
					new EliminarDomicilioTask(direccion).start();
				}
			}
		}else{
			String mensajeInfo = "Para eliminar una Dirección debe seleccionarla";
			log.debug("borrarDireccion() -" + mensajeInfo);
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
			return;
		}
	}
	
	private class EliminarDomicilioTask extends BackgroundTask<Void> {
		
		private DireccionTablaBean direccion;
		
		public EliminarDomicilioTask(DireccionTablaBean direccion) {
			super();
			this.direccion = direccion;
		}
		@Override
		protected Void call() {
			log.debug("EliminarDomicilioTask() - Inicio del borrado de la Dirección : " + direccion.getSherpaCodeDireccion());
			String posType = SherpaApiBuilder.getSherpaPosType();
			String sherpaShop = sesion.getAplicacion().getCodAlmacen();
			String sherpaTpv = sesion.getAplicacion().getCodCaja();
			domicilioService.deleteDomicilio(posType, sherpaShop, sherpaTpv, direccion.getDirecciones().getPersonData().getSherpaCode(), direccion.getSherpaCodeDireccion());
			return null;
		}
		@Override
		protected void succeeded() {
			super.succeeded();
			log.debug("EliminarDomicilioTask() - Finalizado el borrado.");
			
			tvDirecciones.getItems().remove(direccion);
			/* Refrescamos los datos de la tabla */
			tvDirecciones.refresh();
		}
		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeError = "Se ha producido un error al eliminar la dirección del usuario";
			log.error("EliminarDomicilioTask() - " + mensajeError + " - " + exception.getMessage());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
			return;
		}
	}
	
}
