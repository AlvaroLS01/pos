package com.comerzzia.iskaypet.pos.services.ticket.comprasfidelizados;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.JerseyInvocation.Builder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.api.rest.path.BackofficeWebservicesPath;
import com.comerzzia.instoreengine.rest.ClientBuilder;
import com.comerzzia.iskaypet.pos.persistence.ticket.comprasfidelizado.ComprasFidelizado;
import com.comerzzia.iskaypet.pos.persistence.ticket.comprasfidelizado.SolicitudComprasFidelizadoResponse;
import com.comerzzia.pos.services.clientes.ClienteNotFoundException;

@Service
@Primary
public class ComprasFidelizadosService {

	private Logger log =  Logger.getLogger(ComprasFidelizadosService.class);
	
	public List<ComprasFidelizado> getComprasFidelizado(String apiKey, String uidActividad, String numTarjFidelizado) throws RestException, RestHttpException, ClienteNotFoundException {
		log.info("getComprasFidelizado() - Realizadon busqueda de las compras del cliente en central");

		GenericType<SolicitudComprasFidelizadoResponse> genericType = new GenericType<SolicitudComprasFidelizadoResponse>(){};
    	List<ComprasFidelizado> response = null;
    	
    	try {
			WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path("iskaypet/comprasFidelizado/consulta");				

			target = target.queryParam("apiKey", apiKey).queryParam("uidActividad", uidActividad).queryParam("numTarjFidelizado", numTarjFidelizado);
    		
			log.info("getComprasFidelizado() - URL de servicio rest en la que se realiza la petición: " + target.getUri());
			
			Builder builder = (Builder) target.request(MediaType.APPLICATION_XML);

			SolicitudComprasFidelizadoResponse respuesta = builder.get(genericType);
			
			if(respuesta!=null) {
				 response = respuesta.getComprasFidelizado();
			}else {
				throw new ClienteNotFoundException();
			}
			
			return response;

			
    	}catch(BadRequestException e){
        	throw RestHttpException.establecerException(e);
		}
		catch (WebApplicationException e) {
			throw new RestHttpException(e.getResponse().getStatus(),
			        "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		catch (ProcessingException e) {
			if (e.getCause() instanceof ConnectException) {
				throw new RestConnectException("Se ha producido un error al conectar con el servidor - " + e.getLocalizedMessage(), e);
			}
			else if (e.getCause() instanceof SocketTimeoutException) {
				throw new RestTimeoutException("Se ha producido timeout al conectar con el servidor - " + e.getLocalizedMessage(), e);
			}
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}		
	}

}
