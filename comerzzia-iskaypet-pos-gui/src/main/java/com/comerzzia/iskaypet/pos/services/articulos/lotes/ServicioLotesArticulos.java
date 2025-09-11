package com.comerzzia.iskaypet.pos.services.articulos.lotes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteArticulo;
import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteArticuloExample;
import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteArticuloMapper;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class ServicioLotesArticulos {
	
	private Logger log = Logger.getLogger(ServicioLotesArticulos.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	LoteArticuloMapper mapper;
	
	/**
	 * Consulta los lotes disponibles (con stock y sin caducar) para un artículo y sus desgloses. Si no se encuentran los desgloses o en desglose1/desglose2 se alimenta "*", se buscará por desglose genérico 
	 * @param articulo
	 * @param desglose1
	 * @param desglose2
	 * @return
	 * @throws LoteArticuloException
	 */
	public List<LoteArticulo> consultarLotesDisponiblesDeArticulo(ArticuloBean articulo, String desglose1, String desglose2) throws LoteArticuloException {
		if(articulo==null || StringUtils.isEmpty(articulo.getCodArticulo())) {
			throw new LoteArticuloException(I18N.getTexto("El artículo no puede ser nulo"));
		}
		log.debug("consultarLotesDeArticulo() - Consultando lotes de artículo [codArt:"+articulo.getCodArticulo()+", desglose1:"+articulo.getDesglose1()+", desglose2:"+articulo.getDesglose2()+"]");
		
		List<LoteArticulo> resultado = new ArrayList<>();
		desglose1 = desglose1==null ? "*" : desglose1;
		desglose2 = desglose2==null ? "*" : desglose2;
		
		LoteArticuloExample example = new LoteArticuloExample();
		LoteArticuloExample.Criteria criteria = example.createCriteria();
		criteria
			.andActivityUidEqualTo(sesion.getAplicacion().getUidActividad())
			.andWhCodeEqualTo(sesion.getAplicacion().getCodAlmacen())
			.andItemCodeEqualTo(articulo.getCodArticulo())
			.andExpirationDateGreaterThanOrEqualTo(new Date())
			.andStockGreaterThan(BigDecimal.ZERO)
			.andCombination1CodeEqualTo(desglose1)
			.andCombination2CodeEqualTo(desglose2);
		
		resultado = mapper.selectByExample(example);
		
		if(resultado.isEmpty() && !desglose1.equals("*") && !desglose1.equals("*")) {
			log.debug("consultarLotesDeArticulo() - No se han encontrado lotes para desgloses especificados, buscando lotes sin desgloses");
			
			criteria
				.andCombination1CodeEqualTo("*")
				.andCombination2CodeEqualTo("*");
			
			resultado = mapper.selectByExample(example);
		}
		
		return resultado;
	}
	
	/**
	 * Consulta los lotes totales (indiferente a stock y fecha de caducidad) para un artículo y sus desgloses. Si no se encuentran los desgloses o en desglose1/desglose2 se alimenta "*", se buscará por desglose genérico 
	 * @param articulo
	 * @param desglose1
	 * @param desglose2
	 * @return
	 * @throws LoteArticuloException
	 */
	public List<LoteArticulo> consultarLotesTotalesDeArticulo(ArticuloBean articulo, String desglose1, String desglose2) throws LoteArticuloException {
		if(articulo==null || StringUtils.isEmpty(articulo.getCodArticulo())) {
			throw new LoteArticuloException(I18N.getTexto("El artículo no puede ser nulo"));
		}
		log.debug("consultarLotesDeArticulo() - Consultando lotes de artículo [codArt:"+articulo.getCodArticulo()+", desglose1:"+articulo.getDesglose1()+", desglose2:"+articulo.getDesglose2()+"]");
		
		List<LoteArticulo> resultado = new ArrayList<>();
		desglose1 = desglose1==null ? "*" : desglose1;
		desglose2 = desglose2==null ? "*" : desglose2;
		
		LoteArticuloExample example = new LoteArticuloExample();
		LoteArticuloExample.Criteria criteria = example.createCriteria();
		criteria
			.andActivityUidEqualTo(sesion.getAplicacion().getUidActividad())
			.andWhCodeEqualTo(sesion.getAplicacion().getCodAlmacen())
			.andItemCodeEqualTo(articulo.getCodArticulo())
			.andCombination1CodeEqualTo(desglose1)
			.andCombination2CodeEqualTo(desglose2);
		
		resultado = mapper.selectByExample(example);
		
		if(resultado.isEmpty() && !desglose1.equals("*") && !desglose1.equals("*")) {
			log.debug("consultarLotesDeArticulo() - No se han encontrado lotes para desgloses especificados, buscando lotes sin desgloses");
			
			criteria
				.andCombination1CodeEqualTo("*")
				.andCombination2CodeEqualTo("*");
			
			resultado = mapper.selectByExample(example);
		}
		
		return resultado;
	}

}
