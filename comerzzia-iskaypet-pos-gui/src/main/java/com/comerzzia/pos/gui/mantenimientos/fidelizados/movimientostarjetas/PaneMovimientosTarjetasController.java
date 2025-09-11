package com.comerzzia.pos.gui.mantenimientos.fidelizados.movimientostarjetas;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.loyalty.MovimientoBean;
import com.comerzzia.api.model.loyalty.TarjetaBean;
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
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarMovimientosTarjetaTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ConsultarTarjetasFidelizadoTask;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.FidelizadoController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.MovimientoGui;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Component
public class PaneMovimientosTarjetasController extends TabController<FidelizadoController> implements Initializable, IContenedorBotonera{
	
	protected static final Logger log = Logger.getLogger(PaneMovimientosTarjetasController.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
    protected VariablesServices variablesService;
	
	@FXML
	protected ComboBox<TarjetaBean> cbNumeroTarjeta;
	
	@FXML
	protected TextField tfTipoTarjeta, tfSaldo;
	
	@FXML
	protected TableView<MovimientoGui> tableMovimientos;
	
	@FXML
	protected TableColumn<MovimientoGui, String> tcConcepto;
	
	@FXML
	protected TableColumn<MovimientoGui, String> tcEstadoMovimiento;
	
	@FXML
	protected TableColumn<MovimientoGui, Date> tcFecha;
	
	@FXML
	protected TableColumn<MovimientoGui, BigDecimal> tcEntrada, tcSalida;
	
	@FXML
	protected Label lbTotalEntrada, lbTotalSalida;
	
	protected FidelizadoBean fidelizado;
	
	protected ObservableList<TarjetaBean> tarjetasFidelizado;
	
	protected ObservableList<MovimientoGui> movimientosTarjeta;
	
	protected BotoneraComponent botoneraAccionesTabla;
	
	@FXML
	protected AnchorPane panelBotonesMovimientos;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tableMovimientos.setPlaceholder(new Text(""));
		
		tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableMovimientos", "tcFecha", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));
		tcConcepto.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableMovimientos", "tcConcepto", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcEstadoMovimiento.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableMovimientos", "tcEstadoMovimiento", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcEntrada.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableMovimientos", "tcEntrada", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcSalida.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableMovimientos", "tcSalida", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		tcFecha.setCellValueFactory(new PropertyValueFactory<MovimientoGui, Date>("fecha"));
		tcConcepto.setCellValueFactory(new PropertyValueFactory<MovimientoGui, String>("concepto"));
		tcEstadoMovimiento.setCellValueFactory(new PropertyValueFactory<MovimientoGui, String>("estado"));
		tcEntrada.setCellValueFactory(new PropertyValueFactory<MovimientoGui, BigDecimal>("entrada"));
		tcSalida.setCellValueFactory(new PropertyValueFactory<MovimientoGui, BigDecimal>("salida"));
		List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
		try {
			botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelBotonesMovimientos.getPrefWidth(), panelBotonesMovimientos.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getCanonicalName());
		} catch (CargarPantallaException e) {
			log.error("initializeComponents() - Ha habido un error al cargar la botonera de la tabla: " + e.getMessage());
		}
		panelBotonesMovimientos.getChildren().add(botoneraAccionesTabla);
		
		StringConverter<TarjetaBean> converter = new StringConverter<TarjetaBean>() {
			
			@Override
			public String toString(TarjetaBean arg0) {
				return arg0.getNumeroTarjeta();
			}
			
			@Override
			public TarjetaBean fromString(String arg0) {
				return null;
			}
		};
		
		tarjetasFidelizado = FXCollections.observableArrayList();
		cbNumeroTarjeta.setConverter(converter);
		
		movimientosTarjeta = FXCollections.observableArrayList();
		
		tfSaldo.setEditable(false);
		tfTipoTarjeta.setEditable(false);
		
