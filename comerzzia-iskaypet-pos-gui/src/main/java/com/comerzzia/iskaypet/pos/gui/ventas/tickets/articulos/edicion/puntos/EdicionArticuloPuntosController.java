package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.edicion.puntos;

import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.canjeoPuntos.ArticlesPointsXMLBean;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class EdicionArticuloPuntosController extends WindowController implements Initializable {

    private static final Logger log = Logger.getLogger(EdicionArticuloPuntosController.class.getName());

    public static final String TICKET = "TICKET";
    public static final String LINEA_SELECCIONADA = "LINEA_SELECCIONADA";
    public static final String LINEAS_CANJEABLES = "LINEAS_CANJEABLES";
    public static final String CANTIDAD_CANJEO = "CANTIDAD_CANJEO";

    @FXML
    protected TextField tfArticulo;
    @FXML
    protected TextField tfDescripcion;
    @FXML
    protected TextField tfCantidadTotal;
    @FXML
    protected TextField tfPuntosDisponibles;
    @FXML
    protected TextField tfPuntosNecesarios;
    @FXML
    protected TextField tfCantidadCanjeo;
    @FXML
    protected TextField tfTotalPuntos;
    @FXML
    protected TextField tfPrecioTotalArticulos;

    @FXML
    protected Label lbCantidadTotal;
    @FXML
    protected Label lbCantidadCanjeo;
    @FXML
    protected Label lbPuntosDisponibles;
    @FXML
    protected Label lbPuntosNecesarios;

    @FXML
    protected Button btAceptar;
    @FXML
    protected Button btCancelar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfCantidadCanjeo.textProperty().addListener((observable, oldValue, newValue) -> calcularTotalPuntos());
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {

    }

    @Override
    public void initializeForm() throws InitializeGuiException {
        TicketVentaAbono ticket = (TicketVentaAbono) getDatos().get(TICKET);
        IskaypetLineaTicket lineaSeleccionada = (IskaypetLineaTicket) getDatos().get(LINEA_SELECCIONADA);
        tfArticulo.setText(lineaSeleccionada.getCodArticulo());
        tfDescripcion.setText(lineaSeleccionada.getDesArticulo());
        tfCantidadTotal.setText(getDatos().get(LINEAS_CANJEABLES).toString());
        tfCantidadCanjeo.setText(String.valueOf(lineaSeleccionada.getCantidad()));
        tfCantidadCanjeo.setTextFormatter(IskaypetFormatter.getIntegerFormat());
        tfPuntosDisponibles.setText(ticket.getCabecera().getDatosFidelizado().getSaldoTotalAsString());
        int puntos = lineaSeleccionada.getArticlePoints().getPoints().intValue();
        tfPuntosNecesarios.setText(String.valueOf(puntos));
        tfTotalPuntos.setText(String.valueOf(puntos));
        tfPrecioTotalArticulos.setText(lineaSeleccionada.getArticlePoints().getValue().toString());
    }

    @Override
    public void initializeFocus() {

    }

    @FXML
    void accionAceptar() {
        if(Integer.parseInt(tfCantidadCanjeo.getText()) > Integer.parseInt(tfCantidadTotal.getText())) {
            String msgAviso = I18N.getTexto("No se pueden canjear más artículos de los que hay disponibles en el ticket.");
            VentanaDialogoComponent.crearVentanaAviso(msgAviso, getStage());
            return;
        }
        if(Integer.parseInt(tfTotalPuntos.getText()) > Integer.parseInt(tfPuntosDisponibles.getText().replace(".", ""))) {
            String msgAviso = I18N.getTexto("No se pueden utilizar más puntos de los que tiene disponibles el fidelizado.");
            VentanaDialogoComponent.crearVentanaAviso(msgAviso, getStage());
        }
        else {
            StringBuilder msgConfirm = new StringBuilder(I18N.getTexto("¿Está seguro que desea canjear por puntos {0}", tfCantidadCanjeo.getText()));

            if(Integer.parseInt(tfCantidadCanjeo.getText()) > 1){
                msgConfirm.append(I18N.getTexto(" unidades"));
            } else {
                msgConfirm.append(I18N.getTexto(" unidad"));
            }

            msgConfirm.append(I18N.getTexto(" del artículo: {0} - {1}?", "'"+tfArticulo.getText(), tfDescripcion.getText()+"'")).append("\n")
                .append(I18N.getTexto("Puntos a utilizar por artículo: {0}", tfPuntosNecesarios.getText())).append("\n")
                .append(I18N.getTexto("Puntos a utilizar totales: {0}", new BigDecimal(tfPuntosNecesarios.getText()).multiply(new BigDecimal(tfCantidadCanjeo.getText())))).append("\n")
                .append(I18N.getTexto("Precio total de los artículos: {0}", new BigDecimal(tfPrecioTotalArticulos.getText()).multiply(new BigDecimal(tfCantidadCanjeo.getText()))));

            if (VentanaDialogoComponent.crearVentanaConfirmacion(msgConfirm.toString(), getStage())) {
                getDatos().put(CANTIDAD_CANJEO, tfCantidadCanjeo.getText());
                getStage().close();
            }
        }
    }

    private void calcularTotalPuntos() {
        try {
            int cantidad = Integer.parseInt(tfCantidadCanjeo.getText());
            int puntosPorUnidad = Integer.parseInt(tfPuntosNecesarios.getText());
            int total = cantidad * puntosPorUnidad;
            tfTotalPuntos.setText(String.valueOf(total));
        } catch (NumberFormatException e) {
            tfTotalPuntos.setText(tfPuntosNecesarios.getText());
        }
    }
}
