package com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception;

import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.path.BackofficeWebservicesPath;
import com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion;
import com.comerzzia.iskaypet.pos.persistence.colectivos.LocalColectivos;
import com.comerzzia.iskaypet.pos.persistence.colectivos.LocalColectivosKey;
import com.comerzzia.iskaypet.pos.persistence.colectivos.LocalColectivosMapper;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosExample;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosKey;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosMapper;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.fidelizados.LocalFidelizados;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.fidelizados.LocalFidelizadosExample;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.fidelizados.LocalFidelizadosKey;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.fidelizados.LocalFidelizadosMapper;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.FormularioBusquedaFidelizadoBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FidelizacionService {

    public static final Logger log = Logger.getLogger(FidelizacionService.class);


    @Autowired
    public LocalFidelizadosMapper localFidelizadosMapper;

    @Autowired
    public LocalColectivosMapper localColectivosMapper;

    @Autowired
    public LocalFidelizadosColectivosMapper localFidelizadosColectivosMapper;

    private volatile boolean offlineMode;

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }


    public boolean isOfflineMode() {
        return this.offlineMode;
    }

    public boolean checkOnlineStatus() {
        log.debug("checkOnlineStatus() - enviando peticion a 'status' endpoint para comprobar conexión con el servidor.");

        try {
            ClientConfig config = new ClientConfig();
            config.property(ClientProperties.CONNECT_TIMEOUT, 3000);
            config.property(ClientProperties.READ_TIMEOUT, 3000);

            Client client = ClientBuilder.newClient(config);
            WebTarget target = client.target(BackofficeWebservicesPath.servicio).path("status");
            log.debug("checkOnlineStatus() - URL de servicio: " + target.getUri());

            Response response = target.request(MediaType.APPLICATION_XML).get();
            int status = response.getStatus();
            log.debug("checkOnlineStatus() - Código de respuesta: " + status);

            if (status == 200) {
                String responseBody = response.readEntity(String.class);

                if (responseBody.contains("<statusOk>true</statusOk>")) {
                    log.debug("checkOnlineStatus() - Conexión confirmada. Estamos en modo online.");
                    return false;
                }
            }
        } catch (ProcessingException e) {
            if (e.getCause() instanceof UnknownHostException) {
                log.warn("checkOnlineStatus() - No se pudo resolver el host. Sin conexión.");
            } else if (e.getCause() instanceof ConnectException) {
                log.warn("checkOnlineStatus() - Error de conexión con el servidor: " + e.getMessage());
            } else {
                log.warn("checkOnlineStatus() - Error de procesamiento de la solicitud: " + e.getMessage());
            }
        } catch (Exception e) {
            log.warn("checkOnlineStatus() - Error inesperado al verificar conexión con el servidor: " + e.getMessage());
        }
        // Si la llamada falla por cualquier motivo, activamos el modo offline
        return true;
    }


    public List<FidelizadoBean> buscarLocalmenteFidelizado(FormularioBusquedaFidelizadoBean frBusqueda) {
        log.debug("buscarLocalmenteFidelizadoAsFidelizadoBean() - Buscando en local DB con: " + frBusqueda);

        LocalFidelizadosExample example = new LocalFidelizadosExample();
        LocalFidelizadosExample.Criteria criteria = example.createCriteria();

        // "Nombre"
        if (StringUtils.isNotBlank(frBusqueda.getNombre())) {
            criteria.andNombreLike("%" + frBusqueda.getNombre().trim() + "%");
        }

        // "Apellidos"
        if (StringUtils.isNotBlank(frBusqueda.getApellidos())) {
            criteria.andApellidosLike("%" + frBusqueda.getApellidos().trim() + "%");
        }

        // "Documento" (DNI, NIF, etc.)
        if (StringUtils.isNotBlank(frBusqueda.getDocumento())) {
            criteria.andDocumentoLike("%" + frBusqueda.getDocumento().trim() + "%");
        }

        // "Número de tarjeta"
        if (StringUtils.isNotBlank(frBusqueda.getCodTarjRegalo())) {
            criteria.andNumeroTarjetaLike("%" + frBusqueda.getCodTarjRegalo().trim() + "%");
        }

        // Telefono
        if (StringUtils.isNotBlank(frBusqueda.getTelefono())) {
            criteria.andTelefonoLike("%" + frBusqueda.getTelefono().trim() + "%");
        }

        // Email
        if (StringUtils.isNotBlank(frBusqueda.getEmail())) {
            criteria.andEmailLike("%" + frBusqueda.getEmail().trim() + "%");
        }

        List<LocalFidelizados> localList = localFidelizadosMapper.selectByExample(example);

        log.debug("buscarLocalmenteFidelizado() - Ha encontrado " + localList.size() + " fidelizados en la tabla local.");

        // Convertir LocalFidelizados a FidelizadoBean
        List<FidelizadoBean> result = new ArrayList<>();
        for (LocalFidelizados row : localList) {
            FidelizadoBean bean = convertirLocalFidelizadosAFidelizadoBean(row);

            // cargar colectivos
            List<ColectivosFidelizadoBean> colectivos = CargarColectivosDesdeLocal(
                    row.getUidInstancia(),
                    row.getIdFidelizado()
            );

            bean.setColectivos(colectivos);

            result.add(bean);
        }

        return result;
    }

    public FidelizadoBean buscarFidelizadoOfflinePorId(Long idFidelizado) {
        log.debug("buscarFidelizadoOfflinePorId() - Buscando en local DB con id: " + idFidelizado);

        if (idFidelizado == null) {
            log.warn("buscarFidelizadoOfflinePorId() - idFidelizado es null.");
            return null;
        }

        LocalFidelizadosExample example = new LocalFidelizadosExample();
        LocalFidelizadosExample.Criteria criteria = example.createCriteria();
        criteria.andIdFidelizadoEqualTo(idFidelizado);

        List<LocalFidelizados> results = localFidelizadosMapper.selectByExample(example);

        if (results.isEmpty()) {
            log.debug("buscarFidelizadoOfflinePorId() - No se ha encontrado records con idFidelizado: " + idFidelizado);
            return null;
        }

        if (results.size() > 1) {
            log.warn("buscarFidelizadoOfflinePorId() - Encontrados multiples records con idFidelizado: " + idFidelizado);
        }

        LocalFidelizados localFidelizado = results.get(0);
        FidelizadoBean fidelizadoBean = convertirLocalFidelizadosAFidelizadoBean(localFidelizado);

        List<ColectivosFidelizadoBean> colectivos = CargarColectivosDesdeLocal(
                localFidelizado.getUidInstancia(),
                localFidelizado.getIdFidelizado()
        );
        fidelizadoBean.setColectivos(colectivos);

        return fidelizadoBean;
    }

    public FidelizadoBean buscarFidelizadoOfflinePorNumeroTarjeta(String numeroTarjeta) {

        log.debug("buscarFidelizadoOfflinePorNumeroTarjeta() - Buscando en local DB con NUMERO_TARJETA: " + numeroTarjeta);

        if (org.apache.commons.lang3.StringUtils.isBlank(numeroTarjeta)) {
            log.warn("buscarFidelizadoOfflinePorNumeroTarjeta() - numeroTarjeta es null o está vacío.");
            return null;
        }

        LocalFidelizadosExample example = new LocalFidelizadosExample();
        LocalFidelizadosExample.Criteria criteria = example.createCriteria();

        criteria.andNumeroTarjetaEqualTo(numeroTarjeta);

        List<LocalFidelizados> results = localFidelizadosMapper.selectByExample(example);

        if (results.isEmpty()) {
            log.debug("buscarFidelizadoOfflinePorNumeroTarjeta() - No se ha encontrado ningún registro con NUMERO_TARJETA: " + numeroTarjeta);
            return null;
        }

        if (results.size() > 1) {
            log.warn("buscarFidelizadoOfflinePorNumeroTarjeta() - Se han encontrado múltiples registros con NUMERO_TARJETA: " + numeroTarjeta);
        }

        LocalFidelizados localFidelizado = results.get(0);
        FidelizadoBean fidelizadoBean = convertirLocalFidelizadosAFidelizadoBean(localFidelizado);

        List<ColectivosFidelizadoBean> colectivos = CargarColectivosDesdeLocal(
                localFidelizado.getUidInstancia(),
                localFidelizado.getIdFidelizado()
        );
        fidelizadoBean.setColectivos(colectivos);

        return fidelizadoBean;
    }

    public FidelizadoBean convertirLocalFidelizadosAFidelizadoBean(LocalFidelizados local) {
        FidelizadoBean bean = new FidelizadoBean();

        bean.setIdFidelizado(local.getIdFidelizado());
        bean.setNumeroTarjeta(local.getNumeroTarjeta());
        bean.setNombre(local.getNombre());
        bean.setApellidos(local.getApellidos());
        bean.setDomicilio(local.getDomicilio());
        bean.setLocalidad(local.getLocalidad());
        bean.setProvincia(local.getProvincia());
        bean.setCp(local.getCp());
        bean.setCodPais(local.getCodpais());
        bean.setCodTipoIden(local.getCodtipoiden());
        bean.setDocumento(local.getDocumento());
        bean.setFechaAlta(local.getFechaAlta());
        bean.setFechaModificacion(local.getFechaModificacion());
        bean.setCodlengua(local.getCodlengua());
        bean.setCodFidelizado(local.getCodfidelizado());

        // Add telefono and email
        if (StringUtils.isNotBlank(local.getTelefono())) {
            TiposContactoFidelizadoBean telefono = new TiposContactoFidelizadoBean();
            telefono.setCodTipoCon("TELEFONO1");
            telefono.setValor(local.getTelefono());
            bean.getContactos().add(telefono);
        }

        if (StringUtils.isNotBlank(local.getEmail())) {
            TiposContactoFidelizadoBean email = new TiposContactoFidelizadoBean();
            email.setCodTipoCon("EMAIL");
            email.setValor(local.getEmail());
            bean.getContactos().add(email);
        }

        return bean;
    }

    /**
     * Lee x_local_fidelizados_colectivos_tbl matching (UID_INSTANCIA, ID_FIDELIZADO).
     */
    public List<ColectivosFidelizadoBean> CargarColectivosDesdeLocal(String uidInstancia, Long idFidelizado) {
        LocalFidelizadosColectivosExample example = new LocalFidelizadosColectivosExample();
        LocalFidelizadosColectivosExample.Criteria cr = example.createCriteria();
        cr.andUidInstanciaEqualTo(uidInstancia);
        cr.andIdFidelizadoEqualTo(idFidelizado);

        List<LocalFidelizadosColectivosKey> bridgingRows = localFidelizadosColectivosMapper.selectByExample(example);

        List<ColectivosFidelizadoBean> colectivos = new ArrayList<>();
        for (LocalFidelizadosColectivosKey row : bridgingRows) {
            LocalColectivosKey key = new LocalColectivosKey();
            key.setUidInstancia(row.getUidInstancia());
            key.setCodColectivo(row.getCodColectivo());

            LocalColectivos localColectivo = localColectivosMapper.selectByPrimaryKey(key);

            ColectivosFidelizadoBean colectivoBean = new ColectivosFidelizadoBean();
            colectivoBean.setCodColectivo(row.getCodColectivo());

            if (localColectivo != null) {
                colectivoBean.setDesColectivo(localColectivo.getDesColectivo());
                colectivoBean.setCodtipcolectivo(localColectivo.getCodtipcolectivo());
                colectivoBean.setDestipcolectivo(localColectivo.getDestipcolectivo());
            }

            colectivos.add(colectivoBean);
        }

        return colectivos;
    }

    /**
     * inserts/updates un fidelizado record y sus colectivos
     * en la DB local usando MyBatis-generated mappers.
     */
    public void guardarFidelizadoOffline(FidelizadoBean fidelizadoBean, Sesion sesion) {

        if (fidelizadoBean == null) {
            log.warn("guardarFidelizadoOffline() - Se ha llamado con fidelizadoBean null, ignorando.");
            return;
        }

        log.debug("guardarFidelizadoOffline() - guardando fidelizado " + fidelizadoBean.getIdFidelizado() + " to local DB.");

        //Preparamos clave compuesta: (uidInstancia, idFidelizado)
        String uidInstancia = sesion.getAplicacion().getUidInstancia();
        Long idFidelizado = fidelizadoBean.getIdFidelizado();

        LocalFidelizadosKey key = new LocalFidelizadosKey();
        key.setUidInstancia(uidInstancia);
        key.setIdFidelizado(idFidelizado);

        //Revisar si el record existe
        LocalFidelizados existing = localFidelizadosMapper.selectByPrimaryKey(key);

        //Construir el objeto LocalFidelizados desde FidelizacionBean
        LocalFidelizados local = new LocalFidelizados();
        local.setUidInstancia(uidInstancia);
        local.setIdFidelizado(idFidelizado);

        local.setNombre(fidelizadoBean.getNombre());
        local.setApellidos(fidelizadoBean.getApellidos());
        local.setDomicilio(fidelizadoBean.getDomicilio());
        local.setLocalidad(fidelizadoBean.getLocalidad());
        local.setProvincia(fidelizadoBean.getProvincia());
        local.setCp(fidelizadoBean.getCp());
        local.setCodpais(fidelizadoBean.getCodPais());
        local.setCodtipoiden(fidelizadoBean.getCodTipoIden());
        local.setDocumento(fidelizadoBean.getDocumento());
        local.setCodlengua(fidelizadoBean.getCodlengua());
        local.setNumeroTarjeta(fidelizadoBean.getNumeroTarjeta());


        // Extraer telefono/email de "contactos" si  existe:
        TiposContactoFidelizadoBean tlfBean = fidelizadoBean.getTipoContacto("TELEFONO1");
        if (tlfBean == null) {
            tlfBean = fidelizadoBean.getTipoContacto("MOVIL");
        }
        if (tlfBean != null) {
            local.setTelefono(tlfBean.getValor());
        }

        TiposContactoFidelizadoBean mailBean = fidelizadoBean.getTipoContacto("EMAIL");
        if (mailBean != null) {
            local.setEmail(mailBean.getValor());
        }

        //Almacenar "fechaAlta" solo la primera vez de ser nuevo
        Date now = new Date();
        if (existing == null) {
            local.setFechaAlta(now);
        } else {
            local.setFechaAlta(existing.getFechaAlta());
        }
        local.setFechaModificacion(now);

        local.setFechaVersion(now); // guardar fecha ultima modificacion con timestamp actual

        local.setCodfidelizado(fidelizadoBean.getCodFidelizado());

        //Insert o update
        if (existing == null) {
            // Not found => insert
            log.debug("guardarFidelizadoOffline() - Inserting new fidelizado: " + idFidelizado);
            localFidelizadosMapper.insert(local);
        } else {
            // Found => update
            log.debug("guardarFidelizadoOffline() - Updating existing fidelizado: " + idFidelizado);
            localFidelizadosMapper.updateByPrimaryKey(local);
        }

        //colectivos
        if (fidelizadoBean.getColectivos() != null) {
            deleteAllLocalFidelizadosColectivos(uidInstancia, idFidelizado);

            for (ColectivosFidelizadoBean c : fidelizadoBean.getColectivos()) {
                upsertLocalColectivo(uidInstancia, c);

                //  Insertar la relación en x_local_fidelizados_colectivos_tbl
                insertLocalFidelizadoColectivo(uidInstancia, idFidelizado, c.getCodColectivo());
            }
        }

        log.debug("guardarFidelizadoOffline() - Done for fidelizado " + idFidelizado);
    }

    public void deleteAllLocalFidelizadosColectivos(String uidInstancia, Long idFidelizado) {
        LocalFidelizadosColectivosExample example = new LocalFidelizadosColectivosExample();
        LocalFidelizadosColectivosExample.Criteria criteria = example.createCriteria();
        criteria.andUidInstanciaEqualTo(uidInstancia);
        criteria.andIdFidelizadoEqualTo(idFidelizado);

        // Esto elimina los registros de relación *solo* para el fidelizado dado
        localFidelizadosColectivosMapper.deleteByExample(example);
    }

    public void upsertLocalColectivo(String uidInstancia, ColectivosFidelizadoBean colectivoBean) {
        // Construir la clave para x_local_colectivos_tbl
        LocalColectivosKey key = new LocalColectivosKey();
        key.setUidInstancia(uidInstancia);
        key.setCodColectivo(colectivoBean.getCodColectivo());

        // Verificar si el colectivo ya existe
        LocalColectivos existing = localColectivosMapper.selectByPrimaryKey(key);

        // Construir o actualizar el registro local
        LocalColectivos local = new LocalColectivos();
        local.setUidInstancia(uidInstancia);
        local.setCodColectivo(colectivoBean.getCodColectivo());
        local.setDesColectivo(colectivoBean.getDesColectivo());
        local.setCodtipcolectivo(colectivoBean.getCodtipcolectivo());
        local.setDestipcolectivo(colectivoBean.getDestipcolectivo());

        // 4) Insert o update
        if (existing == null) {
            localColectivosMapper.insert(local);
        } else {
            // Actualizar solo si algo cambió
            localColectivosMapper.updateByPrimaryKey(local);
        }
    }

    public void insertLocalFidelizadoColectivo(String uidInstancia, Long idFidelizado, String codColectivo) {
        LocalFidelizadosColectivosKey key = new LocalFidelizadosColectivosKey();
        key.setUidInstancia(uidInstancia);
        key.setIdFidelizado(idFidelizado);
        key.setCodColectivo(codColectivo);

        localFidelizadosColectivosMapper.insert(key);
    }


    public static com.comerzzia.api.model.loyalty.FidelizadoBean convertPosToApi(FidelizacionBean posBean) {
        com.comerzzia.api.model.loyalty.FidelizadoBean apiBean = new com.comerzzia.api.model.loyalty.FidelizadoBean();

        apiBean.setIdFidelizado(posBean.getIdFidelizado());
        apiBean.setNumeroTarjeta(posBean.getNumTarjetaFidelizado());
        apiBean.setNombre(posBean.getNombre());
        apiBean.setApellidos(posBean.getApellido());
        apiBean.setDomicilio(posBean.getDomicilio());
        apiBean.setLocalidad(posBean.getLocalidad());
        apiBean.setDocumento(posBean.getDocumento());
        apiBean.setProvincia(posBean.getProvincia());
        apiBean.setCp(posBean.getCp());

        return apiBean;
    }

    public static FidelizacionBean convertApiToPos(com.comerzzia.api.model.loyalty.FidelizadoBean apiBean) {
        FidelizacionBean posBean = new FidelizacionBean();
        posBean.setIdFidelizado(apiBean.getIdFidelizado());
        posBean.setNumTarjetaFidelizado(apiBean.getNumeroTarjeta());
        posBean.setNombre(apiBean.getNombre());
        posBean.setApellido(apiBean.getApellidos());
        posBean.setDomicilio(apiBean.getDomicilio());
        posBean.setLocalidad(apiBean.getLocalidad());
        posBean.setCodTipoIden(apiBean.getCodTipoIden());
        posBean.setDocumento(apiBean.getDocumento());
        posBean.setProvincia(apiBean.getProvincia());
        posBean.setCp(apiBean.getCp());
        posBean.setCodPais(apiBean.getCodPais());
        posBean.setDesPais(apiBean.getDesPais());

        posBean.setActiva(true);
        posBean.setBaja(false);
        posBean.setSaldo(BigDecimal.ZERO);
        posBean.setSaldoProvisional(BigDecimal.ZERO);
        posBean.setPaperLess(apiBean.getPaperLess() != null ? apiBean.getPaperLess() : false);

        // Se maneja la lista de colectivos, si no tiene, se asigna una lista vacía
        List<String> codColectivosList = apiBean.getColectivos() != null && !apiBean.getColectivos().isEmpty()
                ? apiBean.getColectivos().stream().map(ColectivosFidelizadoBean::getCodColectivo).collect(Collectors.toList())
                : Collections.emptyList();
        posBean.setCodColectivos(codColectivosList);

        // Se añaden adicionales (fidelizacion, carencia, saldo no disponible, etc)
        posBean.putAdicional(IskaypetFidelizacion.PARAMETRO_APLICA_FIDELIZACION, IskaypetFidelizacion.isFidelizado(codColectivosList));
        posBean.putAdicional(IskaypetFidelizacion.PARAMETRO_SALDO_NO_DISPONIBLE, BigDecimal.ZERO);
        posBean.putAdicional(IskaypetFidelizacion.PARAMETRO_APLICA_CARENCIA, IskaypetFidelizacion.aplicaCarencia());
        posBean.putAdicional(IskaypetFidelizacion.PARAMETRO_COD_LENGUAJE, apiBean.getCodlengua());

        // Añadir datos contacto
        if (apiBean.getContactos() != null && !apiBean.getContactos().isEmpty()) {
            for (TiposContactoFidelizadoBean contactoBean : apiBean.getContactos()) {
                switch (contactoBean.getCodTipoCon()) {
                    case  IskaypetFidelizacion.TIPO_CONTACTO_COD_EMAIL:
                        posBean.putAdicional(IskaypetFidelizacion.PARAMETRO_EMAIL, contactoBean.getValor());
                        break;
                    case IskaypetFidelizacion.TIPO_CONTACTO_COD_TELEFONO:
                        posBean.putAdicional(IskaypetFidelizacion.PARAMETRO_TELEFONO, contactoBean.getValor());
                        break;
                    case IskaypetFidelizacion.TIPO_CONTACTO_COD_MOVIL:
                        posBean.putAdicional(IskaypetFidelizacion.PARAMETRO_MOVIL, contactoBean.getValor());
                        break;
                    default:
                        break;
                }
            }
        }

        return posBean;
    }
}
