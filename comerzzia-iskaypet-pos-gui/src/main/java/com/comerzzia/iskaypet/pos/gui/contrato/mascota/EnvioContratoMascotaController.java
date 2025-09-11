package com.comerzzia.iskaypet.pos.gui.contrato.mascota;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.resumen.ResumenContratoView;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.mascotas.DatosCabeceraContrato;
import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;
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
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

@Component
public class EnvioContratoMascotaController extends WindowController implements Initializable, IContenedorBotonera {

	private static final Logger log = Logger.getLogger(EnvioContratoMascotaController.class);
	
	@Autowired
	private ServicioContratoMascotas servicioContratoMascotas;
	
	@Autowired
	private Sesion sesion;
	
    @FXML
    private HBox hbProcess;

    @FXML
    private Button btImprimirTodos;

    @FXML
    private Button btReenviar;
    
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
    @Autowired
    protected IskaypetTicketManager ticketManager;
	
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
		
		lstContratos = (List<DatosCabeceraContrato>) getDatos().get(IskaypetEvicertiaService.LST_CONTRATOS);
		tvContratos.setPlaceholder(new Label(""));
		tvContratos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		tcCodigo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tvContratos", "tcCodigo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tvContratos", "tcDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcEnviado.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tvContratos", "tcEnviado", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));

		tcDescripcion.setCellFactory(param -> new TableCell<DatosCabeceraContrato, String>() {

		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null) {
		            setGraphic(null);
		            return;
		        }

		        DatosCabeceraContrato contrato = (DatosCabeceraContrato) getTableRow().getItem();
		        if (contrato == null) {
		            setGraphic(null);
		            return;
		        }

		        establecerEstiloDescripcion(item, contrato);
		    }

