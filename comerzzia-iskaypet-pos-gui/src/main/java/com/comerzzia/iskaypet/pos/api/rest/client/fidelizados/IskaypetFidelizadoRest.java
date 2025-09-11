package com.comerzzia.iskaypet.pos.api.rest.client.fidelizados;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.comerzzia.api.model.loyalty.MovimientoBean;
import com.comerzzia.api.rest.client.fidelizados.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyInvocation.Builder;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.rest.ClientBuilder;
import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.api.rest.path.BackofficeWebservicesPath;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.response.IskaypetResponseGetFidelizadoRest;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.response.IskaypetResponseGetTarjetaRegaloRest;
import com.comerzzia.iskaypet.pos.api.rest.path.IskaypetBackofficeWebservicesPath;

public class IskaypetFidelizadoRest extends FidelizadosRest{

	private static final Logger log = Logger.getLogger(IskaypetFidelizadoRest.class);

	public IskaypetFidelizadoRest(){}

	public static IskaypetResponseGetFidelizadoRest getFidelizado(ConsultarFidelizadoRequestRest consultarFidelizado) throws RestException, RestHttpException{
		log.info("getFidelizado() - Realizando petición de procesamiento de fidelizado");
		GenericType<JAXBElement<IskaypetResponseGetFidelizadoRest>> genericType = new GenericType<JAXBElement<IskaypetResponseGetFidelizadoRest>>(){};
		try{
			WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio)
			        .path(IskaypetBackofficeWebservicesPath.servicioFidelizadoFidelizados);
			target = target.queryParam("numeroTarjeta", consultarFidelizado.getNumeroTarjeta()).queryParam("apiKey", consultarFidelizado.getApiKey())
			        .queryParam("uidActividad", consultarFidelizado.getUidActividad());
			log.info("getFidelizado() - URL de servicio rest en la que se realiza la petición: " + target.getUri());

			return (target.request()
					.property(ClientProperties.READ_TIMEOUT, 120000)
					.get(genericType))
			        .getValue();
		}
		catch(BadRequestException badRequestException){
			throw RestHttpException.establecerException(badRequestException);
		}
		catch(WebApplicationException webApplicationException){
			throw new RestHttpException(webApplicationException.getResponse().getStatus(),
			        "Se ha producido un error HTTP " + webApplicationException.getResponse().getStatus()
							+ ". Causa: " + webApplicationException.getClass().getName() + " - "
							+ webApplicationException.getLocalizedMessage(), webApplicationException);
		}
		catch(ProcessingException processingException){
			throwServerException(processingException);
		}
		catch(Exception exception){
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + exception.getCause().getClass().getName() + " - " + exception.getLocalizedMessage(), exception);
		}
        return null;
    }

	public static List<FidelizadoBean> getFidelizadosDatos(ConsultarFidelizadoRequestRest consultarFidelizado) throws RestException, RestHttpException{
		log.info("getFidelizadosDatos() - Realizando petición de procesamiento de fidelizados");
		GenericType<List<FidelizadoBean>> genericType = new GenericType<List<FidelizadoBean>>(){};
		try{
			WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path(IskaypetBackofficeWebservicesPath.servicioFidelizadoFidelizadosDatos);

			target = target.queryParam("apiKey", consultarFidelizado.getApiKey()).queryParam("uidActividad", consultarFidelizado.getUidActividad());
			if(consultarFidelizado.getNombre() != null && !consultarFidelizado.getNombre().isEmpty()){
				target = target.queryParam("nombre", consultarFidelizado.getNombre());
			}
			if(consultarFidelizado.getTelefono() != null && !consultarFidelizado.getTelefono().isEmpty()){
				target = target.queryParam("telefono", consultarFidelizado.getTelefono());
			}
			if(consultarFidelizado.getApellidos() != null && !consultarFidelizado.getApellidos().isEmpty()){
				target = target.queryParam("apellidos", consultarFidelizado.getApellidos());
			}
			if(consultarFidelizado.getEmail() != null && !consultarFidelizado.getEmail().isEmpty()){
				target = target.queryParam("email", consultarFidelizado.getEmail());
			}
			if(consultarFidelizado.getNumeroTarjeta() != null && !consultarFidelizado.getNumeroTarjeta().isEmpty()){
				target = target.queryParam("numeroTarjeta", consultarFidelizado.getNumeroTarjeta());
			}
			if(consultarFidelizado.getDocumento() != null && !consultarFidelizado.getDocumento().isEmpty()){
				target = target.queryParam("documento", consultarFidelizado.getDocumento());
			}
			if(consultarFidelizado.getActivo() != null){
				target = target.queryParam("activo", consultarFidelizado.getActivo());
			}

			log.info("getFidelizadosDatos() - URL de servicio rest en la que se realiza la petición: " + target.getUri());

			return target.request(MediaType.APPLICATION_XML)
			        .property(ClientProperties.READ_TIMEOUT, 120000)
			        .get(genericType);
		}
		catch(BadRequestException e){
			throw RestHttpException.establecerException(e);
		}
		catch(WebApplicationException e){
			throw new RestHttpException(e.getResponse().getStatus(),
			        "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		catch(ProcessingException e){
			throwServerException(e);
		}
		catch(Exception e){
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
        return java.util.Collections.emptyList();
    }


	public static IskaypetResponseGetTarjetaRegaloRest getIskaypetTarjetaRegalo(ConsultarFidelizadoRequestRest consultarTarjetaRegalo) throws RestException, RestHttpException {
		GenericType<JAXBElement<IskaypetResponseGetTarjetaRegaloRest>> genericType = new GenericType<JAXBElement<IskaypetResponseGetTarjetaRegaloRest>>() {};

		try {
			WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path(IskaypetBackofficeWebservicesPath.servicioFidelizadoTarjetaRegalo);
			target = target.queryParam("numeroTarjeta", consultarTarjetaRegalo.getNumeroTarjeta()).queryParam("apiKey", consultarTarjetaRegalo.getApiKey()).queryParam("uidActividad", consultarTarjetaRegalo.getUidActividad());
			log.info("getTarjetaRegalo() - URL de servicio rest en la que se realiza la petición: " + target.getUri());
            return (target.request().get(genericType)).getValue();
		} catch (BadRequestException badRequestException) {
			throw RestHttpException.establecerException(badRequestException);
		} catch (WebApplicationException webApplicationException ) {
			throw new RestHttpException(webApplicationException.getResponse()
					.getStatus(), "Se ha producido un error HTTP " + webApplicationException.getResponse()
					.getStatus() + ". Causa: " + webApplicationException.getClass().getName() + " - "
					+ webApplicationException.getLocalizedMessage(), webApplicationException);
		} catch (ProcessingException processingException) {
			throwServerException(processingException);
		} catch (Exception exception) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + exception.getCause().getClass().getName() + " - " + exception.getLocalizedMessage(), exception);
		}
        return null;
    }
	
	public static FidelizadoBean getFidelizadoPorId(FidelizadoRequestRest fidelizadoRequestRest) throws RestException, RestHttpException {
    	log.info("getFidelizadoPorId() - Realizando petición de consulta de fidelizados");
    	GenericType<ResponseGetFidelizado> genericType = new GenericType<ResponseGetFidelizado>(){};
        
        try{
            WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path(IskaypetBackofficeWebservicesPath.servicioFidelizados);

            target = target.resolveTemplate("id_fidelizado", fidelizadoRequestRest.getIdFidelizado())
            				.queryParam("apiKey", fidelizadoRequestRest.getApiKey())
            				.queryParam("uidActividad", fidelizadoRequestRest.getUidActividad())
            				.queryParam("numeroTarjeta", fidelizadoRequestRest.getNumeroTarjeta());
           		 
            log.info("getFidelizadoPorId() - URL de servicio rest en la que se realiza la petición: "+target.getUri());
            Builder builder = (Builder) target.request(MediaType.APPLICATION_XML);
            if(StringUtils.isNotBlank(fidelizadoRequestRest.getLanguageCode())){
            	builder = builder.acceptLanguage(fidelizadoRequestRest.getLanguageCode().toUpperCase());
            }
            return builder.get(genericType).getFidelizado();
        }
        catch(BadRequestException e){
        	throw RestHttpException.establecerException(e);
        }
        catch(WebApplicationException e){
    		throw new RestHttpException(e.getResponse().getStatus(), "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }
        catch(ProcessingException e){
        	if(e.getCause() != null){
	        	throwServerException(e);
        	}else{
        		throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        	}
        }
        catch(Exception e){
    		throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }
        return null;
    }

    public static FidelizadoBean updateFidelizado(FidelizadoRequestRest fidelizadoRequestRest) throws RestException, RestHttpException {
    	GenericType<FidelizadoBean> genericType = new GenericType<FidelizadoBean>(){};
    	try {
    		 WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path(IskaypetBackofficeWebservicesPath.servicioFidelizados);
    		 
    		 target = target.resolveTemplate("id_fidelizado", fidelizadoRequestRest.getFidelizado().getIdFidelizado())
    		 .queryParam("apiKey", fidelizadoRequestRest.getApiKey())
    		 .queryParam("uidActividad", fidelizadoRequestRest.getUidActividad());
    		 
             log.info("updateFidelizado() - URL de servicio rest en la que se realiza la petición: "+target.getUri());
             
             JAXBElement<FidelizadoRequestRest> jaxbElement = new JAXBElement<>(new QName("FidelizadoRequestRest"), FidelizadoRequestRest.class, fidelizadoRequestRest);
            return target.request().header("Accept", MediaType.APPLICATION_XML).put(Entity.entity(jaxbElement, MediaType.APPLICATION_XML), genericType);
    	}
    	catch(BadRequestException e){
        	throw RestHttpException.establecerException(e);
        }
    	catch(ProcessingException e){
			throwServerException(e);
        }
        catch(Exception e){
        	throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }
        return null;
    }

	public static List<MovimientoBean> getMovimientosTarjeta(ConsultarFidelizadoRequestRest consultarFidelizado) throws RestException, RestHttpException {
		log.info("getMovimientosTarjeta() - Realizando petición de procesamiento de fidelizados");
		GenericType<ResponseGetFidelizadoMovimientosTarjeta> genericType = new GenericType<ResponseGetFidelizadoMovimientosTarjeta>() {
		};

		try {
			WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path(IskaypetBackofficeWebservicesPath.servicioFidelizadoTarjetaMovimientos);
			target = target.resolveTemplate("id_fidelizado",
					consultarFidelizado.getIdFidelizado())
					.resolveTemplate("id_tarjeta", consultarFidelizado.getIdTarjeta())
					.queryParam("apiKey", consultarFidelizado.getApiKey())
					.queryParam("uidActividad", consultarFidelizado.getUidActividad())
					.queryParam("ultimosMovimientos", consultarFidelizado.getUltimosMovimientos());
			log.info("getMovimientosTarjeta() - URL de servicio rest en la que se realiza la petición: " + target.getUri());
            return target.request(new String[]{"application/xml"}).get(genericType).getMovimientos();
		} catch (BadRequestException e) {
			throw RestHttpException.establecerException(e);
		} catch (WebApplicationException e) {
			throw new RestHttpException(e.getResponse().getStatus(), "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		} catch (ProcessingException e) {
			return throwServerException(e);
		} catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}

	private static List<MovimientoBean> throwServerException(ProcessingException e) throws RestException {
		if (e.getCause() instanceof ConnectException) {
			throw new RestConnectException("Se ha producido un error al conectar con el servidor - " + e.getLocalizedMessage(), e);
		} else if (e.getCause() instanceof SocketTimeoutException) {
			throw new RestTimeoutException("Se ha producido timeout al conectar con el servidor - " + e.getLocalizedMessage(), e);
		} else {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}

}
