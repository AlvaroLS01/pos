package com.comerzzia.dinosol.pos.services.ticket;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.ticket.lineas.DinoLineaTicket;
import com.comerzzia.dinosol.pos.services.ticket.sorteos.ParticipacionesDTO;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
@XmlRootElement(name = "ticket")
@Scope("prototype")
public class DinoTicketVentaAbono extends TicketVentaAbono {

	private Logger log = Logger.getLogger(DinoTicketVentaAbono.class);
	
	@Autowired
	private VariablesServices variablesServices;
	
	@XmlTransient
	private Integer lastPaymentId;
	
	@XmlElementWrapper
	@XmlElement(name = "auditoria")
	private List<AuditoriaDto> auditoriasNoRegistradas;
	
	//Aqui metemos todo
	@XmlElement(name = "entrega_participaciones")
	private ParticipacionesDTO participaciones;

	public DinoTicketVentaAbono() {
		super();
		this.auditoriasNoRegistradas = new ArrayList<AuditoriaDto>();
	}

	public List<LineaTicket> getLineaAgrupadas() {
		List<LineaTicket> lineasOriginales = new ArrayList<LineaTicket>(lineas);
		try {
			
			List<Integer> lineasYaAgrupadas = new ArrayList<Integer>();
			List<LineaTicket> nuevasLineas = new ArrayList<LineaTicket>();

			List<LineaTicket> copiaLineasAux = new ArrayList<LineaTicket>(lineas);

			for (LineaTicket linea : lineasOriginales) {

				String balanzaTipoArticulo = linea.getArticulo().getBalanzaTipoArticulo();
				
				if(StringUtils.isBlank(balanzaTipoArticulo)) {
					ArticuloBean articulo = SpringContext.getBean(ArticulosService.class).consultarArticulo(linea.getCodArticulo());
					linea.setArticulo(articulo);
				}
				balanzaTipoArticulo = linea.getArticulo().getBalanzaTipoArticulo();
				
				if(StringUtils.isNotBlank(balanzaTipoArticulo) && balanzaTipoArticulo.trim().toUpperCase().equals("P")) {
					nuevasLineas.add(linea);
				}
				else if(((DinoLineaTicket)linea).getTarjetaRegalo()!=null) {
					nuevasLineas.add(linea);
				}
				else {
					if (!lineasYaAgrupadas.contains(linea.getIdLinea())) {
						lineasYaAgrupadas.add(linea.getIdLinea());
						
						if (copiaLineasAux.contains(linea)) {
							copiaLineasAux.remove(linea);
						}
	
						BigDecimal cantidadTotal = linea.getCantidad();
						BigDecimal importeTotalPromociones = linea.getImporteTotalPromociones();
						BigDecimal importeTotalConDto = linea.getImporteTotalConDto();
						Map<Long, PromocionLineaTicket> promociones = new HashMap<Long, PromocionLineaTicket>();
						for(PromocionLineaTicket promocionLinea : linea.getPromociones()) {
							PromocionLineaTicket promoCopiada = copiarPromocionLineaTicket(promocionLinea);
							promociones.put(promocionLinea.getIdPromocion(), promoCopiada);
						}
	
						Iterator<LineaTicket> itAux = copiaLineasAux.iterator();
						
						while (itAux.hasNext()) {							
							LineaTicket lineaAux = itAux.next();
	
							if (tienenMismasCondicionesVenta(linea, lineaAux)) {
								itAux.remove();
								lineasYaAgrupadas.add(lineaAux.getIdLinea());
								copiaLineasAux.remove(lineaAux);
	
								cantidadTotal = cantidadTotal.add(lineaAux.getCantidad());
								importeTotalPromociones = importeTotalPromociones.add(lineaAux.getImporteTotalPromociones());
								importeTotalConDto = importeTotalConDto.add(lineaAux.getImporteTotalConDto());
								
								for(PromocionLineaTicket promoLinea : lineaAux.getPromociones()) {									
									if(promoLinea.isDescuentoMenosMargen()) {
										if(promociones.containsKey(promoLinea.getIdPromocion())) {
											PromocionLineaTicket promoLineaTicket = promociones.get(promoLinea.getIdPromocion());
											BigDecimal importeAcumuladoPromo = promoLineaTicket.getImporteTotalDtoMenosMargen().add(promoLinea.getImporteTotalDtoMenosMargen());
											promoLineaTicket.setImporteTotalDtoMenosMargen(importeAcumuladoPromo);
										}
										else {
											PromocionLineaTicket promoCopiada = copiarPromocionLineaTicket(promoLinea);
											promociones.put(promoLinea.getIdPromocion(), promoCopiada);
										}
									}
								}
							}
						}
	
						if (!BigDecimalUtil.isIgualACero(cantidadTotal)) {
							DinoLineaTicket copiaLinea = (DinoLineaTicket) linea.clone();
							copiaLinea.setImporteTotalConDto(importeTotalConDto);
							copiaLinea.setCantidad(cantidadTotal);
							copiaLinea.setImporteTotalPromociones(importeTotalPromociones);
							List<PromocionLineaTicket> promocionesAgrupadas = new ArrayList<PromocionLineaTicket>(promociones.values());
							copiaLinea.setPromociones(promocionesAgrupadas);
							nuevasLineas.add(copiaLinea);
						}
					}
				}
			}
			
//			ordenarLineasPorPromocion(nuevasLineas);
			
			return nuevasLineas;
		}
		catch (Exception e) {
			log.error("agruparLineas() - Ha habido un error al agrupar líneas: " + e.getMessage(), e);
			return lineasOriginales;
		}
	}

