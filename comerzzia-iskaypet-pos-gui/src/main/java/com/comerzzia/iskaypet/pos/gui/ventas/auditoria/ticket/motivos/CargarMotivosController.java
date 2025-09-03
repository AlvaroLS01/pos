package com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaDto;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.persistence.auditorias.motivos.MotivosAuditoria;
import com.comerzzia.iskaypet.pos.persistence.auditorias.motivos.MotivosAuditoriaExample;
import com.comerzzia.iskaypet.pos.persistence.auditorias.motivos.MotivosAuditoriaMapper;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


@Component
public class CargarMotivosController extends WindowController implements Initializable, IContenedorBotonera {
	
	private static final Logger log = Logger.getLogger(CargarMotivosController.class.getName());

	@FXML
	private Label lbTitulo;

	@FXML
	protected Button btAceptar, btCancelar;
	
	@FXML
	private AnchorPane panelBotoneraTabla;
	
	@FXML
	private TableView<MotivoAuditoriaDto> tbFiltro;

	@FXML
	private TableColumn<MotivoAuditoriaDto, String> tcCod;

	@FXML
	private TableColumn<MotivoAuditoriaDto, String> tcDescripcion;

	@FXML 
	private AnchorPane apCargarFiltros;
	
	@Autowired
	private MotivosAuditoriaMapper auditoriaMapper;
	
	@Autowired
	protected Sesion sesion;

	public final static String MOTIVO = "MOTIVO";
	
    protected BotoneraComponent botoneraAccionesTabla;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		
		log.debug("inicializarComponentes()");
        //Se registra el evento para salir de la pantalla pulsando la tecla escape. 
		registrarAccionCerrarVentanaEscape();
        crearEventoEnterTabla(tbFiltro);
        crearEventoNavegacionTabla(tbFiltro);

        try {
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = new LinkedList<>();
            
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", ""));
            listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", ""));

            botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelBotoneraTabla.getPrefWidth(), panelBotoneraTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotoneraTabla.getChildren().clear();
            panelBotoneraTabla.getChildren().add(botoneraAccionesTabla);

        }
        catch (CargarPantallaException ex) {
            log.error("inicializarComponentes() - Error inicializando pantalla de gestiond e tickets");
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), getStage());
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando pantalla...");
		
		tcCod.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbFiltro", "tcCod", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCod.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbFiltro", "tcDescripcion", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
		
		MotivosAuditoriaExample example = new MotivosAuditoriaExample();
		
		String tipoDoc = (String) getDatos().get(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO);
		
		example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
				.andCodEmpEqualTo(sesion.getAplicacion().getEmpresa().getCodEmpresa())
				.andTipoAuditoriaEqualTo(tipoDoc)
				.andMotivoActivoEqualTo(true);
		
		example.setOrderByClause("COD_MOTIVO");
		
		log.debug("initializeForm() - Buscando motivos en BBDD...");
		List<MotivosAuditoria> listaRecogida = auditoriaMapper.selectByExample(example);
		List<MotivoAuditoriaDto>listaMotivosDto = new ArrayList<>();
		
		//Filtrar los motivos dependiendo del tipo de auditoria
		for (MotivosAuditoria motivosAuditoria : listaRecogida) {
			MotivoAuditoriaDto mDto = new MotivoAuditoriaDto();
			mDto.setCodigo(Integer.parseInt(motivosAuditoria.getCodMotivo().toString()));
			mDto.setDescripcion(I18N.getTexto(motivosAuditoria.getDesMotivo()));
			mDto.setPermiteObservaciones(motivosAuditoria.getPermiteObservaciones());
			listaMotivosDto.add(mDto);
		}
			
		ObservableList<MotivoAuditoriaDto> cargarResultados = FXCollections.observableArrayList(listaMotivosDto);

		log.debug("initializeForm() - Seteamos resultados...");
		tbFiltro.setItems(cargarResultados);
		tbFiltro.getSelectionModel().selectFirst();

	}

	@Override
	public void initializeFocus() {		
	}
	
	@FXML
	void accionAceptar(ActionEvent event) {
		log.info("accionAceptar() - Buscando por filtro seleccionado...");
		MotivoAuditoriaDto seleccionado = tbFiltro.getSelectionModel().getSelectedItem();
		getDatos().put(MOTIVO, seleccionado);
		getStage().close();
	}

	@FXML
	void accionCancelar(ActionEvent event) {
		log.info("accionCancelar() - cancelando busqueda de motivos");
		getStage().close();
	}

	@FXML
	void aceptarDocDobleClick(MouseEvent event) {
		log.info("accionAceptarDobleClick() - Buscando por filtro seleccionado...");
		accionAceptar(null);
	}
	@FXML
	public void salirEsc(KeyEvent event) {
		if(event.getCode()==KeyCode.ESCAPE) {
			getStage().close();
		}
	}


	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
        switch (botonAccionado.getClave()) {
            // BOTONERA TABLA MOVIMIENTOS
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionTablaPrimerRegistro();
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionTablaAnteriorRegistro();
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionTablaSiguienteRegistro();
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                accionTablaUltimoRegistro();
                break;
        }
	}
	
    @FXML
    private void accionTablaPrimerRegistro() {
        log.debug("accionTablaPrimerRegistro() - Acción ejecutada");
        if (tbFiltro.getItems() != null && !tbFiltro.getItems().isEmpty()) {
        	tbFiltro.getSelectionModel().select(0);
        	tbFiltro.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }


    @FXML
    private void accionTablaAnteriorRegistro() {
        log.debug("accionTablaAnteriorRegistro() - Acción ejecutada");
        if (tbFiltro.getItems() != null && !tbFiltro.getItems().isEmpty()) {
            int indice = tbFiltro.getSelectionModel().getSelectedIndex();
            if (indice > 0) {
            	tbFiltro.getSelectionModel().select(indice - 1);
            	tbFiltro.scrollTo(indice - 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    @FXML
    private void accionTablaSiguienteRegistro() {
        log.debug("accionIrSiguienteRegistroTabla() - Acción ejecutada");
        if (tbFiltro.getItems() != null && !tbFiltro.getItems().isEmpty()) {
            int indice = tbFiltro.getSelectionModel().getSelectedIndex();
            if (indice < tbFiltro.getItems().size()) {
            	tbFiltro.getSelectionModel().select(indice + 1);
            	tbFiltro.scrollTo(indice + 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    @FXML
    private void accionTablaUltimoRegistro() {
        log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
        if (tbFiltro.getItems() != null && !tbFiltro.getItems().isEmpty()) {
        	tbFiltro.getSelectionModel().select(tbFiltro.getItems().size() - 1);
        	tbFiltro.scrollTo(tbFiltro.getItems().size() - 1);  // Mueve el scroll para que se vea el registro
        }
    }

}
