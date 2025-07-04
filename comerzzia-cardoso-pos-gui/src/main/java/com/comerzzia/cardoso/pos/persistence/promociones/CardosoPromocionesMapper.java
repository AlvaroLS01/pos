package com.comerzzia.cardoso.pos.persistence.promociones;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * GAP - PROMOCION CANDIDATA
 */
public interface CardosoPromocionesMapper{

	List<PromocionCandidataBean> selectPromocionAplicable(@Param("uidActividad") String uidActividad,
			@Param("codArt") String codArt, @Param("codTar") String codTar,
	        @Param("usaFidelizacion") String usaFidelizacion, @Param("fecha") Date fecha);
	
}