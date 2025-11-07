package com.comerzzia.api.loyalty.service.customers.contacts;

import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.persistence.customers.contacts.TiposContactoFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.contacts.TiposContactoFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.contacts.TiposContactoFidelizadoKey;
import com.comerzzia.api.loyalty.persistence.customers.contacts.LoyalCustomerContactsMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.mybatis.exception.PersistenceExceptionFactory;

@Service
public class LoyalCustomerContactsServiceImpl implements LoyalCustomerContactsService {
    protected static Logger log = Logger.getLogger(LoyalCustomerContactsServiceImpl.class);
	
	@Autowired
	LoyalCustomerContactsMapper mapper;
	
	@Override
	public List<TiposContactoFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		TiposContactoFidelizadoExample example = new TiposContactoFidelizadoExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.selectByExample(example);
	}
	
	@Override
	public int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		TiposContactoFidelizadoExample example = new TiposContactoFidelizadoExample();
		example.or().andUidInstanciaEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.deleteByExample(example);
	}
	
	@Override
	public List<TiposContactoFidelizadoBean> selectByExample(TiposContactoFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.selectByExample(example);
	}
	
	@Override
	public TiposContactoFidelizadoBean selectByPrimaryKey(TiposContactoFidelizadoKey key, IDatosSesion datosSesion) {
		return mapper.selectByPrimaryKey(key);
	}
	
		
	@Override
	public void deleteByPrimaryKey(TiposContactoFidelizadoKey key, IDatosSesion datosSesion) {
		mapper.deleteByPrimaryKey(key);		
	}
	
	@Override
	public int deleteByExample(TiposContactoFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.deleteByExample(example);
	}
	
	
	@Override
	public void insertUpdate(TiposContactoFidelizadoBean record, IDatosSesion datosSesion){
		log.debug("insertar() - Insertando el estado de la notificación: "+ record.getCodTipoCon() +" - " + (record.getRecibeNotificaciones() ? "S" : "N"));			
		
		try {
			record.setUidInstancia(datosSesion.getUidInstancia());

			mapper.insert(record);
		}
		catch (PersistenceException e){ //KEYCONSTRAIT
			//Si ya existe el tipo de la notificación para el fidelizado, la actualizamos
			log.debug("insertar() - Modificando el estado de la notificación: "+ record.getCodTipoCon() +" - " + (record.getRecibeNotificaciones() ? "S" : "N"));
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()){
				try {
					mapper.updateByPrimaryKey(record);
				} catch (Exception e1) {
					String msg = "Error modificando el estado de la notificación: " + e.getMessage();
		    		log.error("insertar() - " + msg);
		    		throw new TiposContactoFidelizadoException(msg, e);
				}
			}
		}		
	}
	
		
	@Override
	public void updateByPrimaryKey(TiposContactoFidelizadoBean record, IDatosSesion datosSesion) {
	    mapper.updateByPrimaryKey(record);	    
    }	
}
