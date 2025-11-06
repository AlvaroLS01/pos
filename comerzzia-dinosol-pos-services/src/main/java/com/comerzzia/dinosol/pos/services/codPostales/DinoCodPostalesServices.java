package com.comerzzia.dinosol.pos.services.codPostales;

import java.util.List;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.codPostales.CodigoPostalBean;
import com.comerzzia.pos.persistence.codPostales.CodigoPostalExample;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.codPostales.CodPostalesException;
import com.comerzzia.pos.services.codPostales.CodPostalesServices;
import com.comerzzia.pos.util.i18n.I18N;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@SuppressWarnings("deprecation")
@Service
@Primary
public class DinoCodPostalesServices extends CodPostalesServices {

	protected static final Logger log = Logger.getLogger(DinoCodPostalesServices.class);

	public String buscaProvinciaPorCP(String cp) throws CodPostalesException {
		log.debug("buscaProvinciaPorCP() - Buscando provincias a partir de los dos primeros dígitos del código postal: " + cp);

		SqlSession sqlSession = new SqlSession();

		try {
			String primerosDosDigitosCP = cp.substring(0, 2);

			sqlSession.openSession(SessionFactory.openSession());
			CodigoPostalExample example = new CodigoPostalExample();
			example.or().andUidInstanciaEqualTo(sesion.getAplicacion().getUidInstancia()).andCpLike(primerosDosDigitosCP + "%");

			CodigoPostalExample.Criteria criteria = example.createCriteria();
			criteria.andCpLike(primerosDosDigitosCP + "%");
			criteria.andUidInstanciaEqualTo(sesion.getAplicacion().getUidInstancia());

			List<CodigoPostalBean> selectByExample = codigoPostalMapper.selectByExample(example);

			if (!selectByExample.isEmpty()) {
				return selectByExample.get(0).getProvincia();
			}
			else {
				throw new CodPostalesException(I18N.getTexto("Error cargando la provincia a partir del código postal: " + cp));
			}
		}
		catch (Exception e) {
			log.error("buscaProvinciaPorCP() - Se ha producido un error consultando el codigo postal: " + e.getMessage(), e);
			throw new CodPostalesException(I18N.getTexto("Error cargando el código postal"));
		}
		finally {
			sqlSession.close();
		}
	}

}
