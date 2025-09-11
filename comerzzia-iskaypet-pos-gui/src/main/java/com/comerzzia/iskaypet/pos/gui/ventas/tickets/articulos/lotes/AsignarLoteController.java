package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.comerzzia.iskaypet.pos.util.date.DateUtils;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteArticulo;
import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteDTO;
import com.comerzzia.iskaypet.pos.services.articulos.lotes.LoteArticuloException;
import com.comerzzia.iskaypet.pos.services.articulos.lotes.ServicioLotesArticulos;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * GAP12 - ISK-8 GESTIÓN DE LOTES
 * GAP 12.1 ISK-253
 */
@Component
public class AsignarLoteController extends WindowController implements Initializable {

    protected Logger log = Logger.getLogger(getClass());

    public static final String CLAVE_PARAMETRO_LISTA_LINEAS = "listaLineas";
    public static final String CLAVE_PARAMETRO_MODIFICAR_LOTE = "modificarLote";
    public static final String CLAVE_TICKET_MANAGER = "ticketManager";
    public static final String CLAVE_SE_HA_CANCELADO_PANTALLA = "seHaCanceladoPantalla";

    @Autowired
    ServicioLotesArticulos servicioLotesArticulos;

    private List<IskaypetLineaTicket> lineasMedicamentos;
    private IskaypetTicketManager ticketManager;
    private int indice;
    private boolean esPantallaSeleccionarLote;
    private boolean esDevolucion, esDevolucionSinOrigen, esModificarLote;
    private Map<String, Map.Entry<LoteArticulo, Integer>> mapaContadorLotes;


    @FXML
    protected Label lbNumMedicamento;
    @FXML
    protected Label lbCodArt;
    @FXML
    protected Label lbDesArt;
    @FXML
    protected ComboBox<LoteArticulo> cbLote;
    @FXML
    protected Label lbFechaCaducidad;

    @FXML
    protected TextField tfUnidades;
    @FXML
    protected TextField tfLote1;
    @FXML
    protected TextField tfLote2;
    @FXML
    protected DatePicker tfFecha1;
    @FXML
    protected DatePicker tfFecha2;

    @FXML
    protected HBox hbSeleccionLote;
    @FXML
    protected HBox hbIntroduccionManual;

    @FXML
    protected Button btIntroduccionManual;
    @FXML
    protected Button btAceptar;
    @FXML
    protected Button btCancelar;

