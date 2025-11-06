package com.comerzzia.dinosol.pos.services.core.documentos.propiedades;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoExample;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.documentos.propiedades.PropiedadesDocumentoService;

@SuppressWarnings("deprecation")
@Component
@Primary
public class DinoPropiedadesDocumentoService extends PropiedadesDocumentoService{

	/**
	 * Devuelve un listado de propiedades para un tipo de documento específico.
	 * @param uidActividad
	 * @param tipoDocumento : ID del tipo de documento a consultar.
	 * @return propiedadesTipoDocumentos
	 */
    public List<PropiedadDocumentoBean> consultarPropiedadesTipoDocumento(String uidActividad, Long tipoDocumento) {
        SqlSession sqlSession =  new SqlSession();
        try{
            sqlSession.openSession(SessionFactory.openSession());
            log.debug("Iniciamos la consulta de propiedades para el tipo de documento : " + tipoDocumento);
            PropiedadDocumentoExample filtro = new PropiedadDocumentoExample();
            filtro.or().andUidActividadEqualTo(uidActividad).andIdTipoDocumentoEqualTo(tipoDocumento);
            filtro.setOrderByClause(PropiedadDocumentoExample.ORDER_BY_PROPIEDAD);

            List<PropiedadDocumentoBean> propiedadesTipoDocumentos = propiedadDocumentoMapper.selectByExample(filtro);
            log.debug("Finalizada consultad de propiedades"
            		+ ", la respuesta contiene " + propiedadesTipoDocumentos.size() + " propiedades");
            return propiedadesTipoDocumentos;
        }
        finally{
            sqlSession.close();
        }
    }
	
}
