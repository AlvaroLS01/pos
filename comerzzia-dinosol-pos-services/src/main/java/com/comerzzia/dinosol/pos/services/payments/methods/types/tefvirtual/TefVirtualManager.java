package com.comerzzia.dinosol.pos.services.payments.methods.types.tefvirtual;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;

@Component
@Scope("prototype")
public class TefVirtualManager extends TefBasicManager {

	@Override
	public void conect() {
	}

	@Override
	public void disconect() {
	}

	@Override
	public void cancel() {
	}

	@Override
	protected void doPaymentRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response) {
	}

	@Override
	protected void doReturnRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response) {
	}

	@Override
	protected void doPaymentCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response) {
	}

	@Override
	protected void doReturnCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response) {
	}
}