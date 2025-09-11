package com.comerzzia.pos.services.ticket.lineas;

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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;



@XmlAccessorType(XmlAccessType.NONE)
public abstract class LineaTicketAbstract implements ILineaTicket, Cloneable, Serializable{
    
	protected static final long serialVersionUID = 1L;
	protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LineaTicket.class.getName());

    protected BigDecimal cantidad;
    protected ArticuloBean articulo;
    protected String desglose1;
    protected String desglose2;
    protected TarifaDetalleBean tarifa;
    protected TarifaDetalleBean tarifaOriginal;

    // Precios tarifa origen
	protected BigDecimal precioTarifaOrigen;
	protected BigDecimal precioTotalTarifaOrigen;

    // Importes sin descuento
	protected BigDecimal precioSinDto;
	protected BigDecimal precioTotalSinDto;

    // Precio de promoción precio aplicada previa al resto de promociones
	protected BigDecimal precioPromocionSinDto;
	protected BigDecimal precioPromocionTotalSinDto;

	// Importes con descuento línea
	protected BigDecimal precioConDto;
	protected BigDecimal precioTotalConDto;
	protected BigDecimal importeConDto;
	protected BigDecimal importeTotalConDto;

	protected BigDecimal descuentoManual;

	protected BigDecimal descuento;
	
    protected BigDecimal impuestos;
    protected Integer idLinea;
    protected ICabeceraTicket cabecera;
    protected BigDecimal cantidadDevuelta;
    protected BigDecimal cantidadADevolver;
    
	protected List<PromocionLineaTicket> promociones;
    protected BigDecimal importeTotalPromociones;
	protected BigDecimal importeTotalPromocionesMenosIngreso;
	protected List<PromocionLineaCandidataTicket> promocionesAplicables;
	protected List<PromocionLineaTicket> promocionesCandidatas;
	protected BigDecimal cantidadPromocion;
	protected BigDecimal cantidadPromocionCandidata;
	protected UsuarioBean vendedor;
    protected boolean editable;
    protected boolean imprimirTicketRegalo;
    protected boolean ivaIncluido;

    protected Double puntosADevolver;
    /**
     * Id que relaciona la linea del ticket del abono con el de la venta
     */
    protected Integer lineaDocumentoOrigen;
    
    protected String codigoBarras;
    
    @XmlTransient
	private BigDecimal importeTotalSinPromociones;

	public LineaTicketAbstract() {
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
	}
	
	//Llamado por JAXB automágicamente
	public abstract void afterUnmarshal(Unmarshaller u, Object parent);
	
    public LineaTicketAbstract(ICabeceraTicket cabecera){
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

    public abstract void recalcularPreciosImportes() ;

    
	public abstract void recalcularImporteFinal();   
	
	public BigDecimal getPrecioAplicacionPromocion(){
		if (getPrecioPromocionTotalSinDto()!=null){
			return getPrecioPromocionTotalSinDto();
		}
		return getPrecioTotalSinDto();
	}
	
	public BigDecimal getImporteAplicacionPromocionConDto(){
		return getImporteTotalConDto();
	}
	
	public boolean tieneDescuentoManual(){
		return BigDecimalUtil.isMayorACero(descuentoManual);
	}
	public boolean tieneCambioPrecioManual(){
		return !BigDecimalUtil.redondear(precioTotalTarifaOrigen).equals(BigDecimalUtil.redondear(precioTotalSinDto));
	}
	
	public BigDecimal getImporteTotalSinDto(){
		return getPrecioTotalSinDto().multiply(cantidad);
	}
    
    public BigDecimal getImpuestos() {
        return impuestos;
    }


    public void setTarifa(TarifaDetalleBean tarifa) {
        this.tarifa = tarifa;
    }
    
    public TarifaDetalleBean getTarifa() {
        return tarifa;
    }

	public TarifaDetalleBean getTarifaOriginal() {
		return tarifaOriginal;
	}

	public void setTarifaOriginal(TarifaDetalleBean tarifaOriginal) {
		this.tarifaOriginal = tarifaOriginal;
	}

	public String getCodArticulo() {
        return articulo.getCodArticulo();
    }
    
    public void setCodArticulo(String codArticulo) {
        this.articulo.setCodArticulo(codArticulo);
    }

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

    public String getDesArticulo() {
        return articulo.getDesArticulo();
    }
    
    /**
     * Método para el unmarshall
     * @param desArticulo
     * @deprecated No usar el método. Exclusivo para unmarshal
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

	public void setDescuentoManual(BigDecimal descuentoManual) {
		this.descuentoManual = descuentoManual;
	}

	public BigDecimal getPrecioSinDto() {
        return precioSinDto;
    }

    public void setPrecioSinDto(BigDecimal precioSinDto) {
        this.precioSinDto = precioSinDto;
    }

    public BigDecimal getPrecioTotalSinDto() {
        return precioTotalSinDto;
    }

    public void setPrecioTotalSinDto(BigDecimal precioTotalSinDto) {
        this.precioTotalSinDto = precioTotalSinDto;
    }
    
    public BigDecimal getPrecioTotalConDto() {
        return precioTotalConDto;
    }

    public void setPrecioTotalConDto(BigDecimal precioTotalConDto) {
        this.precioTotalConDto = precioTotalConDto;
    }

    public BigDecimal getPrecioConDto() {
        return precioConDto;
    }
    
    public void setPrecioConDto(BigDecimal precioConDto) {
        this.precioConDto = precioConDto;
    }
    
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
	
    public String getCantidadAsString(){
        return FormatUtil.getInstance().formateaNumero(getCantidad());
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
		recalcularImporteFinal();
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

	protected void setImporteTotalPromocionesMenosIngreso(
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

	public Double getPuntosADevolver() {
		return puntosADevolver;
	}

	public void setPuntosADevolver(Double puntosADevolver) {
		this.puntosADevolver = puntosADevolver;
	}
	
}

