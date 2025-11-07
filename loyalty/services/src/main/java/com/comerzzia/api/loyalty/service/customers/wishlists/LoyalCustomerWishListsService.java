package com.comerzzia.api.loyalty.service.customers.wishlists;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.wishlists.ListaDeseosFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.ListaDeseosFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.ListaDeseosFidelizadoKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyalCustomerWishListsService {
	List<ListaDeseosFidelizadoBean> selectByCustomer(Long idFidelizado, IDatosSesion datosSesion);

	int deleteByCustomer(Long idFidelizado, IDatosSesion datosSesion);

	List<ListaDeseosFidelizadoBean> selectByExample(ListaDeseosFidelizadoExample example, IDatosSesion datosSesion);

	void insert(ListaDeseosFidelizadoBean record, IDatosSesion datosSesion);

	int updateByPrimaryKey(ListaDeseosFidelizadoBean listaDeseos, IDatosSesion datosSesion);

	int deleteByPrimaryKey(ListaDeseosFidelizadoKey key, IDatosSesion datosSesion);

	int deleteByExample(ListaDeseosFidelizadoExample example, IDatosSesion datosSesion);
}