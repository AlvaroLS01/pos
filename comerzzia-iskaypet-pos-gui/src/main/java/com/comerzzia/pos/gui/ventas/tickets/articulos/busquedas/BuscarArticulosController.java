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

package com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.imagenArticulo.ImagenArticulo;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.disponibilidadstock.DisponibilidadStockController;
import com.comerzzia.pos.core.gui.disponibilidadstock.DisponibilidadStockModalView;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class BuscarArticulosController extends WindowController implements Initializable, IContenedorBotonera {

    // <editor-fold desc="Declaración de variables">
    private static final Logger log = Logger.getLogger(BuscarArticulosController.class.getName());
    
    @Autowired
    private Sesion sesion;

    public static final String CLAVE_PARAMETRO_ACCION = "accionFacturarArticulo";

    public static final String PARAMETRO_ENTRADA_CLIENTE = "PARAMETRO_ENTRADA_CLIENTE";
    public static final String PARAMETRO_ENTRADA_CODTARIFA = "PARAMETRO_ENTRADA_CODTARIFA";
    public static final String PARAMETRO_ES_STOCK = "PARAMETRO_ES_STOCK";
    public static final String PARAMETRO_SALIDA_CODART = "salida_codArt";
    public static final String PARAMETRO_SALIDA_DESGLOSE1 = "salida_desglose1";
    public static final String PARAMETRO_SALIDA_DESGLOSE2 = "salida_desglose2";
    public static final String PARAM_MODAL = "modal";

    @FXML
    protected Button btCancelar;
    @FXML
    protected TextField tfDescripcion;
    @FXML
    protected TableView<LineaResultadoBusqGui> tbArticulos;
    @FXML
    protected TextField tfCodigoArt;
    @FXML
    protected Label lbError, lbDesglose1, lbDesglose2;
    @FXML
    protected TilePane panelBotonera;
    @FXML
    protected TableColumn<LineaResultadoBusqGui, String> tcArticulosArticulo;
    @FXML
    protected TableColumn<LineaResultadoBusqGui, String> tcArticulosDescripcion;
    @FXML
    protected TableColumn<LineaResultadoBusqGui, String> tcArticulosDesglose1;
    @FXML
    protected TableColumn<LineaResultadoBusqGui, String> tcArticulosDesglose2;
    @FXML
    protected TextField tfDetalleDescripcion, tfDetalleCodArticulo, tfDetalleDesglose1, tfDetalleDesglose2, tfDetallePrecio;
    @FXML
    protected VBox panelPromociones;
    @FXML
    protected Button btStocksStocks;
    @FXML
    protected Button btStocksPorModeloStocks;
    @FXML
    protected Button btStocksPorDesglose1Stocks;
    @FXML
    protected Button btStocksPorDesglose2Stocks;
    @FXML
    protected Button btStocksVentas;
    @FXML
    protected Button btStocksPorModeloVentas;
    @FXML
    protected Button btStocksPorDesglose1Ventas;
    @FXML
    protected Button btStocksPorDesglose2Ventas;
	@FXML
	protected ImagenArticulo imagenArticulo;
	@FXML
	protected HBox hbStocks;
	@FXML
	protected HBox hbVentas;

    // botonera de acciones de tabla
    protected BotoneraComponent botoneraAccionesTabla;

    //Lineas de la tabla
    protected ObservableList<LineaResultadoBusqGui> lineas;

    //Formulario que valida los valores introducidos para la búsqueda
    protected FormularioBusquedaArtBean frBusquedaArt;

    protected Runnable accionBuscar;
    
    protected String codTarifaBusqueda;
    
    protected ClienteBean clienteBusqueda;
    
    @Autowired
    private ArticulosService articulosService;
    @Autowired
    private VariablesServices variablesServices;
    @Autowired
    private ArticulosTarifaService articulosTarifaService;
    
    @Autowired
    protected TicketManager ticketManager;

    protected Boolean esStock;
    
    protected Boolean modal;

    // </editor-fold>
    // <editor-fold desc="Creación e inicialización">
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.debug("initialize() - Inicializando ventana...");

        // Mensaje sin contenido para tabla. los establecemos a vacio
        tbArticulos.setPlaceholder(new Label(""));

        // Listener a cambio de objeto seleccionado en la lista
        tbArticulos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LineaResultadoBusqGui>() {
            @Override
            public void changed(ObservableValue<? extends LineaResultadoBusqGui> observable, LineaResultadoBusqGui oldValue, LineaResultadoBusqGui newValue) {
            	if (newValue != null && AppConfig.rutaImagenes != null) {
					imagenArticulo.mostrarImagen(newValue.getCodArticulo());
				}
                lineaSeleccionadaChanged();
            }
        });

        frBusquedaArt = SpringContext.getBean(FormularioBusquedaArtBean.class);

        //Se asignan los campos del formulario a los componentes a validar
        frBusquedaArt.setFormField("codArticulo", tfCodigoArt);
        frBusquedaArt.setFormField("descripcion", tfDescripcion);

        // CENTRADO CON ESTILO A LA DERECHA
        tcArticulosArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcArticulosArticulo", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcArticulosDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcArticulosDescripcion", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

        lineas = FXCollections.observableList(new ArrayList<LineaResultadoBusqGui>());
        // Asignamos las lineas a la tabla
        tbArticulos.setItems(lineas);

        // Definimos un factory para cada celda para aumentar el rendimiento
        tcArticulosArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaResultadoBusqGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<LineaResultadoBusqGui, String> cdf) {
                return cdf.getValue().getArtProperty();
            }
        });
        tcArticulosDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaResultadoBusqGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<LineaResultadoBusqGui, String> cdf) {
                return cdf.getValue().getDescripcionProperty();
            }
        });
        tcArticulosDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaResultadoBusqGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<LineaResultadoBusqGui, String> cdf) {
                return cdf.getValue().getDesglose1Property();
            }
        });
        tcArticulosDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaResultadoBusqGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<LineaResultadoBusqGui, String> cdf) {
                return cdf.getValue().getDesglose2Property();
            }
        });

        log.debug("inicializarComponentes() - Configuración de la tabla");
        if (sesion.getAplicacion().isDesglose1Activo()) { //Si hay desglose 1, establecemos el texto
            tcArticulosDesglose1.setText(I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO)));
            lbDesglose1.setText(I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO)));
            tfDetalleDesglose1.setVisible(true);
        }
        else { // si no hay desgloses, compactamos la línea
            tcArticulosDesglose1.setVisible(false);
            lbDesglose1.setText("");
            tfDetalleDesglose1.setVisible(false);
        }
        if (sesion.getAplicacion().isDesglose2Activo()) { //Si hay desglose 1, establecemos el texto
            tcArticulosDesglose2.setText(I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO)));
            lbDesglose2.setText(I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO)));
            tfDetalleDesglose2.setVisible(true);
        }
        else { // si no hay desgloses, compactamos la línea
            tcArticulosDesglose2.setVisible(false);
            lbDesglose2.setText("");
            tfDetalleDesglose2.setVisible(false);
        }
    
    }

    @Override
    public void initializeComponents() {
        log.debug("inicializarComponentes()");

        try {
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = new LinkedList<>();
            
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", ""));

            botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotonera.getChildren().clear();
            panelBotonera.getChildren().add(botoneraAccionesTabla);

            //Se registra el evento para salir de la pantalla pulsando la tecla escape.
            registrarAccionCerrarVentanaEscape();
            crearEventoEnterTabla(tbArticulos);
            crearEventoNavegacionTabla(tbArticulos);
        }
        catch (CargarPantallaException ex) {
            log.error("inicializarComponentes() - Error inicializando pantalla de gestiond e tickets");
            VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
        }
    }
    
    @Override
    public void registrarAccionCerrarVentanaEscape() {
		getScene().addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ESCAPE && modal) {
					accionCancelar();
					t.consume();
				}
				else if (t.getCode() == KeyCode.ESCAPE && !modal) {
					POSApplication.getInstance().getMainView().close();
					t.consume();
				}
			}
		});
	}

	/**
	 * Establece un determiando orden en la secuencia de navegación mediante. si no se establece, se toma la navegación por defecto de la pantalla. tabulador
	 */
	protected void inicializaControlFocos() {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando formulario...");
		lineas = FXCollections.observableList(new ArrayList<LineaResultadoBusqGui>());
		tbArticulos.setItems(lineas);

		tfCodigoArt.setText("");
		tfDescripcion.setText("");
		tfDetalleDescripcion.setText("");
		tfDetalleCodArticulo.setText("");
		tfDetalleDesglose1.setText("");
		tfDetalleDesglose2.setText("");
		tfDetallePrecio.setText("");

		limpiarPanelPromociones();

		codTarifaBusqueda = (String) getDatos().get(PARAMETRO_ENTRADA_CODTARIFA);
		if(codTarifaBusqueda == null) {
			codTarifaBusqueda = sesion.getAplicacion().getTienda().getCliente().getCodtar();
		}
		clienteBusqueda = (ClienteBean) getDatos().get(PARAMETRO_ENTRADA_CLIENTE);
		if(clienteBusqueda == null) {
			clienteBusqueda = sesion.getAplicacion().getTienda().getCliente();
		}
		esStock = (Boolean) getDatos().get(PARAMETRO_ES_STOCK);
		if(esStock == null) {
			esStock = Boolean.TRUE;
		}

		if (imagenArticulo != null) {
			imagenArticulo.setImage(null);
		}
		if (esStock) {
			hbVentas.setVisible(Boolean.FALSE);
			hbVentas.setManaged(Boolean.FALSE);
			hbStocks.setVisible(Boolean.TRUE);
			hbStocks.setManaged(Boolean.TRUE);
			if (sesion.getAplicacion().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
				btStocksPorDesglose1Stocks.setText("Stocks por " + I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO).toLowerCase()));
			}
			if (sesion.getAplicacion().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
				btStocksPorDesglose2Stocks.setText("Stocks por " + I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO).toLowerCase()));
			}
		}
		else {
			hbVentas.setVisible(Boolean.TRUE);
			hbVentas.setManaged(Boolean.TRUE);
			hbStocks.setVisible(Boolean.FALSE);
			hbStocks.setManaged(Boolean.FALSE);
			if (sesion.getAplicacion().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
				btStocksPorDesglose1Ventas.setText("Stocks por " + I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO).toLowerCase()));
			}
			if (sesion.getAplicacion().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
				btStocksPorDesglose2Ventas.setText("Stocks por " + I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO).toLowerCase()));
			}
		}
		modal = (Boolean) getDatos().get(PARAM_MODAL);
		if(modal == null) {
			modal = Boolean.FALSE;
		}
		if (esStock && modal) {
			mostrarBoton(btCancelar);
		}
		else {
			ocultarBoton(btCancelar);
		}
	}

	protected void limpiarPanelPromociones() {
		panelPromociones.getChildren().clear();
    }

    @Override
    public void initializeFocus() {
        tfCodigoArt.requestFocus();
    }

	protected void lineaSeleccionadaChanged() {
		LineaResultadoBusqGui lineaSeleccionada = tbArticulos.getSelectionModel().getSelectedItem();

		limpiarPanelPromociones();

		if (lineaSeleccionada != null) {
			tfDetalleCodArticulo.setText(lineaSeleccionada.getCodArticulo());
			tfDetalleDescripcion.setText(lineaSeleccionada.getDescripcion());
			try {
				TicketManager ticketManager = SpringContext.getBean(TicketManager.class);
				ticketManager.inicializarTicket();
				ticketManager.getTicket().setCliente(clienteBusqueda);
				LineaTicket linea = ticketManager.nuevaLineaArticulo(lineaSeleccionada.getCodArticulo(), lineaSeleccionada.getDesglose1(), lineaSeleccionada.getDesglose2(), BigDecimal.ONE, null);
				tfDetallePrecio.setText(FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalConDto()));
			}
			catch (Exception e) {
				log.error("lineaSeleccionadaChanged() - No se ha podido encontrar la tarifa para este artículo: " + e.getMessage());
				tfDetallePrecio.setText("-");
			}
			tfDetalleDesglose1.setText(lineaSeleccionada.getDesglose1());
			tfDetalleDesglose2.setText(lineaSeleccionada.getDesglose2());
			try {
				evaluarPromocion(lineaSeleccionada.getCodArticulo(), lineaSeleccionada.getDesglose1(), lineaSeleccionada.getDesglose2());
			}
			catch (Exception e) {
				log.error("lineaSeleccionadaChanged() - Ha ocurrido un error consultando las promociones para este artículo: " + e.getMessage(), e);
				Label lbDatos = construirLabelTipoPromocion(I18N.getTexto("Datos de la Promocion") + ": ");
				Label lbError = new Label(I18N.getTexto("Error"));
				FlowPane flowPane = constuirFlowPanePromocion();
				flowPane.getChildren().add(lbDatos);
				flowPane.getChildren().add(lbError);
				panelPromociones.getChildren().add(flowPane);
			}
			
			cargarBotonesStock(lineaSeleccionada);
		}
		else {
			ocultarBotonesStock();
		}
	}
	
	protected void ocultarBotonesStock() {
		if (esStock) {
			ocultarBoton(btStocksStocks);
			ocultarBoton(btStocksPorModeloStocks);
			ocultarBoton(btStocksPorDesglose1Stocks);
			ocultarBoton(btStocksPorDesglose2Stocks);
		}
		else {
			ocultarBoton(btStocksVentas);
			ocultarBoton(btStocksPorModeloVentas);
			ocultarBoton(btStocksPorDesglose1Ventas);
			ocultarBoton(btStocksPorDesglose2Ventas);		
		}
    }

	protected void cargarBotonesStock(LineaResultadoBusqGui lineaSeleccionada) {
		if (esStock) {
			mostrarBoton(btStocksPorModeloStocks);

			if (lineaSeleccionada.getDesglose1() != null && !lineaSeleccionada.getDesglose1().isEmpty() && !"*".equals(lineaSeleccionada.getDesglose1()) && lineaSeleccionada.getDesglose2() != null && !lineaSeleccionada.getDesglose2().isEmpty() && !"*".equals(lineaSeleccionada.getDesglose2())) {
				mostrarBoton(btStocksStocks);
			}
			else {
				ocultarBoton(btStocksStocks);
			}

			if (lineaSeleccionada.getDesglose1() != null && !lineaSeleccionada.getDesglose1().isEmpty() && !"*".equals(lineaSeleccionada.getDesglose1())) {
				mostrarBoton(btStocksPorDesglose1Stocks);
			}
			else {
				ocultarBoton(btStocksPorDesglose1Stocks);
			}
			if (lineaSeleccionada.getDesglose2() != null && !lineaSeleccionada.getDesglose2().isEmpty() && !"*".equals(lineaSeleccionada.getDesglose2())) {
				mostrarBoton(btStocksPorDesglose2Stocks);
			}
			else {
				ocultarBoton(btStocksPorDesglose2Stocks);
			}
		}
		else {
			mostrarBoton(btStocksPorModeloVentas);

			if (lineaSeleccionada.getDesglose1() != null && !lineaSeleccionada.getDesglose1().isEmpty() && !"*".equals(lineaSeleccionada.getDesglose1()) && lineaSeleccionada.getDesglose2() != null && !lineaSeleccionada.getDesglose2().isEmpty() && !"*".equals(lineaSeleccionada.getDesglose2())) {
				mostrarBoton(btStocksVentas);
			}
			else {
				ocultarBoton(btStocksVentas);
			}
			if (lineaSeleccionada.getDesglose1() != null && !lineaSeleccionada.getDesglose1().isEmpty() && !"*".equals(lineaSeleccionada.getDesglose1())) {
				mostrarBoton(btStocksPorDesglose1Ventas);
			} 
			else {
				ocultarBoton(btStocksPorDesglose1Ventas);
			}
			if (lineaSeleccionada.getDesglose2() != null && !lineaSeleccionada.getDesglose2().isEmpty() && !"*".equals(lineaSeleccionada.getDesglose2())) {
				mostrarBoton(btStocksPorDesglose2Ventas);
			}
			else {
				ocultarBoton(btStocksPorDesglose2Ventas);
			}		
		}
    }

	protected void mostrarBoton(Button bt) {
		bt.setVisible(Boolean.TRUE);
		bt.setManaged(Boolean.TRUE);
    }
	
	protected void ocultarBoton(Button bt) {
		bt.setVisible(Boolean.FALSE);
		bt.setManaged(Boolean.FALSE);
    }

	@SuppressWarnings("rawtypes")
    protected void evaluarPromocion(String sCodart, String sDesglose1, String sDesglose2) {
		
		try {
			
			limpiarPanelPromociones();
			
			ticketManager.inicializarTicket();
			TicketVenta ticketAux = (TicketVenta) ticketManager.getTicket();

			// Asignamos el cliente
			ticketAux.setCliente(clienteBusqueda);

			// Insertamos el articulo en el ticket con cantidad 10 para las promociones de NxM
			ticketManager.nuevaLineaArticulo(sCodart, sDesglose1, sDesglose2, new BigDecimal(10), null);
			ticketManager.recalcularConPromociones();
			muestraDatosPromocion(ticketAux, false);

			// Rellenamos los datos de fidelizado para que muestre las promociones de fidelizado
			FidelizacionBean datosFidelizacion = new FidelizacionBean();
			datosFidelizacion.setActiva(true);
			ticketAux.getCabecera().setDatosFidelizado(datosFidelizacion);
			ticketManager.recalcularConPromociones();
			muestraDatosPromocion(ticketAux, true);
			
			// Añadimos paneles vacíos para que la visualización sea siempre la misma, evitando diferentes interlineados entre los paneles de las promociones 
			completarPanel();
		}
		catch (PromocionesServiceException | DocumentoException e) {
			LineaTicketException ex = new LineaTicketException(I18N.getTexto("Error creando el ticket de venta."), e);
			log.error("Error creando ticket", ex);
		}
		catch (LineaTicketException e) {
			LineaTicketException ex = new LineaTicketException(I18N.getTexto("Error insertando línea."), e);
			log.error("Error insertando línea", ex);
		}
		
	}
	
	@SuppressWarnings("rawtypes")
    protected void muestraDatosPromocion(TicketVenta ticket, boolean mostrandoPromoFidelizado) {
		LineaTicket linea = (LineaTicket) ticket.getLineas().get(0);
		
		Promocion promocion = null;
		if(linea.getPromociones() != null && !linea.getPromociones().isEmpty()) {
			promocion = ticket.getPromocion(linea.getPromociones().get(0).getIdPromocion()).getPromocion();
		}
		
		if(promocion != null) {
			String descripcionPromocion = promocion.getDescripcion();
			
			String labelTipoPromocion = null;
			if(mostrandoPromoFidelizado && ticket.getCabecera().getDatosFidelizado() != null && promocion.getSoloFidelizacion()) {
				labelTipoPromocion = I18N.getTexto("Dato promocion fidelizado:");
			}
			else if (!mostrandoPromoFidelizado && !promocion.getSoloFidelizacion()) {
				labelTipoPromocion = I18N.getTexto("Datos promocion NO fidelizado:");
			}
			
			if(StringUtils.isNotBlank(labelTipoPromocion)) {
				Label lbTipoPromocion = construirLabelTipoPromocion(labelTipoPromocion);
				Label lbDescripcionPromocion = new Label(descripcionPromocion);
				
				FlowPane flowPane = constuirFlowPanePromocion();
				flowPane.getChildren().add(lbTipoPromocion);
				flowPane.getChildren().add(lbDescripcionPromocion);
				panelPromociones.getChildren().add(flowPane);
				
				FlowPane flowPaneDetalles = construirFlowPaneDetallePromocion(promocion, linea);
				if(flowPaneDetalles != null) {
					panelPromociones.getChildren().add(flowPaneDetalles);
				}
			}
		}
	}

	protected void completarPanel() {
		int panelesActuales = panelPromociones.getChildren().size();
		for(int i = 0 ; i < 4 - panelesActuales ; i++) {
			panelPromociones.getChildren().add(constuirFlowPanePromocion());
		}
    }

	protected String getPvpPromocion(Promocion promocion, LineaTicket linea) {
		String pvpPromocion = null;
		if (promocionPresentaPrecio(promocion) || promocionPresentaDescuento(promocion)) {
			pvpPromocion = linea.getPrecioTotalConDtoAsString();
		}
	    return pvpPromocion;
    }

	protected FlowPane construirFlowPaneDetallePromocion(Promocion promocion, LineaTicket linea) {
		String pvpPromocion = getPvpPromocion(promocion, linea);
		String dtoPromocion = promocionPresentaDescuento(promocion) ? linea.getDescuento().toString() + "%" : null;
		
	    if(StringUtils.isNotBlank(pvpPromocion) || StringUtils.isNotBlank(dtoPromocion)) {
	    	FlowPane flowPaneDetalles = constuirFlowPanePromocion();
	    	Label lbVacio = construirLabelTipoPromocion("");
	    	flowPaneDetalles.getChildren().add(lbVacio);

	    	if(StringUtils.isNotBlank(dtoPromocion)) {
	    		Label lbLabelDto = construirLabelDetalleDto();
	    		Label lbDto = construirLabelDetalleValorPromocion(dtoPromocion);
	    		flowPaneDetalles.getChildren().add(lbLabelDto);
	    		flowPaneDetalles.getChildren().add(lbDto);
	    	}
	    	
	    	if(StringUtils.isNotBlank(pvpPromocion) && StringUtils.isNotBlank(dtoPromocion)) {
	    		Label lbVacioIntermedio = new Label();
	    		lbVacioIntermedio.setPrefWidth(20);
	    		flowPaneDetalles.getChildren().add(lbVacioIntermedio);
	    	}
	    	
	    	if(StringUtils.isNotBlank(pvpPromocion)) {
	    		Label lbLabelPvp = constuirLabelDetallePrecio();
	    		Label lbPvp = construirLabelDetalleValorPromocion(pvpPromocion);
	    		flowPaneDetalles.getChildren().add(lbLabelPvp);
	    		flowPaneDetalles.getChildren().add(lbPvp);
	    	}
	    	
	    	return flowPaneDetalles;
	    }
	    
	    return null;
    }

	protected Label constuirLabelDetallePrecio() {
	    return construirLabelDetallePromocion(I18N.getTexto("PVP") + ": ");
    }

	protected Label construirLabelDetalleDto() {
	    return construirLabelDetallePromocion(I18N.getTexto("DTO.") + ": ");
    }

	protected Label construirLabelDetallePromocion(String texto) {
		Label label = new Label(texto);
		label.getStyleClass().add("textoResaltado");
		label.setPrefWidth(50.0);
		label.setAlignment(Pos.CENTER_RIGHT);
		return label;
    }

	protected Label construirLabelDetalleValorPromocion(String pvpPromocion) {
		Label label = new Label(pvpPromocion);
		label.setPrefWidth(50.0);
		label.setAlignment(Pos.CENTER_LEFT);
		return label;
    }

	protected FlowPane constuirFlowPanePromocion() {
	    FlowPane flowPane = new FlowPane();
	    flowPane.setPrefHeight(120.0);
	    flowPane.setHgap(5.0);
	    flowPane.setAlignment(Pos.CENTER_LEFT);
	    return flowPane;
    }

	protected Label construirLabelTipoPromocion(String labelTipoPromocion) {
	    Label lbTipoPromocion = new Label(labelTipoPromocion);
	    lbTipoPromocion.getStyleClass().add("textoResaltado");
	    lbTipoPromocion.setPrefWidth(180.0);
	    lbTipoPromocion.setAlignment(Pos.CENTER_RIGHT);
	    return lbTipoPromocion;
    }
	
	protected boolean promocionPresentaDescuento(Promocion promocion) {
		String presentaDescuentoFinalStr = promocion.getPromocionBean().getTipoPromocion().getConfiguracionMap().get("PresentaDescuentoFinal");
		if ("S".equals(presentaDescuentoFinalStr) || "true".equals(presentaDescuentoFinalStr)) {
			return true;
		}
		return false;
	}
	
	protected boolean promocionPresentaPrecio(Promocion promocion) {
		String presentaPrecioFinalStr = promocion.getPromocionBean().getTipoPromocion().getConfiguracionMap().get("PresentaPrecioFinal");
		if ("S".equals(presentaPrecioFinalStr) || "true".equals(presentaPrecioFinalStr)) {
			return true;
		}
		return false;
	}
    
    // </editor-fold>
    // <editor-fold desc="Funciones relacionadas con interfaz GUI y manejo de pantalla">
    public void refrescarDatosPantalla() {
        lineas.clear();
        tfCodigoArt.setText("");
        tfDescripcion.setText("");
        tfDetalleCodArticulo.setText("");
        tfDetalleDescripcion.setText("");
        tfDetallePrecio.setText("");
        tfDetalleDesglose1.setText("");
        tfDetalleDesglose2.setText("");
    }

    // </editor-fold>
    // <editor-fold desc="Funciones relacionadas con boton buscar y botonera de tabla">
    /**
     * Ejecuta la acción pasada por parámetros.
     *
     * @param botonAccionado botón que ha sido accionado
     */
    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {

        log.trace("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());

        switch (botonAccionado.getClave()) {
            // BOTONERA TABLA MOVIMIENTOS
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionTablaPrimerRegistro(tbArticulos);
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionTablaIrAnteriorRegistro(tbArticulos);
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionTablaIrSiguienteRegistro(tbArticulos);
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                accionTablaUltimoRegistro(tbArticulos);
                break;
            default:
                log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
                break;
        }
    }

    @FXML
    public void accionBuscarTeclado(KeyEvent event) {
        log.trace("accionBuscarTeclado()");

        if (event.getCode() == KeyCode.ENTER && !event.isControlDown()) {
            accionBuscar();
        }
        else if(event.getCode() == KeyCode.DOWN && !event.isControlDown() && tbArticulos.getItems().size() > 0) {
			tbArticulos.requestFocus();
		}
    }

    @FXML
    public void accionBuscar() {
        log.trace("accionBuscar()");

        //Vaciamos el resultado de la busqueda anterior
        lineas.clear();
        //Limpiamos los detalles del artículo de la posible selección anterior
        tfDetalleCodArticulo.setText("");
        tfDetalleDescripcion.setText("");
        tfDetallePrecio.setText("");
        tfDetalleDesglose1.setText("");
        tfDetalleDesglose2.setText("");
        
        frBusquedaArt.setCodArticulo(tfCodigoArt.getText());
        frBusquedaArt.setDescripcion(tfDescripcion.getText());
        
        if (validarFormularioBusqueda()) {
            ArticulosParamBuscar parametrosArticulo = new ArticulosParamBuscar();
            parametrosArticulo.setCodigo(frBusquedaArt.getCodArticulo().toUpperCase());
            parametrosArticulo.setDescripcion(frBusquedaArt.getDescripcion());
            parametrosArticulo.setCliente(clienteBusqueda);
            parametrosArticulo.setCodTarifa(codTarifaBusqueda);
            parametrosArticulo.setActivo(true);
            
            BuscarArticulosTask buscarArticulosTask = new BuscarArticulosTask(parametrosArticulo);
            buscarArticulosTask.start();
        }
    }
    
    protected class BuscarArticulosTask extends BackgroundTask<List<ArticuloBuscarBean>>{
        private final ArticulosParamBuscar parametrosArticulo;
        
        public BuscarArticulosTask(ArticulosParamBuscar parametrosArticulo){
            this.parametrosArticulo = parametrosArticulo;
        }
        
        @Override
        protected List<ArticuloBuscarBean> call() throws Exception {
            return articulosService.buscarArticulos(parametrosArticulo);
        }

        @Override
        protected void succeeded() {
            buscarArticulosSucceeded(getValue());
            
            super.succeeded();
        }

        @Override
        protected void failed() {
        	super.failed();
            log.error("accionBuscar() - Error consultando artículos");
            VentanaDialogoComponent.crearVentanaError(getCMZException().getMessageI18N(), getStage());
            
        }
    }

    
    
    @Override
	public void accionEventoEnterTabla(String idTabla) {
    	accionEnviarArticuloFacturacion();
	}

	/**
     * Accion que trata el evento de teclado para enviar el artículo a facturar.
     *
     * @param event
     */
    public void aceptarArticuloTeclado(KeyEvent event) {
        log.trace("aceptarArticuloTeclado(KeyEvent event) - Aceptar");
        if (event.getCode() == KeyCode.ENTER) {
            accionEnviarArticuloFacturacion();
        }
    }
    
	/**
	 * @param event
	 */
	public void aceptarArticuloDobleClick(MouseEvent event) {
		log.debug("aceptarArticuloDobleClick() - Acción aceptar");
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				if (esStock) {
					accionConsultarStocks();
				}
				else {
					accionEnviarArticuloFacturacion();
				}
			}
		}
	}

    /**
     * Acción aceptar
     */
    public void aceptarArticuloBoton(ActionEvent event) {
        log.debug("aceptarArticuloBoton(ActionEvent event) - Aceptar");
        accionEnviarArticuloFacturacion();
    }

    /**
     * Método que envía el artículo para facturar.
     */
    public void accionEnviarArticuloFacturacion() {
        int indiceArt = tbArticulos.getSelectionModel().getSelectedIndex();
        if (indiceArt < 0) {
            log.debug("enviarArticuloFacturacion() - No hay artículo seleccionado");
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha seleccionado un artículo."), getStage());
            return;                // Salimos d ela función
        }
        //
        LineaResultadoBusqGui selectedItem = tbArticulos.getSelectionModel().getSelectedItem();

        // Seteamos los parametros de salida en el mapa
        this.getDatos().put(BuscarArticulosController.PARAMETRO_SALIDA_CODART, selectedItem.getCodArticulo());
        this.getDatos().put(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE1, selectedItem.getDesglose1());
        this.getDatos().put(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE2, selectedItem.getDesglose2());

        // Cerramos la ventana
        getStage().close();
    }

    protected boolean validarFormularioBusqueda() {

        // Limpiamos los errores que pudiese tener el formulario
        frBusquedaArt.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<FormularioBusquedaArtBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBusquedaArt);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioBusquedaArtBean> next = constraintViolations.iterator().next();
            frBusquedaArt.setErrorStyle(next.getPropertyPath(), true);
            frBusquedaArt.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            return false;
        }
        return true;

    }

    // </editor-fold>
    // <editor-fold desc="Funciones relacionadas con la tabla de resultados de búsqueda">
    /**
     * Acción mover a primer registro de la tabla
     */
    @FXML
    protected void accionTablaPrimerRegistro() {
        log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
        if (tbArticulos.getItems() != null) {
            tbArticulos.getSelectionModel().select(0);
            tbArticulos.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }

    /**
     * Acción mover a anterior registro de la tabla
     */
    @FXML
    protected void accionTablaAnteriorRegistro() {
        log.debug("accionTablaAnteriorRegistro() - Acción ejecutada");
        if (tbArticulos.getItems() != null) {
            int indice = tbArticulos.getSelectionModel().getSelectedIndex();
            if (indice > 0) {
                tbArticulos.getSelectionModel().select(indice - 1);
                tbArticulos.scrollTo(indice - 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a siguiente registro de la tabla
     */
    @FXML
    protected void accionTablaSiguienteRegistro() {
        log.debug("accionTablaSiguienteRegistro() - Acción ejecutada");
        if (tbArticulos.getItems() != null) {
            int indice = tbArticulos.getSelectionModel().getSelectedIndex();
            if (indice < tbArticulos.getItems().size()) {
                tbArticulos.getSelectionModel().select(indice + 1);
                tbArticulos.scrollTo(indice + 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a último registro de la tabla
     */
    @FXML
    protected void accionTablaUltimoRegistro() {
        log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
        if (tbArticulos.getItems() != null) {
            tbArticulos.getSelectionModel().select(tbArticulos.getItems().size() - 1);
            tbArticulos.scrollTo(tbArticulos.getItems().size() - 1);  // Mueve el scroll para que se vea el registro
        }
    }
    
	public void accionConsultarStocksPorModelo() {
		int indiceArt = tbArticulos.getSelectionModel().getSelectedIndex();
		if (indiceArt < 0) {
			log.debug("accionConsultarStocks() - No hay artículo seleccionado");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha seleccionado un artículo."), getStage());
			return; // Salimos de la función
		}
		HashMap<String, Object> datos = new HashMap<>();
		LineaResultadoBusqGui lineaSeleccionada = tbArticulos.getSelectionModel().getSelectedItem();
		if (lineaSeleccionada != null) {
			datos.put(DisponibilidadStockController.PARAM_CODART, lineaSeleccionada.getCodArticulo());
			datos.put(DisponibilidadStockController.PARAM_DESGLOSE1, "");
			datos.put(DisponibilidadStockController.PARAM_DESGLOSE2, "");
		}
		consultarStocks(datos);
	}

	public void accionConsultarStocks() {
		int indiceArt = tbArticulos.getSelectionModel().getSelectedIndex();
		if (indiceArt < 0) {
			log.debug("accionConsultarStocksPorModelo() - No hay artículo seleccionado");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha seleccionado un artículo."), getStage());
			return; // Salimos de la función
		}
		HashMap<String, Object> datos = new HashMap<>();
		LineaResultadoBusqGui lineaSeleccionada = tbArticulos.getSelectionModel().getSelectedItem();
		if (lineaSeleccionada != null) {
			datos.put(DisponibilidadStockController.PARAM_CODART, lineaSeleccionada.getCodArticulo());
			datos.put(DisponibilidadStockController.PARAM_DESGLOSE1, lineaSeleccionada.getDesglose1());
			datos.put(DisponibilidadStockController.PARAM_DESGLOSE2, lineaSeleccionada.getDesglose2());
		}

		consultarStocks(datos);
	}

	public void accionConsultarStocksPorDesglose1() {
		int indiceArt = tbArticulos.getSelectionModel().getSelectedIndex();
		if (indiceArt < 0) {
			log.debug("accionConsultarStocksPorDesglose1() - No hay artículo seleccionado");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha seleccionado un artículo."), getStage());
			return; // Salimos de la función
		}
		HashMap<String, Object> datos = new HashMap<>();
		LineaResultadoBusqGui lineaSeleccionada = tbArticulos.getSelectionModel().getSelectedItem();
		if (lineaSeleccionada != null) {
			datos.put(DisponibilidadStockController.PARAM_CODART, lineaSeleccionada.getCodArticulo());
			datos.put(DisponibilidadStockController.PARAM_DESGLOSE1, lineaSeleccionada.getDesglose1());
			datos.put(DisponibilidadStockController.PARAM_DESGLOSE2, "");
		}
		consultarStocks(datos);
	}

	public void accionConsultarStocksPorDesglose2() {
		int indiceArt = tbArticulos.getSelectionModel().getSelectedIndex();
		if (indiceArt < 0) {
			log.debug("accionConsultarStocksPorDesglose2() - No hay artículo seleccionado");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha seleccionado un artículo."), getStage());
			return; // Salimos de la función
		}
		HashMap<String, Object> datos = new HashMap<>();
		LineaResultadoBusqGui lineaSeleccionada = tbArticulos.getSelectionModel().getSelectedItem();
		if (lineaSeleccionada != null) {
			datos.put(DisponibilidadStockController.PARAM_CODART, lineaSeleccionada.getCodArticulo());
			datos.put(DisponibilidadStockController.PARAM_DESGLOSE1, "");
			datos.put(DisponibilidadStockController.PARAM_DESGLOSE2, lineaSeleccionada.getDesglose2());
		}
		consultarStocks(datos);
	}

	protected void consultarStocks(HashMap<String, Object> datos) {
		datos.put(DisponibilidadStockController.PARAM_MODAL, Boolean.TRUE);
		getApplication().getMainView().showModal(DisponibilidadStockModalView.class, datos);
	}
	
    public void accionBtCancelar(){
    	getStage().close();
    }

	protected void buscarArticulosSucceeded(List<ArticuloBuscarBean> articulosF) {
	    List<LineaResultadoBusqGui> lineasRes = new ArrayList<>();
	                
	    if (articulosF.isEmpty()) {
	        tbArticulos.setPlaceholder(new Label(I18N.getTexto("No se han encontrado resultados")));
	    }
	    for(ArticuloBuscarBean articulo: articulosF){
	        lineasRes.add(new LineaResultadoBusqGui(articulo));
	    }

	    lineas.addAll(lineasRes);
	    
	    tbArticulos.getSelectionModel().select(0);
	    tbArticulos.getFocusModel().focus(0);
    }
	
}
