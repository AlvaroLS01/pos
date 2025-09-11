package com.comerzzia.pos.services.articulos.categorizaciones;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.ArticuloExample;
import com.comerzzia.pos.persistence.articulos.ArticuloMapper;
import com.comerzzia.pos.persistence.articulos.categorizaciones.CategorizacionBean;
import com.comerzzia.pos.persistence.articulos.categorizaciones.CategorizacionExample;
import com.comerzzia.pos.persistence.articulos.categorizaciones.CategorizacionKey;
import com.comerzzia.pos.persistence.articulos.categorizaciones.CategorizacionMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Component
public class CategorizacionesService {
	
	private Logger log = Logger.getLogger(CategorizacionesService.class);
	
	@Autowired
	private Sesion Categorizacion;
	
	@Autowired
	private CategorizacionMapper categorizacionMapper;
	
	@Autowired
	private ArticuloMapper articuloMapper;
	
	@Autowired
	private Sesion sesion;
	
	public List<CategorizacionBean> consultarCategorias() {
		log.debug("consultarCategorias() - Consultando las categorías padre");
		
		CategorizacionExample example = new CategorizacionExample();
		example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodcatLengthEqualTo(2).andDescatIsNotNull().andActivoEqualTo("S");
		
		return categorizacionMapper.selectByExampleWithArticles(example);
	}
	
	public List<CategorizacionBean> consultarCategoriasHijas(String codcat) {
		log.debug("consultarCategoriasHijas() - Consultando las categorías hijas de la categorías " + codcat);
		
		CategorizacionExample example = new CategorizacionExample();
		example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodcatLike(codcat + "%").andCodcatLengthEqualTo(codcat.length() + 2).andDescatIsNotNull().andActivoEqualTo("S");
		
		return categorizacionMapper.selectByExampleWithArticles(example);
	}
	
	public List<ArticuloBean> buscarArticulos(String codcat) {
		log.debug("buscarArticulos() - Consultando artículos de la categoría " + codcat);
		
		ArticuloExample example = new ArticuloExample();
		example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodCategorizacionEqualTo(codcat).andActivoEqualTo(true);
		
		return articuloMapper.selectByExample(example);
	}
	
	public CategorizacionBean consultarCategoria(String codcat) {
		log.debug("consultarCategoria() - Consultando la categoría " + codcat);
		
		CategorizacionKey categorizacionKey = new CategorizacionKey();
		categorizacionKey.setUidActividad(sesion.getAplicacion().getUidActividad());
		categorizacionKey.setCodcat(codcat);
		
		return categorizacionMapper.selectByPrimaryKey(categorizacionKey);
	}

}
