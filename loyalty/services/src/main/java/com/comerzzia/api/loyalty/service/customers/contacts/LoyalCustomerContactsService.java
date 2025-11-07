package com.comerzzia.api.loyalty.service.customers.contacts;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.contacts.TiposContactoFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.contacts.TiposContactoFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.contacts.TiposContactoFidelizadoKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyalCustomerContactsService {
	List<TiposContactoFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	
	List<TiposContactoFidelizadoBean> selectByExample(TiposContactoFidelizadoExample example, IDatosSesion datosSesion);

	TiposContactoFidelizadoBean selectByPrimaryKey(TiposContactoFidelizadoKey key, IDatosSesion datosSesion);

	void deleteByPrimaryKey(TiposContactoFidelizadoKey key, IDatosSesion datosSesion);

	int deleteByExample(TiposContactoFidelizadoExample example, IDatosSesion datosSesion);

	void insertUpdate(TiposContactoFidelizadoBean record, IDatosSesion datosSesion);

	void updateByPrimaryKey(TiposContactoFidelizadoBean record, IDatosSesion datosSesion);

}