package com.comerzzia.cardoso.pos.gui.ventas.tickets.pagos.rest.empleados;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.comerzzia.cardoso.pos.persistence.promociones.rest.response.PromoEmpleadosImporte;
import com.comerzzia.pos.util.i18n.I18N;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * GAP - PERSONALIZACIONES V3 - PROMOCIÓN EMPLEADO
 */
public class CARDOSOClientRestPromocionEmpleados {

	private static Logger log = Logger.getLogger(CARDOSOClientRestPromocionEmpleados.class);

	public static BigDecimal getAcceso(String path, String apiKey, String uidActividad, String numTarjetaFidelizado,
			Long idPromocion, BigDecimal totalPagar) throws Exception {
		log.debug("getAcceso() - GAP - PERSONALIZACIONES V3 - PROMOCIÓN EMPLEADO");
		log.info(
				"getAcceso() - Iniciamos la petición para recibir el total de descuento de la promoción de empleados...");

		try {
			Client client = Client.create();
			String url = path + "/promocionEmpleados/acceso" + "?apiKey=" + apiKey + "&uidActividad=" + uidActividad
					+ "&numTarjetaFidelizado=" + numTarjetaFidelizado.trim() + "&idPromocion=" + idPromocion
					+ "&totalPagar=" + totalPagar;

			log.info("getAcceso() - REQUEST : " + url);
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			String output = response.getEntity(String.class);
			log.info("getAcceso() - RESPONSE : " + output);

			Gson gson = new Gson();
			PromoEmpleadosImporte promoImporte = gson.fromJson(output, PromoEmpleadosImporte.class);
			if (promoImporte.getImporte() != null) {
				return promoImporte.getImporte();
			} else {
				String msgError = I18N
						.getTexto("No se ha podido calcular el importe de descuento para la promoción de empleados.");
				throw new Exception(msgError);
			}
		} catch (Exception e) {
			String msgError = I18N.getTexto(e.getMessage());
			log.error("getAcceso() - " + msgError);
			throw new Exception(msgError);
		}

	}

	public static void generarUso(String path, String apiKey, String uidActividad, Long idPromocion,
			String numeroTarjeta, String uidTransaccion, String referenciaUso, Date fechaUso, BigDecimal importeUso,
			BigDecimal importeTotal) throws Exception {
		
		log.debug("generarUso() - GAP - PERSONALIZACIONES V3 - PROMOCIÓN EMPLEADO");
		log.info("generarUso() - Iniciamos la petición para generar uso de la promoción de empleados...");

		try {
			Client client = Client.create();
			String url = path + "/promocionEmpleados/usos" + "?apiKey=" + apiKey + "&uidActividad=" + uidActividad
					+ "&idPromocion=" + idPromocion + "&numeroTarjeta=" + numeroTarjeta + "&uidTransaccion=" + uidTransaccion 
					+ "&referenciaUso=" + referenciaUso + "&importeUso=" + importeUso + "&importeTotal=" + importeTotal;
			
			SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			if(fechaUso != null){
				url += "&fechaUso=" + formateador.format(fechaUso).replace(" ", "+");
			}
			else{
				url += "&fechaUso=" + "";
			}

			log.info("generarUso() - REQUEST : " + url);
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.put(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				log.info("generarUso() - Error generando uso de promoción de empleado en la API. Response Status: " + response.getStatus());
				throw new Exception();				
			} else {
				log.info("generarUso() - Uso de la promoción de empleado correctamente generado. Response Status: " + response.getStatus());
			}

		} catch (Exception e) {
			String msgError = I18N.getTexto(e.getMessage());
			log.error("generarUso() - " + msgError);
			throw new Exception(msgError);
		}

	}
	
	public static void anularUso(String path, String apiKey, String uidActividad, Long idPromocion,
			String numeroTarjeta, String uidTransaccion) throws Exception {
		
		log.debug("anularUso() - GAP - PERSONALIZACIONES V3 - PROMOCIÓN EMPLEADO");
		log.info("anularUso() - Iniciamos la petición para anular uso de la promoción de empleados...");

		try {
			Client client = Client.create();
			String url = path + "/promocionEmpleados/usos" + "?apiKey=" + apiKey + "&uidActividad=" + uidActividad
					+ "&idPromocion=" + idPromocion + "&numeroTarjeta=" + numeroTarjeta + "&uidTransaccion=" + uidTransaccion;

			log.info("anularUso() - REQUEST : " + url);
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.delete(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				log.info("anularUso() - Error generando uso de promoción de empleado en la API. Response Status: " + response.getStatus());
				throw new Exception();				
			} else {
				log.info("anularUso() - Uso de la promoción de empleado correctamente generado. Response Status: " + response.getStatus());
			}

		} catch (Exception e) {
			String msgError = I18N.getTexto(e.getMessage());
			log.error("anularUso() - " + msgError);
			throw new Exception(msgError);
		}

	}

}
