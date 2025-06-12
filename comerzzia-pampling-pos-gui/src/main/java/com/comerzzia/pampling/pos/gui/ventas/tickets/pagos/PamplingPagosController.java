package com.comerzzia.pampling.pos.gui.ventas.tickets.pagos;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.devices.impresoras.fiscal.alemania.GermanyFiscalPrinter;
import com.comerzzia.pampling.pos.services.fiscal.alemania.GermanyFiscalPrinterService;
import com.comerzzia.pampling.pos.services.payments.PamplingPaymentsManagerImpl;
import com.comerzzia.pampling.pos.services.payments.methods.types.CashlogyManager;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaException;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.fiscal.IFiscalPrinter;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentSelectEvent;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.payments.methods.types.ContadoManager;
import com.comerzzia.pos.services.payments.methods.types.GiftCardManager;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@Primary
@Component
public class PamplingPagosController extends PagosController {

        @Autowired
        protected GermanyFiscalPrinterService germanyFiscalPrinterService;

        @FXML
        private Button btEfectivoCashlogy;

        private boolean autoAceptarEfectivo = false;

	@Override
	protected void accionSalvarTicketOnFailure(Exception e) {
		String msg = I18N.getTexto("Error al salvar el ticket.");
		if (e.getMessage().contains("UPOS_EFPTR_WRONG_STATE")) {
			msg = "Stampante in stato errato. Si prega di tornare allo stato \"REGISTRAZIONE\".";
		}
		if (e instanceof TarjetaException) {
			msg = e.getMessage();
		}
		if (!(e instanceof TarjetaRegaloException)) {
			VentanaDialogoComponent.crearVentanaError(getStage(), msg, e);
		}
	}

	/**
	 * Método modificado: para pagos de Contado (antes Cashlogy) se utiliza el importe depositado retornado por la API y
	 * se actualiza el campo tfImporte.
	 */
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
                ticketManager.guardarCopiaSeguridadTicket();

                if (autoAceptarEfectivo && eventOk.getSource() instanceof ContadoManager) {
                        autoAceptarEfectivo = false;
                        try {
                                aceptar();
                        } catch (DocumentoException e) {
                                VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
                        }
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

	/**
	 * Método auxiliar que devuelve el importe actual ingresado en tfImporte.
	 */

        private BigDecimal eventualPaymentAmount() {
                return FormatUtil.getInstance().desformateaImporte(tfImporte.getText());
        }

        @Override
        public void anotarPago(BigDecimal importe) {
		try {
			IPrinter printer = Dispositivos.getInstance().getImpresora1();
			if (printer instanceof GermanyFiscalPrinter && !((GermanyFiscalPrinter) printer).compruebaAutoTest()) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido realizar la conexión con el TSE"), getStage());
				return;
			}
		}
		catch (Exception e) {
			log.error("anotarPago() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido realizar la conexión con el TSE"), getStage());
			return;
		}
                super.anotarPago(importe);
        }

        @FXML
        private void accionBtEfectivoCashlogy() {
                BigDecimal pendiente = ticketManager.getTicket().getTotales().getPendiente();
                tfImporte.setText(FormatUtil.getInstance().formateaImporte(pendiente));
                autoAceptarEfectivo = true;
                anotarPago(pendiente);
        }

	@Override
	public void aceptar() throws DocumentoException {
		if (Dispositivos.getInstance().getImpresora1() instanceof GermanyFiscalPrinter) {
			if (Dispositivos.getInstance().getImpresora1().getConfiguracion().getNombreConexion().equals(GermanyFiscalPrinterService.NOMBRE_CONEXION_TSE)) {
				Boolean correcto = germanyFiscalPrinterService.tseStartTransaction(ticketManager);
				if (!correcto) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido realizar la conexión con el TSE"), getStage());
					return;
				}
			}
		}
		super.aceptar();
	}

	@Override
	protected boolean isFiscalPrinter() {
		if (isImpresoraFiscalAlemania()) {
			return false;
		}
		return Dispositivos.getInstance().getImpresora1() instanceof IFiscalPrinter;
	}

	private boolean isImpresoraFiscalAlemania() {
		IPrinter printer = Dispositivos.getInstance().getImpresora1();
		return (printer != null && printer instanceof GermanyFiscalPrinter);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();

                boolean cashlogyActivo = ((PamplingPaymentsManagerImpl) paymentsManager).isCashlogyEnable();
                log.debug("Inicializando formulario, cashlogyActivo=" + cashlogyActivo);

                if (cashlogyActivo) {
                        if (panelPagos.getTabs().contains(panelPestanaPagoEfectivo)) {
                                panelPagos.getTabs().remove(panelPestanaPagoEfectivo);
                        }
                        btAnotarPago.setVisible(false);
                        btAnotarPago.setManaged(false);
                        btEfectivoCashlogy.setVisible(true);
                        btEfectivoCashlogy.setManaged(true);
                }
                else {
                        if (!panelPagos.getTabs().contains(panelPestanaPagoEfectivo)) {
                                panelPagos.getTabs().add(0, panelPestanaPagoEfectivo);
                        }
                        panelPagos.getSelectionModel().select(panelPestanaPagoEfectivo);
                        btAnotarPago.setVisible(true);
                        btAnotarPago.setManaged(true);
                        btEfectivoCashlogy.setVisible(false);
                        btEfectivoCashlogy.setManaged(false);
                }
        }
}
