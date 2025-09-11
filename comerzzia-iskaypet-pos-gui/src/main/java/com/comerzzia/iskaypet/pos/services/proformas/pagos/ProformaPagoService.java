package com.comerzzia.iskaypet.pos.services.proformas.pagos;

import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaPagoBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.pagos.ProformaPagoBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.pagos.ProformaPagoBeanExample;
import com.comerzzia.iskaypet.pos.persistence.proformas.pagos.ProformaPagoBeanKey;
import com.comerzzia.iskaypet.pos.persistence.proformas.pagos.ProformaPagoBeanMapper;
import com.comerzzia.iskaypet.pos.services.proformas.pagos.exception.ProformaPagoAlreadyExistException;
import com.comerzzia.iskaypet.pos.services.proformas.pagos.exception.ProformaPagoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ProformaPagoService {

    protected static ProformaPagoService instance;

    public static ProformaPagoService get() {
        if (instance == null) {
            instance = new ProformaPagoService();
        }
        return instance;
    }


    public ProformaPagoBean consultar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma, Integer pago) throws ProformaPagoNotFoundException {
        log.debug("consultar() - Consultando proforma con id: {} y pago: {}", idProforma, pago);

        ProformaPagoBeanKey proformaPagoBeanKey = new ProformaPagoBeanKey();
        proformaPagoBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaPagoBeanKey.setIdProforma(idProforma);
        proformaPagoBeanKey.setLinea(pago);

        ProformaPagoBeanMapper proformaPagoBeanMapper = sqlSession.getMapper(ProformaPagoBeanMapper.class);
        ProformaPagoBean proforma = proformaPagoBeanMapper.selectByPrimaryKey(proformaPagoBeanKey);
        if (proforma == null) {
            log.error("consultar() - No se ha encontrado la pago de la proforma con id: {} y pago: {}", idProforma, pago);
            throw new ProformaPagoNotFoundException(idProforma);
        }

        log.debug("consultar() - Pago proforma encontrada: {}", proforma);
        return proforma;
    }


    public List<ProformaPagoBean> consultar(SqlSession sqlSession, DatosSesionBean datosSesion) {
        log.debug("consultar() - Consultando todas las lines de proformas de la actividad: {}", datosSesion.getUidActividad());

        ProformaPagoBeanExample example = new ProformaPagoBeanExample();
        example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        ProformaPagoBeanMapper proformaPagoBeanMapper = sqlSession.getMapper(ProformaPagoBeanMapper.class);
        List<ProformaPagoBean> proformas = proformaPagoBeanMapper.selectByExample(example);
        if (proformas == null || proformas.isEmpty()) {
            log.error("consultar() - No se han encontrado pagos de proformas para la actividad: {}", datosSesion.getUidActividad());
            return Collections.emptyList();
        }

        log.debug("consultar() - Pagos proformas encontradas: {}", proformas.size());
        return proformas;
    }


    public List<ProformaPagoBean> consultar(SqlSession sqlSession, DatosSesionBean datosSesion, ParametroBuscarProformaPagoBean parametroBuscarProforma) {
        log.debug("consultar() - Consultando pagos proformas de la actividad: {} con los parametros: {}", datosSesion.getUidActividad(), parametroBuscarProforma);

        ProformaPagoBeanExample example = new ProformaPagoBeanExample();
        ProformaPagoBeanExample.Criteria criteria = example.or().andUidActividadEqualTo(datosSesion.getUidActividad());

        if (StringUtils.isNotBlank(parametroBuscarProforma.getIdProforma())) {
            criteria.andIdProformaEqualTo(parametroBuscarProforma.getIdProforma());
        }

        if (StringUtils.isNotBlank(parametroBuscarProforma.getUidActividad())) {
            criteria.andUidActividadEqualTo(parametroBuscarProforma.getUidActividad());
        }


        ProformaPagoBeanMapper proformaPagoBeanMapper = sqlSession.getMapper(ProformaPagoBeanMapper.class);
        List<ProformaPagoBean> proformas = proformaPagoBeanMapper.selectByExample(example);
        log.debug("consultar() - Pagos proformas encontradas: {}", proformas.size());
        return proformas;
    }


    public ProformaPagoBean crear(SqlSession sqlSession, DatosSesionBean datosSesion, ProformaPagoBean pago) throws ProformaPagoAlreadyExistException {
        log.debug("crear() - Creando pago proforma: {}", pago);

        ProformaPagoBeanKey proformaPagoBeanKey = new ProformaPagoBeanKey();
        proformaPagoBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaPagoBeanKey.setIdProforma(pago.getIdProforma());
        proformaPagoBeanKey.setLinea(pago.getLinea());

        ProformaPagoBeanMapper proformaPagoBeanMapper = sqlSession.getMapper(ProformaPagoBeanMapper.class);
        ProformaPagoBean proformaExistente = proformaPagoBeanMapper.selectByPrimaryKey(proformaPagoBeanKey);
        if (proformaExistente != null) {
            log.error("crear() - La pago de la proforma con id: {} y pago: {} ya existe", pago.getIdProforma(), pago.getLinea());
            throw new ProformaPagoAlreadyExistException(pago.getIdProforma());
        }

        proformaPagoBeanMapper.insert(pago);
        log.debug("crear() - Pago proforma creada: {}", pago);
        return pago;
    }


    public ProformaPagoBean modificar(SqlSession sqlSession, DatosSesionBean datosSesion, ProformaPagoBean pago) throws ProformaPagoNotFoundException {
        log.debug("modificar() - Modificando pago proforma: {}", pago);

        ProformaPagoBeanKey proformaPagoBeanKey = new ProformaPagoBeanKey();
        proformaPagoBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaPagoBeanKey.setIdProforma(pago.getIdProforma());

        ProformaPagoBeanMapper proformaPagoBeanMapper = sqlSession.getMapper(ProformaPagoBeanMapper.class);
        ProformaPagoBean proformaExistente = proformaPagoBeanMapper.selectByPrimaryKey(proformaPagoBeanKey);
        if (proformaExistente == null) {
            log.error("modificar() - La pago de la proforma con id: {} y pago: {} no existe", pago.getIdProforma(), pago.getLinea());
            throw new ProformaPagoNotFoundException(pago.getIdProforma());
        }

        proformaPagoBeanMapper.updateByPrimaryKey(pago);
        log.debug("modificar() - Pago proforma modificada: {}", pago);
        return pago;
    }


    public void eliminar(SqlSession sqlSession, DatosSesionBean datosSesion, String idProforma, Integer pago) throws ProformaPagoNotFoundException {
        log.debug("eliminar() - Eliminando pago proforma con id: {} y pago: {}", idProforma, pago);

        ProformaPagoBeanKey proformaPagoBeanKey = new ProformaPagoBeanKey();
        proformaPagoBeanKey.setUidActividad(datosSesion.getUidActividad());
        proformaPagoBeanKey.setIdProforma(idProforma);
        proformaPagoBeanKey.setLinea(pago);

        ProformaPagoBeanMapper proformaPagoBeanMapper = sqlSession.getMapper(ProformaPagoBeanMapper.class);
        ProformaPagoBean proformaPago = proformaPagoBeanMapper.selectByPrimaryKey(proformaPagoBeanKey);
        if (proformaPago == null) {
            log.error("eliminar() - La pago de la proforma con id: {} y pago: {} no existe", idProforma, pago);
            throw new ProformaPagoNotFoundException(idProforma);
        }

        proformaPagoBeanMapper.deleteByPrimaryKey(proformaPagoBeanKey);
        log.debug("eliminar() - Pago proforma eliminada: {}", idProforma);
    }


}
