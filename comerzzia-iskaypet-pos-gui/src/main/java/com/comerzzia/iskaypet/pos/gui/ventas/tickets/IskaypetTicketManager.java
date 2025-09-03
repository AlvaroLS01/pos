package com.comerzzia.iskaypet.pos.gui.ventas.tickets;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.servicios.tipodocumento.TipoDocumentoNotFoundException;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.core.util.tipoidentificacion.ValidadorDocumentoIdentificacionException;
import com.comerzzia.firma.pt.HashSaftPt;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ContextoValidadorCodigoPostal;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostal;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostalException;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostalFactory;
import com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables.InyectableArticuloManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.LoteArticuloManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.exception.ProformaLineasEmptyException;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.exception.ProformaPagosEmptyException;
import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteDTO;
import com.comerzzia.iskaypet.pos.persistence.colectivos.LocalColectivosMapper;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosMapper;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.fidelizados.LocalFidelizadosMapper;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.fidelizacion.ProformaFidelizacionBeanKey;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarAuditoriaLineaProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaLineaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaPagoBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.ProformaLineaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.lineas.auditorias.AuditoriaLineaProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.pagos.ProformaPagoBean;
import com.comerzzia.iskaypet.pos.persistence.ticket.lineas.DtoPromocion;
import com.comerzzia.iskaypet.pos.services.articulos.IskaypetArticulosService;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriasService;
import com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception.FidelizacionService;
import com.comerzzia.iskaypet.pos.services.cuponespuntos.CuponesPuntosService;
import com.comerzzia.iskaypet.pos.services.proformas.ProformaService;
import com.comerzzia.iskaypet.pos.services.proformas.exception.ProformaException;
import com.comerzzia.iskaypet.pos.services.proformas.exception.ProformaNotFoundException;
import com.comerzzia.iskaypet.pos.services.proformas.fidelizacion.ProformaFidelizadoService;
import com.comerzzia.iskaypet.pos.services.proformas.lineas.ProformaLineaService;
import com.comerzzia.iskaypet.pos.services.proformas.lineas.auditorias.AuditoriaLineaProformaService;
import com.comerzzia.iskaypet.pos.services.proformas.pagos.ProformaPagoService;
import com.comerzzia.iskaypet.pos.services.proformas.rest.ProformaRestService;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.cliente.ClienteDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.lineas.LineaProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.lineas.auditorias.AuditoriaLineaProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.pagos.PagoProformaDTO;
import com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos.IskaypetPromocionPuntosBean;
import com.comerzzia.iskaypet.pos.services.promotionsticker.PromotionStickerService;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketService;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.iskaypet.pos.services.ticket.aparcados.IskaypetTicketsAparcadosService;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales.DatosOrigenTicketBean;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales.ProformaXmlBean;
import com.comerzzia.iskaypet.pos.services.ticket.contrato.trazabilidad.TrazabilidadMascotasService;
import com.comerzzia.iskaypet.pos.services.ticket.cupones.IskaypetCuponEmitidoTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.canjeoPuntos.ArticlesPointsXMLBean;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.inyectables.Inyectable;
import com.comerzzia.iskaypet.pos.util.date.DateUtils;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.BalanzaNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.IBalanza;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.ConsultaTarjetaFidelizadoException;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.fidelizacion.Fidelizacion;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloView;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.ConfigContadorBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.parametros.ConfigContadorParametroBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.persistence.tickets.datosfactura.DatosFactura;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesServices;
import com.comerzzia.pos.services.core.config.configContadores.ServicioConfigContadores;
import com.comerzzia.pos.services.core.config.configContadores.rangos.CounterRangeManager;
import com.comerzzia.pos.services.core.config.configContadores.rangos.CounterRangeParamDto;
import com.comerzzia.pos.services.core.config.configContadores.rangos.ServicioConfigContadoresRangos;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.services.core.usuarios.UsuarioNotFoundException;
import com.comerzzia.pos.services.core.usuarios.UsuariosService;
import com.comerzzia.pos.services.core.usuarios.UsuariosServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.cupones.CuponAplicationException;
import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.fiscaldata.FiscalDataException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.*;
import com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket;
import com.comerzzia.pos.services.ticket.cabecera.FirmaTicket;
import com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.lineas.*;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GAP 107 - ISK-262 GLOVO (venta por plataforma digital)
 */
@Component
@Scope("prototype")
@Primary
@SuppressWarnings({"unchecked", "rawtypes"})
public class IskaypetTicketManager extends TicketManager {

    /* GAP 63 - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN */
    public static final String UID_TICKET_FALSO = "DEVOLUCION_ISKAYPET";
    public static final String VARIABLE_DEVOLUCION_SIN_ORIGEN = "POS.X_DEVOLUCION_SIN_ORIGEN";
    public static final String VARIABLE_FAMILIA_PLANES_DE_SALUD = "X_POS.FAMILIA_PLANES_DE_SALUD";

    private static final String PROPIEDADES_IMPORTE_MAXIMO_SERVICIOS = "IMPORTE_MAXIMO_SERVICIOS";
    private static final String STARTS_PREFIX_SERVICIOS = "SER";

    // GAP 49 APLICACIÓN EN POS: PROMO PUNTOS CON DECIMALES
    @Autowired
    private SesionPromociones sesionPromociones;
    // @Autowired
    // private IskaypetPromocionPuntosBean promocionPuntos;

    protected ITicket ticketConsultaPromociones;
    @Autowired
    private VariablesServices variablesServices;

    @Autowired
    protected CodBarrasEspecialesServices codBarrasEspecialesServices;
    @Autowired
    protected LineasTicketServices lineasTicketServices;
    @Autowired
    protected TicketsService ticketsService;
    @Autowired
    protected CuponesPuntosService cuponesPuntosService;
    @Autowired
    private MediosPagosService mediosPagosService;
    @Autowired
    private PaymentsMethodsConfiguration paymentsMethodsConfiguration;

    // GAP62 - PEGATINAS PROMOCIONALES
    @Autowired
    protected PromotionStickerService promotionStickerService;

    @Autowired
    protected SesionImpuestos sesionImpuestos;

    @Autowired
    protected ServicioConfigContadores servicioConfigContadores;

    @Autowired
    protected ServicioConfigContadoresRangos servicioConfigContadoresRangos;

    @Autowired
    protected ServicioContadores servicioContadores;

    @Autowired
    protected CounterRangeManager counterRangeManager;

    public static final String EXTENSION_RANGE_ID = "RANGE_ID";

    //GAP 172 TRAZABILIDAD ANIMALES
    @Autowired
    protected TrazabilidadMascotasService trazabilidadMascotasService;

    @Autowired
    protected IskaypetArticulosService articulosService;

    @Autowired
    private IskaypetTicketsAparcadosService ticketsAparcadosService;

    @Autowired
    private UsuariosService usuariosService;

    @Autowired
    private LocalFidelizadosMapper localFidelizadosMapper;

    @Autowired
    private LocalFidelizadosColectivosMapper localFidelizadosColectivosMapper;

    @Autowired
    private LocalColectivosMapper localColectivosMapper;

    @Autowired
    private IskaypetTicketService iskaypetTicketService;

    // Flag to block UI resets while a ticket is being persisted
    private final AtomicBoolean registrandoTicket = new AtomicBoolean(false);

    @Autowired
    private LoteArticuloManager loteArticuloManager;

    @Autowired
    private TiposIdentService tiposIdentService;

    @Autowired
    private InyectableArticuloManager inyectableArticuloManager;

    @Autowired
    protected ProformaService proformaService;

    @Autowired
    protected ProformaFidelizadoService proformaFidelizadoService;

    @Autowired
    protected FidelizacionService fidelizacionService;

    @Autowired
    protected ProformaLineaService proformaLineaService;

    @Autowired
    protected AuditoriaLineaProformaService auditoriaLineaProformaService;

    @Autowired
    protected ProformaPagoService proformaPagoService;

    @Override
    public List<Class<?>> getTicketClasses(TipoDocumentoBean tipoDocumento) {
        List<Class<?>> clases = super.getTicketClasses(tipoDocumento);
        clases.add(TicketVentaAbono.class);
        clases.add(IskaypetCabeceraTicket.class);
        clases.add(IskaypetLineaTicket.class);
        clases.add(IskaypetCuponEmitidoTicket.class);
        return clases;
    }

    public boolean comprobarMascota(LineaTicket linea) {
        BigDecimal valor = linea.getArticulo().getCantidadUmEtiqueta();
        return valor == null;
    }

    @Override
    public void recuperarTicket(Stage stage, TicketAparcadoBean ticketAparcado) throws TicketsServiceException, PromocionesServiceException, DocumentoException, LineaTicketException {
        log.debug("recuperarTicket() - Recuperando ticket...");

        nuevoTicket();
        // Realizamos el unmarshall
        log.debug("Ticket recuperado:\n" + new String(ticketAparcado.getTicket()));
        TicketVenta ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(ticketAparcado.getTicket(), getTicketClasses(documentoActivo).toArray(new Class[]{}));

        ticketPrincipal.getCabecera().setIdTicket(ticketRecuperado.getIdTicket());
        ticketPrincipal.getCabecera().setUidTicket(ticketRecuperado.getUidTicket());
        ticketPrincipal.getCabecera().setCodTicket(ticketRecuperado.getCabecera().getCodTicket());
        ticketPrincipal.getCabecera().setSerieTicket(ticketRecuperado.getCabecera().getSerieTicket());

        String tipoDocumentoFacturaDirecta = getDocumentoActivo().getTipoDocumentoFacturaDirecta();
        if (ticketRecuperado.getCabecera().getCodTipoDocumento().equals(tipoDocumentoFacturaDirecta)) {
            setDocumentoActivo(sesion.getAplicacion().getDocumentos().getDocumento(tipoDocumentoFacturaDirecta));
        }

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
            } else {
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
        if (datosFidelizado != null) {
            try {
                FidelizacionBean tarjetaFidelizado = null;
                Fidelizacion fidelizacion = (Fidelizacion) Dispositivos.getInstance().getFidelizacion();
                if (fidelizacion instanceof IskaypetFidelizacion) {
                    tarjetaFidelizado = ((IskaypetFidelizacion) fidelizacion).consultarTarjetaFidelizado(stage, datosFidelizado.getNumTarjetaFidelizado(), ticketPrincipal.getCabecera().getUidActividad(),
                            localFidelizadosMapper, localFidelizadosColectivosMapper, localColectivosMapper);
                } else {
                    tarjetaFidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(stage, datosFidelizado.getNumTarjetaFidelizado(), ticketPrincipal.getCabecera().getUidActividad());
                }

                ticketPrincipal.getCabecera().setDatosFidelizado(tarjetaFidelizado);
            } catch (ConsultaTarjetaFidelizadoException e) {
                log.debug("recuperarTicket() - Error al consultar fidelizado", e);
                FidelizacionBean fidelizacionBean = new FidelizacionBean();
                fidelizacionBean.setNumTarjetaFidelizado(datosFidelizado.getNumTarjetaFidelizado());
                ticketPrincipal.getCabecera().setDatosFidelizado(fidelizacionBean);
            }
        }

        // Se recuperan los datos personalizados
        recuperarDatosPersonalizados(ticketRecuperado);

        for (PagoTicket pago : (List<PagoTicket>) ticketRecuperado.getPagos()) {
            pago.setMedioPago(mediosPagosService.getMedioPago(pago.getCodMedioPago()));
            ticketPrincipal.getPagos().add(pago);
        }

        recalcularConPromociones();

        // Establecemos el contador
        contadorLinea = ticketPrincipal.getLineas().size() + 1;
        //Eliminamos el ticket recuperado de la lista de tickets aparcados.
        ticketsAparcadosService.eliminarTicket(ticketAparcado.getUidTicket());

    }

    @Override
    protected void recuperarDatosPersonalizados(TicketVenta ticketRecuperado) {
        super.recuperarDatosPersonalizados(ticketRecuperado);

        // GAP46 - CANJEO ARTÍCULOS POR PUNTOS
        if (ticketPrincipal.getCabecera().getDatosFidelizado() != null) {
            for (IskaypetLineaTicket lineaOrigen : (List<IskaypetLineaTicket>) ticketRecuperado.getLineas()) {
                ArticlesPointsXMLBean articlePoints = lineaOrigen.getArticlePoints();
                if (articlePoints != null && ArticlesPointsXMLBean.VALUE_REEDEM_OK.equalsIgnoreCase(articlePoints.getReedem())) {
                    ticketPrincipal.getCabecera().getDatosFidelizado().setSaldo(ticketPrincipal.getCabecera().getDatosFidelizado().getSaldo().subtract(articlePoints.getPoints()));
                }
            }
        }

        // Recuperamos los datos de la cabecera del ticket recuperado
        if (ticketRecuperado.getCabecera() instanceof IskaypetCabeceraTicket && ticketPrincipal.getCabecera() instanceof IskaypetCabeceraTicket) {
            // Obtenemos los datos de la cabecera del ticket recuperado
            IskaypetCabeceraTicket cabeceraRecuperada = (IskaypetCabeceraTicket) ticketRecuperado.getCabecera();
            IskaypetCabeceraTicket cabeceraPrincipal = (IskaypetCabeceraTicket) ticketPrincipal.getCabecera();

            // ISK-262 GAP-107 GLOVO
            cabeceraPrincipal.setDelivery(cabeceraRecuperada.getDelivery());

            // CZZ-115 - APARCAR TICKET
            cabeceraPrincipal.setTicketAparcado(cabeceraRecuperada.getTicketAparcado());

            // Proforma
            cabeceraPrincipal.setProforma(cabeceraRecuperada.getProforma());
        }

        // Recuperamos los cupones aplicados en el ticket aparcado y los aplicamos en el ticket principal
        if (ticketRecuperado.getPromociones() != null && !ticketRecuperado.getPromociones().isEmpty()) {
            List<PromocionTicket> cupones = (((List<PromocionTicket>) ticketRecuperado.getPromociones()).stream().filter(el -> el.getAcceso().equalsIgnoreCase("CUPON")).collect(Collectors.toList()));
            for (PromocionTicket cupon : cupones) {
                CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO(cupon.getCodAcceso(), true);
                try {
                    sesionPromociones.aplicarCupon(customerCouponDTO, (TicketVentaAbono) ticketPrincipal);
                } catch (Exception e) {
                    log.error("recuperarDatosPersonalizados() - Error en la aplicación del cupón -" + e.getMessage());
                }
            }
        }
        //Recuper transaccion pendiente de sipay
        if (((IskaypetCabeceraTicket) ticketRecuperado.getCabecera()).getTransactionsSipay() != null) {
            ((IskaypetCabeceraTicket) ticketPrincipal.getCabecera()).setTransactionsSipay(((IskaypetCabeceraTicket) ticketRecuperado.getCabecera()).getTransactionsSipay());
        }

    }

    @Override
    protected void recuperarDatosPersonalizadosLinea(LineaTicket lineaRecuperada, LineaTicket nuevaLineaArticulo) {

        ((IskaypetLineaTicket) nuevaLineaArticulo).setTextosPromociones(((IskaypetLineaTicket) lineaRecuperada).getTextosPromociones());

        // GAP46 - CANJEO ARTÍCULOS POR PUNTOS
        ((IskaypetLineaTicket) nuevaLineaArticulo).setArticlePoints(((IskaypetLineaTicket) lineaRecuperada).getArticlePoints());

        // GAP 12 - ISK-8 GESTIÓN DE LOTES
        ((IskaypetLineaTicket) nuevaLineaArticulo).setLote(((IskaypetLineaTicket) lineaRecuperada).getLote());

        // GAP62 - PEGATINAS PROMOCIONALES
        ((IskaypetLineaTicket) nuevaLineaArticulo).setPegatinaPromocional(((IskaypetLineaTicket) lineaRecuperada).getPegatinaPromocional());

        // GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
        ((IskaypetLineaTicket) nuevaLineaArticulo).setAuditorias(((IskaypetLineaTicket) lineaRecuperada).getAuditorias());

        ((IskaypetLineaTicket) nuevaLineaArticulo).setContratoAnimal(((IskaypetLineaTicket) lineaRecuperada).getContratoAnimal());

        //GAP 172 TRAZABILIDAD ANIMALES
        ((IskaypetLineaTicket) nuevaLineaArticulo).setDetallesTrazabilidad(((IskaypetLineaTicket) lineaRecuperada).getDetallesTrazabilidad());
        ((IskaypetLineaTicket) nuevaLineaArticulo).setRequiereContrato(((IskaypetLineaTicket) lineaRecuperada).isRequiereContrato());
        ((IskaypetLineaTicket) nuevaLineaArticulo).setRequiereIdentificacion(((IskaypetLineaTicket) lineaRecuperada).isRequiereIdentificacion());
    }

    public Integer getContadorLineas() {
        return this.contadorLinea;
    }

    public void setContadorLineas(Integer contadorLinea) {
        this.contadorLinea = contadorLinea;
    }

