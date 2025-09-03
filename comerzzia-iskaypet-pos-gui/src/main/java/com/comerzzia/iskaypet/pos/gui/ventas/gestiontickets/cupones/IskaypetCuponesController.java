package com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.cupones;

import com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController;
import com.comerzzia.iskaypet.pos.services.promociones.IskaypetPromocionesService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//CZZ-2293
@Controller
public class IskaypetCuponesController extends WindowController implements IContenedorBotonera{

	protected static final Logger log = Logger.getLogger(IskaypetCuponesController.class.getName());

	public static final String PARAMETRO_TICKET_CUPONES="TICKET_CUPONES";

	public static final String PARAMETRO_SALIDA_CANCELAR="SALIDA_CANCELAR";

	public static final String EXTENSION_TIPO_DESC_CUPON = "tipoDescCupon";
	public static final String EXTENSION_IMPORTE_DESC_CUPON = "importeDescCupon";
	public static final String EXTENSION_INFO_EXCLUSIONES_CUPON = "infoExclusionesCupon";

	protected TicketVentaAbono ticket;
	protected List<CuponEmitidoTicket> cuponesEmitidos;

	@FXML
	protected TableView<IskaypetLineaCuponGui> tbTicket;
	@FXML
	protected AnchorPane panelBotoneraTabla;
	@FXML
	protected AnchorPane panelBotoneraTicket;
	@FXML
	protected TableColumn<IskaypetLineaCuponGui, String> tcTitulo;
	
	@FXML
	protected TableColumn<IskaypetLineaCuponGui, String> tcCodigo;
	@FXML
	protected TableColumn<IskaypetLineaCuponGui, BigDecimal> tcImporte;
	@FXML
	protected TableColumn<IskaypetLineaCuponGui, String> tcFechaInicio;
	@FXML
	protected TableColumn<IskaypetLineaCuponGui, String> tcFechaFin;
	@FXML
	protected TableColumn<IskaypetLineaCuponGui, Boolean> tcBtSelec;
	
	@FXML
	protected Button btAceptar;
	
	@FXML
	protected Button btCancelar;

	protected ObservableList<IskaypetLineaCuponGui> lineas;
	
	@Autowired
	protected Sesion sesion;
	
	protected BotoneraComponent botoneraAccionesTabla;
	
	@Autowired
	protected VariablesServices variablesServices;

    @Autowired
    protected IskaypetPromocionesService iskaypetPromocionesService;

	private boolean todoSeleccionado;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		tbTicket.setPlaceholder(new Label(""));

		tcTitulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcTitulo", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCodigo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcCodigo", null,CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tfImporte", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcFechaInicio.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcFechaInicio", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcFechaFin.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcFechaFin", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

		tcTitulo.setCellValueFactory(cdf -> cdf.getValue().getTitulo());
		tcCodigo.setCellValueFactory(cdf -> cdf.getValue().getCodigo());
		tcImporte.setCellValueFactory(cdf -> cdf.getValue().getImporte());
		tcFechaInicio.setCellValueFactory(cdf -> cdf.getValue().getFechaInicio());
		tcFechaFin.setCellValueFactory(cdf -> cdf.getValue().getFechaFin());

