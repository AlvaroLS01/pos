package com.comerzzia.bimbaylola.pos.services.ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.bimbaylola.pos.persistence.ticket.articulos.agregarnotainformativa.NotaInformativaBean;
import com.comerzzia.bimbaylola.pos.services.core.sesion.ByLSesionImpuestos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Primary
@Scope("prototype")
public class ByLLineaTicket extends LineaTicket {

	public static final String COD_PAIS_SINGAPUR = "SG";
	public  static final String COD_PAIS_ECUADOR = "EC";

	private static final long serialVersionUID = 8094045799093253050L;

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
	
	@Autowired
	protected ByLSesionImpuestos sesionImpuestosEspecial;
	
	@XmlElement(name = "cambio_precio_manual")
	protected Boolean cambioPrecioManual;
	
	public ByLLineaTicket() {
		super();
		cambioPrecioManual = false;
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
	
//	public Boolean getCambioPrecioManual() {
//		return !BigDecimalUtil.redondear(precioTotalTarifaOrigen).equals(BigDecimalUtil.redondear(precioTotalSinDto));
//	}

	public Boolean getCambioPrecioManual() {
		return cambioPrecioManual;
	}

	
	public void setCambioPrecioManual(Boolean cambioPrecioManual) {
		this.cambioPrecioManual = cambioPrecioManual;
	}

	@Override
	public void recalcularImporteFinal() {
    	Long idTratamientoImpuestos = getCabecera().getCliente().getIdTratImpuestos();

    	importeTotalPromociones = BigDecimal.ZERO;
		importeTotalPromocionesMenosIngreso = BigDecimal.ZERO;
		BigDecimal importeTotalPromocionesSinRedondear = BigDecimal.ZERO;
		BigDecimal importeTotalConDtoSinRedondear = BigDecimal.ZERO;
		boolean cantidadTieneDecimales = !BigDecimalUtil.isIgualACero(cantidad.remainder(BigDecimal.ONE));
		BigDecimal importeLineaConImpuestos = BigDecimal.ZERO;
		boolean numerosDecimales = false;
		if (AppConfig.numeroDecimales != null) {
			numerosDecimales = AppConfig.numeroDecimales == 0 ? true : false;
		}
		
		
		if (tieneDescuentoManual()){
			importeTotalConDtoSinRedondear = BigDecimalUtil.menosPorcentajeR4(getPrecioTotalSinDto(), descuentoManual).multiply(cantidad);
			importeTotalConDto = BigDecimalUtil.redondear(importeTotalConDtoSinRedondear);
			importeTotalConDto = numerosDecimales ? importeTotalConDto.setScale(0, RoundingMode.HALF_UP): importeTotalConDto;
			importeLineaConImpuestos = importeTotalConDto;
		}
		else if (promociones != null) {
			for (IPromocionLineaTicket promocionLinea : promociones) {
				BigDecimal descuento = promocionLinea.getImporteTotalDtoMenosMargen();
				if (cantidadTieneDecimales) {
					//En lineas de peso (cantidad con decimales) usamos descuento sin redondear para no perder precisión en los siguientes cálculos
					descuento = promocionLinea.getImporteTotalDtoMenosMargenSinRedondear();
				}
				if (numerosDecimales) {
					importeTotalPromociones = importeTotalPromociones.add(descuento);
					importeTotalPromociones = importeTotalPromociones.remainder(BigDecimal.ONE).compareTo(new BigDecimal("0.5")) >= 0 ? importeTotalPromociones.setScale(0, RoundingMode.CEILING)
					        : importeTotalPromociones.setScale(0, RoundingMode.FLOOR);

					importeTotalPromocionesSinRedondear = importeTotalPromocionesSinRedondear.add(promocionLinea.getImporteTotalDtoMenosMargenSinRedondear());
					importeTotalPromocionesSinRedondear = importeTotalPromocionesSinRedondear.remainder(BigDecimal.ONE).compareTo(new BigDecimal("0.5")) >= 0
					        ? importeTotalPromocionesSinRedondear.setScale(0, RoundingMode.CEILING)
					        : importeTotalPromocionesSinRedondear.setScale(0, RoundingMode.FLOOR);

					importeTotalPromocionesMenosIngreso = importeTotalPromocionesMenosIngreso.add(promocionLinea.getImporteTotalDtoMenosIngreso());
					importeTotalPromocionesMenosIngreso = importeTotalPromocionesMenosIngreso.remainder(BigDecimal.ONE).compareTo(new BigDecimal("0.5")) >= 0
					        ? importeTotalPromocionesMenosIngreso.setScale(0, RoundingMode.CEILING)
					        : importeTotalPromocionesMenosIngreso.setScale(0, RoundingMode.FLOOR);
				}

				else {
					importeTotalPromociones = importeTotalPromociones.add(descuento);
					importeTotalPromocionesSinRedondear = importeTotalPromocionesSinRedondear.add(promocionLinea.getImporteTotalDtoMenosMargenSinRedondear());
					importeTotalPromocionesMenosIngreso = importeTotalPromocionesMenosIngreso.add(promocionLinea.getImporteTotalDtoMenosIngreso());
				}
				
			}
			importeTotalConDto = BigDecimalUtil.redondear(getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromociones));
			importeTotalConDtoSinRedondear = getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromocionesSinRedondear);
			
			// Para calcular el precio con impuestos y el importe sin impuestos se mantiene toda la precisión de la operación de cantidad*preciototal-descuento
			importeLineaConImpuestos = getCantidad().multiply(precioTotalSinDto).subtract(importeTotalPromociones);
		}
		
