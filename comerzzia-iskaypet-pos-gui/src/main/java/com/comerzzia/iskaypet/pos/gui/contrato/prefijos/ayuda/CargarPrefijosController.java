package com.comerzzia.iskaypet.pos.gui.contrato.prefijos.ayuda;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.prefijos.PrefijosPaisesKey;
import com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas.ServicioContratoMascotas;
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
public class CargarPrefijosController extends WindowController implements Initializable, IContenedorBotonera {
	
	private static final Logger log = Logger.getLogger(CargarPrefijosController.class.getName());

	@FXML
	private Label lbTitulo;

	@FXML
	protected Button btAceptar, btCancelar;
	
	@FXML
	private AnchorPane panelBotoneraTabla;
	
	@FXML
	private TableView<PrefijosPaisesKey> tbFiltro;

	@FXML
	private TableColumn<PrefijosPaisesKey, String> tcCod;

	@FXML
	private TableColumn<PrefijosPaisesKey, String> tcDescripcion;

	@FXML 
	private AnchorPane apCargarFiltros;
	
	@Autowired
	protected Sesion sesion;

    protected BotoneraComponent botoneraAccionesTabla;
    
    @Autowired
    protected ServicioContratoMascotas servicioContratoMascotas;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		
		log.debug("inicializarComponentes()");

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
            log.error("inicializarComponentes() - Error inicializando ayuda de prefijos paises");
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), getStage());
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando pantalla...");
		
		tcCod.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbFiltro", "tcCod", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCod.setCellValueFactory(new PropertyValueFactory<PrefijosPaisesKey, String>("prefijo"));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbFiltro", "tcDescripcion", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDescripcion.setCellValueFactory(new PropertyValueFactory<PrefijosPaisesKey, String>("codpais"));
		
		List<PrefijosPaisesKey>listaPrefijosPaises = servicioContratoMascotas.getPrefijosPaises();
		
		ObservableList<PrefijosPaisesKey> cargarResultados = FXCollections.observableArrayList(listaPrefijosPaises);

		log.debug("initializeForm() - Seteamos resultados...");
		tbFiltro.setItems(cargarResultados);
		tbFiltro.getSelectionModel().selectFirst();

	}

	@Override
	public void initializeFocus() {		
	}
	
	@FXML
	void accionAceptar(ActionEvent event) {
		PrefijosPaisesKey seleccionado = tbFiltro.getSelectionModel().getSelectedItem();
		getDatos().put("prefijoSelect", seleccionado);
		getStage().close();
	}

	@FXML
	void accionCancelar(ActionEvent event) {
		getDatos().put("cancelar", true);
		getStage().close();
	}

	@FXML
	void aceptarDocDobleClick(MouseEvent event) {
		if(event.getClickCount() == 2) {
			accionAceptar(null);
		}
	}
	@FXML
	public void salirEsc(KeyEvent event) {
		if(event.getCode()==KeyCode.ESCAPE) {
			getDatos().put("cancelar", true);
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
        if (tbFiltro.getItems() != null && !tbFiltro.getItems().isEmpty()) {
        	tbFiltro.getSelectionModel().select(0);
        	tbFiltro.scrollTo(0); 
        }
    }


    @FXML
    private void accionTablaAnteriorRegistro() {
        if (tbFiltro.getItems() != null && !tbFiltro.getItems().isEmpty()) {
            int indice = tbFiltro.getSelectionModel().getSelectedIndex();
            if (indice > 0) {
            	tbFiltro.getSelectionModel().select(indice - 1);
            	tbFiltro.scrollTo(indice - 1);  
            }
        }
    }

    @FXML
    private void accionTablaSiguienteRegistro() {
        if (tbFiltro.getItems() != null && !tbFiltro.getItems().isEmpty()) {
            int indice = tbFiltro.getSelectionModel().getSelectedIndex();
            if (indice < tbFiltro.getItems().size()) {
            	tbFiltro.getSelectionModel().select(indice + 1);
            	tbFiltro.scrollTo(indice + 1);  
            }
        }
    }

    @FXML
    private void accionTablaUltimoRegistro() {
        if (tbFiltro.getItems() != null && !tbFiltro.getItems().isEmpty()) {
        	tbFiltro.getSelectionModel().select(tbFiltro.getItems().size() - 1);
        	tbFiltro.scrollTo(tbFiltro.getItems().size() - 1);  
        }
    }

}