		tcBtSelec.setCellValueFactory(new PropertyValueFactory<>("lineaSelec"));
		tcBtSelec.setCellFactory(CheckBoxTableCell.forTableColumn(tcBtSelec));
		tcBtSelec.getStyleClass().add(CellFactoryBuilder.ESTILO_ALINEACION_CEN);
		tcBtSelec.setEditable(true);
		tbTicket.setEditable(true);		
				
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {

		try {
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new BotoneraComponent(1, 6, this, listaAccionesAccionesTabla, panelBotoneraTabla.getPrefWidth(), panelBotoneraTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotoneraTabla.getChildren().add(botoneraAccionesTabla);
            
            crearEventoEnterTabla(tbTicket);
            crearEventoNavegacionTabla(tbTicket);
            
            EventHandler<KeyEvent> evhGeneral = event -> {
                KeyCodeCombination keyCode = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
                if (keyCode.match(event)) {
                    if (todoSeleccionado) {
                        borrarSeleccionLineas();
                    } else {
                        seleccionarLineas();
                    }
                    if (tbTicket.isFocused()) {
                        // Si la tabla tiene el foco, se ejecutará tanto este evento como el de accionEventoEnterTabla,
                        // por lo que quedaría la línea seleccionada al contrario que todas las demás.
                        // Para evitar este efecto, forzamos la ejecución del evento enter.
                        accionEventoEnterTabla(null);
                    }
                }
            };
    
            if (getScene() != null){
            	registraEventoTeclado(evhGeneral, KeyEvent.KEY_RELEASED);
            }
        }
        catch (CargarPantallaException ex) {
            log.error("initializeComponents() - " + ex.getClass().getName() + " - " + ex.getLocalizedMessage(), ex);
        }
		
		registrarAccionCerrarVentanaEscape();
	}
	

	@Override
	public void initializeForm() throws InitializeGuiException {
		ticket = (TicketVentaAbono) this.getDatos().get(PARAMETRO_TICKET_CUPONES);
		cuponesEmitidos = ticket.getCuponesEmitidos();
        
        lineas = FXCollections.observableList(new ArrayList<IskaypetLineaCuponGui>());
        
        tbTicket.setItems(lineas);
        
        for(CuponEmitidoTicket cupon: cuponesEmitidos){
        	lineas.add(new IskaypetLineaCuponGui(cupon));
        }
        
        seleccionarLineas();
        tbTicket.getSelectionModel().select(0);
        Platform.runLater(()-> initializeFocus());
        
	}
	
	@Override
	public void initializeFocus() {
		btAceptar.requestFocus();
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.trace("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
        switch (botonAccionado.getClave()) {
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionTablaPrimerRegistro(tbTicket);
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionTablaIrAnteriorRegistro(tbTicket);
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionTablaIrSiguienteRegistro(tbTicket);
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                accionTablaUltimoRegistro(tbTicket);
                break;
            case "ACCION_TABLA_SELECCIONAR_TODO":
                seleccionarLineas();
                break;
            case "ACCION_TABLA_BORRAR_SELECCION":
                borrarSeleccionLineas();
                break;
            default:
            	break;
        }
	}
	
	@FXML
	public void accionAceptar() {
		log.debug("accionAceptar()");
	    if (hayLineasSeleccionadas()) {
	        List<IskaypetLineaCuponGui> lineasSeleccionadas = lineas.stream().filter(IskaypetLineaCuponGui::isLineaSelec).collect(Collectors.toList());
	        for (IskaypetLineaCuponGui linea : lineasSeleccionadas) {
	            Map<String, Object> mapaParametrosCupon = construirMapaParametros(linea);
	            try {
	    	        ServicioImpresion.imprimir("cupon_promocion", mapaParametrosCupon);
	    	    } catch (DeviceException e) {
	    	        VentanaDialogoComponent.crearVentanaError(getStage(),
	    	                I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), e);
	    	    }
	        }
	        getStage().close();
	    } else {
	        VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay ninguna línea seleccionada."), getStage());
	    }
	}

	/**
	 *  CZZ-2293 - Permite saber si hay lineas seleccionadas en el selector de cupones a imprimir.
	 * @return
	 */
	private boolean hayLineasSeleccionadas() {
	    return lineas.stream().anyMatch(IskaypetLineaCuponGui::isLineaSelec);
	}

	/**
	 * CZZ-2293 - Construye el mismo mapa de parámetros de la pantalla de pagos para imprimir los cupones.
	 * @param linea
	 * @return
	 */
	private Map<String, Object> construirMapaParametros(IskaypetLineaCuponGui linea) {
		log.debug("construirMapaParametros()");
	    Map<String, Object> mapa = new HashMap<>();
	    CuponEmitidoTicket cupon = linea.getCupon();

	    mapa.put(IskaypetDetalleGestionticketsController.TICKET, ticket);
	    mapa.put(IskaypetDetalleGestionticketsController.IMPRIMIR_LOGO,IskaypetDetalleGestionticketsController.requierImprimirLogo(variablesServices));

	    FidelizacionBean datosFidelizado = ticket.getCabecera().getDatosFidelizado();
	    boolean esPaperless = datosFidelizado != null && Boolean.TRUE.equals(datosFidelizado.getPaperLess());
	    mapa.put(IskaypetDetalleGestionticketsController.PAPERLESS, esPaperless);

	    mapa.put("cupon", cupon);
	    mapa.put("fechaEmision", new SimpleDateFormat().format(ticket.getCabecera().getFecha()));

	    Date fechaInicio = cupon.getFechaInicio();
	    Date fechaTicket = ticket.getCabecera().getFecha();
	    mapa.put("fechaInicio", FormatUtil.getInstance().formateaFecha((fechaInicio != null && !fechaInicio.before(fechaTicket)) ? fechaInicio : fechaTicket));

	    mapa.put("fechaFin", FormatUtil.getInstance().formateaFecha(cupon.getFechaFin()));

	    mapa.put("maximoUsos", cupon.getMaximoUsos() != null ? cupon.getMaximoUsos().toString() : "");

	    mapa.put("importeCuponFormateado", getImporteCuponFormateado(cupon));

        Map<String, String> mapaExtensiones = iskaypetPromocionesService.getExtensionesPromocion(cupon.getIdPromocionOrigen());

        if(cupon.getIdPromocionOrigen() != null) {
            mapa.put(EXTENSION_TIPO_DESC_CUPON, mapaExtensiones.get(EXTENSION_TIPO_DESC_CUPON));
        }
        if(cupon.getIdPromocionOrigen() != null) {
            mapa.put(EXTENSION_IMPORTE_DESC_CUPON, mapaExtensiones.get(EXTENSION_IMPORTE_DESC_CUPON));
        }
        if(cupon.getIdPromocionOrigen() != null) {
            mapa.put(EXTENSION_INFO_EXCLUSIONES_CUPON, mapaExtensiones.get(EXTENSION_INFO_EXCLUSIONES_CUPON));
        }

	    return mapa;
	}

	/**
	 * 
	 * CZZ-2293 - Formatea el importe del cupón a imprimir
	 */
	public String getImporteCuponFormateado(CuponEmitidoTicket cupon) {
		BigDecimal importeCupon = cupon.getImporteCupon().setScale(2, RoundingMode.HALF_UP);

        // Formateo con patron para mostrar importe -> x.xxx,yy
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

        return decimalFormat.format(importeCupon);
	}

	@Override
	@FXML
	public void accionCancelar(){
		datos.put(PARAMETRO_SALIDA_CANCELAR, true);
		getStage().close();
	}
	
	/**
     * La acción Enter muestra pantalla de visualización del ticket
     *
     * @param idTabla
     */
	@Override
    public void accionEventoEnterTabla(String idTabla) {
        log.debug("accionEventoEnterTabla() - Acción ejecutada");
        IskaypetLineaCuponGui selectedItem = tbTicket.getSelectionModel().getSelectedItem();
        selectedItem.setLineaSelec(!selectedItem.isLineaSelec());
    }
	
	protected void borrarSeleccionLineas(){
        log.debug("borrarSeleccionLineas() - Acción ejecutada");
        
        for(IskaypetLineaCuponGui lineaTicket: lineas){
            lineaTicket.setLineaSelec(false);
        }
        todoSeleccionado = false;
    }
    
    protected void seleccionarLineas(){
        log.debug("seleccionarLineas() - Acción ejecutada");
        
        for(IskaypetLineaCuponGui lineaTicket: lineas){
            lineaTicket.setLineaSelec(true);
        }
        todoSeleccionado = true;
    }
	
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, KeyCode.HOME.getName(), "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, KeyCode.END.getName(), "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_check.png", null, null, "ACCION_TABLA_SELECCIONAR_TODO", "REALIZAR_ACCION")); 
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_cross.png", null, null, "ACCION_TABLA_BORRAR_SELECCION", "REALIZAR_ACCION"));
        
        return listaAcciones;
    }
}