	private void ordenarLineasPorPromocion(List<LineaTicket> nuevasLineas) {
		// Define un comparador personalizado
		Comparator<LineaTicket> promocionComparator = (l1, l2) -> {
			List<PromocionLineaTicket> promociones1 = l1.getPromociones();
			List<PromocionLineaTicket> promociones2 = l2.getPromociones();

			// Verifica si las listas de promociones están vacías
			boolean promociones1Empty = promociones1.isEmpty();
			boolean promociones2Empty = promociones2.isEmpty();

			// Maneja el caso de listas de promociones vacías
			if (promociones1Empty && promociones2Empty) {
				return 0; // Ambos son iguales
			}
			else if (promociones1Empty) {
				return 1; // l1 tiene promociones vacías, debe ir después de l2
			}
			else if (promociones2Empty) {
				return -1; // l2 tiene promociones vacías, debe ir antes de l1
			}
			else {
				// Ninguna lista está vacía, compara por el ID de la primera promoción
				return promociones1.get(0).getIdPromocion().compareTo(promociones2.get(0).getIdPromocion());
			}
		};

		// Ordena las líneas de acuerdo al comparador personalizado y luego por importe total de promociones
		nuevasLineas.sort(promocionComparator.thenComparingDouble(l -> l.getImporteTotalPromociones().doubleValue()));
	}

	private PromocionLineaTicket copiarPromocionLineaTicket(PromocionLineaTicket promoLinea) {
		PromocionLineaTicket promoCopiada = new PromocionLineaTicket();
		promoCopiada.setIdPromocion(promoLinea.getIdPromocion());
		promoCopiada.setIdTipoPromocion(promoLinea.getIdTipoPromocion());
		promoCopiada.setPuntos(promoLinea.getPuntos());
		promoCopiada.setTipoDescuento(promoLinea.getTipoDescuento());
		promoCopiada.setCantidadPromocion(promoLinea.getCantidadPromocion());
		promoCopiada.setCantidadPromocionAplicada(promoLinea.getCantidadPromocionAplicada());
		promoCopiada.setImporteTotalDtoMenosMargen(promoLinea.getImporteTotalDtoMenosMargen());
		promoCopiada.setImporteTotalDtoMenosIngreso(promoLinea.getImporteTotalDtoMenosIngreso());
		promoCopiada.setAcceso(promoLinea.getAcceso());
		promoCopiada.setCodAcceso(promoLinea.getCodAcceso());
		promoCopiada.setExclusiva(promoLinea.isExclusiva());
		return promoCopiada;
	}

	private boolean tienenMismasCondicionesVenta(LineaTicket linea, LineaTicket lineaAux) {
		if (!linea.getCodArticulo().equals(lineaAux.getCodArticulo())) {
			return false;
		}
		if (!BigDecimalUtil.isIgual(linea.getPrecioTotalSinDto(), lineaAux.getPrecioTotalSinDto())) {
			return false;
		}
		if(linea.getCantidad().signum() != lineaAux.getCantidad().signum()) {
			return false;
		}
		return true;
	}

	public IPagoTicket getPagoAcumulado(String codMedioPago) {
		IPagoTicket pagoAcum = null;

		for (IPagoTicket pago : pagos) {
			if (pago.getCodMedioPago().equals(codMedioPago)) {
				if (pagoAcum == null) {
					pagoAcum = pago;
				}
				else {
					pagoAcum.setImporte(pagoAcum.getImporte().add(pago.getImporte()));
				}
			}
		}

		return pagoAcum;
	}
	
	@Override
	public BigDecimal getCantidadTotal() {
		BigDecimal cantidad = BigDecimal.ZERO;

		try {
			for (LineaTicket lineaTicket : (List<LineaTicket>) getLineas()) {
				if (lineaTicket.getArticulo() != null && lineaTicket.getArticulo().getBalanzaTipoArticulo() != null && "P".equals(lineaTicket.getArticulo().getBalanzaTipoArticulo())) {
					cantidad = cantidad.add(BigDecimal.ONE);
				}
				else {
					if (lineaTicket.getCantidad() != null) {
						cantidad = cantidad.add(lineaTicket.getCantidad());
	
					}
				}
			}
		}
		catch(Exception e) {
			log.error("getCantidadTotal() - Ha habido un error al calcular la cantidad total de artículos: " + e.getMessage(), e);
		}
		
		String codartParking = variablesServices.getVariableAsString("X_PARKING_CODART");	
		String codartExtravio = variablesServices.getVariableAsString("X_PARKING_CODART_EXTRAVIO");
		for(LineaTicket linea : (List<LineaTicket>) getLineas()) {
			if(linea.getCodArticulo().equals(codartParking) || linea.getCodArticulo().equals(codartExtravio)) {
				cantidad = cantidad.subtract(linea.getCantidad());
				cantidad = cantidad.add(BigDecimal.ONE);
			}
		}
		
		cabecera.setCantidadArticulos(cantidad.abs());

		return cantidad;
	}

