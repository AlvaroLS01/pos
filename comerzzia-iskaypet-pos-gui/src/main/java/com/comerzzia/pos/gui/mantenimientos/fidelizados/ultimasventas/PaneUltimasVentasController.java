package com.comerzzia.pos.gui.mantenimientos.fidelizados.ultimasventas;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.sales.ArticuloAlbaranVentaBean;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarUltimasVentasFidelizadoTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.FidelizadoController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.VentaGui;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class PaneUltimasVentasController extends TabController<FidelizadoController> implements IContenedorBotonera, Initializable{
	
	protected static final Logger log = Logger.getLogger(PaneUltimasVentasController.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
    protected VariablesServices variablesService;
	
	@FXML
	protected TableView<VentaGui> tableVentas;
	
	@FXML
	protected TableColumn<VentaGui, Date> tcFechaVenta;
	@FXML
	protected TableColumn<VentaGui, String> tcArticuloVenta;
	@FXML
	protected TableColumn<VentaGui, String> tcDescripcionVenta;
	@FXML
	protected TableColumn<VentaGui, String> tcDesglose1Venta;
	@FXML
	protected TableColumn<VentaGui, String> tcDesglose2Venta;
	@FXML
	protected TableColumn<VentaGui, BigDecimal> tcCantidadVenta;
	@FXML
	protected TableColumn<VentaGui, BigDecimal> tcImporteVenta;
	@FXML
	protected TableColumn<VentaGui, String> tcCodTiendaVenta;
	
	protected FidelizadoBean fidelizado;
	
	protected ObservableList<VentaGui> ventasFidelizado;
	
	protected BotoneraComponent botoneraAccionesTabla;
	
	@FXML
	protected AnchorPane panelBotonesVentas;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tableVentas.setPlaceholder(new Text(""));
		
		tcFechaVenta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcFechaVenta", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));
		tcArticuloVenta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcArticuloVenta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDescripcionVenta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcDescripcionVenta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDesglose1Venta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcDesglose1Venta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDesglose2Venta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcDesglose2Venta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCantidadVenta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcCantidadVenta", null, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcImporteVenta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcImporteVenta", null, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcCodTiendaVenta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableVentas", "tcCodTiendaVenta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

		tcFechaVenta.setCellValueFactory(new PropertyValueFactory<VentaGui, Date>("fecha"));
		tcArticuloVenta.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("articulo"));
		tcDescripcionVenta.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("descripcion"));
		tcDesglose1Venta.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("desglose1"));
		tcDesglose2Venta.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("desglose2"));
		tcCantidadVenta.setCellValueFactory(new PropertyValueFactory<VentaGui, BigDecimal>("cantidad"));
		tcImporteVenta.setCellValueFactory(new PropertyValueFactory<VentaGui, BigDecimal>("importe"));
		tcCodTiendaVenta.setCellValueFactory(new PropertyValueFactory<VentaGui, String>("codTienda"));
		
		ventasFidelizado = FXCollections.observableArrayList();
		
		if (sesion.getAplicacion().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
			tcDesglose1Venta.setText(I18N.getTexto(variablesService.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcDesglose1Venta.setVisible(false);
			tcDescripcionVenta.setPrefWidth(tcDescripcionVenta.getPrefWidth() + 70.0);
		}
		if (sesion.getAplicacion().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
			tcDesglose2Venta.setText(I18N.getTexto(variablesService.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcDesglose2Venta.setVisible(false);
			tcDescripcionVenta.setPrefWidth(tcDescripcionVenta.getPrefWidth() + 70.0);
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
	
	public void selected(){
		FidelizadoBean newFidelizado = getTabParentController().getFidelizado();
		if(newFidelizado != null && (fidelizado == null || !fidelizado.getIdFidelizado().equals(newFidelizado.getIdFidelizado()))){
			cargarVentas(newFidelizado);
		}	
		fidelizado = newFidelizado;		
	}
	
	protected void cargarVentas(FidelizadoBean fidelizado){
		
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		ConsultarFidelizadoRequestRest consultaVentas = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
		consultaVentas.setIdFidelizado(String.valueOf(fidelizado.getIdFidelizado()));
		Date fechaHoy = new Date();
		Calendar calDesde = Calendar.getInstance();
		calDesde.set(Calendar.YEAR, calDesde.get(Calendar.YEAR)-1);
		Date fechaDesde = calDesde.getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		consultaVentas.setFechaDesde(format.format(fechaDesde));
		consultaVentas.setFechaHasta(format.format(fechaHoy));
		ConsultarUltimasVentasFidelizadoTask consultarVentasTask = SpringContext.getBean(ConsultarUltimasVentasFidelizadoTask.class, 
				consultaVentas, 
					new RestBackgroundTask.FailedCallback<List<ArticuloAlbaranVentaBean>>() {
						@Override
						public void succeeded(List<ArticuloAlbaranVentaBean> result) {						
							ventasFidelizado.clear();
							for(ArticuloAlbaranVentaBean art : result){
								VentaGui artGui = new VentaGui(art);
								ventasFidelizado.add(artGui);
							}
							tableVentas.setItems(ventasFidelizado);
						}
						@Override
						public void failed(Throwable throwable) {
							getTabParentController().getApplication().getMainView().close();
						}
					}, getTabParentController().getStage());
		consultarVentasTask.start();
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
	
	public void setFidelizado(FidelizadoBean fidelizado){
		this.fidelizado = fidelizado;
	}

}
