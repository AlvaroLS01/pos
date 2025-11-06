package com.comerzzia.dinosol.pos.gui.ventas.recargas.cancelacion;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.devices.recarga.DispositivoRecargaAbstract;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosPeticionCancelacionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosRespuestaCancelacionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosPeticionRecargaDto;
import com.comerzzia.dinosol.pos.gui.ventas.recargas.RecargasAbstractController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.TicketRecarga;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.devoluciones.FormularioConsultaTicketBean;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

@Component
public class CancelacionRecargasController extends RecargasAbstractController {

	private static final Logger log = Logger.getLogger(CancelacionRecargasController.class.getName());

	public static final String PLANTILLA_CANCEL_RECARGA_MOVILES = "cancel_recarga_movil";

	@FXML
	protected TextField tfOperacion;

	@FXML
	private FlowPane fpDocumento;

	@FXML
	protected Label lbMensajeError;

	@FXML
	protected Button btAceptar;

	@FXML
	protected Button btDoc;

	protected TipoDocumentoBean docRecarga;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		frConsultaTicket = SpringContext.getBean(FormularioConsultaTicketBean.class);
		frConsultaTicket.setFormField("codOperacion", tfOperacion);
	}

	@Override
	public void initializeComponents() {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfOperacion.setUserData(keyboardDataDto);
		addSeleccionarTodoEnFoco(tfOperacion);

	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		compruebaConfiguracion();

		muestraPaso1();

		ticketManager = SpringContext.getBean(TicketManager.class);

		visor.escribirLineaArriba(I18N.getTexto("--CANCELACION RECARGA--"));

		btAceptar.setDisable(false);
		lbMensajeError.setText("");
		tfOperacion.setText("");
	}

	@Override
	public void initializeFocus() {
		tfOperacion.requestFocus();
	}

	public class RecuperarTicketRecarga extends BackgroundTask<TicketBean> {

		private String codigo;
		private String codTienda;
		private String codCaja;

		public RecuperarTicketRecarga(String codigo, String codTienda, String codCaja) {
			this.codigo = codigo;
			this.codTienda = codTienda;
			this.codCaja = codCaja;
		}

		@Override
		protected TicketBean call() throws Exception {
			return ((DinoTicketManager) ticketManager).recuperarTicketCancelacion(codigo, codTienda, codCaja);

		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof com.comerzzia.pos.util.exception.Exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
			}
			else {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), getException());
			}
		}

		@Override
		protected void succeeded() {
			TicketBean res = getValue();
			recuperarTicketCancelacionRecargaSucceeded(res);
			super.succeeded();
			fpDocumento.requestFocus();
			initializeFocus();
		}

	}

	protected void recuperarTicketCancelacionRecargaSucceeded(TicketBean ticketBean) {
		if (ticketBean != null && ticketBean.getTicket() != null) {
			TicketRecarga ticketRecarga = (TicketRecarga) MarshallUtil.leerXML(ticketBean.getTicket(), TicketRecarga.class);

			if (ticketRecarga != null) {
				String mensaje = "¿Desea cancelar la recarga de " + FormatUtil.getInstance().formateaImporte(ticketRecarga.getImporte()) + " €?";
				if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto(mensaje), getStage())) {
					new CancelarRecargarTask(ticketBean, ticketRecarga).start();
				}
			}
			else {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido realizar la cancelación de la recarga de este ticket"), getStage());
				tfOperacion.clear();
				fpDocumento.requestFocus();
				initializeFocus();
				String msg = "Se ha producido un error al parsear el xml del documento de la recarga";
				log.error("recuperarTicketCancelacionRecargaSucceeded() - " + msg);
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha encontrado ningún ticket de recarga con esos datos"), getStage());
			fpDocumento.requestFocus();
			initializeFocus();
		}
	}

	public class CancelarRecargarTask extends BackgroundTask<DatosRespuestaCancelacionRecargaDto> {

		private TicketBean ticketOriginal;
		private TicketRecarga ticketRecargaOriginal;

		public CancelarRecargarTask(TicketBean ticketOriginal, TicketRecarga ticketRecarga) {
			super();
			this.ticketOriginal = ticketOriginal;
			this.ticketRecargaOriginal = ticketRecarga;
		}

		@Override
		protected DatosRespuestaCancelacionRecargaDto call() throws Exception {
			DatosPeticionCancelacionRecargaDto datosPeticion = generarPeticionCancelacionRecarga(ticketRecargaOriginal);
			return ((DispositivoRecargaAbstract) Dispositivos.getInstance().getRecargaMovil()).cancelarRecarga(datosPeticion);
		}

		private DatosPeticionCancelacionRecargaDto generarPeticionCancelacionRecarga(TicketRecarga ticketRecargaOriginal) {
			DatosPeticionCancelacionRecargaDto peticion = new DatosPeticionCancelacionRecargaDto();
			peticion.setNumReferenciaProveedor(ticketRecargaOriginal.getCodReferenciaProveedor());
			peticion.setImporte(ticketRecargaOriginal.getImporte());
			peticion.setTelefono(ticketRecargaOriginal.getTelefono());
			peticion.setOperador(ticketRecargaOriginal.getEan());
			peticion.setUsuario(sesion.getAplicacion().getCodAlmacen() + "-" + sesion.getAplicacion().getCodCaja());
			return peticion;
		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof com.comerzzia.pos.util.exception.Exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
			}
			else if (getException() instanceof DispositivoException) {
				String msg = "Se ha producido un error al realizar la cancelación de la recarga.";
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
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			DatosRespuestaCancelacionRecargaDto response = getValue();

			if (response.isRespuestaOk()) {
				TicketRecarga ticketRecarga = generarTicketRecarga(response);

				salvarDocumentoRecarga(ticketRecarga, ticketRecargaOriginal.getUidTicketOriginal());

				String mensaje = I18N.getTexto("Tarjeta POSA/PIN desactivada. Para finalizar la devolución del artículo/tarjeta, debe hacerlo en el apartado de \"Devoluciones\".");
				if (ticketRecarga.getTipoRecarga().equals(TicketRecarga.TIPO_RECARGA_MOVIL)) {
					mensaje = I18N.getTexto("Recarga móvil anulada. Para finalizar la devolución del artículo/tarjeta, debe hacerlo en el apartado de \"Devoluciones\".");
				}
				VentanaDialogoComponent.crearVentanaInfo(mensaje, getStage());

				imprimirDocumentoRecarga(ticketRecarga);

				muestraPaso1();
			}
			else {
				VentanaDialogoComponent.crearVentanaAviso(response.getMensaje(), response.getMensaje(), getStage());
			}
		}

		protected TicketRecarga generarTicketRecarga(DatosRespuestaCancelacionRecargaDto response) {
			TicketRecarga ticketRecarga = new TicketRecarga();
			ticketRecarga.setUidTicketOriginal(ticketOriginal.getUidTicket());
			ticketRecarga.setCodTicketOriginal(ticketOriginal.getCodTicket());
			ticketRecarga.setCodTienda(sesion.getAplicacion().getCodAlmacen());
			ticketRecarga.setCodCaja(sesion.getAplicacion().getCodCaja());
			ticketRecarga.setFecha(new Date());
			ticketRecarga.setCajero(sesion.getSesionCaja().getCajaAbierta().getUsuario());
			ticketRecarga.setEan(ticketRecargaOriginal.getEan());
			ticketRecarga.setTelefono(ticketRecargaOriginal.getTelefono());
			ticketRecarga.setMensaje(response.getMensaje());
			ticketRecarga.setImporte(ticketRecargaOriginal.getImporte());
			ticketRecarga.setTipoRecarga(ticketRecargaOriginal.getTipoRecarga());
			ticketRecarga.setCodReferenciaProveedor(response.getReferenciaProveedor());

			if(TicketRecarga.TIPO_RECARGA_MOVIL.equals(ticketRecargaOriginal.getTipoRecarga())) {
				ticketRecarga.setTelefono(ticketRecarga.getTelefono());
			}

			return ticketRecarga;
		}
	}

	@Override
	public boolean canClose() {
		visor.escribirLineaArriba(I18N.getTexto("---CANCELACION RECARGA FINALIZADA---"));
		visor.modoEspera();
		return super.canClose();
	}

	@Override
	protected void muestraPaso1() {
		tfOperacion.setText("");

		if (!vbContenedor.getChildren().contains(vbPaso1)) {
			vbContenedor.getChildren().add(0, vbPaso1);
		}
	}

	@Override
	protected void muestraPaso2() {
		// En esta pantalla no hay segundo paso, no es necesario implementar
	}

	@Override
	public DatosPeticionRecargaDto generarPeticionRecarga() {
		return null;
	}

	@Override
	public DatosPeticionCancelacionRecargaDto generarPeticionCancelacionRecarga() {
		return null;
	}

	@Override
	public void accionBuscar() {
		lbMensajeError.setText("");
		if (comprobarFormulario()) {

			ticketManager = SpringContext.getBean(TicketManager.class);
			String codTienda = sesion.getAplicacion().getTienda().getCodAlmacen();
			String codCaja = sesion.getAplicacion().getCodCaja();
			String codigo = tfOperacion.getText();

			if (codigo.isEmpty()) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El campo 'Código de ticket' no puede estar vacío."), getStage());
				btAceptar.requestFocus();
				tfOperacion.requestFocus();
			}
			else {
				new RecuperarTicketRecarga(codigo, codTienda, codCaja).start();

			}
		}
	}

	@Override
	public boolean comprobarFormulario() {
		boolean valido = true;

		// Limpiamos el posible error anterior
		lbMensajeError.setText("");

		String codigo = tfOperacion.getText();
		if (StringUtils.isBlank(codigo)) {
			// No es codDocumento válido
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El código {0} no es un localizador o un código de documento válido", codigo), getStage());
			fpDocumento.requestFocus();
			initializeFocus();
			valido = false;
			return valido;
		}

		return valido;
	}

	@Override
	public void accionCancelar() {
		getApplication().getMainView().close();
		tfOperacion.requestFocus();
	}

	@Override
	protected void imprimirDocumentoRecarga(TicketRecarga ticketRecarga) {
		log.trace("imprimirDocumentoRecarga()");
		try {
			// Rellenamos los parametros
			Map<String, Object> contextoTicket = new HashMap<String, Object>();

			// Introducimos los parámetros que necesita el ticket para imprimir la información del cierre
			contextoTicket.put("documento", ticketRecarga);
			contextoTicket.put("esCopia", false);

			// Llamamos al servicio de impresión
			ServicioImpresion.imprimir(PLANTILLA_CANCEL_RECARGA_MOVILES, contextoTicket);

			contextoTicket.put("esCopia", true);
			// Llamamos al servicio de impresión para la copia
			ServicioImpresion.imprimir(PLANTILLA_CANCEL_RECARGA_MOVILES, contextoTicket);

		}
		catch (DeviceException e) {
			log.error("imprimirDocumentoRecarga() - Error en la impresión de la cancelación de la recarga: " + e.getMessage(), e);
		}
	}

	@Override
	public void salvarDocumentoRecarga(TicketRecarga ticketRecarga, String uidTicketOrigen) {
		recargasService.salvarDocumentoCancelacionRecargaMovil(ticketRecarga, uidTicketOrigen);
	}

	@Override
	protected String getTipoRecarga() {
		return TicketRecarga.TIPO_RECARGA_CANCELACION;
	}

}
