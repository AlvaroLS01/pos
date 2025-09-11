package com.comerzzia.pos.gui.mantenimientos.fidelizados.tiendas;

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
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class TiendaController extends WindowController{
	
	public static final String PARAMETRO_SALIDA_TIENDA = "TIENDA_SELECCIONADA";
    
	protected static final Logger log = Logger.getLogger(TiendaController.class);

    @FXML
    protected TableView<TiendaGui> tbTiendas;
    
    @FXML
    protected TextField tfDescTienda, tfCodTienda;
    
    @FXML
    protected TableColumn<TiendaGui, String> tcDescTienda, tcCodTienda;
    
    protected FormularioTiendaBean frBusquedaTienda;
    
    private ObservableList<TiendaGui> tiendas;
    private List<TiendaGui> tiendasOriginal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tbTiendas.setPlaceholder(new Label());
        
        tiendas = FXCollections.observableArrayList();
        tbTiendas.setItems(tiendas);
        
        frBusquedaTienda = SpringContext.getBean(FormularioTiendaBean.class);
        frBusquedaTienda.setFormField("codTienda", tfCodTienda);
        frBusquedaTienda.setFormField("descTienda", tfDescTienda);
        
        tcDescTienda.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTiendas", "tcDescTienda", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCodTienda.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTiendas", "tcCodTienda", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        
        tcDescTienda.setCellValueFactory(new PropertyValueFactory<TiendaGui, String>("desTienda"));
        tcCodTienda.setCellValueFactory(new PropertyValueFactory<TiendaGui, String>("codTienda"));
        
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
        
        registrarAccionCerrarVentanaEscape();
    }

    @Override
    public void initializeForm() throws InitializeGuiException {
    	tiendasOriginal = (List<TiendaGui>) getDatos().get("tiendas");
    	
        tfCodTienda.setText("");
        tfDescTienda.setText("");
        accionBuscar();
    }

    @Override
    public void initializeFocus() {

        tfCodTienda.requestFocus();
    }
    
    public void accionBuscar(){
        
        frBusquedaTienda.clearErrorStyle();
        
        frBusquedaTienda.setCodTienda(tfCodTienda.getText());
        frBusquedaTienda.setDescTienda(tfDescTienda.getText());
        
        Set<ConstraintViolation<FormularioTiendaBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBusquedaTienda);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioTiendaBean> next = constraintViolations.iterator().next();
            frBusquedaTienda.setErrorStyle(next.getPropertyPath(), true);
            frBusquedaTienda.setFocus(next.getPropertyPath());
        }
        else{
            List<TiendaGui> result = tiendasOriginal;
            
            tiendas.clear();
            if(result != null && !result.isEmpty()){
	            for(TiendaGui tienda: result){
	            	if(tienda.getCodTienda().toUpperCase().contains(frBusquedaTienda.getCodTienda().toUpperCase()) && tienda.getDesTienda().toUpperCase().contains(frBusquedaTienda.getDescTienda().toUpperCase()))
	            		tiendas.add(tienda);
	            }
            }else{
            	log.debug("No se han encontrado resultados");
            }
           
        }
    }
    
    public void accionBuscarTiendaIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            accionBuscar();
        }
    }
    
    public void accionCancelar(){
        getStage().close();
    }
    
    public void accionAceptar(){
        
        if(tbTiendas.getSelectionModel().getSelectedItem()!=null){
            HashMap<String,Object> datos = getDatos();
            if(datos == null){
                datos = new HashMap<String,Object>();
            }
            datos.put(PARAMETRO_SALIDA_TIENDA, tbTiendas.getSelectionModel().getSelectedItem());
            getStage().close();
        }
        else{
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar antes la tienda correspondiente."), getStage());
        }
    }
    
    public void aceptarTiendaDobleClick(MouseEvent event) {
        log.debug("aceptarTiendaDobleClick() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                accionAceptar();
            }
        }
    }
    
    public void aceptarTiendaIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            accionAceptar();
        }
    }

}
