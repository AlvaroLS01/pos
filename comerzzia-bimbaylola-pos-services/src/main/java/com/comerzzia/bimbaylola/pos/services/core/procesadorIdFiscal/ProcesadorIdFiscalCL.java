package com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLConfigContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRango;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.bimbaylola.pos.persistence.core.contadores.ByLContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.idfiscal.IdFiscalDTO;
import com.comerzzia.bimbaylola.pos.persistence.idfiscal.RespuestaTokenDTOIdFiscal;
import com.comerzzia.bimbaylola.pos.persistence.idfiscal.SolicitudTokenDTOIdFiscal;
import com.comerzzia.bimbaylola.pos.services.core.ByLServicioContadores;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.ByLServicioConfigContadoresImpl;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos.ServicioConfigContadoresRangos;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.client.exceptions.RestConnectException;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;
import com.comerzzia.rest.client.exceptions.RestTimeoutException;
import com.google.gson.Gson;

@SuppressWarnings("rawtypes")
@Component
public class ProcesadorIdFiscalCL implements IProcesadorFiscal {

	protected static final Logger log = Logger.getLogger(ProcesadorIdFiscalCL.class);

	public static final String REST_URL_IDFISCAL_CHILE = "CL.REST_URL_IDFISCAL";
	public static final String ID_FISCAL_CL = "ID_FISCAL_CL";

	public static final String URL_TOKEN = "STOCK.URL_TOKEN";
	public static final String subcripcion = "STOCK.SUBCRIPTION_KEY";
	public static final String GRANT_TYPE = "NAVISION.GRANT_TYPE";
	public static final String CLIENT_ID = "NAVISION.CLIENT_ID";
	public static final String CLIENT_SECRET = "NAVISION.CLIENT_SECRET";
	public static final String SCOPE_TICKETS = "NAVISION.SCOPE_TICKETS";

	@Autowired
	protected Documentos documentos;

	@Autowired
	protected VariablesServices variablesService;

	@Autowired
	private ByLServicioContadores byLServicioContadores;

