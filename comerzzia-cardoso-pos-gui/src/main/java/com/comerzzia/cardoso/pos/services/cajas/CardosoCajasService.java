package com.comerzzia.cardoso.pos.services.cajas;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.cajas.CajaBean;
import com.comerzzia.pos.persistence.cajas.CajaExample;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.util.i18n.I18N;

/**
 * GAP - PERSONALIZACIONES V3 - DÍAS ENTRE APERTURA CAJA
 */
@SuppressWarnings({ "deprecation", "static-access" })
@Primary
@Component
public class CardosoCajasService extends CajasService{

	/**
	 * Consulta la última caja abierta para el proceso de "DÍAS ENTRE APERTURA CAJA".
	 * TABLA : D_CAJA_CAB_TBL
	 * @return Caja
	 * @throws CajasServiceException
	 * @throws CajaEstadoException
	 */
	public Caja consultarUltimaCajaAbierta() throws CajasServiceException, CajaEstadoException{
		log.debug("consultarUltimaCajaAbierta() : GAP - PERSONALIZACIONES V3 - DÍAS ENTRE APERTURA CAJA");
		
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			CajaExample exampleCaja = new CajaExample();
			exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
			        .andCodcajaEqualTo(sesion.getAplicacion().getCodCaja()).andFechaAperturaIsNotNull();
			exampleCaja.setOrderByClause(exampleCaja.ORDER_BY_FECHA_APERTURA_DESC);
			log.debug("consultarUltimaCajaAbierta() - Consultado última caja abierta en sesion");
			List<CajaBean> cajasBean = cajaMapper.selectByExample(exampleCaja);

			if(cajasBean.isEmpty()){
				throw new CajaEstadoException(I18N.getTexto("No existen cajas abiertas en el sistema"));
			}
			return new Caja(cajasBean.get(0));
		}
		catch(CajaEstadoException e){
			throw e;
		}
		catch(Exception e){
			String msg = "Se ha producido un error consultando caja cerrada en sesion :" + e.getMessage();
			log.error("consultarUltimaCajaAbierta() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar caja abierta en sesión del sistema"), e);
		}
		finally{
			sqlSession.close();
		}
	}

}