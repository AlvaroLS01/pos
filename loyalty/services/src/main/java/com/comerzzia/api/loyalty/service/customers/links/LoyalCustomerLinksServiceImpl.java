package com.comerzzia.api.loyalty.service.customers.links;


import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.links.LoyalCustomerLinksMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.mybatis.exception.PersistenceExceptionFactory;

@Service
public class LoyalCustomerLinksServiceImpl implements LoyalCustomerLinksService {	
    protected static Logger log = Logger.getLogger(LoyalCustomerLinksServiceImpl.class);
    
    @Autowired
    LoyalCustomerLinksMapper mapper;
    
	@Override
	public List<EnlaceFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		EnlaceFidelizadoExample example = new EnlaceFidelizadoExample();
		example.or().andUidActividadEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.selectByExample(example);
	}
	
	@Override
	public int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		EnlaceFidelizadoExample example = new EnlaceFidelizadoExample();
		example.or().andUidActividadEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.deleteByExample(example);
	}
    

	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.customers.links.CustomersLinksService#selectByPrimaryKey(com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoBean, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	public EnlaceFidelizadoBean selectByPrimaryKey(EnlaceFidelizadoBean record, IDatosSesion datosSesion) {
		return mapper.selectByPrimaryKey(record);
	}
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.customers.links.CustomersLinksService#selectByExample(com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoExample, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	public List<EnlaceFidelizadoBean> selectByExample(EnlaceFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.selectByExample(example);
	}    
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.customers.links.CustomersLinksService#insert(com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoBean, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	public void insert(EnlaceFidelizadoBean enlaceFidelizacion,	IDatosSesion datosSesion) {
		log.debug("crear() - Creando nuevo enlace");

		try {
			enlaceFidelizacion.setUidActividad(datosSesion.getUidActividad());
			mapper.insert(enlaceFidelizacion);
		} catch (PersistenceException e) { 
			log.error("crear() - No se ha podido crear el enlace: "
					+ e.getMessage());
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()){
				throw new EnlaceConstraintViolationException(
					"El enlace introducido ya está registrado.");
			}
		} 
	}
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.customers.links.CustomersLinksService#updateInsertUniqueObjectClass(com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoBean, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	public void updateInsertUniqueObjectClass(EnlaceFidelizadoBean enlaceFidelizacion, IDatosSesion datosSesion){
		
		try{
			EnlaceFidelizadoExample example = new EnlaceFidelizadoExample();
			example.or().andUidActividadEqualTo(datosSesion.getUidActividad())
						.andIdClaseEqualTo(enlaceFidelizacion.getIdClase())
						.andIdFidelizadoEqualTo(enlaceFidelizacion.getIdFidelizado());
			
			//Si no se producen cambio se creará el enlace. 
			if(mapper.updateByExample(enlaceFidelizacion, example) == 0){
				mapper.insert(enlaceFidelizacion);
			}			
		} catch (PersistenceException e) { 
			log.info("crear() - No se ha podido crear el enlace: " + e.getMessage());
			if(PersistenceExceptionFactory.getPersistenceExpception(e).isKeyConstraintViolationException()){
				throw new EnlaceConstraintViolationException("El enlace introducido ya está registrado.");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.customers.links.CustomersLinksService#deleteByPrimaryKey(com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoBean, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	public int deleteByPrimaryKey(EnlaceFidelizadoBean record, IDatosSesion datosSesion) {
		return mapper.deleteByPrimaryKey(record);
	}
	
	/* (non-Javadoc)
	 * @see com.comerzzia.api.loyalty.service.customers.links.CustomersLinksService#deleteByExample(com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoExample, com.comerzzia.core.servicios.sesion.IDatosSesion)
	 */
	@Override
	public int deleteByExample(EnlaceFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.deleteByExample(example);
	}	
}