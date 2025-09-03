package com.comerzzia.iskaypet.pos.services.proformas;

import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBeanExample;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBeanKey;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBeanMapper;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaBean;
import com.comerzzia.iskaypet.pos.services.proformas.exception.ProformaAlreadyExistException;
import com.comerzzia.iskaypet.pos.services.proformas.exception.ProformaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProformaService {

    protected static ProformaService instance;

    public static ProformaService get() {
        if (instance == null) {
            instance = new ProformaService();
        }
        return instance;
    }


    public boolean exist(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma) {
        log.debug("exist() - Consultando si existe proforma con id: {}", idProforma);

        ProformaBeanKey proformaBeanKey = new ProformaBeanKey();
        proformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaBeanKey.setIdProforma(idProforma);

        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        ProformaBean proforma = proformaBeanMapper.selectByPrimaryKey(proformaBeanKey);
        if (proforma == null) {
            log.debug("exist() - Proforma no encontrada con id: {}", idProforma);
            return false;
        }
        log.debug("exist() - Proforma encontrada con id: {}", idProforma);
        return true;
    }


    public long count(SqlSession sqlSession, DatosSesionBean datosSesion) {
        log.debug("count() - Consultando cuantas profromas existen para la actividad: {}", datosSesion.getUidActividad());

        ProformaBeanExample example = new ProformaBeanExample();
        example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        long proformas = proformaBeanMapper.countByExample(example);

        log.debug("consultar() - Proformas encontradas: {}", proformas);
        return proformas;
    }


    public long count(SqlSession sqlSession, DatosSesionBean datosSesion, ParametroBuscarProformaBean parametroBuscarProforma) {
        log.debug("consultar() - Consultando proformas de la actividad: {} con los parametros: {}", datosSesion.getUidActividad(), parametroBuscarProforma);

        ProformaBeanExample example = generateProformaExample(datosSesion, parametroBuscarProforma);

        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        long proformas = proformaBeanMapper.countByExample(example);
        log.debug("consultar() - Proformas encontradas: {}", proformas);
        return proformas;
    }


    public ProformaBean consultar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma) throws ProformaNotFoundException {
        log.debug("consultar() - Consultando proforma con id: {}", idProforma);

        ProformaBeanKey proformaBeanKey = new ProformaBeanKey();
        proformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaBeanKey.setIdProforma(idProforma);

        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        ProformaBean proforma = proformaBeanMapper.selectByPrimaryKey(proformaBeanKey);
        if (proforma == null) {
            log.error("consultar() - No se ha encontrado la proforma con id: {}", idProforma);
            throw new ProformaNotFoundException(idProforma);
        }

        log.debug("consultar() - Proforma encontrada: {}", proforma);
        return proforma;
    }


    public List<ProformaBean> consultar(SqlSession sqlSession, DatosSesionBean datosSesion) {
        log.debug("consultar() - Consultando todas las proformas de la actividad: {}", datosSesion.getUidActividad());

        ParametroBuscarProformaBean parametroBuscarProforma = new ParametroBuscarProformaBean();
        parametroBuscarProforma.setUidActividad(datosSesion.getUidActividad());

        ProformaBeanExample example = generateProformaExample(datosSesion, parametroBuscarProforma);
        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        List<ProformaBean> proformas = proformaBeanMapper.selectByExample(example);
        if (proformas == null || proformas.isEmpty()) {
            log.error("consultar() - No se han encontrado proformas para la actividad: {}", datosSesion.getUidActividad());
            return Collections.emptyList();
        }

        log.debug("consultar() - Proformas encontradas: {}", proformas.size());
        return proformas;
    }


    public List<ProformaBean> consultar(SqlSession sqlSession, DatosSesionBean datosSesion, ParametroBuscarProformaBean parametroBuscarProforma) {
        log.debug("consultar() - Consultando proformas de la actividad: {} con los parametros: {}", datosSesion.getUidActividad(), parametroBuscarProforma);
        ProformaBeanExample example = generateProformaExample(datosSesion, parametroBuscarProforma);
        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        List<ProformaBean> proformas = proformaBeanMapper.selectByExample(example);
        log.debug("consultar() - Proformas encontradas: {}", proformas.size());
        return proformas;
    }


    public ProformaBean crear(SqlSession sqlSession, DatosSesionBean datosSesion, ProformaBean proforma) throws ProformaAlreadyExistException {
        log.debug("crear() - Creando proforma: {}", proforma);

        ProformaBeanKey proformaBeanKey = new ProformaBeanKey();
        proformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaBeanKey.setIdProforma(proforma.getIdProforma());

        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        ProformaBean proformaExistente = proformaBeanMapper.selectByPrimaryKey(proformaBeanKey);
        if (proformaExistente != null) {
            log.error("crear() - La proforma con id: {} ya existe", proforma.getIdProforma());
            throw new ProformaAlreadyExistException(proforma.getIdProforma());
        }

        proforma.setFechaCreacion(new Date());
        proformaBeanMapper.insert(proforma);
        log.debug("crear() - Proforma creada: {}", proforma);
        return proforma;
    }


    public ProformaBean modificar(SqlSession sqlSession, DatosSesionBean datosSesion, ProformaBean proforma) throws ProformaNotFoundException {
        log.debug("modificar() - Modificando proforma: {}", proforma);

        ProformaBeanKey proformaBeanKey = new ProformaBeanKey();
        proformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaBeanKey.setIdProforma(proforma.getIdProforma());

        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        ProformaBean proformaExistente = proformaBeanMapper.selectByPrimaryKey(proformaBeanKey);
        if (proformaExistente == null) {
            log.error("modificar() - La proforma con id: {} no existe", proforma.getIdProforma());
            throw new ProformaNotFoundException(proforma.getIdProforma());
        }

        proforma.setFechaModificacion(new Date());
        proformaBeanMapper.updateByPrimaryKey(proforma);
        log.debug("modificar() - Proforma modificada: {}", proforma);
        return proforma;
    }


    public void eliminar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma) throws ProformaNotFoundException {
        log.debug("eliminar() - Eliminando proforma con id: {}", idProforma);

        ProformaBeanKey proformaBeanKey = new ProformaBeanKey();
        proformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaBeanKey.setIdProforma(idProforma);

        ProformaBeanMapper proformaBeanMapper = sqlSession.getMapper(ProformaBeanMapper.class);
        ProformaBean proforma = proformaBeanMapper.selectByPrimaryKey(proformaBeanKey);
        if (proforma == null) {
            log.error("eliminar() - La proforma con id: {} no existe", idProforma);
            throw new ProformaNotFoundException(idProforma);
        }

        proformaBeanMapper.deleteByPrimaryKey(proformaBeanKey);
        log.debug("eliminar() - Proforma eliminada: {}", idProforma);
    }


    private ProformaBeanExample generateProformaExample(DatosSesionBean datosSesion, ParametroBuscarProformaBean parametroBuscarProforma) {
        log.debug("generateProformaExample() - Generando ejemplo de proforma con los parametros: {}", parametroBuscarProforma);

        ProformaBeanExample example = new ProformaBeanExample();
        ProformaBeanExample.Criteria criteria = example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        if (StringUtils.isNotBlank(parametroBuscarProforma.getIdProforma())) {
            log.debug("generateProformaExample() - Se ha encontrado id proforma: {}", parametroBuscarProforma.getIdProforma());
            criteria.andIdProformaEqualTo(parametroBuscarProforma.getIdProforma());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getUidActividad())) {
            log.debug("generateProformaExample() - Se ha encontrado uid actividad: {}", parametroBuscarProforma.getUidActividad());
            criteria.andUidActividadEqualTo(parametroBuscarProforma.getUidActividad());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getSistemaOrigen())) {
            log.debug("generateProformaExample() - Se ha encontrado sistema origen: {}", parametroBuscarProforma.getSistemaOrigen());
            criteria.andSistemaOrigenEqualTo(parametroBuscarProforma.getSistemaOrigen());
        }

        if (parametroBuscarProforma.getFecha() != null) {
            log.debug("generateProformaExample() - Se ha encontrado fecha: {}", parametroBuscarProforma.getFecha());
            criteria.andFechaEqualTo(parametroBuscarProforma.getFecha());
        }

        if (parametroBuscarProforma.getFechaDesde() != null) {
            log.debug("generateProformaExample() - Se ha encontrado fecha desde: {}", parametroBuscarProforma.getFechaDesde());
            criteria.andFechaGreaterThan(parametroBuscarProforma.getFechaDesde());
        }

        if (parametroBuscarProforma.getFechaDesdeOIgual() != null) {
            log.debug("generateProformaExample() - Se ha encontrado fecha desde o igual: {}", parametroBuscarProforma.getFechaDesdeOIgual());
            criteria.andFechaGreaterThanOrEqualTo(parametroBuscarProforma.getFechaDesdeOIgual());
        }

        if (parametroBuscarProforma.getFechaHasta() != null) {
            log.debug("generateProformaExample() - Se ha encontrado fecha hasta: {}", parametroBuscarProforma.getFechaHasta());
            criteria.andFechaLessThan(parametroBuscarProforma.getFechaHasta());
        }

        if (parametroBuscarProforma.getFechaHastaOIgual() != null) {
            log.debug("generateProformaExample() - Se ha encontrado fecha hasta o igual: {}", parametroBuscarProforma.getFechaHastaOIgual());
            criteria.andFechaLessThanOrEqualTo(parametroBuscarProforma.getFechaHastaOIgual());
        }

        if (parametroBuscarProforma.getIdTipoDocumento() != null) {
            log.debug("generateProformaExample() - Se ha encontrado id tipo documento: {}", parametroBuscarProforma.getIdTipoDocumento());
            criteria.andIdTipoDocumentoEqualTo(parametroBuscarProforma.getIdTipoDocumento());
        }

        if (parametroBuscarProforma.getAutomatica() != null) {
            log.debug("generateProformaExample() - Se ha encontrado automatica: {}", parametroBuscarProforma.getAutomatica());
            criteria.andAutomaticaEqualTo(parametroBuscarProforma.getAutomatica());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getCodalm())) {
            log.debug("generateProformaExample() - Se ha encontrado codalm: {}", parametroBuscarProforma.getCodalm());
            criteria.andCodalmEqualTo(parametroBuscarProforma.getCodalm());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getEstadoActual())) {
            log.debug("generateProformaExample() - Se ha encontrado estado actual: {}", parametroBuscarProforma.getEstadoActual());
            criteria.andEstadoActualEqualTo(parametroBuscarProforma.getEstadoActual());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getUidTicketOrigen())) {
            log.debug("generateProformaExample() - Se ha encontrado uid ticket origen: {}", parametroBuscarProforma.getUidTicketOrigen());
            criteria.andUidTicketOrigenEqualTo(parametroBuscarProforma.getUidTicketOrigen());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getSerieOrigen())) {
            log.debug("generateProformaExample() - Se ha encontrado serie origen: {}", parametroBuscarProforma.getSerieOrigen());
            criteria.andSerieOrigenEqualTo(parametroBuscarProforma.getSerieOrigen());
        }

        if (parametroBuscarProforma.getNumalbOrigen() != null) {
            log.debug("generateProformaExample() - Se ha encontrado numalb origen: {}", parametroBuscarProforma.getNumalbOrigen());
            criteria.andNumalbOrigenEqualTo(parametroBuscarProforma.getNumalbOrigen());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getCodalmOrigen())) {
            log.debug("generateProformaExample() - Se ha encontrado codalm origen: {}", parametroBuscarProforma.getCodalmOrigen());
            criteria.andCodalmOrigenEqualTo(parametroBuscarProforma.getCodalmOrigen());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getCajaOrigen())) {
            log.debug("generateProformaExample() - Se ha encontrado caja origen: {}", parametroBuscarProforma.getCajaOrigen());
            criteria.andCajaOrigenEqualTo(parametroBuscarProforma.getCajaOrigen());
        }

        if (parametroBuscarProforma.getTipoDocumentoOrigen() != null) {
            log.debug("generateProformaExample() - Se ha encontrado tipo de documento origen: {}", parametroBuscarProforma.getTipoDocumentoOrigen());
            criteria.andTipoDocumentoOrigenEqualTo(parametroBuscarProforma.getTipoDocumentoOrigen());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getCodigoFacturaOrigen())) {
            log.debug("generateProformaExample() - Se ha encontrado codigo de factura origen: {}", parametroBuscarProforma.getCodigoFacturaOrigen());
            criteria.andCodigoFacturaOrigenEqualTo(parametroBuscarProforma.getCodigoFacturaOrigen());
        }

        if (parametroBuscarProforma.getFechaCreacion() != null) {
            log.debug("generateProformaExample() - Se ha encontrado fecha de creacion: {}", parametroBuscarProforma.getFechaCreacion());
            criteria.andFechaCreacionEqualTo(parametroBuscarProforma.getFechaCreacion());
        }

        if (parametroBuscarProforma.getFechaModificacion() != null) {
            log.debug("generateProformaExample() - Se ha encontrado fecha de modificacion: {}", parametroBuscarProforma.getFechaModificacion());
            criteria.andFechaModificacionEqualTo(parametroBuscarProforma.getFechaModificacion());
        }

        log.debug("generateProformaExample() - Ejemplo de proforma generado: {}", example);
        return example;

    }
}
