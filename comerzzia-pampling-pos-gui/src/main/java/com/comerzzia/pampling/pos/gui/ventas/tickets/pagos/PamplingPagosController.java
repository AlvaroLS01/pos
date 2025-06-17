package com.comerzzia.pampling.pos.gui.ventas.tickets.pagos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.devices.impresoras.fiscal.alemania.GermanyFiscalPrinter;
import com.comerzzia.pampling.pos.services.fiscal.alemania.GermanyFiscalPrinterService;
import com.comerzzia.pampling.pos.services.payments.PamplingPaymentsManagerImpl;
import com.comerzzia.pampling.pos.services.payments.methods.types.CashlogyTask;
import com.comerzzia.pampling.pos.services.payments.methods.types.CashlogyManager;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaException;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.medioPago.BotonBotoneraTextoComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.fiscal.IFiscalPrinter;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.events.PaymentErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentSelectEvent;
import com.comerzzia.pos.services.payments.events.PaymentsErrorEvent;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.payments.methods.types.ContadoManager;
import com.comerzzia.pos.services.payments.methods.types.GiftCardManager;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;

@Primary
@Component
public class PamplingPagosController extends PagosController {

	@Autowired
	protected GermanyFiscalPrinterService germanyFiscalPrinterService;
	
	protected BotoneraComponent botoneraCashLogy;
	private boolean autoAceptarEfectivo = false;

	@Override
	protected void accionSalvarTicketOnFailure(Exception e) {
		// Construimos mensaje en una variable efectiva final
		String baseMsg = I18N.getTexto("Error al salvar el ticket.");
		if (e.getMessage().contains("UPOS_EFPTR_WRONG_STATE")) {
			baseMsg = "Stampante in stato errato. Si prega di tornare allo stato \"REGISTRAZIONE\".";
		}
		else if (e instanceof TarjetaException) {
			baseMsg = e.getMessage();
		}
		// Solo si no es TarjetaRegaloException
		if (!(e instanceof TarjetaRegaloException)) {
			final String msg = baseMsg;
			Platform.runLater(() -> VentanaDialogoComponent.crearVentanaError(getStage(), msg, e));
		}
	}

	@Override
	protected void addPayment(PaymentOkEvent eventOk) {
		BigDecimal amount;
		if (eventOk.getSource() instanceof ContadoManager) {
			amount = eventOk.getAmount();
			tfImporte.setText(FormatUtil.getInstance().formateaImporte(amount));
		}
		else {
			amount = eventOk.getAmount();
		}

		String paymentCode = ((PaymentMethodManager) eventOk.getSource()).getPaymentCode();
		Integer paymentId = eventOk.getPaymentId();
		boolean removable = eventOk.isRemovable();
		boolean cashFlowRecorded = ((PaymentMethodManager) eventOk.getSource()).recordCashFlowImmediately();

		log.debug("addPayment() - Se añadirá el nuevo pago. [PaymentCode: " + paymentCode + ", amount: " + amount + ", paymentId: " + paymentId + ", removable: " + removable + "]");

		PagoTicket payment = ticketManager.nuevaLineaPago(paymentCode, amount, true, removable, paymentId, true);
		payment.setMovimientoCajaInsertado(cashFlowRecorded);

		if (ticketManager.getTicket().getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO) < 0) {
			amount = amount.negate();
		}

		if (eventOk.getSource() instanceof GiftCardManager) {
			log.debug("addPayment() - GiftCardManager payment.");
			GiftCardBean giftCard = (GiftCardBean) eventOk.getExtendedData().get(GiftCardManager.PARAM_TARJETA);
			payment.addGiftcardBean(amount, giftCard);
			GiftCardBean tarjetaRegaloPago = obtenerPagoTarjetaRegalo(giftCard);
			asociarPagoTarjetaRegalo(ticketManager.getTicket().getCabecera().esVenta(), tarjetaRegaloPago);
		}
		else {
			addCustomPaymentData(eventOk, payment);
		}

		// Guardar copia de seguridad en FX Thread
		Platform.runLater(() -> ticketManager.guardarCopiaSeguridadTicket());

