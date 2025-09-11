package com.comerzzia.iskaypet.pos.devices.proformas.automaticas;

import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.persistence.proformas.ProformaBean;
import com.comerzzia.iskaypet.pos.persistence.proformas.filter.ParametroBuscarProformaBean;
import com.comerzzia.iskaypet.pos.services.proformas.ProformaService;
import com.comerzzia.iskaypet.pos.services.proformas.exception.ProformaNotFoundException;
import com.comerzzia.iskaypet.pos.util.date.DateUtils;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.simboloCargando.VentanaCargando;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


@Component
@SuppressWarnings("unchecked")
public class ProformasAutomaticasController extends WindowController {

    private static final Logger log = Logger.getLogger(ProformasAutomaticasController.class);

    public static final String PARAM_ACCION_CANCELAR = "ACCION_CANCELAR";

    @Autowired
    protected VariablesServices variablesServices;

    @Autowired
    private Sesion sesion;

    protected ObservableList<ProformaBean> proformas;

    private VentanaCargando ventana;

    @FXML
    private TableView<ProformaBean> tblProformas;
    @FXML
    private TableColumn<ProformaBean, String> colIdProforma;
    @FXML
    private TableColumn<ProformaBean, String> colTipoDocumento;
    @FXML
    private TableColumn<ProformaBean, String> colFechaCreacion;
    @FXML
    private TableColumn<ProformaBean, String> colEstado;
    @FXML
    private Label lbMessage;
    @FXML
    private Button btAceptar;

    @Autowired
    protected IskaypetTicketManager ticketManager;

    @Autowired
    protected ProformaService proformaService;

    @Getter
    @Setter
    protected int numProformasFacturadas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        proformas = FXCollections.observableList(new ArrayList());
        log.debug("Inicializando el controlador de selección de proformas");
        colIdProforma.setCellValueFactory(new PropertyValueFactory<>("idProforma"));
        colTipoDocumento.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerCodDocumento(cellData.getValue().getIdTipoDocumento())));
        colFechaCreacion.setCellValueFactory(cellData -> new SimpleStringProperty(DateUtils.formatDate(cellData.getValue().getFecha(), DateUtils.PATRON_FECHA_LARGA)));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoActual"));
        tblProformas.setItems(proformas);
        log.debug("Controlador de selección de proformas inicializado");
    }


    @Override
    public void initializeComponents() throws InitializeGuiException {
        log.debug("initializeComponents - Se inicializan los componentes del controlador de selección de proformas");
    }

    @Override
    public void initializeForm() {
        log.debug("initializeForm - Se inicializa el formulario para seleccionar una proforma");
        btAceptar.setDisable(true);
        setNumProformasFacturadas(0);
        loadProformasTable();
        setMessage();
        log.debug("initializeForm - Se ha inicializado el formulario para seleccionar una proforma");
    }

    @Override
    public void initializeFocus() {
        log.debug("initializeFocus - Foco inicial en la tabla de proformas");
        tblProformas.getSelectionModel().selectFirst();

        Task<Void> facturacionTask = new Task<Void>() {
            @Override
            protected Void call() {
                facturarProformasEnBackground();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> btAceptar.setDisable(false));
            }
        };

        new Thread(facturacionTask).start();
    }

    @FXML
    public void accionAceptar() {
        log.debug("accionAceptar - Se inicia el proceso de facturación de proformas");
       getStage().close();
    }

    @FXML
    public void accionCancelar() {
        getDatos().put(PARAM_ACCION_CANCELAR, true);
        getStage().close();
    }

    private void setMessage() {
        lbMessage.setText(I18N.getTexto("Proformas automaticas facturada {0} de ", numProformasFacturadas) + proformas.size());
    }

    private void loadProformasTable() {
        log.debug("loadProformasTable - Se cargan las proformas en la tabla");
        SqlSession sqlSession = new SqlSession();
        try {
            proformas.clear();
            sqlSession.openSession(SessionFactory.openSession());

            DatosSesionBean datosSesion = new DatosSesionBean();
            datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
            datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());

            ParametroBuscarProformaBean param = new ParametroBuscarProformaBean();
            param.setUidActividad(datosSesion.getUidActividad());
            param.setEstadoActual(ProformaBean.ESTADO_ENVIADA);
            param.setCodalm(sesion.getAplicacion().getCodAlmacen());
            param.setAutomatica(true);

            proformas.addAll(proformaService.consultar(sqlSession, datosSesion, param));
            log.debug("loadProformasTable - Se han cargado las proformas en la tabla");
        } catch (Exception e) {
            log.error("loadProformasTable - Error al cargar las proformas en la tabla", e);
            VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error al cargar las proformas"), e);
            accionCancelar();
        }

    }

    private String obtenerCodDocumento(long idTipoDocumento) {
        log.debug("obtenerCodDocumento - Se obtiene el código del documento con id: " + idTipoDocumento);
        try {
            TipoDocumentoBean doc = sesion.getAplicacion().getDocumentos().getDocumento(idTipoDocumento);
            String codDocumento = doc.getCodtipodocumento();
            log.debug("obtenerCodDocumento - Se ha obtenido el código del documento: " + codDocumento);
            return codDocumento;
        } catch (DocumentoException e) {
            log.error("obtenerCodDocumento - Error al obtener el código del documento con id: " + idTipoDocumento, e);
            return "";
        }
    }

    private void facturarProformasEnBackground() {

        SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
        try {
            DatosSesionBean datosSesion = new DatosSesionBean();
            datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
            datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());

            sqlSession.openSession(SessionFactory.openSession());

            for (int i = 0; i < proformas.size(); i++) {
                ProformaBean proforma = proformas.get(i);

                if (proforma.getEstadoActual().equals(ProformaBean.ESTADO_ENVIADA)) {

                    ticketManager.facturarProforma(datosSesion, proforma, getStage());
                    setNumProformasFacturadas(getNumProformasFacturadas() + 1);

                    // Actualizar UI
                    final int currentIndex = i;
                    Platform.runLater(() -> {
                        proformas.set(currentIndex, proforma);
                        setMessage();
                        tblProformas.getSelectionModel().select(currentIndex);
                        tblProformas.refresh();
                    });

                }
            }
            sqlSession.commit();
        } catch (Exception e) {
            log.error("Error al facturar las proformas", e);
        } finally {
            sqlSession.close();
        }

    }

}

