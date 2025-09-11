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
package com.comerzzia.pos.gui.ventas.gestiontickets.ticketRegalo;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class TicketRegaloController extends WindowController implements IContenedorBotonera{

	protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TicketRegaloController.class.getName());

	public static final String PARAMETRO_TICKET_REGALO="TICKET_REGALO";
	public static final String PARAMETRO_SALIDA_CANCELAR="SALIDA_CANCELAR";

	protected Ticket ticketRegalo;

	@FXML
	protected TableView tbTicket;
	@FXML
	protected AnchorPane panelBotoneraTabla;
	@FXML
	protected AnchorPane panelBotoneraTicket;
	@FXML
	protected TableColumn tcCodArticulo, tcDescArticulo, tcCantidad, tcBtSelec, tcDesglose1, tcDesglose2;
	@FXML
	protected Button btAceptar, btCancelar;

	protected ObservableList<LineaTicketRegaloGui> lineas;
	
	@Autowired
	protected Sesion sesion;

	// botonera de acciones de tabla
	protected BotoneraComponent botoneraAccionesTabla;
	
	@Autowired
	protected VariablesServices variablesServices;

	private boolean todoSeleccionado;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		tbTicket.setPlaceholder(new Label(""));

		tcCodArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcCodArticulo", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcCantidad", null,CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcDescArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcDescArticulo", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDesglose1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcDesglose1", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDesglose2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcDesglose2", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		//tcBtSelec.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcBtSelec", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

		tcCodArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketRegaloGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketRegaloGui, String> cdf) {
				return cdf.getValue().getCodArticulo();
			}
		});
		tcDescArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketRegaloGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketRegaloGui, String> cdf) {
				return cdf.getValue().getDesArticulo();
			}
		});
		tcCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketRegaloGui, BigDecimal>, ObservableValue<BigDecimal>>() {
			@Override
			public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketRegaloGui, BigDecimal> cdf) {
				return cdf.getValue().getCantidad();
			}
		});
		tcDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketRegaloGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketRegaloGui, String> cdf) {
				return cdf.getValue().getDesglose1();
			}
		});
		tcDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketRegaloGui, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketRegaloGui, String> cdf) {
				return cdf.getValue().getDesglose2();
			}
		});

		tcBtSelec.setCellValueFactory(new PropertyValueFactory<LineaTicketRegaloGui, Boolean>("lineaSelec"));
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
            
            EventHandler<KeyEvent> evhGeneral = new EventHandler<KeyEvent>() {
                @Override
                public void handle(javafx.scene.input.KeyEvent event) {
                    KeyCodeCombination keyCode = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
                    if (keyCode.match(event)) {
                    	if (todoSeleccionado) {
                    		borrarSeleccionLineas();
                    	} else {
                    		seleccionarLineas();
                    	}
                    	if (tbTicket.isFocused()) {
                    		//Si la tabla tiene el foco, se ejecutará tanto este evento como el de accionEventoEnterTabla
                    		//por lo que quedaría la linea seleccionada al contrario que todas las demás. Para evitar este efecto, forzamos la ejecución del evento enter
                    		accionEventoEnterTabla(null);
                    	}
                    }
                }
            };
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
		ticketRegalo = (Ticket)this.getDatos().get(PARAMETRO_TICKET_REGALO);
        List<LineaTicket> lineasTicketRegalo = ticketRegalo.getLineas();
        
        lineas = FXCollections.observableList(new ArrayList<LineaTicketRegaloGui>());
        
        tbTicket.setItems(lineas);
        
        for(LineaTicket linea: lineasTicketRegalo){
        	if (!isIntegerValue(linea.getCantidad())) {
        		lineas.add(new LineaTicketRegaloGui(linea.getDesArticulo(), linea.getCantidad(),
                        linea.getIdLinea(), linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2()));
        	} else {
	            int cantidad = linea.getCantidad().intValue();
	            
	            for(int i=0;i<cantidad;i++){
	                lineas.add(new LineaTicketRegaloGui(linea.getDesArticulo(),BigDecimal.ONE,
	                        linea.getIdLinea(), linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2()));
	            }
        	}
        }
        
        seleccionarLineas();
        tbTicket.getSelectionModel().select(0);
        Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initializeFocus();
			}
		});
	}

	protected boolean isIntegerValue(BigDecimal bd) {
		return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0;
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
        }
	}
	
	@FXML
    public void accionAceptar(){
        
        boolean hayLineasSelec = false;
        
        try {
            for(LineaTicketRegaloGui lineaGui: lineas){
                if(lineaGui.isLineaSelec()){
                    LineaTicket lineaticket = (LineaTicket)ticketRegalo.getLinea(lineaGui.getIdLinea());
                    if(!lineaticket.isImprimirTicketRegalo()){
                        lineaticket.setImprimirTicketRegalo(true);
                        lineaticket.setCantidad(BigDecimal.ZERO);
                    }
                    
                    BigDecimal cantidad = lineaticket.getCantidad();
                    lineaticket.setCantidad(cantidad.add(BigDecimal.ONE));
                    hayLineasSelec = true;
                }
            }

            if(hayLineasSelec){
            	String formatoTicketRegalo = sesion.getAplicacion().
            			getDocumentos().getDocumento(ticketRegalo.getCabecera().getCodTipoDocumento()).getFormatoImpresionTicketRegalo();
            	if(formatoTicketRegalo!=null){
            		ticketRegalo.getCabecera().setFormatoImpresion(formatoTicketRegalo);

                        HashMap<String,Object> mapaParametrosTicket = new HashMap<String,Object>();
                        mapaParametrosTicket.put("ticket",ticketRegalo);
                        mapaParametrosTicket.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
                        
                    try{
                    	ServicioImpresion.imprimir(ticketRegalo.getCabecera().getFormatoImpresionTicketRegalo(), mapaParametrosTicket);  
                    }catch (DeviceException e) {
        	        	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), e);
        			}
            	}
            	else{
            		VentanaDialogoComponent.crearVentanaError("No existe un formato de ticket regalo para el tipo de documento del ticket original.", this.getStage());
            	}
            	getStage().close();
            }
            else{
            	VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay ninguna línea seleccionada."), getStage());
            }
        }catch (DocumentoException ex) {
            log.trace("No se encontró el documento del ticket regalo.",ex);
        }
    }

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
    public void accionEventoEnterTabla(String idTabla) {
        log.debug("accionEventoEnterTabla() - Acción ejecutada");
        LineaTicketRegaloGui selectedItem = (LineaTicketRegaloGui) tbTicket.getSelectionModel().getSelectedItem();
        selectedItem.setLineaSelec(!selectedItem.isLineaSelec());
    }
	
	protected void borrarSeleccionLineas(){
        log.debug("borrarSeleccionLineas() - Acción ejecutada");
        
        for(LineaTicketRegaloGui lineaTicket: lineas){
            lineaTicket.setLineaSelec(false);
        }
        todoSeleccionado = false;
    }
    
    protected void seleccionarLineas(){
        log.debug("seleccionarLineas() - Acción ejecutada");
        
        for(LineaTicketRegaloGui lineaTicket: lineas){
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
