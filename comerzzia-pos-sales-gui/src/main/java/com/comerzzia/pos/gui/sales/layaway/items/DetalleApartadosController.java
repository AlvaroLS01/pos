package com.comerzzia.pos.gui.sales.layaway.items;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.facade.model.TaxTreatment;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.facade.service.permissions.PermissionDTO;
import com.comerzzia.core.facade.service.tax.TaxTreatmentServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.ApartadosCabeceraBean;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketLineException;
import com.comerzzia.omnichannel.facade.service.basket.layaway.LayawayBasketManager;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.login.userselection.UserSelectionController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.balancecard.BalanceCardService;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract.NewBasketItemNoAllowedException;
import com.comerzzia.pos.gui.sales.layaway.ApartadosManager;
import com.comerzzia.pos.gui.sales.layaway.items.datosCliente.CambiarDatosClienteApartadoController;
import com.comerzzia.pos.gui.sales.layaway.items.marcarVenta.MarcarVentaApartadoController;
import com.comerzzia.pos.gui.sales.layaway.items.verPagos.VerPagosApartadoController;
import com.comerzzia.pos.gui.sales.retail.items.ItemLineValidationForm;
import com.comerzzia.pos.gui.sales.retail.items.RetailBasketItemizationController;
import com.comerzzia.pos.gui.sales.retail.items.edit.RetailBasketItemModificationController;
import com.comerzzia.pos.gui.sales.retail.items.search.ItemSearchController;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.date.CzzDate;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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

@Component
@SuppressWarnings("rawtypes")
@CzzScene
public class DetalleApartadosController extends SceneController implements ButtonsGroupController{

	protected Logger log = Logger.getLogger(getClass());

	public static final String PARAMETRO_APARTADO = "APARTADO";
	public static final String PARAMETRO_EVENTO_BUSQUEDA = "ACCION_BUSQUEDA";
	public static final String PARAMETRO_EDITAR_APARTADO = "EDITAR_APARTADO";
	public static final String PARAMETRO_NUEVO_APARTADO = "NUEVO_APARTADO";
	public static final String PARAMETRO_APARTADO_MGR = "APARTADO_MGR";
	public static final String PARAMETRO_IMPORTE_MAXIMO_PAGO = "IMPORTE_MAXIMO_PAGO";
	public static final String INICIALIZAR_PANTALLA = "N";
	
    final DeviceLineDisplay visor = Devices.getInstance().getLineDisplay();

	@FXML
	protected TextField tfArticulo, tfTelefono, tfCodCliente, tfDesCliente, tfProvincia, tfCP,
	tfDomicilio, tfPoblacion, tfNumDoc;
	@FXML
	protected NumericTextField tfCantidadIntro;
	
	@FXML
	protected DatePicker tfFechaRecogida;

	@FXML
	protected Label lbCabecera;
	
	@FXML
	protected TextField tfSaldo, tfPendiente, tfServido, tfAbonado;

	@FXML
	protected NumericKeypad tecladoNumerico;

	protected ApartadosManager apartadoManager;
	
	@Autowired
	private Session sesion;
	
	protected LayawayBasketManager ticketManager;

	@FXML
	protected TableView tbArticulos;
	
	@FXML
	protected TableColumn tcArticulo, tcDescripcion, tcDesglose1, tcDesglose2, tcDescuento, tcCantidad, tcPrecio, tcImporte;

	protected ObservableList<LineaArticuloApartadoRow> articulos;

	protected ItemLineValidationForm frArticulo;
	
	// botonera inferior de pantalla
	protected ButtonsGroupComponent botonera, botoneraAccionesTabla;
    
    @FXML
    public AnchorPane panelBotonera, panelBotoneraTabla;
    
    protected int nuevosArticulos;
    
    protected FormularioDetalleApartadoBean frDatosApartado;
    
    protected boolean apartadoNuevo = false;
    
    protected boolean modoEdicion = false;
    
    protected EventHandler actionHandlerBuscar;
    
    @Autowired
    private VariableServiceFacade variablesServices;
    
    @Autowired
	private TaxTreatmentServiceFacade tratamientoImpuestoService;
    
    @Autowired
    protected BalanceCardService giftCardService;
    
    @Autowired
    protected ModelMapper modelMapper;
    
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		frDatosApartado = SpringContext.getBean(FormularioDetalleApartadoBean.class);
		frArticulo = SpringContext.getBean(ItemLineValidationForm.class);

		frDatosApartado.setFormField("fechaRecogida", tfFechaRecogida);
		frArticulo.setFormField("codArticulo", tfArticulo);
		frArticulo.setFormField("cantidad", tfCantidadIntro);

		articulos = FXCollections.observableArrayList();
		tbArticulos.setPlaceholder(new Label(""));

		tcArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcArticulo", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcDescripcion", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcDesglose1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcDesglose1", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcDesglose2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcDesglose2", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcPrecio.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcPrecio", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcImporte", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcDescuento.setCellFactory(CellFactoryBuilder.createCellRendererCeldaPorcentaje("tcDescuento", "tcDescuento", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		//        tcVendedor.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcVendedor", 2, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcCantidad", 3, CellFactoryBuilder.RIGHT_ALIGN_STYLE));

		Boolean usaDescuentoEnLinea = variablesServices.getVariableAsBoolean(VariableServiceFacade.TICKETS_USA_DESCUENTO_EN_LINEA);
		if(!usaDescuentoEnLinea){
			tcDescuento.setVisible(false);
		}
		if (sesion.getApplicationSession().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
			tcDesglose1.setText(I18N.getText(variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcDesglose1.setVisible(false);
		}
		if (sesion.getApplicationSession().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
			tcDesglose2.setText(I18N.getText(variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO)));
		}
		else { // si no hay desgloses, compactamos la línea
			tcDesglose2.setVisible(false);
		}
		
		
		// Asignamos las lineas a la tabla
		tbArticulos.setItems(articulos);

		// Definimos un factory para cada celda para aumentar el rendimiento
		tcArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaArticuloApartadoRow, String> cdf) {
				return cdf.getValue().getArticuloProperty();
			}
		});
		tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaArticuloApartadoRow, String> cdf) {
				return cdf.getValue().getDescripcionProperty();
			}
		});
		tcCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaArticuloApartadoRow, BigDecimal> cdf) {
				return cdf.getValue().getCantidadProperty();
			}
		});
		tcDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaArticuloApartadoRow, String> cdf) {
				return cdf.getValue().getDesglose1Property();
			}
		});
		tcDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaArticuloApartadoRow, String> cdf) {
				return cdf.getValue().getDesglose2Property();
			}
		});
		tcPrecio.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaArticuloApartadoRow, BigDecimal> cdf) {
				return cdf.getValue().getPvpProperty();
			}
		});
		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaArticuloApartadoRow, BigDecimal> cdf) {
				return cdf.getValue().getImporteTotalProperty();
			}
		});
		tcDescuento.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaArticuloApartadoRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaArticuloApartadoRow, BigDecimal> cdf) {
				return cdf.getValue().getDescuentoProperty();
			}
		});
		//        tcVendedor.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoGui, String>, ObservableValue<String>>() {
			//            @Override
			//            public ObservableValue<String> call(CellDataFeatures<LineaApartadoGui, String> cdf) {
				//                return cdf.getValue().getVendedorProperty();
		//            }
		//        });

		tbArticulos.setRowFactory(new Callback<TableView<LineaArticuloApartadoRow>, TableRow<LineaArticuloApartadoRow>>() {

			@Override
			public TableRow<LineaArticuloApartadoRow> call(TableView<LineaArticuloApartadoRow> p) {
				final TableRow<LineaArticuloApartadoRow> row = new TableRow<LineaArticuloApartadoRow>() {
					@Override
					protected void updateItem(LineaArticuloApartadoRow linea, boolean empty){
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
		initNumericKeypad(tecladoNumerico);
		
		try{
			ButtonsGroupModel panelBotoneraBean = getSceneView().loadButtonsGroup();
			botonera = new ButtonsGroupComponent(panelBotoneraBean, null, null, this, ActionButtonNormalComponent.class);
			panelBotonera.getChildren().add(botonera);
		}
		catch (InitializeGuiException | LoadWindowException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		} 
		
		tfCantidadIntro.setText(FormatUtils.getInstance().formatNumber(BigDecimal.ONE, 3));
		tfCantidadIntro.focusedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
				if (oldValue) {
					formateaCantidad();
				}
			}
		});
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {	
		//si no tenemos que inicializar la pantalla nos salimos
		if(!sceneData.containsKey(INICIALIZAR_PANTALLA)){
			return;
		}
		else{
			sceneData.remove(INICIALIZAR_PANTALLA);
		}
		
		if(!sceneData.isEmpty()){
			nuevosArticulos = 0;
			actionHandlerBuscar = null;
			articulos.clear();
			frArticulo.clearErrorStyle();
		
		
			if(sceneData.containsKey(PARAMETRO_APARTADO_MGR)){
				apartadoManager = (ApartadosManager)sceneData.remove(PARAMETRO_APARTADO_MGR);
			}
			
			if(sceneData.containsKey(BasketItemizationControllerAbstract.BASKET_KEY)){
				ticketManager = (LayawayBasketManager)sceneData.remove(BasketItemizationControllerAbstract.BASKET_KEY);
			}		

			if(sceneData.containsKey(PARAMETRO_APARTADO)){
				
				ApartadosCabeceraBean apartado = (ApartadosCabeceraBean)sceneData.remove(PARAMETRO_APARTADO);
				apartadoManager.cargarApartado(apartado);
				tfFechaRecogida.setSelectedDate(apartado.getFechaApartado());			

				lbCabecera.setText(I18N.getText("Detalle apartado {0}", apartado.getIdApartado()));
				refrescarDatosPantalla();
			}
			else{
				apartadoManager.nuevoTicketApartado();
				Customer clienteInicial = apartadoManager.getCliente();
				tfCodCliente.setText(clienteInicial.getCustomerCode());
				tfCP.setText(clienteInicial.getPostalCode());
				tfDesCliente.setText(clienteInicial.getCustomerDes());
				tfDomicilio.setText(clienteInicial.getAddress());
				tfNumDoc.setText(clienteInicial.getVatNumber());
				tfPoblacion.setText(clienteInicial.getCity());
				tfProvincia.setText(clienteInicial.getProvince());
				tfTelefono.setText(clienteInicial.getPhone1());

				tfFechaRecogida.setSelectedDate(new Date());

				tfSaldo.setText(FormatUtils.getInstance().formatAmount(BigDecimal.ZERO));
				tfPendiente.setText(FormatUtils.getInstance().formatAmount(BigDecimal.ZERO));
				tfServido.setText(FormatUtils.getInstance().formatAmount(BigDecimal.ZERO));
				tfAbonado.setText(FormatUtils.getInstance().formatAmount(BigDecimal.ZERO));

				lbCabecera.setText(I18N.getText("Nuevo apartado"));
			}

			if(sceneData.containsKey(PARAMETRO_EDITAR_APARTADO)){
				modoEdicion = true;
				tfArticulo.setDisable(false);
				tfCantidadIntro.setDisable(false);
				tfFechaRecogida.setDisable(false);
				if(sceneData.containsKey(PARAMETRO_NUEVO_APARTADO)){
					actionHandlerBuscar = (EventHandler) sceneData.get(PARAMETRO_EVENTO_BUSQUEDA);
					apartadoNuevo = true;
				}
				else{
					apartadoNuevo = false;
				}

//				ticketManager.createBasketTransaction();
			}
			else{
				modoEdicion = false;
				tfArticulo.setDisable(true);
				tfCantidadIntro.setDisable(true);
				tfFechaRecogida.setDisable(true);
				apartadoNuevo = false;
			}
			
			refrescarDatosPantalla();
			
			escribirEntradaEnVisor();
		}
	}

	protected void escribirEntradaEnVisor() {
		if(apartadoManager.getTicketApartado() != null) {
			if(apartadoManager.getTicketApartado().getArticulos() != null && apartadoManager.getTicketApartado().getArticulos().isEmpty()) {
				visor.writeLineUp(I18N.getText("---NUEVO APARTADO---"));
			}
			else {
				int ultimArticulo = apartadoManager.getTicketApartado().getArticulos().size();
				ApartadosDetalleBean detalle = apartadoManager.getTicketApartado().getArticulos().get(ultimArticulo - 1);
				String desc = detalle.getDesart();
				visor.write(desc, FormatUtils.getInstance().formatAmount(detalle.getCantidad()) + " X " + FormatUtils.getInstance().formatAmount(detalle.getImporteTotal()));
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
		
		Customer clienteApartado = apartadoManager.getCliente();
		tfCodCliente.setText(clienteApartado.getCustomerCode());
		tfCP.setText(clienteApartado.getPostalCode());
		tfDesCliente.setText(clienteApartado.getCustomerDes());
		tfDomicilio.setText(clienteApartado.getAddress());
		tfNumDoc.setText(clienteApartado.getVatNumber());
		tfPoblacion.setText(clienteApartado.getCity());
		tfProvincia.setText(clienteApartado.getProvince());
		tfTelefono.setText(clienteApartado.getPhone1());
		
		int rowSelected = tbArticulos.getSelectionModel().getSelectedIndex();
		
		if(rowSelected<0){
			rowSelected = 0;
		}
		articulos.clear();
		
		for(ApartadosDetalleBean lineaTicket: apartadoManager.getTicketApartado().getArticulos()){
			articulos.add(new LineaArticuloApartadoRow(lineaTicket));
		}
		
		tbArticulos.getSelectionModel().select(rowSelected);
		
		tfSaldo.setText(FormatUtils.getInstance().formatNumber(apartadoManager.getTicketApartado().getCabecera().getSaldoCliente(), 2));
		
		tfServido.setText(FormatUtils.getInstance().formatNumber(apartadoManager.getTicketApartado().getTotalServido(), 2));
		tfAbonado.setText(FormatUtils.getInstance().formatNumber(apartadoManager.getTicketApartado().getTotalAbonado(), 2));
		tfPendiente.setText(FormatUtils.getInstance().formatNumber(apartadoManager.getTicketApartado().getTotalPendiente(), 2));
		
		
		if(modoEdicion){
			tfArticulo.setDisable(false);
			tfCantidadIntro.setDisable(false);
			tfFechaRecogida.setDisable(false);
		}
		else{
			tfArticulo.setDisable(true);
			tfCantidadIntro.setDisable(true);
			tfFechaRecogida.setDisable(true);
		}
		
		try{          
            log.debug("refrescarDatosPantalla() - Carga de acciones de botonera de tabla de ventas");
            List<ButtonConfigurationBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new ButtonsGroupComponent(1, 8, this, listaAccionesAccionesTabla, panelBotoneraTabla.getPrefWidth(), panelBotoneraTabla.getPrefHeight(), ActionButtonSimpleComponent.class.getName());
            panelBotoneraTabla.getChildren().clear();
            panelBotoneraTabla.getChildren().add(botoneraAccionesTabla);
		}
		catch (LoadWindowException e) {
			log.error("refrescarDatosPantalla() - Error al crear botonera: " + e.getMessage(), e);
		} 		
		
		initializeFocus();
	}

	public void nuevoArticulo(){
		//Validamos los  datos		
		frArticulo.setCantidad(tfCantidadIntro.getText().trim());
		frArticulo.setCodArticulo(tfArticulo.getText().trim());
		BigDecimal cantidad = frArticulo.getCantidadAsBigDecimal();
		
		if (tfArticulo.getText().trim().isEmpty() ||
			(!accionValidarFormulario()) ||
			cantidad == null ||
			BigDecimalUtil.isEqualsToZero(cantidad)
			) {
			return;
		}

		if (giftCardService.isBalanceCardItem(frArticulo.getCodArticulo())) {
			DialogWindowComponent.openWarnWindow(I18N.getText("No están permitidas recargas de tarjeta regalo en apartados."), this.getStage());
		}
		
		log.debug("nuevoArticulo() - Creando línea de artículo");

		new NuevoCodigoArticuloTask(frArticulo.getCodArticulo().toUpperCase(),
				cantidad, false).start();
			
		tfArticulo.setText("");
	}

	protected class NuevoCodigoArticuloTask extends BackgroundTask<BasketItem> {

		private final String codArticulo;
		private final BigDecimal cantidad;

		public NuevoCodigoArticuloTask(String codArticulo, BigDecimal cantidad, Boolean esTarjetaRegalo) {
			this.codArticulo = codArticulo;
			this.cantidad = cantidad;
		}

		@Override
		protected BasketItem execute() throws Exception {
			return insertarLineaVenta(codArticulo, null, null, cantidad);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			BasketItem lineaNueva = getValue();
			
			try {
				comprobarArticuloGenerico(lineaNueva);

				if(lineaNueva.getItemData().getGenericItem()){
					sceneData.put(RetailBasketItemModificationController.PARAM_BASKET_ITEM, lineaNueva);
					sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, ticketManager);
					//TODO: MSB Y esto?
//					openScene(EdicionArticuloController.class);
//					
//					if(!sceneData.containsKey(EdicionArticuloController.CLAVE_CANCELADO)){
//						openScene(EdicionArticuloController.class);
//					}
				}
				else{
					comprobarLineaPrecioCero(lineaNueva);

					apartadoManager.introducirDetalleApartado(lineaNueva);
				}
				
				tfCantidadIntro.setText(FormatUtils.getInstance().formatNumber(BigDecimal.ONE, 3));
	
				nuevosArticulos++;
				refrescarDatosPantalla();
			
			} catch (NewBasketItemNoAllowedException e) {
				ticketManager.getBasketTransaction().getLines().remove(getValue());
				if(e.getMessage() != null){
					DialogWindowComponent.openErrorWindow(e.getMessage(), getStage());
				}
			}
		}

		@Override
		protected void failed() {
			super.failed();
			log.error("failed() - NuevoCodigoArticuloTask failed: " + getCMZException(), getCMZException());
			DialogWindowComponent.openWarnWindow(getCMZException().getMessage(), getStage());
		}
	}

	public BasketItem insertarLineaVenta(String sCodart, String sDesglose1, String sDesglose2, BigDecimal nCantidad) throws BasketLineException {
		BasketItem linea = null; //ticketManager.createAndInsertBasketItem(sCodart, sDesglose1, sDesglose2, frArticulo.getCantidadAsBigDecimal());
		visor.write(linea.getItemDes(),FormatUtils.getInstance().formatNumber(linea.getQuantity()) + " X " + FormatUtils.getInstance().formatAmount(linea.getPriceWithTaxes()));
		return linea;
	}
	
	public void accionIntroBuscarArticulos(KeyEvent e){
		log.trace("accionIntroBuscarArticulos()");
		if(e.getCode() == KeyCode.ENTER){
			nuevoArticulo();
		}
	}
	
	/**
	 * Acción de introducción de cantidad desde la interfaz
	 *
	 * @param event
	 */
	public void actionTfCantidadIntro(KeyEvent event) {
		log.debug("actionTfCantidadIntro() - acción de introducción de cantidad de artículo");
		if (event.getCode() == KeyCode.ENTER) {
			tfArticulo.requestFocus();
			tfArticulo.selectAll();
		}
	}
	
	protected void formateaCantidad() {
		nuevaCantidad();
		frArticulo.setCantidad(tfCantidadIntro.getText().trim());
		frArticulo.setCodArticulo(tfArticulo.getText().trim());
		if (accionValidarFormulario()) {
			BigDecimal bigDecimal = FormatUtils.getInstance().parseBigDecimal(tfCantidadIntro.getText().trim());
			bigDecimal = bigDecimal.abs();
			tfCantidadIntro.setText(FormatUtils.getInstance().formatNumber(bigDecimal, 3));
		}
		else {
			tfCantidadIntro.setText(FormatUtils.getInstance().formatNumber(BigDecimal.ONE, 3));
			cambiarCantidad();
		}
	}

	/**
	 * Preparamos la interfaz para un cambio de código tras cambiar una cantidad
	 */
	public void nuevaCantidad() {
		log.debug("nuevaCantidad() - preparamos la interfaz para un cambio de código tras cambiar una cantidad");
		tfCantidadIntro.setText(tfCantidadIntro.getText().replace("*", ""));
		if (tfCantidadIntro.getText().isEmpty()) {
			tfCantidadIntro.setText(FormatUtils.getInstance().formatNumber(BigDecimal.ONE, 3));
		}
	}
	
	/**
	 * Preparamos la interfaz para una modificación de la cantidad
	 */
	public void cambiarCantidad() {
		log.debug("cambiarCantidad() - preparamos la interfaz para una modificación de la cantidad");
		tfArticulo.setText(tfArticulo.getText().replace("*", ""));
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				tfCantidadIntro.requestFocus();
			}
		});
		tfCantidadIntro.selectAll();
	}

	public void abrirBusquedaArticulos(){
		log.trace("abrirBusquedaArticulos()");
		
		if(!modoEdicion){
			DialogWindowComponent.openWarnWindow(I18N.getText("Para insertar artículos debe estar en modo edición."), this.getStage());
			return;
		}

		sceneData.put(ItemSearchController.PARAM_INPUT_CUSTOMER, apartadoManager.getCliente());
		sceneData.put(ItemSearchController.PARAM_INPUT_RATE_CODE, apartadoManager.getTarifaDefault());
		sceneData.put(ItemSearchController.PARAM_MODAL, Boolean.TRUE);
		sceneData.put(UserSelectionController.PARAM_IS_STOCK, Boolean.FALSE);
		openScene(ItemSearchController.class, new SceneCallback<Map<String, Object>>() {
			
			@Override
			public void onSuccess(Map<String, Object> callbackData) {
				log.debug("abrirBusquedaArticulos() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
	            String codArt = (String) callbackData.get(ItemSearchController.PARAM_OUTPUT_ITEM_CODE);
	            String desglose1 = (String) callbackData.get(ItemSearchController.PARAM_OUTPUT_COMBINATION_1_CODE);
	            String desglose2 = (String) callbackData.get(ItemSearchController.PARAM_OUTPUT_COMBINATION_2_CODE);

	            frArticulo.setCantidad("1");
	            try {
	            	if (accionValidarFormulario()) {
	            		if(giftCardService.isBalanceCardItem(codArt)){
	            			DialogWindowComponent.openWarnWindow(I18N.getText("No están permitidas recargas de tarjeta regalo en apartados."), getStage());
	            		}
	            		else{
	            			BasketItem lineaNueva = insertarLineaVenta(codArt, desglose1, desglose2, frArticulo.getCantidadAsBigDecimal());
	            			
	            			comprobarArticuloGenerico(lineaNueva);
//	            			TODO: MSB Revisar apartados
//	            			if(lineaNueva.getGenerico()){
//	                    		HashMap<String, Object> sceneData = new HashMap<>();
//								sceneData.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, lineaNueva);
//								sceneData.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
//								openScene(EdicionArticuloController.class);
//								
//								if(sceneData.containsKey(EdicionArticuloController.CLAVE_CANCELADO)){
//									throw new LineaInsertadaNoPermitidaException(lineaNueva);
//								}
//	            			}
//	            			
//	            			comprobarLineaPrecioCero(lineaNueva);
//	            			
//	            			ticketManager.recalcularConPromociones();
//	            			apartadoManager.introducirDetalleApartado(lineaNueva);
//	    					
//	            			nuevosArticulos++;
//	            			refrescarDatosPantalla();
	            		}            		
	            	}
	            }
	            catch (BasketLineException ex) {
	                log.error("abrirBusquedaArticulos() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
	                DialogWindowComponent.openInfoWindow(ex.getLocalizedMessage(), getScene().getWindow());
//	            } catch (NewBasketItemNoAllowedException e) {
//					ticketManager.getBasketTransaction().getLines().remove(e.getLinea());
//					if(e.getMessage() != null){
//						DialogWindowComponent.openErrorWindow(e.getMessage(), getStage());
//					}
	            } catch (Exception e) {
	            	log.error("abrirBusquedaArticulos() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
	            	DialogWindowComponent.openErrorWindow(getStage(), I18N.getText("No se ha podido insertar la línea"), e);
				}
	            initializeFocus();
			}
			
			@Override
			public void onCancel() {
				initializeFocus();
			}
		});
		
	}

	protected boolean accionValidarFormulario() {
		// Limpiamos los errores que pudiese tener el formulario
		frArticulo.clearErrorStyle();

		// Validamos el formulario 
		Set<ConstraintViolation<ItemLineValidationForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frArticulo);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<ItemLineValidationForm> next = constraintViolations.iterator().next();
			frArticulo.setErrorStyle(next.getPropertyPath(), true);
			frArticulo.setFocus(next.getPropertyPath());
			DialogWindowComponent.openErrorWindow(next.getMessage(), this.getScene().getWindow());
			return false;
		}

		return true;
	}

	@Override
	public void executeAction(ActionButtonComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
    	switch (botonAccionado.getClave()) {
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
    		closeCancel();
    		break;
    	default:
    		log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
    		break;
    	}
	}
	
	protected List<ButtonConfigurationBean> cargarAccionesTabla() {
        List<ButtonConfigurationBean> listaAcciones = new ArrayList<>();
                
        if(!modoEdicion){
        	listaAcciones.add(new ButtonConfigurationBean("icons/back.png", null, null, "ACCION_TABLA_VOLVER_A_APARTADOS", "REALIZAR_ACCION")); //"Home"
        }

        if(modoEdicion){
        	listaAcciones.add(new ButtonConfigurationBean("icons/aceptar.png", null, null, "ACCION_TABLA_CONFIRMAR_CAMBIOS", "REALIZAR_ACCION"));
        	listaAcciones.add(new ButtonConfigurationBean("icons/cancelar.png", null, null, "ACCION_TABLA_RECHAZAR_CAMBIOS", "REALIZAR_ACCION"));  //"Num Pad -"
        }
        else{
        	listaAcciones.add(new ButtonConfigurationBean("icons/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO", "REALIZAR_ACCION")); //"Delete"
        }
        
        return listaAcciones;
    }
	
	public void closeSuccess() {
		visor.clear();
		super.closeSuccess();
	}
	
	public void closeCancel(){
		visor.clear();
		super.closeCancel();
	}
	
	public void mostrarPagos(){
		log.trace("mostrarPagos()");
		
		List<ApartadosPagoBean> pagos = apartadoManager.getTicketApartado().getMovimientos();
		
		if(pagos.isEmpty())
		{
			DialogWindowComponent.openWarnWindow(I18N.getText("No existen pagos asociados al apartado."), this.getStage());
		}
		else
		{
			sceneData.put(VerPagosApartadoController.PARAMETRO_PAGOS, pagos);
			sceneData.put(VerPagosApartadoController.PARAMETRO_APARTADO_MGR, apartadoManager);
//			TODO: MSB: Revisar apartados
//			openScene(VerPagosApartadoController.class);
		}
		
		initializeFocus();
	}

	@SuppressWarnings("unchecked")
	public void confirmarCambios(){
		log.trace("confirmarCambios()"); 
		
		CzzDate f = CzzDate.getCzzDate(tfFechaRecogida.getSelectedDate());
		CzzDate fActual = new CzzDate(new CzzDate(new Date()).getString());
		if(f.afterOrEquals(fActual)){

			frDatosApartado.setFechaRecogida(tfFechaRecogida.getTexto());

			if(validarDatosApartado()){
				if(apartadoManager.getTicketApartado().getArticulos().isEmpty() && nuevosArticulos == 0){
					DialogWindowComponent.openWarnWindow(I18N.getText("No se puede crear un apartado vacío"), this.getStage());
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
					
					DialogWindowComponent.openInfoWindow(I18N.getText("Cambios guardados correctamente."), getStage());

					modoEdicion = false;
					int rowSelected = tbArticulos.getSelectionModel().getSelectedIndex();
					if(rowSelected<0){
						tbArticulos.getSelectionModel().select(0);
					}
//					ticketManager.createBasketTransaction();

					if(actionHandlerBuscar!=null){
						actionHandlerBuscar.handle(null);
						closeSuccess();
					}
					else{
						refrescarDatosPantalla();
					}
				}
			}
		}
		else{
			DialogWindowComponent.openWarnWindow(I18N.getText("La fecha de recogida no debe ser anterior al día de hoy."), getStage());
		}
	}

	public void rechazarCambios(){
		log.trace("rechazarCambios()");

		modoEdicion = false;
		nuevosArticulos = 0;
		apartadoManager.rechazarNuevosArticulos();
//		ticketManager.createBasketTransaction();
		
		if(apartadoNuevo){
			closeCancel();
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
			DialogWindowComponent.openWarnWindow(I18N.getText("No se pueden editar datos de un apartado finalizado o cancelado."), getStage());
		}
		else{
			sceneData.put(CambiarDatosClienteApartadoController.PARAMETRO_APARTADOS_MANAGER, apartadoManager);
			//TODO: MSB: Revisar apartados
//			openScene(CambiarDatosClienteApartadoController.class);
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
			DialogWindowComponent.openErrorWindow(next.getMessage(), this.getScene().getWindow());
			return false;
		}

		return true;
	}
	
	public void accionEliminarArticulo(){
		log.trace("accionEliminarArticulo()");

		LineaArticuloApartadoRow linea = (LineaArticuloApartadoRow)tbArticulos.getSelectionModel().getSelectedItem();
		
		if(linea!=null){
			if(linea.isVendido()){
				DialogWindowComponent.openWarnWindow(I18N.getText("No se puede borrar una línea que ya está servida."), this.getStage());
			}
			else
			{
				ApartadosDetalleBean detalle = linea.getDetalle();	
				
				if(detalle.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_CANCELADO){
					DialogWindowComponent.openWarnWindow(I18N.getText("El artículo ya está cancelado."), this.getStage());
				}	
				else
				{
					if(DialogWindowComponent.openConfirmWindow(I18N.getText("Se va a cancelar el artículo seleccionado. ¿Está seguro?"), getStage())){																
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
			DialogWindowComponent.openWarnWindow(I18N.getText("Debe confirmar o rechazar antes los cambios."), getStage());
			initializeFocus();
		}
		else{
			if(apartadoManager.getTicketApartado().getCabecera().getEstadoApartado() != ApartadosCabeceraBean.ESTADO_DISPONIBLE){
				DialogWindowComponent.openWarnWindow(I18N.getText("No se pueden añadir pagos al apartado."), getStage());
			}
			else{
				if (apartadoManager.getTicketApartado().getTotalPendiente().compareTo(BigDecimal.ZERO)>0) {        
					log.debug("abrirPagos() - El ticket tiene líneas");

//					ticketManager.createBasketTransaction();
					ticketManager.getBasketTransaction().getTotals().setTotalToPay(apartadoManager.getTicketApartado().getTotalPendiente());		

					sceneData.put(PARAMETRO_IMPORTE_MAXIMO_PAGO, ticketManager.getBasketTransaction().getTotals().getTotalToPay());	
//					TODO: MSB Revisar apartados
//					openScene(PagoApartadoController.class);
//					if(sceneData.containsKey(PagoApartadoController.PARAMETRO_IMPORTE_PAGO)){
//						BigDecimal importePago = (BigDecimal)sceneData.remove(PagoApartadoController.PARAMETRO_IMPORTE_PAGO);
//						ticketManager.getBasketTransaction().getCabecera().getTotales().setTotalAPagar(importePago);
//						sceneData.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
//						sceneData.put(NuevoPagoApartadoController.PARAMETRO_APARTADO_MANAGER, apartadoManager);
//						openScene(NuevoPagoApartadoController.class);
//						escribirEntradaEnVisor();
//						apartadoManager.actualizarPagos();
//						refrescarDatosPantalla();
////						TODO: MSB: Borrado temporal
////						getMainView().resetSubViews();
//					}
				}
				else {
					log.warn("abrirPagos() - Ticket vacio");
					DialogWindowComponent.openWarnWindow(I18N.getText("El importe pendiente de abonar es 0."), this.getStage());
				}
			}
		}
		initializeFocus();
    }
	
	public void vender(){
		log.trace("vender()");

		if(modoEdicion){
			DialogWindowComponent.openWarnWindow(I18N.getText("Debe confirmar antes los cambios."), getStage());
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
				DialogWindowComponent.openWarnWindow(I18N.getText("No hay artículos disponibles para su venta."), this.getStage());
			}
			else{
				sceneData.put(MarcarVentaApartadoController.PARAMETRO_APARTADO_MGR, apartadoManager);
				sceneData.put(MarcarVentaApartadoController.PARAMETRO_ARTICULOS, articulosDisponiblesVenta);
//				TODO: MSB Revisar apartados
//				openScene(MarcarVentaApartadoController.class);
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
//				TODO: MSB Revisar apartados
//				sceneData.put(ConsultaClienteController.MODO_MODAL, true);
//				
//				openScene(ConsultaClienteController.class);
//				if(sceneData.containsKey(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE)){
//					ClienteBean cliente = (ClienteBean)sceneData.get(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE);
//					if(checkClientTaxes(cliente)) {
//						ticketManager.getBasketTransaction().getCabecera().setCliente(modelMapper.map(cliente, BasketCustomer.class));
//						apartadoManager.setCliente(cliente);
//					}
//					refrescarDatosPantalla();
//				}
			}
			catch (Exception ex) {
				log.error(ex.getLocalizedMessage(), ex);
				DialogWindowComponent.openErrorWindow(getStage(), ex);
			}
		}
		else{
			DialogWindowComponent.openWarnWindow(I18N.getText("No se puede cambiar el cliente del apartado."), getStage());
		}
		initializeFocus();
	}
	
	protected boolean checkClientTaxes(Customer cliente) {
		TaxTreatment trat = null;
		TaxTreatment shopTrat = null;
		try {
			trat = tratamientoImpuestoService.findById(cliente.getTaxTreatmentId());
			shopTrat = tratamientoImpuestoService.findById(sesion.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getTaxTreatmentId());
		}catch(Exception ignore) {}
		if(trat == null || !trat.getCountryCode().equals(sesion.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode())) {
			DialogWindowComponent.openWarnWindow(I18N.getText("No es posible seleccionar este cliente al tener un tratamiento de impuestos no disponible para el país asociado a la tienda actual."), this.getStage());
			return false;
		}else if(sesion.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getTaxTreatmentId().equals(trat.getTaxTreatmentId()) || DialogWindowComponent.openConfirmWindow(I18N.getText("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}. Confirme si desea continuar.",trat.getTaxTreatmentDes(),shopTrat.getTaxTreatmentDes()), this.getStage())) {		
			return true;
		}
		return false;
	}
	
	@Override
    public boolean canClose() {
		log.trace("canClose()");

    	if (modoEdicion) {    
    		DialogWindowComponent.openWarnWindow(I18N.getText("Debe confirmar o rechazar los cambios para poder cerrar la pantalla de detalle de apartado."), getStage());
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
			DialogWindowComponent.openWarnWindow(I18N.getText("Debe confirmar o rechazar los cambios antes de imprimir el extracto."), getStage());
		}
		else{
			try {
				apartadoManager.imprimirApartado();
			} catch (DeviceException e) {
				log.error("Error imprimiendo el resumen del apartado.", e);
				DialogWindowComponent.openWarnWindow(I18N.getText("Se produjo un error en la impresión"), getStage());
			}
		}
	}
	
	protected void comprobarLineaPrecioCero(BasketItem linea) throws NewBasketItemNoAllowedException {
		Boolean permiteVentaPrecioCero = variablesServices.getVariableAsBoolean(VariableServiceFacade.TPV_PERMITIR_VENTA_PRECIO_CERO, true);
		// TODO: AMA, ver si esto hace falta
//		//Comprobamos tarifa es distinta de null
//		if(linea.getTarifa().getVersion() == null){ //Si la versión es null, es cero y no viene de BD
//			log.debug("comprobarLineaPrecioCero() - La versión de la tarifa de la linea es null");
//			if(permiteVentaPrecioCero){
//				boolean vender = VentanaDialogoComponent.crearVentanaConfirmacion(
//						I18N.getTexto("El artículo \"{0} - {1}\" no está tarificado.", linea.getCodArticulo(), linea.getDesArticulo()) + "\n" + I18N.getTexto("¿Desea vender el artículo a precio 0?")
//						, getStage());
//				if(!vender){
//					throw new LineaInsertadaNoPermitidaException(linea);
//				}else{
//					return;
//				}
//			}else{
//				throw new LineaInsertadaNoPermitidaException(I18N.getTexto("El artículo \"{0} - {1}\" no está tarificado.", linea.getCodArticulo(), linea.getDesArticulo()) + "\n" + I18N.getTexto("No está permitida la venta a precio 0."), linea);
//			}
//		}
		
//		//Comprobamos precio cero
//		if(BigDecimalUtil.isEqualsToZero(linea.getPriceWithTaxes())){
//			if(permiteVentaPrecioCero){
//				boolean vender = DialogWindowComponent.openConfirmWindow(
//						I18N.getText("¿Desea vender el artículo a precio 0?")
//						, getStage());
//				if(!vender){
//					throw new NewBasketItemNoAllowedException(linea);
//				}else{
//					return;
//				}
//			}else{
//				throw new NewBasketItemNoAllowedException(I18N.getText("No está permitida la venta a precio 0."), linea);
//			}
//		}
	}
	
	protected void comprobarArticuloGenerico(BasketItem linea) throws NewBasketItemNoAllowedException {
		//Si el artículo es genérico y no tiene permiso, no se puede insertar
		if(linea.getItemData().getGenericItem()){
			try {
				checkOperationPermissions(RetailBasketItemizationController.PERMISSION_GENERIC_ITEMS);
			} catch (PermissionDeniedException e) {
				boolean permiso = false;
				EffectiveActionPermissionsDto  permisosEfectivos = sesion.getPOSUserSession().getEffectiveActionPermissions(getStageController().getCurrentAction().getAction());
				Map<String, PermissionDTO> permisos = permisosEfectivos.getPermissions();
				if(!permisos.containsKey(RetailBasketItemizationController.PERMISSION_GENERIC_ITEMS)){
					log.warn("comprobarArticuloGenerico() - No existe la operación " + RetailBasketItemizationController.PERMISSION_GENERIC_ITEMS + " en base de datos, se devuelve que SÍ tiene permiso");
					permiso = true;
				}
				
//				if(!permiso){
//					throw new NewBasketItemNoAllowedException(I18N.getText("No tiene permisos para usar articulos genéricos"), linea);
//				}
				
			}
		}
	}
	
}
