package com.comerzzia.bimbaylola.pos.services.articulos.tarifas;

import java.util.Date;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaServiceException;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;

@Component
@Primary
public class ByLArticulosTarifaService extends ArticulosTarifaService{

	/**
	 * Obtiene la tarifa del artículo indicado
	 *
	 * @param codigoArticulo
	 * @param codTarifa
	 * @return
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaServiceException
	 */
	public TarifaDetalleBean consultarArticuloTarifa(String codigoArticulo,
				String codTarifa, String desglose1, String desglose2, Date vigencia)
						throws ArticuloTarifaNotFoundException, ArticuloTarifaServiceException{
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			return consultarArticuloTarifa(sqlSession, codigoArticulo, 
					codTarifa, desglose1, desglose2, vigencia);
		}
		finally {
			sqlSession.close();
		}
	}
	
}
