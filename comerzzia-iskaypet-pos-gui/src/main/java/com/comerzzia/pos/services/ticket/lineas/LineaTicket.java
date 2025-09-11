/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */

package com.comerzzia.pos.services.ticket.lineas;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket;
import com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;


@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="linea" )
@XmlType(propOrder={"codArticulo", "desArticulo", "idLinea", "cantidad", "desglose1", "desglose2", "generico",
					"vendedor", "editable", "lineaDocumentoOrigen", "codImpuesto", "codtar", "precioTarifaOrigen", "precioTotalTarifaOrigen",
					"precioSinDto", "precioTotalSinDto", "precioPromocionSinDto", "precioPromocionTotalSinDto", "precioConDto", 
					"precioTotalConDto","importeConDto", "importeTotalConDto", "descuento", "descuentoManual", "importeTotalPromociones", 
					"importeTotalPromocionesMenosIngreso", "promociones", "numerosSerie", "codigoBarras", "puntosADevolver", "documentoOrigen", "updateStock"})
public class LineaTicket extends LineaTicketAbstract implements ILineaTicket, Cloneable, Serializable{
    
	protected static final long serialVersionUID = 1L;

	protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LineaTicket.class.getName());
    
    @XmlElement(name = "cantidad")
    protected BigDecimal cantidad;
    @XmlTransient
    protected ArticuloBean articulo;
    @XmlElement(name = "desglose1")
    protected String desglose1;
    @XmlElement(name = "desglose2")
    protected String desglose2;
    @XmlTransient
    protected TarifaDetalleBean tarifa;
    
    @XmlElement
    private String codtar;

    // Precios tarifa origen
    @XmlElement(name = "precio_tarifa_origen")
	protected BigDecimal precioTarifaOrigen;
    @XmlElement(name = "precio_total_tarifa_origen")
	protected BigDecimal precioTotalTarifaOrigen;

    // Precio de promoción precio aplicada previa al resto de promociones
	@XmlElement(name = "precio_sin_dto_promocion")
	protected BigDecimal precioPromocionSinDto;
	@XmlElement(name = "precio_total_sin_dto_promocion")
	protected BigDecimal precioPromocionTotalSinDto;
    
    
    // Importes sin descuento
	@XmlElement(name = "precio_sin_dto")
	protected BigDecimal precioSinDto;
	@XmlElement(name = "precio_total_sin_dto")
	protected BigDecimal precioTotalSinDto;

	// Importes con descuento línea
	@XmlElement(name = "precio")
	protected BigDecimal precioConDto;
	@XmlElement(name = "precio_total")
	protected BigDecimal precioTotalConDto;
	@XmlElement(name = "importe")
	protected BigDecimal importeConDto;
	@XmlElement(name = "importe_total")
	protected BigDecimal importeTotalConDto;

	@XmlElement(name = "descuento_manual")
	protected BigDecimal descuentoManual;

	protected BigDecimal descuento;
	
    @XmlTransient
    protected BigDecimal impuestos;
    @XmlAttribute (name = "idlinea")
    protected Integer idLinea;
    @XmlTransient
    protected ICabeceraTicket cabecera;
    @XmlTransient
    protected BigDecimal cantidadDevuelta;
    @XmlTransient
    protected BigDecimal cantidadADevolver;
    
    @XmlElementWrapper(name = "promociones")
	@XmlElement(name = "promocion")
	protected List<PromocionLineaTicket> promociones;
    protected BigDecimal importeTotalPromociones;
	protected BigDecimal importeTotalPromocionesMenosIngreso;
	@XmlTransient
	protected List<PromocionLineaCandidataTicket> promocionesAplicables;
	@XmlTransient
	protected List<PromocionLineaTicket> promocionesCandidatas;
	@XmlTransient
	protected BigDecimal cantidadPromocion;
	@XmlTransient
	protected BigDecimal cantidadPromocionCandidata;
	@XmlElement(name = "vendedor")
	protected UsuarioBean vendedor;
	@XmlElement
    protected DocumentoOrigen documentoOrigen;
    @XmlElement
    protected boolean editable;
    @XmlTransient
    protected boolean imprimirTicketRegalo;
    @XmlTransient
    protected boolean ivaIncluido;
    /**
     * Id que relaciona la linea del ticket del abono con el de la venta
     */
    @XmlElement(name = "linea_documento_origen")
    protected Integer lineaDocumentoOrigen;
    
    @XmlElementWrapper(name = "numeros_serie")
	@XmlElement(name = "numero_serie")
	protected List<String> numerosSerie;
    
    protected String codigoBarras;
    
    @XmlTransient
    protected Map<String, TarifaDetalleBean> tarifasDisponibles;

    @XmlTransient
    protected boolean isAdmitePromociones = true;

    @XmlTransient
    protected boolean hayCambioTarifaReferencia;
    
    @XmlElement(name = "puntos_a_devolver")
	protected Double puntosADevolver;
    
    @XmlElement(name = "update_stock")
   	protected Boolean updateStock;

	public LineaTicket() {
		descuento = BigDecimal.ZERO;
		descuentoManual = BigDecimal.ZERO;
		precioSinDto = BigDecimal.ZERO;
		precioTotalSinDto = BigDecimal.ZERO;
		precioConDto = BigDecimal.ZERO;
		precioTotalConDto = BigDecimal.ZERO;
		importeConDto = BigDecimal.ZERO;
		importeTotalConDto = BigDecimal.ZERO;
		precioTotalTarifaOrigen = BigDecimal.ZERO;
		
		impuestos = BigDecimal.ZERO;
		articulo = new ArticuloBean();
		cantidadADevolver = BigDecimal.ZERO;
		cantidadDevuelta = BigDecimal.ZERO;
		
		importeTotalPromociones = BigDecimal.ZERO;
		importeTotalPromocionesMenosIngreso = BigDecimal.ZERO;
		promocionesAplicables = new ArrayList<>();
		promocionesCandidatas = new ArrayList<>();
		promociones = new ArrayList<>();
		cantidadPromocion = BigDecimal.ZERO;
		cantidadPromocionCandidata = BigDecimal.ZERO;
		
		ivaIncluido = false;
		
		tarifasDisponibles = new HashMap<String, TarifaDetalleBean>();
		
		updateStock = Boolean.TRUE;
	}
	
	//Llamado por JAXB automágicamente
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.cabecera = ((Ticket) parent).getCabecera();
	}
	
    public LineaTicket(ICabeceraTicket cabecera){
    	this();
        this.cabecera = cabecera;
        editable = true;
    }
  
    public ArticuloBean getArticulo() {
        return articulo;
    }

    public void setArticulo(ArticuloBean articulo) {
        this.articulo = articulo;
    }

    public void recalcularPreciosImportes() {
    	Long idTratamientoImpuestos = getCabecera().getCliente().getIdTratImpuestos();
        Sesion sesion = SpringContext.getBean(Sesion.class);
        SesionImpuestos sesionImpuestos = sesion.getImpuestos();
    	
        // Calculamos precio con impuestos de la tarifa origen
        if (precioTarifaOrigen == null){
        	precioTarifaOrigen = tarifa.getPrecioVenta();
        }
        
        if(ivaIncluido){
        	precioTotalTarifaOrigen = tarifa.getPrecioTotal();
        	precioTarifaOrigen = sesionImpuestos.getPrecioVenta(getCodImpuesto(), idTratamientoImpuestos, precioTotalTarifaOrigen);
        }
        else{
        	impuestos = sesionImpuestos.getImpuestos(getCodImpuesto(), idTratamientoImpuestos, precioTarifaOrigen);
        	precioTotalTarifaOrigen = BigDecimalUtil.redondear(precioTarifaOrigen.add(impuestos));
        }        
        
        // Por defecto, el precio de venta sin descuentos es igual al precio de la tarifa origen
        precioSinDto = precioTarifaOrigen;
        precioTotalSinDto = precioTotalTarifaOrigen;
        
        recalcularImporteFinal();
    }

    
	public void recalcularImporteFinal() {
    	Long idTratamientoImpuestos = getCabecera().getCliente().getIdTratImpuestos();

    	importeTotalPromociones = BigDecimal.ZERO;
		importeTotalPromocionesMenosIngreso = BigDecimal.ZERO;
		BigDecimal importeTotalPromocionesSinRedondear = BigDecimal.ZERO;
		BigDecimal importeTotalConDtoSinRedondear = BigDecimal.ZERO;
		boolean cantidadTieneDecimales = !BigDecimalUtil.isIgualACero(cantidad.remainder(BigDecimal.ONE));
		BigDecimal importeLineaConImpuestos = BigDecimal.ZERO;
		
		if (tieneDescuentoManual()){
			importeTotalConDtoSinRedondear = BigDecimalUtil.menosPorcentajeR4(getPrecioTotalSinDto(), descuentoManual).multiply(cantidad);
			importeTotalConDto = BigDecimalUtil.redondear(importeTotalConDtoSinRedondear);
			importeLineaConImpuestos = importeTotalConDto;
		}
		else if (promociones != null) {
			for (IPromocionLineaTicket promocionLinea : promociones) {
				BigDecimal descuento = promocionLinea.getImporteTotalDtoMenosMargen();
				if (cantidadTieneDecimales) {
					//En lineas de peso (cantidad con decimales) usamos descuento sin redondear para no perder precisión en los siguientes cálculos
					descuento = promocionLinea.getImporteTotalDtoMenosMargenSinRedondear();
				}
				importeTotalPromociones = importeTotalPromociones.add(descuento);
				importeTotalPromocionesSinRedondear = importeTotalPromocionesSinRedondear.add(promocionLinea.getImporteTotalDtoMenosMargenSinRedondear());
				importeTotalPromocionesMenosIngreso = importeTotalPromocionesMenosIngreso.add(promocionLinea.getImporteTotalDtoMenosIngreso());
			}
			importeTotalConDto = BigDecimalUtil.redondear(getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromociones));
			importeTotalConDtoSinRedondear = getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromocionesSinRedondear);
			
			// Para calcular el precio con impuestos y el importe sin impuestos se mantiene toda la precisión de la operación de cantidad*preciototal-descuento
			importeLineaConImpuestos = getCantidad().multiply(precioTotalSinDto).subtract(importeTotalPromociones);
		}
		
		Sesion sesion = SpringContext.getBean(Sesion.class);
		SesionImpuestos sesionImpuestos = sesion.getImpuestos();
		importeConDto = sesionImpuestos.getPrecioSinImpuestos(getCodImpuesto(), BigDecimalUtil.redondear(importeLineaConImpuestos), idTratamientoImpuestos);
		
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
	
	public boolean tieneDescuentoManual(){
		return BigDecimalUtil.isMayorACero(descuentoManual);
	}
	public boolean tieneCambioPrecioManual(){
		return !BigDecimalUtil.redondear(precioTotalTarifaOrigen).equals(BigDecimalUtil.redondear(precioTotalSinDto));
	}
	
	public BigDecimal getImporteTotalSinDto(){
		return BigDecimalUtil.redondear(getPrecioTotalSinDto().multiply(cantidad));
	}
	
	public BigDecimal getImporteTotalSinDtoSinRedondear(){
		return getPrecioTotalSinDto().multiply(cantidad);
	}
	
	public String getImporteTotalSinDtoAsString(){
		return FormatUtil.getInstance().formateaImporte(getImporteTotalSinDto());
	}
    
    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setTarifa(TarifaDetalleBean tarifa) {
        this.tarifa = tarifa;
        this.codtar = tarifa.getCodTarifa();
        addTarifaDisponible(tarifa.getCodTarifa(), tarifa);
    }
    
    public TarifaDetalleBean getTarifa() {
        return tarifa;
    }
    
    @XmlElement (name = "codart")
    public String getCodArticulo() {
        return articulo.getCodArticulo();
    }
    
    public void setCodArticulo(String codArticulo) {
        this.articulo.setCodArticulo(codArticulo);
    }

	@XmlElement(name = "generico")
	public Boolean getGenerico() {
		return articulo.getGenerico();
	}

	public void setGenerico(Boolean generico) {
		this.articulo.setGenerico(generico);
	}
    
    public String getDesglose1() {
        return desglose1;
    }
    
    public void setDesglose1(String desglose1) {
        this.desglose1 = desglose1!=null?desglose1:"*";
    }

    public String getDesglose2() {
        return desglose2;
    }
    
    public void setDesglose2(String desglose2) {
        this.desglose2 = desglose2!=null?desglose2:"*";
    }

    @XmlElement (name = "desart")
    public String getDesArticulo() {
        return articulo.getDesArticulo();
    }
    
    /**
     * Método para el unmarshall
     * @param desArticulo
     */
    public void setDesArticulo(String desArticulo) {
        articulo.setDesArticulo(desArticulo);
    }

    public BigDecimal getCantidad() {
        
        return cantidad;
    }
    
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    
    public BigDecimal getDescuentoManual() {
		return descuentoManual;
	}
    
    public String getManualAsString() {
        return FormatUtil.getInstance().formateaImporte(descuentoManual);
    }

	public void setDescuentoManual(BigDecimal descuentoManual) {
		this.descuentoManual = descuentoManual;
	}

	public BigDecimal getPrecioSinDto() {
        return precioSinDto;
    }
    
    public String getPrecioSinDtoAsString() {
        return FormatUtil.getInstance().formateaImporte(precioSinDto);
    }

    public void setPrecioSinDto(BigDecimal precioSinDto) {
        this.precioSinDto = precioSinDto;
    }

    public BigDecimal getPrecioTotalSinDto() {
        return precioTotalSinDto;
    }
    
    public String getPrecioTotalSinDtoAsString() {
        return FormatUtil.getInstance().formateaImporte(precioTotalSinDto);
    }

    public void setPrecioTotalSinDto(BigDecimal precioTotalSinDto) {
        this.precioTotalSinDto = precioTotalSinDto;
    }
    
    public BigDecimal getPrecioTotalConDto() {
        return precioTotalConDto;
    }
    
    public String getPrecioTotalConDtoAsString() {
        return FormatUtil.getInstance().formateaImporte(precioTotalConDto);
    }

    public void setPrecioTotalConDto(BigDecimal precioTotalConDto) {
        this.precioTotalConDto = precioTotalConDto;
    }

    public BigDecimal getPrecioConDto() {
        return precioConDto;
    }
    
    public String getPrecioConDtoAsString() {
        return FormatUtil.getInstance().formateaImporte(precioConDto);
    }
    
    public void setPrecioConDto(BigDecimal precioConDto) {
        this.precioConDto = precioConDto;
    }
    
    
    @XmlElement (name = "codImp")
    public String getCodImpuesto() {
        return articulo.getCodImpuesto();
    }
    
    /**
     * Método para el unmarshall
     * @param codImpuesto
     * @deprecated No usar el método. Exclusivo para unmarshal
     */
    public void setCodImpuesto(String codImpuesto) {
        articulo.setCodImpuesto(codImpuesto);
    }

    @Override
    public Integer getIdLinea() {
        return idLinea;
    }
    
    public void setIdLinea(Integer idLinea) {
        this.idLinea = idLinea;
    }
    
	public String getDescuentoFinalAsString() {
		String dtoFinal = "0";

		if (descuento != null) {
			dtoFinal = FormatUtil.getInstance().formateaNumero(descuento, 0);
		}
		return dtoFinal;
	}
    
	public String getDescuentoFinalConDecimalesAsString() {
		String dtoFinal = "0";

		if (descuento != null) {
			dtoFinal = FormatUtil.getInstance().formateaNumero(descuento, 2);
		}
		return dtoFinal;
	}
	
    public String getCantidadAsString(){
        return FormatUtil.getInstance().formateaNumero(getCantidad(), 3);
    }

	public ICabeceraTicket getCabecera() {
		return cabecera;
	}

	public void setCabecera(ICabeceraTicket cabecera) {
		this.cabecera = cabecera;
	}

    public BigDecimal getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public void setCantidadDevuelta(BigDecimal cantidadDevuelta) {
        this.cantidadDevuelta = cantidadDevuelta;
    }

    public BigDecimal getCantidadADevolver() {
        return cantidadADevolver;
    }

    public void setCantidadADevolver(BigDecimal cantidadADevolver) {
        this.cantidadADevolver = cantidadADevolver;
    }
        
    public BigDecimal getCantidadDisponibleDevolver(){
        return getCantidad().subtract(cantidadDevuelta.add(cantidadADevolver));
    }
    
    public Integer getLineaDocumentoOrigen() {
        return lineaDocumentoOrigen;
    }
    
    public void setLineaDocumentoOrigen(Integer lineaDocumentoOrigen) {
        this.lineaDocumentoOrigen = lineaDocumentoOrigen;
    }
	// Promociones
	public List<PromocionLineaTicket> getPromociones() {
		return promociones;
	}

	public void setPromociones(List<PromocionLineaTicket> promociones) {
		this.promociones = promociones;
	}

	public boolean isPromocionesAplicadas() {
		return promociones != null && !promociones.isEmpty();
	}

	public void addPromocion(PromocionLineaTicket promocion) {
		this.promociones.add(promocion);
	}

	public void addPromocionCandidata(PromocionLineaTicket promocion) {
		this.promocionesCandidatas.add(promocion);
	}

	public void addPromocionAplicable(PromocionLineaCandidataTicket promocion) {
		this.promocionesAplicables.add(promocion);
	}

	public List<PromocionLineaCandidataTicket> getPromocionesAplicables() {
		return promocionesAplicables;
	}

	public boolean tienePromocionesAplicables() {
		return promocionesAplicables != null && !promocionesAplicables.isEmpty();
	}

	public void resetPromociones() {
		promociones.clear();
		importeTotalPromociones = BigDecimal.ZERO;
		importeTotalPromocionesMenosIngreso = BigDecimal.ZERO;
		cantidadPromocion = BigDecimal.ZERO;
		cantidadPromocionCandidata = BigDecimal.ZERO;
		precioPromocionSinDto = null;
		precioPromocionTotalSinDto = null;
		
		if(hayCambioTarifaReferencia) {
			cambiarTarifaCalculos(tarifaOriginal.getCodTarifa());
			recalcularPreciosImportes();
		}
		else {
			recalcularImporteFinal();
		}
	}

	public void resetPromocionesAplicables() {
		promocionesAplicables.clear();
	}

	public void resetPromocionesCandidatas() {
		promocionesCandidatas.clear();
		cantidadPromocionCandidata = BigDecimal.ZERO;
	}

	public PromocionLineaTicket getPromocion(Long idPromocion) {
		return getPromocion(idPromocion, promociones);
	}

	public PromocionLineaTicket getPromocionCandidata(Long idPromocion) {
		return getPromocion(idPromocion, promocionesCandidatas);
	}

	protected PromocionLineaTicket getPromocion(Long idPromocion, List<PromocionLineaTicket> listaPromociones) {
		for (PromocionLineaTicket promocionLineaTicket : listaPromociones) {
			if (promocionLineaTicket.getIdPromocion().equals(idPromocion)) {
				return promocionLineaTicket;
			}
		}
		return null;
	}

	public boolean tienePromocionLineaExclusiva() {
		if (!promociones.isEmpty()) { // Sólo nos interesan las promociones de línea. 
			// Como son las primeras que se aplican, si hay una exclusiva tiene que estar en primer lugar
			return promociones.get(0).isExclusiva();
		}
		return false;
	}

	public BigDecimal getImporteTotalPromociones() {
		return importeTotalPromociones;
	}
	
	public BigDecimal getImporteTotalPromocionesMenosIngreso() {
		return importeTotalPromocionesMenosIngreso;
	}

	public void setImporteTotalPromociones(BigDecimal importeTotalPromociones) {
		this.importeTotalPromociones = importeTotalPromociones;
	}

	public BigDecimal getCantidadPromocion() {
		return cantidadPromocion;
	}

	public void addCantidadPromocion(BigDecimal aumento) {
		cantidadPromocion = cantidadPromocion.add(aumento);
	}

	public BigDecimal getCantidadPromocionCandidata() {
		return cantidadPromocionCandidata.add(cantidadPromocion);
	}

	public void addCantidadPromocionCandidata(BigDecimal aumento) {
		cantidadPromocionCandidata = cantidadPromocionCandidata.add(aumento);
	}
	
	
	
	public BigDecimal getPrecioTarifaOrigen() {
		return precioTarifaOrigen;
	}

	public void setPrecioTarifaOrigen(BigDecimal precioTarifaOrigen) {
		this.precioTarifaOrigen = precioTarifaOrigen;
	}

	public BigDecimal getPrecioTotalTarifaOrigen() {
		return precioTotalTarifaOrigen;
	}

	public void setPrecioTotalTarifaOrigen(BigDecimal precioTotalTarifaOrigen) {
		this.precioTotalTarifaOrigen = precioTotalTarifaOrigen;
	}

	public BigDecimal getImporteConDto() {
		return importeConDto;
	}

	public void setImporteConDto(BigDecimal importeConDto) {
		this.importeConDto = importeConDto;
	}

	public BigDecimal getImporteTotalConDto() {
		return importeTotalConDto;
	}
	
	public String getImporteTotalConDtoAsString() {
		return FormatUtil.getInstance().formateaImporte(importeTotalConDto);
	}

	public void setImporteTotalConDto(BigDecimal importeTotalConDto) {
		this.importeTotalConDto = importeTotalConDto;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public DocumentoOrigen getDocumentoOrigen() {
		return documentoOrigen;
	}

	public void setDocumentoOrigen(DocumentoOrigen documentoOrigen) {
		this.documentoOrigen = documentoOrigen;
	}

	public void setImprimirTicketRegalo(boolean imprimir) {
		this.imprimirTicketRegalo = imprimir;
	}

	public boolean isImprimirTicketRegalo() {
		return imprimirTicketRegalo;
	}

	public UsuarioBean getVendedor() {
		return vendedor;
	}

	public void setVendedor(UsuarioBean vendedor) {
		this.vendedor = vendedor;
	}	
	
	public void setIvaIncluido(boolean incluyeIva){
		this.ivaIncluido = incluyeIva;
	}

	@Override
	public LineaTicket clone() {
		try {
			LineaTicket clone = this.getClass().newInstance();
			for (Class<?> obj = this.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
				Field[] fields = obj.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					fields[i].setAccessible(true);
					try{
						if(!Modifier.isFinal(fields[i].getModifiers())){
							fields[i].set(clone, fields[i].get(this));
						}
					}catch(IllegalAccessException e){
						log.warn("clone() - Error al clonar atributo de LineaTicket", e);
					}
				}
			}
			return clone;
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("clone() - Error al clonar LineaTicket, lanzando RuntimeException", e);
			throw new RuntimeException("Error al clonar LineaTicket", e);
		}
	}

	
	protected List<PromocionLineaTicket> getPromocionesCandidatas() {
		return promocionesCandidatas;
	}

	protected void setPromocionesCandidatas(
			List<PromocionLineaTicket> promocionesCandidatas) {
		this.promocionesCandidatas = promocionesCandidatas;
	}

	protected boolean isIvaIncluido() {
		return ivaIncluido;
	}

	protected void setImpuestos(BigDecimal impuestos) {
		this.impuestos = impuestos;
	}

	public void setImporteTotalPromocionesMenosIngreso(
			BigDecimal importeTotalPromocionesMenosIngreso) {
		this.importeTotalPromocionesMenosIngreso = importeTotalPromocionesMenosIngreso;
	}

	protected void setPromocionesAplicables(
			List<PromocionLineaCandidataTicket> promocionesAplicables) {
		this.promocionesAplicables = promocionesAplicables;
	}

	protected void setCantidadPromocion(BigDecimal cantidadPromocion) {
		this.cantidadPromocion = cantidadPromocion;
	}

	protected void setCantidadPromocionCandidata(
			BigDecimal cantidadPromocionCandidata) {
		this.cantidadPromocionCandidata = cantidadPromocionCandidata;
	}
	
    public BigDecimal getPrecioPromocionSinDto() {
    	return precioPromocionSinDto;
    }

	
    public void setPrecioPromocionSinDto(BigDecimal precioPromocionSinDto) {
    	this.precioPromocionSinDto = precioPromocionSinDto;
    }

	
    public BigDecimal getPrecioPromocionTotalSinDto() {
    	return precioPromocionTotalSinDto;
    }
	
    public void setPrecioPromocionTotalSinDto(BigDecimal precioPromocionTotalSinDto) {
    	this.precioPromocionTotalSinDto = precioPromocionTotalSinDto;
    }

    public List<String> getNumerosSerie() {
    	return numerosSerie;
    }

    public void setNumerosSerie(List<String> numerosSerie) {
    	this.numerosSerie = numerosSerie;
    }	
    
    /**Indica si los precios deben llevar impuestos incluidos o no
     * */
    public boolean isPrecioIncluyeImpuestos() {
    	return true;
    }

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	@Override
	public boolean isAdmitePromociones() {
		return isAdmitePromociones;
	}

	public void setAdmitePromociones(boolean admitePromociones) {
		isAdmitePromociones = admitePromociones;
	}

	public Map<String, TarifaDetalleBean> getTarifasDisponibles() {
		return tarifasDisponibles;
	}

	public void setTarifasDisponibles(Map<String, TarifaDetalleBean> tarifasDisponibles) {
		this.tarifasDisponibles = tarifasDisponibles;
	}
	
	public boolean containsTarifaDisponible(String codtar) {
		return tarifasDisponibles.containsKey(codtar);
	}
	
	public void addTarifaDisponible(String codtar, TarifaDetalleBean tarifa) {
		this.tarifasDisponibles.put(codtar, tarifa);
	}

	public String getCodtar() {
		return codtar;
	}

	public void setCodtar(String codtar) {
		this.codtar = codtar;
	}
	
	public Double getPuntosADevolver() {
		return puntosADevolver;
	}

	public void setPuntosADevolver(Double puntosADevolver) {
		this.puntosADevolver = puntosADevolver;
	}

	public void cambiarTarifaCalculos(String codtar) {
		if(tarifasDisponibles.containsKey(codtar)) {
			TarifaDetalleBean tarifaNueva = tarifasDisponibles.get(codtar);
			setTarifa(tarifaNueva);
			precioTarifaOrigen = null;
			recalcularPreciosImportes();
		}
		else {
			log.warn("cambiarTarifa() - No está registrada la tarifa " + codtar + " para la línea " + idLinea);
		}
	}

	@Override
    public boolean hayCambioTarifaReferencia() {
		return hayCambioTarifaReferencia;
    }

	public void setHayCambioTarifaReferencia(boolean hayCambioTarifaReferencia) {
		this.hayCambioTarifaReferencia = hayCambioTarifaReferencia;
	}

	@Override
    public void cambiarTarifaOriginal() {
		cambiarTarifaCalculos(tarifaOriginal.getCodTarifa());
		this.hayCambioTarifaReferencia = false;
    }
	
	public BigDecimal getImporteDescuento() {
		return getImporteTotalSinDto().subtract(importeTotalConDto);
	}
	
	public Boolean isUpdateStock() {
		return updateStock;
	}

	public void setUpdateStock(Boolean updateStock) {
		this.updateStock = updateStock;
	}

	public String toString() {
		return "ID: " + idLinea + ". (" + articulo.getCodArticulo() + ") " + articulo.getDesArticulo() + ". " + getCantidadAsString() + " x " + getPrecioSinDtoAsString();
	}
	
}
