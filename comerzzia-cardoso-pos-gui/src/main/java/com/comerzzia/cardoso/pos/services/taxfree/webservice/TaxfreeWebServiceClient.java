package com.comerzzia.cardoso.pos.services.taxfree.webservice;

import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;

import com.comerzzia.api.rest.client.exceptions.RestException;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class TaxfreeWebServiceClient {

	private static Logger log = Logger.getLogger(TaxfreeWebServiceClient.class);
	
	public static String llamadaWsConsulta(String url, String user, String pass, String json) throws Exception {
		log.debug("llamadaWsConsulta() - Inicio de la llamada a Innova");
		
		String respuesta = null;
		OkHttpClient client = new OkHttpClient();
		SSLContext context;
		try {
			context = SSLContext.getInstance("TLSv1.2");
			context.init(null, null, null);
			client.setSslSocketFactory(context.getSocketFactory());
			com.squareup.okhttp.MediaType mediaType = com.squareup.okhttp.MediaType.parse("application/json");
			
			Request request = null;
			RequestBody body = RequestBody.create(mediaType, json);
			
			log.debug("llamadaWsConsulta() - URL de servicio rest en la que se realiza la petición: " + url);
			log.debug("llamadaWsConsulta() - JSON enviado: " + json);
			
			String credentials = Credentials.basic(user, pass); 
			request = new Request.Builder().url(url).post(body).addHeader("content-type", "application/json").addHeader("Authorization", credentials).build();
			
			com.squareup.okhttp.Response response;
			
			response = client.newCall(request).execute();
			respuesta = response.body().string();
			
			log.debug("llamadaWsConsulta() - JSON recibido: " + respuesta);
			
		} catch (Exception e) {
			
			throw new RestException("Se ha producido un error realizando la petición. Causa: "
			+ e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		return respuesta;
		
	}
}
