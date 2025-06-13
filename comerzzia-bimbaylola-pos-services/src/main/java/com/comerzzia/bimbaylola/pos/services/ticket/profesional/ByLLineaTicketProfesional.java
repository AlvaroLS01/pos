package com.comerzzia.bimbaylola.pos.services.ticket.profesional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.bimbaylola.pos.persistence.ticket.articulos.agregarnotainformativa.NotaInformativaBean;
import com.comerzzia.bimbaylola.pos.services.ticket.ImpuestoLinea;
import com.comerzzia.bimbaylola.pos.services.ticket.LineaTicketTrazabilidad;
import com.comerzzia.bimbaylola.pos.services.vertex.LineaDetailVertex;
import com.comerzzia.bimbaylola.pos.services.vertex.LineaVertex;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.ticket.profesional.LineaTicketProfesional;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "linea")
public class ByLLineaTicketProfesional extends LineaTicketProfesional {

	private static final long serialVersionUID = -574720174884441766L;

	@XmlTransient
	private Boolean nuevo;

	@XmlElement(name = "cantidadEntregada")
	private String cantidadEntregada;
	@XmlElement(name = "cantidadCancelada")
	private String cantidadCancelada;
	@XmlElement(name = "estado_linea_apartado")
	private Short estadoLineaApartado;

	@XmlElement(name = "nota_informativa")
	private NotaInformativaBean notaInformativa;

	@XmlElement(name = "precio_venta_ref")
	protected BigDecimal precioVentaRef;
	@XmlElement(name = "precio_venta_ref_total")
	protected BigDecimal precioVentaRefTotal;

	@XmlElement(name = "descuento_sobre_inicial")
	protected BigDecimal descuentoSobreInicial;

	@XmlElement(name = "trazabilidad")
	protected LineaTicketTrazabilidad trazabilidad;

	@XmlElementWrapper(name = "impuestos_linea")
	@XmlElement(name = "impuesto_linea")
	protected List<ImpuestoLinea> impuestosLinea;

	@XmlElement(name = "tax_breakdown_line")
	protected LineaVertex lineaVertex;

	@XmlElementWrapper(name = "tax_breakdown_lines_details")
	@XmlElement(name = "tax_breakdown_line_detail")
	protected List<LineaDetailVertex> listaImpuestosVertex = new ArrayList<LineaDetailVertex>();

	@XmlElement(name = "totalImpuestos")
	protected BigDecimal totalImpuestos;

	@XmlElement(name = "porcentaje")
	protected BigDecimal porcentaje;

	@XmlElement(name = "porcentajeRecargo")
	protected BigDecimal porcentajeRecargo;
	
	@XmlElement(name = "cambio_precio_manual")
	protected Boolean cambioPrecioManual;
	
	
	public BigDecimal getTotalImpuestos() {
		return totalImpuestos;
	}

