package com.comerzzia.api.loyalty.service.customers.collectives;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyalCustomerCollectivesService {
	List<ColectivosFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion);
	
	List<ColectivosFidelizadoBean> selectByExample(ColectivosFidelizadoExample example, IDatosSesion datosSesion);

	ColectivosFidelizadoBean selectByPrimaryKey(ColectivosFidelizadoKey key, IDatosSesion datosSesion);

	int deleteByExample(ColectivosFidelizadoExample example, IDatosSesion datosSesion);

	int deletetByPrimaryKey(ColectivosFidelizadoKey key, IDatosSesion datosSesion);

	void insert(ColectivosFidelizadoBean colectivosFidelizadoBean, IDatosSesion datosSesion);
}