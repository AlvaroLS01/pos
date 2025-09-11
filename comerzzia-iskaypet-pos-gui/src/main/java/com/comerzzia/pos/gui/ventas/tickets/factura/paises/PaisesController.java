/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */


package com.comerzzia.pos.gui.ventas.tickets.factura.paises;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class PaisesController extends WindowController{
    
    public static final String PARAMETRO_SALIDA_PAIS = "PAIS_SELECCIONADO";
    
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PaisesController.class);

    @FXML
    TableView<PaisGui> tbPaises;
    
    @FXML
    TextField tfDescPais, tfCodPais;
    
    @FXML
    TableColumn tcDescPais, tcCodPais;
    
    FormularioPaisBean frBusquedaPais;
    
    private ObservableList<PaisGui> paises;
    
    @Autowired
    private PaisService paisService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tbPaises.setPlaceholder(new Label());
        
        paises = FXCollections.observableArrayList();
        tbPaises.setItems(paises);
        
        frBusquedaPais = SpringContext.getBean(FormularioPaisBean.class);
        frBusquedaPais.setFormField("codPais", tfCodPais);
        frBusquedaPais.setFormField("descPais", tfDescPais);
        
        tcDescPais.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPaises", "tcDescPais", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcCodPais.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPaises", "tcCodPais", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        
        tcDescPais.setCellValueFactory(new PropertyValueFactory<PaisGui, String>("descPais"));
        tcCodPais.setCellValueFactory(new PropertyValueFactory<PaisGui, String>("codPais"));
        
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
        
        registrarAccionCerrarVentanaEscape();
    }

    @Override
    public void initializeForm() throws InitializeGuiException {
        
        tfCodPais.setText("");
        tfDescPais.setText("");
    }

    @Override
    public void initializeFocus() {

        tfCodPais.requestFocus();
    }
    
    public void accionBuscar(){
        
        frBusquedaPais.clearErrorStyle();
        
        frBusquedaPais.setCodPais(tfCodPais.getText());
        frBusquedaPais.setDescPais(tfDescPais.getText());
        
        Set<ConstraintViolation<FormularioPaisBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBusquedaPais);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioPaisBean> next = constraintViolations.iterator().next();
            frBusquedaPais.setErrorStyle(next.getPropertyPath(), true);
            frBusquedaPais.setFocus(next.getPropertyPath());
        }
        else{
            try {
            	paises.clear();
            	
                List<PaisBean> result = paisService.consultarPais(tfCodPais.getText(), tfDescPais.getText());
                
                for(PaisBean pais: result){
                    paises.add(new PaisGui(pais));
                }
                
            }
            catch (PaisNotFoundException ex) {
                log.debug("No se encontraron resultados en la búsqueda de países con los parámetros codPais="+tfCodPais.getText()+", y descPais="+tfDescPais.getText());
            }
            catch (PaisServiceException ex) {
                log.error("Error en la búsqueda de países", ex);
            }
        }
    }
    
    public void accionBuscarPaisIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            accionBuscar();
        }
    }
    
    public void accionCancelar(){
        getStage().close();
    }
    
    public void accionAceptar(){
        
        if(tbPaises.getSelectionModel().getSelectedItem()!=null){
            HashMap<String,Object> datos = getDatos();
            if(datos == null){
                datos = new HashMap();
            }
            datos.put(PARAMETRO_SALIDA_PAIS, tbPaises.getSelectionModel().getSelectedItem().getPais());
            getStage().close();
        }
        else{
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar antes el país correspondiente."), getStage());
        }
    }
    
    public void aceptarPaisDobleClick(MouseEvent event) {
        log.debug("aceptarPaisDobleClick() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                accionAceptar();
            }
        }
    }
    
    public void aceptarPaisIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            accionAceptar();
        }
    }
}
