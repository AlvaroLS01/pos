package com.comerzzia.api.loyalty.service.customers.wishlists;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.persistence.customers.wishlists.ListaDeseosFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.ListaDeseosFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.ListaDeseosFidelizadoKey;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.LoyalCustomerWishListsMapper;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.ArticuloListaDeseosFidelizadoExample;
import com.comerzzia.api.loyalty.service.customers.wishlists.details.LoyalCustomerWishListDetailsService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class LoyalCustomerWishListsServiceImpl implements LoyalCustomerWishListsService {
	protected static Logger log = Logger.getLogger(LoyalCustomerWishListsServiceImpl.class);

	@Autowired
	LoyalCustomerWishListsMapper mapper;
	
	@Autowired
	LoyalCustomerWishListDetailsService customersWishListDetailsService;
	
	@Override
	public List<ListaDeseosFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		ListaDeseosFidelizadoExample example = new ListaDeseosFidelizadoExample();
		example.or().andUidActividadEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		return mapper.selectByExample(example);
	}
	
	@Override
	public int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion) {
		ListaDeseosFidelizadoExample example = new ListaDeseosFidelizadoExample();
		example.or().andUidActividadEqualTo(datosSesion.getUidInstancia()).andIdFidelizadoEqualTo(idFidelizado);
		
		return deleteByExample(example, datosSesion);
	}
	

	@Override
	public List<ListaDeseosFidelizadoBean> selectByExample(ListaDeseosFidelizadoExample example,
			IDatosSesion datosSesion) {
		return mapper.selectByExample(example);
	}

	@Override
	public void insert(ListaDeseosFidelizadoBean record, IDatosSesion datosSesion) {
		record.setUidActividad(datosSesion.getUidActividad());
		record.setUidListaDeseos(UUID.randomUUID().toString());
		record.setFecha(new Date());

		mapper.insert(record);
	}

	@Override
	public int updateByPrimaryKey(ListaDeseosFidelizadoBean listaDeseos, IDatosSesion datosSesion) {
		return mapper.updateByPrimaryKey(listaDeseos);
	}
	
	
	@Override
	public int deleteByPrimaryKey(ListaDeseosFidelizadoKey key, IDatosSesion datosSesion) {
		// borrar detalle de la lista de deseos
		ArticuloListaDeseosFidelizadoExample example = new ArticuloListaDeseosFidelizadoExample(); 
		example.or().andUidActividadEqualTo(datosSesion.getUidActividad()).andUidListaDeseosEqualTo(key.getUidListaDeseos());		
		customersWishListDetailsService.deleteByExample(example, datosSesion);
		
		return mapper.deleteByPrimaryKey(key);
	}
	
	@Override
	public int deleteByExample(ListaDeseosFidelizadoExample example, IDatosSesion datosSesion) {		
		List<ListaDeseosFidelizadoBean> registros = selectByExample(example, datosSesion);
		
		// borrar de forma recursiva para eliminar los detalles
		for (ListaDeseosFidelizadoBean record : registros) {
			deleteByPrimaryKey(record, datosSesion);
		}
		
		return registros.size();
	}	
}
