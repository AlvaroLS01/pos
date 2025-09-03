package com.comerzzia.iskaypet.pos.persistence.tickets.articulos.promociones.tipos.combinado;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IskaypetGetArticulosPromoMapper {
	
    List<IskaypetArticulosPromo> getArticulosIdPromoAplicacionPack(@Param(value = "uidActividad") String uidActividad, @Param(value = "idTipoPromo") Long idTipoPromo);
   
    List<IskaypetArticulosPromo> getArticulosIdPromoCondicionesCombi(@Param(value = "uidActividad") String uidActividad, @Param(value = "idTipoPromo") Long idTipoPromo);

}