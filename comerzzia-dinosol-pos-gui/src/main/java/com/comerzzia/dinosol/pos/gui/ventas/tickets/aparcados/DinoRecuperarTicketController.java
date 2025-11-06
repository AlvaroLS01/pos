package com.comerzzia.dinosol.pos.gui.ventas.tickets.aparcados;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.ticket.aparcados.DinoTicketsAparcadosService;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.LineaTicketAparcadoGui;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.RecuperarTicketController;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.client.exceptions.RestException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

@Component
@Primary
public class DinoRecuperarTicketController extends RecuperarTicketController {
	
	@Autowired
	private DinoTicketsAparcadosService ticketsAparcadosService;
	
	@FXML
	protected CheckBox cbMostrarTodos;
	
	@Autowired
    private Sesion sesion;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {      
		cbMostrarTodos.selectedProperty().setValue(false);

        tcCajero.prefWidthProperty().bind(tbTickets.widthProperty().divide(4));
        tcCaja.prefWidthProperty().bind(tbTickets.widthProperty().divide(4));
        tcFecha.prefWidthProperty().bind(tbTickets.widthProperty().divide(4));
        tcImporte.prefWidthProperty().bind(tbTickets.widthProperty().divide(4));
		
		if(!ticketsAparcadosService.isTicketAparcadoRemotoActivo()) {
			super.initializeForm();
		}
		else {	        
			ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);
			Long tipoDoc = (Long)getDatos().get(PARAMETRO_TIPO_DOCUMENTO);
			
			tbTickets.setItems(FXCollections.observableArrayList());
			
			new BackgroundTask<List<TicketAparcadoBean>>(){
				@Override
                protected List<TicketAparcadoBean> call() throws Exception {
	                return ticketManager.recuperarTicketsAparcados(tipoDoc);
                }
				
				@Override
				protected void succeeded() {
				    super.succeeded();
				    
				    List<TicketAparcadoBean> ticketsAparcados = getValue();
				    
				    ticketsAparcadosTabla = FXCollections.observableArrayList();
					List<LineaTicketAparcadoGui> lstTicketsAparcadoOperador = consultarTicketsPorOperador(ticketsAparcados);
					ticketsAparcadosTabla.addAll(lstTicketsAparcadoOperador);
			    	
	                tbTickets.setItems(ticketsAparcadosTabla);
	                tbTickets.getSelectionModel().selectFirst();
				}
				
				@Override
				protected void failed() {
				    super.failed();
				    
				    Throwable e = getException();
	                String message = e.getMessage();
					log.error("initializeForm() - Error cargando tickets aparcados: " + message, e);
	                
					message = I18N.getTexto("Ha habido un problema al recuperar los tickets aparcados de la caja máster: ");
	                if(e.getCause() instanceof RestException) {
	                	message = message + e.getCause().getCause().getMessage();
	                }
	                
	                if(e.getCause().getCause().getCause() instanceof ConnectException) {
	                	message = I18N.getTexto("No se ha podido establecer la comunicación con la caja máster.");
	                }
	                
	                VentanaDialogoComponent.crearVentanaError(message, getStage());
				}
			}.start();
		}
	}
	
    @FXML
    public void accionMostrarTodos(MouseEvent event) {
        log.debug("accionMostrarTodos() - Acción chequear mostrar todos");
		String mostrarTodos = (cbMostrarTodos.isSelected() ? "S" : "N");
		
		Long tipoDoc = (Long)getDatos().get(PARAMETRO_TIPO_DOCUMENTO);
		new BackgroundTask<List<TicketAparcadoBean>>(){
			@Override
            protected List<TicketAparcadoBean> call() throws Exception {
                return ticketManager.recuperarTicketsAparcados(tipoDoc);
            }
			
			@SuppressWarnings("unchecked")
            @Override
			protected void succeeded() {
			    super.succeeded();
			    
			    List<TicketAparcadoBean> ticketsAparcados = getValue();
			    
			    ticketsAparcadosTabla = FXCollections.observableArrayList();
			    if(("S").equals(mostrarTodos)){
			    	for(TicketAparcadoBean ticket: ticketsAparcados){
	                    ticketsAparcadosTabla.add(new LineaTicketAparcadoGui(ticket));
	                }
			    }else if(("N").equals(mostrarTodos)){
			    	List<LineaTicketAparcadoGui> lstTicketsAparcadoOperador = consultarTicketsPorOperador(ticketsAparcados);
			    	ticketsAparcadosTabla.addAll(lstTicketsAparcadoOperador);
			    }

                tbTickets.setItems(ticketsAparcadosTabla);
                tbTickets.getSelectionModel().selectFirst();
			}
			
			@Override
			protected void failed() {
			    super.failed();
			    
			    Throwable e = getException();
                String message = e.getMessage();
				log.error("initializeForm() - Error cargando tickets aparcados: " + message, e);
                
				message = I18N.getTexto("Ha habido un problema al recuperar los tickets aparcados de la caja máster: ");
                if(e.getCause() instanceof RestException) {
                	message = message + e.getCause().getCause().getMessage();
                }
                
                if(e.getCause().getCause().getCause() instanceof ConnectException) {
                	message = I18N.getTexto("No se ha podido establecer la comunicación con la caja máster.");
                }
                
                VentanaDialogoComponent.crearVentanaError(message, getStage());
			}
		}.start();
    }
    
    public List<LineaTicketAparcadoGui> consultarTicketsPorOperador(List<TicketAparcadoBean> ticketsAparcados){
    	List<LineaTicketAparcadoGui> listaTicketsAparcados = new ArrayList<LineaTicketAparcadoGui>();
    	String usuario = sesion.getSesionCaja().getCajaAbierta().getUsuario();
    	for(TicketAparcadoBean ticket: ticketsAparcados){
    		if(ticket.getUsuario().equals(usuario)){
        		listaTicketsAparcados.add(new LineaTicketAparcadoGui(ticket));
    		}
    	}
    	return listaTicketsAparcados;
    }
    
    @Override
    public void establecerTicketAparcadoSeleccionado() {
		LineaTicketAparcadoGui lineaSeleccionada = (LineaTicketAparcadoGui) tbTickets.getSelectionModel().getSelectedItem();
		if (lineaSeleccionada == null)
			return;

		try {
			TicketAparcadoBean ticket = lineaSeleccionada.getTicket();
			ticketManager.recuperarTicket(ticket);
			getDatos().put(PARAMETRO_TICKET_RECUPERADO, ticket);
		}
		catch (Exception e) {
			log.error("establecerTicketAparcadoSeleccionado() - Error recuperando ticket: " + e.getMessage(), e);
			
			try {
				ticketManager.nuevoTicket();
			}
			catch (Exception ex) {
				log.error("establecerTicketAparcadoSeleccionado() - Ha habido un error al limpiar el ticket: " + ex.getMessage(), ex);
			}
			
			try {
				compruebaPermisos("BORRAR TICKET APARCADO ERRÓNEO");
				
				String mensaje = I18N.getTexto("El ticket no se puede recuperar. ¿Desea eliminarlo?");
				boolean borrarTicket = VentanaDialogoComponent.crearVentanaConfirmacion(mensaje, getStage());
				
				if(borrarTicket) {
					ticketsAparcadosService.eliminarTicket(lineaSeleccionada.getTicket().getUidTicket());
				}
			}
			catch (SinPermisosException e1) {
				VentanaDialogoComponent.crearVentanaError(getStage(), "Lo sentimos, ocurrió un error al recuperar la venta. Contacte con un responsable.", e);
			}
			catch (TicketsServiceException e1) {
				log.error("establecerTicketAparcadoSeleccionado() - Ha habido un error al eliminar el ticket aparcado: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), "Ha habido un error al eliminar el ticket aparcado. Contacte con un administrador.", e);
			}
		}

		getStage().close();
    }

}
