package com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.ticketRegalo;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ticket.regalo.RegaloDto;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.gestiontickets.ticketRegalo.LineaTicketRegaloGui;
import com.comerzzia.pos.gui.ventas.gestiontickets.ticketRegalo.TicketRegaloController;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import javafx.scene.input.KeyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController.*;

/**
 * IER-29 GAP Ticket regalo
 */

@Component
@Primary
public class IskaypetTicketRegaloController extends TicketRegaloController {

    private static final Long ID_TIPO_DOC_TICKET_REGALO = 800L;

    @Autowired
    private TicketsService ticketsService;

    @Autowired
    private ServicioContadores servicioContadores;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);
		tcBtSelec.setEditable(false);
	}
	
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, KeyCode.HOME.getName(), "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, KeyCode.END.getName(), "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        //listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_check.png", null, null, "ACCION_TABLA_SELECCIONAR_TODO", "REALIZAR_ACCION")); 
        //listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_cross.png", null, null, "ACCION_TABLA_BORRAR_SELECCION", "REALIZAR_ACCION"));
        
        return listaAcciones;
    }

    @Override
    public void accionAceptar() {
        boolean hayLineasSelec = false;

        try {

            for (LineaTicketRegaloGui lineaGui : lineas) {
                if (lineaGui.isLineaSelec()) {
                    LineaTicket lineaticket = (LineaTicket) ticketRegalo.getLinea(lineaGui.getIdLinea());
                    if (!lineaticket.isImprimirTicketRegalo()) {
                        lineaticket.setImprimirTicketRegalo(true);
                        lineaticket.setCantidad(BigDecimal.ZERO);
                    }

                    BigDecimal cantidad = lineaticket.getCantidad();
                    lineaticket.setCantidad(cantidad.add(BigDecimal.ONE));
                    hayLineasSelec = true;
                }
            }

            if (hayLineasSelec) {
                String formatoTicketRegalo = sesion.getAplicacion().getDocumentos().getDocumento(ticketRegalo.getCabecera().getCodTipoDocumento()).getFormatoImpresionTicketRegalo();
                if (formatoTicketRegalo != null) {
                    ticketRegalo.getCabecera().setFormatoImpresion(formatoTicketRegalo);
                    HashMap<String, Object> mapaParametrosTicket = new HashMap<>();
                    mapaParametrosTicket.put(IMPRIMIR_LOGO, requierImprimirLogo(variablesServices));
                    mapaParametrosTicket.put(TICKET, ticketRegalo);
                    mapaParametrosTicket.put(LINEAS, generarLineasAgrupadasTicketRegalo(ticketRegalo.getLineas()));
                    mapaParametrosTicket.put(URL_QR, variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
                    try {
                        guardarTicketRegalo(ticketRegalo.getUidTicket());
                        ServicioImpresion.imprimir(ticketRegalo.getCabecera().getFormatoImpresionTicketRegalo(), mapaParametrosTicket);
                    } catch (DeviceException e) {
                        VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), e);
                    }
                } else {
                    VentanaDialogoComponent.crearVentanaError("No existe un formato de ticket regalo para el tipo de documento del ticket original.", getStage());
                }

                getStage().close();
            } else {
                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay ninguna línea seleccionada."), getStage());
            }
        } catch (DocumentoException ex) {
            log.trace("No se encontró el documento del ticket regalo.", ex);
        }
    }

    private void guardarTicketRegalo(String uidTicketOrigen) {
        log.debug("guardarTicketRegalo() - Guardando Ticket Regalo en BBDD");
        SqlSession sqlSession = new SqlSession();
        try {

            String uidActividad = sesion.getAplicacion().getUidActividad();

            TicketBean ticket = new TicketBean();
            ticket.setIdTicket(servicioContadores.obtenerValorContador("ID_TICKET", uidActividad)); //todo esto está bien?
            ticket.setUidActividad(uidActividad);
            String uid = UUID.randomUUID().toString();
            ticket.setUidTicket(uid);
            ticket.setLocatorId(uid);
            ticket.setCodAlmacen(sesion.getAplicacion().getCodAlmacen());
            ticket.setCodcaja(sesion.getAplicacion().getCodCaja());
            ticket.setFecha(new Date());
            ticket.setIdTipoDocumento(ID_TIPO_DOC_TICKET_REGALO);
            ticket.setCodTicket("*");
            ticket.setFirma("*");
            ticket.setSerieTicket("*");

            RegaloDto regaloDto = new RegaloDto();
            regaloDto.setUidActividad(uidActividad);
            regaloDto.setFechaEnvio(new Date());
            regaloDto.setUidTicketOrigen(uidTicketOrigen);

            byte[] xmlTicketRegalo = MarshallUtil.crearXML(regaloDto);
            log.debug("guardarTicketRegalo() - XML del ticket asociado al ticket regalo: " + new String(xmlTicketRegalo));
            ticket.setTicket(xmlTicketRegalo);

            sqlSession.openSession(SessionFactory.openSession());
            ticketsService.insertarTicket(sqlSession, ticket, false);
            sqlSession.commit();
        }
        catch (Exception e) {
            log.error("guardarTicketRegalo() - Ha habido un error al guardar el ticket regalo: " + e.getMessage(), e);
            sqlSession.rollback();
        }
        finally {
            sqlSession.close();
        }
    }

    public static List<IskaypetLineaTicket> generarLineasAgrupadasTicketRegalo(List<IskaypetLineaTicket> lineasOriginales) {
        // Agrupamos las lineas por codigo de articulo y las ordenamos por su codigo de articulo
        List<IskaypetLineaTicket> lineas = new ArrayList<>();
        for (IskaypetLineaTicket linea : lineasOriginales) {
            IskaypetLineaTicket lineaAgrupada = lineas.stream().filter(el -> el.getCodArticulo().equals(linea.getCodArticulo())).findFirst().orElse(null);
            if (lineaAgrupada != null) {
                lineaAgrupada.setCantidad(lineaAgrupada.getCantidad().add(linea.getCantidad()));
            }
            else {
                lineaAgrupada = (IskaypetLineaTicket) linea.clone();
                lineas.add(lineaAgrupada);
            }
        }

        Collections.sort(lineas, Comparator.comparing(IskaypetLineaTicket::getCodArticulo));

        return lineas;
    }
}
