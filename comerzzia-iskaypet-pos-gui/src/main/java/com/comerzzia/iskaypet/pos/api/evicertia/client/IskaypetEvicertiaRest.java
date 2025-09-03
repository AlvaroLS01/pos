package com.comerzzia.iskaypet.pos.api.evicertia.client;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit.EvinSingSubmitDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IskaypetEvicertiaRest {

	private static Logger log = Logger.getLogger(IskaypetEvicertiaRest.class);
	
	public IskaypetEvicertiaRest() {
	}
	
	public static String enviarContrato(EvinSingSubmitDTO datosContrato, String valorCredenciales,String urlEvicertia, int timeoutEvicertia) {
		Client client = null;
		String uniqueId = null;
		try { 
			log.debug("enviarContrato() - Realizando llamada al servicio: " + urlEvicertia);
			
			// Nombre de usuario y contraseña para la autenticación básica
			// Imagino que en una variable para que pueda modificarse
			
			String username = "";
			String password ="";
			if(StringUtils.isNotBlank(valorCredenciales)) {
				String [] partesCredenciales = valorCredenciales.split(";");
				 username = partesCredenciales[0];
				 password = partesCredenciales[1];
			}

			client = ClientBuilder.newClient();
			client.property("jersey.config.client.connectTimeout", timeoutEvicertia);
			client.property("jersey.config.client.readTimeout", timeoutEvicertia);

			String authHeader = "Basic "
					+ java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(datosContrato);

			// Realizar la llamada a la API
			WebTarget webTarget = client.target(urlEvicertia);
			Response response = webTarget.request(MediaType.APPLICATION_JSON)
					.header("Authorization", authHeader).post(Entity.json(json));

			if (response.getStatus() == 200) { 
				String[] resp = response.readEntity(String.class).split("\"");
				uniqueId = resp[3];
			} else {
				log.error("enviarContrato() - Respuesta: " + response.readEntity(String.class));
				log.error("enviarContrato() - Código de estado: " + response.getStatus());
			}
		}catch (JsonProcessingException e) {
			log.error("enviarContrato() - JsonProcessingException: " + e.getMessage(), e);
		}catch (ProcessingException e) {
			log.error("enviarContrato() - Error de conexión o timeout: " + e.getMessage(), e);
		}catch (Exception e) {
			log.error("enviarContrato() - Error inesperado: " + e.getMessage(), e);
		}
		finally {
			if (client != null) {
				client.close();
			}
		}
		return uniqueId;

	}
}
