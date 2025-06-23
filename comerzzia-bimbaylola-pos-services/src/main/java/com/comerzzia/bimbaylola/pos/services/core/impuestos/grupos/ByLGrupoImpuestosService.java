package com.comerzzia.bimbaylola.pos.services.core.impuestos.grupos;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.impuestos.grupos.GrupoImpuestoBean;
import com.comerzzia.pos.persistence.core.impuestos.grupos.GrupoImpuestoExample;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.impuestos.grupos.GrupoImpuestosNotFoundException;
import com.comerzzia.pos.services.core.impuestos.grupos.GrupoImpuestosService;
import com.comerzzia.pos.services.core.impuestos.grupos.GrupoImpuestosServiceException;

@Service
@Primary
public class ByLGrupoImpuestosService extends GrupoImpuestosService {

	public GrupoImpuestoBean consultarGrupoImpuestosID(int idGrupoImpuestos) throws GrupoImpuestosServiceException, GrupoImpuestosNotFoundException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			String uidActividad = sesion.getAplicacion().getUidActividad();

			GrupoImpuestoExample filtro = new GrupoImpuestoExample();
			filtro.or().andUidActividadEqualTo(uidActividad).andIdGrupoImpuestosEqualTo(idGrupoImpuestos);
			filtro.setOrderByClause(GrupoImpuestoExample.ORDER_BY_VIGENCIA_DESDE_DESC);

			List<GrupoImpuestoBean> gruposImp = grupoImpuestoMapper.selectByExample(filtro);

			if (gruposImp != null && !gruposImp.isEmpty()) {
				return gruposImp.get(0);
			}
			else {
				throw new GrupoImpuestosNotFoundException();
			}
		}
		catch (GrupoImpuestosNotFoundException e) {
			log.error("consultarGrupoImpuestosActual() - Grupo de impuestos no encontrado. ");
			throw new GrupoImpuestosNotFoundException();
		}
		finally {
			sqlSession.close();
		}
	}

	public GrupoImpuestoBean consultarGrupoImpuestosFecha(Date fecha) throws GrupoImpuestosServiceException, GrupoImpuestosNotFoundException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			String uidActividad = sesion.getAplicacion().getUidActividad();

			GrupoImpuestoExample filtro = new GrupoImpuestoExample();
			filtro.or().andUidActividadEqualTo(uidActividad);
			filtro.setOrderByClause(GrupoImpuestoExample.ORDER_BY_VIGENCIA_DESDE_DESC);

			List<GrupoImpuestoBean> gruposImp = grupoImpuestoMapper.selectByExample(filtro);

			if (gruposImp != null && !gruposImp.isEmpty()) {
				for (GrupoImpuestoBean grupoImp : gruposImp) {
					if (fecha.after(grupoImp.getVigenciaDesde())) {
						return grupoImp;
					}
				}
			}
			else {
				throw new GrupoImpuestosNotFoundException();
			}
		}
		catch (GrupoImpuestosNotFoundException e) {
			log.error("consultarGrupoImpuestosFecha() - Grupo de impuestos no encontrado. ");
			throw new GrupoImpuestosNotFoundException();
		}
		finally {
			sqlSession.close();
		}
		return null;
	}
}
