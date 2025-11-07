package com.comerzzia.api.loyalty.service.customers;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.customers.FidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.FidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.FidelizadoKey;
import com.comerzzia.core.servicios.contadores.ContadorException;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.db.Connection;

public interface LoyalCustomersService {

	List<FidelizadoBean> selectByExample(FidelizadoExample example, IDatosSesion datosSesion);

	FidelizadoBean selectByPrimaryKey(FidelizadoKey key, IDatosSesion datosSesion) throws FidelizadoNotFoundException;

	FidelizadoBean selectByUniqueKey(String loyaltyCustomerCode, IDatosSesion sessionData) throws FidelizadoNotFoundException;

	void insert(FidelizadoBean fidelizado, IDatosSesion datosSesion, Connection conn) throws ContadorException;

	void updateByPrimaryKey(FidelizadoBean fidelizado, IDatosSesion datosSesion);

	void deleteByPrimaryKey(FidelizadoKey key, IDatosSesion datosSesion) throws ApiException;

}