package com.comerzzia.api.loyalty.service.customers.wishlists.details;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.ArticuloListaDeseosFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.ArticuloListaDeseosFidelizadoExample;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.ArticuloListaDeseosFidelizadoKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface LoyalCustomerWishListDetailsService {

	List<ArticuloListaDeseosFidelizadoBean> selectByExample(ArticuloListaDeseosFidelizadoExample example,
			IDatosSesion datosSesion);

	void insert(ArticuloListaDeseosFidelizadoBean record, IDatosSesion datosSesion);

	int updateByPrimaryKey(ArticuloListaDeseosFidelizadoBean record, IDatosSesion datosSesion);

	int deleteByPrimaryKey(ArticuloListaDeseosFidelizadoKey key, IDatosSesion datosSesion);

	int deleteByExample(ArticuloListaDeseosFidelizadoExample example, IDatosSesion datosSesion);

}