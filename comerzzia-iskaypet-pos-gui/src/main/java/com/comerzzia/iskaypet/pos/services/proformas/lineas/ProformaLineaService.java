package com.comerzzia.iskaypet.pos.services.proformas.lineas;

import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaLineaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.ProformaLineaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.ProformaLineaBeanExample;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.ProformaLineaBeanKey;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.ProformaLineaBeanMapper;
import com.comerzzia.iskaypet.pos.services.proformas.lineas.exception.ProformaLineaAlreadyExistException;
import com.comerzzia.iskaypet.pos.services.proformas.lineas.exception.ProformaLineaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ProformaLineaService {

    protected static ProformaLineaService instance;

    public static ProformaLineaService get() {
        if (instance == null) {
            instance = new ProformaLineaService();
        }
        return instance;
    }

    public ProformaLineaBean consultar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma, Integer linea) throws ProformaLineaNotFoundException {
        log.debug("consultar() - Consultando proforma con id: {} y linea: {}", idProforma, linea);

        ProformaLineaBeanKey proformaLineaBeanKey = new ProformaLineaBeanKey();
        proformaLineaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaLineaBeanKey.setIdProforma(idProforma);
        proformaLineaBeanKey.setLinea(linea);

        ProformaLineaBeanMapper proformaLineaBeanMapper = sqlSession.getMapper(ProformaLineaBeanMapper.class);
        ProformaLineaBean proforma = proformaLineaBeanMapper.selectByPrimaryKey(proformaLineaBeanKey);
        if (proforma == null) {
            log.error("consultar() - No se ha encontrado la linea de la proforma con id: {} y linea: {}", idProforma, linea);
            throw new ProformaLineaNotFoundException(idProforma);
        }

        log.debug("consultar() - Linea proforma encontrada: {}", proforma);
        return proforma;
    }


    public List<ProformaLineaBean> consultar(SqlSession sqlSession, DatosSesionBean datosSesion) {
        log.debug("consultar() - Consultando todas las lines de proformas de la actividad: {}", datosSesion.getUidActividad());

        ProformaLineaBeanExample example = new ProformaLineaBeanExample();
        example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        ProformaLineaBeanMapper proformaLineaBeanMapper = sqlSession.getMapper(ProformaLineaBeanMapper.class);
        List<ProformaLineaBean> proformas = proformaLineaBeanMapper.selectByExample(example);
        if (proformas == null || proformas.isEmpty()) {
            log.error("consultar() - No se han encontrado lineas de proformas para la actividad: {}", datosSesion.getUidActividad());
            return Collections.emptyList();
        }

        log.debug("consultar() - Lineas proformas encontradas: {}", proformas.size());
        return proformas;
    }


    public List<ProformaLineaBean> consultar(SqlSession sqlSession, DatosSesionBean datosSesion, ParametroBuscarProformaLineaBean parametroBuscarProforma) {
        log.debug("consultar() - Consultando lineas proformas de la actividad: {} con los parametros: {}", datosSesion.getUidActividad(), parametroBuscarProforma);

        ProformaLineaBeanExample example = new ProformaLineaBeanExample();
        ProformaLineaBeanExample.Criteria criteria = example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        if (StringUtils.isNotBlank(parametroBuscarProforma.getIdProforma())) {
            criteria.andIdProformaEqualTo(parametroBuscarProforma.getIdProforma());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getUidActividad())) {
            criteria.andUidActividadEqualTo(parametroBuscarProforma.getUidActividad());
        }


        ProformaLineaBeanMapper proformaLineaBeanMapper = sqlSession.getMapper(ProformaLineaBeanMapper.class);
        List<ProformaLineaBean> proformas = proformaLineaBeanMapper.selectByExample(example);
        log.debug("consultar() - Lineas proformas encontradas: {}", proformas.size());
        return proformas;
    }


    public ProformaLineaBean crear(SqlSession sqlSession, DatosSesionBean datosSesion, ProformaLineaBean linea) throws ProformaLineaAlreadyExistException {
        log.debug("crear() - Creando linea proforma: {}", linea);

        ProformaLineaBeanKey proformaLineaBeanKey = new ProformaLineaBeanKey();
        proformaLineaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaLineaBeanKey.setIdProforma(linea.getIdProforma());
        proformaLineaBeanKey.setLinea(linea.getLinea());

        ProformaLineaBeanMapper proformaLineaBeanMapper = sqlSession.getMapper(ProformaLineaBeanMapper.class);
        ProformaLineaBean proformaExistente = proformaLineaBeanMapper.selectByPrimaryKey(proformaLineaBeanKey);
        if (proformaExistente != null) {
            log.error("crear() - La linea de la proforma con id: {} y linea: {} ya existe", linea.getIdProforma(), linea.getLinea());
            throw new ProformaLineaAlreadyExistException(linea.getIdProforma());
        }

        proformaLineaBeanMapper.insert(linea);
        log.debug("crear() - Linea proforma creada: {}", linea);
        return linea;
    }


    public ProformaLineaBean modificar(SqlSession sqlSession, DatosSesionBean datosSesion, ProformaLineaBean linea) throws ProformaLineaNotFoundException {
        log.debug("modificar() - Modificando linea proforma: {}", linea);

        ProformaLineaBeanKey proformaLineaBeanKey = new ProformaLineaBeanKey();
        proformaLineaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaLineaBeanKey.setIdProforma(linea.getIdProforma());

        ProformaLineaBeanMapper proformaLineaBeanMapper = sqlSession.getMapper(ProformaLineaBeanMapper.class);
        ProformaLineaBean proformaExistente = proformaLineaBeanMapper.selectByPrimaryKey(proformaLineaBeanKey);
        if (proformaExistente == null) {
            log.error("modificar() - La linea de la proforma con id: {} y linea: {} no existe", linea.getIdProforma(), linea.getLinea());
            throw new ProformaLineaNotFoundException(linea.getIdProforma());
        }

        proformaLineaBeanMapper.updateByPrimaryKey(linea);
        log.debug("modificar() - Linea proforma modificada: {}", linea);
        return linea;
    }


    public void eliminar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma, Integer linea) throws ProformaLineaNotFoundException {
        log.debug("eliminar() - Eliminando linea proforma con id: {} y linea: {}", idProforma, linea);

        ProformaLineaBeanKey proformaLineaBeanKey = new ProformaLineaBeanKey();
        proformaLineaBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaLineaBeanKey.setIdProforma(idProforma);
        proformaLineaBeanKey.setLinea(linea);

        ProformaLineaBeanMapper proformaLineaBeanMapper = sqlSession.getMapper(ProformaLineaBeanMapper.class);
        ProformaLineaBean proformaLinea = proformaLineaBeanMapper.selectByPrimaryKey(proformaLineaBeanKey);
        if (proformaLinea == null) {
            log.error("eliminar() - La linea de la proforma con id: {} no existe", idProforma);
            throw new ProformaLineaNotFoundException(idProforma);
        }

        proformaLineaBeanMapper.deleteByPrimaryKey(proformaLineaBeanKey);
        log.debug("eliminar() - Linea proforma eliminada: {}", idProforma);
    }


}
