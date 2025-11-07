package com.comerzzia.api.loyalty.service.collectivesTypes;

import com.comerzzia.api.loyalty.persistence.collectivesTypes.ParametrosBuscarTiposColectivosBean;
import com.comerzzia.api.loyalty.persistence.collectivesTypes.TipoColectivoBean;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.paginacion.PaginaResultados;

public interface CollectivesTypesService {

	PaginaResultados consultar(ParametrosBuscarTiposColectivosBean param, IDatosSesion datosSesion);

	TipoColectivoBean consultar(String codtipcolectivo, IDatosSesion datosSesion) throws TipoColectivoNotFoundException;

	void salvar(TipoColectivoBean tipoColectivo, IDatosSesion datosSesion);

}