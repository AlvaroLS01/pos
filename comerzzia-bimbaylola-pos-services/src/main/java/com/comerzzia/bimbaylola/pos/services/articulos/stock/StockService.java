package com.comerzzia.bimbaylola.pos.services.articulos.stock;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.articulos.stock.RespuestaErrorStockDTO;
import com.comerzzia.bimbaylola.pos.persistence.articulos.stock.RespuestaTokenDTO;
import com.comerzzia.bimbaylola.pos.persistence.articulos.stock.SolicitarStockDTO;
import com.comerzzia.bimbaylola.pos.persistence.articulos.stock.SolicitarTokenDTO;
import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestTimeoutException;
import com.google.gson.Gson;

@Service
public class StockService{

	@Autowired
	private VariablesServices variablesServices;
	
	private Logger log = Logger.getLogger(StockService.class);
	
	/**
	 * Realiza una petición a un endPoint para solicitar el token de un articulo.
	 * @param solicitarToken : Datos para la petición.
	 * @return respuestaToken : Objeto DTO que contiene la respuesta.
	 * @throws RestException
	 */
	@SuppressWarnings({"rawtypes"})
	public RespuestaTokenDTO solicitarTokenArticulo(SolicitarTokenDTO solicitarToken) throws RestException{
		/* Objeto para llenar con el resultado JSON. */
		RespuestaTokenDTO respuestaToken = new RespuestaTokenDTO();
		
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(null, null, null);
		} catch(Exception e) {
			throw new RestException(e);
		}

		Client client = javax.ws.rs.client.ClientBuilder.newBuilder()
				.sslContext(ctx).build();
		
		log.debug("solicitarTokenArticulo()/StockService - Inicio de la solicitud de token para un articulo");
		/* Creamos la URL desde donde queremos traer el token. */
		WebTarget target = client.target("").
				path(variablesServices.getVariableAsString(ByLVariablesServices.URL_SOLICITAR_TOKEN));
		log.debug("solicitarTokenArticulo()/StockService - URL de servicio rest en la que se realiza la petición: " + target.getUri());

		String auth = Base64.encodeBase64String((solicitarToken.getClientId()+":"+solicitarToken.getClientSecret()).getBytes());

		Form formData = new Form();
		formData.param("scope", solicitarToken.getScope());
		formData.param("grant_type", solicitarToken.getGrantType());
		
		/* Creamos el Gson y el Entity para poder controlar el formato de salida de la respuesta.*/
		Gson gson = new Gson();
		Entity entity = Entity.form(formData);
		try{
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, "Basic " + auth).post(entity);
			respuestaToken = gson.fromJson(response.readEntity(String.class), RespuestaTokenDTO.class);
			
			log.debug("solicitarTokenArticulo()/StockService - Fin de la solicitud de token para un articulo");
			return respuestaToken;
		}catch(ProcessingException e) {
			throw new RestTimeoutException(e);
		}catch(Exception e){
			throw new RestException("Se ha producido un error realizando la petición. Causa: "
							+ e.getMessage(), e);
		}
	}
	
	/**
	 * Realiza una petición a un endPoint para solicitar el stock de un articulo.
	 * @param codBar
	 * @return
	 * @throws RestException
	 */
	public SolicitarStockDTO[] solicitarStockArticulo(String codBar, String codTienda, String token) throws RestException{
		log.debug("solicitarTokenArticulo()/StockService - Inicio de la solicitud de stock para un articulo");
		/* Creamos la URL desde donde queremos traer el stock. */
		
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(null, null, null);
		} catch(Exception e) {
			throw new RestException(e);
		}

		Client client = javax.ws.rs.client.ClientBuilder.newBuilder()
				.sslContext(ctx).build();
		
		WebTarget target = client.target("")
				.path(variablesServices.getVariableAsString(ByLVariablesServices.URL_SOLICITAR_STOCK));
		log.debug("solicitarTokenArticulo()/StockService - URL de servicio rest en la que se realiza la petición: " + target.getUri());

		try{
			/* Añadimos el código de barras del articulo a la URL y el queryParam del ApiVersion. */
			Response response = target.resolveTemplate("codBar", codBar)
					.resolveTemplate("codTienda", codTienda)
					.queryParam("api-version", variablesServices.getVariableAsString(ByLVariablesServices.API_VERSION))
					.request(MediaType.APPLICATION_JSON_TYPE)
					.header("Ocp-Apim-Subscription-Key", variablesServices.getVariableAsString(ByLVariablesServices.SUBCRIPTION_KEY))
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.get();
			
			Gson gson = new Gson();
			SolicitarStockDTO[] respuestaStock = null;
			if(response.getStatus() == 200) {
				respuestaStock = gson.fromJson(response.readEntity(String.class), SolicitarStockDTO[].class);	
			}else{
				RespuestaErrorStockDTO respuestaError = gson.fromJson(response.readEntity(String.class), RespuestaErrorStockDTO.class);
				throw new Exception(respuestaError.getDetail());
			}
			
			log.debug("solicitarTokenArticulo()/StockService - Fin de la solicitud de stock para un articulo");
			return respuestaStock;
		}catch(ProcessingException e) {
			throw new RestTimeoutException("No se puede conectar con el servidor.", e);
		}catch(Exception e){
			throw new RestException("Se ha producido un error realizando la petición. Causa:  - "
							+ e.getLocalizedMessage(), e);
		}
	}
		
}
