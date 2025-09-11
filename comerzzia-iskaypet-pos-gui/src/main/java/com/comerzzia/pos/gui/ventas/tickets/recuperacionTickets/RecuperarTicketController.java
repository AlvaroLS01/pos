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


package com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

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
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;

@Component
public class RecuperarTicketController extends WindowController implements IContenedorBotonera{

    protected Logger log = Logger.getLogger(getClass());
    
    public static final String PARAMETRO_TIPO_DOCUMENTO = "TIPO_DOCUMENTO";
    public static final String PARAMETRO_TICKET_RECUPERADO = "TICKET_RECUPERADO";
    
    @FXML
    protected TableView tbTickets;
    
    @FXML
    protected Button btCancelar, btAceptar;
    
    @FXML   
    protected AnchorPane panelBotonera;
    
    protected BotoneraComponent botoneraDesplazamientoTabla;
    
    protected TicketManager ticketManager;
    
    protected ObservableList<LineaTicketAparcadoGui> ticketsAparcadosTabla;
    
    @FXML
    protected TableColumn<LineaTicketAparcadoGui,String> tcFecha;
    
    @FXML
    protected TableColumn<LineaTicketAparcadoGui,String> tcImporte;
    
    @FXML
    protected TableColumn<LineaTicketAparcadoGui,String> tcCliente;
    
    @FXML
    protected TableColumn<LineaTicketAparcadoGui,String> tcCaja;
    
    @FXML
    protected TableColumn<LineaTicketAparcadoGui,String> tcCajero;
    
