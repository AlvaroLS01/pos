package com.comerzzia.bimbaylola.pos.services.ticket.pagos.tarjetaregalo;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.axis.session.Session;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.articulos.stock.RespuestaTokenDTO;
import com.comerzzia.bimbaylola.pos.persistence.articulos.stock.SolicitarTokenDTO;
import com.comerzzia.bimbaylola.pos.persistence.giftcard.ErrorGiftCardDTO;
import com.comerzzia.bimbaylola.pos.persistence.giftcard.GiftCardDTO;
import com.comerzzia.bimbaylola.pos.persistence.giftcard.GiftCardMovimientoDTO;
import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestTimeoutException;
import com.google.gson.Gson;

@Service
public class GiftCardService{

	@Autowired
	private VariablesServices variablesServices;
	private static Logger log = Logger.getLogger(GiftCardService.class);
	
	@Autowired
	private Sesion sesion;
	
	/**
	 * Realiza una petición a un endPoint para solicitar el token de una giftCard.
	 * @param solicitarToken : Datos para la petición.
	 * @return respuestaToken : Objeto DTO que contiene la respuesta.
	 * @throws RestException
	 */
	@SuppressWarnings({"rawtypes"})
	public RespuestaTokenDTO solicitarTokenGiftCard(SolicitarTokenDTO solicitarToken) throws RestException, RestTimeoutException{
		log.debug("solicitarTokenGiftCard()/GiftCardService - Inicio de la solicitud de token para una tarjeta de regalo");
		
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(null, null, null);
		} catch(Exception e) {
			throw new RestException(e);
		}

		Client client = javax.ws.rs.client.ClientBuilder.newBuilder()
				.sslContext(ctx).build();
		
		/* Creamos la URL desde donde queremos traer el token. */
		WebTarget target = client.
				target("").path(variablesServices.getVariableAsString(ByLVariablesServices.GIFTCARD_URL_SOLICITAR_TOKEN));
		log.debug("solicitarTokenGiftCard()/GiftCardService - URL de servicio rest en la que se realiza la petición: " + target.getUri());

		String auth = Base64.encodeBase64String((solicitarToken.getClientId()+":"+solicitarToken.getClientSecret()).getBytes());

		Form formData = new Form();
		formData.param("scope", solicitarToken.getScopeCards());
		formData.param("grant_type", solicitarToken.getGrantType());
		
		/* Creamos el Gson y el Entity para poder controlar el formato de salida de la respuesta.*/
		Gson gson = new Gson();
		Entity entity = Entity.form(formData);
		try{
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, "Basic " + auth).post(entity);
			
