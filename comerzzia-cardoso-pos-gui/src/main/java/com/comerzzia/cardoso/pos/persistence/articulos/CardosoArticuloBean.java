package com.comerzzia.cardoso.pos.persistence.articulos;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.context.annotation.Primary;

import com.comerzzia.cardoso.pos.persistence.lotes.CardosoAtributosAdicionalesArticuloBean;
import com.comerzzia.cardoso.pos.persistence.lotes.anexa.CardosoLoteArticuloBean;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;

@Primary
public class CardosoArticuloBean extends ArticuloBean{

	public CardosoArticuloBean(){
		super();
	}

	public CardosoArticuloBean getBean(ArticuloBean articulo) throws Exception{
		try{
			CardosoArticuloBean responseFinal = new CardosoArticuloBean();
			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			BeanUtils.copyProperties(responseFinal, articulo);
			return responseFinal;
		}
		catch(Exception e){
			throw new Exception("Error al convertir los datos del artículo.");
		}
	}

	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - LOTES
	 * GAP - PERSONALIZACIONES V3 - ARTÍCULOS PELIGROSOS
	 */
	
	private CardosoAtributosAdicionalesArticuloBean atributosAdicionalesArticulo;
	private CardosoLoteArticuloBean lote;

	public CardosoAtributosAdicionalesArticuloBean getAtributosAdicionalesArticulo(){
		return atributosAdicionalesArticulo;
	}

	public void setAtributosAdicionalesArticulo(CardosoAtributosAdicionalesArticuloBean atributosAdicionalesArticulo){
		this.atributosAdicionalesArticulo = atributosAdicionalesArticulo;
	}

	public CardosoLoteArticuloBean getLote(){
		return lote;
	}

	public void setLote(CardosoLoteArticuloBean lote){
		this.lote = lote;
	}

}
