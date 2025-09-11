package com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class ColectivoController extends WindowController{
	
	public static final String PARAMETRO_SALIDA_COLECTIVO = "COLECTIVO_SELECCIONADO";
    
	protected static final Logger log = Logger.getLogger(ColectivoController.class);

    @FXML
    protected TableView<ColectivoAyudaGui> tbColectivos;
    
    @FXML
    protected TextField tfDescColectivo, tfCodColectivo;
    
    @FXML
    protected TableColumn<ColectivoAyudaGui, String> tcDescColectivo, tcCodColectivo;
    
    protected FormularioColectivoBean frBusquedaColectivo;
    
    private ObservableList<ColectivoAyudaGui> colectivos;
    private List<ColectivoAyudaGui> colectivosOriginal;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		tbColectivos.setPlaceholder(new Label());
        
		colectivos = FXCollections.observableArrayList();
		tbColectivos.setItems(colectivos);
        
		frBusquedaColectivo = SpringContext.getBean(FormularioColectivoBean.class);
		frBusquedaColectivo.setFormField("codColectivo", tfCodColectivo);
		frBusquedaColectivo.setFormField("descColectivo", tfDescColectivo);
        
		tcDescColectivo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbColectivos", "tcDescColectivo", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCodColectivo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbColectivos", "tcCodColectivo", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        
        tcDescColectivo.setCellValueFactory(new PropertyValueFactory<ColectivoAyudaGui, String>("desColectivo"));
        tcCodColectivo.setCellValueFactory(new PropertyValueFactory<ColectivoAyudaGui, String>("codColectivo"));
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		
		registrarAccionCerrarVentanaEscape();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		colectivosOriginal = (List<ColectivoAyudaGui>) getDatos().get("colectivos");
    	
		tfCodColectivo.setText("");
        tfDescColectivo.setText("");
        accionBuscar();
	}

	@Override
	public void initializeFocus() {
		tfCodColectivo.requestFocus();
	}
	
	public void accionBuscar(){
        
		frBusquedaColectivo.clearErrorStyle();
        
		frBusquedaColectivo.setCodColectivo(tfCodColectivo.getText());
        frBusquedaColectivo.setDescColectivo(tfDescColectivo.getText());
        
        Set<ConstraintViolation<FormularioColectivoBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBusquedaColectivo);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioColectivoBean> next = constraintViolations.iterator().next();
            frBusquedaColectivo.setErrorStyle(next.getPropertyPath(), true);
            frBusquedaColectivo.setFocus(next.getPropertyPath());
        }
        else{
            List<ColectivoAyudaGui> result = colectivosOriginal;
            
            colectivos.clear();
            if(result != null && !result.isEmpty()){
	            for(ColectivoAyudaGui colectivo: result){
	            	if(colectivo.getCodColectivo().toUpperCase().contains(frBusquedaColectivo.getCodColectivo().toUpperCase()) && colectivo.getDesColectivo().toUpperCase().contains(frBusquedaColectivo.getDescColectivo().toUpperCase())){
	            		colectivos.add(colectivo);
	            	}
	            }
            }else{
            	log.debug("No se han encontrado resultados");
            }
           
        }
    }
    
    public void accionBuscarColectivoIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            accionBuscar();
        }
    }
    
    public void accionCancelar(){
        getStage().close();
    }
    
    public void accionAceptar(){
        
        if(tbColectivos.getSelectionModel().getSelectedItem()!=null){
            HashMap<String,Object> datos = getDatos();
            if(datos == null){
                datos = new HashMap<String, Object>();
            }
            datos.put(PARAMETRO_SALIDA_COLECTIVO, tbColectivos.getSelectionModel().getSelectedItem());
            getStage().close();
        }
        else{
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar antes el colectivo correspondiente."), getStage());
        }
    }
    
    public void aceptarColectivoDobleClick(MouseEvent event) {
        log.debug("aceptarColectivoDobleClick() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                accionAceptar();
            }
        }
    }
    
    public void aceptarColectivoIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            accionAceptar();
        }
    }

}
