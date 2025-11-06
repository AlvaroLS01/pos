package com.comerzzia.dinosol.pos.services.ticket.aparcados;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionAplicacion;
import com.comerzzia.instoreengine.master.rest.ClientBuilder;
import com.comerzzia.rest.client.exceptions.RestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class ClienteTicketsAparcadosIse {

	private Logger log = Logger.getLogger(ClienteTicketsAparcadosIse.class);
	
	@Autowired
	private DinoSesionAplicacion sesionAplicacion;

    public List<TicketAparcadoDto> consultarTicketsAparcados(String uidActividad, String codalm, String apiKey) throws RestException {
		log.debug("consultarTicketsAparcados() - Enviando petición de consulta de tickets aparcados.");

		/* Creamos la URL donde debemos enviar el nuevo cliente. */
		WebTarget target = ClientBuilder.getClient().target(sesionAplicacion.getUrlDinoWsCajaMaster()).path("ticketsAparcados/{codAlm}");

		target = target.resolveTemplate("codAlm", codalm).queryParam("uidActividad", uidActividad).queryParam("apiKey", apiKey);

		log.debug("consultarTicketsAparcados() URL de servicio rest en la que se realiza la petición: " + target.getUri());

		/* Creamos el JSON con los datos del cliente. */
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		try {
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

			String respuesta = response.readEntity(String.class);
			log.debug("consultarTicketsAparcados() - Respuesta del servicio de consulta de tickets aparcados: " + respuesta);
			TicketAparcadoDto[] arrayTickets = gson.fromJson(respuesta, TicketAparcadoDto[].class);
			return Arrays.asList(arrayTickets);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}

	}
    
	public List<TicketAparcadoDto> consultarTicketsAparcadosPorUsuario(String uidActividad, String codalm, String apiKey, String codUsuario) throws RestException {
		log.debug("consultarTicketsAparcadosPorUsuario() - Enviando petición de consulta de tickets aparcados.");

		/* Creamos la URL donde debemos enviar el nuevo cliente. */
		WebTarget target = ClientBuilder.getClient().target(sesionAplicacion.getUrlDinoWsCajaMaster()).path("ticketsAparcados/{codAlm}/usuario/{codUsuario}");

		target = target.resolveTemplate("codAlm", codalm).resolveTemplate("codUsuario", codUsuario).queryParam("uidActividad", uidActividad).queryParam("apiKey", apiKey);

		log.debug("consultarTicketsAparcadosPorUsuario() URL de servicio rest en la que se realiza la petición: " + target.getUri());

		/* Creamos el JSON con los datos del cliente. */
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		try {
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

			String respuesta = response.readEntity(String.class);
			log.debug("consultarTicketsAparcadosPorUsuario() - Respuesta del servicio de consulta de tickets aparcados: " + respuesta);
			TicketAparcadoDto[] arrayTickets = gson.fromJson(respuesta, TicketAparcadoDto[].class);
			return Arrays.asList(arrayTickets);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}

	}
    
    public void aparcarTicket(TicketAparcadoDto ticketAparcadoDto) throws RestException, TicketsMaximoAparcadosException {
    	log.debug("aparcarTicket() - Enviando petición de ticket aparcado: " + ticketAparcadoDto);

		/* Creamos la URL donde debemos enviar el nuevo cliente. */
    	WebTarget target = ClientBuilder.getClient().target(sesionAplicacion.getUrlDinoWsCajaMaster()).path("ticketsAparcados/{codAlm}/aparcar");

		target = target.resolveTemplate("codAlm", ticketAparcadoDto.getCodAlmacen()).queryParam("uidActividad", ticketAparcadoDto.getUidActividad()).queryParam("apiKey", ticketAparcadoDto.getApiKey());


		/* Creamos el JSON con los datos del cliente. */
		Gson gson = new Gson();
		Entity<String> ticketAparcado = Entity.json(gson.toJson(ticketAparcadoDto));
		try {
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(ticketAparcado);
			
			String respuesta = response.readEntity(String.class);
			log.debug("aparcarTicket() - Respuesta del servicio de aparcar ticket: " + respuesta);
			
			ErrorDto error = gson.fromJson(respuesta, ErrorDto.class);
			if(error != null && error.getCode() == 501) {
				throw new TicketsMaximoAparcadosException();
			}
		}
		catch (TicketsMaximoAparcadosException e) {
			throw new TicketsMaximoAparcadosException();
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
    }

    public TicketAparcadoDto consultarTicketAparcado(String uidActividad, String codalm, String apiKey, String uidTicket) throws RestException {
		log.debug("consultarTicketsAparcados() - Enviando petición de consulta de ticket aparcado.");

		/* Creamos la URL donde debemos enviar el nuevo cliente. */
		WebTarget target = ClientBuilder.getClient().target(sesionAplicacion.getUrlDinoWsCajaMaster()).path("ticketsAparcados/{codAlm}/{uidTicket}");

		target = target.resolveTemplate("codAlm", codalm).resolveTemplate("uidTicket", uidTicket).queryParam("uidActividad", uidActividad).queryParam("apiKey", apiKey);

		log.debug("consultarTicketAparcado() URL de servicio rest en la que se realiza la petición: " + target.getUri());

		/* Creamos el JSON con los datos del cliente. */
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		try {
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

			String respuesta = response.readEntity(String.class);
			log.debug("consultarTicketAparcado() - Respuesta del servicio de consulta de ticket aparcado: " + respuesta);
			return gson.fromJson(respuesta, TicketAparcadoDto.class);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}

    public TicketAparcadoDto eliminarTicketAparcado(String uidActividad, String codalm, String apiKey, String uidTicket) throws RestException {
		log.debug("eliminarTicketAparcado() - Enviando petición de consulta de tickets aparcados.");

		/* Creamos la URL donde debemos enviar el nuevo cliente. */
		WebTarget target = ClientBuilder.getClient().target(sesionAplicacion.getUrlDinoWsCajaMaster()).path("ticketsAparcados/{codAlm}/{uidTicket}/recuperar");

		target = target.resolveTemplate("codAlm", codalm).resolveTemplate("uidTicket", uidTicket).queryParam("uidActividad", uidActividad).queryParam("apiKey", apiKey);

		log.debug("eliminarTicketAparcado() URL de servicio rest en la que se realiza la petición: " + target.getUri());

		/* Creamos el JSON con los datos del cliente. */
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		try {
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

			String respuesta = response.readEntity(String.class);
			log.debug("eliminarTicketAparcado() - Respuesta del servicio de eliminación de ticket aparcado: " + respuesta);
			return gson.fromJson(respuesta, TicketAparcadoDto.class);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}

}
