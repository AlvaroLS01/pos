package com.comerzzia.dinosol.pos.services.articulos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;

@Service
public class QRBalanzaService {
	private Logger log = Logger.getLogger(QRBalanzaService.class);
	
	private static final int LONGITUD_SUBDEPARTAMENTO = 1;
	private static final int LONGITUD_PLU = 5;
	private static final int LONGITUD_PESO = 5;
	private static final int LONGITUD_PRECIO_UNITARIO = 5;
	private static final int LONGITUD_IMPORTE_LINEA = 5;
	private static final int NUMERO_DIGITOS_CABECERA = 41;
	private static final int NUMERO_DIGITOS_LINEA = 21;
	private static final int NUMERO_DIGITOS_CHECK = 1;
	private static final int OPERADOR_CABECERA = 25;
	
	@Autowired
	private DinoArticulosService dinoArticulosService;
	
	public class LineaQRBalanza {
		protected String departamento;
		protected String plu;
		protected String codart;
		protected BigDecimal cantidad;
		protected BigDecimal precio;
		protected BigDecimal importe;
		protected String operador;
		
		public String getDepartamento() {
			return departamento;
		}
		public void setDepartamento(String departamento) {
			this.departamento = departamento;
		}
		public String getPlu() {
			return plu;
		}
		public void setPlu(String plu) {
			this.plu = plu;
		}
		public String getCodart() {
			return codart;
		}
		public void setCodart(String codart) {
			this.codart = codart;
		}
		public BigDecimal getCantidad() {
			return cantidad;
		}
		public void setCantidad(BigDecimal cantidad) {
			this.cantidad = cantidad;
		}
		public BigDecimal getPrecio() {
			return precio;
		}
		public void setPrecio(BigDecimal precio) {
			this.precio = precio;
		}
		public BigDecimal getImporte() {
			return importe;
		}
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}
		public String getOperador() {
			return operador;
		}
		public void setOperador(String operador) {
			this.operador = operador;
		}		
	}
	
	private BigDecimal getNumericValue(String campo, String valor, int divisor) throws Exception {
		try {
		   return new BigDecimal(valor).divide(new BigDecimal(divisor));
		} catch (NumberFormatException e) {
		   throw new Exception("Formato del campo " + campo + " incorrecto: " + valor);
			
		}
	}
	
	public List<LineaQRBalanza> leerQrBalanza(String lecturaQR) throws Exception {
		// Rescatamos el contenido de la cabecera
		String contenidoCabecera = "";
		String codOperador = "";

		if (lecturaQR.length() >= (NUMERO_DIGITOS_CABECERA + NUMERO_DIGITOS_LINEA + NUMERO_DIGITOS_CHECK)) {
			contenidoCabecera = lecturaQR.substring(0, NUMERO_DIGITOS_CABECERA);
			codOperador = contenidoCabecera.substring(OPERADOR_CABECERA, OPERADOR_CABECERA + 8);
		} else {
			String mensajeError = "QR de Balanza mal formado: " + lecturaQR;
			log.error("leerQrBalanza() - " + mensajeError);

			throw new Exception(mensajeError);			
		}

		// Rescatamos el contenido de las lineas, restando el último dígito 
		String contenidoLineas = lecturaQR.substring(NUMERO_DIGITOS_CABECERA);
		contenidoLineas = contenidoLineas.substring(0, contenidoLineas.length());

		int numeroLineas = 0;
		// Calculamos el número de lineas 
		if ((contenidoLineas.length() / NUMERO_DIGITOS_LINEA) == 1) {
			numeroLineas = 1;
		}
		else if ((contenidoLineas.length() / NUMERO_DIGITOS_LINEA) > 1) {
			numeroLineas = contenidoLineas.length() / NUMERO_DIGITOS_LINEA;
		}

		// Sacamos los datos de las lineas, para cada linea ahi un artículo 
		int contadorSubstring = 0;
		
		List<LineaQRBalanza> lineasQRBalanza = new ArrayList<LineaQRBalanza>();

		for (int i = 1; i <= numeroLineas; i++) {
			LineaQRBalanza lineaQRBalanza = new LineaQRBalanza();
			lineaQRBalanza.setPlu(contenidoLineas.substring(1 + contadorSubstring, LONGITUD_PLU + LONGITUD_SUBDEPARTAMENTO + contadorSubstring));

			// obtener código de artículo del código de PLU
			try {
				// Se obtiene el artículo por el PLU y por la sección
				ArticuloBean articuloBean = dinoArticulosService.buscarArticuloEtiquetaPrecio(lineaQRBalanza.getPlu());

				lineaQRBalanza.setCodart(articuloBean.getCodArticulo());
			}
			catch (ArticulosServiceException e) {
				String mensajeError = "No se encontró el artículo con PLU " + lineaQRBalanza.getPlu();
				log.error("leerQrBalanza() - " + mensajeError);

				throw new Exception(mensajeError);
			}
			
			BigDecimal cantidad = getNumericValue("Cantidad", contenidoLineas.substring(LONGITUD_PLU + LONGITUD_SUBDEPARTAMENTO + contadorSubstring, LONGITUD_PLU + LONGITUD_SUBDEPARTAMENTO + LONGITUD_PESO + contadorSubstring), 1000);
			BigDecimal precio = getNumericValue("Precio",  contenidoLineas.substring(LONGITUD_PLU + LONGITUD_SUBDEPARTAMENTO + LONGITUD_PESO + contadorSubstring, LONGITUD_PLU + LONGITUD_SUBDEPARTAMENTO + LONGITUD_PESO
			        + LONGITUD_PRECIO_UNITARIO + contadorSubstring), 100);
			BigDecimal importe = getNumericValue("Importe", contenidoLineas.substring(LONGITUD_PLU + LONGITUD_SUBDEPARTAMENTO + LONGITUD_PESO + LONGITUD_PRECIO_UNITARIO + contadorSubstring, LONGITUD_PLU + LONGITUD_SUBDEPARTAMENTO
			        + LONGITUD_PESO + LONGITUD_PRECIO_UNITARIO + LONGITUD_IMPORTE_LINEA + contadorSubstring), 100);
			
			
			lineaQRBalanza.setDepartamento(contenidoLineas.substring(0 + contadorSubstring, LONGITUD_SUBDEPARTAMENTO + contadorSubstring));			
			lineaQRBalanza.setCantidad(cantidad);
			lineaQRBalanza.setPrecio(precio);
			lineaQRBalanza.setImporte(importe);
			lineaQRBalanza.setOperador(codOperador);
			
			lineasQRBalanza.add(lineaQRBalanza);

			contadorSubstring = contadorSubstring + NUMERO_DIGITOS_LINEA;
		}
		
		return lineasQRBalanza;
	}
}
