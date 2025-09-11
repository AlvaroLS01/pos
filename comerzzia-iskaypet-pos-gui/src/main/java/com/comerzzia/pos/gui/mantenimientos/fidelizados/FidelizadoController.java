package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.core.TiendaBean;
import com.comerzzia.api.model.loyalty.ColectivoBean;
import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.EnlaceFidelizadoBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.loyalty.TarjetaBean;
import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.client.colectivos.ColectivosRequestRest;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.tarjetas.ResponseGetTarjetaregaloRest;
import com.comerzzia.api.rest.client.general.tiendas.ConsultarTiendaRequestRest;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.gui.view.View;
import com.comerzzia.pos.dispositivo.comun.tarjeta.CodigoTarjetaController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos.ColectivoAyudaGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos.PaneColectivosController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.datosgenerales.PaneDatosGeneralesController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.etiquetas.PaneEtiquetasController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.movimientostarjetas.PaneMovimientosTarjetasController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.observaciones.PaneObservacionesController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.resumen.PaneResumenFidelizadoController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.tiendas.TiendaGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ultimasventas.PaneUltimasVentasController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosView;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.servicios.impresion.ImpresionJasper;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

@Component
public class FidelizadoController extends Controller implements Initializable{
	
	protected static final Logger log = Logger.getLogger(FidelizadoController.class);
	
	public static final String PERMISO_EJECUTAR = "EJECUCION";
	public static final String PERMISO_EDITAR = "EDITAR";
	public static final String PERMISO_ALTA = "AÑADIR";
	public static final String PERMISO_COLECTIVOS = "COLECTIVOS";
	public static final String PERMISO_MOVTARJETAS = "MOVIMIENTOS";
	public static final String PERMISO_ULTVENTAS = "VENTAS";
	public static final String PERMISO_ETIQUETAS= "ETIQUETAS";
	public static final String PERMISO_DATOSSENSIBLES= "DATOS SENSIBLES";
	public static final String PERMISO_IMPRIMIR= "IMPRIMIR";
	
	protected TicketManager ticketManager;
	
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
    protected VariablesServices variablesService;
	
	@Autowired
	private PaisService paisService = SpringContext.getBean(PaisService.class);
	
	@FXML
	protected PaneDatosGeneralesController paneDatosGeneralesController;
	
	@FXML
	protected PaneResumenFidelizadoController paneResumenFidelizadoController;
	
	@FXML
	protected PaneObservacionesController paneObservacionesController;
	
	@FXML
	protected PaneColectivosController paneColectivosController;
	
	@FXML
	protected PaneMovimientosTarjetasController paneMovimientosTarjetasController;
	
	@FXML
	protected PaneUltimasVentasController paneUltimasVentasController;
	
	@FXML
	protected PaneEtiquetasController paneEtiquetasController;
	
	@FXML
	protected TabPane tpDatosFidelizado;
	
	@FXML
	protected AnchorPane paneDatosGenerales, paneResumenFidelizado, paneObservaciones, paneEtiquetas;
	
	@FXML
	protected AnchorPane paneColectivos, paneMovimientosTarjetas, paneUltimasVentas;
	
	@FXML
	protected Tab tabResumen, tabDatosGenerales, tabObservaciones, tabColectivos, tabMovimientosTarjetas, tabUltimasVentas, tabEtiquetas;
	
	@FXML
	protected Button btAceptar, btCancelar, btEditar, btCerrar, btImprimir;
	
	@FXML
	protected Label lbError;
	
	@FXML
	protected Label lblCampoObligatorios;
	
	protected FidelizadoBean fidelizado;
	
	protected FormularioFidelizadoBean formFidelizado;
	
	protected List<TiendaGui> todasTiendas = new ArrayList<TiendaGui>();
	
	protected List<ColectivoAyudaGui> todosColectivos = new ArrayList<ColectivoAyudaGui>();
	
	protected String modo;
	
	protected String numeroTarjetaFidelizado;
	

	protected boolean verDatosSensibles, verColectivos, verUltVentas, verMovTarjetas, verEtiquetas, verImprimir;
	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		formFidelizado = new FormularioFidelizadoBean();
		formFidelizado.setFormField("codigo", paneDatosGeneralesController.getTfCodigo());
		formFidelizado.setFormField("numeroTarjeta", paneDatosGeneralesController.getTfNumeroTarjeta());
		formFidelizado.setFormField("nombre", paneDatosGeneralesController.getTfNombre());
		formFidelizado.setFormField("apellidos", paneDatosGeneralesController.getTfApellidos());
		formFidelizado.setFormField("tipoDocumento", paneDatosGeneralesController.getCbTipoDocumento());
		formFidelizado.setFormField("documento", paneDatosGeneralesController.getTfDocumento());
		formFidelizado.setFormField("sexo", paneDatosGeneralesController.getCbSexo());
		formFidelizado.setFormField("estadoCivil", paneDatosGeneralesController.getCbEstadoCivil());
		formFidelizado.setFormField("fechaNacimiento", paneDatosGeneralesController.getDpFechaNacimiento());
		formFidelizado.setFormField("email", paneDatosGeneralesController.getTfEmail());
		formFidelizado.setFormField("movil", paneDatosGeneralesController.getTfMovil());
		formFidelizado.setFormField("codPais", paneDatosGeneralesController.getTfCodPais());
		formFidelizado.setFormField("desPais", paneDatosGeneralesController.getTfDesPais());
		formFidelizado.setFormField("cp", paneDatosGeneralesController.getTfCodPostal());
		formFidelizado.setFormField("provincia", paneDatosGeneralesController.getTfProvincia());
		formFidelizado.setFormField("poblacion", paneDatosGeneralesController.getTfPoblacion());
		formFidelizado.setFormField("localidad", paneDatosGeneralesController.getTfLocalidad());
		formFidelizado.setFormField("domicilio", paneDatosGeneralesController.getTfDomicilio());
		formFidelizado.setFormField("codAlmFav", paneDatosGeneralesController.getTfCodTienda());
		formFidelizado.setFormField("desAlmFav", paneDatosGeneralesController.getTfDesTienda());
		formFidelizado.setFormField("codColectivo", paneDatosGeneralesController.getTfCodColectivo());
		formFidelizado.setFormField("desColectivo", paneDatosGeneralesController.getTfDesColectivo());
		formFidelizado.setFormField("observaciones", paneObservacionesController.getTaObservaciones());
		
