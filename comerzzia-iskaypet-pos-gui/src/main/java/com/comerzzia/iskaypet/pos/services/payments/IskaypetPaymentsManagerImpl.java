package com.comerzzia.iskaypet.pos.services.payments;

import com.comerzzia.pos.services.payments.PaymentsManagerImpl;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Primary
@Component
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
public class IskaypetPaymentsManagerImpl extends PaymentsManagerImpl {

    private Logger log = Logger.getLogger(IskaypetPaymentsManagerImpl.class);

    @Override
    protected void recordCashFlow(String paymentCode, BigDecimal amount, boolean isIncome) {
        log.debug("recordCashFlow - no registramos el movimiento, ya que lo haremos al finalizar el ticket.");
    }
}