		// Auto-aceptar efectivo también en FX Thread
		if (autoAceptarEfectivo && eventOk.getSource() instanceof ContadoManager) {
			autoAceptarEfectivo = false;
			Platform.runLater(() -> {
				try {
					aceptar();
				}
				catch (DocumentoException e) {
					VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
				}
			});
		}
	}

	@Override
	protected void selectCustomPaymentMethod(PaymentSelectEvent paymentSelectEvent) {
		PaymentMethodManager paymentManager = (PaymentMethodManager) paymentSelectEvent.getSource();
		if (paymentManager instanceof ContadoManager) {
			anotarPago(eventualPaymentAmount());
		}
		else {
			BigDecimal importe = FormatUtil.getInstance().desformateaImporte(tfImporte.getText());
			anotarPago(importe);
		}
	}

	private BigDecimal eventualPaymentAmount() {
		return FormatUtil.getInstance().desformateaImporte(tfImporte.getText());
	}

	@Override
	public void anotarPago(BigDecimal importe) {
		try {
			
			if(medioPagoSeleccionado.equals(MediosPagosService.medioPagoDefecto) && ((PamplingPaymentsManagerImpl) paymentsManager).isCashlogyEnable()) {
				autoAceptarEfectivo = true;
			}
			IPrinter printer = Dispositivos.getInstance().getImpresora1();
			if (printer instanceof GermanyFiscalPrinter && !((GermanyFiscalPrinter) printer).compruebaAutoTest()) {
				Platform.runLater(() -> VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido realizar la conexión con el TSE"), getStage()));
				return;
			}
		}
		catch (Exception e) {
			log.error("anotarPago() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			Platform.runLater(() -> VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido realizar la conexión con el TSE"), getStage()));
			return;
		}
		super.anotarPago(importe);
	}
	
	@Override
	public void aceptar() throws DocumentoException {
		if (Dispositivos.getInstance().getImpresora1() instanceof GermanyFiscalPrinter) {
			if (Dispositivos.getInstance().getImpresora1().getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE)) {
				Boolean correcto = germanyFiscalPrinterService.tseStartTransaction(ticketManager);
				if (!correcto) {
					Platform.runLater(() -> VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido realizar la conexión con el TSE"), getStage()));
					return;
				}
			}
		}
		super.aceptar();
	}

	@Override
	protected boolean isFiscalPrinter() {
		if (isImpresoraFiscalAlemania())
			return false;
		return Dispositivos.getInstance().getImpresora1() instanceof IFiscalPrinter;
	}

	private boolean isImpresoraFiscalAlemania() {
		IPrinter printer = Dispositivos.getInstance().getImpresora1();
		return printer instanceof GermanyFiscalPrinter;
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();

		boolean cashlogyActivo = ((PamplingPaymentsManagerImpl) paymentsManager).isCashlogyEnable();
		log.debug("Inicializando formulario, cashlogyActivo=" + cashlogyActivo);
		if (cashlogyActivo) {
			try {
				List<ConfiguracionBotonBean> listaAccionesCashLogy = new LinkedList<ConfiguracionBotonBean>();
				List<String> codMediosPagosUtilizados = new ArrayList<String>();
				
				panelPagoEfectivo.getChildren().clear();
				MedioPagoBean medioPagoCashLogy = MediosPagosService.medioPagoDefecto;
				crearBotonMedioPago(listaAccionesCashLogy, medioPagoCashLogy, codMediosPagosUtilizados);
				botoneraCashLogy = new BotoneraComponent(3, 4, this, listaAccionesCashLogy, panelPagoTarjeta.getPrefWidth(), panelPagoTarjeta.getPrefHeight(),
				        BotonBotoneraTextoComponent.class.getName());
				panelPagoEfectivo.getChildren().add(botoneraCashLogy);
			}
			catch (Exception e) {
				log.error("initializeComponents() - Ha ocurrido un error al inicializar la botonera de CashLogy: " + e.getMessage(), e);
			}
		}
		
		if (cashlogyActivo) {
			if (botoneraImportes != null) {
				botoneraImportes.setVisible(false);
				botoneraImportes.setManaged(false);
			}
			btAnotarPago.setVisible(false);
			btAnotarPago.setManaged(false);
		}
		else {
			if (botoneraImportes != null) {
				botoneraImportes.setVisible(true);
				botoneraImportes.setManaged(true);
			}
			btAnotarPago.setVisible(true);
			btAnotarPago.setManaged(true);
		}

		panelPagos.getSelectionModel().select(panelPestanaPagoEfectivo);
	}
	
	@Override
	protected void pay(PaymentMethodManager paymentMethodManager, String codMedioPago, BigDecimal importe) {
		final BigDecimal importeFinal = importe;
		final String codMedioPagoFinal = codMedioPago;
		if (((PamplingPaymentsManagerImpl) paymentsManager).isCashlogyEnable() && medioPagoSeleccionado.equals(MediosPagosService.medioPagoDefecto)) {

			new CashlogyTask<Void>(){

				@Override
				protected Void call() throws Exception {
					paymentsManager.pay(codMedioPagoFinal, importeFinal);
					return null;
				}

				@Override
				protected void succeeded() {
					escribirVisor();
					btAnotarPago.setDisable(false);
					super.succeeded();
				}

				@Override
				protected void failed() {
					super.failed();
					Throwable e = getException();
					stage.close();

					if (e instanceof PaymentException) {
						PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, ((PaymentException) e).getPaymentId(), e, ((PaymentException) e).getErrorCode(),
						        ((PaymentException) e).getMessage());
						PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
						paymentsManager.getEventsHandler().paymentsError(event);

					}
					else {
						PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, -1, e, null, null);
						PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
						paymentsManager.getEventsHandler().paymentsError(event);
					}
					btAnotarPago.setDisable(false);

				}
                        }.start(getStage(), () -> {
                                if (paymentMethodManager instanceof CashlogyManager) {
                                        ((CashlogyManager) paymentMethodManager).requestCancelSale();
                                }
                        });

		}
		else {
			super.pay(paymentMethodManager, codMedioPagoFinal, importeFinal);
		}
	}
}
