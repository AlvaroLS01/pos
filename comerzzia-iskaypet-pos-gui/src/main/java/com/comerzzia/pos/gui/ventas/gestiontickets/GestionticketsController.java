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
    
package com.comerzzia.pos.gui.ventas.gestiontickets;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.InsertarApunteView;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoController;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoView;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionTicketView;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionticketsController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class GestionticketsController extends Controller implements Initializable, IContenedorBotonera {

    public static final String PARAMETRO_ENTRADA_TIPO_DOC = "TIPO_DOC";
    
    private static final Logger log = Logger.getLogger(GestionticketsController.class.getName());
    
    @Autowired
    private Sesion sesion;
    
    @Autowired
    private LocatorManager locatorManager;
    
    @FXML
    protected DatePicker tfFecha;

    @FXML
    protected TextField tfTicket, tfCaja, tfCodDoc, tfDesDoc, tfLocalizador;

    @FXML
    protected AnchorPane panelMenuTabla;

    @FXML
    protected Button btBuscarDoc, btnBuscar;

    @FXML
    protected TableView<GestionTicketGui> tbTickets;

    @FXML
    protected TableColumn tcCaja, tcFecha, tcTicket, tcTipoDoc;

    @FXML
    protected Label lbError;
    
    protected Label lbSinResultados;

    // 
    protected ObservableList<GestionTicketGui> tickets;
    protected BotoneraComponent botoneraAccionesTabla;

    //Formulario de validación
    protected FormularioGestionTicketBean frGestionTicket;
    
    protected List<String> tiposDocValidos;
    protected List<Long> idTiposDocValidos;
    
    @Autowired
    private TicketsService ticketsService;
	
    final IVisor visor = Dispositivos.getInstance().getVisor();

    // <editor-fold defaultstate="collapsed" desc="Creación e inicialización"> 
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Creamos el validador de los datos introducidos del ticket
        frGestionTicket = SpringContext.getBean(FormularioGestionTicketBean.class);
        frGestionTicket.setFormField("codCaja", tfCaja);
        frGestionTicket.setFormField("codTicket", tfTicket);
        frGestionTicket.setFormField("fecha", tfFecha);
        frGestionTicket.setFormField("idDoc", tfCodDoc);

        //Mensaje sin contenido para tabla. los establecemos a vacio
        tbTickets.setPlaceholder(new Label(""));

        // Inicializamos las celdas de las tablas
        tcCaja.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCaja", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFechaHora("tbTickets", "tcFecha", 2,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcTicket.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcTicket", 2,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcTipoDoc.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTicket", "tcTipoDoc", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

        // Indicamos los campos de las tablas        
        tcFecha.setCellValueFactory(new PropertyValueFactory<GestionTicketGui, Date>("fecha"));
        tcCaja.setCellValueFactory(new PropertyValueFactory<GestionTicketGui, String>("caja"));
        tcTicket.setCellValueFactory(new PropertyValueFactory<GestionTicketGui, Long>("idTicket"));
        tcTipoDoc.setCellValueFactory(new PropertyValueFactory<GestionTicketGui, String>("desDoc"));
        
        crearEventoEnterTabla(tbTickets);
        crearEventoNavegacionTabla(tbTickets);
    }

    @Override
    public void initializeComponents() {

        try {
            log.debug("inicializarComponentes() - Inicialización de componentes");

            log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de tickets");
            List<ConfiguracionBotonBean> listaAccionesTablaVen = BotoneraComponent.cargarAccionesTablaSimple();
            listaAccionesTablaVen.add(new ConfiguracionBotonBean("iconos/view.png", null, null, "ACCION_VER", "REALIZAR_ACCION"));
            botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesTablaVen, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelMenuTabla.getChildren().clear();
            panelMenuTabla.getChildren().add(botoneraAccionesTabla);
            
            tfCodDoc.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){
                    procesarTipoDoc();
                }
            }});
            
            cargarAccionDatePicker();
            
            lbSinResultados = new Label(I18N.getTexto("No se han encontrado resultados"));
        }
        catch (CargarPantallaException ex) {
            log.error("inicializarComponentes() - Error inicializando pantalla de gestion de tickets");
            VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
        }

    }

    @Override
    public void initializeForm() throws InitializeGuiException {
    	visor.limpiar();
    	
        // Por defecto la caja será la caja configurada
        tfCaja.setText(sesion.getAplicacion().getCodCaja());
        lbError.setText("");
        tfFecha.setSelectedDate(new Date());
        
        try {
            if(getDatos().containsKey(PARAMETRO_ENTRADA_TIPO_DOC)){
                
                String codDoc = (String)getDatos().get(PARAMETRO_ENTRADA_TIPO_DOC);
                
                TipoDocumentoBean doc = sesion.getAplicacion().getDocumentos().getDocumento(codDoc);
                tfCodDoc.setText(doc.getCodtipodocumento());
                tfDesDoc.setText(doc.getDestipodocumento());
            }
            else{
                tfCodDoc.setText("");
                tfDesDoc.setText("");
            }
            
        	List<TipoDocumentoBean> tiposDocs = new ArrayList<TipoDocumentoBean>(sesion.getAplicacion().getDocumentos().getDocumentos().values());
        	tiposDocValidos = new ArrayList<String>();
        	idTiposDocValidos = new ArrayList<>();
        	for(TipoDocumentoBean doc:tiposDocs){
                if(!doc.getFormatoImpresion().equals(TipoDocumentoBean.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)){
        			tiposDocValidos.add(doc.getCodtipodocumento());
        			idTiposDocValidos.add(doc.getIdTipoDocumento());
        		}
        	}
        }
        catch (DocumentoException ex) {
            log.error("Error buscando el documento por defecto", ex);
        }

        refrescarDatosPantalla();
    }

    @Override
    public void initializeFocus() {
        tfLocalizador.requestFocus();
    }

    // </editor-fold>

    public void refrescarDatosPantalla() {
        tickets = FXCollections.observableList(new ArrayList<GestionTicketGui>());
        tbTickets.setItems(tickets);
    }

    public void accionVerTicket() {
    	getApplication().getMainView().showModalCentered(InsertarApunteView.class, this.getStage());
    }

    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
        log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
        switch (botonAccionado.getClave()) {
            // MENU PRINCIPAL DE GESTIÓN DE TICKETS
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionTablaPrimerRegistro(tbTickets);
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionTablaIrAnteriorRegistro(tbTickets);
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionTablaIrSiguienteRegistro(tbTickets);
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
            	accionTablaUltimoRegistro(tbTickets);
            	break;
            case "ACCION_VER":
                mostrarTicket();
                break;
            default:
                log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
                break;
        }
    }

    @FXML
    public void accionBuscarEnter(KeyEvent event) {
        log.trace("accionBuscarTeclado()");
        
        if (event.getCode() == KeyCode.ENTER) {
            accionBuscar();
        }
    }
    
    public void accionBuscar() {
        log.trace("accionBuscar()");
        
        tickets.clear();
        tbTickets.setPlaceholder(new Label(""));
        
        if(StringUtils.isNotBlank(tfLocalizador.getText())){
        	log.debug("accionIntroTfLocalizador()");
        	
    		try {
    			String localizador = tfLocalizador.getText().trim();
    			log.debug("accionIntroTfLocalizador() - Realizando búsqueda con localizador = "+localizador);
    			locatorManager.decode(localizador);

    			List<TicketBean> ticketsBean = ticketsService.consultarTicketLocalizador(localizador, idTiposDocValidos);
    			Collections.sort(ticketsBean);

    			if(ticketsBean.isEmpty()){
    				tbTickets.setPlaceholder(lbSinResultados);
    				tfLocalizador.requestFocus();
		    		tfLocalizador.selectAll();
    			}
    			else{
    				for (TicketBean ticketBean : ticketsBean) {
    					GestionTicketGui ticketGui = new GestionTicketGui(ticketBean);
    					tickets.add(ticketGui);
    				}
    				tfLocalizador.setText("");	
                	tbTickets.requestFocus();
                	tbTickets.getSelectionModel().select(0);
                	tbTickets.getFocusModel().focus(0);
    			}
    			    			
    		} catch (TicketsServiceException e1) {
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pudo obtener el ticket con el localizador introducido."), this.getStage());
    			log.error("Error en la búsqueda del ticket con el localizador.", e1);
    			tfLocalizador.requestFocus();
	    		tfLocalizador.selectAll();
    		}
    		catch (Exception e1) {
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Se produjo un error procesando el localizador. " + e1.getMessage()), this.getStage());
    			log.error("Error procesando el localizador ", e1);
    			tfLocalizador.requestFocus();
	    		tfLocalizador.selectAll();
    		}
    		
        }else{
	        frGestionTicket.setCodCaja(tfCaja.getText());
	        frGestionTicket.setCodTicket(tfTicket.getText().equals("") ? null : tfTicket.getText());
	        frGestionTicket.setFecha(tfFecha.getTexto());
	        frGestionTicket.setIdDoc(tfCodDoc.getText().equals("") ? null : tfCodDoc.getText());
	        
	        tfTicket.deselect();
	        if(validarDatosFormulario()){
	            new BuscarTask().start();
	        }
        }
    }
    protected class BuscarTask extends BackgroundTask<List<TicketBean>>{

        @Override
        protected List<TicketBean> call() throws Exception {
        	
        	String codDoc = frGestionTicket.getIdDoc();
        	Long idDoc = null;
        	
        	if(codDoc != null){
        		idDoc = sesion.getAplicacion().getDocumentos().getDocumento(codDoc).getIdTipoDocumento();
        	}
        	
        	return ticketsService.consultarTickets(frGestionTicket.getCodCaja(), frGestionTicket.getIdTicketAsLong(), frGestionTicket.getFechaAsDate(), idDoc, idTiposDocValidos);
        }

        @Override
        protected void succeeded() {
            //Ordenamos la lista de tickets obtenida por la fecha de los mismos
            List<TicketBean> ticketsBean = getValue();
            Collections.sort(ticketsBean);
          
            if(ticketsBean.isEmpty()){
            	tbTickets.setPlaceholder(lbSinResultados);	
            	tfTicket.requestFocus();
            	tfTicket.selectAll();
            }
            else{
            	for (TicketBean ticketBean : ticketsBean) {
            		GestionTicketGui ticketGui = new GestionTicketGui(ticketBean);
            		tickets.add(ticketGui);
            	}
            	//seleccionamos el primer registro del resultado
            	tbTickets.requestFocus();
            	tbTickets.getSelectionModel().select(0);
            	tbTickets.getFocusModel().focus(0);
            }

            super.succeeded();
        }

        @Override
        protected void failed() {
            VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessageI18N(), getCMZException());
            tfTicket.requestFocus();
        	tfTicket.selectAll();
            super.failed();
        }
    }
    
    /**
     * Valida los valores introducidos para buscar el ticket
     *
     * @return
     */
    protected boolean validarDatosFormulario() {

        boolean valido;

        // Limpiamos los errores que pudiese tener el formulario
        frGestionTicket.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<FormularioGestionTicketBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frGestionTicket);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioGestionTicketBean> next = constraintViolations.iterator().next();
            frGestionTicket.setErrorStyle(next.getPropertyPath(), true);
            frGestionTicket.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            valido = false;
        }
        else {
            valido = true;
        }

        return valido;
    }

    /**
     * La acción Enter muestra pantalla de visualización del ticket
     *
     * @param idTabla
     */
    public void accionEventoEnterTabla(String idTabla) {
        log.debug("accionEventoEnterTabla() - Acción ejecutada");
        mostrarTicket();
    }

    /**
     * Gestiona el evento de doble click en un registro de la tabla
     *
     * @param event
     */
    @FXML
    public void accionDobleClickTicket(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                mostrarTicket();
            }
        }
    }

    /**
     * Muestra el ticket seleccionado de la tabla
     */
    protected void mostrarTicket() {
        if (tbTickets.getItems() != null && tbTickets.getItems() != null) {
            GestionTicketGui lineaTicket = tbTickets.getSelectionModel().getSelectedItem();
            if (lineaTicket != null) {
                HashMap<String, Object> datosTicket = new HashMap<String, Object>();
                datosTicket.put(DetalleGestionticketsController.CLAVE_PARAMETRO_TICKETS, tbTickets.getItems());
                datosTicket.put(DetalleGestionticketsController.CLAVE_PARAMETRO_POSICION_TICKET, tbTickets.getSelectionModel().getSelectedIndex());
                if(lineaTicket.getTicketXML()!=null){
                	datosTicket.put(DetalleGestionticketsController.CLAVE_PARAMETRO_TICKET_XML, lineaTicket.getTicketXML());
                }

                getApplication().getMainView().showModalCentered(DetalleGestionTicketView.class, datosTicket, this.getStage());
            }
        }
    }

    @Override
    public Scene getScene() {
        return getApplication().getScene();
    }

    @FXML
    public void accionBuscarTipoDoc(){
        
        datos = new HashMap();
        datos.put(TipoDocumentoController.PARAMETRO_ENTRADA_POSIBLES_DOCS, tiposDocValidos);
        getApplication().getMainView().showModalCentered(TipoDocumentoView.class, datos, this.getStage());
        
        if(datos.containsKey(TipoDocumentoController.PARAMETRO_SALIDA_DOC)){
            TipoDocumentoBean o = (TipoDocumentoBean)datos.get(TipoDocumentoController.PARAMETRO_SALIDA_DOC);
            tfDesDoc.setText(o.getDestipodocumento());
            tfCodDoc.setText(o.getCodtipodocumento());
        }
    }
    
    protected void procesarTipoDoc(){
        
        String codDoc = tfCodDoc.getText();
        
        if(!codDoc.trim().isEmpty()){
            try {
                TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(codDoc);
                tfCodDoc.setText(documento.getCodtipodocumento());
                tfDesDoc.setText(documento.getDestipodocumento());
            }
            catch (DocumentoException ex) {
                log.error("Error obteniendo el tipo de documento", ex);
                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El tipo de documento indicado no existe en la base de datos."), getStage());
                tfCodDoc.setText("");
                tfDesDoc.setText("");
            }
            catch(NumberFormatException nfe){
                log.error("El id de documento introducido no es válido.", nfe);
                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El id introducido no es válido."), getStage());
                tfCodDoc.setText("");
                tfDesDoc.setText("");
            }
        }
        else{
            tfDesDoc.setText("");
        }
    }
    
    protected void cargarAccionDatePicker() {
    	tfFecha.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent action) {
				accionBuscar();
			}
		});
    }
}
