package com.comerzzia.iskaypet.pos.services.lenguajes;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje.LenguajeNotFoundException;
import com.comerzzia.iskaypet.pos.persistence.lenguajes.LenguajeBean;
import com.comerzzia.iskaypet.pos.persistence.lenguajes.LenguajeExample;
import com.comerzzia.iskaypet.pos.persistence.lenguajes.LenguajeExample.Criteria;
import com.comerzzia.iskaypet.pos.persistence.lenguajes.LenguajeKey;
import com.comerzzia.iskaypet.pos.persistence.lenguajes.LenguajeMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class LenguajesService {
	
	private static final Logger log = Logger.getLogger(LenguajesService.class);
	
	@Autowired
	protected Sesion sesion;
	@Autowired
	protected LenguajeMapper mapper;
	
	public List<LenguajeBean> getLenguajesByParams(String codLenguaje, String desLenguaje) throws Exception {
		try{
			LenguajeExample example = new LenguajeExample();
			Criteria criteria = example.createCriteria();
			criteria.andUidInstanciaEqualTo(sesion.getAplicacion().getUidInstancia()).andActivoEqualTo("S");
			if (StringUtils.isNotBlank(codLenguaje)) {
				criteria.andCodlenguaLikeInsensitive(codLenguaje);
			}
			if (StringUtils.isNotBlank(desLenguaje)) {
				criteria.andDeslenguaLikeInsensitive("%"+desLenguaje+"%");
			}
			example.setOrderByClause(LenguajeExample.ORDER_BY_DESLENGUA);
			return mapper.selectByExample(example);
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar los idiomas : " + e.getMessage());
			log.error("getLenguajes() - " + msgError, e);
			throw new Exception(msgError, e);
		}
	}

	public LenguajeBean getLenguajeByCodLengua(String codLengua) throws LenguajeNotFoundException, Exception {
		try{
			if (StringUtils.isNotBlank(codLengua)) {
				LenguajeKey key = new LenguajeKey();
				key.setUidInstancia(sesion.getAplicacion().getUidInstancia());
				key.setCodlengua(codLengua);
				LenguajeBean leng = mapper.selectByPrimaryKey(key);
				if (leng == null) {
					throw new LenguajeNotFoundException("No se ha encontrado ningún idioma con el código " + codLengua);
				}
				return leng;
			}
		} catch(LenguajeNotFoundException e){
				throw new LenguajeNotFoundException(e.getMessage(), e);
		} catch(Exception e){
			String msgError = I18N.getTexto("Error al consultar el idioma con código " + codLengua + " : " + e.getMessage());
			log.error("getLenguajeByCodLengua() - " + msgError, e);
			throw new Exception(msgError, e);
		}
		return null;
	}
	
}
