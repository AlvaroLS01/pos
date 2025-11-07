package com.comerzzia.api.loyalty.service.customers.access;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.access.AccesoFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.access.AccesoFidelizadoExample;
import com.comerzzia.api.loyalty.service.customers.FidelizadoNotFoundException;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyalCustomerAccessService {
	List<AccesoFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion);

	int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion);

	void insert(AccesoFidelizadoBean fidelizado, IDatosSesion datosSesion) throws FidelizadoNotFoundException;

	AccesoFidelizadoBean loginWithEmail(IDatosSesion datosSesion, String email, String clave)
			throws AccesoFidelizadoNotFoundException;

	void updateByPrimaryKey(AccesoFidelizadoBean record, IDatosSesion datosSesion);

	void updatePassword(IDatosSesion datosSesion, AccesoFidelizadoBean prevAccesoFidelizado)
			throws AccesoFidelizadoNotFoundException;

	int deleteByExample(AccesoFidelizadoExample example, IDatosSesion datosSesion);

	AccesoFidelizadoBean selectUkByExample(AccesoFidelizadoExample example, IDatosSesion datosSesion)
			throws AccesoFidelizadoNotFoundException;

	void insertForEmail(Long idFidelizado, String email, IDatosSesion datosSesion);
}