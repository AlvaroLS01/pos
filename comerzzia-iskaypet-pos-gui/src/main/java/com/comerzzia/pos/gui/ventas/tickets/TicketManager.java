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


package com.comerzzia.pos.gui.ventas.tickets;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.core.EtiquetaBean;
import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.omnichannel.TransaccionCajaDTO;
import com.comerzzia.api.model.omnichannel.TransaccionCajaDetalleBean;
import com.comerzzia.api.model.sales.ArticulosDevueltosBean;
import com.comerzzia.api.rest.client.clientes.ClientesRest;
import com.comerzzia.api.rest.client.clientes.ConsultarClienteRequestRest;
import com.comerzzia.api.rest.client.clientes.ResponseGetClienteRest;
import com.comerzzia.api.rest.client.exceptions.HttpServiceRestException;
import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.api.rest.client.exceptions.ValidationDataRestException;
import com.comerzzia.api.rest.client.exceptions.ValidationRequestRestException;
import com.comerzzia.api.rest.client.fidelizados.FidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.api.rest.client.servicios.FacturarServicioRequestRest;
import com.comerzzia.api.rest.client.servicios.ServiciosRest;
import com.comerzzia.api.rest.client.tickets.ConsultarTicketCodPedidoRequestRest;
import com.comerzzia.api.rest.client.tickets.ConsultarTicketRequestRest;
import com.comerzzia.api.rest.client.tickets.ResponseGetTicketDev;
import com.comerzzia.api.rest.client.tickets.TicketLocalizadorRequestRest;
import com.comerzzia.api.rest.client.tickets.TicketsRest;
import com.comerzzia.api.rest.client.till.transactions.TillTransactionRequestRest;
import com.comerzzia.api.rest.client.till.transactions.TillTransactionsRest;
import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.core.model.notificaciones.Notificacion.Tipo;
import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.core.servicios.documents.LocatorParseException;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.BalanzaNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.IBalanza;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.ConsultaTarjetaFidelizadoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.FidelizacionNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.ITarjeta;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaCallback;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaException;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController.LineaInsertadaNoPermitidaException;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloView;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.pos.persistence.articulos.etiquetas.EtiquetaArticuloBean;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.ConfigContadorBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.parametros.ConfigContadorParametroBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.persistence.tickets.TicketExample;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.apartados.ApartadosService;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.clientes.ClienteConstraintViolationException;
import com.comerzzia.pos.services.clientes.ClienteNotFoundException;
import com.comerzzia.pos.services.clientes.ClientesService;
import com.comerzzia.pos.services.clientes.ClientesServiceException;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesServices;
import com.comerzzia.pos.services.core.config.configContadores.ServicioConfigContadores;
import com.comerzzia.pos.services.core.config.configContadores.parametros.ConfigContadoresParametrosException;
import com.comerzzia.pos.services.core.config.configContadores.rangos.ConfigContadoresRangosConstraintViolationException;
import com.comerzzia.pos.services.core.config.configContadores.rangos.ConfigContadoresRangosException;
import com.comerzzia.pos.services.core.config.configContadores.rangos.ServicioConfigContadoresRangos;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.core.usuarios.UsuarioNotFoundException;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import com.comerzzia.pos.services.core.usuarios.UsuariosServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.cupones.CuponAplicationException;
import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.giftcard.GiftCardService;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.notificaciones.Notificaciones;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketEntregaCuenta;
import com.comerzzia.pos.services.ticket.TicketPagosApartado;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.aparcados.TicketsAparcadosService;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket;
import com.comerzzia.pos.services.ticket.cabecera.TarjetaRegaloTicket;
import com.comerzzia.pos.services.ticket.cabecera.TotalesTicket;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.lineas.DocumentoOrigen;
import com.comerzzia.pos.services.ticket.lineas.LineaDevolucionCambioException;
import com.comerzzia.pos.services.ticket.lineas.LineaDevolucionNuevoArticuloException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.lineas.LineasTicketServices;
import com.comerzzia.pos.services.ticket.pagos.EntregaCuentaTicket;
import com.comerzzia.pos.services.ticket.pagos.EntregasCuentaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.stage.Stage;

@Component
@Scope("prototype")
@SuppressWarnings({"unchecked", "rawtypes"})
public class TicketManager {
    
    protected Logger log = Logger.getLogger(getClass());
    
	protected ITicket ticketPrincipal;
    
    protected TicketVenta ticketOrigen;
    
    protected Integer contadorLinea;
    
    protected ITarjeta creditoMgr;
    
    protected boolean esDevolucion;
    
    protected boolean esOperacionTarjetaRegalo;
    
    protected boolean esRecargaTarjetaRegalo;
    
    protected boolean devolucionTarjetaRegalo;
    
    protected boolean esFacturacionVentaCredito;
    
    protected boolean tieneVigencias;
    
    protected Boolean procesoTarjetaRegaloActivo;
    
    protected ContadorBean contador;
    
    public static final String PESAR_ARTICULO = "P";
    
    @Autowired
    private ApartadosService apartadosService;
    @Autowired
    private MediosPagosService mediosPagosService;
    @Autowired
    private VariablesServices variablesServices;
    @Autowired
    private LineasTicketServices lineasTicketServices;
    @Autowired
    private TicketsService ticketsService;
    @Autowired
    private TicketsAparcadosService ticketsAparcadosService;
    @Autowired
    protected Sesion sesion;
	@Autowired
	protected Documentos documentos;
    @Autowired
    private SesionPromociones sesionPromociones;
    @Autowired
    private CopiaSeguridadTicketService copiaSeguridadTicketService;
    @Autowired
    private ClientesService clientesService;
    @Autowired
    private CodBarrasEspecialesServices codBarrasEspecialesServices;
    @Autowired
    private ServicioConfigContadores servicioConfigContadores;
    @Autowired
    private ServicioContadores servicioContadores;
    @Autowired
    private ServicioConfigContadoresRangos servicioConfigContadoresRangos;
    
    @Autowired
    private UsuariosService usuariosService;
    
    @Autowired
    private GiftCardService giftCardService;
    
    @Autowired
    private LocatorManager locatorManager;
    
    /* Atributos que afectan a la venta */
    protected TipoDocumentoBean documentoActivo; // Nos servirá para averiguar el signo de las líneas, el importeMaximo e importeMaximoSinImpuestos
    // Indica el los impuestos a aplicar y la plantilla del documento a imprimir
    
    
    public TicketManager(){
        creditoMgr = Dispositivos.getInstance().getTarjeta();
    }
    
    public void init() throws SesionInitException{
        log.debug("init() - Inicializando sesión de ticket...");
    }
    
    protected LineaTicketAbstract createLinea() {
    	return SpringContext.getBean(LineaTicket.class);
    }
    
    public ITicket getTicket() {
        return ticketPrincipal;
    }
    
    public void setTicket(ITicket ticket) {
    	this.ticketPrincipal = ticket;
    }

	public TicketVenta getTicketOrigen(){
        return ticketOrigen;
    }
    
    public void nuevoTicket() throws PromocionesServiceException, DocumentoException{
        inicializarTicket();
	    guardarCopiaSeguridadTicket();
    }

	public void inicializarTicket() throws DocumentoException, PromocionesServiceException {
	    log.debug("inicializarTicket() - Creando nuevo ticket con valores iniciales...");
        documentoActivo = getNuevoDocumentoActivo();
        ticketPrincipal = SpringContext.getBean(getTicketClass(documentoActivo));
        crearTicket();
        
        devolucionTarjetaRegalo = false;
        esRecargaTarjetaRegalo = false;
        esOperacionTarjetaRegalo = false;
        esDevolucion = false;
        esFacturacionVentaCredito = false;
        ticketPrincipal.setEsDevolucion(false);
    }
    
    public TipoDocumentoBean getNuevoDocumentoActivo() throws DocumentoException{
    	return sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
    }
    
    protected Class<? extends ITicket> getTicketClass(TipoDocumentoBean tipoDocumento){
    	String claseDocumento = tipoDocumento.getClaseDocumento();
    	if(claseDocumento != null){
    		try {
				return (Class<? extends ITicket>) Class.forName(claseDocumento);
			} catch (ClassNotFoundException e) {
				log.error(String.format("getTicketClass() - Clase %s no encontrada, devolveremos TicketVentaAbono", claseDocumento));
			}
    	}
		return TicketVentaAbono.class;
    }
    
	/**
	 * Devuelve la lista de clases que el Unmarshaller debe conocer.
	 * Además de la clase root, hay que pasarle la lista de superClasses de la
	 * root en orden descendente
	 * */
	public List<Class<?>> getTicketClasses(TipoDocumentoBean tipoDocumento) {
		List<Class<?>> classes = new LinkedList<>();
		
		// Obtenemos la clase root
		Class<?> clazz = SpringContext.getBean(getTicketClass(tipoDocumento)).getClass();
		
		// Generamos lista de clases "ancestras" de la principal
		Class<?> superClass = clazz.getSuperclass();
		while (!superClass.equals(Object.class)) {
			classes.add(superClass);
			superClass = superClass.getSuperclass();
		}
		// Las ordenamos descendentemente
		Collections.reverse(classes);
		
		//Añadimos la clase principal y otras necesarias
		classes.add(clazz);
		classes.add(SpringContext.getBean(LineaTicket.class).getClass());
		classes.add(SpringContext.getBean(CabeceraTicket.class).getClass());
		classes.add(SpringContext.getBean(TotalesTicket.class).getClass());
		classes.add(SpringContext.getBean(PagoTicket.class).getClass());

		return classes;
	}
    
	public void nuevoTicketPagosApartado() throws PromocionesServiceException, DocumentoException{    	
    	
    	log.debug("nuevoTicketApartado() - Creando nuevo ticket con valores iniciales...");
        documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.APARTADOS);
        ticketPrincipal = SpringContext.getBean(TicketPagosApartado.class);
        ticketPrincipal.getCabecera().inicializarCabecera(ticketPrincipal);
        ((TicketPagosApartado)ticketPrincipal).inicializarTotales();
        ticketPrincipal.setCliente(sesion.getAplicacion().getTienda().getCliente().clone());
        ticketPrincipal.setCajero(sesion.getSesionUsuario().getUsuario());
        ticketPrincipal.getCabecera().getTotales().setCambio(SpringContext.getBean(PagoTicket.class , MediosPagosService.medioPagoDefecto));
        ticketPrincipal.getCabecera().setDocumento(documentoActivo);
        
