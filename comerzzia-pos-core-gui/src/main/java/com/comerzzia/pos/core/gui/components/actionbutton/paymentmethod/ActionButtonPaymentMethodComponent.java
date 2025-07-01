

package com.comerzzia.pos.core.gui.components.actionbutton.paymentmethod;

import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.paymentmethod.PaymentMethodButtonConfigurationBean;


public class ActionButtonPaymentMethodComponent extends ActionButtonNormalComponent {
    protected PaymentMethodButtonConfigurationBean paymentMethodConfiguration;
    
    /**
     * Constructor de botón de tipo medio de pago
     */
    public ActionButtonPaymentMethodComponent() {
        super();

        //establecemos un estilo para el botón
        btAccion.getStyleClass().add("boton-mediopago-botonera");
    }

    
    public void setButtonConfiguration(ButtonConfigurationBean configuracion) {

        if (configuracion instanceof PaymentMethodButtonConfigurationBean) this.paymentMethodConfiguration = (PaymentMethodButtonConfigurationBean)configuracion;
        
        super.setButtonConfiguration(configuracion);
    }
    
    public PaymentMethodDetail getPaymentMethod(){
        return paymentMethodConfiguration.getPaymentMethod();
    }
    
    public boolean getDirectBalanceDuePayment() {
    	if (paymentMethodConfiguration != null) {
    		return paymentMethodConfiguration.getDirectBalanceDuePayment();
    	}
    	return false;
    }
}
