package com.comerzzia.pos.gui.sales.layaway.items.marcarVenta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.omnichannel.facade.service.basket.layaway.LayawayBasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.layaway.ApartadosManager;
import com.comerzzia.pos.gui.sales.retail.items.serialnumbers.RetailSerialNumberController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

@Component
@CzzScene
public class MarcarVentaApartadoController extends SceneController implements ButtonsGroupController{
	
	protected final Logger log = Logger.getLogger(getClass());
	
	public static final String PARAMETRO_ARTICULOS = "articulos_venta";
	
	public static final String PARAMETRO_APARTADO_MGR = "apartado_manager";
	
	protected ButtonsGroupComponent botoneraAccionesTabla;
	
	@FXML
	protected AnchorPane panelBotonera;
	@FXML
	protected TableView<LineaVentaApartadoRow> tbArticulos;
	@FXML
	@SuppressWarnings("rawtypes")
	protected TableColumn tcArticulo, tcDescripcion, tcDesglose1, tcDesglose2, tcPrecio, tcDescuento, tcCantidad, tcImporte, tcBtSelec;
	
	protected ObservableList<LineaVentaApartadoRow> articulos;
	
	protected List<ApartadosDetalleBean> articulosApartado;
	
	protected ApartadosManager apartadosManager;
	
	protected LayawayBasketManager ticketManager;
	
	@Autowired
	protected VariableServiceFacade variablesServices;
		
	@Autowired
    protected ModelMapper modelMapper;
	
	@Autowired
	protected Session sesion;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		articulos = FXCollections.observableArrayList();
		
		tbArticulos.setPlaceholder(new Label(""));
		
		tbArticulos.setItems(articulos);
		
		tcArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcArticulo", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDescripcion", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcDesglose1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDesglose1", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcDesglose2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDesglose2", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcPrecio.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcPrecio", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcImporte", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcDescuento.setCellFactory(CellFactoryBuilder.createCellRendererCeldaPorcentaje("tbArticulos", "tcDescuento", 2, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcCantidad", 3, CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcBtSelec.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcBtSelec", null, CellFactoryBuilder.CENTER_ALIGN_STYLE));

		Boolean usaDescuentoEnLinea = variablesServices.getVariableAsBoolean(VariableServiceFacade.TICKETS_USA_DESCUENTO_EN_LINEA);
		if(!usaDescuentoEnLinea){
			tcDescuento.setVisible(false);
		}

		// Asignamos las lineas a la tabla
		tbArticulos.setItems(articulos);

