package com.comerzzia.pos.gui.sales.layaway;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.comerzzia.catalog.facade.model.CatalogItemDetail;
import com.comerzzia.catalog.facade.service.Catalog;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.conversion.BasketDocumentIssued;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.ApartadosCabeceraBean;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.omnichannel.facade.service.basket.BasketManagerBuilder;
import com.comerzzia.omnichannel.facade.service.basket.layaway.LayawayBasketManager;
import com.comerzzia.omnichannel.facade.service.deprecated.layawaydocument.LayawayDocumentService;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.gui.BackgroundTask;
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
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.basket.SaveDocumentBackgroundTask;
import com.comerzzia.pos.gui.sales.basket.SaveDocumentCallback;
import com.comerzzia.pos.gui.sales.layaway.items.DetalleApartadosController;
import com.comerzzia.pos.gui.sales.retail.items.serialnumbers.RetailSerialNumberController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
@CzzScene
public class ApartadosController extends SceneController implements Initializable, ButtonsGroupController {

    // log
    private static final Logger log = Logger.getLogger(ApartadosController.class.getName());
    
    @FXML
    protected TextField tfCliente, tfApartado;
     
     @FXML
     protected CheckBox cbVerTodo;
     
    // Componentes de ventana
    @FXML
    protected AnchorPane panelBotonera;
    // botonera de acciones de tabla
    protected ButtonsGroupComponent botoneraAccionesTabla;

    protected ObservableList<LineaApartadoDto> lineas;
    
    protected LineaApartadoDto lastSelected;
    protected int lastSelectedIndex;
    
    public ApartadosManager apartadosManager;
    
    public LayawayBasketManager ticketManager;
    
    @FXML
    protected TableView<LineaApartadoDto> tbApartados;
    
    @FXML
    protected Label lbError;
    
    @FXML
    protected TableColumn tcFecha, tcImporte, tcCliente, tcNumApartado, tcSaldoCliente;
    
    protected FormularioBusquedaApartadoBean frBusquedaApartado;
    
    @Autowired
    private LayawayDocumentService apartadosService;
    @Autowired
    protected VariableServiceFacade variablesServices;
    @Autowired
    private Session sesion;
    
    @Autowired
    protected ModelMapper modelMapper;
    
    final DeviceLineDisplay visor = Devices.getInstance().getLineDisplay();
    
