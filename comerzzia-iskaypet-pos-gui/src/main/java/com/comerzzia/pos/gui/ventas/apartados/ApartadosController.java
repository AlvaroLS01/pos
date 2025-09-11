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
package com.comerzzia.pos.gui.ventas.apartados;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.apartados.detalle.DetalleApartadosController;
import com.comerzzia.pos.gui.ventas.apartados.detalle.DetalleApartadosView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager.SalvarTicketCallback;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieView;
import com.comerzzia.pos.gui.ventas.tickets.factura.FacturaView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.vuelta.VueltaController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.vuelta.VueltaView;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.apartados.ApartadosService;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class ApartadosController extends Controller implements Initializable, IContenedorBotonera {

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
    protected BotoneraComponent botoneraAccionesTabla;

    protected ObservableList<LineaApartadoGui> lineas;
    
    protected LineaApartadoGui lastSelected;
    protected int lastSelectedIndex;
    
    public ApartadosManager apartadosManager;
    
    public TicketManager ticketManager;
    
    @FXML
    protected TableView<LineaApartadoGui> tbApartados;
    
    @FXML
    protected Label lbError;
    
    @FXML
    protected TableColumn tcFecha, tcImporte, tcCliente, tcNumApartado, tcSaldoCliente;
    
    protected FormularioBusquedaApartadoBean frBusquedaApartado;
    
    @Autowired
    private ApartadosService apartadosService;
    @Autowired
    private VariablesServices variablesServices;
    @Autowired
    private Sesion sesion;
    @Autowired
    private ArticulosService articulosService;
	
    final IVisor visor = Dispositivos.getInstance().getVisor();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	frBusquedaApartado = SpringContext.getBean(FormularioBusquedaApartadoBean.class);
    	frBusquedaApartado.setFormField("numApartado", tfApartado);
    	
    	lineas = FXCollections.observableArrayList(new ArrayList<LineaApartadoGui>());
    	
    	tbApartados.setPlaceholder(new Label(""));
    	tbApartados.setItems(lineas);
    	
    	tcNumApartado.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcNumApartado", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
    	tcCliente.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcCliente", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFecha("tbLineas", "tcFecha", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcImporte", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcSaldoCliente.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcSaldoCliente", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
    	
		tcNumApartado.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoGui, Long>, ObservableValue<Long>>() {
            @Override
            public ObservableValue<Long> call(CellDataFeatures<LineaApartadoGui, Long> cdf) {
                return cdf.getValue().getNumApartadoProperty();
            }
        });
		tcCliente.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<LineaApartadoGui, String> cdf) {
                return cdf.getValue().getClienteProperty();
            }
        });
		tcFecha.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoGui, Date>, ObservableValue<Date>>() {
			@Override
            public ObservableValue<Date> call(CellDataFeatures<LineaApartadoGui, Date> cdf) {
                return cdf.getValue().getFechaProperty();
            }
        });
		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(CellDataFeatures<LineaApartadoGui, BigDecimal> cdf) {
                return cdf.getValue().getImporteProperty();
            }
        });
		tcSaldoCliente.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaApartadoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(CellDataFeatures<LineaApartadoGui, BigDecimal> cdf) {
                return cdf.getValue().getSaldoClienteProperty();
            }
        });
		
		tbApartados.setRowFactory(new Callback<TableView<LineaApartadoGui>, TableRow<LineaApartadoGui>>() {

        	@Override
        	public TableRow<LineaApartadoGui> call(TableView<LineaApartadoGui> p) {
        		final TableRow<LineaApartadoGui> row = new TableRow<LineaApartadoGui>() {
        			@Override
        			protected void updateItem(LineaApartadoGui linea, boolean empty){
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
            
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new BotoneraComponent(1, 9, this, listaAccionesAccionesTabla, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotonera.getChildren().add(botoneraAccionesTabla);
            
        } catch (CargarPantallaException ex) {
            log.error("inicializarComponentes() - Error cargando pantalla de apartados");
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla de apartados."), getStage());
        }
    }

    @Override
    public void initializeForm() throws InitializeGuiException {
    	
    	try {
	        comprobarAperturaPantalla();
        }
        catch (CajasServiceException | CajaEstadoException e) {
        	log.error("initializeForm() - Error inicializando pantalla:" + e.getMessageI18N(), e);
			throw new InitializeGuiException(e.getMessageI18N(), e);
        }
    	
    	frBusquedaApartado.clearErrorStyle();
    	tfApartado.setText("");
    	tfCliente.setText("");
    	
    	lineas.clear();
    	accionBtBuscar();
    	
    	visor.limpiar();
    }
    
	/**
	 * Realiza las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
	 * @throws CajasServiceException
	 * @throws CajaEstadoException
	 * @throws InitializeGuiException
	 */
	protected void comprobarAperturaPantalla() throws CajasServiceException, CajaEstadoException, InitializeGuiException {
	    if (!sesion.getSesionCaja().isCajaAbierta()) {
	    	Boolean aperturaAutomatica = variablesServices.getVariableAsBoolean(VariablesServices.CAJA_APERTURA_AUTOMATICA, true);
	    	if(aperturaAutomatica){
	    		VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No hay caja abierta. Se abrirá automáticamente."), getStage());
	    		sesion.getSesionCaja().abrirCajaAutomatica();
	    	}else{
	    		VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."), getStage());
	    		throw new InitializeGuiException(false);
	    	}
	    }
	    
	    if(!ticketManager.comprobarCierreCajaDiarioObligatorio()){
	    	String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
	    	String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
	    	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual), getStage());
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
    	for (LineaApartadoGui linea : lineas) {
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
    	ticketManager = SpringContext.getBean(TicketManager.class);
    	apartadosManager = SpringContext.getBean(ApartadosManager.class);
    }
    
    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
    	log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
    	switch (botonAccionado.getClave()) {
    	case "ACCION_TABLA_PRIMER_REGISTRO":
    		accionTablaPrimerRegistro(tbApartados);
    		reasignarFocoBtDesplazamiento();
    		break;
    	case "ACCION_TABLA_ANTERIOR_REGISTRO":
    		accionTablaIrAnteriorRegistro(tbApartados);
    		reasignarFocoBtDesplazamiento();
    		break;
    	case "ACCION_TABLA_SIGUIENTE_REGISTRO":
    		accionTablaIrSiguienteRegistro(tbApartados);
    		reasignarFocoBtDesplazamiento();
    		break;
    	case "ACCION_TABLA_ULTIMO_REGISTRO":
    		accionTablaUltimoRegistro(tbApartados);
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
   
    protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/view.png", null, null, "ACCION_TABLA_VER_REGISTRO", "REALIZAR_ACCION"));
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row_edit.png", null, null, "ACCION_TABLA_EDITAR_APARTADO", "REALIZAR_ACCION"));
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row-plus.png", null, null, "ACCION_TABLA_NUEVO_APARTADO", "REALIZAR_ACCION"));
        listaAcciones.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO", "REALIZAR_ACCION")); //"Delete"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/hands.png", null, null, "ACCION_TABLA_FINALIZAR_APARTADO", "REALIZAR_ACCION"));
        return listaAcciones;
    }
    
	protected LineaApartadoGui getLineaSeleccionada() {
		LineaApartadoGui linea = tbApartados.getSelectionModel().getSelectedItem();
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
        protected List<ApartadosCabeceraBean> call() throws Exception {
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
    			LineaApartadoGui linea = new LineaApartadoGui(apartadoCabecera);
    			lineas.add(linea);
    		}
            log.debug("BuscarTask succeeded() for terminado");
            Platform.runLater(new Runnable() {
				public void run() {
					tbApartados.getSelectionModel().select(searchLastSelectedIndex());
					tbApartados.scrollTo(tbApartados.getSelectionModel().getSelectedIndex());
				}
			});
			log.debug("BuscarTask succeeded() initializeFocus");
    		log.debug("BuscarTask succeeded() fin");
        }

        @Override
        protected void failed() {
        	log.debug("BuscarTask failed()");
            VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessageI18N(), getCMZException());
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
    	
    	getDatos().clear();
    	getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
    	getDatos().put(DetalleApartadosController.PARAMETRO_APARTADO_MGR, apartadosManager);
    	getDatos().put(DetalleApartadosController.INICIALIZAR_PANTALLA, "S");

    	LineaApartadoGui lineaApartado = getLineaSeleccionada();
    	if( lineaApartado !=null){

    		ApartadosCabeceraBean apartado = lineaApartado.getApartado();

    		if(apartado.getEstadoApartado() == ApartadosCabeceraBean.ESTADO_DISPONIBLE){
    			getDatos().put(DetalleApartadosController.PARAMETRO_EDITAR_APARTADO, true);

    			getDatos().put(DetalleApartadosController.PARAMETRO_APARTADO, apartado);
    			try {
    				getView().changeSubView(DetalleApartadosView.class, getDatos());
    			} catch (InitializeGuiException e) {
    				log.error("accionCambiarArticulo() - Error abriendo ventana", e);
    				VentanaDialogoComponent.crearVentanaError(getApplication().getStage(), I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), e);
    			}
    		}
    		else{
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El apartado seleccionado no se puede editar."), getStage());
    		}
    	}
    }
    
    public void crearApartado(){
    	log.trace("crearApartado()");
    	
    	getDatos().clear();
    	getDatos().put(DetalleApartadosController.PARAMETRO_APARTADO_MGR, apartadosManager);
    	getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
    	getDatos().put(DetalleApartadosController.PARAMETRO_EDITAR_APARTADO, true);
    	getDatos().put(DetalleApartadosController.PARAMETRO_NUEVO_APARTADO, true);
    	getDatos().put(DetalleApartadosController.INICIALIZAR_PANTALLA, "S");
    	getDatos().put(DetalleApartadosController.PARAMETRO_EVENTO_BUSQUEDA, new EventHandler() {
    		@Override
    		public void handle(Event arg0) {
    			lastSelectedIndex = 0;
    			lastSelected = null;
    			accionBtBuscar();
    		}
    	}
    			);

    	try {
			getView().changeSubView(DetalleApartadosView.class, getDatos());
			initializeFocus();
    	} catch (InitializeGuiException e) {
			log.error("accionCambiarArticulo() - Error abriendo ventana", e);
			VentanaDialogoComponent.crearVentanaError(getApplication().getStage(), "Error cargando pantalla. Para mas información consulte el log.", e);
    	}
    }

    public void verApartado(){
    	log.trace("verApartado()");

    	getDatos().clear();
    	getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
    	getDatos().put(DetalleApartadosController.PARAMETRO_APARTADO_MGR, apartadosManager);

    	LineaApartadoGui lineaApartado = getLineaSeleccionada();
    	if( lineaApartado !=null){

    		ApartadosCabeceraBean apartado = lineaApartado.getApartado();
    	
    		getDatos().put(DetalleApartadosController.PARAMETRO_APARTADO, apartado);
    		getDatos().put(DetalleApartadosController.INICIALIZAR_PANTALLA, "S");
    		try {
    			int elemSeleccionado = tbApartados.getSelectionModel().getSelectedIndex();
    			getView().changeSubView(DetalleApartadosView.class, getDatos());
    			tbApartados.getSelectionModel().select(elemSeleccionado);
    		} catch (InitializeGuiException e) {
    			log.error("accionCambiarArticulo() - Error abriendo ventana", e);
    			VentanaDialogoComponent.crearVentanaError(getApplication().getStage(), I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), e);
    		}    	
    	}
    }
    
    public void cancelarApartado(){
    	log.trace("cancelarApartado()");

    	LineaApartadoGui lineaApartado = getLineaSeleccionada();
    	if(lineaApartado !=null){
    		ApartadosCabeceraBean apartado = lineaApartado.getApartado();
    		if(apartado.getEstadoApartado() != ApartadosCabeceraBean.ESTADO_DISPONIBLE){
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El apartado no se puede cancelar."), getStage());
    		}
    		else{
    			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se cancelará el apartado. ¿Está seguro?"), this.getStage())){

    				if(apartado.getSaldoCliente().compareTo(BigDecimal.ZERO)>0){
    					getDatos().put(VueltaController.CLAVE_OCULTAR_BOTONES, true);
    					getApplication().getMainView().showModalCentered(VueltaView.class, getDatos(), getStage());
    					getDatos().remove(VueltaController.CLAVE_OCULTAR_BOTONES);
    				}
    				if(!getDatos().containsKey(VueltaController.CLAVE_CANCELADO)){
    					boolean hayLineasVendidas = apartadosManager.cancelarLineas(apartado);
        				if(hayLineasVendidas){
        					apartado.setEstadoApartado(ApartadosCabeceraBean.ESTADO_FINALIZADO);
        				}
        				else{
        					apartado.setEstadoApartado(ApartadosCabeceraBean.ESTADO_CANCELADO);
        				}
        				apartadosManager.cargarApartado(apartado);  				
        				apartadosManager.getTicketApartado().calcularTotales();
                 	    apartadosManager.getTicketApartado().getCabecera().setImporteTotalApartado(apartadosManager.getTicketApartado().getTotalServido());       	   
    					apartadosService.actualizarCabeceraApartado(apartado);
        				if(apartado.getSaldoCliente().compareTo(BigDecimal.ZERO)>0){
        					String codMedioPagoSeleccionado = (String)getDatos().remove(VueltaController.CLAVE_PARAMETRO_SALIDA_VUELTA);
        					MedioPagoBean mp = SpringContext.getBean(MediosPagosService.class).getMedioPago(codMedioPagoSeleccionado);
        					if(mp == null){
        						mp = MediosPagosService.medioPagoDefecto;
        					}
        					try {
        						apartadosManager.grabarMovimientoDevolucion(apartado, apartado.getSaldoCliente(), mp);
        					} catch (TicketsServiceException e) {
        						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error grabando el movimiento de caja."), getStage());
        					} 
    						
        				}
        				seleccionarSiguienteLinea();
        				accionBtBuscar();
    				}
    				else{
    					getDatos().remove(VueltaController.CLAVE_CANCELADO);
    				}
    				
    			}
    		}
    	}
    }

    public void finalizarApartado(){
    	log.trace("finalizarApartado()");
    	boolean bLineasFinalizadas = false;

    	final LineaApartadoGui lineaApartado = getLineaSeleccionada();
    	if(lineaApartado !=null ){
    		final ApartadosCabeceraBean detalleApartado = lineaApartado.getApartado();
    		if(detalleApartado.getEstadoApartado()!=ApartadosCabeceraBean.ESTADO_DISPONIBLE){
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El apartado no está disponible para su venta."), getStage());
    		}
    		else{
    			
    			apartadosManager.cargarApartado(detalleApartado);
    			
    			if(apartadosManager.getTicketApartado().getTotalPendiente().compareTo(BigDecimal.ZERO)>0){
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El coste del apartado no está cubierto."), getStage());
					return;
				}
    			
    			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se va a proceder a finalizar el apartado. ¿Desea continuar?"), getStage())){
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
								ArticuloBean articuloBean = articulosService.consultarArticulo(detalleApartadoBean.getCodart());
								if (articuloBean.getNumerosSerie()) {
									ticketManager.nuevoTicket();
									ticketManager.getTicket().setCliente(apartadosManager.getCliente());
									LineaTicket lineaTicket = ticketManager.nuevaLineaArticulo(detalleApartadoBean.getCodart(), detalleApartadoBean.getDesglose1(), detalleApartadoBean.getDesglose2(), detalleApartadoBean.getCantidad(), null);

									asignarNumerosSerie(lineaTicket, detalleApartadoBean);

									if (detalleApartadoBean.getNumerosSerie() == null || (detalleApartadoBean.getNumerosSerie() != null && detalleApartadoBean.getNumerosSerie().isEmpty()) || (detalleApartadoBean.getNumerosSerie() != null && detalleApartadoBean.getNumerosSerie().size() < detalleApartadoBean.getCantidad().setScale(0, RoundingMode.HALF_UP).abs().intValue())) {
										VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe indicar todos los números de serie para continuar."), getStage());
										return;
									}
								}
							}
							catch (Exception e) {
								log.error("accionAceptar() - No se han podido procesar los números de serie: " + e.getMessage());
								VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error durante el procesado de los números de serie"), e);
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
                    	   
	    				   apartadosManager.registrarVentaApartado(ticketManager.getTicket().getTotales().getTotalAPagar());
	    				   
	    				   ticketManager.finalizarTicket();
							
	    				    detalleApartado.setImporteTotalApartado(importeTotal);
						    apartadosService.actualizarCabeceraApartado(detalleApartado);			
							
	        			    if(detalleApartado.getSaldoCliente().compareTo(BigDecimal.ZERO)>0){
	        					getApplication().getMainView().showModalCentered(VueltaView.class, getDatos(), getStage());
					            String codMedioPagoSeleccionado = (String)getDatos().remove(VueltaController.CLAVE_PARAMETRO_SALIDA_VUELTA);
					            MedioPagoBean mp = SpringContext.getBean(MediosPagosService.class).getMedioPago(codMedioPagoSeleccionado);
	        					if(mp == null){
	        						mp = MediosPagosService.medioPagoDefecto;
	        					}
	        					try {
	        						apartadosManager.grabarMovimientoDevolucion(detalleApartado, detalleApartado.getSaldoCliente(), mp);
	        					} catch (TicketsServiceException e) {
	        						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error grabando el movimiento de caja."), getStage());
	        					} 
	        				 }
	        				 
	        			     seleccionarSiguienteLinea();
	        				 accionBtBuscar();
						}
                    	else{      
                    		    ticketManager.salvarTicket(getStage(), new SalvarTicketCallback() {	
								@Override
								public void onSucceeded() {
								   apartadosManager.getTicketApartado().getCabecera().setEstadoApartado(ApartadosCabeceraBean.ESTADO_FINALIZADO);
							       //ticketManager.generarVentaDeApartados(apartadosManager.getTicketApartado().getArticulos(), apartadosManager.getCliente(), detalleApartado);
    						   	   apartadosManager.registrarVentaApartado(ticketManager.getTicket().getTotales().getTotalAPagar());
							       apartadosManager.actualizarEstadoLineasVendidas(ticketManager.getTicket().getCabecera().getUidTicket(),articulos);// (ticketManager.getTicket().getCabecera().getUidTicket(), apartadosManager.getTicketApartado().getArticulos());
							       
							       if(ticketManager.getTicket().getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO)>0){
							            if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Desea asignar datos de facturación?"), getStage())){
											getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
											getApplication().getMainView().showModalCentered(FacturaView.class, getDatos(), getStage());
										}
							       
										Map<String,Object> mapaParametros= new HashMap<String,Object>();
										mapaParametros.put("ticket",ticketManager.getTicket());
										mapaParametros.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
										mapaParametros.put("apartado", lineaApartado.getApartado().getIdApartado().toString());

										try{
											ServicioImpresion.imprimir(ticketManager.getTicket().getCabecera().getFormatoImpresion(), mapaParametros);
										} catch (DeviceException e) {
											log.error("Error imprimiendo el documento de venta.", e);
										}
									}
									ticketManager.finalizarTicket();
								
									detalleApartado.setImporteTotalApartado(importeTotal);
								    apartadosService.actualizarCabeceraApartado(detalleApartado);			
									
									if (detalleApartado.getSaldoCliente().compareTo(BigDecimal.ZERO) > 0) {
										getApplication().getMainView().showModalCentered(VueltaView.class, getDatos(), getStage());
										String codMedioPagoSeleccionado = (String)getDatos().remove(VueltaController.CLAVE_PARAMETRO_SALIDA_VUELTA);
										MedioPagoBean mp = SpringContext.getBean(MediosPagosService.class).getMedioPago(codMedioPagoSeleccionado);
										if (mp == null) {
											mp = MediosPagosService.medioPagoDefecto;
										}
										try {
											apartadosManager.grabarMovimientoDevolucion(detalleApartado, detalleApartado.getSaldoCliente(), mp);
										} catch (TicketsServiceException e) {
											VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error grabando el movimiento de caja."), getStage());
										}
									}
									
									seleccionarSiguienteLinea();
									accionBtBuscar();
								}
								
								@Override
								public void onFailure(Exception e) {
									String msg = I18N.getTexto("Error al salvar el ticket.");
									VentanaDialogoComponent.crearVentanaError(getStage(), msg, e);
								}
							});
							}
						}catch (Exception e){// (LineaTicketException e) {
	    					log.error("Error procesando la venta", e);
	    					VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
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

	@SuppressWarnings("unchecked")
	protected void asignarNumerosSerie(LineaTicket linea, ApartadosDetalleBean detalleApartado){
		getDatos().put(NumerosSerieController.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, new ArrayList<String>());
		getDatos().put(NumerosSerieController.PARAMETRO_LINEA_DOCUMENTO_ACTIVO, linea);
		getApplication().getMainView().showModalCentered(NumerosSerieView.class, getDatos(), getStage());
        List<String> numerosSerie = (List<String>) getDatos().get(NumerosSerieController.PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS);
        detalleApartado.setNumerosSerie(numerosSerie);
	}
}
