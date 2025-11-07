package com.comerzzia.api.loyalty.service.collectivesTypes;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.loyalty.persistence.collectivesTypes.ParametrosBuscarTiposColectivosBean;
import com.comerzzia.api.loyalty.persistence.collectivesTypes.TipoColectivoBean;
import com.comerzzia.api.loyalty.persistence.collectivesTypes.TipoColectivoExample;
import com.comerzzia.api.loyalty.persistence.collectivesTypes.TipoColectivoExample.Criteria;
import com.comerzzia.api.loyalty.persistence.collectivesTypes.TipoColectivoKey;
import com.comerzzia.api.loyalty.persistence.collectivesTypes.TipoColectivoMapper;
import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.mybatis.exception.PersistenceExceptionFactory;
import com.comerzzia.core.util.paginacion.PaginaResultados;

@Service
public class CollectivesTypesServiceImpl implements CollectivesTypesService {

	protected static Logger log = Logger.getLogger(CollectivesTypesServiceImpl.class);

	protected static CollectivesTypesService instance;
	
	@Autowired
	TipoColectivoMapper mapper;

	@Deprecated
	public static CollectivesTypesService get() {
		if (instance != null) {
		   return instance;
		} else { 
   		   return (CollectivesTypesService) ContextHolder.get().getBean("CollectivesTypesService");
		}
	}

	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.collectivesTypes.CollectivesTypesService#consultar(com.comerzzia.api.loyalty.persistence.collectivesTypes.ParametrosBuscarTiposColectivosBean, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	public PaginaResultados consultar(ParametrosBuscarTiposColectivosBean param, IDatosSesion datosSesion) {
		log.debug("consultar() - Consultando tipos de colectivos");
		TipoColectivoExample tipoColectivoExample = new TipoColectivoExample();
		Criteria criteria = tipoColectivoExample.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia());
		
		// COD_COLECTIVO
		if (param.getCodtipcolectivo() != null && !param.getCodtipcolectivo().isEmpty()) {
			criteria.andCodtipcolectivoLikeInsensitive(param.getCodtipcolectivo() +"%");
		}
		// DES_COLECTIVO
		if (param.getDestipcolectivo() != null && !param.getDestipcolectivo().isEmpty()) {
			criteria.andDestipcolectivoLikeInsensitive(param.getDestipcolectivo() +"%");
		}
		//ACTIVO
		if (param.getActivo() != null && !param.getActivo().isEmpty()) {
			criteria.andActivoEqualTo(param.isActivo());
		}
		//PRIVADO
		if (param.getPrivado() != null && !param.getPrivado().isEmpty()) {
			criteria.andPrivadoEqualTo(param.getPrivado());
		}
		//ORDEN
		tipoColectivoExample.setOrderByClause(param.getOrden());
		
		List<TipoColectivoBean> resultados = new ArrayList<TipoColectivoBean>();
		PaginaResultados paginaResultados = new PaginaResultados(param, resultados);
		
		List<TipoColectivoBean> tiposColectivos = mapper.selectByExample(tipoColectivoExample);
		int fromIndex = (paginaResultados.getInicio() - 1);
		int toIndex = (paginaResultados.getInicio() + paginaResultados.getTamañoPagina() - 1);
		if(toIndex > tiposColectivos.size()) {
			toIndex = tiposColectivos.size();
		}
	   
		resultados.addAll(tiposColectivos.subList(fromIndex, toIndex));
		paginaResultados.setTotalResultados(tiposColectivos.size());
	   
		return paginaResultados;
	}
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.collectivesTypes.CollectivesTypesService#consultar(java.lang.String, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	public TipoColectivoBean consultar(String codtipcolectivo, IDatosSesion datosSesion) throws TipoColectivoNotFoundException {
		TipoColectivoKey key = new TipoColectivoKey(datosSesion.getUidInstancia(), codtipcolectivo);
		TipoColectivoBean tipoColectivo = mapper.selectByPrimaryKey(key);

		if (codtipcolectivo == null) {
			String msg = "No se ha encontrado el tipo de colectivo con código: " + codtipcolectivo;
			log.info("consultar() - " + msg);
			throw new TipoColectivoNotFoundException(msg);
		}
		return tipoColectivo;
	}
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.collectivesTypes.CollectivesTypesService#salvar(com.comerzzia.api.loyalty.persistence.collectivesTypes.TipoColectivoBean, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void salvar(TipoColectivoBean tipoColectivo, IDatosSesion datosSesion) {
		switch (tipoColectivo.getEstadoBean()) {
		case Estado.NUEVO:
			crear(tipoColectivo, datosSesion);
			break;

		case Estado.MODIFICADO:
			modificar(tipoColectivo, datosSesion);
			break;

		case Estado.BORRADO:
			eliminar(tipoColectivo.getCodtipcolectivo(), datosSesion);
		}
	}

	protected void crear(TipoColectivoBean tipoColectivo, IDatosSesion datosSesion) {
		try {
			log.debug("crear() - Creando nuevo tipo de colectivo");

			tipoColectivo.setUidInstancia(datosSesion.getUidInstancia());
			mapper.insert(tipoColectivo);
		} 
		catch (PersistenceException e) {
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()) {
				String msg = "No se ha podido crear el colectivo. El código introducido ya está registrado para otro colectivo: " + e.getMessage();
				log.error("crear() - " + msg);
				throw new TipoColectivoConstraintViolationException(msg, e);
			}
		}
	}

	protected void modificar(TipoColectivoBean tipoColectivo, IDatosSesion datosSesion) {
		log.debug("modificar() - Modificando tipo de colectivo con Código " + tipoColectivo.getCodtipcolectivo());
		mapper.updateByPrimaryKey(tipoColectivo);
	}

	protected void eliminar(String colectivo, IDatosSesion datosSesion) {
		try {
			log.debug("eliminar() - Eliminando colectivo con Código " + colectivo);
			
			TipoColectivoKey key = new TipoColectivoKey(datosSesion.getUidInstancia(), colectivo);
			mapper.deleteByPrimaryKey(key);			
		} 
		catch(PersistenceException e) {
			String msg = "No se ha podido eliminar el colectivo: " + colectivo ;
			log.error("eliminar() -"+ msg+" : " + e.getMessage());
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isForeingKeyConstraintViolationException()) {
				throw new TipoColectivoConstraintViolationException(msg, e);
			}
		}				 
	}
}
