package com.comerzzia.bimbaylola.pos.services.core.impuestos.porcentajes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.impuestos.porcentajes.ByLPorcentajeImpuesto;
import com.comerzzia.bimbaylola.pos.persistence.impuestos.porcentajes.ByLPorcentajeImpuestoExample;
import com.comerzzia.bimbaylola.pos.persistence.impuestos.porcentajes.ByLPorcentajeImpuestoMapper;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Service
public class PorcentajeImpuestosService {

	@Autowired
	protected Sesion sesion;
	@Autowired
	protected ByLPorcentajeImpuestoMapper porcentajeImpuestoMapper;

	public Map<String, ByLPorcentajeImpuesto> obtenerPorcentajes(Integer idGrupoImpuestos, Long idTratImpuestos) {
		SqlSession sqlSession = new SqlSession();
		sqlSession.openSession(SessionFactory.openSession());

		String uidActividad = sesion.getAplicacion().getUidActividad();
		ByLPorcentajeImpuestoExample example = new ByLPorcentajeImpuestoExample();
		example.or().andUidActividadEqualTo(uidActividad).andIdGrupoImpuestosEqualTo(idGrupoImpuestos).andIdTratImpuestosEqualTo(idTratImpuestos);
		List<ByLPorcentajeImpuesto> result = porcentajeImpuestoMapper.selectByExample(example);
		
		Map<String, ByLPorcentajeImpuesto> impuestos = new HashMap<String, ByLPorcentajeImpuesto>();
		for(int i=0;i<result.size();i++) {
			impuestos.put(result.get(i).getCodimp(), result.get(i)); // Los índices del Map coincidirán con el codImp correspondiente
		}
		
		return impuestos;
	}

}