		// Definimos un factory para cada celda para aumentar el rendimiento
		tcArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoRow, String> cdf) {
				return cdf.getValue().getCodArtProperty();
			}
		});
		tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoRow, String> cdf) {
				return cdf.getValue().getDesArtProperty();
			}
		});
		tcCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaVentaApartadoRow, BigDecimal> cdf) {
				return cdf.getValue().getCantidadProperty();
			}
		});
		tcDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoRow, String> cdf) {
				return cdf.getValue().getDesglose1Property();
			}
		});
		tcDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoRow, String> cdf) {
				return cdf.getValue().getDesglose2Property();
			}
		});
		tcPrecio.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaVentaApartadoRow, BigDecimal> cdf) {
				return cdf.getValue().getPrecioProperty();
			}
		});
		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaVentaApartadoRow, BigDecimal> cdf) {
				return cdf.getValue().getImporteProperty();
			}
		});
		tcDescuento.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaVentaApartadoRow, BigDecimal> cdf) {
				return cdf.getValue().getDtoProperty();
			}
		});
		tcBtSelec.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaVentaApartadoRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaVentaApartadoRow, String> cdf) {
				if(cdf.getValue().isLineaSelec()){
					return new SimpleStringProperty("X");
				}
				else{
					return new SimpleStringProperty("");
				}
			}
		});
		
		tbArticulos.setRowFactory(new Callback<TableView<LineaVentaApartadoRow>, TableRow<LineaVentaApartadoRow>>() {

			@Override
			public TableRow<LineaVentaApartadoRow> call(TableView<LineaVentaApartadoRow> p) {
				final TableRow<LineaVentaApartadoRow> row = new TableRow<LineaVentaApartadoRow>() {
					@Override
					protected void updateItem(LineaVentaApartadoRow linea, boolean empty){
						super.updateItem(linea, empty);
						if (linea!=null){
							if(linea.isLineaSelec()) {
								if (!getStyleClass().contains("cell-renderer-cupon")) {
									getStyleClass().add("cell-renderer-cupon");
								}
							}
							else {
        						getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
        					}
							
        				} 
        				else {
        					getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
        					
        				}
					}
				};
				return row;
			}
		});
		
		tbArticulos.setEditable(true);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		
		ticketManager = SpringContext.getBean(LayawayBasketManager.class);
		
		apartadosManager = (ApartadosManager)sceneData.remove(PARAMETRO_APARTADO_MGR);

		try{          
            log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de ventas");
            List<ButtonConfigurationBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new ButtonsGroupComponent(1, 7, this, listaAccionesAccionesTabla, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), ActionButtonSimpleComponent.class.getName());
            panelBotonera.getChildren().add(botoneraAccionesTabla);
		}
		catch (LoadWindowException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		} 		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		
		articulosApartado = (List<ApartadosDetalleBean>)sceneData.remove(PARAMETRO_ARTICULOS);
		refrescarPantalla();
	}

	@Override
	public void initializeFocus() {
		tbArticulos.requestFocus();
		tbArticulos.getSelectionModel().select(0);
	}
	
	public void refrescarPantalla(){
		
		int rowSelected = tbArticulos.getSelectionModel().getSelectedIndex();
		articulos.clear();
		
		for(ApartadosDetalleBean articulo : articulosApartado){
			articulos.add(new LineaVentaApartadoRow(articulo));
		}
		tbArticulos.getSelectionModel().select(rowSelected);
	}

	@Override
	public void executeAction(ActionButtonComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
    	switch (botonAccionado.getClave()) {
    	case "ACCION_TABLA_SELECCIONAR_TODOS":
    		seleccionarTodos(true);
    		break;
    	case "ACCION_TABLA_DESELECCIONAR_TODOS":
    		seleccionarTodos(false);
    		break;
    	case "ACCION_TABLA_SELECCIONAR_MARCADO":
    		seleccionarUno();
    		break;
    	default:
    		log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
    		break;
    	}
	}
	
	protected List<ButtonConfigurationBean> cargarAccionesTabla() {
        List<ButtonConfigurationBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ButtonConfigurationBean("icons/row-plus.png", null, null, "ACCION_TABLA_SELECCIONAR_MARCADO", "REALIZAR_ACCION")); //"Delete"
        listaAcciones.add(new ButtonConfigurationBean("icons/row-all.png", null, null, "ACCION_TABLA_SELECCIONAR_TODOS", "REALIZAR_ACCION")); //"Delete"
        listaAcciones.add(new ButtonConfigurationBean("icons/row-del-sel.png", null, null, "ACCION_TABLA_DESELECCIONAR_TODOS", "REALIZAR_ACCION"));
        return listaAcciones;
    }
	
	public void seleccionarUno(){
		log.trace("seleccionarUno()");
		
		LineaVentaApartadoRow linea = tbArticulos.getSelectionModel().getSelectedItem();
		
		for(ApartadosDetalleBean lineaArticulo : articulosApartado){
			if(lineaArticulo.getLinea() == linea.getArticulo().getLinea()){
				if(lineaArticulo.isLineaSeleccionadaVenta()){
					lineaArticulo.setLineaSeleccionadaVenta(false);
				}
				else{
					lineaArticulo.setLineaSeleccionadaVenta(true);
				}
			}
		}
		
		refrescarPantalla();
	}

	public void seleccionarTodos(boolean seleccionar){
		log.trace("seleccionarTodos()");
		
		for(ApartadosDetalleBean lineaArticulo : articulosApartado){
			lineaArticulo.setLineaSeleccionadaVenta(seleccionar);
		}
		refrescarPantalla();
	}
	
	public void accionAceptar(){
		log.trace("accionAceptar()");
		
		Catalog catalog = applicationSession.getValidCatalog();

		List<ApartadosDetalleBean> articulosVenta = new ArrayList<ApartadosDetalleBean>();
		BigDecimal importeVenta = BigDecimal.ZERO;

		for(ApartadosDetalleBean linea : articulosApartado){
			if(linea.isLineaSeleccionadaVenta()){
				
				try {
					CatalogItemDetail articulo = catalog.getCatalogItemService().findByBarcode(linea.getCodart());
					
					if (articulo.getSerialNumbersActive()) {
//						ticketManager.createBasketTransaction();
						ticketManager.updateCustomer(modelMapper.map(apartadosManager.getCliente(), BasketCustomer.class));
						
						BasketItem lineaTicket = null; // ticketManager.createAndInsertBasketItem(linea.getCodart(), linea.getDesglose1(), linea.getDesglose2(), linea.getCantidad());
						asignarNumerosSerie(lineaTicket, linea);
						
						if(lineaTicket.getSerialNumbers() == null || (lineaTicket.getSerialNumbers() != null && lineaTicket.getSerialNumbers().isEmpty()) || (lineaTicket.getSerialNumbers() != null && lineaTicket.getSerialNumbers().size() < lineaTicket.getQuantity().setScale(0, RoundingMode.HALF_UP).abs().intValue())) {
							DialogWindowComponent.openWarnWindow(I18N.getText("Debe indicar los números de serie para continuar."), getStage());
							return;
						}
					}
				}
				catch(Exception e) {
					log.error("accionAceptar() - No se han podido procesar los números de serie: " + e.getMessage(), e);
					DialogWindowComponent.openErrorWindow(getStage(), I18N.getText("Ha habido un error al procesar los números de serie"), e);
				}
				
				articulosVenta.add(linea);
				importeVenta = importeVenta.add(linea.getImporteTotal());
			}
		}
		
		
		if(articulosVenta.isEmpty()){
			DialogWindowComponent.openWarnWindow(I18N.getText("Debe seleccionar al menos un artículo para la venta."), getStage());
		}
		else{
			if(importeVenta.compareTo(apartadosManager.getTicketApartado().getCabecera().getSaldoCliente())>0){
				DialogWindowComponent.openWarnWindow(I18N.getText("El saldo disponible del cliente es inferior al total de la venta."), getStage());
			}
			else{
//					try {
//						boolean errorVigencia = false;
//						ticketManager.generarVentaDeApartados(articulosVenta, apartadosManager.getCliente(), apartadosManager.getTicketApartado().getCabecera());
//						if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Desea asignar datos de facturación?"), getStage())){
//							if(!ticketManager.comprobarConfigContador(sesion.getAplicacion().getDocTypeForNatureDocumentType(DocTypeServiceFacade.NATURE_INVOICE_DOCUMENT).getDocTypeCode())) {
//								errorVigencia = true;
//							}else{
//								sceneData.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
////					    		TODO: MSB Revisar apartados
////								openScene(FacturaController.class);
//							}
//						}else{
//							errorVigencia = !ticketManager.comprobarConfigContador(ticketManager.getDocumentoActivo().getDocTypeCode());
//						}
//						if(errorVigencia){
//							VentanaDialogoComponent.crearVentanaError(ticketManager.getCounterErrorMessage(), getStage());
//						}else{
////							ticketsService.setContadorIdTicket(((POSBasketManagerAbstract)ticketManager).getTicket());
//							apartadosManager.actualizarEstadoLineasVendidas(ticketManager.getBasketTransaction().getBasketUid(), articulosVenta);
//							apartadosManager.registrarVentaApartado(importeVenta);						
//							
//							new SalvarTicketBackgroundTask(ticketManager, new SalvarTicketCallback() {
//								
//								@Override
//								public void onSucceeded() {
//									Map<String,Object> mapaParametros= new HashMap<String,Object>();
//									mapaParametros.put("ticket",ticketManager.getBasketTransaction());
//									mapaParametros.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
//									mapaParametros.put("apartado", apartadosManager.getTicketApartado().getCabecera().getIdApartado().toString());
//									
//									try{
//										ServicioImpresion.imprimir(ticketManager.getBasketTransaction().getHeader().getPrintFormat(), mapaParametros);
//									}catch (Exception e) {
//										VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), e);
//									}
//									ticketManager.finalizarTicket();
//									close();
//								}
//								
//								@Override
//								public void onFailure(Exception e) {
//									log.error("onFailure() - Error al salvar el ticket", e);
//									VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error al salvar el ticket."), e);
//									close();
//								}
//							});
//						}
//					} catch (LineaTicketException e) {
//						log.error(e.getMessage(), e);
//						VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
////					} catch (TicketsServiceException e) {
////						log.error(e.getMessage(), e);
////						VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
//					}
			}
		}
	}

	public void accionCancelar(){
		log.trace("accionCancelar()");
		
		seleccionarTodos(false);
		closeCancel();
	}
	
	public void aceptarArticuloDobleClick(MouseEvent event) {
		log.debug("aceptarArticuloDobleClick() - Acción aceptar");
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				seleccionarUno();
			}
		}
	}
	
	protected boolean quedanNumerosSeriePorAsignar(List<ApartadosDetalleBean> articulosVenta, Map<ApartadosDetalleBean, List<String>> numerosSerie) {
		boolean res = false;
		for(ApartadosDetalleBean detalle : articulosVenta) {
			List<String> numerosSerieDetalle = numerosSerie.get(detalle);
			res = numerosSerieDetalle != null && numerosSerieDetalle.size() < detalle.getCantidad().intValue();
			if(res) {
				break;
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	protected void asignarNumerosSerie(BasketItem linea, ApartadosDetalleBean detalleApartado){
//		sceneData.put(SerialNumberController.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, new ArrayList<String>());
//		sceneData.put(SerialNumberController.PARAM_SERIAL_NUMBER_LINE, linea);
//		openScene(NumerosSerieController.class);
//	    List<String> numerosSerie = (List<String>) sceneData.get(NumerosSerieController.PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS);
//	    detalleApartado.setNumerosSerie(numerosSerie);
	}

}