			log.debug("solicitarTokenGiftCard()/GiftCardService - Fin de la solicitud de token para una tarjeta de regalo");
			String respuesta = response.readEntity(String.class);
			log.debug("solicitarTokenGiftCard() - Respuesta del servicio de tokens: " + respuesta);
			if(response.getStatus() != 200) {
				throw new ProcessingException("Se ha producido un error HTTP " + response.getStatus());
			}
			return gson.fromJson(respuesta, RespuestaTokenDTO.class);
		}catch(ProcessingException e) {
			throw new RestTimeoutException(e);
		}catch(Exception e){
			throw new RestException("Se ha producido un error realizando la petición. Causa: "
							+ e.getCause().getClass().getName() + " - "
							+ e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * Realiza una petición a un endPoint para solicitar el stock de un articulo.
	 * @param codBar
	 * @return
	 * @throws RestException
	 */
	public GiftCardDTO solicitarEstadoGiftCard(String numerotarjeta, String token) throws RestException, RestTimeoutException{
		log.debug("solicitarEstadoGiftCard()/GiftCardService - Inicio de la solicitud de estado para la tarjeta de regalo : " + numerotarjeta);
		
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(null, null, null);
		} catch(Exception e) {
			throw new RestException(e);
		}

		Client client = javax.ws.rs.client.ClientBuilder.newBuilder()
				.sslContext(ctx).build();
		
		/* Creamos la URL desde donde queremos traer el stock. */
		WebTarget target = client.target("")
				.path(variablesServices.getVariableAsString(ByLVariablesServices.URL_SOLICITAR_ESTADO));
		log.debug("solicitarEstadoGiftCard()/GiftCardService - URL de servicio rest en la que se realiza la petición: " + target.getUri());
		
		Gson gson = new Gson();
		try{
			/* Añadimos el código de barras del articulo a la URL y el queryParam del ApiVersion. */
			Response response = target.resolveTemplate("numerotarjeta", numerotarjeta)
					.queryParam("codeShop", "O22")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.header("Ocp-Apim-Subscription-Key", variablesServices.getVariableAsString(ByLVariablesServices.GIFTCARD_SUBCRIPTION_KEY))
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.get();
			log.debug("solicitarEstadoGiftCard()/GiftCardService - Fin de la solicitud de estado para la tarjeta de regalo : " + numerotarjeta);
			if(response.getStatus() != 200 && response.getStatus() != 404) {
				throw new ProcessingException("Se ha producido un error HTTP " + response.getStatus());
			}else if(response.getStatus() == 404) {
				throw new Exception("No se ha encontrado la tarjeta en el sistema.");
			}
			String respuesta = response.readEntity(String.class);
			log.debug("solicitarEstadoGiftCard() - Respuesta: " + respuesta);
			return gson.fromJson(respuesta, GiftCardDTO.class);
		}catch(ProcessingException e) {
			throw new RestTimeoutException(e);
		}catch(Exception e){
			throw new RestException("Se ha producido un error realizando la petición. Causa: "
							+ e.getMessage(), e);
		}
	}
	
	/**
	 * Realiza una petición a un endPoint para insertar un movimiento de tarjeta
	 * @param movimientoTarjeta : Saldo gastado en el movimiento.
	 * @throws RestException
	 */
	public void insertarMovimientoGiftCard(GiftCardMovimientoDTO tarjeta, String token, String cardNumber) throws RestException, RestTimeoutException{
		log.debug("insertarMovimientoGiftCard()/GiftCardService - Inicio de la solicitud para insertar un movimiento en la tarjeta : " + cardNumber);

		// En el caso de que la tarjeta esté inactiva, volvemos a comprobar su estado por si se ha activado previamente
		// en el mismo proceso de pago
		GiftCardDTO giftcardEstado;
		if (tarjeta.getTypeCode().equals("0")) {
			giftcardEstado = solicitarEstadoGiftCard(cardNumber, token);
			tarjeta.setTypeCode(String.valueOf(giftcardEstado.getStateCode()));
		}
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(null, null, null);
		} catch(Exception e) {
			throw new RestException(e);
		}

		Client client = javax.ws.rs.client.ClientBuilder.newBuilder()
				.sslContext(ctx).build();
		
		/* Creamos la URL desde donde queremos traer el token. */
		WebTarget target = client
				.target("").path(variablesServices.getVariableAsString(ByLVariablesServices.URL_INSERTAR_MOVIMIENTO));
		log.debug("insertarMovimientoGiftCard()/GiftCardService - URL de servicio rest en la que se realiza la petición: " + target.getUri());

		Gson gson = new Gson();
		try{
			Response response = target.resolveTemplate("cardNumber", cardNumber)
				.queryParam("api-version", variablesServices.getVariableAsString(ByLVariablesServices.GIFTCARD_API_VERSION))
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("Ocp-Apim-Subscription-Key", variablesServices.getVariableAsString(ByLVariablesServices.GIFTCARD_SUBCRIPTION_KEY))
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.post(Entity.entity(generateJSON(tarjeta), MediaType.APPLICATION_JSON_TYPE));
			
			if(response.getStatus() != 200) {
				String detalleError = null;
				try {
					detalleError = gson.fromJson(response.readEntity(String.class), ErrorGiftCardDTO.class).getDetail();
				}catch(Exception e) {
					throw new RestTimeoutException(e);
				}
				throw new Exception(detalleError);
			}
			log.debug("insertarMovimientoGiftCard()/GiftCardService - Fin de la solicitud para insertar un movimiento en la tarjeta : " + cardNumber);
		}catch(ProcessingException e) {
			throw new RestTimeoutException(e);
		}catch(Exception e){
			throw new RestException("Se ha producido un error confirmando el movimiento de tarjeta regalo/abono. Causa: "
							+ e.getMessage(), e);
		}
	}
	
	/**
	 * Realiza una petición a un endPoint para insertar un movimiento de tarjeta
	 * @param movimientoTarjeta : Saldo gastado en el movimiento.
	 * @throws RestException
	 */
	public void insertarMovimientoCancelacionGiftCard(GiftCardMovimientoDTO tarjeta, String token, String cardNumber) throws RestException, RestTimeoutException{
		log.debug("insertarMovimientoCancelacionGiftCard()/GiftCardService - Inicio de la solicitud para cancelar la activación/compra de la tarjeta de regalo: " + cardNumber);
		
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(null, null, null);
		}
		catch (Exception e) {
			throw new RestException(e);
		}

		Client client = javax.ws.rs.client.ClientBuilder.newBuilder().sslContext(ctx).build();

		/* Creamos la URL desde donde queremos traer el token. */
		WebTarget target = client.target("").path(variablesServices.getVariableAsString(ByLVariablesServices.URL_INSERTAR_MOVIMIENTO));
		log.debug("insertarMovimientoCancelacionGiftCard()/GiftCardService - URL de servicio rest en la que se realiza la petición: " + target.getUri());

		Gson gson = new Gson();
		try {
			Response response = target.resolveTemplate("cardNumber", cardNumber)
				.queryParam("api-version", variablesServices.getVariableAsString(ByLVariablesServices.GIFTCARD_API_VERSION))
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("Ocp-Apim-Subscription-Key", variablesServices.getVariableAsString(ByLVariablesServices.GIFTCARD_SUBCRIPTION_KEY))
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.post(Entity.entity(generateJSON(tarjeta), MediaType.APPLICATION_JSON_TYPE));
			
			if (response.getStatus() != 200) {
				String detalleError = null;
				try {
					detalleError = gson.fromJson(response.readEntity(String.class), ErrorGiftCardDTO.class).getDetail();
				}
				catch (Exception e) {
					throw new RestTimeoutException(e);
				}
				throw new Exception(detalleError);
			}
			log.debug("insertarMovimientoCancelacionGiftCard()/GiftCardService - Fin de la solicitud para cancelar la activación/compra de la tarjeta de regalo : " + cardNumber);
		}
		catch (ProcessingException e) {
			throw new RestTimeoutException(e);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error confirmando el movimiento de tarjeta regalo/abono. Causa: " + e.getMessage(), e);
		}
	}
	
	
	/**
	 * En el caso de no ser una devolución necesita llevar el "sourceDocumentNumber"
	 * a null, y sino necesita enviar un texto, el null no puede ser encapsulado entre comillas.
	 * @param tarjeta : Datos de la tarjeta de la petición.
	 * @return payload : JSON ya relleno con los datos.
	 */
	public String generateJSON(GiftCardMovimientoDTO tarjeta){
		String payload = null;
		if(tarjeta.getSourceDocumentNumber() == null){
			payload = "{" +
	                "\"typeCode\": " + tarjeta.getTypeCode() + ", " +
	                "\"amount\": "+ tarjeta.getAmount() + ", " +
	                "\"currencyCode\": \""+ tarjeta.getCurrencyCode() +"\", " +
	                "\"documentNumber\": \""+ tarjeta.getDocumentNumber() +"\", " +
	                "\"shopCode\": \""+ tarjeta.getShopCode() +"\", " +
	                "\"userCode\": \""+ tarjeta.getUserCode() +"\", " +
	                "\"sourceDocumentNumber\": " + tarjeta.getSourceDocumentNumber() + ", " +
			        "\"isCompensation\": " + tarjeta.getIsCompensation() +", " +
			        "\"codeShop\": \"" + sesion.getAplicacion().getTienda().getCodAlmacen() +"\""+
	                "}";
		}else{
			payload = "{" +
	                "\"typeCode\": " + tarjeta.getTypeCode() + ", " +
	                "\"amount\": "+ tarjeta.getAmount() + ", " +
	                "\"currencyCode\": \""+ tarjeta.getCurrencyCode() +"\", " +
	                "\"documentNumber\": \""+ tarjeta.getDocumentNumber() +"\", " +
	                "\"shopCode\": \""+ tarjeta.getShopCode() +"\", " +
	                "\"userCode\": \""+ tarjeta.getUserCode() +"\", " +
	                "\"sourceDocumentNumber\": \""+ tarjeta.getSourceDocumentNumber() +"\", " +
			        "\"isCompensation\": " + tarjeta.getIsCompensation() +", " +
			        "\"codeShop\": \"" + sesion.getAplicacion().getTienda().getCodAlmacen() +"\""+
	                "}";
		}
		log.debug("generateJSON() - " + payload);
		return payload;
	}
	
}