    /**
     * Initializes the controller class.
     */
	@Override
    public void initialize(URL url, ResourceBundle rb) {
    	frBusquedaApartado = SpringContext.getBean(FormularioBusquedaApartadoBean.class);
    	frBusquedaApartado.setFormField("numApartado", tfApartado);
    	
    	lineas = FXCollections.observableArrayList(new ArrayList<LineaApartadoDto>());
    	
    	tbApartados.setPlaceholder(new Label(""));
    	tbApartados.setItems(lineas);
    	
    	tcNumApartado.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcNumApartado", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
    	tcCliente.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcCliente", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFecha("tbLineas", "tcFecha", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcImporte", CellFactoryBuilder.RIGHT_ALIGN_STYLE));
		tcSaldoCliente.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcSaldoCliente", CellFactoryBuilder.RIGHT_ALIGN_STYLE));
    	
		tcNumApartado.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoDto, Long>, ObservableValue<Long>>() {
            @Override
            public ObservableValue<Long> call(CellDataFeatures<LineaApartadoDto, Long> cdf) {
                return cdf.getValue().getNumApartadoProperty();
            }
        });
		tcCliente.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<LineaApartadoDto, String> cdf) {
                return cdf.getValue().getClienteProperty();
            }
        });
		tcFecha.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoDto, Date>, ObservableValue<Date>>() {
			@Override
            public ObservableValue<Date> call(CellDataFeatures<LineaApartadoDto, Date> cdf) {
                return cdf.getValue().getFechaProperty();
            }
        });
		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoDto, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(CellDataFeatures<LineaApartadoDto, BigDecimal> cdf) {
                return cdf.getValue().getImporteProperty();
            }
        });
		tcSaldoCliente.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoDto, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(CellDataFeatures<LineaApartadoDto, BigDecimal> cdf) {
                return cdf.getValue().getSaldoClienteProperty();
            }
        });
		
		tbApartados.setRowFactory(new Callback<TableView<LineaApartadoDto>, TableRow<LineaApartadoDto>>() {

        	@Override
        	public TableRow<LineaApartadoDto> call(TableView<LineaApartadoDto> p) {
        		final TableRow<LineaApartadoDto> row = new TableRow<LineaApartadoDto>() {
        			@Override
        			protected void updateItem(LineaApartadoDto linea, boolean empty){
        				super.updateItem(linea, empty);
        				if (linea!=null){
        					if(linea.getEstado() == ApartadosCabeceraBean.ESTADO_CANCELADO) {
        						getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));  
                                if (!getStyleClass().contains("cell-renderer-apartadoCancelado")) {
                                    getStyleClass().add("cell-renderer-apartadoCancelado");
                                }
                            }
        					else if(linea.getEstado() == ApartadosCabeceraBean.ESTADO_FINALIZADO) {
        						getStyleClass().removeAll(Collections.singleton("cell-renderer-apartadoCancelado"));
        						if (!getStyleClass().contains("cell-renderer-lineaDocAjeno")) {
        							getStyleClass().add("cell-renderer-lineaDocAjeno");
        						}
        					}
        					else {
        						getStyleClass().removeAll(Collections.singleton("cell-renderer-apartadoCancelado"));
        						getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));        						
        					}
        				} 
        				else {
        					getStyleClass().removeAll(Collections.singleton("cell-renderer-apartadoCancelado"));
        					getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));        					
        				}
        			}
        		};
        		return row;
        	}
        });
    }
    
    @Override
    public void initializeComponents() {
        try {
            log.debug("inicializarComponentes() - Inicializando componentes");
            
            initializeManager();
            
            List<ButtonConfigurationBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new ButtonsGroupComponent(1, 5, this, listaAccionesAccionesTabla, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), ActionButtonSimpleComponent.class.getName());
            panelBotonera.getChildren().add(botoneraAccionesTabla);
            
        } catch (LoadWindowException ex) {
            log.error("inicializarComponentes() - Error cargando pantalla de apartados");
            DialogWindowComponent.openErrorWindow(I18N.getText("Error cargando pantalla de apartados."), getStage());
        }
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	
    	comprobarAperturaPantalla();
    	
    	frBusquedaApartado.clearErrorStyle();
    	tfApartado.setText("");
    	tfCliente.setText("");
    	
    	lineas.clear();
    	accionBtBuscar();
    	
    	visor.clear();
    }
    
	/**
	 * Realiza las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
	 * @throws InitializeGuiException
	 */
	protected void comprobarAperturaPantalla() throws InitializeGuiException {
	    if (!sesion.getCashJournalSession().isOpenedCashJournal()) {
	    	Boolean aperturaAutomatica = variablesServices.getVariableAsBoolean(VariableServiceFacade.CAJA_APERTURA_AUTOMATICA, true);
	    	if(aperturaAutomatica){
	    		DialogWindowComponent.openInfoWindow(I18N.getText("No hay caja abierta. Se abrirá automáticamente."), getStage());
	    		sesion.getCashJournalSession().openAutomaticCashJournal();
	    	}else{
	    		DialogWindowComponent.openErrorWindow(I18N.getText("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."), getStage());
	    		throw new InitializeGuiException(false);
	    	}
	    }
	    
	    if(!sesion.getCashJournalSession().checkCashJournalClosingMandatory()){
	    	String fechaCaja = FormatUtils.getInstance().formatDate(sesion.getCashJournalSession().getOpeningDate());
	    	String fechaActual = FormatUtils.getInstance().formatDate(new Date());
	    	DialogWindowComponent.openErrorWindow(I18N.getText("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual), getStage());
	    	throw new InitializeGuiException(false);
	    }
    }

    @Override
    public void initializeFocus() {
		if (lastSelected == null) {
			tfApartado.requestFocus();
		} else {
			tbApartados.requestFocus();
			tbApartados.getSelectionModel().selectFirst();
		}
    }

    protected int searchLastSelectedIndex(){
    	if(lastSelected == null) {
    		return 0;
    	}
    	for (LineaApartadoDto linea : lineas) {
			if(linea.getApartado().getIdApartado() != null && linea.getApartado().getIdApartado().equals(lastSelected.getApartado().getIdApartado())){
				return lineas.indexOf(linea);
			}
		}
    	if(lastSelectedIndex == 0){
    		return 0;
    	}
    	return -1;
    }
    
    public void initializeManager() {
    	ticketManager = BasketManagerBuilder.build(LayawayBasketManager.class, sesion.getApplicationSession().getStorePosBusinessData());
    	
    	apartadosManager = SpringContext.getBean(ApartadosManager.class);
    }
    
    @Override
    public void executeAction(ActionButtonComponent botonAccionado) {
    	log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
    	switch (botonAccionado.getClave()) {
    	case "ACCION_TABLA_PRIMER_REGISTRO":
    		actionTableEventFirst(tbApartados);
    		reasignarFocoBtDesplazamiento();
    		break;
    	case "ACCION_TABLA_ANTERIOR_REGISTRO":
    		actionTableEventPrevious(tbApartados);
    		reasignarFocoBtDesplazamiento();
    		break;
    	case "ACCION_TABLA_SIGUIENTE_REGISTRO":
    		actionTableEventNext(tbApartados);
    		reasignarFocoBtDesplazamiento();
    		break;
    	case "ACCION_TABLA_ULTIMO_REGISTRO":
    		actionTableEventLast(tbApartados);
    		reasignarFocoBtDesplazamiento();
    		break;
    	case "ACCION_TABLA_BORRAR_REGISTRO":
    		cancelarApartado();
    		break;
    	case "ACCION_TABLA_VER_REGISTRO":
    		verApartado();
    		initializeFocus();
    		break;
    	case "ACCION_TABLA_NUEVO_APARTADO":
    		crearApartado();
    		break;
    	case "ACCION_TABLA_FINALIZAR_APARTADO":
    		finalizarApartado();
    		break;
    	case "ACCION_TABLA_EDITAR_APARTADO":
    		editarApartado();
    		initializeFocus();
    		break;
    	default:
    		log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
    		break;
    	}
    }
    
    protected void reasignarFocoBtDesplazamiento(){
    	
    	if(tbApartados.getSelectionModel().isEmpty()){
    		tfApartado.requestFocus();
    	}
    	else{
    		tbApartados.requestFocus();
    	}
    }
   
    protected List<ButtonConfigurationBean> cargarAccionesTabla() {
        List<ButtonConfigurationBean> listaAcciones = new ArrayList<>();
//        listaAcciones.add(new ConfiguracionBotonBean("icons/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
//        listaAcciones.add(new ConfiguracionBotonBean("icons/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
//        listaAcciones.add(new ConfiguracionBotonBean("icons/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
//        listaAcciones.add(new ConfiguracionBotonBean("icons/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        listaAcciones.add(new ButtonConfigurationBean("icons/view.png", null, null, "ACCION_TABLA_VER_REGISTRO", "REALIZAR_ACCION"));
        listaAcciones.add(new ButtonConfigurationBean("icons/row_edit.png", null, null, "ACCION_TABLA_EDITAR_APARTADO", "REALIZAR_ACCION"));
        listaAcciones.add(new ButtonConfigurationBean("icons/row-plus.png", null, null, "ACCION_TABLA_NUEVO_APARTADO", "REALIZAR_ACCION"));
        listaAcciones.add(new ButtonConfigurationBean("icons/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO", "REALIZAR_ACCION")); //"Delete"
        listaAcciones.add(new ButtonConfigurationBean("icons/hands.png", null, null, "ACCION_TABLA_FINALIZAR_APARTADO", "REALIZAR_ACCION"));
        return listaAcciones;
    }
    
	protected LineaApartadoDto getLineaSeleccionada() {
		LineaApartadoDto linea = tbApartados.getSelectionModel().getSelectedItem();
		if (linea == null) {
			tbApartados.getSelectionModel().selectFirst();
			linea = tbApartados.getSelectionModel().getSelectedItem();
		}else{
			tbApartados.getSelectionModel().select(linea);
		}
		lastSelected = linea;
		lastSelectedIndex = lineas.indexOf(lastSelected);
		return linea;
	}
	
	protected void seleccionarSiguienteLinea() {
		if(lastSelectedIndex == 0){
			tbApartados.getSelectionModel().select(0);
		}else{
			int index = lastSelectedIndex;
			if(index+1 >= tbApartados.getItems().size()){
				tbApartados.getSelectionModel().select(index-1);
			}else{
				tbApartados.getSelectionModel().select(index+1);
			}
		}
		getLineaSeleccionada();
	}
    
    public void accionBtBuscar(){
    	log.trace("accionBtBuscar()");
    	
    	String cliente = tfCliente.getText();
    	String apartado = tfApartado.getText();
    	
    	frBusquedaApartado.setNumApartado(apartado.isEmpty()?"0":apartado);
    	
    	if(validarDatosFormulario()){
    		
    		Long numApartado = apartado.isEmpty()? null:Long.parseLong(apartado);
    		lineas.clear();
    		
    		if(validarDatosFormulario()){
                new BuscarTask(numApartado, cliente).start();
            }
    	}
    }
    
    protected class BuscarTask extends BackgroundTask<List<ApartadosCabeceraBean>>{

    	Long numApartado;
    	String cliente;
    	
    	public BuscarTask(Long numApartado, String cliente){
    		
    		this.cliente = cliente;
    		this.numApartado = numApartado;
    	}
    	
        @Override
        protected List<ApartadosCabeceraBean> execute() throws Exception {
        	log.debug("BuscarTask call()");
        	return apartadosManager.consultarApartados(numApartado, cliente, cbVerTodo.isSelected());
        }

        @Override
        protected void succeeded() {
        	log.debug("BuscarTask succeeded()");
        	super.succeeded();
        	log.debug("BuscarTask succeeded() super terminado");
            //Ordenamos la lista de tickets obtenida por la fecha de los mismos
            List<ApartadosCabeceraBean> apartados = getValue();
            log.debug("BuscarTask succeeded() list size: " + apartados.size());
            lineas.clear();
            log.debug("BuscarTask succeeded() clear terminado");
            for(ApartadosCabeceraBean apartadoCabecera : apartados){
    			LineaApartadoDto linea = new LineaApartadoDto(apartadoCabecera);
    			lineas.add(linea);
    		}
            log.debug("BuscarTask succeeded() for terminado");
            Platform.runLater(new Runnable() {
				public void run() {
					tbApartados.getSelectionModel().select(searchLastSelectedIndex());
					tbApartados.scrollTo(tbApartados.getSelectionModel().getSelectedIndex());
				}
			});
			log.debug("BuscarTask succeeded() focusComponent");
    		log.debug("BuscarTask succeeded() fin");
        }

        @Override
        protected void failed() {
        	log.debug("BuscarTask failed()");
            DialogWindowComponent.openErrorWindow(getStage(), getCMZException().getMessage(), getCMZException());
            log.debug("BuscarTask crearVentanaError terminado()");
            super.failed();
            log.debug("BuscarTask failed() super terminado");
            log.debug("BuscarTask failed() fin");
        }

		@Override
		public void start() {
			super.start();
			log.debug("BuscarTask start()");
		}

		@Override
		protected void running() {
			super.running();
			log.debug("BuscarTask running()");
		}
        
        
    }
    
    /**
     * Valida los valores introducidos para buscar el apartado
     *
     * @return
     */
    protected boolean validarDatosFormulario() {
    	log.trace("validarDatosFormulario()");

        boolean valido;

        // Limpiamos los errores que pudiese tener el formulario
        frBusquedaApartado.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<FormularioBusquedaApartadoBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBusquedaApartado);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioBusquedaApartadoBean> next = constraintViolations.iterator().next();
            frBusquedaApartado.setErrorStyle(next.getPropertyPath(), true);
            frBusquedaApartado.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            valido = false;
        }
        else {
            valido = true;
        }

        return valido;
    }
    
    public void editarApartado(){
    	log.trace("editarApartado()");
    	
    	sceneData.clear();
    	sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, ticketManager);
    	sceneData.put(DetalleApartadosController.PARAMETRO_APARTADO_MGR, apartadosManager);
    	sceneData.put(DetalleApartadosController.INICIALIZAR_PANTALLA, "S");

    	LineaApartadoDto lineaApartado = getLineaSeleccionada();
    	if( lineaApartado !=null){

    		ApartadosCabeceraBean apartado = lineaApartado.getApartado();

    		if(apartado.getEstadoApartado() == ApartadosCabeceraBean.ESTADO_DISPONIBLE){
    			sceneData.put(DetalleApartadosController.PARAMETRO_EDITAR_APARTADO, true);

    			sceneData.put(DetalleApartadosController.PARAMETRO_APARTADO, apartado);
    			openScene(DetalleApartadosController.class);
    		}
    		else{
    			DialogWindowComponent.openWarnWindow(I18N.getText("El apartado seleccionado no se puede editar."), getStage());
    		}
    	}
    }
    
    public void crearApartado(){
    	log.trace("crearApartado()");
    	
    	sceneData.clear();
    	sceneData.put(DetalleApartadosController.PARAMETRO_APARTADO_MGR, apartadosManager);
    	sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, ticketManager);
    	sceneData.put(DetalleApartadosController.PARAMETRO_EDITAR_APARTADO, true);
    	sceneData.put(DetalleApartadosController.PARAMETRO_NUEVO_APARTADO, true);
    	sceneData.put(DetalleApartadosController.INICIALIZAR_PANTALLA, "S");
    	sceneData.put(DetalleApartadosController.PARAMETRO_EVENTO_BUSQUEDA, new EventHandler<Event>() {
    		@Override
    		public void handle(Event arg0) {
    			lastSelectedIndex = 0;
    			lastSelected = null;
    			accionBtBuscar();
    		}
    	}
    			);

    	openScene(DetalleApartadosController.class, new SceneCallback<Void>() {
			
			@Override
			public void onSuccess(Void callbackData) {
				initializeFocus();
			}
		});
    }

    public void verApartado(){
    	log.trace("verApartado()");

    	sceneData.clear();
    	sceneData.put(BasketItemizationControllerAbstract.BASKET_KEY, ticketManager);
    	sceneData.put(DetalleApartadosController.PARAMETRO_APARTADO_MGR, apartadosManager);

    	LineaApartadoDto lineaApartado = getLineaSeleccionada();
    	if( lineaApartado !=null){

    		ApartadosCabeceraBean apartado = lineaApartado.getApartado();
    	
    		sceneData.put(DetalleApartadosController.PARAMETRO_APARTADO, apartado);
    		sceneData.put(DetalleApartadosController.INICIALIZAR_PANTALLA, "S");
    		int elemSeleccionado = tbApartados.getSelectionModel().getSelectedIndex();
//    		TODO: MSB Revisar apartados
//			openScene(DetalleApartadosController.class);
			tbApartados.getSelectionModel().select(elemSeleccionado);    	
    	}
    }
    
    public void cancelarApartado(){
    	log.trace("cancelarApartado()");

    	LineaApartadoDto lineaApartado = getLineaSeleccionada();
    	if(lineaApartado !=null){
    		ApartadosCabeceraBean apartado = lineaApartado.getApartado();
    		if(apartado.getEstadoApartado() != ApartadosCabeceraBean.ESTADO_DISPONIBLE){
    			DialogWindowComponent.openWarnWindow(I18N.getText("El apartado no se puede cancelar."), getStage());
    		}
    		else{
    			if(DialogWindowComponent.openConfirmWindow(I18N.getText("Se cancelará el apartado. ¿Está seguro?"), this.getStage())){
//    	    		TODO: MSB Revisar apartados
//    				if(apartado.getSaldoCliente().compareTo(BigDecimal.ZERO)>0){
//    					sceneData.put(VueltaController.CLAVE_OCULTAR_BOTONES, true);
////    					openScene(VueltaController.class);
//    					sceneData.remove(VueltaController.CLAVE_OCULTAR_BOTONES);
//    				}
//    				if(!sceneData.containsKey(VueltaController.CLAVE_CANCELADO)){
//    					boolean hayLineasVendidas = apartadosManager.cancelarLineas(apartado);
//        				if(hayLineasVendidas){
//        					apartado.setEstadoApartado(ApartadosCabeceraBean.ESTADO_FINALIZADO);
//        				}
//        				else{
//        					apartado.setEstadoApartado(ApartadosCabeceraBean.ESTADO_CANCELADO);
//        				}
//        				apartadosManager.cargarApartado(apartado);  				
//        				apartadosManager.getTicketApartado().calcularTotales();
//                 	    apartadosManager.getTicketApartado().getCabecera().setImporteTotalApartado(apartadosManager.getTicketApartado().getTotalServido());       	   
//    					apartadosService.actualizarCabeceraApartado(apartado);
//        				if(apartado.getSaldoCliente().compareTo(BigDecimal.ZERO)>0){
//        					String codMedioPagoSeleccionado = (String)sceneData.remove(VueltaController.CLAVE_PARAMETRO_SALIDA_VUELTA);
//        					MedioPagoBean mp = SpringContext.getBean(MediosPagosServiceImpl.class).getMedioPago(codMedioPagoSeleccionado);
//        					if(mp == null){
//        						mp = MediosPagosServiceImpl.medioPagoDefecto;
//        					}
//        					try {
//        						apartadosManager.grabarMovimientoDevolucion(apartado, apartado.getSaldoCliente(), mp);
//        					} catch (TicketsServiceException e) {
//        						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error grabando el movimiento de caja."), getStage());
//        					} 
//    						
//        				}
//        				seleccionarSiguienteLinea();
//        				accionBtBuscar();
//    				}
//    				else{
//    					sceneData.remove(VueltaController.CLAVE_CANCELADO);
//    				}
    				
    			}
    		}
    	}
    }

    public void finalizarApartado(){
    	log.trace("finalizarApartado()");
    	boolean bLineasFinalizadas = false;
    	
    	Catalog catalog = applicationSession.getValidCatalog();

    	final LineaApartadoDto lineaApartado = getLineaSeleccionada();
    	if(lineaApartado !=null ){
    		final ApartadosCabeceraBean detalleApartado = lineaApartado.getApartado();
    		if(detalleApartado.getEstadoApartado()!=ApartadosCabeceraBean.ESTADO_DISPONIBLE){
    			DialogWindowComponent.openWarnWindow(I18N.getText("El apartado no está disponible para su venta."), getStage());
    		}
    		else{
    			
    			apartadosManager.cargarApartado(detalleApartado);
    			
    			if(apartadosManager.getTicketApartado().getTotalPendiente().compareTo(BigDecimal.ZERO)>0){
					DialogWindowComponent.openWarnWindow(I18N.getText("El coste del apartado no está cubierto."), getStage());
					return;
				}
    			
    			if(DialogWindowComponent.openConfirmWindow(I18N.getText("Se va a proceder a finalizar el apartado. ¿Desea continuar?"), getStage())){
    				apartadosManager.cargarApartado(detalleApartado);
   					
					final List<ApartadosDetalleBean> articulos = new ArrayList<>();
					
					for(ApartadosDetalleBean articulo : apartadosManager.getTicketApartado().getArticulos()){
						if(articulo.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_DISPONIBLE){
							articulos.add(articulo);
						}
						else{ 
							if(articulo.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_FINALIZADO){
								bLineasFinalizadas = true;
							}
						}
					}
					
					apartadosManager.getTicketApartado().calcularTotales();
              	    final BigDecimal importeTotal = apartadosManager.getTicketApartado().getTotalServido().add(apartadosManager.getTicketApartado().getImporteTotal());
              	    //detalleApartado.setImporteTotalApartado(apartadosManager.getTicketApartado().getTotalServido().add(apartadosManager.getTicketApartado().getImporteTotal()));
              	    //apartadosManager.getTicketApartado().getCabecera().setImporteTotalApartado(apartadosManager.getTicketApartado().getTotalServido().add(apartadosManager.getTicketApartado().getImporteTotal()));
              	    
					for (ApartadosDetalleBean detalleApartadoBean : apartadosManager.getTicketApartado().getArticulos()) {
						if (detalleApartadoBean.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_DISPONIBLE) {
							try {
								CatalogItemDetail articuloBean = catalog.getCatalogItemService().findByBarcode(detalleApartadoBean.getCodart());
								
								if (articuloBean.getSerialNumbersActive()) {
//									ticketManager.createBasketTransaction();
									ticketManager.updateCustomer(modelMapper.map(apartadosManager.getCliente(), BasketCustomer.class));
									BasketItem lineaTicket = null; //ticketManager.createAndInsertBasketItem(detalleApartadoBean.getCodart(), detalleApartadoBean.getDesglose1(), detalleApartadoBean.getDesglose2(), detalleApartadoBean.getCantidad());

									asignarNumerosSerie(lineaTicket, detalleApartadoBean);

									if (detalleApartadoBean.getNumerosSerie() == null || (detalleApartadoBean.getNumerosSerie() != null && detalleApartadoBean.getNumerosSerie().isEmpty()) || (detalleApartadoBean.getNumerosSerie() != null && detalleApartadoBean.getNumerosSerie().size() < detalleApartadoBean.getCantidad().setScale(0, RoundingMode.HALF_UP).abs().intValue())) {
										DialogWindowComponent.openWarnWindow(I18N.getText("Debe indicar todos los números de serie para continuar."), getStage());
										return;
									}
								}
							}
							catch (Exception e) {
								log.error("accionAceptar() - No se han podido procesar los números de serie: " + e.getMessage());
								DialogWindowComponent.openErrorWindow(getStage(), I18N.getText("Ha habido un error durante el procesado de los números de serie"), e);
								return;
							}
						}
					}
                        
                    try
                    {                  	    
                    	ticketManager.generarVentaDeApartados(apartadosManager.getTicketApartado().getArticulos(), apartadosManager.getCliente(), detalleApartado);
                    	
                    	if(articulos.isEmpty()){
                    	   //si no hay lineas para servir y no hay ninguna sevida el estado del apartado pasará a cancelado.
                    	   if(bLineasFinalizadas){
                    		   apartadosManager.getTicketApartado().getCabecera().setEstadoApartado(ApartadosCabeceraBean.ESTADO_FINALIZADO);
                    	   }
                    	   else{
                    		   apartadosManager.getTicketApartado().getCabecera().setEstadoApartado(ApartadosCabeceraBean.ESTADO_CANCELADO);
                    	   }
                    	   
	    				   apartadosManager.registrarVentaApartado(ticketManager.getBasketTransaction().getTotals().getTotalToPay());
	    				   
//	    				   ticketManager.createBasketTransaction();
							
	    				    detalleApartado.setImporteTotalApartado(importeTotal);
						    apartadosService.actualizarCabeceraApartado(detalleApartado);			
//				    		TODO: MSB Revisar apartados
//	        			    if(detalleApartado.getSaldoCliente().compareTo(BigDecimal.ZERO)>0){
//	        			    	openScene(VueltaController.class);
//					            String codMedioPagoSeleccionado = (String)sceneData.remove(VueltaController.CLAVE_PARAMETRO_SALIDA_VUELTA);
//					            MedioPagoBean mp = SpringContext.getBean(MediosPagosServiceImpl.class).getMedioPago(codMedioPagoSeleccionado);
//	        					if(mp == null){
//	        						mp = MediosPagosServiceImpl.medioPagoDefecto;
//	        					}
//	        					try {
//	        						apartadosManager.grabarMovimientoDevolucion(detalleApartado, detalleApartado.getSaldoCliente(), mp);
//	        					} catch (TicketsServiceException e) {
//	        						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error grabando el movimiento de caja."), getStage());
//	        					} 
//	        				 }
	        				 
	        			     seleccionarSiguienteLinea();
	        				 accionBtBuscar();
						}
                    	else{      
                    		ticketManager.setCabeceraApartado(apartadosManager.getTicketApartado().getCabecera());
                    		
                    		new SaveDocumentBackgroundTask(ticketManager, new SaveDocumentCallback() {	
								@Override
								public void onSucceeded(BasketPromotable<?> basketTransaction, BasketDocumentIssued<?> documentIssued) {
								   apartadosManager.getTicketApartado().getCabecera().setEstadoApartado(ApartadosCabeceraBean.ESTADO_FINALIZADO);
							       //ticketManager.generarVentaDeApartados(apartadosManager.getTicketApartado().getArticulos(), apartadosManager.getCliente(), detalleApartado);
    						   	   apartadosManager.registrarVentaApartado(ticketManager.getBasketTransaction().getTotals().getTotalToPay());
							       apartadosManager.actualizarEstadoLineasVendidas(ticketManager.getBasketTransaction().getBasketUid(),articulos);// (ticketManager.getTicket().getCabecera().getUidTicket(), apartadosManager.getTicketApartado().getArticulos());
//						    		TODO: MSB Revisar apartados
//							       if(ticketManager.getBasketTransaction().getCabecera().getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO)>0){
//							            if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Desea asignar datos de facturación?"), getStage())){
//											sceneData.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
//											openScene(FacturaController.class);
//										}
//							       
//										Map<String,Object> mapaParametros= new HashMap<String,Object>();
//										mapaParametros.put("ticket",ticketManager.getBasketTransaction());
//										mapaParametros.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
//										mapaParametros.put("apartado", lineaApartado.getApartado().getIdApartado().toString());
//
//										try{
//											ServicioImpresion.imprimir(ticketManager.getBasketTransaction().getCabecera().getFormatoImpresion(), mapaParametros);
//										} catch (DispositivoException e) {
//											log.error("Error imprimiendo el documento de venta.", e);
//										}
//									}
//									ticketManager.finalizarTicket();
//								
//									detalleApartado.setImporteTotalApartado(importeTotal);
//								    apartadosService.actualizarCabeceraApartado(detalleApartado);			
//									
//									if (detalleApartado.getSaldoCliente().compareTo(BigDecimal.ZERO) > 0) {
//										openScene(VueltaController.class);
//										String codMedioPagoSeleccionado = (String)sceneData.remove(VueltaController.CLAVE_PARAMETRO_SALIDA_VUELTA);
//										MedioPagoBean mp = SpringContext.getBean(MediosPagosServiceImpl.class).getMedioPago(codMedioPagoSeleccionado);
//										if (mp == null) {
//											mp = MediosPagosServiceImpl.medioPagoDefecto;
//										}
//										try {
//											apartadosManager.grabarMovimientoDevolucion(detalleApartado, detalleApartado.getSaldoCliente(), mp);
//										} catch (TicketsServiceException e) {
//											VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error grabando el movimiento de caja."), getStage());
//										}
//									}
//									
//									seleccionarSiguienteLinea();
//									accionBtBuscar();
								}
								
								@Override
								public void onFailure(Exception e) {
									String msg = I18N.getText("Error al salvar el ticket.");
									DialogWindowComponent.openErrorWindow(getStage(), msg, e);
								}
							}).start();
							}
						}catch (Exception e){// (LineaTicketException e) {
	    					log.error("Error procesando la venta", e);
	    					DialogWindowComponent.openErrorWindow(getStage(), e.getMessage(), e);
						}
					}
				}
			}
		}

	public void accionBuscarIntro(KeyEvent e){
		log.trace("accionBuscarIntro()");
		if(e.getCode() == KeyCode.ENTER){
			accionBtBuscar();
		}
	}

	/**
	 * @param event 
	 */
	public void aceptarArticuloDobleClick(MouseEvent event) {
		log.debug("aceptarArticuloDobleClick() - Acción aceptar");
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				verApartado();
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

	protected void asignarNumerosSerie(BasketItem linea, ApartadosDetalleBean detalleApartado){
//		sceneData.put(SerialNumberController.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, new ArrayList<String>());
//		sceneData.put(SerialNumberController.PARAM_SERIAL_NUMBER_LINE, linea);
//		openScene(NumerosSerieController.class);
//        List<String> numerosSerie = (List<String>) sceneData.get(NumerosSerieController.PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS);
//        detalleApartado.setNumerosSerie(numerosSerie);
	}
}