	public Integer getLastPaymentId() {
		return lastPaymentId;
	}

	public void setLastPaymentId(Integer lastPaymentId) {
		if(lastPaymentId != null) {
			this.lastPaymentId = lastPaymentId;
		}
	}
	
	public void removePromocion(PromocionTicket promocion) {
		Iterator<PromocionTicket> it = promociones.iterator();
		while(it.hasNext()) {
			PromocionTicket promocionTicket = it.next();
			boolean esMismaPromocion = promocionTicket.getIdPromocion().equals(promocion.getIdPromocion());
			boolean esMismoCupon = StringUtils.isNotBlank(promocionTicket.getCodAcceso()) && promocionTicket.getCodAcceso().equals(promocion.getCodAcceso());
			
			if(esMismaPromocion && esMismoCupon) {
				it.remove();
			}
		}
		
		for(LineaTicket linea : lineas) {
			Iterator<PromocionLineaTicket> itPromocionesLineas = linea.getPromociones().iterator();

			while(itPromocionesLineas.hasNext()) {
				PromocionLineaTicket promocionLineaTicket = itPromocionesLineas.next();
				
				boolean esMismaPromocion = promocionLineaTicket.getIdPromocion().equals(promocion.getIdPromocion());
				boolean esMismoCupon = StringUtils.isNotBlank(promocionLineaTicket.getCodAcceso()) && promocionLineaTicket.getCodAcceso().equals(promocion.getCodAcceso());
				
				if(esMismaPromocion && esMismoCupon) {
					itPromocionesLineas.remove();
					linea.recalcularImporteFinal();
				}
			}
		}
		
		cabecera.getTotales().recalcular();
	}
	
	@Override
	public boolean tienePromocionesAhorroMayor0() {
		for (PromocionTicket promocion : getPromociones()) {
			if(BigDecimalUtil.isMayorACero(promocion.getImporteTotalAhorro())){
				return true;
			}
		}
    	return false;
	}

	
	public List<AuditoriaDto> getAuditoriasNoRegistradas() {
		if(auditoriasNoRegistradas == null) {
			this.auditoriasNoRegistradas = new ArrayList<AuditoriaDto>();
		}
		return auditoriasNoRegistradas;
	}
	
	public void addAuditoriaNoRegistrada(AuditoriaDto auditoria) {
		if(auditoriasNoRegistradas == null) {
			this.auditoriasNoRegistradas = new ArrayList<AuditoriaDto>();
		}
		
		log.debug("addAuditoriaNoRegistrada() - Añadiendo auditoría no registrada: " + auditoria);
		
		this.auditoriasNoRegistradas.add(auditoria);
	}

	public void clearAuditoriasNoRegistradas() {
		if(auditoriasNoRegistradas != null) {
			this.auditoriasNoRegistradas.clear();
		}
	}

	public void setAuditoriasNoRegistradas(List<AuditoriaDto> auditoriasNoRegistradas) {
		this.auditoriasNoRegistradas = auditoriasNoRegistradas;
	}

	public ParticipacionesDTO getParticipaciones() {
		return participaciones;
	}

	public void setParticipaciones(ParticipacionesDTO participaciones) {
		this.participaciones = participaciones;
	}
	
	public boolean hayParticipacionesNoSolicitadas() {
		return participaciones != null && participaciones.getParticipacionesObtenidas() > 0 && (participaciones.getListaParticipaciones() == null || participaciones.getListaParticipaciones().isEmpty());
	}
	
	public String getDescripcionPromocion(Long idPromocion) {
		for(PromocionTicket promocion : promociones) {
			if(promocion.getIdPromocion().equals(idPromocion)) {
				return promocion.getTextoPromocion();
			}
		}
		
		return I18N.getTexto("PROMOCIÓN ") + idPromocion;
	}
	
	public String getCodigoValidacionBarreraSCO() {
		String prefijo = "99";
		String codalm = getCabecera().getTienda().getCodAlmacen();
		String codCaja = getCodCaja();
		String numeroTicket = StringUtils.leftPad(getIdTicket() + "", 8, '0');
		
		SimpleDateFormat sdfFecha = new SimpleDateFormat("ddMMyyyy");
		String fecha = sdfFecha.format(new Date());
		
		SimpleDateFormat sdfHora = new SimpleDateFormat("HHmmss");
		String hora = sdfHora.format(new Date());
		
		String segundosCortesia = "180";
		
		String codigo = prefijo + codalm + codCaja + numeroTicket + fecha + hora + segundosCortesia;
		
		return codigo;
	}

}
