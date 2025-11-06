package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.TarjetaIdentificacionDto;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.tipos.PromocionCabecera;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;

@Component
@Scope("prototype")
public class PromocionPuntosBPBean extends PromocionCabecera {

	private Logger log = Logger.getLogger(PromocionPuntosBPBean.class);

	public static final String PARAM_PUNTOS_CONCEDIDOS = "puntosConcedidos";

	private List<TramoBpDto> tramos;

	private String textoNoAplicaSinTarjeta;

	private String textoNoAplicaConTarjeta;

	@Override
	public void leerDatosPromocion(byte[] datosPromocion) {
		try {
			leerCondicionesCabecera(datosPromocion);
			leerTramos(datosPromocion);
			leerExtensiones(datosPromocion);
			leerTextos(datosPromocion);
		}
		catch (XMLDocumentException e) {
			log.error("leerDatosPromocion() - Error al leer los datos de la promoción de tipo puntos: " + e.getMessage(), e);
		}
	}

	private void leerTextos(byte[] datosPromocion) throws XMLDocumentException {
		XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
		XMLDocumentNode nodoTextoSinTarjeta = xmlPromocion.getNodo("texto_no_aplica_sin_tarjeta");
		textoNoAplicaSinTarjeta = nodoTextoSinTarjeta.getValue();
		XMLDocumentNode nodoTextoConTarjeta = xmlPromocion.getNodo("texto_no_aplica_con_tarjeta_off");
		textoNoAplicaConTarjeta = nodoTextoConTarjeta.getValue();
    }

	private void leerTramos(byte[] datosPromocion) throws XMLDocumentException {
		tramos = new ArrayList<TramoBpDto>();

		XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
		XMLDocumentNode nodoAplicacion = xmlPromocion.getNodo("aplicacion");
		for (XMLDocumentNode nodoTramo : nodoAplicacion.getHijos()) {
			BigDecimal importeTramo = nodoTramo.getNodo("importeTramo").getValueAsBigDecimal();
			BigDecimal puntosTramo = nodoTramo.getNodo("puntosTramo").getValueAsBigDecimal();
			String textoTramo = nodoTramo.getNodo("texto_promo_off_line").getValue();
			tramos.add(new TramoBpDto(importeTramo, puntosTramo, textoTramo));
		}
	}

	@Override
	public boolean aplicarPromocion(DocumentoPromocionable documento) {
		
		TarjetaIdentificacionDto tarjetaIdentificacion = ((DinoCabeceraTicket) documento.getCabecera()).buscarTarjeta("BP");
		PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);

		if (tarjetaIdentificacion != null) {
			BigDecimal total = documento.getCabecera().getTotales().getTotalAPagar();

			TramoBpDto tramo = elegirTramo(total);

			if (tramo != null) {
				BigDecimal valorTramo = tramo.getImporteTramo();
				BigDecimal puntosTramo = tramo.getPuntosTramo();
				BigDecimal puntosConcedidos = total.divide(valorTramo, 0, RoundingMode.FLOOR).multiply(puntosTramo);
				tarjetaIdentificacion.putAdicional(PARAM_PUNTOS_CONCEDIDOS, puntosConcedidos);

				promocionTicket.setPuntos(puntosConcedidos.intValue());
				String textoAplicacion = tramo.getTextoAplica();
				textoAplicacion = textoAplicacion.replaceAll("#TOTPTOS#", puntosConcedidos.toString());
				promocionTicket.setTextoPromocion(textoAplicacion);
			}
			else {
				log.trace("aplicarPromocion() - No hay ningún tramo que cumpla las condiciones del ticket.");
				promocionTicket.setTextoPromocion(textoNoAplicaConTarjeta);
				tarjetaIdentificacion.putAdicional(PARAM_PUNTOS_CONCEDIDOS, BigDecimal.ZERO);
			}
		}
		else {
			log.trace("aplicarPromocion() - No se ha indicado la tarjeta de fidelización.");
			promocionTicket.setTextoPromocion(textoNoAplicaSinTarjeta);
		}
		
		PromocionTicket promocionTipo = documento.getPromocion(this.getIdPromocion());
		if(promocionTipo == null) {
			documento.addPromocion(promocionTicket);
		}
		else {
			promocionTipo.setTextoPromocion(promocionTicket.getTextoPromocion());
		}

		return true;
	}

	protected TramoBpDto elegirTramo(BigDecimal total) {
		TramoBpDto tramoElegido = null;
		for (TramoBpDto tramo : tramos) {
			if (BigDecimalUtil.isMayorOrIgual(total, tramo.getImporteTramo())) {
				if (tramoElegido == null) {
					tramoElegido = tramo;
				}
				else {
					if (BigDecimalUtil.isMayorOrIgual(tramo.getImporteTramo(), tramoElegido.getImporteTramo())) {
						tramoElegido = tramo;
					}
				}
			}
		}
		return tramoElegido;
	}

	private class TramoBpDto {

		private BigDecimal importeTramo;

		private BigDecimal puntosTramo;

		private String textoAplica;

		public TramoBpDto(BigDecimal importeTramo, BigDecimal puntosTramo, String textoAplica) {
			super();
			this.importeTramo = importeTramo;
			this.puntosTramo = puntosTramo;
			this.textoAplica = textoAplica;
		}

		public BigDecimal getImporteTramo() {
			return importeTramo;
		}

		public BigDecimal getPuntosTramo() {
			return puntosTramo;
		}

		public String getTextoAplica() {
			return textoAplica;
		}

	}

}
