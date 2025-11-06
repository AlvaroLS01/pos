package com.comerzzia.dinosol.pos.services.codbarrasesp.liquidacion;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.fechas.FechaException;
import com.comerzzia.core.util.fechas.Fechas;
import com.comerzzia.pos.core.gui.exception.ValidationException;
import com.comerzzia.pos.util.i18n.I18N;

import org.apache.log4j.Logger;

public class QRLiquidacionDTO { // DIN-112

	protected Logger log = Logger.getLogger(getClass());

	protected String codigoQr;
	protected String prefijo;
	protected String tienda;
	protected String codEAN13;
	protected String codOperador;
	protected BigDecimal precioLiquidacion;
	protected QRTipoLiquidacion tipoLiquidacion;
	protected Date finFechaVigencia;

	protected int anyoJuliano;
	protected int diaJuliano;

	SimpleDateFormat formatter;

	private String codArticulo;

	public QRLiquidacionDTO(String codigoQr, String prefijo, String tienda, String codEAN13, String codOperador, BigDecimal precioLiquidacion, String tipoLiquidacion, String diaJuliano, String anyo) {
		super();
		formatter = new SimpleDateFormat(Fecha.PATRON_FECHA_CORTA);

		// obligatorios
		this.prefijo = prefijo;
		this.codEAN13 = codEAN13;
		this.precioLiquidacion = precioLiquidacion;
		this.finFechaVigencia = convierteFechaVigencia(diaJuliano, anyo);
		this.tipoLiquidacion = convierteTipoLiquidacion(tipoLiquidacion);
		// obligatorios

		this.tienda = tienda;
		this.codOperador = codOperador;
		this.codigoQr = codigoQr;

		this.anyoJuliano = Integer.valueOf(anyo);
		this.diaJuliano = Integer.valueOf(diaJuliano);
	}

	public String getPrefijo() {
		return prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	public String getTienda() {
		return tienda;
	}

	public void setTienda(String tienda) {
		this.tienda = tienda;
	}

	public String getCodEAN13() {
		return codEAN13;
	}

	public void setCodEAN13(String codEAN13) {
		this.codEAN13 = codEAN13;
	}

	public String getCodOperador() {
		return codOperador;
	}

	public void setCodOperador(String codOperador) {
		this.codOperador = codOperador;
	}

	public BigDecimal getPrecioLiquidacion() {
		return precioLiquidacion;
	}

	public void setPrecioLiquidacion(BigDecimal precioLiquidacion) {
		this.precioLiquidacion = precioLiquidacion;
	}

	public QRTipoLiquidacion getTipoLiquidacion() {
		return tipoLiquidacion;
	}

	public void setTipoLiquidacion(QRTipoLiquidacion tipoLiquidacion) {
		this.tipoLiquidacion = tipoLiquidacion;
	}

	public Date getFinFechaVigencia() {
		return finFechaVigencia;
	}

	public void setFinFechaVigencia(Date finFechaVigencia) {
		this.finFechaVigencia = finFechaVigencia;
	}

	public String getCodigoQr() {
		return codigoQr;
	}

	public void setCodigoQr(String codigoQr) {
		this.codigoQr = codigoQr;
	}

	public String getCodArticulo() {
		return codArticulo;
	}

	public void setCodArticulo(String codArticulo) {
		this.codArticulo = codArticulo;
	}
	// -------- MÉTODOS PERSONALIZADOS -------------

	protected Date convierteFechaVigencia(String diaJuliano, String anyo) {
		log.debug("convierteFechaVigencia() - Conviertiendo fin fecha vigencia");
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.set(Calendar.YEAR, Integer.valueOf(anyo));
			calendar.set(Calendar.DAY_OF_YEAR, Integer.parseInt(diaJuliano));
			Date time = calendar.getTime();

			String timeFormated = formatter.format(time);
			Date finFechaVigencia = Fechas.getFecha(timeFormated);
			return finFechaVigencia;
		}
		catch (NumberFormatException | FechaException e) {
			log.error("convierteFechaVigencia() - Error Conviertiendo fin fecha vigencia: " + e.getMessage(), e);
			return null;
		}
	}

