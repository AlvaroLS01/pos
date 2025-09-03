package com.comerzzia.iskaypet.pos.services.ventas.cajas.apuntes;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS.MovimientoEyS;
import com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS.MovimientoEySExample;
import com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS.MovimientoEySMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@SuppressWarnings("deprecation")
@Component
@Service
public class IskaypetApuntesService {

	private static final Logger log = Logger.getLogger(IskaypetApuntesService.class.getName());

	@Autowired
	protected MovimientoEySMapper mapperEyS;

	public void registrarMovimientoEyS(MovimientoEyS movimiento) throws Exception {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			mapperEyS.insert(movimiento);
			sqlSession.commit();
		}
		catch (Exception e) {
			String msgError = "registrarMovimientoEyS() -  Error registrando el movimiento en bbdd. ";
			log.error(msgError + e.getMessage());
			throw new Exception(msgError, e);
		}finally {
			sqlSession.close();
		}

	}

	public List<MovimientoEyS> consultarMovmientoCajaEyS(String uidActividad, String uidDiarioCaja) throws Exception {

		try {
			MovimientoEySExample example = new MovimientoEySExample();
			example.or().andUidActividadEqualTo(uidActividad).andUidDiarioCajaEqualTo(uidDiarioCaja);
			return mapperEyS.selectByExample(example);
		}
		catch (Exception e) {
			String msgError = "Error consultando los movimiento en bbdd. ";
			log.error("consultarMovmientoCajaEyS() - " +msgError + e.getMessage());
			throw new Exception(msgError, e);
		}

	}

}
