package com.comerzzia.api.loyalty.service.collectives;

import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.loyalty.persistence.collectives.ColectivoBean;
import com.comerzzia.api.loyalty.persistence.collectives.ColectivoExample;
import com.comerzzia.api.loyalty.persistence.collectives.ColectivoKey;
import com.comerzzia.api.loyalty.persistence.collectives.ColectivoMapper;
import com.comerzzia.api.loyalty.persistence.customers.LoyalCustomerMapper;
import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoExample;
import com.comerzzia.api.loyalty.service.customers.collectives.LoyalCustomerCollectivesService;
import com.comerzzia.core.servicios.i18n.I18NService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.mybatis.exception.PersistenceExceptionFactory;

@Service
public class CollectivesServiceImpl implements CollectivesService {	
    protected static Logger log = Logger.getLogger(CollectivesServiceImpl.class);

	
	@Autowired
	ColectivoMapper mapper;
	
	@Autowired
	LoyalCustomerMapper fidelizadoMapper;
	
	@Autowired
	LoyalCustomerCollectivesService customerCollectivesService;
			
	@Autowired
	I18NService i18NService;

	@Override
	public List<ColectivoBean> selectByExample(ColectivoExample example, IDatosSesion datosSesion){
		return mapper.selectByExample(example);
	}
		
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insert(ColectivoBean record, IDatosSesion datosSesion) {
		log.debug("crear() - Creando nuevo colectivo");

		try {
			record.setUidInstancia(datosSesion.getUidInstancia());
			mapper.insert(record);
			i18NService.insertUpdateDelete(record, record.getCodColectivo(), datosSesion);
		} 
		catch (PersistenceException e) {
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()) {
				String msg = "No se ha podido crear el colectivo. El código introducido ya está registrado para otro colectivo: " + e.getMessage();
				log.error("crear() - " + msg);
				throw new ColectivoConstraintViolationException(msg, e);
			}
			
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByPrimaryKey(ColectivoBean record, IDatosSesion datosSesion) {		
		mapper.updateByPrimaryKey(record);
		i18NService.insertUpdateDelete(record, record.getCodColectivo(), datosSesion);
	}	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByPrimaryKey(ColectivoKey key, IDatosSesion datosSesion) {
		try {
			log.debug("eliminar() - Eliminando colectivo con Código " + key.getCodColectivo());
			
			// Eliminar asignacion de fidelizados al colectivo
			ColectivosFidelizadoExample example  = new ColectivosFidelizadoExample();
			example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andCodColectivoEqualTo(key.getCodColectivo());
			
			customerCollectivesService.deleteByExample(example  , datosSesion);
			
			// Eliminar traducciones
			i18NService.deleteByObjectId(ColectivoBean.CLASE_COLECTIVO.concat(".%"), key.getCodColectivo(), datosSesion);
			
			mapper.deleteByPrimaryKey(key);
		} 
		catch(PersistenceException e) {
			String msg = "No se ha podido eliminar el colectivo: " + key.getCodColectivo() ;
			log.error("eliminar() -"+ msg+" : " + e.getMessage());
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isForeingKeyConstraintViolationException()) {
				throw new ColectivoConstraintViolationException(msg, e);
			}
			
		}		
	}	
	
}