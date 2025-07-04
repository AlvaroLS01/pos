package com.comerzzia.cardoso.pos.services.ticket.lineas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.DatosDescuentoGea;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.format.FormatUtil;

@SuppressWarnings("serial")
@Component
@Primary
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class CARDOSOLineaTicket extends LineaTicket{

	/* GAP - PERSONALIZACIONES V3 - LOTES */
	@XmlElementWrapper(name = "lotes")
	@XmlElement(name = "lote")
	private List<CardosoLote> lotes;

	/* GAP - CAJERO AUXILIAR */
	@XmlElement(name = "cajero")
	protected UsuarioBean cajeroAux;
	@XmlElement(name = "datos_devolucion")
	protected CardosoDatosDevolucionBean datosDevolucion;

	/* GAP - PERSONALIZACIONES V3 - DESCUENTO TARIFA */
	@XmlElement(name = "descuentoTarifa")
	protected BigDecimal descuentoTarifa;
	@XmlElement(name = "importedescuentoTarifa")
	protected BigDecimal importeDescuentoTarifa;
	@XmlElement(name = "precioVentaSinDtoTarifa")
	private BigDecimal precioVentaSinDtoTarifa;
	@XmlElement(name = "precioVentaTotalSinDtoTarifa")
	private BigDecimal precioVentaTotalSinDtoTarifa;
	@XmlTransient
	private BigDecimal backupPrecioTarifaOrigen;
	@XmlTransient
	private BigDecimal backupPrecioTotalTarifaOrigen;

	/* GAP - PROMOCION CANDIDATA */
	@XmlElement(name = "promocion_aplicable")
	private Long idPromocionAplicable;
	@XmlElement(name = "promocion_dto_aplicable")
	private Long idPromocionDtoAplicable;
	
	/* GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA */
	@XmlElement(name = "uid_ticket_balanza")
	protected String uidTicketBalanza;

	private BigDecimal totalIva;
	private BigDecimal totalRecargo;
	private BigDecimal porcentajeIva;
	private BigDecimal porcentajeRecargo;
	
	/* GAP - PERSONALIZACIONES V3 - PROMOCIONES MONOGRÁFICAS */
	@XmlElement(name = "importe_monografica")
	protected BigDecimal importeDtoMonografica;
	@XmlElement(name = "importe_total_monografica")
	protected BigDecimal importeTotalDtoMonografica;
	
	@XmlElement(name = "importe_sin_monografica")
	protected BigDecimal importeSinDtoMonogafica;
	@XmlElement(name = "importe_total_sin_monografica")
	protected BigDecimal importeTotalSinDtoMonogafica;
	
	@XmlElement(name = "porcentaje_dto_monografica")
	protected BigDecimal porcentajeDtoMonografica;
	
	@XmlTransient
	private PromocionBean promocionMonograficaAplicada;
	
	@XmlTransient
	private boolean isBorrada;
	
	public Boolean tienePromocionMonografica(){
		return this.promocionMonograficaAplicada != null;
	}
	
	@XmlElement(name = "datos_descuento_gea")
	protected DatosDescuentoGea datosDescuentoGea;
	
	public CARDOSOLineaTicket(){
		super();
		/* GAP - PERSONALIZACIONES V3 - PROMOCIONES MONOGRÁFICAS */
		importeSinDtoMonogafica = BigDecimal.ZERO;
		importeTotalSinDtoMonogafica = BigDecimal.ZERO;
		importeDtoMonografica = BigDecimal.ZERO;
		importeTotalDtoMonografica = BigDecimal.ZERO;
		porcentajeDtoMonografica = BigDecimal.ZERO;
	}
	
//	@Override
//	public void recalcularImporteFinal(){
//		Long idTratamientoImpuestos = getCabecera().getCliente().getIdTratImpuestos();
//
//		importeTotalPromociones = BigDecimal.ZERO;
//		importeTotalPromocionesMenosIngreso = BigDecimal.ZERO;
//		BigDecimal importeTotalPromocionesSinRedondear = BigDecimal.ZERO;
//		BigDecimal importeTotalConDtoSinRedondear = BigDecimal.ZERO;
//		boolean cantidadTieneDecimales = !BigDecimalUtil.isIgualACero(cantidad.remainder(BigDecimal.ONE));
//		BigDecimal importeLineaConImpuestos = BigDecimal.ZERO;
//
//		/* GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS */
//		if(tieneDescuentoManual()){// && !tienePromocionMonografica()){
//			importeTotalConDtoSinRedondear = BigDecimalUtil.menosPorcentajeR4(getPrecioTotalSinDto(), getDescuentoManual()).multiply(cantidad);
//			importeTotalConDto = BigDecimalUtil.redondear(importeTotalConDtoSinRedondear);
//			importeLineaConImpuestos = importeTotalConDto;
//		}
//		else if(promociones != null){
//			for(IPromocionLineaTicket promocionLinea : promociones){
//				BigDecimal descuento = promocionLinea.getImporteTotalDtoMenosMargen();
//				if(cantidadTieneDecimales){
//					descuento = promocionLinea.getImporteTotalDtoMenosMargenSinRedondear();
//				}
//				importeTotalPromociones = importeTotalPromociones.add(descuento);
//				
//				/* GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS 
//				if(tienePromocionMonografica()){
//					importeTotalConDto = BigDecimalUtil.menosPorcentajeR(importeTotalConDto, getDescuentoManual());
//				}*/
//				
//				importeTotalPromocionesSinRedondear = importeTotalPromocionesSinRedondear.add(promocionLinea.getImporteTotalDtoMenosMargenSinRedondear());
//				importeTotalPromocionesMenosIngreso = importeTotalPromocionesMenosIngreso.add(promocionLinea.getImporteTotalDtoMenosIngreso());
//			}
//			importeTotalConDto = BigDecimalUtil.redondear(getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromociones));
//			importeTotalConDtoSinRedondear = getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromocionesSinRedondear);
//			importeLineaConImpuestos = getCantidad().multiply(precioTotalSinDto).subtract(importeTotalPromociones);
//		}
//
//		Sesion sesion = SpringContext.getBean(Sesion.class);
//		SesionImpuestos sesionImpuestos = sesion.getImpuestos();
//		importeConDto = sesionImpuestos.getPrecioSinImpuestos(getCodImpuesto(), BigDecimalUtil.redondear(importeLineaConImpuestos), idTratamientoImpuestos);
//
//		precioConDto = BigDecimalUtil.isIgualACero(cantidad) ? BigDecimal.ZERO : importeConDto.divide(cantidad, 4, RoundingMode.HALF_UP);
//		precioTotalConDto = BigDecimalUtil.isIgualACero(cantidad) ? BigDecimal.ZERO : importeLineaConImpuestos.divide(cantidad, 2, RoundingMode.HALF_UP);
//		descuento = BigDecimal.ZERO;
//		
//		/* GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS */
//		if(tieneDescuentoManual()){// && !tienePromocionMonografica()){
//			setDescuento(getDescuentoManual());
//		}
//		else if(!promociones.isEmpty()){
//			descuento = BigDecimalUtil.getTantoPorCientoMenos(getImporteTotalSinDtoSinRedondear(), importeTotalConDtoSinRedondear);
//			if(!BigDecimalUtil.isIgual(BigDecimalUtil.redondear(BigDecimalUtil.menosPorcentajeR(getPrecioTotalSinDto(), BigDecimalUtil.redondear(descuento)).multiply(cantidad)),
//			        (BigDecimalUtil.redondear(importeTotalConDto)))){
//				descuento = BigDecimalUtil.getTantoPorCientoMenos(getImporteTotalSinDtoSinRedondear(), importeTotalConDto);
//			}
//		}
//	}
	
	public List<CardosoLote> getLotes(){
		return lotes;
	}

	public void setLotes(List<CardosoLote> lotes){
		this.lotes = lotes;
	}

	public UsuarioBean getCajeroAux(){
		return cajeroAux;
	}

	public void setCajeroAux(UsuarioBean cajeroAux){
		this.cajeroAux = cajeroAux;
	}

	public CardosoDatosDevolucionBean getDatosDevolucion(){
		return datosDevolucion;
	}

	public void setDatosDevolucion(CardosoDatosDevolucionBean datosDevolucion){
		this.datosDevolucion = datosDevolucion;
	}

	public BigDecimal getDescuentoTarifa(){
		return descuentoTarifa;
	}

	public void setDescuentoTarifa(BigDecimal descuentoTarifa){
		this.descuentoTarifa = descuentoTarifa;
	}

	public String getDescuentoTarifaAsString(){
		return FormatUtil.getInstance().formateaImporte(descuentoTarifa);
	}

	public BigDecimal getImporteDescuentoTarifa(){
		return importeDescuentoTarifa != null ? importeDescuentoTarifa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
	}

	public void setImporteDescuentoTarifa(BigDecimal importeDescuentoTarifa){
		this.importeDescuentoTarifa = importeDescuentoTarifa != null ? importeDescuentoTarifa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
	}

	public Long getIdPromocionAplicable(){
		return idPromocionAplicable;
	}

	public void setIdPromocionAplicable(Long idPromocionAplicable){
		this.idPromocionAplicable = idPromocionAplicable;
	}

	public Long getIdPromocionDtoAplicable(){
		return idPromocionDtoAplicable;
	}

	public void setIdPromocionDtoAplicable(Long idPromocionDtoAplicable){
		this.idPromocionDtoAplicable = idPromocionDtoAplicable;
	}

	public String getUidTicketBalanza(){
		return uidTicketBalanza;
	}

	public void setUidTicketBalanza(String uidTicketBalanza){
		this.uidTicketBalanza = uidTicketBalanza;
	}

	public BigDecimal getTotalIva(){
		return totalIva;
	}
	
	public void setTotalIva(BigDecimal totalIva){
		this.totalIva = totalIva;
	}

	public BigDecimal getTotalRecargo(){
		return totalRecargo;
	}

	public void setTotalRecargo(BigDecimal totalRecargo){
		this.totalRecargo = totalRecargo;
	}

	public BigDecimal getPorcentajeIva(){
		return porcentajeIva;
	}

	public void setPorcentajeIva(BigDecimal porcentajeIva){
		this.porcentajeIva = porcentajeIva;
	}
	
	public BigDecimal getPorcentajeRecargo(){
		return porcentajeRecargo;
	}

	public void setPorcentajeRecargo(BigDecimal porcentajeRecargo){
		this.porcentajeRecargo = porcentajeRecargo;
	}

	
	public BigDecimal getBackupPrecioTarifaOrigen(){
		return backupPrecioTarifaOrigen;
	}
	
	public void setBackupPrecioTarifaOrigen(BigDecimal backupPrecioTarifaOrigen){
		this.backupPrecioTarifaOrigen = backupPrecioTarifaOrigen;
	}
	
	public BigDecimal getBackupPrecioTotalTarifaOrigen(){
		return backupPrecioTotalTarifaOrigen;
	}
	
	public void setBackupPrecioTotalTarifaOrigen(BigDecimal backupPrecioTarifaOrigenTotal){
		this.backupPrecioTotalTarifaOrigen = backupPrecioTarifaOrigenTotal;
	}
	
	public BigDecimal getPrecioVentaSinDtoTarifa(){
		return precioVentaSinDtoTarifa;
	}

	public void setPrecioVentaSinDtoTarifa(BigDecimal precioVentaSinDtoTarifa){
		this.precioVentaSinDtoTarifa = precioVentaSinDtoTarifa;
	}

	public BigDecimal getPrecioVentaTotalSinDtoTarifa(){
		return precioVentaTotalSinDtoTarifa;
	}

	public void setPrecioVentaTotalSinDtoTarifa(BigDecimal precioVentaTotalSinDtoTarifa){
		this.precioVentaTotalSinDtoTarifa = precioVentaTotalSinDtoTarifa;
	}

	public PromocionBean getPromocionMonograficaAplicada(){
		return promocionMonograficaAplicada;
	}
	
	public void setPromocionMonograficaAplicada(PromocionBean promocionMonograficaAplicada){
		this.promocionMonograficaAplicada = promocionMonograficaAplicada;
	}

	public BigDecimal getImporteSinDtoMonogafica(){
		return importeSinDtoMonogafica;
	}

	public void setImporteSinDtoMonogafica(BigDecimal importeSinDtoMonogafica){
		this.importeSinDtoMonogafica = importeSinDtoMonogafica;
	}

	public BigDecimal getImporteTotalSinDtoMonogafica(){
		return importeTotalSinDtoMonogafica;
	}

	public void setImporteTotalSinDtoMonogafica(BigDecimal importeTotalSinDtoMonogafica){
		this.importeTotalSinDtoMonogafica = importeTotalSinDtoMonogafica;
	}
	
	public BigDecimal getImporteDtoMonografica(){
		return importeDtoMonografica;
	}
	
	public void setImporteDtoMonografica(BigDecimal importeDtoMonografica){
		this.importeDtoMonografica = importeDtoMonografica;
	}
	
	public BigDecimal getImporteTotalDtoMonografica(){
		return importeTotalDtoMonografica;
	}
	
	public void setImporteTotalDtoMonografica(BigDecimal importeTotalDtoMonografica){
		this.importeTotalDtoMonografica = importeTotalDtoMonografica;
	}

	public BigDecimal getPorcentajeDtoMonografica(){
		return porcentajeDtoMonografica;
	}

	public void setPorcentajeDtoMonografica(BigDecimal porcentajeDtoMonografica){
		this.porcentajeDtoMonografica = porcentajeDtoMonografica;
	}

	public boolean isBorrada() {
		return isBorrada;
	}

	public void setBorrada(boolean isBorrada) {
		this.isBorrada = isBorrada;
	}

	public DatosDescuentoGea getDatosDescuentoGea() {
		return datosDescuentoGea;
	}

	public void setDatosDescuentoGea(DatosDescuentoGea datosDescuentoGea) {
		this.datosDescuentoGea = datosDescuentoGea;
	}

}
