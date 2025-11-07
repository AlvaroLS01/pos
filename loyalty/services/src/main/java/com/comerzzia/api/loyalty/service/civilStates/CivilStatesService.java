package com.comerzzia.api.loyalty.service.civilStates;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.civilStates.EstadoCivilBean;
import com.comerzzia.api.loyalty.persistence.civilStates.EstadoCivilExample;
import com.comerzzia.api.loyalty.persistence.civilStates.EstadoCivilKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CivilStatesService {

	List<EstadoCivilBean> selectByExample(EstadoCivilExample example, IDatosSesion datosSesion);

	List<EstadoCivilBean> selectAll(IDatosSesion datosSesion);

	void insert(EstadoCivilBean record, IDatosSesion datosSesion);

	int updateByPrimaryKey(EstadoCivilBean record, IDatosSesion datosSesion);

	int deleteByPrimaryKey(EstadoCivilKey key, IDatosSesion datosSesion);

}