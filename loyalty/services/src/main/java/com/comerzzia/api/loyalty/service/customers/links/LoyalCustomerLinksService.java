package com.comerzzia.api.loyalty.service.customers.links;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoExample;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyalCustomerLinksService {
	List<EnlaceFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	
	int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion);

	EnlaceFidelizadoBean selectByPrimaryKey(EnlaceFidelizadoBean record, IDatosSesion datosSesion);

	List<EnlaceFidelizadoBean> selectByExample(EnlaceFidelizadoExample example, IDatosSesion datosSesion);

	void insert(EnlaceFidelizadoBean enlaceFidelizacion, IDatosSesion datosSesion);

	/**
	 * 
	 * Este método modificará o creará un enlace único a una clase.
	 * Por ejemplo se utiliza para establecer una única tienda favorita al fidelizado
	 * @param enlaceFidelizacion
	 * @param datosSesion
	 */
	void updateInsertUniqueObjectClass(EnlaceFidelizadoBean enlaceFidelizacion, IDatosSesion datosSesion);

	int deleteByPrimaryKey(EnlaceFidelizadoBean record, IDatosSesion datosSesion);

	int deleteByExample(EnlaceFidelizadoExample example, IDatosSesion datosSesion);

}