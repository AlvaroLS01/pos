package com.comerzzia.dinosol.pos.services.payments.methods.types;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.DescuentosEmpleadoManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.VirtualMoneyManager;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentsManagerImpl;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentsCompletedEvent;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
@Scope("prototype")
@SuppressWarnings("unchecked")
public class DinoPaymentsManagerImpl extends PaymentsManagerImpl {
	
	private Logger log = Logger.getLogger(DinoPaymentsManagerImpl.class);

    public static final String PAYMENT_CODE_CASH = "0001";
    public static final String PAYMENT_CODE_DATAPHONE = "0003";
    public static final String PAYMENT_CODE_CREDIT_CARD = "0002";

    @Autowired
    public Sesion sesion;
    
	@Override
	public boolean isPaymentMethodAvailableForReturn(String paymentCode) {
	    boolean available =  super.isPaymentMethodAvailableForReturn(paymentCode);
	    
	    if(available && ticketOrigen != null) {
	    	List<PagoTicket> originalPayments = ticketOrigen.getPagos();
	    	
	    	available = false;
	    	
	    	boolean payWithCash = false;
	    	boolean payWithCreditCard = false;
	    	boolean payWithDataphone = false;
	    	
	    	if(originalPayments != null) {
	    		for(PagoTicket originalPayment : originalPayments) {
	    			if(originalPayment.getCodMedioPago().equals(paymentCode)) {
	    				available = true;
	    			}
	    			
	    			if(originalPayment.getCodMedioPago().equals(PAYMENT_CODE_CASH)) {
	    				payWithCash = true;
	    			}
	    			
	    			if(originalPayment.getCodMedioPago().equals(PAYMENT_CODE_CREDIT_CARD)) {
	    				payWithCreditCard = true;
	    			}
	    			
	    			if(originalPayment.getCodMedioPago().equals(PAYMENT_CODE_DATAPHONE)) {
	    				payWithDataphone = true;
	    			}
	    		}
	    	}
	    	
	    	if(paymentCode.equals(PAYMENT_CODE_CASH) && (payWithCash || payWithCreditCard || payWithDataphone)) {
	    		available = true;
	    	}
	    }
	    
	    return available;
	}
	
	@Override
	public void returnAmount(String paymentCode, BigDecimal amount) {
		if(ticketOrigen != null) {
			boolean amountAllowed = isAmountAllowed(paymentCode, amount);
			
			if(!amountAllowed) {
				throw new RuntimeException(I18N.getTexto("El importe introducido supera el importe original pagado en este medio de pago."));
			}
		}
		
	    super.returnAmount(paymentCode, amount);
	}

	private boolean isAmountAllowed(String paymentCode, BigDecimal amount) {
		List<PagoTicket> originalPayments = ticketOrigen.getPagos();
		BigDecimal acumOriginAmountPayment = BigDecimal.ZERO;
		if(originalPayments != null) {
    		for(PagoTicket originalPayment : originalPayments) {
    			boolean returnWithDataphone = originalPayment.getCodMedioPago().equals(PAYMENT_CODE_CREDIT_CARD) && paymentCode.equals(PAYMENT_CODE_DATAPHONE);
				if(originalPayment.getCodMedioPago().equals(paymentCode) || returnWithDataphone) {
					acumOriginAmountPayment = acumOriginAmountPayment.add(originalPayment.getImporte());
    			}
    		}
    	}
		
		List<PagoTicket> payments = ticket.getPagos();
		BigDecimal acumAmountPayment = BigDecimal.ZERO;
		if(payments != null) {
    		for(PagoTicket payment : payments) {
				if(payment.getCodMedioPago().equals(paymentCode)) {
					acumAmountPayment = acumAmountPayment.add(payment.getImporte());
    			}
    		}
    	}
		
		if(paymentCode.equals(PAYMENT_CODE_CASH)) {
			return true;
		}
		
		if(BigDecimalUtil.isMayor(acumAmountPayment.abs().add(amount), acumOriginAmountPayment)) {
			return false;
		}
		else {
			return true;
		}
    }
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setTicketData(ITicket ticket, ITicket ticketOrigen) {
		super.setTicketData(ticket, ticketOrigen);
		
		Integer paymentId = ((DinoTicketVentaAbono) ticket).getLastPaymentId();
		if(paymentId != null) {
			paymentId++;
			this.paymentId = this.paymentId + paymentId;
		}
	}
	
