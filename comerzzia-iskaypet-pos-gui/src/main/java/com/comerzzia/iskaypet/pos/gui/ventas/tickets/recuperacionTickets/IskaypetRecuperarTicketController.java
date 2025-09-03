package com.comerzzia.iskaypet.pos.gui.ventas.tickets.recuperacionTickets;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.LineaTicketAparcadoGui;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.RecuperarTicketController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

@Primary
@Component
public class IskaypetRecuperarTicketController extends RecuperarTicketController {

    @Autowired
    private Sesion sesion;

    @FXML
    protected TableColumn<IskaypetLineaTicketAparcadoGui,String> tcFecha;
    
    @FXML
    protected TableColumn<IskaypetLineaTicketAparcadoGui,String> tcImporte;
    
    @FXML
    protected TableColumn<IskaypetLineaTicketAparcadoGui,String> tcCliente;
    
    @FXML
    protected TableColumn<IskaypetLineaTicketAparcadoGui,String> tcCaja;
    
    @FXML
    protected TableColumn<IskaypetLineaTicketAparcadoGui,String> tcCajero;
    
    @FXML
    protected TableColumn<IskaypetLineaTicketAparcadoGui,String> tcCodDoc;
    
    @FXML
    protected TableColumn<IskaypetLineaTicketAparcadoGui,String> tcContrato;
    
	@Autowired
	private IskaypetEvicertiaService evicertiaService;

    /**
     * CZZ-490 - Sobreescrito metodo para añadir la nueva columna de contrato.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcCajero.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCajero", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCliente.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCliente", 2,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCaja.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCaja", 2,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcImporte", 2,CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
        tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFechaHora("tbTickets", "tcFecha", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCodDoc.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcCodDoc", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcContrato.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTickets", "tcContrato", null,CellFactoryBuilder.ESTILO_ALINEACION_CEN));

        
        tcCajero.setCellValueFactory(new PropertyValueFactory<IskaypetLineaTicketAparcadoGui,String>("cajero"));
        tcCliente.setCellValueFactory(new PropertyValueFactory<IskaypetLineaTicketAparcadoGui,String>("cliente"));
        tcCaja.setCellValueFactory(new PropertyValueFactory<IskaypetLineaTicketAparcadoGui,String>("caja"));
        tcFecha.setCellValueFactory(new PropertyValueFactory<IskaypetLineaTicketAparcadoGui,String>("fecha"));
        tcImporte.setCellValueFactory(new PropertyValueFactory<IskaypetLineaTicketAparcadoGui,String>("importe"));
        tcCodDoc.setCellValueFactory(new PropertyValueFactory<IskaypetLineaTicketAparcadoGui,String>("codDoc"));
        tcContrato.setCellValueFactory(new PropertyValueFactory<IskaypetLineaTicketAparcadoGui,String>("contrato"));
        
        tcCajero.prefWidthProperty().bind(tbTickets.widthProperty().divide(7));
        tcCliente.prefWidthProperty().bind(tbTickets.widthProperty().divide(7));
        tcCaja.prefWidthProperty().bind(tbTickets.widthProperty().divide(7));
        tcFecha.prefWidthProperty().bind(tbTickets.widthProperty().divide(7));
        tcCodDoc.prefWidthProperty().bind(tbTickets.widthProperty().divide(7)); 
        tcImporte.prefWidthProperty().bind(tbTickets.widthProperty().divide(7));
        tcContrato.prefWidthProperty().bind(tbTickets.widthProperty().divide(7));
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void initializeForm() throws InitializeGuiException {
        this.ticketManager = (TicketManager)this.getDatos().get("TICKET_KEY");
        Long tipoDoc = (Long)this.getDatos().get("TIPO_DOCUMENTO");
        if (this.ticketManager.isTicketVacio()) {
            try {
                List<TicketAparcadoBean> ticketsAparcado = this.ticketManager.recuperarTicketsAparcados(tipoDoc);
                this.ticketsAparcadosTabla = FXCollections.observableArrayList();

                for (TicketAparcadoBean ticket : ticketsAparcado) {
                    this.ticketsAparcadosTabla.add(createLineaTicketAparcadoGui(ticket));
                }

                this.tbTickets.setItems(this.ticketsAparcadosTabla);
            } catch (TicketsServiceException var5) {
                TicketsServiceException ex = var5;
                this.log.error("initializeForm() - Error cargando tickets aparcados: " + ex.getMessage(), ex);
            }
        }

        this.accionTablaPrimerRegistro();
    }

    private LineaTicketAparcadoGui createLineaTicketAparcadoGui(TicketAparcadoBean ticket) {
        String codClienteOriginal = ticket.getCodCliente();
        try {
            TipoDocumentoBean tipoDocumentoBean = sesion.getAplicacion().getDocumentos().getDocumento(ticket.getIdTipoDocumento());

            IskaypetTicketVentaAbono ticketRecuperado = (IskaypetTicketVentaAbono) MarshallUtil.leerXML(ticket.getTicket(), ticketManager.getTicketClasses(tipoDocumentoBean).toArray(new Class[] {}));
            if (ticketRecuperado.getDatosFidelizado() != null) {
                FidelizacionBean fidelizadoBean = ticketRecuperado.getDatosFidelizado();
                ticket.setCodCliente( String.join(" ", fidelizadoBean.getNombre(), fidelizadoBean.getApellido()));
                log.debug("Ticket recuperado con fidelizado: " + ticket.getCodCliente());
            }
            IskaypetLineaTicketAparcadoGui lineaTicketAparcado = new IskaypetLineaTicketAparcadoGui(ticket);
            lineaTicketAparcado.setContrato(evicertiaService.tieneContratoAnimalLineas(ticketRecuperado) ? "SI" : "NO");
            
            return lineaTicketAparcado;
        } catch (Exception e) {
            log.error("Error al recuperar ticket, se recupera el ticket original", e);
            ticket.setCodCliente(codClienteOriginal);
            return new LineaTicketAparcadoGui(ticket);
        }
    }
    
}