	public void setTotalImpuestos(BigDecimal totalImpuestos) {
		this.totalImpuestos = totalImpuestos;
	}

	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
	}

	public BigDecimal getPorcentajeRecargo() {
		return porcentajeRecargo;
	}

	public void setPorcentajeRecargo(BigDecimal porcentajeRecargo) {
		this.porcentajeRecargo = porcentajeRecargo;
	}

	public void inicializarLineaVertex() {
		lineaVertex = new LineaVertex();
	}

	public void inicializarListaImpuestos() {
		listaImpuestosVertex = new ArrayList<LineaDetailVertex>();
	}

	public ByLLineaTicketProfesional() {
		impuestosLinea = new ArrayList<>();
	}

	public NotaInformativaBean getNotaInformativa() {
		return notaInformativa;
	}

	public void setNotaInformativa(NotaInformativaBean notaInformativa) {
		this.notaInformativa = notaInformativa;
	}

	public String getCantidadEntregada() {
		return cantidadEntregada;
	}

	public void setCantidadEntregada(String cantidadEntregada) {
		this.cantidadEntregada = cantidadEntregada;
	}

	public String getCantidadCancelada() {
		return cantidadCancelada;
	}

	public void setCantidadCancelada(String cantidadCancelada) {
		this.cantidadCancelada = cantidadCancelada;
	}

	public Short getEstadoLineaApartado() {
		return estadoLineaApartado;
	}

	public void setEstadoLineaApartado(Short estadoLineaApartado) {
		this.estadoLineaApartado = estadoLineaApartado;
	}

	public Boolean getNuevo() {
		return nuevo;
	}

	public void setNuevo(Boolean nuevo) {
		this.nuevo = nuevo;
	}

	public BigDecimal getPrecioVentaRef() {
		return precioVentaRef;
	}

	public void setPrecioVentaRef(BigDecimal precioVentaRef) {
		this.precioVentaRef = precioVentaRef;
	}

	public BigDecimal getPrecioVentaRefTotal() {
		return precioVentaRefTotal;
	}

	public void setPrecioVentaRefTotal(BigDecimal precioVentaRefTotal) {
		this.precioVentaRefTotal = precioVentaRefTotal;
	}

	public BigDecimal getDescuentoSobreInicial() {
		return descuentoSobreInicial;
	}

	public void setDescuentoSobreInicial(BigDecimal descuentoSobreInicial) {
		this.descuentoSobreInicial = descuentoSobreInicial;
	}

	public LineaTicketTrazabilidad getTrazabilidad() {
		return trazabilidad;
	}

	public void setTrazabilidad(LineaTicketTrazabilidad trazabilidad) {
		this.trazabilidad = trazabilidad;
	}

	public List<ImpuestoLinea> getImpuestosLinea() {
		return impuestosLinea;
	}

	public void setImpuestosLinea(List<ImpuestoLinea> impuestosLinea) {
		this.impuestosLinea = impuestosLinea;
	}

	public LineaVertex getLineaVertex() {
		return lineaVertex;
	}

	public void setLineaVertex(LineaVertex lineaVertex) {
		this.lineaVertex = lineaVertex;
	}

	public List<LineaDetailVertex> getListaImpuestosVertex() {
		return listaImpuestosVertex;
	}

	public void setListaImpuestosVertex(List<LineaDetailVertex> listaImpuestosVertex) {
		this.listaImpuestosVertex = listaImpuestosVertex;
	}
	
	public Boolean getCambioPrecioManual() {
		return cambioPrecioManual;
	}

	
	public void setCambioPrecioManual(Boolean cambioPrecioManual) {
		this.cambioPrecioManual = cambioPrecioManual;
	}

	/*
	 * BYL-140 - etiqueta totalIva y totalRecargo se calculaba con el precioSinDto
	 */
	@Override
	protected void realizarCalculosDeLinea() {
		super.realizarCalculosDeLinea();
		Long idTratamientoImpuestos = getCabecera().getTicket().getIdTratImpuestos();

		Sesion sesion = SpringContext.getBean(Sesion.class);
		SesionImpuestos sesionImpuestos = sesion.getImpuestos();
		// TotalIva = total del iva con descuento, no sin descuento y totalRecargo igual
		totalIva = BigDecimalUtil.redondear(sesionImpuestos.getPorcentajeIva(getCodImpuesto(), idTratamientoImpuestos, precioConDto).multiply(cantidad), 2);
		totalRecargo = BigDecimalUtil.redondear(sesionImpuestos.getPorcentajeRecargo(getCodImpuesto(), idTratamientoImpuestos, precioConDto).multiply(cantidad), 2);

	}

	@Override
	public void recalcularImporteFinal() {
		log.debug("recalcularImporteFinal()");

		if (lineaVertex != null) {
			if (StringUtils.isNotBlank(lineaVertex.getAmount()) && StringUtils.isNotBlank(lineaVertex.getAmountWithTax())) {
				log.debug("recalcularImporteFinal() - Tiene impuestos de Vertex");

				BigDecimal importeSinImpuestos = new BigDecimal(lineaVertex.getAmount());
				BigDecimal importeConImpuestos = new BigDecimal(lineaVertex.getAmountWithTax());

				calcularDescuento();

				precioConDto = BigDecimalUtil.redondear(importeSinImpuestos.divide(cantidad.abs(), RoundingMode.HALF_UP), 2);
				precioSinDto = BigDecimalUtil.redondear(importeSinImpuestos.divide(cantidad.abs(), RoundingMode.HALF_UP), 2);
				descuento = BigDecimalUtil.redondear(descuento, 2);

				importeConDto = importeSinImpuestos;
				impuestos = importeConImpuestos.subtract(importeSinImpuestos);

				importeTotalConDto = importeConImpuestos;

				precioTotalConDto = importeConImpuestos.divide(cantidad.abs());

				// Extraemos el total de iva y el total de recargo para su uso posterior en el cÃ¡lculo de totales
				totalIva = impuestos;
				totalRecargo = BigDecimal.ZERO;

				precioTotalSinDto = importeConImpuestos.divide(cantidad.abs());
				precioTotalTarifaOrigen = precioSinDto;

				if (BigDecimalUtil.isMenorACero(cantidad)) {
					importeConDto = importeConDto.negate();
					importeTotalConDto = importeTotalConDto.negate();
				}
			}
		}
		else {
			super.recalcularImporteFinal();
		}
	}
}
