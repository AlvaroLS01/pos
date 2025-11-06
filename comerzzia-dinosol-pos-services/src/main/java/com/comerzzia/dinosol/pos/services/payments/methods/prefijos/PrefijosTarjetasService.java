package com.comerzzia.dinosol.pos.services.payments.methods.prefijos;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.pos.services.payments.methods.types.bp.BPManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.GttManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.VirtualMoneyManager;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;

@Component
public class PrefijosTarjetasService {

	private Logger log = Logger.getLogger(PrefijosTarjetasService.class);

	private Map<String, List<String>> prefijos;

	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;

	// Devuelve el código de medio de pago de la tarjeta que se introduce por parámetro
	public String getMedioPagoPrefijo(String numTarjeta) {
		log.debug("getMedioPagoPrefijo() - Consultando medio de pago asociado a " + numTarjeta);

		if (prefijos == null) {
			leerXmlConfiguracion();
		}

		String resultado = null;

		for (String codMedioPago : prefijos.keySet()) {
			List<String> listaPrefijos = prefijos.get(codMedioPago);
			for (String prefijo : listaPrefijos) {
				if (numTarjeta.startsWith(prefijo)) {
					resultado = codMedioPago;
					break;
				}
			}
		}

		return resultado;
	}

	public boolean isTarjetaBp(String numTarjeta) {
		boolean res = false;
		String codigoMedioPago = getMedioPagoPrefijo(numTarjeta);
		log.debug("isTarjetaBp() - Comprobando los códigos de configuración de los medios de pago");
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			String codigoPago = configuration.getPaymentCode();

			if (codigoPago.equals(codigoMedioPago)) {
				try {
					if (configuration.getManager() instanceof BPManager) {
						res = true;
						log.debug("isTarjetaBp() - Configuración cargada : " + configuration.getManager().getPaymentCode());
					}
				}
				catch (Exception e) {
					String mensajeError = "Error al cargar la configuración del medio";
					log.error("isTarjetaBp() - " + mensajeError + " " + configuration.getPaymentCode() + " : " + e.getMessage());
				}
			}
		}
		return res;
	}

	public boolean isTarjetaGtt(String numTarjeta) {
		boolean res = false;
		String codigoMedioPago = getMedioPagoPrefijo(numTarjeta);
		log.debug("isTarjetaGtt() - Comprobando los códigos de configuración de los medios de pago");
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			String codigoPago = configuration.getPaymentCode();

			if (codigoPago.equals(codigoMedioPago)) {
				try {
					if (configuration.getManager() instanceof GttManager) {
						res = true;
						log.debug("isTarjetaGtt() - Configuración cargada : " + configuration.getManager().getPaymentCode());
					}
				}
				catch (Exception e) {
					String mensajeError = "Error al cargar la configuración del medio";
					log.error("isTarjetaGtt() - " + mensajeError + " " + configuration.getPaymentCode() + " : " + e.getMessage());
				}
			}
		}
		return res;
	}

	public boolean isTarjetaVirtualMoney(String numTarjeta) {
		boolean res = false;
		String codigoMedioPago = getMedioPagoPrefijo(numTarjeta);
		log.debug("isTarjetaVirtualMoney() - Comprobando los códigos de configuración de los medios de pago");
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			String codigoPago = configuration.getPaymentCode();

			if (codigoPago.equals(codigoMedioPago)) {
				try {
					if (configuration.getManager() instanceof VirtualMoneyManager) {
						res = true;
						log.debug("isTarjetaVirtualMoney() - Configuración cargada : " + configuration.getManager().getPaymentCode());
					}
				}
				catch (Exception e) {
					String mensajeError = "Error al cargar la configuración del medio";
					log.error("isTarjetaVirtualMoney() - " + mensajeError + " " + configuration.getPaymentCode() + " : " + e.getMessage());
				}
			}
		}
		return res;
	}

	private void leerXmlConfiguracion() {
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = comerzziaApp.obtenerUrlFicheroConfiguracion("prefijos_mediospago.xml");
		File file = new File(url.getPath());
		log.debug("Iniciamos la lectura del archivo prefijos_mediospago.xml en la ruta : " + url.toString());

		prefijos = new HashMap<String, List<String>>();
		try {
			XMLDocument xml = new XMLDocument(file);

			for (XMLDocumentNode nodoXml : xml.getRoot().getHijos()) {
				String codMedioPago = nodoXml.getNodo("codMedioPago").getValue();
				List<String> listaPrefijos = new ArrayList<String>();
				for (XMLDocumentNode nodoPrefijo : nodoXml.getNodo("prefijos").getHijos()) {
					String prefijo = nodoPrefijo.getValue();
					listaPrefijos.add(prefijo);
				}

				prefijos.put(codMedioPago, listaPrefijos);
			}
		}
		catch (Exception e) {
			log.error("leerXmlConfiguracion() - Ha habido un error al cargar el XML de configuración: " + e.getMessage(), e);
		}
	}
	
	public List<String> getCodMediosPagoTarjetas() {
		if (prefijos == null) {
			leerXmlConfiguracion();
		}
		
		List<String> listaMediosPago = new ArrayList<String>();
		for(String codMedioPago : prefijos.keySet()) {
			listaMediosPago.add(codMedioPago);
		}
		return listaMediosPago;
	}

}
