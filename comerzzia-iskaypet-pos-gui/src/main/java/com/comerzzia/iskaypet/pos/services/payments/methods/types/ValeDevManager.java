package com.comerzzia.iskaypet.pos.services.payments.methods.types;

import com.comerzzia.api.rest.client.exceptions.HttpServiceRestException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.movimientos.ListaMovimientoRequestRest;
import com.comerzzia.api.rest.client.movimientos.MovimientoRequestRest;
import com.comerzzia.api.rest.client.movimientos.MovimientosRest;
import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucion;
import com.comerzzia.iskaypet.pos.services.valesdevolucion.ValesDevolucionService;
import com.comerzzia.iskaypet.pos.util.date.DateUtils;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.giftcard.GiftCardService;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.i18n.I18N;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * GAPXX - GENERAR VALE DE DEVOLUCIÓN
 */
@Component
@Scope("prototype")
@SuppressWarnings("unchecked")
public class ValeDevManager extends BasicPaymentMethodManager {

	private static final Logger log = Logger.getLogger(ValeDevManager.class);

	public static final String PARAM_TARJETA = "PARAM_TARJETA";

	public static final String PARAM_TARJETA_FECHA_ACTIVACION = "fecha_activacion";

	public static final String PARAM_TARJETA_FECHA_BAJA = "fecha_baja";

	public static final String PARAM_TARJETA_FECHA_FORMAT = "dd/MM/yyyy";

	public static final String PARAM_CONSULTADO_LOCAL_CANCEL = "PARAM_CONSULTADO_LOCAL_CANCEL";

	public static final SimpleDateFormat sdfGifCardNumber = new SimpleDateFormat("ddMMyyyyHHmmss");

	@Autowired
	protected Sesion sesion;
	@Autowired
	protected VariablesServices variablesServices;
	@Autowired
	protected ValesDevolucionService valesDevolucionService;
	@Autowired
	protected GiftCardService giftCardService;

