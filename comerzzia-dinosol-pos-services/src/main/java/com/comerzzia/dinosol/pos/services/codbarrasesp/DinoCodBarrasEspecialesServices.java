package com.comerzzia.dinosol.pos.services.codbarrasesp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.articulos.DinoArticulosService;
import com.comerzzia.dinosol.pos.services.codbarrasesp.liquidacion.QRLiquidacionDTO;
import com.comerzzia.pos.core.gui.exception.ValidationException;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesServices;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
public class DinoCodBarrasEspecialesServices extends CodBarrasEspecialesServices {

	@Autowired
	private DinoArticulosService articulosService;

	@Override
	public CodigoBarrasBean esCodigoBarrasEspecial(String codigo) {
		CodigoBarrasBean codBarrasEspecial = null;

		for (CodigoBarrasBean codigoBarras : codigos) {
			if (codigo.length() < codigoBarras.getPrefijo().length()) {
				continue;
			}
			String prefijoCodigo = codigo.substring(0, codigoBarras.getPrefijo().length());

			if (prefijoCodigo.equals(codigoBarras.getPrefijo())) {
				codBarrasEspecial = codigoBarras;
				break;
			}
		}

		if(codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("QR BALANZA") && codigo.length() <= 13) {
			return null;
		}
		
		if(codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("FLYER") && codigo.length() != 13) {
			return null;
		}
		
		if(codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("Etiquetas Precios") && codigo.length() != 13) {
			return null;
		}
		
		if(codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("Solo PLU") && codigo.length() != 8) {
			return null;
		}
		
		if(codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("QR LIQUIDACION") && codigo.length() == 45) {
			CodigoBarrasBean codigoBarras = new CodigoBarrasBean();
			codigoBarras.setCodigoIntroducido(codigo);
			codigoBarras.setCodticket("X");
			codigoBarras.setDescripcion(codBarrasEspecial.getDescripcion());
			return codigoBarras;
		}

		if (codBarrasEspecial != null) {
			CodigoBarrasBean codigoBarras = new CodigoBarrasBean();
			codigoBarras.setCodigoIntroducido(codigo);
			codigoBarras.setDescripcion(codBarrasEspecial.getDescripcion());
			codigoBarras.setPrefijo(codBarrasEspecial.getPrefijo());
			codigoBarras.setFidelizacion(codBarrasEspecial.getFidelizacion());

			String[] codArtCodBar = codBarrasEspecial.getCodart().split("\\|");
			String[] ticketCodBar = codBarrasEspecial.getCodticket().split("\\|");
			String[] cantidadCodBar = codBarrasEspecial.getCantidad().split("\\|");
			String[] precioCodBar = codBarrasEspecial.getPrecio().split("\\|");

			if (codArtCodBar.length > 0) {
				int inicioCodArt = Integer.valueOf(codArtCodBar[0]) - 1;
				int cantCodArt = Integer.valueOf(codArtCodBar[1]);
				if (cantCodArt > 0) {
					String codArt = codigo.substring(inicioCodArt, inicioCodArt + cantCodArt);
					codigoBarras.setCodart(codArt);
				}
			}
			if (ticketCodBar.length > 0) {
				if(ticketCodBar[0].equals("X")) {
					codigoBarras.setCodticket("X");
				}
				else {
					int inicioCodTicket = Integer.valueOf(ticketCodBar[0]) - 1;
					int cantCodTicket = Integer.valueOf(ticketCodBar[1]);
					if (cantCodTicket > 0) {
						String codTicket = codigo.substring(inicioCodTicket, inicioCodTicket + cantCodTicket);
						codigoBarras.setCodticket(codTicket);
					}
				}
			}

			char decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
			if(decimalSeparator == '.') {
				decimalSeparator = ',';
			}
			int posCursor = 0;

			int inicioPrecio = Integer.valueOf(precioCodBar[0]) - 1;
			int cantPrecioEntero = Integer.valueOf(precioCodBar[1]);
			int cantPrecioDecimal = Integer.valueOf(precioCodBar[2]);
			if (cantPrecioEntero > 0 || cantPrecioDecimal > 0) {
				posCursor = inicioPrecio;
				String precio = "";
				if (cantPrecioEntero > 0) {
					precio = codigo.substring(posCursor, posCursor + cantPrecioEntero);
				}
				else {
					precio = "0";
				}
				precio = precio.concat(decimalSeparator + "");
				posCursor += cantPrecioEntero;
				if (cantPrecioDecimal > 0) {
					precio = precio.concat(codigo.substring(posCursor, posCursor + cantPrecioDecimal));
				}
				else {
					precio = precio.concat("0");
				}
				codigoBarras.setPrecio(precio);
			}

			int inicioCantidad = Integer.valueOf(cantidadCodBar[0]) - 1;
			int cantCantidadEntero = Integer.valueOf(cantidadCodBar[1]);
			int cantCantidadDecimal = Integer.valueOf(cantidadCodBar[2]);
			if (cantCantidadEntero > 0 || cantCantidadDecimal > 0) {
				posCursor = inicioCantidad;
				String cantCodBar = null;
				if (cantCantidadEntero > 0) {
					cantCodBar = codigo.substring(posCursor, posCursor + cantCantidadEntero);
				}
				else {
					cantCodBar = "0";
				}
				posCursor += cantCantidadEntero;
				cantCodBar = cantCodBar.concat(decimalSeparator + "");
				if (cantCantidadDecimal > 0) {
					cantCodBar = cantCodBar.concat(codigo.substring(posCursor, posCursor + cantCantidadDecimal));
				}
				else {
					cantCodBar = cantCodBar.concat("0");
				}
				codigoBarras.setCantidad(cantCodBar);
			}

			if (codigoBarras != null && StringUtils.isNotBlank(codigoBarras.getCodticket()) && codigoBarras.getCodticket().trim().equals("X")) {
				try {
					ArticuloBean articulo = articulosService.buscarArticuloEtiquetaPrecio(codigoBarras.getCodart());
					codigoBarras.setCodart(articulo.getCodArticulo());
					codigoBarras.setCodticket(null);
				}
				catch (Exception e) {
					log.error("esCodigoBarrasEspecial() - Ha habido un problema al consultar el código de barras especial: " + e.getMessage(), e);
					return null;
				}
			}

			return codigoBarras;
		}

		return null;
	}
	
	
	
