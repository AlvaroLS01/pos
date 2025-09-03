package com.comerzzia.iskaypet.pos.services.proformas.rest;

import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.iskaypet.pos.api.rest.proformas.ProformasRest;
import com.comerzzia.iskaypet.pos.api.rest.proformas.request.ProformaRequestRest;
import com.comerzzia.iskaypet.pos.persistence.proformas.facturada.ProformaFacturadaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.facturada.ProformaFacturadaBeanKey;
import com.comerzzia.iskaypet.pos.persistence.proformas.facturada.ProformaFacturadaBeanMapper;
import com.comerzzia.iskaypet.pos.services.proformas.exception.ProformaException;
import com.comerzzia.iskaypet.pos.services.proformas.exception.ProformaRestException;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaHeaderDTO;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ProformaRestService {

    @Autowired
    private VariablesServices variablesService;

    @Autowired
    private ProformaFacturadaBeanMapper proformaFacturadaBeanMapper;

    public static final String WEBSERVICE_PROFORMAS = "X_URL_PROFORMAS";
    public static final String TIPO_DEVOLUCION = "DEVOLUCION";
    public static final String TIPO_VENTA = "VENTA";

    public static final List<String> VALORES_VENTA = Arrays.asList("FS", "FT");
    public static final List<String> VALORES_DEVOLUCION = Arrays.asList("NS", "NC");
    public static final List<String> VALORES_DATOS_FACTURACION = Arrays.asList("FT", "NC");


    private static final Logger log = Logger.getLogger(ProformaRestService.class);


    public List<ProformaHeaderDTO> listarProformas(Sesion sesion, String tipo) throws ProformaRestException {

        try {

            String url = variablesService.getVariableAsString(WEBSERVICE_PROFORMAS);
            String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);

            ProformaRequestRest request = new ProformaRequestRest();
            request.setAlmacen(sesion.getAplicacion().getCodAlmacen());
            request.setUidActividad(sesion.getAplicacion().getUidActividad());
            request.setApiKey(apiKey);

            if (TIPO_VENTA.equalsIgnoreCase(tipo)) {
                request.setTiposDocumentos(VALORES_VENTA);
            } else if (TIPO_DEVOLUCION.equalsIgnoreCase(tipo)) {
                request.setTiposDocumentos(VALORES_DEVOLUCION);
            }

            List<ProformaHeaderDTO> lstProforma = ProformasRest.getProformas(url, request);
            List<ProformaHeaderDTO> lstProformasNoFacturadas = new ArrayList<>();
            for (ProformaHeaderDTO proforma : lstProforma) {
                ProformaFacturadaBeanKey key = new ProformaFacturadaBeanKey();
                key.setUidActividad(proforma.getUidActividad());
                key.setIdProforma(proforma.getIdProforma());
                ProformaFacturadaBean proformaFacturada = proformaFacturadaBeanMapper.selectByPrimaryKey(key);
                if (proformaFacturada == null) {
                    lstProformasNoFacturadas.add(proforma);
                }
            }
            if (!lstProformasNoFacturadas.isEmpty()) {
                log.debug("Se ordenan las proformas no facturadas por fecha de proforma de mas reciente a mas antigua");
                // Se muestran las proformas no facturadas ordenadas por fecha de proforma de mas reciente a mas antigua
                lstProformasNoFacturadas.sort(Comparator.comparing(ProformaHeaderDTO::getFechaProforma).reversed());
            }

            return lstProformasNoFacturadas;
        } catch (RestException | RestHttpException e) {
            log.error("Error listando proformas: " + e.getMessage(), e);
            throw new ProformaRestException(e);
        }
    }

    public ProformaDTO obtenerProformaCompleta(Sesion sesion, String idProforma) throws ProformaRestException {
        try {
            String url = variablesService.getVariableAsString(WEBSERVICE_PROFORMAS);
            ProformaDTO proforma = ProformasRest.getProformaCompleta(url, generarProformaRequest(sesion, idProforma));
            log.debug("Proforma completa recibida: " + proforma);
            return proforma;
        } catch (RestException | RestHttpException e) {
            log.error("Error obteniendo proforma completa: " + e.getMessage(), e);
            throw new ProformaRestException(e);
        }
    }

    public void facturarProformaEnSegundoPlano(Sesion sesion, String idProforma) {
        // Crear un Task para ejecutar en segundo plano
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    // 1.- Insertar la proforma como facturada en la base de datos local
                    log.debug("Facturando proforma en segundo plano: " + idProforma);
                    insertarProformaFacturada(sesion, idProforma);

                    // 2.- Llamar al servicio REST para notificar de la proforma facturada
                    String url = variablesService.getVariableAsString(WEBSERVICE_PROFORMAS);
                    ProformasRest.facturarProforma(url, generarProformaRequest(sesion, idProforma));

                    log.debug("Proforma facturada: " + idProforma);

                } catch (Exception e) {
                    log.error("Error en facturación en segundo plano para la proforma: " + idProforma + " - " + e.getMessage(), e);
                }
                return null;
            }
        };

        // Iniciar la tarea en un nuevo hilo
        new Thread(task).start();
    }

    private void insertarProformaFacturada(Sesion sesion, String idProforma) throws ProformaException {

        try {
            log.info("Insertando factura proforma facturada: " + idProforma);
            ProformaFacturadaBean proformaFacturadaBean = new ProformaFacturadaBean();
            proformaFacturadaBean.setUidActividad(sesion.getAplicacion().getUidActividad());
            proformaFacturadaBean.setIdProforma(idProforma);
            proformaFacturadaBean.setFechaFacturacion(new Date());
            proformaFacturadaBeanMapper.insert(proformaFacturadaBean);
            log.debug("Proforma facturada insertada: " + proformaFacturadaBean);
        } catch (Exception e) {
            log.error("Error facturando proforma: " + e.getMessage(), e);
            throw new ProformaException("Error facturando proforma: " + e.getMessage(), e);
        }
    }

    private ProformaRequestRest generarProformaRequest(Sesion sesion, String idProforma) {
        log.info("Generando proforma request: " + idProforma);
        String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
        ProformaRequestRest request = new ProformaRequestRest();
        request.setUidActividad(sesion.getAplicacion().getUidActividad());
        request.setApiKey(apiKey);
        request.setIdProforma(idProforma);
        log.debug("Generada proforma request: " + request);
        return request;
    }

}