		    /**
		     * CZZ 1355 - Establece el estilo de la columna de descripción, de tal forma que se pinte en dos lineas, en la primera la descripción y en la segunda
		     * el identificador que si es demasiado grande se completa con "..."
		     */
			private void establecerEstiloDescripcion(String item, DatosCabeceraContrato contrato) {
				TextFlow textFlow = new TextFlow();

		        // Primera línea: lo que venga en "item"
		        Text textDescripcion = new Text(item);
		        textDescripcion.setFill(getTableView().getSelectionModel().isSelected(getIndex()) ? Color.WHITE : Color.BLACK);
		        textFlow.getChildren().add(textDescripcion);

		        // Salto de línea
		        textFlow.getChildren().add(new Text("\n"));

		        // Segunda línea: el numIden
		        String idText = "ID: " + contrato.getNumIden();
		        double maxWidth = 200;
		        Text temp = new Text(idText);
		        temp.setFont(Font.font("System", FontWeight.BOLD, 12));

		        // Calcular si cabe
		        double textWidth = temp.getLayoutBounds().getWidth();
		        if (textWidth > maxWidth) {
		            // Si no cabe, recortar y añadir "..."
		            String ellipsis = "...";
		            for (int i = idText.length(); i > 0; i--) {
		                String sub = idText.substring(0, i) + ellipsis;
		                temp.setText(sub);
		                if (temp.getLayoutBounds().getWidth() <= maxWidth) {
		                    idText = sub;
		                    break;
		                }
		            }
		        }
		        Text textId = new Text(idText);
		        textId.setStyle("-fx-font-weight: bold");
		        textId.setFill(getTableView().getSelectionModel().isSelected(getIndex()) ? Color.WHITE : Color.BLACK);

		        textFlow.getChildren().add(textId);

		        setPrefHeight(textFlow.prefHeight(-1) + 2);
		        setGraphic(textFlow);
			}

		});

		tvContratos.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
			if (newIndex.intValue() >= 0) {
				tcDescripcion.getTableView().refresh();
			}
		});

		
		
		inicializarCeldas();
	
	}
	//GAP 116 GUARDADO LOCAL DEL PDF CONTRATO
	@Override
	public void initializeComponents() throws InitializeGuiException {
		if(getDatos().containsKey(IskaypetEvicertiaService.RECUPERA_CONTRATO)) {
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
		
		lstContratos = (List<DatosCabeceraContrato>) getDatos().get(IskaypetEvicertiaService.LST_CONTRATOS);
		
		obsLstContratos = FXCollections.observableList(new ArrayList<DatosCabeceraContrato>());
		if(IskaypetEvicertiaService.OPERACION_REENVIAR.equals(getDatos().get(IskaypetEvicertiaService.OPERACION))) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Reenvío a Evicertia realizado, consulta el estado en la pantalla de resumen"), getStage());
		}

		// BOTONERA LATERAL
		try{
			List<ConfiguracionBotonBean> listaAccionesTablaVen = new ArrayList<>();
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
		selectedColumn = new TableColumn<>(I18N.getTexto("SELECCIONADO"));
        selectedColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setStyle("-fx-alignment: CENTER;");
        
        tvContratos.getColumns().add(0, selectedColumn);
		
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
		getDatos().put(IskaypetEvicertiaService.OPERACION, IskaypetEvicertiaService.OPERACION_SALIR);
		getStage().close();
    	
    }
    
    @FXML
    public void accionAparcar() {
		getDatos().put(IskaypetEvicertiaService.OPERACION, IskaypetEvicertiaService.OPERACION_APARCAR);
		getStage().close();
    	
    }
    
    /**
     * CZZ 1355 - Reenvía a Evicertia los pedidos seleccionados.
     */
    @FXML
    public void accionReenviar() {
    	 List<Integer> idsSeleccionados = getIdsSeleccionados();
         
         if(idsSeleccionados.isEmpty()) {
         	VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Por favor, selecciona los contratos que quiere reenviar a Evicertia"),getStage());
         	return;
         }
         
    	if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro que desea reenviar a Evicertia los contratos seleccionados?"), getStage())) { 
            getDatos().put(IskaypetEvicertiaService.IDS_REENVIAR, idsSeleccionados);
    		getDatos().put(IskaypetEvicertiaService.OPERACION, IskaypetEvicertiaService.OPERACION_REENVIAR);
    		getStage().close();
    	}
    }

    /**
     *  CZZ 1355 - Recupera los id de las lineas seleccionadas.
     */
	private List<Integer> getIdsSeleccionados() {
		List<Integer> idsSeleccionados = new ArrayList<>();
		for(DatosCabeceraContrato datosImprimir : tvContratos.getItems()){
			if (datosImprimir.isSelected()) {
				 idsSeleccionados.add(datosImprimir.getIdLinea());
			}
		}
		return idsSeleccionados;
	}
    
    @FXML
    public void accionContinuar() {
		getDatos().put(IskaypetEvicertiaService.OPERACION, null);
		getStage().close();
    }
    
    
    /**
     * GAP 116 - Guardado Local del PDF del contrato
     * CZZ 1355 - Modificado método para que imprima contrato solo de los seleccionados independientemente que se hayan enviado o no.
     */
    @FXML
    public void accionImprimirTodos() {
    	List<Integer> idsSeleccionados = getIdsSeleccionados();
        
        if(idsSeleccionados.isEmpty()) {
        	VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Por favor, selecciona los contratos que quiere imprimir"),getStage());
        	return;
        }
    	
		try {
			for (DatosCabeceraContrato datosImprimir : tvContratos.getItems()) {
				if (datosImprimir.isSelected()) {
					servicioContratoMascotas.imprimirContrato(datosImprimir);
					servicioContratoMascotas.actualizarMetodoFirma(sesion.getAplicacion().getUidActividad(), datosImprimir.getUidTicket(), datosImprimir.getIdLinea(), IskaypetEvicertiaService.METODO_FIRMA_MANUAL);
				}
			}
		} catch (Exception e) {
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
		}
    }

    //GAP 116 GUARDADO LOCAL DEL PDF CONTRATO
    @FXML
    void aceptarDocDobleClick(MouseEvent event) {
    	if(recuperaContrato && event.getClickCount() == 2) {
    		mostrarDatosMascota();
    	}
    	
    }
	private void mostrarDatosMascota() {
		
		DatosCabeceraContrato datosModal = tvContratos.getSelectionModel().getSelectedItem();
		HashMap<String, Object> parmetros = new HashMap<String, Object>();
		parmetros.put(ContratoAnimalController.PARAM_CONTRATO, datosModal.getContratoAnimal());
		parmetros.put(IskaypetEvicertiaService.RECUPERA_CONTRATO, true);
		getApplication().getMainView().showModalCentered(ResumenContratoView.class, parmetros, getStage());
	}

}
