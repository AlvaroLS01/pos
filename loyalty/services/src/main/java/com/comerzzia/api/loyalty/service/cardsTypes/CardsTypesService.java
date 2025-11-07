package com.comerzzia.api.loyalty.service.cardsTypes;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.cardsTypes.TipoTarjetaBean;
import com.comerzzia.api.loyalty.persistence.cardsTypes.TipoTarjetaExample;
import com.comerzzia.api.loyalty.persistence.cardsTypes.TipoTarjetaKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CardsTypesService {
	List<TipoTarjetaBean> selectByExample(TipoTarjetaExample example, IDatosSesion datosSesion);

	List<TipoTarjetaBean> selectAll(IDatosSesion datosSesion);

	void insert(TipoTarjetaBean record, IDatosSesion datosSesion);

	int updateByPrimaryKey(TipoTarjetaBean record, IDatosSesion datosSesion);

	int deleteByPrimaryKey(TipoTarjetaKey key, IDatosSesion datosSesion);

}