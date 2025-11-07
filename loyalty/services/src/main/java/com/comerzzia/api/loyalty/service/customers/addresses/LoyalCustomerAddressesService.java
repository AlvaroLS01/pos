package com.comerzzia.api.loyalty.service.customers.addresses;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.addresses.DireccionFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.addresses.DireccionFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.addresses.DireccionFidelizadoKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyalCustomerAddressesService {
	List<DireccionFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	
	List<DireccionFidelizadoBean> selectByExample(DireccionFidelizadoExample example, IDatosSesion datosSesion);

	DireccionFidelizadoBean selectByPrimaryKey(DireccionFidelizadoKey key, IDatosSesion datosSesion);
		
	void insert(DireccionFidelizadoBean record, IDatosSesion datosSesion);
	
	void updateByPrimaryKey(DireccionFidelizadoBean record, IDatosSesion datosSesion);
	
	void deleteByPrimaryKey(DireccionFidelizadoKey record, IDatosSesion datosSesion);
	
	int deleteByExample(DireccionFidelizadoExample example, IDatosSesion datosSesion);

	void modificarDescripcionDireccionFidelizado(String direccionOld, DireccionFidelizadoBean direccion,
			IDatosSesion datosSesion);
}