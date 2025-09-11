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

package com.comerzzia.pos.gui.ventas.gestiontickets.detalle;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
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
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionTicketGui;
import com.comerzzia.pos.gui.ventas.gestiontickets.ticketRegalo.TicketRegaloController;
import com.comerzzia.pos.gui.ventas.gestiontickets.ticketRegalo.TicketRegaloView;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

@Controller
public class DetalleGestionticketsController extends WindowController implements Initializable, IContenedorBotonera {

    private static final Logger log = Logger.getLogger(DetalleGestionticketsController.class.getName());

    //Claves para obtener los parámetros que se le pasan al controlador al crear la ventana
    public static final String CLAVE_PARAMETRO_POSICION_TICKET = "posicionActual";
    public static final String CLAVE_PARAMETRO_TICKETS = "tickets";
    public static final String CLAVE_PARAMETRO_TICKET_XML = "ticket_xml";

    @FXML
    protected WebView taTicket;

    @FXML
    protected Button btnImprimirCopia, btnTicketRegalo, btnVolver;

    @FXML
    protected AnchorPane panelMenuTabla;

    // Ticket a mostrar en pantalla
    protected GestionTicketGui ticket;
    
    protected ITicket ticketOperacion;

    protected ObservableList<GestionTicketGui> tickets;
    protected int posicionActual;

    protected BotoneraComponent botoneraAccionesTabla;
    
    @Autowired
    protected VariablesServices variablesService;
    @Autowired
    protected TicketsService ticketsService;
    @Autowired
    protected ArticulosService articulosService;
    
    @Autowired
    protected Sesion sesion;