	@Override
	protected void addCurrentPayment(PaymentOkEvent event) {
		super.addCurrentPayment(event);
		
		// Personalización: el movimiento de caja no se guarda como en el estándar si viene de Descuentos de Empleado para guardar el valor real que devuelve la pasarela
		if(event.getSource() instanceof DescuentosEmpleadoManager) {
			String paymentCode = ((DescuentosEmpleadoManager) event.getSource()).getPaymentCode();
			BigDecimal amount = event.getAmount();
			try {
				recordCashFlow(paymentCode, amount, true);
			}
			catch (Exception e) {
				log.error("addCurrentPayment() - Ha habido un error al guardar el movimiento de caja para el descuento de empleado: " + e.getMessage(), e);
			}
		}
		// Fin de personalización
	}
	
	@Override
	public void pay(String paymentCode, BigDecimal amount) {
		try {
			log.debug("pay() - Se va a pagar " + amount + " del medio de pago " + paymentCode);
			
			PaymentMethodManager manager = paymentsAvailable.get(paymentCode);
			
			if(manager.isUniquePayment() && existsPayment(paymentCode)) {
				throw new RuntimeException(I18N.getTexto("Este medio de pago no permite más de un pago."));
			}
			
			controlarPagoEnPosReparto(paymentCode);
			
			assignIdTicket();
			manager.setPaymentId(paymentId);
			
			boolean paymentOk = manager.pay(amount);
			incrementPaymentId();
			
			if(paymentOk) {
				// Personalización: el movimiento de caja no se guarda como en el estándar si viene de Descuentos de Empleado para guardar el valor real que devuelve la pasarela
				boolean hayQueGrabarMovimientoCaja = manager.recordCashFlowImmediately() && !(manager instanceof DescuentosEmpleadoManager);
				// Fin de personalización
				if(hayQueGrabarMovimientoCaja) {
					recordCashFlow(paymentCode, amount, true);
				}
				
				if(!(manager instanceof DescuentosEmpleadoManager)) {
					pending = ticket.getTotales().getPendiente().subtract(amount);
					log.debug("pay() - Importe pendiente: " + pending);
				}
				
				if (BigDecimalUtil.isMenorOrIgualACero(pending)) {
					PaymentsCompletedEvent event = new PaymentsCompletedEvent(this);
					getEventsHandler().paymentsCompleted(event);
				}
			}
		}
		catch(Exception e) {
			log.error("pay() - Ha habido un error al realizar el pago: " + e.getMessage(), e);
			if (e instanceof RuntimeException ) {
				throw (RuntimeException)e;
			} else {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	protected void controlarPagoEnPosReparto(String paymentCode) {
		if(((DinoCabeceraTicket) ticket.getCabecera()).getServicioRepartoDto() != null) {
			if(!isPaymentMethodAvailablePosReparto(paymentCode)) {
				throw new RuntimeException(I18N.getTexto("Este medio de pago no es válido para el reparto, por favor use otra tarjeta"));
			}
		}
	}
	
	public void setCurrentPayments(Map<Integer, PaymentDto> currentPayments) {
		this.currentPayments = currentPayments;
	}
	
	// DIN-289 - POS de reparto
	public boolean isPaymentMethodAvailablePosReparto(String paymentCode) {
		return getPaymentsMehtodManagerAvailables().get(paymentCode) instanceof VirtualMoneyManager;
	}
	
	public void addPending(BigDecimal amount) {
		this.pending = this.pending.add(amount);
		log.debug("addPending() - Importe pendiente: " + pending);
	}
	
}