    public void inicializarTicketConsultaPromocion() throws DocumentoException, PromocionesServiceException {
        log.debug("inicializarTicketConsultaPromocion() - Creando nuevo ticket para consulta promociones con valores iniciales...");
        documentoActivo = getNuevoDocumentoActivo();
        ticketConsultaPromociones = SpringContext.getBean(getTicketClass(documentoActivo));

        // crearTicket
        ticketConsultaPromociones.getCabecera().inicializarCabecera(ticketConsultaPromociones);
        ((TicketVentaAbono) ticketConsultaPromociones).inicializarTotales();
        ticketConsultaPromociones.setCliente(sesion.getAplicacion().getTienda().getCliente().clone());
        ticketConsultaPromociones.setCajero(sesion.getSesionUsuario().getUsuario());
        ticketConsultaPromociones.getCabecera().getTotales().setCambio(SpringContext.getBean(PagoTicket.class, MediosPagosService.medioPagoDefecto));
        ticketConsultaPromociones.getTotales().recalcular();

        // Establecemos los parámetros de tipo de documento del ticket
        cambiarTipoDocumentoConsultaPromocion(documentoActivo);

        // Actualizamos promociones activas
        sesionPromociones.actualizarPromocionesActivas();

        devolucionTarjetaRegalo = false;
        esRecargaTarjetaRegalo = false;
        esOperacionTarjetaRegalo = false;
        esDevolucion = false;
        esFacturacionVentaCredito = false;
        ticketConsultaPromociones.setEsDevolucion(false);

    }

