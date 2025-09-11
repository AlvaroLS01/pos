/**
ç * ComerZZia 3.0
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


package com.comerzzia.pos.services.ticket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.impuestos.porcentajes.PorcentajeImpuestoBean;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.ComparadorSubtotalesIvaTicketPorcentaje;
import com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.cabecera.TotalesTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.EntregaCuentaTicket;
import com.comerzzia.pos.services.ticket.pagos.EntregasCuentaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;


@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ticket")
@XmlType(propOrder={"schemaVersion", "softwareVersion", "localCopyVersion", "cabecera", "lineas", "pagos", "entregasCuenta", "cuponesAplicados", "promociones", "cuponesEmitidos"})
@Component
@Scope("prototype")
public class TicketVentaAbono extends TicketVenta implements DocumentoPromocionable {
	
	private Logger log = Logger.getLogger(TicketVentaAbono.class);
		
	@XmlAttribute
	protected String schemaVersion;
	
	@XmlAttribute
	protected String softwareVersion;
	
	@XmlAttribute
	protected String localCopyVersion;
	
	@XmlElement(name="cabecera")
    protected CabeceraTicket cabecera;
    
    @XmlElementWrapper(name = "lineas")
    @XmlElement(name = "linea")
    protected List<LineaTicket> lineas;
    
    @XmlElementWrapper(name = "pagos")
    @XmlElement(name = "pago")
    protected List<PagoTicket> pagos;
    
    @XmlElement(name = "entregasCuenta")
    protected EntregasCuentaTicket entregasCuenta;
    
    @XmlElementWrapper(name = "promociones")
    @XmlElement(name = "promocion")
    protected List<PromocionTicket> promociones;	
    
    @XmlElementWrapper(name = "cupones")
    @XmlElement(name = "cupon")
    protected List<CuponEmitidoTicket> cuponesEmitidos;

    @XmlElementWrapper(name = "cuponesAplicados")
    @XmlElement(name = "cupon")
    protected List<CuponAplicadoTicket> cuponesAplicados;
    
    @XmlTransient
    protected boolean esDevolucion;
    @XmlTransient
    protected boolean esApartado;
    @XmlTransient
    protected boolean isAdmitePromociones = true;
    
    //Map linking the promotions with the coupons triying to apply them.
    @XmlTransient
    protected Map<Long, List<CustomerCouponDTO>> couponsAppliyingPromotions;
	
    @XmlTransient
	protected Map<String, Object> extensiones;
    
    public TicketVentaAbono(){    	
        super();
        schemaVersion = "1.1";
        cabecera = SpringContext.getBean(CabeceraTicket.class);
        pagos = new ArrayList<>();
        lineas = new ArrayList<>();
        promociones = new ArrayList<>();
        cuponesEmitidos = new ArrayList<>();
        cuponesAplicados = new ArrayList<>();
    }    
    
    @Override
    public String getSchemaVersion() {
		return schemaVersion;
	}

	@Override
	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}	
	
	@Override
    public String getSoftwareVersion() {
		return softwareVersion;
	}

	@Override
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	@Override
	public String getLocalCopyVersion() {
		return localCopyVersion;
	}

	@Override
	public void setLocalCopyVersion(String softwareRevision) {
		this.localCopyVersion = softwareRevision;
	}

	@Override
    public LineaTicket getLinea(Integer idLinea){
        for (LineaTicket linea : lineas) {
            if (linea.getIdLinea().equals(idLinea)){
                return linea;
            }
        }
        return null;
    }
    
    protected SubtotalIvaTicket crearSubTotalIvaTicket() {
    	return new SubtotalIvaTicket();
    }
    
    public void recalcularSubtotalesIva(){
        // Construimos mapa con subtotales recorriendo todas las líneas del ticket
        Map<String, SubtotalIvaTicket> subtotales = new HashMap<>();
        Sesion sesion = SpringContext.getBean(Sesion.class);
        for (LineaTicket linea : lineas) {
        	linea.recalcularImporteFinal();
            String codImpuesto = linea.getArticulo().getCodImpuesto();
            SubtotalIvaTicket subtotal = subtotales.get(codImpuesto);
            if (subtotal == null){
                subtotal = crearSubTotalIvaTicket();
				PorcentajeImpuestoBean porcentajeImpuesto = sesion.getImpuestos().getPorcentaje(getCliente().getIdTratImpuestos(), codImpuesto);
                subtotal.setPorcentajeImpuestoBean(porcentajeImpuesto);
                subtotales.put(codImpuesto, subtotal);
            }
            subtotal.addLinea(linea);
        }
               
        // Añadimos cada subtotal a la lista de la cabecera del ticket
        getCabecera().getSubtotalesIva().clear();
        for (SubtotalIvaTicket subtotal : subtotales.values()) {
            // Recalculamos cada subtotal (impuestos, cuotas y totales)
            subtotal.recalcular();
            getCabecera().getSubtotalesIva().add(subtotal);
        }
        
        ComparadorSubtotalesIvaTicketPorcentaje comparador = new ComparadorSubtotalesIvaTicketPorcentaje();
        Collections.sort(getCabecera().getSubtotalesIva(),comparador);
    }
    
    @Override
	public String getCodTarifa() {
		ClienteBean cliente = getCabecera().getCliente();
		if (cliente != null && cliente.getCodtar() != null) {
			return cliente.getCodtar();
		}
		
		Tienda tienda = getCabecera().getTienda();
		ClienteBean clienteTienda = tienda.getCliente();
		if (clienteTienda != null && clienteTienda.getCodtar() != null){
			return clienteTienda.getCodtar();
		} else {
			return ArticulosTarifaService.COD_TARIFA_GENERAL;
		}
	}
    
    public void addLinea(LineaTicket linea) {
        lineas.add(linea);
    }

    public List<PagoTicket> getPagos() {
        return pagos;
    }

    public void addPago(PagoTicket pago) {
        pagos.add(pago);
    }
    
    public IPagoTicket getPago(String codMedioPago) {
        for (IPagoTicket pagoTicket : pagos) {
            if (pagoTicket.getMedioPago().getCodMedioPago().equals(codMedioPago)) {
                return pagoTicket;
            }
        }
        return null;
    }
    
    
    /**
     * Elimina todos los pagos con codigo codMedioPago de la lista de pagos
     *
     * @param codMedioPago
     */
    public void removePago(String codMedioPago) {
    	ListIterator<PagoTicket> listIterator = pagos.listIterator();
    	while(listIterator.hasNext()){
			IPagoTicket pagoTicket = listIterator.next(); 
            if (pagoTicket.getMedioPago().getCodMedioPago().equals(codMedioPago)) {
                listIterator.remove();
            }
        }
    }
        
    /**
     * Comprueba que el total pendiente de pagar del ticket es 0
     * @return
     */
    public boolean isPagosCubiertos(){
        return (getTotales().getPendiente()!=null && getTotales().getPendiente().compareTo(BigDecimal.ZERO) == 0);
    }

    public EntregasCuentaTicket getEntregasCuenta() {
        return entregasCuenta;
    }
    
    public void setEntregasCuenta(EntregasCuentaTicket entregasCuenta) {
		this.entregasCuenta = entregasCuenta;
	}

	public void addEntregaCuenta(EntregaCuentaTicket entregaCuenta) {
    	if (entregasCuenta == null) {
    		entregasCuenta = new EntregasCuentaTicket();
    	}
    	entregasCuenta.getEntregasCuenta().add(entregaCuenta);
    }
    
    @Override
    public List<LineaTicket> getLineas() {
    	return lineas;
    }
    
    @Override
    public List<LineaDocumentoPromocionable> getLineasDocumentoPromocionable() {
    	List<LineaDocumentoPromocionable> list = new LinkedList<>();
    	list.addAll(getLineas());
        return list;
    }
    
    public void setLineas(List lineas) {
        this.lineas = lineas;
    }
    
    public List<LineaTicket> getLinea (String codArticulo){
        
        List<LineaTicket> lineasArticulo = new ArrayList();
        
        for (LineaTicket linea : lineas) {
            if (linea.getArticulo().getCodArticulo().equals(codArticulo)){
                lineasArticulo.add(linea);
            }
        }
        return lineasArticulo;
    }
    
    public void setEsDevolucion(boolean esDevolucion){
    	this.esDevolucion = esDevolucion;
    }
    
    @Override
    public boolean isEsDevolucion() {
    	return esDevolucion;
    }
    
    public void setEsApartado(boolean esApartado){
        this.esApartado = esApartado;
    }

    public boolean isEsApartado() {
    	return esApartado;
    }

    public boolean tienePromocionesAhorroMayor0(){
    	for (PromocionTicket promocion : getPromociones()) {
			if(BigDecimalUtil.isMayorACero(promocion.getImporteTotalAhorro()) && !promocion.isDescuentoAFuturo()){
				return true;
			}
		}
    	return false;
    }
    
    
    public PromocionTicket getPromocion(Long idPromocion){
    	for (PromocionTicket promocion : promociones) {
			if (promocion.getIdPromocion().equals(idPromocion)){
				return promocion;
			}
		}
    	return null;
    }
    
    public PromocionTicket getPromocion(Long idPromocion, String codCupon) {
    	for (PromocionTicket promocion : promociones) {
    		if(promocion.getIdPromocion().equals(idPromocion) && (promocion.getCodAcceso() == null || promocion.getCodAcceso().equals(codCupon))) {
    			return promocion;
    		}
    	}
    	return null;
    }
   
    public void addPromocion(PromocionTicket promocion) {
        promociones.add(promocion);
    }

    public List<CuponEmitidoTicket> getCuponesEmitidos() {
        return cuponesEmitidos;
    }
    
    public void addCuponEmitido(CuponEmitidoTicket cupon) {
        this.cuponesEmitidos.add(cupon);
    }

    public List<CuponAplicadoTicket> getCuponesAplicados() {
        return cuponesAplicados;
    }
    
    public CuponAplicadoTicket getCuponAplicado(String codigo){
        for (CuponAplicadoTicket cupon : cuponesAplicados) {
            if (cupon.getCodigo().equals(codigo)){
                return cupon;
            }
        }
        return null;
    }

    public void addCuponAplicado(CuponAplicadoTicket cupon) {
        this.cuponesAplicados.add(cupon);
    }
    