	public QRLiquidacionDTO obtenDatosQrLiquidacionDTO(String codigoQrLiquidacion) throws DinoCodBarrasEspecialesException {
		log.debug("obtenDatosQrLiquidacionDTO()- Obteniendo datos del código QR de Liquidación: " + codigoQrLiquidacion);

		try {
			String prefijo = codigoQrLiquidacion.substring(0, 3);
			String tienda = codigoQrLiquidacion.substring(3, 7);
			String codEAN13 = new BigDecimal(codigoQrLiquidacion.substring(7, 20)).toString();
			String codOperador = String.valueOf(codigoQrLiquidacion.substring(20, 30)).trim();
			String precioLiquidacionParteEntera = codigoQrLiquidacion.substring(31, 35);
			String precioLiquidacionParteDecimal = codigoQrLiquidacion.substring(35, 37);
			BigDecimal precioLiquidacion = new BigDecimal(precioLiquidacionParteEntera + "." + precioLiquidacionParteDecimal);
			String tipoLiquidacion = codigoQrLiquidacion.substring(37, 38);
			String diaJuliano = codigoQrLiquidacion.substring(38, 41);
			String anyo = codigoQrLiquidacion.substring(41, 45);

			QRLiquidacionDTO qRLiquidacionDTO = new QRLiquidacionDTO(codigoQrLiquidacion, prefijo, tienda, codEAN13, codOperador, precioLiquidacion, tipoLiquidacion, diaJuliano, anyo);
			qRLiquidacionDTO.valida();
			qRLiquidacionDTO.validaTienda(sesion.getAplicacion().getCodAlmacen());
			
			if(codEAN13.startsWith("251")) {
				CodigoBarrasBean codigoBarrasEspecial = esCodigoBarrasEspecial(codEAN13);
				qRLiquidacionDTO.setCodArticulo(codigoBarrasEspecial.getCodart());
			}
			else {
				ArticuloCodBarraBean articulo = articulosService.consultarCodigoBarras(codEAN13);
				if(articulo != null) {
					qRLiquidacionDTO.setCodArticulo(articulo.getCodArticulo());
				}
				else {
					throw new ValidationException(I18N.getTexto("El código introducido no coincide con ningún artículo"));
				}
			}
			
			log.debug("obtenDatosQrLiquidacionDTO() - QR LIQUIDACION leído con éxito. ");
			log.debug(qRLiquidacionDTO.toString());

			return qRLiquidacionDTO;
		}
		catch (ValidationException e) {
			log.error("obtenDatosQrLiquidacionDTO()- Error al leer el QR de liquidación: " + e.getMessage());
			throw new DinoCodBarrasEspecialesException(e.getMessage());
		}
		catch (Exception e) {
			String errorParseo = I18N.getTexto("Error inesperado al parsear QR LIQUIDACION");
			log.error("obtenDatosQrLiquidacionDTO() - " + errorParseo + ": " + e.getMessage(), e);
			throw new DinoCodBarrasEspecialesException(errorParseo);
		}
	}
	
	public boolean esCodigoBarrasEspecialQRLiquidacion(String codigo) {
		log.debug("esCodigoBarrasEspecialQRLiquidacion() - Comprobando si el código: " + codigo + " , es codigo de barras especial QR LIQUIDACION");
		
		CodigoBarrasBean codBarrasEspecial = null;

		for (CodigoBarrasBean codigoBarras : codigos) {
			if (codigo.length() < codigoBarras.getPrefijo().length()) {
				continue;
			}
			String prefijoCodigo = codigo.substring(0, codigoBarras.getPrefijo().length());

			if (prefijoCodigo.equals(codigoBarras.getPrefijo())) {
				codBarrasEspecial = codigoBarras;
				break;
			}
		}
		boolean codBarrasQRLiquidacion = codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("QR LIQUIDACION") && codigo.length() <= 46;
		
		return codBarrasQRLiquidacion;
	}

	public String generarCodBarras241Equivalente(String codBarras, LineaTicketAbstract linea) {
		try {
			log.debug("generarCodBarras241Equivalente() - Para el código " + codBarras + " se va a generar el código de barras 241 equivalente");
			DecimalFormat formato = new DecimalFormat("000.00");
	        String importeFormateado = formato.format(linea.getImporteTotalConDto()).replace(",", "").replace(".", "");
			String newEan13 = "241" + codBarras.substring(3, 7) + importeFormateado;
			newEan13 = newEan13 + new EAN13CheckDigit().calculate(newEan13);
			log.debug("generarCodBarras241Equivalente() - Código generado: " + newEan13);
			return newEan13;
		}
		catch (Exception e) {
			log.error("generarCodBarras241Equivalente() - No se ha podido generar el código de barras 241 equivalente: " + e.getMessage(), e);
			return codBarras;
		}
	}

}
