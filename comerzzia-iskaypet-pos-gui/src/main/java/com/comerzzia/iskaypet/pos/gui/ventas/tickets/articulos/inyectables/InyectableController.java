package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.inyectables.Inyectable;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GAP12 - ISK-8 GESTIÓN DE InyectableS
 * GAP 12.1 ISK-253
 */
@Component
@SuppressWarnings("unchecked")
public class InyectableController extends WindowController implements Initializable {

    protected Logger log = Logger.getLogger(getClass());

    public static final String CLAVE_PARAMETRO_LISTA_LINEAS = "listaLineas";
    public static final String CLAVE_PARAMETRO_MODIFICAR_INYECTABLE = "modificarInyectable";
    public static final String CLAVE_TICKET_MANAGER = "ticketManager";
    public static final String CLAVE_CANCELAR = "cancelar";

    private List<IskaypetLineaTicket> lineasInyectables;
    private IskaypetTicketManager ticketManager;
    private int indice;
    private boolean esModificarInyectable;

    @FXML
    protected Label lbNumInyectable;

    @FXML
    protected Label lbCodArt;
    @FXML
    protected Label lbDesArt;

    @FXML
    protected TextField tfUnidades;
    @FXML
    protected TextField tfCantidadConvertida;
    @FXML
    protected TextField tfCantidadSuministrada;
    @FXML
    protected ComboBox<String> cbUnidadMedida;

    @FXML
    protected Button btAceptar;
    @FXML
    protected Button btCancelar;

