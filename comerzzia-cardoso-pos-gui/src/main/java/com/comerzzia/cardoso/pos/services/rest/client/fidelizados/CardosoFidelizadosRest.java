package com.comerzzia.cardoso.pos.services.rest.client.fidelizados;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.api.rest.ClientBuilder;
import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoRest;
import com.comerzzia.api.rest.path.BackofficeWebservicesPath;
import com.comerzzia.cardoso.pos.services.rest.model.loyalty.CardosoFidelizadoBean;

public class CardosoFidelizadosRest {
	
	private static Logger log = Logger.getLogger(CardosoFidelizadosRest.class);

	public static CardosoResponseGetFidelizadoRest getFidelizado(ConsultarFidelizadoRequestRest consultarFidelizado) throws RestException, RestHttpException {
    	log.info("getFidelizado() - Realizando petición de procesamiento de fidelizado");
    	GenericType<JAXBElement<CardosoResponseGetFidelizadoRest>> genericType = new GenericType<JAXBElement<CardosoResponseGetFidelizadoRest>>(){};
    	
    	try{
    		WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path(BackofficeWebservicesPath.servicioFidelizadoFidelizados);
    		
    		target = target.queryParam("numeroTarjeta", consultarFidelizado.getNumeroTarjeta()).queryParam("apiKey", consultarFidelizado.getApiKey()).queryParam("uidActividad", consultarFidelizado.getUidActividad());
    		log.info("getFidelizado() - URL de servicio rest en la que se realiza la petición: "+target.getUri());
    		CardosoResponseGetFidelizadoRest response = (CardosoResponseGetFidelizadoRest) target.request().get(genericType).getValue();
    		return response;
    	}
    	catch(BadRequestException e){
    		throw RestHttpException.establecerException(e);
    	}
    	catch(WebApplicationException e){
    		throw new RestHttpException(e.getResponse().getStatus(), "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
    	}
    	catch(ProcessingException e){
    		if(e.getCause() instanceof ConnectException){
    			throw new RestConnectException("Se ha producido un error al conectar con el servidor - " + e.getLocalizedMessage(), e);
    		}else if(e.getCause() instanceof SocketTimeoutException){
    			throw new RestTimeoutException("Se ha producido timeout al conectar con el servidor - " + e.getLocalizedMessage(), e);
    		}
    		throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
    	}
    	catch(Exception e){
    		throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
    	}
    }
	
	public static List<CardosoFidelizadoBean> getFidelizadosDatos(ConsultarFidelizadoRequestRest consultarFidelizado) throws RestException, RestHttpException {
    	log.info("getFidelizadosDatos() - Realizando petición de procesamiento de fidelizados");
    	GenericType<List<CardosoFidelizadoBean>> genericType = new GenericType<List<CardosoFidelizadoBean>>(){};
        
        try{
            WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path(BackofficeWebservicesPath.servicioFidelizadoDatosFidelizadosList);
            
            target = target.queryParam("apiKey", consultarFidelizado.getApiKey())
            				.queryParam("uidActividad", consultarFidelizado.getUidActividad());
            if(consultarFidelizado.getNombre()!=null && !consultarFidelizado.getNombre().isEmpty()){
            	target = target.queryParam("nombre", consultarFidelizado.getNombre());
            }
            if(consultarFidelizado.getTelefono()!=null && !consultarFidelizado.getTelefono().isEmpty()){
            	target = target.queryParam("telefono", consultarFidelizado.getTelefono());
            }
            if(consultarFidelizado.getApellidos()!=null && !consultarFidelizado.getApellidos().isEmpty()){
            	target = target.queryParam("apellidos", consultarFidelizado.getApellidos());
            }
            if(consultarFidelizado.getEmail()!=null && !consultarFidelizado.getEmail().isEmpty()){
            	target = target.queryParam("email", consultarFidelizado.getEmail());
            }
            if(consultarFidelizado.getNumeroTarjeta()!=null && !consultarFidelizado.getNumeroTarjeta().isEmpty()){
            	target = target.queryParam("numeroTarjeta", consultarFidelizado.getNumeroTarjeta());
            }
            if(consultarFidelizado.getDocumento()!=null && !consultarFidelizado.getDocumento().isEmpty()){
            	target = target.queryParam("documento", consultarFidelizado.getDocumento());
            }
            if(consultarFidelizado.getActivo() != null){
            	target = target.queryParam("activo", consultarFidelizado.getActivo());
            }
            if(StringUtils.isNotBlank(consultarFidelizado.getCodColectivos())){
            	target = target.queryParam("codColectivos", consultarFidelizado.getCodColectivos());
            }
            if(StringUtils.isNotBlank(consultarFidelizado.getEtiquetas())){
            	target = target.queryParam("etiquetas", consultarFidelizado.getEtiquetas());
            }
            
            log.info("getFidelizadosDatos() - URL de servicio rest en la que se realiza la petición: "+target.getUri());
            List<CardosoFidelizadoBean> response = target.request(MediaType.APPLICATION_XML).get(genericType);
            return response;
        }
        catch(BadRequestException e){
        	throw RestHttpException.establecerException(e);
        }
        catch(WebApplicationException e){
    		throw new RestHttpException(e.getResponse().getStatus(), "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }
        catch(ProcessingException e){
        	if(e.getCause() instanceof ConnectException){
        		throw new RestConnectException("Se ha producido un error al conectar con el servidor - " + e.getLocalizedMessage(), e);
        	}else if(e.getCause() instanceof SocketTimeoutException){
        		throw new RestTimeoutException("Se ha producido timeout al conectar con el servidor - " + e.getLocalizedMessage(), e);
        	}
        	throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }
        catch(Exception e){
        	throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }
    }
}