        devolucionTarjetaRegalo = false;
        esRecargaTarjetaRegalo = false;
        esOperacionTarjetaRegalo = false;
        esDevolucion = false;
        ticketPrincipal.setEsDevolucion(false);
    }
    
	protected void crearTicket() throws PromocionesServiceException, DocumentoException{
        
        ticketPrincipal.getCabecera().inicializarCabecera(ticketPrincipal);
        ((TicketVentaAbono)ticketPrincipal).inicializarTotales();
        ticketPrincipal.setCliente(sesion.getAplicacion().getTienda().getCliente().clone());
        ticketPrincipal.setCajero(sesion.getSesionUsuario().getUsuario());
        ticketPrincipal.getCabecera().getTotales().setCambio(SpringContext.getBean(PagoTicket.class , MediosPagosService.medioPagoDefecto));
        ticketPrincipal.getTotales().recalcular();
        contadorLinea = 1;
        
        // Establecemos los parámetros de tipo de documento del ticket
        cambiarTipoDocumento(documentoActivo);
        
        // Actualizamos promociones activas
        sesionPromociones.actualizarPromocionesActivas();
    }
    
    public TipoDocumentoBean getDocumentoActivo() {
        return documentoActivo;
    }
    
    public void setDocumentoActivo(TipoDocumentoBean doc){
        cambiarTipoDocumento(doc);
        this.documentoActivo = doc;
    }

	public void cambiarTipoDocumento(TipoDocumentoBean nuevoTipo) {
		log.debug("cambiarTipoDocumento() - Se va a cambiar el tipo de documento del ticket al tipo de documento (" + nuevoTipo.getCodtipodocumento() + ") " + nuevoTipo.getDestipodocumento());
		
		boolean tieneIdTicket = ticketPrincipal.getIdTicket() != null;
		
		if(tieneIdTicket) {
			TipoDocumentoBean tipoAntiguo = null;
			try {
				tipoAntiguo = documentos.getDocumento(ticketPrincipal.getCabecera().getTipoDocumento());
			}
			catch(Exception e) {
				log.error("cambiarTipoDocumento() - Ha habido un error al buscar el tipo de documento actual: " + e.getMessage(), e);
				log.error("cambiarTipoDocumento() - Para guardar el ticket vacío se guardará el tipo de documento nuevo.");
			}
			
			TipoDocumentoBean tipoTicketOrigen = null;
			if(ticketOrigen != null) {
				try {
					tipoTicketOrigen = documentos.getDocumento(ticketOrigen.getCabecera().getTipoDocumento());
				}
				catch(Exception e) {
					log.error("cambiarTipoDocumento() - Ha habido un error al buscar el tipo de documento del ticket origen: " + e.getMessage(), e);
				}
			}
			
			ticketsService.saveEmptyTicket(ticketPrincipal, tipoAntiguo, tipoTicketOrigen);
		}
		
		ticketPrincipal.getCabecera().setDocumento(nuevoTipo);
		
		if(tieneIdTicket) {
		    try {
		    	log.debug("cambiarTipoDocumento() - Se va a asignar el ID_TICKET ya que el ticket lo tenía asignado antes del cambio de tipo de documento.");
				ticketsService.setContadorIdTicket((Ticket) ticketPrincipal);
				log.debug("cambiarTipoDocumento() - Contador obtenido: " + ticketPrincipal.getIdTicket());
			}
			catch (Exception e) {
				log.error("cambiarTipoDocumento() - Ha habido un error al obtener el contador del ticket.");
			}
		}
	}
    
    public void actualizarCantidadesOrigenADevolver(LineaTicketAbstract linea, BigDecimal cantidad) {
        BigDecimal oldCantidadADevolver = linea.getCantidadADevolver();
        linea.setCantidadADevolver(cantidad.abs());
        if(BigDecimalUtil.isMenorACero(linea.getCantidadDisponibleDevolver())){
        	linea.setCantidadADevolver(oldCantidadADevolver);
        	//Error en la programación, nunca debería ser menor a 0, hay que validar antes
        	throw new RuntimeException();
        }
    }
    
    
    //Método que llama al nuevo en el que se ha añadido el campo de peso, por si hay configurada una balanza
    public synchronized LineaTicket nuevaLineaArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Integer idLineaDocOrigen) throws LineaTicketException {
    	return nuevaLineaArticulo(codArticulo, desglose1, desglose2, cantidad, null, idLineaDocOrigen, false);
    }
    
    public synchronized LineaTicket nuevaLineaArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen, boolean esLineaDevolucionPositiva) throws LineaTicketException {
    	return nuevaLineaArticulo(codArticulo, desglose1, desglose2, cantidad, stage, idLineaDocOrigen, esLineaDevolucionPositiva, true);
    }
    
	public synchronized LineaTicket nuevaLineaArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen, boolean esLineaDevolucionPositiva, boolean applyDUN14Factor) throws LineaTicketException {
		log.debug("nuevaLineaArticulo() - Creando nueva línea de artículo...");
		LineaTicketAbstract linea = null;
		
		boolean isCupon = sesionPromociones.isCouponWithPrefix(codArticulo);
		if(isCupon) {
			try {
				CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO(codArticulo, true);
				if(!sesionPromociones.aplicarCupon(customerCouponDTO, (TicketVentaAbono) ticketPrincipal)) {
					throw new LineaTicketException(I18N.getTexto("No se ha podido aplicar el cupón."));
				}
				ticketPrincipal.getTotales().recalcular();
			}
			catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
				log.warn("nuevaLineaArticulo() - Error en la aplicación del cupón -" + ex.getMessageI18N());
				throw new LineaTicketException(ex.getMessageI18N(), ex);
			}
		}
		else {
			BigDecimal precio = null;
			
			boolean pesarArticulo = stage != null;
			
			String codBarras = null;
			// Comprobamos si es codigo de barras especial o normal y actualizamos codigoArticulo y otras variables
			try {
				CodigoBarrasBean codBarrasEspecial = codBarrasEspecialesServices.esCodigoBarrasEspecial(codArticulo);
	
				if (codBarrasEspecial != null) {
					
					codBarras = codArticulo;
					
					// Ponemos la variable a falsa ya que se cogerá el peso del código de barras
					pesarArticulo = false;
					
					if (codBarrasEspecial.getCodticket() != null) {
						return tratarCodigoBarraEspecialTicket(codBarrasEspecial);
					}
					
					codArticulo = codBarrasEspecial.getCodart();
					String cantCodBar = codBarrasEspecial.getCantidad();
					if (cantCodBar != null) {
						cantidad = FormatUtil.getInstance().desformateaBigDecimal(cantCodBar, 3);
					}else {
						cantidad = BigDecimal.ONE;
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
				linea = lineasTicketServices.createLineaArticulo((TicketVenta) ticketPrincipal, codArticulo, desglose1, desglose2, cantidad, precio, createLinea(), applyDUN14Factor);
				linea.setCantidad(tratarSignoCantidad(linea.getCantidad(), linea.getCabecera().getCodTipoDocumento()));
				if(esLineaDevolucionPositiva) {
					linea.setCantidad(linea.getCantidad().abs());
				}
				
				if(codBarras != null){
					linea.setCodigoBarras(codBarras);
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
				ticketPrincipal.getTotales().recalcular();
			} catch (ArticuloNotFoundException e) {
				linea = null;
				
				try { // Si no se ha encontrado artículo, intentamos aplicar cupón
					CustomerCouponDTO coupon = new CustomerCouponDTO(codArticulo, false);
					
					isCupon = sesionPromociones.aplicarCupon(coupon, (TicketVentaAbono) ticketPrincipal);
					if (!isCupon) { // Si el código no es de un cupón válido,
									// lanzamos excepción de artículo no encontrado
						log.warn("nuevaLineaArticulo() - Artículo no encontrado " + codArticulo);
						throw new LineaTicketException(e.getMessageI18N());
					}
					ticketPrincipal.getTotales().recalcular();

				} // Si tenemos excepción durante la aplicación del cupón, lanzamos
					// excepción indicativa
				catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
					log.warn("nuevaLineaArticulo() - Error en la aplicación del cupón -" + ex.getMessageI18N());
					throw new LineaTicketException(ex.getMessageI18N(), e);
				}
			}
		}
		return (LineaTicket) linea;
	}
    
	protected void addLinea(LineaTicketAbstract linea) throws LineaTicketException{
        linea.setIdLinea(contadorLinea);
        ((TicketVentaAbono)ticketPrincipal).addLinea((LineaTicket)linea);
        contadorLinea++;
    }
	
	protected Integer getIdLineaTicketOrigen(String codArticulo, String desglose1, String desglose2, BigDecimal cantidadAbs) throws LineaTicketException {
		List<LineaTicket> lineasOrigen = ticketOrigen.getLinea(codArticulo);
		Integer idLinea = null;
		int numLineas = 0;
		boolean encontrado = false;
		
		for (LineaTicket lineaOrigen : lineasOrigen) {
			if(lineaOrigen.getCodArticulo().equals(codArticulo) && 
					lineaOrigen.getDesglose1().equals(desglose1) && 
					lineaOrigen.getDesglose2().equals(desglose2)) {
				encontrado = true;
				if(BigDecimalUtil.isMayorOrIgualACero(lineaOrigen.getCantidad().subtract(lineaOrigen.getCantidadDevuelta().add(lineaOrigen.getCantidadADevolver().add(cantidadAbs))))){
					numLineas++;
					if(idLinea == null){
						idLinea = lineaOrigen.getIdLinea();
					}
				}
			}
		}
		if(!encontrado){
			throw new LineaDevolucionNuevoArticuloException(I18N.getTexto("El artículo {0} no se ha encontrado en el documento origen de la devolución.", codArticulo));
		}
		if(idLinea == null){ //Se ha encontrado pero idLinea es null -> cantidad superada
			throw new LineaDevolucionCambioException(I18N.getTexto("La cantidad a devolver del artículo supera a la cantidad vendida."));
		}
		if(numLineas > 1){
			throw new LineaTicketException(I18N.getTexto("El artículo {0} existe en varias líneas del ticket original, deberá indicar la línea de devolución manualmente.", codArticulo));
		}
		return idLinea;
	}
    
    public boolean comprobarImporteMaximoOperacion(Stage stage) {
		BigDecimal importeMaximo = documentoActivo.getImporteMaximoSinImpuestos();
		
		if(importeMaximo != null){
	    	if(BigDecimalUtil.isMayor(ticketPrincipal.getCabecera().getTotales().getBase(), importeMaximo)){
	    		VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Imposible realizar la venta, el total sin impuestos de la venta supera el máximo permitido ({0}) para el tipo de documento: {1}", FormatUtil.getInstance().formateaImporte(importeMaximo), documentoActivo.getCodtipodocumento()), stage);
	    		return false;
	    	}
		}else{
			importeMaximo = documentoActivo.getImporteMaximo();
			if(importeMaximo != null){
				if(BigDecimalUtil.isMayor(ticketPrincipal.getCabecera().getTotales().getTotalAPagar(), importeMaximo)){
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Imposible realizar la venta, el total de la venta supera el máximo permitido ({0}) para el tipo de documento: {1}", FormatUtil.getInstance().formateaImporte(importeMaximo), documentoActivo.getCodtipodocumento()), stage);
					return false;
				}
			}
		}
        
        return true;
    }
    
	public boolean comprobarCierreCajaDiarioObligatorio() {
    	Boolean obligatorio = variablesServices.getVariableAsBoolean(VariablesServices.CAJA_CIERRE_CAJA_DIARIO_OBLIGATORIO, true);
    	if(obligatorio){
	    	Fecha fechaApertura = new Fecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
	    	Fecha fechaActual = new Fecha(new Date());
	    	if(!fechaApertura.equalsFecha(fechaActual)){
	    		return false;
	    	}
    	}
		return true;
	}
    public boolean comprobarConfigContador(){
    	return comprobarConfigContador(null);
    }
    
    public boolean comprobarConfigContador(String tipoDocumento){
    	boolean res = true;
    	String documentoTipo = tipoDocumento!=null?tipoDocumento:ticketPrincipal.getCabecera().getCodTipoDocumento();
		try {
			TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(documentoTipo);
	    	ConfigContadorBean confContador = servicioConfigContadores.consultar(documento.getIdContador());
	    	if(!confContador.isRangosCargados()){
	    		ConfigContadorRangoExample example = new ConfigContadorRangoExample();
				example.or().andIdContadorEqualTo(confContador.getIdContador());
				example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
						+ ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
	    		List<ConfigContadorRangoBean> rangos = servicioConfigContadoresRangos.consultar(example);
	    		
	    		confContador.setRangos(rangos);
	    		confContador.setRangosCargados(true);
	    	}
	    	//Si tiene rangos se aplica la lógica de rangos
	    	if(!confContador.getRangos().isEmpty()){
	    		res = comprobarRangoActivo(documento,confContador);
	    	}
		} catch (Exception e) {
			log.error("comprobarConfigContador() - Ha ocurrido un error al comprobar las vigencias del ticket", e);
			res = false;
		}
    	return res;
    }
    
    protected boolean comprobarRangoActivo(TipoDocumentoBean documento, ConfigContadorBean confContador) throws ConfigContadoresRangosConstraintViolationException, ConfigContadoresRangosException {
    	boolean res = true;
    	Map<String, String> parametrosContador = new HashMap<>();
    	Map<String, String> condicionesVigencias = new HashMap<>();
        parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODEMP,sesion.getAplicacion().getEmpresa().getCodEmpresa());
        parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODALM,sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
        parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODSERIE,sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
        parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODCAJA,sesion.getAplicacion().getCodCaja());
        parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODDOC,documento.getCodtipodocumento());
        parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_PERIODO,((new Fecha()).getAño().toString()));
        
        condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODCAJA,sesion.getAplicacion().getCodCaja());
        condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODALM,sesion.getAplicacion().getCodAlmacen());
        condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODEMP,sesion.getAplicacion().getEmpresa().getCodEmpresa());
        
		try {
			contador = servicioContadores.consultarContadorActivo(confContador, parametrosContador,condicionesVigencias, sesion.getAplicacion().getUidActividad(), false);
		}catch (ConfigContadoresParametrosException | ContadorServiceException e) {
			log.warn("comprobarRangoActivo(): No se pudo obtener un contador disponible");
		}
    	if(contador==null||contador.getError()!=null){
    		res = false;
    	}
		return res;
	}
    
    public void notificarContadores(){
    	if(contador!=null && ticketPrincipal!=null){
    		contador.setValor(ticketPrincipal.getIdTicket()); //Si el valor ha sido editado mientras se salvaba el ticket se actualiza.
    		Long valorUltimoContador = contador.getValor(); 
            Long rangoSiguienteAviso = contador.getRangoSiguienteAviso();
    		if(rangoSiguienteAviso !=null &&( rangoSiguienteAviso<=valorUltimoContador ||
    				(contador.getConfigContadorRango()!= null && valorUltimoContador>=contador.getConfigContadorRango().getRangoFin()))){
    			TipoDocumentoBean documento = null;
    			try {
    				documento = sesion.getAplicacion().getDocumentos().getDocumento(ticketPrincipal.getCabecera().getTipoDocumento());
    			} catch (DocumentoException e) {}
    			String msg = I18N.getTexto("La vigencia para el tipo de documento {0} ha alcanzado el valor {1} sobre su máximo de {2}.", 
    					documento.getDestipodocumento(), valorUltimoContador, contador.getConfigContadorRango().getRangoFin());
    			Notificacion notif = new Notificacion(msg, Tipo.WARN);
    			Notificaciones.get().addNotification(notif);
    		}
        }
    }

	public void negarLineaArticulo(Integer idLinea) throws LineaTicketException  {
        log.debug("negarLineaArticulo() - Cambiando signo cantidad de línea de ticket con idLinea: " + idLinea);
        LineaTicket linea = ((TicketVentaAbono)ticketPrincipal).getLinea(idLinea);
        
        linea.setCantidad(linea.getCantidad().negate());
        
        recalcularConPromociones();
    }
    
    
    public LineaTicket eliminarLineaArticulo(Integer idLinea)  {
        log.debug("eliminarLineaArticulo() - Eliminando línea de ticket con idLinea: " + idLinea);
        LineaTicket linea = ((TicketVentaAbono)ticketPrincipal).getLinea(idLinea);
        ticketPrincipal.getLineas().remove(linea);
        recalcularConPromociones();
        if(ticketOrigen != null && linea.getLineaDocumentoOrigen() != null){
        	LineaTicketAbstract lineaOrigen = ticketOrigen.getLinea(linea.getLineaDocumentoOrigen());
			actualizarCantidadesOrigenADevolver(lineaOrigen, lineaOrigen.getCantidadADevolver().subtract((((LineaTicket) linea).getCantidad().abs())));
        }
        return (LineaTicket) linea;
    }
    
    public void eliminarTicketCompleto() throws TicketsServiceException, PromocionesServiceException, DocumentoException  {
        log.debug("eliminarTicketCompleto() - Eliminando ticket completo...");
        
        finalizarTicket();
        
        boolean esDevolucion = this.esDevolucion;
        TipoDocumentoBean tipoDocumentoActivo = getDocumentoActivo();
        nuevoTicket();
        ticketOrigen = null;
        if(esDevolucion){
        	setEsDevolucion(esDevolucion);
        	setDocumentoActivo(tipoDocumentoActivo);
        }
    }
    
	public PagoTicket nuevaLineaPago(String codigo, BigDecimal importe, boolean modificable, boolean eliminable, Integer paymentId, boolean introducidoPorCajero) {
        log.debug("nuevaLineaPago() - Creando nueva línea de pago...");
        
		MedioPagoBean medioPago = mediosPagosService.getMedioPago(codigo);
		
		if(medioPago == null) {
			throw new IllegalArgumentException("El medio de pago con código " + codigo + " no existe.");
		}
		
		PagoTicket pago = SpringContext.getBean(PagoTicket.class, medioPago);
		pago.setEliminable(eliminable);
		((TicketVenta)ticketPrincipal).addPago(pago);
		
        if(ticketPrincipal.getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO)<0){
            importe = importe.negate();
        }
        pago.setImporte(importe);
        
        if(paymentId != null) {
        	pago.setPaymentId(paymentId);
        }
        
        pago.setIntroducidoPorCajero(introducidoPorCajero);
        
        ticketPrincipal.getTotales().recalcular();
        
        return pago;
    }

    public void eliminarPagos(){
        log.debug("elminarPagos() - Eliminando pagos del ticket...");
        List<PagoTicket> pagos = ((TicketVenta)ticketPrincipal).getPagos();
        ListIterator<PagoTicket> listIterator = pagos.listIterator();
    	while(listIterator.hasNext()){
			PagoTicket pagoTicket = listIterator.next(); 
            if (pagoTicket.isEliminable()) {
                listIterator.remove();
            }
        }
        ticketPrincipal.getTotales().recalcular();
    }

	public boolean deletePayment(Integer paymentId) {
		log.debug("deletePayment() - Eliminando pago con ID: " + paymentId);
		
		List<IPagoTicket> payments = ticketPrincipal.getPagos();
		Iterator<IPagoTicket> it = payments.iterator();
		while(it.hasNext()) {
			IPagoTicket payment = it.next();
			log.debug("deletePayment() - Comprobando pago. " + payment);
			
			if(payment.getPaymentId() != null && payment.getPaymentId().equals(paymentId)) {
				log.debug("deletePayment() - Borrando pago.");
				it.remove();
				ticketPrincipal.getTotales().recalcular();
				return true;
			}
			
		}
		
		log.error("deletePayment() - No se ha encontrado el pago, no se puede eliminar.");
		return false;
	}
    
    public void salvarTicket(Stage stage, SalvarTicketCallback callback){
    	new SalvarTicketTask(stage, callback).start();
    }
    
    protected class SalvarTicketTask extends BackgroundTask<Void> {
    	protected Stage stage;
		protected SalvarTicketCallback callback;

		public SalvarTicketTask(Stage stage, SalvarTicketCallback callback){
			this.stage = stage;
			this.callback = callback;
		}
		
		@Override
		protected Void call() throws Exception {
            sesionPromociones.aplicarPromocionesFinales((TicketVentaAbono) ticketPrincipal);
            
            sesionPromociones.generarCuponesDtoFuturo(ticketPrincipal);
            
            ticketPrincipal.getTotales().recalcular();
            
            return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			//Generamos los DatosPeticionTarjeta
			List<DatosPeticionPagoTarjeta> datosPeticionTarjeta = new LinkedList<>();
			//Añadimos todos los pagos
			final List<IPagoTicket> pagosTarjeta = new LinkedList<>(ticketPrincipal.getPagos());
			for (ListIterator<IPagoTicket> iterator = pagosTarjeta.listIterator(); iterator.hasNext();) {
				IPagoTicket pagoTicket = iterator.next();
				//Quitamos los pagos que no acepta el dispositivo
				if(!Dispositivos.getInstance().getTarjeta().isCodMedPagoAceptado(pagoTicket.getCodMedioPago())){
					iterator.remove();
					continue;
				}
				DatosPeticionPagoTarjeta datoPeticion = new DatosPeticionPagoTarjeta(ticketPrincipal.getCabecera().getCodTicket(), ticketPrincipal.getCabecera().getIdTicket(), pagoTicket.getImporte());
				datosPeticionTarjeta.add(datoPeticion);
				if(ticketOrigen != null){ 
					//Asociamos los datos respuesta de los pagos origen
					List<IPagoTicket> pagosTarjetaOrig = new LinkedList<>(ticketOrigen.getPagos());
					for (IPagoTicket pagoTicketOrigen : pagosTarjetaOrig) {
						if(pagoTicket.getCodMedioPago().equals(pagoTicketOrigen.getCodMedioPago())){
							DatosRespuestaPagoTarjeta datosRespuestaPagoTarjeta = pagoTicketOrigen.getDatosRespuestaPagoTarjeta();
			            	if(datosRespuestaPagoTarjeta != null){
			            		datoPeticion.setCodAutorizacion(datosRespuestaPagoTarjeta.getCodAutorizacion());
			            		datoPeticion.setNumOpBanco(datosRespuestaPagoTarjeta.getNumOperacionBanco());
			            		datoPeticion.setNumOperacion(datosRespuestaPagoTarjeta.getNumOperacion());
			            		
								datoPeticion.setIdDocumentoOrigen(datosRespuestaPagoTarjeta.getNumTransaccion());
								datoPeticion.setFechaDocumentoOrigen(datosRespuestaPagoTarjeta.getFechaTransaccion());
			            	}
						}
					}
				}
			}
				
            TarjetaCallback<List<DatosRespuestaPagoTarjeta>> dispositivoCallback = new TarjetaCallback<List<DatosRespuestaPagoTarjeta>>() {
				
				@Override
				public void onSuccess(List<DatosRespuestaPagoTarjeta> datosRespuesta) {
					//Asignamos los datos respuesta a los pagos
					for (DatosRespuestaPagoTarjeta datoRespuesta : datosRespuesta) {
						pagosTarjeta.get(datosRespuesta.indexOf(datoRespuesta)).setDatosRespuestaPagoTarjeta(datoRespuesta);
					}
					crearClaseRegistrarTicketTask(stage, callback, datosRespuesta).start();
				}

				@Override
				public void onFailure(List<DatosRespuestaPagoTarjeta> datosRespuesta, Throwable caught) {
		            anularPagos(datosRespuesta, stage);
		            anularPromocionesFinales(stage);
		            
		            salvarTicketVacio();
		            
					callback.onFailure((Exception) caught);
				}
			};
			
	        if(ticketPrincipal.getCabecera().esVenta()){
	        	Dispositivos.getInstance().getTarjeta().solicitarPagosTarjeta(datosPeticionTarjeta, stage, dispositivoCallback);
	        }else{
	        	Dispositivos.getInstance().getTarjeta().solicitarDevolucion(datosPeticionTarjeta, stage, dispositivoCallback);
	        }
			
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable ex = getException();
			if(ticketPrincipal.getIdTicket() != null){
				salvarTicketVacio();
			}
            callback.onFailure((Exception) ex);
		}
    	
    }
	
	protected BackgroundTask<Void> crearClaseRegistrarTicketTask(Stage stage, SalvarTicketCallback callback, List<DatosRespuestaPagoTarjeta> datosRespuesta) {
		return new RegistrarTicketTask(stage, callback, datosRespuesta);
	}
    
    class SalvarTicketApartadoTask extends BackgroundTask<Void> {
    	protected ApartadosCabeceraBean cabeceraApartado;
    	protected Stage stage;
		protected SalvarTicketCallback callback;

		public SalvarTicketApartadoTask(ApartadosCabeceraBean cabeceraApartado, Stage stage, SalvarTicketCallback callback){
			this.cabeceraApartado = cabeceraApartado;
			this.stage = stage;
			this.callback = callback;
		}
		
		@Override
		protected Void call() throws Exception {
			//Nada que hacer
            return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
	    	final List<PagoTicket> pagosTarjeta = new ArrayList<>();
	        
	    	List<IPagoTicket> pagos = ticketPrincipal.getPagos();
	        for(IPagoTicket pago : pagos){
	            if(Dispositivos.getInstance().getTarjeta().isCodMedPagoAceptado(pago.getCodMedioPago())){
	                pagosTarjeta.add((PagoTicket)pago);
	            }
	        }
	    	
	        List<DatosPeticionPagoTarjeta> datosPeticionTarjeta = new LinkedList<>();
	        //Generamos los DatosPeticionTarjeta
	        for (PagoTicket pago : pagosTarjeta) {
	        	String id = "Apartado nº " + cabeceraApartado.getIdApartado(); //Igual que en ApartadosService.registrarMovimientoAbonoApartado()
	        	DatosPeticionPagoTarjeta datoPeticion = new DatosPeticionPagoTarjeta(id, cabeceraApartado.getIdApartado(), pago.getImporte());
	            datosPeticionTarjeta.add(datoPeticion);
			}
	        
	        TarjetaCallback<List<DatosRespuestaPagoTarjeta>> tarjetaCallback = new TarjetaCallback<List<DatosRespuestaPagoTarjeta>>() {
	        	
	        	@Override
	        	public void onSuccess(List<DatosRespuestaPagoTarjeta> datosRespuesta) {
	        		BigDecimal saldoAntes = cabeceraApartado.getSaldoCliente();
	        		try {
	        			//Asignamos los datos respuesta a los pagos
						for (DatosRespuestaPagoTarjeta datoRespuesta : datosRespuesta) {
							pagosTarjeta.get(datosRespuesta.indexOf(datoRespuesta)).setDatosRespuestaPagoTarjeta(datoRespuesta);
						}
		        		if(esOperacionTarjetaRegalo){
		        			procesarTarjetaRegalo(stage);
		        		}
		        		((Ticket) ticketPrincipal).getCabecera().setTienda(sesion.getAplicacion().getTienda());
		        		((Ticket) ticketPrincipal).getCabecera().setEmpresa(sesion.getAplicacion().getEmpresa());
		        		((Ticket) ticketPrincipal).setFecha(new Date());
		        		((Ticket) ticketPrincipal).setIdTicket(cabeceraApartado.getIdApartado());
		        		
		        		cabeceraApartado.setSaldoCliente(cabeceraApartado.getSaldoCliente().add(ticketPrincipal.getTotales().getTotalAPagar()));
	        		
						apartadosService.registrarMovimientosPagosApartado((Ticket) ticketPrincipal, ((TicketVenta)ticketPrincipal).getPagos(), cabeceraApartado);
						//apartadosService.actualizarCabeceraApartado(cabeceraApartado);
						
						callback.onSucceeded();
					} catch (Exception e) {
						cabeceraApartado.setSaldoCliente(saldoAntes);
			            anularPagos(datosRespuesta, stage);
			            anularPromocionesFinales(stage);
			            
						callback.onFailure(e);
					}
	        	}
	        	
	        	@Override
	        	public void onFailure(List<DatosRespuestaPagoTarjeta> datosRespuesta, Throwable caught) {
		            anularPagos(datosRespuesta, stage);
		            anularPromocionesFinales(stage);
		            
					callback.onFailure((Exception) caught);
	        	}
	        };
	        
	        boolean bVenta = BigDecimalUtil.isMayorOrIgualACero(ticketPrincipal.getTotales().getTotal());
	        if(bVenta){
	        	Dispositivos.getInstance().getTarjeta().solicitarPagosTarjeta(datosPeticionTarjeta, stage, tarjetaCallback);
	        }else{
	        	Dispositivos.getInstance().getTarjeta().solicitarDevolucion(datosPeticionTarjeta, stage, tarjetaCallback);
	        }
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable ex = getException();
            callback.onFailure((Exception) ex);
		}
    	
    }
    
    protected class RegistrarTicketTask extends BackgroundTask<Void>{

    	protected Stage stage;
    	protected SalvarTicketCallback callback;
    	protected List<DatosRespuestaPagoTarjeta> pagosAutorizados;

		public RegistrarTicketTask(Stage stage, SalvarTicketCallback callback, List<DatosRespuestaPagoTarjeta> pagosAutorizados) {
			this.stage = stage;
			this.callback = callback;
			this.pagosAutorizados = pagosAutorizados;
		}

		@Override
		protected Void call() throws Exception {
			if(esOperacionTarjetaRegalo){
				procesarTarjetaRegalo(stage);
	        }
			
	        redondearImportesTicket();
	        
	        boolean processTicket = esDevolucion || esFacturacionVentaCredito || !ticketPrincipal.getCuponesAplicados().isEmpty();
	        
			ticketsService.registrarTicket((Ticket)ticketPrincipal, documentoActivo, processTicket);

            confirmarPagosTarjeta(pagosAutorizados, stage);
			
			return null;
		}

		@Override
		protected void failed() {
			super.failed();
			Exception ex = (Exception) getException();
			
			log.error("salvarTicket() Error salvando ticket : " +ex.getMessage(), ex);
			
            anularPagos(pagosAutorizados, stage);
            anularPromocionesFinales(stage);
            
            if(ticketPrincipal.getIdTicket() != null){
				salvarTicketVacio();
			}
            
			callback.onFailure(ex);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			callback.onSucceeded();
		}
		
    }
    
    public interface SalvarTicketCallback{
    	void onSucceeded();
    	void onFailure(Exception e);
    }
    
    protected void redondearImportesTicket() {
    	for (LineaTicket linea : (List<LineaTicket>)ticketPrincipal.getLineas()) {
			linea.setImporteConDto(BigDecimalUtil.redondear(linea.getImporteConDto()));
			linea.setImporteTotalConDto(BigDecimalUtil.redondear(linea.getImporteTotalConDto()));
			linea.setPrecioTotalSinDto(BigDecimalUtil.redondear(linea.getPrecioTotalSinDto()));
			linea.setImporteTotalPromociones(BigDecimalUtil.redondear(linea.getImporteTotalPromociones()));
			linea.setImporteTotalPromocionesMenosIngreso(BigDecimalUtil.redondear(linea.getImporteTotalPromocionesMenosIngreso()));
			for (PromocionLineaTicket promocionLinea : linea.getPromociones()) {
				promocionLinea.setImporteTotalDtoMenosMargen(BigDecimalUtil.redondear(promocionLinea.getImporteTotalDtoMenosMargen()));
				promocionLinea.setImporteTotalDtoMenosIngreso(BigDecimalUtil.redondear(promocionLinea.getImporteTotalDtoMenosIngreso()));
			}
		}
    	for (PromocionTicket promocion : (List<PromocionTicket>)ticketPrincipal.getPromociones()) {
			promocion.setImporteTotalAhorro(BigDecimalUtil.redondear(promocion.getImporteTotalAhorro()));
		}
	}

    public void anularPromocionesFinales(Stage stage) {
    	if(ticketPrincipal.getPromociones() != null) {
    		Iterator<PromocionTicket> it = ticketPrincipal.getPromociones().iterator();
    		while(it.hasNext()) {
    			PromocionTicket promocionTicket = it.next();
    			Promocion promocion = sesionPromociones.getPromocionActiva(promocionTicket.getIdPromocion());
    			if(promocion != null && promocion.isAplicacionFinal()) {
    				ticketPrincipal.getTotales().addPuntos(-promocionTicket.getPuntos());
    				it.remove();
    			}
    		}
    		ticketPrincipal.getTotales().recalcular();
    	}
		
    }

	protected void anularPagos(List<DatosRespuestaPagoTarjeta> datosRespuesta, final Stage stage){
		List<DatosPeticionPagoTarjeta> datosPeticionTarjeta = new LinkedList<>();
		for (DatosRespuestaPagoTarjeta datoRespuesta : datosRespuesta) {
			if(datoRespuesta != null) {
				DatosPeticionPagoTarjeta datosPeticionOrig = datoRespuesta.getDatosPeticion();
				DatosPeticionPagoTarjeta datosPeticion = new DatosPeticionPagoTarjeta(datosPeticionOrig.getIdTransaccion(), datosPeticionOrig.getIdDocumento(), datosPeticionOrig.getImporte());
				datosPeticion.setCodAutorizacion(datoRespuesta.getCodAutorizacion());
				datosPeticion.setNumOpBanco(datoRespuesta.getNumOperacionBanco());
				datosPeticion.setNumOperacion(datoRespuesta.getNumOperacion());
				datosPeticionTarjeta.add(datosPeticion);
			}
		}
		
		log.info("anularPagos() - Anulando pagos: " + datosPeticionTarjeta);
        TarjetaCallback<List<DatosRespuestaPagoTarjeta>> dispositivoCallback = new TarjetaCallback<List<DatosRespuestaPagoTarjeta>>() {
			public void onSuccess(List<DatosRespuestaPagoTarjeta> result) {
				log.info("anularPagos() - Pagos anulados");
			}
			@Override
			public void onFailure(List<DatosRespuestaPagoTarjeta> result, Throwable caught) {
				log.fatal("anularPagos() - Ha ocurrido un error al anular pagos", caught);
				if (caught instanceof TarjetaException) {
					VentanaDialogoComponent.crearVentanaError(stage, caught.getMessage(), caught);
				} else {
					VentanaDialogoComponent.crearVentanaError(stage, I18N.getTexto("Ha ocurrido un error al anular los pagos"), caught);
				}
			}
		};
        
        if(ticketPrincipal.getCabecera().esVenta()){
        	Dispositivos.getInstance().getTarjeta().solicitarAnulacionPago(datosPeticionTarjeta, stage, dispositivoCallback);
        }else{
        	Dispositivos.getInstance().getTarjeta().solicitarAnulacionDevolucion(datosPeticionTarjeta, stage, dispositivoCallback);
        }
    }

    protected void confirmarPagosTarjeta(List<DatosRespuestaPagoTarjeta> datosRespuesta, final Stage stage){
        List<DatosPeticionPagoTarjeta> datosPeticionTarjeta = new LinkedList<>();
        for (DatosRespuestaPagoTarjeta datoRespuesta : datosRespuesta) {
            DatosPeticionPagoTarjeta datosPeticionOrig = datoRespuesta.getDatosPeticion();
            DatosPeticionPagoTarjeta datosPeticion = new DatosPeticionPagoTarjeta(datosPeticionOrig.getIdTransaccion(), datosPeticionOrig.getIdDocumento(), datosPeticionOrig.getImporte());
            datosPeticion.setCodAutorizacion(datoRespuesta.getCodAutorizacion());
            datosPeticion.setNumOpBanco(datoRespuesta.getNumOperacionBanco());
            datosPeticion.setNumOperacion(datoRespuesta.getNumOperacion());
            datosPeticionTarjeta.add(datosPeticion);
        }

        log.info("confirmarPagosTarjeta() - Confirmando pagos con tarjeta: " + datosPeticionTarjeta);
        TarjetaCallback<List<DatosRespuestaPagoTarjeta>> dispositivoCallback = new TarjetaCallback<List<DatosRespuestaPagoTarjeta>>() {
            public void onSuccess(List<DatosRespuestaPagoTarjeta> result) {
                log.info("confirmarPagosTarjeta() - Pagos confirmados");
            }
            @Override
            public void onFailure(List<DatosRespuestaPagoTarjeta> result, Throwable caught) {
                log.fatal("confirmarPagosTarjeta() - Ha ocurrido un error al confirmar los pagos con tarjeta de crédito", caught);
                VentanaDialogoComponent.crearVentanaError(stage, I18N.getTexto("Ha ocurrido un error al confirmar los pagos con tarjeta de crédito"), caught);
            }
        };

        if(ticketPrincipal.getCabecera().esVenta()){
            Dispositivos.getInstance().getTarjeta().solicitarConfirmacionPagos(datosPeticionTarjeta, stage, dispositivoCallback);
        }else{
            Dispositivos.getInstance().getTarjeta().solicitarConfirmacionDevolucion(datosPeticionTarjeta, stage, dispositivoCallback);
        }
    }
    
	public void salvarTicketVacio() {
		TipoDocumentoBean tipoTicketOrigen = null;
		if(ticketOrigen != null) {
			try {
				tipoTicketOrigen = documentos.getDocumento(ticketOrigen.getCabecera().getTipoDocumento());
			}
			catch(Exception e) {
				log.error("salvarTicketVacio() - Ha habido un error al buscar el tipo de documento del ticket origen: " + e.getMessage(), e);
			}
		}
		TipoDocumentoBean tipoDocumento = documentoActivo;
		try {
			tipoDocumento = sesion.getAplicacion().getDocumentos().getDocumento(ticketPrincipal.getCabecera().getTipoDocumento());
		}
		catch (DocumentoException e) {
			log.error("salvarTicketVacio() - Ha habido un error cuando se buscaba el documento activo: " + e.getMessage(), e);
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
		
		ticketsService.saveEmptyTicket(ticketPrincipal, tipoDocumento, tipoTicketOrigen);
	}
	
    public void aparcarTicket() throws TicketsServiceException {
        log.debug("aparcarTicket() - Aparcando ticket...");
        ticketPrincipal.getTotales().recalcular();
        ticketsAparcadosService.aparcarTicket((TicketVentaAbono)ticketPrincipal);
        finalizarTicket();
    }
    
    public void finalizarTicket() {
        log.debug("finalizarTicket() - Finalizando ticket...");
        
        ticketPrincipal = null;
        contador = null;
    }
    
    public boolean isTicketAbierto(){
        return ticketPrincipal != null;
    }
    
    public String getTarifaDefault(){
        if (((TicketVenta)ticketPrincipal).getCliente().isTarifaAsignada()){
            return ((TicketVenta)ticketPrincipal).getCliente().getCodtar();
        }
        if (sesion.getAplicacion().getTienda().getCliente().isTarifaAsignada()){
            return sesion.getAplicacion().getTienda().getCliente().getCodtar();
        }
        return ArticulosTarifaService.COD_TARIFA_GENERAL;
    }
    
    public void recuperarTicket(Stage stage, TicketAparcadoBean ticketAparcado) throws TicketsServiceException, PromocionesServiceException, DocumentoException, LineaTicketException {
        log.debug("recuperarTicket() - Recuperando ticket...");

        nuevoTicket();
        // Realizamos el unmarshall
        log.debug("Ticket recuperado:\n"+new String(ticketAparcado.getTicket()));
        TicketVenta ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(ticketAparcado.getTicket(), getTicketClasses(documentoActivo).toArray(new Class[]{}));

        ticketPrincipal.getCabecera().setIdTicket(ticketRecuperado.getIdTicket());
        ticketPrincipal.getCabecera().setUidTicket(ticketRecuperado.getUidTicket());
        ticketPrincipal.getCabecera().setCodTicket(ticketRecuperado.getCabecera().getCodTicket());
        ticketPrincipal.getCabecera().setSerieTicket(ticketRecuperado.getCabecera().getSerieTicket());
        
        if(ticketAparcado.getUsuario() == null || !ticketAparcado.getUsuario().equals("FASTPOS")){
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
			if(StringUtils.isBlank(codigo)) {
				codigo = lineaRecuperada.getCodArticulo();
			}
			else {
				desglose1 = null;
				desglose2 = null;
			}
			LineaTicket nuevaLineaArticulo = nuevaLineaArticulo(codigo, desglose1, desglose2, lineaRecuperada.getCantidad(), null, null, false, false);
			
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
			
			String sellerName = lineaRecuperada.getVendedor().getUsuario();
			try {
				UsuarioBean seller = usuariosService.consultarUsuario(sellerName);
				nuevaLineaArticulo.setVendedor(seller);
			} catch (UsuarioNotFoundException e) {
				// active user
				log.warn("recuperarTicket() - No se ha encontrado el usuario: " + sellerName);
			} catch (UsuariosServiceException e) {
				// active user
				log.warn("recuperarTicket() - Se ha producido un error al consultar el: " + sellerName);
			}
			recuperarDatosPersonalizadosLinea(lineaRecuperada, nuevaLineaArticulo);
		}
        
        FidelizacionBean datosFidelizado = ticketRecuperado.getCabecera().getDatosFidelizado();
		if(datosFidelizado!=null){
        	try {
				FidelizacionBean tarjetaFidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(stage, datosFidelizado.getNumTarjetaFidelizado(), ticketPrincipal.getCabecera().getUidActividad());
				ticketPrincipal.getCabecera().setDatosFidelizado(tarjetaFidelizado);
			} catch (ConsultaTarjetaFidelizadoException e) {
				log.debug("recuperarTicket() - Error al consultar fidelizado", e);
				FidelizacionBean fidelizacionBean = new FidelizacionBean();
				fidelizacionBean.setNumTarjetaFidelizado(datosFidelizado.getNumTarjetaFidelizado());
				ticketPrincipal.getCabecera().setDatosFidelizado(fidelizacionBean);
			}
        }
		
		for(PagoTicket pago : (List<PagoTicket>) ticketRecuperado.getPagos()) {
			pago.setMedioPago(mediosPagosService.getMedioPago(pago.getCodMedioPago()));
			ticketPrincipal.getPagos().add(pago);
		}
        
        recalcularConPromociones();
        
        // Establecemos el contador
        contadorLinea = ticketPrincipal.getLineas().size()+1;
        //Eliminamos el ticket recuperado de la lista de tickets aparcados.
        ticketsAparcadosService.eliminarTicket(ticketAparcado.getUidTicket());
    }
    
    protected void recuperarDatosPersonalizadosLinea(LineaTicket lineaRecuperada, LineaTicket nuevaLineaArticulo) {
	}

	public List<TicketAparcadoBean> recuperarTicketsAparcados(Long idTipoDoc) throws TicketsServiceException{
        
        return ticketsAparcadosService.consultarTickets(null, null, null, idTipoDoc);
    }
    
    /**
     * Función que nos dice si un ticket está vacío
     * @return
     */
    public boolean isTicketVacio() {
        return ((ticketPrincipal == null || ticketPrincipal.getLineas() == null || ticketPrincipal.getLineas().isEmpty()));
    }
    
    public void cambiarMedioPagoVuelta(MedioPagoBean medioPago) {
        log.debug("cambiarMedioPagoVuelta() - Cambiando medio pago de vuelta por: " + medioPago);
        ticketPrincipal.getTotales().getCambio().setMedioPago(medioPago);
    }
    
    public int countTicketsAparcados(){
        return ticketsAparcadosService.countTicketsAparcados(ticketPrincipal.getCabecera().getTipoDocumento());
    }
    
    public boolean recuperarTicketDevolucion(String codigo, String codAlmacen, String codCaja, Long idTipoDoc) throws TicketsServiceException {
    	return recuperarTicketDevolucion(codigo, codAlmacen, codCaja, idTipoDoc, true);
    }
    
    public boolean recuperarTicketDevolucion(String codigo, String codAlmacen, String codCaja, Long idTipoDoc, boolean controlarPlazoMaximoDevolucion) throws TicketsServiceException {
    	try{
	    	log.debug("recuperarTicketDevolucion() - Recuperando ticket...");
	    	byte[] xmlTicketOrigen = null;
	    	ResponseGetTicketDev datosDevolucion = null;
	    	
	    	HashMap<String, Object> decodeLocator = null;
			try {
				decodeLocator = locatorManager.decode(codigo);
			}
			catch (LocatorParseException e) {
				log.error("recuperarTicketDevolucion() - No se ha podido descodificar el localizador: " + e.getMessage());
			}
			
	    	//Si es localizador
			if(decodeLocator != null){
				//Obtenemos por localizador desde central
				try {
					xmlTicketOrigen = obtenerTicketDevolucionCentralLocalizador(codigo, false, idTipoDoc);
				} catch (LineaTicketException e) {
					log.warn("recuperarTicketDevolucion() - Error al obtener ticket devolución desde central - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				}
				
				if(xmlTicketOrigen != null){
					//Si no null, buscamos datos devolucion
					tratarTicketRecuperado(xmlTicketOrigen);

					datosDevolucion = obtenerDatosDevolucion(ticketOrigen.getUidTicket());
				}else{
					//Si null, obtenemos por localizador desde local
					List<TicketBean> tickets  = ticketsService.consultarTicketLocalizador(codigo, Arrays.asList(idTipoDoc));
	        		if(!tickets.isEmpty()){
	        			xmlTicketOrigen = tickets.get(0).getTicket();
	        			tratarTicketRecuperado(xmlTicketOrigen);
	        		}else{
	        			throw new TicketsServiceException("No se ha encontrado ticket con el localizador: " + codigo);
	        		}
				}
			}
	    	
			//Si no tenemos ticket, consultamos como id de documento en lugar de como localizador 
			if(xmlTicketOrigen == null){
				//por codigo desde central
				try{
					xmlTicketOrigen = obtenerTicketDevolucionCentral(codCaja, codAlmacen, codigo, idTipoDoc);
				}catch(Exception e){
					log.warn("recuperarTicketDevolucion() - Error al obtener ticket devolución desde central - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				}
				
				if(xmlTicketOrigen != null){
					//Si no null, buscamos datos devolucion
					tratarTicketRecuperado(xmlTicketOrigen);
					datosDevolucion = obtenerDatosDevolucion(ticketOrigen.getUidTicket());
				}else{
					//Si null, obtenemos por codigo desde local
					TicketBean ticketA  = ticketsService.consultarTicketAbono(codAlmacen, codCaja, codigo, idTipoDoc);
				    if(ticketA!=null){
				    	xmlTicketOrigen = ticketA.getTicket();
				    	tratarTicketRecuperado(xmlTicketOrigen);
				    }else{
				    	throw new TicketsServiceException("No se ha encontrado ticket con codigo: " + codigo);
				    }
				}
			}
			
			asignarLineasDevueltas(datosDevolucion);
			
	        descontarLineasNegativasTicketOrigen();
	        
    	} catch (TicketsServiceException e) {
    		log.error("recuperarTicketDevolucion() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			return false;
		}
    	
    	if(controlarPlazoMaximoDevolucion) {
    		controlarPlazoMaximoDevolucion();
    	}
    	
    	return true;
    }

	protected void controlarPlazoMaximoDevolucion() throws TicketsServiceException {
		TipoDocumentoBean tipoDocumento;
        try {
	        tipoDocumento = documentos.getDocumento(ticketOrigen.getCabecera().getTipoDocumento());
        }
        catch (DocumentoException e) {
        	throw new TicketsServiceException(I18N.getTexto("No se ha podido recuperar el tipo de documento origen."));
        }
        
		Integer diasMaximoDevolucion = tipoDocumento.getDiasMaximoDevolucion();
		if(diasMaximoDevolucion != null) {
			Date fechaOrigen = ticketOrigen.getFecha();
			
			long diferencia = new Date().getTime() - fechaOrigen.getTime();
			long antiguedad = TimeUnit.DAYS.convert(diferencia, TimeUnit.MILLISECONDS);
			
			if(antiguedad > diasMaximoDevolucion) {
				throw new TicketsServiceException(I18N.getTexto("La devolución no se puede hacer porque superar el plazo máximo de devolución."));
			}
		}
    }

	protected ResponseGetTicketDev obtenerDatosDevolucion(String uidTicket) {
		ConsultarTicketRequestRest request = new ConsultarTicketRequestRest(sesion.getAplicacion().getUidActividad(), 
				variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY), uidTicket);
		try {
			return TicketsRest.getTicketDevolucion(request);
		} catch (Exception e) {
			log.warn("recuperarTicketDevolucion() - Error al obtener datos de devolución - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			return null;
		}
	}
    
    protected byte[] obtenerTicketDevolucionCentral( String codCaja, String codTienda, String codOperacion, Long idTipoDocumento) throws RestHttpException, RestException{
        ConsultarTicketRequestRest request = new ConsultarTicketRequestRest(sesion.getAplicacion().getUidActividad(), variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY), codTienda, codOperacion, codCaja, idTipoDocumento);
        
        byte[] ticketDevolucion;
		try {
			ticketDevolucion = TicketsRest.getTicket(request).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
        
        return ticketDevolucion;
    }
    
    protected byte[] obtenerTicketDevolucionCentralLocalizador(String localizador, boolean throwRestExceptions, Long idTipoDoc) throws LineaTicketException{
    	log.trace("consultarTicketLocalizador() - localizador: "+localizador);
    	
    	byte[] ticketRecuperado = null;
    	
    	String uidActividad = sesion.getAplicacion().getUidActividad();
		String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		TicketLocalizadorRequestRest request = new TicketLocalizadorRequestRest(uidActividad, apiKey, localizador, idTipoDoc);
    	try {
    		ticketRecuperado = TicketsRest.recuperarTicketLocalizador(request).getBytes("UTF-8");
    		
    	}catch (RestException | RestHttpException e){
    		if(throwRestExceptions){
    			if(e instanceof ValidationRequestRestException ||
    					e instanceof ValidationDataRestException ||
    					e instanceof HttpServiceRestException){
    				throw new LineaTicketException(e.getMessage(), e);
    			}else if(e instanceof RestHttpException){
    				throw new LineaTicketException(I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición"), e);
    			}else if(e instanceof RestConnectException){
    				throw new LineaTicketException(I18N.getTexto("No se ha podido conectar con el servidor"), e);
    			}else if(e instanceof RestTimeoutException){
    				throw new LineaTicketException(I18N.getTexto("El servidor ha tardado demasiado tiempo en responder"), e);
    			}else if(e instanceof RestException){
    				throw new LineaTicketException(I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición"), e);
    			}else{
    				throw new LineaTicketException(I18N.getTexto("Lo sentimos, ha ocurrido un error."), e);
    			}
    		}
    	} catch (UnsupportedEncodingException e) {
    		throw new RuntimeException(e);
    	}
    	return ticketRecuperado;
    }
    
    protected byte[] obtenerTicketVentaCreditoCentralLocalizador(String localizador, Long idTipoDoc, boolean throwRestExceptions) throws LineaTicketException{
    	log.trace("obtenerTicketVentaCreditoCentralLocalizador() - localizador: "+localizador);
    	
    	byte[] ticketRecuperado = null;
    	
    	String uidActividad = sesion.getAplicacion().getUidActividad();
		String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		TicketLocalizadorRequestRest request = new TicketLocalizadorRequestRest(uidActividad, apiKey, localizador, idTipoDoc);
    	request.setEjecucionOperacion("FACTURAR");
    	try {
    		ticketRecuperado = TicketsRest.recuperarTicketLocalizadorOperacionPermitida(request).getBytes(StandardCharsets.UTF_8);
    	}catch (RestException | RestHttpException e){
    		if(throwRestExceptions){
    			if(e instanceof ValidationRequestRestException ||
    					e instanceof ValidationDataRestException ||
    					e instanceof HttpServiceRestException){
    				throw new LineaTicketException(e.getMessage(), e);
    			}else if(e instanceof RestHttpException){
    				throw new LineaTicketException(I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición"), e);
    			}else if(e instanceof RestConnectException){
    				throw new LineaTicketException(I18N.getTexto("No se ha podido conectar con el servidor"), e);
    			}else if(e instanceof RestTimeoutException){
    				throw new LineaTicketException(I18N.getTexto("El servidor ha tardado demasiado tiempo en responder"), e);
    			}else if(e instanceof RestException){
    				throw new LineaTicketException(I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición"), e);
    			}else{
    				throw new LineaTicketException(I18N.getTexto("Lo sentimos, ha ocurrido un error."), e);
    			}
    		}
    	}
    	return ticketRecuperado;
    }
    
    protected byte[] obtenerTicketVentaCreditoCentralCodServicio(String codServicio) throws RestException, RestHttpException{
        log.trace("obtenerTicketVentaCreditoCentralCodServicio() - codServicio: "+codServicio);
        
        byte[] ticketRecuperado = null;
        
        FacturarServicioRequestRest facturarServicioRequestRest = new FacturarServicioRequestRest(variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY), sesion.getAplicacion().getUidActividad(), codServicio, sesion.getSesionUsuario().getUsuario().getIdUsuario());
        
        ticketRecuperado = ServiciosRest.consultaServicioFacturar(facturarServicioRequestRest).getBytes(StandardCharsets.UTF_8);
        return ticketRecuperado;
    }
    
    protected byte[] obtenerTicketVentaCreditoCentralCodPedido(String codPedido) throws RestException, RestHttpException{
        log.trace("obtenerTicketVentaCreditoCentralCodPedido() - codPedido: "+codPedido);
        
        byte[] ticketRecuperado = null;
        
        ConsultarTicketCodPedidoRequestRest request = new ConsultarTicketCodPedidoRequestRest(sesion.getAplicacion().getUidActividad(), variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY), codPedido);
        
        
        ticketRecuperado = TicketsRest.getTicketCodPedido(request).getBytes(StandardCharsets.UTF_8);
        return ticketRecuperado;
    }
    
    public boolean obtenerDocumentoVentaCredito(Stage stage, String codCaja, String codTienda, String codOperacion, String codTipoDocumento) throws RestException, RestHttpException, LineaTicketException, DocumentoException, ClientesServiceException, ClienteConstraintViolationException{
        boolean result = false;
        
        ConsultarTicketRequestRest request = new ConsultarTicketRequestRest(sesion.getAplicacion().getUidActividad(), variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY), codTienda, codOperacion, codCaja,
                sesion.getAplicacion().getDocumentos().getDocumento(codTipoDocumento).getIdTipoDocumento());
        request.setEjecucionOperacion("FACTURAR");
        byte[] docVentaCredito = TicketsRest.getTicketOperacionPermitida(request).getBytes(StandardCharsets.UTF_8);
        
        result = tratarDocumentoVentaCredito(stage, docVentaCredito);
        if(result){
            ticketPrincipal.getTotales().recalcular();
            esFacturacionVentaCredito = true;
        }
        
        return result;
    }
    
    public boolean obtenerDocumentoVentaCreditoLocalizador(Stage stage, String localizador, Long idTipoDoc) throws LineaTicketException, ClientesServiceException, RestException, RestHttpException, ClienteConstraintViolationException, DocumentoException{
    	boolean result = false;
    	
    	byte[] docVentaCredito = obtenerTicketVentaCreditoCentralLocalizador(localizador, idTipoDoc, true);
    	
    	result = tratarDocumentoVentaCredito(stage, docVentaCredito);
    	if(result){
    		ticketPrincipal.getTotales().recalcular();
    		esFacturacionVentaCredito = true;
    	}
    	
    	return result;
    }
    
    public boolean obtenerDocumentoVentaCreditoCodServicio(Stage stage, String codServicio) throws LineaTicketException, ClientesServiceException, RestException, RestHttpException, ClienteConstraintViolationException, DocumentoException{
        boolean result = false;
        
        byte[] docVentaCredito = obtenerTicketVentaCreditoCentralCodServicio(codServicio);
        
        result = tratarDocumentoVentaCredito(stage, docVentaCredito);
        if(result){
            ticketPrincipal.getTotales().recalcular();
            esFacturacionVentaCredito = true;
        }
        
        return result;
    }
    
    public boolean obtenerDocumentoVentaCreditoCodPedido(Stage stage, String codPedido) throws RestException, RestHttpException, LineaTicketException, ClientesServiceException, DocumentoException, ClienteConstraintViolationException {
        boolean result = false;
        
        byte[] docVentaCredito = obtenerTicketVentaCreditoCentralCodPedido(codPedido);
        
        result = tratarDocumentoVentaCredito(stage, docVentaCredito);
        if(result){
            ticketPrincipal.getTotales().recalcular();
            esFacturacionVentaCredito = true;
        }
        
        return result;
    }
    
    protected void asignarLineasDevueltas(ResponseGetTicketDev res) {
		log.debug("asignarLineasDevueltas() - Actualizamos precio de lineas origen restando importe de promociones de tipo menos ingreso");
		List<LineaTicketAbstract> lineas = ticketOrigen.getLineas();
		SesionImpuestos sesionImpuestos = sesion.getImpuestos();
		BigDecimal importeLineas = BigDecimal.ZERO; 
		BigDecimal importeLineasOriginal = BigDecimal.ZERO; 
		Boolean promocionMenosIngreso = variablesServices.getVariableAsBoolean(VariablesServices.TPV_TRATAR_PROMOCIONES_MENOS_INGRESO);
		
		//calculamos el precio origen restando el precio de las promociones de menos ingreso
		for (LineaTicketAbstract lineaOrigen : lineas) {
			
			if(promocionMenosIngreso && !BigDecimalUtil.isIgualACero(lineaOrigen.getImporteTotalPromocionesMenosIngreso())){
                //Calculamos el precioSinDto 
                BigDecimal importeSinPromocionesMenosIngreso = lineaOrigen.getImporteTotalConDto().subtract(lineaOrigen.getImporteTotalPromocionesMenosIngreso()); 
                BigDecimal precioSinPromocionesMenosIngreso =  BigDecimalUtil.isIgualACero(lineaOrigen.getCantidad()) ? BigDecimal.ZERO : importeSinPromocionesMenosIngreso.divide(lineaOrigen.getCantidad(), 6, RoundingMode.HALF_UP);
                
                //Actualizamos todas los demás precios e importes a partir del precioSinDto 
                lineaOrigen.setPrecioTotalSinDto(precioSinPromocionesMenosIngreso); 
                BigDecimal precioSinImpuestos = sesionImpuestos.getPrecioSinImpuestos(lineaOrigen.getCodImpuesto(), precioSinPromocionesMenosIngreso, lineaOrigen.getCabecera().getCliente().getIdTratImpuestos());
                
              //Actualizamos todas los demás precios e importes a partir del precioSinDto
    			lineaOrigen.setPrecioSinDto(precioSinImpuestos);
    			lineaOrigen.setPrecioConDto(lineaOrigen.getPrecioSinDto());
    			lineaOrigen.setPrecioTotalConDto(lineaOrigen.getPrecioTotalSinDto());
    			lineaOrigen.setImporteConDto(BigDecimalUtil.redondear(lineaOrigen.getPrecioConDto().multiply(lineaOrigen.getCantidad()))); 
                lineaOrigen.setImporteTotalConDto(BigDecimalUtil.redondear(lineaOrigen.getPrecioTotalConDto().multiply(lineaOrigen.getCantidad())));
			}
			else{
				//igualmos los precios sin DTO a los precios con DTO, en devoluciones no hay descuentos 
				lineaOrigen.setPrecioTotalConDto(BigDecimalUtil.isIgualACero(lineaOrigen.getCantidad())?BigDecimal.ZERO:lineaOrigen.getImporteTotalConDto().setScale(6, BigDecimal.ROUND_HALF_UP).divide(lineaOrigen.getCantidad().setScale(6, BigDecimal.ROUND_HALF_UP),BigDecimal.ROUND_HALF_UP)); 
				lineaOrigen.setPrecioTotalSinDto(lineaOrigen.getPrecioTotalConDto()); 
				lineaOrigen.setPrecioSinDto(lineaOrigen.getPrecioConDto()); 
				
				importeLineasOriginal = importeLineasOriginal.add(lineaOrigen.getImporteTotalConDto());
			}
			
			importeLineas = importeLineas.add(lineaOrigen.getImporteTotalConDto());
		}
		
		if(promocionMenosIngreso){
            //Añadimos a la ultima linea la diferencia entre el total de la cabecera del ticket y el total de precio de las lineas 
			if(!lineas.isEmpty()){
				BigDecimal totalTicket = ticketOrigen.getCabecera().getTotales().getTotalAPagar().subtract(ticketOrigen.getCabecera().getTotales().getTotalPromocionesCabecera()); 
				BigDecimal diferenciaImportes = totalTicket.subtract(importeLineas);
				
				int i = 1;
				LineaTicketAbstract ultimaLinea = lineas.get(lineas.size()-i);
				BigDecimal precioTotalSinDtoUltimaLinea = BigDecimalUtil.isIgualACero(ultimaLinea.getCantidad()) ? BigDecimal.ZERO : ultimaLinea.getPrecioTotalSinDto().add(diferenciaImportes.divide(ultimaLinea.getCantidad(), 2, RoundingMode.HALF_UP));
				BigDecimal precioTotalSinDto = precioTotalSinDtoUltimaLinea;
				while(BigDecimalUtil.isMenorACero(precioTotalSinDto)) {
					i++;
					ultimaLinea = lineas.get(lineas.size()-i);
					precioTotalSinDto = precioTotalSinDtoUltimaLinea;
				}
				
				BigDecimal precioSinImpuestos = sesionImpuestos.getPrecioSinImpuestos(ultimaLinea.getCodImpuesto(), ultimaLinea.getPrecioTotalSinDto(), ultimaLinea.getCabecera().getCliente().getIdTratImpuestos());
				ultimaLinea.setPrecioTotalSinDto(precioTotalSinDtoUltimaLinea);
				ultimaLinea.setPrecioSinDto(precioSinImpuestos);
				ultimaLinea.setPrecioConDto(ultimaLinea.getPrecioSinDto());
				ultimaLinea.setPrecioTotalConDto(ultimaLinea.getPrecioTotalSinDto());
                ultimaLinea.setImporteConDto(ultimaLinea.getPrecioConDto().multiply(ultimaLinea.getCantidad())); 
                ultimaLinea.setImporteTotalConDto(ultimaLinea.getPrecioTotalConDto().multiply(ultimaLinea.getCantidad()));
				
			}
		}
		else{
			//Añadimos a la ultima linea la diferencia entre la suma de importes de las lineas del ticket original y la suma de importes de las lineas del ticket de devolución
			BigDecimal diferenciaImportes = importeLineasOriginal.subtract(importeLineas);
			if(!lineas.isEmpty() && !BigDecimalUtil.isIgualACero(diferenciaImportes)){
				LineaTicketAbstract ultimaLinea = lineas.get(lineas.size()-1);
				ultimaLinea.setPrecioTotalSinDto(ultimaLinea.getPrecioTotalSinDto().add(diferenciaImportes));
				
				BigDecimal precioSinImpuestos = sesionImpuestos.getPrecioSinImpuestos(ultimaLinea.getCodImpuesto(), ultimaLinea.getPrecioTotalSinDto(), ultimaLinea.getCabecera().getCliente().getIdTratImpuestos());
				ultimaLinea.setPrecioSinDto(precioSinImpuestos);
				ultimaLinea.setPrecioConDto(ultimaLinea.getPrecioSinDto());
				ultimaLinea.setPrecioTotalConDto(ultimaLinea.getPrecioTotalSinDto());
				ultimaLinea.setImporteConDto(BigDecimalUtil.redondear(ultimaLinea.getPrecioConDto().multiply(ultimaLinea.getCantidad())));
				ultimaLinea.setImporteTotalConDto(BigDecimalUtil.redondear(ultimaLinea.getPrecioTotalConDto().multiply(ultimaLinea.getCantidad())));
			}
		}

		
		//Si tenemos datos devolución los usamos
    	if(res != null){
    		log.debug("asignarLineasDevueltas() - Sí hemos recibido datos de devolución, actualizamos precios de líneas origen");
	        List<ArticulosDevueltosBean> lineasDevolucion = res.getLineas();
	        for(ArticulosDevueltosBean articulo: lineasDevolucion){
	            
	            LineaTicketAbstract linea = (LineaTicketAbstract) ticketOrigen.getLinea(articulo.getLinea());
	            if(linea!=null){
	            	linea.setCantidadDevuelta(new BigDecimal(articulo.getCantidadDevuelta()).setScale(3, RoundingMode.HALF_UP));
	                linea.setPrecioTotalConDto(new BigDecimal(articulo.getPrecioTotal()).setScale(4, RoundingMode.HALF_UP));
	            }
	        }
    		ticketPrincipal.getCabecera().getDatosDocOrigen().setRecoveredOnline(true);
        }
    	else {
    		ticketPrincipal.getCabecera().getDatosDocOrigen().setRecoveredOnline(false);
    	}
    }
    /**
     * Trata el ticket recuperado del servicio rest o en local para el abono.
     *
     * @param ticketRecuperado
     */
	protected boolean tratarTicketRecuperado(byte[] ticketRecuperado) throws TicketsServiceException {
    	try{
	        log.debug(new String(ticketRecuperado, "UTF-8"));
	        
	        contadorLinea = 1;
	        boolean resultado = false;

			nuevoTicket();

			// Realizamos el unmarshall
			ticketOrigen = (TicketVentaAbono) MarshallUtil.leerXML(ticketRecuperado, getTicketClasses(documentoActivo).toArray(new Class[]{}));

			TipoDocumentoBean docAbono = documentos.getDocumentoAbono(ticketOrigen.getCabecera().getCodTipoDocumento());

			setEsDevolucion(true);
			setDocumentoActivo(docAbono);

			if (!documentos.isDocumentosAbonoConfigurados()) {
				List<String> tiposDoc = docAbono.getTiposDocumentosOrigen();
				boolean codTipoDocumentoValido = false;
				for (String codTipoDocumento : tiposDoc) {
					if (codTipoDocumento.equals(ticketOrigen.getCabecera().getCodTipoDocumento())) {
						codTipoDocumentoValido = true;
						break;
					}
				}
				if (!codTipoDocumentoValido) {
					throw new TicketsServiceException(String.format("El documento obtenido '%s' no se encuentra entre los documentos de origen del tipo '%s'",
							ticketOrigen.getCabecera().getCodTipoDocumento(), docAbono.getCodtipodocumento()));
				}
			}
	        
	        if(ticketOrigen!=null ){
                //Se tiene que crear este DatosDocumentoOrigen antes de inicializar los datos de la cabecera con los datos de la devolución
                DatosDocumentoOrigenTicket datosOrigen = new DatosDocumentoOrigenTicket();
                datosOrigen.setCaja(ticketOrigen.getCabecera().getCodCaja());
                datosOrigen.setCodTipoDoc(ticketOrigen.getCabecera().getCodTipoDocumento());
                datosOrigen.setIdTipoDoc(ticketOrigen.getCabecera().getTipoDocumento());
                datosOrigen.setNumFactura(ticketOrigen.getIdTicket());
                datosOrigen.setSerie(ticketOrigen.getCabecera().getTienda().getCodAlmacen());
                datosOrigen.setFecha(ticketOrigen.getCabecera().getFechaAsLocale());
                datosOrigen.setDesTipoDoc(ticketOrigen.getCabecera().getDesTipoDocumento());
                datosOrigen.setUidTicket(ticketOrigen.getUidTicket());
                datosOrigen.setCodTicket(ticketOrigen.getCabecera().getCodTicket());
                datosOrigen.setTienda(ticketOrigen.getTienda().getCodAlmacen());
                
                ticketPrincipal.getCabecera().setUidTicketEnlace(ticketOrigen.getUidTicket());
                // Seteamos las referencias internas de Cabecera y totales hacia el ticket
                ticketPrincipal.getCabecera().inicializarCabecera(ticketPrincipal);
                ticketPrincipal.setCajero(sesion.getSesionUsuario().getUsuario());
                ticketPrincipal.getCabecera().getTotales().setCambio(SpringContext.getBean(PagoTicket.class, MediosPagosService.medioPagoDefecto));
                ticketPrincipal.getCabecera().setDatosDocOrigen(datosOrigen);
                
                ticketPrincipal.setCliente(ticketOrigen.getCliente());
                
                documentoActivo = docAbono;
                
                // Establecemos los parámetros de tipo de documento del ticket
                ticketPrincipal.getCabecera().setTipoDocumento(documentoActivo.getIdTipoDocumento());
                ticketPrincipal.getCabecera().setCodTipoDocumento(documentoActivo.getCodtipodocumento());
                ticketPrincipal.getCabecera().setFormatoImpresion(documentoActivo.getFormatoImpresion());
                
                resultado = true;
                
	            ticketPrincipal.getCabecera().setDatosFidelizado(ticketOrigen.getCabecera().getDatosFidelizado());
	        }
	        
	        return resultado;
    	}catch(Exception e){
    		throw new TicketsServiceException(new com.comerzzia.core.util.base.Exception(I18N.getTexto("Lo sentimos, ha ocurrido un error al recuperar el ticket"), e));
    	}
    }

    protected void descontarLineasNegativasTicketOrigen() {
    	List<LineaTicket> lineasNegativas = new ArrayList<LineaTicket>();
    	 
    	Iterator<LineaTicket> it = ticketOrigen.getLineas().iterator();
    	while(it.hasNext()) {
    		LineaTicket linea = it.next();
    		if(BigDecimalUtil.isMenorACero(linea.getImporteTotalConDto()) || linea.getLineaDocumentoOrigen() != null)  {
    			lineasNegativas.add(linea);
    			it.remove();
    		}
    	}

    }

	protected boolean puedoInsertarTicket(TicketEntregaCuenta ticketOrigen){
    	boolean add = true;
    	List<LineaTicket> lineas = ticketPrincipal.getLineas();
    	for(LineaTicket linea : lineas){
    		if(linea.getDocumentoOrigen() != null){
    			esFacturacionVentaCredito = true;
	    		if(linea.getDocumentoOrigen().getUidDocumentoOrigen().equals(ticketOrigen.getUidTicket())){
	    			add = false;
	    			break;
	    		}
    		}
    	}
    	return add;
    }
    
    protected boolean tratarDocumentoVentaCredito(Stage stage, byte[] docVentaCredito) throws LineaTicketException, RestException, RestHttpException, ClientesServiceException, ClienteConstraintViolationException, DocumentoException{
        log.trace("tratarDocumentoVentaCredito() - Tratando documento de venta a credito para su facturación.");
        
        boolean result = false;
        
        log.debug("Ticket recuperado para la facturación de venta a credito: \n"+new String(docVentaCredito));
        
        List<Class<?>> ticketClasses = getTicketClasses(documentoActivo);
        ticketClasses.add(TicketEntregaCuenta.class);
		TicketEntregaCuenta ticketOrigen = (TicketEntregaCuenta) MarshallUtil.leerXML(docVentaCredito, ticketClasses.toArray(new Class[]{}));
		ticketOrigen.getCabecera().setTicket(ticketOrigen);
	
		if(puedoInsertarTicket(ticketOrigen)){
			if (!isEsFacturacionVentaCredito()) {
				//Asignamos al ticket principal el cliente del ticket origen
				//Primero buscaremos en local y luego en central. Si se encuentra en central, lo salvamos en local. 
				String codCliente = ticketOrigen.getCliente().getCodCliente();
				
				ClienteBean cliente = null;
				try {
					cliente = clientesService.consultarCliente(codCliente);
				} catch (ClienteNotFoundException e1) {
					ConsultarClienteRequestRest consultaCliente = new ConsultarClienteRequestRest(
							variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY),
							sesion.getAplicacion().getUidActividad());
					consultaCliente.setCodcli(codCliente);
					ResponseGetClienteRest res = ClientesRest.getClienteByCodcli(consultaCliente);
					clientesService.salvar(res);
					try {
						cliente = clientesService.consultarCliente(codCliente);
					} catch (ClienteNotFoundException e) {
						throw new ClientesServiceException("No se ha podido salvar el cliente recuperado desde central", e);
					}
				}
				ticketPrincipal.setCliente(cliente);
			} else {
				//Si ya hemos recuperado una factura, comprobamos que solo podemos recuperar otra si es el mismo cliente
				String codClienteActual = ticketPrincipal.getCabecera().getCliente().getCodCliente();
				String codClienteOrigen = ticketOrigen.getCabecera().getCliente().getCodCliente();
				if (!codClienteOrigen.equals(codClienteActual)) {
					throw new IllegalStateException("distinto-cliente");
				}
			}
			TicketVentaAbono ticketVentaAbono = (TicketVentaAbono) ticketPrincipal;
			
			FidelizacionBean datosFidelizado = ticketOrigen.getDatosFidelizado();
			try {
				if (datosFidelizado != null) {
					String numTarjetaOrigen = datosFidelizado.getNumTarjetaFidelizado(); 
					String uidActividad = sesion.getAplicacion().getUidActividad();
					
					if(!(Dispositivos.getInstance().getFidelizacion() instanceof FidelizacionNoConfig)) {
				        datosFidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(stage, numTarjetaOrigen, uidActividad);
					}
					else {
						String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
						FidelizadoRequestRest fidelizadoRequest = new FidelizadoRequestRest(apiKey, uidActividad, datosFidelizado.getIdFidelizado());
						fidelizadoRequest.setNumeroTarjeta(numTarjetaOrigen);
						FidelizadoBean fidelizado = FidelizadosRest.getFidelizadoPorId(fidelizadoRequest);
						datosFidelizado = new FidelizacionBean();
						datosFidelizado.setNumTarjetaFidelizado(numTarjetaOrigen);
				        List<String> codColectivos = new ArrayList<>();
				        if(fidelizado.getColectivos() != null && !fidelizado.getColectivos().isEmpty()) {
					        for (ColectivosFidelizadoBean responseGetFidelizadoColectivoRest : fidelizado.getColectivos()) {
					        	codColectivos.add(responseGetFidelizadoColectivoRest.getCodColectivo());
							}
				        }
				        datosFidelizado.setCodColectivos(codColectivos);
				        List<String> uidEtiquetas = new ArrayList<String>();
				        if(fidelizado.getEtiquetasCategorias() != null && !fidelizado.getEtiquetasCategorias().isEmpty()){
				        	for(EtiquetaBean responseGetFidelizadoEtiquetaRest : fidelizado.getEtiquetasCategorias()){
				        		uidEtiquetas.add(responseGetFidelizadoEtiquetaRest.getUidEtiqueta());
				        	}
				        }
				        datosFidelizado.setUidEtiquetas(uidEtiquetas);
				        datosFidelizado.setNombre(fidelizado.getNombre());
				        datosFidelizado.setApellido(fidelizado.getApellidos());
				        datosFidelizado.setCodTipoIden(fidelizado.getCodTipoIden());
				        datosFidelizado.setCp(fidelizado.getCp());
				        datosFidelizado.setDocumento(fidelizado.getDocumento());
				        datosFidelizado.setDomicilio(fidelizado.getDomicilio());
				        datosFidelizado.setLocalidad(fidelizado.getLocalidad());
				        datosFidelizado.setPoblacion(fidelizado.getPoblacion());
				        datosFidelizado.setProvincia(fidelizado.getProvincia());
				        datosFidelizado.setCodPais(fidelizado.getCodPais());
				        datosFidelizado.setDesPais(fidelizado.getDesPais());
				        if(fidelizado.getSaldo() != null){
				        	datosFidelizado.setSaldo(new BigDecimal(fidelizado.getSaldo()));
				        }
				        if(fidelizado.getSaldoProvisional() != null){
				        	datosFidelizado.setSaldoProvisional(new BigDecimal(fidelizado.getSaldoProvisional()));
				        }
					}
				}
			} catch(Exception e) {
				log.warn("tratarDocumentoVentaCredito() - No se ha podido consultar el fidelizado - "+ e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			} finally {
				ticketVentaAbono.getCabecera().setDatosFidelizado(datosFidelizado);
			}
			
	        
	        ticketVentaAbono.setEntregasCuenta(new EntregasCuentaTicket());
	        if (ticketOrigen.getEntregasCuenta() != null) {
	    		try {
	    			BeanUtils.copyProperties(ticketVentaAbono.getEntregasCuenta(), ticketOrigen.getEntregasCuenta());
	    		} catch (Exception e) {
	    			log.debug("tratarDocumentoVentaCredito() - " + e.getClass().getName() + " - " + e.getLocalizedMessage());
	    		}
	    	}
	                
	        BigDecimal pagadoACuenta = BigDecimal.ZERO;
	        if (ticketOrigen.getEntregasCuenta() != null && ticketOrigen.getEntregasCuenta().getEntregasCuenta() != null) {
		        for(EntregaCuentaTicket pago:ticketOrigen.getEntregasCuenta().getEntregasCuenta()){
		            pagadoACuenta = pagadoACuenta.add(pago.getImporte());
		        }
	        }
	        if (ticketOrigen.getPagos() != null && ticketVentaAbono.getEntregasCuenta().getEntregasCuenta().isEmpty()) {
	        	for (PagoTicket pagoOrigen : ticketOrigen.getPagos()) {
	        		MedioPagoBean medioPago = mediosPagosService.getMedioPago(pagoOrigen.getMedioPago().getCodMedioPago());
	        		
	        		if(medioPago == null) {
	        			throw new DocumentoException("El medio de pago " + pagoOrigen.getDesMedioPago() + " utilizado en la venta original no está disponible en esta tienda");
	        		}
	        		
	        		if(!isMedioPagoContrareembolso(medioPago)){
						EntregaCuentaTicket entregaCuenta = new EntregaCuentaTicket();
						try {
			    			BeanUtils.copyProperties(entregaCuenta, pagoOrigen);
			    		} catch (Exception e) {
			    			log.debug("tratarDocumentoVentaCredito() - " + e.getClass().getName() + " - " + e.getLocalizedMessage());
			    		}
						ticketVentaAbono.getEntregasCuenta().getEntregasCuenta().add(entregaCuenta);
						pagadoACuenta = pagadoACuenta.add(entregaCuenta.getImporte());
	        		}
				}
	        }
	        if(ticketOrigen.getEntregasCuenta() != null) {
		        TillTransactionRequestRest tillTransactionRequest = new TillTransactionRequestRest();
		        tillTransactionRequest.setApiKey(variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY));
		        tillTransactionRequest.setTransactionId(ticketOrigen.getEntregasCuenta().getUidTransaccion());
		        tillTransactionRequest.setUidActividad(sesion.getAplicacion().getUidActividad());
		        TransaccionCajaDTO transaction = TillTransactionsRest.getTillTransaction(tillTransactionRequest);
		        
		        for(TransaccionCajaDetalleBean detail : transaction.getDetails()) {
		        	boolean isPayment = false;
		        	for(EntregaCuentaTicket prepayment : ticketVentaAbono.getEntregasCuenta().getEntregasCuenta()) {
		        		if(detail.getUidTransaccionDet().equals(prepayment.getUidTransaccionDet())) {
		        			isPayment = true;
		        			if(prepayment.getImporte().compareTo(detail.getImporte()) != 0) {
		        				prepayment.setImporte(detail.getImporte());
		        			}
		        			break;
		        		}
		        	}
		        	if(!isPayment) {
		        		EntregaCuentaTicket newPrepayment = new EntregaCuentaTicket();
		        		MedioPagoBean paymentMethod = mediosPagosService.getMedioPago(detail.getCodmedpag());
		        		newPrepayment.setCodMedioPago(paymentMethod.getCodMedioPago());   		
		        		newPrepayment.setDesMedioPago(paymentMethod.getDesMedioPago());
		        		newPrepayment.setImporte(detail.getImporte());
		        		newPrepayment.setUidTransaccionDet(detail.getUidTransaccionDet());
		        		ticketVentaAbono.getEntregasCuenta().getEntregasCuenta().add(newPrepayment);
		        		pagadoACuenta = pagadoACuenta.add(newPrepayment.getImporte());
		        	}
		        }
	        }
	        
	        ticketPrincipal.getCabecera().getTotales().setEntregadoACuenta(ticketPrincipal.getCabecera().getTotales().getEntregadoACuenta().add(pagadoACuenta));
	        
	        if (ticketOrigen.getLineas() != null) {
		        for (LineaTicket lineaRecuperada : ticketOrigen.getLineas()) {
					LineaTicket nuevaLineaArticulo = nuevaLineaArticulo(lineaRecuperada.getCodArticulo(), lineaRecuperada.getDesglose1(), lineaRecuperada.getDesglose2(), lineaRecuperada.getCantidad(), null);
					
					nuevaLineaArticulo.setDesArticulo(lineaRecuperada.getDesArticulo());
					
					BigDecimal descuento = lineaRecuperada.getDescuento();
					if(BigDecimalUtil.isIgualACero(descuento)) {
						descuento = lineaRecuperada.getDescuentoManual();
					}
					nuevaLineaArticulo.setDescuentoManual(descuento);
					
					nuevaLineaArticulo.setPrecioTotalSinDto(lineaRecuperada.getPrecioTotalSinDto());
					nuevaLineaArticulo.setPrecioSinDto(lineaRecuperada.getPrecioSinDto());
					nuevaLineaArticulo.setAdmitePromociones(false);
					
					nuevaLineaArticulo.setEditable(false);
					nuevaLineaArticulo.setIdLinea(contadorLinea);
					contadorLinea++;
					
					DocumentoOrigen lineaReferenciaTicket = new DocumentoOrigen(ticketOrigen.getUidTicket(), lineaRecuperada.getIdLinea());
					lineaReferenciaTicket.setPromociones(lineaRecuperada.getPromociones());
					nuevaLineaArticulo.setDocumentoOrigen(lineaReferenciaTicket);
					nuevaLineaArticulo.setUpdateStock(Boolean.FALSE);
				}
	        }
	        
	        result = true;
	        
	        recalcularConPromociones();
	        
	        ticketPrincipal.getTotales().setPuntos(ticketOrigen.getTotales().getPuntos());
		}else{
			String message = "El documento ya ha sido insertado";
			log.debug("tratarDocumentoVentaCredito() - " +  message);
			throw new  DocumentoException(message);
		}
	    return result;
    }
    
    public boolean isEsDevolucion() {
        return ticketPrincipal.isEsDevolucion();
    }
    
    public void setEsDevolucion(boolean esDevolucion) {
    	ticketPrincipal.setEsDevolucion(esDevolucion);
    	this.esDevolucion = esDevolucion;
    }
    
	public void setEsApartado(boolean esApartado) {
		((TicketVentaAbono)ticketPrincipal).setEsApartado(esApartado);
	}
    
    public boolean isHayPagoAbreCajon(){
        boolean abreCajon = false;
        for(Object pago:((TicketVentaAbono)ticketPrincipal).getPagos()){
            if(((PagoTicket) pago).getMedioPago().getCodMedioPago().equals(MediosPagosService.medioPagoDefecto.getCodMedioPago())){
                log.trace("isHayPagoAbreCajon() - Establecemos booleano para la apertura de cajón");
                abreCajon =true;
                break;
            }
        }
        return abreCajon;
    }
    
    public BigDecimal tratarSignoCantidad(BigDecimal cantidad, String codTipoDoc){
        return lineasTicketServices.tratarSignoDocumento(cantidad, codTipoDoc, esDevolucion);
    }
    
    /**
     * Solo se hará esto si no hay ticket origen, lo que significa que no estamos en una devolución controlada.
     * @throws DocumentoException
     */
    public void borrarDatosFactura() throws DocumentoException {
    	if(ticketOrigen == null && ((TicketVentaAbono)ticketPrincipal).getDatosFacturacion() != null){
    		setDocumentoActivo(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA));
	        ((TicketVentaAbono)ticketPrincipal).setDatosFacturacion(null);
    	}
    }
    
    public boolean isEsOperacionTarjetaRegalo() {
        return esOperacionTarjetaRegalo;
    }
    
    public void setEsOperacionTarjetaRegalo(boolean esOperacionTarjetaRegalo){
        this.esOperacionTarjetaRegalo = esOperacionTarjetaRegalo;
    }
    
    public boolean isEsRecargaTarjetaRegalo(){
        return esRecargaTarjetaRegalo;
    }
    
    public void setEsRecargaTarjetaRegalo(boolean esTarjetaRegalo){
        this.esOperacionTarjetaRegalo = esTarjetaRegalo;
        this.esRecargaTarjetaRegalo = esTarjetaRegalo;
    }
    
    public boolean isDevolucionTarjetaRegalo(){
        return devolucionTarjetaRegalo;
    }
    
    public void setEsDevolucionTarjetaRegalo(boolean devolucionTarjRegalo){
        this.esOperacionTarjetaRegalo = devolucionTarjRegalo;
        this.devolucionTarjetaRegalo = devolucionTarjRegalo;
    }
    
    public boolean comprobarTarjetaRegalo(String codArticulo) throws TarjetaRegaloException{
		boolean esTarjetaRegalo = false;

		if (giftCardService.isGiftCardItem(codArticulo)) {
			esTarjetaRegalo = true;

			if (!this.isTicketVacio()) {
				log.info(I18N.getTexto("No se puede recargar la tarjeta regalo en un ticket ya iniciado."));
				throw new TarjetaRegaloException(I18N.getTexto("No se puede recargar la tarjeta regalo en un ticket ya iniciado."));
			}
			else if (esOperacionTarjetaRegalo) {
				log.info(I18N.getTexto("No puede recargarse la tarjeta regalo con el pago de otra. "));
				throw new TarjetaRegaloException(I18N.getTexto("No puede recargarse la tarjeta regalo con el pago de otra."));
			}
		}

		return esTarjetaRegalo;
    }
    
    public boolean comprobarTarjetaRegaloLineaYaInsertada(LineaTicket linea) throws LineaInsertadaNoPermitidaException{
        boolean esTarjetaRegalo = false;
        if (linea != null) {
			String codArticulo = linea.getCodArticulo();

			if (giftCardService.isGiftCardItem(codArticulo)) {
				esTarjetaRegalo = true;

				if (!(ticketPrincipal == null || ticketPrincipal.getLineas() == null || ticketPrincipal.getLineas().size() == 1)) {
					log.info(I18N.getTexto("No se puede recargar la tarjeta regalo en un ticket ya iniciado."));
					throw new LineaInsertadaNoPermitidaException(I18N.getTexto("No se puede recargar la tarjeta regalo en un ticket ya iniciado."), null);
				} else if (esOperacionTarjetaRegalo) {
					log.info(I18N.getTexto("No puede recargarse la tarjeta regalo con el pago de otra. "));
					throw new LineaInsertadaNoPermitidaException(I18N.getTexto("No puede recargarse la tarjeta regalo con el pago de otra."), null);
				}
			}
		}
        return esTarjetaRegalo;
    }
    
    protected void procesarTarjetaRegalo(Stage stage) throws TarjetaRegaloException, DocumentoException{
        log.trace("procesarTarjetaRegalo()");
        
        TarjetaRegaloTicket tarjRegalo = ticketPrincipal.getCabecera().getTarjetaRegalo();
        
        if(tarjRegalo!=null){
        	String uidTransaccion = UUID.randomUUID().toString();
            tarjRegalo.setUidTransaccion(uidTransaccion);
            
            if(esRecargaTarjetaRegalo){
                log.trace("Procesando recarga de la tarjeta regalo "+ tarjRegalo.getNumTarjetaRegalo());
                
                try {
	                giftCardService.recharge(stage, uidTransaccion, tarjRegalo, ticketPrincipal);
					tarjRegalo.setSaldoProvisional(tarjRegalo.getSaldoProvisional().add(tarjRegalo.getImporteRecarga()));
                }
                catch(Exception e) {
					log.error("procesarTarjetaRegalo() - Error al recargar la tarjeta regalo: " + e.getMessage(), e);
					
					String message = null;
					if (e instanceof ValidationRequestRestException) {
						message = e.getMessage();
					}
					else if (e instanceof ValidationDataRestException) {
						message = e.getMessage();
					}
					else if (e instanceof HttpServiceRestException) {
						message = e.getMessage();
					}
					else if (e instanceof RestHttpException) {
						message = I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición");
					}
					else if (e instanceof RestConnectException) {
						message = I18N.getTexto("No se ha podido conectar con el servidor");
					}
					else if (e instanceof RestTimeoutException) {
						message = I18N.getTexto("El servidor ha tardado demasiado tiempo en responder");
					}
					else if (e instanceof RestException) {
						message = I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición");
					}
					else {
						message = I18N.getTexto("Lo sentimos, ha ocurrido un error.");
					}
					throw new TarjetaRegaloException(message, e); 
                }
            }
        }
    }
    
    public boolean esDevolucionRecargaTarjetaRegalo(){
        if(ticketOrigen.getCabecera().getTarjetaRegalo()!=null){
            if(ticketOrigen.getLineas().size()==1){
                LineaTicket lineaTarjeta = (LineaTicket)ticketOrigen.getLinea(1);
                if(giftCardService.isGiftCardItem(lineaTarjeta.getCodArticulo())){
                    ticketOrigen.getCabecera().getTarjetaRegalo().setImporteRecarga(lineaTarjeta.getPrecioTotalConDto());
                    devolucionTarjetaRegalo = true;
                }
            }
        }
        
        return devolucionTarjetaRegalo;
    }
    
    public void recalcularConPromociones(){
    	Integer puntosAnteriores = 0;
    	if (isEsFacturacionVentaCredito()){
    		puntosAnteriores = ticketPrincipal.getTotales().getPuntos();
    	}
        sesionPromociones.aplicarPromociones((TicketVentaAbono) ticketPrincipal);
        ticketPrincipal.getTotales().recalcular();
        ticketPrincipal.getTotales().addPuntos(puntosAnteriores);
    }

    public boolean isEsFacturacionVentaCredito() {
    	return esFacturacionVentaCredito;
    }

    public void salvarTicketApartado(final ApartadosCabeceraBean cabeceraApartado, final Stage stage, final SalvarTicketCallback callback) throws TicketsServiceException {
    	new SalvarTicketApartadoTask(cabeceraApartado, stage, callback).start();
    }
    
    public void generarVentaDeApartados(List<ApartadosDetalleBean> articulos, ClienteBean clienteApartado, ApartadosCabeceraBean cabeceraApartado) throws LineaTicketException{
    	
    	try {
			nuevoTicket();
			ticketPrincipal.setCliente(clienteApartado);
			
			for(ApartadosDetalleBean articulo : articulos){
				if(articulo.getEstadoLineaApartado() == ApartadosCabeceraBean.ESTADO_DISPONIBLE){
					LineaTicket nuevaLineaArticulo = nuevaLineaArticulo(articulo.getCodart(), articulo.getDesglose1(), articulo.getDesglose2(), articulo.getCantidad(), null);
					nuevaLineaArticulo.setNumerosSerie(articulo.getNumerosSerie());
					nuevaLineaArticulo.setPrecioTotalSinDto(articulo.getImporteTotal());
					nuevaLineaArticulo.recalcularImporteFinal();
				}
			}
			ticketPrincipal.getTotales().recalcular();
			String codMedioPagoApartado = sesion.getAplicacion().getTienda().getTiendaBean().getCodMedioPagoApartado();
			nuevaLineaPago(codMedioPagoApartado, ticketPrincipal.getTotales().getTotalAPagar(), false, false, null, true);
		} 
    	catch (PromocionesServiceException | DocumentoException e) {
			LineaTicketException ex = new LineaTicketException(I18N.getTexto("Error creando el ticket de venta."),e);
			log.error("Error creando ticket", ex);
			throw ex;
		} 
    	catch (LineaTicketException e) {
			throw e;
		}
    }
    
    /**Llamado cuando se inserta un código de barra especial de tipo ticket. El estándar no ejecuta nada pero puede ser sobreescrito
     * @param codBarrasEspecial */
    protected LineaTicket tratarCodigoBarraEspecialTicket(CodigoBarrasBean codBarrasEspecial) throws LineaTicketException {
    	throw new UnsupportedOperationException("No se ha definido gestión de códigos de barras especiales de tipo codticket");
	}
	
	public void guardarCopiaSeguridadTicket() {
		log.debug("guardarCopiaSeguridadTicket() - Persistiendo ticket en venta para copia de seguridad...");
		try {			
			copiaSeguridadTicketService.guardarBackupTicketActivo((TicketVentaAbono) ticketPrincipal);
		}
		catch (TicketsServiceException e) {
			log.error("guardarCopiaSeguridadTicket() - No se ha podido guardar la copia de seguridad del ticket activo");
		}
	}
	
	public void recuperarCopiaSeguridadTicket(Stage stage, TicketAparcadoBean ticketAparcado) throws LineaTicketException, TicketsServiceException, PromocionesServiceException, DocumentoException {
		log.debug("recuperarCopiaSeguridadTicket() - Recuperando copia de seguridad del ticket...");
		
        recuperarTicket(stage, ticketAparcado);
        
        // Guardamos de nuevo la copia de seguridad ya que está activo el ticket.
        guardarCopiaSeguridadTicket();
	}
	
	protected boolean isMedioPagoContrareembolso(MedioPagoBean medioPago){
		return Boolean.TRUE.equals(medioPago.getContado()) && Boolean.TRUE.equals(medioPago.getEfectivo()) && Boolean.TRUE.equals(medioPago.getVisibleTiendaVirtual());
	}

	public void salvarTicketSeguridad(Stage stage, SalvarTicketCallback callback) {
		new SalvarTicketSeguridadTask(stage, callback).start();
	}

	protected class SalvarTicketSeguridadTask extends SalvarTicketTask {

		public SalvarTicketSeguridadTask(Stage stage, SalvarTicketCallback callback) {
	        super(stage, callback);
        }

		protected Stage stage;
		protected SalvarTicketCallback callback;

		@Override
		protected Void call() throws Exception {
			sesionPromociones.aplicarPromocionesFinales((TicketVentaAbono) ticketPrincipal);
			
			sesionPromociones.generarCuponesDtoFuturo(ticketPrincipal);

			ticketPrincipal.getTotales().recalcular();
			
			if(ticketPrincipal.getIdTicket() == null) {
				ticketsService.setContadorIdTicket((Ticket) ticketPrincipal);
			}
			guardarCopiaSeguridadTicket();
			
			log.debug("SalvarTicketTask() - Contador obtenido: idTicket = " + ticketPrincipal.getIdTicket());

			return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
		}

		@Override
		protected void failed() {
			super.failed();
		}

	}

	public boolean comprobarTratamientoFiscalDev() {
		Long idTratamientoImpuestosOrigen = ticketOrigen.getCabecera().getTienda().getIdTratamientoImpuestos();
		if(idTratamientoImpuestosOrigen == null) {
			return true; // Por compatibilidad con versiones anteriores
		}
		
		Long idTratamientoImpuestosTicket = sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos();
		return idTratamientoImpuestosOrigen.equals(idTratamientoImpuestosTicket);
	}

	public void guardarTicketVacioTrasCopiaSeguridad(Stage stage, TicketAparcadoBean copiaSeguridad) {
       try{
			recuperarTicket(stage, copiaSeguridad);
	        
			if(ticketPrincipal.getCabecera().getIdTicket() != null) {
	        	TicketExample example = new TicketExample();
	        	example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(ticketPrincipal.getTienda().getCodAlmacen())
	        	.andCodcajaEqualTo(ticketPrincipal.getCabecera().getCodCaja()).andIdTipoDocumentoEqualTo(ticketPrincipal.getCabecera().getTipoDocumento())
	        	.andCodTicketEqualTo(ticketPrincipal.getCabecera().getCodTicket());
	        		        	
	        	TicketBean ticket = ticketsService.consultarTicket(example);
	        	if(ticket==null){
	        		salvarTicketVacio();
	        	}
	        	
	        }
		}
		catch(Exception e) {
			log.error("consultarCopiaSeguridad() - Ha habido un error al comprobar si el ticket ya tenía contador asignado. Puede que haya saltos de contador: " + e.getMessage(), e);
		}
       finally {
           ticketPrincipal = null;
       }
    }
	
    /**
     * Método para personalizar en caso de que se añadan campos nuevos al ticket y se quieran rescatar al recuperar un ticket aparcado
     * @param ticketRecuperado
     */
    protected void recuperarDatosPersonalizados(TicketVenta ticketRecuperado) {
    }
    
    public void completaLineaDevolucionPunto(){
    	for(LineaTicketAbstract linea : (List<LineaTicketAbstract>)getTicket().getLineas()){
			if(getTicketOrigen() != null){
				Integer puntosConcedidos = getPuntosConcedidosLinea(linea.getCodArticulo(), linea.getLineaDocumentoOrigen(), linea.getCantidad());
				linea.setPuntosADevolver(puntosConcedidos.doubleValue());
			}
		}
    }
    
    protected Integer getPuntosConcedidosLinea(String codArticulo,
			Integer lineaOrigen, BigDecimal cantidadLineaDevolucion) {
		Integer result = 0;
	    List<LineaTicketAbstract> lineasOriginales = getTicketOrigen().getLinea(codArticulo);
	    for(LineaTicketAbstract lineaOriginal : lineasOriginales){
	    	if(lineaOriginal.getIdLinea().equals(lineaOrigen) &&
	    			lineaOriginal.getCantidadDevuelta().compareTo(lineaOriginal.getCantidad()) <= 0
	    			){
	    		Integer puntosLinea = 0;
	    		if(((LineaTicket) lineaOriginal).getDocumentoOrigen() != null) {	    			
	    			for(PromocionLineaTicket promo : ((LineaTicket) lineaOriginal).getDocumentoOrigen().getPromociones()){
	    				if(promo.getPuntos()>0){
	    					puntosLinea +=promo.getPuntos();
	    				}
	    			}
	    		}
	    		for(PromocionLineaTicket promo : lineaOriginal.getPromociones()){
    				if(promo.getPuntos()>0){
    					puntosLinea +=promo.getPuntos();
    				}
    			}
				BigDecimal factor = cantidadLineaDevolucion.divide(lineaOriginal.getCantidad(),4,RoundingMode.HALF_UP).abs();
	    		result += BigDecimal.valueOf(puntosLinea).multiply(factor).intValue();
				break;
	    	}
	    }
	    return result;
	}

	public void crearVentanaErrorContador(Stage stage) {
		String msg = I18N.getTexto("No es posible emitir un documento por caducidad en fecha o nº secuencia asociada.");
		if(contador!=null && ContadorBean.ERROR_FECHAS.equals(contador.getError())){
			msg = I18N.getTexto("No es posible emitir un documento por caducidad en fecha");
		}else if(contador!=null && ContadorBean.ERROR_RANGOS.equals(contador.getError())){
			msg = I18N.getTexto("No es posible emitir un documento por caducidad en nº secuencia asociada.");
		}else if(contador != null && ContadorBean.ERROR_CONTADOR_INVALIDO.equals(contador.getError())){
			msg = I18N.getTexto("No es posible emitir un documento por tener el contador un valor inválido.");
		}else if(contador != null && ContadorBean.ERROR_PARAMETRO_INVALIDO.equals(contador.getError())){
			msg = I18N.getTexto("No es posible emitir un documento por tener uno de los parámetros un valor inválido. Revise el log.");
		}else if(contador != null && ContadorBean.ERROR_SALTO_NUMERACION.equals(contador.getError())){
			msg = I18N.getTexto("No es posible emitir un documento porque hay un salto en la numeración del contador. Revise la configuración de la vigencia asociada.");
		}
		VentanaDialogoComponent.crearVentanaError(msg, stage);
    }
	
	public ContadorBean getContador(){
		return contador;
	}

	public void toggleArticleLabel(int lineId, String uidEtiqueta) {
		log.debug("toggleArticleTag() - Toggle a tag on a line: " + lineId);
        LineaTicket line = ((TicketVentaAbono)ticketPrincipal).getLinea(lineId);
        EtiquetaArticuloBean tag = new EtiquetaArticuloBean();
        tag.setEtiqueta(uidEtiqueta);
        tag.setUidEtiqueta(uidEtiqueta);
        tag.setUidActividad(sesion.getAplicacion().getUidActividad());
        if(line.getArticulo().getEtiquetas().contains(tag)) {
        	line.getArticulo().getEtiquetas().remove(tag);
        }else {
        	line.getArticulo().getEtiquetas().add(tag);
        }
        
        recalcularConPromociones();
	}
	
	public void generateTicketFromSaleCredit(Stage stage, TicketEntregaCuenta saleCreditTicket, TransaccionCajaDTO transaction) throws DocumentoException, PromocionesServiceException, LineaTicketException {
		inicializarTicket();
		esFacturacionVentaCredito = true;
		TicketVentaAbono ticket = (TicketVentaAbono) getTicket();
		FidelizacionBean loyalCustomerData = saleCreditTicket.getDatosFidelizado();
		try {
			if (loyalCustomerData != null) {
				String cardNumber = loyalCustomerData.getNumTarjetaFidelizado(); 
				String activityId = sesion.getAplicacion().getUidActividad();
				String codCliente = saleCreditTicket.getCliente().getCodCliente();
				
				ClienteBean customer = null;
				try {
					customer = clientesService.consultarCliente(codCliente);
				} catch (ClienteNotFoundException e1) {
					ConsultarClienteRequestRest queryCustomer = new ConsultarClienteRequestRest(
							variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY),
							sesion.getAplicacion().getUidActividad());
					queryCustomer.setCodcli(codCliente);
					ResponseGetClienteRest res = ClientesRest.getClienteByCodcli(queryCustomer);
					clientesService.salvar(res);
					try {
						customer = clientesService.consultarCliente(codCliente);
					} catch (ClienteNotFoundException e) {
						throw new ClientesServiceException("No se ha podido salvar el cliente recuperado desde central", e);
					}
				}
				ticket.setCliente(customer);
				if(!(Dispositivos.getInstance().getFidelizacion() instanceof FidelizacionNoConfig)) {
					loyalCustomerData = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(stage, cardNumber, activityId);
				}
				else {
					String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
					FidelizadoRequestRest loyalCustomerRequest = new FidelizadoRequestRest(apiKey, activityId, loyalCustomerData.getIdFidelizado());
					loyalCustomerRequest.setNumeroTarjeta(cardNumber);
					FidelizadoBean loyalCustomer = FidelizadosRest.getFidelizadoPorId(loyalCustomerRequest);
					loyalCustomerData = new FidelizacionBean();
					loyalCustomerData.setNumTarjetaFidelizado(cardNumber);
			        List<String> colectiveCodes = new ArrayList<>();
			        if(loyalCustomer.getColectivos() != null && !loyalCustomer.getColectivos().isEmpty()) {
				        for (ColectivosFidelizadoBean colective : loyalCustomer.getColectivos()) {
				        	colectiveCodes.add(colective.getCodColectivo());
						}
			        }
			        loyalCustomerData.setCodColectivos(colectiveCodes);
			        List<String> tagUids = new ArrayList<String>();
			        if(loyalCustomer.getEtiquetasCategorias() != null && !loyalCustomer.getEtiquetasCategorias().isEmpty()){
			        	for(EtiquetaBean tag : loyalCustomer.getEtiquetasCategorias()){
			        		tagUids.add(tag.getUidEtiqueta());
			        	}
			        }
			        loyalCustomerData.setUidEtiquetas(tagUids);
			        loyalCustomerData.setNombre(loyalCustomer.getNombre());
			        loyalCustomerData.setApellido(loyalCustomer.getApellidos());
			        loyalCustomerData.setCodTipoIden(loyalCustomer.getCodTipoIden());
			        loyalCustomerData.setCp(loyalCustomer.getCp());
			        loyalCustomerData.setDocumento(loyalCustomer.getDocumento());
			        loyalCustomerData.setDomicilio(loyalCustomer.getDomicilio());
			        loyalCustomerData.setLocalidad(loyalCustomer.getLocalidad());
			        loyalCustomerData.setPoblacion(loyalCustomer.getPoblacion());
			        loyalCustomerData.setProvincia(loyalCustomer.getProvincia());
			        loyalCustomerData.setCodPais(loyalCustomer.getCodPais());
			        loyalCustomerData.setDesPais(loyalCustomer.getDesPais());
			        if(loyalCustomer.getSaldo() != null){
			        	loyalCustomerData.setSaldo(new BigDecimal(loyalCustomer.getSaldo()));
			        }
			        if(loyalCustomer.getSaldoProvisional() != null){
			        	loyalCustomerData.setSaldoProvisional(new BigDecimal(loyalCustomer.getSaldoProvisional()));
			        }
				}
			}
		} catch(Exception e) {
			log.warn("generateTicket() - No se ha podido consultar el fidelizado - "+ e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		} finally {
			ticket.getCabecera().setDatosFidelizado(loyalCustomerData);
		}
		
        
		ticket.setEntregasCuenta(new EntregasCuentaTicket());
        if (saleCreditTicket.getEntregasCuenta() != null) {
    		try {
    			BeanUtils.copyProperties(ticket.getEntregasCuenta(), saleCreditTicket.getEntregasCuenta());
    		} catch (Exception e) {
    			log.warn("generateTicket() - " + e.getClass().getName() + " - " + e.getLocalizedMessage());
    		}
    	}
                
        BigDecimal totalPrepayment = BigDecimal.ZERO;
        for(TransaccionCajaDetalleBean detail : transaction.getDetails()) {
        	boolean isPayment = false;
        	for(EntregaCuentaTicket prepayment : ticket.getEntregasCuenta().getEntregasCuenta()) {    		
        		if(detail.getUidTransaccionDet().equals(prepayment.getUidTransaccionDet())) {
        			totalPrepayment = totalPrepayment.add(prepayment.getImporte());
        			isPayment = true;
        			MedioPagoBean paymentMethod = mediosPagosService.getMedioPago(prepayment.getCodMedioPago());
        			if(paymentMethod.getTarjetaCredito() && (prepayment.getImporte().compareTo(detail.getImporte()) != 0)) {
        				prepayment.setImporte(detail.getImporte());
        			}
        			break;
        		}
        	}
        	if(!isPayment) {
        		EntregaCuentaTicket newPrepayment = new EntregaCuentaTicket();
        		MedioPagoBean paymentMethod = mediosPagosService.getMedioPago(detail.getCodmedpag());
        		newPrepayment.setCodMedioPago(paymentMethod.getCodMedioPago());   		
        		newPrepayment.setDesMedioPago(paymentMethod.getDesMedioPago());
        		newPrepayment.setImporte(detail.getImporte());
        		newPrepayment.setUidTransaccionDet(detail.getUidTransaccionDet());
        		ticket.getEntregasCuenta().getEntregasCuenta().add(newPrepayment);
        		totalPrepayment = totalPrepayment.add(newPrepayment.getImporte());
        	}
        }
        
        ticket.getTotales().setEntregadoACuenta(totalPrepayment);

        if (saleCreditTicket.getLineas() != null) {
        	Integer lineCounter = 1;
	        for (LineaTicket line : saleCreditTicket.getLineas()) {
				LineaTicket newLine = nuevaLineaArticulo(line.getCodArticulo(), line.getDesglose1(), line.getDesglose2(), line.getCantidad(), null);
				
				newLine.setDesArticulo(line.getDesArticulo());
				
				BigDecimal discount = line.getDescuento();
				if(BigDecimalUtil.isIgualACero(discount)) {
					discount = line.getDescuentoManual();
				}
				newLine.setDescuentoManual(discount);
				
				newLine.setPrecioTotalSinDto(line.getPrecioTotalSinDto());
				newLine.setPrecioSinDto(line.getPrecioSinDto());
				newLine.setAdmitePromociones(false);
				
				newLine.setEditable(false);
				newLine.setIdLinea(lineCounter);
				lineCounter++;
				
				DocumentoOrigen sourceDocumentLine = new DocumentoOrigen(saleCreditTicket.getUidTicket(), line.getIdLinea());
				sourceDocumentLine.setPromociones(line.getPromociones());
				newLine.setDocumentoOrigen(sourceDocumentLine);
				newLine.setUpdateStock(Boolean.FALSE);
			}
        }
        
        recalcularConPromociones();
        
        ticket.getTotales().setPuntos(saleCreditTicket.getTotales().getPuntos());
        
       
	}

}