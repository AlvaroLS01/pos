package com.comerzzia.api.loyalty.service.collectives;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.collectives.ColectivoBean;
import com.comerzzia.api.loyalty.persistence.collectives.ColectivoExample;
import com.comerzzia.api.loyalty.persistence.collectives.ColectivoKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CollectivesService {

	String CREADOASIGNADO = "CA";
	String ASIGNADO = "A";
	String ERRORASIGNAR = "EA";
	String ERRORCREAR = "EC";

	List<ColectivoBean> selectByExample(ColectivoExample example, IDatosSesion datosSesion);	

	void insert(ColectivoBean record, IDatosSesion datosSesion);

	void updateByPrimaryKey(ColectivoBean record, IDatosSesion datosSesion);

	void deleteByPrimaryKey(ColectivoKey key, IDatosSesion datosSesion);


	
}