		lblCampoObligatorios.setAlignment(Pos.BASELINE_LEFT);
		lbError.setAlignment(Pos.BASELINE_CENTER);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		tpDatosFidelizado.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
		       @Override
			   public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab tab) {
		    	   if(tab.getContent().equals(paneDatosGenerales)){
		    		   paneDatosGeneralesController.selected();
		       	   }else if(tab.getContent().equals(paneResumenFidelizado)){
		       		   paneResumenFidelizadoController.selected();
		       	   }else if(tab.getContent().equals(paneObservaciones)){
		       		   paneObservacionesController.selected();
		       	   }else if(tab.getContent().equals(paneColectivos)){
		       		   paneColectivosController.selected();
		       	   }else if(tab.getContent().equals(paneMovimientosTarjetas)){
		       		   paneMovimientosTarjetasController.selected();
		       	   }else if(tab.getContent().equals(paneUltimasVentas)){
		       		   paneUltimasVentasController.selected();
		       	   }else if(tab.getContent().equals(paneEtiquetas)){
		       		   paneEtiquetasController.selected();
		       	   }
		    	   
		    	 }
	        });
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		ConsultarTiendaRequestRest consultaTiendas = new ConsultarTiendaRequestRest();
		consultaTiendas.setApiKey(apiKey);
		consultaTiendas.setUidActividad(uidActividad);
		consultaTiendas.setCodEmp(sesion.getAplicacion().getEmpresa().getCodEmpresa());
		
		ConsultarTodasTiendasTask consultarTiendasTask = SpringContext.getBean(ConsultarTodasTiendasTask.class, 
				consultaTiendas, 
				new RestBackgroundTask.FailedCallback<List<TiendaBean>>() {
					@Override
					public void succeeded(List<TiendaBean> result) {	
						List<TiendaGui> tiendas = new ArrayList<TiendaGui>();
						for(TiendaBean tienda : result){
							TiendaGui tiendaGui = new TiendaGui(tienda);
							tiendas.add(tiendaGui);
						}
						todasTiendas = tiendas;
						
					}
					@Override
					public void failed(Throwable throwable) {
						getApplication().getMainView().close();
					}
				}, getStage());
		consultarTiendasTask.start();
		
		ColectivosRequestRest consultaColectivos = new ColectivosRequestRest(apiKey, uidActividad);
		consultaColectivos.setPrivado("N");
		
		ConsultarTodosColectivosTask consultarColectivosTask = SpringContext.getBean(ConsultarTodosColectivosTask.class, 
				consultaColectivos, 
				new RestBackgroundTask.FailedCallback<List<ColectivoBean>>() {
					@Override
					public void succeeded(List<ColectivoBean> result) {	
						List<ColectivoAyudaGui> colectivos = new ArrayList<ColectivoAyudaGui>();
						for(ColectivoBean colectivo : result){
							ColectivoAyudaGui colectivoGui = new ColectivoAyudaGui(colectivo);
							colectivos.add(colectivoGui);
						}
						todosColectivos = colectivos;
						
					}
					@Override
					public void failed(Throwable throwable) {
						getApplication().getMainView().close();
					}
				}, getStage());
		consultarColectivosTask.start();
		
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		formFidelizado.clearErrorStyle();
		String newModo = (String) getDatos().get(CodigoTarjetaController.PARAMETRO_MODO);
		ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);
		if("INSERCION".equals(newModo)){
			try {
				super.compruebaPermisos(PERMISO_ALTA);
			}
			catch (SinPermisosException ex) {
				log.error(ex.getMessage(), ex);
				throw new InitializeGuiException(ex.getMessage());
			}
			paneDatosGeneralesController.limpiarFormulario();
			paneObservacionesController.limpiarFormulario();
			paneResumenFidelizadoController.limpiarFormulario();
			paneMovimientosTarjetasController.limpiarFormulario();
			paneEtiquetasController.limpiarFormulario();
			setModo("INSERCION");
			lblCampoObligatorios.setVisible(true);
			 tabResumen.setDisable(true);
			 tabColectivos.setDisable(true);
			 tabMovimientosTarjetas.setDisable(true);
			 tabUltimasVentas.setDisable(true);
			 tabEtiquetas.setDisable(true);
			 numeroTarjetaFidelizado = null;
			 tpDatosFidelizado.getSelectionModel().select(tabDatosGenerales);
			 paneDatosGeneralesController.selected();
			 btCancelar.setVisible(false);
			 btCancelar.setManaged(false);
		     btCerrar.setVisible(true);
		     btCerrar.setManaged(true);
		     btAceptar.setVisible(true);
		     btAceptar.setManaged(true);
		     btEditar.setVisible(false);
		     btEditar.setManaged(false);
		     btImprimir.setVisible(true);  //Permite imprimir formularios vacíos
		     btImprimir.setManaged(true);
		}

		else if("CONSULTA".equals(newModo)){
			 setModo("CONSULTA");
			 lblCampoObligatorios.setVisible(false);
			 tabResumen.setDisable(false);
			 tabColectivos.setDisable(false);
			 tabMovimientosTarjetas.setDisable(false);
			 tabUltimasVentas.setDisable(false);
			 tabEtiquetas.setDisable(false);
			 btCancelar.setVisible(true);			 
		     btCerrar.setVisible(false);		     
		     btAceptar.setVisible(false);		     
		     btEditar.setVisible(true);
		     btImprimir.setVisible(true);
		     btAceptar.setManaged(false);
		     btCerrar.setManaged(false);
		     btCancelar.setManaged(true);
		     btEditar.setManaged(true);
		     btImprimir.setManaged(true);
			 Long idFidelizado = (Long) getDatos().get(CodigoTarjetaController.PARAMETRO_ID_FIDELIZADO);
			 String numeroTarjeta = (String) getDatos().get(FacturacionArticulosController.PARAMETRO_NUMERO_TARJETA);
			 numeroTarjetaFidelizado = numeroTarjeta;
			 cargarFidelizado(idFidelizado);
			 
			 
		 }
	}

	@Override
	public void initializeFocus() {
		// TODO Auto-generated method stub
		
	}
	
	protected void cargarFidelizado(Long idFidelizado){
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		FidelizadoRequestRest consulta = new FidelizadoRequestRest(apiKey, uidActividad, idFidelizado);
		consulta.setLanguageCode(sesion.getAplicacion().getStoreLanguageCode());
		ConsultarFidelizadoPorIdTask consultarFidelizadoTask = SpringContext.getBean(ConsultarFidelizadoPorIdTask.class, 
				consulta, 
				new RestBackgroundTask.FailedCallback<FidelizadoBean>() {
					@Override
					public void succeeded(FidelizadoBean result) {						
						fidelizado = result;
						
						tpDatosFidelizado.getSelectionModel().select(0);
						paneResumenFidelizadoController.setFidelizado(null);
						paneResumenFidelizadoController.selected();
						 
					     btCancelar.setVisible(false);
					     btCancelar.setManaged(false);
					     btCerrar.setVisible(true);
					     btCerrar.setManaged(true);
					     btAceptar.setVisible(false);
					     btAceptar.setManaged(true);
					     btEditar.setVisible(true);
					     btEditar.setManaged(true);
					     btImprimir.setVisible(true);
					     btImprimir.setManaged(true);
						
					}
					@Override
					public void failed(Throwable throwable) {
						getApplication().getMainView().close();
					}
				}, getStage());
		consultarFidelizadoTask.start();
	}
	
	public FidelizadoBean getFidelizado(){
		return fidelizado;
	}
	
	@FXML
	public void accionImprimir(){
		log.debug("accionImprimir()");
		List<FidelizadoBean> fidelizados = new ArrayList<FidelizadoBean>();
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		
		//Se edita temporalmente el domicilio y documento del fidelizado para la impresión por si fuera necesario ocultarlo
		String domicilioCopia = fidelizado.getDomicilio();
		String documentoCopia = fidelizado.getDocumento();
		fidelizado.setDomicilio(imprimeDatoSensible(fidelizado.getDomicilio()));
		fidelizado.setDocumento(imprimeDatoSensible(fidelizado.getDocumento()));
		
		fidelizados.add(fidelizado);
		parametros.put(ImpresionJasper.LISTA, fidelizados);
		if(fidelizado != null){
			if(fidelizado.getTipoContacto("MOVIL") != null){
				parametros.put("MOVIL",imprimeDatoSensible(fidelizado.getTipoContacto("MOVIL").getValor()));
				parametros.put("MOVIL_NOTIF", "S".equals(fidelizado.getTipoContacto("MOVIL").getRecibeNotificaciones()) ? "Sí" : "No");
			}else{
				parametros.put("MOVIL", "");
				parametros.put("MOVIL_NOTIF", "-");
			}
			if(fidelizado.getTipoContacto("EMAIL") != null){
				parametros.put("EMAIL", imprimeDatoSensible(fidelizado.getTipoContacto("EMAIL").getValor()));
				parametros.put("EMAIL_NOTIF", "S".equals(fidelizado.getTipoContacto("EMAIL").getRecibeNotificaciones()) ? "Sí" : "No");
			}else{
				parametros.put("EMAIL", "");
				parametros.put("EMAIL_NOTIF", "-");
			}
			
		}else{ //Para que no aparezca 'null' en 'Permite notificaciones'
			parametros.put("MOVIL_NOTIF", "");
			parametros.put("EMAIL_NOTIF", "");
		}
		parametros.put("DESEMP", sesion.getAplicacion().getEmpresa().getDesEmpresa());
		parametros.put("DOMICILIO", sesion.getAplicacion().getEmpresa().getDomicilio());
		parametros.put("CP", sesion.getAplicacion().getEmpresa().getCp());
		parametros.put("PROVINCIA", sesion.getAplicacion().getEmpresa().getProvincia());
		
		if(sesion.getAplicacion().getEmpresa().getLogotipo() != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(sesion.getAplicacion().getEmpresa().getLogotipo());
			parametros.put("LOGO", bis);
		}
		
		try {
			ServicioImpresion.imprimir("jasper/fidelizados/formulariofidelizado", parametros);
		} catch (DeviceException e) {
			log.error("Ha ocurrido un error al imprimir el informe ", e);
		} finally {
			fidelizado.setDomicilio(domicilioCopia);
			fidelizado.setDocumento(documentoCopia);
		}
	}
	
	protected String imprimeDatoSensible(String valor) {
		String res = valor;
		if(!verDatosSensibles&&!"EDICION".equals(getModo())){
			String sustituir = res.substring(1, res.length()-1);
			String car = sustituir.replaceAll(".", "*");
			res = res.replace(sustituir, car);
		}
		return res;
	}

	@FXML
	public void accionEditar(){
		
		btAceptar.setVisible(true);
		btAceptar.setManaged(true);
		btCancelar.setVisible(true);
		btCancelar.setManaged(true);
		btEditar.setVisible(false);
		btEditar.setManaged(false);
		btCerrar.setVisible(false);
		btCerrar.setManaged(false);
		tabResumen.setDisable(true);
		tabColectivos.setDisable(true);
		tabMovimientosTarjetas.setDisable(true);
		tabUltimasVentas.setDisable(true);
		tabEtiquetas.setDisable(true);
		lblCampoObligatorios.setVisible(true);
		setModo("EDICION");
		tpDatosFidelizado.getSelectionModel().select(tabDatosGenerales);
		paneObservacionesController.selected();
		paneDatosGeneralesController.selected();
		
		
	}
	
	@FXML
	public void accionAceptar(){	
		
		if("INSERCION".equals(getModo())){
			if(validarDatos()){
				String codigo = paneDatosGeneralesController.getTfCodigo().getText();
				if(StringUtils.isNotBlank(codigo)){
					String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
					String uidActividad = sesion.getAplicacion().getUidActividad();
					ConsultarFidelizadoRequestRest consultarFidelizado = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
					consultarFidelizado.setCodFidelizado(codigo);
					
					ConsultarFidelizadoPorCodTask consultarFidelizadoTask = SpringContext.getBean(ConsultarFidelizadoPorCodTask.class, 
							consultarFidelizado, 
							new RestBackgroundTask.FailedCallback<Boolean>() {
								@Override
								public void succeeded(Boolean result) {
									if(result){
										VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El código de fidelizado indicado ya existe"), getStage());
									}else{
										String numeroTarjeta = paneDatosGeneralesController.getTfNumeroTarjeta().getText();
										if(numeroTarjeta != null && !numeroTarjeta.isEmpty()){
											validarNumeroTarjeta(numeroTarjeta);
										}else{
											crearFidelizado();
										}
									}
									
								}
								@Override
								public void failed(Throwable throwable) {
									getApplication().getMainView().close();
								}
							}, getStage());
					consultarFidelizadoTask.start();
				}else{
					String numeroTarjeta = paneDatosGeneralesController.getTfNumeroTarjeta().getText();
					if(numeroTarjeta != null && !numeroTarjeta.isEmpty()){
						validarNumeroTarjeta(numeroTarjeta);
					}else{
						crearFidelizado();
					}
				}
				
			}
		}else if("EDICION".equals(getModo())){
			if(validarDatos()){
				editarFidelizado();
			}
		}
		
	}
	
	@FXML
	public void accionCancelar(){
		btAceptar.setVisible(false);
		btAceptar.setManaged(false);
		btCancelar.setVisible(false);
		btCancelar.setManaged(false);
		btEditar.setVisible(true);
		btEditar.setManaged(true);
		btCerrar.setVisible(true);
		btCerrar.setManaged(true);
		lblCampoObligatorios.setVisible(false);
		setModo("CONSULTA");
		tabResumen.setDisable(false);
		tabColectivos.setDisable(!verColectivos);
		tabMovimientosTarjetas.setDisable(!verMovTarjetas);
		tabUltimasVentas.setDisable(!verUltVentas);
		tabEtiquetas.setDisable(!verEtiquetas);
		paneDatosGeneralesController.setFidelizado(null);
		paneObservacionesController.setFidelizado(null);
		tpDatosFidelizado.getSelectionModel().select(tabResumen);
		lbError.setText("");
		formFidelizado.clearErrorStyle();
	}
	
	@FXML
	public void accionCerrar(){
		fidelizado = null;
		getApplication().getMainView().close();
	}
	
	protected FidelizadoBean getDatosFidelizado(String modo){
		FidelizadoBean fidelizado = new FidelizadoBean();
		if(paneDatosGeneralesController.getCbEstadoCivil().getValue() != null){
			String codestcivil = paneDatosGeneralesController.getCbEstadoCivil().getValue().getCodEstadoCivil();
			String desestcivil = paneDatosGeneralesController.getCbEstadoCivil().getValue().getDesEstadoCivil();
			fidelizado.setCodEstCivil(codestcivil);
			fidelizado.setDesEstCivil(desestcivil);
		}
		if(paneDatosGeneralesController.getCbSexo().getValue() != null){
			String sexo = paneDatosGeneralesController.getCbSexo().getValue().getCodigo();
			fidelizado.setSexo(sexo);
		}
		if(paneDatosGeneralesController.getCbTipoDocumento().getValue() != null){
			String codtipoiden = paneDatosGeneralesController.getCbTipoDocumento().getValue().getCodigo();
			fidelizado.setCodTipoIden(codtipoiden);
		}
		Boolean notifEmail = paneDatosGeneralesController.getChNotifEmail().isSelected();
		Boolean notifMovil = paneDatosGeneralesController.getChNotifMovil().isSelected();
		Boolean paperLess = paneDatosGeneralesController.getChPaperLess().isSelected();
		Date fechaNacimiento = paneDatosGeneralesController.getDpFechaNacimiento().getSelectedDate();
		String apellidos = paneDatosGeneralesController.getTfApellidos().getText();
		String codcolectivo = !"".equals(paneDatosGeneralesController.getTfCodColectivo().getText()) ? paneDatosGeneralesController.getTfCodColectivo().getText() : null;
		String codfidelizado = !"".equals(paneDatosGeneralesController.getTfCodigo().getText()) ? paneDatosGeneralesController.getTfCodigo().getText() : null;
		String codpais = !"".equals(paneDatosGeneralesController.getTfCodPais().getText()) ? paneDatosGeneralesController.getTfCodPais().getText() : null;
		String codpostal = !"".equals(paneDatosGeneralesController.getTfCodPostal().getText()) ? paneDatosGeneralesController.getTfCodPostal().getText() : null;
		String codalmFav = !"".equals(paneDatosGeneralesController.getTfCodTienda().getText()) ? paneDatosGeneralesController.getTfCodTienda().getText() : null;
		String despais = !"".equals(paneDatosGeneralesController.getTfDesPais().getText()) ? paneDatosGeneralesController.getTfDesPais().getText() : null;
		String documento = !"".equals(paneDatosGeneralesController.getTfDocumento().getText()) ? paneDatosGeneralesController.getTfDocumento().getText() : null;
		String domicilio = !"".equals(paneDatosGeneralesController.getTfDomicilio().getText()) ? paneDatosGeneralesController.getTfDomicilio().getText() : null;
		String email = !"".equals(paneDatosGeneralesController.getTfEmail().getText()) ? paneDatosGeneralesController.getTfEmail().getText() : null;
		String movil = !"".equals(paneDatosGeneralesController.getTfMovil().getText()) ? paneDatosGeneralesController.getTfMovil().getText() : null;
		String nombre = !"".equals(paneDatosGeneralesController.getTfNombre().getText()) ? paneDatosGeneralesController.getTfNombre().getText() : null;
		String poblacion = !"".equals(paneDatosGeneralesController.getTfPoblacion().getText()) ? paneDatosGeneralesController.getTfPoblacion().getText() : null;
		String localidad = !"".equals(paneDatosGeneralesController.getTfLocalidad().getText()) ? paneDatosGeneralesController.getTfLocalidad().getText() : null;
		String provincia = !"".equals(paneDatosGeneralesController.getTfProvincia().getText()) ? paneDatosGeneralesController.getTfProvincia().getText() : null;
		String observaciones = !"".equals(paneObservacionesController.getTaObservaciones().getText()) ? paneObservacionesController.getTaObservaciones().getText() : null;
		String numeroTarjeta = !"".equals(paneDatosGeneralesController.getTfNumeroTarjeta().getText()) ? paneDatosGeneralesController.getTfNumeroTarjeta().getText() : null;
		
		
		fidelizado.setFechaNacimiento(fechaNacimiento);
		fidelizado.setApellidos(apellidos);
		fidelizado.setCodFidelizado(codfidelizado);
		fidelizado.setCodPais(codpais);
		fidelizado.setCp(codpostal);
		fidelizado.setDesPais(despais);
		fidelizado.setDocumento(documento);
		fidelizado.setDomicilio(domicilio);
		fidelizado.setNombre(nombre);
		fidelizado.setPoblacion(poblacion);
		fidelizado.setLocalidad(localidad);
		fidelizado.setProvincia(provincia);
		fidelizado.setObservaciones(observaciones);
		fidelizado.setPaperLess(paperLess);
	
		if(codalmFav != null){
			EnlaceFidelizadoBean enlace = new EnlaceFidelizadoBean();
			enlace.setIdClase("D_TIENDAS_TBL.CODALM");
			enlace.setIdObjeto(codalmFav);
			
			fidelizado.setEnlace(enlace);
		}
		if("INSERCION".equals(modo)){
			if(codcolectivo != null){
				ColectivosFidelizadoBean colectivo = new ColectivosFidelizadoBean();
				colectivo.setCodColectivo(codcolectivo);
					
				List<ColectivosFidelizadoBean> colectivos = new ArrayList<ColectivosFidelizadoBean>();
				colectivos.add(colectivo);
				fidelizado.setColectivos(colectivos);
			}
			List<TiposContactoFidelizadoBean> tiposContacto = new ArrayList<TiposContactoFidelizadoBean>();
			if(email != null){
				TiposContactoFidelizadoBean contactoEmail = new TiposContactoFidelizadoBean();
				contactoEmail.setCodTipoCon("EMAIL");
				contactoEmail.setRecibeNotificaciones(notifEmail);
				contactoEmail.setValor(email);
				tiposContacto.add(contactoEmail);
			}
			if(movil != null){
				TiposContactoFidelizadoBean contactoMovil = new TiposContactoFidelizadoBean();
				contactoMovil.setCodTipoCon("MOVIL");
				contactoMovil.setRecibeNotificaciones(notifMovil);
				contactoMovil.setValor(movil);
			
				tiposContacto.add(contactoMovil);
			}
			
			fidelizado.setContactos(tiposContacto);
			fidelizado.setTarjetas(new ArrayList<TarjetaBean>());
			if(numeroTarjeta != null){
				List<TarjetaBean> tarjetasFidelizado = new ArrayList<TarjetaBean>();
				TarjetaBean tarjeta = new TarjetaBean();
				tarjeta.setNumeroTarjeta(numeroTarjeta);
				tarjetasFidelizado.add(tarjeta);
				
				fidelizado.setTarjetas(tarjetasFidelizado);
			}
		}else if("EDICION".equals(modo)){
			fidelizado.setColectivos(this.fidelizado.getColectivos());
			fidelizado.setIdFidelizado(this.fidelizado.getIdFidelizado());
			fidelizado.setCodAlm(codalmFav);
			fidelizado.setContactos(this.fidelizado.getContactos());
			TiposContactoFidelizadoBean oldMovilContacto = fidelizado.getTipoContacto("MOVIL");
			if(oldMovilContacto != null && oldMovilContacto.getValor() != null){
				String oldMovil = oldMovilContacto.getValor();
				boolean oldRecibeNotif = oldMovilContacto.getRecibeNotificaciones();
				if(movil == null){
					oldMovilContacto.setEstadoBean(Estado.BORRADO);
				}
				else if(!oldMovil.equals(movil) || oldRecibeNotif != notifMovil){
					oldMovilContacto.setValor(movil);
					oldMovilContacto.setRecibeNotificaciones(notifMovil);
					oldMovilContacto.setEstadoBean(Estado.MODIFICADO);
				}
			}else{
				if(movil != null){
					TiposContactoFidelizadoBean contactoMovil = new TiposContactoFidelizadoBean();
					contactoMovil.setCodTipoCon("MOVIL");
					contactoMovil.setRecibeNotificaciones(notifMovil);
					contactoMovil.setValor(movil);
					contactoMovil.setEstadoBean(Estado.NUEVO);
					fidelizado.getContactos().add(contactoMovil);
				}
			}
			TiposContactoFidelizadoBean oldEmailContacto = fidelizado.getTipoContacto("EMAIL");
			if(oldEmailContacto != null && oldEmailContacto.getValor() != null){
				String oldEmail = oldEmailContacto.getValor();
				boolean oldRecibeNotif = oldEmailContacto.getRecibeNotificaciones();
				if(email == null){
					oldEmailContacto.setEstadoBean(Estado.BORRADO);
				}
				else if(!oldEmail.equals(email) || oldRecibeNotif != notifEmail){
					oldEmailContacto.setValor(email);
					oldEmailContacto.setRecibeNotificaciones(notifEmail);
					oldEmailContacto.setEstadoBean(Estado.MODIFICADO);
				}
			}else{
				if(email != null){
					TiposContactoFidelizadoBean contactoEmail = new TiposContactoFidelizadoBean();
					contactoEmail.setCodTipoCon("EMAIL");
					contactoEmail.setRecibeNotificaciones(notifEmail);
					contactoEmail.setValor(email);
					contactoEmail.setEstadoBean(Estado.NUEVO);
				
					fidelizado.getContactos().add(contactoEmail);
				}
			}
		}
		return fidelizado;
	}
	
	public void validarNumeroTarjeta(final String numeroTarjeta){

		
			final String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
			final String uidActividad = sesion.getAplicacion().getUidActividad();
			ConsultarFidelizadoRequestRest consulta = new ConsultarFidelizadoRequestRest(apiKey, uidActividad, numeroTarjeta);
			
			ValidarNumeroTarjetaTask validarTarjetaTask = SpringContext.getBean(ValidarNumeroTarjetaTask.class, 
					consulta, 
					new RestBackgroundTask.FailedCallback<ResponseGetTarjetaregaloRest>() {
						@Override
						public void succeeded(ResponseGetTarjetaregaloRest result) {		
							boolean tarjetaValida = result.isTarjetaValida();
							if(!tarjetaValida){
								String error = result.getErrorValidacion();
								String mensaje = "";
								if("FIDELIZADO".equals(error)){
									mensaje = "Esta tarjeta ya está asignada a un fidelizado";
								}else if("PREFIJO".equals(error)){
									mensaje = "El prefijo del número de la tarjeta no es correcto";
								}else if("LONGITUD".equals(error)){
									mensaje = "La longitud del número de la tarjeta no es correcto";
								}else if("FORMATO".equals(error)){
									mensaje = "El formato del número de la tarjeta no es correcto";
								}else if("NO_TIPO".equals(error)){
									mensaje = "No existen tipos de tarjeta vinculables y que no sean de pago";
								}
								VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensaje), getStage());
							}else{
								numeroTarjetaFidelizado = numeroTarjeta;
								crearFidelizado();
								
							}
							
						}
						@Override
						public void failed(Throwable throwable) {
							getApplication().getMainView().close();
						}
					}, getStage());
			validarTarjetaTask.start();
	
		
	}
	
	public void crearFidelizado(){
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		FidelizadoRequestRest insertFidelizado = new FidelizadoRequestRest(apiKey, uidActividad, getDatosFidelizado("INSERCION"), sesion.getAplicacion().getEmpresa().getCodEmpresa(), sesion.getAplicacion().getCodAlmacen());
		insertFidelizado.setTipoNotificacion("NUEVO_USUARIO_FIDELIZADO");
		CrearFidelizadoTask insertFidelizadoTask = SpringContext.getBean(CrearFidelizadoTask.class, 
				insertFidelizado, 
				new RestBackgroundTask.FailedCallback<FidelizadoBean>() {
					@Override
					public void succeeded(FidelizadoBean result) {
						fidelizado = result;
						tabResumen.setDisable(false);
						tabMovimientosTarjetas.setDisable(!verMovTarjetas);
						tabColectivos.setDisable(!verColectivos);
						tabUltimasVentas.setDisable(!verUltVentas);
						tabEtiquetas.setDisable(!verEtiquetas);
						paneResumenFidelizadoController.selected();
						tpDatosFidelizado.getSelectionModel().select(tabResumen);
						btAceptar.setVisible(false);
						btAceptar.setManaged(false);
						btCancelar.setVisible(false);
						btCancelar.setManaged(false);
						btEditar.setVisible(true);
						btEditar.setManaged(true);
						btImprimir.setVisible(true);
						btImprimir.setManaged(true);
						btCerrar.setVisible(true);
						btCerrar.setManaged(true);
						lblCampoObligatorios.setVisible(false);
						setModo("CONSULTA");
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Fidelizado creado correctamente"), getStage());
						
						asignarFidelizadoAVenta();
					}
					
					@Override
					public void failed(Throwable throwable) {
						getApplication().getMainView().close();
					}
				}, getStage());
		insertFidelizadoTask.start();
	}
	
	protected void asignarFidelizadoAVenta() {
        for(View view : getApplication().getMainView().getSubViews()) {
			if(view instanceof FacturacionArticulosView && view.getController() instanceof FacturacionArticulosController) {
				String numTarjeta = paneDatosGeneralesController.getTfNumeroTarjeta().getText();
				
				if(StringUtils.isNotBlank(numTarjeta)) {
					FacturacionArticulosController facturacionArticulosController = (FacturacionArticulosController) view.getController();
					try {										
						FidelizacionBean fidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(getStage(), numTarjeta, sesion.getAplicacion().getUidActividad());
						facturacionArticulosController.ticketManager.getTicket().getCabecera().setDatosFidelizado(fidelizado);
					}
					catch(Exception e) {
						log.error("crearFidelizado() - Ha habido un error al consultar el fidelizado recién creado: " + e.getMessage(), e);
						facturacionArticulosController.ticketManager.getTicket().getCabecera().setDatosFidelizado(numTarjeta);
					}
					facturacionArticulosController.refrescarDatosPantalla();
				}
			}
		}
    }
	
	public void editarFidelizado(){
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		fidelizado = getDatosFidelizado("EDICION");
		FidelizadoRequestRest insertFidelizado = new FidelizadoRequestRest(apiKey, uidActividad, fidelizado);
		if(fidelizado.getContactos() != null && !fidelizado.getContactos().isEmpty()){
			Map<String, Integer> mapaEstadosTiposContacto = new HashMap<String, Integer>();
			for(TiposContactoFidelizadoBean tipoContacto : fidelizado.getContactos()){
				if(tipoContacto.getEstadoBean() != Estado.SIN_MODIFICAR){
					mapaEstadosTiposContacto.put(tipoContacto.getCodTipoCon(), tipoContacto.getEstadoBean());
				}
			}
			insertFidelizado.setMapaEstadosTiposContacto(mapaEstadosTiposContacto);
		}
		if(fidelizado.getCodAlm() != null){
			insertFidelizado.setCodAlmFav(fidelizado.getCodAlm());
		}
		EditarFidelizadoTask insertFidelizadoTask = SpringContext.getBean(EditarFidelizadoTask.class, 
				insertFidelizado, 
				new RestBackgroundTask.FailedCallback<FidelizadoBean>() {
					@Override
					public void succeeded(FidelizadoBean result) {	
						fidelizado = result;
						actualizaFidelizado();
						setModo("CONSULTA");				
						tabResumen.setDisable(false);
						tabMovimientosTarjetas.setDisable(!verMovTarjetas);
						tabColectivos.setDisable(!verColectivos);
						tabUltimasVentas.setDisable(!verUltVentas);
						tabEtiquetas.setDisable(!verEtiquetas);
						paneResumenFidelizadoController.setFidelizado(null);
						paneResumenFidelizadoController.selected();
						tpDatosFidelizado.getSelectionModel().select(tabResumen);
						btAceptar.setVisible(false);
						btAceptar.setManaged(false);
						btCancelar.setVisible(false);
						btCancelar.setManaged(false);
						btEditar.setVisible(true);
						btEditar.setManaged(true);
						btCerrar.setVisible(true);
						btCerrar.setManaged(true);
						lblCampoObligatorios.setVisible(false);
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Fidelizado editado correctamente"), getStage());
						
					}
					@Override
					public void failed(Throwable throwable) {
						getApplication().getMainView().close();
					}
				}, getStage());
		insertFidelizadoTask.start();
	}
	
	public void actualizaFidelizado() {
		
		FidelizacionBean fb = new FidelizacionBean();
		
		fb.setIdFidelizado(fidelizado.getIdFidelizado());
		fb.setNombre(fidelizado.getNombre());
		fb.setApellido(fidelizado.getApellidos());
		fb.setDomicilio(fidelizado.getDomicilio());
		fb.setLocalidad(fidelizado.getLocalidad());
		fb.setProvincia(fidelizado.getProvincia());
		fb.setCodPais(fidelizado.getCodPais());
		fb.setNumTarjetaFidelizado(getNumeroTarjetaFidelizado());
		fb.setCodTipoIden(fidelizado.getCodTipoIden());
		fb.setDocumento(fidelizado.getDocumento());
		fb.setPaperLess(fidelizado.getPaperLess());
		fb.setCp(fidelizado.getCp());
		fb.setPoblacion(fidelizado.getPoblacion());
		
		
		ticketManager.getTicket().getCabecera().setDatosFidelizado(fb);

	}
	public boolean validarDatos(){
		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		formFidelizado.clearErrorStyle();
		
		formFidelizado.setApellidos(paneDatosGeneralesController.getTfApellidos().getText());
		formFidelizado.setCodColectivo(paneDatosGeneralesController.getTfCodColectivo().getText());
		formFidelizado.setCodigo(paneDatosGeneralesController.getTfCodigo().getText());
		formFidelizado.setCp(paneDatosGeneralesController.getTfCodPostal().getText());
		formFidelizado.setDocumento(paneDatosGeneralesController.getTfDocumento().getText());
		formFidelizado.setDomicilio(paneDatosGeneralesController.getTfDomicilio().getText());
		formFidelizado.setEmail(paneDatosGeneralesController.getTfEmail().getText());
		formFidelizado.setFechaNacimiento(paneDatosGeneralesController.getDpFechaNacimiento().getSelectedDate());
		formFidelizado.setMovil(paneDatosGeneralesController.getTfMovil().getText());
		formFidelizado.setNombre(paneDatosGeneralesController.getTfNombre().getText());
		formFidelizado.setNumeroTarjeta(paneDatosGeneralesController.getTfNumeroTarjeta().getText());
		formFidelizado.setPoblacion(paneDatosGeneralesController.getTfPoblacion().getText());
		formFidelizado.setLocalidad(paneDatosGeneralesController.getTfLocalidad().getText());
		formFidelizado.setProvincia(paneDatosGeneralesController.getTfProvincia().getText());
		formFidelizado.setObservaciones(paneObservacionesController.getTaObservaciones().getText());
		TipoIdentGui tipoIdent = paneDatosGeneralesController.getCbTipoDocumento().getSelectionModel().getSelectedItem();
		if(tipoIdent == null){
			formFidelizado.setTipoDocumento("");
		}
		else{
			formFidelizado.setTipoDocumento(tipoIdent.getCodigo());
		}
		
		SexoGui sexo = paneDatosGeneralesController.getCbSexo().getSelectionModel().getSelectedItem();
		if(sexo == null){
			formFidelizado.setSexo("");
		}
		else{
			formFidelizado.setSexo(sexo.getCodigo());
		}
		
		EstadoCivilGui estCivil = paneDatosGeneralesController.getCbEstadoCivil().getSelectionModel().getSelectedItem();
		if(estCivil == null){
			formFidelizado.setEstadoCivil("");
		}
		else{
			formFidelizado.setEstadoCivil(estCivil.getCodEstadoCivil());
		}
		
		try {
            paisService.consultarCodPais(paneDatosGeneralesController.getTfCodPais().getText());
        }
        catch (PaisNotFoundException e) {
        	PathImpl path = PathImpl.createPathFromString("codPais");
        	formFidelizado.setFocus(path);
        	formFidelizado.setErrorStyle(path, true);
        	return false;
        }
        catch (PaisServiceException e) {
        	log.debug("validarDatos() - Ha habido un error al buscar el país con código " + paneDatosGeneralesController.getTfCodPais().getText() + ": " + e.getMessage());
        	return false;
        }
		
		String codcolectivo = paneDatosGeneralesController.getTfCodColectivo().getText();
		String codtiendafavorita = paneDatosGeneralesController.getTfCodTienda().getText();
		if(codcolectivo != null && !"".equals(codcolectivo)){
			boolean colectivoValido = false;
			for(ColectivoAyudaGui colectivo : todosColectivos){
				if(codcolectivo.equals(colectivo.getCodColectivo())){
					colectivoValido = true;
					break;
				}
			}
			if(!colectivoValido){
				PathImpl path = PathImpl.createPathFromString("codColectivo");
	        	formFidelizado.setFocus(path);
	        	formFidelizado.setErrorStyle(path, true);
	        	return false;
			}
		}
		
		if(codtiendafavorita != null && !"".equals(codtiendafavorita)){
			boolean tiendaValido = false;
			for(TiendaGui tienda : todasTiendas){
				if(codtiendafavorita.equals(tienda.getCodTienda())){
					tiendaValido = true;
					break;
				}
			}
			if(!tiendaValido){
				PathImpl path = PathImpl.createPathFromString("codAlmFav");
	        	formFidelizado.setFocus(path);
	        	formFidelizado.setErrorStyle(path, true);
	        	return false;
			}
		}
		
		//Limpiamos el posible error anterior
		lbError.setText("");
		
		Set<ConstraintViolation<FormularioFidelizadoBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formFidelizado);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioFidelizadoBean> next = constraintViolations.iterator().next();
			formFidelizado.setErrorStyle(next.getPropertyPath(), true);
			formFidelizado.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
	}

	@Override
	public void onClose(){
		fidelizado = null;
		if(POSApplication.getInstance().getMainView().isUserClosing()){
			reiniciarFidelizadoPestañas();
		}
    	super.onClose();
    };
	
	public List<TiendaGui> getTodasTiendas() {
		return todasTiendas;
	}

	public List<ColectivoAyudaGui> getTodosColectivos() {
		return todosColectivos;
	}

	public String getModo() {
		return modo;
	}

	public void setModo(String modo) {
		this.modo = modo;
	}

	public String getNumeroTarjetaFidelizado() {
		return numeroTarjetaFidelizado;
	}
	
	public void reiniciarFidelizadoPestañas(){
		paneColectivosController.setFidelizado(null);
		paneDatosGeneralesController.setFidelizado(null);
		paneMovimientosTarjetasController.setFidelizado(null);
		paneObservacionesController.setFidelizado(null);
		paneResumenFidelizadoController.setFidelizado(null);
		paneUltimasVentasController.setFidelizado(null);
		paneEtiquetasController.setFidelizado(null);
	}
	
	@Override
	public void comprobarPermisosUI() {
		super.comprobarPermisosUI();
		try {
			super.compruebaPermisos(PERMISO_EDITAR);
			btEditar.setDisable(false);
		}
		catch (SinPermisosException ex) {
			btEditar.setDisable(true);
		}
		try {
			super.compruebaPermisos(PERMISO_COLECTIVOS);
			verColectivos = true;
			if("CONSULTA".equals(getModo())){
				tabColectivos.setDisable(false);
			}
		}
		catch (SinPermisosException ex) {
			verColectivos = false;
			tabColectivos.setDisable(true);
			if(tabColectivos.isSelected()){
				paneResumenFidelizadoController.selected();
				tpDatosFidelizado.getSelectionModel().select(tabResumen);
			}
		}
		try {
			super.compruebaPermisos(PERMISO_ULTVENTAS);
			verUltVentas = true;
			if("CONSULTA".equals(getModo())){
				tabUltimasVentas.setDisable(false);
			}
		}
		catch (SinPermisosException ex) {
			verUltVentas = false;
			tabUltimasVentas.setDisable(true);
			if(tabUltimasVentas.isSelected()){
				paneResumenFidelizadoController.selected();
				tpDatosFidelizado.getSelectionModel().select(tabResumen);
			}
		}
		try {
			super.compruebaPermisos(PERMISO_MOVTARJETAS);
			verMovTarjetas = true;
			if("CONSULTA".equals(getModo())){
				tabMovimientosTarjetas.setDisable(false);
			}
		}
		catch (SinPermisosException ex) {
			verMovTarjetas = false;
			tabMovimientosTarjetas.setDisable(true);
			if(tabMovimientosTarjetas.isSelected()){
				paneResumenFidelizadoController.selected();
				tpDatosFidelizado.getSelectionModel().select(tabResumen);
			}
		}
		try {
			super.compruebaPermisos(PERMISO_ETIQUETAS);
			verEtiquetas = true;
			if("CONSULTA".equals(getModo())){
				tabEtiquetas.setDisable(false);
			}
		}
		catch (SinPermisosException ex) {
			verEtiquetas = false;
			tabEtiquetas.setDisable(true);
			if(tabEtiquetas.isSelected()){
				paneResumenFidelizadoController.selected();
				tpDatosFidelizado.getSelectionModel().select(tabResumen);
			}
		}
		try {
			super.compruebaPermisos(PERMISO_IMPRIMIR);
			btImprimir.setDisable(false);
		}
		catch (SinPermisosException ex) {
			btImprimir.setDisable(true);
		}
		try {
			super.compruebaPermisos(PERMISO_DATOSSENSIBLES);
			verDatosSensibles = true;
		}
		catch (SinPermisosException ex) {
			verDatosSensibles = false;
		}
	}

	public boolean isPuedeVerDatosSensibles() {
		return verDatosSensibles;
	}
	
	public boolean isPuedeVerColectivos() {
		return verColectivos;
	}
	
	public boolean isPuedeVerVentas(){
		return verUltVentas;
	}

}
