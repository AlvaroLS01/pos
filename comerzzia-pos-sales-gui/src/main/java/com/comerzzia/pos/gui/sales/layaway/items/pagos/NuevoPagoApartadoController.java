package com.comerzzia.pos.gui.sales.layaway.items.pagos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.conversion.BasketDocumentIssued;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketTotals;
import com.comerzzia.omnichannel.facade.model.basket.payments.BasketPayment;
import com.comerzzia.omnichannel.facade.service.store.StorePosPaymentMethodServiceFacade;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.SaveDocumentBackgroundTask;
import com.comerzzia.pos.gui.sales.basket.SaveDocumentCallback;
import com.comerzzia.pos.gui.sales.layaway.ApartadosManager;
import com.comerzzia.pos.gui.sales.retail.payments.ForbiddenCloseOperationException;
import com.comerzzia.pos.gui.sales.retail.payments.RetailPaymentsController;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class NuevoPagoApartadoController extends RetailPaymentsController{
	
	public static final String PARAMETRO_APARTADO_MANAGER = "MANAGER_APARTADO";
	
	protected ApartadosManager apartadoManager;
	
	@Autowired
	protected VariableServiceFacade variablesServices;
	@Autowired
	protected StorePosPaymentMethodServiceFacade mediosPagosService;
	
	@Autowired
	private Session sesion;
	
	final DeviceLineDisplay visor = Devices.getInstance().getLineDisplay();
		
	@Autowired
	protected ModelMapper modelMapper;
	
	@Autowired
	protected ComerzziaTenantResolver tenantResolver;
	
	private boolean enProceso = false;
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		super.onSceneOpen();
		hbSaleType.setVisible(false);
		hbSaleType.setManaged(false);
//		enProceso = false;
//		
		apartadoManager = (ApartadosManager) sceneData.remove(PARAMETRO_APARTADO_MANAGER);
//		ticketManager = (TicketManager) sceneData.get(FacturacionArticulosController.TICKET_KEY);
//		
//		cargarBotoneraDatosAdicionales();
//		List<ConfiguracionBotonBean> listaAccionesMPAux = new ArrayList<ConfiguracionBotonBean>();
//
//		for(ConfiguracionBotonBean cmg : botoneraMediosPagoContado.getListaAcciones()){
//			listaAccionesMPAux.add(cmg);
//		}
//		try{
//			panelPagoContado.getChildren().clear();
//			botoneraMediosPagoContado = new BotoneraComponent(3, 4, this, listaAccionesMPAux, null, panelPagoContado.getPrefHeight(), BotonBotoneraTextoComponent.class.getName());
//			panelPagoContado.getChildren().add(botoneraMediosPagoContado);
//		}catch(Exception e){}
//
//		lbDocActivo.setText(ticketManager.getDocumentoActivo().getDestipodocumento());
//
//		lbTitulo.setText(I18N.getTexto("Pagos"));
//		
//		panelPagos.focusedProperty().addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
//				if (t.booleanValue() == false && t1.booleanValue() == true){
//					medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
//					lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
//					lbSaldo.setText("");
//					Platform.runLater(new Runnable() {
//						@Override
//						public void run() {
//							tfImporte.requestFocus();
//						}
//					});  
//				}
//			}
//		});
//
//		panelEntregaCuenta.setVisible(false);
//
//		// Establecemos el medio de pago por defecto
//		medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
//		lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
//
//		panelPagos.getSelectionModel().select(panelPestanaPagoEfectivo);
//		lbSaldo.setText("");
//
//		ticketManager.getTicket().getTotales().recalcular();
//		
//		visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
//		escribirVisor();
//		
//		initializePaymentsManager();
//		
//		refrescarDatosPantalla();
	}
	
	protected void paymentRequest(BigDecimal importe) {
		String desMedioPagoUsado = null;
		
		if(!(basketManager.getBasketTransaction()).getPayments().isEmpty()){
			desMedioPagoUsado = basketManager.getBasketTransaction().getPayments().getPaymentsList().get(0).getPaymentMethodDes();
		}

		if(desMedioPagoUsado == null || desMedioPagoUsado.equals(paymentMethodSelected.getPaymentMethodDes())){
//			ticketManager.nuevaLineaPago(medioPagoSeleccionado.getCodMedioPago(), importe, true, true, null, true);
//			apartadoManager.getTicketApartado().calcularTotales();
//			refrescarDatosPantalla();
//			PaymentMethodManager paymentMethodManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(medioPagoSeleccionado.getCodMedioPago());
//			if(!ticketManager.getBasketTransaction().isRefund()){
//				pay(paymentMethodManager, medioPagoSeleccionado.getCodMedioPago(), importe);
//			}
//			else {
//				returnAmount(paymentMethodManager, medioPagoSeleccionado.getCodMedioPago(), importe);
//			}
			apartadoManager.getTicketApartado().calcularTotales();
			refreshScreenData();
		}
		else{
			DialogWindowComponent.openWarnWindow(I18N.getText("No se permite el uso de diferentes medios de pago."), getStage());
		}
	}
	
	/**
	 * Acción borrar registro seleccionado de la tabla
	 */
	@FXML
	protected void accionBorrarRegistroTabla() {
		log.debug("accionBorrarRegistroTabla() - Acción ejecutada");
//		if (tbPagos.getItems() != null && tbPagos.getItems().size() > 0) {
			
			log.debug("accionBorrarRegistroTabla() - Usuario solicita eliminar pagos.");
			if(DialogWindowComponent.openConfirmWindow(I18N.getText("Confirme operación"), I18N.getText("¿Desea eliminar los registros?"), getStage())){
				log.debug("accionBorrarRegistroTabla() - Se confirma la operación");
				// Eliminamos el registro
				basketManager.getBasketPaymentsManager().deleteBasketPayments();
				
//				if(tarjetaRegalo!=null){
//					tarjetaRegalo = null;
//					ticketManager.getTicket().getCabecera().setTarjetaRegalo(null);
//					ticketManager.setEsOperacionTarjetaRegalo(false);
//				}

				// Refrescar datos pantalla recarga la lista de pagos en pantalla.
				refreshScreenData();
			}			
//		}
//		else {
//			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Borrar pago"), I18N.getTexto("Sin pagos para eliminar."), getApplication().getStage());
//		}

		tfBalance.requestFocus();
	}

	
	/**
	 * Acción aceptar
	 */
	@FXML
	public void actionAccept() {
		if(!enProceso){
			log.debug("aceptar()");
			if(!sesion.getCashJournalSession().checkCashJournalClosingMandatory()){
				String fechaCaja = FormatUtils.getInstance().formatDate(sesion.getCashJournalSession().getOpeningDate());
				String fechaActual = FormatUtils.getInstance().formatDate(new Date());
				DialogWindowComponent.openErrorWindow(I18N.getText("No se puede realizar el pago. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual), getStage());
				enProceso = false;
				return;
			}
			enProceso = true;

			if (((basketManager.getBasketTransaction()).getTotals().getBalanceDue().compareTo(BigDecimal.ZERO) != 0 && basketManager.getDocumentType().getRequiereCompletarPagos()) || !basketManager.getDocumentType().getRequiereCompletarPagos()) {
				new SaveDocumentBackgroundTask(basketManager, new SaveDocumentCallback() {
					
					@Override
					public void onSucceeded(BasketPromotable<?> basketTransaction, BasketDocumentIssued<?> documentIssued) {
						try {                                                   
							Map<String,Object> mapaParametros= new HashMap<String,Object>();
							mapaParametros.put("ticket", basketTransaction);
							mapaParametros.put("apartado", apartadoManager.getTicketApartado().getCabecera());
							mapaParametros.put("fecha", FormatUtils.getInstance().formatDateTime(new Date()));
							mapaParametros.put("empresa", sesion.getApplicationSession().getCompany());
							mapaParametros.put("cajero", tenantResolver.getUser().getUserDes());
							mapaParametros.put("pagos", basketTransaction.getPayments());
							mapaParametros.put("totales", basketTransaction.getTotals());

//							ServicioImpresion.imprimir(basketManager.getBasketTransaction().getHeader().getPrintFormat(), mapaParametros);
						} catch (Exception e) {
							DialogWindowComponent.openErrorWindow(getStage(), I18N.getText("Lo sentimos, ha ocurrido un error al imprimir."), e);
						}

						//Mostramos la ventana con la información de importe pagado, cambio...
						if(basketTransaction.getTotals().getChangeDue().getAmount().compareTo(BigDecimal.ZERO) != 0){
							openChangeDueScene(basketTransaction, new SceneCallback<Void>() {
								
								@Override
								public void onSuccess(Void callbackData) {
//									basketManager.createBasketTransaction();

									
									enProceso = false;
									closeSuccess();
								}
								
								@Override
								public void onCancel() {
									DialogWindowComponent.openErrorWindow("Error al salvar el ticket.", getStage());
									enProceso = false;
									closeCancel();
								}
							});
						}
					}
					
					@Override
					public void onFailure(Exception e) {
						DialogWindowComponent.openErrorWindow(getStage(), "Error al salvar el ticket.", e);					    
						enProceso = false;
						closeCancel();
					}
				});
			}
			else {
				log.debug("aceptar() - Pagos no cubiertos");
				DialogWindowComponent.openWarnWindow(I18N.getText("Los pagos han de cubrir el importe a pagar."), this.getStage());
				enProceso = false;
			}

		}else{
			log.warn("aceptar() - Pago en proceso");
		}
	}

	@Override
	protected void writeDisplayDevice(BasketPromotable<?> basketTransaction) {
		BasketTotals totals = basketTransaction.getTotals();
		
		visor.write(I18N.getText("TOTAL:     ") + FormatUtils.getInstance().formatNumber(totals.getTotalToPay()),
					   I18N.getText("PENDIENTE: ") + FormatUtils.getInstance().formatNumber(totals.getBalanceDue()));
	}
	
//	@Override
//	protected void addPayment(PaymentOkEvent eventOk) {
//		BigDecimal amount = eventOk.getAmount();
//		String paymentCode = ((PaymentMethodManager) eventOk.getSource()).getPaymentCode();
//		Integer paymentId = eventOk.getPaymentId();
//		boolean removable = eventOk.isRemovable();
//		
//		MedioPagoBean paymentMethod = ticketManager.getPaymentMethod(paymentCode);
//		
//		boolean cashFlowRecorded = ((PaymentMethodManager) eventOk.getSource()).recordCashFlowImmediately();
//		
//		BasketPayment payment = ticketManager.createAndInsertBasketPayment(paymentCode, amount, true, removable, paymentId, true);
//		payment.setMovimientoCajaInsertado(cashFlowRecorded);
//		
//		if(ticketManager.getBasketTransaction().getTotals().getTotalToPay().compareTo(BigDecimal.ZERO)<0){
//		       amount = amount.negate();
//		}
//		
//		if(eventOk.getSource() instanceof GiftCardManager) {
////			GiftCardBean giftCard = (GiftCardBean) eventOk.getExtendedData().get(GiftCardManager.PARAM_TARJETA);
//			
////			BasketGiftCardData basketGifCardData = modelMapper.map(giftCard, BasketGiftCardData.class);
//						
////			payment.addGiftcardBean(amount, basketGifCardData);
//			
//		}
//		else {
//			addCustomPaymentData(eventOk, payment);
//		}
//		
//		if(paymentMethod.getTarjetaCredito() != null && paymentMethod.getTarjetaCredito()) {
//			if(eventOk.getExtendedData().containsKey(BasicPaymentMethodManager.PARAM_RESPONSE_TEF)) {
//				BasketPaymentCreditCardResponse datosRespuestaPagoTarjeta = (BasketPaymentCreditCardResponse) eventOk.getExtendedData().get(BasicPaymentMethodManager.PARAM_RESPONSE_TEF);
//				payment.setDatosRespuestaPagoTarjeta(datosRespuestaPagoTarjeta);
//				for(String key : eventOk.getExtendedData().keySet()) {
//					payment.addExtendedData(key, eventOk.getExtendedData().get(key));
//				}
//			}
//		}
//		
////		ticketManager.guardarCopiaSeguridadTicket();
//	}
	
//	@Override
//	protected void deletePayment(PaymentOkEvent eventOk) {
//		Integer paymentId = eventOk.getPaymentId();
//		
//		Iterator<PagoTicketGui> it = tbPagos.getItems().iterator();
//		while(it.hasNext()) {
//			PagoTicketGui gui = it.next();
//			if(gui.getPaymentId() != null && gui.getPaymentId().equals(paymentId)) {
//				if(ticketManager.deletePayment(paymentId)) {
//					it.remove();
//				}
//				else {
//					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido borrar el pago, contacte con el administrador."), getStage());
//					return;
//				}		
//			}
//		}
//		
//		ticketManager.getTicket().getCabecera().setTarjetaRegalo(null);
//		ticketManager.setEsOperacionTarjetaRegalo(false);
//		
//		deleteCustomPaymentData(eventOk, paymentId);
//		
////		ticketManager.guardarCopiaSeguridadTicket();
//		
//		Platform.runLater(new Runnable() {
//			@Override
//            public void run() {
//				refrescarDatosPantalla();
//            }
//		});
//	}
//
//	@Override
//	protected void deletePayment(PaymentErrorEvent eventError) {
//		Integer paymentId = eventError.getPaymentId();
//		
//		Iterator<PagoTicketGui> it = tbPagos.getItems().iterator();
//		while(it.hasNext()) {
//			PagoTicketGui gui = it.next();
//			if(gui.getPaymentId() != null && gui.getPaymentId().equals(paymentId)) {
//				if(ticketManager.deletePayment(paymentId)) {
//					it.remove();
//				}
//				else {
//					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido borrar el pago, contacte con el administrador."), getStage());
//					return;
//				}		
//			}
//		}
//		
//		ticketManager.getTicket().getCabecera().setTarjetaRegalo(null);
//		ticketManager.setEsOperacionTarjetaRegalo(false);
//		
//		deleteCustomPaymentData(eventError, paymentId);
//		
////		ticketManager.guardarCopiaSeguridadTicket();
//		
//		Platform.runLater(new Runnable() {
//			@Override
//            public void run() {
//				refrescarDatosPantalla();
//            }
//		});
//	}
//	
//	@Override
//	public void actionCancel() {		
//		boolean hayPagos = false;
//		BasketPromotable<?> basketTransaction = basketManager.getBasketTransaction();
//		for(BasketPayment pago : basketTransaction.getPayments().getPaymentsList()) {
//			if(pago.getCanBeDeleted()) {
//				hayPagos = true;
//				break;
//			}
//		}
//		
//		
//		if(hayPagos){
//			DialogWindowComponent.openErrorWindow(I18N.getText("Se han efectuados pagos. Debe cancelarlos para volver atrás."), getStage());
//		}
//		else {
//			try {
//				customCloseValidations();
//			}
//			catch(ForbiddenCloseOperationException e) {
//				return;
//			}
//			
//			visor.saleMode(basketTransaction);
//			writeDisplayDevice(basketTransaction);
//			sceneData.put(ACCION_CANCELAR, Boolean.TRUE);
//			closeCancel();
//		}
//	}
}
