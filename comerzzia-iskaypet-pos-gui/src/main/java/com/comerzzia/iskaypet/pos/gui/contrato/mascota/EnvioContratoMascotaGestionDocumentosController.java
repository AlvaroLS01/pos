package com.comerzzia.iskaypet.pos.gui.contrato.mascota;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.resumen.ResumenContratoView;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.mascotas.DatosCabeceraContrato;
import com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas.ServicioContratoMascotas;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

@Component
public class EnvioContratoMascotaGestionDocumentosController extends WindowController implements Initializable, IContenedorBotonera {

	private static final Logger log = Logger.getLogger(EnvioContratoMascotaGestionDocumentosController.class);
	
	@Autowired
	private ServicioContratoMascotas servicioContratoMascotas;
    @FXML
    private HBox hbProcess;

    @FXML
    private Button btImprimirTodos;

    @FXML
    private Button btSalir;

    @FXML
    private Label lbTitulo;
	@FXML
    protected AnchorPane panelMenuTabla;
    @FXML
    private TableColumn<DatosCabeceraContrato, String> tcDescripcion;

    @FXML
    private TableColumn<DatosCabeceraContrato, String> tcEnviado;

    @FXML
    private TableColumn<DatosCabeceraContrato, String> tcCodigo;
    
    private TableColumn<DatosCabeceraContrato, Boolean> selectedColumn;

    @FXML
    private TableView<DatosCabeceraContrato> tvContratos;

    @FXML
    private HBox hbEnvios;
    
	protected BotoneraComponent botoneraAccionesTabla;
	
	private List<DatosCabeceraContrato>lstContratos;
	protected ObservableList<DatosCabeceraContrato> obsLstContratos;

	//GAP 116 GUARDADO LOCAL DEL PDF CONTRATO
	private boolean recuperaContrato = false;
	
	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
        log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
        switch (botonAccionado.getClave()){
            case "ACCION_TABLA_PRIMER_REGISTRO":
                accionTablaPrimerRegistro(tvContratos);
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                accionTablaIrAnteriorRegistro(tvContratos);
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                accionTablaIrSiguienteRegistro(tvContratos);
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
            	accionTablaUltimoRegistro(tvContratos);
            	break;
            case "ACCION_VER":
            	mostrarDatosMascota();
                break;
            default:
                log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
                break;
        }
    }
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		lstContratos = (List<DatosCabeceraContrato>) getDatos().get("lstContratos");
		tvContratos.setPlaceholder(new Label(""));
		tvContratos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		tcCodigo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tvContratos", "tcCodigo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tcDescripcion", "tcDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcEnviado.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tvContratos", "tcEnviado", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));
		inicializarCeldas();
	}
	//GAP 116 GUARDADO LOCAL DEL PDF CONTRATO
	@Override
	public void initializeComponents() throws InitializeGuiException {
		if(getDatos().containsKey("recuperaContrato")) {
			tcEnviado.setVisible(false);
			recuperaContrato = true;
		}else {
			tcEnviado.setVisible(true);
			recuperaContrato = false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		lstContratos = (List<DatosCabeceraContrato>) getDatos().get("lstContratos");
		obsLstContratos = FXCollections.observableList(new ArrayList<DatosCabeceraContrato>());
		

		// BOTONERA LATERAL
		try{
			List<ConfiguracionBotonBean> listaAccionesTablaVen = new ArrayList<ConfiguracionBotonBean>();
			listaAccionesTablaVen.addAll(BotoneraComponent.cargarAccionesTablaSimple());
			if(recuperaContrato) {
				listaAccionesTablaVen.add(new ConfiguracionBotonBean("iconos/view.png", null, null, "ACCION_VER", "REALIZAR_ACCION"));
			}
            botoneraAccionesTabla = new BotoneraComponent(5, 1, this, listaAccionesTablaVen, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(),
	        		BotonBotoneraSimpleComponent.class.getName());
	        panelMenuTabla.getChildren().clear();
	        panelMenuTabla.getChildren().add(botoneraAccionesTabla);
		}
		catch(Exception e){
			log.error("inicializarComponentes() - Error inicializando la botonera de navegación de la tabla de arreglos.");
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), getStage());
		}
		
		seleccionarLineas();
		
		for (DatosCabeceraContrato datosContrato : lstContratos) {
			obsLstContratos.add(datosContrato);
		}
		tvContratos.setItems(obsLstContratos);
		tvContratos.getSelectionModel().select(0);
		tvContratos.getFocusModel().focus(0);
	}
	@Override
	public void initializeFocus() {
		seleccionarLineas();
	}
	
	private void inicializarCeldas() {
		tcCodigo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DatosCabeceraContrato, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<DatosCabeceraContrato, String> cdf){
				return cdf.getValue().getCodigoProperty();
			}
		});
		tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DatosCabeceraContrato, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<DatosCabeceraContrato, String> cdf){
				return cdf.getValue().getDescripcionProperty();
			}
		});
		tcEnviado.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DatosCabeceraContrato, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<DatosCabeceraContrato, String> cdf){
				return cdf.getValue().getEnviadoProperty();
			}
		});
		
		tvContratos.setEditable(true);	
		
		//Añadimos la columna select
		selectedColumn = new TableColumn<>(I18N.getTexto("IMPRIMIR"));
        selectedColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setStyle("-fx-alignment: CENTER;");
        
        tvContratos.getColumns().add(selectedColumn);
		
	}
	
    protected void seleccionarLineas(){
    	if(lstContratos!=null) {
	        for(DatosCabeceraContrato datosContrato: lstContratos){
	        	if(datosContrato!=null) {
	        		datosContrato.setSelected(!datosContrato.isEnviado());
	        	}
	        }
    	}
    }
	
    @FXML
    public void accionSalir() {
    	if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro que desea salir?"), getStage())) {
    		getStage().close();
    	}
    }
    
    //GAP 116 GUARDADO LOCAL DEL PDF CONTRATO
    @FXML
    public void accionImprimirTodos() {

		try {
			if(recuperaContrato) {
				for(DatosCabeceraContrato datosImprimir : tvContratos.getItems()){
					if (datosImprimir.isSelected()) {
						servicioContratoMascotas.imprimirContrato(datosImprimir);
					}
				}
			}else {
				for (DatosCabeceraContrato datosImprimir : tvContratos.getItems()) {
					if (datosImprimir.isSelected() || !datosImprimir.isEnviado()) {
						servicioContratoMascotas.imprimirContrato(datosImprimir);
					}
				}
			}
			getStage().close();
		} catch (Exception e) {
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
		}
    }

    //GAP 116 GUARDADO LOCAL DEL PDF CONTRATO
    @FXML
    void aceptarDocDobleClick(MouseEvent event) {
    	if(recuperaContrato) {
    		if(event.getClickCount() == 2) {
    			mostrarDatosMascota();
    		}
    		
    		
    	}
    	
    }
	private void mostrarDatosMascota() {
		
		DatosCabeceraContrato datosModal = tvContratos.getSelectionModel().getSelectedItem();
		HashMap<String, Object> parmetros = new HashMap<String, Object>();
		parmetros.put(ContratoAnimalController.PARAM_CONTRATO, datosModal.getContratoAnimal());
		parmetros.put("recuperaContrato", true);
		getApplication().getMainView().showModalCentered(ResumenContratoView.class, parmetros, getStage());
	}

}