	public boolean consultadoEnLocal = false;

	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		log.debug("setConfiguration() - Cargando configuración para el medio de pago: " + paymentCode);
	}

	/* ########################################################################################################################### */
	/* ########################################## GAPXX - GENERAR VALE DE DEVOLUCIÓN ############################################# */
	/* ########################################################################################################################### */

	/**
	 * Generamos nosotros una tarjeta regalo con los datos básicos, la real se genera en el procesador.
	 */
	@Override
	public boolean returnAmount(BigDecimal amount) throws PaymentException {
		try {
			PaymentInitEvent initEvent = new PaymentInitEvent(this);
			getEventHandler().paymentInitProcess(initEvent);

			GiftCardBean tempGiftCard = new GiftCardBean();
			tempGiftCard.setUidTransaccion(UUID.randomUUID().toString());
			tempGiftCard.setSaldoProvisional(BigDecimal.ZERO);
			tempGiftCard.setSaldo(amount);

			// 13 dígitos = 14 fecha actual con hora sin separación + 4 tienda + 4 caja 
			String giftCardNumber = sdfGifCardNumber.format(new Date()) + StringUtils.leftPad(ticket.getCabecera().getTienda().getCodAlmacen(), 4, "0") + StringUtils.leftPad(
					ticket.getCabecera().getCodCaja(), 4, "0");
			tempGiftCard.setNumTarjetaRegalo(giftCardNumber);

			PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
			event.addExtendedData(PARAM_TARJETA, tempGiftCard);

			Date fechaActivacion = new Date();
			event.addExtendedData(PARAM_TARJETA_FECHA_ACTIVACION, DateUtils.formatDate(fechaActivacion, PARAM_TARJETA_FECHA_FORMAT));
			event.addExtendedData(PARAM_TARJETA_FECHA_BAJA, DateUtils.formatDate(DateUtils.addYears(fechaActivacion, 1), PARAM_TARJETA_FECHA_FORMAT));
			getEventHandler().paymentOk(event);

			return true;
		}
		catch (Exception e) {
			log.error("returnAmount() - Ha habido un error al realizar el pago con tarjeta regalo: " + e.getMessage(), e);
			PaymentErrorEvent event = new PaymentErrorEvent(this, paymentId, e, null, e.getMessage());
			getEventHandler().paymentError(event);
			return false;
		}
	}

	/**
	 * Modificamos el método para que no haga nada, ya que la tarjeta no se ha creado en central.
	 */
	@Override
	public boolean cancelReturn(PaymentDto paymentDto) throws PaymentException {
		try {
			PaymentOkEvent event = new PaymentOkEvent(this, paymentDto.getPaymentId(), paymentDto.getAmount());
			event.setCanceled(true);
			getEventHandler().paymentOk(event);
			return true;
		}
		catch (Exception e) {
			log.error("cancelReturn() - Ha habido un error al cancelar el pago con tarjeta regalo: " + e.getMessage(), e);
			PaymentErrorEvent event = new PaymentErrorEvent(this, paymentId, e, null, e.getMessage());
			getEventHandler().paymentError(event);
			return false;
		}
	}

	/* ########################################################################################################################### */
	/* ###################################################### ESTÁNDAR ########################################################### */
	/* ########################################################################################################################### */

	//GAP 106 VALE DE DEVOLUCIÓN ENTRADA MANUAL
	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		try {
			PaymentInitEvent initEvent = new PaymentInitEvent(this);
			getEventHandler().paymentInitProcess(initEvent);
			GiftCardBean giftCard = (GiftCardBean) parameters.get(PARAM_TARJETA);
			String uidTransaccion = UUID.randomUUID().toString();
			if (!giftCard.isBaja()) {
				if (BigDecimalUtil.isMayor(amount, ticket.getTotales().getPendiente())) {
					String mensaje = I18N.getTexto("El importe indicado supera el importe pendiente de la venta.");
					throw new PaymentException(mensaje);
				}
				if (BigDecimalUtil.isMayor(amount, giftCard.getSaldoTotal())) {
					String mensaje = I18N.getTexto("El importe indicado supera el saldo de la tarjeta.");
					throw new PaymentException(mensaje);
				}
			}

			boolean existeValeManual = checkValeManualExiste(giftCard);
			
			//Personalizamos para que no cree un movimiento si se realiza con un vale de devolución manual que no existe
			if (!existeValeManual && isStartingWithVM(giftCard.getCodTipoTarjeta())) {
				//Cambiamos el tipo tarjeta VM para que se cree con el tipo tarjeta definido para el desarrollo
				giftCard.setCodTipoTarjeta("VM");
				uidTransaccion = "VM";
				BigDecimal saldoRestante = giftCard.getSaldo().subtract(amount);
				//Cambiamos el uidTransaccion para usarlo en el procesado del documento
				//Le añadimos el saldo para luego generar la tarjeta con el restante, no se recupera el saldo en BO
				uidTransaccion = uidTransaccion + "#S#" + saldoRestante.toString();

			}
			else {
				try {
					ListaMovimientoRequestRest request = createProvisionalMovements(amount, giftCard, true, uidTransaccion);
					MovimientosRest.crearMovimientosTarjetaRegaloProvisionales(request);
				} catch (Exception e) {
					// Contralomos HttpServiceRestException para que si el mensaje es que no existe la
					// tarjeta regalo consultamos en local para ver si es vale devolucion creado en tienda y todavia no se ha procesado
					boolean esValeDevTienda = false;
					if(e instanceof HttpServiceRestException) {
						if(e.getMessage().toUpperCase().contains("NO EXISTE LA TARJETA")) {
							ValeDevolucion valeDev = valesDevolucionService.consultarValeDevolucion(giftCard.getNumTarjetaRegalo());
							if(valeDev != null) {
								esValeDevTienda = true;
							}
						}
					}
					if(!esValeDevTienda) {
						throw e;
					}
				}
			}
			PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
			giftCard.setUidTransaccion(uidTransaccion);
			event.addExtendedData(PARAM_TARJETA, giftCard);

			if (!existeValeManual && isStartingWithVM(giftCard.getCodTipoTarjeta())) {
				event.addExtendedData(PARAM_TARJETA_FECHA_ACTIVACION, DateUtils.formatDate(new Date(), PARAM_TARJETA_FECHA_FORMAT));
				event.addExtendedData(PARAM_TARJETA_FECHA_BAJA, DateUtils.formatDate(DateUtils.addYears(new Date(), 1), PARAM_TARJETA_FECHA_FORMAT));
			}
			else {
				if (parameters.containsKey(PARAM_TARJETA_FECHA_ACTIVACION) && parameters.get(PARAM_TARJETA_FECHA_ACTIVACION) != null)
					event.addExtendedData(PARAM_TARJETA_FECHA_ACTIVACION, DateUtils.formatDate((Date) parameters.get(PARAM_TARJETA_FECHA_ACTIVACION), PARAM_TARJETA_FECHA_FORMAT));

				if (parameters.containsKey(PARAM_TARJETA_FECHA_BAJA) && parameters.get(PARAM_TARJETA_FECHA_BAJA) != null)
					event.addExtendedData(PARAM_TARJETA_FECHA_BAJA, DateUtils.formatDate((Date) parameters.get(PARAM_TARJETA_FECHA_BAJA), PARAM_TARJETA_FECHA_FORMAT));
			}

			getEventHandler().paymentOk(event);
			return true;
		}
		catch (Exception e) {
			log.error("pay() - Ha habido un error al realizar el pago con tarjeta regalo: " + e.getMessage(), e);
			PaymentErrorEvent event = new PaymentErrorEvent(this, paymentId, e, null, e.getMessage());
			getEventHandler().paymentError(event);
			return false;
		}
	}

	protected GiftCardBean loadGiftCard(PaymentDto paymentDto) throws RestException, RestHttpException {
		if (paymentDto.getExtendedData().containsKey(PARAM_TARJETA)) {
			return (GiftCardBean) paymentDto.getExtendedData().get(PARAM_TARJETA);
		} else {
			PagoTicket pago = findPagoByPaymentId(paymentDto.getPaymentId());

			if (pago != null && !CollectionUtils.isEmpty(pago.getGiftcards())) {
				return pago.getGiftcards().get(0);
			}
		}
		return null;
	}

	private PagoTicket findPagoByPaymentId(int paymentId) {
		return (PagoTicket) ticket.getPagos()
				.stream()
				.filter(el -> el instanceof PagoTicket && ((PagoTicket) el).getPaymentId() == paymentId)
				.findFirst()
				.orElse(null);
	}


	protected ListaMovimientoRequestRest createProvisionalMovements(BigDecimal amount, GiftCardBean giftCard, boolean isPay, String uidTransacción) throws RestException, RestHttpException {
		List<MovimientoRequestRest> movimientos = new ArrayList<>();
		MovimientoRequestRest mov = new MovimientoRequestRest();
		mov.setUidActividad(sesion.getAplicacion().getUidActividad());
		mov.setNumeroTarjeta(giftCard.getNumTarjetaRegalo());
		mov.setConcepto(ticket.getCabecera().getDesTipoDocumento() + " " + ticket.getCabecera().getCodTicket());
		mov.setUidTransaccion(uidTransacción);
		mov.setFecha(new Date());
		mov.setDocumento(String.valueOf(ticket.getIdTicket()));
		mov.setApiKey(variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY));
		mov.setSaldo(giftCard.getSaldo().doubleValue());
		mov.setSaldoProvisional(giftCard.getSaldoProvisional().doubleValue());
		if (isPay) {
			mov.setSalida(amount.doubleValue());
			mov.setEntrada(0.0);
		}
		else {
			mov.setSalida(0.0);
			mov.setEntrada(amount.doubleValue());
		}
		movimientos.add(mov);
		ListaMovimientoRequestRest request = new ListaMovimientoRequestRest();
		request.setMovimientos(movimientos);
		return request;
	}

	@Override
	public boolean cancelPay(PaymentDto paymentDto) throws PaymentException {
		try {
			GiftCardBean giftCard = loadGiftCard(paymentDto);

			if(giftCard == null) {
				log.error("cancelPay() - No se ha encontrado la tarjeta regalo para el pago seleccionado.");
				PaymentErrorEvent event = new PaymentErrorEvent(this, paymentId, null, null, "No se ha encontrado la tarjeta regalo para el pago seleccionado.");
				getEventHandler().paymentError(event);
				return false;
			}

			String uidTransaccion = giftCard.getUidTransaccion();
			//GAP 176 Vales devolucion
			try {
				if (checkValeManualExiste(giftCard) || !(isStartingWithVM(uidTransaccion) || isStartingWithVM(giftCard.getNumTarjetaRegalo()))) {
					ListaMovimientoRequestRest request = createProvisionalMovements(paymentDto.getAmount(), giftCard, true, uidTransaccion);
					MovimientosRest.anularMovimientosProvisionalesTarjetaRegalo(request);
				}
			} catch (Exception e) {
				//Contralomos HttpServiceRestException para que si el mensaje es que no existe la
				//tarjeta regalo consultamos en local para ver si es vale devolucion creado en tienda y todavia no se ha procesado
				boolean esValeDevTienda = false;
				if(e instanceof HttpServiceRestException) {
					if(e.getMessage().toUpperCase().contains("NO EXISTE LA TARJETA")) {
						ValeDevolucion valeDev = valesDevolucionService.consultarValeDevolucion(giftCard.getNumTarjetaRegalo());
						if(valeDev != null) {
							esValeDevTienda = true;
						}
					}
				}
				if(!esValeDevTienda) {
					throw e;
				}
			}

			PaymentOkEvent event = new PaymentOkEvent(this, paymentDto.getPaymentId(), paymentDto.getAmount());
			event.setCanceled(true);
			getEventHandler().paymentOk(event);
			return true;
		}
		catch (Exception e) {
			log.error("cancelPay() - Ha habido un error al cancelar el pago con tarjeta regalo: " + e.getMessage(), e);
			PaymentErrorEvent event = new PaymentErrorEvent(this, paymentId, e, null, e.getMessage());
			getEventHandler().paymentError(event);
			return false;
		}
	}


	private boolean isStartingWithVM(String value) {
		return value != null && value.startsWith("VM");
	}

	@Override
	public boolean isAvailableForExchange() {
		return false;
	}


	public boolean isConsultadoEnLocal() {
		return consultadoEnLocal;
	}

	public void setConsultadoEnLocal(boolean consultadoEnLocal) {
		this.consultadoEnLocal = consultadoEnLocal;
	}

	private boolean checkValeManualExiste(GiftCardBean giftCard) {
		boolean existeValeManual = false;
		try {
			giftCardService.getGiftCard(giftCard.getNumTarjetaRegalo());
			existeValeManual = true;
		} catch (Exception e) {
			log.error("pay() - No se ha encontrado vale manual.");
		}
		return existeValeManual;
	}

}
