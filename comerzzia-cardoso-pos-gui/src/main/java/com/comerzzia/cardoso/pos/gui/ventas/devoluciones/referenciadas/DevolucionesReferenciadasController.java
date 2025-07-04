package com.comerzzia.cardoso.pos.gui.ventas.devoluciones.referenciadas;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.persistence.auditorias.AuditoriaDTO;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.ApproveResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.error.ApproveErrorException;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.response.CancelResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request.RequestDTO;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.error.RefundErrorException;
import com.comerzzia.cardoso.pos.services.devoluciones.referenciadas.CancelDevolucionReferenciadaTask;
import com.comerzzia.cardoso.pos.services.devoluciones.referenciadas.DevolucionesReferenciadasService;
import com.comerzzia.cardoso.pos.services.devoluciones.referenciadas.DevolucionesReferenciadasTask;
import com.comerzzia.cardoso.pos.services.devoluciones.referenciadas.cancel.response.error.CancelErrorException;
import com.comerzzia.cardoso.pos.util.CardosoVariables;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class DevolucionesReferenciadasController extends Controller {

	private static final Logger log = Logger.getLogger(DevolucionesReferenciadasController.class.getName());

	final IVisor visor = Dispositivos.getInstance().getVisor();

	@Autowired
	private Sesion sesion;

	@Autowired
	private VariablesServices variablesServices;

	@Autowired
	private DevolucionesReferenciadasService devolucionesReferenciadasService;

	@FXML
	protected TextField tfObservaciones, tfPaymentid, tfTerminal, tfComercio;

	@FXML
	protected TextFieldImporte tfImporte;

	@FXML
	protected Label lbMensajeError;

	@FXML
	protected Button btAceptar, btCancelar;

	protected TicketManager ticketManager;

	protected FormularioDevolucionReferenciada formDevolucion;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {

		formDevolucion = SpringContext.getBean(FormularioDevolucionReferenciada.class);

		formDevolucion.setFormField("paymentId", tfPaymentid);
		formDevolucion.setFormField("terminal", tfTerminal);
		formDevolucion.setFormField("comercio", tfComercio);
		formDevolucion.setFormField("importe", tfImporte);
	}

	@Override
	public void initializeFocus() {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {

		VentanaDialogoComponent.crearVentanaAviso("Tenga en cuenta que solo debe esta pantalla para operaciones NO registradas en Comerzzia como pagos duplicados.", getStage());

		formDevolucion.clearErrorStyle();

		visor.escribirLineaArriba(I18N.getTexto("--NUEVA DEVOLUCION--"));

		tfComercio.setText("");
		tfTerminal.setText("");
		tfObservaciones.setText("");
		lbMensajeError.setText("");
	}

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

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER && !btAceptar.isDisable()) {
			accionAceptar();
		}
	}

	@FXML
	public void accionAceptar() {
		lbMensajeError.setText("");

		formDevolucion.setPaymentId(tfPaymentid.getText());
		formDevolucion.setTerminal(tfTerminal.getText());
		formDevolucion.setComercio(tfComercio.getText());
		formDevolucion.setImporte(tfImporte.getText());

		if (validarFormularioDatos()) {
			if (VentanaDialogoComponent.crearVentanaConfirmacion(
			        I18N.getTexto("Va a proceder a realizar la devolución del pago con id ") + tfPaymentid.getText() + I18N.getTexto(" perteneciente al terminal ") + tfTerminal.getText()
			                + I18N.getTexto(" con comercio ") + tfComercio.getText() + I18N.getTexto(" por el importe de " + tfImporte.getText() + "euros ¿Desea continuar?"),
			        getStage(), "Aceptar", "Cancelar")) {

				log.debug("accionAceptar() - Realizando refund.");
				String url = variablesServices.getVariableAsString(CardosoVariables.URL_WORLDLINE);
				String puerto = variablesServices.getVariableAsString(CardosoVariables.PORT_WORLDLINE);

				log.debug("accionAceptar() - url: " + url + " puerto: " + puerto);
				String importe = tfImporte.getText().replace(".", ",");
				BigDecimal importeEntero = FormatUtil.getInstance().desformateaImporte(importe);
				BigDecimal importeEnCentimos = importeEntero.multiply(new BigDecimal("100"));
				try {
					RequestDTO refundRequestDTO = new RequestDTO();
					refundRequestDTO.setAmount(importeEnCentimos.intValue());
					refundRequestDTO.setComment(tfObservaciones.getText());
					refundRequestDTO.setGlobalMerchants(tfComercio.getText());
					refundRequestDTO.setPayments(tfPaymentid.getText());
					refundRequestDTO.setPos(tfTerminal.getText());
					refundRequestDTO.setPuerto(puerto);
					refundRequestDTO.setUrlHost(url);
					DevolucionesReferenciadasTask refundTask = SpringContext.getBean(DevolucionesReferenciadasTask.class, refundRequestDTO, new RestBackgroundTask.FailedCallback<ApproveResponse>(){

						@Override
						public void succeeded(ApproveResponse result) {
							onSucceedRefund(result);
						}

						@Override
						public void failed(Throwable throwable) {

							VentanaDialogoComponent.crearVentanaError("Error: " + throwable.getMessage(), getStage());
						}
					}, getStage());

					refundTask.start();

				}
				catch (Exception e) {
					if (e instanceof RefundErrorException || e instanceof ApproveErrorException) {
						VentanaDialogoComponent.crearVentanaError("Error: " + e.getMessage(), getStage());
					}
					else {
						VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Se ha producido un error realizando la devolución."), getStage());
					}
				}
			}
		}
	}

	private void onSucceedRefund(ApproveResponse approveResponse) {
		try {
			log.debug("onSucceedRefund() - Refund realizado con éxito, se procede a generar el documento de auditoría");
			AuditoriaDTO auditoriaDTO = new AuditoriaDTO();
			auditoriaDTO.setComercio(Integer.parseInt(tfComercio.getText()));
			auditoriaDTO.setImporte(tfImporte.getText());
			auditoriaDTO.setObservaciones(tfObservaciones.getText());
			auditoriaDTO.setPaymentid(Long.parseLong(approveResponse.getPayment().getId()));
			auditoriaDTO.setPosId(Integer.parseInt(tfTerminal.getText()));
			auditoriaDTO.setTipoOperacion("REFUND");
			auditoriaDTO.setPaymentIdOrigen(Long.parseLong(tfPaymentid.getText()));
			devolucionesReferenciadasService.insertDocument(auditoriaDTO, CardosoVariables.CODTIPODOCUMENTO_AUDEV);
		}
		catch (Exception e) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Se ha producido un error realizando la devolución."), getStage());
		}

		VentanaDialogoComponent.crearVentanaInfo("Se ha generado correctamente la devolución.", getStage());
		limpiarFormulario();

	}

	@FXML
	public void accionCancelar() {
		lbMensajeError.setText("");

		formDevolucion.setPaymentId(tfPaymentid.getText());
		formDevolucion.setTerminal(tfTerminal.getText());
		formDevolucion.setComercio(tfComercio.getText());
		formDevolucion.setImporte("Cancelacion");
		if (validarFormularioDatos()) {
			if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Va a proceder a realizar la cancelación del pago con id ") + tfPaymentid.getText()
			        + I18N.getTexto(" perteneciente al terminal ") + tfTerminal.getText() + I18N.getTexto(" con comercio ") + tfComercio.getText() + I18N.getTexto(" ¿Desea continuar?"), getStage(),
			        "Aceptar", "Cancelar")) {

				String url = variablesServices.getVariableAsString(CardosoVariables.URL_WORLDLINE);
				String puerto = variablesServices.getVariableAsString(CardosoVariables.PORT_WORLDLINE);
				RequestDTO refundRequestDTO = new RequestDTO();
				refundRequestDTO.setComment(tfObservaciones.getText());
				refundRequestDTO.setGlobalMerchants(tfComercio.getText());
				refundRequestDTO.setPayments(tfPaymentid.getText());
				refundRequestDTO.setPos(tfTerminal.getText());
				refundRequestDTO.setPuerto(puerto);
				refundRequestDTO.setUrlHost(url);
				try {
					log.debug("accionCancelar() - Realizando la cancelación de la devolución.");
					CancelDevolucionReferenciadaTask refundTask = SpringContext.getBean(CancelDevolucionReferenciadaTask.class, refundRequestDTO,
					        new RestBackgroundTask.FailedCallback<CancelResponse>(){

						        @Override
						        public void succeeded(CancelResponse result) {
							        log.debug("accionCancelar() - Cancelación realizada con éxito, se procede a generar el documento de auditoría.");
							        AuditoriaDTO auditoriaDTO = new AuditoriaDTO();
							        auditoriaDTO.setComercio(Integer.parseInt(tfComercio.getText()));
							        auditoriaDTO.setImporte(tfImporte.getText());
							        auditoriaDTO.setObservaciones(tfObservaciones.getText());
							        auditoriaDTO.setPaymentid(Long.parseLong(tfPaymentid.getText()));
							        auditoriaDTO.setPosId(Integer.parseInt(tfTerminal.getText()));
							        auditoriaDTO.setTipoOperacion("CANCEL");
							        try {
								        devolucionesReferenciadasService.insertDocument(auditoriaDTO, CardosoVariables.CODTIPODOCUMENTO_AUCAN);
							        }
							        catch (Exception e) {
								        VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Se ha producido un error creando el documento de cancelación."), getStage());
							        }
							        VentanaDialogoComponent.crearVentanaInfo("Se ha generado correctamente la cancelación  de la devolución.", getStage());
							        limpiarFormulario();
						        }

						        @Override
						        public void failed(Throwable throwable) {
						        	VentanaDialogoComponent.crearVentanaError("Error: " + throwable.getMessage(), getStage());
						        }
					        }, getStage());

					refundTask.start();
				}
				catch (Exception e) {
					if (e instanceof CancelErrorException) {
						VentanaDialogoComponent.crearVentanaError("Error: " + e.getMessage(), getStage());
					}
					else {
						VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Se ha producido un error realizando la devolución."), getStage());
					}
				}

			}
		}
	}

	private boolean validarFormularioDatos() {
		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		formDevolucion.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbMensajeError.setText("");

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioDevolucionReferenciada>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formDevolucion);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioDevolucionReferenciada> next = constraintViolations.iterator().next();
			formDevolucion.setErrorStyle(next.getPropertyPath(), true);
			formDevolucion.setFocus(next.getPropertyPath());
			lbMensajeError.setText(next.getMessage());
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
	}

	private void limpiarFormulario() {
		tfComercio.setText("");
		tfImporte.setText("");
		tfObservaciones.setText("");
		tfPaymentid.setText("");
		tfTerminal.setText("");

	}
}