    // <editor-fold defaultstate="collapsed" desc="Creación e inicialización"> 
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initializeComponents() {
        log.debug("inicializarComponentes() - Inicialización de componentes");
        try {
            log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de tickets");
            List<ConfiguracionBotonBean> listaAccionesTablaVen = BotoneraComponent.cargarAccionesTablaSimple();
            botoneraAccionesTabla = new BotoneraComponent(5, 1, this, listaAccionesTablaVen, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelMenuTabla.getChildren().clear();
            panelMenuTabla.getChildren().add(botoneraAccionesTabla);

            //Se registra el evento de escape para salir de la ventana
            registrarAccionCerrarVentanaEscape();
        }
        catch (CargarPantallaException ex) {
            log.error("inicializarComponentes() - Error inicializando pantalla de gestiond e tickets");
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), getStage());
        }

    }

    @Override
    public void initializeForm() throws InitializeGuiException {
    	//Se obtienen los datos que llegan como parámetros al controlador
        if (this.getDatos().containsKey(CLAVE_PARAMETRO_POSICION_TICKET)) {
            posicionActual = (int) this.getDatos().get(CLAVE_PARAMETRO_POSICION_TICKET);
        }
        else {
            posicionActual = 0;
        }
        if (this.getDatos().containsKey(CLAVE_PARAMETRO_TICKETS)) {
            tickets = (ObservableList<GestionTicketGui>) this.getDatos().get(CLAVE_PARAMETRO_TICKETS);
        }
        else {
            tickets = FXCollections.observableArrayList();
        }
        // Por defecto la caja será la caja configurada        
        refrescarDatosPantalla();        
    }

    @Override
    public void initializeFocus() {
        btnImprimirCopia.requestFocus();
    }

    // </editor-fold>

    public void refrescarDatosPantalla() throws InitializeGuiException {
        try {
            log.debug("refrescarDatosPantalla()");

            log.debug("Obtenemos el XML del ticket que queremos visualizar");
            
            this.ticket = tickets.get(posicionActual);

            TicketBean ticketConsultado = null;
            byte[] ticketXML = null;

        	ticketConsultado = ticketsService.consultarTicket(ticket.getUidTicket(), sesion.getAplicacion().getUidActividad());
        	ticketXML = ticketConsultado.getTicket();
            	
            TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ticketConsultado.getIdTipoDocumento());
            if(documento.getFormatoImpresion().equals(TipoDocumentoBean.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)){
            	if(getStage() != null && getStage().isShowing()){
            		VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No es posible visualizar este tipo de documento"), getStage());
            	}else{
            		Platform.runLater(new Runnable() {
						@Override
						public void run() {
							VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No es posible visualizar este tipo de documento"), getStage());
						}
					});
            	}
            	setTicketText("<html><body></body></html>");
            	return;
            }
            
            ticketOperacion = (TicketVenta) MarshallUtil.leerXML(ticketXML, getTicketClass(documento)); 
            
            if (ticketOperacion != null) {
            	ticketOperacion.getCabecera().setDocumento(sesion.getAplicacion().getDocumentos().getDocumento(ticketOperacion.getCabecera().getTipoDocumento()));
            	if(sesion.getAplicacion().getDocumentos().getDocumento(ticketOperacion.getCabecera().getTipoDocumento()).getPermiteTicketRegalo()){
            		btnTicketRegalo.setDisable(false);
            	}
            	else{
            		btnTicketRegalo.setDisable(true);
            	}  
            	try{
	                Map<String,Object> mapaParametros= new HashMap<String,Object>();
	                mapaParametros.put("ticket",ticketOperacion);
	                mapaParametros.put("urlQR", variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
	                mapaParametros.put("esGestion", true);
	                // Hay que obtener el resultado de mostrar en pantalla el ticket y mostrarlo en taTicket
	            	String previsualizacion = ServicioImpresion.imprimirPantalla(ticketOperacion.getCabecera().getFormatoImpresion(),mapaParametros);
	            	setTicketText(previsualizacion);	                
            	}catch (Exception e) {
        			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), e);
        			throw new InitializeGuiException(false);
        		}
            }
            else {
                log.error("refrescarDatosPantalla()- Error leyendo ticket otriginal");
                VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error leyendo información de ticket."), getStage());
                throw new InitializeGuiException(false);
            }

        }
        catch (TicketsServiceException ex) {
            log.error("refrescarDatosPantalla() - " + ex.getMessage());
            VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error leyendo información de ticket"), ex);
        } catch (DocumentoException e) {
			log.error("Error recuperando el tipo de documento del ticket.",e);
		}
    }

    @SuppressWarnings({ "unchecked" })
	public Class<? extends ITicket> getTicketClass( TipoDocumentoBean tipoDocumento) {
    	String claseDocumento = tipoDocumento.getClaseDocumento();
    	if(claseDocumento != null){
    		try {
				return (Class<? extends ITicket>) Class.forName(claseDocumento);
			} catch (ClassNotFoundException e) {
				log.error(String.format("getTicketClass() - Clase %s no encontrada, devolveremos TicketVentaAbono", claseDocumento));
			}
    	}
		return TicketVentaAbono.class;
	}
    
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
        log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
        switch (botonAccionado.getClave()) {
            // MENU PRINCIPAL DE GESTIÓN DE TICKETS
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionPrimerTicket();
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionAnteriorTicket();
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionSiguienteTicket();
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                accionUltimoTicket();
                break;
            default:
                log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
                break;
        }
    }

    @FXML
    protected void accionImprimirCopia(ActionEvent event) {

        try {
//            TicketBean ticketConsultado = TicketsService.consultarTicket(ticket.getUidTicket());
//            Ticket ticketOriginal = (Ticket) MarshallUtil.leerXML(ticketConsultado.getTicket(), Ticket.class);
//            // Se reimprime la misma
            Map<String,Object> mapaParametros= new HashMap<String,Object>();
            mapaParametros.put("ticket",ticketOperacion);
            mapaParametros.put("urlQR", variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
            mapaParametros.put("esCopia", true);
            
            ServicioImpresion.imprimir(ticketOperacion.getCabecera().getFormatoImpresion(), mapaParametros);
        }
        catch (DeviceException ex) {
            VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Fallo al imprimir ticket."), getStage());
        }
    }

    @FXML
    protected void accionTicketRegalo(ActionEvent event) {  	
    	String formatoTicketRegalo;
		try {
			formatoTicketRegalo = sesion.getAplicacion().getDocumentos().getDocumento(ticketOperacion.getCabecera().getCodTipoDocumento()).getFormatoImpresionTicketRegalo();
			
			if(formatoTicketRegalo!=null){
		        HashMap<String,Object> datos = new HashMap<String,Object>();
		        datos.put(TicketRegaloController.PARAMETRO_TICKET_REGALO,ticketOperacion);
		        getApplication().getMainView().showModalCentered(TicketRegaloView.class, datos, this.getStage());
		        HashMap<String,Object> datosSalida = datos;
		        if(!datosSalida.containsKey(TicketRegaloController.PARAMETRO_SALIDA_CANCELAR)){
		            this.getStage().close();
		        }  
	    	}
			else{
				VentanaDialogoComponent.crearVentanaError("No existe un formato de ticket regalo para el tipo de documento del ticket original.", this.getStage());
			}
				
		} catch (DocumentoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Establece el ticket en pantalla     
    public GestionTicketGui getTicket() {
        log.debug("getTicket()");
        return ticket;
    }

    /**
     * Setea los datos de la pantalla
     *
     * @param tickets
     * @param posicionActual
     */
//    public void setObjetosPantalla(ObservableList<GestionTicketGui> tickets, int posicionActual) {
//        log.debug("setTicket()");
//        this.posicionActual = posicionActual;
//        this.tickets = tickets;
//        this.ticket = tickets.get(posicionActual);
//        // leemos el xml del ticket actual en refresca datos pantalla
//        dsadas;
//        
//        refrescarDatosPantalla();
//    }
    // <editor-fold defaultstate="collapsed" desc="AccionesMenu"> 
    public void accionPrimerTicket() {
    	taTicket.getEngine().executeScript("window.scrollTo(0, 0);");
    }

    public void accionSiguienteTicket() {
    	taTicket.getEngine().executeScript("window.scrollTo(0, window.pageYOffset + document.body.clientHeight);");
    }

    public void accionAnteriorTicket() {
    	taTicket.getEngine().executeScript("window.scrollTo(0, window.pageYOffset - document.body.clientHeight);");
    }

    public void accionUltimoTicket() {
    	taTicket.getEngine().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    protected void setTicketText(String ticket) {
    	taTicket.getEngine().loadContent(ticket);    	
    }

    // </editor-fold>
}