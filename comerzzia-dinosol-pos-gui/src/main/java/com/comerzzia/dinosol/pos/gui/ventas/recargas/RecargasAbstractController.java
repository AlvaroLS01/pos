package com.comerzzia.dinosol.pos.gui.ventas.recargas;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.dinosol.pos.devices.recarga.DispositivoRecargaAbstract;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosPeticionCancelacionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosPeticionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosRespuestaRecargaDto;
import com.comerzzia.dinosol.pos.persistence.dispositivos.recargas.OperadorDTO;
import com.comerzzia.dinosol.pos.persistence.tickets.TicketAnexoBean;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.RecargasService;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.TicketRecarga;
import com.comerzzia.dinosol.pos.services.ticket.anexo.AnexaTicketsServiceException;
import com.comerzzia.dinosol.pos.services.ticket.anexo.TicketsAnexosService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.recargamovil.RecargaMovilNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.gui.ventas.devoluciones.FormularioConsultaTicketBean;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.tipos.TipoDocumentoService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public abstract class RecargasAbstractController extends Controller {
	
	private Logger log = Logger.getLogger(RecargasAbstractController.class);

	protected final IVisor visor = Dispositivos.getInstance().getVisor();

	@Autowired
	protected Sesion sesion;
	
	@Autowired
	protected VariablesServices variablesServices;
	
	@Autowired
	protected ServicioContadores contadoresService;
	
	@Autowired
	protected TipoDocumentoService tipoDocumentoService;
	
	@Autowired
	protected TicketsService ticketService;
	
	@Autowired
	protected TicketsAnexosService ticketsAnexosService;

	@Autowired
	protected RecargasService recargasService;
	
	@FXML
	protected VBox vbContenedor, vbPaso1;

	protected FormularioConsultaTicketBean frConsultaTicket;

	protected TicketManager ticketManager;

	protected TipoDocumentoBean docNotaCredito;

	protected OperadorDTO operadorActual;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		frConsultaTicket = SpringContext.getBean(FormularioConsultaTicketBean.class);
	}

	@Override
	public void initializeComponents() {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		ticketManager = SpringContext.getBean(TicketManager.class);
	}

	/**
	 * Realiza las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
	 * 
	 * @throws CajasServiceException
	 * @throws CajaEstadoException
	 * @throws InitializeGuiException
	 */
	protected void comprobarAperturaPantalla() throws CajasServiceException, CajaEstadoException, InitializeGuiException {
		if (!sesion.getSesionCaja().isCajaAbierta()) {
			Boolean aperturaAutomatica = variablesServices.getVariableAsBoolean(VariablesServices.CAJA_APERTURA_AUTOMATICA, true);
			if (aperturaAutomatica) {
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No hay caja abierta. Se abrirá automáticamente."), getStage());
				sesion.getSesionCaja().abrirCajaAutomatica();
			}
			else {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."), getStage());
				throw new InitializeGuiException(false);
			}
		}

		if (!ticketManager.comprobarCierreCajaDiarioObligatorio()) {
			String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
			String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual),
			        getStage());
			throw new InitializeGuiException(false);
		}
	}

	protected boolean comprobarRecargaUnica(String uidTicket) throws AnexaTicketsServiceException {
		TicketAnexoBean ticketAnexo = ticketsAnexosService.consultar(uidTicket);

		return ticketAnexo != null && ticketAnexo.getTieneRecarga();
	}

	protected boolean isDispositivoConfigurado() {
		return !(Dispositivos.getInstance().getRecargaMovil() instanceof RecargaMovilNoConfig);
	}

	protected void compruebaConfiguracion() throws InitializeGuiException {
		if (!isDispositivoConfigurado()) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El dispositivo de recarga no está configurado."), getStage());
			throw new InitializeGuiException(false);
		}
	}

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER && isDispositivoConfigurado()) {
			if (vbContenedor.getChildren().contains(vbPaso1)) {
				accionBuscar();
			}
			else if (comprobarFormulario()) {
				realizarAccionRecarga();
			}
		}
	}
	
	public void realizarAccionRecarga() {
		new RecargaDigitalTask().start();
	}

	protected abstract void muestraPaso1();

	protected abstract void muestraPaso2();
	
	public abstract DatosPeticionRecargaDto generarPeticionRecarga();
	
	public abstract DatosPeticionCancelacionRecargaDto generarPeticionCancelacionRecarga();

	public abstract void accionBuscar();

	public abstract boolean comprobarFormulario();

	public abstract void accionCancelar();
	
	protected abstract void imprimirDocumentoRecarga(TicketRecarga ticketRecarga);
	
	public abstract void salvarDocumentoRecarga(TicketRecarga ticketRecarga, String uidTicketOrigen);

	protected abstract String getTipoRecarga();
	
	protected TicketRecarga generarTicketRecarga(DatosRespuestaRecargaDto response) {
		return recargasService.generarTicketRecarga(ticketManager.getTicketOrigen(), operadorActual, response, getTipoRecarga());
	}

	public class RecargaDigitalTask extends BackgroundTask<DatosRespuestaRecargaDto> {

		@Override
		protected DatosRespuestaRecargaDto call() throws Exception {
			DatosPeticionRecargaDto datosPeticion = generarPeticionRecarga();
			return ((DispositivoRecargaAbstract) Dispositivos.getInstance().getRecargaMovil()).recargar(datosPeticion);
		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof com.comerzzia.pos.util.exception.Exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
			}
			else if (getException() instanceof DispositivoException) {
				String msg = "Se ha producido un error al realizar la recarga.";
				log.error("accionRecargar() - " + msg + ": " + getException().getMessage(), getException());
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(msg), getException());
			}
			else if (getException() instanceof SocketTimeoutException) {
				String msg = "No se ha podido realizar la recarga ya que se ha agotado el tiempo de espera para la petición de la recarga.";
				log.error("accionRecargar() - " + msg + ": " + getException().getMessage(), getException());
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Tiempo de conexión agotado"), I18N.getTexto(msg), getStage());
			}
			else {
				log.error("accionRecargar() - Se ha producido un error inesperado al realizar la recarga: " + getException().getMessage(), getException());
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), getException());
			}

			DatosPeticionCancelacionRecargaDto datosPeticion = generarPeticionCancelacionRecarga();
			((DispositivoRecargaAbstract) Dispositivos.getInstance().getRecargaMovil()).cancelarRecargaAutomatica(datosPeticion);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			DatosRespuestaRecargaDto response = getValue();

			if (response.isRespuestaOk()) {
				TicketRecarga ticketRecarga = generarTicketRecarga(response);

				salvarDocumentoRecarga(ticketRecarga, ticketManager.getTicketOrigen().getUidTicket());
				
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Recarga realizada correctamente."), getStage());

				imprimirDocumentoRecarga(ticketRecarga);

				muestraPaso1();
			}
			else {
				VentanaDialogoComponent.crearVentanaAviso(response.getMensaje(), response.getMensaje(), getStage());
			}
		}
	}

}
