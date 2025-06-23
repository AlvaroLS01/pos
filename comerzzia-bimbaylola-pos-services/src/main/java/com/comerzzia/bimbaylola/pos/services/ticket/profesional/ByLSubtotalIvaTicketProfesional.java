package com.comerzzia.bimbaylola.pos.services.ticket.profesional;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.profesional.SubtotalIvaTicketProfesional;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@XmlAccessorType(XmlAccessType.NONE)
public class ByLSubtotalIvaTicketProfesional extends SubtotalIvaTicketProfesional {

	@XmlElement(name = "cuota_recargo2")
	protected BigDecimal cuotaRecargo2;

	@XmlElement(name = "cuota_recargo3")
	protected BigDecimal cuotaRecargo3;

	@XmlElement(name = "cuota_recargo4")
	protected BigDecimal cuotaRecargo4;

	@XmlElement(name = "cuota_recargo5")
	protected BigDecimal cuotaRecargo5;

	protected BigDecimal porcentajeRecargo2;
	protected BigDecimal porcentajeRecargo3;
	protected BigDecimal porcentajeRecargo4;
	protected BigDecimal porcentajeRecargo5;

	public ByLSubtotalIvaTicketProfesional() {
		super();
		cuotaRecargo2 = BigDecimal.ZERO;
		cuotaRecargo3 = BigDecimal.ZERO;
		cuotaRecargo4 = BigDecimal.ZERO;
		cuotaRecargo5 = BigDecimal.ZERO;
	}

	@XmlElement
	public BigDecimal getCuotaRecargo2() {
		return cuotaRecargo2;
	}

	public void setCuotaRecargo2(BigDecimal cuotaRecargo2) {
		this.cuotaRecargo2 = cuotaRecargo2;
	}

	@XmlElement
	public BigDecimal getCuotaRecargo3() {
		return cuotaRecargo3;
	}

	public void setCuotaRecargo3(BigDecimal cuotaRecargo3) {
		this.cuotaRecargo3 = cuotaRecargo3;
	}

	@XmlElement
	public BigDecimal getCuotaRecargo4() {
		return cuotaRecargo4;
	}

	public void setCuotaRecargo4(BigDecimal cuotaRecargo4) {
		this.cuotaRecargo4 = cuotaRecargo4;
	}

	@XmlElement
	public BigDecimal getCuotaRecargo5() {
		return cuotaRecargo5;
	}

	public void setCuotaRecargo5(BigDecimal cuotaRecargo5) {
		this.cuotaRecargo5 = cuotaRecargo5;
	}

	public BigDecimal getPorcentajeRecargo2() {
		return porcentajeRecargo2;
	}

	public void setPorcentajeRecargo2(BigDecimal porcentajeRecargo2) {
		this.porcentajeRecargo2 = porcentajeRecargo2;
	}

	public BigDecimal getPorcentajeRecargo3() {
		return porcentajeRecargo3;
	}

	public void setPorcentajeRecargo3(BigDecimal porcentajeRecargo3) {
		this.porcentajeRecargo3 = porcentajeRecargo3;
	}

	public BigDecimal getPorcentajeRecargo4() {
		return porcentajeRecargo4;
	}

	public void setPorcentajeRecargo4(BigDecimal porcentajeRecargo4) {
		this.porcentajeRecargo4 = porcentajeRecargo4;
	}

	public BigDecimal getPorcentajeRecargo5() {
		return porcentajeRecargo5;
	}

	public void setPorcentajeRecargo5(BigDecimal porcentajeRecargo5) {
		this.porcentajeRecargo5 = porcentajeRecargo5;
	}

	public void addLineaVertex(LineaTicketAbstract linea, BigDecimal cuota, BigDecimal recargo, BigDecimal recargo2, BigDecimal recargo3, BigDecimal recargo4, BigDecimal recargo5) {
		base = base.add(linea.getImporteConDto());
		this.cuota = this.cuota.add(cuota);
		cuotaRecargo = cuotaRecargo.add(recargo);
		cuotaRecargo2 = cuotaRecargo2.add(recargo2);
		cuotaRecargo3 = cuotaRecargo3.add(recargo3);
		cuotaRecargo4 = cuotaRecargo4.add(recargo4);
		cuotaRecargo5 = cuotaRecargo5.add(recargo5);
	}

	public void recalcularVertex() {
		// cuota = BigDecimalUtil.porcentajeR(base, porcentaje.getPorcentaje());
		// cuotaRecargo = BigDecimalUtil.porcentajeR(base, porcentaje.getPorcentajeRecargo());
		base = BigDecimalUtil.redondear(base);
		total = base.add(cuota).add(cuotaRecargo).add(cuotaRecargo2).add(cuotaRecargo3).add(cuotaRecargo4).add(cuotaRecargo5);
		impuestos = BigDecimalUtil.redondear(total.subtract(base));
	}
	
	
	@Override
	public void addLinea(LineaTicketAbstract linea) {
		base = base.add(linea.getImporteConDto());
		// 1.Para evitar descuadres en el total de la base, cogemos los totales iva de cada linea para la cuota y el total recargo
		cuota = cuota.add(((ByLLineaTicketProfesional) linea).getTotalIva());
		cuotaRecargo = cuotaRecargo.add(((ByLLineaTicketProfesional) linea).getTotalRecargo());
	}

	@Override
	public void recalcular() {
		BigDecimal cuotaEstandar = BigDecimalUtil.porcentajeR(base, porcentaje.getPorcentaje());
		// 2.Comparamos que sean iguales el calculo de estandar, si son iguales, dejamos el standar.
		if (BigDecimalUtil.isIgual(cuotaEstandar, cuota)) {
			cuota = cuotaEstandar;
		}
		BigDecimal cuotaRecargoEstandar = BigDecimalUtil.porcentajeR(base, porcentaje.getPorcentajeRecargo());
		if (BigDecimalUtil.isIgual(cuotaRecargoEstandar, cuotaRecargo)) {
			cuotaRecargo = cuotaRecargoEstandar;
		}
		base = BigDecimalUtil.redondear(base);
		total = base.add(cuota).add(cuotaRecargo);
		impuestos = BigDecimalUtil.redondear(total.subtract(base));

	}
}
