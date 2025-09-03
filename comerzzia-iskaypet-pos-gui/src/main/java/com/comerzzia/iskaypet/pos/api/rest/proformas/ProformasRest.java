package com.comerzzia.iskaypet.pos.api.rest.proformas;


import com.comerzzia.api.rest.ClientBuilder;
import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.iskaypet.pos.api.rest.proformas.request.ProformaRequestRest;
import com.comerzzia.iskaypet.pos.api.rest.proformas.responses.ProformaListRestResponse;
import com.comerzzia.iskaypet.pos.api.rest.proformas.responses.ProformaRestResponse;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaHeaderDTO;
import com.comerzzia.iskaypet.pos.util.date.DateUtils;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ProformasRest {

    private static final Logger log = Logger.getLogger(ProformasRest.class);

    public static final String PROFORMAS_API_ENDPOINT = "/api/proformas";
    public static final String PROFORMAS_API_ENDPOINT_FACTURAR = "/facturar";
    private static final Client client = ClientBuilder.getClient().register(JacksonJsonProvider.class);

    public static final String X_ACTIVITY_ID = "X-CZZ-ACTIVITY-ID";
    public static final String X_API_KEY = "X-CZZ-API-KEY";
    public static final String ESTADO_PROFORMA = "ENVIADA";

    public ProformasRest() {
    }

    public static List<ProformaHeaderDTO> getProformas(String url, ProformaRequestRest request)
            throws RestException, RestHttpException {
        log.debug("getProformas() - Realizando petición de busqueda de proformas");

        try {

            GenericType<ProformaListRestResponse> genericType = new GenericType<ProformaListRestResponse>() {
            };

            WebTarget target = client.target(url).path(PROFORMAS_API_ENDPOINT)
                    .queryParam("estado", ESTADO_PROFORMA)
                    .queryParam("automatica", Boolean.FALSE.toString())
                    .queryParam("fechaDesde", DateUtils.formatDate(DateUtils.getCurrentDateWithZeroTime(), "yyyy-MM-dd HH:mm:ss"))
                    .queryParam("fechaHasta", DateUtils.formatDate(DateUtils.getCurrentDateWithLastTime(), "yyyy-MM-dd HH:mm:ss"));

            if (StringUtils.isNotBlank(request.getAlmacen())) {
                target = target.queryParam("almacen", request.getAlmacen());
            }

            for (String tipoDoc : request.getTiposDocumentos()) {
                target = target.queryParam("tipoDocumento", tipoDoc);
            }

            log.debug("getProformas() - URL de servicio REST: " + target.getUri());


            ProformaListRestResponse responseDTO = target.request(MediaType.APPLICATION_JSON)
                    .header(X_ACTIVITY_ID, request.getUidActividad())
                    .header(X_API_KEY, request.getApiKey())
                    .property(ClientProperties.READ_TIMEOUT, 120000)
                    .get(genericType);

            return responseDTO.getProformas();
        } catch (BadRequestException e) {
            String errorMsg = extractErrorMessage(e);
            log.error("BadRequestException al llamar el endpoint: " + errorMsg);
            throw RestHttpException.establecerException(e);
        } catch (WebApplicationException e) {
            String errorMsg = extractErrorMessage(e);
            log.error("WebApplicationException: HTTP - " + e.getResponse().getStatus() + " Mensaje de error: " + errorMsg);
            throw new RestHttpException(e.getResponse().getStatus(),
                    "Error HTTP " + e.getResponse().getStatus() + " - " + errorMsg, e);
        } catch (ProcessingException e) {
            return throwServerException(e);
        } catch (Exception e) {
            log.error("Exception en getProformas(): " + e.getLocalizedMessage(), e);
            throw new RestException("Error realizando la petición: " + e.getLocalizedMessage(), e);
        }
    }


    private static String extractErrorMessage(WebApplicationException e) {
        try {
            if (e.getResponse() != null && e.getResponse().hasEntity()) {
                return e.getResponse().readEntity(String.class);
            }
        } catch (Exception ex) {
            log.warn("Error al leer el mensaje de error: " + ex.getMessage());
        }
        return e.getLocalizedMessage();
    }

    private static List<ProformaHeaderDTO> throwServerException(ProcessingException e) throws RestException {
        if (e.getCause() instanceof ConnectException) {
            throw new RestConnectException("Error al conectar: " + e.getLocalizedMessage(), e);
        } else if (e.getCause() instanceof SocketTimeoutException) {
            throw new RestTimeoutException("Timeout: " + e.getLocalizedMessage(), e);
        } else {
            throw new RestException("Error realizando la petición: " + e.getLocalizedMessage(), e);
        }
    }

    public static ProformaDTO getProformaCompleta(String url, ProformaRequestRest request)
            throws RestException, RestHttpException {
        log.debug("getProformaCompleta() - Realizando petición de proforma completa, uidActividad: "
                + request.getUidActividad() + ", idProforma: " + request.getIdProforma());
        try {
            Client client = ClientBuilder.getClient().register(JacksonJsonProvider.class);

            WebTarget target = client.target(url).path(PROFORMAS_API_ENDPOINT).path(request.getIdProforma());
            log.debug("getProformaCompleta() - URL de servicio REST: " + target.getUri());

            ProformaRestResponse proforma = target.request(MediaType.APPLICATION_JSON)
                    .header(X_ACTIVITY_ID, request.getUidActividad())
                    .header(X_API_KEY, request.getApiKey())
                    .property(ClientProperties.READ_TIMEOUT, 120000)
                    .get(ProformaRestResponse.class);

            log.debug("getProformaCompleta() - Proforma obtenida: " + proforma);
            return proforma.getProforma();
        } catch (BadRequestException e) {
            throw RestHttpException.establecerException(e);
        } catch (WebApplicationException e) {
            throw new RestHttpException(e.getResponse().getStatus(),
                    "Error HTTP " + e.getResponse().getStatus() + " - " + e.getLocalizedMessage(), e);
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                throw new RestConnectException("Error al conectar: " + e.getLocalizedMessage(), e);
            } else if (e.getCause() instanceof SocketTimeoutException) {
                throw new RestTimeoutException("Timeout: " + e.getLocalizedMessage(), e);
            } else {
                throw new RestException("Error realizando la petición: " + e.getLocalizedMessage(), e);
            }
        } catch (Exception e) {
            throw new RestException("Error realizando la petición: " + e.getLocalizedMessage(), e);
        }
    }

    public static void facturarProforma(String url, ProformaRequestRest request)
            throws RestException, RestHttpException {
        log.debug("facturarProforma() - Realizando petición de facturación de proforma, uidActividad: "
                + request.getUidActividad() + ", idProforma: " + request.getIdProforma());
        try {
            Client client = ClientBuilder.getClient().register(JacksonJsonProvider.class);

            WebTarget target = client.target(url).path(PROFORMAS_API_ENDPOINT).path(request.getIdProforma()).path(PROFORMAS_API_ENDPOINT_FACTURAR);
            log.debug("facturarProforma() - URL de servicio REST: " + target.getUri());

            target.request(MediaType.APPLICATION_JSON)
                    .header(X_ACTIVITY_ID, request.getUidActividad())
                    .header(X_API_KEY, request.getApiKey())
                    .property(ClientProperties.READ_TIMEOUT, 120000)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            log.debug("getProformaCompleta() - Proforma facturada: " + request.getIdProforma());
        } catch (BadRequestException e) {
            throw RestHttpException.establecerException(e);
        } catch (WebApplicationException e) {
            throw new RestHttpException(e.getResponse().getStatus(), "Error HTTP " + e.getResponse().getStatus() + " - " + e.getLocalizedMessage(), e);
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                throw new RestConnectException("Error al conectar: " + e.getLocalizedMessage(), e);
            } else if (e.getCause() instanceof SocketTimeoutException) {
                throw new RestTimeoutException("Timeout: " + e.getLocalizedMessage(), e);
            } else {
                throw new RestException("Error realizando la petición: " + e.getLocalizedMessage(), e);
            }
        } catch (Exception e) {
            throw new RestException("Error realizando la petición: " + e.getLocalizedMessage(), e);
        }
    }


}
