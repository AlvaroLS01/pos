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


package com.comerzzia.pos.gui.ventas.devoluciones.articulos;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieView;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.giftcard.GiftCardService;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class DevolucionArticulosController extends WindowController implements IContenedorBotonera{
    
    private static final Logger log = Logger.getLogger(DevolucionArticulosController.class.getName());
    
    @FXML
    protected TableView tbArticulos;
    @FXML
    protected TableColumn<LineaTicketAbonoGui, String> tcDesglose1;
    @FXML
    protected TableColumn<LineaTicketAbonoGui, String> tcDesglose2;
    // botonera de acciones de tabla
    protected BotoneraComponent botoneraAccionesTabla, botoneraOpcionesSeleccion;
    @FXML
    protected AnchorPane panelMenuTabla, panelBotonera;
    @FXML
    protected TableColumn<LineaTicketAbonoGui, String> tcArticulo;
    @FXML
    protected TableColumn<LineaTicketAbonoGui, String> tcDescripcion;
    @FXML
    protected TableColumn<LineaTicketAbonoGui, BigDecimal> tcCantidad;
    @FXML
    protected TableColumn<LineaTicketAbonoGui, BigDecimal> tcCantDevuelta;
    @FXML
    protected TableColumn<LineaTicketAbonoGui, BigDecimal> tcCantADevolver;
    @FXML
    protected TableColumn<LineaTicketAbonoGui, BigDecimal> tcPVP;
    @FXML
    protected TextField tfDesDoc, tfCodDoc, tfFecha, tfTicket, tfCaja;
    
    @FXML
    protected Button btAceptar, btCancelar;
            
    protected ObservableList<LineaTicketAbonoGui> lineas;
    
    protected TicketManager ticketManager;
    
    protected boolean cambiosRealizados = false;
    
    protected List<LineaProvisionalDevolucion> lineasProvisionales;
    
    @Autowired
    private VariablesServices variablesServices;
    
    @Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;
    
    @Autowired
    private Sesion sesion;
    
    @Autowired
    private GiftCardService giftCardService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tbArticulos.setPlaceholder(new Label(""));
        
        //Inicializamos la lista de lineas de articulos
        lineas = FXCollections.observableList(new ArrayList<LineaTicketAbonoGui>());
        
        tbArticulos.setItems(lineas);
        
        tcArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcArticulo", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDescripcion", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDesglose1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDesglose1", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDesglose2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcDesglose2", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcPVP.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbArticulos", "tcPVP", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
        tcCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcCantidad", 3,CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
        tcCantDevuelta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "tcCantDevuelta", 3, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
        tcCantADevolver.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbArticulos", "cantidadADevolver", 3, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
        
        // Definimos un factory para cada celda para aumentar el rendimiento
        tcArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketAbonoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketAbonoGui, String> cdf) {
                return cdf.getValue().getArtProperty();
            }
        });
        tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketAbonoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketAbonoGui, String> cdf) {
                return cdf.getValue().getDescripcionProperty();
            }
        });
        tcCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketAbonoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketAbonoGui, BigDecimal> cdf) {
                return cdf.getValue().getCantidadProperty();
            }
        });
        tcDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketAbonoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketAbonoGui, String> cdf) {
                return cdf.getValue().getDesglose1Property();
            }
        });
        tcDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketAbonoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketAbonoGui, String> cdf) {
                return cdf.getValue().getDesglose2Property();
            }
        });
        tcPVP.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketAbonoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketAbonoGui, BigDecimal> cdf) {
                return cdf.getValue().getPvpConDtoProperty();
            }
        });

        tcCantDevuelta.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketAbonoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketAbonoGui, BigDecimal> cdf) {
                return cdf.getValue().getCantidadDevuelta();
            }
        });
        tcCantADevolver.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketAbonoGui, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketAbonoGui, BigDecimal> cdf) {
                return cdf.getValue().getCantidadADevolver();
            }
        });
        
    }

    @Override
    public void initializeComponents() {

        try {
            // Botonera de Tabla
            log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de devoluciones");
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new BotoneraComponent(1, 7, this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelMenuTabla.getChildren().add(botoneraAccionesTabla);
            
            PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
            botoneraOpcionesSeleccion = new BotoneraComponent(panelBotoneraBean, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
            panelBotonera.getChildren().add(botoneraOpcionesSeleccion);
            
            log.debug("inicializarComponentes() - registrando acciones de la tabla principal");
            crearEventoEnterTabla(tbArticulos);
            crearEventoNegarTabla(tbArticulos);
            crearEventoEliminarTabla(tbArticulos);
            crearEventoNavegacionTabla(tbArticulos);
            
            log.debug("inicializarComponentes() - Configuración de la tabla");
            if (sesion.getAplicacion().isDesglose1Activo()) { //Si hay desglose 1, establecemos el texto
                tcDesglose1.setText(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO));
            }
            else { // si no hay desgloses, compactamos la línea
            	tcDesglose1.setVisible(false);
            }
            if (sesion.getAplicacion().isDesglose2Activo()) { //Si hay desglose 1, establecemos el texto
                tcDesglose2.setText(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO));
            }
            else { // si no hay desgloses, compactamos la línea
            	tcDesglose2.setVisible(false);
            }
            
            registrarAccionCerrarVentanaEscape();
        }
        catch (CargarPantallaException | InitializeGuiException ex) {
            log.error("inicializarComponentes() - Error inicializando pantalla de devolución de artículos");
            VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
        }
        
    }
    
    private List<ConfiguracionBotonBean> cargarAccionesTabla() {
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        
        return listaAcciones;
    }
    
    @Override
    public void initializeForm() throws InitializeGuiException {
        try {
            HashMap<String, Object> datos = getDatos();
            ticketManager = (TicketManager) datos.get(FacturacionArticulosController.TICKET_KEY);
            
            lineasProvisionales = new ArrayList();
            
            DatosDocumentoOrigenTicket datosOrigen = ticketManager.getTicket().getCabecera().getDatosDocOrigen();
            
            if(datosOrigen != null) {
	            tfCaja.setText(datosOrigen.getCaja());
	            tfCodDoc.setText(datosOrigen.getCodTipoDoc());
	            tfFecha.setText(datosOrigen.getFecha());
	            tfTicket.setText(datosOrigen.getNumFactura()+"");
	            tfDesDoc.setText(sesion.getAplicacion().getDocumentos().getDocumento(datosOrigen.getCodTipoDoc()).getDestipodocumento());
            }
            
            
            cambiosRealizados = false;           
            
            refrescarDatosPantalla();
           
        }
        catch (DocumentoException ex) {
            log.error("No se encontró el documento de la operación original.", ex);
        }
        
        // Borramos el ticket guardado como copia de seguridad para evitar introducir basura en la base de datos.
	    try {
	        copiaSeguridadTicketService.guardarBackupTicketActivo(new TicketVentaAbono());
        }
        catch (TicketsServiceException  e) {
        	log.error("initializeForm() - " + e.getMessage());
        	VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessageI18N(), e);
        }

    }
    
    @Override
    public void initializeFocus() {
        
        tbArticulos.getSelectionModel().select(0);
        tbArticulos.requestFocus();
    }
    
    @FXML
    public void accionCancelar(){
        if(cambiosRealizados){

        	if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se perderán los cambios realizados, ¿está seguro?"), this.getStage())){
                for(LineaProvisionalDevolucion lineaProvisional: lineasProvisionales){
                	// Sustituimos en el ticket la cantidad a devolver como suma de las líneas referenciadas a la línea original
                	// existentes en el ticket
                 	BigDecimal cantidadADevolver = BigDecimal.ZERO;
                 	for(Object linea : ticketManager.getTicket().getLineas()) {
                 		LineaTicketAbstract lineaTicket = (LineaTicketAbstract) linea;
                 		if(lineaTicket.getLineaDocumentoOrigen().equals(lineaProvisional.getLineaOriginal().getIdLinea())) {
                 			cantidadADevolver = cantidadADevolver.add(lineaTicket.getCantidad().abs());
                 		}
                 	}
                         
                    LineaTicketAbstract lineaTicket = ((TicketVenta)ticketManager.getTicketOrigen()).getLinea(lineaProvisional.getIdLinea());
                    lineaTicket.setCantidadADevolver(cantidadADevolver);
                }
                eliminarEventosTeclado();
                
                getStage().close();
            }
            
        }
        else{
            eliminarEventosTeclado();
            
            getStage().close();
        }
    }
    
    @FXML
    public void accionAceptar(){
        //Accion ir a la pantalla de pagos
        log.trace("accionAceptar()");
        
        if(cambiosRealizados){
            boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se procederá a aplicar los cambios en el ticket de devolución, ¿está seguro?"), getStage());
            if(confirmacion) {
            	facturarLineasADevolver();
                POSApplication.getInstance().activarTimer();
                getStage().close();
            }
        }
        else{
            getStage().close();
        }
    }
    
    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
        
        log.trace("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
        switch (botonAccionado.getClave()) {
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionTablaPrimerRegistro();
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionTablaAnteriorRegistro();
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionTablaSiguienteRegistro();
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                accionTablaUltimoRegistro();
                break;
            case  "ACCION_TABLA_AGNADIR_UNO":
                accionTablaAgnadirUno();
                break;
            case "ACCION_TABLA_AGNADIR_LINEA":
                accionTablaAgnadirLinea();
                break;
            case "ACCION_TABLA_SELECCIONAR_TODO":
                accionTablaSeleccionarTodo();
                break;
        }
    }
    
    /**
     * Acción mover a primer registro de la tabla
     */
    protected void accionTablaPrimerRegistro() {
        log.debug("accionTablaPrimerRegistro() - Acción ejecutada");
        if (tbArticulos.getItems() != null && tbArticulos.getItems() != null) {
            tbArticulos.getSelectionModel().select(0);
            tbArticulos.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }
    
    /**
     * Acción mover a anterior registro de la tabla
     */
    protected void accionTablaAnteriorRegistro() {
        log.debug("accionTablaAnteriorRegistro() - Acción ejecutada");
        if (tbArticulos.getItems() != null && tbArticulos.getItems() != null) {
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
    protected void accionTablaSiguienteRegistro() {
        log.debug("accionIrSiguienteRegistroTabla() - Acción ejecutada");
        if (tbArticulos.getItems() != null && tbArticulos.getItems() != null) {
            int indice = tbArticulos.getSelectionModel().getSelectedIndex();
            if (indice < tbArticulos.getItems().size()) {
                tbArticulos.getSelectionModel().select(indice + 1);
                tbArticulos.scrollTo(indice + 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }
    
    /**
     * Acción mover a siguiente registro de la tabla
     */
    public void accionTablaAgnadirUno() {
        log.debug("accionTablaAgnadirUno() - Acción ejecutada");
        if (tbArticulos.getItems() != null) {
            int indice = tbArticulos.getSelectionModel().getSelectedIndex();
            if (indice < tbArticulos.getItems().size()) {
                LineaTicketAbonoGui lineaTablaSelec =  ((LineaTicketAbonoGui)tbArticulos.getSelectionModel().getSelectedItem());
                if(lineaTablaSelec!=null){
                    LineaTicket lineaTicketSelec= ((LineaTicket)ticketManager.getTicketOrigen().getLinea(lineaTablaSelec.getIdLinea()));
                    
                    if(lineaTicketSelec!=null&& BigDecimalUtil.isMayorOrIgualACero(lineaTicketSelec.getCantidad().subtract(lineaTicketSelec.getCantidadDevuelta().add(lineaTicketSelec.getCantidadADevolver().add(BigDecimal.ONE))))){
                        actualizarLineasProvisionales(BigDecimal.ONE, lineaTicketSelec);
                        lineaTicketSelec.setCantidadADevolver(lineaTicketSelec.getCantidadADevolver().add(BigDecimal.ONE));
//                        lineaTicketSelec.recalcularImporteFinal(lineaTicketSelec.getCabecera().getCliente().getIdTratImpuestos());
                        cambiosRealizados = true;
                        refrescarDatosPantalla();
                        tbArticulos.getSelectionModel().select(indice);
                    }
                }
                else{
                    VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar previamente la línea que desea modificar."), this.getStage());
                }
            }
        }
    }
    
    /**
     * Acción seleccionar toda la línea para devolver
     */
    public void accionTablaAgnadirLinea() {
        
        log.debug("accionTablaAgnadirLinea() - Acción ejecutada");
        if (tbArticulos.getItems() != null) {
            int indice = tbArticulos.getSelectionModel().getSelectedIndex();
            if (indice < tbArticulos.getItems().size()) {
                LineaTicketAbonoGui lineaTablaSelec =  ((LineaTicketAbonoGui)tbArticulos.getSelectionModel().getSelectedItem());
                if(lineaTablaSelec!=null){
                    
                    LineaTicket lineaTicketSelec= ((LineaTicket)ticketManager.getTicketOrigen().getLinea(lineaTablaSelec.getIdLinea()));
                    BigDecimal cantASumar = lineaTicketSelec.getCantidad().subtract(lineaTicketSelec.getCantidadDevuelta());
                    actualizarLineasProvisionales(cantASumar, lineaTicketSelec);
                    
                    lineaTicketSelec.setCantidadADevolver(lineaTicketSelec.getCantidadADevolver().add(lineaTicketSelec.getCantidadDisponibleDevolver()));
//                    lineaTicketSelec.recalcularImporteFinal(lineaTicketSelec.getCabecera().getCliente().getIdTratImpuestos());
                    
                    cambiosRealizados = true;
                    refrescarDatosPantalla();
                    tbArticulos.getSelectionModel().select(indice);
                }
                else{
                    VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar previamente la línea que desea modificar."), this.getStage());
                }
            }
        }
    }
    
    /**
     * Acción seleccionar todo el ticket para devolver
     */
    public void accionTablaSeleccionarTodo(){
        
        log.debug("accionTablaSeleccionarTodo() - Acción ejecutada");
        if (tbArticulos.getItems() != null) {
            for(LineaTicketAbstract linea: (List<LineaTicketAbstract>)ticketManager.getTicketOrigen().getLineas()){
                if(BigDecimalUtil.isMayorACero(linea.getCantidadDisponibleDevolver())){
                	BigDecimal cantASumar = linea.getCantidad().subtract(linea.getCantidadDevuelta());
                    actualizarLineasProvisionales(cantASumar, linea);
                    linea.setCantidadADevolver(linea.getCantidadADevolver().add(linea.getCantidadDisponibleDevolver()));
//                    linea.recalcularImporteFinal(linea.getCabecera().getCliente().getIdTratImpuestos());
                    cambiosRealizados = true;
                }
            }
            refrescarDatosPantalla();
        }
    }

    /**
     * Acción mover a último registro de la tabla
     */
    protected void accionTablaUltimoRegistro() {
        log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
        if (tbArticulos.getItems() != null && tbArticulos.getItems() != null) {
            tbArticulos.getSelectionModel().select(tbArticulos.getItems().size() - 1);
            tbArticulos.scrollTo(tbArticulos.getItems().size() - 1);  // Mueve el scroll para que se vea el registro
        }
    }
    
    protected void refrescarDatosPantalla(){
        
        lineas.clear();
        for (LineaTicketAbstract lineaTicket : (List<LineaTicketAbstract>)ticketManager.getTicketOrigen().getLineas()) {
            lineas.add(new LineaTicketAbonoGui(lineaTicket));
        }
    }
    
    protected void actualizarLineasProvisionales(BigDecimal cantidadASumar, LineaTicketAbstract lineaOriginal){
        LineaProvisionalDevolucion lineaProvisional = getLineaProvisional(lineaOriginal.getIdLinea());
        
        if(lineaProvisional!=null){
            lineaProvisional.setCantADevolver(lineaProvisional.getCantADevolver().add(cantidadASumar));
        }
        else{
            lineaProvisional = new LineaProvisionalDevolucion(lineaOriginal);
            lineaProvisional.setIdLinea(lineaOriginal.getIdLinea());
            lineaProvisional.setCantADevolver(cantidadASumar);
            lineasProvisionales.add(lineaProvisional);
        }
    }
    
    protected LineaProvisionalDevolucion getLineaProvisional(int id){
        LineaProvisionalDevolucion linea = null;
        
        for(LineaProvisionalDevolucion lineaProv: lineasProvisionales){
            if(lineaProv.getIdLinea() == id){
                linea = lineaProv;
                break;
            }
        }
        
        return linea;
    }
    
    /**
     * Añade las líneas seleccionadas al ticket de devolución.
     */
	protected void facturarLineasADevolver() {

		for (LineaProvisionalDevolucion lineaProvisional : lineasProvisionales) {
			LineaTicketAbstract linea = lineaProvisional.getLineaOriginal();
			//Volvemos a poner el devolver original a 0 porque hemos ido actualizando para actualizar la interfaz
			linea.setCantidadADevolver(lineaProvisional.getCantADevolverOriginal());
			
			try {
				if(BigDecimalUtil.isMayor(lineaProvisional.getCantADevolver().add(linea.getCantidadADevolver()), linea.getCantidad())) {
					lineaProvisional.setCantADevolver(linea.getCantidad().subtract(linea.getCantidadADevolver()));
				}
				
				LineaTicket lineaDevolucion = null;
				
				if(BigDecimalUtil.isMayorACero(lineaProvisional.getCantADevolver())) {
					if (ticketManager.comprobarTarjetaRegalo(linea.getCodArticulo())) {
						lineaDevolucion = ticketManager.nuevaLineaArticulo(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), lineaProvisional.getCantADevolver(), linea.getIdLinea());
						String numTarjeta = ticketManager.getTicketOrigen().getCabecera().getTarjetaRegalo().getNumTarjetaRegalo();
	
						GiftCardBean tarjeta = giftCardService.getGiftCard(numTarjeta);
	
						// Si encuentra la tarjeta.
						if (tarjeta != null) {
							if (numTarjeta.equals(ticketManager.getTicketOrigen().getCabecera().getTarjetaRegalo().getNumTarjetaRegalo())) {
								ticketManager.getTicket().getCabecera().agnadirTarjetaRegalo(tarjeta);
								ticketManager.getTicket().getCabecera().getTarjetaRegalo().setImporteRecarga(ticketManager.getTicket().getTotales().getTotalAPagar());
								ticketManager.setEsDevolucionTarjetaRegalo(true);
							} else {
								log.warn("facturarLineasADevolver() - " + I18N.getTexto("El número de tarjeta no coincide con el de la operación original"));
								throw new TarjetaRegaloException(I18N.getTexto("El número de tarjeta no coincide con el de la operación original"));
							}
						} else {
							log.error("facturarLineasADevolver() - " + I18N.getTexto("El número de tarjeta no es válido"));
							throw new TarjetaRegaloException(I18N.getTexto("El número de tarjeta no es válido"));
						}
					} else {
						lineaDevolucion = ticketManager.nuevaLineaArticulo(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), lineaProvisional.getCantADevolver(), linea.getIdLinea());
					}
					
					lineaDevolucion.setAdmitePromociones(false);
					asignarNumerosSerie(lineaDevolucion);
				}
			} catch (LineaTicketException e) {
				log.error("facturarLineasADevolver() -Error facturando la línea a devolver: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(this.getStage(), I18N.getTexto("La línea {0} no se ha podido insertar por el siguiente motivo: ", linea.getIdLinea()) + System.lineSeparator() + e.getMessage(), e);
			} catch (TarjetaRegaloException e) {
				log.error("facturarLineasADevolver() - Error en el proceso de tarjeta regalo: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(this.getStage(), e.getMessage(), e);
			} catch (Exception e) {
				log.error("facturarLineasADevolver() - Ha habido un error al procesar la línea: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(this.getStage(), e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void asignarNumerosSerie(LineaTicket linea){
		if (linea.getArticulo().getNumerosSerie()) {
			getDatos().put(NumerosSerieController.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, ((LineaTicket) ticketManager.getTicketOrigen().getLinea(linea.getLineaDocumentoOrigen())).getNumerosSerie());
			getDatos().put(NumerosSerieController.PARAMETRO_LINEA_DOCUMENTO_ACTIVO, linea);
			getApplication().getMainView().showModalCentered(NumerosSerieView.class, getDatos(), getStage());
            List<String> numerosSerie = (List<String>) getDatos().get(NumerosSerieController.PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS);
			linea.setNumerosSerie(numerosSerie);
		}
	}
    
}
