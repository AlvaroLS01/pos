package com.comerzzia.iskaypet.pos.devices.proformas.seleccion;

import com.comerzzia.iskaypet.pos.services.proformas.rest.ProformaRestService;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaHeaderDTO;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


@Component
@SuppressWarnings("unchecked")
public class SeleccionProformaController extends WindowController {
    private static final Logger log = Logger.getLogger(SeleccionProformaController.class);

    public static final String PARAM_PROFORMAS_SELECCION = "PROFORMAS_SELECCION";
    public static final String PARAM_PROFORMA_SELECCIONADA = "PROFORMA_SELECCIONADA";
    public static final String PARAM_ACCION_CANCELAR = "ACCION_CANCELAR";

    @Autowired
    protected VariablesServices variablesServices;

    @Autowired
    protected ProformaRestService proformaService;

    @Autowired
    private Sesion sesion;

    @FXML
    private TableView<ProformaHeaderDTO> tblProformas;
    @FXML
    private TableColumn<ProformaHeaderDTO, Long> colIdProforma;
    @FXML
    private TableColumn<ProformaHeaderDTO, String> colTipoDocumento;
    @FXML
    private TableColumn<ProformaHeaderDTO, String> colCliente;
    @FXML
    private TableColumn<ProformaHeaderDTO, String> colEstado;
    @FXML
    private TableColumn<ProformaHeaderDTO, String> colFechaCreacion;
    @FXML
    private Button btnAceptar;
    @FXML
    private Button btnCancelar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.debug("Inicializando el controlador de selección de proformas");
        colIdProforma.setCellValueFactory(new PropertyValueFactory<>("idProforma"));
        colTipoDocumento.setCellValueFactory(new PropertyValueFactory<>("tipoDocumento"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoActual"));
        colFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fechaProforma"));
        log.debug("Controlador de selección de proformas inicializado");
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {

    }

    @Override
    public void initializeForm() {
        log.debug("initializeForm - Se inicializa el formulario para seleccionar una proforma");
        tblProformas.getItems().clear();
        tblProformas.getItems().addAll((List<ProformaHeaderDTO>) getDatos().get(PARAM_PROFORMAS_SELECCION));
        log.debug("initializeForm - Se ha inicializado el formulario para seleccionar una proforma");
    }

    @Override
    public void initializeFocus() {
        log.debug("initializeFocus - Se inicializa el foco en la tabla de proformas");
        tblProformas.getSelectionModel().select(0);
        log.debug("initializeFocus - Se ha inicializado el foco en la tabla de proformas");
    }

    @FXML
    void aceptarDocDobleClick() {
        log.info("accionAceptarDobleClick() - Buscando por filtro seleccionado...");
        accionAceptar();
    }

    @FXML
    public void accionAceptar() {
        log.debug("accionAceptar - Se inicializa aceptar la selección de una proforma");

        ProformaHeaderDTO seleccionado = tblProformas.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            VentanaDialogoComponent.crearVentanaConfirmacionUnBoton("Debe seleccionar una proforma", getStage());
            return;
        }

        try {
            ProformaDTO fullProforma = proformaService.obtenerProformaCompleta(sesion, seleccionado.getIdProforma());
            getDatos().put(PARAM_PROFORMA_SELECCIONADA, fullProforma);
            getStage().close();
        } catch (Exception e) {
            log.error("Error obteniendo la proforma seleccionada", e);
            VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error obteniendo la proforma seleccionada"), e);
        }

    }

    @FXML
    public void accionCancelar() {
        getDatos().put(PARAM_PROFORMA_SELECCIONADA, null);
        getDatos().put(PARAM_ACCION_CANCELAR, true);
        getStage().close();
    }

}

