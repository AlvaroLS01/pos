package com.comerzzia.api.loyalty.service.customers.personsRelations;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.personsRelations.PersonaRelacionadaBean;
import com.comerzzia.api.loyalty.persistence.customers.personsRelations.PersonaRelacionadaExample;
import com.comerzzia.api.loyalty.persistence.customers.personsRelations.PersonaRelacionadaKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyalCustomerPersonsRelationsService {
	List<PersonaRelacionadaBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	
	List<PersonaRelacionadaBean> selectByExample(PersonaRelacionadaExample example, IDatosSesion datosSesion);

	void insert(PersonaRelacionadaBean persona, IDatosSesion datosSesion);

	void updateByPrimaryKey(PersonaRelacionadaBean record, IDatosSesion datosSesion);

	int deleteByExample(PersonaRelacionadaExample example, IDatosSesion datosSesion);

	int deleteByPrimaryKey(PersonaRelacionadaKey key, IDatosSesion datosSesion);

}