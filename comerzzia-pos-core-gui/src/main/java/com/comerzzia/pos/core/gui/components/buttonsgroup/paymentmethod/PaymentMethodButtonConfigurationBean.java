
package com.comerzzia.pos.core.gui.components.buttonsgroup.paymentmethod;

import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;

public class PaymentMethodButtonConfigurationBean extends ButtonConfigurationBean{
    
    private PaymentMethodDetail paymentMethod;
    private Boolean directBalanceDuePayment = false;
    
    public PaymentMethodButtonConfigurationBean(String rutaImagen, String texto, String textoAccesoRapido, String nombreAccion, String tipoAccion, PaymentMethodDetail medioPago, Boolean directBalanceDuePayment){
        super(rutaImagen, texto, textoAccesoRapido, nombreAccion, tipoAccion);
        
        this.paymentMethod = medioPago;
        if (directBalanceDuePayment != null) this.directBalanceDuePayment = directBalanceDuePayment;
    }

    public PaymentMethodDetail getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodDetail medioPago) {
        this.paymentMethod = medioPago;
    }

	public Boolean getDirectBalanceDuePayment() {
		return directBalanceDuePayment;
	}

	public void setDirectBalanceDuePayment(Boolean directBalanceDuePayment) {
		this.directBalanceDuePayment = directBalanceDuePayment;
	}
    
}
