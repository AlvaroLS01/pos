/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.gui.ventas.apartados.detalle;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuarioController;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.apartados.ApartadosManager;
import com.comerzzia.pos.gui.ventas.apartados.detalle.datosCliente.CambiarDatosClienteApartadoController;
import com.comerzzia.pos.gui.ventas.apartados.detalle.datosCliente.CambiarDatosClienteApartadoView;
import com.comerzzia.pos.gui.ventas.apartados.detalle.marcarVenta.MarcarVentaApartadoController;
import com.comerzzia.pos.gui.ventas.apartados.detalle.marcarVenta.MarcarVentaApartadoView;
import com.comerzzia.pos.gui.ventas.apartados.detalle.pagos.NuevoPagoApartadoController;
import com.comerzzia.pos.gui.ventas.apartados.detalle.pagos.NuevoPagoApartadoView;
import com.comerzzia.pos.gui.ventas.apartados.detalle.verPagos.VerPagosApartadoController;
import com.comerzzia.pos.gui.ventas.apartados.detalle.verPagos.VerPagosApartadoView;
import com.comerzzia.pos.gui.ventas.apartados.pagoApartado.PagoApartadoController;
import com.comerzzia.pos.gui.ventas.apartados.pagoApartado.PagoApartadoView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController.LineaInsertadaNoPermitidaException;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FormularioLineaArticuloBean;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloView;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteController;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteView;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.pos.persistence.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.impuestos.tratamientos.TratamientoImpuestoBean;
import com.comerzzia.pos.persistence.core.permisos.PermisoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.impuestos.tratamientos.TratamientoImpuestoService;
import com.comerzzia.pos.services.core.permisos.PermisoException;
import com.comerzzia.pos.services.core.permisos.PermisosEfectivosAccionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class DetalleApartadosController extends WindowController implements IContenedorBotonera{

	protected Logger log = Logger.getLogger(getClass());

	public static final String PARAMETRO_APARTADO = "APARTADO";
	public static final String PARAMETRO_EVENTO_BUSQUEDA = "ACCION_BUSQUEDA";
	public static final String PARAMETRO_EDITAR_APARTADO = "EDITAR_APARTADO";
	public static final String PARAMETRO_NUEVO_APARTADO = "NUEVO_APARTADO";
	public static final String PARAMETRO_APARTADO_MGR = "APARTADO_MGR";
	public static final String PARAMETRO_IMPORTE_MAXIMO_PAGO = "IMPORTE_MAXIMO_PAGO";
	public static final String INICIALIZAR_PANTALLA = "N";
	
    final IVisor visor = Dispositivos.getInstance().getVisor();

	@FXML
	protected TextField tfArticulo, tfTelefono, tfCodCliente, tfDesCliente, tfProvincia, tfCP,
	tfDomicilio, tfPoblacion, tfNumDoc;
	
	@FXML
	protected DatePicker tfFechaRecogida;

	@FXML
	protected Label lbSaldo, lbPendiente, lbServido, lbAbonado, lbCabecera;

	@FXML
	protected TecladoNumerico tecladoNumerico;

	protected ApartadosManager apartadoManager;
	
	@Autowired
	private Sesion sesion;
	
	protected TicketManager ticketManager;

	@FXML
	protected TableView tbArticulos;

	@FXML
	protected TableColumn tcArticulo, tcDescripcion, tcDesglose1, tcDesglose2, tcDescuento, tcCantidad, tcPrecio, tcImporte;

	protected ObservableList<LineaArticuloApartadoGui> articulos;

	protected FormularioLineaArticuloBean frArticulo;
	
	// botonera inferior de pantalla
	protected BotoneraComponent botonera, botoneraAccionesTabla;
    
    @FXML
    public AnchorPane panelBotonera, panelBotoneraTabla;
    
    @FXML
    public Button btBuscarArticulos;
    
    protected int nuevosArticulos;
    
    protected FormularioDetalleApartadoBean frDatosApartado;
    
    protected boolean apartadoNuevo = false;
    
    protected boolean modoEdicion = false;
    
    protected EventHandler actionHandlerBuscar;
    
    @Autowired
    private VariablesServices variablesServices;
    
    @Autowired
	private TratamientoImpuestoService tratamientoImpuestoService;
    
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		frDatosApartado = SpringContext.getBean(FormularioDetalleApartadoBean.class);
		frArticulo = SpringContext.getBean(FormularioLineaArticuloBean.class);

		frDatosApartado.setFormField("fechaRecogida", tfFechaRecogida);
		frArticulo.setFormField("codArticulo", tfArticulo);

		articulos = FXCollections.observableArrayList();
		tbArticulos.setPlaceholder(new Label(""));

		tcArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcArticulo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDesglose1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcDesglose1", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDesglose2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcDesglose2", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcPrecio.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcPrecio", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcImporte", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcDescuento.setCellFactory(CellFactoryBuilder.createCellRendererCeldaPorcentaje("tcDescuento", "tcDescuento", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		//        tcVendedor.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcVendedor", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcCantidad", 3, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		Boolean usaDescuentoEnLinea = variablesServices.getVariableAsBoolean(VariablesServices.TICKETS_USA_DESCUENTO_EN_LINEA);
		if(!usaDescuentoEnLinea){
			tcDescuento.setVisible(false);
		}
		if (sesion.getAplicacion().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
			tcDesglose1.setText(I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcDesglose1.setVisible(false);
		}
		if (sesion.getAplicacion().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
			tcDesglose2.setText(I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcDesglose2.setVisible(false);
		}
		
		
		// Asignamos las lineas a la tabla
		tbArticulos.setItems(articulos);

		// Definimos un factory para cada celda para aumentar el rendimiento
		tcArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaArticuloApartadoGui, String> cdf) {
				return cdf.getValue().getArticuloProperty();
			}
		});
		tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaArticuloApartadoGui, String> cdf) {
				return cdf.getValue().getDescripcionProperty();
			}
		});
		tcCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaArticuloApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getCantidadProperty();
			}
		});
		tcDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaArticuloApartadoGui, String> cdf) {
				return cdf.getValue().getDesglose1Property();
			}
		});
		tcDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaArticuloApartadoGui, String> cdf) {
				return cdf.getValue().getDesglose2Property();
			}
		});
		tcPrecio.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaArticuloApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getPvpProperty();
			}
		});
		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaArticuloApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getImporteTotalProperty();
			}
		});
		tcDescuento.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaArticuloApartadoGui, BigDecimal> cdf) {
				return cdf.getValue().getDescuentoProperty();
			}
		});
		//        tcVendedor.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoGui, String>, ObservableValue<String>>() {
			//            @Override
			//            public ObservableValue<String> call(CellDataFeatures<LineaApartadoGui, String> cdf) {
				//                return cdf.getValue().getVendedorProperty();
		//            }
		//        });

		tbArticulos.setRowFactory(new Callback<TableView<LineaArticuloApartadoGui>, TableRow<LineaArticuloApartadoGui>>() {

			@Override
			public TableRow<LineaArticuloApartadoGui> call(TableView<LineaArticuloApartadoGui> p) {
				final TableRow<LineaArticuloApartadoGui> row = new TableRow<LineaArticuloApartadoGui>() {
					@Override
					protected void updateItem(LineaArticuloApartadoGui linea, boolean empty){
						super.updateItem(linea, empty);
						if (linea!=null){
							if(linea.isVendido()) {
								getStyleClass().removeAll(Collections.singleton("cell-renderer-apartadoCancelado"));
								if (!getStyleClass().contains("cell-renderer-lineaDocAjeno")) {
									getStyleClass().add("cell-renderer-lineaDocAjeno");
								}
							}
							else if(linea.getDetalle().getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_CANCELADO) {
								getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
								getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
        						if (!getStyleClass().contains("cell-renderer-apartadoCancelado")) {
        							getStyleClass().add("cell-renderer-apartadoCancelado");
        						}
        					}
							else {
								getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
        						getStyleClass().removeAll(Collections.singleton("cell-renderer-apartadoCancelado"));
        						getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
        					}
							
        				} 
        				else {
        					getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
        					getStyleClass().removeAll(Collections.singleton("cell-renderer-apartadoCancelado"));
        					getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
        					
        				}
					}
				};
				return row;
			}
		});
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		initTecladoNumerico(tecladoNumerico);
		
		try{
			PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
			botonera = new BotoneraComponent(panelBotoneraBean, null, null, this, BotonBotoneraNormalComponent.class);
			panelBotonera.getChildren().add(botonera);
		}
		catch (InitializeGuiException | CargarPantallaException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		} 
	}

	@Override
	public void initializeForm() throws InitializeGuiException {	
		//si no tenemos que inicializar la pantalla nos salimos
		if(!getDatos().containsKey(INICIALIZAR_PANTALLA)){
			return;
		}
		else{
			getDatos().remove(INICIALIZAR_PANTALLA);
		}
		
		if(!getDatos().isEmpty()){
			nuevosArticulos = 0;
			actionHandlerBuscar = null;
			articulos.clear();
			frArticulo.clearErrorStyle();
		
		
			if(getDatos().containsKey(PARAMETRO_APARTADO_MGR)){
				apartadoManager = (ApartadosManager)getDatos().remove(PARAMETRO_APARTADO_MGR);
			}
			
			if(getDatos().containsKey(FacturacionArticulosController.TICKET_KEY)){
				ticketManager = (TicketManager)getDatos().remove(FacturacionArticulosController.TICKET_KEY);
			}		

			if(getDatos().containsKey(PARAMETRO_APARTADO)){
				
				ApartadosCabeceraBean apartado = (ApartadosCabeceraBean)getDatos().remove(PARAMETRO_APARTADO);
				apartadoManager.cargarApartado(apartado);
				tfFechaRecogida.setSelectedDate(apartado.getFechaApartado());			

				lbCabecera.setText(I18N.getTexto("Detalle apartado {0}", apartado.getIdApartado()));
				refrescarDatosPantalla();
			}
			else{
				apartadoManager.nuevoTicketApartado();
				ClienteBean clienteInicial = apartadoManager.getCliente();
				tfCodCliente.setText(clienteInicial.getCodCliente());
				tfCP.setText(clienteInicial.getCp());
				tfDesCliente.setText(clienteInicial.getDesCliente());
				tfDomicilio.setText(clienteInicial.getDomicilio());
				tfNumDoc.setText(clienteInicial.getCif());
				tfPoblacion.setText(clienteInicial.getPoblacion());
				tfProvincia.setText(clienteInicial.getProvincia());
				tfTelefono.setText(clienteInicial.getTelefono1());

				tfFechaRecogida.setSelectedDate(new Date());

				lbSaldo.setText(FormatUtil.getInstance().formateaImporte(BigDecimal.ZERO));
				lbPendiente.setText(FormatUtil.getInstance().formateaImporte(BigDecimal.ZERO));
				lbServido.setText(FormatUtil.getInstance().formateaImporte(BigDecimal.ZERO));
				lbAbonado.setText(FormatUtil.getInstance().formateaImporte(BigDecimal.ZERO));

				lbCabecera.setText(I18N.getTexto("Nuevo apartado"));
			}

			if(getDatos().containsKey(PARAMETRO_EDITAR_APARTADO)){
				modoEdicion = true;
				tfArticulo.setDisable(false);
				tfFechaRecogida.setDisable(false);
				btBuscarArticulos.setDisable(false);
				if(getDatos().containsKey(PARAMETRO_NUEVO_APARTADO)){
					actionHandlerBuscar = (EventHandler) getDatos().get(PARAMETRO_EVENTO_BUSQUEDA);
					apartadoNuevo = true;
				}
				else{
					apartadoNuevo = false;
				}
				try {
					ticketManager.nuevoTicket();
					ticketManager.setEsApartado(true);
				} catch (PromocionesServiceException | DocumentoException e) {
					log.error("Error creando el nuevo ticket.", e);
				}
			}
			else{
				modoEdicion = false;
				tfArticulo.setDisable(true);
				tfFechaRecogida.setDisable(true);
				btBuscarArticulos.setDisable(true);
				apartadoNuevo = false;
			}
			
			refrescarDatosPantalla();
			
			escribirEntradaEnVisor();
		}
	}

	protected void escribirEntradaEnVisor() {
		if(apartadoManager.getTicketApartado() != null) {
			if(apartadoManager.getTicketApartado().getArticulos() != null && apartadoManager.getTicketApartado().getArticulos().isEmpty()) {
				visor.escribirLineaArriba(I18N.getTexto("---NUEVO APARTADO---"));
			}
			else {
				int ultimArticulo = apartadoManager.getTicketApartado().getArticulos().size();
				ApartadosDetalleBean detalle = apartadoManager.getTicketApartado().getArticulos().get(ultimArticulo - 1);
				String desc = detalle.getDesart();
				visor.escribir(desc, FormatUtil.getInstance().formateaImporte(detalle.getCantidad()) + " X " + FormatUtil.getInstance().formateaImporte(detalle.getImporteTotal()));
			}
		}
	}

	@Override
	public void initializeFocus() {

		if(modoEdicion){
			tfArticulo.requestFocus();
		}
		else{
			tbArticulos.requestFocus();
		}
	}
	
	public void refrescarDatosPantalla(){
		
		ClienteBean clienteApartado = apartadoManager.getCliente();
		tfCodCliente.setText(clienteApartado.getCodCliente());
		tfCP.setText(clienteApartado.getCp());
		tfDesCliente.setText(clienteApartado.getDesCliente());
		tfDomicilio.setText(clienteApartado.getDomicilio());
		tfNumDoc.setText(clienteApartado.getCif());
		tfPoblacion.setText(clienteApartado.getPoblacion());
		tfProvincia.setText(clienteApartado.getProvincia());
		tfTelefono.setText(clienteApartado.getTelefono1());
		
		int rowSelected = tbArticulos.getSelectionModel().getSelectedIndex();
		
		if(rowSelected<0){
			rowSelected = 0;
		}
		articulos.clear();
		
		for(ApartadosDetalleBean lineaTicket: apartadoManager.getTicketApartado().getArticulos()){
			articulos.add(new LineaArticuloApartadoGui(lineaTicket));
		}
		
		tbArticulos.getSelectionModel().select(rowSelected);
		
		lbSaldo.setText(FormatUtil.getInstance().formateaNumero(apartadoManager.getTicketApartado().getCabecera().getSaldoCliente(), 2));
		
		lbServido.setText(FormatUtil.getInstance().formateaNumero(apartadoManager.getTicketApartado().getTotalServido(), 2));
		lbAbonado.setText(FormatUtil.getInstance().formateaNumero(apartadoManager.getTicketApartado().getTotalAbonado(), 2));
		lbPendiente.setText(FormatUtil.getInstance().formateaNumero(apartadoManager.getTicketApartado().getTotalPendiente(), 2));
		
		applyTotalLabelStyle(lbSaldo);
		applyTotalLabelStyle(lbServido);
		applyTotalLabelStyle(lbAbonado);
		applyTotalLabelStyle(lbPendiente);
		
		if(modoEdicion){
			tfArticulo.setDisable(false);
			tfFechaRecogida.setDisable(false);
			btBuscarArticulos.setDisable(false);
		}
		else{
			tfArticulo.setDisable(true);
			tfFechaRecogida.setDisable(true);
			btBuscarArticulos.setDisable(true);
		}
		
		try{          
            log.debug("refrescarDatosPantalla() - Carga de acciones de botonera de tabla de ventas");
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new BotoneraComponent(1, 8, this, listaAccionesAccionesTabla, panelBotoneraTabla.getPrefWidth(), panelBotoneraTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotoneraTabla.getChildren().clear();
            panelBotoneraTabla.getChildren().add(botoneraAccionesTabla);
		}
		catch (CargarPantallaException e) {
			log.error("refrescarDatosPantalla() - Error al crear botonera: " + e.getMessage(), e);
		} 		
		
		initializeFocus();
	}
	
	protected void applyTotalLabelStyle(Label label) {
		try {
			String text = label.getText();
			
			label.getStyleClass().clear();
			label.getStyleClass().add("label");
			label.getStyleClass().add("total-sub");
			
			if(text.length()>=14) {
				label.getStyleClass().add("total-reduced-10");
			} 
		} catch (Exception e) {
			log.debug("applyTotalLabelStyle() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}

	public void nuevoArticulo(){
		log.debug("nuevoArticulo() - Creando línea de artículo");

		//Validamos los  datos
		if (!tfArticulo.getText().trim().isEmpty()) {
			frArticulo.setCantidad("1");
			frArticulo.setCodArticulo(tfArticulo.getText().trim());

			if (accionValidarFormulario()) {
				log.debug("nuevoCodigoArticulo()- Formulario validado");

				NuevoCodigoArticuloTask taskArticulo = new NuevoCodigoArticuloTask(frArticulo.getCodArticulo().toUpperCase(),
						new BigDecimal(1), false);
				try{
					if(ticketManager.comprobarTarjetaRegalo(frArticulo.getCodArticulo())){
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No están permitidas recargas de tarjeta regalo en apartados."), this.getStage());
					}
					else{
						taskArticulo.start();
					}
				}
				catch(TarjetaRegaloException e){
					e.printStackTrace();
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No están permitidas recargas de tarjeta regalo en apartados."), this.getStage());
					//VentanaDialogoComponent.crearVentanaInfo(e.getMessageI18N(), getApplication().getStage());
				} 
			}
			tfArticulo.setText("");
		}
	}

	protected class NuevoCodigoArticuloTask extends BackgroundTask<LineaTicket> {

		private final String codArticulo;
		private final BigDecimal cantidad;

		public NuevoCodigoArticuloTask(String codArticulo, BigDecimal cantidad, Boolean esTarjetaRegalo) {
			this.codArticulo = codArticulo;
			this.cantidad = BigDecimalUtil.redondear(cantidad, 3);
		}

		@Override
		protected LineaTicket call() throws Exception {
			return insertarLineaVenta(codArticulo, null, null, frArticulo.getCantidadAsBigDecimal());
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			LineaTicket lineaNueva = getValue();
			
			try {
				comprobarArticuloGenerico(lineaNueva);

				if(lineaNueva.getGenerico()){
					HashMap<String, Object> parametrosEdicionArticulo = new HashMap<>();
					parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, lineaNueva);
					parametrosEdicionArticulo.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
					getApplication().getMainView().showModalCentered(EdicionArticuloView.class, parametrosEdicionArticulo, getStage());
					
					if(!parametrosEdicionArticulo.containsKey(EdicionArticuloController.CLAVE_CANCELADO)){
						getApplication().getMainView().showModalCentered(EdicionArticuloView.class, parametrosEdicionArticulo, getStage());
					}
				}
				else{
					comprobarLineaPrecioCero(lineaNueva);
					ticketManager.recalcularConPromociones();
					apartadoManager.introducirDetalleApartado(lineaNueva);
				}
				
	
				nuevosArticulos++;
				refrescarDatosPantalla();
			
			} catch (LineaInsertadaNoPermitidaException e) {
				ticketManager.getTicket().getLineas().remove(getValue());
				if(e.getMessage() != null){
					VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
				}
			}
		}

		@Override
		protected void failed() {
			super.failed();
			log.error("failed() - NuevoCodigoArticuloTask failed: " + getCMZException(), getCMZException());
			VentanaDialogoComponent.crearVentanaAviso(getCMZException().getMessageI18N(), getApplication().getStage());
		}
	}

	public LineaTicket insertarLineaVenta(String sCodart, String sDesglose1, String sDesglose2, BigDecimal nCantidad) throws LineaTicketException, PromocionesServiceException, DocumentoException{
		if(ticketManager.getTicket().getLineas().isEmpty()){
			//Es la primera linea así que llamamos a nuevoTicket()
			ticketManager.nuevoTicket();
		}
		LineaTicket linea = ticketManager.nuevaLineaArticulo(sCodart, sDesglose1, sDesglose2, frArticulo.getCantidadAsBigDecimal(), null);
		visor.escribir(linea.getDesArticulo(),FormatUtil.getInstance().formateaNumero(linea.getCantidad()) + " X " + FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalConDto()));
		return linea;
	}
	
	public void accionIntroBuscarArticulos(KeyEvent e){
		log.trace("accionIntroBuscarArticulos()");
		if(e.getCode() == KeyCode.ENTER){
			nuevoArticulo();
		}
	}

	public void abrirBusquedaArticulos(){
		log.trace("abrirBusquedaArticulos()");
		
		if(!modoEdicion){
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Para insertar artículos debe estar en modo edición."), this.getStage());
			return;
		}

		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CLIENTE, apartadoManager.getCliente());
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CODTARIFA, apartadoManager.getTarifaDefault());
		getDatos().put(BuscarArticulosController.PARAM_MODAL, Boolean.TRUE);
		getDatos().put(SeleccionUsuarioController.PARAMETRO_ES_STOCK, Boolean.FALSE);
		getApplication().getMainView().showModal(BuscarArticulosView.class, getDatos());
		initializeFocus();
		
		if (datos.containsKey(BuscarArticulosController.PARAMETRO_SALIDA_CODART)) {
            log.debug("abrirBusquedaArticulos() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
            String codArt = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_CODART);
            String desglose1 = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE1);
            String desglose2 = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE2);

            frArticulo.setCantidad("1");
            try {
            	if (accionValidarFormulario()) {
            		if(ticketManager.comprobarTarjetaRegalo(codArt)){
            			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No están permitidas recargas de tarjeta regalo en apartados."), this.getStage());
            		}
            		else{
            			LineaTicket lineaNueva = insertarLineaVenta(codArt, desglose1, desglose2, frArticulo.getCantidadAsBigDecimal());
            			
            			comprobarArticuloGenerico(lineaNueva);
            			
            			if(lineaNueva.getGenerico()){
                    		HashMap<String, Object> parametrosEdicionArticulo = new HashMap<>();
							parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, lineaNueva);
							parametrosEdicionArticulo.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
							getApplication().getMainView().showModalCentered(EdicionArticuloView.class, parametrosEdicionArticulo, this.getStage());
							
							if(parametrosEdicionArticulo.containsKey(EdicionArticuloController.CLAVE_CANCELADO)){
								throw new LineaInsertadaNoPermitidaException(lineaNueva);
							}
            			}
            			
            			comprobarLineaPrecioCero(lineaNueva);
            			
            			ticketManager.recalcularConPromociones();
            			apartadoManager.introducirDetalleApartado(lineaNueva);
    					
            			nuevosArticulos++;
            			refrescarDatosPantalla();
            		}            		
            	}
            }
            catch (LineaTicketException ex) {
                log.error("abrirBusquedaArticulos() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
                VentanaDialogoComponent.crearVentanaInfo(ex.getLocalizedMessage(), this.getScene().getWindow());
            } catch (LineaInsertadaNoPermitidaException e) {
				ticketManager.getTicket().getLineas().remove(e.getLinea());
				if(e.getMessage() != null){
					VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
				}
            } catch (TarjetaRegaloException e) {
            	log.error(e);
            	VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No están permitidas recargas de tarjeta regalo en apartados."), this.getStage());
            	//VentanaDialogoComponent.crearVentanaInfo(e.getMessageI18N(), getApplication().getStage());
            } catch (Exception e) {
            	log.error("abrirBusquedaArticulos() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
            	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido insertar la línea"), e);
			}
        }
		initializeFocus();
	}

	protected boolean accionValidarFormulario() {
		// Limpiamos los errores que pudiese tener el formulario
		frArticulo.clearErrorStyle();

		// Validamos el formulario 
		Set<ConstraintViolation<FormularioLineaArticuloBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frArticulo);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioLineaArticuloBean> next = constraintViolations.iterator().next();
			frArticulo.setErrorStyle(next.getPropertyPath(), true);
			frArticulo.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getScene().getWindow());
			return false;
		}

		return true;
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
    	switch (botonAccionado.getClave()) {
    	case "ACCION_TABLA_PRIMER_REGISTRO":
    		accionTablaPrimerRegistro(tbArticulos);
    		initializeFocus();
    		break;
    	case "ACCION_TABLA_ANTERIOR_REGISTRO":
    		accionTablaIrAnteriorRegistro(tbArticulos);
    		initializeFocus();
    		break;
    	case "ACCION_TABLA_SIGUIENTE_REGISTRO":
    		accionTablaIrSiguienteRegistro(tbArticulos);
    		initializeFocus();
    		break;
    	case "ACCION_TABLA_ULTIMO_REGISTRO":
    		accionTablaUltimoRegistro(tbArticulos);
    		initializeFocus();
    		break;
    	case "ACCION_TABLA_BORRAR_REGISTRO":
    		accionEliminarArticulo();
    		initializeFocus();
    		break;
    	case "ACCION_TABLA_CONFIRMAR_CAMBIOS":
    		confirmarCambios();
    		initializeFocus();
    		break;
    	case "ACCION_TABLA_RECHAZAR_CAMBIOS":
    		rechazarCambios();
    		break;
    	case "ACCION_TABLA_VOLVER_A_APARTADOS":
    		close();
    		break;
    	default:
    		log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
    		break;
    	}
	}
	
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
                
        if(!modoEdicion){
        	listaAcciones.add(new ConfiguracionBotonBean("iconos/back.png", null, null, "ACCION_TABLA_VOLVER_A_APARTADOS", "REALIZAR_ACCION")); //"Home"
        }
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        
        if(modoEdicion){
        	listaAcciones.add(new ConfiguracionBotonBean("iconos/aceptar.png", null, null, "ACCION_TABLA_CONFIRMAR_CAMBIOS", "REALIZAR_ACCION"));
        	listaAcciones.add(new ConfiguracionBotonBean("iconos/cancelar.png", null, null, "ACCION_TABLA_RECHAZAR_CAMBIOS", "REALIZAR_ACCION"));  //"Num Pad -"
        }
        else{
        	listaAcciones.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO", "REALIZAR_ACCION")); //"Delete"
        }
        
        return listaAcciones;
    }
	
	public void close(){
		visor.limpiar();
		getApplication().getMainView().close();
	}
	
	public void mostrarPagos(){
		log.trace("mostrarPagos()");
		
		List<ApartadosPagoBean> pagos = apartadoManager.getTicketApartado().getMovimientos();
		
		if(pagos.isEmpty())
		{
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No existen pagos asociados al apartado."), this.getStage());
		}
		else
		{
			getDatos().put(VerPagosApartadoController.PARAMETRO_PAGOS, pagos);
			getDatos().put(VerPagosApartadoController.PARAMETRO_APARTADO_MGR, apartadoManager);
			getApplication().getMainView().showModalCentered(VerPagosApartadoView.class, getDatos(), this.getStage());
		}
		
		initializeFocus();
	}

	public void confirmarCambios(){
		log.trace("confirmarCambios()"); 
		
		Fecha f = Fecha.getFecha(tfFechaRecogida.getSelectedDate());
		Fecha fActual = new Fecha(new Fecha(new Date()).getString());
		if(f.despuesOrEquals(fActual)){

			frDatosApartado.setFechaRecogida(tfFechaRecogida.getTexto());

			if(validarDatosApartado()){
				if(apartadoManager.getTicketApartado().getArticulos().isEmpty() && nuevosArticulos == 0){
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede crear un apartado vacío"), this.getStage());
				}
				else{
					if(nuevosArticulos>0){
						if(apartadoNuevo){
							apartadoManager.nuevoApartado();
							apartadoNuevo = false;
						}
						for(ApartadosDetalleBean linea: apartadoManager.getTicketApartado().getArticulos()){
							if(linea.isArticuloNuevo()){
								linea.setUidApartado(apartadoManager.getTicketApartado().getCabecera().getUidApartado());
								apartadoManager.nuevoArticuloApartado(linea);
								linea.setArticuloNuevo(false);
								nuevosArticulos--;
							}
						}					
						apartadoManager.getTicketApartado().calcularTotales();
					}
					apartadoManager.getTicketApartado().getCabecera().setFechaApartado(tfFechaRecogida.getSelectedDate());
					
					apartadoManager.getTicketApartado().calcularTotales();
					apartadoManager.getTicketApartado().getCabecera().setImporteTotalApartado(apartadoManager.getTicketApartado().getImporteTotal());
					
					apartadoManager.actualizarCabecera();
					
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Cambios guardados correctamente."), getStage());

					modoEdicion = false;
					int rowSelected = tbArticulos.getSelectionModel().getSelectedIndex();
					if(rowSelected<0){
						tbArticulos.getSelectionModel().select(0);
					}
					ticketManager.finalizarTicket();

					if(actionHandlerBuscar!=null){
						actionHandlerBuscar.handle(null);
						close();
					}
					else{
						refrescarDatosPantalla();
					}
				}
			}
		}
		else{
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La fecha de recogida no debe ser anterior al día de hoy."), getStage());
		}
	}

	public void rechazarCambios(){
		log.trace("rechazarCambios()");

		modoEdicion = false;
		nuevosArticulos = 0;
		apartadoManager.rechazarNuevosArticulos();
		ticketManager.finalizarTicket();
		
		if(apartadoNuevo){
			close();
		}
		else{
			int rowSelected = tbArticulos.getSelectionModel().getSelectedIndex();
			if(rowSelected<0){
				tbArticulos.getSelectionModel().select(0);
			}
			refrescarDatosPantalla();
		}	
	}

	public void cambiarDatosCliente(){
		log.trace("cambiarDatosCliente()");

		if(apartadoManager.getTicketApartado().getCabecera().getEstadoApartado()!= null && apartadoManager.getTicketApartado().getCabecera().getEstadoApartado()!= ApartadosCabeceraBean.ESTADO_DISPONIBLE){
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pueden editar datos de un apartado finalizado o cancelado."), getStage());
		}
		else{
			getDatos().put(CambiarDatosClienteApartadoController.PARAMETRO_APARTADOS_MANAGER, apartadoManager);
			getApplication().getMainView().showModalCentered(CambiarDatosClienteApartadoView.class, getDatos(), this.getStage());
			refrescarDatosPantalla();
		}
	}	
	
	protected boolean validarDatosApartado(){
		frDatosApartado.clearErrorStyle();

		// Validamos el formulario 
		Set<ConstraintViolation<FormularioDetalleApartadoBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frDatosApartado);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioDetalleApartadoBean> next = constraintViolations.iterator().next();
			frDatosApartado.setErrorStyle(next.getPropertyPath(), true);
			frDatosApartado.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getScene().getWindow());
			return false;
		}

		return true;
	}
	
	public void accionEliminarArticulo(){
		log.trace("accionEliminarArticulo()");

		LineaArticuloApartadoGui linea = (LineaArticuloApartadoGui)tbArticulos.getSelectionModel().getSelectedItem();
		
		if(linea!=null){
			if(linea.isVendido()){
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede borrar una línea que ya está servida."), this.getStage());
			}
			else
			{
				ApartadosDetalleBean detalle = linea.getDetalle();	
				
				if(detalle.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_CANCELADO){
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El artículo ya está cancelado."), this.getStage());
				}	
				else
				{
					if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se va a cancelar el artículo seleccionado. ¿Está seguro?"), getStage())){																
						apartadoManager.eliminarArticuloApartado(detalle);
						refrescarDatosPantalla();
					}
				}
			}
		}
	}
	
	public void abrirPagos() {
		log.trace("abrirPagos()");
		if(modoEdicion){
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe confirmar o rechazar antes los cambios."), getStage());
			initializeFocus();
		}
		else{
			if(apartadoManager.getTicketApartado().getCabecera().getEstadoApartado() != ApartadosCabeceraBean.ESTADO_DISPONIBLE){
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pueden añadir pagos al apartado."), getStage());
			}
			else{
				if (apartadoManager.getTicketApartado().getTotalPendiente().compareTo(BigDecimal.ZERO)>0) {        
					log.debug("abrirPagos() - El ticket tiene líneas");
					try {
						ticketManager.nuevoTicketPagosApartado();
						ticketManager.getTicket().getTotales().setTotalAPagar(apartadoManager.getTicketApartado().getTotalPendiente());		

						getDatos().put(PARAMETRO_IMPORTE_MAXIMO_PAGO, ticketManager.getTicket().getTotales().getTotalAPagar());	
						getApplication().getMainView().showModalCentered(PagoApartadoView.class, getDatos(), getStage());
						if(getDatos().containsKey(PagoApartadoController.PARAMETRO_IMPORTE_PAGO)){
							BigDecimal importePago = (BigDecimal)getDatos().remove(PagoApartadoController.PARAMETRO_IMPORTE_PAGO);
							ticketManager.getTicket().getTotales().setTotalAPagar(importePago);
							getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
							getDatos().put(NuevoPagoApartadoController.PARAMETRO_APARTADO_MANAGER, apartadoManager);
							getApplication().getMainView().showModal(NuevoPagoApartadoView.class, getDatos());
							escribirEntradaEnVisor();
							apartadoManager.actualizarPagos();
							refrescarDatosPantalla();
							getView().resetSubViews();
						}
					} catch (PromocionesServiceException | DocumentoException e) {
						log.error("Error creando ticket.", e);
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Se produjo un error en la carga del documento de pagos."), getStage());						
					}
				}
				else {
					log.warn("abrirPagos() - Ticket vacio");
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe pendiente de abonar es 0."), this.getApplication().getStage());
				}
			}
		}
		initializeFocus();
    }
	
	public void vender(){
		log.trace("vender()");

		if(modoEdicion){
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe confirmar antes los cambios."), getStage());
			initializeFocus();
		}
		else {
			List<ApartadosDetalleBean> articulos = apartadoManager.getTicketApartado().getArticulos();
			List<ApartadosDetalleBean> articulosDisponiblesVenta = new ArrayList<ApartadosDetalleBean>();

			for(ApartadosDetalleBean detalleApartado: articulos){
				if(detalleApartado.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_DISPONIBLE && !detalleApartado.isArticuloNuevo()){
					articulosDisponiblesVenta.add(detalleApartado);
				}
			}

			if(articulosDisponiblesVenta.isEmpty()){
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay artículos disponibles para su venta."), this.getApplication().getStage());
			}
			else{
				getDatos().put(MarcarVentaApartadoController.PARAMETRO_APARTADO_MGR, apartadoManager);
				getDatos().put(MarcarVentaApartadoController.PARAMETRO_ARTICULOS, articulosDisponiblesVenta);
				getApplication().getMainView().showModalCentered(MarcarVentaApartadoView.class, getDatos(), this.getStage());
			}
			
			apartadoManager.getTicketApartado().calcularTotales();
			refrescarDatosPantalla();
		}
	}
	
	public void buscarCliente(){
		log.trace("buscarCliente()");
		
		if(apartadoNuevo && nuevosArticulos == 0){
			try {
				log.trace("accionBuscarCliente()");

				HashMap<String, Object> parametrosBusquedaCliente = new HashMap<>();
				parametrosBusquedaCliente.put(ConsultaClienteController.MODO_MODAL, true);
				getApplication().getMainView().showModal(ConsultaClienteView.class, parametrosBusquedaCliente);
				if(parametrosBusquedaCliente.containsKey(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE)){
					ClienteBean cliente = (ClienteBean)parametrosBusquedaCliente.get(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE);
					if(checkClientTaxes(cliente)) {
						ticketManager.getTicket().setCliente(cliente);
						apartadoManager.setCliente(cliente);
					}
					refrescarDatosPantalla();
				}
			}
			catch (Exception ex) {
				log.error(ex.getLocalizedMessage(), ex);
				VentanaDialogoComponent.crearVentanaError(getStage(), ex);
			}
		}
		else{
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede cambiar el cliente del apartado."), getStage());
		}
		initializeFocus();
	}
	
	protected boolean checkClientTaxes(ClienteBean cliente) {
		TratamientoImpuestoBean trat = null;
		TratamientoImpuestoBean shopTrat = null;
		try {
			trat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), cliente.getIdTratImpuestos());
			shopTrat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos());
		}catch(Exception ignore) {}
		if(trat == null || !trat.getCodpais().equals(sesion.getAplicacion().getTienda().getCliente().getCodpais())) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No es posible seleccionar este cliente al tener un tratamiento de impuestos no disponible para el país asociado a la tienda actual."), this.getStage());
			return false;
		}else if(sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos().equals(trat.getIdTratImpuestos()) || VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}. Confirme si desea continuar.",trat.getDestratimp(),shopTrat.getDestratimp()), this.getStage())) {		
			return true;
		}
		return false;
	}
	
	@Override
    public boolean canClose() {
		log.trace("canClose()");

    	if (modoEdicion) {    
    		VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe confirmar o rechazar los cambios para poder cerrar la pantalla de detalle de apartado."), getStage());
    		return false;
    		
    		/*if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Existen datos de PENDIENTES de aceptar. Si cierra la pantalla de apartados se perderán. ¿Desea continuar?"), getStage())){	
    			rechazarCambios();
    			return true;
    		}
    		else{
    			return false;
    		}*/
    	}
    	return true;
    }
	
	public void imprimirResumen(){
		log.trace("imprimirResumen()");
		
		if(modoEdicion){
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe confirmar o rechazar los cambios antes de imprimir el extracto."), getStage());
		}
		else{
			try {
				apartadoManager.imprimirApartado();
			} catch (DeviceException e) {
				log.error("Error imprimiendo el resumen del apartado.", e);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Se produjo un error en la impresión"), getStage());
			}
		}
	}
	
	protected void comprobarLineaPrecioCero(LineaTicket linea) throws LineaInsertadaNoPermitidaException {
		Boolean permiteVentaPrecioCero = variablesServices.getVariableAsBoolean(VariablesServices.TPV_PERMITIR_VENTA_PRECIO_CERO, true);
		//Comprobamos tarifa es distinta de null
		if(linea.getTarifa().getVersion() == null){ //Si la versión es null, es cero y no viene de BD
			log.debug("comprobarLineaPrecioCero() - La versión de la tarifa de la linea es null");
			if(permiteVentaPrecioCero){
				boolean vender = VentanaDialogoComponent.crearVentanaConfirmacion(
						I18N.getTexto("El artículo \"{0} - {1}\" no está tarificado.", linea.getCodArticulo(), linea.getDesArticulo()) + "\n" + I18N.getTexto("¿Desea vender el artículo a precio 0?")
						, getStage());
				if(!vender){
					throw new LineaInsertadaNoPermitidaException(linea);
				}else{
					return;
				}
			}else{
				throw new LineaInsertadaNoPermitidaException(I18N.getTexto("El artículo \"{0} - {1}\" no está tarificado.", linea.getCodArticulo(), linea.getDesArticulo()) + "\n" + I18N.getTexto("No está permitida la venta a precio 0."), linea);
			}
		}
		
		//Comprobamos precio cero
		if(BigDecimalUtil.isIgualACero(linea.getPrecioTotalConDto())){
			if(permiteVentaPrecioCero){
				boolean vender = VentanaDialogoComponent.crearVentanaConfirmacion(
						I18N.getTexto("¿Desea vender el artículo a precio 0?")
						, getStage());
				if(!vender){
					throw new LineaInsertadaNoPermitidaException(linea);
				}else{
					return;
				}
			}else{
				throw new LineaInsertadaNoPermitidaException(I18N.getTexto("No está permitida la venta a precio 0."), linea);
			}
		}
	}
	
	protected void comprobarArticuloGenerico(LineaTicket linea) throws LineaInsertadaNoPermitidaException {
		//Si el artículo es genérico y no tiene permiso, no se puede insertar
		if(linea.getGenerico()){
			try {
				compruebaPermisos(FacturacionArticulosController.PERMISO_USAR_GENERICOS);
			} catch (SinPermisosException e) {
				boolean permiso = false;
				try {
					PermisosEfectivosAccionBean permisosEfectivos = sesion.getSesionUsuario().getPermisosEfectivos(getApplication().getMainView().getCurrentAccion());
					Map<String, PermisoBean> permisos = permisosEfectivos.getPermisos();
					if(!permisos.containsKey(FacturacionArticulosController.PERMISO_USAR_GENERICOS)){
						log.warn("comprobarArticuloGenerico() - No existe la operación " + FacturacionArticulosController.PERMISO_USAR_GENERICOS + " en base de datos, se devuelve que SÍ tiene permiso");
						permiso = true;
					}
				} catch (PermisoException e1) {
					log.error("comprobarArticuloGenerico() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
					log.warn("comprobarArticuloGenerico() - Error al consultar permiso, se devuelve que SÍ tiene permiso");
					permiso = true;
				}
				
				if(!permiso){
					throw new LineaInsertadaNoPermitidaException(I18N.getTexto("No tiene permisos para usar articulos genéricos"), linea);
				}
				
			}
		}
	}
	
}