	@Override
	public String obtenerIdFiscal(Ticket ticket) throws ProcesadorIdFiscalException {
		log.debug("obtenerIdFiscal() - Obteniendo número de identificación fiscal para CHILE ...");
		String identificadorFiscal = null;
		try {

			String urlRestIdFiscal = variablesService.getVariableAsString(REST_URL_IDFISCAL_CHILE);
			String variableSubcripcion = variablesService.getVariableAsString(subcripcion);
			String variableGrantType = variablesService.getVariableAsString(GRANT_TYPE);
			String variableClientID = variablesService.getVariableAsString(CLIENT_ID);
			String variableClientSecret = variablesService.getVariableAsString(CLIENT_SECRET);
			String variableScopeTickets = variablesService.getVariableAsString(SCOPE_TICKETS);

			RespuestaTokenDTOIdFiscal respuestaTokenCrear = null;
			try {
				/* Solicitamos el Token necesario para las peticiones a Personas */
				SolicitudTokenDTOIdFiscal solicitudCrear = new SolicitudTokenDTOIdFiscal(variableGrantType, variableClientID, variableClientSecret, variableScopeTickets);
				respuestaTokenCrear = solicitarToken(solicitudCrear);
			}
			catch (KeyManagementException | NoSuchAlgorithmException | RestException e) {
				String mensajeError = "Se ha producido un error al solicitar el Token para las operaciones con identificadores fiscales";
				log.error("ProcesadorIdFiscalCL/obtenerIdFiscal() - " + mensajeError + " - " + e.getMessage(), e);
				throw new Exception(mensajeError, e);
			}

			IdFiscalDTO idFiscalDTO = new IdFiscalDTO(documentos.getDocumento(ticket.getCabecera().getTipoDocumento()).getCodtipodocumento());

			urlRestIdFiscal = urlRestIdFiscal.replace("{codTienda}", ticket.getTienda().getAlmacenBean().getCodAlmacen());
			identificadorFiscal = peticionIdFiscal(idFiscalDTO, respuestaTokenCrear.getAccess_token(), urlRestIdFiscal, variableSubcripcion);

		}
		catch (Exception e) {

			Map<String, String> parametrosContador = new HashMap<>();
			Map<String, String> condicionesVigencias = new HashMap<>();

			parametrosContador.put("CODEMP", ticket.getEmpresa().getCodEmpresa());
			parametrosContador.put("CODALM", ticket.getTienda().getAlmacenBean().getCodAlmacen());
			parametrosContador.put("CODSERIE", ticket.getTienda().getAlmacenBean().getCodAlmacen());
			parametrosContador.put("CODCAJA", ticket.getCodCaja());
			parametrosContador.put("CODTIPODOCUMENTO", ticket.getCabecera().getCodTipoDocumento());
			parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

			// Se añaden vigencias para los rangos
			condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODCAJA, ticket.getCabecera().getCodCaja());
			condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODALM, ticket.getCabecera().getTienda().getCodAlmacen());
			condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODEMP, ticket.getCabecera().getEmpresa().getCodEmpresa());

			try {
				TipoDocumentoBean documentoActivo = documentos.getDocumento(ticket.getCabecera().getTipoDocumento());

				// Tratamos los rangos en caso de que los tenga
				ByLConfigContadorBean confContador = (ByLConfigContadorBean) ByLServicioConfigContadoresImpl.get().consultar(ID_FISCAL_CL + "_" + documentoActivo.getCodtipodocumento());
				if (!confContador.isRangosCargados()) {
					ConfigContadorRangoExample example = new ConfigContadorRangoExample();
					example.or().andIdContadorEqualTo(confContador.getIdContador());
					example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
					        + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
					List<ConfigContadorRango> rangos = ServicioConfigContadoresRangos.get().consultar(example);

					if(rangos == null || rangos.isEmpty()){
						throw new ProcesadorIdFiscalException("No existen rangos para el identificador fiscal.");
					}
					
					confContador.setRangos(rangos);
					confContador.setRangosCargados(true);
				}

				ByLContadorBean ticketContador;
				ticketContador = byLServicioContadores.consultarContadorActivo(confContador, parametrosContador, condicionesVigencias, ticket.getUidActividad(), true);

				if (ticketContador == null || ticketContador.getError() != null) {
					throw new ContadorNotFoundException(I18N.getTexto("No se ha encontrado contador con rangos disponible"));
				}

				identificadorFiscal = ticketContador.getValorFormateado();
			}
			catch (Exception e1) {
				String msg = "Se ha producido un error procesando el identificador fiscal del ticket con uid " + ticket.getUidTicket() + " : " + e.getMessage();
				log.error("obtenerIdFiscal() - " + msg, e1);
				throw new ProcesadorIdFiscalException(e1.getMessage());
			}

		}

		return identificadorFiscal;
	}

	public RespuestaTokenDTOIdFiscal solicitarToken(SolicitudTokenDTOIdFiscal solicitarToken) throws RestException, NoSuchAlgorithmException, KeyManagementException {
		log.debug("ProcesadorIdFiscalCL/solicitarToken() - Inicio de la petición para solicitar el Token...");
		log.debug("ProcesadorIdFiscalCL/solicitarToken() - grant_type: " + solicitarToken.getGrant_type());
		log.debug("ProcesadorIdFiscalCL/solicitarToken() - client_id: " + solicitarToken.getClient_id());
		log.debug("ProcesadorIdFiscalCL/solicitarToken() - client_secret: " + solicitarToken.getClient_secret());
		log.debug("ProcesadorIdFiscalCL/solicitarToken() - scope: " + solicitarToken.getScope());
		
		RespuestaTokenDTOIdFiscal respuestaToken = new RespuestaTokenDTOIdFiscal();
		/* Creamos la URL desde donde queremos traer el token. */
		SSLContext ctx = SSLContext.getInstance("TLSv1.2");
		ctx.init(null, null, null);
		Client client = javax.ws.rs.client.ClientBuilder.newBuilder().sslContext(ctx).build();
		WebTarget target = client.target("").path(variablesService.getVariableAsString(URL_TOKEN));

		String auth = Base64.encodeBase64String((solicitarToken.getClient_id()+":"+solicitarToken.getClient_secret()).getBytes());
		
		/* Creamos la URL desde donde queremos traer el token. */
		Form formData = new Form();
		formData.param("grant_type", solicitarToken.getGrant_type());
		formData.param("scope", solicitarToken.getScope());

		/* Creamos el Gson y el Entity para poder controlar el formato de salida de la respuesta. */
		Gson gson = new Gson();
		Entity formEntity = Entity.form(formData);
		Response response = null;

		try {
		    response = target.request(MediaType.APPLICATION_JSON_TYPE)
		            .header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
		            .post(formEntity);

		    if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
		        log.error("Error en la respuesta del servidor, codigo de estado: " + response.getStatus());
		        throw new RestException("Error en la respuesta del servidor", new Throwable("Response status: " + response.getStatus()));
		    }
		    
		    log.info("Respuesta del servidor, codigo de estado: " + response.getStatus());
		    String responseBody = response.readEntity(String.class);

		    if (StringUtils.isBlank(responseBody)) {
		    	String msgError = "La respuesta del servidor esta vacia.";
		        log.error(msgError);
		        throw new RestException(msgError, new Throwable("Empty response body"));
		    }

		    respuestaToken = gson.fromJson(responseBody, RespuestaTokenDTOIdFiscal.class);
		    log.debug("Token recibido: " + respuestaToken.getAccess_token());

		    return respuestaToken;

		} catch (Exception e) {
		    log.error("Error al solicitar el token: " + e.getMessage(), e);
		    throw new RestException("Se ha producido un error realizando la peticion de solicitar Token", e);
		} finally {
		    if (response != null) {
		        response.close();
		    }
		}

	}

	public String peticionIdFiscal(IdFiscalDTO idFiscalDTO, String token, String url, String subcription) throws RestException, KeyManagementException, NoSuchAlgorithmException, RestHttpException {
		log.debug("ProcesadorIdFiscalCL/peticionIdFiscal() - Inicio de la peticion de identificador fiscal...");
		log.debug("ProcesadorIdFiscalCL/peticionIdFiscal() - Tipo Documento: " + idFiscalDTO.getDocumentTypeCode());
		log.debug("ProcesadorIdFiscalCL/peticionIdFiscal() - Token: " + token);
		log.debug("ProcesadorIdFiscalCL/peticionIdFiscal() - URL: " + url);
		log.debug("ProcesadorIdFiscalCL/peticionIdFiscal() - Subcription: " + subcription);

		SSLContext ctx = SSLContext.getInstance("TLSv1.2");
		ctx.init(null, null, null);
		Client client = javax.ws.rs.client.ClientBuilder.newBuilder().sslContext(ctx).build();
		/* Creamos la URL donde debemos pedir el identificador fiscal. */
		WebTarget target = client.target("").path(url);
		log.debug("ProcesadorIdFiscalCL/peticionIdFiscal() - URL de servicio rest en la que se realiza la petición: " + target.getUri());

		/* Creamos el JSON con los datos del tipo de documento. */
		Gson gson = new Gson();
		String json = gson.toJson(idFiscalDTO);
		try {
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + token).header("Accept", "application/json")
			        .header("Content-Type", "application/json").header("Ocp-Apim-Subscription-Key", subcription).post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));

			String respuesta = response.readEntity(String.class);
			if (response.getStatus() != 200) {
				String mensajeError = "La respuesta es errónea: " + respuesta;
				log.error("ProcesadorIdFiscalCL/peticionIdFiscal() - " + mensajeError);
				throw new Exception(mensajeError);
			}

			log.debug("ProcesadorIdFiscalCL/peticionIdFiscal() - Finalizada la peticion de identificador fiscal");
			return gson.fromJson(respuesta, String.class);
		}
		catch (BadRequestException e) {
			throw RestHttpException.establecerException(e);
		}
		catch (WebApplicationException e) {
			String mensajeError = "Se ha producido un error HTTP al pedir un identificador fiscal";
			log.error("ProcesadorIdFiscalCL/peticionIdFiscal() - " + mensajeError + " - " + e.getMessage(), e);
			throw new RestHttpException(e.getResponse().getStatus(), mensajeError, e);
		}
		catch (ProcessingException e) {
			if (e.getCause() != null) {
				if (e.getCause() instanceof ConnectException) {
					String mensajeError = "Se ha producido Timeout al conectar con el servidor al pedir un identificador fiscal";
					log.error("ProcesadorIdFiscalCL/peticionIdFiscal() - " + mensajeError + " - " + e.getMessage(), e);
					throw new RestConnectException(mensajeError, e);
				}
				else if (e.getCause() instanceof SocketTimeoutException) {
					String mensajeError = "Se ha producido Timeout al conectar con el servidor al pedir un identificador fiscal";
					log.error("ProcesadorIdFiscalCL/peticionIdFiscal() - " + mensajeError + " - " + e.getMessage(), e);
					throw new RestTimeoutException(mensajeError, e);
				}
				String mensajeError = "Se ha producido un error realizando la petición de identificador fiscal";
				log.error("ProcesadorIdFiscalCL/peticionIdFiscal() - " + mensajeError + " - " + e.getMessage(), e);
				throw new RestException(mensajeError, e);
			}
			else {
				String mensajeError = "Se ha producido un error realizando la petición de identificador fiscal";
				log.error("ProcesadorIdFiscalCL/peticionIdFiscal() - " + mensajeError + " - " + e.getMessage(), e);
				throw new RestException(mensajeError, e);
			}
		}
		catch (Exception e) {
			String mensajeError = "Se ha producido un error realizando la petición de identificador fiscal";
			log.error("ProcesadorIdFiscalCL/peticionIdFiscal() - " + mensajeError + " - " + e.getMessage(), e);
			throw new RestException(mensajeError, e);
		}
	}

	@Override
	public byte[] procesarDocumentoFiscal(Ticket ticket) throws ProcesarDocumentoFiscalException {
		return null;
	}
}
