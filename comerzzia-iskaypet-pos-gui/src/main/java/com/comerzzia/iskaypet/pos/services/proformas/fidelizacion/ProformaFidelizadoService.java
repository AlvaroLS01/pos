package com.comerzzia.iskaypet.pos.services.proformas.fidelizacion;

import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.persistence.proformas.fidelizacion.ProformaFidelizacionBeanExample;
import com.comerzzia.iskaypet.pos.persistence.proformas.fidelizacion.ProformaFidelizacionBeanKey;
import com.comerzzia.iskaypet.pos.persistence.proformas.fidelizacion.ProformaFidelizacionBeanMapper;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaFidelizadoBean;
import com.comerzzia.iskaypet.pos.services.proformas.fidelizacion.exception.ProformaFidelizadoAlreadyExistException;
import com.comerzzia.iskaypet.pos.services.proformas.fidelizacion.exception.ProformaFidelizadoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ProformaFidelizadoService {

    protected static ProformaFidelizadoService instance;

    public static ProformaFidelizadoService get() {
        if (instance == null) {
            instance = new ProformaFidelizadoService();
        }
        return instance;
    }

    public ProformaFidelizacionBeanKey consultar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma) {
        log.debug("consultar() - Consultando fidelizacion de proforma: {} de la actividad: {}", idProforma, datosSesion.getUidActividad());

        ParametroBuscarProformaFidelizadoBean param = new ParametroBuscarProformaFidelizadoBean();
        param.setUidActividad(datosSesion.getUidActividad());
        param.setIdProforma(idProforma);

        List<ProformaFidelizacionBeanKey> proformaFidelizados = consultar(sqlSession, datosSesion, param);
        if (proformaFidelizados == null || proformaFidelizados.isEmpty()) {
            log.error("consultar() - No se han encontrado proformaFidelizado para la proforma: {} de la actividad: {}", idProforma, datosSesion.getUidActividad());
            return null;
        }

        ProformaFidelizacionBeanKey proformaFidelizacion = proformaFidelizados.get(0);
        log.debug("consultar() - Proforma fidelizado encontrado: {}", proformaFidelizacion);
        return proformaFidelizacion;
    }

    public List<ProformaFidelizacionBeanKey> consultar(SqlSession sqlSession, DatosSesionBean datosSesion) {
        log.debug("consultar() - Consultando todas las proforma fidelizados de la actividad: {}", datosSesion.getUidActividad());

        ProformaFidelizacionBeanExample example = new ProformaFidelizacionBeanExample();
        example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        ProformaFidelizacionBeanMapper proformaFidelizacionBeanMapper = sqlSession.getMapper(ProformaFidelizacionBeanMapper.class);
        List<ProformaFidelizacionBeanKey> proformaFidelizados = proformaFidelizacionBeanMapper.selectByExample(example);
        if (proformaFidelizados == null || proformaFidelizados.isEmpty()) {
            log.error("consultar() - No se han encontrado proformaFidelizados para la actividad: {}", datosSesion.getUidActividad());
            return Collections.emptyList();
        }

        log.debug("consultar() - Proforma fidelizados encontradas: {}", proformaFidelizados.size());
        return proformaFidelizados;
    }


    public List<ProformaFidelizacionBeanKey> consultar(SqlSession sqlSession, DatosSesionBean datosSesion, ParametroBuscarProformaFidelizadoBean parametroBuscarProformaFidelizado) {
        log.debug("consultar() - Consultando proformaFidelizados de la actividad: {} con los parametros: {}", datosSesion.getUidActividad(), parametroBuscarProformaFidelizado);

        ProformaFidelizacionBeanExample example = new ProformaFidelizacionBeanExample();
        ProformaFidelizacionBeanExample.Criteria criteria = example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        if (StringUtils.isNotBlank(parametroBuscarProformaFidelizado.getUidActividad())) {
            criteria.andUidActividadEqualTo(parametroBuscarProformaFidelizado.getUidActividad());
        }

        if (StringUtils.isNotBlank(parametroBuscarProformaFidelizado.getUidInstancia())) {
            criteria.andUidInstanciaEqualTo(parametroBuscarProformaFidelizado.getUidInstancia());
        }

        if (StringUtils.isNotBlank(parametroBuscarProformaFidelizado.getIdProforma())) {
            criteria.andIdProformaEqualTo(parametroBuscarProformaFidelizado.getIdProforma());
        }

        if (parametroBuscarProformaFidelizado.getIdFidelizado() != null) {
            criteria.andIdFidelizadoEqualTo(parametroBuscarProformaFidelizado.getIdFidelizado());
        }


        ProformaFidelizacionBeanMapper proformaFidelizacionBeanKeyMapper = sqlSession.getMapper(ProformaFidelizacionBeanMapper.class);
        List<ProformaFidelizacionBeanKey> proformaFidelizados = proformaFidelizacionBeanKeyMapper.selectByExample(example);
        log.debug("consultar() - ProformaFidelizados encontradas: {}", proformaFidelizados.size());
        return proformaFidelizados;
    }


    public ProformaFidelizacionBeanKey crear(SqlSession sqlSession, DatosSesionBean datosSesion, ProformaFidelizacionBeanKey proformaFidelizado) throws ProformaFidelizadoAlreadyExistException {
        log.debug("crear() - Creando proformaFidelizado: {}", proformaFidelizado);

        ProformaFidelizacionBeanMapper proformaFidelizacionBeanKeyMapper = sqlSession.getMapper(ProformaFidelizacionBeanMapper.class);
        try {
            proformaFidelizacionBeanKeyMapper.insert(proformaFidelizado);
        } catch (PersistenceException e) {
            log.error("crear() - Error al crear la proformaFidelizado: {}", e.getMessage());
            throw new ProformaFidelizadoAlreadyExistException("Error al crear la proforma fidelizado, ya existe");
        }

        log.debug("crear() - ProformaFidelizado creada: {}", proformaFidelizado);
        return proformaFidelizado;
    }


    public void eliminar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma, Long idFidelizado) throws ProformaFidelizadoNotFoundException {
        log.debug("eliminar() - Eliminando proforma fidelizado con idProforma: {} y con idFidelizado", idProforma, idFidelizado);

        ProformaFidelizacionBeanKey proformaFidelizacionBeanKey = new ProformaFidelizacionBeanKey();
        proformaFidelizacionBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaFidelizacionBeanKey.setIdProforma(idProforma);
        proformaFidelizacionBeanKey.setUidInstancia(datosSesion.getUidInstancia());
        proformaFidelizacionBeanKey.setIdFidelizado(idFidelizado);

        ProformaFidelizacionBeanMapper proformaFidelizacionBeanKeyMapper = sqlSession.getMapper(ProformaFidelizacionBeanMapper.class);
        int deleted = proformaFidelizacionBeanKeyMapper.deleteByPrimaryKey(proformaFidelizacionBeanKey);
        if (deleted == 0) {
            log.error("eliminar() - La proforma fidelizado con idProforma: {}, e idFidelizado: {} no existe", idProforma, idFidelizado);
            throw new ProformaFidelizadoNotFoundException("Error al eliminar la proforma fidelizado, no existe");
        }
        log.debug("eliminar() - ProformaFidelizado eliminada: {}", proformaFidelizacionBeanKey);
    }


}
