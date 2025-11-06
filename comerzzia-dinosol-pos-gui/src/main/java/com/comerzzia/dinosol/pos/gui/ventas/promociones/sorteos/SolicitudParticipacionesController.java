package com.comerzzia.dinosol.pos.gui.ventas.promociones.sorteos;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.v2.sorteos.client.model.ParticipacionOfflineDto;
import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.core.servicios.documents.LocatorParseException;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.gui.ventas.cajas.DinoCajasController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.services.sorteos.SorteosService;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.sorteos.ParticipacionesDTO;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.dispositivo.impresora.parser.PrintParserException;
import com.comerzzia.pos.gui.ventas.devoluciones.FormularioConsultaTicketBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

@Component
public class SolicitudParticipacionesController extends Controller {

	private Logger log = Logger.getLogger(DinoCajasController.class);
	
	final IVisor visor = Dispositivos.getInstance().getVisor();

	@FXML
	protected TextField tfDocumento;

	@FXML
	protected Button btAceptar, btDoc;

	@FXML
	protected Label lbMensajeError;

	@Autowired
	private Sesion sesion;

	@Autowired
	private LocatorManager locatorManager;

	@Autowired
	protected Documentos documentos;

	@Autowired
	private SorteosService sorteosService;

	protected FormularioConsultaTicketBean frConsultaTicket;

	protected DinoTicketManager ticketManager;

	public static final String PLANTILLA_PARTICIPACIONES = "plantilla_participaciones";

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		frConsultaTicket = SpringContext.getBean(FormularioConsultaTicketBean.class);
		frConsultaTicket.setFormField("codOperacion", tfDocumento);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfDocumento.setUserData(keyboardDataDto);