    protected static final int VALOR_MINIMO_UNIDADES = 1;
    protected int valorUnidadesSinLote;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
        registraEventoTeclado(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    accionAceptar();
                } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                    accionCancelar();
                }
            }

        }, KeyEvent.KEY_RELEASED);


        cbLote.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                lbFechaCaducidad.setText("");
            } else {
                String fecha = new Fecha(newValue.getExpirationDate()).getString(Fecha.PATRON_FECHA_CORTA);
                lbFechaCaducidad.setText(fecha);
            }
        });
        StringConverter<LoteArticulo> converter = new StringConverter<LoteArticulo>() {
            @Override
            public String toString(LoteArticulo lote) {
                //Formateamos fecha para mostrar nombre - fecha caducidad
                Date fechaActual = lote.getExpirationDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fechaFormateada = sdf.format(fechaActual);

                String response = lote.getBatch().concat(" - ").concat(fechaFormateada);

                if (esDevolucion && !esDevolucionSinOrigen) {
                    String unidades = BigDecimalUtil.isIgual(lote.getStock(), BigDecimal.ONE) ? I18N.getTexto("UD") : I18N.getTexto("UDS");
                    response += " - " + I18N.getTexto("Máximo") + ": " + lote.getStock() + " " + unidades;
                }

                return response;
            }

            @Override
            public LoteArticulo fromString(String string) {
                return null;
            }
        };
        cbLote.setConverter(converter);

        // Impedir copiar y pegar
        tfLote1.focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
        tfLote1.selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
        tfLote2.focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
        tfLote2.selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
        tfFecha1.getTextField().focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
        tfFecha1.getTextField().selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
        tfFecha2.getTextField().focusedProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());
        tfFecha2.getTextField().selectedTextProperty().addListener((observable, oldValue, newValue) -> Clipboard.getSystemClipboard().clear());

        IskaypetFormatter.setUpperCaseFormatter(tfLote1);
        IskaypetFormatter.setUpperCaseFormatter(tfLote2);

        valorUnidadesSinLote = 1; // Por defecto el valor será uno.
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
                    if (newValueInt > valorUnidadesSinLote || newValueInt < VALOR_MINIMO_UNIDADES) {
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

    @SuppressWarnings("unchecked")
    @Override
    public void initializeForm() throws InitializeGuiException {
        clearData();

        lineasMedicamentos = (List<IskaypetLineaTicket>) getDatos().get(CLAVE_PARAMETRO_LISTA_LINEAS);
        ticketManager = (IskaypetTicketManager) getDatos().get(CLAVE_TICKET_MANAGER);
        esDevolucion = ticketManager.isEsDevolucion();
        esDevolucionSinOrigen = esDevolucion && ticketManager.getDatosOrigenDevolucionSinOrigen() != null;
        esModificarLote = (Boolean) getDatos().getOrDefault(CLAVE_PARAMETRO_MODIFICAR_LOTE, false);

        try {
            inicializarInsercionLotes();
        } catch (LoteArticuloException e) {
            String error = "Error inicializando la pantalla de asignación de lotes: " + e.getMessage();
            log.error("initializeForm() - " + error);
            throw new InitializeGuiException(error, e);
        }
    }

    @Override
    public void initializeFocus() {
        cbLote.requestFocus();
    }

    protected void clearData() {
        lbNumMedicamento.setText(null);
        lbCodArt.setText(null);
        lbDesArt.setText(null);
        cbLote.setItems(null);
        lbFechaCaducidad.setText(null);
        clearDataIntroduccionManual();
    }

    protected void clearDataIntroduccionManual() {
        tfLote1.setText(null);
        tfLote2.setText(null);
        tfFecha1.clear();
        tfFecha2.clear();
    }

    protected void inicializarInsercionLotes() throws LoteArticuloException {
        indice = 0;
        cargarInicializarContadorLotes();
        mostrarSiguienteArticulo();
    }

    protected void mostrarSiguienteArticulo() throws LoteArticuloException {
        clearData();

        // Añadimos el maximo de unidades que se pueden introducir
        valorUnidadesSinLote = (int) lineasMedicamentos.stream().filter(el -> el.getLote() == null).count();

        // Es modificar lote, y no hay unidades sin asignar
        if(esModificarLote && valorUnidadesSinLote == 0) {
            valorUnidadesSinLote = 1;
        }

        // Asignamos el valor de cantidad por defecto cuando es 1, y vacio cuando son varias unidades
        if (valorUnidadesSinLote == 1) {
            tfUnidades.setText("1");
            tfUnidades.setEditable(false);
        } else {
            tfUnidades.setText("");
            tfUnidades.setEditable(true);
        }
        if (esDevolucion && esDevolucionSinOrigen) {
            mostrarPantallaInsertarLote();
        } else {
            mostrarPantallaSeleccionarLote();
        }
        cargarDatos();
    }

    protected void cargarDatos() throws LoteArticuloException {
        IskaypetLineaTicket linea = lineasMedicamentos.get(indice);
        ArticuloBean articulo = linea.getArticulo();

        lbCodArt.setText(articulo.getCodArticulo());
        lbDesArt.setText(articulo.getDesArticulo());
        actualizarLabelContadorMedicamentos();

        List<LoteArticulo> listaLotesComboBox = null;
        if (!esDevolucion) {
            // Para ventas se buscan los lotes DISPONIBLES guardados en tienda de ese artículo
            listaLotesComboBox = servicioLotesArticulos.consultarLotesDisponiblesDeArticulo(articulo, linea.getDesglose1(), linea.getDesglose2());
        } else if (esDevolucionSinOrigen) {
            // Para dev sin origen se buscan los lotes TOTALES guardados en tienda de ese artículo
            listaLotesComboBox = servicioLotesArticulos.consultarLotesTotalesDeArticulo(articulo, linea.getDesglose1(), linea.getDesglose2());

        } else {
            //Para devoluciones con origen, se asignan los lotes del ticket origen filtrados en el mapa de contador
            listaLotesComboBox = new ArrayList<>(mapaContadorLotes.size());
            for (Map.Entry<String, Map.Entry<LoteArticulo, Integer>> entry : mapaContadorLotes.entrySet()) {
                LoteArticulo lote = entry.getValue().getKey();
                lote.setStock(BigDecimal.valueOf(entry.getValue().getValue()));
                listaLotesComboBox.add(lote);
            }
        }

        cbLote.setItems(FXCollections.observableArrayList(listaLotesComboBox));
        establecerLoteSeleccionado(linea);
    }

    /**
     * Carga el mapa de lotes disponibles (los del ticket origen menos los que ya se han seleccionado)
     */
    @SuppressWarnings("unchecked")
    protected void cargarInicializarContadorLotes() {

        if (!esDevolucion || esDevolucionSinOrigen) {
            return;
        }

        //Se crea mapa con los lotes de medicamentos aún no devueltos y su cantidad

        //Se añaden los lotes del ticket original
        mapaContadorLotes = new HashMap<>();
        for (IskaypetLineaTicket linea : (List<IskaypetLineaTicket>) ticketManager.getTicketOrigen().getLineas()) {

            boolean exists = lineasMedicamentos.stream()
                    .anyMatch(l -> linea.getCodArticulo().equals(l.getCodArticulo()));

            if (exists && linea.getLote() != null) {
                Map.Entry<LoteArticulo, Integer> entryContador = mapaContadorLotes.get(linea.getLote().getConcatLoteLoteS4().concat(linea.getLote().getFechaCaducidadString()));

                if (entryContador == null) {
                    LoteArticulo loteArticulo = new LoteArticulo();
                    loteArticulo.setBatch(linea.getLote().getLote());
                    loteArticulo.setBatchNumberS4(linea.getLote().getBatchNumberS4());
                    loteArticulo.setExpirationDate(linea.getLote().getFechaCaducidad());

                    entryContador = new AbstractMap.SimpleEntry<>(loteArticulo, 1);
                    mapaContadorLotes.put(linea.getLote().getConcatLoteLoteS4().concat(linea.getLote().getFechaCaducidadString()), entryContador);

                } else {
                    entryContador.setValue(entryContador.getValue() + 1);
                }
            }
        }

        //Se eliminan los lotes presentes en el ticket principal
        for (IskaypetLineaTicket linea : (List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas()) {
            boolean exists = lineasMedicamentos.stream()
                    .anyMatch(l -> linea.getCodArticulo().equals(l.getCodArticulo()));

            if (exists && linea.getLote() != null) {
                String idLote = linea.getLote().getConcatLoteLoteS4().concat(linea.getLote().getFechaCaducidadString());
                Map.Entry<LoteArticulo, Integer> entryContador = mapaContadorLotes.get(idLote);
                if (entryContador != null && entryContador.getValue() == 1) {
                    mapaContadorLotes.remove(idLote);
                } else {
                    entryContador.setValue(entryContador.getValue() - 1);
                }
            }
        }
    }

    /**
     * Muestra seleccionado el lote por defecto antes de que el usuario lo seleccione
     */
    protected void establecerLoteSeleccionado(IskaypetLineaTicket linea) {

        //Selección por defecto de un lote solo aplicable si se ha seleccionado lote para esa línea
        if (linea.getLote() == null) {
            return;
        }

        //Se busca entre los lotes disponibles el lote seleccionado
        boolean seHaSeleccionadoLote = false;
        for (LoteArticulo lote : cbLote.getItems()) {
            if (lote.getBatchNumberS4().equals(linea.getLote().getBatchNumberS4())) {
                seHaSeleccionadoLote = true;
                cbLote.getSelectionModel().select(lote);
                break;
            }
        }

        //Si no existe, se añade in situ el lote introducido manualmente
        if (!seHaSeleccionadoLote) {
            LoteArticulo loteTemporal = new LoteArticulo();
            loteTemporal.setBatch(linea.getLote().getLote());
            loteTemporal.setExpirationDate(linea.getLote().getFechaCaducidad());
            cbLote.getItems().add(loteTemporal);
            cbLote.getSelectionModel().select(loteTemporal);
        }
    }

    protected void actualizarLabelContadorMedicamentos() {
        if (lineasMedicamentos.size() < 2) {
            lbNumMedicamento.setVisible(false);
        } else {
            lbNumMedicamento.setVisible(true);
            lbNumMedicamento.setText(": " + valorUnidadesSinLote + " " + I18N.getTexto("unidades"));
        }
    }

    protected void mostrarPantallaSeleccionarLote() {
        hbSeleccionLote.setVisible(true);
        hbSeleccionLote.setManaged(true);
        hbIntroduccionManual.setVisible(false);
        hbIntroduccionManual.setManaged(false);
        //Botón de introducción manual solo se muestra en ventas y devoluciones sin origen
        btIntroduccionManual.setVisible(!esDevolucion || esDevolucionSinOrigen);
        esPantallaSeleccionarLote = true;
        cbLote.requestFocus();

        this.getStage().setHeight(300.0);
    }

    protected void mostrarPantallaInsertarLote() {
        hbSeleccionLote.setVisible(false);
        hbSeleccionLote.setManaged(false);
        hbIntroduccionManual.setVisible(true);
        hbIntroduccionManual.setManaged(true);
        btIntroduccionManual.setVisible(false);
        esPantallaSeleccionarLote = false;
        tfLote1.requestFocus();

        this.getStage().setHeight(400.0);
    }

    @FXML
    public void accionAceptar() {
        log.debug("accionAceptar()");

        if (!validarFormulario()) {
            return;
        }

        //Se recuperan de pantalla datos de lote y se guardan en lotes seleccionados
        LoteDTO loteDTO = new LoteDTO();
        if (esPantallaSeleccionarLote) {
            loteDTO.setBatchNumberS4(cbLote.getSelectionModel().getSelectedItem().getBatchNumberS4());
            loteDTO.setLote(cbLote.getSelectionModel().getSelectedItem().getBatch());
            loteDTO.setFechaCaducidad(cbLote.getSelectionModel().getSelectedItem().getExpirationDate());
        } else {
            loteDTO.setLote(tfLote1.getText());
            loteDTO.setFechaCaducidad(tfFecha1.getSelectedDate());
        }

        int unidades = Integer.parseInt(tfUnidades.getText());
        if (esDevolucion && !esDevolucionSinOrigen) {
            Map.Entry<LoteArticulo, Integer> entry = mapaContadorLotes.get(loteDTO.getConcatLoteLoteS4().concat(loteDTO.getFechaCaducidadString()));
            if (unidades > entry.getValue()) {
                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Has introducido más unidades de este lote de las que hay en el ticket origen"), getStage());
                return;
            }
        }


        for (int i = 0; i < unidades; i++) {
            lineasMedicamentos.get(indice).setLote(loteDTO);
            indice++;
            valorUnidadesSinLote--;

            //Solo en devoluciones con doc origen se elimina 1 lote del contador para control de lotes disponibles
            if (esDevolucion && !esDevolucionSinOrigen) {
                Map.Entry<LoteArticulo, Integer> entry = mapaContadorLotes.get(loteDTO.getConcatLoteLoteS4().concat(loteDTO.getFechaCaducidadString()));
                if (entry != null) {
                    if (entry.getValue() == 1) {
                        mapaContadorLotes.remove(loteDTO.getConcatLoteLoteS4().concat(loteDTO.getFechaCaducidadString()));
                    } else {
                        entry.setValue(entry.getValue() - 1);
                    }
                }
            }
        }

        if (indice < lineasMedicamentos.size()) {
            //Si quedan más lotes, se pasa a la siguiente pantalla
            try {
                mostrarSiguienteArticulo();
            } catch (LoteArticuloException e) {
                log.error("accionAceptar() - Error pasando al siguiente artículo: " + e.getMessage(), e);
                VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha ocurrido un error en la selección de lotes"), e);
                getStage().close();
            }
        } else {
            //Si no, se acaba el proceso
            getDatos().put(CLAVE_SE_HA_CANCELADO_PANTALLA, Boolean.FALSE);
            getStage().close();

        }

    }

    @FXML
    public void accionCancelar() {
        if (esPantallaSeleccionarLote || (esDevolucion && esDevolucionSinOrigen)) {
            //En pantalla de selección de lote o edición de línea de dev sin origen se cancela operación
            if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("No se ha asignado el lote, la pantalla se cerrará, ¿Está seguro?"), getStage())) {
                getDatos().put(CLAVE_SE_HA_CANCELADO_PANTALLA, Boolean.TRUE);
                super.accionCancelar();
            }
        } else {
            // En pantalla de introducción manual se pasa a pantalla de selección borrando los datos
            boolean cerrar = true;

            if (pantallaIntroduccionManualTieneDatos()) {
                cerrar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Quiere cancelar la introducción manual de lote?"), getStage());
            }

            if (cerrar) {
                clearDataIntroduccionManual();
                mostrarPantallaSeleccionarLote();
            }

        }

    }

    @FXML
    public void accionIntroduccionManual() {
        mostrarPantallaInsertarLote();
        btIntroduccionManual.setVisible(false);
    }

    protected boolean validarFormulario() {

        if(StringUtils.isBlank(tfUnidades.getText())) {
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe introducir un número de unidades"), getStage());
            tfUnidades.requestFocus();
            return false;
        }
        if (esPantallaSeleccionarLote) {
            if (cbLote.getSelectionModel().getSelectedItem() == null) {
                VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe seleccionar un lote"), getStage());
                return false;
            }
        } else {
            if (!(StringUtils.isNotEmpty(tfLote1.getText()) &&
                    StringUtils.isNotEmpty(tfFecha1.getTextField().getText()) &&
                    tfLote1.getText().equals(tfLote2.getText()) &&
                    tfFecha1.getTextField().getText().equals(tfFecha2.getTextField().getText()) &&
                    !tfFecha1.invalidProperty().get() &&
                    !tfFecha2.invalidProperty().get()
            )) {
                VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Los datos indicados no son correctos o no coinciden"), getStage());
                tfLote1.requestFocus();
                return false;
            }

            for (LoteArticulo lote : cbLote.getItems()) {
                if (lote.getBatch().equals(tfLote1.getText())) {
                    VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El lote introducido existe en la lista, selecciónelo"), getStage());
                    mostrarPantallaSeleccionarLote();
                    return false;
                }
            }

            Date fechaCaducidad = tfFecha1.getSelectedDate();
            if (fechaCaducidad.before(DateUtils.getCurrentDateWithZeroTime())) {
                VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La fecha de caducidad no puede ser anterior a la fecha actual"), getStage());
                tfFecha1.requestFocus();
                return false;
            }

        }

        return true;
    }

    private boolean pantallaIntroduccionManualTieneDatos() {
        if (
                StringUtils.isNotEmpty(tfLote1.getText()) ||
                        StringUtils.isNotEmpty(tfFecha1.getTextField().getText()) ||
                        StringUtils.isNotEmpty(tfLote2.getText()) ||
                        StringUtils.isNotEmpty(tfFecha2.getTextField().getText())
        ) {
            return true;
        }
        return false;
    }

}
