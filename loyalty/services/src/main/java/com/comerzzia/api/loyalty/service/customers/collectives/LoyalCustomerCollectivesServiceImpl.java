package com.comerzzia.api.loyalty.service.customers.collectives;

import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoKey;
import com.comerzzia.api.loyalty.persistence.customers.collectives.LoyalCustomerCollectivesMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.mybatis.exception.PersistenceExceptionFactory;

@Service
public class LoyalCustomerCollectivesServiceImpl implements LoyalCustomerCollectivesService {	
    protected static Logger log = Logger.getLogger(LoyalCustomerCollectivesServiceImpl.class);
		
	@Autowired
	LoyalCustomerCollectivesMapper mapper;
	
	@Override
	public List<ColectivosFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		ColectivosFidelizadoExample example = new ColectivosFidelizadoExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.selectByExample(example);
	}
	
	@Override
	public int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		ColectivosFidelizadoExample example = new ColectivosFidelizadoExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.deleteByExample(example);
	}
			
	@Override
	public List<ColectivosFidelizadoBean> selectByExample(final ColectivosFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.selectByExample(example);
	}
	
	@Override
	public ColectivosFidelizadoBean selectByPrimaryKey(final ColectivosFidelizadoKey key, IDatosSesion datosSesion) {
		return mapper.selectByPrimaryKey(key);
	}
	
	@Override
	public int deleteByExample(final ColectivosFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.deleteByExample(example);
	}	
		
	@Override
	public int deletetByPrimaryKey(final ColectivosFidelizadoKey key, IDatosSesion datosSesion) {
		return mapper.deleteByPrimaryKey(key);
	}	
	
	@Override
	public void insert(ColectivosFidelizadoBean colectivosFidelizadoBean, IDatosSesion datosSesion) {
		try {
			log.debug("crear() - Creando nuevo colectivo");
			colectivosFidelizadoBean.setUidInstancia(datosSesion.getUidInstancia());

			mapper.insert(colectivosFidelizadoBean);			
		} catch (PersistenceException e) {
			String msg = "Error creando el colectivo: " + e.getMessage();
			log.error("crear() - " + msg);
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()){
				throw new ColectivoConstraintViolationException(msg, e);
			}
		}
	}
}
