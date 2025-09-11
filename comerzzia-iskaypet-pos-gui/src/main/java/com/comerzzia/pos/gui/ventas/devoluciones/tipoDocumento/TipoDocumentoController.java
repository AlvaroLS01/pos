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


package com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class TipoDocumentoController extends WindowController implements Initializable, IContenedorBotonera{

    public static final String PARAMETRO_SALIDA_DOC = "DOC";
    public static final String PARAMETRO_ENTRADA_POSIBLES_DOCS = "POSIBLES_DOCS";
    
    @Autowired
    private Sesion sesion;
    
    @FXML
    protected TableView tbTipoDoc;
    
    @FXML
    protected AnchorPane panelBotoneraTabla;
    
    @FXML
    protected Label lbTitulo;

    ObservableList<TipoDocumentoGui> docs;
    
    @FXML
    protected TableColumn tcCodDoc, tcDescripcion;
    
    @FXML
    protected Button btAceptar, btCancelar;
    
    // botonera de acciones de tabla
    BotoneraComponent botoneraAccionesTabla;
    
    protected Logger log = Logger.getLogger(getClass());
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tbTipoDoc.setPlaceholder(new Label(""));
        
        docs = FXCollections.observableList(new ArrayList<TipoDocumentoGui>());
        
        tcCodDoc.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTipoDoc", "tcCodDoc", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTipoDoc", "tcDesDoc", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
    
        tcCodDoc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TipoDocumentoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TipoDocumentoGui, String> cdf) {
                return cdf.getValue().getCodDoc();
            }
        });
        tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TipoDocumentoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TipoDocumentoGui, String> cdf) {
                return cdf.getValue().getDesDoc();
            }
        });
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
        
        registrarAccionCerrarVentanaEscape();
        crearEventoEnterTabla(tbTipoDoc);
        crearEventoNavegacionTabla(tbTipoDoc);
        
        try{
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
            botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelBotoneraTabla.getPrefWidth(), panelBotoneraTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelBotoneraTabla.getChildren().add(botoneraAccionesTabla);
        }
        catch (CargarPantallaException ex) {
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), getStage());
        }
    }
    
    @Override
    public void initializeForm() throws InitializeGuiException {
    	try {
	    	HashMap<String,Object> parametros = getDatos();
	    	setTitulo();
	    	
	        docs.clear();
	        
	        List<String> documentos = (List<String>)parametros.get(PARAMETRO_ENTRADA_POSIBLES_DOCS);
	        
	        for(String codDocumento: documentos){
	        	try {
	        		docs.add(new TipoDocumentoGui(sesion.getAplicacion().getDocumentos().getDocumento(codDocumento)));
	        	}
	        	catch (DocumentoException ex) {
	        		log.error("Error procesando los documentos de origen válidos para nota de crédito.", ex);
	        	}
	        }
	        
	        tbTipoDoc.setItems(docs);
    	} catch(Exception e) {
    		log.error("initializeForm() - Error inicializando la pantalla: " + e.getMessage());
    	}
    }

    @Override
    public void initializeFocus() {
    	tbTipoDoc.requestFocus();
    	tbTipoDoc.scrollTo(0);
		if(tbTipoDoc.getSelectionModel().getSelectedIndex()<0){
			tbTipoDoc.getSelectionModel().select(0);
		}
    }

    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
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
    
    private List<ConfiguracionBotonBean> cargarAccionesTabla() {
        List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); //"Home"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); //"Page Up"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); //"Page Down"
        listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); //"End"
        return listaAcciones;
    }

    @FXML
    public void accionCancelar(){
        getStage().close();
    }
    
    @FXML
    public void accionAceptar(){
        if(tbTipoDoc.getSelectionModel().getSelectedItem()!=null){
            getDatos().put(PARAMETRO_SALIDA_DOC, ((TipoDocumentoGui)tbTipoDoc.getSelectionModel().getSelectedItem()).getDoc());
            getStage().close();
        }
        else{
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar el tipo de documento correspondiente."), this.getStage());
        }
    }
    
    /**
     * Acción mover a primer registro de la tabla
     *
     * @param event
     */
    @FXML
    private void accionTablaPrimerRegistro() {
        log.debug("accionTablaPrimerRegistro() - Acción ejecutada");
        if (tbTipoDoc.getItems() != null && !tbTipoDoc.getItems().isEmpty()) {
            tbTipoDoc.getSelectionModel().select(0);
            tbTipoDoc.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }

    /**
     * Acción mover a anterior registro de la tabla
     *
     * @param event
     */
    @FXML
    private void accionTablaAnteriorRegistro() {
        log.debug("accionTablaAnteriorRegistro() - Acción ejecutada");
        if (tbTipoDoc.getItems() != null && !tbTipoDoc.getItems().isEmpty()) {
            int indice = tbTipoDoc.getSelectionModel().getSelectedIndex();
            if (indice > 0) {
                tbTipoDoc.getSelectionModel().select(indice - 1);
                tbTipoDoc.scrollTo(indice - 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a siguiente registro de la tabla
     *
     * @param event
     */
    @FXML
    private void accionTablaSiguienteRegistro() {
        log.debug("accionIrSiguienteRegistroTabla() - Acción ejecutada");
        if (tbTipoDoc.getItems() != null && !tbTipoDoc.getItems().isEmpty()) {
            int indice = tbTipoDoc.getSelectionModel().getSelectedIndex();
            if (indice < tbTipoDoc.getItems().size()) {
                tbTipoDoc.getSelectionModel().select(indice + 1);
                tbTipoDoc.scrollTo(indice + 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a último registro de la tabla
     *
     * @param event
     */
    @FXML
    private void accionTablaUltimoRegistro() {
        log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
        if (tbTipoDoc.getItems() != null && !tbTipoDoc.getItems().isEmpty()) {
            tbTipoDoc.getSelectionModel().select(tbTipoDoc.getItems().size() - 1);
            tbTipoDoc.scrollTo(tbTipoDoc.getItems().size() - 1);  // Mueve el scroll para que se vea el registro
        }
    }
    
    /**
     * Accion que trata el evento de teclado para enviar el artículo a facturar.
     *
     * @param event
     */
   public void aceptarDocDobleClick(MouseEvent event) {
        log.trace("aceptarArticuloDobleClick() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                accionAceptar();
            }
        }
    }
   
   @Override
	public void accionEventoEnterTabla(String idTabla) {
	   log.trace("accionEventoEnterTabla() - Aceptar");
	   accionAceptar();
	}
   
   public void setTitulo(){
	   lbTitulo.setText(I18N.getTexto("Tipo de Documento"));
   }
}