	protected QRTipoLiquidacion convierteTipoLiquidacion(String tipoLiquidacion) {
		log.debug("convierteTipoLiquidacion() - convirtiendo tipo liquidacion...");

		if (tipoLiquidacion.equals(QRTipoLiquidacion.QR_LIQUIDACION_CAMBIO_PRECIO.tipo)) {
			return QRTipoLiquidacion.QR_LIQUIDACION_CAMBIO_PRECIO;
		}
		else if (tipoLiquidacion.equals(QRTipoLiquidacion.QR_LIQUIDACION_DESCUENTO.tipo)) {
			return QRTipoLiquidacion.QR_LIQUIDACION_DESCUENTO;
		}
		else {
			log.error("convierteTipoLiquidacion() - Error convirtiendo tipo de liquidacion, no existe tipo de liquidacion: " + tipoLiquidacion);
			return null;
		}
	}

	public void valida() throws ValidationException {
		log.debug("valida() - Validanto QRLiquidacionDTO...");

		List<String> camposObligatoriosNulos = new ArrayList<>();
		if (prefijo == null) {
			camposObligatoriosNulos.add("prefijo");
		}
		if (precioLiquidacion == null) {
			camposObligatoriosNulos.add("precioLiquidacion");
		}
		if (finFechaVigencia == null) {
			camposObligatoriosNulos.add("finFechaVigencia");
		}
		if (tipoLiquidacion == null) {
			camposObligatoriosNulos.add("tipoLiquidacion");
		}

		if (!camposObligatoriosNulos.isEmpty()) {
			throw new ValidationException(I18N.getTexto("Error al parsear QR LIQUIDACION, campos nulos: {0}", camposObligatoriosNulos.toString()));
		}
	}

	public boolean esVigente() {
		log.debug("esVigente() - Comprobando vigencia de QR LIQUIDACION...");

		boolean vigente = false;

		Calendar fechaJuliana = Calendar.getInstance();
		fechaJuliana.clear();
		fechaJuliana.set(Calendar.YEAR, anyoJuliano);
		fechaJuliana.set(Calendar.DAY_OF_YEAR, diaJuliano);
		fechaJuliana.set(Calendar.HOUR_OF_DAY, 0);
		fechaJuliana.set(Calendar.MINUTE, 0);
		fechaJuliana.set(Calendar.SECOND, 0);
		fechaJuliana.set(Calendar.MILLISECOND, 0);

		Calendar fechaActual = Calendar.getInstance();
		fechaActual.set(Calendar.HOUR_OF_DAY, 0);
		fechaActual.set(Calendar.MINUTE, 0);
		fechaActual.set(Calendar.SECOND, 0);
		fechaActual.set(Calendar.MILLISECOND, 0);

		if (fechaJuliana.before(fechaActual)) {
			// QR no vigente
		}
		else if (fechaJuliana.after(fechaActual)) {
			vigente = true;
		}
		else {
			vigente = true;
		}

		log.debug("esVigente() - El QR LIQUIDACION " + (vigente ? " es vigente" : " no es vigente"));

		return vigente;
	}

	@Override
	public String toString() {
		return "QRLiquidacionDTO [log=" + log + ", prefijo=" + prefijo + ", tienda=" + tienda + ", codEAN13=" + codEAN13 + ", codOperador=" + codOperador + ", precioLiquidacion=" + precioLiquidacion
		        + ", tipoLiquidacion=" + tipoLiquidacion + ", finFechaVigencia=" + finFechaVigencia + "]";
	}

	public void validaTienda(String codAlmacen) throws ValidationException {
		if (!tienda.equals(codAlmacen)) {
			throw new ValidationException(I18N.getTexto("La tienda indicada en el QR no corresponde con esta tienda."));
		}
	}

}
