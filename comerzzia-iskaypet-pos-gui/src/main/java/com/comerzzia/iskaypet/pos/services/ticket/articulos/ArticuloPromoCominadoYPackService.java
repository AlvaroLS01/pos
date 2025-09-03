package com.comerzzia.iskaypet.pos.services.ticket.articulos;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.iskaypet.pos.persistence.tickets.articulos.promociones.tipos.combinado.IskaypetArticulosPromo;
import com.comerzzia.iskaypet.pos.persistence.tickets.articulos.promociones.tipos.combinado.IskaypetGetArticulosPromoMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Service
@Primary
public class ArticuloPromoCominadoYPackService {

	@Autowired
	protected Sesion sesion;
	@Autowired
	protected IskaypetGetArticulosPromoMapper articulosPromoMapper;
	
	public HashMap<String, String> cargarArticulosCombinados() {
		HashMap<String, String> mapArtDesPromoCombi = new HashMap<String, String>();
		List<IskaypetArticulosPromo> lstArtPomoCombi = articulosPromoMapper.getArticulosIdPromoCondicionesCombi(sesion.getAplicacion().getUidActividad(),5L);
		for (IskaypetArticulosPromo artPromoCombi : lstArtPomoCombi) {
			String [] codArticulosCombi = null;
			if(StringUtils.isNotBlank(artPromoCombi.getCodart()) && StringUtils.isNotBlank(artPromoCombi.getCodartCondition())) {
				codArticulosCombi = artPromoCombi.getCodartCondition().split(" ");
			}else {
				codArticulosCombi = artPromoCombi.getCodart().split(" ");
			}
		
			for (int i = 0; i < codArticulosCombi.length; i++) {
				String artPromoIncluido = codArticulosCombi[i];
					if ("S".equalsIgnoreCase(artPromoCombi.getPromoFidelizado())) {
						artPromoIncluido = "*" + artPromoIncluido;
					}
					mapArtDesPromoCombi.put(artPromoIncluido ,
							StringUtils.isNotBlank(artPromoCombi.getTextoPromocion())
									? artPromoCombi.getTextoPromocion()
									: artPromoCombi.getDescripcion());
			}
		}
		return mapArtDesPromoCombi;
	}
	
	public HashMap<String, String> cargarArticulosPack () {
		HashMap<String, String> mapArtDesPromoPack = new HashMap<String, String>();
		List<IskaypetArticulosPromo> lstArtPomoPack = articulosPromoMapper.getArticulosIdPromoAplicacionPack(sesion.getAplicacion().getUidActividad(),10L);
		for (IskaypetArticulosPromo artPromoPack : lstArtPomoPack) {
			String[] codArticulosPack = artPromoPack.getCodart().split(" ");
			for (int i = 0; i < codArticulosPack.length; i++) {
				String artPromoIncluido = codArticulosPack[i];
				if ("S".equalsIgnoreCase(artPromoPack.getPromoFidelizado())) {
					artPromoIncluido = "*" + artPromoIncluido;
				}
				mapArtDesPromoPack.put(artPromoIncluido,
						StringUtils.isNotBlank(artPromoPack.getTextoPromocion()) ? artPromoPack.getTextoPromocion()+ " Pvp Pack: " + artPromoPack.getPrecioPack()
								: artPromoPack.getDescripcion() + " Pvp Pack: " + artPromoPack.getPrecioPack());
			}
		}
		return mapArtDesPromoPack;
	}

}