    protected static final int VALOR_MINIMO_UNIDADES = 1;
    protected int valorUnidadesSinInyectable;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
        registraEventoTeclado(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                accionAceptar();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                accionCancelar();
            }
        }, KeyEvent.KEY_RELEASED);

        // Impedir copiar y pegar
        tfCantidadConvertida.focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
        tfCantidadSuministrada.selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());

        tfCantidadConvertida.setTextFormatter(IskaypetFormatter.getDoubleFormat(4, 3, 0.00));
        tfCantidadSuministrada.setTextFormatter(IskaypetFormatter.getDoubleFormat(4, 0, 0.00));

        valorUnidadesSinInyectable = 1; // Por defecto el valor será uno.
        tfUnidades.textProperty().addListener((observable, oldValue, newValue) -> {
            // Permitir que el campo esté vacío
            if (newValue.isEmpty()) {
                return; // Simplemente no hacer nada si el nuevo valor es vacío
            }

            // Verificar si el nuevo valor es numérico
            if (newValue.matches("\\d*")) {
                try {
                    int newValueInt = Integer.parseInt(newValue);
                    // Verificar si el nuevo valor está dentro del rango permitido
                    if (newValueInt > valorUnidadesSinInyectable || newValueInt < VALOR_MINIMO_UNIDADES) {
                        tfUnidades.setText(oldValue); // Restaurar el valor anterior si se excede el máximo o mínimo
                    }
                } catch (NumberFormatException e) {
                    tfUnidades.setText(oldValue); // Restaurar el valor anterior si hay un error en el formato
                }
            } else {
                tfUnidades.setText(oldValue); // Restaurar el valor anterior si no es numérico
            }
        });


    }


    @Override
    public void initializeForm() throws InitializeGuiException {
        clearData();
        lineasInyectables = (List<IskaypetLineaTicket>) getDatos().get(CLAVE_PARAMETRO_LISTA_LINEAS);
        ticketManager = (IskaypetTicketManager) getDatos().get(CLAVE_TICKET_MANAGER);
        esModificarInyectable = (Boolean) getDatos().getOrDefault(CLAVE_PARAMETRO_MODIFICAR_INYECTABLE, false);
        inicializarInsercionInyectables();

    }

    @Override
    public void initializeFocus() {
        if (tfUnidades.getText().equalsIgnoreCase("1")) {
            tfCantidadConvertida.requestFocus();
        } else {
            tfUnidades.requestFocus();
        }
    }

    protected void clearData() {
        lbNumInyectable.setText(null);
        lbCodArt.setText(null);
        lbDesArt.setText(null);
        cbUnidadMedida.setItems(null);
        tfCantidadSuministrada.setText("");
        tfCantidadConvertida.setText("");
    }


    protected void inicializarInsercionInyectables() {
        indice = 0;
        mostrarSiguienteArticulo();
    }

    protected void mostrarSiguienteArticulo() {
        clearData();

        // Añadimos el maximo de unidades que se pueden introducir
        valorUnidadesSinInyectable = (int) lineasInyectables.stream().filter(el -> el.getInyectable() == null).count();

        if (esModificarInyectable) {
            log.debug("Se modificará el inyectable");
            if (valorUnidadesSinInyectable == 0) {
                log.debug("Se indica que se modificará el inyectable");
                valorUnidadesSinInyectable = 1;
            }

            IskaypetLineaTicket lineaTicket = lineasInyectables.get(indice);
            if (lineaTicket != null) {
                Inyectable inyectable = lineaTicket.getInyectable();
                if (inyectable != null) {
                    log.debug("Se carga el inyectable para su modificación");
                    tfCantidadConvertida.setText(FormatUtil.getInstance().formateaNumero(inyectable.getCantidadConvertida(), 3));
                    tfCantidadSuministrada.setText(FormatUtil.getInstance().formateaNumero(inyectable.getCantidadSuministrada(), 0));
                    cbUnidadMedida.getSelectionModel().select(inyectable.getUnidadMedidaSuministrada());
                }
            }
        }

        // Asignamos el valor de cantidad por defecto cuando es 1, y vacio cuando son varias unidades
        if (valorUnidadesSinInyectable == 1) {
            tfUnidades.setText("1");
            tfUnidades.setEditable(false);
        } else {
            tfUnidades.setText("");
            tfUnidades.setEditable(true);
        }

        cargarDatos();
    }

    protected void cargarDatos() {
        IskaypetLineaTicket linea = lineasInyectables.get(indice);
        ArticuloBean articulo = linea.getArticulo();

        lbCodArt.setText(articulo.getCodArticulo());
        lbDesArt.setText(articulo.getDesArticulo());
        actualizarLabelContadorInyectables();

        cbUnidadMedida.setItems(FXCollections.observableArrayList("ml", "mg", "g", "u", "kg", "l"));
        establecerUnidadMedidaSeleccionada(linea);

    }


    /**
     * Establece la unidad de medida seleccionada en el ComboBox de unidades de medida
     *
     * @param linea la línea de ticket que contiene el inyectable
     */
    protected void establecerUnidadMedidaSeleccionada(IskaypetLineaTicket linea) {

        //Selección por defecto de un Inyectable solo aplicable si se ha seleccionado Inyectable para esa línea
        if (linea.getInyectable() == null) {
            return;
        }

        //Se busca entre los unidades de medida disponibles el inyectable seleccionado
        for (String unidadMedida : cbUnidadMedida.getItems()) {
            if (unidadMedida.equals(linea.getInyectable().getUnidadMedidaSuministrada())) {
                cbUnidadMedida.getSelectionModel().select(unidadMedida);
                break;
            }
        }

    }

    protected void actualizarLabelContadorInyectables() {
        if (lineasInyectables.size() < 2) {
            lbNumInyectable.setVisible(false);
        } else {
            lbNumInyectable.setVisible(true);
            lbNumInyectable.setText(": " + valorUnidadesSinInyectable + " " + I18N.getTexto("unidades"));
        }
    }


    @FXML
    public void accionAceptar() {
        log.debug("accionAceptar() - Se ha clickado el botón aceptar");

        boolean valid = validarDatos();
        if (!valid) {
            log.error("accionAceptar() - Se han encontrado errores en los datos obligatorios");
            return;
        }

        // Insertamos las unidades seleccionadas
        int unidades = Integer.parseInt(tfUnidades.getText());
        for (int i = 0; i < unidades; i++) {
            log.debug("accionAceptar() - Se asigna el inyectable a la línea " + (indice + 1));
            Inyectable inyectable = new Inyectable();
            BigDecimal cantidadConvertida = FormatUtil.getInstance().desformateaBigDecimal(tfCantidadConvertida.getText(), 3);
            BigDecimal cantidadSuministrada = FormatUtil.getInstance().desformateaBigDecimal(tfCantidadSuministrada.getText(), 3);
            inyectable.setCantidadConvertida(cantidadConvertida);
            inyectable.setCantidadSuministrada(cantidadSuministrada);
            inyectable.setUnidadMedidaSuministrada(cbUnidadMedida.getSelectionModel().getSelectedItem());
            IskaypetLineaTicket lineaTicket = lineasInyectables.get(indice);
            lineaTicket.setInyectable(inyectable);
            ticketManager.calcularImporteCantidadConvertida(lineaTicket, lineaTicket.getPrecioTotalTarifaOrigen(), cantidadConvertida);
            indice++;
            valorUnidadesSinInyectable--;
        }

        // Comprobamos que no queden unidades sin asignar
        if (indice < lineasInyectables.size()) {
            log.debug("Quedan unidades sin asignar, se mostrará el siguiente inyectable");
            mostrarSiguienteArticulo();
        }

        log.debug("accionAceptar() - Se han asignado todas las unidades de inyectables");
        getDatos().put(CLAVE_CANCELAR, Boolean.FALSE);
        getStage().close();
    }

    private boolean validarDatos() {
        log.debug("validarDatos() - Se validan los datos del inyectable");
        if (!validarDatosObligatorios()) {
            return false;
        }

        if (!BigDecimalUtil.isMayorACero(new BigDecimal(tfCantidadConvertida.getText().replace(",", ".")))) {
            log.error("validarDatos() - La cantidad convertida no puede ser menor o igual a cero");
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La cantidad convertida no puede ser menor o igual a cero"), getStage());
            return false;
        }

        if (!BigDecimalUtil.isMayorACero(new BigDecimal(tfCantidadSuministrada.getText().replace(",", ".")))) {
            log.error("validarDatos() - La cantidad suministrada no puede ser menor o igual a cero");
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La cantidad suministrada no puede ser menor o igual a cero"), getStage());
            return false;
        }

        log.debug("validarDatos() - Todos los datos son válidos");
        return true;
    }

    private boolean validarDatosObligatorios() {
        log.debug("validarDatosObligatorios() - Se validan los datos obligatorios");

        List<String> errores = new ArrayList<>();

        if (tfCantidadConvertida.getText().isEmpty()) errores.add("cantidad");
        if (tfCantidadSuministrada.getText().isEmpty()) errores.add("cantidad suministrada");
        if (cbUnidadMedida.getSelectionModel().isEmpty()) errores.add("unidad de medida");

        if (!errores.isEmpty()) {
            log.error("validarDatosObligatorios() - Se han encontrado errores en los datos obligatorios");
            StringBuilder mensaje = new StringBuilder(I18N.getTexto("Los siguientes campos son obligatorios:"));
            for (String error : errores) {
                mensaje.append(",").append(I18N.getTexto(error));
            }
            VentanaDialogoComponent.crearVentanaError(mensaje.toString(), getStage());
            return false;
        }
        log.debug("validarDatosObligatorios() - Todos los datos obligatorios son válidos");
        return true;
    }

    @FXML
    public void accionCancelar() {
        log.debug("accionCancelar() - Se ha clickado el botón cancelar");
        if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("No se ha asignado el Inyectable, la pantalla se cerrará, ¿Está seguro?"), getStage())) {
            log.debug("accionCancelar() - Se cancela la inserción de inyectables");
            getDatos().put(CLAVE_CANCELAR, Boolean.TRUE);
            super.accionCancelar();
        }
    }


}