//    public boolean tienePromocionCabecera(){
//        for (PromocionTicket promocion : promociones) {
//        	if (promocion.getIdTipoPromocion().equals(PromocionTipoBean.TIPO_DESCUENTO_CABECERA)){
//        		return true;
//        	}
//        }
//        return false;
//    }
    
    public void inicializarTotales(){
    	getCabecera().setTotales(SpringContext.getBean(TotalesTicket.class, this));
    }

	@Override
	public void setPagos(List pagos) {
		this.pagos = pagos;
		
	}

	@Override
	public ICabeceraTicket getCabecera() {
		return cabecera;
	}

	@Override
	public void addPago(IPagoTicket pago) {
		pagos.add((PagoTicket) pago);
		
	}

	@Override
	public List<PromocionTicket> getPromociones() {
		return this.promociones;
	}

	
	public void setPromociones(List promociones) {
		this.promociones = promociones;
		
	}

	@Override
	public void aplicarPromocion(TicketVenta ticket,
			LineasAplicablesPromoBean lineasCondicion,
			LineasAplicablesPromoBean lineasAplicables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCuponesEmitidos(List cuponesEmitidos) {
		this.cuponesEmitidos = cuponesEmitidos;
	}

	@Override
	public void setCuponesAplicados(List cuponesAplicados) {
		this.cuponesAplicados = cuponesAplicados;
	}

	@Override
	public FidelizacionBean getDatosFidelizado() {
		return getCabecera().getDatosFidelizado();
	}

	@Override
	public void addPuntos(Integer puntos) {
		getTotales().addPuntos(puntos);
	}

	@Override
	public void resetPuntos() {
		getTotales().resetPuntos();
	}

	@Override
	public boolean isAdmitePromociones() {
		return isAdmitePromociones;
	}

	public void setAdmitePromociones(boolean admitePromociones) {
		isAdmitePromociones = admitePromociones;
	}
	
	public BigDecimal getCantidadTotal() {
		BigDecimal cantidad = BigDecimal.ZERO;

		try {
			for (LineaTicket lineaTicket : (List<LineaTicket>) getLineas()) {
				if (lineaTicket.getArticulo() != null && lineaTicket.getArticulo().getBalanzaTipoArticulo() != null && "P".equals(lineaTicket.getArticulo().getBalanzaTipoArticulo())) {
					cantidad = cantidad.add(BigDecimal.ONE);
				}
				else {
					if (lineaTicket.getCantidad() != null) {
						cantidad = cantidad.add(lineaTicket.getCantidad().abs());
	
					}
				}
			}
		}
		catch(Exception e) {
			log.error("getCantidadTotal() - Ha habido un error al calcular la cantidad total de artículos: " + e.getMessage(), e);
		}
		
		cabecera.setCantidadArticulos(cantidad);

		return cantidad;
	}
	
	public Integer getPuntosDevueltos(){
		Integer puntosDevueltos = 0;
		if(this.getLineas()!=null){    			
			for(LineaTicket lineaTicket: (List<LineaTicket>) this.getLineas()){
				Double puntosLineaDevueltos = lineaTicket.getPuntosADevolver();
				if(puntosLineaDevueltos != null){
					puntosDevueltos += puntosLineaDevueltos.intValue();
				}
			}
		}
		
		return puntosDevueltos;
	}

	@Override
    public Map<String, Object> getExtensiones() {
		if(this.extensiones == null) {
			this.extensiones = new HashMap<String, Object>();
		}
	    return extensiones;
    }

	@Override
    public void addExtension(String clave, Object valor) {
		if(this.extensiones == null) {
			this.extensiones = new HashMap<String, Object>();
		}
		this.extensiones.put(clave, valor);
    }

	@Override
    public Object getExtension(String clave) {
		if(this.extensiones == null) {
			this.extensiones = new HashMap<String, Object>();
		}
		return extensiones.get(clave);
    }

	@Override
    public void clearExtensiones() {
		if(this.extensiones != null) {
			this.extensiones.clear();
		}
    }

	@Override
	public List<CustomerCouponDTO> getCouponsAppliyingPromotion(Long idPromocion) {
		if(couponsAppliyingPromotions == null) {
			couponsAppliyingPromotions = new HashMap<Long,List<CustomerCouponDTO>>();
		}
		if(couponsAppliyingPromotions.get(idPromocion) == null) {
			couponsAppliyingPromotions.put(idPromocion, new ArrayList<CustomerCouponDTO>());
		}
		return couponsAppliyingPromotions.get(idPromocion);
	}

	@Override
	public void addCouponAppliyingPromotion(Long idPromocion, CustomerCouponDTO customerCouponDTO) {
		getCouponsAppliyingPromotion(idPromocion).add(customerCouponDTO);
	}

	@Override
	public void removeCouponsAppliyingPromotion(Long idPromocion) {
		couponsAppliyingPromotions.remove(idPromocion);
	}

}
