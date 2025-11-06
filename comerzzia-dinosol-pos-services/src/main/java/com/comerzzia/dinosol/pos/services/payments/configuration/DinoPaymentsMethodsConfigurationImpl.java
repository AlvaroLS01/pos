package com.comerzzia.dinosol.pos.services.payments.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.pos.persistence.core.tiendas.cajas.TiendaCajaBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.cajas.TiendaCajaService;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfigurationImpl;

@Component
@Primary
public class DinoPaymentsMethodsConfigurationImpl extends PaymentsMethodsConfigurationImpl {
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private TiendaCajaService tiendaCajaService;

	@Override
	public void saveConfiguration() throws Exception {
		TiendaCajaBean tiendaCaja = tiendaCajaService.consultarTPV(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getUidCaja());
		XMLDocument xmlConfiguracion = new XMLDocument(tiendaCaja.getConfiguracion());

		storeConfigXml = xmlConfiguracion.getRoot();

		XMLDocumentNode paymentsMethodsNode = storeConfigXml.getNodo("medios_pago", true);
		if (paymentsMethodsNode == null) {
			paymentsMethodsNode = new XMLDocumentNode(xmlConfiguracion, "medios_pago");
		}
		else {
			storeConfigXml.getNode().removeChild(paymentsMethodsNode.getNode());
		}

		paymentsMethodsNode = new XMLDocumentNode(xmlConfiguracion, "medios_pago");
		for (PaymentMethodConfiguration configuration : paymentMethodConfigurations) {
			XMLDocumentNode paymentMethodNode = new XMLDocumentNode(xmlConfiguracion, "medio_pago");

			paymentMethodNode.añadirHijo("cod_medio_pago", configuration.getPaymentCode());

			XMLDocumentNode paymentMethodParamsNode = new XMLDocumentNode(xmlConfiguracion, "parametros");
			for (String key : configuration.getStoreConfigurationProperties().keySet()) {
				for(ConfigurationPropertyDto configurationManager : configuration.getManager().getConfigurationProperties()) {
					// Corrección de error del estándar. Si la propiedad no está en el manager, no se guarda
					if(key.equals(configurationManager.getKey())) {						
						String value = configuration.getStoreConfigurationProperty(key);
						paymentMethodParamsNode.añadirHijo(key, value);
					}
				}
			}
			paymentMethodNode.añadirHijo(paymentMethodParamsNode);

			paymentsMethodsNode.añadirHijo(paymentMethodNode);
		}
		storeConfigXml.añadirHijo(paymentsMethodsNode);

		tiendaCaja.setConfiguracion(xmlConfiguracion.getBytes());
		tiendaCajaService.grabarConfiguracionDispositivos(tiendaCaja);

		loadConfiguration();
	}

}