    public synchronized LineaTicket nuevaLineaArticuloConsultaPromocion(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen,
                                                                        boolean esLineaDevolucionPositiva, boolean applyDUN14Factor) throws LineaTicketException {
        log.debug("nuevaLineaArticuloConsultaPromocion() - Creando nueva línea de artículo...");
        LineaTicketAbstract linea = null;

        boolean isCupon = sesionPromociones.isCouponWithPrefix(codArticulo);
        if (isCupon) {
            try {
                CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO(codArticulo, true);
                if (!sesionPromociones.aplicarCupon(customerCouponDTO, (TicketVentaAbono) ticketConsultaPromociones)) {
                    throw new LineaTicketException(I18N.getTexto("No se ha podido aplicar el cupón."));
                }
                ticketConsultaPromociones.getTotales().recalcular();
            } catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
                log.warn("nuevaLineaArticuloConsultaPromocion() - Error en la aplicación del cupón -" + ex.getMessageI18N());
                throw new LineaTicketException(ex.getMessageI18N(), ex);
            }
        } else {
            BigDecimal precio = null;

            boolean pesarArticulo = stage != null;

            String codBarras = null;

            if (!esDevolucion) {
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
                        } else {
                            cantidad = BigDecimal.ONE;
                        }
                        String precioCodBar = codBarrasEspecial.getPrecio();
                        if (precioCodBar != null) {
                            precio = FormatUtil.getInstance().desformateaBigDecimal(codBarrasEspecial.getPrecio(), 2);
                        } else {
                            precio = null;
                        }

                        if (codArticulo == null) {
                            log.error(String.format("nuevaLineaArticuloConsultaPromocion() - El código de barra especial obtenido no es válido. CodArticulo: %s, cantidad: %s, precio: %s", codArticulo,
                                    cantidad, precio));
                            throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
                        }
                    }
                } catch (LineaTicketException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("Error procesando el código de barras especial : " + codArticulo, e);
                    throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
                }
            }

            try {
                linea = lineasTicketServices.createLineaArticulo((TicketVenta) ticketConsultaPromociones, codArticulo, desglose1, desglose2, cantidad, precio, createLinea(), applyDUN14Factor);
                comprobarCantidadUnitaria(linea);
                linea.setCantidad(tratarSignoCantidad(linea.getCantidad(), linea.getCabecera().getCodTipoDocumento()));
                if (esLineaDevolucionPositiva) {
                    linea.setCantidad(linea.getCantidad().abs());
                }

                if (codBarras != null) {
                    linea.setCodigoBarras(codBarras);
                }

                // Si el artículo tiene en su campo FORMATO en BBDD...
                if (pesarArticulo && StringUtils.isNotBlank(linea.getArticulo().getBalanzaTipoArticulo()) && linea.getArticulo().getBalanzaTipoArticulo().trim().equalsIgnoreCase(PESAR_ARTICULO)) {
                    IBalanza balanza = Dispositivos.getInstance().getBalanza();
                    if (!(balanza instanceof BalanzaNoConfig)) {
                        HashMap<String, Object> params = new HashMap<>();
                        POSApplication.getInstance().getMainView().showModalCentered(SolicitarPesoArticuloView.class, params, stage);
                        if (params.containsKey(SolicitarPesoArticuloController.PARAM_PESO)) {
                            BigDecimal peso = (BigDecimal) params.get(SolicitarPesoArticuloController.PARAM_PESO);

                            if (peso == null || BigDecimalUtil.isMenorOrIgualACero(peso)) {
                                throw new LineaTicketException(I18N.getTexto("No se ha podido pesar el artículo, compruebe la configuración de la balanza."));
                            }

                            linea.setCantidad(peso);
                        } else {
                            throw new LineaTicketException(I18N.getTexto("Este artículo no puede ser introducido sin ser pesado previamente."));
                        }
                    }
                }

                if (esDevolucion && ticketOrigen != null && !esLineaDevolucionPositiva) {
                    if (idLineaDocOrigen == null) {
                        idLineaDocOrigen = getIdLineaTicketOrigen(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), linea.getCantidad().abs());
                    }
                    LineaTicketAbstract lineaOrigen = ticketOrigen.getLinea(idLineaDocOrigen);
                    lineaOrigen.setPrecioTotalConDto(
                            lineaOrigen.getImporteTotalConDto().setScale(6, RoundingMode.HALF_UP).divide(lineaOrigen.getCantidad().setScale(6, RoundingMode.HALF_UP), RoundingMode.HALF_UP));
                    linea.resetPromociones();
                    linea.setPrecioSinDto(lineaOrigen.getPrecioConDto());
                    linea.setPrecioTotalSinDto(lineaOrigen.getPrecioTotalConDto());
                    linea.recalcularImporteFinal();

                    linea.setLineaDocumentoOrigen(lineaOrigen.getIdLinea());

                    actualizarCantidadesOrigenADevolver(lineaOrigen, lineaOrigen.getCantidadADevolver().add(linea.getCantidad().abs()));
                }

                linea.setIdLinea(contadorLinea);
                ((TicketVentaAbono) ticketConsultaPromociones).addLinea((LineaTicket) linea);
                contadorLinea++;
                ticketConsultaPromociones.getTotales().recalcular();
            } catch (ArticuloNotFoundException e) {
                linea = null;

                try { // Si no se ha encontrado artículo, intentamos aplicar cupón
                    CustomerCouponDTO coupon = new CustomerCouponDTO(codArticulo, false);

                    isCupon = sesionPromociones.aplicarCupon(coupon, (TicketVentaAbono) ticketConsultaPromociones);
                    if (!isCupon) { // Si el código no es de un cupón válido,
                        // lanzamos excepción de artículo no encontrado
                        log.warn("nuevaLineaArticuloConsultaPromocion() - Artículo no encontrado " + codArticulo);
                        throw new LineaTicketException(e.getMessageI18N());
                    }
                    ticketConsultaPromociones.getTotales().recalcular();

                } // Si tenemos excepción durante la aplicación del cupón, lanzamos
                // excepción indicativa
                catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
                    log.warn("nuevaLineaArticuloConsultaPromocion() - Error en la aplicación del cupón -" + ex.getMessageI18N());
                    throw new LineaTicketException(ex.getMessageI18N(), e);
                }
            }
        }
        return (LineaTicket) linea;
    }

    @Override
    public void recalcularConPromociones() {
        BigDecimal puntosAnteriores = BigDecimal.ZERO;
        if (isEsFacturacionVentaCredito()) {
            puntosAnteriores = ticketPrincipal.getTotales().getPuntos();
        }
        sesionPromociones.aplicarPromociones((TicketVentaAbono) ticketPrincipal);
        ticketPrincipal.getTotales().recalcular();
        ticketPrincipal.getTotales().addPuntos(puntosAnteriores);
        super.recalcularConPromociones();
    }

    public void recalcularConPromocionesConsulta() {
        BigDecimal puntosAnteriores = BigDecimal.ZERO;
        if (isEsFacturacionVentaCredito()) {
            puntosAnteriores = ticketConsultaPromociones.getTotales().getPuntos();
        }
        sesionPromociones.aplicarPromociones((TicketVentaAbono) ticketConsultaPromociones);
        ticketConsultaPromociones.getTotales().recalcular();
        ticketConsultaPromociones.getTotales().addPuntos(puntosAnteriores);
    }

    @Override
    public boolean comprobarImporteMaximoOperacion(Stage stage) {
        BigDecimal importeMaximoServicios = null;
        Map<String, PropiedadDocumentoBean> propiedades = documentoActivo.getPropiedades();
        if (propiedades.containsKey(PROPIEDADES_IMPORTE_MAXIMO_SERVICIOS)) {
            String importeMax = propiedades.get(PROPIEDADES_IMPORTE_MAXIMO_SERVICIOS).getValor();
            if (importeMax != null && !importeMax.isEmpty()) {
                importeMaximoServicios = new BigDecimal(importeMax);
            }
        }

        if (importeMaximoServicios != null) {
            Boolean tieneServicios = tieneServiciosLineas(ticketPrincipal.getCabecera().getTicket().getLineas());

            if (tieneServicios) {
                if (BigDecimalUtil.isMayorOrIgual(ticketPrincipal.getCabecera().getTotales().getTotalAPagar(), importeMaximoServicios)) {
                    VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El total de la venta con servicios es superior a {0}€, es obligatorio sacar factura completa",
                            new Object[]{FormatUtil.getInstance().formateaImporte(importeMaximoServicios)}), stage);
                    return false;
                }
            }

        }

        BigDecimal importeMaximo = this.documentoActivo.getImporteMaximoSinImpuestos();
        if (importeMaximo != null) {
            if (BigDecimalUtil.isMayorOrIgual(this.ticketPrincipal.getCabecera().getTotales().getBase(), importeMaximo)) {
                VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El total de la venta sin impuestos es superior a  {0}€, es obligatorio sacar factura completa",
                        new Object[]{FormatUtil.getInstance().formateaImporte(importeMaximo)}), stage);
                return false;
            }
        } else {
            importeMaximo = this.documentoActivo.getImporteMaximo();
            if (importeMaximo != null && BigDecimalUtil.isMayorOrIgual(this.ticketPrincipal.getCabecera().getTotales().getTotalAPagar(), importeMaximo)) {
                VentanaDialogoComponent.crearVentanaError(
                        I18N.getTexto("El total de la venta es superior a {0}€, es obligatorio sacar factura completa", FormatUtil.getInstance().formateaImporte(importeMaximo)), stage);
                return false;
            }
        }

        return true;
    }

    public boolean tieneServiciosLineas(List<IskaypetLineaTicket> lista) {
        log.debug("tieneServiciosLineas() - Comprobando si hay servicios en la línea...");
        for (IskaypetLineaTicket linea : lista) {
            if (StringUtils.isNotBlank(linea.getArticulo().getCodFamilia()) && linea.getArticulo().getCodFamilia().startsWith(STARTS_PREFIX_SERVICIOS)) {
                log.debug("tieneServiciosLineas() - Se ha encontrado un servicio en la línea: " + linea.getIdLinea());
                return true;
            }
        }
        log.debug("tieneServiciosLineas() - No se ha encontrado ningún servicio en la línea.");
        return false;
    }

    public boolean tieneMedicamentosLineas(List<IskaypetLineaTicket> lista) {
        log.debug("tieneMedicamentosLineas() - Comprobando si hay medicamentos en la línea...");
        for (LineaTicket linea : lista) {
            if (loteArticuloManager.esArticuloMedicamento(linea.getCodArticulo())) {
                log.debug("tieneMedicamentosLineas() - Se ha encontrado un medicamento en la línea: " + linea.getIdLinea());
                return true;
            }
        }
        log.debug("tieneMedicamentosLineas() - No se ha encontrado ningún medicamento en la línea.");
        return false;
    }

    public boolean tieneMedicamentosConRecetaLineas(List<IskaypetLineaTicket> lista) {
        log.debug("tieneMedicamentosLineas() - Comprobando si hay medicamentos con receta en la línea...");
        for (LineaTicket linea : lista) {
            if (loteArticuloManager.esMedicamentoConReceta(linea.getCodArticulo())) {
                log.debug("tieneMedicamentosLineas() - Se ha encontrado un medicamento con receta en la línea: " + linea.getIdLinea());
                return true;
            }
        }
        log.debug("tieneMedicamentosLineas() - No se ha encontrado ningún medicamento en la línea.");
        return false;
    }

    public boolean esMascotaLinea(List<IskaypetLineaTicket> lista){
        log.debug("esMascotaLinea() - Comprobando si hay mascotas en la línea...");
        for (IskaypetLineaTicket linea : lista) {
            if (linea.isMascota()) {
                log.debug("esMascotaLinea() - Se ha encontrado una mascota en la línea: " + linea.getIdLinea());
                return true;
            }
        }
        log.debug("esMascotaLinea() - No se ha encontrado ninguna mascota en la línea.");
        return false;
    }


    @Override
    public void cambiarTipoDocumento(TipoDocumentoBean nuevoTipo) {
        log.debug("cambiarTipoDocumento() - Se va a cambiar el tipo de documento del ticket al tipo de documento (" + nuevoTipo.getCodtipodocumento() + ") " + nuevoTipo.getDestipodocumento());
        boolean tieneIdTicket = ticketPrincipal.getIdTicket() != null;
        if (tieneIdTicket) {
            //Deshacemos el contador y seteamos a null los IDs
            iskaypetTicketService.deshacerContadorIdTicket(ticketPrincipal, documentoActivo);
        }
        ticketPrincipal.getCabecera().setDocumento(nuevoTipo);
    }

    public void cambiarTipoDocumentoConsultaPromocion(TipoDocumentoBean nuevoTipo) {
        log.debug("cambiarTipoDocumentoConsultaPromocion() - Se va a cambiar el tipo de documento del ticket al tipo de documento (" + nuevoTipo.getCodtipodocumento() + ") "
                + nuevoTipo.getDestipodocumento());

        boolean tieneIdTicket = ticketConsultaPromociones.getIdTicket() != null;

        if (tieneIdTicket) {
            TipoDocumentoBean tipoAntiguo = null;
            try {
                tipoAntiguo = documentos.getDocumento(ticketConsultaPromociones.getCabecera().getTipoDocumento());
            } catch (Exception e) {
                log.error("cambiarTipoDocumentoConsultaPromocion() - Ha habido un error al buscar el tipo de documento actual: " + e.getMessage(), e);
                log.error("cambiarTipoDocumentoConsultaPromocion() - Para deshacer el contador se usará el tipo de documento nuevo.");
            }

            //Deshacemos el contador y seteamos a null los IDs
            iskaypetTicketService.deshacerContadorIdTicket(ticketConsultaPromociones, tipoAntiguo != null ? tipoAntiguo : nuevoTipo);

            //iskaypetTicketService.saveEmptyTicket(ticketConsultaPromociones, tipoAntiguo, null);
        }

        ticketConsultaPromociones.getCabecera().setDocumento(nuevoTipo);

        if (tieneIdTicket) {
            try {
                log.debug("cambiarTipoDocumentoConsultaPromocion() - Se va a asignar el ID_TICKET ya que el ticket lo tenía asignado antes del cambio de tipo de documento.");
                ticketsService.setContadorIdTicket((Ticket) ticketConsultaPromociones);
                log.debug("cambiarTipoDocumentoConsultaPromocion() - Contador obtenido: " + ticketConsultaPromociones.getIdTicket());
            } catch (Exception e) {
                log.error("cambiarTipoDocumentoConsultaPromocion() - Ha habido un error al obtener el contador del ticket.");
            }
        }
    }

    public ITicket getTicketConsultaPromociones() {
        return ticketConsultaPromociones;
    }

    @Override
    public void salvarTicketSeguridad(Stage stage, SalvarTicketCallback callback) {
        new SalvarTicketSeguridadIskaypetTask(stage, callback).start();
    }

    protected class SalvarTicketSeguridadIskaypetTask extends SalvarTicketTask {

        public SalvarTicketSeguridadIskaypetTask(Stage stage, SalvarTicketCallback callback) {
            super(stage, callback);
        }

        protected Stage stage;
        protected SalvarTicketCallback callback;

        @Override
        protected Void call() throws Exception {

            List<CuponEmitidoTicket> lstCuponEmitidosDevolucion = null;
            if (!isTicketVentaDelivery()) {
                //GAP 169 DEVOLUCIÓN DE PROMOCIONES
                //Antes de aplicar las finales, si existen cupones de devoluciones los guardamos en una lista a parte para luego setearlo al ticket
                if (isEsDevolucion() && ((TicketVentaAbono) ticketPrincipal).getCuponesEmitidos() != null && !((TicketVentaAbono) ticketPrincipal).getCuponesEmitidos().isEmpty()) {
                    lstCuponEmitidosDevolucion = new ArrayList<CuponEmitidoTicket>();
                    for (CuponEmitidoTicket cupon : ((TicketVentaAbono) ticketPrincipal).getCuponesEmitidos()) {
                        if (cupon.getTipoCupon().equals("DEVOLUCION")) {
                            lstCuponEmitidosDevolucion.add(cupon);
                        }
                    }
                }

                if (!isEsDevolucion()) {
                    sesionPromociones.aplicarPromocionesFinales((TicketVentaAbono) ticketPrincipal);
                }

                //GAP 169 DEVOLUCIÓN DE PROMOCIONES
                //Setemos la lista de cupones de devolucion guardada para el ticket
                if (lstCuponEmitidosDevolucion != null && !lstCuponEmitidosDevolucion.isEmpty()) {
                    for (CuponEmitidoTicket cuponEmitidoTicket : lstCuponEmitidosDevolucion) {
                        //Devolvemos el valor DEFAULT para el procesador
                        cuponEmitidoTicket.setTipoCupon("DEFAULT");
                        ((TicketVentaAbono) ticketPrincipal).addCuponEmitido(cuponEmitidoTicket);
                    }
                }

                sesionPromociones.generarCuponesDtoFuturo(ticketPrincipal);

                ticketPrincipal.getTotales().recalcular();

                // ISK-216 GAP 50b Generación de cupones en POS
                if (!ticketPrincipal.isEsDevolucion() && ticketPrincipal.getCabecera().getDatosFidelizado() != null) {
                    cuponesPuntosService.gestionCuponesPuntosFidelizado(ticketPrincipal);
                    cuponesPuntosService.gestionCuponesPorDiferenciaFidelizado(ticketPrincipal);
                }
            }
            String uidTicket = ticketPrincipal.getUidTicket();
            if (ticketPrincipal.getIdTicket() == null) {
                ticketsService.setContadorIdTicket((Ticket) ticketPrincipal);
                ticketPrincipal.getCabecera().setUidTicket(uidTicket);
            }
            log.debug("SalvarTicketSeguridadIskaypetTask() - Guardando ticket con nº de líneas = " + ticketPrincipal.getLineas().size() + " e importe entregado = " + ticketPrincipal.getCabecera().getTotales().getEntregadoAsString());
            guardarCopiaSeguridadTicket();

            log.debug("SalvarTicketSeguridadIskaypetTask() - Contador obtenido: idTicket = " + ticketPrincipal.getIdTicket());

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

    // Sobrescribimos este método para que no se procese ningún ticket desde aquí (estaba dando problemas por los cupones)
    @Override
    protected BackgroundTask<Void> crearClaseRegistrarTicketTask(Stage stage, SalvarTicketCallback callback, List<DatosRespuestaPagoTarjeta> datosRespuesta) {
        return new RegistrarTicketIskaypetTask(stage, callback, datosRespuesta);
    }

    protected class RegistrarTicketIskaypetTask extends BackgroundTask<Void> {

        protected Stage stage;
        protected SalvarTicketCallback callback;
        protected List<DatosRespuestaPagoTarjeta> pagosAutorizados;

        public RegistrarTicketIskaypetTask(Stage stage, SalvarTicketCallback callback, List<DatosRespuestaPagoTarjeta> pagosAutorizados) {
            this.stage = stage;
            this.callback = callback;
            this.pagosAutorizados = pagosAutorizados;
        }

        @Override
        protected Void call() throws Exception {
            if (esOperacionTarjetaRegalo) {
                procesarTarjetaRegalo(stage);
            }

            redondearImportesTicket();

            // boolean processTicket = esDevolucion || esFacturacionVentaCredito ||
            // !ticketPrincipal.getCuponesAplicados().isEmpty();
            boolean processTicket = false;

            registrandoTicket.set(true);
            try {
                ticketsService.registrarTicket((Ticket) ticketPrincipal, documentoActivo, processTicket);
                confirmarPagosTarjeta(pagosAutorizados, stage);
            } finally {
                registrandoTicket.set(false);
            }

            return null;
        }

        @Override
        protected void failed() {
            super.failed();
            Exception ex = (Exception) getException();

            log.error("salvarTicket() Error salvando ticket : " + ex.getMessage(), ex);

            anularPagos(pagosAutorizados, stage);
            anularPromocionesFinales(stage);

            if (ticketPrincipal.getIdTicket() != null) {
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

    @Override
    public void salvarTicketVacio() {
        iskaypetTicketService.deshacerContadorIdTicket(ticketPrincipal, documentoActivo);
    }

    // ISK-199 GAP 91Devoluciones-Vale de devolucion
    @Override
    public PagoTicket nuevaLineaPago(String codigo, BigDecimal importe, boolean modificable, boolean eliminable, Integer paymentId, boolean introducidoPorCajero) {
        log.debug("nuevaLineaPago() - Creando nueva línea de pago...");

        MedioPagoBean medioPago = mediosPagosService.getMedioPago(codigo);

        if (medioPago == null) {
            throw new IllegalArgumentException("El medio de pago con código " + codigo + " no existe.");
        }

        PagoTicket pago = SpringContext.getBean(PagoTicket.class, medioPago);
        boolean creaNuevaTarjetaValeDev = true;

        String codMedPagVale = "";
        for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
            if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getControlClass().equals("valeDevManager")) {
                codMedPagVale = configuration.getPaymentCode();
            }
        }

        /*
         * Si es una devolución y el medio de pago ha sido vale de devolución, comprobamos si ya hay insertada una linea con ese
         * medio de pago para saber si crear una nueva tarjeta o sumarle el importe a la anterior creada
         */
        if (ticketPrincipal.isEsDevolucion() && (StringUtils.isNotBlank(codMedPagVale) && codMedPagVale.equals(codigo))) {
            for (PagoTicket pagoTicket : (List<PagoTicket>) ticketPrincipal.getPagos()) {
                if (pagoTicket.getCodMedioPago().equals(codigo)) {
                    creaNuevaTarjetaValeDev = false;
                    pago = pagoTicket;
                }
            }
        }

        if (introducidoPorCajero && ticketPrincipal.getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO) < 0) {
            importe = importe.negate();
        }

        if (creaNuevaTarjetaValeDev) {
            pago.setEliminable(eliminable);
            ((TicketVenta) ticketPrincipal).addPago(pago);

            pago.setImporte(importe);

            if (paymentId != null) {
                pago.setPaymentId(paymentId);
            }

            pago.setIntroducidoPorCajero(introducidoPorCajero);
        } else {
            pago.setImporte(pago.getImporte().add(importe));
            BigDecimal saldoGiftCard = pago.getGiftcards().get(0).getSaldo();
            pago.getGiftcards().get(0).setSaldo(saldoGiftCard.add(importe.abs()));
            pago.getGiftcards().get(0).setImportePago(pago.getImporte());
        }

        ticketPrincipal.getTotales().recalcular();

        // Se traduce el medio de pago
        pago.setDesMedioPago(I18N.getTexto(medioPago.getDesMedioPago()));

        return pago;
    }

    /**
     * ######################################################################################## ISK-182 GAP-63 - DEVOLUCIÓN
     * SIN DOCUMENTO ORIGEN Consultamos la variable "POS.X_DEVOLUCION_SIN_ORIGEN" para comprobar si realizamos este proceso.
     */
    private DatosOrigenTicketBean datosOrigenDevolucionSinOrigen;

    public void setTicketOrigen(TicketVenta ticket) {
        this.ticketOrigen = ticket;
    }

    public DatosOrigenTicketBean getDatosOrigenDevolucionSinOrigen() {
        return datosOrigenDevolucionSinOrigen;
    }

    public void setDatosOrigenDevolucionSinOrigen(DatosOrigenTicketBean datosOrigenDevolucionSinOrigen) {
        this.datosOrigenDevolucionSinOrigen = datosOrigenDevolucionSinOrigen;
    }

    @Override
    public boolean comprobarTratamientoFiscalDev() {
        Long idTratamientoImpuestosOrigen = this.ticketOrigen.getCabecera().getTienda().getIdTratamientoImpuestos();
        if (idTratamientoImpuestosOrigen == null) {
            return true; // Por compatibilidad con versiones anteriores
        }
        Long idTratamientoImpuestosTicket = sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos();
        return idTratamientoImpuestosOrigen.equals(idTratamientoImpuestosTicket);
    }

    public boolean getRealizarDevolucionSinDocumento() {
        log.debug("getRealizarDevolucionSinDocumento() : GAP 63 - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN...");

        boolean resultado = false;
        try {
            resultado = variablesServices.getVariableAsString(VARIABLE_DEVOLUCION_SIN_ORIGEN).equals("S") ? true : false;
            log.debug("getRealizarDevolucionSinDocumento() - Permiso para realizar devoluciones sin documento origen: " + (resultado ? "SÍ" : "NO"));
        } catch (Exception e) {
            String msgError = "Error al consultar la variable " + VARIABLE_DEVOLUCION_SIN_ORIGEN + " no se encontró, o no está bien configurada.";
            log.debug("getRealizarDevolucionSinDocumento() - " + msgError + " : " + e.getMessage(), e);
        }
        return resultado;
    }

    @Override
    public boolean recuperarTicketDevolucion(String codigo, String codAlmacen, String codCaja, Long idTipoDoc) throws TicketsServiceException {
        boolean resultadoEstandar = super.recuperarTicketDevolucion(codigo, codAlmacen, codCaja, idTipoDoc);

        // GAP46 - CANJEO ARTÍCULOS POR PUNTOS
        deleteOrigenLinesArticlePointsReedem();

        // GAP63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
        if (!resultadoEstandar && getRealizarDevolucionSinDocumento()) {
            setTicketOrigenFalso(codigo, codAlmacen, codCaja, idTipoDoc);
        }

        return resultadoEstandar;
    }

    public void setTicketOrigenFalso(String codigo, String codAlmacen, String codCaja, Long idTipoDoc) {
        log.debug("setTicketOrigenFalso() : GAP 63 - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN...");
        log.info("setTicketOrigenFalso() : GAP 63 - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN...");
        try {
            Long idTicket = new Long(0);

            // DATOS INICIALES TICKET FALSO
            TicketVentaAbono ticketOrigenFalso = new TicketVentaAbono();
            ticketOrigenFalso.setFecha(new Date());
            ticketOrigenFalso.setCajero(sesion.getSesionUsuario().getUsuario());
            ticketOrigenFalso.setCliente(sesion.getAplicacion().getTienda().getCliente());
            ticketOrigenFalso.setIdTicket(idTicket);

            // CABECERA TICKET FALSO
            ticketOrigenFalso.getCabecera().inicializarCabecera(ticketOrigenFalso);
            ticketOrigenFalso.getCabecera().setFecha(new Date());
            ticketOrigenFalso.getCabecera().setCajero(sesion.getSesionUsuario().getUsuario());
            ticketOrigenFalso.getCabecera().setCliente(sesion.getAplicacion().getTienda().getCliente());
            ticketOrigenFalso.getCabecera().setTienda(sesion.getAplicacion().getTienda());

            TipoDocumentoBean documentoActivoTicketFalso = sesion.getAplicacion().getDocumentos().getDocumento(idTipoDoc);
            TipoDocumentoBean documentoAbonoTicketFalso = documentos.getDocumentoAbono(documentoActivoTicketFalso.getCodtipodocumento());
            ticketOrigenFalso.getCabecera().setDocumento(documentoAbonoTicketFalso);
            ticketOrigenFalso.getCabecera().setTipoDocumento(documentoAbonoTicketFalso.getIdTipoDocumento());
            ticketOrigenFalso.getCabecera().setCodTipoDocumento(documentoAbonoTicketFalso.getCodtipodocumento());
            ticketOrigenFalso.getCabecera().setFormatoImpresion(documentoAbonoTicketFalso.getFormatoImpresion());

            ticketOrigenFalso.getCabecera().setIdTicket(idTicket);
            ticketOrigenFalso.getCabecera().setCodTicket(codigo);
            ticketOrigenFalso.getCabecera().setUidTicket(UID_TICKET_FALSO);
            ticketOrigenFalso.getCabecera().setSerieTicket(codigo);

            ticketOrigenFalso.inicializarTotales();

            String fechaFormateada = FormatUtil.getInstance().formateaFechaHoraTicket(ticketOrigenFalso.getFecha());
            SimpleDateFormat formateaFechaSimple = new SimpleDateFormat("yyyy-MM-dd");
            String fechaSimple = formateaFechaSimple.format(ticketOrigenFalso.getFecha());
            DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
            simbolos.setDecimalSeparator('.');
            DecimalFormat formateaSeparadorDecimal = new DecimalFormat("0.00", simbolos);
            String totalTicket = formateaSeparadorDecimal.format(BigDecimal.ZERO);

            String firmaString = fechaSimple + ";" + fechaFormateada + ";" + ticketOrigenFalso.getCabecera().getCodTicket() + ";" + totalTicket + ";" + "";
            firmaString = HashSaftPt.firma(firmaString);
            FirmaTicket firma = new FirmaTicket();
            firma.setFirma(firmaString);
            ticketOrigenFalso.getCabecera().setFirma(firma);

            setTicketOrigen(ticketOrigenFalso);

            // Generamos los datos de documento origen con los datos de ISKAYPET y con el codTicket para Portugal.
            DatosOrigenTicketBean datosOrigenDevolucionFalsos = new DatosOrigenTicketBean();
            datosOrigenDevolucionFalsos.setUidTicket(UID_TICKET_FALSO);
            datosOrigenDevolucionFalsos.setNumFactura(idTicket);
            datosOrigenDevolucionFalsos.setCodCaja(sesion.getAplicacion().getCodCaja());
            datosOrigenDevolucionFalsos.setCodAlmacen(sesion.getAplicacion().getTienda().getCodAlmacen());
            // Formamos el código de ticket y la serie como Portugal : COTIPODOC + " " + PERIODOACTUAL + CODTIENDA + CODCAJA
            /*
             * SimpleDateFormat format = new SimpleDateFormat("yyyy"); String serie =
             * documentoActivoTicketFalso.getCodtipodocumento() + " " + format.format(new Date()) +
             * sesion.getAplicacion().getTienda().getCodAlmacen() + sesion.getAplicacion().getCodCaja();
             * datosOrigenDevolucionFalsos.setSerie(serie); // A partir de la serie, generamos el código del ticket : SERIE + / + 8
             * DIGITOS CON DOCUMENTO INCLUIDO (00000063) datosOrigenDevolucionFalsos.setCodTicket(serie + "/" +
             * StringUtils.leftPad(codigo, 8, "0"));
             */
            Long numFactura = 0L;
            try {
                numFactura = Long.valueOf(codigo);
            } catch (Exception ignore) {
                ;
            }
            datosOrigenDevolucionFalsos.setSerie(codigo);
            datosOrigenDevolucionFalsos.setCodTicket(codigo);
            datosOrigenDevolucionFalsos.setNumFactura(numFactura);

            /* Sacamos los datos del tipo de documento que vamos a usar para el ticket, en nuestro caso es factura simplificada */
            datosOrigenDevolucionFalsos.setIdTipoDocumento(documentoActivoTicketFalso.getIdTipoDocumento());
            datosOrigenDevolucionFalsos.setCodTipoDocumento(documentoActivoTicketFalso.getCodtipodocumento());
            datosOrigenDevolucionFalsos.setDesTipoDocumento(documentoActivoTicketFalso.getDestipodocumento());

            this.datosOrigenDevolucionSinOrigen = datosOrigenDevolucionFalsos;

            // Además de los datos origen falso, se setean temporalmente datos doc origen para que procese el documento de
            // devolución de forma estándar
            // Estos datos se deben quitar al entrar en facturación
            DatosDocumentoOrigenTicket docOrigen = new DatosDocumentoOrigenTicket();
            docOrigen.setSerie(datosOrigenDevolucionFalsos.getSerie());
            docOrigen.setCaja(datosOrigenDevolucionFalsos.getCodCaja());
            docOrigen.setNumFactura(datosOrigenDevolucionFalsos.getNumFactura());
            docOrigen.setIdTipoDoc(datosOrigenDevolucionFalsos.getIdTipoDocumento());
            docOrigen.setCodTipoDoc(datosOrigenDevolucionFalsos.getCodTipoDocumento());
            docOrigen.setUidTicket(datosOrigenDevolucionFalsos.getUidTicket());
            docOrigen.setCodTicket(datosOrigenDevolucionFalsos.getCodTicket());
            this.getTicketOrigen().getCabecera().setTipoDocumento(datosOrigenDevolucionFalsos.getIdTipoDocumento());
            this.getTicketOrigen().getCabecera().setCodTipoDocumento(datosOrigenDevolucionFalsos.getCodTipoDocumento());
            this.getTicketOrigen().getCabecera().setDatosDocOrigen(docOrigen);
        } catch (Exception e) {
            String msgError = "Error al generar la firma del ticket falso para devoluciones sin documento origen.";
            log.error("setTicketOrigenFalso() - " + msgError + " : " + e.getMessage(), e);
        }
    }

    /**
     * ######################################################################################## ISK-182 GAP-63 - DEVOLUCIÓN
     * SIN DOCUMENTO ORIGEN Para devolución sin doc origen se setea idLinea de doc. origen a 0
     *
     * @throws ArticuloNotFoundException
     */

    public synchronized LineaTicket nuevaLineaArticuloIskaypet(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Integer idLineaDocOrigen) throws LineaTicketException, ArticuloNotFoundException {
        return nuevaLineaArticuloIskaypet(codArticulo, desglose1, desglose2, cantidad, null, idLineaDocOrigen, false);
    }

    public synchronized LineaTicket nuevaLineaArticuloIskaypet(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen, boolean esLineaDevolucionPositiva) throws LineaTicketException, ArticuloNotFoundException {
        return nuevaLineaArticuloIskaypet(codArticulo, desglose1, desglose2, cantidad, stage, idLineaDocOrigen, esLineaDevolucionPositiva, true);
    }

    public synchronized LineaTicket nuevaLineaArticuloIskaypet(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen,
                                                               boolean esLineaDevolucionPositiva, boolean applyDUN14Factor) throws LineaTicketException, ArticuloNotFoundException {
        log.debug("nuevaLineaArticuloIskaypet() - Creando nueva línea de artículo...");
        LineaTicketAbstract linea = null;

        //GAP147 - USO DE CUPONES DE INTEGRACION NAV EN POS
//		// CUPONES COMERZZIA 
        boolean isCupon = sesionPromociones.isCouponWithPrefix(codArticulo);

        if (isCupon) {
            try {
                CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO(codArticulo, true);
                if (!sesionPromociones.aplicarCupon(customerCouponDTO, (TicketVentaAbono) ticketPrincipal)) {
                    throw new LineaTicketException(I18N.getTexto("No se ha podido aplicar el cupón."));
                }
                ticketPrincipal.getTotales().recalcular();
            } catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
                log.warn("nuevaLineaArticuloIskaypet() - Error en la aplicación del cupón -" + ex.getMessageI18N());
                throw new LineaTicketException(ex.getMessageI18N(), ex);
            }
        } else {
            // ARTÍCULO CON CODBARRAS ESPECIAL
            BigDecimal precio = null;
            boolean pesarArticulo = stage != null;
            String codBarras = null;
            if (!esDevolucion) {
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
                        } else {
                            cantidad = BigDecimal.ONE;
                        }
                        String precioCodBar = codBarrasEspecial.getPrecio();
                        if (precioCodBar != null) {
                            precio = FormatUtil.getInstance().desformateaBigDecimal(codBarrasEspecial.getPrecio(), 2);
                        } else {
                            precio = null;
                        }
                        if (codArticulo == null) {
                            log.error(String.format("nuevaLineaArticuloIskaypet() - El código de barra especial obtenido no es válido. CodArticulo: %s, cantidad: %s, precio: %s", codArticulo, cantidad,
                                    precio));
                            throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
                        }
                    }
                } catch (LineaTicketException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("Error procesando el código de barras especial : " + codArticulo, e);
                    throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
                }
            }

            // ARTICULOS NORMALES
            try {

                linea = lineasTicketServices.createLineaArticulo((TicketVenta) ticketPrincipal, codArticulo, desglose1, desglose2, cantidad, precio, createLinea(), applyDUN14Factor);

                String msgError = "Debe tener un cliente asociado para poder realizar ventas de planes de salud";
                validarPlanesSalud(linea.getCodArticulo(), msgError);

                comprobarCantidadUnitaria(linea);
                linea.setCantidad(tratarSignoCantidad(linea.getCantidad(), linea.getCabecera().getCodTipoDocumento()));
                if (esLineaDevolucionPositiva) {
                    linea.setCantidad(linea.getCantidad().abs());
                }
                if (codBarras != null) {
                    linea.setCodigoBarras(codBarras);
                }
                // Si el artículo tiene en su campo FORMATO en BBDD...
                if (pesarArticulo && StringUtils.isNotBlank(linea.getArticulo().getBalanzaTipoArticulo()) && linea.getArticulo().getBalanzaTipoArticulo().trim().toUpperCase().equals(PESAR_ARTICULO)) {
                    IBalanza balanza = Dispositivos.getInstance().getBalanza();
                    if (!(balanza instanceof BalanzaNoConfig)) {
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        POSApplication.getInstance().getMainView().showModalCentered(SolicitarPesoArticuloView.class, params, stage);
                        if (params.containsKey(SolicitarPesoArticuloController.PARAM_PESO)) {
                            BigDecimal peso = (BigDecimal) params.get(SolicitarPesoArticuloController.PARAM_PESO);
                            if (peso == null || BigDecimalUtil.isMenorOrIgualACero(peso)) {
                                throw new LineaTicketException(I18N.getTexto("No se ha podido pesar el artículo, compruebe la configuración de la balanza."));
                            }
                            linea.setCantidad(peso);
                        } else {
                            throw new LineaTicketException(I18N.getTexto("Este artículo no puede ser introducido sin ser pesado previamente."));
                        }
                    }
                }
                if (esDevolucion && ticketOrigen != null && !esLineaDevolucionPositiva) {
                    // GAP 63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
                    if (UID_TICKET_FALSO.equalsIgnoreCase(ticketOrigen.getUidTicket())) {
                        linea.setLineaDocumentoOrigen(0);
                    } else {
                        if (idLineaDocOrigen == null) {
                            idLineaDocOrigen = getIdLineaTicketOrigen(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), linea.getCantidad().abs());
                        }
                        LineaTicketAbstract lineaOrigen = ticketOrigen.getLinea(idLineaDocOrigen);
                        lineaOrigen.setPrecioTotalConDto(lineaOrigen.getImporteTotalConDto().setScale(6, BigDecimal.ROUND_HALF_UP)
                                .divide(lineaOrigen.getCantidad().setScale(6, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP));
                        linea.resetPromociones();
                        linea.setPrecioSinDto(lineaOrigen.getPrecioConDto());
                        linea.setPrecioTotalSinDto(lineaOrigen.getPrecioTotalConDto());
                        linea.recalcularImporteFinal();

                        linea.setLineaDocumentoOrigen(lineaOrigen.getIdLinea());

                        actualizarCantidadesOrigenADevolver(lineaOrigen, lineaOrigen.getCantidadADevolver().add(linea.getCantidad().abs()));
                    }
                }

                if (isTicketVentaDelivery()) {
                    ((IskaypetLineaTicket) linea).setAdmitePromociones(false);
                }

                addLinea(linea);
                ticketPrincipal.getTotales().recalcular();
            }
            // GAP147 - USO DE CUPONES DE INTEGRACION NAV EN POS
            // Modificamos el tipo de Exception que subimos para poder identicar el error en la pantalla de ventas.
            catch (Exception e) {
                linea = null;

                boolean errorArticulo = e instanceof ArticuloNotFoundException || e.getCause() instanceof ArticuloNotFoundException;

                try { // Si no se ha encontrado artículo, intentamos aplicar cupón
                    CustomerCouponDTO coupon = new CustomerCouponDTO(codArticulo, false);
                    isCupon = sesionPromociones.aplicarCupon(coupon, (TicketVentaAbono) ticketPrincipal);
                    if (!isCupon) { // Si el código no es de un cupón válido,
                        // lanzamos excepción de artículo no encontrado
                        log.warn("nuevaLineaArticuloIskaypet() - Artículo no encontrado " + codArticulo);
                        if (errorArticulo) {
                            throw new ArticuloNotFoundException(e.getMessage(), e);
                        } else {
                            throw new LineaTicketException(e.getMessage());
                        }
                    }
                    ticketPrincipal.getTotales().recalcular();

                } // Si tenemos excepción durante la aplicación del cupón, lanzamos
                // excepción indicativa
                catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
                    log.warn("nuevaLineaArticuloIskaypet() - Error en la aplicación del cupón - " + ex.getMessageI18N());
                    if (ex instanceof CuponUseException | ex instanceof CuponAplicationException) {
                        throw new LineaTicketException(ex.getMessageI18N(), e);
                    }
                    if (errorArticulo) {
                        throw new ArticuloNotFoundException(e.getMessage(), e);
                    }
                    throw new LineaTicketException(ex.getMessageI18N(), e);
                }

            }
        }
        if (linea instanceof IskaypetLineaTicket) {
            registrarImpresionLinea((IskaypetLineaTicket) linea);
        }
        return (LineaTicket) linea;
    }


    @Override
    public synchronized LineaTicket nuevaLineaArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, Stage stage, Integer idLineaDocOrigen,
                                                       boolean esLineaDevolucionPositiva, boolean applyDUN14Factor) throws LineaTicketException {
        log.debug("nuevaLineaArticulo() - Creando nueva línea de artículo...");
        LineaTicketAbstract linea = null;

        boolean isCupon = sesionPromociones.isCouponWithPrefix(codArticulo);
        if (isCupon) {
            try {
                CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO(codArticulo, true);
                if (!sesionPromociones.aplicarCupon(customerCouponDTO, (TicketVentaAbono) ticketPrincipal)) {
                    throw new LineaTicketException(I18N.getTexto("No se ha podido aplicar el cupón."));
                }
                ticketPrincipal.getTotales().recalcular();
            } catch (CuponAplicationException | CuponUseException | CuponesServiceException ex) {
                log.warn("nuevaLineaArticulo() - Error en la aplicación del cupón -" + ex.getMessageI18N());
                throw new LineaTicketException(ex.getMessageI18N(), ex);
            }
        } else {
            BigDecimal precio = null;

            boolean pesarArticulo = stage != null;

            String codBarras = null;

            if (!esDevolucion) {
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
                        } else {
                            cantidad = BigDecimal.ONE;
                        }
                        String precioCodBar = codBarrasEspecial.getPrecio();
                        if (precioCodBar != null) {
                            precio = FormatUtil.getInstance().desformateaBigDecimal(codBarrasEspecial.getPrecio(), 2);
                        } else {
                            precio = null;
                        }

                        if (codArticulo == null) {
                            log.error(String.format("nuevaLineaArticulo() - El código de barra especial obtenido no es válido. CodArticulo: %s, cantidad: %s, precio: %s", codArticulo, cantidad,
                                    precio));
                            throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
                        }
                    }
                } catch (LineaTicketException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("Error procesando el código de barras especial : " + codArticulo, e);
                    throw new LineaTicketException(I18N.getTexto("Error procesando el código de barras. Revise configuración."));
                }
            }

            try {
                linea = lineasTicketServices.createLineaArticulo((TicketVenta) ticketPrincipal, codArticulo, desglose1, desglose2, cantidad, precio, createLinea(), applyDUN14Factor);
                comprobarCantidadUnitaria(linea);
                linea.setCantidad(tratarSignoCantidad(linea.getCantidad(), linea.getCabecera().getCodTipoDocumento()));
                if (esLineaDevolucionPositiva) {
                    linea.setCantidad(linea.getCantidad().abs());
                }

                if (codBarras != null) {
                    linea.setCodigoBarras(codBarras);
                }

                // Si el artículo tiene en su campo FORMATO en BBDD...
                if (pesarArticulo && StringUtils.isNotBlank(linea.getArticulo().getBalanzaTipoArticulo()) && linea.getArticulo().getBalanzaTipoArticulo().trim().toUpperCase().equals(PESAR_ARTICULO)) {
                    IBalanza balanza = Dispositivos.getInstance().getBalanza();
                    if (!(balanza instanceof BalanzaNoConfig)) {
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        POSApplication.getInstance().getMainView().showModalCentered(SolicitarPesoArticuloView.class, params, stage);
                        if (params.containsKey(SolicitarPesoArticuloController.PARAM_PESO)) {
                            BigDecimal peso = (BigDecimal) params.get(SolicitarPesoArticuloController.PARAM_PESO);

                            if (peso == null || BigDecimalUtil.isMenorOrIgualACero(peso)) {
                                throw new LineaTicketException(I18N.getTexto("No se ha podido pesar el artículo, compruebe la configuración de la balanza."));
                            }

                            linea.setCantidad(peso);
                        } else {
                            throw new LineaTicketException(I18N.getTexto("Este artículo no puede ser introducido sin ser pesado previamente."));
                        }
                    }
                }

                if (esDevolucion && ticketOrigen != null && !esLineaDevolucionPositiva) {
                    // GAP 63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
                    if (UID_TICKET_FALSO.equalsIgnoreCase(ticketOrigen.getUidTicket())) {
                        linea.setLineaDocumentoOrigen(0);
                    } else {
                        if (idLineaDocOrigen == null) {
                            idLineaDocOrigen = getIdLineaTicketOrigen(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), linea.getCantidad().abs());
                        }
                        LineaTicketAbstract lineaOrigen = ticketOrigen.getLinea(idLineaDocOrigen);
                        lineaOrigen.setPrecioTotalConDto(lineaOrigen.getImporteTotalConDto().setScale(6, BigDecimal.ROUND_HALF_UP)
                                .divide(lineaOrigen.getCantidad().setScale(6, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP));
                        linea.resetPromociones();
                        linea.setPrecioSinDto(lineaOrigen.getPrecioConDto());
                        linea.setPrecioTotalSinDto(lineaOrigen.getPrecioTotalConDto());

                        if (((IskaypetLineaTicket) lineaOrigen).getDtoPromociones() != null) {
                            boolean precioAsignado = false;
                            BigDecimal precioTotalDevolucion = BigDecimal.ZERO;
                            for (DtoPromocion dtoPromo : ((IskaypetLineaTicket) lineaOrigen).getDtoPromociones()) {
                                if (!precioAsignado) {
                                    precioAsignado = true;
                                    precioTotalDevolucion = dtoPromo.getPrecioTotalSinDto();
                                }

                                precioTotalDevolucion = precioTotalDevolucion.subtract(dtoPromo.getImporteTotalDtoProrrateado());
                                //if(dtoPromo.getItTipoPromocion() != null && dtoPromo.getItTipoPromocion() != 12L ) {
                                //linea.setPrecioTotalSinDto(linea.getPrecioTotalSinDto().subtract(dtoPromo.getImporteTotalDtoProrrateado()));
                                //}
                            }

                            linea.setPrecioTotalConDto(precioTotalDevolucion);
                            linea.setPrecioTotalSinDto(precioTotalDevolucion);
                            linea.setPrecioConDto(sesion.getImpuestos().getPrecioSinImpuestos(linea.getCodImpuesto(), linea.getPrecioTotalConDto(), ticketOrigen.getCabecera().getCliente().getIdTratImpuestos()));
                            linea.setPrecioSinDto(sesion.getImpuestos().getPrecioSinImpuestos(linea.getCodImpuesto(), linea.getPrecioTotalSinDto(), ticketOrigen.getCabecera().getCliente().getIdTratImpuestos()));
                        }
                        linea.recalcularImporteFinal();

                        linea.setLineaDocumentoOrigen(lineaOrigen.getIdLinea());

                        actualizarCantidadesOrigenADevolver(lineaOrigen, lineaOrigen.getCantidadADevolver().add(linea.getCantidad().abs()));
                    }
                }

                // ISK-262 GAP-107 GLOVO - En venta delivery las líneas no admiten promociones
                if (isTicketVentaDelivery()) {
                    ((LineaTicket) linea).setAdmitePromociones(false);
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

        // Se registra la impresión de la línea
        if (linea instanceof IskaypetLineaTicket) {
            registrarImpresionLinea((IskaypetLineaTicket) linea);
        }

        return (LineaTicket) linea;
    }


    @Override
    public LineaTicket eliminarLineaArticulo(Integer idLinea) {
        log.debug("eliminarLineaArticulo() - Eliminando línea de ticket con idLinea: " + idLinea);

        LineaTicket linea = ((TicketVentaAbono) ticketPrincipal).getLinea(idLinea);
        ticketPrincipal.getLineas().remove(linea);
        recalcularConPromociones();

        // ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
        // Se añade comprobación para evitar NPE
        if (ticketOrigen == null || !UID_TICKET_FALSO.equalsIgnoreCase(ticketOrigen.getUidTicket())) {
            if (ticketOrigen != null && linea.getLineaDocumentoOrigen() != null) {
                LineaTicketAbstract lineaOrigen = ticketOrigen.getLinea(linea.getLineaDocumentoOrigen());
                actualizarCantidadesOrigenADevolver(lineaOrigen, lineaOrigen.getCantidadADevolver().subtract((((LineaTicket) linea).getCantidad().abs())));
            }
        }

        // GAP62 - PEGATINAS PROMOCIONALES
        // En caso de la linea ser una pegatina promocional, debemos ponerla como habilitada de nuevo ya que estamos eliminando
        // su uso.
        if (((IskaypetLineaTicket) linea).getPegatinaPromocional() != null) {
            try {
                promotionStickerService.processPromotionStickerArticleDelete(((IskaypetLineaTicket) linea).getPegatinaPromocional().getEan());
                ((IskaypetLineaTicket) linea).setPegatinaPromocional(null);
            } catch (Exception e) {
                // Actualmente no devolvemos error.
            }
        }

        return (LineaTicket) linea;
    }

    @Override
    public void eliminarTicketCompleto() throws TicketsServiceException, PromocionesServiceException, DocumentoException {
        // GAP62 - PEGATINAS PROMOCIONALES
        // Sacamos el listado de BarCodes de las pegatinas promocionales del ticket en caso de tenerlas.
        List<String> listPromotionalSticker = new ArrayList<String>();
        List<LineaTicket> lines = ticketPrincipal.getLineas();
        for (LineaTicket line : lines) {
            if (((IskaypetLineaTicket) line).getPegatinaPromocional() != null) {
                listPromotionalSticker.add(((IskaypetLineaTicket) line).getPegatinaPromocional().getEan());
            }
        }

        super.eliminarTicketCompleto();

        // GAP62 - PEGATINAS PROMOCIONALES
        // Una vez sacado el listado, pasamos a realizar su update para ponerlas como no usadas.
        if (listPromotionalSticker != null && !listPromotionalSticker.isEmpty()) {
            for (String ean : listPromotionalSticker) {
                try {
                    promotionStickerService.processPromotionStickerArticleDelete(ean);
                } catch (Exception e) {
                    // Actualmente no devolvemos error.
                }
            }
        }
    }

    /*
     * #####################################################################################################################
     * ######
     */
    /*
     * ########################################## GAP46 - CANJEO ARTÍCULOS POR PUNTOS
     * ############################################
     */
    /*
     * #####################################################################################################################
     * ######
     */

    public void deleteOrigenLinesArticlePointsReedem() {
        if (ticketOrigen != null && ticketOrigen.getLineas() != null && !ticketOrigen.getLineas().isEmpty()) {
            List<LineaTicketAbstract> lineasOrigen = (List<LineaTicketAbstract>) ticketOrigen.getLineas();
            for (int i = 0; i < lineasOrigen.size(); i++) {
                ArticlesPointsXMLBean articlePoints = ((IskaypetLineaTicket) lineasOrigen.get(i)).getArticlePoints();
                if (articlePoints != null && ArticlesPointsXMLBean.VALUE_REEDEM_OK.equals(articlePoints.getReedem())) {
                    ticketOrigen.getLineas().remove(lineasOrigen.get(i));
                    i--;
                }
            }
        }
    }

    // Se obtienen las promoicones de puntos activas
    public List<IskaypetPromocionPuntosBean> getPromocionesActivaPuntos() {
        return sesionPromociones.getPromocionesActivas().values().stream().filter(promocion -> promocion instanceof IskaypetPromocionPuntosBean)
                .map(promocion -> (IskaypetPromocionPuntosBean) promocion).collect(Collectors.toList());
    }

    @Override
    public void completaLineaDevolucionPunto() {
        BigDecimal totalPuntosDevolucion = BigDecimal.ZERO;
        // Solamente cuando es devolución
        if (getTicket().isEsDevolucion()) {
            for (LineaTicketAbstract linea : (List<LineaTicketAbstract>) getTicket().getLineas()) {
                if (getTicketOrigen() != null && getTicketOrigen().getLineas().size() > 0) {
                    // Si es una devolución con lineas en origen
                    BigDecimal puntosConcedidos = getPuntosConcedidosLinea(linea.getCodArticulo(), linea.getLineaDocumentoOrigen(), linea.getCantidad());
                    // puntosConcedidos = puntosConcedidos.setScale(0, RoundingMode.DOWN);
                    linea.setPuntosADevolver(puntosConcedidos.doubleValue());
                    // acumulamos los puntos totales a devolver para luego utilizarlo para redonder si el sistema estña así configurado.
                    totalPuntosDevolucion = totalPuntosDevolucion.add(puntosConcedidos);
                } else {
                    // Si es una devolución libre sin lineas de origen
                    List<IskaypetPromocionPuntosBean> promociones = getPromocionesActivaPuntos();
                    BigDecimal puntosADevolver = BigDecimal.ZERO;
                    IskaypetTicketVentaAbono ticket = (IskaypetTicketVentaAbono) getTicket();
                    // Seteamos los puntos totales a 0
                    ticket.getTotales().resetPuntos();
                    for (IskaypetPromocionPuntosBean promocion : promociones) {
                        boolean isAplicable = promocion.isAplicable(ticket);
                        if (isAplicable && linea instanceof IskaypetLineaTicket) {
                            // Calculamos los puntos para saber los que debemos devolver
                            ((IskaypetLineaTicket) linea).setAdmitePromociones(true);
                            promocion.aplicarPromocion(ticket);
                            ((IskaypetLineaTicket) linea).setAdmitePromociones(false);
                            puntosADevolver = puntosADevolver.add(linea.getPromociones().stream().map(PromocionLineaTicket::getPuntos).reduce(BigDecimal.ZERO, BigDecimal::add).abs());

                            // Volvemos a setear las promociones
                            ticket.getPromociones().removeIf(el -> el.getIdPromocion().equals(promocion.getIdPromocion()));
                            linea.getPromociones().removeIf(el -> el.getIdPromocion().equals(promocion.getIdPromocion()));
                            ticket.getTotales().resetPuntos();
                        }
                    }
                    linea.setPuntosADevolver(puntosADevolver.doubleValue());
                    totalPuntosDevolucion = totalPuntosDevolucion.add(puntosADevolver);
                }
            }

            // si tenemos configurada la variable de redondeo de los puntos
            // hacemos el ajuste para el caso de que tengamos que devolver puntos enteros en devoluciones

            if (variablesServices.getVariableAsBoolean(IskaypetPromocionPuntosBean.VARIABLE_APLICA_REDONDEO_PUNTOS, false)) {
                BigDecimal totalPuntosDevolucionRedondeado = totalPuntosDevolucion.setScale(0, RoundingMode.DOWN);

                // tenemos decimales en los puntos a devolver
                if (totalPuntosDevolucionRedondeado.doubleValue() != totalPuntosDevolucion.doubleValue()) {
                    Double ajustePuntos = totalPuntosDevolucion.subtract(totalPuntosDevolucionRedondeado).doubleValue();

                    if (ajustePuntos > 0.0) {
                        for (LineaTicketAbstract linea : (List<LineaTicketAbstract>) getTicket().getLineas()) {
                            if (getTicketOrigen() != null) {
                                if ((linea.getPuntosADevolver() != null) && (linea.getPuntosADevolver() > 0.0) && (linea.getPuntosADevolver() > ajustePuntos)) {
                                    linea.setPuntosADevolver(linea.getPuntosADevolver() - ajustePuntos);
                                    ajustePuntos = 0.0;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * #####################################################################################################################
     * ######
     */
    /*
     * ############### GAP NN CONTROL PARA LAS DEVOLUCIONES CUANDO SON VARIOS ARTICULOS REPETIDOS EN EL TICKET
     * ###################
     */
    /*
     * #####################################################################################################################
     * ######
     */

    protected BigDecimal damePrecioLineaCandidata(IskaypetLineaTicket linea) {
        BigDecimal precioLinea = BigDecimal.ZERO;
        if (linea.getDtoPromociones() != null) {
            boolean precioAsignado = false;
            for (DtoPromocion dtoPromocion : linea.getDtoPromociones()) {
                if (!precioAsignado) {
                    precioAsignado = true;
                    precioLinea = dtoPromocion.getPrecioTotalSinDto();
                }
                if (dtoPromocion.getItTipoPromocion() != 12L) {
                    precioLinea = precioLinea.subtract(dtoPromocion.getImporteTotalDtoProrrateado());
                }
            }
        } else {
            precioLinea = linea.getPrecioTotalConDto();
        }

        return precioLinea;
    }

    @Override
    protected Integer getIdLineaTicketOrigen(String codArticulo, String desglose1, String desglose2, BigDecimal cantidadAbs) throws LineaTicketException {
        List<LineaTicket> lineasOrigen = ticketOrigen.getLinea(codArticulo);
        Integer idLinea = null;
        // int numLineas = 0;
        boolean encontrado = false;

        int idLineaCandidata = -1;
        BigDecimal precioLineaCandidata = BigDecimal.ZERO;

        for (LineaTicket lineaOrigen : lineasOrigen) {
            if (lineaOrigen.getCodArticulo().equals(codArticulo) && lineaOrigen.getDesglose1().equals(desglose1) && lineaOrigen.getDesglose2().equals(desglose2)) {
                encontrado = true;
                if (BigDecimalUtil.isMayorOrIgualACero(lineaOrigen.getCantidad().subtract(lineaOrigen.getCantidadDevuelta().add(lineaOrigen.getCantidadADevolver().add(cantidadAbs))))) {
                    // numLineas++;
                    //Recorremos todas las lineas en busca de articulos que no tengan prorrateo y tengan disponibilidad
                    BigDecimal precioLinea = damePrecioLineaCandidata((IskaypetLineaTicket) lineaOrigen);

                    if (BigDecimalUtil.isMayorOrIgual(precioLinea, precioLineaCandidata)) {
                        idLineaCandidata = lineaOrigen.getIdLinea();
                        precioLineaCandidata = precioLinea;
                    }
                }
            }
        }

        //asignamos la linea de devolucion.
        if (idLineaCandidata != -1) {
            idLinea = idLineaCandidata;
        }

        if (!encontrado) {
            throw new LineaDevolucionNuevoArticuloException(I18N.getTexto("El artículo {0} no se ha encontrado en el documento origen de la devolución.", codArticulo));
        }
        if (idLinea == null) { // Se ha encontrado pero idLinea es null -> cantidad superada
            throw new LineaDevolucionCambioException(I18N.getTexto("La cantidad a devolver del artículo supera a la cantidad vendida."));
        }
        // ESTE CONTROL QUEDA ANULADO PARA LA INSERCCIÓN DE LAS LINEAS EN LA DEVOLUCIÓN SI EXISTE MÁS DE UNA
        // if(numLineas > 1){
        // throw new LineaTicketException(I18N.getTexto("El artículo {0} existe en varias líneas del ticket original, deberá
        // indicar la línea de devolución manualmente.", codArticulo));
        // }
        return idLinea;
    }

    // ISK-153 INTEGRACION CZZ Y EVICERTIA
    // Personalizamos la recuperación del ticket para poder tratar la linea y comprobar que no tiene un contrato animal
    // dentro y poder hacer la devolucion
    @Override
    protected boolean tratarTicketRecuperado(byte[] ticketRecuperado) throws TicketsServiceException {
        try {
            log.debug(new String(ticketRecuperado, "UTF-8"));

            contadorLinea = 1;
            boolean resultado = false;

            nuevoTicket();

            // Realizamos el unmarshall

            ticketOrigen = (TicketVentaAbono) MarshallUtil.leerXML(ticketRecuperado, getTicketClasses(documentoActivo).toArray(new Class[]{}));
            for (LineaTicket line : ((TicketVentaAbono) ticketOrigen).getLineas()) {
                if (line.getDocumentoOrigen() != null && line.getDocumentoOrigen().getPromociones() != null) {
                    BigDecimal dtoMenosIngreso = BigDecimal.ZERO;
                    for (PromocionLineaTicket promo : line.getDocumentoOrigen().getPromociones()) {
                        if (promo.isDescuentoMenosIngreso()) {
                            dtoMenosIngreso = dtoMenosIngreso.add(promo.getImporteTotalDtoMenosIngreso());
                            line.setImporteTotalPromocionesMenosIngreso((line.getImporteTotalPromocionesMenosIngreso() != null ? line.getImporteTotalPromocionesMenosIngreso() : BigDecimal.ZERO)
                                    .add(promo.getImporteTotalDtoMenosIngreso()));
                        }
                    }
                }
            }
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

            if (ticketOrigen != null) {
                // Se tiene que crear este DatosDocumentoOrigen antes de inicializar los datos de la cabecera con los datos de la
                // devolución
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
        } catch (Exception e) {
            throw new TicketsServiceException(new com.comerzzia.core.util.base.Exception(I18N.getTexto("Lo sentimos, ha ocurrido un error al recuperar el ticket"), e));
        }
    }

    /**
     * @return <ul><li>true solo si es venta y tiene un colectivo de fidelizado delivery (tag cabecera delivery relleno)</li>
     * <li>false en cualquier otro caso</li></ul>
     */
    public boolean isTicketVentaDelivery() {
        return ticketPrincipal != null && ticketPrincipal.getCabecera() != null && ticketPrincipal.getCabecera() instanceof IskaypetCabeceraTicket && StringUtils.isNotEmpty(((IskaypetCabeceraTicket) ticketPrincipal.getCabecera()).getDelivery());
    }

    public static final String COLECTIVO_INTERCOMPANY = "INTERCOMPANY";

    public boolean isTicketVentaIntercompany() {
        // Paso 1: Comprobamos que exista el ticket principal y que tenga una cabecera y que esta tenga datos fidelizado
        if (ticketPrincipal == null || ticketPrincipal.getCabecera() == null || ticketPrincipal.getCabecera().getDatosFidelizado() == null) {
            return false;
        }
        // Paso 2: Comprobamos que los datos fidelizado tengan un colectivo de fidelizado intercompany
        List<String> codColectivos = ticketPrincipal.getCabecera().getDatosFidelizado().getCodColectivos();
        return codColectivos != null && codColectivos.stream().anyMatch(COLECTIVO_INTERCOMPANY::equals);
    }

    /**
     * @return <ul><li>true solo si es devolución y tiene un colectivo de fidelizado delivery en ticket origen (tag cabecera ticket origen delivery relleno)</li>
     * <li>false en cualquier otro caso</li></ul>
     */
    public boolean isDevolucionDelivery() {
        return isEsDevolucion() && ticketOrigen != null && ticketOrigen.getCabecera() != null && ticketOrigen.getCabecera() instanceof IskaypetCabeceraTicket && StringUtils.isNotEmpty(((IskaypetCabeceraTicket) ticketOrigen.getCabecera()).getDelivery());
    }

    public String getCodDelivery() {
        if (ticketPrincipal == null || ticketPrincipal.getCabecera() == null || !(ticketPrincipal.getCabecera() instanceof IskaypetCabeceraTicket) || StringUtils.isEmpty(((IskaypetCabeceraTicket) ticketPrincipal.getCabecera()).getDelivery())) {
            return null;
        }
        return ((IskaypetCabeceraTicket) ticketPrincipal.getCabecera()).getDelivery();
    }

    public void comprobarGeneracionATCUD() throws Exception {
        TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento("FS");
        if (documentoActivo.getCodpais().equalsIgnoreCase("PT")) {
            comprobarGeneracionATCUD("FS");
            comprobarGeneracionATCUD("FT");
            comprobarGeneracionATCUD("NC");
        }
    }

    public String comprobarGeneracionATCUD(String codTipoDoc) throws Exception {
        TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(codTipoDoc);
        if (documentoActivo.getCodpais().equalsIgnoreCase("PT")) {
            //Pedimos el valor actual del contador asociado al documento
            try {
                log.debug("setContadorIdTicket() - Obteniendo contador para identificador...");
                Map<String, String> parametrosContador = new HashMap<>();
                Map<String, String> condicionesVigencias = new HashMap<>();

                parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODEMP, sesion.getAplicacion().getEmpresa().getCodEmpresa());
                parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODALM, sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
                parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODSERIE, sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
                parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODCAJA, sesion.getAplicacion().getCodCaja());
                parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODDOC, codTipoDoc);
                parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_PERIODO, ((new Fecha()).getAño().toString()));

                condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODCAJA, sesion.getAplicacion().getCodCaja());
                condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODALM, sesion.getAplicacion().getTienda().getCodAlmacen());
                condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODEMP, sesion.getAplicacion().getEmpresa().getCodEmpresa());


                ConfigContadorBean confContador = servicioConfigContadores.consultar(documentoActivo.getIdContador());
                if (!confContador.isRangosCargados()) {
                    ConfigContadorRangoExample example = new ConfigContadorRangoExample();
                    example.or().andIdContadorEqualTo(confContador.getIdContador());
                    example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
                            + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
                    List<ConfigContadorRangoBean> rangos = servicioConfigContadoresRangos.consultar(example);

                    confContador.setRangos(rangos);
                    confContador.setRangosCargados(true);
                }
                ContadorBean ticketContador = servicioContadores.consultarContadorActivo(confContador, parametrosContador, condicionesVigencias, sesion.getAplicacion().getUidActividad(), false);
                if (ticketContador == null || ticketContador.getError() != null) {
                    throw new ContadorNotFoundException(I18N.getTexto("No se ha encontrado un contador disponible"));
                }

                CounterRangeParamDto counterRangeParam = new CounterRangeParamDto();
                counterRangeParam.setCounterId(ticketContador.getIdContador());
                counterRangeParam.setDivisor1(ticketContador.getDivisor1());
                counterRangeParam.setDivisor2(ticketContador.getDivisor2());
                counterRangeParam.setDivisor3(ticketContador.getDivisor3());

                String rangeId = counterRangeManager.findRangeId(counterRangeParam);

                if (StringUtils.isBlank(rangeId)) {
                    String msg = I18N.getTexto("No se dispone de un código de validación para generar un ATCUD. Revise la configuración.");
                    throw new FiscalDataException(I18N.getTexto(msg));
                }

                return rangeId;

            } catch (Exception e) {
                String msg = I18N.getTexto("Error comprobando configuracion de ATCUD: {0}", e.getMessage());
                log.error("comprobarGeneracionATCUD() - " + msg, e);
                throw e;
            }
        }

        return null;
    }

    /**
     * Método que valida si hay un fidelizado seleccionado
     *
     * @return true si hay un fidelizado seleccionado, false en caso contrario
     */
    public boolean isFidelizadoSeleccionado() {
        FidelizacionBean datosFidelizado = getTicket().getCabecera().getDatosFidelizado();
        if (datosFidelizado != null) {
            log.debug("isFidelizadoSeleccionado() - Fidelizado seleccionado: " + datosFidelizado.getIdFidelizado());
            return true;
        }
        log.debug("isFidelizadoSeleccionado() - No hay fidelizado seleccionado");
        return false;
    }

    /**
     * Método que valida si el artículo pertenece a la familia de planes de salud y si hay un fidelizado seleccionado
     *
     * @param codArt   Código de artículo
     * @param msgError Mensaje de error
     * @throws LineaTicketException
     */
    public void validarPlanesSalud(String codArt, String msgError) throws LineaTicketException {
        String planesSalud = variablesServices.getVariableAsString(VARIABLE_FAMILIA_PLANES_DE_SALUD);

        // Si no hay planes de salud configurados, no se realiza la validación
        if (StringUtils.isBlank(planesSalud)) {
            return;
        }

        // Se consulta el artículo por el código de artículo
        ArticuloBean articuloBean = null;
        try {
            articuloBean = articulosService.consultarArticulo(codArt);
        } catch (Exception e) {
            throw new LineaTicketException(I18N.getTexto("Error al consultar el artículo"), e);
        }

        // Se comprueba si el artículo pertenece a la familia de planes de salud y si hay un fidelizado seleccionado
        String codFamilia = articuloBean.getCodFamilia();
        List<String> listaPlanesSalud = Arrays.asList(planesSalud.split(";"));
        if (listaPlanesSalud.contains(codFamilia) && !isFidelizadoSeleccionado()) {
            throw new LineaTicketException(I18N.getTexto(msgError));
        }

    }

    public int countTicketsAparcadosTotales() {
        return ticketsAparcadosService.countTicketsAparcadosTotales(this.ticketPrincipal.getCabecera().getTipoDocumento());
    }

    /* ################################################################################################################ */
    /* ######################################### CZZ - 1542 PROFORMAS ################################################# */
    /* ################################################################################################################ */

    /**
     * Genera un ticket a partir de una proforma
     *
     * @param proforma ProformaDTO con los datos de la proforma obtenida
     * @param stage    Stage de la aplicación
     * @throws InitializeGuiException Excepción de inicialización de GUI
     */
    public void generarTicketDesdeProforma(ProformaDTO proforma, Stage stage) throws InitializeGuiException {
        log.debug("generarTicketDesdeProforma() - Generando ticket desde proforma...");

        try {
            IskaypetCabeceraTicket cabecera = (IskaypetCabeceraTicket) ticketPrincipal.getCabecera();
            String codTipoDocumento = proforma.getTipoDocumento();
            TipoDocumentoBean tipoDocumentoActivo = getDocumentoActivo();
            if (!tipoDocumentoActivo.getCodtipodocumento().equalsIgnoreCase(codTipoDocumento)) {
                log.debug("Cambiando el tipo de documento del ticket a " + codTipoDocumento);
                setDocumentoActivo(sesion.getAplicacion().getDocumentos().getDocumento(codTipoDocumento));
            }

            ClienteDTO cliente = proforma.getCliente();
            if (cliente != null) {
                log.debug("Generando datos de fidelización desde proforma para el cliente " + cliente.getIdComerzzia());
                FidelizacionBean fidelizado = buscarFidelizado(cliente, stage);
                ticketPrincipal.getCabecera().setDatosFidelizado(fidelizado);

                if (ProformaRestService.VALORES_DATOS_FACTURACION.contains(codTipoDocumento) && fidelizado != null) {
                    log.debug("Generando datos de facturación desde proforma para el cliente " + cliente.getIdComerzzia());
                    guardarDatosFacturacion(fidelizado);
                }
            }

            registrarCabeceraProforma(cabecera, proforma.getIdProforma(), proforma.isAutomatica());
            registrarLineasProforma(proforma.getLineas());
            registrarPagosProforma(proforma.getPagos());

            recalcularConPromociones();
            contadorLinea = ticketPrincipal.getLineas().size() + 1;
            log.debug("generarTicketDesdeProforma() - Ticket generado desde proforma exitosamente.");
        } catch (Exception e) {
            log.error("generarTicketDesdeProforma() - Error al inicializar la proforma", e);
            throw new InitializeGuiException("Ha ocurrido un error al inicializar la proforma", e);
        }
    }

    /**
     * Genera un ticket a partir de una proforma automatica
     *
     * @param proforma ProformaBean con los datos de la proforma obtenida
     * @param stage    Stage de la aplicación
     * @throws InitializeGuiException Excepción de inicialización de GUI
     */
    public void generarTicketDesdeProforma(ProformaBean proforma, Stage stage) throws InitializeGuiException {
        log.debug("generarTicketDesdeProforma() - Generando ticket desde proforma...");
        SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
        try {
            sqlSession.openSession(SessionFactory.openSession());

            DatosSesionBean datosSesion = new DatosSesionBean();
            datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
            datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());

            IskaypetCabeceraTicket cabecera = (IskaypetCabeceraTicket) ticketPrincipal.getCabecera();
            Long idTipoDocumento = proforma.getIdTipoDocumento();
            TipoDocumentoBean tipoDocumentoActivo = getDocumentoActivo();
            TipoDocumentoBean tipoDocumentoProforma = sesion.getAplicacion().getDocumentos().getDocumento(idTipoDocumento);
            if (!tipoDocumentoActivo.getCodtipodocumento().equalsIgnoreCase(tipoDocumentoProforma.getCodtipodocumento())) {
                log.debug("Cambiando el tipo de documento del ticket a " + tipoDocumentoProforma.getCodtipodocumento());
                setDocumentoActivo(tipoDocumentoProforma);
            }

            // Se asigna el idTicket al ticket principal
            if (ticketPrincipal.getIdTicket() == null) {
                ticketsService.setContadorIdTicket((Ticket) ticketPrincipal);
            }

            if (StringUtils.isNotBlank(proforma.getUidTicketOrigen())) {
                registrarDatosCabeceraOrigen(cabecera, proforma);
            } else if (StringUtils.isNotBlank(proforma.getCodigoFacturaOrigen())) {
                registrarDatosCabeceraAux(cabecera, proforma);
            }

            FidelizacionBean fidelizado = buscarClienteDesdeProforma(sqlSession, datosSesion, proforma.getIdProforma());
            if (fidelizado != null) {
                log.debug("Generando datos de fidelización desde proforma para el cliente " + fidelizado.getIdFidelizado());
                ticketPrincipal.getCabecera().setDatosFidelizado(fidelizado);

                if (ProformaRestService.VALORES_DATOS_FACTURACION.contains(tipoDocumentoProforma.getCodtipodocumento())) {
                    log.debug("Generando datos de facturación desde proforma para el cliente " + fidelizado.getIdFidelizado());
                    guardarDatosFacturacion(fidelizado);
                }
            }

            registrarCabeceraProforma(cabecera, proforma.getIdProforma(), proforma.getAutomatica());
            registrarLineasProforma(buscarLineasDesdeProforma(sqlSession, datosSesion, proforma.getIdProforma()));
            registrarPagosProforma(buscarPagosDesdeProforma(sqlSession, datosSesion, proforma.getIdProforma()));

            recalcularConPromociones();
            contadorLinea = ticketPrincipal.getLineas().size() + 1;
            log.debug("generarTicketDesdeProforma() - Ticket generado desde proforma exitosamente.");
        } catch (Exception e) {
            log.error("generarTicketDesdeProforma() - Error al inicializar la proforma", e);
            throw new InitializeGuiException("Ha ocurrido un error al inicializar la proforma", e);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * Registra la cabecera de proforma en el ticket
     *
     * @param cabecera   Cabecera del ticket
     * @param idProforma ID de la proforma
     * @param automatica Indica si la proforma es automática
     */
    private void registrarCabeceraProforma(IskaypetCabeceraTicket cabecera, String idProforma, boolean automatica) {
        log.debug("registrarCabeceraProforma() - Registrando cabecera de proforma en ticket.");
        ProformaXmlBean proformaXmlBean = new ProformaXmlBean();
        proformaXmlBean.setIdProforma(idProforma);
        proformaXmlBean.setAutomatica(automatica);
        cabecera.setProforma(proformaXmlBean);
        log.debug("registrarCabeceraProforma() - Cabecera de proforma registrada en ticket exitosamente.");
    }

    /**
     * Registra los datos de la cabecera de origen en el ticket
     * @param cabecera Cabecera del ticket
     * @param proforma ProformaBean con los datos de la proforma
     */
    private void registrarDatosCabeceraOrigen(IskaypetCabeceraTicket cabecera, ProformaBean proforma) throws DocumentoException {
        log.debug("registrarDatosCabeceraOrigen() - Registrando datos de cabecera de origen en ticket.");

        DatosDocumentoOrigenTicket datosOrigen =  new DatosDocumentoOrigenTicket();
        datosOrigen.setSerie(proforma.getSerieOrigen());
        datosOrigen.setCaja(proforma.getCajaOrigen());
        datosOrigen.setNumFactura(proforma.getNumalbOrigen());
        datosOrigen.setTienda(proforma.getCodalmOrigen());
        datosOrigen.setCodTicket(proforma.getCodigoFacturaOrigen());
        datosOrigen.setRecoveredOnline(false);

        TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(proforma.getTipoDocumentoOrigen());
        datosOrigen.setIdTipoDoc(tipoDocumentoActivo.getIdTipoDocumento());
        datosOrigen.setCodTipoDoc(tipoDocumentoActivo.getCodtipodocumento());
        datosOrigen.setDesTipoDoc(tipoDocumentoActivo.getDestipodocumento());

        cabecera.setDatosDocOrigen(datosOrigen);

        setEsDevolucion(true);
        log.debug("registrarDatosCabeceraOrigen() - Datos de cabecera de origen registrados en ticket exitosamente.");
    }

    /**
     * Registra los datos de la cabecera auxiliar en el ticket
     * @param cabecera Cabecera del ticket
     * @param proforma ProformaBean con los datos de la proforma
     */
    private void registrarDatosCabeceraAux(IskaypetCabeceraTicket cabecera, ProformaBean proforma) throws DocumentoException {
        log.debug("registrarDatosCabeceraAux() - Registrando datos de cabecera auxiliar en ticket.");
        DatosOrigenTicketBean datosDocumentoOrigenAux = new DatosOrigenTicketBean();
        datosDocumentoOrigenAux.setUidTicket(UID_TICKET_FALSO);
        datosDocumentoOrigenAux.setNumFactura(0L);
        datosDocumentoOrigenAux.setCodAlmacen(sesion.getAplicacion().getTienda().getCodAlmacen());
        datosDocumentoOrigenAux.setCodCaja(sesion.getAplicacion().getCodCaja());
        datosDocumentoOrigenAux.setSerie(proforma.getCodigoFacturaOrigen());
        datosDocumentoOrigenAux.setCodTicket(proforma.getCodigoFacturaOrigen());

        TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
        datosDocumentoOrigenAux.setIdTipoDocumento(tipoDocumentoActivo.getIdTipoDocumento());
        datosDocumentoOrigenAux.setCodTipoDocumento(tipoDocumentoActivo.getCodtipodocumento());
        datosDocumentoOrigenAux.setDesTipoDocumento(tipoDocumentoActivo.getDestipodocumento());

        cabecera.setDatosOrigenFalsos(datosDocumentoOrigenAux);

        setEsDevolucion(true);
        log.debug("registrarDatosCabeceraAux() - Datos de cabecera auxiliar registrados en ticket exitosamente.");
    }

    /**
     * Registra las lineas de proforma en el ticket
     *
     * @param lstLineasProforma Lista de líneas de proforma
     * @throws ProformaLineasEmptyException Excepción de líneas de proforma vacías
     * @throws LineaTicketException         Excepción de línea de ticket
     * @throws ArticuloNotFoundException    Excepción de artículo no encontrado
     */
    private void registrarLineasProforma(List<LineaProformaDTO> lstLineasProforma) throws ProformaLineasEmptyException, LineaTicketException, ArticuloNotFoundException, EmpresaException, ContadorServiceException, DocumentoException, TipoDocumentoNotFoundException {
        log.debug("registrarLineasProforma() - Registrando líneas de proforma en ticket.");
        if (lstLineasProforma == null || lstLineasProforma.isEmpty()) {
            log.error("registrarLineasProforma() - No se han encontrado líneas de proforma.");
            throw new ProformaLineasEmptyException();
        }

        for (LineaProformaDTO lineaProforma : lstLineasProforma) {
            nuevaLineaArticuloProforma(lineaProforma);
        }
        log.debug("registrarLineasProforma() - Líneas de proforma registradas en ticket exitosamente.");
    }

    /**
     * Crea una nueva línea de ticket a partir de una línea de proforma
     *
     * @param proformaLinea línea de proforma
     * @throws LineaTicketException      Excepción de línea de ticket
     * @throws ArticuloNotFoundException Excepción de artículo no encontrado
     */
    private void nuevaLineaArticuloProforma(LineaProformaDTO proformaLinea) throws LineaTicketException, ArticuloNotFoundException, EmpresaException, ContadorServiceException, DocumentoException, TipoDocumentoNotFoundException {
        log.debug("nuevaLineaArticuloProforma() - Creando línea de ticket desde proforma.");
        for (int i = 0; i < proformaLinea.getUnidades(); i++) {
            IskaypetLineaTicket lineaTicket = (IskaypetLineaTicket) nuevaLineaArticuloIskaypet(proformaLinea.getArticulo(), null, null, BigDecimal.ONE, null, null, false, false);
            registrarImporteLineaProforma(proformaLinea, lineaTicket);
            registrarDescuentoLineaProforma(proformaLinea);
            registrarLote(lineaTicket, proformaLinea);
            registrarCantidadSuministrada(lineaTicket, proformaLinea);
            registrarPlanSalud(lineaTicket);
            registrarAuditorias(lineaTicket, proformaLinea);
        }
        log.debug("nuevaLineaArticuloProforma() - Línea de ticket creada desde proforma exitosamente.");
    }

    private void registrarPlanSalud(IskaypetLineaTicket lineaTicket) {
        log.debug("registrarPlanSalud() - Registrando si es plan de salud en línea de ticket.");
        String planesSalud = variablesServices.getVariableAsString(VARIABLE_FAMILIA_PLANES_DE_SALUD);
        if (Arrays.asList(planesSalud.split(";")).contains(lineaTicket.getArticulo().getCodFamilia())) {
            log.debug("registrarPlanSalud() - El artículo " + lineaTicket.getCodArticulo() + " es un plan de salud.");
            lineaTicket.setPlanSalud(true);
        }
    }

    private void registrarAuditorias(IskaypetLineaTicket lineaTicket, LineaProformaDTO lineaProforma) throws EmpresaException, ContadorServiceException, DocumentoException, TipoDocumentoNotFoundException {
        log.debug("registrarAuditorias() - Registrando auditorías en línea de ticket.");
        if (CollectionUtils.isEmpty(lineaProforma.getAuditorias())) {
            log.debug("registrarAuditorias() - No hay auditorías registradas en la línea de ticket:" + lineaTicket.getIdLinea());
            return;
        }

        for (AuditoriaLineaProformaDTO auditoria : lineaProforma.getAuditorias()) {
            log.debug("registrarAuditorias() - Registrando auditoría: " + auditoria.getMotivo());
            registrarAuditoriaTicket(lineaTicket, auditoria);
            log.debug("registrarAuditorias() - Auditoría registrada exitosamente: " + auditoria.getMotivo());
        }
        log.debug("registrarAuditorias() - Auditorías registradas en línea de ticket exitosamente.");
    }

    @Autowired
    private AuditoriasService auditoriasService;

    private void registrarAuditoriaTicket(IskaypetLineaTicket lineaTicket, AuditoriaLineaProformaDTO auditoria) throws EmpresaException, ContadorServiceException, DocumentoException, TipoDocumentoNotFoundException {
        log.debug("registrarAuditoriaTicket() - Registrando auditoría en línea de ticket.");
        String tipoDoc = auditoria.getTipo();
        AuditoriaDto auditoriaDto = new AuditoriaDto();
        auditoriaDto.setUidActividad(sesion.getAplicacion().getUidActividad());
        auditoriaDto.setUidAuditoria(auditoria.getUidAuditoria());
        auditoriaDto.setCodMotivo(auditoria.getCodigo().toString());
        auditoriaDto.setObservaciones(auditoria.getObservaciones());
        auditoriaDto.setCodArticulo(lineaTicket.getCodArticulo());
        auditoriaDto.setDesArt(lineaTicket.getDesArticulo());
        auditoriaDto.setDesglose1(lineaTicket.getDesglose1());
        auditoriaDto.setDesglose2(lineaTicket.getDesglose2());
        auditoriaDto.setPrecioInicial(lineaTicket.getPrecioTotalTarifaOrigen().setScale(2, RoundingMode.HALF_UP));
        auditoriaDto.setPrecioFinal(lineaTicket.getPrecioTotalSinDto().setScale(2, RoundingMode.HALF_UP));
        auditoriasService.generarAuditoria(auditoriaDto, tipoDoc, null, Boolean.TRUE);
        auditoriasService.addAuditoriaLinea(lineaTicket, tipoDoc, auditoriaDto.getUidAuditoria(), auditoriaDto.getCodMotivo());
        log.debug("registrarAuditoriaTicket() - Auditoría registrada en línea de ticket exitosamente.");
    }

    /**
     * Registra la impresión de la línea de ticket
     *
     * @param linea línea de ticket
     */
    private void registrarImpresionLinea(IskaypetLineaTicket linea) {
        log.debug("registrarImpresionLinea() - Registrando impresión en línea de ticket.");
        linea.setImprimir(true);
        boolean esMedicamento = loteArticuloManager.esArticuloMedicamento(linea.getCodArticulo());
        if (esMedicamento) {
            log.debug("registrarImpresionLinea() - El artículo " + linea.getCodArticulo() + " es un medicamento.");
            // Comprobamos si se debe imprimir la linea medicamento
            boolean debeImprimir = variablesServices.getVariableAsBoolean(IskaypetLineaTicket.IMPRIMIR_MEDICAMENTOS, true);
            linea.setImprimir(debeImprimir);
        }
        log.debug("registrarImpresionLinea() - Impresión registrada en línea de ticket exitosamente.");

    }

    /**
     * Registra el importe en la línea de ticket
     *
     * @param proformaLinea línea de proforma
     * @param lineaTicket   línea de ticket
     */
    private void registrarImporteLineaProforma(LineaProformaDTO proformaLinea, IskaypetLineaTicket lineaTicket) {
        BigDecimal importe = proformaLinea.getImporte();
        if (importe != null) {
            log.debug("registrarImporte() - Registrando importe en línea de ticket");
            lineaTicket.setPrecioTotalSinDto(lineaTicket.getCantidad().abs().multiply(importe.divide(BigDecimal.valueOf(proformaLinea.getUnidades()), 2, RoundingMode.HALF_UP)));
            log.debug("registrarImporte() - Importe registrado en línea de ticket exitosamente.");
        }
    }

    /**
     * Registra el descuento en la línea de ticket
     *
     * @param proformaLinea línea de proforma
     */
    private void registrarDescuentoLineaProforma(LineaProformaDTO proformaLinea) {
        try {
            String descuentoLinea = proformaLinea.getDescuento();
            if (StringUtils.isNotBlank(descuentoLinea)) {
                log.debug("registrarDescuento() - Registrando descuento en línea de ticket");
                sesionPromociones.aplicarCupon(descuentoLinea, (TicketVentaAbono) ticketPrincipal);
                log.debug("registrarDescuento() - Descuento registrado en línea de ticket exitosamente.");
            }
        } catch (Exception e) {
            log.error("registrarDescuento() - Error al registrar descuento en línea de ticket", e);
        }
    }

    /**
     * Registra el lote en la línea de ticket
     *
     * @param lineaTicket   línea de ticket
     * @param proformaLinea línea de proforma
     */
    private void registrarLote(IskaypetLineaTicket lineaTicket, LineaProformaDTO proformaLinea) {
        if (StringUtils.isNotBlank(proformaLinea.getLote()) && StringUtils.isNotBlank(proformaLinea.getFechaCaducidad())) {
            boolean esMedicamento = loteArticuloManager.esArticuloMedicamento(lineaTicket.getCodArticulo());
            if (!esMedicamento) {
                log.warn("registrarLote() - El artículo " + lineaTicket.getCodArticulo() + " no es un medicamento.");
                return;
            }

            log.debug("registrarLote() - Registrando lote en línea de ticket");
            LoteDTO loteDto = new LoteDTO();
            loteDto.setLote(proformaLinea.getLote());
            loteDto.setFechaCaducidad(DateUtils.parseDate(proformaLinea.getFechaCaducidad(), "yyyy-MM-dd"));
            lineaTicket.setLote(loteDto);

            log.debug("registrarLote() - Lote registrado en línea de ticket exitosamente.");
        }
    }

    /**
     * Registra la cantidad suministrada en la línea de ticket
     *
     * @param lineaTicket   línea de ticket
     * @param proformaLinea línea de proforma
     */
    private void registrarCantidadSuministrada(IskaypetLineaTicket lineaTicket, LineaProformaDTO proformaLinea) {
        boolean esInyectable = inyectableArticuloManager.esArticuloInyectable(lineaTicket.getCodArticulo());
        if (!esInyectable) {
            log.warn("registrarCantidadSuministrada() - El artículo " + lineaTicket.getCodArticulo() + " no es un inyectable.");
            return;
        }

        BigDecimal cantidadConvertida = proformaLinea.getCantidadConvertida();
        BigDecimal cantidadSuministrada = proformaLinea.getCantidadSuministrada();
        String unidadMedidaSuministrada = proformaLinea.getUnidadMedidaSuministrada();
        if (cantidadConvertida != null && cantidadSuministrada != null && StringUtils.isNotBlank(unidadMedidaSuministrada)) {
            log.debug("registrarCantidadSuministrada() - Registrando cantidad suministrada en línea de ticket");
            Inyectable inyectable = new Inyectable();
            inyectable.setCantidadConvertida(cantidadConvertida);
            inyectable.setCantidadSuministrada(cantidadSuministrada);
            inyectable.setUnidadMedidaSuministrada(unidadMedidaSuministrada);
            lineaTicket.setInyectable(inyectable);
            calcularImporteCantidadConvertida(lineaTicket, cantidadConvertida);
            log.debug("registrarCantidadSuministrada() - Cantidad suministrada registrada en línea de ticket exitosamente.");
        }
    }

    /**
     * Calcula el importe de la cantidad convertida en la línea de ticket
     *
     * @param lineaTicket        línea de ticket
     * @param cantidadConvertida cantidad convertida
     */
    public void calcularImporteCantidadConvertida(IskaypetLineaTicket lineaTicket, BigDecimal cantidadConvertida) {
        if (lineaTicket != null && cantidadConvertida != null && !getTicket().isEsDevolucion()) {
            log.debug("calcularImporteCantidadConvertida() - Calculando importe de cantidad convertida en línea de ticket");
            BigDecimal importe = lineaTicket.getPrecioTotalSinDto();
            calcularImporteCantidadConvertida(lineaTicket, importe, cantidadConvertida);
            log.debug("calcularImporteCantidadConvertida() - Importe de cantidad convertida calculado en línea de ticket exitosamente.");
        }
    }

    /**
     * Calcula el importe de la cantidad convertida en la línea de ticket
     *
     * @param lineaTicket        línea de ticket
     * @param importe            importe a calcular
     * @param cantidadConvertida cantidad convertida
     */
    public void calcularImporteCantidadConvertida(IskaypetLineaTicket lineaTicket, BigDecimal importe, BigDecimal cantidadConvertida) {
        if (lineaTicket != null && importe != null && cantidadConvertida != null) {
            log.debug("calcularImporteCantidadConvertida() - Calculando importe de cantidad convertida en línea de ticket con importe");
            lineaTicket.setPrecioTotalSinDto(cantidadConvertida.abs().multiply(importe).setScale(2, RoundingMode.HALF_UP));
            log.debug("calcularImporteCantidadConvertida() - Importe de cantidad convertida con importe calculado en línea de ticket exitosamente.");
        }
    }


    /**
     * Registra los pagos de proforma en el ticket
     *
     * @param lstPagosProforma lista de pagos de proforma
     */
    private void registrarPagosProforma(List<PagoProformaDTO> lstPagosProforma) {
        log.debug("registrarPagosProforma() - Registrando pagos de la proforma seleccionada en ticket.");
        if (lstPagosProforma == null || lstPagosProforma.isEmpty()) {
            log.warn("registrarPagosProforma() - No se han encontrado pagos de proforma.");
            return;
        }

        int paymentId = 0;
        for (PagoProformaDTO pagoProforma : lstPagosProforma) {
            MedioPagoBean medioPagoBean = mediosPagosService.getMedioPago(pagoProforma.getMedioPago());
            if (medioPagoBean == null) {
                log.warn("registrarPagosProforma() - No se ha encontrado el medio de pago con código " + pagoProforma.getMedioPago());
                continue;
            }
            nuevaLineaPago(medioPagoBean.getCodMedioPago(), pagoProforma.getImportePago(), false, false, paymentId, true);
            paymentId++;
            log.debug("registrarPagosProforma() - Pago de proforma registrado en ticket con medio de pago " + pagoProforma.getMedioPago());
        }
        log.debug("registrarPagosProforma() - Pagos de proforma registrados en ticket exitosamente.");
    }

    /**
     * Método que busca un cliente fidelizado en el sistema
     *
     * @param cliente cliente a buscar
     * @param stage   ventana de la aplicación
     * @return FidelizacionBean con los datos del cliente fidelizado
     */
    private FidelizacionBean buscarFidelizado(ClienteDTO cliente, Stage stage) {
        try {
            Fidelizacion fidelizacion = (Fidelizacion) Dispositivos.getInstance().getFidelizacion();
            FidelizacionBean tarjetaFidelizado = null;
            if (fidelizacion instanceof IskaypetFidelizacion) {
                tarjetaFidelizado = ((IskaypetFidelizacion) fidelizacion).consultarTarjetaFidelizado(stage, cliente.getNumeroTarjeta(), ticketPrincipal.getCabecera().getUidActividad(), localFidelizadosMapper, localFidelizadosColectivosMapper, localColectivosMapper);
            } else {
                tarjetaFidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(stage, cliente.getNumeroTarjeta(), ticketPrincipal.getCabecera().getUidActividad());
            }

            log.debug("buscarFidelizado() - Tarjeta de fidelizado consultada: " + tarjetaFidelizado);
            return tarjetaFidelizado;
        } catch (ConsultaTarjetaFidelizadoException e) {
            log.error("Ha ocurrido un error al consultar la tarjeta de fidelizado", e);
            return null;
        }
    }

    /**
     * Método que comprueba si el ticket es una proforma
     *
     * @return true si es una proforma, false en caso contrario
     */
    public boolean esTicketProforma() {

        if (ticketPrincipal == null || ticketPrincipal.getCabecera() == null) {
            log.debug("esTicketProforma() - El ticket no esta inicializado");
            return false;
        }

        ICabeceraTicket cabeceraTicket = ticketPrincipal.getCabecera();
        if (cabeceraTicket instanceof IskaypetCabeceraTicket) {
            return ((IskaypetCabeceraTicket) cabeceraTicket).getProforma() != null;
        }
        return false;
    }

    /**
     * Método que comprueba si el artículo existe en el sistema
     *
     * @param codArt Código del artículo
     * @return true si el artículo existe, false en caso contrario
     */
    public boolean esArticulo(String codArt) {
        try {
            ConsultaCodigoArticuloResponse codigoArticuloResponse = lineasTicketServices.consultarLineaArticulo(codArt, null, null, true);
            if (codigoArticuloResponse == null) {
                log.debug("El artículo " + codArt + " no existe");
                return false;
            }
            log.debug("El artículo " + codArt + " existe");
            return true;
        } catch (Exception e) {
            log.error("Error al consultar el artículo " + codArt, e);
            return false;
        }
    }

    /**
     * Validación de datos de fidelizado para la factura
     *
     * @param cliente datos del cliente
     * @param stage   ventana de la aplicación
     * @return true si los datos son válidos, false en caso contrario
     */
    public boolean validarDatosFidelizadoFactura(FidelizacionBean cliente, Stage stage) {
        log.debug("validarDatosFidelizadoFactura() - Validando datos de factura del cliente " + cliente.getIdFidelizado());

        // Validar campos obligatorios
        List<String> errorMessages = validarCamposObligatorios(cliente);
        List<String> errorFormatos = validarFormatoCampos(cliente);
        if (!errorMessages.isEmpty() || !errorFormatos.isEmpty()) {
            mostrarErrores(errorMessages, errorFormatos, stage);
            return false;
        }

        // Validar documento
        if (!validarDocumento(cliente, stage)) {
            return false;
        }

        log.debug("validarDatosFidelizadoFactura() - Validación de datos de factura del cliente " + cliente.getIdFidelizado() + " finalizada con éxito");
        return true;
    }

    /**
     * Método que valida los campos obligatorios de la ficha del cliente
     *
     * @param cliente datos del cliente
     * @return lista de mensajes de error
     */
    private List<String> validarCamposObligatorios(FidelizacionBean cliente) {
        List<String> errorMessages = new ArrayList<>();
        if (StringUtils.isBlank(cliente.getNombre())) errorMessages.add(I18N.getTexto("nombre"));
        if (StringUtils.isBlank(cliente.getApellido())) errorMessages.add(I18N.getTexto("apellidos o razón social"));
        if (StringUtils.isBlank(cliente.getCodTipoIden())) errorMessages.add(I18N.getTexto("tipo de documento"));
        if (StringUtils.isBlank(cliente.getDocumento())) errorMessages.add(I18N.getTexto("documento"));
        if (StringUtils.isBlank(cliente.getCp())) errorMessages.add(I18N.getTexto("código postal"));
        if (StringUtils.isBlank(cliente.getLocalidad())) errorMessages.add(I18N.getTexto("localidad"));
        if (StringUtils.isBlank(cliente.getDomicilio())) errorMessages.add(I18N.getTexto("dirección"));
        if (StringUtils.isBlank(cliente.getProvincia())) errorMessages.add(I18N.getTexto("provincia"));
        if (StringUtils.isBlank(cliente.getCodPais())) errorMessages.add(I18N.getTexto("país"));
        return errorMessages;
    }

    private List<String> validarFormatoCampos(FidelizacionBean cliente) {
        List<String> errorMessages = new ArrayList<>();
        ValidadorCodigoPostal validadorCP = ValidadorCodigoPostalFactory.getValidadorCodigoPostal(cliente.getCodPais());
        ContextoValidadorCodigoPostal context = new ContextoValidadorCodigoPostal();
        context.setValidadorCodigoPostal(validadorCP);
        try {
            context.ejecutaValidacionPostal(cliente.getCp());
        } catch (ValidadorCodigoPostalException e) {
            String error;
            if(!Objects.equals(cliente.getCodPais(), "ES") && !Objects.equals(cliente.getCodPais(), "PT")) {
                error = I18N.getTexto("Código postal no válido: debe tener entre 4 y 8 caracteres, con letras, números y un guion (no al inicio ni al final).");
            } else {
                error = I18N.getTexto("El código postal del cliente no es válido. Por favor, asegúrese de que el código tenga el formato correcto {0} y solo contenga números.", e.getMessage());
            }
            errorMessages.add(error);
        }
        return errorMessages;
    }

    /**
     * Muestra los errores de validación en una ventana de error
     *
     * @param errorMessages lista de mensajes de error
     * @param stage         ventana de la aplicación
     */
    private void mostrarErrores(List<String> errorMessages, List<String> errorFormatos, Stage stage) {
        String mensaje = "";
        if(!errorMessages.isEmpty()){
            mensaje = errorMessages.size() == 1
                    ? I18N.getTexto("Es necesario rellenar el siguiente dato en la ficha del cliente: ") + errorMessages.get(0)
                    : I18N.getTexto("Es necesario rellenar los siguientes datos en la ficha del cliente: ") + String.join(", ", errorMessages);
            log.error("Errores en los datos del cliente: " + String.join(", ", errorMessages));
        }
        if(!errorFormatos.isEmpty()){
            mensaje += "\n" + errorFormatos;
            log.error("Errores en el formato de datos del cliente: " + String.join(", ", errorFormatos));
        }
        VentanaDialogoComponent.crearVentanaError(mensaje, stage);
    }

    /**
     * Método que valida el documento de identificación del cliente
     *
     * @param cliente datos del cliente
     * @param stage   ventana de la aplicación
     * @return true si el documento es válido, false en caso contrario
     */
    private boolean validarDocumento(FidelizacionBean cliente, Stage stage) {
        try {
            log.debug("validarDocumento() - Validando documento de identificación del cliente " + cliente.getIdFidelizado());
            TiposIdentBean tipo = obtenerTipoIdentificacion(cliente);
            if (tipo == null) {
                log.error("El tipo de documento " + cliente.getCodTipoIden() + " no está configurado en el sistema");
                VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El tipo de documento {0} no está configurado", cliente.getCodTipoIden()), stage);
                return false;
            }
            return validarDocumentoConClase(cliente, tipo, stage);
        } catch (Exception e) {
            log.error("Error al validar el documento de identificación del cliente", e);
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error al validar el documento de identificación del cliente"), stage);
            return false;
        }
    }

    /**
     * Método que obtiene el tipo de identificación del cliente
     *
     * @param cliente datos del cliente
     * @return TiposIdentBean con los datos del tipo de identificación
     * @throws TiposIdentNotFoundException Excepción lanzada si no se encuentra el tipo de identificación
     * @throws TiposIdentServiceException  Excepción lanzada si ocurre un error al consultar el tipo de identificación
     */
    private TiposIdentBean obtenerTipoIdentificacion(FidelizacionBean cliente) throws TiposIdentNotFoundException, TiposIdentServiceException {
        log.debug("obtenerTipoIdentificacion() - Obteniendo tipo de identificación del cliente " + cliente.getIdFidelizado());
        List<TiposIdentBean> tiposIdent = tiposIdentService.consultarTiposIdent(null, true, cliente.getCodPais());
        TiposIdentBean tiposIdentBean = tiposIdent.stream()
                .filter(el -> el.getCodTipoIden().equals(cliente.getCodTipoIden()))
                .findFirst()
                .orElse(null);
        log.debug("obtenerTipoIdentificacion() - Tipo de identificación obtenido: " + tiposIdentBean);
        return tiposIdentBean;
    }

    /**
     * Método que valida el documento de identificación del cliente con la clase de validación
     *
     * @param cliente datos del cliente
     * @param tipo    tipo de identificación
     * @param stage   ventana de la aplicación
     * @return true si el documento es válido, false en caso contrario
     * @throws ClassNotFoundException                    Excepción lanzada si no se encuentra la clase de validación
     * @throws ValidadorDocumentoIdentificacionException Excepción lanzada si ocurre un error al validar el documento
     * @throws InstantiationException                    Excepción lanzada si ocurre un error al instanciar la clase de validación
     * @throws IllegalAccessException                    Excepción lanzada si ocurre un error al acceder a la clase de validación
     */
    private boolean validarDocumentoConClase(FidelizacionBean cliente, TiposIdentBean tipo, Stage stage) throws ClassNotFoundException, ValidadorDocumentoIdentificacionException, InstantiationException, IllegalAccessException {
        log.debug("validarDocumentoConClase() - Validando documento con clase de validación");
        String claseValidacion = tipo.getClaseValidacion();
        if (claseValidacion != null) {
            log.debug("validarDocumentoConClase() - Clase de validación: " + claseValidacion);
            IValidadorDocumentoIdentificacion validador = (IValidadorDocumentoIdentificacion) Class.forName(claseValidacion).newInstance();
            if (!validador.validarDocumentoIdentificacion(cliente.getDocumento())) {
                log.debug("validarDocumentoConClase() - Documento no válido");
                VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Es necesario corregir el documento en la ficha del cliente, documento no válido."), stage);
                return false;
            }
        }
        log.debug("validarDocumentoConClase() - Documento válido");
        return true;
    }

    /**
     * Método que guarda los datos de facturación del cliente en el ticket
     *
     * @param cliente datos del cliente
     */
    public void guardarDatosFacturacion(FidelizacionBean cliente) {
        log.debug("guardarDatosFactura() - Guardando datos de factura del cliente " + cliente.getIdFidelizado());
        DatosFactura datosFactura = new DatosFactura();

        datosFactura.setNombre(String.join(" ", cliente.getNombre(), cliente.getApellido()));
        datosFactura.setTipoIdentificacion(cliente.getCodTipoIden());
        datosFactura.setCif(cliente.getDocumento());
        datosFactura.setCp(cliente.getCp());
        datosFactura.setDomicilio(cliente.getDomicilio());
        datosFactura.setProvincia(cliente.getProvincia());
        datosFactura.setPoblacion(cliente.getPoblacion());
        datosFactura.setLocalidad(cliente.getLocalidad());
        datosFactura.setPais(cliente.getCodPais());

        ((TicketVenta) getTicket()).setDatosFacturacion(datosFactura);
        log.debug("guardarDatosFactura() - Datos de factura del cliente " + cliente.getIdFidelizado() + " guardados con éxito");
    }

    /**
     * Método que obtiene el nuevo documento activo
     *
     * @return TipoDocumentoBean con los datos del nuevo documento activo (estandar) o en caso de ser proforma, el documento activo
     * @throws DocumentoException Excepción lanzada si ocurre un error al obtener el documento activo
     */
    @Override
    public TipoDocumentoBean getNuevoDocumentoActivo() throws DocumentoException {
        log.debug("getNuevoDocumentoActivo() - Obteniendo nuevo documento activo");
        if (esTicketProforma()) {
            log.debug("getNuevoDocumentoActivo() - El ticket es una proforma, obteniendo documento activo");
            return getDocumentoActivo();
        }
        log.debug("getNuevoDocumentoActivo() - El ticket no es una proforma, obteniendo nuevo documento activo (estandar)");
        return super.getNuevoDocumentoActivo();
    }

    private FidelizacionBean buscarClienteDesdeProforma(SqlSession sqlSession, DatosSesionBean datosSesionBean, String idProforma) {
        log.debug("buscarClienteDesdeProforma() - Buscando cliente desde proforma con id " + idProforma);
        ProformaFidelizacionBeanKey proformaFidelizacion = proformaFidelizadoService.consultar(sqlSession, datosSesionBean, idProforma);
        if (proformaFidelizacion == null) {
            log.debug("buscarClienteDesdeProforma() - No se ha encontrado el cliente desde proforma con id " + idProforma);
            return null;
        }

        FidelizadoBean datosFidelizacion = fidelizacionService.buscarFidelizadoOfflinePorId(proformaFidelizacion.getIdFidelizado());
        FidelizacionBean fidelizado = FidelizacionService.convertApiToPos(datosFidelizacion);
        log.debug("buscarClienteDesdeProforma() - Cliente encontrado desde proforma: " + fidelizado);
        return fidelizado;
    }

    private List<LineaProformaDTO> buscarLineasDesdeProforma(SqlSession sqlSession, DatosSesionBean datosSesionBean, String idProforma) throws ProformaLineasEmptyException {
        log.debug("buscarLineasDesdeProforma() - Buscando líneas desde proforma con id " + idProforma);

        ParametroBuscarProformaLineaBean param = new ParametroBuscarProformaLineaBean();
        param.setUidActividad(datosSesionBean.getUidActividad());
        param.setIdProforma(idProforma);

        List<ProformaLineaBean> lstLineas = proformaLineaService.consultar(sqlSession, datosSesionBean, param);
        if (lstLineas.isEmpty()) {
            throw new ProformaLineasEmptyException("No se han encontrado líneas de proforma");
        }

        List<LineaProformaDTO> lstLineasProforma = new ArrayList<>();
        for (ProformaLineaBean linea: lstLineas) {
            log.debug("buscarLineasDesdeProforma() - Línea de proforma encontrada: " + linea.getLinea() + " con id: " + linea.getIdProforma());

            LineaProformaDTO lineaProforma = convertLineaProforma(linea);
            lineaProforma.setAuditorias(buscarAuditoriasLineaProforma(sqlSession, datosSesionBean, linea.getIdProforma(), linea.getLinea()));

            lstLineasProforma.add(lineaProforma);
        }
        log.debug("buscarLineasDesdeProforma() - Líneas de proforma encontradas: " + lstLineasProforma.size());
        return lstLineasProforma;
    }


    private LineaProformaDTO convertLineaProforma(ProformaLineaBean linea) {
        log.debug("convertLineaProforma() - Convirtiendo línea: " +linea.getLinea() + " a LineaProformaDTO de la proforma con id: " + linea.getIdProforma());
        LineaProformaDTO lineaProforma = new LineaProformaDTO();
        lineaProforma.setArticulo(linea.getCodart());
        lineaProforma.setUnidades(linea.getUnidades());
        lineaProforma.setImporte(linea.getImporte());
        lineaProforma.setDescuento(linea.getDescuento());
        lineaProforma.setLote(linea.getLote());
        if (linea.getFechaCaducidad() != null) {
            lineaProforma.setFechaCaducidad(DateUtils.formatDate(linea.getFechaCaducidad(), DateUtils.PATRON_FECHA_LARGA));
        }
        lineaProforma.setCantidadSuministrada(linea.getCantidadSuministrada());
        lineaProforma.setUnidadMedidaSuministrada(linea.getUnidadMedidaSuministrada());
        log.debug("convertLineaProforma() - Línea convertida: " + lineaProforma);
        return lineaProforma;
    }

    private List<AuditoriaLineaProformaDTO> buscarAuditoriasLineaProforma(SqlSession sqlSession, DatosSesionBean datosSesionBean, String idProforma, Integer linea) {
        log.debug("buscarAuditoriasLineaProforma() - Buscando auditorías de línea de proforma con id " + idProforma + " y línea " + linea);
        ParametroBuscarAuditoriaLineaProformaBean param = new ParametroBuscarAuditoriaLineaProformaBean();
        param.setUidActividad(datosSesionBean.getUidActividad());
        param.setIdProforma(idProforma);
        param.setLinea(linea);

        List<AuditoriaLineaProformaBean> lstAuditorias = auditoriaLineaProformaService.consultar(sqlSession, datosSesionBean, param);
        if (lstAuditorias.isEmpty()) {
            log.debug("buscarAuditoriasLineaProforma() - No se han encontrado auditorías de línea de proforma");
            return Collections.emptyList();
        }
        List<AuditoriaLineaProformaDTO> lstAuditoriasDTO = lstAuditorias.stream()
                .map(this::convertAuditoriaLineaProforma)
                .collect(Collectors.toList());

        log.debug("buscarAuditoriasLineaProforma() - Auditorías de línea de proforma encontradas: " + lstAuditoriasDTO.size());
        return lstAuditoriasDTO;
    }

    private AuditoriaLineaProformaDTO convertAuditoriaLineaProforma(AuditoriaLineaProformaBean linea) {
        log.debug("convertAuditoriaLineaProforma() - Convirtiendo auditoría de línea de proforma: " + linea.getUidAuditoria() + " a AuditoriaLineaProformaDTO");
        AuditoriaLineaProformaDTO auditoria = new AuditoriaLineaProformaDTO();
        auditoria.setUidAuditoria(linea.getUidAuditoria());
        auditoria.setTipo(linea.getTipoAuditoria());
        auditoria.setCodigo(linea.getCodMotivo());
        auditoria.setObservaciones(linea.getObservaciones());
        log.debug("convertAuditoriaLineaProforma() - Auditoría de línea de proforma convertida: " + auditoria);
        return auditoria;
    }

    private List<PagoProformaDTO> buscarPagosDesdeProforma(SqlSession sqlSession, DatosSesionBean datosSesionBean, String idProforma) throws ProformaPagosEmptyException {
        log.debug("buscarPagosDesdeProforma() - Buscando pagos desde proforma con id " + idProforma);
        ParametroBuscarProformaPagoBean param = new ParametroBuscarProformaPagoBean();
        param.setUidActividad(datosSesionBean.getUidActividad());
        param.setIdProforma(idProforma);

        List<ProformaPagoBean> lstPagos = proformaPagoService.consultar(sqlSession, datosSesionBean, param);
        if (lstPagos.isEmpty()) {
            log.debug("buscarPagosDesdeProforma() - No se han encontrado pagos de proforma");
            throw new ProformaPagosEmptyException("No se han encontrado pagos de proforma");
        }

        List<PagoProformaDTO> lstPagosProforma = lstPagos.stream().map(this::convertPagoProforma).collect(Collectors.toList());
        log.debug("buscarPagosDesdeProforma() - Pagos de proforma encontrados: " + lstPagosProforma.size());
        return lstPagosProforma;
    }

    private PagoProformaDTO convertPagoProforma(ProformaPagoBean pago) {
        log.debug("convertPagoProforma() - Convirtiendo pago: " + pago.getLinea() + " a PagoProformaDTO de la proforma con id: " + pago.getIdProforma());
        PagoProformaDTO pagoProforma = new PagoProformaDTO();
        pagoProforma.setMedioPago(pago.getCodmedpag());
        pagoProforma.setImportePago(pago.getImportePago());
        log.debug("convertPagoProforma() - Pago convertido: " + pagoProforma);
        return pagoProforma;
    }

    public void facturarProforma(DatosSesionBean datosSesion, ProformaBean proforma, Stage stage) {
        log.debug("facturarProforma() - Facturando proforma con id " + proforma.getIdProforma());
        SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
        try {
            sqlSession.openSession(SessionFactory.openSession());

            // Comprobamos que existe la proforma y que no ha sido facturada
            ProformaBean proformaBean = proformaService.consultar(sqlSession, datosSesion, proforma.getIdProforma());
            if (proformaBean.getEstadoActual().equalsIgnoreCase(ProformaBean.ESTADO_FACTURADA)) {
                log.error("La proforma ya ha sido facturada");
                return;
            }

            // Inicializamos el ticket y facturamos la proforma
            inicializarTicket();
            generarTicketDesdeProforma(proforma, stage);
            validarTicketProformaAutomatica();
            guardarCopiaSeguridadTicket();
            ticketsService.registrarTicket((Ticket) ticketPrincipal, documentoActivo, false);
            proforma.setEstadoActual(ProformaBean.ESTADO_FACTURADA);
            log.debug("Proforma facturada correctamente con id: " + proforma.getIdProforma());
        } catch (DocumentoException | PromocionesServiceException | InitializeGuiException |
                 TicketsServiceException e) {
            proforma.setEstadoActual(ProformaBean.ESTADO_ERROR);
            log.error("Error al inicializar el ticket para la proforma con id:" + proforma.getIdProforma(), e);
        } catch (ProformaNotFoundException e) {
            proforma.setEstadoActual(ProformaBean.ESTADO_ERROR);
            log.error("Error al consultar la proforma con id:" + proforma.getIdProforma(), e);
        } catch (ProformaException e) {
            proforma.setEstadoActual(ProformaBean.ESTADO_ERROR);
            log.error("Error al validar la proforma con id:" + proforma.getIdProforma(), e);
        } finally {
            finalizarTicket();
            try {
                proformaService.modificar(sqlSession, datosSesion, proforma);
            } catch (ProformaNotFoundException e) {
                log.error("Error al modificar la proforma con id: " + proforma.getIdProforma(), e);
            }
            sqlSession.close();
        }
    }

    private void validarTicketProformaAutomatica() throws ProformaException {
        log.debug("validarTicketProformaAutomatica() - Validando importes de la proforma automática");
        BigDecimal importesLineas = ((List<LineaTicket>)ticketPrincipal.getLineas()).stream()
                .map(LineaTicket::getImporteTotalConDto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal importesPagos = ((List<PagoTicket>)ticketPrincipal.getPagos()).stream()
                .map(PagoTicket::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (importesLineas.compareTo(importesPagos) != 0) {
            log.error("validarTicketProformaAutomatica() - La suma de los importes de las lineas no coincide con la suma de los importes de los pagos");
            throw new ProformaException("La suma de los importes de las lineas no coincide con la suma de los importes de los pagos");
        }
        log.debug("validarTicketProformaAutomatica() - La suma de los importes de las lineas coincide con la suma de los importes de los pagos");


    }

    @Override
    public void inicializarTicket() throws DocumentoException, PromocionesServiceException {
        if (registrandoTicket.get()) {
            log.warn("inicializarTicket() - ignorado mientras se registra el ticket");
            return;
        }
        mostrarTrazaReducida();
        super.inicializarTicket();
    }

    @Override
    public void nuevoTicket() throws DocumentoException, PromocionesServiceException {
        if (registrandoTicket.get()) {
            log.warn("nuevoTicket() - ignorado mientras se registra el ticket");
            return;
        }
        super.nuevoTicket();
    }

    public void mostrarTrazaReducida() {
    	// lustrum - 136.030 - Ticket sin lineas
    	try {
			log.debug("mostrarTrazaReducida() - inicializarTicket() invocado. Stack trace reducido:\n" +
			        Arrays.stream(Thread.currentThread().getStackTrace())
			              .skip(2).limit(6)
			              .map(StackTraceElement::toString)
			              .collect(Collectors.joining("\n"))
      );
		} catch (Exception e) {
			log.error("mostrarTrazaReducida() - Ha ocurrido un error mostrando la traza: "+ e.getMessage());
		}
    }
}
