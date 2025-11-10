package com.comerzzia.dinosol.pos.gui.ventas.tickets;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.api.errorhandlers.ApiClientException;
import com.comerzzia.dinosol.api.client.loyalty.model.CouponDTO;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RecuperarTicketSadBusquedaException;
import com.comerzzia.dinosol.pos.gui.ventas.tarjetasregalo.ValidacionTarjetaRegaloController;
import com.comerzzia.dinosol.pos.gui.ventas.tarjetasregalo.ValidacionTarjetaRegaloView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.serviciosreparto.SeleccionarServicioRepartoController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.serviciosreparto.SeleccionarServicioRepartoView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.auxiliar.LineaPlasticoInexistente;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.auxiliar.LineaPlasticoInexistenteException;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.dto.LecturaCuponImporteDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.dto.LecturaQRLiquidacionDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.dto.LecturaQrBalanzaDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.dto.LecturaTarjetaBpDto;
import com.comerzzia.dinosol.pos.persistence.motivos.MotivosCambioPrecio;
import com.comerzzia.dinosol.pos.persistence.tickets.sad.TicketAnexoSadBean;
import com.comerzzia.dinosol.pos.services.articulos.QRBalanzaService;
import com.comerzzia.dinosol.pos.services.articulos.QRBalanzaService.LineaQRBalanza;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.codbarrasesp.DinoCodBarrasEspecialesException;
import com.comerzzia.dinosol.pos.services.codbarrasesp.DinoCodBarrasEspecialesServices;
import com.comerzzia.dinosol.pos.services.codbarrasesp.liquidacion.QRLiquidacionDTO;
import com.comerzzia.dinosol.pos.services.codbarrasesp.liquidacion.QRTipoLiquidacion;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionPromociones;
import com.comerzzia.dinosol.pos.services.cupones.CustomerCouponDTO;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.articulos.ArticulosRecargaService;
import com.comerzzia.dinosol.pos.services.motivos.MotivosCambioPrecioServices;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.rascas.RascasService;
import com.comerzzia.dinosol.pos.services.tarjetasregalo.TarjetasRegaloService;
import com.comerzzia.dinosol.pos.services.tarjetasregalo.TipoTarjetaRegaloDto;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.TarjetaIdentificacionDto;
import com.comerzzia.dinosol.pos.services.ticket.lineas.DinoLineaTicket;
import com.comerzzia.dinosol.pos.services.ticket.lineas.TarjetaRegaloDto;
import com.comerzzia.dinosol.pos.services.ticket.liquidacion.QRLiquidacionException;
import com.comerzzia.dinosol.pos.services.ticket.sad.TicketAnexoSadService;
import com.comerzzia.dinosol.pos.services.ventas.reparto.ServiciosRepartoService;
import com.comerzzia.dinosol.pos.services.ventas.reparto.dto.ServicioRepartoDto;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.BalanzaNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.IBalanza;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.ConsultaTarjetaFidelizadoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloView;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.persistence.core.acciones.AccionBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.permisos.PermisoException;
import com.comerzzia.pos.services.core.permisos.PermisosEfectivosAccionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.cupones.CuponAplicationException;
import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.cupones.CuponesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.aparcados.TicketsAparcadosService;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaDevolucionCambioException;
import com.comerzzia.pos.services.ticket.lineas.LineaDevolucionNuevoArticuloException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.lineas.LineasTicketServices;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.rest.client.tickets.ResponseGetTicketDev;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javafx.stage.Stage;

@Component
@Primary
@Scope("prototype")
public class DinoTicketManager extends TicketManager {

	private Logger log = Logger.getLogger(DinoTicketManager.class);
	
	public static Long ID_TIPO_PROMO_FICITIA_CUPON = -1L;

	public static final String ARTICULO_UNITARIO = "U";
		
	@Autowired 
	private DinoCodBarrasEspecialesServices dinoCodBarrasEspecialesService;
	
    @Autowired
    private LineasTicketServices lineasTicketServices;
    
    @Autowired
    private SesionPromociones sesionPromociones;

	@Autowired
	private TicketsService ticketsService;
	
	@Autowired
	private ArticulosRecargaService articulosRecargaService;
	
	@Autowired
	private TicketAnexoSadService ticketAnexoSadService;
	
	@Autowired
	private QRBalanzaService qrBalanzaService;
	
	@Autowired
	private TicketsAparcadosService ticketsAparcadosService;
	
	@Autowired
	private MediosPagosService mediosPagosService;
	
	@Autowired
	private CuponesServices cuponesServices;
	
	@Autowired
	private TarjetasRegaloService tarjetasRegaloService;
	
	@Autowired
	private AuditoriasService auditoriasService;
	
	@Autowired
	private RascasService rascasService;
	
	@Autowired
    private ArticulosService articulosService;
	
	@Autowired
	private MotivosCambioPrecioServices motivosCambioPrecioServices;
	
	@Autowired
	private ServiciosRepartoService serviciosRepartoService;
	
	final IVisor visor = Dispositivos.getInstance().getVisor();
	
	private TicketVentaAbono ticketVacio;
	
	private boolean recuperandoTicket;
	
	private QRLiquidacionDTO qrLiquidacionDTO; // DIN-112