		addSeleccionarTodoEnFoco(tfDocumento);
	}

	@Override
	public void initializeFocus() {
		tfDocumento.requestFocus();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		ticketManager = SpringContext.getBean(DinoTicketManager.class);

		tfDocumento.setText("");
		lbMensajeError.setText("");
	}

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER && !btAceptar.isDisable()) {
			accionAceptar();
		}
	}

	@FXML
	public void accionAceptar() {
		lbMensajeError.setText("");

		if (validarCodigo()) {
			ticketManager = SpringContext.getBean(DinoTicketManager.class);
			String codigo = tfDocumento.getText();

			try {
				if (ticketManager.comprobarConfigContador(documentos.getDocumentoAbono(Documentos.FACTURA_SIMPLIFICADA).getCodtipodocumento())) {
					Long idTipoDocumento = documentos.getDocumento(Documentos.FACTURA_SIMPLIFICADA).getIdTipoDocumento();
					new RecuperarTicketDevolucion(codigo, idTipoDocumento).start();
				}
				else {
					ticketManager.crearVentanaErrorContador(getStage());
				}
			}
			catch (DocumentoException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("El documento %s no se ha encontrado"), Documentos.FACTURA_SIMPLIFICADA), e);
			}
		}

	}

	protected boolean validarCodigo() {
		boolean valido;

		frConsultaTicket.clearErrorStyle();
		lbMensajeError.setText("");

		frConsultaTicket.setCodTienda(sesion.getAplicacion().getCodAlmacen());
		frConsultaTicket.setCodCaja(sesion.getAplicacion().getCodCaja());
		frConsultaTicket.setCodOperacion(tfDocumento.getText());
		frConsultaTicket.setCodDoc(Documentos.FACTURA_SIMPLIFICADA);

		String codigo = frConsultaTicket.getCodOperacion();

		try {
			locatorManager.decode(codigo);
		}
		catch (LocatorParseException e) {
			if (codigo.length() > 10) {
				lbMensajeError.setText(I18N.getTexto("El código {0} no es un localizador o un codigo de documento válido", codigo));
				valido = false;
				return valido;
			}
		}

		Set<ConstraintViolation<FormularioConsultaTicketBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frConsultaTicket);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioConsultaTicketBean> next = constraintViolations.iterator().next();
			frConsultaTicket.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(next.getMessage()), getStage());
			tfDocumento.requestFocus();
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
	}

	private class RecuperarTicketDevolucion extends BackgroundTask<Boolean> {

		private String codigo;
		private Long idTipoDoc;

		public RecuperarTicketDevolucion(String codigo, Long idTipoDoc) {
			this.codigo = codigo;
			this.idTipoDoc = idTipoDoc;
		}

		@Override
		protected Boolean call() throws Exception {
			String codTienda = sesion.getAplicacion().getCodAlmacen();
			String codCaja = sesion.getAplicacion().getCodCaja();

			return ticketManager.recuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDoc, false);
		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof com.comerzzia.pos.util.exception.Exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
			}
			else {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error"), getException());
			}
		}

		@Override
		protected void succeeded() {
			boolean res = getValue();
			recuperarTicketDevolucionSucceeded(res);
			super.succeeded();
		}

		@SuppressWarnings({ "rawtypes" })
		protected void recuperarTicketDevolucionSucceeded(boolean encontrado) {
			if (encontrado) {
				TicketVenta ticket = ticketManager.getTicketOrigen();

				if (!noTieneParticipacionesAsignadas(ticket)) {
					VentanaDialogoComponent.crearVentanaInfo("A esta venta no le corresponde ningún reparto de participaciones.", getStage());
					tfDocumento.clear();
					tfDocumento.requestFocus();
					return;
				}
				
				ParticipacionesDTO participaciones = ((DinoTicketVentaAbono) ticket).getParticipaciones();
				
				if (participaciones != null && !participaciones.getListaParticipaciones().isEmpty()) {
					
					VentanaDialogoComponent.crearVentanaAviso("Este ticket ya ha recibido sus respectivas participaciones.", getStage());
					return;
				}
				
				try {
					ParticipacionOfflineDto participacionOffline = sorteosService.getOfflineTicketRaffles(ticket.getCabecera().getLocalizador());
					if (participacionOffline.getGiven()) {
						VentanaDialogoComponent.crearVentanaAviso("Este ticket ya ha recibido sus respectivas participaciones.", getStage());
						return;
					}
				}
				catch (Exception e) {
					log.error("recuperarTicketDevolucionSucceeded - No se ha podido establecer conexión con la API de Sorteos: " + e.getMessage());
				}
				
				new SolicitarParticipacionesTask(ticket).start();
			}
			else {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha encontrado ningún ticket con esos datos"), getStage());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private class SolicitarParticipacionesTask extends BackgroundTask<Void> {
		
		private TicketVenta ticket;

		public SolicitarParticipacionesTask(TicketVenta ticket) {
			super();
			this.ticket = ticket;
		}

		@Override
		protected Void call() throws Exception {
			sorteosService.solicitarParticipacionesSorteoOffline(ticket, true);
			return null;
		}
		
		@Override
		protected void succeeded() {
			super.succeeded();
			
			boolean hayParticipaciones = ((DinoTicketVentaAbono) ticket).getParticipaciones() != null;
			if (hayParticipaciones) {
				((DinoVisorPantallaSecundaria) visor).modoParticipacionesAniversario((DinoTicketVentaAbono) ticket);
			}
			
			imprimir(ticket);
		}
		
		@Override
		protected void failed() {
			super.failed();
			
			Throwable exception = getException();
			
			log.error("SolicitarParticipacionesTask::failed() - Ha habido un error al solicitar participaciones: " + exception.getMessage(), exception);
			
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al solicitar las participaciones. Contacte con un responsable."), exception);
			
			tfDocumento.clear();
			tfDocumento.requestFocus();
			
			visor.modoEspera();
		}
		
	}

	@SuppressWarnings("rawtypes")
	protected void imprimir(TicketVenta ticket) {
		new BackgroudPrintTask(this.getStage(), false, ticket).start();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean noTieneParticipacionesAsignadas(TicketVenta ticket) {
		List<PromocionTicket> listaPromociones = ticket.getPromociones();

		for (PromocionTicket promo : listaPromociones) {
			if (promo.getIdTipoPromocion().equals(1001L)) {
				int puntosCabecera = Integer.parseInt(promo.getAdicionales().get("puntos_cabecera").toString());
				int puntosAdicionales = Integer.parseInt(promo.getAdicionales().get("puntos_lineas").toString());
				int rascasConcedidos = puntosCabecera + puntosAdicionales;

				if (rascasConcedidos > 0) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	protected class BackgroudPrintTask extends BackgroundTask<Void> {

		protected Stage stage;
		protected boolean resetPrinter;
		
		private TicketVenta ticket;

		public BackgroudPrintTask(Stage stage, boolean resetPrinter, TicketVenta ticket) {
			super(true);
			this.stage = stage;
			this.resetPrinter = resetPrinter;
			this.ticket = ticket;
		}

		@Override
		protected Void call() throws Exception {
			if (resetPrinter) {
				log.info("Reseteando impresora....");
				IPrinter impresora = Dispositivos.getInstance().getImpresora1();

				if (impresora.reset()) {
					log.info("Impresora reseteada con éxito.");
				}
			}

			Map<String, Object> mapaParametros = new HashMap<String, Object>();
			mapaParametros.put("ticket", ticket);

			ServicioImpresion.imprimir("sorteos/participaciones_diferidas", mapaParametros);

			List<CuponEmitidoTicket> cupones = ((DinoTicketVentaAbono) ticket).getCuponesEmitidos();
			if (cupones.size() > 0) {
				log.debug("BackgroudPrintTask::call() - Se van a imprimir " + cupones.size() + " cupones.");
				Map<String, Object> mapaParametrosCupon = new HashMap<String, Object>();
				mapaParametrosCupon.put("ticket", ticket);
				for (CuponEmitidoTicket cupon : cupones) {
					log.debug("BackgroudPrintTask::call() - Imprimiendo cupón con código " + cupon.getCodigoCupon());
					mapaParametrosCupon.put("cupon", cupon);
					SimpleDateFormat df = new SimpleDateFormat();
					mapaParametrosCupon.put("fechaEmision", df.format(ticket.getCabecera().getFecha()));
					ServicioImpresion.imprimir("cupon_promocion", mapaParametrosCupon);
				}
			}

			return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Participaciones entregadas correctamente"), stage);
			
			tfDocumento.clear();
			tfDocumento.requestFocus();
			
			visor.modoEspera();
		}

		@Override
		protected void failed() {
			super.failed();

			Throwable e = getException();

			log.error("ImpresionTicketTask::failed() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);

			String error = e.getMessage();
			if (e instanceof DeviceException && e.getCause() != null) {
				if (e.getCause() instanceof PrintParserException) {
					error = e.getCause().getCause().getMessage();
				}
				else {
					error = e.getCause().getMessage();
				}
			}

			String mensajeError = I18N.getTexto("Las participaciones se han generado correctamente aunque no se haya podido imprimir en papel.") + System.lineSeparator() + System.lineSeparator() + error
			        + System.lineSeparator() + I18N.getTexto("¿Desea reimprimir nuevamente?");

			VentanaDialogoComponent ventana = VentanaDialogoComponent.crearVentana(null, mensajeError, VentanaDialogoComponent.TIPO_ERROR, true, getStage(), e, true);
			if (ventana.isPulsadoAceptar()) {
				new BackgroudPrintTask(getStage(), true, ticket).start();
			}
			else {
				tfDocumento.clear();
				tfDocumento.requestFocus();
				
				visor.modoEspera();
			}
		}

	}
	
}
