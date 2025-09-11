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
package com.comerzzia.pos.gui.ventas.cajas.apuntes.tablaApuntes;


import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoController;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServices;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class TablaApuntesController extends TipoDocumentoController{

	public static final String PARAMETRO_CONCEPTO_SELECCIONADO = "CONCEPTO_SELECCIONADO";
	
	ObservableList<ConceptoGui> conceptos;
	
	@Autowired
	private CajaConceptosServices cajaConceptosServices;
	
	@Override
    public void initialize(URL url, ResourceBundle rb) {

        tbTipoDoc.setPlaceholder(new Label(""));
        
        conceptos = FXCollections.observableList(new ArrayList<ConceptoGui>());
        
        tcCodDoc.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTipoDoc", "tcCodDoc", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTipoDoc", "tcDesDoc", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
    
        tcCodDoc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ConceptoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ConceptoGui, String> cdf) {
                return cdf.getValue().getCodigoProperty();
            }
        });
        tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ConceptoGui, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ConceptoGui, String> cdf) {
                return cdf.getValue().getDescripcionProperty();
            }
        });
    }
	
	@Override
	public void initializeForm() throws InitializeGuiException {

		HashMap<String,Object> parametros = getDatos();

		setTitulo();
		conceptos.clear();

		List<CajaConceptoBean> conceptosBean = cajaConceptosServices.getConceptosCajaManual();

		for(CajaConceptoBean concepto: conceptosBean){
			conceptos.add(new ConceptoGui(concepto));
		}

		tbTipoDoc.setItems(conceptos);
	}
	
	@FXML
    public void accionAceptar(){
        if(tbTipoDoc.getSelectionModel().getSelectedItem()!=null){
            getDatos().put(PARAMETRO_CONCEPTO_SELECCIONADO, ((ConceptoGui)tbTipoDoc.getSelectionModel().getSelectedItem()).getConcepto());
            getStage().close();
        }
        else{
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar un elemento de la tabla"), this.getStage());
        }
    }
	
	public void setTitulo(){
		lbTitulo.setText(I18N.getTexto("Conceptos"));
	}
}