	public TicketBean recuperarTicketCancelacion(String codigo, String codAlmacen, String codCaja) {
		TicketBean result = null;
		try {
			log.debug("recuperarTicketDevolucion() - Recuperando ticket...");
			
			List<Long> tiposDoc = Arrays.asList(18000L, 18001L, 18002L);
			
			// Probamos primero con el localizador
			// Obtenemos por localizador desde local
			List<TicketBean> tickets = ticketsService.consultarTicketLocalizador(codigo, tiposDoc);
			if (!tickets.isEmpty()) {
				result = tickets.get(0);
			}
			else {
				// Obtenemos por codigo desde local
				for(Long idTipoDoc : tiposDoc) {
					result = ticketsService.consultarTicketAbono(codAlmacen, codCaja, codigo, idTipoDoc);
				}
				if (result == null) {
					throw new TicketsServiceException("No se ha encontrado ticket con codigo: " + codigo);
				}
			}
		}
		catch (TicketsServiceException e) {
			log.error("recuperarTicketDevolucion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		return result;
	}

	public Boolean recuperarTicketRecarga(String codigo, String codAlmacen, String codCaja, Long idTipoDoc) {
		try {
			log.debug("recuperarTicketRecarga() - Recuperando ticket...");
			byte[] xmlTicketOrigen = null;

			// Obtenemos por localizador desde local
			List<TicketBean> tickets = ticketsService.consultarTicketLocalizador(codigo, Arrays.asList(idTipoDoc));
			if (!tickets.isEmpty()) {
				xmlTicketOrigen = tickets.get(0).getTicket();
				tratarTicketRecuperado(xmlTicketOrigen);
			}

			// Si no tenemos ticket, consultamos como id de documento en lugar de como localizador
			if (xmlTicketOrigen == null) {
				// Obtenemos por codigo desde local
				TicketBean ticketA = ticketsService.consultarTicketAbono(codAlmacen, codCaja, codigo, idTipoDoc);
				if (ticketA != null) {
					xmlTicketOrigen = ticketA.getTicket();
					tratarTicketRecuperado(xmlTicketOrigen);
				}
				else {
					throw new TicketsServiceException("No se ha encontrado ticket con codigo: " + codigo);
				}
			}
			
			if(ticketOrigen.getLineas().size() == 1) {
				for(String codart : articulosRecargaService.getConfiguracion().getArticulosRecargaMovil()) {
					if(ticketOrigen.getLinea(codart).size() == 1) {
						return true;
					}
				}
				return false;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			log.error("recuperarTicketDevolucion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			return false;
		}
	}

	@Override
	protected LineaTicket tratarCodigoBarraEspecialTicket(CodigoBarrasBean codBarrasEspecial) throws LineaTicketException {
		log.debug("tratarCodigoBarraEspecialTicket() - Tratando código de barras especial: " + codBarrasEspecial.getCodigoIntroducido());
		
		if (codBarrasEspecial.getDescripcion().equals("BP")) {
			return guardarTarjetaIdentificacionBp(codBarrasEspecial);
		}
		else if (codBarrasEspecial.getDescripcion().equals("FLYER")) {
			return leerCuponImporte(codBarrasEspecial);
		}
		else if (codBarrasEspecial.getDescripcion().equals("QR LIQUIDACION")) { // DIN-112
			return guardaQRLiquidacion(codBarrasEspecial);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
    private LineaTicket leerCuponImporte(CodigoBarrasBean codBarrasEspecial) throws LineaTicketException {
		log.debug("leerCuponImporte() _ Leyendo cupón con código: " + codBarrasEspecial.getCodigoIntroducido());
		
		BigDecimal importe = new BigDecimal(codBarrasEspecial.getCodticket()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		
		CuponAplicadoTicket cupon = new CuponAplicadoTicket();
		cupon.setTextoPromocion(I18N.getTexto("FLYER"));
		cupon.setCodigo(codBarrasEspecial.getCodigoIntroducido());
		cupon.setImporteTotalAhorrado(importe);
		cupon.setIdTipoPromocion(ID_TIPO_PROMO_FICITIA_CUPON);
		cupon.setTipoCupon("FLYER");
		
		ticketPrincipal.getCuponesAplicados().add(cupon);
		
	    return new LecturaCuponImporteDto();
    }

	private LecturaTarjetaBpDto guardarTarjetaIdentificacionBp(CodigoBarrasBean codBarrasEspecial) {
		DinoCabeceraTicket cabecera = (DinoCabeceraTicket) ticketPrincipal.getCabecera();
		String numeroTarjeta = codBarrasEspecial.getCodigoIntroducido();
		if (!cabecera.containsTarjeta(numeroTarjeta)) {
			TarjetaIdentificacionDto tarjetaIdentificacion = new TarjetaIdentificacionDto();
			tarjetaIdentificacion.setTipoTarjeta("BP");
			tarjetaIdentificacion.setNumeroTarjeta(numeroTarjeta);
			cabecera.addTarjetaIdentificacion(tarjetaIdentificacion);
			return new LecturaTarjetaBpDto(numeroTarjeta);
		}
		return null;
	}

	private LineaTicket leerQrBalanza(CodigoBarrasBean codBarrasEspecial, BigDecimal cantidad) throws LineaTicketException, QRLiquidacionException {
		List<LineaQRBalanza> lineas = null;

		try {
			lineas = qrBalanzaService.leerQrBalanza(codBarrasEspecial.getCodigoIntroducido());
		} catch (Exception e) {
			throw new LineaTicketException(e.getMessage(), e);			
		}
		
		int numLineas = lineas.size();

		List<Integer> numerosLineas = new ArrayList<Integer>();
		
		try {
			for (LineaQRBalanza lineaQR : lineas) {
				DinoLineaTicket linea = (DinoLineaTicket) damePrecioVenta(lineaQR.getCodart());
				
				BigDecimal cantidadLinea = lineaQR.getCantidad().multiply(cantidad);
				boolean esArticuloPesoVariable = "P".equals(linea.getArticulo().getBalanzaTipoArticulo());
				if(esArticuloPesoVariable && numLineas == 1) {
					cantidadLinea = lineaQR.getCantidad();
					cantidadLinea = cantidadLinea.multiply(new BigDecimal(cantidad.signum()));
				}
				linea.setCantidad(cantidadLinea);
	
				// si el precio no coincide (discrepancias entre precio tarifa y balanza)
				// se aplica el precio origen de la balanza
				if (linea.getPrecioTotalConDto().compareTo(lineaQR.getPrecio()) != 0) {
					linea.setPrecioTotalConDto(lineaQR.getPrecio());
					linea.resetPromociones();
					linea.setPrecioSinDto(lineaQR.getPrecio());
					linea.setPrecioTotalSinDto(lineaQR.getPrecio());
				}				
				
				linea.recalcularImporteFinal();				
				
				linea.setCodOperador(lineaQR.getOperador());
				linea.setCodSeccion(lineaQR.getDepartamento());
				
				if(lineas.size() == 1) {
					automatizaCambioPreciosLiquidacion(linea);
				}
				
				addLinea(linea);
				
				numerosLineas.add(linea.getIdLinea());			
			}
		} catch (LineaTicketException e) {
			String mensajeError = "Se ha producido un error al leer el ticket de balanza: " + e.getMessage();
			log.error("leerQrBalanza() - " + mensajeError + " - " + e.getMessage());
			
			if(!numerosLineas.isEmpty()) {
				for(Integer idLinea : numerosLineas) {
					eliminarLineaArticulo(idLinea);
				}
			}
			
			throw new LineaTicketException(mensajeError);
		}
		
		recalcularConPromociones();
		
		return new LecturaQrBalanzaDto();
	}



	public Boolean recuperarTicketContenidoDigital(String codigo, String codAlmacen, String codCaja, Long idTipoDoc) {
		try {
			log.debug("recuperarTicketContenidoDigital() - Recuperando ticket...");
			byte[] xmlTicketOrigen = null;

			// Obtenemos por localizador desde local
			List<TicketBean> tickets = ticketsService.consultarTicketLocalizador(codigo, Arrays.asList(idTipoDoc));
			if (!tickets.isEmpty()) {
				xmlTicketOrigen = tickets.get(0).getTicket();
				tratarTicketRecuperado(xmlTicketOrigen);
			}

			// Si no tenemos ticket, consultamos como id de documento en lugar de como localizador
			if (xmlTicketOrigen == null) {
				// Obtenemos por codigo desde local
				TicketBean ticketA = ticketsService.consultarTicketAbono(codAlmacen, codCaja, codigo, idTipoDoc);
				if (ticketA != null) {
					xmlTicketOrigen = ticketA.getTicket();
					tratarTicketRecuperado(xmlTicketOrigen);
				}
				else {
					throw new TicketsServiceException("No se ha encontrado ticket con codigo: " + codigo);
				}
			}
			
			if(ticketOrigen.getLineas().size() > 0) {
				for(String codart : articulosRecargaService.getConfiguracion().getArticulosPosaCard()) {
					if(ticketOrigen.getLinea(codart).size() == 1) {
						return true;
					}
				}
				for(String codart : articulosRecargaService.getConfiguracion().getArticulosPinPrinting()) {
					if(ticketOrigen.getLinea(codart).size() == 1) {
						return true;
					}
				}
				return false;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			log.error("recuperarTicketContenidoDigital() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			return false;
		}
	}
		
	@Override
	public void nuevoTicket() throws PromocionesServiceException, DocumentoException {
		String codigoParking = null;
		String horaSalidaParking = null;
		boolean generarQrParking = false;
		boolean isFormatoParkingQr = false;
		List<CustomerCouponDTO> cuponesLeidosActualmente = null;
		ServicioRepartoDto servicioRepartoSeleccionado = null;
		if(ticketPrincipal != null) {
			codigoParking = ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).getCodigoParking();
			horaSalidaParking = ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).getHoraSalidaParking();
			generarQrParking = ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).isGenerarQrParking(); 
			isFormatoParkingQr = ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).isCodigoParkingFormatoQr();
			cuponesLeidosActualmente = getCuponesLeidos();
			servicioRepartoSeleccionado = ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).getServicioRepartoDto();
		}
		
	    super.nuevoTicket();
	    
	    ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setCodigoParking(codigoParking);
	    ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setHoraSalidaParking(horaSalidaParking);
	    ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setGenerarQrParking(generarQrParking);
	    ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setCodigoParkingFormatoQr(isFormatoParkingQr);
	    ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setServicioRepartoDto(servicioRepartoSeleccionado);

	    if(cuponesLeidosActualmente == null) {
	    	((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setCuponesLeidos(new ArrayList<CustomerCouponDTO>());
	    }
	    else {
	    	((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setCuponesLeidos(cuponesLeidosActualmente);
	    }
	}
	
	@SuppressWarnings("rawtypes")
    @Override
	protected void recuperarDatosPersonalizados(TicketVenta ticketRecuperado) {
		((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setTarjetasIdentificacion(((DinoCabeceraTicket) ticketRecuperado.getCabecera()).getTarjetasIdentificacion());
		((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setServicioRepartoDto(((DinoCabeceraTicket) ticketRecuperado.getCabecera()).getServicioRepartoDto());
		
		if(((DinoCabeceraTicket) ticketRecuperado.getCabecera()).getCuponesLeidos() != null) {
			for(CustomerCouponDTO coupon : ((DinoCabeceraTicket) ticketRecuperado.getCabecera()).getCuponesLeidos()) {
				if(!coupon.isFromLoyaltyRequest()) {
					((DinoCabeceraTicket) ticketPrincipal.getCabecera()).getCuponesLeidos().add(coupon);
				}
			}
		}

		((DinoTicketVentaAbono) ticketPrincipal).setAuditoriasNoRegistradas(((DinoTicketVentaAbono) ticketRecuperado).getAuditoriasNoRegistradas());
		
		String servicioReparto = ((DinoCabeceraTicket) ticketRecuperado.getCabecera()).getServicioReparto();
		ServicioRepartoDto servicioRepartoDto = serviciosRepartoService.getServicioReparto(servicioReparto);
		if(servicioRepartoDto != null) {
			((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setServicioRepartoDto(servicioRepartoDto);
		}
		
		if(((DinoCabeceraTicket) ticketRecuperado.getCabecera()).getTransactionsSipay() != null){
			((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setTransactionsSipay(((DinoCabeceraTicket) ticketRecuperado.getCabecera()).getTransactionsSipay());
		}
	}
	
	@Override
	protected void recuperarDatosPersonalizadosLinea(LineaTicket lineaRecuperada, LineaTicket nuevaLineaArticulo) {
		if(lineaRecuperada instanceof DinoLineaTicket) {
			((DinoLineaTicket) nuevaLineaArticulo).setPrecioTotalManual(((DinoLineaTicket) lineaRecuperada).getPrecioTotalManual());
			((DinoLineaTicket) nuevaLineaArticulo).setTarjetaRegalo(((DinoLineaTicket) lineaRecuperada).getTarjetaRegalo());
			((DinoLineaTicket) nuevaLineaArticulo).setCodOperador(((DinoLineaTicket) lineaRecuperada).getCodOperador());
			((DinoLineaTicket) nuevaLineaArticulo).setCodSeccion(((DinoLineaTicket) lineaRecuperada).getCodSeccion());
			((DinoLineaTicket) nuevaLineaArticulo).setCodMotivo(((DinoLineaTicket) lineaRecuperada).getCodMotivo());
			((DinoLineaTicket) nuevaLineaArticulo).setHoraRegistro(((DinoLineaTicket) lineaRecuperada).getHoraRegistro());
		}
		else {
			log.warn("recuperarDatosPersonalizadosLinea() - La línea recuperada no está personalizada, por lo que se pueden haber perdido algunos datos de la línea original.");
		}
	}
	
	public LineaTicket damePrecioVenta(String codart) {
		DinoTicketManager ticketManagerTemporal = SpringContext.getBean(DinoTicketManager.class);
		try {
			ticketManagerTemporal.inicializarTicket();
			ticketManagerTemporal.getTicket().setCliente(this.getTicket().getCliente());
			
			// asignar datos de fidelizado si están asignados
			if (this.getTicket().getCabecera().getDatosFidelizado() != null) {
				this.getTicket().getCabecera().setDatosFidelizado(this.getTicket().getCabecera().getDatosFidelizado());
			}
			
			LineaTicket linea = ticketManagerTemporal.nuevaLineaArticuloCodart(codart, BigDecimal.ONE);
			ticketManagerTemporal.recalcularConPromociones();
			
			return linea;					
		} catch (DocumentoException | PromocionesServiceException | LineaTicketException e) {
			throw new RuntimeException("Error obteniendo precio de venta", e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public synchronized LineaTicket nuevaLineaArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen, boolean esLineaDevolucionPositiva) throws LineaTicketException {
		log.debug("nuevaLineaArticulo() - Creando nueva línea de artículo...");
		
		if(ticketPrincipal.getLineas().isEmpty()) {
			mostrarServiciosRepartoDisponibles(stage);
		}
		
		try {
			if(comprobarCupon(codArticulo)) {
				return null;
			}
		}
		catch (ApiClientException e) {
			log.error("nuevaLineaArticulo() - Error al comprobar cupón online: " + codArticulo, e);
			throw new LineaTicketException(I18N.getTexto(e.getLocalizedMessage()), e);
		}
		catch (CuponAplicationException e) {
			log.error("nuevaLineaArticulo() - Error al comprobar la aplicación del cupón : " + codArticulo, e);
			throw new LineaTicketException(I18N.getTexto(e.getLocalizedMessage()), e);
		}
		catch (Exception e) {
			log.error("nuevaLineaArticulo() - Error comprobando si el código es un cupón: " + codArticulo, e);
			throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."), e);
		}
		
		LineaTicketAbstract linea = null;
		LineaTicketAbstract lineaPlastico = null;
		BigDecimal precio = null;
		CodigoBarrasBean codBarrasEspecial = null;
		
		boolean pesarArticulo = stage != null;
		boolean articuloRegarga = false;
		boolean contenidoDigital = false;
		String codBarras = null;
		
		// Comprobamos si es codigo de barras especial o normal y actualizamos codigoArticulo y otras variables
		try {
			
			for(String codArtRecarga : articulosRecargaService.getConfiguracion().getArticulosRecargaMovil()){
				if(codArtRecarga.equals(codArticulo)){
					articuloRegarga=true;
					break;
				}
			}
			for(String codArtContenidoDigital : articulosRecargaService.getConfiguracion().getArticulosPinPrinting()){
				if(codArtContenidoDigital.equals(codArticulo)){
					contenidoDigital = true;
					break;
				}
			}
			for(String codArtContenidoDigital : articulosRecargaService.getConfiguracion().getArticulosPosaCard()){
				if(codArtContenidoDigital.equals(codArticulo)){
					contenidoDigital = true;
					break;
				}
			}
			
			codBarrasEspecial = dinoCodBarrasEspecialesService.esCodigoBarrasEspecial(codArticulo);
			if (codBarrasEspecial != null) {
				
				codBarras = codArticulo;
				
				// Ponemos la variable a falsa ya que se cogerá el peso del código de barras
//				pesarArticulo = false;
				
				if (codBarrasEspecial.getCodticket() != null) {
					if (codBarrasEspecial.getDescripcion().equals("QR BALANZA")) {
						BigDecimal cantidadCodigoBarras = new BigDecimal (cantidad.signum());
						String permiteMultiplicacion = codBarrasEspecial.getFidelizacion();
						if(StringUtils.isNotBlank(permiteMultiplicacion) && permiteMultiplicacion.equals("S")) {
							cantidadCodigoBarras = cantidad;
						}
						return leerQrBalanza(codBarrasEspecial, cantidadCodigoBarras);
					}
					else {
						return tratarCodigoBarraEspecialTicket(codBarrasEspecial);
					}
				}
				
				codArticulo = codBarrasEspecial.getCodart();
				String cantCodBar = codBarrasEspecial.getCantidad();
				if (cantCodBar != null) {
					cantidad = FormatUtil.getInstance().desformateaBigDecimal(cantCodBar, 3);
				}
				String precioCodBar = codBarrasEspecial.getPrecio();
				if (precioCodBar != null) {
					precio = FormatUtil.getInstance().desformateaBigDecimal(codBarrasEspecial.getPrecio(), 2);
				} else {
					precio = null;
				}
				
				if (codArticulo == null) {
					log.error(String.format("nuevaLineaArticulo() - El código de barra especial obtenido no es válido. CodArticulo: %s, cantidad: %s, precio: %s", codArticulo, cantidad, precio));
					throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
				}
			}
		} catch (LineaTicketException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error procesando el código de barras especial : " + codArticulo, e);
			throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
		}

		try {
			if (codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("Etiquetas Pesos")) {
				pesarArticulo = false;
			}
			
			if (codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("Etiquetas Precios")) {
				
				linea = damePrecioVenta(codArticulo);
				BigDecimal precioCodBarra =  FormatUtil.getInstance().desformateaBigDecimal(codBarrasEspecial.getPrecio());
				BigDecimal cantidadCalculada = calculaCantidadLinea(precioCodBarra, linea.getImporteTotalConDto());
				
//				boolean cantidadMultiplicada = false;
				if("U".equals(linea.getArticulo().getBalanzaTipoArticulo()) && !BigDecimalUtil.isIgual(cantidad, BigDecimal.ONE)) {
					cantidadCalculada = BigDecimalUtil.redondear(cantidadCalculada.multiply(cantidad), 3);
//					cantidadMultiplicada = true;
				}
				
				linea.setCantidad(cantidadCalculada);
				
				linea.recalcularImporteFinal();
				
				// Para el código de barras especial con prefijo 241 no se requiere pesar el artículo
				pesarArticulo = false;
			}
			else if (codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("MULT.ETIQUETA")) {
				linea = damePrecioVenta(codArticulo);
				BigDecimal precioCodBarra = FormatUtil.getInstance().desformateaBigDecimal(codBarrasEspecial.getPrecio());
				BigDecimal cantidadCalculada = calculaCantidadLinea(precioCodBarra, linea.getImporteTotalConDto());
				
				cantidadCalculada = BigDecimalUtil.redondear((cantidadCalculada.multiply(cantidad)), 3);
				
				linea.setCantidad(cantidadCalculada);
			}
			else {
				linea = lineasTicketServices.createLineaArticulo((TicketVenta) ticketPrincipal, codArticulo, desglose1, desglose2, cantidad, precio, createLinea());
				
				linea.setCantidad(tratarSignoCantidad(linea.getCantidad(), linea.getCabecera().getCodTipoDocumento()));
			}
			
			if(!recuperandoTicket) {
				lineaPlastico = tratarArticulosPlastico(cantidad, linea, lineaPlastico);
			}
			
			automatizaCambioPreciosLiquidacion(linea); // DIN-112
			
			if(isEsDevolucion() && tarjetasRegaloService.getTipoTarjeta(linea.getCodArticulo()) != null) {
				throw new LineaTicketException(I18N.getTexto("Las tarjetas regalos no se pueden devolver."));
			}
			
			if(pesarArticulo || BigDecimalUtil.isMenorACero(cantidad)) {
				TipoTarjetaRegaloDto tipoTarjeta = tarjetasRegaloService.getTipoTarjeta(linea.getCodArticulo());
				if(tipoTarjeta != null) {
					
					if(BigDecimalUtil.isMayor(linea.getCantidad(), BigDecimal.ONE)) {
						throw new LineaTicketException(I18N.getTexto("Las tarjetas regalos solo se pueden vender con cantidad 1."));
					}
					
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put(ValidacionTarjetaRegaloController.PARAM_CANTIDAD, linea.getCantidad());
					params.put(ValidacionTarjetaRegaloController.PARAM_TIPO_TARJETA, tipoTarjeta);
					POSApplication.getInstance().getMainView().showModalCentered(ValidacionTarjetaRegaloView.class, params, stage);
					
					TarjetaRegaloDto tarjetaRegalo = (TarjetaRegaloDto) params.get(ValidacionTarjetaRegaloController.PARAM_TARJETA_REGALO);
					if(tarjetaRegalo != null) {
						BigDecimal numTarjetaRegalo = getNumeroTarjetasRegalo(tarjetaRegalo.getNumeroTarjeta());
						numTarjetaRegalo = numTarjetaRegalo.add(linea.getCantidad());
						
						if(BigDecimalUtil.isMenorACero(numTarjetaRegalo)) {
							throw new LineaTicketException(I18N.getTexto("La cantidad total de este artículo no puede ser negativa."));
						}
						
						if(!BigDecimalUtil.isIgual(numTarjetaRegalo, BigDecimal.ONE) && !BigDecimalUtil.isIgual(numTarjetaRegalo, BigDecimal.ZERO)) {
							throw new LineaTicketException(I18N.getTexto("Este número de tarjeta regalo ya ha sido introducida en esta venta."));
						}
						
						if(BigDecimalUtil.isIgualACero(numTarjetaRegalo)) {
							anularTarjetaRegalo(linea, tarjetaRegalo);
						}
						else {
							BigDecimal importe = tarjetaRegalo.getSaldo();
							linea.setPrecioTotalSinDto(importe);
						}
						
						((DinoLineaTicket) linea).setTarjetaRegalo(tarjetaRegalo);
					}
					else {
						throw new LineaTicketException(I18N.getTexto("No se puede vender una tarjeta regalo sin especificar sus datos."));
					}
				}
			}
			
			if(esLineaDevolucionPositiva) {
				linea.setCantidad(linea.getCantidad().abs());
			}
			
			if(codBarras != null){
				if(codBarras.startsWith("251")) {
					codBarras = dinoCodBarrasEspecialesService.generarCodBarras241Equivalente(codBarras, linea);
				}
				linea.setCodigoBarras(codBarras);
			}
			if(articuloRegarga){
				((DinoLineaTicket) linea).setRecargaMovil(true); 
			}
			if(contenidoDigital){
				((DinoLineaTicket) linea).setContendioDigital(true);
			}
			//Si el artículo tiene en su campo FORMATO en BBDD...
			if(pesarArticulo && StringUtils.isNotBlank(linea.getArticulo().getBalanzaTipoArticulo()) && linea.getArticulo().getBalanzaTipoArticulo().trim().toUpperCase().equals(PESAR_ARTICULO)){
				IBalanza balanza = Dispositivos.getInstance().getBalanza();
				if(!(balanza instanceof BalanzaNoConfig)) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					POSApplication.getInstance().getMainView().showModalCentered(SolicitarPesoArticuloView.class, params, stage);
					if(params.containsKey(SolicitarPesoArticuloController.PARAM_PESO)) {
						BigDecimal peso = (BigDecimal) params.get(SolicitarPesoArticuloController.PARAM_PESO);
						
						if(peso == null || BigDecimalUtil.isMenorOrIgualACero(peso)) {
							throw new LineaTicketException(I18N.getTexto("No se ha podido pesar el artículo, compruebe la configuración de la balanza."));
						}
						
						linea.setCantidad(peso);
						((DinoLineaTicket)linea).setCodOperador(sesion.getSesionUsuario().getUsuario().getUsuario());
					}
					else {
						throw new LineaTicketException(I18N.getTexto("Este artículo no puede ser introducido sin ser pesado previamente."));
					}
				}
			}
			
			if (esDevolucion && ticketOrigen != null && !esLineaDevolucionPositiva) {
				if(idLineaDocOrigen == null){
					idLineaDocOrigen = getIdLineaTicketOrigen(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), linea.getCantidad().abs());
				}
				LineaTicketAbstract lineaOrigen = ticketOrigen.getLinea(idLineaDocOrigen);
				lineaOrigen.setPrecioTotalConDto(lineaOrigen.getImporteTotalConDto().setScale(6, BigDecimal.ROUND_HALF_UP).divide(lineaOrigen.getCantidad().setScale(6, BigDecimal.ROUND_HALF_UP),BigDecimal.ROUND_HALF_UP));
				linea.resetPromociones();
				linea.setPrecioSinDto(lineaOrigen.getPrecioConDto());
				linea.setPrecioTotalSinDto(lineaOrigen.getPrecioTotalConDto());
				linea.recalcularImporteFinal();
				
				linea.setLineaDocumentoOrigen(lineaOrigen.getIdLinea());
				
				actualizarCantidadesOrigenADevolver(lineaOrigen, lineaOrigen.getCantidadADevolver().add(linea.getCantidad().abs()));
			}
			
			addLinea(linea);
			
			if(lineaPlastico != null && !recuperandoTicket) {
				if(lineaPlastico instanceof LineaPlasticoInexistente) {
					ticketPrincipal.getTotales().recalcular();
					throw new LineaPlasticoInexistenteException(I18N.getTexto("El envase de plástico asociado no está disponible. Consulte con el administrador."));
				}
				
				addLinea(lineaPlastico);
				((DinoLineaTicket) linea).setIdLineaAsociado(lineaPlastico.getIdLinea());
				((DinoLineaTicket) lineaPlastico).setIdLineaAsociado(linea.getIdLinea());
			}
			
			ticketPrincipal.getTotales().recalcular();
		}
		catch (ArticuloNotFoundException | QRLiquidacionException e) {
			linea = null;
			
			qrLiquidacionDTO = null;
			
			ticketPrincipal.getTotales().recalcular();
			
			throw new LineaTicketException(e.getMessageI18N());
		}
		return (LineaTicket) linea;
	
	    
	}

	@SuppressWarnings("rawtypes")
	protected LineaTicketAbstract tratarArticulosPlastico(BigDecimal cantidad, LineaTicketAbstract linea, LineaTicketAbstract lineaPlastico) throws LineaTicketException, ArticuloNotFoundException {
		/* [DINO-108] Comprobamos si tenemos que meter el artículo de plástico correspondiente, o si tenemos que notificar un error */
		boolean articuloEsEnvasePlastico = ((DinoLineaTicket) linea).isEsPlastico();
		
		if(articuloEsEnvasePlastico && BigDecimalUtil.isMayorACero(cantidad) && !isEsDevolucion()) {
			throw new LineaTicketException(I18N.getTexto("Este artículo no se puede vender de manera unitaria"));
		}
		else {
			String codArtPlasticoAsociado = ((DinoLineaTicket) linea).getCodArtPlasticoAsociado();
			if(codArtPlasticoAsociado != null) {
				lineaPlastico = null;
				try {
					lineaPlastico = lineasTicketServices.createLineaArticulo((TicketVenta) ticketPrincipal, codArtPlasticoAsociado, null, null, cantidad, null, createLinea());
				}
				catch (ArticuloNotFoundException e) {
					log.error("tratarArticulosPlastico() - No se ha encontrado el artículo de plástico asociado.");
					return new LineaPlasticoInexistente();
				}
				BigDecimal cantidadLinea = linea.getCantidad();
				if(BigDecimalUtil.isMenor(cantidadLinea, BigDecimal.ONE)) {
					cantidadLinea = BigDecimal.ONE;
				}
				lineaPlastico.setCantidad(tratarSignoCantidad(cantidadLinea, linea.getCabecera().getCodTipoDocumento()));
				
				if (esDevolucion && ticketOrigen != null) {
					int idLineaDocOrigen = 0;
					try {
						idLineaDocOrigen = getIdLineaTicketOrigen(lineaPlastico.getCodArticulo(), lineaPlastico.getDesglose1(), lineaPlastico.getDesglose2(), lineaPlastico.getCantidad().abs());
					}
					catch (LineaDevolucionCambioException e) {
						return null;
					}
					
					LineaTicketAbstract lineaOrigen = ticketOrigen.getLinea(idLineaDocOrigen);
					lineaOrigen.setPrecioTotalConDto(lineaOrigen.getImporteTotalConDto().setScale(6, BigDecimal.ROUND_HALF_UP).divide(lineaOrigen.getCantidad().setScale(6, BigDecimal.ROUND_HALF_UP),BigDecimal.ROUND_HALF_UP));
					lineaPlastico.resetPromociones();
					lineaPlastico.setPrecioSinDto(lineaOrigen.getPrecioConDto());
					lineaPlastico.setPrecioTotalSinDto(lineaOrigen.getPrecioTotalConDto());
					lineaPlastico.recalcularImporteFinal();
					
					lineaPlastico.setLineaDocumentoOrigen(lineaOrigen.getIdLinea());
					
					actualizarCantidadesOrigenADevolver(lineaOrigen, lineaOrigen.getCantidadADevolver().add(lineaPlastico.getCantidad().abs()));
				}
			}
		}
		return lineaPlastico;
	}

	protected boolean comprobarCupon(String codArticulo) throws CuponUseException, CuponesServiceException, CuponAplicationException {
		if(StringUtils.isBlank(codArticulo)) {
			log.debug("comprobarCupon() - Código vacío");
			return false;
		}
		log.debug("comprobarCupon() - Comprobando si el código " + codArticulo + " es un cupón.");
		
		boolean isCupon = false;
		
		boolean esCuponNuevo = ((DinoSesionPromociones) sesionPromociones).isCouponWithPrefix(codArticulo);
		boolean esCuponAntiguo = ((DinoCuponesService) cuponesServices).esCuponAntiguo(codArticulo);
		
		if(esCuponNuevo) {
			try {
				CouponDTO couponDto = ((DinoSesionPromociones) sesionPromociones).validateCoupon(codArticulo);
				
                    if(couponDto == null) {
                            Integer lastStatus = ((DinoSesionPromociones) sesionPromociones).getLastCouponValidationStatus();
                            if(lastStatus != null && lastStatus == 400) {
                                    try {
                                            CouponDTO couponInfo = ((DinoSesionPromociones) sesionPromociones).getCoupon(codArticulo);
                                            if(couponInfo != null && isCouponMarkedAsUsed(couponInfo.getUses())) {
                                                    throw new CuponAplicationException(construirMensajeCuponCanjeado(couponInfo));
                                            }
                                    }
                                    catch (ApiClientException apiClientException) {
                                            if(!"Registro no encontrado".equals(apiClientException.getMessage())) {
                                                    throw apiClientException;
                                            }
                                            log.debug("comprobarCupon() - El cupón no existe en la consulta de detalle tras respuesta 400 de validación.");
                                    }

                                    throw new CuponAplicationException(I18N.getTexto("El cupón ya ha sido redimido."));
                            }
                    }

				for(CustomerCouponDTO cupon : getCuponesLeidos()) {
					if(cupon.getCouponCode().equals(codArticulo)) {
						// A petición de Dinosol, si es un cupón de los nuevos no se puede introducir dos veces en la venta.
						log.debug("comprobarCupon() - Este cupón ya ha sido introducido en la venta y es un cupón de los nuevos.");
						throw new CuponAplicationException(I18N.getTexto("Este cupón ya ha sido introducido en la venta actual."));
					}
				}
				
				if(couponDto != null) {
					CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO(couponDto.getCouponCode(), true, CustomerCouponDTO.CUPON_NUEVO);
					customerCouponDTO.setPromotionId(couponDto.getPromotionId());
					customerCouponDTO.setBalance(couponDto.getBalance());
					getCuponesLeidos().add(customerCouponDTO);
					
					isCupon = true;
				}
				else if (esCuponAntiguo) {
					CustomerCouponDTO coupon = new CustomerCouponDTO(codArticulo, false, CustomerCouponDTO.CUPON_ANTIGUO);
					getCuponesLeidos().add(coupon);
					
					isCupon = true;
				}
			}
			catch (ApiClientException exception) {
				if("Registro no encontrado".equals(exception.getMessage())) {
					if (esCuponAntiguo) {
						CustomerCouponDTO coupon = new CustomerCouponDTO(codArticulo, false, CustomerCouponDTO.CUPON_ANTIGUO);
						getCuponesLeidos().add(coupon);
						
						isCupon = true;
					}
				}
				else {
					throw exception;
				}
			}
		}
		else if (esCuponAntiguo) {
			CustomerCouponDTO coupon = new CustomerCouponDTO(codArticulo, false, CustomerCouponDTO.CUPON_ANTIGUO);
			getCuponesLeidos().add(coupon);
			
			isCupon = true;
                }

                return isCupon;
        }

        private boolean isCouponMarkedAsUsed(Object uses) {
                Boolean used = (Boolean) invokeUsesGetter(uses, "getUsed");
                return Boolean.TRUE.equals(used);
        }

        private static final String CLASS_ID_STORE = "D_ALMACENES_TBL.CODALM";
        private static final String CLASS_ID_TICKET = "D_TICKETS_TBL.UID_TICKET";
        private static final ObjectMapper LOYALTY_COUPON_MAPPER = createLoyaltyCouponMapper();
        private static final String[] CLASS_ID_FIELD_CANDIDATES = { "classId", "idClase", "class_id", "idclase" };
        private static final String[] OBJECT_ID_FIELD_CANDIDATES = { "objectId", "idObjeto", "value", "object_id", "idobjeto" };

        private String construirMensajeCuponCanjeado(CouponDTO coupon) {
                Object uses = coupon != null ? coupon.getUses() : null;

                Date redemptionDate = (Date) invokeUsesGetter(uses, "getLastUse");
                if (redemptionDate == null) {
                        redemptionDate = (Date) invokeUsesGetter(uses, "getFirstUse");
                }

                String formattedDate = redemptionDate != null ? FormatUtil.getInstance().formateaFecha(redemptionDate) : "-";

                String codigoCupon = (coupon != null && StringUtils.isNotBlank(coupon.getCouponCode())) ? coupon.getCouponCode()
                                : "-";

                JsonNode couponNode = convertCouponToJsonNode(coupon);
                String centroClassValue = obtenerValorPorClassId(couponNode, CLASS_ID_STORE);
                String ticketClassValue = obtenerValorPorClassId(couponNode, CLASS_ID_TICKET);

                String lockByTerminalId = StringUtils.trimToNull((String) invokeUsesGetter(uses, "getLockByTerminalId"));
                String lastTerminalId = StringUtils.trimToNull((String) invokeUsesGetter(uses, "getLastTerminalId"));

                String centro = obtenerCentro(centroClassValue, lockByTerminalId, lastTerminalId);
                String ticket = obtenerNumeroTicket(ticketClassValue, lockByTerminalId);

                return "CUPÓN " + codigoCupon + " CANJEADO EL DIA " + formattedDate + " EN EL CENTRO " + centro
                                + " EN EL NÚMERO DE TICKET " + ticket;
        }

        private String obtenerCentro(String centroClassValue, String lockByTerminalId, String lastTerminalId) {
                String centro = StringUtils.trimToNull(centroClassValue);
                if (StringUtils.isBlank(centro)) {
                        centro = extraerParteTerminal(lockByTerminalId, true);
                }
                if (StringUtils.isBlank(centro)) {
                        centro = extraerParteTerminal(lastTerminalId, true);
                }
                return StringUtils.isBlank(centro) ? "-" : centro;
        }

        private String obtenerNumeroTicket(String ticketClassValue, String lockByTerminalId) {
                String ticket = StringUtils.trimToNull(ticketClassValue);
                if (StringUtils.isBlank(ticket)) {
                        ticket = extraerParteTerminal(lockByTerminalId, false);
                }
                return StringUtils.isBlank(ticket) ? "-" : ticket;
        }

        private String extraerParteTerminal(String valor, boolean primeraParte) {
                if (StringUtils.isBlank(valor)) {
                        return null;
                }

                String[] partes = valor.split("[\\-#/_]");
                if (partes.length > 1) {
                        if (primeraParte) {
                                return StringUtils.trimToNull(partes[0]);
                        }

                        for (int indice = partes.length - 1; indice >= 0; indice--) {
                                if (StringUtils.isNotBlank(partes[indice])) {
                                        return StringUtils.trimToNull(partes[indice]);
                                }
                        }
                        return null;
                }

                return primeraParte ? StringUtils.trimToNull(valor) : null;
        }

        private static ObjectMapper createLoyaltyCouponMapper() {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
                return mapper;
        }

        private JsonNode convertCouponToJsonNode(Object coupon) {
                if (coupon == null) {
                        return null;
                }

                try {
                        return LOYALTY_COUPON_MAPPER.valueToTree(coupon);
                }
                catch (IllegalArgumentException exception) {
                        if (log.isDebugEnabled()) {
                                log.debug("construirMensajeCuponCanjeado() - No se pudo convertir el cupón a JSON: "
                                                + exception.getMessage(), exception);
                        }
                        return null;
                }
        }

        private Object invokeUsesGetter(Object uses, String methodName) {
                return invokeGetter(uses, methodName);
        }

        private Object invokeGetter(Object target, String methodName) {
                if (target == null) {
                        return null;
                }

                try {
                        Method method = target.getClass().getMethod(methodName);
                        return method.invoke(target);
                }
                catch (Exception e) {
                        if (log.isDebugEnabled()) {
                                log.debug("comprobarCupon() - No se pudo obtener el valor '" + methodName
                                                + "' del objeto de tipo " + target.getClass().getName(), e);
                        }
                        return null;
                }
        }

        private String obtenerValorPorClassId(JsonNode couponNode, String classId) {
                if (couponNode == null || StringUtils.isBlank(classId)) {
                        return null;
                }

                JsonNode match = findNodeByClassId(couponNode, classId);
                if (match == null) {
                        return null;
                }

                JsonNode objectIdNode = getFieldIgnoreCase(match, OBJECT_ID_FIELD_CANDIDATES);
                if (objectIdNode == null || objectIdNode.isNull()) {
                        return null;
                }

                String value = StringUtils.trimToNull(objectIdNode.asText());
                return value;
        }

        private JsonNode findNodeByClassId(JsonNode node, String classId) {
                if (node == null || StringUtils.isBlank(classId)) {
                        return null;
                }

                if (node.isObject()) {
                        JsonNode classIdNode = getFieldIgnoreCase(node, CLASS_ID_FIELD_CANDIDATES);
                        if (classIdNode != null && !classIdNode.isNull()) {
                                String value = StringUtils.trimToNull(classIdNode.asText());
                                if (StringUtils.equalsIgnoreCase(classId, value)) {
                                        return node;
                                }
                        }

                        Iterator<JsonNode> values = node.elements();
                        while (values.hasNext()) {
                                JsonNode match = findNodeByClassId(values.next(), classId);
                                if (match != null) {
                                        return match;
                                }
                        }
                }
                else if (node.isArray()) {
                        for (JsonNode element : node) {
                                JsonNode match = findNodeByClassId(element, classId);
                                if (match != null) {
                                        return match;
                                }
                        }
                }

                return null;
        }

        private JsonNode getFieldIgnoreCase(JsonNode node, String... candidateNames) {
                if (node == null || !node.isObject() || candidateNames == null) {
                        return null;
                }

                for (String candidate : candidateNames) {
                        if (candidate == null) {
                                continue;
                        }
                        JsonNode direct = node.get(candidate);
                        if (direct != null) {
                                return direct;
                        }
                }

                Iterator<String> fieldNames = node.fieldNames();
                while (fieldNames.hasNext()) {
                        String fieldName = fieldNames.next();
                        for (String candidate : candidateNames) {
                                if (StringUtils.equalsIgnoreCase(fieldName, candidate)) {
                                        return node.get(fieldName);
                                }
                        }
                }

                return null;
        }

    @SuppressWarnings("unchecked")
        private void anularTarjetaRegalo(LineaTicketAbstract lineaNueva, TarjetaRegaloDto tarjetaRegalo) {
        tarjetaRegalo.setEstado(TarjetaRegaloDto.ESTADO_ANULADA);
        for(DinoLineaTicket linea : (List<DinoLineaTicket>) ticketPrincipal.getLineas()) {
                TarjetaRegaloDto tarjetaRegaloLinea = linea.getTarjetaRegalo();
			if(tarjetaRegaloLinea != null && tarjetaRegaloLinea.getNumeroTarjeta().equals(tarjetaRegalo.getNumeroTarjeta())) {
				tarjetaRegaloLinea.setEstado(TarjetaRegaloDto.ESTADO_ANULADA);

				lineaNueva.setPrecioSinDto(linea.getPrecioTotalSinDto());
				lineaNueva.setPrecioTotalSinDto(linea.getPrecioTotalSinDto());
				lineaNueva.setPrecioConDto(linea.getPrecioTotalSinDto());
				lineaNueva.setPrecioTotalConDto(linea.getPrecioTotalSinDto());
				lineaNueva.recalcularImporteFinal();
    		}
    	}
	}

	@SuppressWarnings("unchecked")
	private BigDecimal getNumeroTarjetasRegalo(String numeroTarjeta) {
    	BigDecimal numTarjetasRegalo = BigDecimal.ZERO;
    	for(DinoLineaTicket linea : (List<DinoLineaTicket>) ticketPrincipal.getLineas()) {
    		if(linea.getTarjetaRegalo() != null && linea.getTarjetaRegalo().getNumeroTarjeta().equals(numeroTarjeta)) {
    			numTarjetasRegalo = numTarjetasRegalo.add(linea.getCantidad());
    		}
    	}
		return numTarjetasRegalo;
	}

	@Override
	public synchronized LineaTicket nuevaLineaArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Integer idLineaDocOrigen) throws LineaTicketException {
		return nuevaLineaArticulo(codArticulo, desglose1, desglose2, cantidad, null, idLineaDocOrigen, false);
	}

	public BigDecimal  calculaCantidadLinea(BigDecimal precioCodBarra, BigDecimal precioLinea){
		BigDecimal cantidad = BigDecimal.ONE;
		
		if(!BigDecimalUtil.isIgualACero(precioCodBarra) && !BigDecimalUtil.isIgualACero(precioLinea)){
			cantidad = precioCodBarra.divide(precioLinea, 5, RoundingMode.HALF_UP);
		}
		
		if(isEsDevolucion()) {
			cantidad = cantidad.negate();
		}
		
		return cantidad;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Integer getIdLineaTicketOrigen(String codArticulo, String desglose1, String desglose2, BigDecimal cantidadAbs) throws LineaTicketException {
		List<LineaTicket> lineasOrigen = ticketOrigen.getLinea(codArticulo);
		Integer idLinea = null;
		
		boolean encontrado = false;
		
		BigDecimal precioTotalConDtoAnterior = null;
		boolean preciosDistintos = false;

		for (LineaTicket lineaOrigen : lineasOrigen) {
			if (lineaOrigen.getCodArticulo().equals(codArticulo) && lineaOrigen.getDesglose1().equals(desglose1) && lineaOrigen.getDesglose2().equals(desglose2)) {
				encontrado = true;
				
				if(precioTotalConDtoAnterior == null) {
					precioTotalConDtoAnterior = lineaOrigen.getPrecioTotalConDto();
				}
				else {
					if(!BigDecimalUtil.isIgual(precioTotalConDtoAnterior, lineaOrigen.getPrecioTotalConDto()) && !BigDecimalUtil.isIgualACero(lineaOrigen.getPrecioTotalConDto())) {
						preciosDistintos = true;
					}
				}
				
				if (BigDecimalUtil.isMayorOrIgualACero(lineaOrigen.getCantidad().subtract(lineaOrigen.getCantidadDevuelta().add(lineaOrigen.getCantidadADevolver().add(cantidadAbs))))) {
					if (idLinea == null) {
						idLinea = lineaOrigen.getIdLinea();
					}
				}
			}
		}
		if (!encontrado) {
			throw new LineaDevolucionNuevoArticuloException(I18N.getTexto("El artículo {0} no se ha encontrado en el documento origen de la devolución.", codArticulo));
		}
		if (idLinea == null) { // Se ha encontrado pero idLinea es null -> cantidad superada
			throw new LineaDevolucionCambioException(I18N.getTexto("La cantidad a devolver del artículo supera a la cantidad vendida."));
		}
		if (preciosDistintos) {
			throw new LineaTicketException(I18N.getTexto("El artículo {0} existe en varias líneas del ticket original con diferentes precios, deberá indicar la línea de devolución manualmente.", codArticulo));
		}
		return idLinea;
	}

	/**
	 * Recupera un ticket para la pantalla de Servicios a domicilio.
	 * @param codigo : Código del localizador.
	 * @param codAlmacen : Código de la tienda.
	 * @param codCaja : Código de la caja usada.
	 * @param idTipoDoc : Tipo de documento que vamos a buscar.
	 * @return Boolean
	 * @throws ServicioDomicilioBusquedaException 
	 * @throws TicketsServiceException 
	 */
	public Boolean recuperarTicketServicioDomicilio(String codigo, String codAlmacen, String codCaja, Long idTipoDoc) throws RecuperarTicketSadBusquedaException, TicketsServiceException {
		log.debug("recuperarTicketServicioDomicilio() - Iniciando la recuperación del ticket...");
		try {
			/* Iniciamos con el parseo del codigo en el localizador */
			byte[] xmlTicketOrigen = null;
			
			/* Obtener por localizador en total */
			log.debug("recuperarTicketServicioDomicilio() - Consultamos el ticket por el localizador : " + codigo);
			List<TicketBean> tickets = ticketsService.consultarTicketLocalizador(codigo, Arrays.asList(idTipoDoc));
			if (!tickets.isEmpty()) {
				log.debug("recuperarTicketServicioDomicilio() - Ticket encontrado :" + tickets.get(0).getCodTicket());
				xmlTicketOrigen = tickets.get(0).getTicket();
				tratarTicketRecuperado(xmlTicketOrigen);
			}

			/* Si no tenemos ticket, consultamos como id de documento en lugar de como localizador */
			if (xmlTicketOrigen == null) {
				if(!StringUtils.isNumeric(codigo)) {
					throw new RecuperarTicketSadBusquedaException(I18N.getTexto("No se ha encontrado el ticket consultado."));
				}
				
				/* Obtenemos por código desde local */
				log.debug("recuperarTicketServicioDomicilio() - Consultamos el ticket por CodAlmacen(" + codAlmacen + "),"
						+ " CodCaja(" + codCaja + "), Código(" + codigo + ") y idTipoDoc(" + idTipoDoc + ")");
				TicketBean ticketA = ticketsService.consultarTicketAbono(codAlmacen, codCaja, codigo, idTipoDoc);
				if (ticketA != null) {
					log.debug("recuperarTicketServicioDomicilio() - Ticket encontrado :" + ticketA.getCodTicket());
					xmlTicketOrigen = ticketA.getTicket();
					tratarTicketRecuperado(xmlTicketOrigen);
				}
				/* En caso de no encontrar ninguno devolveremos un exception */
				else {
					String mensajeError = "No se ha encontrado ticket con código: " + codigo;
					log.error("recuperarTicketServicioDomicilio() - " + mensajeError);
					throw new TicketsServiceException(mensajeError);
				}
			}
			
			TicketAnexoSadBean ticketAnexo = null;
			try {
				ticketAnexo = ticketAnexoSadService.consultar(ticketOrigen.getCabecera().getUidTicket());
			}
			catch(Exception e) {
				log.error("recuperarTicketServicioDomicilio() - Ha habido un error al consultar el ticket anexo: " + e.getMessage(), e);
			}
			
			/* Realizamos las comprobaciones que tienen que ver con el Servicio a domicilio.
			 * El ticket no debe tener datos de sad en la cabecera. */
			if(((DinoCabeceraTicket) ticketOrigen.getCabecera()).getDatosSad() != null || ticketAnexo != null){
				String mensajeError = "El ticket solicitado ya realizó un pedido a domicilio";
				log.error("recuperarTicketServicioDomicilio() - " + mensajeError);	
				throw new RecuperarTicketSadBusquedaException(mensajeError);
			}
			
			log.debug("recuperarTicketServicioDomicilio() - Finalizada la recuperación del ticket...");
			return true;
		}
		catch (Exception e1) {
			log.error("recuperarTicketServicioDomicilio() - " + e1.getMessage());
			throw new RecuperarTicketSadBusquedaException(e1.getMessage());
		}
	}
	
	@Override
	public boolean comprobarImporteMaximoOperacion(Stage stage) {
		BigDecimal importeMaximo = documentoActivo.getImporteMaximoSinImpuestos();
		
		if(importeMaximo != null){
	    	if(BigDecimalUtil.isMayor(ticketPrincipal.getCabecera().getTotales().getBase().abs(), importeMaximo)){
	    		VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Imposible realizar la venta, el total sin impuestos de la venta supera el máximo permitido ({0}) para el tipo de documento: {1}", FormatUtil.getInstance().formateaImporte(importeMaximo), documentoActivo.getCodtipodocumento()), stage);
	    		return false;
	    	}
		}else{
			importeMaximo = documentoActivo.getImporteMaximo();
			if(importeMaximo != null){
				if(BigDecimalUtil.isMayor(ticketPrincipal.getCabecera().getTotales().getTotalAPagar().abs(), importeMaximo)){
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Imposible realizar la venta, el total de la venta supera el máximo permitido ({0}) para el tipo de documento: {1}", FormatUtil.getInstance().formateaImporte(importeMaximo), documentoActivo.getCodtipodocumento()), stage);
					return false;
				}
			}
		}
        
        return true;
    }
	
    public List<CustomerCouponDTO> getCuponesLeidos() {
    	return ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).getCuponesLeidos();
    }
    
    public void addCuponLeido(String codigoCupon) {
    	if(StringUtils.isBlank(codigoCupon)) {
    		return;
    	}
    	else {
    		codigoCupon = codigoCupon.trim();
    	}
    	
    	if(getCuponesLeidos() == null) {
    		((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setCuponesLeidos(new ArrayList<CustomerCouponDTO>());
    	}
    	
    	CustomerCouponDTO coupon = new CustomerCouponDTO(codigoCupon, false, CustomerCouponDTO.CUPON_ANTIGUO);
    	
    	getCuponesLeidos().add(coupon);
    	
    	log.debug("addCuponLeido() - Cupones leídos en la venta actual: " + getCuponesLeidos());
    }
    
    @Override
    protected void asignarLineasDevueltas(ResponseGetTicketDev res) {
        super.asignarLineasDevueltas(res);
        if(ticketOrigen.getCabecera() instanceof DinoCabeceraTicket) {
        	if(res != null) {
        		((DinoCabeceraTicket) ticketOrigen.getCabecera()).setRecuperadoTicketOrigenCentral(true);
        	}
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void salvarTicketVacio() {
		if(ticketPrincipal.getIdTicket() == null) {
			try {
				ticketsService.setContadorIdTicket((Ticket) ticketPrincipal);
			}
			catch (Exception e) {
				log.error("salvarTicketVacio() - No se ha podido asignar ID al ticket vacío: " + e.getMessage(), e);
			}
		}
		
    	//Inicializamos un ticket sin lineas
		ticketVacio = SpringContext.getBean(TicketVentaAbono.class);
		ticketVacio.getCabecera().inicializarCabecera(ticketVacio);
		ticketVacio.inicializarTotales();
        ticketVacio.setCliente(ticketPrincipal.getCliente());
        ticketVacio.setCajero(ticketPrincipal.getCabecera().getCajero());
        IPagoTicket cambio = ticketsService.createPago();
        cambio.setMedioPago(MediosPagosService.medioPagoDefecto);
        ticketVacio.getCabecera().getTotales().setCambio(cambio);
        ticketVacio.getTotales().recalcular();
        ticketVacio.getCabecera().setDocumento(documentoActivo);
        ticketVacio.setEsDevolucion(ticketPrincipal.isEsDevolucion());
        //Añadimos los datos del contador obtenido antes
        ticketVacio.setIdTicket(ticketPrincipal.getIdTicket());
        ticketVacio.getCabecera().setSerieTicket(ticketPrincipal.getCabecera().getSerieTicket());
        ticketVacio.getCabecera().setCodTicket(ticketPrincipal.getCabecera().getCodTicket());
        ticketVacio.getCabecera().setDatosDocOrigen(ticketPrincipal.getCabecera().getDatosDocOrigen());
        ticketVacio.getCabecera().setUidTicket(ticketPrincipal.getCabecera().getUidTicket());
        //Añadimos las lineas de impuestos
        for (SubtotalIvaTicket sub : (List<SubtotalIvaTicket>)ticketPrincipal.getCabecera().getSubtotalesIva()) {
			sub.setBase(BigDecimalUtil.redondear(BigDecimal.ZERO, 2));
			sub.setCuota(BigDecimalUtil.redondear(BigDecimal.ZERO, 2));
			sub.setCuotaRecargo(BigDecimalUtil.redondear(BigDecimal.ZERO, 2));
			sub.setImpuestos(BigDecimalUtil.redondear(BigDecimal.ZERO, 2));
			sub.setTotal(BigDecimalUtil.redondear(BigDecimal.ZERO, 2));
			ticketVacio.getCabecera().getSubtotalesIva().add(sub);
		}	
        List<AuditoriaDto> auditoriasNoRegistradas = ((DinoTicketVentaAbono) ticketPrincipal).getAuditoriasNoRegistradas();
		if(auditoriasNoRegistradas != null && !auditoriasNoRegistradas.isEmpty()) {
			for(AuditoriaDto auditoria : auditoriasNoRegistradas) {
				((DinoTicketVentaAbono) ticketVacio).addAuditoriaNoRegistrada(auditoria);
			}
		}
        
        // Añadimos una línea por si se hubiesen borrado manualmente todas las líneas en la pantalla de 
        // devolución antes de cancelar la devolución.
        if(isTicketVacio() && ticketOrigen != null) {
        	List<LineaTicket> lineasOrigen = ticketOrigen.getLineas();
        	if(lineasOrigen != null && !lineasOrigen.isEmpty()) {
				LineaTicket linea = (LineaTicket) lineasOrigen.get(0);
				try {
					log.debug("salvarTicketVacio() - Insertamos la primera línea del ticket origen ya que el ticket activo no tiene líneas.");
					nuevaLineaArticulo(linea.getCodArticulo(), null, null, BigDecimal.ONE, null);
					log.debug("salvarTicketVacio() - Línea insertada al ticket vacío.");
				}
				catch (Exception e) {
					log.error("salvarTicketVacio() - Error al añadir línea al ticket vacío: " + e.getMessage(), e);
				}
        	}
        	else {
        		log.warn("salvarTicketVacio() - El ticket origen no tiene líneas");
        	}
        }
        
        for(LineaTicket lineaOriginal : (List<LineaTicket>) ticketPrincipal.getLineas()) {
        	ticketVacio.addLinea(lineaOriginal.clone());
        	
        	LineaTicket lineaNegativa = lineaOriginal.clone();
        	
        	List<PromocionLineaTicket> promocionesNuevas = new ArrayList<PromocionLineaTicket>();
        	for(PromocionLineaTicket promocion : lineaNegativa.getPromociones()) {
        		PromocionTicket promocionTicket = new PromocionTicket();
        		promocionTicket.setIdPromocion(promocion.getIdPromocion());
        		promocionTicket.setIdTipoPromocion(promocion.getIdTipoPromocion());
        		promocionTicket.setCodAcceso(promocion.getCodAcceso());
        		promocionTicket.setAcceso(promocion.getAcceso());
        		promocionTicket.setExclusiva(false);
        		promocionTicket.setTipoDescuento(promocion.getTipoDescuento());
        		
        		PromocionLineaTicket promocionNueva = new PromocionLineaTicket(promocionTicket);
        		promocionNueva.setCantidadPromocion(promocion.getCantidadPromocion().negate());
        		promocionNueva.addImporteTotalDto(promocion.getImporteTotalDtoMenosMargen().negate());
        		
        		promocionesNuevas.add(promocionNueva);
        	}
        	lineaNegativa.setPromociones(promocionesNuevas);
        	
        	lineaNegativa.setCantidad(lineaNegativa.getCantidad().negate());
        	lineaNegativa.recalcularImporteFinal();
        	ticketVacio.addLinea(lineaNegativa);
        	
        	TarjetaRegaloDto tarjetaRegalo = ((DinoLineaTicket) lineaOriginal).getTarjetaRegalo();
			if(tarjetaRegalo != null) {
        		anularTarjetaRegalo(lineaOriginal, tarjetaRegalo);
        	}
        }
        
        PagoTicket pagoVacio = ticketsService.createPago();
        pagoVacio.setMedioPago(MediosPagosService.medioPagoDefecto);
        pagoVacio.setImporte(BigDecimal.ZERO);
		ticketVacio.addPago(pagoVacio);
        
        //Lo salvamos
        try {
			ticketsService.registrarTicket(ticketVacio, documentoActivo, false);
		} catch (TicketsServiceException e) {
			log.error("salvarTicketVacio() - Ha ocurrido un error al salvar un ticket vacío: " + e.getMessage(), e);
		}
    }
    
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void recuperarTicket(TicketAparcadoBean ticketAparcado) throws TicketsServiceException, PromocionesServiceException, DocumentoException, LineaTicketException {
		try {
			log.debug("recuperarTicket() - Recuperando ticket...");
			
			recuperandoTicket = true;
	
			TicketVenta ticketRecuperado = recuperarDatosTicket(ticketAparcado);
	
			// Establecemos el contador
			contadorLinea = ticketPrincipal.getLineas().size() + 1;
			// Eliminamos el ticket recuperado de la lista de tickets aparcados.
			ticketsAparcadosService.eliminarTicket(ticketAparcado.getUidTicket());
	        
			AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setTipo("RECUPERAR TICKET");
			auditoria.setUidTicket(ticketAparcado.getUidTicket());
			if (ticketPrincipal.getCabecera().getDatosFidelizado() != null) {
				auditoria.setNumTarjetaFidelizado(ticketPrincipal.getCabecera().getDatosFidelizado().getNumTarjetaFidelizado());
				auditoria.setNombreFidelizado(ticketPrincipal.getCabecera().getDatosFidelizado().getNombre());
			}
			auditoria.setImporteTicket(ticketPrincipal.getTotales().getTotalAPagar());
			auditoria.setNumArticulosTicket(((TicketVentaAbono)ticketRecuperado).getCabecera().getCantidadArticulos());
			auditoriasService.guardarAuditoria(auditoria);
		}
		finally {
			recuperandoTicket = false;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TicketVenta recuperarDatosTicket(TicketAparcadoBean ticketAparcado) throws PromocionesServiceException, DocumentoException, LineaTicketException {
		try {
			recuperandoTicket = true;
			
			nuevoTicket();
			// Realizamos el unmarshall
			log.debug("Ticket recuperado:\n" + new String(ticketAparcado.getTicket()));
			TicketVenta ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(ticketAparcado.getTicket(), getTicketClasses(documentoActivo).toArray(new Class[] {}));
	
			ticketPrincipal.getCabecera().setIdTicket(ticketRecuperado.getIdTicket());
			ticketPrincipal.getCabecera().setUidTicket(ticketRecuperado.getUidTicket());
			ticketPrincipal.getCabecera().setCodTicket(ticketRecuperado.getCabecera().getCodTicket());
			ticketPrincipal.getCabecera().setSerieTicket(ticketRecuperado.getCabecera().getSerieTicket());
	
			if (ticketAparcado.getUsuario() == null || !ticketAparcado.getUsuario().equals("FASTPOS")) {
				// Recuperamos el cliente del ticket aparcado
				ticketPrincipal.getCabecera().setCliente(ticketRecuperado.getCabecera().getCliente());
			}
			String uidDiarioCaja = sesion.getSesionCaja().getUidDiarioCaja();
			ticketPrincipal.getCabecera().setUidDiarioCaja(uidDiarioCaja);
	
			recuperarDatosPersonalizados(ticketRecuperado);
	
			List<LineaTicket> lineas = ticketRecuperado.getLineas();
			for (LineaTicket lineaRecuperada : lineas) {
				String codigo = lineaRecuperada.getCodigoBarras();
				String desglose1 = lineaRecuperada.getDesglose1();
				String desglose2 = lineaRecuperada.getDesglose2();
				if (StringUtils.isBlank(codigo)) {
					codigo = lineaRecuperada.getCodArticulo();
				}
				else {
					desglose1 = null;
					desglose2 = null;
				}
				LineaTicket nuevaLineaArticulo = nuevaLineaArticulo(codigo, desglose1, desglose2, lineaRecuperada.getCantidad(), null);
				
				// Forzamos de nuevo la cantidad ya que al meter la línea puede que recalcule la cantidad en el caso de
				// los códigos de barra 241
				nuevaLineaArticulo.setCantidad(lineaRecuperada.getCantidad());
	
				nuevaLineaArticulo.setDocumentoOrigen(lineaRecuperada.getDocumentoOrigen());
	
				nuevaLineaArticulo.setDesArticulo(lineaRecuperada.getDesArticulo());
				nuevaLineaArticulo.setDescuentoManual(lineaRecuperada.getDescuentoManual());
				BigDecimal nuevoPrecio = lineaRecuperada.getPrecioTotalSinDto();
				nuevaLineaArticulo.setPrecioTotalSinDto(nuevoPrecio);
				BigDecimal precioSinDto = lineaRecuperada.getPrecioSinDto();
				nuevaLineaArticulo.setPrecioSinDto(precioSinDto);
				nuevaLineaArticulo.setCodigoBarras(lineaRecuperada.getCodigoBarras());
				nuevaLineaArticulo.setNumerosSerie(lineaRecuperada.getNumerosSerie());
				nuevaLineaArticulo.setEditable(lineaRecuperada.isEditable());
				recuperarDatosPersonalizadosLinea(lineaRecuperada, nuevaLineaArticulo);
			}
	
			FidelizacionBean datosFidelizado = ticketRecuperado.getCabecera().getDatosFidelizado();
			if (datosFidelizado != null && StringUtils.isNotBlank(datosFidelizado.getNumTarjetaFidelizado())) {
				try {
					FidelizacionBean tarjetaFidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(datosFidelizado.getNumTarjetaFidelizado(),
					        ticketPrincipal.getCabecera().getUidActividad());
					ticketPrincipal.getCabecera().setDatosFidelizado(tarjetaFidelizado);
					if(tarjetaFidelizado != null) {
						List<CustomerCouponDTO> cuponesFidelizado = (List<CustomerCouponDTO>) tarjetaFidelizado.getAdicionales().get("coupons");
						if (cuponesFidelizado != null) {
							setCuponesLeidos(cuponesFidelizado);
						}
					}
				}
				catch (ConsultaTarjetaFidelizadoException e) {
					log.debug("recuperarTicket() - Error al consultar fidelizado", e);
					FidelizacionBean fidelizacionBean = new FidelizacionBean();
					fidelizacionBean.setNumTarjetaFidelizado(datosFidelizado.getNumTarjetaFidelizado());
					ticketPrincipal.getCabecera().setDatosFidelizado(fidelizacionBean);
				}
			}
	
			for (PagoTicket pago : (List<PagoTicket>) ticketRecuperado.getPagos()) {
				pago.setMedioPago(mediosPagosService.getMedioPago(pago.getCodMedioPago()));
				ticketPrincipal.getPagos().add(pago);
			}
	
			recalcularConPromociones();
			return ticketRecuperado;
		}
		finally {
			recuperandoTicket = false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized LineaTicket nuevaLineaArticuloCodart(String codArticulo, BigDecimal cantidad) throws LineaTicketException {
		log.debug("nuevaLineaArticuloCodart() - Creando nueva línea de artículo para comprobar el precio para el artículo " + codArticulo);
		
		LineaTicketAbstract linea = null;
		BigDecimal precio = null;

		try {
			linea = lineasTicketServices.createLineaArticulo((TicketVenta) ticketPrincipal, codArticulo, null, null, cantidad, precio, createLinea());
			linea.setCantidad(tratarSignoCantidad(linea.getCantidad(), linea.getCabecera().getCodTipoDocumento()));

			addLinea(linea);
			ticketPrincipal.getTotales().recalcular();
		}
		catch (ArticuloNotFoundException e) {
			log.error("nuevaLineaArticuloCodart() - Ha habido un error al al comprobar el precio del artículo: " + e.getMessage(), e);
		}
		return (LineaTicket) linea;

	}
	
	@Override
	public PagoTicket nuevaLineaPago(String codigo, BigDecimal importe, boolean modificable, boolean eliminable, Integer paymentId, boolean introducidoPorCajero) {
		PagoTicket pago = super.nuevaLineaPago(codigo, importe, modificable, eliminable, paymentId, introducidoPorCajero);
		
		((DinoTicketVentaAbono) ticketPrincipal).setLastPaymentId(paymentId);
		
		return pago;
	}

	public void setCuponesLeidos(List<CustomerCouponDTO> cuponesLeidos) {
		if(cuponesLeidos != null && !cuponesLeidos.isEmpty()) {
			getCuponesLeidos().addAll(cuponesLeidos);
		}
	}

	public void aplicarPromocionesBp() {
        ((DinoSesionPromociones) sesionPromociones).aplicarPromocionesBp((TicketVentaAbono) ticketPrincipal);
        ticketPrincipal.getTotales().recalcular();
	}

	@SuppressWarnings("rawtypes")
	public int getRascasConcedidos() {
		ITicket ticket = ticketPrincipal;
		return getRascasConcedidos(ticket);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int getRascasConcedidos(ITicket ticket) {
		int rascasConcedidos = 0;
		for(PromocionTicket promocion : (List<PromocionTicket>) ticket.getPromociones()) {
			if(promocion.getIdTipoPromocion().equals(1001L) && promocion.getIdPromocion().equals(rascasService.getIdPromocionControlada())) {
				if(promocion.getAdicionales() != null) {
					String puntosCabecera = (String) promocion.getAdicionales().get("puntos_cabecera");
					if(StringUtils.isBlank(puntosCabecera)) {
						puntosCabecera = "0";
					}
					String puntosLineas = (String) promocion.getAdicionales().get("puntos_lineas");
					if(StringUtils.isBlank(puntosLineas)) {
						puntosLineas = "0";
					}
					rascasConcedidos = rascasConcedidos + Integer.valueOf(puntosCabecera) + Integer.valueOf(puntosLineas);
				}
			}
		}
		return rascasConcedidos;
	}
	
	public TicketVentaAbono getTicketVacio() {
		return ticketVacio;
	}
	
	@Override
	public void eliminarTicketCompleto() throws TicketsServiceException, PromocionesServiceException, DocumentoException {
		super.eliminarTicketCompleto();
		
		this.ticketVacio = null;
	}
	
	private void automatizaCambioPreciosLiquidacion(LineaTicketAbstract linea) throws LineaTicketException, QRLiquidacionException{  // DIN-112

		try {
			if (hayQrLiquidacionGuardado()) {
				log.debug("automatizaCambioPreciosLiquidacion() - Existe QR LIQUIDACION válido introducido");
			}
			else {
				log.debug("automatizaCambioPreciosLiquidacion() - No hay QR LIQUIDACION introducido");
				return;
			}

			coincideQrLiquidacionArticuloIntroducido(linea);

			LineaTicket lineaOriginal = ((LineaTicket) linea).clone();
			aplicaQRLiquidacion(linea);
			guardaAuditoria(linea, lineaOriginal, QRTipoLiquidacion.QR_LIQUIDACION_DESCUENTO);

			qrLiquidacionDTO = null;
			log.debug("automatizaCambioPreciosLiquidacion() - QR LIQUIDACION usado y borrado");
		}
		catch (LineaTicketException | QRLiquidacionException e) {
			log.error("automatizaCambioPreciosLiquidacion() - Error aplicando QR Liquidacion: " + e.getMessage(), e);
			qrLiquidacionDTO = null;
			log.error("automatizaCambioPreciosLiquidacion() - QR LIQUIDACION borrado");
			throw e;

		}
	}
	
	protected void guardaAuditoria(LineaTicketAbstract linea, LineaTicket lineaOriginal, QRTipoLiquidacion tipoLiquidacion) throws QRLiquidacionException { // DIN-112

		if (linea.getGenerico()) {
			log.debug("guardaAuditoria() - No se guardara auditoría por ser el artículo genérico: " + linea.getCodArticulo());
			return;
		}

		try {
			log.info("guardaAuditoria() - El cajero '" + sesion.getSesionUsuario().getUsuario() + "' ha cambiado el precio manual usando QR LIQUIDACION; Precio Original: "
			        + lineaOriginal.getPrecioTotalTarifaOrigen() + ". Precio liquidacion: " + linea.getPrecioTotalConDto());

			log.debug("guardaAuditoria() - Guardando auditoria: ");
			AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setTipo(tipoLiquidacion.label);
			auditoria.setUidTicket(ticketPrincipal.getUidTicket());
			auditoria.setCodart(linea.getCodArticulo());
			auditoria.setPrecioAnterior(lineaOriginal.getPrecioTotalSinDto());
			auditoria.setPrecioNuevo(linea.getPrecioTotalConDto());
			auditoria.setCantidadLinea(linea.getCantidad());
			auditoria.setFecha(new Date());
			MotivosCambioPrecio motivo = motivosCambioPrecioServices.consultarPorCodigo(tipoLiquidacion.motivo);
			auditoria.setCodMotivo(motivo.getCodMotivo());
			auditoria.setDesMotivo(motivo.getDesMotivo());

			if (qrLiquidacionDTO != null) {
				auditoria.setCajeroOperacion(qrLiquidacionDTO.getCodOperador());
			}
			
			((DinoTicketVentaAbono) ticketPrincipal).addAuditoriaNoRegistrada(auditoria);
		}
		catch (Exception e) {
			String error = "Error guardando auditoria de cambio de precio por código, tipo: " + tipoLiquidacion.label;
			log.error("guardaAuditoria() - " + error);
			throw new QRLiquidacionException(error);
		}
	}
	
	private void aplicaQRLiquidacion(LineaTicketAbstract linea) { // DIN-112
		log.debug("aplicaQRLiquidacion() - Aplicando QR LIQUIDACIÓN sobre artículo introducido " + linea.getCodArticulo());
		
		if(qrLiquidacionDTO.getTipoLiquidacion().equals(QRTipoLiquidacion.QR_LIQUIDACION_CAMBIO_PRECIO)){
			aplicaCambioPrecioQRLiquidacion(linea);
		}
		else if(qrLiquidacionDTO.getTipoLiquidacion().equals(QRTipoLiquidacion.QR_LIQUIDACION_DESCUENTO)) {
			aplicaPorcentajeDescuentoQRLiquidacion(linea);
		}
		
		((DinoLineaTicket) linea).setCodMotivo(qrLiquidacionDTO.getTipoLiquidacion().motivo);
	}
	
	private void aplicaPorcentajeDescuentoQRLiquidacion(LineaTicketAbstract linea)  { // DIN-112
		log.debug("aplicaPorcentajeDescuentoQRLiquidacion - Aplicando porcentaje de descuento usando QR LIQUIDACION sobre el artículo:" + linea.getCodArticulo());

		BigDecimal descuentoLiquidacion = qrLiquidacionDTO.getPrecioLiquidacion();
		linea.setDescuentoManual(descuentoLiquidacion);
		linea.recalcularImporteFinal();
	}

	private void aplicaCambioPrecioQRLiquidacion(LineaTicketAbstract linea) { // DIN-112
		log.debug("aplicaCambioPrecioQRLiquidacion() - Aplicando cambio de precio a la línea");

		BigDecimal precioLiquidacion = qrLiquidacionDTO.getPrecioLiquidacion();
		linea.setPrecioSinDto(precioLiquidacion);
		linea.setPrecioTotalSinDto(precioLiquidacion);
		linea.recalcularImporteFinal();
	}
	
	private void coincideQrLiquidacionArticuloIntroducido(LineaTicketAbstract linea) throws LineaTicketException { // DIN-112

		log.debug("coincideQrLiquidacionArticuloIntroducido() - Comprobando que codigo barras del artículo introducido y QR coinciden..");
		try {
			String codArticulo = linea.getArticulo().getCodArticulo();
			
			if(codArticulo.equals(qrLiquidacionDTO.getCodArticulo()) && qrLiquidacionDTO.getCodEAN13().startsWith("251")) {
				return;
			}
			
			if(!codArticulo.equals(qrLiquidacionDTO.getCodArticulo())) {
				throw new LineaTicketException(I18N.getTexto("QR no coincide con artículo introducido"));
			}
			
			List<ArticuloCodBarraBean> codBarraArticuloIntroducido = articulosService.consultarCodigosBarrasArticulo(codArticulo);
			if (codBarraArticuloIntroducido.isEmpty()) {
				throw new LineaTicketException(I18N.getTexto("QR no coincide con artículo introducido"));
			}
			
			boolean coincide = false;
			for(ArticuloCodBarraBean codigoBarras : codBarraArticuloIntroducido) {
				if(codigoBarras.getCodigoBarras().equals(qrLiquidacionDTO.getCodEAN13())) {
					coincide = true;
				}
			}
			
			if(!coincide) {
				coincide = qrLiquidacionDTO.getCodEAN13().equals(linea.getCodArticulo());
			}
			
			if (!coincide) {
				throw new LineaTicketException(I18N.getTexto("QR no coincide con artículo introducido"));
			}

		}
		catch (LineaTicketException e) {
			log.error("coincideQrLiquidacionArticuloIntroducido() - Error: " + e.getMessage());
			qrLiquidacionDTO = null;
			log.error("coincideQrLiquidacionArticuloIntroducido() - QR borrado");
			throw e;
		}
		catch (ArticuloNotFoundException | ArticulosServiceException e) {
			log.debug("coincideQrLiquidacionArticuloIntroducido() - Error inesperado: " + e.getMessage() + e);
			throw new LineaTicketException(I18N.getTexto("QR no coincide con artículo introducido"), e);
		}
	}
	
	private LineaTicket guardaQRLiquidacion(CodigoBarrasBean codBarrasEspecial) throws LineaTicketException { // DIN-112
		log.debug("guardaQRLiquidacion() - Iniciando lectura de QR LIQUIDACION...");
		qrLiquidacionDTO = null;
		
		try {
			String codigoQRLiquidacion = codBarrasEspecial.getCodigoIntroducido();
			qrLiquidacionDTO = dinoCodBarrasEspecialesService.obtenDatosQrLiquidacionDTO(codigoQRLiquidacion);
			
			if (qrLiquidacionDTO == null || !qrLiquidacionDTO.esVigente()) {
				String error = I18N.getTexto("Código QR no vigente. Contacte con un responsable.");

				if (StringUtils.isBlank(error) && !hayPermisoModificarLinea()) {
					error = I18N.getTexto("No tiene permisos para editar la línea. Contacte con un responsable.");
				}

				qrLiquidacionDTO = null;
				log.error("guardaQRLiquidacion() - " + error + " QR no guardado");
				throw new LineaTicketException(error);
			}
			
			log.debug("guardaQRLiquidacion() - QR LIQUIDACION guardado");
			
			return new LecturaQRLiquidacionDto();
		}
		catch (DinoCodBarrasEspecialesException e) {
			String error = I18N.getTexto( "Error leyendo QR LIQUIDACION no se ha podido guardar");
			log.debug("guardaQRLiquidacion() - " + error + " QR no guardado");
			throw new LineaTicketException(e);
		}
	}
	
	private boolean hayPermisoModificarLinea() { // DIN-112
		log.debug("hayPermisoCambioLinea() - Comprobando si hay permisos para modificar línea...");
		try {
			compruebaPermisos(FacturacionArticulosController.PERMISO_MODIFICAR_LINEA);
			return true;
		}
		catch (SinPermisosException e) {
			return false;
		}
	}

	public void compruebaPermisos(String operacion) throws SinPermisosException { // DIN-112
    	log.trace("compruebaermisos() - Comprobando permisos efectivos para la operación: " + operacion);
    	
    	AccionBean accion = POSApplication.getInstance().getMainView().getCurrentAccion();
    	
    	Sesion sesion = SpringContext.getBean(Sesion.class);
        if (accion == null || sesion.getSesionUsuario() == null) {
            log.warn("compruebaPermisos() - Se ha solicitado comprobación de permisos para sin tener cargada la Acción o Sesión de usuario.");
            throw new SinPermisosException();
        }
        try {
            PermisosEfectivosAccionBean permiso = sesion.getSesionUsuario().getPermisosEfectivos(accion);
            if (!permiso.isPuedeEjecucion(operacion)) {
                log.info("compruebaPermisos() - Acción "+ accion+ " sin permiso "+ operacion +" para usuario "+ permiso.getIdUsuario());
                throw new SinPermisosException();
            }
        }
        catch (PermisoException ex) {
            log.error("compruebaPermisos() - Error consultando permisos en base de datos: " + ex.getMessage(), ex);
            throw new SinPermisosException();
        }
    }

	public boolean hayQrLiquidacionGuardado() { // DIN-112
		return qrLiquidacionDTO != null;
	}

	
	public QRLiquidacionDTO getQrLiquidacionDTO() {
		return qrLiquidacionDTO;
	}

	
	public void setQrLiquidacionDTO(QRLiquidacionDTO qrLiquidacionDTO) {
		this.qrLiquidacionDTO = qrLiquidacionDTO;
	}
	
	@Override
	protected BackgroundTask<Void> crearClaseRegistrarTicketTask(Stage stage, SalvarTicketCallback callback, List<DatosRespuestaPagoTarjeta> datosRespuesta) {
		return new DinoRegistrarTicketTask(stage, callback, datosRespuesta);
	}
	
	protected class DinoRegistrarTicketTask extends RegistrarTicketTask {

		public DinoRegistrarTicketTask(Stage stage, SalvarTicketCallback callback, List<DatosRespuestaPagoTarjeta> pagosAutorizados) {
			super(stage, callback, pagosAutorizados);
			mostrarVentanaCargando = true;
		}
		
	}

	public Boolean esVentaContenidoDigital() {
		log.debug("esVentaContenidoDigital() - Comprobando si la venta contiene contenido digital");
		try {
			if (ticketPrincipal.getLineas().size() > 0) {
				for (int i = 0; i < ticketPrincipal.getLineas().size(); i++) {
					for (String codPOSA : articulosRecargaService.getConfiguracion().getArticulosPosaCard()) {
						if (((LineaTicket) ticketPrincipal.getLineas().get(i)).getCodArticulo().equals(codPOSA)) {
							return true;
						}
					}
					for (String codPinPrinting : articulosRecargaService.getConfiguracion().getArticulosPinPrinting()) {
						if (((LineaTicket) ticketPrincipal.getLineas().get(i)).getCodArticulo().equals(codPinPrinting)) {
							return true;
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.error("esVentaContenidoDigital() - Error comprobando si la venta contiene contenido digital: " + e.getMessage());
		}
		return false;
	}
	
	public Boolean esVentaRecargaMovil() {
		log.debug("esVentaRecargaMovil() - Comprobando si la venta contiene recargas de tarjetas moviles");
		try {
			if (ticketPrincipal.getLineas().size() > 0) {
				for (int i = 0; i < ticketPrincipal.getLineas().size(); i++) {
					for (String codRecarga : articulosRecargaService.getConfiguracion().getArticulosRecargaMovil()) {
						if (((LineaTicket) ticketPrincipal.getLineas().get(i)).getCodArticulo().equals(codRecarga)) {
							return true;
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.error("esVentaRecargaMovil() - Error comprobando si la venta contiene recargas de tarjetas moviles: " + e.getMessage());
		}
		return false;
	}

	private void mostrarServiciosRepartoDisponibles(Stage stage) throws LineaTicketException {
		List<ServicioRepartoDto> servicios = serviciosRepartoService.getServiciosReparto();

		if (servicios != null && stage != null) {
			boolean hayServicioIndicado = ((DinoCabeceraTicket) ticketPrincipal.getCabecera()).getServicioRepartoDto() != null;
			if (ticketPrincipal.getLineas().isEmpty() && hayServicioIndicado) {
				return;
			}

			HashMap<String, Object> params = new HashMap<String, Object>();
			POSApplication.getInstance().getMainView().showModalCentered(SeleccionarServicioRepartoView.class, params, stage);

			if (params.containsKey(SeleccionarServicioRepartoController.PARAM_SERVICIO_SELECCIONADO)) {
				ServicioRepartoDto servicioSeleccionado = (ServicioRepartoDto) params.get(SeleccionarServicioRepartoController.PARAM_SERVICIO_SELECCIONADO);
				log.debug("mostrarServiciosRepartoDisponibles() - Servicio seleccionado: " + servicioSeleccionado);

				((DinoCabeceraTicket) ticketPrincipal.getCabecera()).setServicioRepartoDto(servicioSeleccionado);
				ticketPrincipal.getCliente().setCodtar(servicioSeleccionado.getCodTar());
			}
			else if (params.containsKey(SeleccionarServicioRepartoController.PARAM_CANCELAR)) {
				throw new LineaTicketException(I18N.getTexto("No se puede añadir una línea sin indicar el servicio de reparto."));
			}
			else {
				log.warn("mostrarServiciosRepartoDisponibles() - Ha habido un error al salir de la pantalla de servicios de reparto. Se abrirá de nuevo la pantalla.");
				mostrarServiciosRepartoDisponibles(stage);
			}
		}
	}

}