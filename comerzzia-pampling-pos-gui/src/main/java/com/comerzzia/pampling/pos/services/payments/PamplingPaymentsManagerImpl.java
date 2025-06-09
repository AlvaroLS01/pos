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
               for (PaymentMethodManager manager : paymentsAvailable.values()) {
                       if (manager instanceof CashlogyManager) {
                               return ((CashlogyManager) manager).isCashlogyActivo();
                       }
               }
               return false;
       }

       /**
        * Devuelve la instancia de {@link CashlogyManager} si está disponible.
        */
       public CashlogyManager getCashlogyManager() {
               for (PaymentMethodManager manager : paymentsAvailable.values()) {
                       if (manager instanceof CashlogyManager) {
                               return (CashlogyManager) manager;
                       }
               }
               return null;
       }

}
