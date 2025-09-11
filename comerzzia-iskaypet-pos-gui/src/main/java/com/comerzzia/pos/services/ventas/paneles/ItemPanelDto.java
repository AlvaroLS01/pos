package com.comerzzia.pos.services.ventas.paneles;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.categorizaciones.CategorizacionBean;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.categorizaciones.CategorizacionesService;
import com.comerzzia.pos.util.config.SpringContext;

@Component
@Scope("prototype")
public class ItemPanelDto {
	
	private Logger log = Logger.getLogger(ItemPanelDto.class);

	public static final String TIPO_ARTICULO = "ARTICULO";

	public static final String TIPO_CATEGORIA = "CATEGORIA";

	public static final String TIPO_HUECO = "HUECO";

	public static final String TIPO_FAMILIA = "FAMILIA";

	public static final String TIPO_CATEGORIZACION = "CATEGORIZACION";
	
	public static final String TIPO_SECCION = "SECCION";
	
	public static final String CONTINUE_BEHAVIOUR = "CONTINUAR";
	
	public static final String MAIN_BEHAVIOUR = "INICIO";
	
	public static final String CLOSE_BEHAVIOUR = "CERRAR";
	
	@Autowired
	private CategorizacionesService categorizacionesService;
	
	@Autowired
	private ArticulosService articulosService;

	protected String codigo;

	protected String descripción;

	protected String tipo;

	protected String foto;
	
	protected String behaviour;

	protected List<ItemPanelDto> subItems;

	protected Integer fila;

	protected Integer columna;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripción() {
		if(StringUtils.isBlank(descripción)) {
			log.debug("getDescripción() - La descripción del item " + codigo + " está en blanco. Se intentará buscar en base de datos.");
			descripción = "";
			try {
				if(tipo.equals(TIPO_ARTICULO)) {					
					ArticuloBean articulo = articulosService.consultarArticulo(codigo);
					if(articulo != null) {
						descripción = articulo.getDesArticulo();
					}
				}
				else {
					CategorizacionBean categoria = categorizacionesService.consultarCategoria(codigo);
					if(categoria != null) {
						descripción = categoria.getDescat();
					}
				}
			}
			catch(Exception e) {
				log.error("getDescripción() - No se ha podido cargar la descripción del item con código " + codigo + ": " + e.getMessage());
			}
		}
		return descripción;
	}

	public void setDescripción(String descripción) {
		this.descripción = descripción;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		if (tipo.equalsIgnoreCase(ItemPanelDto.TIPO_ARTICULO) || tipo.equalsIgnoreCase(ItemPanelDto.TIPO_CATEGORIA) || tipo.equalsIgnoreCase(ItemPanelDto.TIPO_FAMILIA)
		        || tipo.equalsIgnoreCase(ItemPanelDto.TIPO_CATEGORIZACION) || tipo.equalsIgnoreCase(ItemPanelDto.TIPO_HUECO) || tipo.equalsIgnoreCase(ItemPanelDto.TIPO_SECCION)) {
			this.tipo = tipo;
		}
		else {
			this.tipo = ItemPanelDto.TIPO_ARTICULO;
		}
	}

	public String getFoto() {
		if (foto == null) {
			if (tipo.equals(ItemPanelDto.TIPO_ARTICULO)) {
				return "iconos/articulo.png";
			}
			else {
				return "iconos/categoria.png";
			}
		}
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<ItemPanelDto> getSubItems() {
		if (subItems == null) {
			log.debug("getSubItems() - La lista de subitems del item está vacía. Se va a intentar completar.");
			subItems = new ArrayList<ItemPanelDto>();

			if (tipo.equals(ItemPanelDto.TIPO_CATEGORIA)) {
				if (StringUtils.isNotBlank(codigo)) {
					List<CategorizacionBean> categorizacionesHijas = categorizacionesService.consultarCategoriasHijas(codigo);
					for (CategorizacionBean categoriaHija : categorizacionesHijas) {
						ItemPanelDto subdestacado = convertirCategoriaADestacado(categoriaHija);
						subItems.add(subdestacado);
					}
				}

				List<ArticuloBean> articulosCategoria = categorizacionesService.buscarArticulos(codigo);
				for (ArticuloBean articulo : articulosCategoria) {
					ItemPanelDto subdestacado = SpringContext.getBean(ItemPanelDto.class);
					subdestacado.setCodigo(articulo.getCodArticulo());
					subdestacado.setDescripción(articulo.getDesArticulo());
					subdestacado.setTipo(ItemPanelDto.TIPO_ARTICULO);
					subItems.add(subdestacado);
				}
			}
		}
		return subItems;
	}

	protected ItemPanelDto convertirCategoriaADestacado(CategorizacionBean categoria) {
		ItemPanelDto destacado = SpringContext.getBean(ItemPanelDto.class);
		destacado.setCodigo(categoria.getCodcat());
		destacado.setDescripción(categoria.getDescat());
		destacado.setTipo(ItemPanelDto.TIPO_CATEGORIA);
		return destacado;
	}

	public List<ItemPanelDto> getSubItemsSinBuscar() {
		return subItems;
	}

	public void setSubItems(List<ItemPanelDto> subItems) {
		this.subItems = subItems;
	}

	public Integer getFila() {
		return fila;
	}

	public void setFila(Integer fila) {
		this.fila = fila;
	}

	public Integer getColumna() {
		return columna;
	}

	public void setColumna(Integer columna) {
		this.columna = columna;
	}

	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}

	public String toString() {
		return codigo + " -> " + subItems;
	}

}
