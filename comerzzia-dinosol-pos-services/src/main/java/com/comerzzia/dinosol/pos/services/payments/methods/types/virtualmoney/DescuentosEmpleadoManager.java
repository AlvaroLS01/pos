package com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.virtualmoney.client.AccountsBalancesApi;
import com.comerzzia.api.virtualmoney.client.model.AccountDTO;
import com.comerzzia.api.virtualmoney.client.model.BalanceEvent;
import com.comerzzia.api.virtualmoney.client.model.BalanceRedemptionCancelRequest;
import com.comerzzia.api.virtualmoney.client.model.BalanceTransactionRequest;
import com.comerzzia.api.virtualmoney.client.model.TransactionItem;
import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.dto.LineaAgrupadaDto;
import com.comerzzia.dinosol.pos.util.xml.ObjectParseUtil;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Scope("prototype")
public class DescuentosEmpleadoManager extends VirtualMoneyManager {
	
	private Logger log = Logger.getLogger(DescuentosEmpleadoManager.class);
	
	@Autowired
	private Sesion sesion;

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		log.debug("pay() - Iniciamos la petición para pagar " + amount);
		
		if(BigDecimalUtil.isIgualACero(ticket.getTotales().getPendiente())) {
			throw new PaymentException(I18N.getTexto("No se puede utilizar el descuento de empleado porque no hay importe pendiente."));
		}
		
		controlarPagoUnicoRango();

		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		String numeroTarjeta = (String) parameters.get(PARAM_NUMERO_TARJETA);
		
		comprobarPagoUnicoTarjeta(numeroTarjeta);