		tfSaldo.setAlignment(Pos.CENTER_RIGHT);
		lbTotalEntrada.setAlignment(Pos.BASELINE_RIGHT);
		lbTotalSalida.setAlignment(Pos.BASELINE_RIGHT);
	}
	
	public void selected(){
		FidelizadoBean newFidelizado = getTabParentController().getFidelizado();
		if(newFidelizado != null && (fidelizado == null || !fidelizado.getIdFidelizado().equals(newFidelizado.getIdFidelizado()))){
			cargarTarjetas(newFidelizado);
		}	
		fidelizado = newFidelizado;
	}
	
	protected void cargarTarjetas(FidelizadoBean fidelizado){
		movimientosTarjeta.clear();
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		ConsultarFidelizadoRequestRest consultaTarjetas = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
		consultaTarjetas.setIdFidelizado(String.valueOf(fidelizado.getIdFidelizado()));
		consultaTarjetas.setPermiteVincular("S");
		ConsultarTarjetasFidelizadoTask consultarTarjetasTask = SpringContext.getBean(ConsultarTarjetasFidelizadoTask.class, 
					consultaTarjetas, 
					new RestBackgroundTask.FailedCallback<List<TarjetaBean>>() {
						@Override
						public void succeeded(List<TarjetaBean> result) {						
							tarjetasFidelizado.clear();
							for(TarjetaBean tarj : result){
								tarjetasFidelizado.add(tarj);
							}
							cbNumeroTarjeta.setItems(tarjetasFidelizado);
							String numeroTarjetaFidelizado = getTabParentController().getNumeroTarjetaFidelizado();
							for(TarjetaBean tarj : cbNumeroTarjeta.getItems()){
								if(numeroTarjetaFidelizado.equals(tarj.getNumeroTarjeta())){
									cbNumeroTarjeta.getSelectionModel().select(tarj);
									break;
								}
							}
						}
						@Override
						public void failed(Throwable throwable) {
							getTabParentController().getApplication().getMainView().close();
						}
					}, getTabParentController().getStage());
		consultarTarjetasTask.start();
	}
	
	@FXML
	public void cargarMovimientos(){
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		TarjetaBean tarjetaSeleccionada = cbNumeroTarjeta.getValue();
		if(tarjetaSeleccionada != null){
			tfTipoTarjeta.setText(tarjetaSeleccionada.getDesTipoTarj());
			tfSaldo.setText(FormatUtil.getInstance().formateaNumero(new BigDecimal(tarjetaSeleccionada.getSaldo() + tarjetaSeleccionada.getSaldoProvisional()), 2));
			ConsultarFidelizadoRequestRest consultaMovimientos = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
			consultaMovimientos.setIdFidelizado(String.valueOf(fidelizado.getIdFidelizado()));
			consultaMovimientos.setIdTarjeta(String.valueOf(tarjetaSeleccionada.getIdTarjeta()));
			consultaMovimientos.setUltimosMovimientos(20);
			ConsultarMovimientosTarjetaTask consultarMovimientosTask = SpringContext.getBean(ConsultarMovimientosTarjetaTask.class, 
					consultaMovimientos, 
					new RestBackgroundTask.FailedCallback<List<MovimientoBean>>() {
						@Override
						public void succeeded(List<MovimientoBean> result) {						
							movimientosTarjeta.clear();
							BigDecimal totalEntrada = BigDecimal.ZERO;
							BigDecimal totalSalida = BigDecimal.ZERO;
							for(MovimientoBean mov : result){
								MovimientoGui movGui = new MovimientoGui(mov);
								if(movGui.getEntrada() != null){
									totalEntrada = totalEntrada.add(movGui.getEntrada());
								}
								if(movGui.getSalida() != null){
									totalSalida = totalSalida.add(movGui.getSalida());
								}
								movimientosTarjeta.add(movGui);
							}
							tableMovimientos.setItems(movimientosTarjeta);
							lbTotalEntrada.setText(FormatUtil.getInstance().formateaNumero(totalEntrada, 2));
							lbTotalSalida.setText(FormatUtil.getInstance().formateaNumero(totalSalida, 2));
						}
						@Override
						public void failed(Throwable throwable) {
							getTabParentController().getApplication().getMainView().close();
						}
					}, getTabParentController().getStage());
			consultarMovimientosTask.start();
		}
	}
	
	private List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<ConfiguracionBotonBean>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO_MOVIMIENTOS", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO_MOVIMIENTOS", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO_MOVIMIENTOS", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO_MOVIMIENTOS", "REALIZAR_ACCION"));
		return listaAcciones;
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
			case "ACCION_TABLA_PRIMER_REGISTRO_MOVIMIENTOS":
				getTabParentController().accionTablaPrimerRegistro(tableMovimientos);
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO_MOVIMIENTOS":
				getTabParentController().accionTablaIrAnteriorRegistro(tableMovimientos);
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO_MOVIMIENTOS":
				getTabParentController().accionTablaIrSiguienteRegistro(tableMovimientos);
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO_MOVIMIENTOS":
				getTabParentController().accionTablaUltimoRegistro(tableMovimientos);
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
		}
	}

	@Override
	public void compruebaPermisos(String operacion) throws SinPermisosException {
		// TODO Auto-generated method stub
		
	}
	
	public void limpiarFormulario(){
		tfSaldo.clear();
		tfTipoTarjeta.clear();
	}
	
	public void setFidelizado(FidelizadoBean fidelizado){
		this.fidelizado = fidelizado;
	}

}
