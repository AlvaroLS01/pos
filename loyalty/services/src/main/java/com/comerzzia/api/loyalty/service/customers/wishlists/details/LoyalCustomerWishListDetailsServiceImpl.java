package com.comerzzia.api.loyalty.service.customers.wishlists.details;


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.ArticuloListaDeseosFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.ArticuloListaDeseosFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.ArticuloListaDeseosFidelizadoKey;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.LoyalCustomerWishListDetailsMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class LoyalCustomerWishListDetailsServiceImpl implements LoyalCustomerWishListDetailsService {	
    protected static Logger log = Logger.getLogger(LoyalCustomerWishListDetailsServiceImpl.class);
    
    @Autowired
    LoyalCustomerWishListDetailsMapper mapper;
	
	@Override
	public List<ArticuloListaDeseosFidelizadoBean> selectByExample(ArticuloListaDeseosFidelizadoExample example, IDatosSesion datosSesion) {		
	   return mapper.selectByExample(example);
	}
			
	@Override
	public void insert(ArticuloListaDeseosFidelizadoBean record, IDatosSesion datosSesion) {	
		record.setUidActividad(datosSesion.getUidActividad());
		
		mapper.insert(record);
	}

	@Override
	public int updateByPrimaryKey(ArticuloListaDeseosFidelizadoBean record, IDatosSesion datosSesion) {
		return mapper.updateByPrimaryKey(record);
	}
	
	@Override
	public int deleteByPrimaryKey(ArticuloListaDeseosFidelizadoKey key, IDatosSesion datosSesion) {
		return mapper.deleteByPrimaryKey(key);
	}
	
	@Override
	public int deleteByExample(ArticuloListaDeseosFidelizadoExample example, IDatosSesion datosSesion) {
		return mapper.deleteByExample(example);
	}		
}
