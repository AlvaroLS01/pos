package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.iskaypet.pos.persistence.lenguajes.LenguajeBean;
import com.comerzzia.iskaypet.pos.services.lenguajes.LenguajesService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

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

@Controller
@SuppressWarnings({"rawtypes", "unchecked"})
public class LenguajesController extends WindowController{
    
    public static final String PARAMETRO_SALIDA_LENGUAJE = "LENGUAJE_SELECCIONADO";
    
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LenguajesController.class);

    @FXML
    protected TableView<LenguajeGui> tbLenguajes;
    
    @FXML
    protected TextField tfDescLenguaje, tfCodLenguaje;
    
	@FXML
    protected TableColumn tcDescLenguaje, tcCodLenguaje;
    
    protected FormularioLenguajeBean frBusquedaLenguaje;
    
    protected ObservableList<LenguajeGui> lenguajes;
    
    @Autowired
    private LenguajesService lenguajesService;
    
    
	@Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tbLenguajes.setPlaceholder(new Label());
        
        lenguajes = FXCollections.observableArrayList();
        tbLenguajes.setItems(lenguajes);
        
        frBusquedaLenguaje = SpringContext.getBean(FormularioLenguajeBean.class);
        frBusquedaLenguaje.setFormField("codLenguaje", tfCodLenguaje);
        frBusquedaLenguaje.setFormField("descLenguaje", tfDescLenguaje);
        
        tcDescLenguaje.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLenguajes", "tcDescLenguaje", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCodLenguaje.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLenguajes", "tcCodLenguaje", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        
        tcDescLenguaje.setCellValueFactory(new PropertyValueFactory<LenguajeGui, String>("descLenguaje"));
        tcCodLenguaje.setCellValueFactory(new PropertyValueFactory<LenguajeGui, String>("codLenguaje"));
        
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
        
        registrarAccionCerrarVentanaEscape();
    }

    @Override
    public void initializeForm() throws InitializeGuiException {
    	tfCodLenguaje.setText("");
    	tfDescLenguaje.setText("");
    	lenguajes.clear();
    }

    @Override
    public void initializeFocus() {
    	tfCodLenguaje.requestFocus();
    }
    
    public void accionBuscar(){
        frBusquedaLenguaje.clearErrorStyle();
        
        frBusquedaLenguaje.setCodLenguaje(tfCodLenguaje.getText());
        frBusquedaLenguaje.setDescLenguaje(tfDescLenguaje.getText());
        
        Set<ConstraintViolation<FormularioLenguajeBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBusquedaLenguaje);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioLenguajeBean> next = constraintViolations.iterator().next();
            frBusquedaLenguaje.setErrorStyle(next.getPropertyPath(), true);
            frBusquedaLenguaje.setFocus(next.getPropertyPath());
        }
        else{
            try {
            	lenguajes.clear();
                List<LenguajeBean> result = lenguajesService.getLenguajesByParams(tfCodLenguaje.getText(), tfDescLenguaje.getText());
                
                for(LenguajeBean lenguaje: result){
                    lenguajes.add(new LenguajeGui(lenguaje));
                }
                
            } catch (Exception ex) {
                log.error("Error en la búsqueda de idiomas", ex);
            }
        }
    }
    
    @FXML
    public void accionBuscarLenguajeIntro(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            accionBuscar();
        }
    }
    
    public void accionCancelar(){
        getStage().close();
    }
    
    public void accionAceptar(){
        if(tbLenguajes.getSelectionModel().getSelectedItem()!=null){
            HashMap<String,Object> datos = getDatos();
            if(datos == null){
                datos = new HashMap();
            }
            datos.put(PARAMETRO_SALIDA_LENGUAJE, tbLenguajes.getSelectionModel().getSelectedItem().getLenguaje());
            getStage().close();
        }
        else{
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar antes el idioma correspondiente."), getStage());
        }
    }
    
    @FXML
    public void aceptarLenguajeDobleClick(MouseEvent event) {
        log.debug("aceptarLenguajeDobleClick() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                accionAceptar();
            }
        }
    }
    
    @FXML
    public void aceptarLenguajeIntro(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            accionAceptar();
        }
    }
}
