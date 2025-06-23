package com.comerzzia.bimbaylola.pos.services.core.config.configContadores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLConfigContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLConfigContadorExample;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLConfigContadorMapper;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLParametrosBuscarConfigContadoresBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.paginacion.PaginaResultados;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.config.configContadores.ContadoresConfigException;
import com.comerzzia.pos.services.core.config.configContadores.ContadoresConfigNotFoundException;

@Component
@Primary
public class ByLServicioConfigContadoresImpl{

	
	protected static Logger log = Logger.getLogger(ByLServicioConfigContadoresImpl.class);

	private static ByLServicioConfigContadoresImpl instance;

	protected static Map<String, ByLConfigContadorBean> cacheConfigContadores = new HashMap<String, ByLConfigContadorBean>();
	
	@Autowired
	protected ByLConfigContadorMapper configContadorMapper;
	
	public static ByLServicioConfigContadoresImpl get(){
		if(instance == null){
			instance = new ByLServicioConfigContadoresImpl();
		}
		return instance;
	}
	
	public PaginaResultados consultar(ByLParametrosBuscarConfigContadoresBean param) throws ContadoresConfigException {
		SqlSession sqlSession = new SqlSession();
		PaginaResultados paginaResultados = null;
		try {
			sqlSession.openSession(SessionFactory.openSession());
			
			log.debug("consultar() - Consultando variables");
			
			List<ByLConfigContadorBean> resultados = new ArrayList<ByLConfigContadorBean>(param.getTamañoPagina());
			paginaResultados = new PaginaResultados(param, resultados);
			
			ByLConfigContadorExample filtro = obtenerFiltro(param);
			paginaResultados.setTotalResultados(configContadorMapper.countByExample(filtro));
			resultados.addAll(configContadorMapper.selectByExampleWithRowbounds(filtro, new RowBounds(paginaResultados.getInicio()-1, paginaResultados.getTamañoPagina())));
			
			return paginaResultados;
		}
		catch (Exception e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "consultar() - Error al consultar contadores: " + e.getMessage();
			throw new ContadoresConfigException(mensaje, e);
		}
		finally {
			sqlSession.close();
		}
	}
	
	protected ByLConfigContadorExample obtenerFiltro(ByLParametrosBuscarConfigContadoresBean param) {
		ByLConfigContadorExample filtro = new ByLConfigContadorExample();
		
		ByLConfigContadorExample.Criteria cirteria = filtro.or();
		
		// ID_CONTADOR
		if (!param.getIdContador().isEmpty()) {
			cirteria.andIdContadorEqualTo(param.getIdContador());
		}
	
		// DESCRIPCION
		if (!param.getDescripcion().isEmpty()) {
			cirteria.andDescripcionLikeInsensitive("%"+param.getDescripcion()+"%");
			
		}
		return filtro;
	}
	
	public ByLConfigContadorBean consultar(String idContador) throws ContadoresConfigException, ContadoresConfigNotFoundException {
		SqlSession sqlSession = new SqlSession();
		
		try {
			sqlSession.openSession(SessionFactory.openSession());
			ByLConfigContadorMapper contadorMapper = sqlSession.getMapper(ByLConfigContadorMapper.class);

			log.debug("consultar() - Consultando datos del contador: " + idContador);
			ByLConfigContadorBean configContador = contadorMapper.selectByPrimaryKey(idContador);

			if (configContador == null) {
				String msg = "No se ha encontrado el contador con identificador: " + idContador;
				log.info("consultar() - " + msg);
				throw new ContadoresConfigNotFoundException(msg);
			}

			return configContador;
		}
		catch (Exception e) {
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar datos de un contador: " + e.getMessage();
			if(e instanceof ContadoresConfigNotFoundException) {
				throw new ContadoresConfigNotFoundException(e.getMessage());
			}
			else
			{
				throw new ContadoresConfigException(mensaje, e);
			}
		}
		finally {
			sqlSession.close();
		}
	}
	

	
    public Map<String, ByLConfigContadorBean> getCacheConfigContadores() {
    	return cacheConfigContadores;
    }
}
