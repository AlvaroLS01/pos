package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.origen;

import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagoTicketGui;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController.TICKET;

@Component
public class PagosOrigenController extends WindowController implements Initializable {

    protected Logger log = Logger.getLogger(getClass());

    protected ObservableList<PagoTicketGui> lineas;

    @FXML
    protected TableView<PagoTicketGui> tbLineas;
    @FXML
    protected TableColumn<PagoTicketGui, String> tcLineasFormasPago;
    @FXML
    protected TableColumn<PagoTicketGui, BigDecimal> tcLineasImporte;
    @FXML
    protected Label lbTotalDetalle;

    @FXML
    protected Button btAceptar;
    @FXML
    protected Button btCancelar;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        tbLineas.setPlaceholder(new Label(""));
        lineas = FXCollections.observableList(new ArrayList());
        tcLineasFormasPago.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasFormasPago", null, "leftCol"));
        tcLineasImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasImporte", "rightCol"));
        tbLineas.setItems(lineas);
        tcLineasFormasPago.setCellValueFactory(new PropertyValueFactory("formaPago"));
        tcLineasImporte.setCellValueFactory(new PropertyValueFactory("importe"));
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {

    }

    @Override
    public void initializeForm() throws InitializeGuiException {
        IskaypetTicketVentaAbono ticketVentaAbono = (IskaypetTicketVentaAbono) getDatos().get(TICKET);
        if (ticketVentaAbono != null) {
            cargarDatosPantalla(ticketVentaAbono);
        }

    }

    @Override
    public void initializeFocus() {

    }

    @FXML
    public void aceptar() {
        getStage().close();
    }


    public void cargarDatosPantalla(TicketVentaAbono ticketVentaAbono) {
        log.debug("cargarDatosPantalla() - ticketVentaAbono: " + ticketVentaAbono);

        BigDecimal total = BigDecimal.ZERO;

        List<PagoTicket> lstPagos = ticketVentaAbono.getPagos();
        List<PagoTicketGui> lstPagosGui = new ArrayList<>();
        for (PagoTicket pago : lstPagos) {
            PagoTicketGui pagoGui = new PagoTicketGui(pago);
            lstPagosGui.add(pagoGui);
            total = total.add(pago.getImporte());
        }

        // Actualizamos el total del ticket
        lbTotalDetalle.setText(total.setScale(2, RoundingMode.HALF_UP) + " €");

        // Actualizamos la tabla
        lineas.clear();
        lineas.addAll(lstPagosGui);

        tbLineas.getSelectionModel().selectFirst();
        tbLineas.scrollTo(0);
        log.debug("cargarDatosPantalla() - lineas: " + lineas);
    }

}
