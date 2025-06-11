package com.comerzzia.pampling.pos.services.payments;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.services.payments.methods.types.CashlogyManager;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.PaymentsManagerImpl;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;

@Component
@Primary
@Scope("prototype")
public class PamplingPaymentsManagerImpl extends PaymentsManagerImpl {

        public boolean isCashlogyEnable() {
		//Por si no esta definido el medio de pago por defecto que me coja el EFECTIVO
		String key = (MediosPagosService.medioPagoDefecto == null) ? "0000" : MediosPagosService.medioPagoDefecto.getCodMedioPago();

		PaymentMethodManager paymentMethodManager = paymentsAvailable.get(key);

		if (paymentMethodManager instanceof CashlogyManager) {
			return ((CashlogyManager) paymentMethodManager).isCashlogyActivo();
		}

                return false;
        }

       public CashlogyManager getCashlogyManager() {
               String key = (MediosPagosService.medioPagoDefecto == null) ? "0000" : MediosPagosService.medioPagoDefecto.getCodMedioPago();
               PaymentMethodManager paymentMethodManager = paymentsAvailable.get(key);
               if (paymentMethodManager instanceof CashlogyManager) {
                       return (CashlogyManager) paymentMethodManager;
               }
               return null;
       }

}
