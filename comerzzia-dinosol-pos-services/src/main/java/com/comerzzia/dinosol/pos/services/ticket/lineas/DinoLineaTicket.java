package com.comerzzia.dinosol.pos.services.ticket.lineas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.util.xml.HoraXmlAdapter;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;

@SuppressWarnings("serial")
@Component
@Primary
@Scope("prototype")
public class DinoLineaTicket extends LineaTicket {

	@XmlElement(name = "codSeccion")
	private String codSeccion;
	@XmlElement(name = "codOperador")
	private String codOperador;
	@XmlElement(name = "precioTotalManual")
	private BigDecimal precioTotalManual;
	@XmlElement(name = "recargaMovil")
	private boolean recargaMovil;
	@XmlElement(name = "contenidoDigital")
	private boolean contendioDigital;
	@XmlElement(name = "codMotivo")
	private String codMotivo;
	@XmlElement
	private TarjetaRegaloDto tarjetaRegalo;
	@XmlElement(name = "idLineaAsociado")
	private Integer idLineaAsociado;
	@XmlTransient
	private boolean esPlastico;
	@XmlTransient
	private String codArtPlasticoAsociado;

	@XmlElement(name = "hora_registro")
	@XmlJavaTypeAdapter(HoraXmlAdapter.class)
	private Date horaRegistro;
	
	@XmlTransient
	private String qrLiquidacionOrigen;

	public String getCodMotivo() {
		return codMotivo;
	}

	public void setCodMotivo(String codMotivo) {
		this.codMotivo = codMotivo;
	}

	public String getCodSeccion() {
		return codSeccion;
	}

	public void setCodSeccion(String codSeccion) {
		this.codSeccion = codSeccion;
	}

	public String getCodOperador() {
		return codOperador;
	}

	public void setCodOperador(String codOperador) {
		this.codOperador = codOperador;
	}

	public BigDecimal getPrecioTotalManual() {
		return precioTotalManual;
	}

	public void setPrecioTotalManual(BigDecimal precioTotalManual) {
		this.precioTotalManual = precioTotalManual;
	}

	public void removePromocionCandidata(PromocionLineaTicket promo) {
		this.promocionesCandidatas.remove(promo);
	}

	public boolean isRecargaMovil() {
		return recargaMovil;
	}

	public void setRecargaMovil(boolean recargaMovil) {
		this.recargaMovil = recargaMovil;
	}

	public boolean isContendioDigital() {
		return contendioDigital;
	}

	public void setContendioDigital(boolean contendioDigital) {
		this.contendioDigital = contendioDigital;
	}

	public TarjetaRegaloDto getTarjetaRegalo() {
		return tarjetaRegalo;
	}

	public void setTarjetaRegalo(TarjetaRegaloDto tarjetaRegalo) {
		this.tarjetaRegalo = tarjetaRegalo;
	}

	public boolean isEsPlastico() {
		return esPlastico;
	}

	public void setEsPlastico(boolean esPlastico) {
		this.esPlastico = esPlastico;
	}

	public String getCodArtPlasticoAsociado() {
		return codArtPlasticoAsociado;
	}

	public void setCodArtPlasticoAsociado(String codArtPlasticoAsociado) {
		this.codArtPlasticoAsociado = codArtPlasticoAsociado;
	}

	public void setIdLineaAsociado(Integer idLineaAsociado) {
		this.idLineaAsociado = idLineaAsociado;
	}

	public Integer getIdLineaAsociado() {
		return idLineaAsociado;
	}

