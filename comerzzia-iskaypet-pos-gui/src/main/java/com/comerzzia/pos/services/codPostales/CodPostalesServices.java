/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.services.codPostales;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.codPostales.CodigoPostalBean;
import com.comerzzia.pos.persistence.codPostales.CodigoPostalExample;
import com.comerzzia.pos.persistence.codPostales.CodigoPostalMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class CodPostalesServices {
	
	protected static final Logger log = Logger.getLogger(CodPostalesServices.class);
	
	@Autowired
	protected Sesion sesion;
	
	@Autowired
	protected CodigoPostalMapper codigoPostalMapper;

	/**
	 * Devuelve una lista de objetos código postal que coincidan con el código que se pasa por parámetro
	 * @param codPostal
	 * @return List<CodigoPostalBean>
	 * @throws CodPostalesException
	 */
	public List<CodigoPostalBean> obtieneCodigoPostal(String codPostal) throws CodPostalesException {
		SqlSession sqlSession = new SqlSession();
		
        try {
            sqlSession.openSession(SessionFactory.openSession());
            CodigoPostalExample example = new CodigoPostalExample();
            CodigoPostalExample.Criteria criteria = example.createCriteria();
            criteria.andCpEqualTo(codPostal);
            criteria.andUidInstanciaEqualTo(sesion.getAplicacion().getUidActividad());
            
            return codigoPostalMapper.selectByExample(example);
        }
		catch (Exception e) {
			String msg = "Se ha producido un error consultando el codigo postal: " + e.getMessage();
			log.error("obtieneCodigoPostal() - " + msg, e);
			throw new CodPostalesException(I18N.getTexto("Error cargando el código postal"));
		}
        finally {
            sqlSession.close();
        }
	}
	
	/**
	 * Realiza una búsqueda de códigos postales a través del objeto pasado por parámtro.
	 * Este método relaiza una búsqueda con LIKE en todos los campos excepto en el orden, donde busca con EQUALS.
	 * @param codigoPostalBusqueda
	 * @return
	 * @throws CodPostalesException
	 */
	public List<CodigoPostalBean> buscaCodigosPostales(CodigoPostalBean busqueda) throws CodPostalesException {
		SqlSession sqlSession = new SqlSession();
		
        try {
            sqlSession.openSession(SessionFactory.openSession());
            CodigoPostalExample example = new CodigoPostalExample();
            CodigoPostalExample.Criteria criteria = example.createCriteria();

            if(busqueda.getCodPais() != null && !busqueda.getCodPais().equals("")) {
            	criteria.andCodPaisLikeInsensitive("%" + busqueda.getCodPais().toUpperCase() + "%");
            }
            
            if(busqueda.getCp() != null && !busqueda.getCp().equals("")) {
            	criteria.andCpLikeInsensitive("%" + busqueda.getCp().toUpperCase() + "%");
            }
            
            if(busqueda.getProvincia() != null && !busqueda.getProvincia().equals("")) {
            	criteria.andProvinciaLikeInsensitive("%" + busqueda.getProvincia().toUpperCase() + "%");
            }
            
            if(busqueda.getLocalidad() != null && !busqueda.getLocalidad().equals("")) {
            	criteria.andLocalidadLikeInsensitive("%" + busqueda.getLocalidad().toUpperCase() + "%");
            }
            
            if(busqueda.getPoblacion() != null && !busqueda.getPoblacion().equals("")) {
            	criteria.andPoblacionLikeInsensitive("%" + busqueda.getPoblacion().toUpperCase() + "%");
            }
            
            if(busqueda.getOrden() != null) {
            	criteria.andOrdenEqualTo(busqueda.getOrden());
            }
            
            criteria.andUidInstanciaEqualTo(sesion.getAplicacion().getUidActividad());
            
            return codigoPostalMapper.selectByExample(example);
        }
		catch (Exception e) {
			String msg = "Se ha producido un error consultando el codigo postal: " + e.getMessage();
			log.error("obtieneCodigoPostal() - " + msg, e);
			throw new CodPostalesException(I18N.getTexto("Error cargando el código postal"));
		}
        finally {
            sqlSession.close();
        }
	}

}
