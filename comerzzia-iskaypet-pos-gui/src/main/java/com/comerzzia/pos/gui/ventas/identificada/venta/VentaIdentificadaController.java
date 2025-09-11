package com.comerzzia.pos.gui.ventas.identificada.venta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.TicketVentaDocumentoVisorConverter;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class VentaIdentificadaController extends FacturacionArticulosController {
	
	public static final String PARAMETRO_CLIENTE = "CLIENTE";
	public static final String PARAMETRO_TICKET_MANAGER = "TICKET_MANAGER";

	protected ClienteBean cliente;
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;

	@Autowired
	protected TicketVentaDocumentoVisorConverter visorConverter;

	@Override
	public void cancelarVenta() {
		log.debug("cancelarVenta()");
		try {
			boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar todas las líneas del ticket?"), getStage());
			if (!confirmacion) {
				return;
			}
			ticketManager.eliminarTicketCompleto();
			if(cliente != null) {
				((TicketVenta) ticketManager.getTicket()).setCliente(cliente);
			}
            refrescarDatosPantalla();
            getApplication().getMainView().close();
			initializeFocus();
			tbLineas.getSelectionModel().clearSelection();

			visor.escribirLineaArriba(I18N.getTexto("---NUEVA VENTA---"));
			visor.modoEspera();
		}
		catch (TicketsServiceException | PromocionesServiceException | DocumentoException ex) {
			log.error("accionAnularTicket() - Error inicializando nuevo ticket: " + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
		}
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		
		if(getDatos().get(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE) != null) {
			cliente = (ClienteBean) getDatos().get(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE);
			ticketManager.getTicket().setCliente(cliente);
		}
		
		TicketManager ticketManager = (TicketManager) getDatos().get(PARAMETRO_TICKET_MANAGER);
		if(ticketManager != null) {
			this.ticketManager = ticketManager;
			visor.escribir(I18N.getTexto("TOTAL A PAGAR"), this.ticketManager.getTicket().getTotales().getTotalAsString());
			visor.modoVenta(visorConverter.convert(((TicketVentaAbono) this.ticketManager.getTicket())));
			guardarCopiaSeguridad();
		}
		
		refrescarDatosPantalla();
		
		lbCodCliente.setText((this.ticketManager.getTicket()).getCliente().getCodCliente());
		lbDesCliente.setText((this.ticketManager.getTicket()).getCliente().getDesCliente());
	}
	
	@Override
	protected void consultarCopiaSeguridad() throws DocumentoException, TicketsServiceException {
		// Se elimina la consulta para pasarla a la pantalla de identificación
	}
	
	@Override
	public boolean canClose() {
		int numLineas = tbLineas.getItems().size();
		if (numLineas > 0) {
			try {
				super.compruebaPermisos(PERMISO_CANCELAR_VENTA);
				// Tiene permisos para cancelar, notificar que debe cerrar primero
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Existen tickets pendientes de confirmar. Antes debería finalizar la operación."), getStage());
			}
			catch (SinPermisosException ex) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Tiene tickets pendientes de confirmar. No tiene permisos para cancelar la venta."), getStage());
			}
			return false;
		}

		visor.escribirLineaArriba(I18N.getTexto("---CAJA CERRADA---"));
		visor.modoEspera();

		return true;
	}
	
	@Override
	public void aparcarTicket() {
		log.debug("aparcarTicket()");
		if (!ticketManager.isTicketVacio()) { // Si el ticket no es vacío se puede aparcar
			try {
				log.debug("accionAparcarTicket()");
				
				// Se borra la copia de seguridad para que no de fallos de violación de claves al guardar el ticket aparcado en la misma tabla
		        copiaSeguridadTicketService.guardarBackupTicketActivo(new TicketVentaAbono());
				
				// Comprobamos que el ticket tiene almenos un artículo
				ticketManager.aparcarTicket();
				
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("El ticket ha sido aparcado."), getStage());

				try {
					getView().loadAndInitialize();
	                getApplication().getMainView().close();
	                guardarCopiaSeguridad();

					visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
					visor.modoEspera();
				}
				catch (InitializeGuiException e) {
					VentanaDialogoComponent.crearVentanaError(getStage(), e);
				}
			}
			catch (TicketsServiceException ex) {
				log.error("accionAparcarTicket()");
				VentanaDialogoComponent.crearVentanaError(this.getScene().getWindow(), ex.getMessageDefault(), ex);
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo."), this.getScene().getWindow());
		}
	}
	
	@Override
	public void comprobarPermisosUI() {
		super.comprobarPermisosUI();
		botonera.comprobarPermisosOperaciones();
		try {
			super.compruebaPermisos(PERMISO_BORRAR_LINEA);
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_BORRAR_REGISTRO", false);
		}
		catch (SinPermisosException ex) {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_BORRAR_REGISTRO", true);
		}
		try {
			super.compruebaPermisos(PERMISO_MODIFICAR_LINEA);
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", false);
		}
		catch (SinPermisosException ex) {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", true);
		}
		try {
			super.compruebaPermisos(PERMISO_DEVOLUCIONES);
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_NEGAR_REGISTRO", false);
		}
		catch (SinPermisosException ex) {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_NEGAR_REGISTRO", true);
		}
	}
	
	@Override
	protected void cerrarPantallaPagos() {
		if(getDatos().get(PagosController.ACCION_CANCELAR) == null) {
			getApplication().getMainView().close();
		}
	}

}
