package com.comerzzia.iskaypet.pos.services.proformas.lineas.auditorias;

import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarAuditoriaLineaProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.auditorias.AuditoriaLineaProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.auditorias.AuditoriaLineaProformaBeanExample;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.auditorias.AuditoriaLineaProformaBeanKey;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.auditorias.AuditoriaLineaProformaBeanMapper;
import com.comerzzia.iskaypet.pos.services.proformas.lineas.auditorias.exception.AuditoriaLineaProformaAlreadyExistException;
import com.comerzzia.iskaypet.pos.services.proformas.lineas.auditorias.exception.AuditoriaLineaProformaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AuditoriaLineaProformaService {

    protected static AuditoriaLineaProformaService instance;

    public static AuditoriaLineaProformaService get() {
        if (instance == null) {
            instance = new AuditoriaLineaProformaService();
        }
        return instance;
    }

    public AuditoriaLineaProformaBean consultar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma, Integer linea, String uidAuditoria) throws AuditoriaLineaProformaNotFoundException {
        log.debug("consultar() - Consultando auditoria con id: {}, linea: {} y uidAuditoria: {}", idProforma, linea, uidAuditoria);

        AuditoriaLineaProformaBeanKey auditoriaLineaProformaBeanKey = new AuditoriaLineaProformaBeanKey();
        auditoriaLineaProformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        auditoriaLineaProformaBeanKey.setIdProforma(idProforma);
        auditoriaLineaProformaBeanKey.setLinea(linea);
        auditoriaLineaProformaBeanKey.setUidAuditoria(uidAuditoria);

        AuditoriaLineaProformaBeanMapper auditoriaLineaProformaBeanMapper = sqlSession.getMapper(AuditoriaLineaProformaBeanMapper.class);
        AuditoriaLineaProformaBean proforma = auditoriaLineaProformaBeanMapper.selectByPrimaryKey(auditoriaLineaProformaBeanKey);
        if (proforma == null) {
            log.error("consultar() - No se ha encontrado la auditoria de la proforma con  id: {}, linea: {} y uidAuditoria: {}", idProforma, linea, uidAuditoria);
            throw new AuditoriaLineaProformaNotFoundException(idProforma);
        }

        log.debug("consultar() - Auditoria proforma encontrada: {}", proforma);
        return proforma;
    }


    public List<AuditoriaLineaProformaBean> consultar(SqlSession sqlSession, DatosSesionBean datosSesion) {
        log.debug("consultar() - Consultando todas las auditorias de proformas de la actividad: {}", datosSesion.getUidActividad());

        AuditoriaLineaProformaBeanExample example = new AuditoriaLineaProformaBeanExample();
        example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        AuditoriaLineaProformaBeanMapper auditoriaLineaProformaBeanMapper = sqlSession.getMapper(AuditoriaLineaProformaBeanMapper.class);
        List<AuditoriaLineaProformaBean> proformas = auditoriaLineaProformaBeanMapper.selectByExample(example);
        if (proformas == null || proformas.isEmpty()) {
            log.error("consultar() - No se han encontrado auditorias de proformas para la actividad: {}", datosSesion.getUidActividad());
            return Collections.emptyList();
        }

        log.debug("consultar() - Auditorias de las proformas encontradas: {}", proformas.size());
        return proformas;
    }


    public List<AuditoriaLineaProformaBean> consultar(SqlSession sqlSession, DatosSesionBean datosSesion, ParametroBuscarAuditoriaLineaProformaBean parametroBuscarAuditoriaLineaProforma) {
        log.debug("consultar() - Consultando auditorias proformas de la actividad: {} con los parametros: {}", datosSesion.getUidActividad(), parametroBuscarAuditoriaLineaProforma);

        AuditoriaLineaProformaBeanExample example = new AuditoriaLineaProformaBeanExample();
        AuditoriaLineaProformaBeanExample.Criteria criteria = example.or().andUidActividadEqualTo(datosSesion.getUidActividad());


        if (StringUtils.isNotBlank(parametroBuscarAuditoriaLineaProforma.getUidActividad())) {
            criteria.andUidActividadEqualTo(parametroBuscarAuditoriaLineaProforma.getUidActividad());
        }

        if (StringUtils.isNotBlank(parametroBuscarAuditoriaLineaProforma.getIdProforma())) {
            criteria.andIdProformaEqualTo(parametroBuscarAuditoriaLineaProforma.getIdProforma());
        }

        if (parametroBuscarAuditoriaLineaProforma.getLinea() != null) {
            criteria.andLineaEqualTo(parametroBuscarAuditoriaLineaProforma.getLinea());
        }

        if (StringUtils.isNotBlank(parametroBuscarAuditoriaLineaProforma.getUidAuditoria())) {
            criteria.andUidAuditoriaEqualTo(parametroBuscarAuditoriaLineaProforma.getUidAuditoria());
        }

        if (StringUtils.isNotBlank(parametroBuscarAuditoriaLineaProforma.getCodemp())) {
            criteria.andCodempEqualTo(parametroBuscarAuditoriaLineaProforma.getCodemp());
        }

        if (StringUtils.isNotBlank(parametroBuscarAuditoriaLineaProforma.getTipoAuditoria())) {
            criteria.andTipoAuditoriaEqualTo(parametroBuscarAuditoriaLineaProforma.getTipoAuditoria());
        }

        if (parametroBuscarAuditoriaLineaProforma.getCodMotivo() != null) {
            criteria.andCodMotivoEqualTo(parametroBuscarAuditoriaLineaProforma.getCodMotivo());
        }

        if (StringUtils.isNotBlank(parametroBuscarAuditoriaLineaProforma.getObservaciones())) {
            criteria.andObservacionesLike("%" + parametroBuscarAuditoriaLineaProforma.getObservaciones() + "%");
        }


        AuditoriaLineaProformaBeanMapper auditoriaLineaProformaBeanMapper = sqlSession.getMapper(AuditoriaLineaProformaBeanMapper.class);
        List<AuditoriaLineaProformaBean> proformas = auditoriaLineaProformaBeanMapper.selectByExample(example);
        log.debug("consultar() - Auditorias de las proformas encontradas: {}", proformas.size());
        return proformas;
    }


    public AuditoriaLineaProformaBean crear(SqlSession sqlSession, DatosSesionBean datosSesion, AuditoriaLineaProformaBean auditoria) throws AuditoriaLineaProformaAlreadyExistException {
        log.debug("crear() - Creando auditoria proforma: {}", auditoria);

        AuditoriaLineaProformaBeanKey auditoriaLineaProformaBeanKey = new AuditoriaLineaProformaBeanKey();
        auditoriaLineaProformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        auditoriaLineaProformaBeanKey.setIdProforma(auditoria.getIdProforma());
        auditoriaLineaProformaBeanKey.setLinea(auditoria.getLinea());
        auditoriaLineaProformaBeanKey.setUidAuditoria(auditoria.getUidAuditoria());

        AuditoriaLineaProformaBeanMapper auditoriaLineaProformaBeanMapper = sqlSession.getMapper(AuditoriaLineaProformaBeanMapper.class);
        AuditoriaLineaProformaBean proformaExistente = auditoriaLineaProformaBeanMapper.selectByPrimaryKey(auditoriaLineaProformaBeanKey);
        if (proformaExistente != null) {
            log.error("crear() - La auditoria de la proforma con id: {} y auditoria: {} ya existe", auditoria.getIdProforma(), auditoria.getLinea());
            throw new AuditoriaLineaProformaAlreadyExistException(auditoria.getIdProforma());
        }

        auditoriaLineaProformaBeanMapper.insert(auditoria);
        log.debug("crear() - Auditoria de la proforma creada: {}", auditoria);
        return auditoria;
    }


    public AuditoriaLineaProformaBean modificar(SqlSession sqlSession, DatosSesionBean datosSesion, AuditoriaLineaProformaBean auditoria) throws AuditoriaLineaProformaNotFoundException {
        log.debug("modificar() - Modificando auditoria proforma: {}", auditoria);

        AuditoriaLineaProformaBeanKey auditoriaLineaProformaBeanKey = new AuditoriaLineaProformaBeanKey();
        auditoriaLineaProformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        auditoriaLineaProformaBeanKey.setIdProforma(auditoria.getIdProforma());
        auditoriaLineaProformaBeanKey.setLinea(auditoria.getLinea());
        auditoriaLineaProformaBeanKey.setUidAuditoria(auditoria.getUidAuditoria());

        AuditoriaLineaProformaBeanMapper auditoriaLineaProformaBeanMapper = sqlSession.getMapper(AuditoriaLineaProformaBeanMapper.class);
        AuditoriaLineaProformaBean proformaExistente = auditoriaLineaProformaBeanMapper.selectByPrimaryKey(auditoriaLineaProformaBeanKey);
        if (proformaExistente == null) {
            log.error("modificar() - La auditoria de la proforma con id: {} y auditoria: {} no existe", auditoria.getIdProforma(), auditoria.getLinea());
            throw new AuditoriaLineaProformaNotFoundException(auditoria.getIdProforma());
        }

        auditoriaLineaProformaBeanMapper.updateByPrimaryKey(auditoria);
        log.debug("modificar() - Auditoria de la proforma modificada: {}", auditoria);
        return auditoria;
    }


    public void eliminar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma, Integer linea, String uidAuditoria) throws AuditoriaLineaProformaNotFoundException {
        log.debug("eliminar() - Eliminando auditoria proforma con id: {}, linea: {} y uidAuditoria: {}", idProforma, linea, uidAuditoria);

        AuditoriaLineaProformaBeanKey auditoriaLineaProformaBeanKey = new AuditoriaLineaProformaBeanKey();
        auditoriaLineaProformaBeanKey.setUidActividad(datosSesion.getUidActividad());
        auditoriaLineaProformaBeanKey.setIdProforma(idProforma);
        auditoriaLineaProformaBeanKey.setLinea(linea);
        auditoriaLineaProformaBeanKey.setUidAuditoria(uidAuditoria);

        AuditoriaLineaProformaBeanMapper auditoriaLineaProformaBeanMapper = sqlSession.getMapper(AuditoriaLineaProformaBeanMapper.class);
        AuditoriaLineaProformaBean auditoriaLineaProforma = auditoriaLineaProformaBeanMapper.selectByPrimaryKey(auditoriaLineaProformaBeanKey);
        if (auditoriaLineaProforma == null) {
            log.error("eliminar() - La auditoria de la proforma con id: {} no existe", idProforma);
            throw new AuditoriaLineaProformaNotFoundException(idProforma);
        }

        auditoriaLineaProformaBeanMapper.deleteByPrimaryKey(auditoriaLineaProformaBeanKey);
        log.debug("eliminar() - Auditoria de la proforma proforma eliminada: {}", idProforma);
    }


}