    @FXML
    protected TableColumn<LineaTicketAparcadoGui,String> tcCodDoc;
    
    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcCajero.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCajero", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCliente.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCliente", 2,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCaja.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCaja", 2,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcImporte", 2,CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
        tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFechaHora("tbTickets", "tcFecha", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCodDoc.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCodDoc", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
                
        tcCajero.setCellValueFactory(new PropertyValueFactory<LineaTicketAparcadoGui,String>("cajero"));
        tcCliente.setCellValueFactory(new PropertyValueFactory<LineaTicketAparcadoGui,String>("cliente"));
        tcCaja.setCellValueFactory(new PropertyValueFactory<LineaTicketAparcadoGui,String>("caja"));
        tcFecha.setCellValueFactory(new PropertyValueFactory<LineaTicketAparcadoGui,String>("fecha"));
        tcImporte.setCellValueFactory(new PropertyValueFactory<LineaTicketAparcadoGui,String>("importe"));
        tcCodDoc.setCellValueFactory(new PropertyValueFactory<LineaTicketAparcadoGui,String>("codDoc"));
        
        tcCajero.prefWidthProperty().bind(tbTickets.widthProperty().divide(6));
        tcCliente.prefWidthProperty().bind(tbTickets.widthProperty().divide(6));
        tcCaja.prefWidthProperty().bind(tbTickets.widthProperty().divide(6));
        tcFecha.prefWidthProperty().bind(tbTickets.widthProperty().divide(6));
        tcCodDoc.prefWidthProperty().bind(tbTickets.widthProperty().divide(6)); 
        tcImporte.prefWidthProperty().bind(tbTickets.widthProperty().divide(6));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initializeComponents() {
        
        registrarAccionCerrarVentanaEscape();
        
        List<ConfiguracionBotonBean> listaAccionesTabla = BotoneraComponent.cargarAccionesTablaSimple();
        
        tbTickets.setPlaceholder(new Label(""));
        try {
            botoneraDesplazamientoTabla = new BotoneraComponent(4, 1, this, listaAccionesTabla, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotonera.getChildren().clear();
            panelBotonera.getChildren().add(botoneraDesplazamientoTabla);
        }
        catch (CargarPantallaException ex) {
            log.error("Error cargando pantalla.", ex);
        }
        
        tbTickets.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initializeForm() throws InitializeGuiException {
        ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);
        Long tipoDoc = (Long)getDatos().get(PARAMETRO_TIPO_DOCUMENTO);
        if (ticketManager.isTicketVacio()) {
            try {
                // Solo si el ticket esta vacío, podemos recuperar el ticket
                List<TicketAparcadoBean> ticketsAparcado = ticketManager.recuperarTicketsAparcados(tipoDoc);
                ticketsAparcadosTabla = FXCollections.observableArrayList();
                
                for(TicketAparcadoBean ticket: ticketsAparcado){
                    ticketsAparcadosTabla.add(new LineaTicketAparcadoGui(ticket));
                }
                tbTickets.setItems(ticketsAparcadosTabla);
            }
            catch (TicketsServiceException ex) {
                log.error("initializeForm() - Error cargando tickets aparcados: " + ex.getMessage(), ex);
            }
        }

        accionTablaPrimerRegistro();       
    }

    @Override
    public void initializeFocus() {
    	tbTickets.requestFocus();
    	tbTickets.getSelectionModel().select(0);
    	tbTickets.getFocusModel().focus(0);
    }

    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
        log.trace("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
        
        switch(botonAccionado.getClave()){
            // BOTONERA TABLA MOVIMIENTOS
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
        }
    }
    
    /**
     * Acción mover a primer registro de la tabla
     */
    @FXML
    protected void accionTablaPrimerRegistro() {
        log.debug("accionTablaPrimerRegistro() - Acción ejecutada");
        if (tbTickets.getItems() != null) {
            tbTickets.getSelectionModel().select(0);
            tbTickets.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }

    /**
     * Acción mover a anterior registro de la tabla
     */
    @FXML
    protected void accionTablaAnteriorRegistro() {
        log.debug("accionTablaAnteriorRegistro() - Acción ejecutada");
        if (tbTickets.getItems() != null && tbTickets.getItems() != null) {
            int indice = tbTickets.getSelectionModel().getSelectedIndex();
            if (indice > 0) {
                tbTickets.getSelectionModel().select(indice - 1);
                tbTickets.scrollTo(indice - 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a siguiente registro de la tabla
     */
    @FXML
    protected void accionTablaSiguienteRegistro() {
        log.debug("accionIrSiguienteRegistroTabla() - Acción ejecutada");
        if (tbTickets.getItems() != null && tbTickets.getItems() != null) {
            int indice = tbTickets.getSelectionModel().getSelectedIndex();
            if (indice < tbTickets.getItems().size()) {
                tbTickets.getSelectionModel().select(indice + 1);
                tbTickets.scrollTo(indice + 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a último registro de la tabla
     */
    @FXML
    protected void accionTablaUltimoRegistro() {
        log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
        if (tbTickets.getItems() != null && tbTickets.getItems() != null) {
            tbTickets.getSelectionModel().select(tbTickets.getItems().size() - 1);
            tbTickets.scrollTo(tbTickets.getItems().size() - 1);  // Mueve el scroll para que se vea el registro
        }
    }
    
    /**
     * Accion para tratar el doble click en una de las filas de la pantalla
     *
     * @param event
     */
    @FXML
    public void accionTablaAceptarTicket(MouseEvent event) {
        log.debug("accionTablaAceptarCliente() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                establecerTicketAparcadoSeleccionado();
            }
        }
    }
    
    public void establecerTicketAparcadoSeleccionado(){
        try {
            LineaTicketAparcadoGui lineaSeleccionada = (LineaTicketAparcadoGui)tbTickets.getSelectionModel().getSelectedItem();
            if(lineaSeleccionada!=null){
                TicketAparcadoBean ticket = lineaSeleccionada.getTicket();
                ticketManager.recuperarTicket(getStage(), ticket);
                getDatos().put(PARAMETRO_TICKET_RECUPERADO, ticket);
                getStage().close();
            }
        }
        catch (TicketsServiceException | PromocionesServiceException | DocumentoException | LineaTicketException ex) {
        	getStage().close();
        	log.error("Error recuperando ticket",ex);
        	VentanaDialogoComponent.crearVentanaError(getStage(), "Lo sentimos, ocurrió un error al recuperar el documento.", ex);
        }
    }
    
    public void aceptarArticuloTeclado(KeyEvent event) {
        log.trace("aceptarArticuloTeclado(KeyEvent event) - Aceptar");
        if (event.getCode() == KeyCode.ENTER) {
            establecerTicketAparcadoSeleccionado();
        }
    }
}