		Sesion sesion = SpringContext.getBean(Sesion.class);
		if(sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_SINGAPUR) || sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_ECUADOR)) {
			try {
				sesionImpuestosEspecial.init(cabecera.getCliente().getIdGrupoImpuestos());
				importeConDto = sesionImpuestosEspecial.getPrecioSinImpuestos(getCodImpuesto(), BigDecimalUtil.redondear(importeLineaConImpuestos), idTratamientoImpuestos);			
			}
			catch (SesionInitException e) {
				log.error("recalcularImporteFinal() - Error iniciando sesión de impuestos");
			}
		}
		else {
			SesionImpuestos sesionImpuestos = sesion.getImpuestos();
			importeConDto = sesionImpuestos.getPrecioSinImpuestos(getCodImpuesto(), BigDecimalUtil.redondear(importeLineaConImpuestos), idTratamientoImpuestos);			
		}
		
		precioConDto = BigDecimalUtil.isIgualACero(cantidad) ? BigDecimal.ZERO : importeConDto.divide(cantidad, 4, RoundingMode.HALF_UP);
		precioTotalConDto = BigDecimalUtil.isIgualACero(cantidad) ? BigDecimal.ZERO : importeLineaConImpuestos.divide(cantidad, 2, RoundingMode.HALF_UP);
		descuento = BigDecimal.ZERO;
		if (tieneDescuentoManual()){
			descuento = descuentoManual;
		}
		else if(!promociones.isEmpty()) {
			// Calculamos el porcentaje de descuento desde el importe final sin redondear para que coincida con el porcentaje
			// exacto configurado en la promoción (por ejemplo, si configuro un 10% dto, que no salga un 9,99% tras los cÃ¡lculos)
			descuento = BigDecimalUtil.getTantoPorCientoMenos(getImporteTotalSinDtoSinRedondear(), importeTotalConDtoSinRedondear);

			// Confirmamos que el % descuento calculado, al aplicarlo sobre el precio unitario y multiplicarlo por la cantidad es igual
			// al importe final previamente calculado. En caso contrario, volveremos a calcular el % de descuento a partir del importe redondeado
			if (!BigDecimalUtil.isIgual(BigDecimalUtil.redondear(BigDecimalUtil.menosPorcentajeR(getPrecioTotalSinDto(), BigDecimalUtil.redondear(descuento)).multiply(cantidad)),(BigDecimalUtil.redondear(importeTotalConDto)))){
				descuento = BigDecimalUtil.getTantoPorCientoMenos(getImporteTotalSinDtoSinRedondear(), importeTotalConDto); 
			} 
		}
	}
	
	@Override
	public void recalcularPreciosImportes() {
    	Long idTratamientoImpuestos = getCabecera().getCliente().getIdTratImpuestos();
        Sesion sesion = SpringContext.getBean(Sesion.class);
        SesionImpuestos sesionImpuestos = sesion.getImpuestos();
        if(sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_SINGAPUR) || sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_ECUADOR)) {
        	try {
				sesionImpuestosEspecial.init(cabecera.getCliente().getIdGrupoImpuestos());
			}
			catch (SesionInitException e) {
				log.error("recalcularPreciosImportes() - Error iniciando sesión de impuestos");
			}
        }
        
        // Calculamos precio con impuestos de la tarifa origen
        if (precioTarifaOrigen == null){
        	precioTarifaOrigen = tarifa.getPrecioVenta();
        }
        
        if(ivaIncluido){
        	precioTotalTarifaOrigen = tarifa.getPrecioTotal();
        	precioTarifaOrigen = sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_SINGAPUR) || sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_ECUADOR)
        			? sesionImpuestosEspecial.getPrecioVenta(getCodImpuesto(), idTratamientoImpuestos, precioTotalTarifaOrigen)
        			: sesionImpuestos.getPrecioVenta(getCodImpuesto(), idTratamientoImpuestos, precioTotalTarifaOrigen);
        }
        else{
        	impuestos = sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_SINGAPUR) || sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_ECUADOR)
        			? sesionImpuestosEspecial.getImpuestos(getCodImpuesto(), idTratamientoImpuestos, precioTarifaOrigen)
        			: sesionImpuestos.getImpuestos(getCodImpuesto(), idTratamientoImpuestos, precioTarifaOrigen);
        	precioTotalTarifaOrigen = BigDecimalUtil.redondear(precioTarifaOrigen.add(impuestos));
        }        
        
        // Por defecto, el precio de venta sin descuentos es igual al precio de la tarifa origen
        precioSinDto = precioTarifaOrigen;
        precioTotalSinDto = precioTotalTarifaOrigen;
        
        recalcularImporteFinal();
    }
	
}