	@Override
	public void recalcularImporteFinal() {
		Long idTratamientoImpuestos = getCabecera().getTicket().getIdTratImpuestos();

		importeTotalPromociones = BigDecimal.ZERO;
		importeTotalPromocionesMenosIngreso = BigDecimal.ZERO;
		BigDecimal importeTotalPromocionesSinRedondear = BigDecimal.ZERO;
		BigDecimal importeTotalConDtoSinRedondear = BigDecimal.ZERO;
		boolean cantidadTieneDecimales = !BigDecimalUtil.isIgualACero(cantidad.remainder(BigDecimal.ONE));
		BigDecimal importeLineaConImpuestos = BigDecimal.ZERO;

		if (tieneDescuentoManual()) {
			importeTotalConDtoSinRedondear = BigDecimalUtil.menosPorcentajeR4(getPrecioTotalSinDto(), descuentoManual).multiply(cantidad);
			importeTotalConDto = BigDecimalUtil.redondear(importeTotalConDtoSinRedondear);
			importeLineaConImpuestos = importeTotalConDto;
		}
		else if (promociones != null) {
			for (IPromocionLineaTicket promocionLinea : promociones) {
				BigDecimal descuento = promocionLinea.getImporteTotalDtoMenosMargen();
				if (cantidadTieneDecimales) {
					// En lineas de peso (cantidad con decimales) usamos descuento sin redondear para no perder
					// precisión en los siguientes cálculos
					descuento = promocionLinea.getImporteTotalDtoMenosMargenSinRedondear();
				}
				importeTotalPromociones = importeTotalPromociones.add(descuento);
				importeTotalPromocionesSinRedondear = importeTotalPromocionesSinRedondear.add(promocionLinea.getImporteTotalDtoMenosMargenSinRedondear());
				importeTotalPromocionesMenosIngreso = importeTotalPromocionesMenosIngreso.add(promocionLinea.getImporteTotalDtoMenosIngreso());
			}
			importeTotalConDto = BigDecimalUtil.redondear(getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromociones));
			importeTotalConDtoSinRedondear = getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromocionesSinRedondear);

			// Para calcular el precio con impuestos y el importe sin impuestos se mantiene
			// toda la precisión de la operación de cantidad*preciototal-descuento
			importeLineaConImpuestos = getCantidad().multiply(precioTotalSinDto).subtract(importeTotalPromociones);
		}

		Sesion sesion = SpringContext.getBean(Sesion.class);
		SesionImpuestos sesionImpuestos = sesion.getImpuestos();
		importeConDto = sesionImpuestos.getPrecioSinImpuestos(getCodImpuesto(), BigDecimalUtil.redondear(importeLineaConImpuestos), idTratamientoImpuestos);

		precioConDto = BigDecimalUtil.isIgualACero(cantidad) ? BigDecimal.ZERO : importeConDto.divide(cantidad, 4, RoundingMode.HALF_UP);
		precioTotalConDto = BigDecimalUtil.isIgualACero(cantidad) ? BigDecimal.ZERO : importeLineaConImpuestos.divide(cantidad, 2, RoundingMode.HALF_UP);
		descuento = BigDecimal.ZERO;
		if (tieneDescuentoManual()) {
			descuento = descuentoManual;
		}
		else if (!promociones.isEmpty()) {
			// Calculamos el porcentaje de descuento desde el importe final sin redondear para que coincida con el
			// porcentaje
			// exacto configurado en la promoción (por ejemplo, si configuro un 10% dto, que no salga un 9,99% tras los
			// cálculos)
			descuento = BigDecimalUtil.getTantoPorCientoMenos(getImporteTotalSinDtoSinRedondear(), importeTotalConDtoSinRedondear);

			// Confirmamos que el % descuento calculado, al aplicarlo sobre el precio unitario y multiplicarlo por la
			// cantidad es igual
			// al importe final previamente calculado. En caso contrario, volveremos a calcular el % de descuento a
			// partir del importe redondeado
			if (!BigDecimalUtil.isIgual(BigDecimalUtil.redondear(BigDecimalUtil.menosPorcentajeR(getPrecioTotalSinDto(), BigDecimalUtil.redondear(descuento)).multiply(cantidad)),
			        (BigDecimalUtil.redondear(importeTotalConDto)))) {
				descuento = BigDecimalUtil.getTantoPorCientoMenos(getImporteTotalSinDtoSinRedondear(), importeTotalConDto);
			}
		}
	}

	public BigDecimal getImporteTotalSinDtoSinRedondear() {
		return getPrecioTotalSinDto().multiply(cantidad);
	}

	public Date getHoraRegistro() {
		return horaRegistro;
	}

	public void setHoraRegistro(Date horaRegistro) {
		this.horaRegistro = horaRegistro;
	}
	
	public boolean isParking() {
		String codartParking = SpringContext.getBean(VariablesServices.class).getVariableAsString("X_PARKING_CODART");
		return codartParking.equals(getCodArticulo());
	}

	public String getQrLiquidacionOrigen() {
		return qrLiquidacionOrigen;
	}

	public void setQrLiquidacionOrigen(String qrLiquidacionOrigen) {
		this.qrLiquidacionOrigen = qrLiquidacionOrigen;
	}

}
