package com.comerzzia.api.loyalty.service.contactsTypes;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.loyalty.persistence.contactsTypes.ParametrosBuscarTiposContactoBean;
import com.comerzzia.api.loyalty.persistence.contactsTypes.TipoContactoBean;
import com.comerzzia.api.loyalty.persistence.contactsTypes.TipoContactoExample;
import com.comerzzia.api.loyalty.persistence.contactsTypes.TipoContactoExample.Criteria;
import com.comerzzia.api.loyalty.persistence.contactsTypes.TipoContactoKey;
import com.comerzzia.api.loyalty.persistence.contactsTypes.TipoContactoMapper;
import com.comerzzia.core.model.empresas.ConfigEmpresaBean;
import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.core.servicios.i18n.I18NService;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.paginacion.PaginaResultados;

@Service
public class ContactsTypesServiceImpl implements ContactsTypesService {
	protected static Logger log = Logger.getLogger(ContactsTypesServiceImpl.class);

	protected static ContactsTypesService instance;

	@Autowired
	TipoContactoMapper mapper;
	
	@Autowired
	I18NService i18NService;

	@Deprecated
	public static ContactsTypesService get() {
		if (instance != null) {
		   return instance;
		} else { 
   		   return (ContactsTypesService) ContextHolder.get().getBean("ContactsTypesService");
		}
	}

	public TipoContactoBean consultar(String codtipocon, DatosSesionBean datosSesion)
			throws TipoContactoException, TipoContactoNotFoundException {
		log.debug("consultar() - Consultando datos del tipo de contacto: " + codtipocon);
		TipoContactoKey key = new TipoContactoKey();
		key.setUidInstancia(datosSesion.getConfigEmpresa().getUidInstancia());
		key.setCodTipoCon(codtipocon);
		TipoContactoBean tipo = mapper.selectFromViewByPrimaryKey(key);

		if (tipo == null) {
			String msg = "No se ha encontrado el tipo de contacto con identificador: " + codtipocon;
			log.info("consultar() - " + msg);
			throw new TipoContactoNotFoundException(msg);
		}

		return tipo;
	}

	@Transactional(rollbackFor = Exception.class)
	public void salvar(TipoContactoBean tipoContacto, DatosSesionBean datosSesion)
			throws TipoContactoException, TipoContactoConstraintViolationException {

		switch (tipoContacto.getEstadoBean()) {
		case Estado.NUEVO:
			crear(tipoContacto, datosSesion);
			break;

		case Estado.MODIFICADO:
			modificar(tipoContacto, datosSesion);
			break;

		case Estado.BORRADO:
			eliminar(tipoContacto.getCodTipoCon(), datosSesion);
		}
	}

	protected void crear(TipoContactoBean tipoContacto, DatosSesionBean datosSesion)
			throws TipoContactoException, TipoContactoConstraintViolationException {
		try {
			log.debug("crear() - Creando tipo de contacto " + tipoContacto.getCodTipoCon());
			tipoContacto.setUidInstancia(datosSesion.getConfigEmpresa().getUidInstancia());
			mapper.insert(tipoContacto);
			i18NService.insertUpdateDelete(tipoContacto, tipoContacto.getCodTipoCon(), datosSesion);			
		} catch (Exception e) {
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				String msg = "Ya existe el tipo de contacto " + tipoContacto.getCodTipoCon();
				throw new TipoContactoConstraintViolationException(msg, e);
			} else {
				log.error("crear() - " + e.getMessage());
				String mensaje = "Error al salvar el tipo de contacto: " + e.getMessage();
				throw new TipoContactoException(mensaje, e);
			}
		}

	}

	protected void eliminar(String codtipocon, DatosSesionBean datosSesion)
			throws TipoContactoException, TipoContactoConstraintViolationException {
		try {
			log.debug("eliminar() - Eliminando tipo de contacto " + codtipocon);
			TipoContactoKey key = new TipoContactoKey();
			key.setUidInstancia(datosSesion.getConfigEmpresa().getUidInstancia());
			key.setCodTipoCon(codtipocon);

			mapper.deleteByPrimaryKey(key);
			
			i18NService.deleteByObjectId(TipoContactoBean.CLASE_TIPOCON.concat(".%"), codtipocon, datosSesion);
		} catch (Exception e) {
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				String msg = "Existen registros asociados al tipo de contacto " + codtipocon;
				throw new TipoContactoConstraintViolationException(msg, e);
			} else {
				String msg = "Error eliminando el tipo de contacto: " + e.getMessage();
				log.error("eliminar() - " + msg);
				throw new TipoContactoException(msg, e);
			}
		}
	}

	protected void modificar(TipoContactoBean tipoContacto, DatosSesionBean datosSesion) throws TipoContactoException {
		try {
			log.debug("modificar() - Modificando tipo de contacto ");
			tipoContacto.setUidInstancia(datosSesion.getConfigEmpresa().getUidInstancia());
			mapper.updateByPrimaryKey(tipoContacto);
			i18NService.insertUpdateDelete(tipoContacto, tipoContacto.getCodTipoCon(), datosSesion);
		} catch (Exception e) {
			String msg = "Error modificando tipo de contacto: " + e.getMessage();
			log.error("modificar() - " + msg);

			throw new TipoContactoException(msg, e);
		}
	}

	public PaginaResultados consultar(ParametrosBuscarTiposContactoBean param, ConfigEmpresaBean config)
			throws TipoContactoException {
		log.debug("consultar() - Consultando tipos de contacto");

		TipoContactoExample tipoExample = new TipoContactoExample();
		Criteria criteria = tipoExample.or().andUidInstanciaEqualTo(config.getUidInstancia());

		// CODTIPOCON
		if (param.getCodtipocon() != null && !param.getCodtipocon().isEmpty()) {
			criteria.andCodTipoConEqualTo(param.getCodtipocon());
		}

		// DESTIPOCON
		if (param.getDestipocon() != null && !param.getDestipocon().isEmpty()) {
			criteria.andDesTipoConLikeInsensitive("%" + param.getDestipocon() + "%");
		}

		// ACTIVO
		if (!param.getActivo().isEmpty()) {
			if ("S".equals(param.getActivo())) {
				criteria.andActivoEqualTo(Boolean.TRUE);
			} else if ("N".equals(param.getActivo())) {
				criteria.andActivoEqualTo(Boolean.FALSE);
			}
		}

		if (!param.getOrden().isEmpty()) {
			tipoExample.setOrderByClause(param.getOrden());
		}

		List<TipoContactoBean> resultados = new ArrayList<TipoContactoBean>();
		PaginaResultados paginaResultados = new PaginaResultados(param, resultados);

		List<TipoContactoBean> tiposContacto = mapper.selectFromViewByExample(tipoExample);

		int fromIndex = (paginaResultados.getInicio() - 1);
		int toIndex = (paginaResultados.getInicio() + paginaResultados.getTamañoPagina() - 1);
		if (toIndex > tiposContacto.size()) {
			toIndex = tiposContacto.size();
		}

		resultados.addAll(tiposContacto.subList(fromIndex, toIndex));
		paginaResultados.setTotalResultados(tiposContacto.size());

		return paginaResultados;
	}


}