		try {
			AccountsBalancesApi accountsBalancesApi = getAccountsBalancesApi();

			AccountDTO account = consultarSaldo(numeroTarjeta);

			BalanceTransactionRequest request = new BalanceTransactionRequest();
			request.setTransactionDate(new Date());
			request.setAmount(ticket.getTotales().getTotalAPagar());
			request.setDocument(ticket.getCabecera().getCodTicket());
			request.setDocumentLine(paymentId);
			request.setUserId(sesion.getSesionUsuario().getUsuario().getIdUsuario());
			request.setStoreId(sesion.getAplicacion().getCodAlmacen());
			request.setTerminalId(sesion.getAplicacion().getCodCaja());

			List<TransactionItem> items = getTransactionItems();
			request.setTransactionItems(items);

			BalanceEvent balanceEvent = accountsBalancesApi.accountBalanceNewTransaction(account.getId(), account.getPrincipaltBalance().getBalanceId(), request);
			log.debug("pay() - " + balanceEvent);
			Map<String, Object> respuestaDto = ObjectParseUtil.introspect(balanceEvent);

			PaymentOkEvent event = new PaymentOkEvent(this, paymentId, balanceEvent.getOutput());
			for (String key : respuestaDto.keySet()) {
				event.addExtendedData(key, respuestaDto.get(key));
			}
			event.addExtendedData("accountId", account.getId());
			event.addExtendedData("accountBalanceId", account.getPrincipaltBalance().getBalanceId());
			event.addExtendedData("numeroTarjeta", numeroTarjeta);
			event.addExtendedData("balanceAmount", account.getPrincipaltBalance().getBalance().subtract(amount));
			event.addExtendedData("fecha", respuestaDto.get("eventDate"));
			event.addExtendedData("numeroOperacion", respuestaDto.get("accountTransactionUid"));
			getEventHandler().paymentOk(event);

			log.debug("pay() - Finalizada la petición de pago.");

			return true;
		}
		catch (Exception e) {
			String message = e.getMessage();
			log.error("pay() - Ha habido un error al pagar: " + message, e);
			
			message = message.replaceAll("Error interno: com.comerzzia.api.core.service.exception.BadRequestException: ", "");
			throw new PaymentException(I18N.getTexto("Ha habido un error al realizar la petición de pago: " + message), e, paymentId, this);
		}
	}

	@SuppressWarnings("unchecked")
	private void controlarPagoUnicoRango() throws PaymentException {
		for(PagoTicket pago : (List<PagoTicket>) ticket.getPagos()) {
			String claseControl = pago.getMedioPago().getClaseControl();
			if(StringUtils.isNotBlank(claseControl)) {
				PaymentMethodManager paymentMethodManager = (PaymentMethodManager) ContextHolder.get().getBean(claseControl);
				if(paymentMethodManager instanceof DescuentosEmpleadoManager) {
					throw new PaymentException(I18N.getTexto("Ya existe una tarjeta de la misma categoría en esta compra. Debe eliminarla para poder añadir la actual."));
				}
			}
		}
	}
	
	@Override
	public boolean cancelPay(PaymentDto payment) throws PaymentException {
		log.debug("cancelPay() - Iniciamos la petición para cancelar el pago" + payment.getPaymentId());
		
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);
		
		try {
			AccountsBalancesApi accountsBalancesApi = getAccountsBalancesApi();
			
			Long accountId = (Long) payment.getExtendedData().get("accountId");
			Long accountBalanceId = (Long) payment.getExtendedData().get("accountBalanceId");
			String transactionUid = (String) payment.getExtendedData().get("accountTransactionUid");
			String document = (String) payment.getExtendedData().get("document");
			BigDecimal amount = ticket.getTotales().getTotalAPagar().abs();
			
			BalanceRedemptionCancelRequest request = new BalanceRedemptionCancelRequest();
			request.setAccountTransactionUid(transactionUid);
			request.setAmount(amount);
			request.setDocument(document);
			request.setCancelUserId(sesion.getSesionUsuario().getUsuario().getIdUsuario());
			
			BalanceEvent response = accountsBalancesApi.accountBalanceTransactionCancel(accountId, accountBalanceId, request);
			log.debug("cancelPay() - " + response);

			PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
			event.setCanceled(true);
			getEventHandler().paymentOk(event);

			log.debug("cancelPay() - Finalizada la petición de pago.");
			return true;
		}
		catch (Exception e) {
			log.error("cancelPay() - Ha habido un error al pagar: " + e.getMessage(), e);

			throw new PaymentException(I18N.getTexto("Ha habido un error al realizar la petición de pago: " + e.getMessage()), e, paymentId, this);
		}
	}
	
	@Override
	public boolean returnAmount(BigDecimal amount) throws PaymentException {
		return false;
	}
	
	@Override
	public boolean cancelReturn(PaymentDto payment) throws PaymentException {
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<TransactionItem> getTransactionItems() {
		List<TransactionItem> transactionItems = new ArrayList<TransactionItem>();
		
		List<LineaAgrupadaDto> lineasAgrupadas = new ArrayList<LineaAgrupadaDto>();
		for(LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
			LineaAgrupadaDto lineaAgrupada = new LineaAgrupadaDto();
			lineaAgrupada.setCodart(linea.getCodArticulo());
			lineaAgrupada.setPrecio(linea.getPrecioTotalConDto());
			if(lineasAgrupadas.contains(lineaAgrupada)) {
				lineaAgrupada = lineasAgrupadas.get(lineasAgrupadas.indexOf(lineaAgrupada));
			}
			else {
				lineasAgrupadas.add(lineaAgrupada);
			}
			
			lineaAgrupada.addCantidad(linea.getCantidad());
		}
		
		for(LineaAgrupadaDto lineaAgrupada : lineasAgrupadas) {
			TransactionItem item = new TransactionItem();
			item.setItemCode(lineaAgrupada.getCodart());
			item.setQuantity(lineaAgrupada.getCantidad());
			item.setPrice(lineaAgrupada.getPrecio());
			BigDecimal extendedPrice = BigDecimalUtil.redondear(item.getPrice().multiply(item.getQuantity()), 2, BigDecimal.ROUND_HALF_UP);
			item.setExtendedPrice(extendedPrice);
			
			transactionItems.add(item);
		}
		
		return transactionItems;
	}
	
	@Override
	public boolean isUniquePayment() {
		return true;
	}

}
