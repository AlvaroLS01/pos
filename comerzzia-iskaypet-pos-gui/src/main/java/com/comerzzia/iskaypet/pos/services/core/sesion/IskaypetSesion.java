package com.comerzzia.iskaypet.pos.services.core.sesion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.librerias.sipay.client.constants.SipayConstants;
import com.comerzzia.iskaypet.pos.services.payments.methods.types.sipay.SipayService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;

@Component
@Primary
public class IskaypetSesion extends Sesion {
	
	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	@Autowired
	protected SipayService sipayService;
	
	@Override
	public void initAplicacion() throws SesionInitException {
		super.initAplicacion();
		
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			if (SipayConstants.MANAGER_CONTROL_CLASS.equals(configuration.getControlClass())) {
				try {
					sipayService.cargarClavesCajones();
				} catch (Exception e) {
					log.error("initAplicacion() - Error al inicializar dispositivo y cargar las claves de los cajones - " + e.getMessage());
				}
				
			}
		}
		
	}

}
