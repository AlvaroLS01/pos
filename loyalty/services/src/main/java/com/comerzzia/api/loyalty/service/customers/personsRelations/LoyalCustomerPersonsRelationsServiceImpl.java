package com.comerzzia.api.loyalty.service.customers.personsRelations;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.persistence.customers.personsRelations.PersonaRelacionadaBean;
import com.comerzzia.api.loyalty.persistence.customers.personsRelations.PersonaRelacionadaExample;
import com.comerzzia.api.loyalty.persistence.customers.personsRelations.PersonaRelacionadaKey;
import com.comerzzia.api.loyalty.persistence.customers.personsRelations.LoyalCustomerPersonsRelationsMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.mybatis.exception.PersistenceExceptionFactory;

@Service
public class LoyalCustomerPersonsRelationsServiceImpl implements LoyalCustomerPersonsRelationsService {	
    protected static Logger log = Logger.getLogger(LoyalCustomerPersonsRelationsServiceImpl.class);
    
    @Autowired
    LoyalCustomerPersonsRelationsMapper mapper;
		
	@Override
	public List<PersonaRelacionadaBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		PersonaRelacionadaExample example = new PersonaRelacionadaExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoOrigenEqualTo(idFidelizado);
		return mapper.selectByExample(example);
	}
	
	@Override
	public int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		PersonaRelacionadaExample example = new PersonaRelacionadaExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoOrigenEqualTo(idFidelizado);
		return mapper.deleteByExample(example);
	}
	
	@Override
	public List<PersonaRelacionadaBean> selectByExample(PersonaRelacionadaExample example, IDatosSesion datosSesion) {
		return mapper.selectByExample(example); 	
	}
	
	
	@Override
	public void insert(PersonaRelacionadaBean persona, IDatosSesion datosSesion) {
		log.debug("crear() - Añadiendo nueva persona relacionada");
		
		try {
			persona.setUidInstancia(datosSesion.getUidInstancia());
			persona.setUidRelacionFidelizado(UUID.randomUUID().toString());
			mapper.insert(persona);			
		}		
		catch (PersistenceException e) {
			log.info("crear() - No se ha podido añadir la persona relacionada: " + e.getMessage());
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()){
				throw new PersonaRelacionadaConstraintViolationException(e.getMessage());
			}
		}
		
	}
	
	@Override
	public void updateByPrimaryKey(PersonaRelacionadaBean record, IDatosSesion datosSesion) {
		mapper.updateByPrimaryKey(record);
	}
	
	@Override
	public int deleteByExample(PersonaRelacionadaExample example, IDatosSesion datosSesion) {
		return mapper.deleteByExample(example);
	}
	
	
	@Override
	public int deleteByPrimaryKey(PersonaRelacionadaKey key, IDatosSesion datosSesion) {
		return mapper.deleteByPrimaryKey(key);
	}
}
