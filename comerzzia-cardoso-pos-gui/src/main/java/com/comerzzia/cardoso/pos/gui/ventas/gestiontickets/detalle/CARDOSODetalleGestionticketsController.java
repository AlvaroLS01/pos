package com.comerzzia.cardoso.pos.gui.ventas.gestiontickets.detalle;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicketProfesional;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionticketsController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.application.Platform;

@Component
@Primary
public class CARDOSODetalleGestionticketsController extends DetalleGestionticketsController {

	private static final Logger log = Logger.getLogger(CARDOSODetalleGestionticketsController.class.getName());
	@SuppressWarnings("rawtypes")
	@Override
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
            
            ticketOperacion = (TicketVenta) MarshallUtil.leerXML(ticketXML, getTicketClassesCardoso(documento).stream().toArray(Class[]::new)); 
            
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
	
	
	public List<Class<?>> getTicketClassesCardoso(TipoDocumentoBean tipoDocumento) {
        List<Class<?>> classes = new LinkedList<>();
        Class<?> clazz = SpringContext.getBean(getTicketClass(tipoDocumento)).getClass();

        Class<?> superClass = clazz.getSuperclass();
        while (!superClass.equals(Object.class)) {
            classes.add(superClass);
            superClass = superClass.getSuperclass();
        }

        Collections.reverse(classes);

        classes.add(clazz);
        classes.add(CARDOSOCabeceraTicket.class);
        classes.add(CARDOSOLineaTicket.class);
		classes.add(CARDOSOLineaTicketProfesional.class);
        return classes;
    }
}
