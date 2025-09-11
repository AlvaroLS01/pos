package com.comerzzia.pos.gui.mantenimientos.fidelizados.resumen;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.core.TiendaBean;
import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.loyalty.TarjetaBean;
import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.model.sales.ArticuloAlbaranVentaBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ColectivoGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarTarjetasFidelizadoTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarTiendaFavoritaFidelizadoTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarUltimasVentasFidelizadoTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.FidelizadoController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.VentaGui;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class PaneResumenFidelizadoController extends TabController<FidelizadoController> implements IContenedorBotonera, Initializable{
	
	protected static final Logger log = Logger.getLogger(PaneResumenFidelizadoController.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
    protected VariablesServices variablesService;
	
	protected FidelizadoBean fidelizado;
	
	@FXML
	protected TextField tfCodigo, tfNumeroTarjeta, tfSaldo, tfNombre, tfDocumento, tfCodTienda, tfDesTienda, tfEmail, tfMovil, tfTipoDocumento;
	
	@FXML
	protected DatePicker dpFechaNacimiento;
	
	@FXML
	protected TableView<ColectivoGui> tableColectivos;
	
	protected ObservableList<ColectivoGui> colectivos;
	
	@FXML
	protected TableColumn<ColectivoGui, String> tcCodColectivo, tcDesColectivo, tcTipoColectivo;
	
	@FXML
	protected TableView<VentaGui> tableVentas;
	
	@FXML
	protected TableColumn<VentaGui, String> tcArticuloVentaRes, tcDescripcionVentaRes, tcDesglose1VentaRes, tcDesglose2VentaRes, tcCodTiendaVentaRes;
	
	protected ObservableList<VentaGui> ventas;
	
	@FXML
	protected TableColumn<VentaGui, Date> tcFechaVentaRes;
	
	@FXML
	protected TableColumn<VentaGui, BigDecimal> tcCantidadVentaRes, tcImporteVentaRes;
	
	protected TiendaBean tiendaFavorita;
	
	protected BotoneraComponent botoneraAccionesTabla;
	
	@FXML
	protected AnchorPane panelBotonesVentas;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		editarCampos();
		tfSaldo.setAlignment(Pos.BASELINE_RIGHT);
	}
	
	public void selected(){
		FidelizadoBean newFidelizado = getTabParentController().getFidelizado();
		if(newFidelizado != null && (fidelizado == null || !fidelizado.getIdFidelizado().equals(newFidelizado.getIdFidelizado()))){
			limpiarFormulario();
			cargarTiendaFavorita(newFidelizado);
		}	
		fidelizado = newFidelizado;
		
	}
	
	@FXML
	public void accionBuscarTienda(){
		
	}
	
	public void limpiarFormulario(){
		tfCodigo.clear();
		tfNumeroTarjeta.clear();
		tfSaldo.clear();
		tfNombre.clear();
		tfDocumento.clear();
		tfCodTienda.clear();
		tfDesTienda.clear();
		tfEmail.clear();
		tfMovil.clear();
		dpFechaNacimiento.clear();
	}
	
	protected void editarCampos(){
		tfCodigo.setEditable(false);
		tfNumeroTarjeta.setEditable(false);
		tfSaldo.setEditable(false);
		tfNombre.setEditable(false);
		tfDocumento.setEditable(false);
		tfCodTienda.setEditable(false);
		tfDesTienda.setEditable(false);
		tfEmail.setEditable(false);
		tfMovil.setEditable(false);
		dpFechaNacimiento.setDisable(true);
		tfTipoDocumento.setEditable(false);
		
		tableColectivos.setPlaceholder(new Text(""));
		
		tcCodColectivo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableColectivos", "tcCodColectivo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDesColectivo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableColectivos", "tcDesColectivo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcTipoColectivo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableColectivos", "tcTipoColectivo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

        tcCodColectivo.setCellValueFactory(new PropertyValueFactory<ColectivoGui, String>("codColectivo"));
        tcDesColectivo.setCellValueFactory(new PropertyValueFactory<ColectivoGui, String>("desColectivo"));
        tcTipoColectivo.setCellValueFactory(new PropertyValueFactory<ColectivoGui, String>("tipoColectivo"));
        
        tableVentas.setPlaceholder(new Text(""));

        tcFechaVentaRes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcFechaVentaRes", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));
        tcArticuloVentaRes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcArticuloVentaRes", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDescripcionVentaRes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcDescripcionVentaRes", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDesglose1VentaRes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcDesglose1VentaRes", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDesglose2VentaRes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcDesglose2VentaRes", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCantidadVentaRes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcCantidadVentaRes", null, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
        tcImporteVentaRes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcImporteVentaRes", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
        tcCodTiendaVentaRes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcCodTiendaVentaRes", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

        tcFechaVentaRes.setCellValueFactory(new PropertyValueFactory<VentaGui, Date>("fecha"));
        tcArticuloVentaRes.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("articulo"));
        tcDescripcionVentaRes.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("descripcion"));
        tcDesglose1VentaRes.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("desglose1"));
        tcDesglose2VentaRes.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("desglose2"));
        tcCantidadVentaRes.setCellValueFactory(new PropertyValueFactory<VentaGui, BigDecimal>("cantidad"));
        tcImporteVentaRes.setCellValueFactory(new PropertyValueFactory<VentaGui, BigDecimal>("importe"));
        tcCodTiendaVentaRes.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("codTienda"));
        
        if (sesion.getAplicacion().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
        	tcDesglose1VentaRes.setText(I18N.getTexto(variablesService.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcDesglose1VentaRes.setVisible(false);
			tcDescripcionVentaRes.setPrefWidth(tcDescripcionVentaRes.getPrefWidth() + 54.0);
			
		}
		if (sesion.getAplicacion().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
			tcDesglose2VentaRes.setText(I18N.getTexto(variablesService.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcDesglose2VentaRes.setVisible(false);
			tcDescripcionVentaRes.setPrefWidth(tcDescripcionVentaRes.getPrefWidth() + 54.0);
		}
		List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
		try {
			botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelBotonesVentas.getPrefWidth(), panelBotonesVentas.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getCanonicalName());
		} catch (CargarPantallaException e) {
			log.error("initializeComponents() - Ha habido un error al cargar la botonera de la tabla: " + e.getMessage());
		}
		panelBotonesVentas.getChildren().add(botoneraAccionesTabla);
	}
	
	protected void cargarTiendaFavorita(final FidelizadoBean fidelizado){
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
			ConsultarFidelizadoRequestRest consulta = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
			consulta.setIdFidelizado(String.valueOf(fidelizado.getIdFidelizado()));
			
			ConsultarTiendaFavoritaFidelizadoTask consultarTiendaFavoritaTask = SpringContext.getBean(ConsultarTiendaFavoritaFidelizadoTask.class, 
					consulta, 
					new RestBackgroundTask.FailedCallback<TiendaBean>() {
						@Override
						public void succeeded(TiendaBean result) {						
							tiendaFavorita = result;	
							cargarResumen(fidelizado);
						}
						@Override
						public void failed(Throwable throwable) {
							getTabParentController().getApplication().getMainView().close();
						}
					}, getTabParentController().getStage());
			consultarTiendaFavoritaTask.start();
			
			ConsultarTarjetasFidelizadoTask consultarTarjetasTask = SpringContext.getBean(ConsultarTarjetasFidelizadoTask.class, 
					consulta, 
					new RestBackgroundTask.FailedCallback<List<TarjetaBean>>() {
						@Override
						public void succeeded(List<TarjetaBean> result) {													
							   Set<Long> cuentas = new HashSet<Long>();		
							   List<TarjetaBean> tarjetasFidelizado = new ArrayList<TarjetaBean>();
							   Double saldoAcumulado = Double.valueOf(0);
							   if(result != null) {
								   for(TarjetaBean tarjeta: result) {
									   if(!tarjeta.getPermitePago()) {
										   if(!cuentas.contains(tarjeta.getIdCuentaTarjeta())){ //Comprobamos que las tarjetas sean de cuentas independientes
											   saldoAcumulado = saldoAcumulado + tarjeta.getSaldo();
										   }								  
										   cuentas.add(tarjeta.getIdCuentaTarjeta());
									   }
									   if(tarjeta.getPermiteVincular() && !tarjeta.getPermitePago() &&tarjeta.isActivo()){
										   tarjetasFidelizado.add(tarjeta);
									   }
								   }
								   tfSaldo.setText(FormatUtil.getInstance().formateaNumero(new BigDecimal(saldoAcumulado), 2));
								   if(StringUtils.isNotBlank(getTabParentController().getNumeroTarjetaFidelizado())){
									   tfNumeroTarjeta.setText(getTabParentController().getNumeroTarjetaFidelizado());
								   }else{
									   tfNumeroTarjeta.setText("");
								   }
								   
							   }

						}
						@Override
						public void failed(Throwable throwable) {
							getTabParentController().getApplication().getMainView().close();
						}
					}, getTabParentController().getStage());
			consultarTarjetasTask.start();
			
			consulta.setIdFidelizado(String.valueOf(fidelizado.getIdFidelizado()));
			Date fechaHoy = new Date();
			Calendar calDesde = Calendar.getInstance();
			calDesde.set(Calendar.YEAR, calDesde.get(Calendar.YEAR)-1);
			Date fechaDesde = calDesde.getTime();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			consulta.setFechaDesde(format.format(fechaDesde));
			consulta.setFechaHasta(format.format(fechaHoy));
			ConsultarUltimasVentasFidelizadoTask consultarVentasTask = SpringContext.getBean(ConsultarUltimasVentasFidelizadoTask.class, 
					consulta, 
						new RestBackgroundTask.FailedCallback<List<ArticuloAlbaranVentaBean>>() {
							@Override
							public void succeeded(List<ArticuloAlbaranVentaBean> result) {
								if(isPuedeVerVentas()){
									ventas = FXCollections.observableArrayList();
									for(ArticuloAlbaranVentaBean art : result){
										VentaGui artGui = new VentaGui(art);
										ventas.add(artGui);
									}							
								}else{
									ventas = null;
								}
								tableVentas.setItems(ventas);
							}
							@Override
							public void failed(Throwable throwable) {
								getTabParentController().getApplication().getMainView().close();
							}
						}, getTabParentController().getStage());
			consultarVentasTask.start();
		
	}
	
	protected void cargarResumen(FidelizadoBean fidelizado){
		log.debug("cargarResumen()");		

		tfCodigo.setText(fidelizado.getCodFidelizado());
		tfNombre.setText(fidelizado.getNombreCompleto());
		tfTipoDocumento.setText(fidelizado.getCodTipoIden());
		dpFechaNacimiento.setValue(fidelizado.getFechaNacimiento());		
		TiposContactoFidelizadoBean email = fidelizado.getTipoContacto("EMAIL");
		TiposContactoFidelizadoBean movil = fidelizado.getTipoContacto("MOVIL");
		if(!isPuedeVerDatosSensibles()){
			ocultaDatoSensible(tfDocumento, fidelizado.getDocumento());
			if(email != null){
				ocultaDatoSensible(tfEmail, email.getValor());
			}
			if(movil != null){
				ocultaDatoSensible(tfMovil, movil.getValor());
			}
			
		}else{
			tfDocumento.setText(fidelizado.getDocumento());
			if(email != null){
				tfEmail.setText(email.getValor());
			}
			if(movil != null){
				tfMovil.setText(movil.getValor());
			}
		}
		if(tiendaFavorita != null){
			tfCodTienda.setText(tiendaFavorita.getCodAlm());
			tfDesTienda.setText(tiendaFavorita.getDesAlm());
		}
		
		List<ColectivoGui> listColectivos = new ArrayList<ColectivoGui>();
		for(ColectivosFidelizadoBean col : fidelizado.getColectivos()){
			if(!"S".equals(col.getPrivado())){
				ColectivoGui colGui = new ColectivoGui(col);
				listColectivos.add(colGui);
			}
		}
		if(isPuedeVerColectivos()){			
			colectivos = FXCollections.observableArrayList(listColectivos);
		}else{
			colectivos = null;
		}
        tableColectivos.setItems(colectivos);
		
		
	}
	
	protected void ocultaDatoSensible(TextField textField, String string) {
		String sustituir = string.substring(1, string.length()-1);
		String car = sustituir.replaceAll(".", "*");
		textField.setText(string.replace(sustituir, car));
	}

	@Override
	public Stage getStage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registraEventoTeclado(EventHandler<KeyEvent> keyEventHandler,
			EventType<KeyEvent> keyEventType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminaEventoTeclado(EventHandler<KeyEvent> keyEventHandler,
			EventType<KeyEvent> keyEventType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
		switch (botonAccionado.getClave()) {
			case "ACCION_TABLA_PRIMER_REGISTRO_VENTAS":
				getTabParentController().accionTablaPrimerRegistro(tableVentas);
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO_VENTAS":
				getTabParentController().accionTablaIrAnteriorRegistro(tableVentas);
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO_VENTAS":
				getTabParentController().accionTablaIrSiguienteRegistro(tableVentas);
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO_VENTAS":
				getTabParentController().accionTablaUltimoRegistro(tableVentas);
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
		}
	}

	@Override
	public void compruebaPermisos(String operacion) throws SinPermisosException {
		// TODO Auto-generated method stub
		
	}
	
	private List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<ConfiguracionBotonBean>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO_VENTAS", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO_VENTAS", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO_VENTAS", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO_VENTAS", "REALIZAR_ACCION"));
		return listaAcciones;
	}

	public FidelizadoBean getFidelizado() {
		return fidelizado;
	}

	public void setFidelizado(FidelizadoBean fidelizado) {
		this.fidelizado = fidelizado;
	}
	
	public boolean isPuedeVerDatosSensibles() {
		return getTabParentController().isPuedeVerDatosSensibles();
	}
	
	public boolean isPuedeVerColectivos(){
		return getTabParentController().isPuedeVerColectivos();
	}
	
	public boolean isPuedeVerVentas(){
		return getTabParentController().isPuedeVerVentas();
	}

}
