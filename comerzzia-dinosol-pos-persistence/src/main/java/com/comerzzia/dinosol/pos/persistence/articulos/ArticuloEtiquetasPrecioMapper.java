package com.comerzzia.dinosol.pos.persistence.articulos;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;

public interface ArticuloEtiquetasPrecioMapper {

	List<ArticuloBean> selectArticuloEtiquetaPrecios(@Param("uidActividad") String uidActividad, @Param("codbar") String codbar);
	
}